package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class visitacliente extends FragmentActivity {  // implements OnMapReadyCallback
    private final String ruta_fotos = Environment.getExternalStorageDirectory().toString() + "/MantisWeb/";
    private File file = new File(ruta_fotos);
    private Button boton;
    Uri uri;
    File mi_foto;
    FrameLayout loadingOverlay;
    Button btn_continuar;
    String rrfile;
    String MensajeBloqueo ="";
    int[] DiasPlazo;
      String[] TipoDocumento;
      String[] TipoNombre;

     String[] colLisPreNom;
     Integer[] colLisPreCod;
    Bundle Extras=null;
    private Uri imageUri;
    private Handler handler ;
    ThumbnailUtils thumbnail;
    visitacliente CameraActivity = null;
    static TextView imageDetails;
    TextView txt_latitud,Cambiofotovis;
    TextView txt_longitud;
    TextView edit_observacion,listapreciooo;
    Spinner spinner_lista;
    Spinner spinner_bodega;
    Spinner spinner_tipo;

     String[] CausalNombre;
     Integer[] CausalCodigo;


    Integer DiasUltVen;
    String Prefijo;
    Integer intento = 1;
    LinearLayout contPUente;
    Switch switch_planpuente,switch_viajedir;
   // private GoogleMap mMap;
    Button btn_cerrarvisita;
    Button btn_registrarvisita;
    String nEmpresa = "";
    String nitsec;
    Integer clisec;
    LinearLayout contenedorbarra;

     String[] colLisbonom;
     Integer[] colLisBodCod;

    int ListaCodigo=0;
    int Listacliente = 0;
    static String Path;
    Spinner spinner_plazo;
    Spinner spinner_causal;
    BaseDatos BaseDeDatos;
    ImageView showImg ;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitacliente);

   String bandera = "N";
        contenedorbarra = (LinearLayout)findViewById(R.id.contenedorbarra);
        handler = new Handler();
        Extras=this.getIntent().getExtras();
        nitsec =Extras.getString("nitsec");
        clisec =Extras.getInt("clisec");
        DiasUltVen=Extras.getInt("dias");
        loadingOverlay = findViewById(R.id.loadingOverlay);
        String lisprecod=Extras.getString("lisprecod");

        String fprefijo=Extras.getString("prefijo");
        Button btn_capturar= (Button) findViewById(R.id.btn_capturar);
    //    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
    //    .findFragmentById(R.id.map1);
    //    mapFragment.getMapAsync(this);

        //getSupportActionBar().hide();
        Extras=this.getIntent().getExtras();
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,}, 1000);
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 1000);

        CameraActivity=this;
        showImg= (ImageView) findViewById(R.id.MiFotoEnvio);
        imageDetails = (TextView) findViewById(R.id.textView24);
        edit_observacion = (TextView) findViewById(R.id.edit_observacion);
        listapreciooo= (TextView) findViewById(R.id.listapreciooo);
        //txt_latitud = (TextView) findViewById(R.id.txt_latitud);
        //txt_longitud = (TextView) findViewById(R.id.txt_longitud);
        Cambiofotovis= (TextView) findViewById(R.id.Cambiofotovis);
        btn_cerrarvisita = (Button) findViewById(R.id.btn_cerrarvisita);
        btn_registrarvisita = (Button) findViewById(R.id.btn_registrarvisita);
        switch_planpuente = findViewById(R.id.switch_planpuente);
        switch_viajedir = findViewById(R.id.switch_viajedir);
        contPUente = findViewById(R.id.contPUente);

        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vEmpresa=vGlobalVariables.getEmpresa();
        nEmpresa = vGlobalVariables.getEmpresa();
        try{
            vEmpresa=vEmpresa.toUpperCase();
            nEmpresa =nEmpresa.toUpperCase();
        }catch (Exception e){
            Log.e("Excepcion upper",e.toString());
        }


         spinner_causal = (Spinner) findViewById(R.id.spinner_causal);
         spinner_lista = (Spinner) findViewById(R.id.spinner_lista);
        spinner_bodega = (Spinner) findViewById(R.id.spinner_bodega);
        spinner_tipo = (Spinner) findViewById(R.id.spinner_tipo);
         spinner_plazo = (Spinner) findViewById(R.id.spinner_plazo);

        spinner_bodega.setEnabled(desbloquear(fprefijo,nitsec,clisec));
       spinner_lista.setEnabled(desbloquear(fprefijo,nitsec,clisec));
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
        Time time = new Time();
        time.setToNow();

        int bodcod = 0;
        int PedLisPreCod = 0;

        contPUente.setVisibility(View.GONE);

        if(vEmpresa.equalsIgnoreCase("FARMACOMERCIAL")){
            btn_registrarvisita.setVisibility(View.GONE);
            btn_cerrarvisita.setVisibility(View.GONE);
            switch_viajedir.setVisibility(View.GONE);
        }
        if(vEmpresa.equalsIgnoreCase("DINGLESA") ){
            btn_registrarvisita.setVisibility(View.GONE);
            btn_cerrarvisita.setVisibility(View.GONE);
            btn_capturar.setVisibility(View.GONE);
            showImg.setVisibility(View.GONE);
        }





        if(vEmpresa.equalsIgnoreCase("IBANEZ") ||vEmpresa.equalsIgnoreCase("IBANEZPRU")  || vEmpresa.equalsIgnoreCase("SURTIMARCAS") ){
            Date fechaactual = new Date(System.currentTimeMillis());
            String fechaInicio = "2026-01-12"; //fecha de ejemplo
            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaInicioDate = null;  //String a date

            try {
                fechaInicioDate = date.parse(fechaInicio);
                if(fechaInicioDate.after(fechaactual)){
                    contPUente.setVisibility(View.VISIBLE);
                }else{
                    contPUente.setVisibility(View.GONE);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }


        }


        Cursor cursorpedido = BaseDeDatos.getWritableDatabase().rawQuery("select bodcod,PedLisPreCod from pedido where prefijo = 'P2' and nitsec = '" + nitsec + "' and clisec = " + clisec + " and  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and  ifNULL(cant,0)+ifNULL(cantcaj,0)<>0 ", null);
         if(cursorpedido.getCount() > 0){
             cursorpedido.moveToFirst();
             bodcod = cursorpedido.getInt(0);
             PedLisPreCod = cursorpedido.getInt(1);
         }
        String CliNoReen = "N";
        Cursor listacli = BaseDeDatos.getWritableDatabase().rawQuery("select  Lisprecod , CliNoree from clientes where nitsec='" +Extras.getString("nitsec")+ "' and clisec=" + Extras.getInt("clisec")+" ", null);
        if (listacli.getCount()>0) {
            listacli.moveToFirst();
            ListaCodigo = listacli.getInt(0);
            listapreciooo.setText(String.valueOf(ListaCodigo));
            Log.e("ListaCodigo :", String.valueOf(ListaCodigo) );
            Listacliente = listacli.getInt(0);
            CliNoReen =  listacli.getString(1);
        }





        Cursor VisitaReg = BaseDeDatos.getWritableDatabase().rawQuery("select visobs,VisLatitud,VisLongitud,vishorfin,visLisPreCod,VisFotoimg  from Visita where nitsec='" +Extras.getString("nitsec")+ "' and clisec=" + Extras.getInt("clisec")+" and  VisAno=" + time.year + " and VisMes=" + (time.month + 1) + " and VisDia=" + time.monthDay+" " /*and VisPref = 'P1'"*/, null);

        if (VisitaReg.getCount()>0) {

            VisitaReg.moveToFirst();
            File bitmapFile = new File(VisitaReg.getString(5));
            Bitmap bitmap = BitmapFactory.decodeFile(bitmapFile.toString());
            //ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


          /* byte[] decodedString = Base64.decode(VisitaReg.getString(5), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);*/
            showImg.setImageBitmap(bitmap);

            edit_observacion.setText(VisitaReg.getString(0));

            ListaCodigo= VisitaReg.getInt(4);
            Log.e("ListaCodigo 2:", String.valueOf(ListaCodigo) );
            btn_cerrarvisita.setVisibility(View.GONE);
            btn_registrarvisita.setVisibility(View.INVISIBLE);
            // BaseDeDatos.getWritableDatabase().execSQL("delete from Visita where nitsec='" +Extras.getString("nitsec")+ "' and clisec=" + Extras.getInt("clisec"));

        }else{
            btn_cerrarvisita.setVisibility(View.GONE);
            btn_registrarvisita.setVisibility(View.GONE);
            TomarUbicacion();
        }
        if(vEmpresa.equalsIgnoreCase("DINGLESA") || vEmpresa.equalsIgnoreCase("FARMACOMERCIAL")  ){
            btn_registrarvisita.setVisibility(View.GONE);
            btn_cerrarvisita.setVisibility(View.GONE);
        }




        Cursor cursor = BaseDeDatos.getWritableDatabase().rawQuery("select MovCauSec,MovCauNom from MovCauPed order by MovCauNom ", null); //order by nombre


        CausalNombre = new String[cursor.getCount()];
        CausalCodigo = new Integer[cursor.getCount()];
        Integer SeleccionarLista=0;
        if (cursor.getCount()>0){
            int vuelta=0;
            cursor.moveToFirst();
            do {
                CausalCodigo[vuelta]=cursor.getInt(0);
                CausalNombre[vuelta]=cursor.getString(1);

                vuelta=vuelta+1;
            } while (cursor.moveToNext());
        }
        spinner_causal.setAdapter(new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item, CausalNombre)); // simple_spinner_item



    //try {
        Cursor cursorListas = BaseDeDatos.getWritableDatabase().rawQuery("select LisPreCod,LisPreNom from ListasPrecios order by LisPreNom ", null); //order by nombre

        colLisPreNom = new String[cursorListas.getCount()];
        colLisPreCod = new Integer[cursorListas.getCount()];

        if (cursorListas.getCount() > 0) {
            int vuelta = 0;
            cursorListas.moveToFirst();
            do {
                try {
                colLisPreCod[vuelta] = cursorListas.getInt(0);
                colLisPreNom[vuelta] = cursorListas.getString(1);


                if(vEmpresa.equalsIgnoreCase("SUHOGAR") ||vEmpresa.equalsIgnoreCase("DIAGNOSTIMAX")  ){
                    if(PedLisPreCod > 0){
                        if (cursorListas.getInt(0)==PedLisPreCod){
                            SeleccionarLista=vuelta;

                        }
                    }else{
                        if (cursorListas.getInt(0)== Listacliente){
                            SeleccionarLista=vuelta;

                        }
                    }

                }else{
                    if (cursorListas.getInt(0)== ListaCodigo){
                        SeleccionarLista=vuelta;

                    }
                }

                    }catch (Exception e){
                        int h=0;
                     }
                vuelta = vuelta + 1;
            } while (cursorListas.moveToNext());
        }



        spinner_lista.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, colLisPreNom)); // simple_spinner_item
        spinner_lista.setSelection(SeleccionarLista);




        Cursor cursorbodegas = BaseDeDatos.getWritableDatabase().rawQuery("select BodCod,BodNom,BodCheckPred from Bodegas order by BodNom ", null); //order by nombre
        //agregar marca de por defecto
        colLisbonom = new String[cursorbodegas.getCount()];
        colLisBodCod = new Integer[cursorbodegas.getCount()];
        int sel  = 0;
        int nsel = 0;
        int ban = 0;
        if (cursorbodegas.getCount() > 0) {
            int vuelta = 0;

            cursorbodegas.moveToFirst();
            do {
                try {
                    colLisBodCod[vuelta] = cursorbodegas.getInt(0);
                    colLisbonom[vuelta] = cursorbodegas.getString(1);
                    if(bodcod == cursorbodegas.getInt(0) ){
                        ban = ban + 1;
                        nsel = vuelta;
                    }
                    if ((cursorbodegas.getString(2).equalsIgnoreCase("S"))) {
                        sel = vuelta;
                    }

                }catch (Exception e){
                    int h=0;
                }
                vuelta = vuelta + 1;
            } while (cursorbodegas.moveToNext());
        }




        spinner_bodega.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, colLisbonom)); // simple_spinner_item
       if(ban > 0){
           spinner_bodega.setSelection(nsel);
       }else {
           spinner_bodega.setSelection(sel);
       }





    //}catch (Exception e){
    //    int h=0;
   // }
        if (vEmpresa.trim().equalsIgnoreCase("MENTAHAIR") || vEmpresa.trim().equalsIgnoreCase("MENTAHAIRCOT")
           || vEmpresa.trim().equalsIgnoreCase("BIOMETRIKA")   || vEmpresa.trim().equalsIgnoreCase("SUHOGAR")
                || vEmpresa.trim().equalsIgnoreCase("DIAGNOSTIMAX")) {
            spinner_lista.setVisibility(View.VISIBLE);
        }else{
           spinner_lista.setVisibility(View.INVISIBLE);
        }






        if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") ||vEmpresa.trim().equalsIgnoreCase("IBANEZPRU") ) {
          TipoDocumento= new String[] {"P1","P2","P3","P4","P5","P6","P7","P8","P9","P10","P11","P12","P13","P14","P15","P16","P17","P18","P19","P20","P21","P22","P23","P24","P25","P26","P27","P28","V15"};
          TipoNombre=new String[] { "Pedido 1","Pedido 2","Pedido 3","Pedido 4","Pedido 5","Pedido 6","Pedido 7","Pedido 8","Pedido 9","Pedido 10","Pedido 11","Pedido 12","Pedido 13","Pedido 14"
                  ,"Pedido 15","Pedido 16","Pedido 17","Pedido 18","Pedido 19","Pedido 20","Pedido 21","Pedido 22","Pedido 23","Pedido 24","Pedido 25","Pedido 26","Pedido 27","Pedido 28","PROX VENCER"};
        }else{

            if ((vEmpresa.trim().equalsIgnoreCase("BEHNER"))) {
                TipoDocumento = new String[]{"P1","P2","P3", "C2"};
                TipoNombre = new String[]{"Pedido 1","Pedido 2","Pedido 3", "Cotizacion 1"};
            }else{
                 if ((vEmpresa.trim().equalsIgnoreCase("MENTAHAIRCOT"))) {
                     TipoDocumento = new String[]{"C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "C10", "C11", "C12", "C13", "C14", "C15", "C16", "C17"};
                     TipoNombre = new String[]{"Cotización 1", "Cotización 2", "Cotización 3", "Cotización 4", "Cotización 5", "Cotización 6", "Cotización 7", "Cotización 8", "Cotización 9", "Cotización 10", "Cotización 11", "Cotización 12", "Cotización 13", "Cotización 14", "Cotización 15", "Cotización 16", "Cotización 17"};

                 }else{
                TipoDocumento = new String[]{"P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8", "P9", "P10", "P11", "P12", "P13", "P14", "P15", "P16", "P17"};
                TipoNombre = new String[]{"Pedido 1", "Pedido 2", "Pedido 3", "Pedido 4", "Pedido 5", "Pedido 6", "Pedido 7", "Pedido 8", "Pedido 9", "Pedido 10", "Pedido 11", "Pedido 12", "Pedido 13", "Pedido 14", "Pedido 15", "Pedido 16", "Pedido 17"};
                 }
            }
        }
        int Plazo=1;
        int InaCod=0;
        int PlazoPed=0;
        Cursor CursorCliente = BaseDeDatos.getReadableDatabase().rawQuery("select CLICONPAG,InaCod from clientes where nitsec='"+nitsec+"' and clisec="+clisec, null);
        Integer vuelta=0;
        if (CursorCliente.getCount()>0) {
            CursorCliente.moveToFirst();
            do {
                Plazo=CursorCliente.getInt(0);
                InaCod=CursorCliente.getInt(1);
            }while (CursorCliente.moveToNext());
        }
        PlazoPed=Plazo;

        if ((vEmpresa.trim().equalsIgnoreCase("IBANEZ")) || (vEmpresa.trim().equalsIgnoreCase("IBANEZPRU"))) {

            if(InaCod==13 || (Plazo==0 && InaCod!=10)){ // contado
                DiasPlazo=new int[1];
            }else{
                if(InaCod==10){ // contraentrega
                    DiasPlazo=new int[2];
                }else{
                    if(InaCod!=13 && InaCod!=10){ // credito
                        DiasPlazo=new int[3];
                    } else{
                        DiasPlazo=new int[3];
                    }
                }
            }


        }else{
                DiasPlazo=new int[Plazo+1];
        }
        int encontro=0;
         // CUANDO CAMBIEN EL P1 POR P2 EL PLAZO SE QUEDARA PEGADO PORQ ESTA FIJO
        Cursor CursorPedido = BaseDeDatos.getReadableDatabase().rawQuery("select plazo from pedido where nitsec='" + Extras.getString("nitsec") + "' and clisec=" + Extras.getInt("clisec") +" and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo='P1'", null);
        if (CursorPedido.getCount()>0) {
            CursorPedido.moveToFirst();
            do {
                encontro=1;
                PlazoPed=CursorPedido.getInt(0);
            }while (CursorPedido.moveToNext());
        }


      //  final  String[] NombrePlazo=new String[Plazo+1];

        List<String> NombrePlazo = new ArrayList<String>();
        int posiselec=0;
        if(Plazo==0 && (!vEmpresa.trim().equalsIgnoreCase("IBANEZ") && (!vEmpresa.trim().equalsIgnoreCase("IBANEZPRU"))) ){
            DiasPlazo[0]=0;
            NombrePlazo.add("CONTADO");
        }else{
         //   if (PlazoPed>0){
           //     DiasPlazo[0]=PlazoPed;
            //    NombrePlazo.add(Integer.toString(PlazoPed));
           // }
            if ((vEmpresa.trim().equalsIgnoreCase("IBANEZ")) || (vEmpresa.trim().equalsIgnoreCase("IBANEZPRU")) ) {
                if(InaCod==13 || (Plazo==0 && InaCod!=10 )){ // contraentrega
                    DiasPlazo[0]=0;
                    NombrePlazo.add("CONTRAENTREGA");
                    posiselec=0;
                }else{
                    if(InaCod==10 ){  // contado
                        DiasPlazo[0]=0;
                        NombrePlazo.add("CONTADO");
                        DiasPlazo[1]=1;
                        NombrePlazo.add("CONTRAENTREGA");
                        if (encontro==1){
                            if(PlazoPed==DiasPlazo[0]){
                                posiselec=0;
                            }
                            if(PlazoPed==DiasPlazo[1]){
                                posiselec=1;
                            }

                        }else{
                            posiselec=0;
                        }
                    }else{
                        if(InaCod!=13 && InaCod!=10){ // credito
                            DiasPlazo[0]=0;
                            NombrePlazo.add("CONTADO");
                            DiasPlazo[1]=1;
                            NombrePlazo.add("CONTRAENTREGA");
                            DiasPlazo[2]=Plazo;
                            NombrePlazo.add("CREDITO "+String.valueOf(Plazo) );
                            Log.e("PlazoPed",String.valueOf(PlazoPed));
                            Log.e("DiasPlazo[2]",String.valueOf(DiasPlazo[2]));
                            if (encontro==1){
                                if(PlazoPed==DiasPlazo[0]){
                                    posiselec=0;
                                }
                                if(PlazoPed==DiasPlazo[1]){
                                    posiselec=1;
                                }
                                if(PlazoPed==DiasPlazo[2]){
                                    posiselec=2;
                                }
                            }else{
                                posiselec=2;
                            }
                        }else{
                            DiasPlazo[0]=0;
                            NombrePlazo.add("CONTADO");
                            DiasPlazo[1]=1;
                            NombrePlazo.add("CONTRAENTREGA");
                            DiasPlazo[2]=Plazo;
                            NombrePlazo.add("CREDITO "+String.valueOf(Plazo) );

                            if (encontro==1){
                                if(PlazoPed==DiasPlazo[0]){
                                    posiselec=0;
                                }
                                if(PlazoPed==DiasPlazo[1]){
                                    posiselec=1;
                                }
                                if(PlazoPed==DiasPlazo[2]){
                                    posiselec=2;
                                }
                            }else{
                                posiselec=2;
                            }
                        }
                    }
                }

            }else{

                for(int i = 0; i <= Plazo; i++){

                    if (PlazoPed==i){
                        posiselec=i;
                    }
                    DiasPlazo[i]=i;
                    NombrePlazo.add(Integer.toString(i));
                }

               //     }

            }

        }


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, NombrePlazo);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_plazo.setAdapter(dataAdapter);
        spinner_plazo.setSelection(posiselec);

        //spinner_plazo.setAdapter(new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item, NombrePlazo));
        spinner_tipo.setAdapter(new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item, TipoNombre)); // simple_spinner_item


        spinner_tipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String Prefijo = TipoDocumento[i];
                int bodcod = 0;
                int plazo = 0;
                int PedLisPreCod = 0;
                int prebodcod = 0;
                Cursor bodpred = BaseDeDatos.getWritableDatabase().rawQuery("select BodCod from Bodegas where BodCheckPred = 'S' ",null);
                if(bodpred.getCount() > 0){
                    bodpred.moveToFirst();
                    prebodcod = bodpred.getInt(0);
                }

                Cursor cursorpedido = BaseDeDatos.getWritableDatabase().rawQuery("select bodcod,plazo,PedLisPreCod from pedido where prefijo = '"+Prefijo+"' and nitsec = '" + nitsec + "' and clisec = " + clisec + " and  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and  ifNULL(cant,0)+ifNULL(cantcaj,0)<>0 ", null);
                if(cursorpedido.getCount() > 0){
                    cursorpedido.moveToFirst();
                    bodcod = cursorpedido.getInt(0);
                    PedLisPreCod =  cursorpedido.getInt(2);
                    plazo = cursorpedido.getInt(1);
                }
                for(int j=0;j<colLisBodCod.length ; j++)
                {
                   if(bodcod==colLisBodCod[j]){
                       spinner_bodega.setSelection(j);
                       break;
                   }else{
                       if(prebodcod==colLisBodCod[j]){
                           spinner_bodega.setSelection(j);
                       }
                   }
                }
                if(nEmpresa.equalsIgnoreCase("SUHOGAR") || nEmpresa.trim().equalsIgnoreCase("DIAGNOSTIMAX")){


                    for(int j=0;j<colLisPreCod.length ; j++)
                    {
                        if(PedLisPreCod==colLisPreCod[j]){
                            spinner_lista.setSelection(j);
                            break;
                        }
                    }

                }



                    if(cursorpedido.getCount() > 0){
                for(int j=0;j<DiasPlazo.length ; j++)
                {
                    if(plazo==DiasPlazo[j]){
                        Log.e("plazo: select",String.valueOf(plazo));
                        Log.e("DiasPlazo: select",String.valueOf(DiasPlazo[j]));
                        Log.e("j: select",String.valueOf(j));
                       spinner_plazo.setSelection(j);
                        break;
                    }
                }

                }

                spinner_bodega.setEnabled(desbloquear(Prefijo,nitsec,clisec));
                spinner_lista.setEnabled(desbloquear(Prefijo,nitsec,clisec));

                Cursor VisitaReg = BaseDeDatos.getWritableDatabase().rawQuery("select visobs,VisLatitud,VisLongitud,vishorfin,visLisPreCod  from Visita where nitsec='" +Extras.getString("nitsec")+ "' and clisec=" + Extras.getInt("clisec")+" and  VisAno=" + time.year + " and VisMes=" + (time.month + 1) + " and VisDia=" + time.monthDay+" " /*and VisPref = '"+Prefijo+"'"*/, null);
                String Obs = "";
                if (VisitaReg.getCount()>0) {

                    VisitaReg.moveToFirst();
                    edit_observacion.setText(VisitaReg.getString(0));
                    Obs  = VisitaReg.getString(0);
                }else{
                    edit_observacion.setText("");
                }
                Cursor cursorobo = BaseDeDatos.getWritableDatabase().rawQuery("select ifnull(Obs,'"+Obs+"')Obs, ifnull(PlanPuente,'N') PlanPuente,ifnull(elisprecod,1) elisprecod from pedidoenc where prefijo = '"+Prefijo+"' and nitsec = '" + nitsec + "' and clisec = " + clisec + " and  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " ", null);

                if (cursorobo.getCount()>0) {
                    cursorobo.moveToFirst();
                   Integer eLisprecod = cursorobo.getInt(2);
                    Obs  = cursorobo.getString(0);
                    edit_observacion.setText(cursorobo.getString(0));

                    for(int u = 0; u<colLisPreCod.length;u++){
                        if(colLisPreCod[u] == eLisprecod){
                            spinner_lista.setSelection(u);
                            break;
                        }
                    }



                    if(cursorobo.getString(1).equalsIgnoreCase("S")){
                        switch_planpuente.setChecked(true);
                    }else{
                        switch_planpuente.setChecked(false);
                    }

                }else{
                    spinner_lista.setEnabled(true);
                    Log.e("ListAaaaaaaa: ",String.valueOf(Listacliente));
                    for(int u = 0; u<colLisPreCod.length;u++){
                        if(colLisPreCod[u] == Listacliente){
                            spinner_lista.setSelection(u);
                            break;
                        }
                    }
                    edit_observacion.setText("");
                }






            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });


        Button btn_ubicacion = (Button) findViewById(R.id.btn_ubicacion);
        btn_ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TomarUbicacion();

            }
        });


         btn_continuar = (Button) findViewById(R.id.btn_continuar);
        String finalCliNoReen = CliNoReen;
        btn_continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GlobalVariables vGlobalVariables = GlobalVariables.getInstance();
                String vEmpresa = vGlobalVariables.getEmpresa();
                vEmpresa = vEmpresa.toUpperCase();
                String vUsuario = vGlobalVariables.getUsuario();

                mostrarCargando();

                btn_continuar.setVisibility(View.GONE);
                contenedorbarra.setVisibility(View.GONE);

                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());

                executor.execute(() -> {

                String[] bloqueos = validacion_bloqueo(Prefijo+vUsuario+Extras.getString("nitsec")+String.valueOf(Extras.getInt("clisec"))+String.valueOf(time.year)+"-"+String.valueOf((time.month + 1))+"-"+String.valueOf(time.monthDay ));





                    handler.post(() -> {

                        ocultarCargando();
                        String remBloqueo = bloqueos[0];
                        String notBloqueo = bloqueos[1];



                        if(remBloqueo.equalsIgnoreCase("S") || notBloqueo.equalsIgnoreCase("S")){



                    AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                    Alerta.setMessage("No se puede editar el pedido: "+MensajeBloqueo);
                    Alerta.setTitle("Alerta");
                    Alerta.setPositiveButton("OK", null);
                    Alerta.setCancelable(true);
                    Alerta.create().show();




                }else{

                    continuarProcesoNormal(view);

                }


                    });

                });

            }
        });


        Button GuardarObs = (Button) findViewById(R.id.GuardarObs);
        GuardarObs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vEmpresa=vGlobalVariables.getEmpresa();
                vEmpresa=vEmpresa.toUpperCase();
                if((Path == null || Path.isEmpty()) && vEmpresa.equalsIgnoreCase("SUHOGAR") ){

                    AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                    Alerta.setMessage("debe realizar una captura ");
                    Alerta.setTitle("Alerta");
                    Alerta.setPositiveButton("OK", null);
                    Alerta.setCancelable(true);
                    Alerta.create().show();
                }else{
                    String Observacion =  edit_observacion.getText().toString();
                    Observacion=Observacion.replaceAll("[^\\w ]+", "");
                    if (DiasUltVen < 90 || !rrfile.trim().isEmpty()) {

                        String Prefijo;
                        if (spinner_tipo.getSelectedItemPosition() > 0) {
                            Integer posicion = spinner_tipo.getSelectedItemPosition();
                            Prefijo = TipoDocumento[posicion];
                        } else {
                            Prefijo = TipoDocumento[0];
                        }

                        int ListaCodigoItem = 0;
                        if (spinner_lista.getSelectedItemPosition() > 0) {
                            Integer posicion = spinner_lista.getSelectedItemPosition();
                            ListaCodigoItem = colLisPreCod[posicion];

                        } else {
                            ListaCodigoItem = colLisPreCod[0];
                        }


                        Integer CausalCodigoItem;
                        String CausalNombreItem;
                        if (spinner_causal.getSelectedItemPosition() > 0) {
                            Integer posicion = spinner_causal.getSelectedItemPosition();
                            CausalCodigoItem = CausalCodigo[posicion];
                            CausalNombreItem = CausalNombre[posicion];
                        } else {
                            CausalCodigoItem = CausalCodigo[0];
                            CausalNombreItem = CausalNombre[0];
                        }

                        Time time = new Time();
                        time.setToNow();

                        Cursor VisitaReg = BaseDeDatos.getWritableDatabase().rawQuery("select VisFotoimg from Visita where nitsec='" + Extras.getString("nitsec") + "' and clisec=" + Extras.getInt("clisec")  +" and  VisAno=" + time.year + " and VisMes=" + (time.month + 1) + " and VisDia=" + time.monthDay+ " " /*and VisPref='" + Prefijo+"'"*/, null);
                        VisitaReg.moveToFirst();
                        Cursor pedidoen = BaseDeDatos.getWritableDatabase().rawQuery("select Obs from Pedidoenc where prefijo = '"+Prefijo+"' and nitsec = '" + Extras.getString("nitsec") + "' and clisec = " + Extras.getInt("clisec")  + " and  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  " /*and VisPref='" + Prefijo+"'"*/, null);
                        pedidoen.moveToFirst();

                        if(pedidoen.getCount() > 0){
                            String planpuente = "N";
                            if(switch_planpuente.isChecked()){
                                planpuente ="S";
                            }


                            BaseDeDatos.getWritableDatabase().execSQL("update Pedidoenc set elisprecod = "+ListaCodigoItem+" ,planpuente='"+planpuente+"',  Obs='"+Observacion+"'   where prefijo = '"+Prefijo+"' and nitsec = '" + Extras.getString("nitsec") + "' and clisec = " + Extras.getInt("clisec")  + " and  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  ");
                        }else{



                            String planpuente = "N";
                            if(switch_planpuente.isChecked()){
                                planpuente ="S";
                            }


                            String consulta2 = "insert into Pedidoenc(prefijo,NitSec,CliSec,Obs,pdyear,pdmonth,pdday,planpuente,elisprecod)" +
                                    "values('"+Prefijo+"','" + Extras.getString("nitsec") + "'," +
                                    Extras.getInt("clisec") + ",'" +
                                    Observacion+ "'," +
                                    time.year + "," +
                                    (time.month + 1) + "," +
                                    (time.monthDay)+",'"+planpuente+"',"+ListaCodigoItem+" )";

                            BaseDeDatos.getWritableDatabase().execSQL(consulta2);
                        }

                        if (VisitaReg.getCount() > 0) {
                            String VisFotoimg = VisitaReg.getString(0);
                            if(Cambiofotovis.getText().toString().equalsIgnoreCase("S")){
                                Bitmap bm = ((BitmapDrawable) showImg.getDrawable()).getBitmap();
                                VisFotoimg = Path;//convert(bm);
                            }



                            BaseDeDatos.getWritableDatabase().execSQL("update Visita set VisObs='"+Observacion+"',VisFotoimg ='"+VisFotoimg+"' where nitsec='" + Extras.getString("nitsec") + "' and clisec=" + Extras.getInt("clisec")  +" and  VisAno=" + time.year + " and VisMes=" + (time.month + 1) + " and VisDia=" + time.monthDay+ " " /* and VisPref='" + Prefijo+"'"*/);
                            //Intent intent = new Intent(view.getContext(), BannerDescuentos.class);
                            AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                            Alerta.setMessage("Guardado con exito");
                            Alerta.setTitle("Alerta");
                            Alerta.setPositiveButton("OK", null);
                            Alerta.setCancelable(true);
                            Alerta.create().show();
                        }else {



                            Cursor VisitaReg2 = BaseDeDatos.getWritableDatabase().rawQuery("select NitCom  from Visita v left join clientes n on ltrim(rtrim(n.nitsec))=ltrim(rtrim(v.nitsec)) and n.clisec=v.clisec where   VisAno=" + time.year + " and VisMes=" + (time.month + 1) + " and VisDia=" + time.monthDay+" and vishorfin is null  ", null);
                            if (VisitaReg2.getCount() > 0 & (vEmpresa.trim().equalsIgnoreCase("GELVEZGIR") )) {
                                VisitaReg2.moveToFirst();
                                AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                                Alerta.setMessage("Tiene visita pendiente por cerrar, termina la visita antes de continuar "+VisitaReg2.getString(0));
                                Alerta.setTitle("Alerta");
                                Alerta.setPositiveButton("OK", null);
                                Alerta.setCancelable(true);
                                Alerta.create().show();
                            }else {

                                String VisFotoimg = "";
                                if(Cambiofotovis.getText().toString().equalsIgnoreCase("S")){
                                    Bitmap bm = ((BitmapDrawable) showImg.getDrawable()).getBitmap();
                                    VisFotoimg = Path;//convert(bm);
                                }


                                String consulta = "insert into Visita(NitSec,CliSec,VisObs,MovCauPed,MovCauNom,VisLatitud,VisLongitud,VisAno,VisMes,VisDia,VisHor,VisMin,VisSeg,VisPref,VisFotoimg)" +
                                        "values('" + Extras.getString("nitsec") + "'," +
                                        Extras.getInt("clisec") + ",'" +
                                        Observacion+ "'," +
                                        CausalCodigoItem + ",'" + CausalNombreItem + "','',''," +
                                        time.year + "," +
                                        (time.month + 1) + "," +
                                        (time.monthDay) + "," +
                                        (time.hour) + "," +
                                        (time.minute) + "," +
                                        time.second + ",'" +
                                        Prefijo+"','"+VisFotoimg+"')";



                                BaseDeDatos.getWritableDatabase().execSQL(consulta);

                                //Intent intent = new Intent(view.getContext(), BannerDescuentos.class);
                                AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                                Alerta.setMessage("Guardado con exito");
                                Alerta.setTitle("Alerta");
                                Alerta.setPositiveButton("OK", null);
                                Alerta.setCancelable(true);
                                Alerta.create().show();



                            }
                        }

                    }else{
                        AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                        Alerta.setMessage("Foto No puede quedar en blanco, cliete tiene mas de 90 dias");
                        Alerta.setTitle("Alerta");
                        Alerta.setPositiveButton("OK", null);
                        Alerta.setCancelable(true);
                        Alerta.create().show();
                    }
                }










            }
        });
        btn_registrarvisita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              int ListaCodigoItem = 0;
                if (spinner_lista.getSelectedItemPosition() > 0) {
                    Integer posicion = spinner_lista.getSelectedItemPosition();
                    ListaCodigoItem = colLisPreCod[posicion];
                } else {
                    ListaCodigoItem = colLisPreCod[0];
                }

                String Observacion = edit_observacion.getText().toString();
                Observacion = Observacion.replaceAll("[^\\w ]+", "");
                String Prefijo;
                if (spinner_tipo.getSelectedItemPosition() > 0) {
                    Integer posicion = spinner_tipo.getSelectedItemPosition();
                    Prefijo = TipoDocumento[posicion];
                } else {
                    Prefijo = TipoDocumento[0];
                }


                Integer CausalCodigoItem;
                String CausalNombreItem;
                if (spinner_causal.getSelectedItemPosition() > 0) {
                    Integer posicion = spinner_causal.getSelectedItemPosition();
                    CausalCodigoItem = CausalCodigo[posicion];
                    CausalNombreItem = CausalNombre[posicion];
                } else {
                    CausalCodigoItem = CausalCodigo[0];
                    CausalNombreItem = CausalNombre[0];
                }


                Time time = new Time();
                time.setToNow();
                String VisFotoimg ="";
                if (Cambiofotovis.getText().toString().equalsIgnoreCase("S")){
                    Bitmap bm = ((BitmapDrawable) showImg.getDrawable()).getBitmap();
                    VisFotoimg = Path;//convert(bm);
               }
                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vEmpresa=vGlobalVariables.getEmpresa();
                vEmpresa=vEmpresa.toUpperCase();

                   if(VisFotoimg.isEmpty() && vEmpresa.equalsIgnoreCase("SUHOGAR")){
                       AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                       Alerta.setMessage("Debe realizar una captura");
                       Alerta.setTitle("Alerta");
                       Alerta.setPositiveButton("OK", null);
                       Alerta.setCancelable(true);
                       Alerta.create().show();
                   }else{
                       Cursor VisitaReg = BaseDeDatos.getWritableDatabase().rawQuery("select *  from Visita where nitsec='" + Extras.getString("nitsec") + "' and clisec=" + Extras.getInt("clisec")  +" and  VisAno=" + time.year + " and VisMes=" + (time.month + 1) + " and VisDia=" + time.monthDay + " "/*and VisPref ='"+Prefijo+"'"*/, null);

                       Cursor pedidoen = BaseDeDatos.getWritableDatabase().rawQuery("select Obs from Pedidoenc where prefijo = '"+Prefijo+"' and nitsec = '" + Extras.getString("nitsec") + "' and clisec = " + Extras.getInt("clisec")  + " and  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  " /*and VisPref='" + Prefijo+"'"*/, null);
                       pedidoen.moveToFirst();


                       if (pedidoen.getCount() > 0) {

                       }else{
                           String planpuente = "N";
                           if(switch_planpuente.isChecked()){
                               planpuente ="S";
                           }


                           String consulta2 = "insert into Pedidoenc(prefijo,NitSec,CliSec,Obs,pdyear,pdmonth,pdday,planpuente,elisprecod)" +
                                   "values('"+Prefijo+"','" + Extras.getString("nitsec") + "'," +
                                   Extras.getInt("clisec") + ",'" +
                                   Observacion+ "'," +
                                   time.year + "," +
                                   (time.month + 1) + "," +
                                   (time.monthDay)+",'"+planpuente+"' , "+ListaCodigoItem+")";

                           BaseDeDatos.getWritableDatabase().execSQL(consulta2);
                       }



                       if (VisitaReg.getCount() > 0) {

                       }else {
                           String consulta = "insert into Visita(NitSec,CliSec,VisObs,MovCauPed,MovCauNom,VisLatitud,VisLongitud,VisAno,VisMes,VisDia,VisHor,VisMin,VisSeg,VisPref,VisFotoimg)" +
                                   "values('" + Extras.getString("nitsec") + "'," +
                                   Extras.getInt("clisec") + ",'" +
                                   Observacion + "'," +
                                   CausalCodigoItem + ",'" + CausalNombreItem + "','',''," +
                                   time.year + "," +
                                   (time.month + 1) + "," +
                                   (time.monthDay) + "," +
                                   (time.hour) + "," +
                                   (time.minute) + "," +
                                   time.second + ",'"+
                                   Prefijo + "','"+VisFotoimg+"')";

                           BaseDeDatos.getWritableDatabase().execSQL(consulta);

                       }
                       finish();
                   }


            }
        });


        btn_cerrarvisita.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
            @Override
            public void onClick(View view) {
               int ListaCodigoItem = 0;

                if (spinner_lista.getSelectedItemPosition() > 0) {
                    Integer posicion = spinner_lista.getSelectedItemPosition();
                    ListaCodigoItem = colLisPreCod[posicion];

                } else {
                    ListaCodigoItem = colLisPreCod[0];

                }

                String Observacion =  edit_observacion.getText().toString();
                Observacion=Observacion.replaceAll("[^\\w ]+", "");
                String Prefijo;
                if (spinner_tipo.getSelectedItemPosition() > 0) {
                    Integer posicion = spinner_tipo.getSelectedItemPosition();
                    Prefijo = TipoDocumento[posicion];
                } else {
                    Prefijo = TipoDocumento[0];
                }


                Integer CausalCodigoItem;
                String CausalNombreItem;
                if (spinner_causal.getSelectedItemPosition() > 0) {
                    Integer posicion = spinner_causal.getSelectedItemPosition();
                    CausalCodigoItem = CausalCodigo[posicion];
                    CausalNombreItem = CausalNombre[posicion];
                } else {
                    CausalCodigoItem = CausalCodigo[0];
                    CausalNombreItem = CausalNombre[0];
                }

                Time time = new Time();
                time.setToNow();

                Cursor VisitaReg = BaseDeDatos.getWritableDatabase().rawQuery("select VisFotoimg from Visita where nitsec='" + Extras.getString("nitsec") + "' and clisec=" + Extras.getInt("clisec")  +" and  VisAno=" + time.year + " and VisMes=" + (time.month + 1) + " and VisDia=" + time.monthDay+" "/*and VisPref = '"+Prefijo+"'"*/, null);
                VisitaReg.moveToFirst();

                if (VisitaReg.getCount() > 0) {
                    String VisFotoimg = VisitaReg.getString(0);
                    if(Cambiofotovis.getText().toString().equalsIgnoreCase("S")){
                        Bitmap bm = ((BitmapDrawable) showImg.getDrawable()).getBitmap();
                         VisFotoimg = Path;//convert(bm);
                    }
                    BaseDeDatos.getWritableDatabase().execSQL("update Visita set VisObs='"+ Observacion+"',VisFotoimg='"+VisFotoimg+"', VisHorFin="+ (time.hour)+",VisMinFin="+(time.minute)+",VisSegFin="+ (time.second)+"  where nitsec='" + Extras.getString("nitsec") + "' and clisec=" + Extras.getInt("clisec") +" and  VisAno=" + time.year + " and VisMes=" + (time.month + 1) + " and VisDia=" + time.monthDay+" "/*and VisPref = '"+Prefijo+"'"*/);

                    String planpuente = "N";
                    if(switch_planpuente.isChecked()){
                        planpuente ="S";
                    }


                    BaseDeDatos.getWritableDatabase().execSQL("update Pedidoenc set elisprecod ="+ListaCodigoItem+",planpuente='"+planpuente+"', Obs='"+Observacion+"'   where prefijo = '"+Prefijo+"' and nitsec = '" + Extras.getString("nitsec") + "' and clisec = " + Extras.getInt("clisec")  + " and  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  ");

                }


                finish();

            }
        });

        btn_capturar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File filedir=new File(ruta_fotos); if(!filedir.exists()) filedir.mkdirs();

                rrfile = ruta_fotos +"mwapp.jpg";
                mi_foto=new File(ruta_fotos, "mwapp.jpg");
                imageUri = Uri.fromFile(mi_foto);


                uri = Uri.fromFile(mi_foto);

                String fileName = "mwapp.jpg";

                ContentValues values = new ContentValues();

                values.put(MediaStore.Images.Media.TITLE, fileName);

                values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");


                try {
                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    //Retorna a la actividad
                    startActivityForResult(cameraIntent, 1);
                }catch (Exception e){
                    String kk="";
                }


            }
        });

        switch_planpuente.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                  if(b){

                  }
            }
        });

    }
    private void continuarProcesoNormal(View view) {

        GlobalVariables vGlobalVariables = GlobalVariables.getInstance();
        String vEmpresa = vGlobalVariables.getEmpresa();
        vEmpresa = vEmpresa.toUpperCase();
        String vUsuario = vGlobalVariables.getUsuario();

        int aPlazo = 0;
        String plazoNom = "";
        plazoNom = spinner_plazo.getSelectedItem().toString();
        if (spinner_plazo.getSelectedItemPosition() > 0) {
            Integer posicion = spinner_plazo.getSelectedItemPosition();

            aPlazo = DiasPlazo[posicion];
        } else {
            aPlazo = DiasPlazo[0];
        }


        GestorCartera gestorcartera = new GestorCartera();
        gestorcartera.TotalesCatera(getApplicationContext(), Extras.getString("nitsec"), 0);
        int Morageneral = gestorcartera.MoraGeneral;


        String Observacion = edit_observacion.getText().toString();
        Observacion = Observacion.replaceAll("[^\\w ]+", "");
        if (DiasUltVen < 90 || !rrfile.trim().isEmpty()) {


            if (spinner_tipo.getSelectedItemPosition() > 0) {
                Integer posicion = spinner_tipo.getSelectedItemPosition();
                Prefijo = TipoDocumento[posicion];
            } else {
                Prefijo = TipoDocumento[0];
            }


            Integer ListaCodigoItem;
            String ListaNombreItem;

           /* if (spinner_lista.getSelectedItemPosition() > 0) {
                Integer posicion = spinner_lista.getSelectedItemPosition();
                ListaCodigoItem = colLisPreCod[posicion];
                ListaNombreItem = colLisPreNom[posicion];
            } else {
                 = colLisPreCod[0];
                ListaNombreItem = colLisPreNom[0];
            }*/
            ListaCodigoItem =ListaCodigo;
            //String.valueOf(ListaCodigo);


            Integer bodegaitem;
            String bodeganomitem;
            if (spinner_bodega.getSelectedItemPosition() > 0) {
                Integer posicion = spinner_bodega.getSelectedItemPosition();
                bodegaitem = colLisBodCod[posicion];
                bodeganomitem = colLisbonom[posicion];
            } else {
                bodegaitem = colLisBodCod[0];
                bodeganomitem = colLisbonom[0];
            }


            Integer CausalCodigoItem;
            String CausalNombreItem;
            if (spinner_causal.getSelectedItemPosition() > 0) {
                Integer posicion = spinner_causal.getSelectedItemPosition();
                CausalCodigoItem = CausalCodigo[posicion];
                CausalNombreItem = CausalNombre[posicion];
            } else {
                CausalCodigoItem = CausalCodigo[0];
                CausalNombreItem = CausalNombre[0];
            }


            Time time = new Time();
            time.setToNow();


            BaseDeDatos.getWritableDatabase().execSQL("update pedido set  plazo=" + aPlazo + ", ConPagnom ='" + plazoNom + "' where nitsec='" + Extras.getString("nitsec") + "' and clisec=" + Extras.getInt("clisec") + " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo='" + Prefijo + "'");
            Cursor VisitaReg = BaseDeDatos.getWritableDatabase().rawQuery("select VisFotoimg  from Visita where nitsec='" + Extras.getString("nitsec") + "' and clisec=" + Extras.getInt("clisec") + " and  VisAno=" + time.year + " and VisMes=" + (time.month + 1) + " and VisDia=" + time.monthDay + " " /*and VisPref ='"+Prefijo+"'"*/, null);
            VisitaReg.moveToFirst();
            Cursor pedidoen = BaseDeDatos.getWritableDatabase().rawQuery("select Obs from Pedidoenc where prefijo = '" + Prefijo + "' and nitsec = '" + Extras.getString("nitsec") + "' and clisec = " + Extras.getInt("clisec") + " and  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  " /*and VisPref='" + Prefijo+"'"*/, null);
            pedidoen.moveToFirst();

            if (pedidoen.getCount() > 0) {
                String planpuente = "N";
                if (switch_planpuente.isChecked()) {
                    planpuente = "S";
                }
                BaseDeDatos.getWritableDatabase().execSQL("update Pedidoenc set elisprecod =" + ListaCodigo + " , planpuente='" + planpuente + "', Obs='" + Observacion + "'   where prefijo = '" + Prefijo + "' and nitsec = '" + Extras.getString("nitsec") + "' and clisec = " + Extras.getInt("clisec") + " and  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  ");
            } else {
                String planpuente = "N";
                if (switch_planpuente.isChecked()) {
                    planpuente = "S";
                }
                String consulta2 = "insert into Pedidoenc(prefijo,NitSec,CliSec,Obs,pdyear,pdmonth,pdday,planpuente,elisprecod)" +
                        "values('" + Prefijo + "','" + Extras.getString("nitsec") + "'," +
                        Extras.getInt("clisec") + ",'" +
                        Observacion + "'," +
                        time.year + "," +
                        (time.month + 1) + "," +
                        (time.monthDay) + ",'" + planpuente + "'," + ListaCodigo + " )";
                BaseDeDatos.getWritableDatabase().execSQL(consulta2);
            }


            if (VisitaReg.getCount() > 0) {
                String VisFotoimg = VisitaReg.getString(0);
                if (Cambiofotovis.getText().toString().equalsIgnoreCase("S")) {
                    Bitmap bm = ((BitmapDrawable) showImg.getDrawable()).getBitmap();
                    VisFotoimg = Path;// convert(bm);
                }

                BaseDeDatos.getWritableDatabase().execSQL("update Visita set VisObs='" + Observacion + "',VisFotoimg='" + VisFotoimg + "',visLisPreCod=" + ListaCodigoItem + " where nitsec='" + Extras.getString("nitsec") + "' and clisec=" + Extras.getInt("clisec") + " and  VisAno=" + time.year + " and VisMes=" + (time.month + 1) + " and VisDia=" + time.monthDay + " " /*and VisPref ='"+Prefijo+"'" */);
                vUsuario = vGlobalVariables.getUsuario();
                String tat = "select ParMovSec from Usuarios where VenCod = '" + vUsuario + "' ";
                Cursor tatcon = BaseDeDatos.getWritableDatabase().rawQuery(tat, null);
                int parmov = 0;
                if (tatcon.getCount() > 0) {
                    tatcon.moveToFirst();
                    parmov = tatcon.getInt(0);
                }

                //Intent intent = new Intent(view.getContext(), BannerDescuentos.class);
                Intent intent;
                int pendientes = 0;
                if (parmov > 1 && (!vEmpresa.equalsIgnoreCase("BRILLO") && !vEmpresa.equalsIgnoreCase("SUHOGAR"))) {
                    pendientes = pendientesnew();
                }


                intent = new Intent(view.getContext(), EditarProductoV2.class);
                intent.putExtra("nitsec", Extras.getString("nitsec"));
                intent.putExtra("clisec", Extras.getInt("clisec"));
                intent.putExtra("lisprecod",ListaCodigo);
                intent.putExtra("invgrucod", "");
                intent.putExtra("invsubgrucod", "");
                intent.putExtra("invfamcod", "");
                intent.putExtra("InvCatCod", "");
                intent.putExtra("plazo", aPlazo);
                intent.putExtra("plazoNom", plazoNom);
                intent.putExtra("prefijo", Prefijo);
                intent.putExtra("bodega", bodegaitem);

                btn_continuar.setVisibility(View.GONE);
                contenedorbarra.setVisibility(View.GONE);
                startActivity(intent);

            } else {


                Cursor VisitaReg2 = BaseDeDatos.getWritableDatabase().rawQuery("select NitCom  from Visita v left join clientes n on ltrim(rtrim(n.nitsec))=ltrim(rtrim(v.nitsec)) and n.clisec=v.clisec where   VisAno=" + time.year + " and VisMes=" + (time.month + 1) + " and VisDia=" + time.monthDay + " and vishorfin is null  ", null);
                if (VisitaReg2.getCount() > 0 && (vEmpresa.trim().equalsIgnoreCase("GELVEZGIR"))) {
                    VisitaReg2.moveToFirst();
                    AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                    Alerta.setMessage("Tiene visita pendiente por cerrar, termina la visita antes de continuar " + VisitaReg2.getString(0));
                    Alerta.setTitle("Alerta");
                    Alerta.setPositiveButton("OK", null);
                    Alerta.setCancelable(true);
                    Alerta.create().show();
                } else {


                    String VisFotoimg = "";
                    if (Cambiofotovis.getText().toString().equalsIgnoreCase("S")) {
                        Bitmap bm = ((BitmapDrawable) showImg.getDrawable()).getBitmap();
                        VisFotoimg = Path;//convert(bm);
                    }


                    String consulta = "insert into Visita(NitSec,CliSec,VisObs,MovCauPed,MovCauNom,VisLatitud,VisLongitud,VisAno,VisMes,VisDia,VisHor,VisMin,visLisPreCod,VisSeg,VisPref,VisFotoimg)" +
                            "values('" + Extras.getString("nitsec") + "'," +
                            Extras.getInt("clisec") + ",'" +
                            Observacion + "'," +
                            CausalCodigoItem + ",'" + CausalNombreItem + "','',''," +
                            time.year + "," +
                            (time.month + 1) + "," +
                            (time.monthDay) + "," +
                            (time.hour) + "," +
                            (time.minute) + "," +
                            ListaCodigoItem + "," +
                            time.second + ",'" +
                            Prefijo + "','" + VisFotoimg + "')";


                    BaseDeDatos.getWritableDatabase().execSQL(consulta);

                    //Intent intent = new Intent(view.getContext(), BannerDescuentos.class);
                    vUsuario = vGlobalVariables.getUsuario();
                    String tat = "select ParMovSec from Usuarios where VenCod = '" + vUsuario + "' ";
                    Cursor tatcon = BaseDeDatos.getWritableDatabase().rawQuery(tat, null);
                    int parmov = 0;
                    if (tatcon.getCount() > 0) {
                        tatcon.moveToFirst();
                        parmov = tatcon.getInt(0);
                    }

                    Intent intent;
                    int pendientes = 0;
                    intent = new Intent(view.getContext(), EditarProductoV2.class);
                    intent.putExtra("nitsec", Extras.getString("nitsec"));
                    intent.putExtra("clisec", Extras.getInt("clisec"));
                    intent.putExtra("lisprecod", Extras.getInt("lisprecod"));
                    intent.putExtra("invgrucod", "");
                    intent.putExtra("invsubgrucod", "");
                    intent.putExtra("invfamcod", "");
                    intent.putExtra("InvCatCod", "");
                    intent.putExtra("plazo", aPlazo);
                    intent.putExtra("plazoNom", plazoNom);
                    intent.putExtra("prefijo", Prefijo);
                    intent.putExtra("bodega", bodegaitem);

                    btn_continuar.setVisibility(View.GONE);
                    contenedorbarra.setVisibility(View.GONE);
                    startActivity(intent);
                }
            }


        } else {
            AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
            Alerta.setMessage("Foto No puede quedar en blanco, cliete tiene mas de 90 dias");
            Alerta.setTitle("Alerta");
            Alerta.setPositiveButton("OK", null);
            Alerta.setCancelable(true);
            Alerta.create().show();
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
                        new visitacliente.LoadImagesFromSDCard().execute(""+imageId);
                        //cambiofoto.setText("S");
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








        /*if(requestCode == 2) {
            if(resultCode == Activity.RESULT_OK) {
                TextView Ciudad= (TextView) findViewById(R.id.textView24);
                Ciudad.setText(data.getStringExtra("CIUDAD"));
            }
        }
        if(requestCode == 3) {
            if(resultCode == Activity.RESULT_OK) {
                TextView Barrio= (TextView) findViewById(R.id.textView24);
                Barrio.setText(data.getStringExtra("CIUDAD"));
            }
        }
        if(requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
                try {

                    if ( resultCode == RESULT_OK) {



                        String imageId = convertImageUriToFile( imageUri,CameraActivity);



                        new LoadImagesFromSDCard().execute(""+imageId);

                        *//*********** Load Captured Image And Data End ****************//*


                    } else if ( resultCode == RESULT_CANCELED) {

                        Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
                    } else {

                        Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
                    }




                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }*/

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

  //  @Override
//    public void onMapReady(GoogleMap googleMap) {

        /*
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-33.569697, -70.62996);
        MarkerOptions Makerop= new MarkerOptions();
        Makerop.position(sydney).title("Aqui estas ubicado ");
        mMap.addMarker(Makerop);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(sydney, 17);

        mMap.moveCamera(cameraUpdate);
        */

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
 //   }


    /**
     * Async task for loading the images from the SD card.
     *
     * @author Android Example
     *
     */

    // Class with extends AsyncTask class

    public class LoadImagesFromSDCard  extends AsyncTask<String, Void, Void> {

        private ProgressDialog Dialog = new ProgressDialog(visitacliente.this);

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
                Cambiofotovis.setText("S");
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

    public void TomarUbicacion(){


        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);

        try {
            String GeoRef = "N";
            Cursor SqlUsuarios = BaseDeDatos.getWritableDatabase().rawQuery("select ifnull(ParGeoRef,'N') from usuarios where VenCnt=1", null); //order by nombre
            if (SqlUsuarios.getCount() > 0) {
                int vuelta = 0;
                SqlUsuarios.moveToFirst();
                do {
                    GeoRef = SqlUsuarios.getString(0);
                } while (SqlUsuarios.moveToNext());
            }
/*
            if (GeoRef.equalsIgnoreCase("S") ) {


                GPSTracker vgpstraker = new GPSTracker(getApplicationContext());
                vgpstraker.getLocation();
                if (vgpstraker.canGetLocation) {
                    Double latitud = vgpstraker.getLatitude();
                    Double longitud = vgpstraker.getLongitude();
                    txt_latitud.setText(latitud.toString().trim());
                    txt_longitud.setText(longitud.toString().trim());

                    //controlMapa.setZoom(10);

                    LatLng sydney = new LatLng(latitud, longitud);
                    MarkerOptions Makerop= new MarkerOptions();
                    Makerop.position(sydney).title("Aqui estas ubicado ");
                    mMap.addMarker(Makerop);

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(sydney, 17);

                    mMap.moveCamera(cameraUpdate);

                } else {
                    AlertDialog.Builder Alerta = new AlertDialog.Builder(getApplicationContext());
                    Alerta.setMessage("Dispositivos no activos, para tomar ubicacion");
                    Alerta.setTitle("Alerta");
                    Alerta.setPositiveButton("OK", null);
                    Alerta.setCancelable(true);
                    Alerta.create().show();
                }
            }

 */
        }catch (Exception e){
            int error=0;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        contenedorbarra.setVisibility(View.GONE);
        Button btn_continuar  = findViewById(R.id.btn_continuar);
        TextView numitems = findViewById(R.id.numitems);
        btn_continuar.setVisibility(View.VISIBLE);
        numitems.setText("0");
    }


    @Override
    protected void onResume() {
        super.onResume();
        spinner_bodega.setEnabled(desbloquear(Prefijo,nitsec,clisec));
        spinner_lista.setEnabled(desbloquear(Prefijo,nitsec,clisec));
    }



    public boolean desbloquear(String prefijo,String NitSec, int CliSec) {
        boolean opcion = true;
        BaseDatos BaseDeDatos = new BaseDatos(this, "MantisMovil", null, 6);
        Time time = new Time();
        time.setToNow();
        Cursor cursorpedido = BaseDeDatos.getWritableDatabase().rawQuery("select ArtSec,bodcod from pedido where prefijo = '" + prefijo + "' and nitsec = '" + NitSec + "' and clisec = " + CliSec + " and  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and  ifNULL(cant,0)+ifNULL(cantcaj,0)<>0 ", null);

        if (cursorpedido.getCount() > 0) {
         opcion = false ;
        } else {
         opcion = true;
        }

        return opcion;
    }

public void actualizarexistencia(int bodegaitem,Intent intent)//sql
{
    final ProgressBar vPrBar_Import;
    final TextView resfinal,numitems;
    ConnectivityManager cm;
    String vnitsec=Extras.getString("nitsec");
    NetworkInfo ni;
    resfinal = findViewById(R.id.resfinal);
    numitems = findViewById(R.id.numitems);
    vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_NArticulos);
    GlobalVariables vGlobalVariables = GlobalVariables.getInstance();
    String vUsuario = vGlobalVariables.getUsuario();
    String vEmpresa = vGlobalVariables.getEmpresa();
    vPrBar_Import.setProgress(0);
        Log.e("Bodega",String.valueOf(bodegaitem));
    if(hizopedido(vnitsec) == 0){
    new Thread(new Runnable() {
        @Override
        public void run() {
            GlobalVariables gGlobalVariables=null;
            intento+=1;
            int TotalFilas = 0;
            int Insertados = 0;
            int Errores = 0;
            int Vueltas = 0;
            int vAliNegCod= 0;
            Integer vSucCod= 0;
            gGlobalVariables = GlobalVariables.getInstance();
            vAliNegCod=gGlobalVariables.getAliNegCod();
            vSucCod=gGlobalVariables.getSucCod();
            BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
            SQLiteDatabase BdSql = vBaseDeDatos.getReadableDatabase();
            Connection conn = null;
            Statement comm = null;
            ResultSet rsImport  = null;
            try {
                ConBd conbd = new ConBd();
                conbd.Variables();
                String sincroniza = conbd.sincronizalinea;
                 conn = conbd.CargarConexion(getApplicationContext());
                if(sincroniza.equalsIgnoreCase("S") && conn != null){



                    try{
                         comm = conn.createStatement();
                        String  Script="select * from (select ROW_NUMBER() OVER(ORDER BY e.ArtSec ASC) AS Row, e.Artsec,ArtBodCod,(Artexiact-(select isnull(sum(artuniminapa),0) from articulosunidad ae left join carunidades c on ae.Artalinegcod=c.alinegcod where  CarUniSucCod="+vSucCod+" and ae.artsec= e.Artsec and AliNegTat='S' and (select AliNegTat from carunidades n where n.alinegcod="+vAliNegCod+")<>'S' )) Artexiact  from Articulosexi e where Artexiact > 0 and ArtBodCod ="+bodegaitem+" ) Consulta order by Row desc ";
                        Log.e("Scriptexis",Script);
                         rsImport = comm.executeQuery(Script);

                        while (rsImport.next()) {
                            if (TotalFilas == 0) {
                                vPrBar_Import.setMax(rsImport.getInt("Row"));
                                TotalFilas = rsImport.getInt("Row");
                                String Filas = rsImport.getString("Row").trim();
                            }
                            Vueltas += 1;
                            vPrBar_Import.setMax(TotalFilas);
                            vPrBar_Import.setProgress(Vueltas);


                            // Log.e("Artsec",rsImport.getString("Artsec").trim());
                            String ArtSec = rsImport.getString("Artsec").trim();
                            String existencia = rsImport.getString("Artexiact").trim();
                            String ArtBodCod = rsImport.getString("ArtBodCod").trim();

                            String updatetScript = "update ArticulosExi set ArtExiAct = " + existencia + "  where ArtSec = " + ArtSec + " and ArtBodCod = "+ArtBodCod+" ";

                            try {
                                if (BdSql.isDbLockedByCurrentThread()) {
                                    BdSql.endTransaction();
                                    Errores += 1;
                                }
                                BdSql.execSQL(updatetScript);
                                Insertados += 1;

                            } catch (Exception ex) {
                                Log.e("erroexr",ex.toString());

                                Errores += 1;
                            }

                            try {
                                final int finalTotalFilas = TotalFilas;
                                final int finalInsertados = Insertados;
                                final int finalErrores = Errores;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        numitems.setText(String.valueOf(finalInsertados));
                                        resfinal.setText(String.valueOf(finalTotalFilas));
                                        // vPrBar_Ciudades.setMax(TotalFilas);
                                        // vPrBar_Ciudades.setProgress(Vueltas);
                                    }
                                });
                            } catch (Exception e) {
                                int hh = 0;
                            }
                        }

                        startActivity(intent);
                    }catch (SQLException sq){
                        Log.e("SQLException",sq.toString());
                    }



                }
                else {
                    startActivity(intent);
                }
            } catch (Exception e) {
                Log.e("error",e.toString());
                startActivity(intent);
            }finally { // Cerramos las conexiones, en orden inverso a su apertura
                try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
            }



        }
    }).start();

}else{
        startActivity(intent);
    }
}
    public void traerArticulos(Intent intent)//sql
    {
        final ProgressBar vPrBar_Import;
        final TextView resfinal,numitems;
        ConnectivityManager cm;
        String vnitsec=Extras.getString("nitsec");
        NetworkInfo ni;
        resfinal = findViewById(R.id.resfinal);
        numitems = findViewById(R.id.numitems);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_NArticulos);
        GlobalVariables vGlobalVariables = GlobalVariables.getInstance();
        String vUsuario = vGlobalVariables.getUsuario();
        String vEmpresa = vGlobalVariables.getEmpresa();
        vPrBar_Import.setProgress(0);
if(hizopedido(vnitsec) == 0){
    new Thread(new Runnable() {
        @Override
        public void run() {
            intento+=1;
            int TotalFilas = 0;
            int Insertados = 0;
            int Errores = 0;
            int Vueltas = 0;

            BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
            SQLiteDatabase BdSql = vBaseDeDatos.getReadableDatabase();
            Connection conn = null;
            Statement comm = null;
            ResultSet rsImport  = null;
            try {
                ConBd conbd = new ConBd();
                conbd.Variables();
                String sincroniza = conbd.sincronizalinea;
                 conn = conbd.CargarConexion(getApplicationContext());
                if(sincroniza.equalsIgnoreCase("S") && conn != null){
                   try{
                        comm = conn.createStatement();


                       int vSucCod = vGlobalVariables.getSucCod();
                       int vAliNegCod = vGlobalVariables.getAliNegCod();

                       String Script = "select * from (select ROW_NUMBER() OVER(ORDER BY a.ArtSec ASC) AS Row,a.ArtSec,ArtCod,replace(ArtNom,'''','')+' '+isnull((select top 1 preartnom from artpre ap left join presentacionarticulos p on ap.preartcod=p.preartcod where preartfaccon='S' and ap.artsec=a.artsec ),'') ArtNom,isnull(ArtMedNomCom,'') ArtMedNomCom,g.InvGruCod,g.InvGruNom," +
                               "s.InvSubGruCod,s.InvSubGruNom,a.InvFamCod,InvFamNom,ParConIva,0 PrePreFijCosPro," +
                               "isnull((select top 1 movparlindes from MovParLineaGrupos ff left join movparlinea ml on ml.movparlinsec=ff.movparlinsec where movparlinencinvgrucod=s.invgrucod and MOVPARLINFECINI<=CONVERT(date, GETDATE()) and MOVPARLINFECFIN>=CONVERT(date, GETDATE()) ),0) desc1," +   //
                               "isnull((select top 1 MovParArtDetDesc from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod=" + vAliNegCod + " and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                               "),0) desc2," +
                               "isnull((select top 1 MovParArtDetDesc2 from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod=" + vAliNegCod + " and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc2<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                               "),0) desc5," +
                               "isnull((select top 1 MovParArtFecMod from MovParArtArticulos where MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) ),'') MovParArtFecMod" +
                               ",0 desc3,0 desc4,0 desc6,1000 Exist2 " +
                               ",(isnull((select sum(artexiact) from articulosexi ae where ae.artsec=a.artsec and artbodcod in(select alinegbodsucccsec from carunidadesalinegbodsuc where alinegcod=" + vAliNegCod + ")),0) - (select isnull(sum(artuniminapa),0) from articulosunidad ae left join carunidades c on ae.Artalinegcod=c.alinegcod where  CarUniSucCod=" + vSucCod + " and ae.artsec=a.artsec and AliNegTat='S' and (select AliNegTat from carunidades n where n.alinegcod=" + vAliNegCod + ")<>'S' )) Exist " +
                               ",isnull((select sum(artexiact) from articulosexi ae where ae.artsec=a.artsec " +
                               "and artbodcod=(select bodsucccsec from bodegasucursalcc where  BodProVen='S' and succod=" + vSucCod + ")),0) ExistFec " +
                               ",isnull(c.ClaArtCod,0) ClaArtCod,isnull(c.ClaArtNom,'') ClaArtNom,isnull(ArtValImp,0) ArtValImp,ArtEmb,isnull(ArtIndMpm,'') ArtIndMpm  " +
                               ",0 ArtRen,0 ArtLim " +
                               "from articulos a " +
                               "left join inventariofamilia f on a.invfamcod=f.invfamcod " +
                               "left join inventariosubgrupo s on f.invsubgrucod= s.invsubgrucod " +
                               "left join inventariogrupo g on g.invgrucod=s.invgrucod " +
                               "left join clasearticulo c on c.claartcod=a.claartcod " +
                               "left join parametrocontable p on p.parconcod=a.parconcod " +
                               "left join artpre pp on a.artsec=pp.artsec and PreArtFacCon='S' " +
                               " WHERE (a.artdes<>'S' or (select sum(artexiact) from articulosexi ae where ae.artsec=a.artsec and artbodcod in(select alinegbodsucccsec from carunidadesalinegbodsuc where alinegcod="+vAliNegCod+")) <> 0 ) " +
                               " and " +
                               "" +
                               "" +
                               "" +
                               "((" +
                               "" +
                               " s.invgrucod in(select invgrucod from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "')  " +
                               "  ) or a.artsec in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes<>'S')) AND a.artsec NOT in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes='S')) Consulta order by Row desc";

                        rsImport = comm.executeQuery(Script);

                       while (rsImport.next()) {
                           if (TotalFilas == 0) {
                               vPrBar_Import.setMax(rsImport.getInt("Row"));
                               TotalFilas = rsImport.getInt("Row");
                               String Filas = rsImport.getString("Row").trim();
                           }
                           Vueltas += 1;
                           vPrBar_Import.setMax(TotalFilas);
                           vPrBar_Import.setProgress(Vueltas);

                           String artsec = rsImport.getString("ArtSec").trim();
                           String existencia = rsImport.getString("Exist").trim();
                           String exfec = rsImport.getString("ExistFec").trim();

                           String updatetScript = "update articulos set Exist = " + existencia + " , ExistFec = " + exfec + " where ArtSec = " + artsec + "  ";

                           try {
                               if (BdSql.isDbLockedByCurrentThread()) {
                                   BdSql.endTransaction();
                                   Errores += 1;
                               }
                               BdSql.execSQL(updatetScript);
                               Insertados += 1;

                           } catch (Exception ex) {
                               Errores += 1;
                           }


                           try {
                               final int finalTotalFilas = TotalFilas;
                               final int finalInsertados = Insertados;
                               final int finalErrores = Errores;
                               handler.post(new Runnable() {
                                   @Override
                                   public void run() {
                                       numitems.setText(String.valueOf(finalInsertados));
                                       resfinal.setText(String.valueOf(finalTotalFilas));
                                       // vPrBar_Ciudades.setMax(TotalFilas);
                                       // vPrBar_Ciudades.setProgress(Vueltas);
                                   }
                               });
                           } catch (Exception e) {
                               int hh = 0;
                           }


                       }

                       startActivity(intent);
                   }catch (SQLException w){
                       Log.e("SQLExceptionw",w.toString());
                   }

                }
                else {
                    startActivity(intent);
                }
            } catch (Exception e) {
                startActivity(intent);
            }finally { // Cerramos las conexiones, en orden inverso a su apertura
                try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
            }



        }
    }).start();
}else{
    startActivity(intent);
}




    }

    public int pendientesnew(){
        final Time time = new Time();
        time.setToNow();
        String fecha =  time.year +"-"+  (time.month + 1) +"-"+  time.monthDay;
        String conNumPed ="";
        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();
        String vnitsec=Extras.getString("nitsec");
        Integer vclisec=Extras.getInt("clisec");
        String conCliente = vnitsec+"-"+vclisec;
        int pendientes = 0;

        BaseDatos vBaseDeDatos = new BaseDatos(this, "MantisMovil", null, 6);
        String selectSrcipt = "select (prefijo||'"+vUsuario+"'||p.nitsec||p.clisec||'"+fecha+"') as pednum from pedido p left join clientes c on p.nitsec=c.nitsec and p.clisec=c.clisec " +
                "  where (p.NitSec||'-'||p.Clisec)  <> '" + conCliente+ "' and CliNoRee ='N' and (pedenviado = 'N' or pedenviado is null) and  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and ifNULL(cant,0)+ifNULL(cantcaj,0)<>0 group by p.NitSec,p.CliSec,c.NitCom,c.CliNom,prefijo,pedenviado ";
        final Cursor cursor = vBaseDeDatos.getWritableDatabase().rawQuery(selectSrcipt, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                conNumPed = cursor.getString(0);

            } while (cursor.moveToNext());
        }
        pendientes = cursor.getCount();
        pendientes = 0;
        return pendientes;
    }

/*
    public int enviospendientes(){
        final Time time = new Time();
        time.setToNow();
        String fecha =  time.year +"-"+  (time.month + 1) +"-"+  time.monthDay;
        String conNumPed ="";
        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();
        String vnitsec=Extras.getString("nitsec");
        Integer vclisec=Extras.getInt("clisec");
       String conCliente = vnitsec+"-"+vclisec;
        int pendientes = 0;

        //order by nombre

        BaseDatos vBaseDeDatos = new BaseDatos(this, "MantisMovil", null, 6);
        String selectSrcipt = "select (prefijo||'"+vUsuario+"'||p.nitsec||p.clisec||'"+fecha+"') as pednum from pedido p left join clientes c on p.nitsec=c.nitsec and p.clisec=c.clisec " +
                "  where (p.NitSec||'-'||p.Clisec)  <> '" + conCliente+ "' and CliNoRee ='N' and  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and ifNULL(cant,0)+ifNULL(cantcaj,0)<>0 group by p.NitSec,p.CliSec,c.NitCom,c.CliNom,prefijo,pedenviado ";
        final Cursor cursor = vBaseDeDatos.getWritableDatabase().rawQuery(selectSrcipt, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                conNumPed = cursor.getString(0);
                pendientes += enviado(conNumPed);

            } while (cursor.moveToNext());
        }

        return pendientes;
    }

    public int enviado(String NumPed) //sql
    {
        int bandera = 0;

        ConBd conbd = new ConBd();
        conbd.Variables();
        String sincroniza = conbd.sincronizalinea;

        if(sincroniza.equalsIgnoreCase("S")){  /// condicional


        Connection conn = conbd.CargarConexion();
        Statement comm = null;
        String Sinbd="N";

if(conn != null){



        try {
            comm = conn.createStatement();



            GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
            String vUsuario=vGlobalVariables.getUsuario();
            String Empresa=vGlobalVariables.getEmpresa();
            Empresa=Empresa.toUpperCase();


            String sExistencia="0.00";

            try {
                ResultSet rsImport = comm.executeQuery("select cast(isnull(sum(((((CotArtCaj*CotArtEmb)+CotArtUni)*(isnull(CotArtValImp,0)+CotArtPrecio))*(1-(CotArtDesUno/100))*(1-(CotArtDesDos/100))*(1-(CotArtDesTre/100))*(1-(CotArtDesCua/100)))*(1+(CotPorIva/100))),0) as numeric(18,2)) val from CotizacionesDetalle1 cd left join  Cotizaciones1 c on c.CotSec=cd.CotSec \n" +
                        "where cotnum='"+NumPed+"' and cotsubvencod='"+vUsuario.trim()+"'");

                while (rsImport.next()) {
                    sExistencia = rsImport.getString("val").trim();

                }
                if (conbd.ActulizaOnline=="S" && !Empresa.trim().equalsIgnoreCase("FARMA")) {
                    ResultSet rsImport2 = comm.executeQuery("select cast(ISNULL(SUM(karvaltotmendes+karartiva),0) as numeric(18,2)) val from kardex cd left join  factura c on c.facsec=cd.facsec \n" +
                            "where facnro='" + NumPed + "' and facvencod='"+vUsuario.trim()+"'");

                    while (rsImport2.next()) {
                        sExistencia = rsImport2.getString("val").trim();

                    }
                }
            }catch (Exception e){
                int hh=0;
            }

            if (sExistencia.equalsIgnoreCase("0.00")) {
                bandera+=1;
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Sinbd="S";
            bandera = 0;
        }


}  else{
            bandera = 0;
        }


        }else{
            bandera = 0;
        }



        return bandera;
    }

    */

    public String convert(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

public int hizopedido(String nitsec){
        int bandera = 0;
    final Time time = new Time();
    time.setToNow();
    BaseDatos vBaseDeDatos = new BaseDatos(this, "MantisMovil", null, 6);

    String selectSrcipt = "select ArtSec as pednum from pedido p left join clientes c on p.nitsec=c.nitsec and p.clisec=c.clisec " +
            "  where p.NitSec = '" + nitsec+ "' and CliNoRee ='N' and  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and ifNULL(cant,0)+ifNULL(cantcaj,0)<>0 group by p.NitSec,p.CliSec,c.NitCom,c.CliNom,prefijo,pedenviado ";
    final Cursor cursor = vBaseDeDatos.getWritableDatabase().rawQuery(selectSrcipt, null);

    if(cursor.getCount() > 0){
        cursor.moveToFirst();
        do{
            bandera += 1;
        }while (cursor.moveToNext());

    }

        return bandera;
}



    public String[] validacion_bloqueo(String NumeroPedido){
        String RemBloqueo ="N";
        String NotaBloqueo ="N";
        ConBd conbd = new ConBd();
        conbd.Variables();
        String sql= conbd.UrlValidarBloqueo;



        URL url = null;
        HttpURLConnection conn;

        try {
            url = new URL(sql);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");//; utf-8
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(1000);

            StringBuilder result = new StringBuilder();
            //   result.append(URLEncoder.encode("SDTArtSecWS2", "UTF-8"));
            //   result.append("=");
            result.append("{\"NumeroPedido\":\""+NumeroPedido+"\"}"); //URLEncoder.encode(  , "UTF-8")


            Log.e("url: ",url.toString());
            Log.e("body: ",result.toString());


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os)); //, "UTF-8"

            writer.write(result.toString());
            writer.flush();
            writer.close();
            os.close();

            conn.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine;

            StringBuffer response = new StringBuffer();

            String json = "";

            while((inputLine = in.readLine()) != null){
                response.append(inputLine);
            }

            json = "["+response.toString()+"]";

            JSONArray jsonArr = null;

            jsonArr = new JSONArray(json);
            String mensaje = "";
            for(int i = 0;i<jsonArr.length();i++){
                JSONObject jsonObject = jsonArr.getJSONObject(i);
                RemBloqueo=jsonObject.optString("RemBloqueo");
                NotaBloqueo=jsonObject.optString("NotBloqueo");

                MensajeBloqueo = jsonObject.optString("Mensaje");

                if(RemBloqueo.equalsIgnoreCase("N") && NotaBloqueo.equalsIgnoreCase("S")){
                    MensajeBloqueo = jsonObject.optString("notMensaje");
                }

            }
            // sal.setText(mensaje);
        } catch (MalformedURLException e) {
             RemBloqueo ="N";
             NotaBloqueo ="N";
            e.printStackTrace();
        } catch (IOException e) {
            RemBloqueo ="N";
            NotaBloqueo ="N";
            e.printStackTrace();
        } catch (JSONException e) {
            RemBloqueo ="N";
            NotaBloqueo ="N";
            e.printStackTrace();
        } catch (Exception e) {
            RemBloqueo ="N";
            NotaBloqueo ="N";
            e.printStackTrace();
        }

        return new String[]{RemBloqueo, NotaBloqueo};
    }

    private void mostrarCargando(){
        loadingOverlay.setVisibility(View.VISIBLE);
    }

    private void ocultarCargando(){
        loadingOverlay.setVisibility(View.GONE);
    }


}



