package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SubirFotos extends AppCompatActivity {
    Bundle Extras;
    SDTFotos[] SDTFotos;

    ListViewAdapterFotos ListViewAdapterFotos;
    private final String ruta_fotos = Environment.getExternalStorageDirectory().toString() + "/MantisWeb/";
    private File file = new File(ruta_fotos);
    ImageView showImg ;
    String rrfile;
    File mi_foto;
    Uri uri;
    int veces = 0;
    static String Path;
    private Uri imageUri;
    SubirFotos CameraActivity = null;

    static TextView imageDetails;
    Integer llave;
    String nitsec,RecNro,ConNro;
    String PucSec;
    String modo;
    String checkval;
    String vUsuario;
    String ciucod;
    String tipo;
    Integer clisec;
    int ano;
    int mes;
    int dia;
    Double valor;
    Double totalAgg = 0.0;

  TextView cambiofoto;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vistafotos);
        Extras=this.getIntent().getExtras();

        getSupportActionBar().hide();
        Integer llave = Extras.getInt("llave");

        modo = Extras.getString("modo");
Log.e("modo",modo);
        if(modo.equalsIgnoreCase("forma")){
            Log.e("entromodo",modo);
            nitsec=Extras.getString("nitsec");
            clisec=Extras.getInt("clisec");
            PucSec = Extras.getString("pucsec");
            checkval = Extras.getString("checkval");
            valor = Extras.getDouble("valor");
            ano = Extras.getInt("ano");
            mes = Extras.getInt("mes");
            dia = Extras.getInt("dia");

            ciucod = Extras.getString("ciucod");
        }else{
            tipo = Extras.getString("tipo");
            Log.e("llego consigna",modo);

            RecNro = Extras.getString("RecNro");
            ConNro = Extras.getString("ConNro");
            valor = Extras.getDouble("valor");
        }



        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
         vUsuario=vGlobalVariables.getUsuario();
        cambiofoto = findViewById(R.id.cambiofoto);
        CameraActivity=this;
        showImg= (ImageView) findViewById(R.id.MiFotoEnvio);

        imageDetails = (TextView) findViewById(R.id.imagedetails);
        Button guardarlist = (Button) findViewById(R.id.guardarlist);
        TextView valorcon = (TextView) findViewById(R.id.valorcon);
        if(valor > 0){
            valorcon.setText(String.format("%,d",valor.intValue()));
        }else{
            valorcon.setVisibility(View.GONE);
        }

        Button guardarfotocon = (Button) findViewById(R.id.guardarfotocon);
        if(modo.equalsIgnoreCase("forma")){
            guardarlist.setVisibility(View.VISIBLE);
            Log.e("entrom2odo",modo);
            guardarfotocon.setVisibility(View.GONE);
        }else{
            guardarfotocon.setVisibility(View.VISIBLE);
            guardarlist.setVisibility(View.GONE);
        }
        Button Guardarreg = (Button) findViewById(R.id.Guardarreg);
        EditText valorop = findViewById(R.id.valorop);
        TextView numeroop = findViewById(R.id.numeroop);
        cargarfotos();
        cambiofoto.setText("N");
        showImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    File filedir = new File(ruta_fotos); if(!filedir.exists()) filedir.mkdirs();


                    veces +=1;
                    String nombrefoto = "mwapp"+String.valueOf(veces)+".jpg";
                    rrfile = ruta_fotos +nombrefoto;
                    mi_foto=new File(ruta_fotos, nombrefoto);

                    imageUri = Uri.fromFile(mi_foto);
                    uri = Uri.fromFile(mi_foto);
                    String fileName = nombrefoto;
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


        guardarlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int id = getResources().getIdentifier("@android:drawable/ic_menu_camera", null, null);
                 int error = 0;
                 double valoropera = 0.0;
                 String mensaje = "";
                if(cambiofoto.getText().toString().equalsIgnoreCase("N")){
                    error = 1;
                    mensaje = "Debe subir una foto";
                }

              if(valorop.getText().toString().isEmpty()){
                  valoropera = 0.0;
                }else{
                  valoropera = Double.valueOf(valorop.getText().toString());
              }

                if(numeroop.getText().toString().trim().isEmpty()){
                    if(checkval.equalsIgnoreCase("S")){
                        if(banderaconsig() > 0){
                            error = 1;
                            mensaje = "Esta Consignacion ya esta Registrada";
                        }
                    }  else
                        {
                            error = 1;
                            mensaje = "Debe ingresar numero de reg. operación";
                        }

                } else{

                    if(banderanum(numeroop.getText().toString().trim()) > 0){
                        error = 1;
                        mensaje = "Número ya registrado";
                    }
                }


                    if(error==0){
                    Bitmap bm = ((BitmapDrawable) showImg.getDrawable()).getBitmap();
                    String nimagedetails = convert(bm); // image
                    BaseDatos BaseDeDatos;
                    BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);


                    String consulta = "Insert into fototemp (secuencia,NroOp,imgBase,ValorOp)  values ("+llave+",'" + numeroop.getText().toString() + "','" + nimagedetails + "',"+valoropera+") ";
                    BaseDeDatos.getWritableDatabase().execSQL(consulta);
                    numeroop.setText("");
                        valorop.setText("");
                    showImg.setImageResource(id);
                    cambiofoto.setText("N");
                    cargarfotos();
                }else{
                        AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                        Alerta.setMessage(mensaje);
                        Alerta.setTitle("Notificacion");
                        Alerta.setPositiveButton("OK", null);
                        Alerta.setCancelable(true);
                        Alerta.create().show();
                    }


            }
        });

        Guardarreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int totalfotos = 0;
                Double TotalOp = 0.0;
                for(int i = 0;i<SDTFotos.length;i++) {
                    if(modo.equalsIgnoreCase("consigna")){
                        totalfotos +=1;
                        TotalOp += SDTFotos[i].valorop;
                    }else{
                        if(SDTFotos[i].image64.isEmpty()){
                        }else{
                            totalfotos +=1;
                            TotalOp += SDTFotos[i].valorop;
                        }
                    }

                }

                if(modo.equalsIgnoreCase("consigna")){

                    if(TotalOp.intValue() == valor.intValue()){
                        Intent intent = new Intent();
                        intent.putExtra("Totalfotos",totalfotos);
                        intent.putExtra("TotalValor",TotalOp);
                        setResult(RESULT_OK, intent);
                        finish();
                    }else{
                        AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                        Alerta.setMessage("Valor ingresado menor al total de la consignación");
                        Alerta.setTitle("Notificacion");
                        Alerta.setPositiveButton("OK", null);
                        Alerta.setCancelable(true);
                        Alerta.create().show();
                    }
                }else{
                    Intent intent = new Intent();
                    intent.putExtra("Totalfotos",totalfotos);
                    intent.putExtra("TotalValor",TotalOp);
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });


        guardarfotocon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int id = getResources().getIdentifier("@android:drawable/ic_menu_camera", null, null);
                int error = 0;
                double valoropera = 0.0;
                String mensaje = "";
                String checkfoto = "S";


                if(cambiofoto.getText().toString().equalsIgnoreCase("N")){
                    checkfoto = "N";
                    if(tipo.equalsIgnoreCase("N")){
                        error = 1;
                        mensaje = "Debe subir una foto";
                    }
                }

                if(valorop.getText().toString().isEmpty()){
                    error = 1;
                    mensaje = "Debe ingresar valor de la operación";
                    valoropera = 0.0;
                }else{
                    valoropera = Double.valueOf(valorop.getText().toString());
                }

                if(numeroop.getText().toString().trim().isEmpty()){
                        error = 1;
                        mensaje = "Debe ingresar numero de reg. operación";
                }

                if(numeroop.getText().toString().trim().isEmpty()){
                    if(checkval.equalsIgnoreCase("S")){
                        if(banderaNroCon() > 0){
                            error = 1;
                            mensaje = "Esta Consignacion ya esta Registrada";
                        }
                    }  else
                    {
                        error = 1;
                        mensaje = "Debe ingresar numero de reg. operación";
                    }

                } else{

                    if(banderanumcon(numeroop.getText().toString().trim()) > 0){
                        error = 1;
                        mensaje = "Número ya registrado";
                    }
                }







                if(error==0){
                    Bitmap bm = ((BitmapDrawable) showImg.getDrawable()).getBitmap();
                    String nimagedetails = convert(bm); // image
                    BaseDatos BaseDeDatos;
                    BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);

                    if(checkfoto.equalsIgnoreCase("N")){
                        nimagedetails = "";
                    }

                    Time time = new Time();
                    time.setToNow();
                    int anoactual = time.year;
                    int mesactual = time.month+1;
                    int diaactual = time.monthDay;

                   int lleve = SDTFotos.length;
                   String llave =  String.valueOf(lleve += 1);
                    String consulta = "Insert into ConsignaReciboFoto (RecNro,ConNro,foto,descr,Valorfoto,conyear,conmonth,conday,checkfoto)  values ('" + llave + "','" + ConNro + "','" + nimagedetails + "','"+numeroop.getText().toString() +"',"+valoropera+","+anoactual+","+mesactual+","+diaactual+",'"+checkfoto+"') ";
                    BaseDeDatos.getWritableDatabase().execSQL(consulta);
                    numeroop.setText("");
                    valorop.setText("");

                    showImg.setImageResource(id);
                    cambiofoto.setText("N");
                    cargarfotos();

                }else{
                    AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                    Alerta.setMessage(mensaje);
                    Alerta.setTitle("Notificacion");
                    Alerta.setPositiveButton("OK", null);
                    Alerta.setCancelable(true);
                    Alerta.create().show();
                }


            }
        });
    }

    public int banderaNroCon(){
        int bandera = 0;

        String consulta = "select  count(*) as total from comprobantedetalle cd left join comprobante c " +
                "on cd.comsec = c.comsec left join tipos t on c.tipcod = t.tipcod " +
                "where ComEstado = 'A' " +
                "and MovDeb+MovCre = "+valor+" " +
                "and TipConDcheck  = 'S' " +
                "and year(ComFecCon) = "+ano+" " +
                "and month(ComFecCon) = "+mes+" " +
                "and day(ComFecCon) = "+dia+" ";

        if(!PucSec.isEmpty()){
            consulta+=   "and ComPucSec = '"+PucSec+"' " ;

        }


        try{
            ConBd conbd = new ConBd();
            Connection conn = conbd.CargarConexion(getApplicationContext());
            Statement comm = conn.createStatement();
            ResultSet rsImport = comm.executeQuery(consulta);

            while (rsImport.next()){
                bandera =  rsImport.getInt("total");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);

        }

        return bandera;
    }



 public int banderaconsig(){
      int bandera = 0;

      String consulta = "select  count(*) as total from comprobantedetalle cd left join comprobante c " +
              "on cd.comsec = c.comsec left join tipos t on c.tipcod = t.tipcod " +
              "where ComEstado = 'A' " +
              "and ComPucSec = '"+PucSec+"' " +
              "and MovDeb+MovCre = "+valor+" " +
              "and TipConDcheck  = 'S' " +
              "and year(ComFecCon) = "+ano+" " +
              "and month(ComFecCon) = "+mes+" " +
              "and day(ComFecCon) = "+dia+" ";
Log.e("Cpmnsulta...",consulta);
try{
    ConBd conbd = new ConBd();
    Connection conn = conbd.CargarConexion(getApplicationContext());
    Statement comm = conn.createStatement();
    ResultSet rsImport = comm.executeQuery(consulta);

    while (rsImport.next()){
        bandera =  rsImport.getInt("total");
    }

} catch (SQLException e) {
    throw new RuntimeException(e);

}

     return bandera;
    }

    public void cargarfotos(){

        BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
        Cursor fotosc;

        if(modo.equalsIgnoreCase("forma")){
             fotosc = BaseDeDatos.getReadableDatabase().rawQuery("select NroOp , imgBase, secuencia,ValorOp from fototemp ", null);
        }else{
             fotosc = BaseDeDatos.getReadableDatabase().rawQuery("select descr , foto, 0 secuencia,Valorfoto,ConNro,RecNro from ConsignaRecibofoto where ConNro = '"+ConNro+"' ", null);
        }

        SDTFotos = new SDTFotos[fotosc.getCount()];
        int vueltas = 0;
        if (fotosc.getCount() > 0) {
            llave = fotosc.getCount()+1;
            fotosc.moveToFirst();
            totalAgg = 0.0;
            do {
                SDTFotos SDTFotosItem = new SDTFotos();
                SDTFotosItem.Vencod = vUsuario ;
                SDTFotosItem.NitSec = nitsec;
                SDTFotosItem.CliSec = fotosc.getInt(2);
                SDTFotosItem.RegOperacion = fotosc.getString(0);
                SDTFotosItem.image64 = fotosc.getString(1);
                SDTFotosItem.valorop = fotosc.getDouble(3);
                if(modo.equalsIgnoreCase("forma")) {
                    SDTFotosItem.ConNro = "";
                }else{
                    SDTFotosItem.ConNro = fotosc.getString(4);
                    SDTFotosItem.NitSec = fotosc.getString(5);
                }
                totalAgg += fotosc.getDouble(3);
                SDTFotos[vueltas] = SDTFotosItem;
                vueltas+=1;
            } while (fotosc.moveToNext());
            final ListView listfotos = (ListView) findViewById(R.id.listafoto);
            ListViewAdapterFotos = new ListViewAdapterFotos(SubirFotos.this, SDTFotos);
            listfotos.setAdapter(ListViewAdapterFotos);
        }

        Log.e("llave: ",String.valueOf(llave));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
                try {
                    if ( resultCode == RESULT_OK) {

                        /*********** Load Captured Image And Data Start ****************/

                        String imageId = convertImageUriToFile( imageUri,CameraActivity);
                        //  Create and excecute AsyncTask to load capture image
                        new SubirFotos.LoadImagesFromSDCard().execute(""+imageId);
                        cambiofoto.setText("S");
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
        }else{
            if (requestCode == 2) {
                Log.e("entro onresult: ",String.valueOf( Activity.RESULT_OK));
                if (resultCode == Activity.RESULT_OK) {

                    TextView ciudad = (TextView) findViewById(R.id.ciudad);
                    TextView ciudadcta = (TextView) findViewById(R.id.ciudadcta);
                    ciudad.setText(data.getStringExtra("CIUDAD"));
                    ciudadcta.setText(data.getStringExtra("CODCIUDAD"));


                }
            }
            else{
                TextView NomBancod = (TextView) findViewById(R.id.NomBancod);
                TextView codbancod = (TextView) findViewById(R.id.codbancod);
                NomBancod.setText(data.getStringExtra("NOMBANCO"));
                codbancod.setText(data.getStringExtra("CODBANCO"));

                final BaseDatos BaseDeDatos;
                BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);

                Cursor bancos;
                bancos= BaseDeDatos.getWritableDatabase().rawQuery("select TCNSEC,TCNNOM from Bancos  where BANFINCOD = "+data.getStringExtra("CODBANCO")+"  ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR
                final String[] values= new String[bancos.getCount()];
                final String[] valuesid= new String[bancos.getCount()];

                if (bancos.getCount()>0) {
                    bancos.moveToFirst();
                    int vuelta=bancos.getCount();
                    vuelta=0;
                    do {
                        values[vuelta]=bancos.getString(1);
                        valuesid[vuelta]=bancos.getString(0);
                        vuelta=vuelta+1;
                    } while (bancos.moveToNext());
                }

                Spinner tipoconsigna  = findViewById(R.id.tipoconsigna);
                tipoconsigna.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, values)); // simple_spinner_item
                tipoconsigna.setSelection(0);
            }
        }

    }



    public int banderanumcon(String numeroop){
        int bandera = 0;

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);

        Cursor numero;
        numero= BaseDeDatos.getWritableDatabase().rawQuery("select * from ConsignaRecibofoto where  descr = '"+numeroop+"'  ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR
        bandera += numero.getCount();


        Cursor numeroog;
        numeroog= BaseDeDatos.getWritableDatabase().rawQuery("select * from ReciboFormaFotos f left join Reciboforma r on f.facnro = r.facnro   where  (NroOp = '"+numeroop+"' or NroCheque =  '"+numeroop+"' ) and Valor = "+valor+"  and  " +
                " rcyear = "+ano+"  and rcmonth = "+mes+"   and rcday = "+dia+" and Ciudad = '"+ciucod+"'  ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR
        bandera += numeroog.getCount();


        Cursor numeromantis;
        numeromantis= BaseDeDatos.getWritableDatabase().rawQuery("select * from NumeroProvisional where  (ReForPagFotosDesc = '"+numeroop+"' or  ReForPagFotosDesc = '"+numeroop+"') and " +
                " RecPagVal = "+valor+" and RecFecyear = "+ano+"  and RecFecmonth = "+mes+"   and RecFecdia = "+dia+" and RecPagCiucod = '"+ciucod+"'     ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR

        bandera += numeromantis.getCount();


        return bandera;
    }





    public int banderanum(String numeroop){
        int bandera = 0;

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);

        Cursor numero;
        numero= BaseDeDatos.getWritableDatabase().rawQuery("select * from fototemp where  NroOp = '"+numeroop+"'  ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR
        bandera += numero.getCount();




       /* Cursor numerodef;
        numerodef= BaseDeDatos.getWritableDatabase().rawQuery("select * from ReciboFormaFotos where  NroOp = '"+numeroop+"'  ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR
        bandera += numerodef.getCount();
*/

        Cursor numeroog;
        numeroog= BaseDeDatos.getWritableDatabase().rawQuery("select * from ReciboFormaFotos f left join Reciboforma r on f.facnro = r.facnro   where  (NroOp = '"+numeroop+"' or NroCheque =  '"+numeroop+"' ) and Valor = "+valor+"  and  " +
                " rcyear = "+ano+"  and rcmonth = "+mes+"   and rcday = "+dia+" and Ciudad = '"+ciucod+"'  ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR
        bandera += numeroog.getCount();



       /* Cursor numeromantis;
        numeromantis= BaseDeDatos.getWritableDatabase().rawQuery("select * from NumeroProvisional where  NroOp = '"+numeroop+"'  ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR
        bandera += numeromantis.getCount();
*/


        Cursor numeromantis;
        numeromantis= BaseDeDatos.getWritableDatabase().rawQuery("select * from NumeroProvisional where  (ReForPagFotosDesc = '"+numeroop+"' or  ReForPagFotosDesc = '"+numeroop+"') and " +
                " RecPagVal = "+valor+" and RecFecyear = "+ano+"  and RecFecmonth = "+mes+"   and RecFecdia = "+dia+" and RecPagCiucod = '"+ciucod+"'     ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR

        bandera += numeromantis.getCount();





        return bandera;
    }

    public static String convertImageUriToFile ( Uri imageUri, Activity activity )  {

        Cursor cursor = null;
        int imageID = 0;

        try {

            String [] proj={
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Thumbnails._ID,
                    MediaStore.Images.ImageColumns.ORIENTATION
            };

            cursor = activity.getContentResolver().query(

                    imageUri,         //  Get data for specific image URI
                    proj,             //  Which columns to return
                    null,             //  WHERE clause; which rows to return (all rows)
                    null,             //  WHERE clause selection arguments (none)
                    null              //  Order-by clause (ascending by name)

            );

            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int columnIndexThumb = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
            int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int size = cursor.getCount();
            if (size == 0) {
                imageDetails.setText("No Image");
            }
            else
            {

                int thumbID = 0;
                if (cursor.moveToFirst()) {
                    imageID     = cursor.getInt(columnIndex);

                    thumbID     = cursor.getInt(columnIndexThumb);

                    Path = cursor.getString(file_ColumnIndex);
                    String CapturedImageDetails = " CapturedImageDetails : \n\n"
                            +" ImageID :"+imageID+"\n"
                            +" ThumbID :"+thumbID+"\n"
                            +" Path :"+Path+"\n";
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return ""+imageID;
    }

    public class LoadImagesFromSDCard  extends AsyncTask<String, Void, Void> {

        private ProgressDialog Dialog = new ProgressDialog(SubirFotos.this);

        Bitmap mBitmap;
        protected void onPreExecute() {
            /****** NOTE: You can call UI Element here. *****/

            // Progress Dialog
            Dialog.setMessage(" Loading image from Sdcard..");
            Dialog.show();
        }

        protected Void doInBackground(String... urls) {

            Bitmap bitmap = null;
            Bitmap newBitmap = null;
            Uri uri = null;


            try {

                uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + urls[0]);
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));

                if (bitmap != null) {

                    /********* Creates a new bitmap, scaled from an existing bitmap. ***********/
                    BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
                    BitmapFactory.decodeFile(uri.getPath(), sizeOptions);
                    int height=bitmap.getHeight()/2;
                    int width=bitmap.getWidth()/2;

                    int inSampleSize = 1;
                    final int heightRatio =height ; // Math.round((float) height / (float) reqHeight);
                    final int widthRatio=Math.round((width*1200)/height);
                    inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
                    newBitmap = Bitmap.createScaledBitmap(bitmap, widthRatio, 1200, true);
                    bitmap.recycle();

                    if (newBitmap != null) {

                        mBitmap = newBitmap;

                    }
                }
            } catch (IOException e) {
                cancel(true);
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            Dialog.dismiss();
            if(mBitmap != null)
            {
                showImg.setImageBitmap(mBitmap);
            }

        }
        private int calculateInSampleSize(BitmapFactory.Options options,int reqWidth,int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;
            final int heightRatio =height ; // Math.round((float) height / (float) reqHeight);
            final int widthRatio= width;  //Math.round((width*240)/height);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;



            return widthRatio;
        }

    }


    public String convert(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }
}