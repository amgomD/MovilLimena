package com.ficc.mwmovil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.layout.simple_spinner_item;

public class CreacionClienteTra extends AppCompatActivity  {


    private String name = "";
    private final String ruta_fotos = Environment.getExternalStorageDirectory().toString() + "/MantisWeb/";
    private File file = new File(ruta_fotos);
    private Button boton;
    Uri uri;
    File mi_foto;
    String rrfile;
    Bundle Extras=null;
    private Uri imageUri;
    ThumbnailUtils thumbnail;
    CreacionClienteTra CameraActivity = null;
    static TextView imageDetails;

    static String Path;
    ImageView showImg ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creacion_cliente_tra);
        getSupportActionBar().hide();
        showImg= (ImageView) findViewById(R.id.MiFotoEnvio);
        imageDetails = (TextView) findViewById(R.id.txt_direccion);
        Button BTNGUARDAR= (Button) findViewById(R.id.Guardar);
        Button btnCiudad= (Button) findViewById(R.id.btnCiudad);
        Button btnBarrio= (Button) findViewById(R.id.BtnBarrio);
        Button btn_Escanear= (Button) findViewById(R.id.btn_Escanear);

        Extras=this.getIntent().getExtras();

        final TextView nit = (TextView) findViewById(R.id.Nit);
        CameraActivity=this;
        btnBarrio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), BuscarBarrioCiudad.class);
                i.putExtra("BUSCIUBAR","BAR");
                startActivityForResult(i, 3);

            }
        });
        btnCiudad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), BuscarBarrioCiudad.class);
                i.putExtra("BUSCIUBAR","CIU");
                startActivityForResult(i, 2);

            }
        });

        final TextView dir1 = (TextView) findViewById(R.id.dir1);
        final TextView dirres = (TextView) findViewById(R.id.dirres);
        final EditText edit_dirnum1 = (EditText) findViewById(R.id.edit_dirnum1);
        final EditText edit_dirnum2 = (EditText) findViewById(R.id.edit_dirnum2);
        final EditText edit_dirnum3 = (EditText) findViewById(R.id.edit_dirnum3);

        final EditText edit_dirnum4 = (EditText) findViewById(R.id.edit_dirnum4);
        final EditText edit_dirnum5 = (EditText) findViewById(R.id.edit_dirnum5);
        final EditText edit_dirnum6 = (EditText) findViewById(R.id.edit_dirnum6);
        final Spinner sDir= (Spinner) findViewById(R.id.sDir);
        final Spinner sDir2= (Spinner) findViewById(R.id.sDir2);

        Button btn_agregar= (Button) findViewById(R.id.btn_agregar);
        btn_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String texto=dir1.getText().toString();
                String nuevotexto="";
                if (!edit_dirnum1.getText().toString().isEmpty()) {
                    nuevotexto=edit_dirnum1.getText().toString();
                }
                if (!edit_dirnum2.getText().toString().isEmpty()) {
                    nuevotexto+=" # "+edit_dirnum2.getText().toString();
                }
                if (!edit_dirnum3.getText().toString().isEmpty()) {
                    nuevotexto+=" - "+edit_dirnum3.getText().toString();
                }
                dir1.setText(texto.trim()+" "+sDir.getSelectedItem().toString().trim()+" "+nuevotexto);
                edit_dirnum1.setText("");
                edit_dirnum2.setText("");
                edit_dirnum3.setText("");
            }
        });

        Button btn_agregar2= (Button) findViewById(R.id.btn_agregar2);
        btn_agregar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String texto=dirres.getText().toString();

                String nuevotexto="";
                if (!edit_dirnum4.getText().toString().isEmpty()) {
                    nuevotexto=edit_dirnum4.getText().toString();
                }
                if (!edit_dirnum5.getText().toString().isEmpty()) {
                    nuevotexto+=" # "+edit_dirnum5.getText().toString();
                }
                if (!edit_dirnum6.getText().toString().isEmpty()) {
                    nuevotexto+=" - "+edit_dirnum6.getText().toString();
                }
                dirres.setText(texto.trim()+" "+sDir2.getSelectedItem().toString().trim()+" "+nuevotexto);
                edit_dirnum4.setText("");
                edit_dirnum5.setText("");
                edit_dirnum6.setText("");


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



        BTNGUARDAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImg.buildDrawingCache();
                Bitmap bm = showImg.getDrawingCache();

                OutputStream fOut = null;
                Uri outputFileUri;
                File sdImageMainDirectory = null;
                try {
                    File root = new File(ruta_fotos);
                    root.mkdirs();
                    sdImageMainDirectory = new File(root, "mw" + nit.getText() + ".jpg");
                    outputFileUri = Uri.fromFile(sdImageMainDirectory);
                    fOut = new FileOutputStream(sdImageMainDirectory);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();
                }
                try {
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (Exception e) {
                }

                TextView nombre = (TextView) findViewById(R.id.Nombre);
                TextView pnom = (TextView) findViewById(R.id.PriNom);
                TextView snom = (TextView) findViewById(R.id.SegNom);
                TextView pape = (TextView) findViewById(R.id.PriApe);
                TextView sape = (TextView) findViewById(R.id.SegApe);
                nombre.setText(pnom.getText() + " " + snom.getText() + " " + pape.getText() + " " + sape.getText() + " ");
                TextView FecVis = (TextView) findViewById(R.id.FecVis);
                Spinner sDirreccion = (Spinner) findViewById(R.id.sDir);
                TextView direccion = (TextView) findViewById(R.id.dir1);
                TextView direccion2 = (TextView) findViewById(R.id.dir2);
                TextView direccion3 = (TextView) findViewById(R.id.dir3);
                TextView NombreEst = (TextView) findViewById(R.id.NombreEst);
                TextView Ciudad = Ciudad = (TextView) findViewById(R.id.ciudad);
                TextView Barrio = (TextView) findViewById(R.id.barrio);
                TextView Telefono = (TextView) findViewById(R.id.Telefono);
                TextView Celuar = (TextView) findViewById(R.id.Celuar);
                ToggleButton Lunes = (ToggleButton) findViewById(R.id.Lunes);
                ToggleButton Martes = (ToggleButton) findViewById(R.id.Martes);
                ToggleButton Miercoles = (ToggleButton) findViewById(R.id.Miercoles);
                ToggleButton Jueves = (ToggleButton) findViewById(R.id.Jueves);
                ToggleButton Viernes = (ToggleButton) findViewById(R.id.Viernes);
                ToggleButton Sabado = (ToggleButton) findViewById(R.id.Sabado);
                TextView Frecuencia = (TextView) findViewById(R.id.Frecuencia);
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
                    Scanal.setSelection(0);
                    sTamano = Tamano.getSelectedItem().toString().trim();
                }
                String kk=nombre.getText().toString();

                if (Lunes.isChecked() == Boolean.FALSE && Martes.isChecked() == Boolean.FALSE && Miercoles.isChecked() == Boolean.FALSE && Miercoles.isChecked() == Boolean.FALSE && Jueves.isChecked() == Boolean.FALSE && Viernes.isChecked() == Boolean.FALSE && Sabado.isChecked() == Boolean.FALSE)
                {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(view.getContext());
                    dlgAlert.setMessage("Debe seleccionar el dia de visita");
                    dlgAlert.setTitle("Alerta");
                    dlgAlert.setPositiveButton("OK", null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();
                }
                else{

                    if (nit.getText().toString().trim().isEmpty()|| nombre.getText().toString().trim().isEmpty() || pape.getText().toString().trim().isEmpty() || direccion.getText().toString().trim().isEmpty() || NombreEst.getText().toString().trim().isEmpty()|| Ciudad.getText().toString().trim().isEmpty()|| Barrio.getText().toString().trim().isEmpty()  || Telefono.getText().toString().trim().isEmpty() || Celuar.getText().toString().trim().isEmpty() || sdImageMainDirectory.getPath().toString().trim().isEmpty()) {
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(view.getContext());
                        dlgAlert.setMessage("Debe diligenciar todos los datos antes de guardar, recuerde tomar captura de imagen");
                        dlgAlert.setTitle("Alerta");
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();
                    }else{
                        String consulta = "insert into prospecto ("
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
                                + "imagen,vencod) values('"
                                + nit.getText().toString().trim() + "','"
                                + nombre.getText() + "','"
                                + pnom.getText() + "','"
                                + snom.getText() + "','"
                                + pape.getText() + "','"
                                + sape.getText() + "','"
                                + FecVis.getText() + "','"
                                + direccion.getText() + "','"
                                + NombreEst.getText() + "','"
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
                                + "N" + "','"
                                + Frecuencia.getText() + "','" + sCanal.trim() + "','" + sSubCanal.trim() + "','" + sTamano.trim() + "','"
                                + Observacion.getText() + "','"
                                +   "','" //sdImageMainDirectory.getPath()
                                +  "','')"; //sdImageMainDirectory.getName()  //" + Extras.getString("Codvend") + "

                        String Consulta2 = "Insert into clientes (nitsec,clisec,NitCom,CliNom,CliDir,Lisprecod,CliTel,NitIde,CLICONPAG,cliintlun,cliintmar,cliintmie,cliintjue,cliintvie,cliintsab,cliintdom,CanSubCod,CliCup,PerCliCod,CanCod,CanNom,CliDiasUltVen) values " +
                                " ('" + nit.getText() + "',1,'" +
                                nombre.getText() + "','" +
                                NombreEst.getText() + "','" +
                                direccion.getText() + "',1,'SIN TELEFONO','" +
                                nit.getText() + "',0,'S','S','S','S','S','S','S',1,1,1,1,'1',0)";


                        BaseDatos BaseDeDatos;
                        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
                        try {
                            BaseDeDatos.getWritableDatabase().execSQL(consulta);
                            BaseDeDatos.getWritableDatabase().execSQL(Consulta2);
                        } catch (Exception e) {
                            String hh = e.getMessage();
                        }

                        finish();

                    }
                }
            }
        });
        Button MiGridViewArt= (Button) findViewById(R.id.Capturar);
        MiGridViewArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

            }
        });
        //   Button Guardar= (Button) findViewById(R.id.Guardar);
        //   MiGridViewArt.setOnClickListener(new View.OnClickListener() {
        //        @Override
        //          public void onClick(View view) {
        // Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //  MiGridViewArt.get

//            }
        //    });


        Spinner spinner = (Spinner) findViewById(R.id.Canal);
        final Spinner spinnerSub = (Spinner) findViewById(R.id.Scanal);
        final Spinner spinnerTam = (Spinner) findViewById(R.id.Tamano);

        final Spinner spinner_movtipdir = (Spinner) findViewById(R.id.spinner_movtipdir);
       // String[] Tamano={"A","B","C","D"}  ;
        //String[] vDir={"CALLE","CARRERA","AVENIDA CALLE","AVENIDA CARRERA","AVENIDA","AUTOPISTA","CIRCULAR","DIAGONAL","MANZANA","TRANSVERSAL","VIA"}  ;
        //sDir.setAdapter(new ArrayAdapter<String>(this, simple_spinner_item, vDir));
       // spinnerTam.setAdapter(new ArrayAdapter<String>(this, simple_spinner_item, Tamano));
       // spinnerSub.setAdapter(new ArrayAdapter<String>(getApplicationContext(), simple_spinner_item, Tamano));
        String[] Canales  ;


        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
        Cursor SqlCanales = BaseDeDatos.getWritableDatabase().rawQuery("select cancod,cannom from canales group by cancod,cannom", null); //order by nombre

        if (SqlCanales.getCount()>0){
            Canales = new String[SqlCanales.getCount()];
            int vuelta=0;
            SqlCanales.moveToFirst();
            do {
                Canales[vuelta]=SqlCanales.getString(1);
                vuelta=vuelta+1;
            } while (SqlCanales.moveToNext());
        }else{
            Canales = new String[SqlCanales.getCount()];
        }
        spinner.setAdapter(new ArrayAdapter<String>(this, simple_spinner_item, Canales));


    /*    String[] MovTipDirNom  ;
        Cursor SqlMovTipDir = BaseDeDatos.getWritableDatabase().rawQuery("select MovTipDirCod,MovTipDirNom from MovTipDir order by MovTipDirCod", null); //order by nombre
        if (SqlCanales.getCount()>0){
            MovTipDirNom = new String[SqlMovTipDir.getCount()];
            int vuelta=0;
            SqlMovTipDir.moveToFirst();
            do {
                MovTipDirNom[vuelta]=SqlMovTipDir.getString(1);
                vuelta=vuelta+1;
            } while (SqlMovTipDir.moveToNext());
        }else{
            MovTipDirNom = new String[SqlMovTipDir.getCount()];
        }
        spinner_movtipdir.setAdapter(new ArrayAdapter<String>(this, simple_spinner_item, MovTipDirNom));*/



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {

                String[] SubCanales;
                Cursor SqlCanales = BaseDeDatos.getWritableDatabase().rawQuery("select cansubcod,cansubnom from canales where cannom='" + adapterView.getItemAtPosition(pos).toString() + "'", null); //order by nombre

                if (SqlCanales.getCount() > 0) {
                    SubCanales = new String[SqlCanales.getCount()];
                    int vuelta = 0;
                    SqlCanales.moveToFirst();
                    do {
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
                spinnerTam.setAdapter(new ArrayAdapter<String>(view.getContext(), R.layout.spinner_itemseparado, Tamano));

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
   //     startActivity(i);

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
                TextView ciudad= (TextView) findViewById(R.id.ciudad);
                TextView Ciudad= (TextView) findViewById(R.id.Ciudad);
                ciudad.setText(data.getStringExtra("CIUDAD"));
                Ciudad.setText(data.getStringExtra("CODCIUDAD"));
            }
        }
        if(requestCode == 3) {
            if(resultCode == Activity.RESULT_OK) {
                TextView barrio= (TextView) findViewById(R.id.barrio);
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

        private ProgressDialog Dialog = new ProgressDialog(CreacionClienteTra.this);

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

            final int widthRatio=Math.round((width*240)/height);

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
