package com.ficc.mwmovil;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Locale;

public class ListViewAdapterPedidosEnviadosNew extends BaseAdapter {


    Context context;
    SDTPedidosEnviados[] SDTPedidosEnviados;

    public ListViewAdapterPedidosEnviadosNew(Context context, SDTPedidosEnviados[] SDTPedidosEnviados) {
        this.context=context;
        this.SDTPedidosEnviados = SDTPedidosEnviados;
    }
    public String banderadia(){
        String bander = "S";
        try {
            ConBd conbd = new ConBd();
            conbd.Variables();
            String sincroniza = conbd.sincronizalinea;
            Time time = new Time();
            int Anio = 0;
            int mes = 0;
            int Dia = 0;
            int Anioant = 0;
            int mesant = 0;
            int Diaant = 0;
            time.setToNow();
            Connection conn = conbd.CargarConexion(context);


            Statement comm = conn.createStatement();
            String  Script="SELECT YEAR(GETDATE()) AS Anio, MONTH(GETDATE()) AS Mes, DAY(GETDATE()) AS Dia, YEAR(GETDATE()-1) AS Anioant, MONTH(GETDATE()-1) AS Mesant, DAY(GETDATE()-1) AS Diaant;";
            classbd classbd = new classbd();
            ResultSet rsImport = comm.executeQuery(classbd.FormatearMysql(Script));
            while (rsImport.next()) {
                Anio = rsImport.getInt("Anio");
                mes = rsImport.getInt("Mes");
                Dia = rsImport.getInt("Dia");
                Anioant = rsImport.getInt("Anioant");
                mesant = rsImport.getInt("Mesant");
                Diaant = rsImport.getInt("Diaant");
            }

            if(Anio == time.year && mes == (time.month + 1) && Dia == time.monthDay){
                bander = "S";
            }
            if(Anioant == time.year && mesant == (time.month + 1) && Diaant == time.monthDay){
                bander = "S";
            }


        } catch (Exception e) {
            Log.e("error",e.toString());
            bander = "S";
        }


        return bander;
    }
    @Override
    public int getCount() {
        return SDTPedidosEnviados.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    @SuppressLint({"MissingInflatedId", "DefaultLocale"})
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.listarpedidos,parent, false);
        try{

            TextView txt_nombrecliente = (TextView) itemView.findViewById(R.id.txt_nombrecliente);
            TextView txt_bodega = (TextView) itemView.findViewById(R.id.txt_bodega);
            TextView txt_nombrenegocio = (TextView) itemView.findViewById(R.id.txt_nombrenegocio);
            TextView txt_pedido = (TextView) itemView.findViewById(R.id.txt_pedido);
            TextView txt_valorpedido = (TextView) itemView.findViewById(R.id.txt_valorpedido);
            TextView txt_creditomalo = (TextView) itemView.findViewById(R.id.txt_creditomalo);
            TextView txt_creditobueno = (TextView) itemView.findViewById(R.id.txt_creditobueno);
            TextView txt_valorpedidoenv = (TextView) itemView.findViewById(R.id.txt_valorpedidoenv);
            TextView txt_porpedidoenv = (TextView) itemView.findViewById(R.id.txt_porpedidoenv);
            TextView tvNuevaFecha = (TextView) itemView.findViewById(R.id.tvNuevaFecha);


            TextView txt_valorpedidoenvalt = (TextView) itemView.findViewById(R.id.txt_valorpedidoenvalt);
            TextView txt_porpedidoenvalt = (TextView) itemView.findViewById(R.id.txt_porpedidoenvalt);
            ImageButton btnBorrar = itemView.findViewById(R.id.btnBorrar);
            ImageButton btnNuevaFecha =
                    itemView.findViewById(R.id.btnNuevaFecha);


             LinearLayout contenedorpedido = itemView.findViewById(R.id.contenedorpedido);
            contenedorpedido.setBackgroundColor(Color.WHITE);



            CheckBox checkBox_enviado = (CheckBox) itemView.findViewById(R.id.checkBox_enviado);
            CheckBox checkBox_envalt = (CheckBox) itemView.findViewById(R.id.checkBox_envalt);


            TextView txt_pedenviado = (TextView) itemView.findViewById(R.id.pedenviado);
            txt_bodega.setText(SDTPedidosEnviados[position].BodNom);
            txt_nombrecliente.setText(SDTPedidosEnviados[position].NombreCliente);
            txt_nombrenegocio.setText(SDTPedidosEnviados[position].NombreNegocio);
            txt_pedido.setText(SDTPedidosEnviados[position].NumeroPedido);
            txt_pedenviado.setText(SDTPedidosEnviados[position].checkenviado);

            Button compartir = (Button) itemView.findViewById(R.id.compartir);
            Button Json = (Button) itemView.findViewById(R.id.Json);

            Button btn_Emviar = (Button) itemView.findViewById(R.id.button3Env);
            BaseDatos BaseDeDatos;
            BaseDeDatos = new BaseDatos(context, "MantisMovil", null, 5);
            GlobalVariables gGlobalVariables = GlobalVariables.getInstance();
            String vEmpresa=gGlobalVariables.getEmpresa();
            Button btnEnvPen = (Button) itemView.findViewById(R.id.btnEnvPen);
            btnEnvPen.setVisibility(View.GONE);
            LinearLayout contPedAlt =  itemView.findViewById(R.id.contentenvalt);
            contPedAlt.setVisibility(View.GONE);
       btnEnvPen.setVisibility(View.VISIBLE);
            btnBorrar.setVisibility(View.VISIBLE);

                //Nuevo andres ------------------- envioalt
                /*Cursor permisoenvalt =  BaseDeDatos.getReadableDatabase().rawQuery("select EnvAlt from usuarios where VenCnt=1  ", null);
                if(permisoenvalt.getCount()> 0){
                    permisoenvalt.moveToFirst();
                    if(permisoenvalt.getString(0).equalsIgnoreCase("S")){*/
                        btnEnvPen.setText("Enviar");
                        btn_Emviar.setVisibility(View.GONE);
                        contPedAlt.setVisibility(View.VISIBLE);

                //Envioalt --------------------------------------





            //+'-'+SDTPedidosEnviados[position].sEnviado

            txt_valorpedido.setText(String.format("%.2f",SDTPedidosEnviados[position].ValorPedido));
            txt_creditomalo.setText(String.format("%.2f",SDTPedidosEnviados[position].CreditoMalo));
            txt_creditobueno.setText(String.format("%.2f",SDTPedidosEnviados[position].CreditoBueno));


            if (!SDTPedidosEnviados[position].sEnviadoExi.equalsIgnoreCase("0.00")) {
                txt_valorpedidoenv.setText("Sin Exi = " + SDTPedidosEnviados[position].sEnviadoExi);
            }else{

                txt_valorpedidoenv.setText("");
            }
            if (SDTPedidosEnviados[position].FacFecEnt == null ||
                    SDTPedidosEnviados[position].FacFecEnt.isEmpty()) {
                tvNuevaFecha.setText("Nueva fecha");
                contenedorpedido.setBackgroundColor(Color.parseColor("#95F7F8CD"));

            } else {
                tvNuevaFecha.setText(SDTPedidosEnviados[position].FacFecEnt);
            }

            btnNuevaFecha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    seleccionarFechaLista(tvNuevaFecha,position);
                }
            });






            if (!SDTPedidosEnviados[position].sEnviado.equalsIgnoreCase("0.00") ){
                //    if(SDTPedidosEnviados[position].checkenviado.equalsIgnoreCase("S") ){ // andres
                checkBox_enviado.setChecked(true);
                txt_valorpedidoenv.setText( SDTPedidosEnviados[position].sEnviado);
                double enviado =SDTPedidosEnviados[position].ValorPedidoEnviado ;
                double pedido =SDTPedidosEnviados[position].ValorPedido ;
                if(pedido == 0.0){
                    pedido =SDTPedidosEnviados[position].CreditoMalo + SDTPedidosEnviados[position].CreditoBueno ;
                }

                double porcentajeenviado = 0.0;
                if(pedido > 0){
                    porcentajeenviado =   (enviado/pedido)*100;
                }


                int redondeo = (int) Math.round(porcentajeenviado);



                if(redondeo >= 97 && redondeo <= 101){
                    redondeo = 100;
                   btnEnvPen.setVisibility(View.GONE);
                    btnBorrar.setVisibility(View.GONE);
                }



                txt_porpedidoenv.setText(String.valueOf(redondeo)+"%");



            } else{
                txt_valorpedidoenv.setText("0");
                txt_porpedidoenv.setText("0%");
            }


            //Nuevo andres ------------------- envioalt
            if (!SDTPedidosEnviados[position].valenvalt.equalsIgnoreCase("0.00") ){
                //    if(SDTPedidosEnviados[position].checkenviado.equalsIgnoreCase("S") ){ // andres
                checkBox_envalt.setChecked(true);
                txt_valorpedidoenvalt.setText( SDTPedidosEnviados[position].valenvalt);
                double enviado =SDTPedidosEnviados[position].ValorPedidoEnvialt ;
                double pedido =SDTPedidosEnviados[position].ValorPedido ;
                if(pedido == 0.0){
                    pedido =SDTPedidosEnviados[position].CreditoMalo + SDTPedidosEnviados[position].CreditoBueno ;
                }
                double porcentajeenviado = 0.0;
                if(pedido > 0){
                     porcentajeenviado =   (enviado/pedido)*100;
                }


                int redondeo = (int) Math.round(porcentajeenviado);
                if(redondeo >= 97 && redondeo <= 101){
                    redondeo = 100;
                    btnEnvPen.setVisibility(View.GONE);
                    btnBorrar.setVisibility(View.GONE);
                }

                txt_porpedidoenvalt.setText(String.valueOf(redondeo)+"%");

            } else{
                txt_valorpedidoenvalt.setText("0");
                txt_porpedidoenvalt.setText("0%");
            }
            // Envio alt ---------------------------------

            Json.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GestorPedidos GestorPedidos = new GestorPedidos();
                    String Parametros = "Prefijo: "+SDTPedidosEnviados[position].prefijo+"\n" +
                            " NitSec:  "+ SDTPedidosEnviados[position].sNitSec+"\n"+
                            " Clisec : "+ String.valueOf(SDTPedidosEnviados[position].clisec)+"";
                    //String   Mensaje = GestorPedidos.EnviarPedidospendientesSOLVEGAJSON(context, SDTPedidosEnviados[position].prefijo, SDTPedidosEnviados[position].sNitSec, SDTPedidosEnviados[position].clisec, 0, "", SDTPedidosEnviados[position].ValorPedido.intValue());
                    mostrarPopupTexto(Parametros,GestorPedidos.EnviarPedidosFiccGx5Json(context,
                            SDTPedidosEnviados[position].prefijo,
                            SDTPedidosEnviados[position].sNitSec,
                            SDTPedidosEnviados[position].clisec, 0),position);


                }
            });

            btnBorrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(context)
                            .setTitle("Confirmación")
                            .setMessage("¿Deseas eliminar el pedido?")
                            .setIcon(android.R.drawable.ic_dialog_alert)

                            .setPositiveButton("Sí", (dialog, which) -> {
                                eliminarPedido(position);
                                SDTPedidosEnviados = eliminarElemento(SDTPedidosEnviados, position);
                                notifyDataSetChanged();

                            })

                            .setNegativeButton("No", (dialog, which) -> {
                                dialog.dismiss(); // Cierra el diálogo
                            })

                            .show();
                }
            });

            compartir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    File file = ReportePedido.generarPDF(context,SDTPedidosEnviados[position].sNitSec,
                            SDTPedidosEnviados[position].NumeroPedido, SDTPedidosEnviados[position].clisec,
                            SDTPedidosEnviados[position].prefijo,SDTPedidosEnviados[position].LisPreCod);

                    Intent intent = new Intent(Intent.ACTION_VIEW);

                    Uri uri = FileProvider.getUriForFile(
                            context,
                            context.getPackageName() + ".fileprovider",
                            file
                    );

                    intent.setDataAndType(uri, "application/pdf");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    context.startActivity(intent);
                }
            });

            btnEnvPen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String Mensaje = "";

                    try {

                        if (SDTPedidosEnviados[position].FacFecEnt == null ||
                                SDTPedidosEnviados[position].FacFecEnt.trim().isEmpty()) {

                                seleccionarFecha(position);

                        }else{

                            Intent intent = new Intent(context, ConfirmarEnvio.class);
                            intent.putExtra("total", 1);
                            intent.putExtra("clisec",SDTPedidosEnviados[position].clisec);
                            intent.putExtra("nitsec",SDTPedidosEnviados[position].sNitSec);
                            intent.putExtra("artsec","");
                            intent.putExtra("prefijo",SDTPedidosEnviados[position].prefijo);
                            intent.putExtra("position",position);
                            intent.putExtra("envioalt",1);
                            ((Wp_ResumenPedidosnew) context).startActivityForResult(intent, 1600);

                            btnEnvPen.setVisibility(View.GONE);
                            btnBorrar.setVisibility(View.GONE);
                        }

                    } catch (Exception e) {
                        int aa = 0;
                        e.printStackTrace();
                    }


                }
            });

            Button btn_ver = (Button) itemView.findViewById(R.id.button4ver);
            btn_ver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent i = new Intent(context, ResumenPedidoEnv.class);
                        i.putExtra("nitsec", SDTPedidosEnviados[position].sNitSec);
                        i.putExtra("invfamcod", "");
                        i.putExtra("invsubgrucod", "");
                        i.putExtra("Numped",SDTPedidosEnviados[position].NumeroPedido);
                        i.putExtra("invgrucod", "");
                        i.putExtra("clisec", SDTPedidosEnviados[position].clisec);
                        i.putExtra("lisprecod", "");
                        i.putExtra("prefijo", SDTPedidosEnviados[position].prefijo);
                        context.startActivity(i);
                    }catch (Exception e){
                        int aa=0;
                    }
                }
            });



            //
        }catch (Exception e){
            int hh=0;
        }

        return itemView;

    }




    private void seleccionarFechaLista(TextView tvNuevaFecha,int position) {


                    Calendar calendario = Calendar.getInstance();

                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            context,
                            (dview, year, month, dayOfMonth) -> {

                                // Mes empieza en 0
                                month = month + 1;

                                String fechaSeleccionada = String.format(
                                        Locale.getDefault(),
                                        "%04d-%02d-%02d",
                                        year,
                                        month,
                                        dayOfMonth
                                );

                                // Actualizar el objeto de la lista
                                SDTPedidosEnviados[position].FacFecEnt = fechaSeleccionada;

                                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                                String vUsuario=vGlobalVariables.getUsuario();
                                final Time time = new Time();
                                time.setToNow();
                                try {
                                    String prefijo = SDTPedidosEnviados[position].prefijo + vUsuario.trim() + SDTPedidosEnviados[position].sNitSec + SDTPedidosEnviados[position].clisec + time.year + "-" + (time.month + 1) + "-" + time.monthDay;
                                    BaseDatos BaseDeDatos;
                                    BaseDeDatos = new BaseDatos(context, "MantisMovil", null, 5);
                                    String consulta = "update pedido set FacFecEnt = '" + fechaSeleccionada + "'   where (prefijo||'" + vUsuario + "'||NitSec||CliSec||" + time.year + "||'-'||" + (time.month + 1) + "||'-'||" + time.monthDay + ")  = '" + prefijo + "' ";
                                    BaseDeDatos.getWritableDatabase().execSQL(consulta);
                                    Notificacion.exito(context,"Guardado exitoso");
                                } catch (Exception e) {
                                    Notificacion.error(context,"Error guardando Fecga "+e.toString());
                                    return;
                                }

                                // Refrescar la lista
                                notifyDataSetChanged();
                                tvNuevaFecha.setText(fechaSeleccionada);

                                /*btnEnvPen.setVisibility(dview.GONE);
                                btnBorrar.setVisibility(dview.GONE);*/

                            },
                            calendario.get(Calendar.YEAR),
                            calendario.get(Calendar.MONTH),
                            calendario.get(Calendar.DAY_OF_MONTH)
                    );

                    datePickerDialog.setTitle("Seleccione fecha de entrega");
                    datePickerDialog.show();


    }

    private void seleccionarFecha(int position) {
        new AlertDialog.Builder(context)
                .setTitle("Fecha de entrega")
                .setMessage("Este pedido no tiene fecha promesa de entrega. Por favor seleccione una fecha.")
                .setPositiveButton("Seleccionar fecha", (dialog, which) -> {

                    Calendar calendario = Calendar.getInstance();

                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            context,
                            (dview, year, month, dayOfMonth) -> {

                                // Mes empieza en 0
                                month = month + 1;

                                String fechaSeleccionada = String.format(
                                        Locale.getDefault(),
                                        "%04d-%02d-%02d",
                                        year,
                                        month,
                                        dayOfMonth
                                );

                                // Actualizar el objeto de la lista
                                SDTPedidosEnviados[position].FacFecEnt = fechaSeleccionada;

                                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                                String vUsuario=vGlobalVariables.getUsuario();
                                final Time time = new Time();
                                time.setToNow();
                                try {
                                    String prefijo = SDTPedidosEnviados[position].prefijo + vUsuario.trim() + SDTPedidosEnviados[position].sNitSec + SDTPedidosEnviados[position].clisec + time.year + "-" + (time.month + 1) + "-" + time.monthDay;
                                    BaseDatos BaseDeDatos;
                                    BaseDeDatos = new BaseDatos(context, "MantisMovil", null, 5);
                                    String consulta = "update pedido set FacFecEnt = '" + fechaSeleccionada + "'   where (prefijo||'" + vUsuario + "'||NitSec||CliSec||" + time.year + "||'-'||" + (time.month + 1) + "||'-'||" + time.monthDay + ")  = '" + prefijo + "' ";
                                    BaseDeDatos.getWritableDatabase().execSQL(consulta);
                                    Notificacion.exito(context,"Guardado exitoso");
                                } catch (Exception e) {
                                    Notificacion.error(context,"Error guardando Fecga "+e.toString());
                                    return;
                                }

                                // Refrescar la lista
                                notifyDataSetChanged();

                                // Continuar con el envío
                                Intent intent = new Intent(context, ConfirmarEnvio.class);

                                intent.putExtra("total", 1);
                                intent.putExtra(
                                        "clisec",
                                        SDTPedidosEnviados[position].clisec
                                );
                                intent.putExtra(
                                        "nitsec",
                                        SDTPedidosEnviados[position].sNitSec
                                );
                                intent.putExtra("artsec", "");
                                intent.putExtra(
                                        "prefijo",
                                        SDTPedidosEnviados[position].prefijo
                                );
                                intent.putExtra("position", position);
                                intent.putExtra("envioalt", 1);

                                ((Wp_ResumenPedidosnew) context)
                                        .startActivityForResult(intent, 1600);

                                /*btnEnvPen.setVisibility(dview.GONE);
                                btnBorrar.setVisibility(dview.GONE);*/

                            },
                            calendario.get(Calendar.YEAR),
                            calendario.get(Calendar.MONTH),
                            calendario.get(Calendar.DAY_OF_MONTH)
                    );

                    datePickerDialog.setTitle("Seleccione fecha de entrega");
                    datePickerDialog.show();

                })
                .setNegativeButton("Cancelar", null)
                .show();

    }

    public void eliminarPedido(int position){
        Time time = new Time();
        time.setToNow();
        BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(context, "MantisMovil", null, 5);

        BaseDeDatos.getWritableDatabase().execSQL("delete from pedido where nitsec='" + SDTPedidosEnviados[position].sNitSec + "' and clisec=" + SDTPedidosEnviados[position].clisec + " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo='" + SDTPedidosEnviados[position].prefijo+ "' ");
        BaseDeDatos.getWritableDatabase().execSQL("delete from movparprem where MovParNitSec='" + SDTPedidosEnviados[position].sNitSec + "' and MovParCliSec=" + SDTPedidosEnviados[position].clisec + " and MovParPremAno=" + time.year + " and MovParPremMes=" + (time.month + 1) + " and MovParPremDia=" + time.monthDay + "  and prefijo='" + SDTPedidosEnviados[position].prefijo+ "' ");
    }



    public void mostrarPopupTexto(String Parametros,String texto,int position) {
        String textoFormateado = texto;


        try {
            // Detecta si es objeto o array
            if (texto.trim().startsWith("[")) {
                JSONArray jsonArray = new JSONArray(texto);
                textoFormateado = jsonArray.toString(4); // indentación
            } else {
                JSONObject jsonObject = new JSONObject(texto);
                textoFormateado = jsonObject.toString(4);
            }
        } catch (Exception e) {
            // Si falla, muestra el original
            textoFormateado = texto;
        }

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setTitle("Contenido");
        builder.setMessage(Parametros);
        // Vista personalizada
        TextView textView = new TextView(context);
        textView.setText(textoFormateado);
        textView.setPadding(40, 40, 40, 40);
        textView.setHeight(1000);
        textView.setTextIsSelectable(true); // permite seleccionar texto

        builder.setView(textView);

        // Botón COPIAR
        builder.setPositiveButton("Copiar", (dialog, which) -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("texto", texto);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(context, "Copiado al portapapeles", Toast.LENGTH_SHORT).show();
        });

        // Botón COMPARTIR
        builder.setNeutralButton("Compartir", (dialog, which) -> {
            /*Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, texto);

            context.startActivity(Intent.createChooser(intent, "Compartir texto"));*/
            try{
                File file = new File(context.getExternalFilesDir(null), SDTPedidosEnviados[position].NumeroPedido+".json");
                FileWriter writer = new FileWriter(file);
                writer.write(texto);
                writer.close();



                Uri uri = FileProvider.getUriForFile(
                        context,
                        context.getPackageName() + ".fileprovider",
                        file
                );


                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                context.startActivity(Intent.createChooser(intent, "Enviar archivo"));


            } catch (Exception e) {
                Toast.makeText(context, "Error:"+e.toString(), Toast.LENGTH_SHORT).show();
            }





        });

        // Botón CERRAR
        builder.setNegativeButton("Cerrar", null);

        builder.show();
    }

    private SDTPedidosEnviados[] eliminarElemento(
            SDTPedidosEnviados[] lista,
            int posicion) {

        SDTPedidosEnviados[] nuevaLista =
                new SDTPedidosEnviados[lista.length - 1];

        for (int i = 0, j = 0; i < lista.length; i++) {

            if (i != posicion) {
                nuevaLista[j++] = lista[i];
            }
        }

        return nuevaLista;
    }



}
