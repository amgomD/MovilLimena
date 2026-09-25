package com.ficc.mwmovil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


public class ResumenRecibosDia extends AppCompatActivity {
    private final String ruta_fotos = Environment.getExternalStorageDirectory().toString() + "/MantisWeb/";
    private File file = new File(ruta_fotos);
    Uri uri;
    File mi_foto;
    String rrfile;
    Bundle Extras=null;
    private Uri imageUri;
    ResumenRecibosDia CameraActivity = null;
    String dirpath;
    static TextView imageDetails;
    static String Path;
    ImageView showImg ;
    SDTPedidosEnviados[] SDTPedidosEnviados ;
    ListViewAdapterRecibosEnviados aListViewAdapterRecibosEnviados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_recibos_dia);
        getSupportActionBar().hide();
        final GestorPedidos GestorPedidos = new GestorPedidos();
        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
        final Time time = new Time();
        time.setToNow();
        TextView btn_confirmar = (TextView) findViewById(R.id.btn_Capturar);
        showImg= (ImageView) findViewById(R.id.miFotoEnvio);
        imageDetails = (TextView) findViewById(R.id.txt_abonoori);
        CameraActivity=this;

        try {

            Double pedidoMinimo=0.0;

            Cursor vCursorUsuarios = BaseDeDatos.getReadableDatabase().rawQuery("select ParMovPedMin from usuarios where VenCnt=1  ", null);
            vCursorUsuarios.moveToFirst();

            if (vCursorUsuarios.getCount() >0) {
                pedidoMinimo=vCursorUsuarios.getDouble(0);
            }


            //String nitsec=Extras.getString("nitsec");
            //Integer clisec=Extras.getInt("clisec");
            //String invgrucod=Extras.getString("invgrucod");


            //Cursor cursor = BaseDeDatos.getWritableDatabase().rawQuery("select NitSec,CliSec,NitCom,CliNom from clientes where nitsec in( " +
            //      "select nitsec from pedido where pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and cant<>0) ", null); //order by nombre
            //*abono,retefue,retica,descuento from
            Cursor cursor = BaseDeDatos.getWritableDatabase().rawQuery("select p.NitSec,p.CliSec,c.NitCom,c.CliNom,sum( CASE  When tipo = 'N' Then abono*-1  else abono end ) abono,sum(retefue) retfue,sum(retica) retica,sum(descuento) retdes,sum(abono-retefue-retica-descuento) efectivo from recibo p left join clientes c on p.nitsec=c.nitsec and p.clisec=c.clisec " +
                    " where rcyear=" + time.year + " and rcmonth=" + (time.month + 1) + " and rcday=" + time.monthDay + " and ifNULL(abono,0)<>0 group by p.NitSec,p.CliSec,c.NitCom,c.CliNom ", null); //order by nombre
            int vuelta = 0;
            Double TotalEnviado=0.0;
            Double TotalPedidosRes=0.0;
            Double TotalSinExi=0.0;

            Double valAbono=0.0;
            Double valRetencion=0.0;
            Double valReteica=0.0;
            Double valDescuento=0.0;
            Double valTotal=0.0;

            final String[] InvGruCod;
            SDTPedidosEnviados = new SDTPedidosEnviados[cursor.getCount()];
            if (cursor.getCount() > 0) {

                cursor.moveToFirst();
                do {
                    valAbono += cursor.getDouble(4);
                    valRetencion += cursor.getDouble(5);
                    valReteica += cursor.getDouble(6);
                    valDescuento += cursor.getDouble(7);
                    valTotal += cursor.getDouble(8);

                    String NumPed = "REC" + cursor.getString(0) + cursor.getInt(1) + time.year + "-" + (time.month + 1) + "-" + time.monthDay;
                    SDTPedidosEnviados SDTPedidosEnviadosItem = new SDTPedidosEnviados();
                    SDTPedidosEnviadosItem.NombreCliente = cursor.getString(2);
                    SDTPedidosEnviadosItem.NombreNegocio = cursor.getString(3);
                    SDTPedidosEnviadosItem.sNitSec = cursor.getString(0);
                    SDTPedidosEnviadosItem.clisec = cursor.getInt(1);
                    SDTPedidosEnviadosItem.NumeroPedido = NumPed;
                    SDTPedidosEnviadosItem.ValorPedido = cursor.getDouble(4);
                    TotalPedidosRes += cursor.getDouble(4);
                    //   SDTPedidosEnviadosItem.ValorPedido = GestorPedidos.TotalesPedido(getApplicationContext(), cursor.getString(4), cursor.getString(0), cursor.getInt(1),"","","").Total;


                    Double valorenv =  GestorPedidos.Recibocajaenviado(getApplicationContext(), NumPed, cursor.getString(0), cursor.getInt(1));
                    SDTPedidosEnviadosItem.sEnviado = String.valueOf(valorenv.intValue());
                    SDTPedidosEnviadosItem.ValorPedidoEnviado=Double.valueOf(SDTPedidosEnviadosItem.sEnviado);
                    //SDTPedidosEnviadosItem.sEnviadoExi=GestorPedidos.SinExistencia;
                    SDTPedidosEnviadosItem.sEnviadoExi="0.00";
                    TotalEnviado+=SDTPedidosEnviadosItem.ValorPedidoEnviado;
                    //TotalSinExi+=Double.valueOf(GestorPedidos.SinExistencia);

                //    Double ValPed= GestorPedidos.TotalesPedido(getApplicationContext(), cursor.getString(4), cursor.getString(0), cursor.getInt(1),"","","").Subtotal;

                  //  if (ValPed<pedidoMinimo && pedidoMinimo>0){
                  //      SDTPedidosEnviadosItem.NumeroPedido = NumPed+" ERROR (PEDIDO POR DEBAJO DEL MINIMO) "+ValPed.toString().trim()+"- MIN = "+pedidoMinimo.toString();
                  //  }

                    //SDTPedidosEnviadosItem.Enviado = cursor.getDouble(2);
                    SDTPedidosEnviados[vuelta] = SDTPedidosEnviadosItem;
                    vuelta = vuelta + 1;
                } while (cursor.moveToNext());
            }

            final ListView listview_pedidosenviados = (ListView) findViewById(R.id.listview_pedidosenviados);
            aListViewAdapterRecibosEnviados = new ListViewAdapterRecibosEnviados(this, SDTPedidosEnviados);
            listview_pedidosenviados.setAdapter(aListViewAdapterRecibosEnviados);

            SDTResumenPedidos SDTResumenPedidos = GestorPedidos.TotalesPedido(getApplicationContext(), "", "", 0,"","","");

            TextView txt_abonoori = (TextView) findViewById(R.id.txt_abonoori);
            TextView txt_retencion = (TextView) findViewById(R.id.txt_retencion);
            TextView txt_retencionica = (TextView) findViewById(R.id.txt_retencionica);
            TextView txt_descuentos = (TextView) findViewById(R.id.txt_descuentos);
            TextView txt_neto = (TextView) findViewById(R.id.txt_neto);
            TextView txt_porenvio = (TextView) findViewById(R.id.txt_porenvio);
            TextView txt_porenvio7 = (TextView) findViewById(R.id.txt_porenvio7);

            TextView txt_numpedidos = (TextView) findViewById(R.id.txt_numpedidos);




            txt_abonoori.setText(String.format("%,d", valAbono.intValue()));
            txt_retencion.setText(String.format("%,d", valRetencion.intValue()));
            txt_retencionica.setText(String.format("%,d", valReteica.intValue()));
            txt_descuentos.setText(String.format("%,d", valDescuento.intValue()));
            txt_neto.setText(String.format("%,d", valTotal.intValue()));
            txt_numpedidos.setText(String.format("%,d",vuelta));

            Double PorEnvio=0.0;
            Integer jj = valTotal.intValue() ;//SDTResumenPedidos.Total.intValue();
            if(jj>0) {
                PorEnvio= (TotalEnviado / jj)*100;
                txt_porenvio7.setText(String.format("%,d",TotalPedidosRes.intValue())+"");
                txt_porenvio.setText(String.format("%,d",PorEnvio.intValue())+"%");
            }else{
                txt_porenvio.setText("0%");
            }

        }catch (Exception e){
            int hh=0;
        }

        final Button btn_enviarpedidos = (Button) findViewById(R.id.btn_enviarpedidos);

        btn_confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_enviarpedidos.setEnabled(false);
                AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                Alerta.setMessage("Confirmacion exitosa");
                Alerta.setTitle("Notificacion");
                Alerta.setPositiveButton("OK", null);
                Alerta.setCancelable(true);
                Alerta.create().show();

                BaseDeDatos.getWritableDatabase().execSQL(" insert into CirreDia (CirAno,CirMes,CirDay)values(" + time.year + "," + (time.month + 1) + "," + time.monthDay +")");
            }
        });

        final Button btn_EmviarSoporte = (Button) findViewById(R.id.btn_EmviarSoporte);

        btn_EmviarSoporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //btn_enviarpedidos.setEnabled(false);
                AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                Alerta.setMessage("Confirmacion exitosa");
                Alerta.setTitle("Notificacion");
                Alerta.setPositiveButton("OK", null);
                Alerta.setCancelable(true);
                Alerta.create().show();

                BaseDeDatos.getWritableDatabase().execSQL(" insert into CirreDia (CirAno,CirMes,CirDay)values(" + time.year + "," + (time.month + 1) + "," + time.monthDay +")");
            }
        });

        btn_enviarpedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Mensaje =GestorPedidos.EnviarRecibos(getApplicationContext(),"","",0);

                AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                Alerta.setMessage("Recibos enviados "+Mensaje);
                Alerta.setTitle("Notificacion");
                Alerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                         finish();
                         startActivity(getIntent());
                    }


                });
                Alerta.setCancelable(true);
                Alerta.create().show();
               // finish();
               // startActivity(getIntent());
            }
        });

        Button Capturar= (Button) findViewById(R.id.btn_Capturar);
        Capturar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    File filedir=new File(ruta_fotos); if(!filedir.exists()) filedir.mkdirs();
                    rrfile = ruta_fotos +"mwapp.jpg";
                    mi_foto=new File(ruta_fotos, "mwapp.jpg");
                    imageUri = Uri.fromFile(mi_foto);
                    uri = Uri.fromFile(mi_foto);
                    String fileName = "mwapp.jpg";
                    // Create parameters for Intent with filename
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, fileName);
                    values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");
                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    //Abre la camara para tomar la foto
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //Guarda imagen
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    //Retorna a la actividad
                    startActivityForResult(cameraIntent, 1);

                }catch (Exception e){
                    String aaaa = "mwapp.jpg";
                }
            }
        });

        Cursor Clientes = BaseDeDatos.getWritableDatabase().rawQuery("select * from CirreDia where CirDay=" + time.year + " and CirMes=" + (time.month + 1) + " and CirAno=" + (time.monthDay) , null);
        if (Clientes.getCount() > 0) {
            btn_enviarpedidos.setEnabled(false);

        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
                try {

                    if ( resultCode == RESULT_OK) {

                        /*********** Load Captured Image And Data Start ****************/

                        String imageId = convertImageUriToFile( imageUri,CameraActivity);


                        //  Create and excecute AsyncTask to load capture image

                        new ResumenRecibosDia.LoadImagesFromSDCard().execute(""+imageId);

                        /*********** Load Captured Image And Data End ****************/


                    } else if ( resultCode == RESULT_CANCELED) {

                        Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
                    } else {

                        Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
                    }


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    public static String convertImageUriToFile ( Uri imageUri, Activity activity )  {

        Cursor cursor = null;
        int imageID = 0;

        try {

            /*********** Which columns values want to get *******/
            String [] proj={
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Thumbnails._ID,
                    MediaStore.Images.ImageColumns.ORIENTATION
            };

            cursor = activity.managedQuery(

                    imageUri,         //  Get data for specific image URI
                    proj,             //  Which columns to return
                    null,             //  WHERE clause; which rows to return (all rows)
                    null,             //  WHERE clause selection arguments (none)
                    null              //  Order-by clause (ascending by name)

            );

            //  Get Query Data

            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int columnIndexThumb = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
            int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            //int orientation_ColumnIndex = cursor.
            //    getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION);

            int size = cursor.getCount();

            /*******  If size is 0, there are no images on the SD Card. *****/

            if (size == 0) {


                imageDetails.setText("No Image");
            }
            else
            {

                int thumbID = 0;
                if (cursor.moveToFirst()) {

                    /**************** Captured image details ************/

                    /*****  Used to show image on view in LoadImagesFromSDCard class ******/
                    imageID     = cursor.getInt(columnIndex);

                    thumbID     = cursor.getInt(columnIndexThumb);

                    Path = cursor.getString(file_ColumnIndex);

                    //String orientation =  cursor.getString(orientation_ColumnIndex);

                    String CapturedImageDetails = " CapturedImageDetails : \n\n"
                            +" ImageID :"+imageID+"\n"
                            +" ThumbID :"+thumbID+"\n"
                            +" Path :"+Path+"\n";

                    // Show Captured Image detail on activity
                    //  imageDetails.setText( CapturedImageDetails );

                }
            }
        } finally {
            if (cursor != null) {
              //  cursor.close();
            }
        }

        // Return Captured Image ImageID ( By this ImageID Image will load from sdcard )

        return ""+imageID;
    }

    public class LoadImagesFromSDCard  extends AsyncTask<String, Void, Void> {

        private ProgressDialog Dialog = new ProgressDialog(ResumenRecibosDia.this);

        Bitmap mBitmap;

        protected void onPreExecute() {
            /****** NOTE: You can call UI Element here. *****/

            // Progress Dialog
            Dialog.setMessage(" Loading image from Sdcard..");
            Dialog.show();
        }


        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {

            Bitmap bitmap = null;
            Bitmap newBitmap = null;
            Uri uri = null;


            try {

                uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + urls[0]);

                /**************  Decode an input stream into a bitmap. *********/
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));

                if (bitmap != null) {

                    /********* Creates a new bitmap, scaled from an existing bitmap. ***********/
                    BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
                    BitmapFactory.decodeFile(uri.getPath(), sizeOptions);

                    int height=bitmap.getHeight()/2;
                    int width=bitmap.getWidth()/2;

                    int inSampleSize = 1;
                    final int heightRatio =height ;
                    final int widthRatio=Math.round((width*1200)/height);
                    inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

                    newBitmap = Bitmap.createScaledBitmap(bitmap, widthRatio, 1200, true);

                    bitmap.recycle();

                    if (newBitmap != null) {

                        mBitmap = newBitmap;
                    }
                }
            } catch (IOException e) {
                // Error fetching image, try to recover

                /********* Cancel execution of this task. **********/
                cancel(true);
            }

            return null;
        }
        protected void onPostExecute(Void unused) {

            // NOTE: You can call UI Element here.

            // Close progress dialog
            Dialog.dismiss();

            if(mBitmap != null)
            {
                // Set Image to ImageView

                showImg.setImageBitmap(mBitmap);
            }

        }
        private int calculateInSampleSize(BitmapFactory.Options options,int reqWidth,int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

//        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio =height ; // Math.round((float) height / (float) reqHeight);

            final int widthRatio= width;  //Math.round((width*240)/height);

            //final int widthRatio = Math.round((float) width / (float) reqWidth);
            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

//        }

            return widthRatio;
        }
    }
}
