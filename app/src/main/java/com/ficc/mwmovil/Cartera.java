package com.ficc.mwmovil;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Cartera extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    SDTCartera[] SDTCartera ;
    ListViewAdapterCartera ListViewAdapterCartera;
    Bundle Extras;
    Switch cartercompa;
    Integer UltimaPosicion=0;
    ListView list_clientes;
    String dirpath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartera);
        Extras=this.getIntent().getExtras();
        String nitsec=Extras.getString("nitsec");
        Integer clisec=Extras.getInt("clisec");
        getSupportActionBar().hide();

        cartercompa = findViewById(R.id.cartercompa);
        verifyStoragePermissions(this);

//        GestorCartera gestorCartera = new GestorCartera();
  //      gestorCartera.TotalesCatera(getApplicationContext(),nitsec,clisec);

  //      txt_carteragen.setText(gestorCartera.CarteraVendedor);

        Log.e("NitSeccartera",nitsec);
        Log.e("cliseccartera",String.valueOf(clisec));
        BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
        Cursor cursorcli = BaseDeDatos.getReadableDatabase().rawQuery("select ifnull(CliCartCom,'N') CliCartCom from clientes where nitsec='" + nitsec + "' and clisec=" + clisec + " ", null);
cursorcli.moveToFirst();
        cartercompa.setEnabled(false);
           if(cursorcli.getCount() > 0){
               cursorcli.moveToFirst();
               if(cursorcli.getString(0).equalsIgnoreCase("S")){
                   cartercompa.setEnabled(true);
               }
           }
        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();

        Time time = new Time();
        time.setToNow();

        Integer TotalCartera=0;
        Integer TotalAbonos=0;
        try {
            Cursor cursor = null;
            if(cartercompa.isChecked()){
                cursor = BaseDeDatos.getReadableDatabase().rawQuery("select MovFacSec,FacConPag,FacFec,FacVen,FacMora,FacTotalImpuestos,FacAbonos,FacSaldo,(select ifNULL(sum(abono),0) abono from Recibo where nitsec='" + nitsec + "' and clisec=" + clisec + " and facnro=MovFacSec and rcyear=" + time.year + " and rcmonth=" + (time.month + 1) + " and rcday=" + time.monthDay +")   from cartera where movnitsec='"+nitsec+"' and movclisec="+clisec+" order by facmora desc", null);
            }else{
                cursor = BaseDeDatos.getReadableDatabase().rawQuery("select MovFacSec,FacConPag,FacFec,FacVen,FacMora,FacTotalImpuestos,FacAbonos,FacSaldo,(select ifNULL(sum(abono),0) abono from Recibo where nitsec='" + nitsec + "' and clisec=" + clisec + " and facnro=MovFacSec and rcyear=" + time.year + " and rcmonth=" + (time.month + 1) + " and rcday=" + time.monthDay +")   from cartera where movnitsec='"+nitsec+"' and movclisec="+clisec+" and FacVenCod ='"+vUsuario+"' order by facmora desc", null);
            }

            SDTCartera=new SDTCartera[cursor.getCount()];
            Integer vuelta=0;
            if (cursor.getCount()>0){
                cursor.moveToFirst();
                do {
                    try {
                        SDTCartera SDTCarteraItem= new SDTCartera();
                        SDTCarteraItem.FacNro = cursor.getString(0);
                        SDTCarteraItem.FacConPag = String.valueOf(cursor.getInt(1));
                        SDTCarteraItem.FacFec = cursor.getString(2);
                        SDTCarteraItem.FacFecVen = cursor.getString(3);
                        SDTCarteraItem.Mora = cursor.getString(4);
                        SDTCarteraItem.FacTotalImpuestos =String.format("%,d",cursor.getInt(5));
                        int abonos = cursor.getInt(6);

                        if(abonos<0){
                            abonos = 0;
                        }
                        SDTCarteraItem.FacAbonos = String.format("%,d",abonos);
                        SDTCarteraItem.FacSaldo = String.format("%,d",cursor.getInt(7));
                        SDTCarteraItem.FacAbonosReci = String.format("%,d",cursor.getInt(8));
                        TotalCartera+=cursor.getInt(7);
                        TotalAbonos+=cursor.getInt(8);
                        SDTCartera[vuelta]=SDTCarteraItem;
                    }catch (Exception e){
                        Integer Error=1;
                    }
                    vuelta=vuelta+1;
                } while (cursor.moveToNext());
            }





            TextView txt_carteraabonada = (TextView) findViewById(R.id.txt_carteraabonada);
            txt_carteraabonada.setText(String.format("%,d",TotalAbonos));
            TextView txt_carteragen = (TextView) findViewById(R.id.txt_carteragen);
            txt_carteragen.setText(String.format("%,d",TotalCartera));

            final ListView list_clientes = (ListView) findViewById(R.id.ListViewCartera);
            ListViewAdapterCartera = new ListViewAdapterCartera(this, SDTCartera);
            list_clientes.setAdapter(ListViewAdapterCartera);



          cartercompa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
              @Override
              public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                  datoscart();
              }
          });




            Button Imprimir = (Button) findViewById(R.id.Imprimir);
            Button Recibo = (Button) findViewById(R.id.Recibos);
            Recibo.setVisibility(View.GONE);
             vGlobalVariables= GlobalVariables.getInstance();
            String Empresa = vGlobalVariables.getEmpresa();
            if(Empresa.equalsIgnoreCase("IBANEZ") || Empresa.equalsIgnoreCase("IBANEZPRU") || Empresa.equalsIgnoreCase("SURTIMARCAS")){
                Recibo.setVisibility(View.VISIBLE);
            }
            Imprimir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), ImprimirRecibo.class);
                    intent.putExtra("nitsec", Extras.getString("nitsec"));
                    intent.putExtra("clisec", Extras.getInt("clisec"));
                    startActivity(intent);
                    /*try {
                        layoutToImage(getWindow().getDecorView().findViewById(android.R.id.content));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        imageToPDF();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }*/

                }
            });
            Recibo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                    String vUsuario=vGlobalVariables.getUsuario();
                    String recibopendien = "";
                     recibopendien = recibospendiente(vUsuario);
                    if( recibopendien.length() > 0){
                        AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                        Alerta.setMessage(recibopendien);
                        Alerta.setTitle("Notificacion");
                        Alerta.setPositiveButton("OK", null);
                        Alerta.setCancelable(true);
                        Alerta.create().show();
                    }else{
                        Intent intent = new Intent(view.getContext(), EnvioReciboPendiente.class);
                        intent.putExtra("nitsec", Extras.getString("nitsec"));
                        intent.putExtra("clisec", Extras.getInt("clisec"));
                        intent.putExtra("check", cartercompa.isChecked());
                        startActivity(intent);
                    }


                }
            });

            list_clientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView adapterView, final View view, int i, long l) {

                /*String pos=String.valueOf(i);

                AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                Alerta.setMessage(pos);
                Alerta.setTitle("Alerta");
                Alerta.setPositiveButton("OK", null);
                Alerta.setCancelable(true);
                Alerta.create().show();*/

/*
                    UltimaPosicion=i;
                    Intent intent = new Intent(getApplicationContext(), DescargarCartera.class);
                    intent.putExtra("nitsec", Extras.getString("nitsec"));
                    intent.putExtra("clisec", Extras.getInt("clisec"));
                    intent.putExtra("numero", SDTCartera[i].FacNro);
                    intent.putExtra("saldo", SDTCartera[i].FacSaldo);
                    //intent.putExtra("FacFec", SDTCartera[i].FacFec);
                    //  intent.putExtra("artsec",SDTProductos[i].ArtSec);
                    startActivityForResult(intent, 3);
                    */

                }
            });


        }catch (Exception e){
            Integer Error=1;
        }


    }

    private String recibospendiente(String vUsuario) {
        int Bandera = 0;
        String mensaje ="";
        Time time = new Time();
        time.setToNow();
        String NumPedido = "REC"+Extras.getString("nitsec")+Extras.getInt("clisec")+time.year +"-"+ (time.month + 1) +"-"+ time.monthDay;
        Log.e("NumPedido",NumPedido);
        String nConsulta = " select  count(*) total from reciboscaja1 where RecVenCod = 'B26' and " +
                " (RecFec = DATEADD(dd, 0, DATEDIFF(dd, 0, GETDATE())) and RecCheckTeso = 'S' and RecNro ='"+NumPedido+"' ) " +
                " and RecCajMovCheck = 'S'  ";
        Connection conn = null;
        Statement comm = null;
        ResultSet rsImport  = null;
        try {
            ConBd conbd = new ConBd();

            conn = conbd.CargarConexion(getApplicationContext());
            if (conn != null){
                comm = conn.createStatement();
                rsImport = comm.executeQuery(nConsulta);

                while (rsImport.next()) {
                    Bandera += rsImport.getInt("total");
                }
            }
        } catch (SQLException e) {
            Log.e("error",e.toString());
            Bandera = 0;
            throw new RuntimeException(e);


        }finally { // Cerramos las conexiones, en orden inverso a su apertura
            try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
            try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
        }

        if(Bandera > 0){
            mensaje = "Ya se acepto el recibo en tesoreria";
        }
        if(Bandera == 0){

            String consulta = " select  count(*) total from reciboscaja1 " +
                    "  where RecVenCod = '"+vUsuario+"' " +
                    "  and RecFec = DATEADD(dd, 0, DATEDIFF(dd, 0, GETDATE()))-1 " +
                    "  and RecEst = 'P' and (RecNroEstPen <> 'S' or RecNroEstPen is null)  " +
                    "  and RecCajMovCheck = 'S' ";
             conn = null;
             comm = null;
             rsImport  = null;
            try {
                ConBd conbd = new ConBd();

                conn = conbd.CargarConexion(getApplicationContext());
                if (conn != null){
                    comm = conn.createStatement();
                    rsImport = comm.executeQuery(consulta);

                    while (rsImport.next()) {
                        Bandera += rsImport.getInt("total");
                    }
                }
            } catch (SQLException e) {
                Log.e("error",e.toString());
                Bandera = 0;
                throw new RuntimeException(e);


            }finally { // Cerramos las conexiones, en orden inverso a su apertura
                try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
            }
            if(Bandera > 0){
                mensaje = "Tiene "+String.valueOf(Bandera)+"  recibos pendientes por aprobar del dia de ayer, no puede tomar nuevos recibos";
            }

        }




        return mensaje ;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 3) {
            //finish();
            //startActivity(getIntent());
            //if(resultCode == Activity) {
            //SDTProductos[UltimaPosicion].ActualizarDescuentos();
            //SDTProductos[UltimaPosicion].ActualizarCantidad();
            //SDTProductos[UltimaPosicion].Calcular();
            //SDTProductos[UltimaPosicion].Unidades=5.0;
            //getView
            //ListViewAdapterProductos.getv
            ListViewAdapterCartera.notifyDataSetChanged();
            //Totalizar();
            //ListViewAdapterProductos.Refrescar();
                /* String PrecioString=data.getStringExtra("PRECIO");
                Double Precio= Double.valueOf(PrecioString);
                SDTProductos[UltimaPosicion].Precio=Precio;
                ListViewAdapterProductos.notifyDataSetChanged();*/
            // }
        }

    }
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    public void layoutToImage(View view) {
        // get view group using reference
        FrameLayout relativeLayout = (FrameLayout) view.findViewById(R.id.framecompleto);
        // convert view group to bitmap
        relativeLayout.setDrawingCacheEnabled(true);
        relativeLayout.buildDrawingCache();
        Bitmap bm = relativeLayout.getDrawingCache();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//Environment.getExternalStorageDirectory()
        //Environment.getRootDirectory();
        //Environment.getExternalStorageDirectory().toString()
      /*  OutputStream fOut = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Uri outputFileUri;
        File sdImageMainDirectory = null;
        try {
            File root = new File(Environment.getExternalStorageDirectory()+"/MantisWeb/");
            root.mkdirs();
            File ruta_sd = Environment.getExternalStorageDirectory();
            File nuevaCarpeta = new File(ruta_sd.getAbsolutePath(), "MantisWeb");
            sdImageMainDirectory = new File(nuevaCarpeta, "image.jpg");
            outputFileUri = Uri.fromFile(sdImageMainDirectory);
            fOut = new FileOutputStream(sdImageMainDirectory);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();
        }*/

       File f = new File( Environment.getExternalStorageDirectory()+"/Mantis/"+ File.separator + "image.jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    public void imageToPDF() throws FileNotFoundException {
        File f = new File( Environment.getExternalStorageDirectory()+"/Mantis/"+ File.separator + "NewPDF.jpg");
        try {
            f.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Document document = new Document();
            dirpath = android.os.Environment.getExternalStorageDirectory().toString();
            PdfWriter.getInstance(document, new FileOutputStream(dirpath + "/Mantis/NewPDF.pdf")); //  Change pdf's name.

            document.open();
            Image img = Image.getInstance(Environment.getExternalStorageDirectory() + File.separator+"/Mantis/"+ "image.jpg");
            float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                    - document.rightMargin() - 0) / img.getWidth()) * 100;
            img.scalePercent(scaler);
            img.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
            document.add(img);
            document.close();
            Toast.makeText(this, "PDF Generated successfully!.." +dirpath + "Mantis/NewPDF.pdf", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void datoscart(){
        String nitsec=Extras.getString("nitsec");
        Integer clisec=Extras.getInt("clisec");

        cartercompa = findViewById(R.id.cartercompa);
        verifyStoragePermissions(this);

//        GestorCartera gestorCartera = new GestorCartera();
        //      gestorCartera.TotalesCatera(getApplicationContext(),nitsec,clisec);

        //      txt_carteragen.setText(gestorCartera.CarteraVendedor);

        BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
        Cursor cursorcli = BaseDeDatos.getReadableDatabase().rawQuery("select ifnull(CliCartCom,'N') CliCartCom from clientes where nitsec='" + nitsec + "' and clisec=" + clisec + " ", null);
        cursorcli.moveToFirst();
        cartercompa.setEnabled(false);
        if(cursorcli.getCount() > 0){
            cursorcli.moveToFirst();
            if(cursorcli.getString(0).equalsIgnoreCase("S")){
                cartercompa.setEnabled(true);
            }
        }
        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();

        Time time = new Time();
        time.setToNow();

        Integer TotalCartera=0;
        Integer TotalAbonos=0;
        try {
            Cursor cursor = null;
            if (cartercompa.isChecked()) {
                cursor = BaseDeDatos.getReadableDatabase().rawQuery("select MovFacSec,FacConPag,FacFec,FacVen,FacMora,FacTotalImpuestos,FacAbonos,FacSaldo,(select ifNULL(sum(abono),0) abono from Recibo where nitsec='" + nitsec + "' and clisec=" + clisec + " and facnro=MovFacSec and rcyear=" + time.year + " and rcmonth=" + (time.month + 1) + " and rcday=" + time.monthDay + ")   from cartera where movnitsec='" + nitsec + "' and movclisec=" + clisec + " order by facmora desc", null);
            } else {
                cursor = BaseDeDatos.getReadableDatabase().rawQuery("select MovFacSec,FacConPag,FacFec,FacVen,FacMora,FacTotalImpuestos,FacAbonos,FacSaldo,(select ifNULL(sum(abono),0) abono from Recibo where nitsec='" + nitsec + "' and clisec=" + clisec + " and facnro=MovFacSec and rcyear=" + time.year + " and rcmonth=" + (time.month + 1) + " and rcday=" + time.monthDay + ")   from cartera where movnitsec='" + nitsec + "' and movclisec=" + clisec + " and FacVenCod ='" + vUsuario + "' order by facmora desc", null);
            }

            SDTCartera = new SDTCartera[cursor.getCount()];
            Integer vuelta = 0;
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    try {
                        SDTCartera SDTCarteraItem = new SDTCartera();
                        SDTCarteraItem.FacNro = cursor.getString(0);
                        SDTCarteraItem.FacConPag = String.valueOf(cursor.getInt(1));
                        SDTCarteraItem.FacFec = cursor.getString(2);
                        SDTCarteraItem.FacFecVen = cursor.getString(3);
                        SDTCarteraItem.Mora = cursor.getString(4);
                        SDTCarteraItem.FacTotalImpuestos = String.format("%,d", cursor.getInt(5));
                        SDTCarteraItem.FacAbonos = String.format("%,d", cursor.getInt(6));
                        SDTCarteraItem.FacSaldo = String.format("%,d", cursor.getInt(7));
                        SDTCarteraItem.FacAbonosReci = String.format("%,d", cursor.getInt(8));
                        TotalCartera += cursor.getInt(7);
                        TotalAbonos += cursor.getInt(8);
                        SDTCartera[vuelta] = SDTCarteraItem;
                    } catch (Exception e) {
                        Integer Error = 1;
                    }
                    vuelta = vuelta + 1;
                } while (cursor.moveToNext());
            }


            TextView txt_carteraabonada = (TextView) findViewById(R.id.txt_carteraabonada);
            txt_carteraabonada.setText(String.format("%,d", TotalAbonos));
            TextView txt_carteragen = (TextView) findViewById(R.id.txt_carteragen);
            txt_carteragen.setText(String.format("%,d", TotalCartera));

            final ListView list_clientes = (ListView) findViewById(R.id.ListViewCartera);
            ListViewAdapterCartera = new ListViewAdapterCartera(this, SDTCartera);
            list_clientes.setAdapter(ListViewAdapterCartera);




    }catch (Exception e){
            Log.e("e",e.toString());
        }


    }

    public void onRestart() {
        super.onRestart();
        super.onResume();
        finish();
        startActivity(getIntent());
    }
}
