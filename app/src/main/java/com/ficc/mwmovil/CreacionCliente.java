package com.ficc.mwmovil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

//import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.R.layout.simple_spinner_item;

public class CreacionCliente extends AppCompatActivity  {


    private String name = "";
    private final String ruta_fotos = Environment.getExternalStorageDirectory().toString() + "/MantisWeb/";
    private File file = new File(ruta_fotos);
    private Button boton;
    Uri uri;
    File mi_foto;
    String rrfile;
    Bundle Extras=null;
    String[] vFre,vCanCod,vcanSubcod,colLisPrenom;
    String[] colPerCliCod,colPerCliNom,colTipCliNom,colTipCliCod;
    String[] colZonCod,colZonNom,ColCatCliCod,colCatCliNom;
    Integer[] collisprecod;
    private Uri imageUri;
    Spinner Frecuencia,listaprecio,percli,tipocliente,zona,categoria;
    ThumbnailUtils thumbnail;
    CreacionCliente CameraActivity = null;
    TextView cambioimg,rutapath;
    static TextView imageDetails;
    GlobalVariables gGlobalVariables=null;
    static String Path;
    String cuposindecimal = "0";

    String MantisFicc = "N";
    ImageView showImg ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creacion_cliente);
        getSupportActionBar().hide();
        showImg= (ImageView) findViewById(R.id.MiFotoEnvio);
        imageDetails = (TextView) findViewById(R.id.txt_direccion);
        rutapath = (TextView) findViewById(R.id.rutapath);
        Button BTNGUARDAR= (Button) findViewById(R.id.Guardar);
        cambioimg = (TextView) findViewById(R.id.cambioimg);
        cambioimg.setText("N");
        listaprecio = findViewById(R.id.listaprecio);
        TextView Cupocli = (TextView) findViewById(R.id.Cupocli);
        TextView cupoText = (TextView) findViewById(R.id.cupoText);


        Button btnCiudad= (Button) findViewById(R.id.btnCiudad);
        Button btnBarrio= (Button) findViewById(R.id.BtnBarrio);
        Button btn_Escanear= (Button) findViewById(R.id.btn_Escanear);
        ConBd conBd = new ConBd();
        conBd.Variables();
        LinearLayout contendorficc = findViewById(R.id.contendorficc);
        MantisFicc = conBd.MantisFicc;
        Extras=this.getIntent().getExtras();

        final TextView nit = (TextView) findViewById(R.id.Nit);
        CameraActivity=this;
        btnBarrio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView Ciudad = (TextView) findViewById(R.id.Ciudad);
                Intent i = new Intent(getApplicationContext(), BuscarBarrioCiudad.class);
                i.putExtra("BUSCIUBAR","BAR");
                String aCiudad = Ciudad.getText().toString();
                i.putExtra("LACIUDAD",aCiudad);
                startActivityForResult(i, 3);

            }
        });
        btnCiudad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), BuscarBarrioCiudad.class);
                i.putExtra("BUSCIUBAR","CIU");
                i.putExtra("CIUDAD","CIU");
                startActivityForResult(i, 2);

            }
        });

        /*btn_Escanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), EscaneoV2.class);
                startActivity(i);
                //startActivityForResult(i, 2);

            }
        });*/
       /*btn_Escanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EscanerView=new ZXingScannerView(this);
                setContentView(view);
                EscanerView.setResultHandler(this);
                EscanerView.startCamera();


            }
        });*/

        Cupocli.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Double cupoval = 0.0;
                if (!Cupocli.getText().toString().isEmpty()){
                    cupoval = Double.parseDouble(Cupocli.getText().toString());
                }

                cupoText.setText(String.format("%,d",cupoval.intValue()));

            }
        });

        BTNGUARDAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImg.buildDrawingCache();
                Bitmap bm = showImg.getDrawingCache();

                OutputStream fOut = null;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Uri outputFileUri;
                File sdImageMainDirectory = null;
                String nombreimg="mw" + nit.getText();
                try {
                    File root = new File(ruta_fotos);
                    root.mkdirs();

                    sdImageMainDirectory = new File(root, "mw" + nit.getText() + ".jpg");
                    outputFileUri = Uri.fromFile(sdImageMainDirectory);
                    fOut = new FileOutputStream(sdImageMainDirectory);
                } catch (Exception e) {
                    Log.e("Error imagen ", e.toString());
                   // Toast.makeText(getApplicationContext(), "Error occured. Please try again later."+e.toString(), Toast.LENGTH_SHORT).show();
                }
                String cadenahex="";



                TextView nombre = (TextView) findViewById(R.id.Nombre);
                TextView pnom = (TextView) findViewById(R.id.PriNom);
                TextView snom = (TextView) findViewById(R.id.SegNom);
                TextView pape = (TextView) findViewById(R.id.PriApe);
                TextView sape = (TextView) findViewById(R.id.SegApe);
                nombre.setText(pnom.getText() + " " + snom.getText() + " " + pape.getText() + " " + sape.getText() + " ");
                TextView FecVis = (TextView) findViewById(R.id.FecVis);
                TextView Correo = (TextView) findViewById(R.id.Correo);
                EditText ciucod = (EditText) findViewById(R.id.Ciudad);
                EditText BarCod = (EditText) findViewById(R.id.Barrio);


                Spinner sDirreccion = (Spinner) findViewById(R.id.sDir);
                TextView direccion = (TextView) findViewById(R.id.dir1);
                TextView direccion2 = (TextView) findViewById(R.id.dir2);
                TextView direccion3 = (TextView) findViewById(R.id.dir3);
                TextView NombreEst = (TextView) findViewById(R.id.NombreEst);
                TextView Ciudad = (TextView) findViewById(R.id.Ciudad);
                TextView ciudad = (TextView) findViewById(R.id.nomciudad);
                TextView Cupocli = (TextView) findViewById(R.id.Cupocli);
                EditText Plazo = (EditText) findViewById(R.id.Plazo);
                TextView Barrio = (TextView) findViewById(R.id.Barrio);
                TextView nomBarrio = (TextView) findViewById(R.id.nombarrio);
                TextView Telefono = (TextView) findViewById(R.id.Telefono);
                TextView Celuar = (TextView) findViewById(R.id.Celuar);
                ToggleButton Lunes = (ToggleButton) findViewById(R.id.Lunes);
                ToggleButton Martes = (ToggleButton) findViewById(R.id.Martes);
                ToggleButton Miercoles = (ToggleButton) findViewById(R.id.Miercoles);
                ToggleButton Jueves = (ToggleButton) findViewById(R.id.Jueves);
                ToggleButton Viernes = (ToggleButton) findViewById(R.id.Viernes);
                ToggleButton Sabado = (ToggleButton) findViewById(R.id.Sabado);
               // TextView Frecuencia = (TextView) findViewById(R.id.Frecuencia);
                Spinner Canal = (Spinner) findViewById(R.id.Canal);
                Spinner Scanal = (Spinner) findViewById(R.id.Scanal);
                Spinner Tamano = (Spinner) findViewById(R.id.Tamano);
                TextView Observacion = (TextView) findViewById(R.id.Observacion);
                String sCanal;
                String sSubCanal;
                String sTamano;
                String Prefijo;
                Integer posicion = sDirreccion.getSelectedItemPosition();
                if (posicion > 0) {
                    Prefijo = sDirreccion.getSelectedItem().toString().trim();
                } else {
                    sDirreccion.setSelection(0);
                    Prefijo = sDirreccion.getSelectedItem().toString().trim();
                }
                String tDireccion = Prefijo + ' ' + direccion.getText() + " # " + direccion2.getText() + " - " + direccion3.getText();
                if (Canal.getSelectedItemPosition() > 0) {
                    sCanal = Canal.getSelectedItem().toString().trim();
                } else {
                    Canal.setSelection(0);
                    sCanal = Canal.getSelectedItem().toString().trim();
                }
                if (Scanal.getSelectedItemPosition() > 0) {
                    sSubCanal = Scanal.getSelectedItem().toString().trim();
                } else {
                    Scanal.setSelection(0);
                    sSubCanal = Scanal.getSelectedItem().toString().trim();
                }
                if (Tamano.getSelectedItemPosition() > 0) {
                    sTamano = Tamano.getSelectedItem().toString().trim();
                } else {
                    Tamano.setSelection(0);
                    sTamano = Tamano.getSelectedItem().toString().trim();
                }
                String kk=nombre.getText().toString();
                String encoded="";


                gGlobalVariables = GlobalVariables.getInstance();
                String vUsuario=gGlobalVariables.getUsuario();
                String vEmpresa=gGlobalVariables.getEmpresa();
                int error = 0;

                if(!vEmpresa.equalsIgnoreCase("SUHOGAR")|| vEmpresa.equalsIgnoreCase("SUHOGARPRU")){
                    if (nombre.getText().toString().trim().isEmpty() || pape.getText().toString().trim().isEmpty()){
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(view.getContext());
                        dlgAlert.setMessage("Primer nombre y Primer apellido no pueden estar en blanco");
                        dlgAlert.setTitle("Alerta");
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();
                        error = 1;
                    }
                    if (Telefono.getText().toString().trim().isEmpty() && Celuar.getText().toString().trim().isEmpty()){
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(view.getContext());
                        dlgAlert.setMessage("Debe diligenciar alguno de los dos telefonos");
                        dlgAlert.setTitle("Alerta");
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();
                        error = 1;
                    }
                    if (Correo.getText().toString().trim().isEmpty()){
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(view.getContext());
                        dlgAlert.setMessage("Debe diligenciar el correo electronico");
                        dlgAlert.setTitle("Alerta");
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();
                        error = 1;
                    }
                    if (nit.getText().toString().trim().isEmpty() || direccion.getText().toString().trim().isEmpty() ||  Ciudad.getText().toString().trim().isEmpty()|| Barrio.getText().toString().trim().isEmpty()  ||  sdImageMainDirectory.getPath().toString().trim().isEmpty()) {
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(view.getContext());
                        dlgAlert.setMessage("Nit, direccion, ciudad,barrio, no pueden estar en blanco");
                        dlgAlert.setTitle("Alerta");
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();
                        error = 1;
                    }


                }else{
                    if (nit.getText().toString().trim().isEmpty()){
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(view.getContext());
                        dlgAlert.setMessage("Debe diligenciar el nit/cedula");
                        dlgAlert.setTitle("Alerta");
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();
                        error = 1;
                    }

                    if (NombreEst.getText().toString().trim().isEmpty()){
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(view.getContext());
                        dlgAlert.setMessage("Debe diligenciar el Nombre del establecimiento");
                        dlgAlert.setTitle("Alerta");
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();
                        error = 1;
                    }
                    if (Ciudad.getText().toString().trim().isEmpty()){
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(view.getContext());
                        dlgAlert.setMessage("Debe diligenciar la ciudad");
                        dlgAlert.setTitle("Alerta");
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();
                        error = 1;
                    }


                    if (nombre.getText().toString().trim().isEmpty() || pape.getText().toString().trim().isEmpty()){
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(view.getContext());
                        dlgAlert.setMessage("Primer nombre y Primer apellido no pueden estar en blanco");
                        dlgAlert.setTitle("Alerta");
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();
                        error = 1;
                    }


                }






                    if( error == 0){


                            try {
                                bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                                byte[] b = baos.toByteArray();


                                // for (byte bi : b) {
                                //     String st = String.format("%02X", bi);
                                //     cadenahex+=st;
                                //  }



                                encoded = Base64.encodeToString(b, Base64.DEFAULT);

                                //final String hex = BaseEncoding.base16().lowerCase().encode(bytes);
                                //Toast.makeText(getApplicationContext(),encoded, Toast.LENGTH_SHORT).show();
                                baos.flush();
                                baos.close();
                            } catch (Exception e) {
                            }

                        String txtFrecuencia ="4";
                           // if(MantisFicc.equalsIgnoreCase("S")){
                                txtFrecuencia =vFre[Frecuencia.getSelectedItemPosition()];
                          //  }
                            String nimagedetails = "" ;

                            if(cambioimg.getText().toString().equalsIgnoreCase("S")){
                               Bitmap bvm = ((BitmapDrawable) showImg.getDrawable()).getBitmap();
                               // nimagedetails = convert(bvm);
                           }

                            String consulta ="";
                            if(vEmpresa.equalsIgnoreCase("SUHOGAR")|| vEmpresa.equalsIgnoreCase("SUHOGARPRU")){

                                String cupo = Cupocli.getText().toString().trim();

                                if(cupo.isEmpty()){
                                    cupo = "0";
                                }

                                String plazo = Plazo.getText().toString().trim();
                                if(plazo.isEmpty()){
                                    plazo = "0";
                                }
                                String barrio = BarCod.getText().toString();
                                if(barrio.isEmpty()){
                                    barrio = "0";
                                }
                                String BarrioNom =   Barrio.getText().toString().trim();
                                if(BarrioNom.isEmpty()){
                                    BarrioNom = "";
                                }

                                 consulta = "insert into prospecto ("
                                        + "nit,"
                                        + "nombre,"
                                        + "prinom,"
                                        + "segnom,"
                                        + "priape,"
                                        + "segape,"
                                        + "fecha,"
                                        + "direccion,"
                                        + "establecimiento,"
                                        + "ciudad,"
                                        + "barrio,"
                                        + "telefono,"
                                        + "celular,"
                                        + "lun,"
                                        + "mar,"
                                        + "mie,"
                                        + "jue,"
                                        + "vie,"
                                        + "sab,"
                                        + "dom,"
                                        + "frecuencia,"
                                        + "canal,"
                                        + "subcanal,"
                                        + "tamano,"
                                        + "observacion,ruta,"
                                        + "imagen,vencod,ProsImg,ProsImg_GXI,Correo,ciucod,BarCod,lisprecod,TipoCliente,perfilcliente,zona,categoria,CliCup,Plazo) values('"
                                        + nit.getText().toString().trim() + "',upper('"
                                        + nombre.getText() + "'),upper('"
                                        + pnom.getText() + "'),upper('"
                                        + snom.getText() + "'),upper('"
                                        + pape.getText() + "'),upper('"
                                        + sape.getText() + "'),'"
                                        + FecVis.getText() + "','"
                                        + direccion.getText() + "',upper('"
                                        + NombreEst.getText() + "'),'"
                                        + Ciudad.getText().toString().trim() + "','"
                                        + BarrioNom + "','"
                                        + Telefono.getText() + "','"
                                        + Celuar.getText() + "','"
                                        + (Lunes.isChecked() == Boolean.TRUE ? "S" : "N") + "','"
                                        + (Martes.isChecked() == Boolean.TRUE ? "S" : "N") + "','"
                                        + (Miercoles.isChecked() == Boolean.TRUE ? "S" : "N") + "','"
                                        + (Jueves.isChecked() == Boolean.TRUE ? "S" : "N") + "','"
                                        + (Viernes.isChecked() == Boolean.TRUE ? "S" : "N") + "','"
                                        + (Sabado.isChecked() == Boolean.TRUE ? "S" : "N") + "','"
                                        + "N" + "','"+txtFrecuencia+"','" + vCanCod[Canal.getSelectedItemPosition()] + "','" + vcanSubcod[Scanal.getSelectedItemPosition()]  + "','" + sTamano.trim() + "','"
                                        + Observacion.getText() + "','"
                                        +   "','" //sdImageMainDirectory.getPath()
                                        +  "','','"+Path+"','gxdbfile:"+nombreimg+".jpg','"+Correo.getText()+"','"+ciucod.getText()+"',"+barrio+","+collisprecod[listaprecio.getSelectedItemPosition()]+",'"+colTipCliCod[tipocliente.getSelectedItemPosition()]+"','"+colPerCliCod[percli.getSelectedItemPosition()]+"','"+colZonCod[zona.getSelectedItemPosition()]+"','"+ColCatCliCod[categoria.getSelectedItemPosition()]+"',"+cupo.trim()+","+plazo.trim()+")"; //sdImageMainDirectory.getName()  //" + Extras.getString("Codvend") + "
                                //Frecuencia.getText() + "




                            }else{
                                consulta = "insert into prospecto ("
                                        + "nit,"
                                        + "nombre,"
                                        + "prinom,"
                                        + "segnom,"
                                        + "priape,"
                                        + "segape,"
                                        + "fecha,"
                                        + "direccion,"
                                        + "establecimiento,"
                                        + "ciudad,"
                                        + "barrio,"
                                        + "telefono,"
                                        + "celular,"
                                        + "lun,"
                                        + "mar,"
                                        + "mie,"
                                        + "jue,"
                                        + "vie,"
                                        + "sab,"
                                        + "dom,"
                                        + "frecuencia,"
                                        + "canal,"
                                        + "subcanal,"
                                        + "tamano,"
                                        + "observacion,ruta,"
                                        + "imagen,vencod,ProsImg,ProsImg_GXI,Correo,ciucod,BarCod,lisprecod,TipoCliente,perfilcliente,zona,categoria,CliCup) values('"
                                        + nit.getText().toString().trim() + "',upper('"
                                        + nombre.getText() + "'),upper('"
                                        + pnom.getText() + "'),upper('"
                                        + snom.getText() + "'),upper('"
                                        + pape.getText() + "'),upper('"
                                        + sape.getText() + "'),'"
                                        + FecVis.getText() + "','"
                                        + direccion.getText() + "',upper('"
                                        + NombreEst.getText() + "'),'"
                                        + Ciudad.getText().toString().trim() + "','"
                                        + Barrio.getText().toString().trim() + "','"
                                        + Telefono.getText() + "','"
                                        + Celuar.getText() + "','"
                                        + (Lunes.isChecked() == Boolean.TRUE ? "S" : "N") + "','"
                                        + (Martes.isChecked() == Boolean.TRUE ? "S" : "N") + "','"
                                        + (Miercoles.isChecked() == Boolean.TRUE ? "S" : "N") + "','"
                                        + (Jueves.isChecked() == Boolean.TRUE ? "S" : "N") + "','"
                                        + (Viernes.isChecked() == Boolean.TRUE ? "S" : "N") + "','"
                                        + (Sabado.isChecked() == Boolean.TRUE ? "S" : "N") + "','"
                                        + "N" + "','"+txtFrecuencia+"','" + vCanCod[Canal.getSelectedItemPosition()] + "','" + vcanSubcod[Scanal.getSelectedItemPosition()]  + "','" + sTamano.trim() + "','"
                                        + Observacion.getText() + "','"
                                        +   "','" //sdImageMainDirectory.getPath()
                                        +  "','','"+Path+"','gxdbfile:"+nombreimg+".jpg','"+Correo.getText()+"','"+ciucod.getText()+"',"+BarCod.getText()+","+0+",'','','','',0)"; //sdImageMainDirectory.getName()  //" + Extras.getString("Codvend") + "
                                //Frecuencia.getText() + "

                            }










 Log.e("consultaconsul ",consulta);

                            String Consulta2="";

                            if (vEmpresa.trim().equalsIgnoreCase("GELVEZ") || vEmpresa.trim().equalsIgnoreCase("GELVEZCAL") || vEmpresa.trim().equalsIgnoreCase("GELVEZGIR") || vEmpresa.trim().equalsIgnoreCase("GELVEZEJE")) {

                            }else{

         if(vEmpresa.equalsIgnoreCase("SUHOGAR")|| vEmpresa.equalsIgnoreCase("SUHOGARPRU")){
             String cupo = Cupocli.getText().toString().trim();
             if(cupo.isEmpty()){
                 cupo = "0";
             }
             String barrio = BarCod.getText().toString();
             if(barrio.isEmpty()){
                 barrio = "0";
             }
             String BarrioNom =   Barrio.getText().toString().trim();
             if(BarrioNom.isEmpty()){
                 BarrioNom = "";
             }

             String plazo = Plazo.getText().toString().trim();
             if(plazo.isEmpty()){
                 plazo = "0";
             }

             Consulta2 = "Insert into clientes (nitsec,clisec,NitCom,CliNom,CliDir,Lisprecod,CliTel,NitIde,CLICONPAG,cliintlun,cliintmar,cliintmie,cliintjue,cliintvie,cliintsab,cliintdom,CanSubCod,CliCup,PerCliCod,CanCod,CanNom,CliDiasUltVen,Frenom,ciucod,ciunom,BarCod,BarNom,TipoCliente,perfilcliente,zona,categoria,CliIva) values " +
                     " ('" + nit.getText() + "',1,'" +
                     nombre.getText() + "','"+NombreEst.getText().toString().trim()+"','" +
                     direccion.getText() + "',"+collisprecod[listaprecio.getSelectedItemPosition()]+",'"+Telefono.getText() +"','" +
                     nit.getText() + "',"+plazo+",'"+(Lunes.isChecked() == Boolean.TRUE ? "S" : "N") +"','"+(Martes.isChecked() == Boolean.TRUE ? "S" : "N")+"','"+(Miercoles.isChecked() == Boolean.TRUE ? "S" : "N")+"','"+(Jueves.isChecked() == Boolean.TRUE ? "S" : "N")+"','"+(Viernes.isChecked() == Boolean.TRUE ? "S" : "N")+"','"+(Sabado.isChecked() == Boolean.TRUE ? "S" : "N")+"','N',1,"+cupo.trim()+",1,1,'1',0,'"+vFre[Frecuencia.getSelectedItemPosition()]+"',"+Ciudad.getText().toString().trim()+",'"+ciudad.getText().toString().trim()+"','"+barrio+"','"+BarrioNom+"','"+colTipCliNom[tipocliente.getSelectedItemPosition()]+"','"+colPerCliNom[percli.getSelectedItemPosition()]+"','"+colZonNom[zona.getSelectedItemPosition()]+"','"+colCatCliNom[categoria.getSelectedItemPosition()]+"','S')";

         }else{
             Consulta2 = "Insert into clientes (nitsec,clisec,NitCom,CliNom,CliDir,Lisprecod,CliTel,NitIde,CLICONPAG,cliintlun,cliintmar,cliintmie,cliintjue,cliintvie,cliintsab,cliintdom,CanSubCod,CliCup,PerCliCod,CanCod,CanNom,CliDiasUltVen,Frenom,ciucod,ciunom,BarCod,BarNom,TipoCliente,perfilcliente,zona,categoria,CliIva) values " +
                     " ('" + nit.getText() + "',1,'" +
                     nombre.getText() + "','"+NombreEst.getText().toString().trim()+"','" +
                     direccion.getText() + "',"+collisprecod[listaprecio.getSelectedItemPosition()]+",'"+Telefono.getText() +"','" +
                     nit.getText() + "',0,'"+(Lunes.isChecked() == Boolean.TRUE ? "S" : "N") +"','"+(Martes.isChecked() == Boolean.TRUE ? "S" : "N")+"','"+(Miercoles.isChecked() == Boolean.TRUE ? "S" : "N")+"','"+(Jueves.isChecked() == Boolean.TRUE ? "S" : "N")+"','"+(Viernes.isChecked() == Boolean.TRUE ? "S" : "N")+"','"+(Sabado.isChecked() == Boolean.TRUE ? "S" : "N")+"','N',1,0,1,1,'1',0,'"+vFre[Frecuencia.getSelectedItemPosition()]+"',"+Ciudad.getText().toString().trim()+",'"+ciudad.getText().toString().trim()+"','"+Barrio.getText().toString()+"','"+nomBarrio.getText().toString()+"','','','','','S')";

         }
                          }
                            BaseDatos BaseDeDatos;
                            BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
                            try {
                                BaseDeDatos.getWritableDatabase().execSQL(consulta);
                                if (!Consulta2.isEmpty()) {
                                    try{
                                        BaseDeDatos.getWritableDatabase().execSQL(Consulta2);
                                    }catch (Exception e){
                                        Log.e("errosqlite2",e.toString());
                                    }

                                }
                            } catch (Exception e) {
                                String hh = e.getMessage();
                                Log.e("errosqlite1",e.toString());
                            }
                            Toast.makeText(getApplicationContext(), " Guardado ", Toast.LENGTH_SHORT).show();

                            finish();


                    }
                //}
            }
        });
        Button MiGridViewArt= (Button) findViewById(R.id.Capturar);
        MiGridViewArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {


                File filedir=new File(ruta_fotos); if(!filedir.exists()) filedir.mkdirs();
                // Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//"+ getCode()+ "
                rrfile = ruta_fotos +"mwapp.jpg";
                mi_foto=new File(ruta_fotos, "mwapp.jpg");
                imageUri = Uri.fromFile(mi_foto);

                //        try {
                //            mi_foto.createNewFile();
                //        } catch (IOException e) {}
                uri = Uri.fromFile(mi_foto);

                String fileName = "mwapp.jpg";

                // Create parameters for Intent with filename

                ContentValues values = new ContentValues();

                values.put(MediaStore.Images.Media.TITLE, fileName);

                values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");

                // imageUri is the current activity attribute, define and save it for later usage

                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                //uri= Uri.parse(ruta_fotos);
                //imageUri = getContentResolver().insert(uri, values);
                //ruta_fotos

                //Abre la camara para tomar la foto
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //Guarda imagen
                //imageUri=uri;

                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                //Retorna a la actividad
                startActivityForResult(cameraIntent, 1);

                //startActivity(cameraIntent);
                //ImageView iv = (ImageView)findViewById(R.id.MiFotoEnvio);
                //iv.setImageURI(Uri.fromFile(mi_foto));


                //   Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                //    startActivityForResult(i, 1);
                }catch (Exception e){
                    String aaaa = "mwapp.jpg";
                }
            }
        });
        Spinner spinner = (Spinner) findViewById(R.id.Canal);
        final Spinner spinnerSub = (Spinner) findViewById(R.id.Scanal);
        final Spinner spinnerTam = (Spinner) findViewById(R.id.Tamano);
        final Spinner sDir = (Spinner) findViewById(R.id.sDir);
        percli= (Spinner) findViewById(R.id.percli);
        tipocliente= (Spinner) findViewById(R.id.tipocliente);
        zona= (Spinner) findViewById(R.id.zona);
        categoria= (Spinner) findViewById(R.id.categoria);
        Frecuencia = (Spinner) findViewById(R.id.Frecuencia);


        if(MantisFicc.equalsIgnoreCase("N")){
            percli.setVisibility(View.GONE);
            tipocliente.setVisibility(View.GONE);
            zona.setVisibility(View.GONE);
            categoria.setVisibility(View.GONE);
           contendorficc.setVisibility(View.VISIBLE);
            listaprecio.setVisibility(View.GONE);
        }

        gGlobalVariables = GlobalVariables.getInstance();
        String vEmpresa =gGlobalVariables.getEmpresa();
        final Spinner spinner_movtipdir = (Spinner) findViewById(R.id.spinner_movtipdir);
        String[] vDir={"CALLE","CARRERA","AVENIDA CALLE","AVENIDA CARRERA","AVENIDA","AUTOPISTA","CIRCULAR","DIAGONAL","MANZANA","TRANSVERSAL","VIA"}  ;
       vFre= new String[]{"DIA", "QUIN1", "QUIN2", "QUIN3", "QUIN4", "QUIN5", "QUIN6", "MEN1", "MEN2", "MEN3", "MEN4", "SEM"};
        String[] txtFre={"Diaria","Quincenal semana 1 - 2","Quincenal semana 1 - 3", "Quincenal semana 1 - 4","Quincenal semana 2 - 3",
                "Quincenal semana 2 - 4","Quincenal semana 3 - 4","Mensual semana 1","Mensual semana 2","Mensual semana 3","Mensual semana 4","Semanal"};
        if(vEmpresa.equalsIgnoreCase("IBANEZ") || vEmpresa.equalsIgnoreCase("IBANEZPRU") || vEmpresa.equalsIgnoreCase("SURTIMARCAS") ){
            vFre= new String[]{"1","2","3"};
            txtFre=new String[]{"Semanal","Quincenal","Mensual"};
        }



        sDir.setAdapter(new ArrayAdapter<String>(this, simple_spinner_item, vDir));
        Frecuencia.setAdapter(new ArrayAdapter<String>(this,simple_spinner_item,txtFre));


        //spinnerSub.setAdapter(new ArrayAdapter<String>(getApplicationContext(), simple_spinner_item, Tamano));
        String[] Canales  ;


        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
        Cursor SqlCanales = BaseDeDatos.getWritableDatabase().rawQuery("select cancod,cannom from canales group by cancod,cannom", null); //order by nombre

        if (SqlCanales.getCount()>0){
            vCanCod = new String[SqlCanales.getCount()];
            Canales = new String[SqlCanales.getCount()];
            int vuelta=0;
            SqlCanales.moveToFirst();
            do {

                Canales[vuelta]=SqlCanales.getString(1);
                vCanCod[vuelta]=SqlCanales.getString(0);
                vuelta=vuelta+1;
            } while (SqlCanales.moveToNext());
        }else{
            Canales = new String[SqlCanales.getCount()];
        }
        spinner.setAdapter(new ArrayAdapter<String>(this, simple_spinner_item, Canales));
        Cursor listapreciocu = BaseDeDatos.getWritableDatabase().rawQuery("select LispreCod,LisPreNom from ListasPrecios", null); //order by nombre
        listapreciocu.moveToFirst();
        if(listapreciocu.getCount()> 0){
            colLisPrenom = new String[listapreciocu.getCount()];
            collisprecod= new Integer[listapreciocu.getCount()];
            int vueltalis = 0;
            do {
                colLisPrenom[vueltalis] = listapreciocu.getString(1);
                collisprecod[vueltalis] = listapreciocu.getInt(0);
                 vueltalis += 1;
            }while (listapreciocu.moveToNext());
        }
        listaprecio.setAdapter(new ArrayAdapter<String>(this, simple_spinner_item, colLisPrenom));



        if(vEmpresa.equalsIgnoreCase("SUHOGAR") || vEmpresa.equalsIgnoreCase("SUHOGARPRU")){
            Cursor perfilc = BaseDeDatos.getWritableDatabase().rawQuery("select PerCliCod,PerCliNom from PerfilDeClientes", null); //order by nombre
            perfilc.moveToFirst();
            if(perfilc.getCount()> 0){
                colPerCliCod = new String[perfilc.getCount()];
                colPerCliNom= new String[perfilc.getCount()];
                int vueltalis = 0;
                do {
                    colPerCliNom[vueltalis] = perfilc.getString(1);
                    colPerCliCod[vueltalis] = perfilc.getString(0);
                    vueltalis += 1;
                }while (perfilc.moveToNext());
            }
            percli.setAdapter(new ArrayAdapter<String>(this, simple_spinner_item, colPerCliNom));






        Cursor tipocli = BaseDeDatos.getWritableDatabase().rawQuery("select TipCliCod,TipCliNom from TipodeClientes", null); //order by nombre
        tipocli.moveToFirst();
        if(tipocli.getCount()> 0){
            colTipCliCod = new String[tipocli.getCount()];
            colTipCliNom = new String[tipocli.getCount()];
            int vueltalis = 0;
            do {
                colTipCliNom[vueltalis] = tipocli.getString(1);
                colTipCliCod[vueltalis] = tipocli.getString(0);
                vueltalis += 1;
            }while (tipocli.moveToNext());
        }
        tipocliente.setAdapter(new ArrayAdapter<String>(this, simple_spinner_item, colTipCliNom));

        Cursor zonacu = BaseDeDatos.getWritableDatabase().rawQuery("select ZonCod,ZonNom from Zona", null); //order by nombre
        zonacu.moveToFirst();
        if(zonacu.getCount()> 0){
            colZonCod = new String[zonacu.getCount()];
            colZonNom= new String[zonacu.getCount()];
            int vueltalis = 0;
            do {
                colZonNom[vueltalis] = zonacu.getString(1);
                colZonCod[vueltalis] = zonacu.getString(0);
                vueltalis += 1;
            }while (zonacu.moveToNext());
        }
        zona.setAdapter(new ArrayAdapter<String>(this, simple_spinner_item, colZonNom));


        Cursor curCategoria = BaseDeDatos.getWritableDatabase().rawQuery("select CatCliCod,CatCliNom from CategoriaCliente", null); //order by nombre
        curCategoria.moveToFirst();
        if(curCategoria.getCount()> 0){
            ColCatCliCod = new String[curCategoria.getCount()];
            colCatCliNom= new String[curCategoria.getCount()];
            int vueltalis = 0;
            do {
                colCatCliNom[vueltalis] = curCategoria.getString(1);
                ColCatCliCod[vueltalis] = curCategoria.getString(0);
                vueltalis += 1;
            }while (curCategoria.moveToNext());
        }
        categoria.setAdapter(new ArrayAdapter<String>(this, simple_spinner_item, colCatCliNom));
        }


       if(vEmpresa.equalsIgnoreCase("IBANEZ") || vEmpresa.equalsIgnoreCase("IBANEZPRU") || vEmpresa.equalsIgnoreCase("SURTIMARCAS")){
           Cupocli.setText("700000");
           contendorficc.setVisibility(View.GONE);
           Cupocli.setEnabled(false);
       }



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {

                String[] SubCanales;
                Cursor SqlCanales = BaseDeDatos.getWritableDatabase().rawQuery("select cansubcod,cansubnom from canales where cannom='" + adapterView.getItemAtPosition(pos).toString() + "'", null); //order by nombre

                if (SqlCanales.getCount() > 0) {
                    SubCanales = new String[SqlCanales.getCount()];
                    vcanSubcod = new String[SqlCanales.getCount()];
                    int vuelta = 0;
                    SqlCanales.moveToFirst();
                    do {
                        vcanSubcod[vuelta] = SqlCanales.getString(0);
                        SubCanales[vuelta] = SqlCanales.getString(1);
                        vuelta = vuelta + 1;
                    } while (SqlCanales.moveToNext());
                } else {
                    SubCanales = new String[SqlCanales.getCount()];
                }

                spinnerSub.setAdapter(new ArrayAdapter<String>(view.getContext(), simple_spinner_item, SubCanales));


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {


                Cursor SqlCanales = BaseDeDatos.getWritableDatabase().rawQuery("select cancona,canconb,canconc from canales where cansubnom='" + adapterView.getItemAtPosition(pos).toString() + "'", null); //order by nombre
                String Condia="";
                String Condib="";
                String Condic="";
                if (SqlCanales.getCount() > 0) {
                    int vuelta = 0;
                    SqlCanales.moveToFirst();
                    do {
                        Condia = SqlCanales.getString(0);
                        Condib = SqlCanales.getString(1);
                        Condic = SqlCanales.getString(2);
                        vuelta = vuelta + 1;
                    } while (SqlCanales.moveToNext());
                } else {
                }

                String[] Tamano={"A-"+Condia,"B-"+Condib,"C-"+Condic}  ;
                spinnerTam.setAdapter(new ArrayAdapter<String>(view.getContext(), simple_spinner_item, Tamano));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        spinnerSub.setSelection(0);
        spinner.setSelection(0);
        spinnerTam.setSelection(0);
    }




    public void EscanerQR(View view){
        //Intent i = new Intent(getApplicationContext(), EscanearDocumento.class);
//        Intent i = new Intent(getApplicationContext(), Escaneov4.class);
        //Intent i = new Intent(getApplicationContext(), BarcodePDF.class);
      //  startActivity(i);

       /* try {
            ZXingScannerView EscanerView = new ZXingScannerView(this);
            setContentView(view);
            EscanerView.setResultHandler((ZXingScannerView.ResultHandler) this);
            EscanerView.startCamera();
        }catch (Exception e){
            int hh=0;
        }*/
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2) {
            if(resultCode == Activity.RESULT_OK) {
                TextView ciudad= (TextView) findViewById(R.id.nomciudad);
                TextView Ciudad= (TextView) findViewById(R.id.Ciudad);
                ciudad.setText(data.getStringExtra("CIUDAD"));
                Ciudad.setText(data.getStringExtra("CODCIUDAD"));
            }
        }
        if(requestCode == 3) {
            if(resultCode == Activity.RESULT_OK) {
                TextView barrio= (TextView) findViewById(R.id.nombarrio);
                TextView Barrio= (TextView) findViewById(R.id.Barrio);
                barrio.setText(data.getStringExtra("CIUDAD"));
                Barrio.setText(data.getStringExtra("CODCIUDAD"));
            }
        }
        if(requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
                try {

                    if ( resultCode == RESULT_OK) {

                        /*********** Load Captured Image And Data Start ****************/

                        String imageId = convertImageUriToFile( imageUri,CameraActivity);


                        //  Create and excecute AsyncTask to load capture image

                        new LoadImagesFromSDCard().execute(""+imageId);

                        /*********** Load Captured Image And Data End ****************/


                    } else if ( resultCode == RESULT_CANCELED) {

                        Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
                    } else {

                        Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
                    }







                      /*if (resultCode == Activity.RESULT_OK) {
                          Uri selectedImage = imageUri;
                          getContentResolver().notifyChange(selectedImage, null);
                          ImageView imageView = (ImageView) findViewById(R.id.MiFotoEnvio);
                          ContentResolver cr = getContentResolver();
                          Bitmap bitmap;
                          try {
                              bitmap = android.provider.MediaStore.Images.Media
                                      .getBitmap(cr, selectedImage);

                              imageView.setImageBitmap(bitmap);
                              Toast.makeText(this, selectedImage.toString(),
                                      Toast.LENGTH_LONG).show();
                          } catch (Exception e) {
                              Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                                      .show();
                              Log.e("Camera", e.toString());
                          }
                      }*/

                    // ImageView imageView;

                    // Bitmap help1;
                    //  ImageView iv = (ImageView)findViewById(R.id.MiFotoEnvio);
                    //  help1 = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                    //  Uri selectedImage = data.getData();
                    //  iv.setImageBitmap((Bitmap) data.getParcelableExtra("data"));

                    // iv.setImageBitmap( thumbnail.extractThumbnail(help1,help1.getWidth(),help1.getHeight()));

                    //         Bundle extras = data.getExtras();
                    //         Bitmap mImageBitmap = (Bitmap) extras.get("data");
                    //    FileOutputStream fOut = null;
                    //    try {
                    //        fOut = new FileOutputStream(mi_foto);
                    //        fOut.close();
                    //    } catch (FileNotFoundException e) {
                    //        e.printStackTrace();
                    //    }

                    //    ImageView imageView = (ImageView)findViewById(R.id.MiFotoEnvio);
                    //mi_foto= new File( rrfile );
                    //   imageView.setImageBitmap(BitmapFactory.decodeFile(rrfile));
                    //imageView.setImageBitmap(mImageBitmap);

                    //       Uri selectedImage = data.getData();
                    //       String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    //       Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    //       cursor.moveToFirst();
                    //       int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                          String picturePath = cursor.getString(columnIndex);
                    //                        cursor.close();
//
                    //                        TextView MiRut = (TextView)findViewById(R.id.Direccion);
                    //  MiRut.setText(picturePath);
                    //  BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
                    //   BitmapFactory.decodeFile(picturePath, sizeOptions);
                    //   Bitmap bm = BitmapFactory.decodeFile(picturePath);
                    //   imageView.setImageBitmap(bm);
                    //    imageView.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(picturePath), calculateInSampleSize(sizeOptions, 500, 500), 500, false));
                    //   imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));



                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }



        //    if (data != null) {
        //      ImageView iv = (ImageView)findViewById(R.id.MiFotoEnvio);
        //     Bitmap bm = BitmapFactory.decodeFile(mi_foto.getPath());
        //      iv.setImageBitmap(bm);
        //      Toast.makeText(getApplicationContext(), "Entro", Toast.LENGTH_SHORT).show();
        //     if (data.hasExtra("data")) {
        //             ImageView iv = (ImageView)findViewById(R.id.MiFotoEnvio);
        //         iv.setImageBitmap((Bitmap) data.getParcelableExtra("data"));
        //     }
        //  } else {
        //        ImageView iv = (ImageView)findViewById(R.id.MiFotoEnvio);
        //      String ruta=mi_foto.getPath();
        //    Bitmap bm = BitmapFactory.decodeFile(ruta);
        // iv.setImageBitmap(bm);
        //     Uri imgUri=Uri.parse(ruta);
        //  Uri.fromFile(
        //     mi_foto= new File( rrfile );
        //      imgUri=mi_foto;
        //     iv.setImageURI(imgUri);

        //     Toast.makeText(getApplicationContext(), "Entro", Toast.LENGTH_SHORT).show();
        //        ImageView iv = (ImageView)findViewById(R.id.MiFotoEnvio);
        //    iv.setImageBitmap(BitmapFactory.decodeFile(name));
        // }

        //if ( resultCode == RESULT_OK && null != data) {
        //    Uri selectedImage = data.getData();
        //    String[] filePathColumn = { MediaStore.Images.Media.DATA };
        //    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        //    cursor.moveToFirst();
        //    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        //    String picturePath = cursor.getString(columnIndex);
        //    cursor.close();
        //   ImageView imageView = (ImageView)findViewById(R.id.MiFotoEnvio);
        //    TextView MiRut = (TextView)findViewById(R.id.TxtRuta);
        //    MiRut.setText(picturePath);
        //    BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        //    BitmapFactory.decodeFile(picturePath, sizeOptions);
        //    Bitmap bm = BitmapFactory.decodeFile(picturePath);
        //    imageView.setImageBitmap(bm);
        //    imageView.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(picturePath), calculateInSampleSize(sizeOptions, 500, 500), 500, false));
        //   imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        //  }
    }
    private String getCode()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date() );
        String photoCode = "pic_" + date;
        return photoCode;

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

            cursor = activity.getContentResolver().query(

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

                   // Log.e("PathPath",Path);

                    // Show Captured Image detail on activity
                    //  imageDetails.setText( CapturedImageDetails );

                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // Return Captured Image ImageID ( By this ImageID Image will load from sdcard )

        return ""+imageID;
    }




    /**
     * Async task for loading the images from the SD card.
     *
     * @author Android Example
     *
     */

    // Class with extends AsyncTask class

    public class LoadImagesFromSDCard  extends AsyncTask<String, Void, Void> {

        private ProgressDialog Dialog = new ProgressDialog(CreacionCliente.this);

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

                /**  Uri.withAppendedPath Method Description
                 * Parameters
                 *    baseUri  Uri to append path segment to
                 *    pathSegment  encoded path segment to append
                 * Returns
                 *    a new Uri based on baseUri with the given segment appended to the path
                 */

                uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + urls[0]);

                /**************  Decode an input stream into a bitmap. *********/
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));

                if (bitmap != null) {

                    /********* Creates a new bitmap, scaled from an existing bitmap. ***********/
                    BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
                    BitmapFactory.decodeFile(uri.getPath(), sizeOptions);

                    //newBitmap = Bitmap.createScaledBitmap(bitmap, calculateInSampleSize(sizeOptions, 500, 500), 500, false);
                    //newBitmap = Bitmap.createBitmap(bitmap);//


                    int height=bitmap.getHeight()/2;
                    int width=bitmap.getWidth()/2;

                    int inSampleSize = 1;
                    final int heightRatio =height ; // Math.round((float) height / (float) reqHeight);
                    final int widthRatio=Math.round((width*1200)/height);
                    inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

//        }

                    //return widthRatio;

                    newBitmap = Bitmap.createScaledBitmap(bitmap, widthRatio, 1200, true);
                    //    BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
                    //    BitmapFactory.decodeFile(picturePath, sizeOptions);
                    //    Bitmap bm = BitmapFactory.decodeFile(picturePath);
                    // imageView.setImageBitmap(bm);
                    //      imageView.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(picturePath), calculateInSampleSize(sizeOptions, 500, 500), 500, false));
                    //   imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
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
              Log.e("showimg: ",mBitmap.toString());
                rutapath.setText(Path);
                cambioimg.setText("S");
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
    public String convert(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

}
