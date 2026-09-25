package com.ficc.mwmovil;
import android.content.Context;
import android.database.Cursor;
import android.text.format.Time;
import android.util.Log;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
public class ReportePedido {



    public static File generarPDF(Context context, String nitsec,  String NumPed, int clisec,String prefijo, int lisprecod) {
        BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(context, "MantisMovil", null, 5);
        String nombre = "pedido_"+NumPed+ System.currentTimeMillis() + ".pdf";

        File file = new File(context.getExternalFilesDir(null), nombre);
  String consultaEncabezado = "" +
          " Select Clinom, Nitide,CliDir from clientes where nitsec = '"+nitsec+"' and clisec = "+clisec+" ";






        try {
            Cursor Clientes = BaseDeDatos.getWritableDatabase().rawQuery(consultaEncabezado, null); //order by nombre




            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            document.setMargins(20,20,20,20);
            document.add(new Paragraph("Distribuidora Limeña, Inc.")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold());

            document.add(new Paragraph("742-A Winer Industrial Way\nLawrenceville, GA 30045\nTel: 770-338-5494")
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("\n"));
            float[] columnasInfo = {1,1};
            Table info = new Table(columnasInfo);
            info.setWidth(UnitValue.createPercentValue(100));
            try{
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String fechaActual = sdf.format(new Date());
                if (Clientes.getCount()>0){
                    int vuelta=0;
                    Clientes.moveToFirst();
                    do {
                        Paragraph cliente = new Paragraph()
                                .add("Cliente: ("+Clientes.getString(1)+")"+Clientes.getString(0)+"\n")
                                .add(Clientes.getString(2)+"\n");


                        Paragraph orden = new Paragraph()
                                .add("Order#: "+NumPed+"\n")
                                .add("Date: "+fechaActual+"\n");


                        info.addCell(new Cell().add(cliente).setBorder(Border.NO_BORDER));
                        info.addCell(new Cell().add(orden).setBorder(Border.NO_BORDER)
                                .setTextAlignment(TextAlignment.RIGHT));

                        document.add(info);

                    } while (Clientes.moveToNext());
                }
            }catch (Exception e)
            {
                Log.e("Errorsqlcliente",e.toString());
                int h=0;
            }



            document.add(new Paragraph("\n"));

            float[] columnas = {40,80, 250,100, 80, 80, 80};
            Table table = new Table(columnas);
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell("Tipo");
            table.addHeaderCell("Codigo");
            table.addHeaderCell("Articulo");
            table.addHeaderCell("Presentacion");
            table.addHeaderCell("Cantidad");
            table.addHeaderCell("Precio");
            table.addHeaderCell("Total");

            Time time = new Time();
            time.setToNow();

            String consultapedido =
                    "select * from (" +

                            "select a.ArtSec, a.ArtCod, a.ArtNom, precio, cant, ap.PreArtNom, ifnull(p.PreArtCod,a.Preartcod) preartcod," +
                            " FechaPedido, ifnull(NotaInv,'N') NotaInv,ifnull(NotaCar,'N') NotaCar " +
                            "from Articulos a " +
                            "left join ClientesDevoluciones d on d.nitsec='" + nitsec + "' and d.clisec='" + clisec + "' and d.artsec=a.artsec " +
                            "left join Clientes cc on cc.nitsec='" + nitsec + "' and cc.clisec='" + clisec + "' " +
                            "left join pedido p on prefijo='" + prefijo + "' and p.nitsec='" + nitsec + "' and p.clisec=" + clisec + " and p.artsec=a.artsec " +
                            "left join articulospresentacion ap on ap.ArtSec=a.artsec and ap.PreArtcod = ifnull(p.PreArtCod,a.PreArtcod) and ap.LisPrecod=" + lisprecod + " " +
                            "where (cant+ifnull(cantinf,0)) <>0 " +
                            "and pdyear=" + time.year + " " +
                            "and prefijo= '" + prefijo + "' " +
                            "and p.nitsec = '" + nitsec + "' " +
                            "and p.clisec= " + clisec + " " +
                            "and pdmonth=" + (time.month + 1) + " " +
                            "and pdday=" + time.monthDay + " " +

                            "union " +

                            "select m.MovParPremArtSec ArtSec, a.ArtCod, a.ArtNom, 0 precio, MovParPremCant cant, '' PreArtNom, a.preartcod," +
                            " ''  FechaPedido,'N' NotaInv,'N' NotaCar " +
                            "from MovParPrem m " +
                            "left join articulos a on a.artsec=m.MovParPremArtSec " +
                            "where MovParPremCant > 0 and Prefijo='" + prefijo + "' " +
                            "and MovParNitSec='" + nitsec + "' " +
                            "and MovParCliSec=" + clisec + " " +
                            "and MovParPremAno=" + time.year + " " +
                            "and MovParPremMes=" + (time.month + 1) + " " +
                            "and MovParPremDia=" + time.monthDay +

                            ") jj order by FechaPedido asc";
            Cursor pedido = BaseDeDatos.getWritableDatabase().rawQuery(consultapedido, null);
            int unidades = 0;
            int cajas = 0;
            double finaltotal = 0.0;
            double finaltotalmalo = 0.0;
            double finaltotalbueno = 0.0;

            PdfFont fontNormal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);


            if (pedido.moveToFirst()) {

                int colArtCod = pedido.getColumnIndex("ArtCod");
                int colArtNom = pedido.getColumnIndex("ArtNom");
                int colPrecio = pedido.getColumnIndex("precio");
                int colCant = pedido.getColumnIndex("cant");
                int colpreartnom= pedido.getColumnIndex("PreArtNom");
                int colpreartcod = pedido.getColumnIndex("preartcod");
                int colNotaInva = pedido.getColumnIndex("NotaInv");
                int colNotacar = pedido.getColumnIndex("NotaCar");


                do {

                    String codigo = pedido.getString(colArtCod);
                    String xnombre = pedido.getString(colArtNom);
                    String prenombre = pedido.getString(colpreartnom);
                    String NotaInv = pedido.getString(colNotaInva);
                    String NotaCar = pedido.getString(colNotacar);
                    String tipo = "PED";

                    double precio = pedido.getDouble(colPrecio);
                    double cant = pedido.getDouble(colCant);

                    double total = precio * cant;

                    if(NotaInv.equalsIgnoreCase("N") && NotaCar.equalsIgnoreCase("N")){
                        finaltotal += total;
                        if(pedido.getInt(colpreartcod) == 9){
                            unidades += cant;
                        }else{
                            cajas += cant;
                        }
                    }


                    Color color = ColorConstants.BLACK;




                    if(NotaInv.equalsIgnoreCase("S") ){
                        finaltotalbueno += total;
                        color  = hex("#009688");
                        tipo = "CRB";
                    }

                    if(NotaCar.equalsIgnoreCase("S") ){
                        finaltotalmalo += total;
                        color  = hex("#E91E63");
                        tipo = "CRM";
                    }





                    table.addCell(celda(tipo, fontNormal, color));
                    table.addCell(celda(codigo, fontNormal, color));
                    table.addCell(celda(xnombre, fontNormal, color));
                    table.addCell(celda(prenombre, fontNormal, color));
                    table.addCell(celda(String.valueOf(cant), fontNormal, color));
                    table.addCell(celda("$" + String.format("%.2f", precio), fontNormal, color));
                    table.addCell(celda("$" + String.format("%.2f", total), fontNormal, color));

                } while (pedido.moveToNext());
            }

            pedido.close();

            document.add(table);
            document.add(new Paragraph("\n"));

            float[] columnasTotales = {1,1};
            Table totales = new Table(columnasTotales);
            totales.setWidth(UnitValue.createPercentValue(100));

// lado izquierdo
            Paragraph izquierda = new Paragraph()
                    .add("Total Cajas: "+String.valueOf(cajas)+"\n")
                    .add("Total Unidades:"+String.valueOf(unidades));

// lado derecho
            Paragraph derecha = new Paragraph()
                    .add(new Text("Total Credito Bueno: ")
                            .setFontColor(hex("#009688")))
                    .add(new Text(String.format("%.2f", finaltotalbueno) + "\n")
                            .setFontColor(hex("#009688")))   // verde

                    .add(new Text("Total Credito Malo: ")
                            .setFontColor(hex("#E91E63")))
                    .add(new Text(String.format("%.2f", finaltotalmalo) + "\n")
                            .setFontColor(hex("#E91E63")))   // rojo

                    .add(new Text("Total: ")
                            .setFontColor(ColorConstants.BLACK))
                    .add(new Text(String.format("%.2f", finaltotal) + "\n")
                            .setFontColor(ColorConstants.BLACK))   // teal

                    .setTextAlignment(TextAlignment.RIGHT);

// agregar celdas
            totales.addCell(new Cell().add(izquierda).setBorder(Border.NO_BORDER));
            totales.addCell(new Cell().add(derecha).setBorder(Border.NO_BORDER));

            document.add(totales);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }
    private static DeviceRgb hex(String hex) {

        hex = hex.replace("#", "");

        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);

        return new DeviceRgb(r, g, b);
    }
    private static  Cell celda(String texto, PdfFont fuente, Color color) {

        Paragraph p = new Paragraph(texto)
                .setFont(fuente)
                .setFontColor(color);

        return new Cell().add(p);
    }


}
