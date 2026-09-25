package com.ficc.mwmovil;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Sincronizar extends AppCompatActivity {
    private Handler handler ;
    static int OrdenImportar;
    static String ErrorTxt ="";
    int ErroresGen=0;
    Context thisContext=null;
    String vEmpresa,mantisficc;
    int vAliNegCod;
    Integer vSucCod;
    GlobalVariables gGlobalVariables=null;

    String ErrorSQL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sincronizar);
        getSupportActionBar().hide();


        gGlobalVariables = GlobalVariables.getInstance();
        String vUsuario=gGlobalVariables.getUsuario();
        vEmpresa=gGlobalVariables.getEmpresa();


        ConBd conbd = new ConBd();
        conbd.Variables();
        mantisficc = conbd.MantisFicc;

        Integer vParMovSec=gGlobalVariables.getParMovSec();
        vAliNegCod=gGlobalVariables.getAliNegCod();
        vSucCod=gGlobalVariables.getSucCod();
        final Time time = new Time();
        time.setToNow();

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);

        final Button btn_Sincronizar= (Button)findViewById(R.id.btn_Sincronizar);
        handler = new Handler();
        btn_Sincronizar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
             if(isOnlineNet()){


                 BaseDeDatos.sincronizartodo(BaseDeDatos.getWritableDatabase());
                 BaseDeDatos.getReadableDatabase().execSQL("delete from Sincronizaciones where SinAno=" + time.year + " and SinMes=" + (time.month + 1) + " and SinDay=" + time.monthDay);
                 try {
                     OrdenImportar=1;
                     thisContext=view.getContext();
                     runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             CargarDatos();
                             //msjpopup();
                         }
                     });

                     new Thread(new Runnable() {
                         @Override
                         public void run() {}

                     }).start();

                 } catch (Exception e) {

                     AlertDialog.Builder Alerta = new AlertDialog.Builder(Sincronizar.this);
                     Alerta.setMessage( e.getMessage());
                     Alerta.setTitle("Error");
                     Alerta.setPositiveButton("OK", null);
                     Alerta.setCancelable(true);
                     Alerta.create().show();
                     int Pare=9999;
                 }
                 btn_Sincronizar.setEnabled(false);
                 btn_Sincronizar.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
             }else{
                 AlertDialog.Builder Alerta = new AlertDialog.Builder(Sincronizar.this, R.style.MyDialogsinconRojo); //
                 Alerta.setMessage("Error de conexón, compruebe su internet");
                 Alerta.setTitle("Error");
                 Alerta.setPositiveButton("OK", null);
                 Alerta.setCancelable(true);
                 Alerta.create().show();
             }



            }
        });
    }
    public  void CargarDatos() {

        gGlobalVariables = GlobalVariables.getInstance();

        String DescV2=gGlobalVariables.getParMovDescV2();
        int vSucCod=gGlobalVariables.getSucCod();
        ScrollView Sv = (ScrollView) findViewById(R.id.ScrollViewSv);

        if (OrdenImportar == 1) {
            Log.e("entro",String.valueOf(OrdenImportar));
            if(mantisficc.equalsIgnoreCase("N")) {
                CargarClientes();
            }else{
                cargarclientesFicc();

            }

        }
        if (OrdenImportar == 2) {
            // CargarClaseXPerf();
            Log.e("entro",String.valueOf(OrdenImportar));


            if(mantisficc.equalsIgnoreCase("N")) {
                if (DescV2.equalsIgnoreCase("N")) { //|| vEmpresa.trim().equalsIgnoreCase("SURTIMARCAS")
                    MovParEsc();
                    //  OrdenImportar += 1;
                } else {
                    OrdenImportar += 1;
                }
            }else{
                OrdenImportar += 1;
            }


            // CargarGrupo();
            // OrdenImportar=3;

        }

        if (OrdenImportar == 3) {
            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {
                CargarCiudades();
            }else{
                CargarCiudadesFicc();
            }

        }
        if (OrdenImportar == 4) {
            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {
                CargarBarrios();
            }else{
                CargarBarriosFicc();
            }
        }


        if (OrdenImportar == 5) {
            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {
                CargarCartera();
            }else{
               // OrdenImportar += 1;
             CargarCarteraFicc();
            }
        }

        if (OrdenImportar == 6) {
            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {
                CargarMovCauPed();
            }else{
                CargarMovCauPedFicc(); //dejar fijo el valor de pedido de ibañez
               // OrdenImportar += 1;
            }
        }
        if (OrdenImportar == 7) {
            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {
                CargarArticulos();
            }else{
                CargarArticulosFicc();
            }
        }
        if (OrdenImportar == 8) {
            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {
                CargarGrupo();
            }else{
                CargarGrupoFicc();
            }
        }
        if (OrdenImportar == 9) {
            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {
                CargarSubGrupo();
            }else{
                CargarSubGrupoFicc();
            }
        }
        if (OrdenImportar == 10) {
            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {
                CargarFamilia();
            }else{
             CargarFamiliaFicc();

            }
        }
        if (OrdenImportar == 11) {
            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {
                if (DescV2.equalsIgnoreCase("N")) { //|| vEmpresa.trim().equalsIgnoreCase("SURTIMARCAS")
                    CargarMovParBonProdBon();
                    Log.e("Entro aca 1prod: ",DescV2);
                    //  OrdenImportar += 1;
                } else {
                    OrdenImportar += 1;
                }
            }else{
                OrdenImportar += 1;
            }

        }
        if (OrdenImportar == 12) {
            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {
            if (DescV2.equalsIgnoreCase("N")  ) { //|| vEmpresa.trim().equalsIgnoreCase("SURTIMARCAS")
                CargarMovParBonBonificados();
                Log.e("Entro aca : ",DescV2);
                //  OrdenImportar += 1;
            }else{
                OrdenImportar += 1;
            }


        } else{
                OrdenImportar += 1;
            }
        //    if (vEmpresa.trim().equalsIgnoreCase("SURTI")==false ) {
            //CargarMovParBonBonificados();
       //     }else{
        //        OrdenImportar += 1;
        //    }
        }

        if (OrdenImportar == 13) {
            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {
                CargarCanalSubCanal();
            }else{
                CargarCanalSubCanalFicc();

            }
        }

        if (OrdenImportar == 14) {
            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {
                CargarClaseXPerf();
            }else{
                OrdenImportar += 1;
            }



        }
        if (OrdenImportar == 15) {

            Log.e("entro",String.valueOf(OrdenImportar));


            if(mantisficc.equalsIgnoreCase("N")) {
                CargarHistorialNotas();
            }else{
                OrdenImportar += 1;
            }

        }
        if (OrdenImportar == 16) {

            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {


            if (DescV2.equalsIgnoreCase("N")  ) { //|| vEmpresa.trim().equalsIgnoreCase("SURTIMARCAS")
                MovParMix();
                //  OrdenImportar += 1;
            }else{
                OrdenImportar += 1;
            }
            }else{
                OrdenImportar += 1;
            }

        }
        if (OrdenImportar == 17) {

            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {

            if (DescV2.equalsIgnoreCase("N")  ) { //|| vEmpresa.trim().equalsIgnoreCase("SURTIMARCAS")
                MovParMixArticulos();
                //  OrdenImportar += 1;
            }else{
                OrdenImportar += 1;
            }
            }else{
                OrdenImportar += 1;
            }


        }
        if (OrdenImportar == 18) {

            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {
                CargarTipoDireccion();
            }else{
                OrdenImportar += 1;
            }
        }


        if (OrdenImportar == 19) {

            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {

            if (DescV2.equalsIgnoreCase("N")  ) { //|| vEmpresa.trim().equalsIgnoreCase("SURTIMARCAS")
                MovParMixBonificados();
                //  OrdenImportar += 1;
            }else{
                OrdenImportar += 1;
            }
            }else{
                OrdenImportar += 1;
            }

        }

        if (OrdenImportar == 19) {

          CargarConceptosNotasFicc();//OrdenImportar += 1;

        }

        if (OrdenImportar == 20) {

            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {
                VentasPorLinea();
            }else{
                OrdenImportar += 1;
            }
        }

        if (OrdenImportar == 21) {

            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {
            if (DescV2.equalsIgnoreCase("N")  ) { //|| vEmpresa.trim().equalsIgnoreCase("SURTIMARCAS")
                MovParLinea();
                //  OrdenImportar += 1;
            }else{
                OrdenImportar += 1;
            }
            }else{
                OrdenImportar += 1;
            }

        }
        if (OrdenImportar == 22) {

            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {
            if (DescV2.equalsIgnoreCase("N")  ) { //|| vEmpresa.trim().equalsIgnoreCase("SURTIMARCAS")
                MovParArt();
                if(vEmpresa.equalsIgnoreCase("MENTAHAIR") || vEmpresa.equalsIgnoreCase("MENTAHAIRCOT")){
                    MovParDespro();
                }

                //  OrdenImportar += 1;
            }else{
                OrdenImportar += 1;
            }
            }else{
                OrdenImportar += 1;
            }
        }
        if (OrdenImportar == 23) {

            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {
            if (DescV2.equalsIgnoreCase("N")  ) { //|| vEmpresa.trim().equalsIgnoreCase("SURTIMARCAS")
                MovParVal();
                //  OrdenImportar += 1;
            }else{
                OrdenImportar += 1;
            }
            }else{
                OrdenImportar += 1;
            }
        }
        if (OrdenImportar == 24) {

            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {
          CargarHistorialNotas();
            }else{

                CargarHistorialNotasFicc();

            }
        }
        if (OrdenImportar == 25) {

            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {
            CargarClientesDcto();
            }else{
                OrdenImportar += 1;
            }
        }
        if (OrdenImportar == 26) {

            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {
            if (DescV2.equalsIgnoreCase("S")  ) { //|| vEmpresa.trim().equalsIgnoreCase("SURTIMARCAS")
                CargarBonificacionesProductoDet();
               // OrdenImportar += 1;
            }else{
                OrdenImportar += 1;

            }
            }else{
                OrdenImportar += 1;
            }
        }
        if (OrdenImportar == 27) {

            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {
            if (DescV2.equalsIgnoreCase("S")  ) { //|| vEmpresa.trim().equalsIgnoreCase("SURTIMARCAS")
                CargarBonificacionesProducto();
               // OrdenImportar += 1;
            }else{
                OrdenImportar += 1;
            }
            }else{
                CargarBonificacionesProductoFicc();
                //  OrdenImportar += 1;
            }
        }
        if (OrdenImportar == 28) {

            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {
            if (DescV2.equalsIgnoreCase("S") ) { //|| vEmpresa.trim().equalsIgnoreCase("SURTIMARCAS")
                CargarDescuentos();
              //  OrdenImportar += 1;
            }else{
                OrdenImportar += 1;
            }
            }else{
                CargarDescuentosFicc();
            }

         //   OrdenImportar += 1;
        }
        if (OrdenImportar == 29) {

            Log.e("entro",String.valueOf(OrdenImportar));


            if(mantisficc.equalsIgnoreCase("N")) {
            if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") || vEmpresa.trim().equalsIgnoreCase("SURTIMARCAS")) {
                CargarControlVentas();
            }else{
                OrdenImportar += 1;
            }
            }else{
                OrdenImportar += 1;
            }
        }
        if (OrdenImportar == 30) {

            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {
            if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") ) {  //|| vEmpresa.trim().equalsIgnoreCase("SURTIMARCAS")
                CargarListasProGrupo();
            }else{
                OrdenImportar += 1;
            }
            }else{
                OrdenImportar += 1;
            }
        }
        if (OrdenImportar == 31) {

            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {
            if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") || vEmpresa.trim().equalsIgnoreCase("SURTIMARCAS")) {
                CargarVersiones();
            }else{
                OrdenImportar += 1;
            }
            }else{
                OrdenImportar += 1;
            }
        }


        if (OrdenImportar == 32) {

            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {
              CargarListasPrecios();

            }else{
               CargarListasPreciosFicc();

            }
        }
        if (OrdenImportar == 33) {

            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {
            CargarPreciosEsp();
            }else{
                OrdenImportar += 1;
            }
        }

        if (OrdenImportar == 34) {

            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {
            CargarBodegas();
            }else{
             CargarBodegasFicc();

            }
        }
        if (OrdenImportar == 35){

            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("N")) {

         CargarExistencia();
            }else{
                CargarExistenciaFicc();

            }
        }
        if (OrdenImportar == 36) {

            Log.e("entro",String.valueOf(OrdenImportar));

            Log.e("Entro 36",String.valueOf(OrdenImportar));
            if(mantisficc.equalsIgnoreCase("N")) {
               if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") || vEmpresa.trim().equalsIgnoreCase("IBANEZPRU")) {
                   Log.e("cargo1",String.valueOf(OrdenImportar));
                    CargarCanalesOferta();
            }else{
                   Log.e("cargo2",String.valueOf(OrdenImportar));
                    OrdenImportar += 1;
                }
                }else{
                Log.e("cargo3",String.valueOf(OrdenImportar));
                OrdenImportar += 1;
                Log.e("fin",String.valueOf(OrdenImportar));
            }
        }

        if(OrdenImportar==37){

            Log.e("entro",String.valueOf(OrdenImportar));

            if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") || vEmpresa.trim().equalsIgnoreCase("IBANEZPRU") || vEmpresa.trim().equalsIgnoreCase("SURTIMARbCAS")) {
                CargarBancos();
            }else {
                OrdenImportar += 1;
            }
        }
        if(OrdenImportar==38){

            Log.e("entro",String.valueOf(OrdenImportar));

            if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") || vEmpresa.trim().equalsIgnoreCase("IBANEZPRU") || vEmpresa.trim().equalsIgnoreCase("SURTIbMARCAS")) {
                CargarDescuentosFac();
            }else {
                OrdenImportar += 1;
            }
        }
        if(OrdenImportar==39){

            Log.e("entro",String.valueOf(OrdenImportar));

            if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") || vEmpresa.trim().equalsIgnoreCase("IBANEZPRU") || vEmpresa.trim().equalsIgnoreCase("SURTIMARbCAS")) {
                CargarJustificacion();
            }else {
                OrdenImportar += 1;
            }
        }
        if(OrdenImportar==40){
            Log.e("entro",String.valueOf(OrdenImportar));

            if ( vEmpresa.trim().equalsIgnoreCase("IBANEZPRU")  ) {
                CargarNumeroProvisional();
            }else {
                OrdenImportar += 1;
            }
        }
        if(OrdenImportar==41){
            Log.e("entro",String.valueOf(OrdenImportar));

            if (vEmpresa.trim().equalsIgnoreCase("TODORAPIDAs")) {
                CargarDescGrupo();
            }else {
                OrdenImportar += 1;
            }
        }

        if(OrdenImportar==42){
            Log.e("entro",String.valueOf(OrdenImportar));

            if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") || vEmpresa.trim().equalsIgnoreCase("IBANEZPRU") || vEmpresa.trim().equalsIgnoreCase("SURTIMARbCAS") ) {
                CargarBancosCuenta();
            }else {
                OrdenImportar += 1;
            }
        }

        if(OrdenImportar==43){
            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("S")) {
                CargarCategoriaFicc();
            }else {
                OrdenImportar += 1;
            }
        }
        if(OrdenImportar==43){
            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("S")) {
                CargarObsMovilFicc();
            }else {
                OrdenImportar += 1;
            }
        }

        if(OrdenImportar==44){
            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("S")) {
                CargarPerfildeClientesFicc();
            }else {
                OrdenImportar += 1;
            }
        }


        if(OrdenImportar==45){
            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("S")) {
                CargarTipodeClientesFicc();
               // OrdenImportar += 1;
            }else {
                OrdenImportar += 1;
            }
        }

        if(OrdenImportar==46){
            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("S")) {
             CargarZonaFicc();
            }else {
                OrdenImportar += 1;
            }
        }

        if(OrdenImportar==47){
            Log.e("entro",String.valueOf(OrdenImportar));

            if(mantisficc.equalsIgnoreCase("S")) {
                CargarCategoriaClienteFicc();
            }else {
                OrdenImportar += 1;
            }
        }




        if(OrdenImportar==48){
            Log.e("entro",String.valueOf(OrdenImportar));

            if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") || vEmpresa.trim().equalsIgnoreCase("IBANEZPRU") || vEmpresa.trim().equalsIgnoreCase("SURTIMARbCAS") ) {
                CargarProveedores();
            }else {
                OrdenImportar += 1;
            }
        }
        if(OrdenImportar==49){
            Log.e("entro",String.valueOf(OrdenImportar));

            if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") || vEmpresa.trim().equalsIgnoreCase("IBANEZPRU") || vEmpresa.trim().equalsIgnoreCase("SURTIMARbCAS") ) {
                CargarctaProveedores();
            }else {
                OrdenImportar += 1;
            }
        }




        if (OrdenImportar == 50) {
            Log.e("entro",String.valueOf(OrdenImportar));


            if (mantisficc.equalsIgnoreCase("N")) {
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);

                try {

                    String ConsultaDcto = " update ARTICULOS set TieneDescEsp='S' WHERE ARTSEC IN( SELECT artsec FROM (";
                    ConsultaDcto += "select MovParBonEncArtSec artsec from MovParBonProdBon ";
                    ConsultaDcto += " Union all ";
                    ConsultaDcto += "select MovParMixDetArtSec artsec from MovParMixArticulos ";
                    ConsultaDcto += " Union all ";
                    ConsultaDcto += "select artsec from Articulos where desc2<>0 ";
                    ConsultaDcto += " Union all ";
                    ConsultaDcto += "select MovParEscArtSec artsec from MovParEsc ";
                    ConsultaDcto += ") KK group by artsec)";

                    vBaseDeDatos.getReadableDatabase().execSQL(ConsultaDcto);

                } catch (Exception e) {
                    Log.e("31",e.toString());

                    int hh = 0;
                }

                try {

                    String ConsultaCartera = " update Clientes set CarteraVend= ";
                    ConsultaCartera += "ifnull((select FacSaldo from Cartera where movnitsec=Clientes.nitsec  and  movclisec=Clientes.clisec ),0)";

                    vBaseDeDatos.getReadableDatabase().execSQL(ConsultaCartera);

                } catch (Exception e) {
                    Log.e("312",e.toString());

                    int hh = 0;
                }
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {


                    SQLiteDatabase BdSql = vBaseDeDatos.getReadableDatabase();
                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script = "select year(GETDATE()) ano,month(GETDATE()) mes,day(GETDATE()) dia,(select top 1 parmovhora from parametrosmoviles) horcie from empresa";
                     rsImport = comm.executeQuery(Script);

                    String Dia = "";
                    String Mes = "";
                    String Ano = "";
                    Integer HorCie = 0;
                    while (rsImport.next()) {
                        Ano = rsImport.getString("ano").trim();
                        Mes = rsImport.getString("mes").trim();
                        Dia = rsImport.getString("dia").trim();
                        HorCie = rsImport.getInt("horcie");
                    }


                    final String finalDia = Dia;
                    final String finalMes = Mes;
                    final String finalAno = Ano;
                    final Integer finalHorCie = HorCie;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            final BaseDatos BaseDeDatos;
                            BaseDeDatos = new BaseDatos(thisContext, "MantisMovil", null, 5);
                            final Time time = new Time();
                            time.setToNow();

                            if (ErroresGen == 0) {
                                AlertDialog.Builder Alerta = new AlertDialog.Builder(thisContext, R.style.MyDialogTheme);
                                Alerta.setMessage("Sincronizacion Exitosa 100%");
                                Alerta.setTitle("Alerta");
                                Alerta.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogo1, int id) {
                                        String consulta = "insert into Sincronizaciones(SinAno,SinMes,SinDay,SinHor,SinMin,SinSeg,SerDay,SerMes,SerAno,HorCie)" +
                                                "values(" + time.year + "," + (time.month + 1) + "," + time.monthDay + "," + time.hour + "," + time.minute + "," + time.second + "," + finalDia + "," + finalMes + "," + finalAno + "," + finalHorCie + ")";
                                        try {
                                            BaseDeDatos.getReadableDatabase().execSQL("delete from Sincronizaciones where SinAno=" + time.year + " and SinMes=" + (time.month + 1) + " and SinDay=" + time.monthDay);
                                            BaseDeDatos.getReadableDatabase().execSQL(consulta);

                                            finish();
                                        } catch (Exception e) {
                                            int kk = 0;
                                        }
                                        //  finish();
                                    }
                                });
                                Alerta.setCancelable(false);
                                Alerta.create().show();
                            } else {
                                AlertDialog.Builder Alerta = new AlertDialog.Builder(thisContext, R.style.MyDialogThemeRojo); //
                                Alerta.setMessage("Sincronizacion Fallida Errores =" + ErroresGen+ "\n Errores: "+ErrorTxt); //
                                Alerta.setTitle("Alerta");
                                //   Alerta.setPositiveButton("Aceptar", null);
                                Alerta.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogo1, int id) {
                                        BaseDeDatos.getReadableDatabase().execSQL("delete from Sincronizaciones where SinAno=" + time.year + " and SinMes=" + (time.month + 1) + " and SinDay=" + time.monthDay);
                                        finish();
                                    }
                                });
                                Alerta.setCancelable(false);
                                Alerta.create().show();
                            }
                            // vPrBar_Clientes.setProgress(Vueltas);
                        }
                    });

                } catch (Exception e) {
                    int ll = 0;
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }




            }else{
                //Fic------------------------------------------------------------------------------

                Log.e("FICC",String.valueOf(OrdenImportar));
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);


                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {


                    SQLiteDatabase BdSql = vBaseDeDatos.getReadableDatabase();
                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    conbd.Variables();
                    String mysql = conbd.Mysql;
                    String Script = "";

                    String ConsultaCartera = " update Clientes set CarteraVend= ";
                    ConsultaCartera += "ifnull((select FacSaldo from Cartera where movnitsec=Clientes.nitsec  and  movclisec=Clientes.clisec ),0)";

                    vBaseDeDatos.getReadableDatabase().execSQL(ConsultaCartera);

                    if(mysql.equalsIgnoreCase("S")){
                         Script = "select year(GETDATE()) ano,month(GETDATE()) mes,day(GETDATE()) dia,(select  parmovhora from ParametrosMoviles LIMIT 1) horcie from Empresa";

                    }else{
                         Script = "select year(GETDATE()) ano,month(GETDATE()) mes,day(GETDATE()) dia,(select top 1 parmovhora from parametrosmoviles) horcie from empresa";

                    }

                    classbd classbd = new classbd();
                     rsImport = comm.executeQuery(classbd.FormatearMysql(Script));

                    String Dia = "";
                    String Mes = "";
                    String Ano = "";
                    Integer HorCie = 0;
                    while (rsImport.next()) {
                        Ano = rsImport.getString("ano").trim();
                        Mes = rsImport.getString("mes").trim();
                        Dia = rsImport.getString("dia").trim();
                        HorCie = rsImport.getInt("horcie");
                    }
                    try {

                        String ConsultaDcto = " update ARTICULOS set TieneDescEsp='S' WHERE ARTSEC IN( SELECT artsec FROM (";
                        ConsultaDcto += "select MovParBonEncArtSec artsec from MovParBonProdBon ";
                        ConsultaDcto += " Union all ";
                        ConsultaDcto += "select MovParMixDetArtSec artsec from MovParMixArticulos ";
                        ConsultaDcto += " Union all ";
                        ConsultaDcto += "select artsec from Articulos where desc2<>0 ";
                        ConsultaDcto += " Union all ";
                        ConsultaDcto += "select MovParEscArtSec artsec from MovParEsc ";
                        ConsultaDcto += " Union all ";
                        ConsultaDcto += "select MovParEscArtSec artsec from MovParEsc ";
                        ConsultaDcto += ") KK group by artsec)";

                        vBaseDeDatos.getReadableDatabase().execSQL(ConsultaDcto);

                    } catch (Exception e) {
                        Log.e("sssssss",e.toString());

                        int hh = 0;
                    }

                    final String finalDia = Dia;
                    final String finalMes = Mes;
                    final String finalAno = Ano;
                    final Integer finalHorCie = HorCie;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            final BaseDatos BaseDeDatos;
                            BaseDeDatos = new BaseDatos(thisContext, "MantisMovil", null, 5);
                            final Time time = new Time();
                            time.setToNow();

                            if (ErroresGen == 0) {
                                AlertDialog.Builder Alerta = new AlertDialog.Builder(thisContext, R.style.MyDialogTheme);
                                Alerta.setMessage("Sincronizacion Exitosa 100%");
                                Alerta.setTitle("Alerta");
                                Alerta.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogo1, int id) {
                                        String consulta = "insert into Sincronizaciones(SinAno,SinMes,SinDay,SinHor,SinMin,SinSeg,SerDay,SerMes,SerAno,HorCie)" +
                                                "values(" + time.year + "," + (time.month + 1) + "," + time.monthDay + "," + time.hour + "," + time.minute + "," + time.second + "," + finalDia + "," + finalMes + "," + finalAno + "," + finalHorCie + ")";
                                        try {
                                            BaseDeDatos.getReadableDatabase().execSQL("delete from Sincronizaciones where SinAno=" + time.year + " and SinMes=" + (time.month + 1) + " and SinDay=" + time.monthDay);
                                            BaseDeDatos.getReadableDatabase().execSQL(consulta);

                                            finish();
                                        } catch (Exception e) {
                                            Log.e("ex",e.toString());

                                            int kk = 0;
                                        }
                                        //  finish();
                                    }
                                });
                                Alerta.setCancelable(false);
                                Alerta.create().show();
                            } else {
                                AlertDialog.Builder Alerta = new AlertDialog.Builder(thisContext, R.style.MyDialogThemeRojo); //
                                Alerta.setMessage("Sincronizacion Fallida Errores =" + ErroresGen+ "\n Errores: "+ErrorTxt); //
                                Alerta.setTitle("Alerta");
                                //   Alerta.setPositiveButton("Aceptar", null);
                                Alerta.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogo1, int id) {
                                        BaseDeDatos.getReadableDatabase().execSQL("delete from Sincronizaciones where SinAno=" + time.year + " and SinMes=" + (time.month + 1) + " and SinDay=" + time.monthDay);
                                        finish();
                                    }
                                });
                                Alerta.setCancelable(false);
                                Alerta.create().show();
                            }
                            // vPrBar_Clientes.setProgress(Vueltas);
                        }
                    });

                } catch (Exception e) {
                    Log.e("ex",e.toString());
                    int ll = 0;
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

            }
        }
        OrdenImportar += 1;
        }

    private void CargarCanalesOferta() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;

                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="";
                    Script="select FACNITSEC,KarBonifProSec,KarBonifPRoLinSec,KarBonifTipo,consumido,CANTPROM,codcanal,puntos, "+
                            " CASE WHEN CANTPROM<>0 THEN isnull(consumido/CANTPROM,0) ELSE 0 END VECES,isnull(BonProDetCanMaxOfe,0) maxcan"+
                            " from("+
                            " select FACNITSEC,KarBonifProSec,KarBonifPRoLinSec,KarBonifTipo,consumido,"+
                            " isnull((select count(*) puntos from clientes where nitsec=facnitsec),0) puntos,"+
                            " isnull((select top 1 cancod from clientes where nitsec=facnitsec),0) codcanal,"+
                            " CASE WHEN KarBonifTipo='DETALLE' "+
                            " THEN ISNULL(BonProGraUni,0)+(ISNULL(BonProGraCaj,0)*ISNULL((SELECT ARTEMB FROM ARTICULOS WHERE ARTSEC=BonProGraArtSec),0)) "+
                            " WHEN KarBonifTipo='ESCALA' "+
                            " THEN ISNULL(BonProEscBonUni,0)+(ISNULL(BonProEscBonCaj,0)*ISNULL((SELECT ARTEMB FROM ARTICULOS WHERE ARTSEC=BonProEscBonArtSec),0)) "+
                            " WHEN  KarBonifTipo='ESCGRU' "+
                            " THEN ISNULL(BonProEscGruBonUni,0)+(ISNULL(BonProEscGruBonCaj,0)*ISNULL((SELECT ARTEMB FROM ARTICULOS WHERE ARTSEC=BonProEscGruBonArtSec),0)) "+
                            " END CANTPROM from "+
                            " ("+
                            " SELECT KarBonifProSec,KarBonifPRoLinSec,KarBonifTipo,FACNITSEC,SUM(KARUNI+(KarCaj*KarArtEmb)) consumido FROM Kardex K2  "+
                            " Left join factura f2 on K2.facsec=f2.facsec "+
                            " where KarBonifProSec  is not null AND KarBonifProSec<>0 and facest='A'"+
                            " GROUP BY KarBonifProSec,KarBonifPRoLinSec,KarBonifTipo,FACNITSEC"+
                            " ) k "+
                            "left join BonificacionesProducto b on BonProSec=k.KarBonifProSec "+
                            " left join BonificacionesProductoDet bd on  bd.BonProSec=k.KarBonifProSec and BonProDetLin=KarBonifPRoLinSec and KarBonifTipo='DETALLE'"+
                            " left join BonificacionesProductoEscala be on be.BonProSec=k.KarBonifProSec and BonProEscLin=KarBonifPRoLinSec and KarBonifTipo='ESCALA'"+
                            " left join BonificacionesProductoEscalaGr beg on beg.BonProSec=k.KarBonifProSec and BonProEscGruLin=KarBonifPRoLinSec and KarBonifTipo='ESCGRU'"+
                            ") cxbon "+
                            " left join BonificacionesProductoCanal c on BonProSec=cxbon.KarBonifProSec  and BonProDetCanCod=codcanal"+
                            " where FACNITSEC in (select nitsec from clientesvendedores cv left join vendedores v on v.vencod = cv.vencod where cv.vencod = '"+vUsuario+"' or v.venid = '"+vUsuario+"')";
                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from CanalOferta");

                    while (rsImport.next()){


                        String InsertScript = "insert into CanalOferta (FacNitSec,BonProSec,BonProLinsec,Tipo,canal,veces,puntos,maxcan) values ('"
                                + rsImport.getString("FACNITSEC").trim()+ "',"
                                + rsImport.getInt("KarBonifProSec")+ ","
                                + rsImport.getInt("KarBonifPRoLinSec") + ",'"
                                + rsImport.getString("KarBonifTipo").trim() + "',"
                                + rsImport.getInt("codcanal") + ","
                                + rsImport.getInt("VECES") + ","
                                + rsImport.getInt("puntos") + ","
                                + rsImport.getInt("maxcan") + ")" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Log.e("sqlEX",ex.toString());
                            Errores+=1;

                        }
//*/
                    }
                } catch (Exception e) {
                    Log.e("sqlE",e.toString());


                    Errores=1;

                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }


                // }while (traerArt.moveToNext());
                //}



                ErroresGen+=Errores;
                Log.e("error2",String.valueOf(ErroresGen));

                CargarDatos();
            }
        }).start();
    }

    private void CargarExistencia() {
        int hh=0;
        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntExistencia);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkExistencia);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrExistencia);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_Existencias);
        vPrBar_Import.setProgress(0);


        new Thread(new Runnable() {
            @Override
            public void run() {



                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();
            Cursor traerArt = vBaseDeDatos.getReadableDatabase().rawQuery("select BodCod,BodCheckPred from Bodegas", null);
                String bodega = "ArtBodCod in (";
                int bodsurti = 0;

              if(traerArt.getCount() > 0){
                  traerArt.moveToFirst();
                  do{
                      bodega += traerArt.getString(0).trim()+",";
                      if(traerArt.getString(1).trim().equalsIgnoreCase("N")){
                          bodsurti = traerArt.getInt(0);
                      }
                  }while (traerArt.moveToNext());



                  bodega +=")";
                  bodega = bodega.replace(",)", ")");
              }else{
                  bodega = "Artexiact > 0";
              }

            Log.e("bodegabodegabodega",bodega);
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;

                  try {

                       ConBd conbd = new ConBd();
                        conn = conbd.CargarConexion(getApplicationContext());
                        comm = conn.createStatement();
                       String Script="";
                      if(vEmpresa.equalsIgnoreCase("SURTIMARCAS")){
                          Script="select * from(select  ROW_NUMBER() OVER(ORDER BY e.Artsec ASC) AS Row, e.Artsec,ArtBodCod,(artexiact - (select isnull(sum(artuniminapa),0) from articulosunidad ae left join carunidades c on ae.Artalinegcod=c.alinegcod where  CarUniSucCod="+vSucCod+" and ae.artsec= e.Artsec and AliNegTat='S' and (select AliNegTat from carunidades n where n.alinegcod="+vAliNegCod+")<>'S' )) Artexiact  from Articulosexi e where "+bodega+" ) Consulta order by Row desc";

                      }else{
                          Script="select * from(select  ROW_NUMBER() OVER(ORDER BY e.Artsec ASC) AS Row, e.Artsec,ArtBodCod,(Artexiact - (select isnull(sum(artuniminapa),0) from articulosunidad ae left join carunidades c on ae.Artalinegcod=c.alinegcod where  CarUniSucCod="+vSucCod+" and ae.artsec= e.Artsec and AliNegTat='S' and (select AliNegTat from carunidades n where n.alinegcod="+vAliNegCod+")<>'S' )) Artexiact  from Articulosexi e where "+bodega+" ) Consulta order by Row desc";

                      }

                      rsImport = comm.executeQuery(Script);
                      Log.e("Script ex: ",Script);
                       BdSql.execSQL("Delete from ArticulosExi");

                       while (rsImport.next()){

                          if(TotalFilas==0) {
                               vPrBar_Import.setMax(rsImport.getInt("Row"));
                               TotalFilas=rsImport.getInt("Row");
                               String Filas=rsImport.getString("Row").trim();
                           }
                           Vueltas+=1;
                           vPrBar_Import.setMax(TotalFilas);
                           vPrBar_Import.setProgress(Vueltas);

                           Integer Exist = 0;

                           try{
                               Exist =  rsImport.getInt("Artexiact")  ;
                           }catch (Exception e){
                               Exist =  0 ;

                               Log.e("ERROR : ","Arteci"+rsImport.getString("Artexiact") +e.toString());
                           }



                           String InsertScript ="";
                         /*  if(vEmpresa.equalsIgnoreCase("SURTIMARCAS")) {
                                InsertScript = "insert into ArticulosExi (ArtSec,ArtBodCod,ArtExiAct) values ('"
                                       + rsImport.getString("Artsec").trim() + "',"
                                       + rsImport.getString("ArtBodCod").trim() + ","
                                       + rsImport.getInt("Artexiact") + ")";
                           }else{
*/
                                InsertScript = "insert into ArticulosExi (ArtSec,ArtBodCod,ArtExiAct) values ('"
                                       + rsImport.getString("Artsec").trim() + "',"
                                       + rsImport.getString("ArtBodCod").trim() + ","
                                       + Exist+ ")";

                           /*}*/

                         //  Log.e("Script ex: ",InsertScript);

                           try {
                               if (BdSql.isDbLockedByCurrentThread()){
                                   BdSql.endTransaction();
                                   Log.e("Swnnnnnnn ",String.valueOf(BdSql.isDbLockedByCurrentThread()));
                               }
                               BdSql.execSQL(InsertScript);
                               Insertados+=1;

                           }catch (Exception ex){
                               Log.e("EerorExi",ex.toString());

                               Errores+=1;

                           }
//*/
                           try {

                               // vPrBar_Clientes.setMax(TotalFilas);
                               final int finalTotalFilas = TotalFilas;
                               final int finalInsertados = Insertados;
                               final int finalErrores = Errores;
                               handler.post(new Runnable() {
                                   @Override
                                   public void run() {
                                       vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                       vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                       vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                       // vPrBar_Clientes.setProgress(Vueltas);
                                   }
                               });
                           }catch (Exception e){
                               Log.e("Errocliente",e.toString());
                               int hh=0;
                           }

                       }


                   /*   if(vEmpresa.equalsIgnoreCase("SURTIMARCAS")) {
                          final BaseDatos BaseDeDatos;
                          BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
                          Cursor cursor = BaseDeDatos.getReadableDatabase().rawQuery("select ArtSec,ArtExiAct from ArticulosExi where ArtBodCod = "+bodsurti+" ", null); //order by nombr
                            if(cursor.getCount()>0){
                                cursor.moveToFirst();
                                do{
                                    BaseDeDatos.getReadableDatabase().execSQL("update ArticulosExi set ArtExiAct = ArtExiAct + "+cursor.getInt(1)+" where  ArtSec = '"+cursor.getString(0)+"' and ArtBodCod <> "+bodsurti+"  ");
                                }while (cursor.moveToNext());
                            }
                      }*/




                  } catch (Exception e) {
                      Log.e("EerorExi",e.toString());

                       Errores=1;
                      Errores=1;
                      handler.post(new Runnable() {
                          @Override
                          public void run() {
                              vtxt_CntImport.setText("N/N");
                              vtxt_OkImport.setText("N/N");
                              vtxt_ErrImport.setText("1");
                          }
                      });
                   }
                ErroresGen+=Errores;
                Log.e("existencia",String.valueOf(ErroresGen));

                CargarDatos();
                 /* finally { // Cerramos las conexiones, en orden inverso a su apertura
                      try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                      try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                      try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                  }*/


                // }while (traerArt.moveToNext());
           //}




            }
        }).start();
    }

    private void CargarDescuentosFac() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);

                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();
                Cursor traerArt = vBaseDeDatos.getReadableDatabase().rawQuery("select MovFacSec from cartera", null);
                String bodega = "Facnro in (";
                String bodsurti = "";

                if(traerArt.getCount() > 0){
                    traerArt.moveToFirst();
                    do{
                        bodega += "'"+traerArt.getString(0).trim()+"',";
                    }while (traerArt.moveToNext());
                    bodega +=")";
                    bodega = bodega.replace(",)", ")");

                    Connection conn = null;
                    Statement comm = null;
                    ResultSet rsImport  = null;


                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="";
                    Script=" select k.FacSec,FacNro, " +
                            " isnull(Sum(((KarPrePub*(karuni+(karcaj*karartemb)))*(kardesinf/100))),0) NoOto, " +
                            " isnull(Sum(((KarPrePub*(karuni+(karcaj*karartemb)))*(kardesinf2/100))),0) Conf, " +
                            " isnull(Sum(((KarPrePub*(karuni+(karcaj*karartemb)))*(kardesinf3/100))),0) ConfProv, " +
                            " isnull(sum(KarDesValPag),0) financiero " +
                            " from kardex k left join factura f on k.facsec = f.facsec " +
                            " where " +bodega+
                            " group by k.FacSec, facnro  ";
                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from DescuentosFac");

                    while (rsImport.next()){
                        String InsertScript ="";

                            InsertScript = "insert into DescuentosFac (FacNro,NoOto,Conf,ConfProv,financiero) values ('"
                                    + rsImport.getString("FacNro").trim() + "',"
                                    + rsImport.getInt("NoOto") + ","
                                    + rsImport.getInt("Conf") + ","
                                    + rsImport.getInt("ConfProv") + ","
                                    + rsImport.getInt("financiero") + ")";

                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Log.e("EerordesExi",ex.toString());

                            Errores+=1;

                        }
//*/
                    }





                } catch (Exception e) {
                    Log.e("EerordesExiw",e.toString());

                    Errores=1;

                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }


                }
                // }while (traerArt.moveToNext());
                //}



                ErroresGen+=Errores;
                Log.e("error3",String.valueOf(ErroresGen));

                CargarDatos();
            }
        }).start();
    }

    private void CargarJustificacion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                    try {
                        ConBd conbd = new ConBd();
                         conn = conbd.CargarConexion(getApplicationContext());
                         comm = conn.createStatement();
                        String Script="select JustSalSec , JustSalDes from JustificacionSaldo";
                         rsImport = comm.executeQuery(Script);
                        BdSql.execSQL("Delete from JustificacionSaldo");

                        while (rsImport.next()){
                            String InsertScript ="";

                            InsertScript = "insert into JustificacionSaldo (JustSalSec,JustSalDes) values ("
                                    + rsImport.getInt("JustSalSec") + ",'"
                                    + rsImport.getString("JustSalDes").trim() + "')";

                            try {
                                if (BdSql.isDbLockedByCurrentThread()){
                                    BdSql.endTransaction();
                                }
                                BdSql.execSQL(InsertScript);
                                Insertados+=1;

                            }catch (Exception ex){
                                Log.e("EerordesExi",ex.toString());
                                Errores+=1;

                            }
//*/
                        }





                    } catch (Exception e) {
                        Log.e("EerordesExiw",e.toString());

                        Errores=1;

                    }finally { // Cerramos las conexiones, en orden inverso a su apertura
                        try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                        try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                        try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                    }



                ErroresGen+=Errores;
                Log.e("error3",String.valueOf(ErroresGen));

                CargarDatos();
            }
        }).start();
    }



    private void CargarNumeroProvisional() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {
                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();

                    /*String Script=" select RecPagCheque from RecibosCajaRecForPag rp " +
                            " left join reciboscaja1 r on rp.RecSec = r.RecSec " +
                            " where RecFec >= DATEFROMPARTS ( YEAR(GETDATE()), month(GETDATE())-6 , day(GETDATE() ) ) union " +
                            " select ReForPagFotosDesc as RecPagCheque from RecibosCajaDetalleRecForPagReF rp left join reciboscaja1 r on rp.RecSec = r.RecSec " +
                            " where RecFec >= DATEFROMPARTS ( YEAR(GETDATE()), month(GETDATE())-6 , day(GETDATE() ) )        " ;*/

                    String Script = " select ReForPagFotosDesc, RecPagCheque,Year(RecFec) as RecFecyear,Month(RecFec) as RecFecmonth,Day(RecFec) as RecFecdia,RecPagCiucod,RecPagVal from RecibosCajaDetalleRecForPagReF rp " +
                            " left join RecibosCajaRecForPag r on rp.RecSec = r.RecSec " +
                            " left join reciboscaja1 r1 on rp.RecSec = r1.RecSec " +
                            "  where RecFec >= getDate()-180  ";






                   rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from NumeroProvisional");

                    while (rsImport.next()){
                        String InsertScript ="";

                        InsertScript = "insert into NumeroProvisional (ReForPagFotosDesc,RecPagCheque,RecFecyear,RecFecmonth,RecFecdia,RecPagCiucod,RecPagVal) values " +
                                " ( '"+ rsImport.getString("ReForPagFotosDesc").trim() +"', " +
                                "  '"+ rsImport.getString("RecPagCheque").trim() +"' ," +
                                "  "+ rsImport.getInt("RecFecyear") +" ," +
                                "  "+ rsImport.getInt("RecFecmonth") +", " +
                                "  "+ rsImport.getInt("RecFecdia") +", " +
                                " '"+ rsImport.getString("RecPagCiucod").trim()+"', " +
                                "  "+ rsImport.getInt("RecPagVal") +" " +
                                ")";

                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Log.e("EerordesExi",ex.toString());
                            Errores+=1;

                        }
//*/
                    }





                } catch (Exception e) {
                    Log.e("EerordesExiw",e.toString());

                    Errores=1;

                }
                finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }





                ErroresGen+=Errores;
                Log.e("error3",String.valueOf(ErroresGen));

                CargarDatos();
            }
        }).start();
    }

    private void CargarBodegas() {

        int hh=0;
        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntBodegas);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_oKBodegas);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrBodegas);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_Bodegas);
        vPrBar_Import.setProgress(0);


        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="";

                 /*   Script= " select Bodsucccnom,BodCodapp,BodCheckPred from vendedores  left join CarUnidadesBodegaMovil  on venAlinegcod = AliNegCod " +
                            " left join BodegaSucursalCC  on Bodcodapp = Bodsucccsec" +
                            "   where VenId = '"+vUsuario+"' ";*/

                  /*  "  select isnull(Bodsucccnom,(SELECT  top 1 Bodsucccnom FROM BodegaSucursalCC WHERE BodPri = 'S')) Bodsucccnom ," +
                            "isnull(BodCodapp,(SELECT  top 1 Bodsucccsec FROM BodegaSucursalCC WHERE BodPri = 'S')) BodCodapp  ,"+
                            "isnull(BodCheckPred ,'S') BodCheckPred from vendedores  left join CarUnidadesBodegaMovil  on venAlinegcod = AliNegCod " +
                            "left join BodegaSucursalCC  on Bodcodapp = Bodsucccsec where VenCod = '"+vUsuario+"'";*/

     if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") || vEmpresa.trim().equalsIgnoreCase("IBANEZPRU")   ) {
            Script = "  select  Bodsucccnom , isnull(BodCodapp,b.ALINEGBODSUCCCSEC)  BodCodapp, isnull(BodCheckPred ,'S') BodCheckPred" +
                    "  from vendedores  left join CarUnidadesBodegaMovil c  on venAlinegcod = c.AliNegCod" +
                    "  left join CarUnidadesAliNegBodSuc b  on venAlinegcod = b.AliNegCod" +
                    "  left join BodegaSucursalCC n  on isnull(Bodcodapp, b.ALINEGBODSUCCCSEC) = n.Bodsucccsec" +
                    "  where vencod = '" + vUsuario + "' or  venid = '" + vUsuario + "'  and (b.AliNegBon = 'N' or b.AliNegBon is null)";

       }else {
         if(vEmpresa.trim().equalsIgnoreCase("SURTIMARCAS") ) {
             Script = "  select  Bodsucccnom , isnull(BodCodapp,b.ALINEGBODSUCCCSEC)  BodCodapp, isnull(BodCheckPred ,Case AlinegBon when 'N' then 'S' when 'S' then'N' else 'S' end) BodCheckPred" +
                     "  from vendedores  left join CarUnidadesBodegaMovil c  on venAlinegcod = c.AliNegCod" +
                     "  left join CarUnidadesAliNegBodSuc b  on venAlinegcod = b.AliNegCod" +
                     "  left join BodegaSucursalCC n  on isnull(Bodcodapp, b.ALINEGBODSUCCCSEC) = n.Bodsucccsec" +
                     "  where vencod = '" + vUsuario + "' or  venid = '" + vUsuario + "'";

         }else
         {
         if(vEmpresa.trim().equalsIgnoreCase("TODORAPIDAS") ) {
             Script = "   select  Bodsucccnom , isnull(BodCodapp,b.ALINEGBODSUCCCSEC)  BodCodapp, isnull(BodCheckPred ,'S') BodCheckPred" +
             " from VendedoresUnidadNegocio v left join CarUnidadesBodegaMovil c  on v.Alinegcod = c.AliNegCod" +
             " left join CarUnidadesAliNegBodSuc b  on v.Alinegcod = b.AliNegCod" +
             " left join BodegaSucursalCC n  on isnull(Bodcodapp, b.ALINEGBODSUCCCSEC) = n.Bodsucccsec " +
             " where vencod =  '" + vUsuario + "'  and (b.AliNegBon = 'N' or b.AliNegBon is null)" ;

         }else{

             if(vEmpresa.trim().equalsIgnoreCase("DINGLESA") ){

                 Script = "select  Bodsucccnom , isnull(b.ALINEGBODSUCCCSEC,Bodsucccsec)  BodCodapp, isnull(AliNegBon ,'S') BodCheckPred " +
                         "from vendedores left join CarUnidadesAliNegBodSuc b  on venAlinegcod = b.AliNegCod " +
                         "left join BodegaSucursalCC n  on b.ALINEGBODSUCCCSEC= n.Bodsucccsec" +
                         "  where vencod = '" + vUsuario + "' or  venid = '" + vUsuario + "'  and (b.AliNegBon = 'N' or b.AliNegBon is null)";

             }else{

                 /*if(vEmpresa.trim().equalsIgnoreCase("GELVEZARA") ){
                     Script = "select Bodsucccnom,isnull(BODSUCCCSEC,c.ALINEGBODSUCCCSEC)  BodCodapp, \n" +
                             "'S' BodCheckPred  from CarUnidadesAliNegBodSuc c \n" +
                             "left join BodegaSucursalCC n on c.AliNegBodSucCCsec = n.BODSUCCCSEC\n" +
                             "left join vendedores v on venAlinegcod = c.AliNegCod where \n" +
                 }else{*/
                     Script = "select  Bodsucccnom , BodSucccSec as BodCodapp , 'S' as BodCheckPred  " +
                             "from BodegaSucursalCC" ;
             //    }



             }

                    // "+ "' or Venid = '" + vUsuario + "')";
         }
         }

     }



                    Log.e("SqlbodScripte",Script);
                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from Bodegas");

                    while (rsImport.next()){

                        String InsertScript = "insert into Bodegas (BodCod,BodCheckPred,BodNom) values ("
                                + rsImport.getInt("BodCodapp")+ ",'"
                                + rsImport.getString("BodCheckPred").trim() + "','"
                                + rsImport.getString("Bodsucccnom").trim() + "')" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Log.e("Sqlbodex",ex.toString());
                            Errores+=1;

                        }
//*/
                    }
                } catch (Exception e) {
                    Log.e("Sqlbode",e.toString());
                    Errores=1;

                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }


                ErroresGen+=Errores;
                Log.e("error4",String.valueOf(ErroresGen));

                CargarDatos();
            }
        }).start();
    }
    private void CargarBancos() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;

                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="";

               if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") || vEmpresa.trim().equalsIgnoreCase("IBANEZPRU") ) {
                        Script = " select b.BANFINCOD,BANFINNOM,TCNNOM,TCNSEC,BanFinCheckValPuc from BancosSectorFinancieroTipoCons bt left join BancosSectorFinanciero b on bt.BANFINCOD = b.BANFINCOD ";
               }




                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from Bancos");

                    while (rsImport.next()){

                        String InsertScript = "insert into Bancos (BANFINCOD,BANFINNOM,BanFinCheckValPuc,TCNNOM,TCNSEC) values ("
                                + rsImport.getInt("BANFINCOD")+ ",'"
                                + rsImport.getString("BANFINNOM").trim() + "','"
                                + rsImport.getString("BanFinCheckValPuc").trim() + "','"
                                + rsImport.getString("TCNNOM").trim() + "',"
                                + rsImport.getInt("TCNSEC")+ ")" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Log.e("Sqlbanco",ex.toString());
                            Errores+=1;

                        }
//*/
                    }
                } catch (Exception e) {
                    Log.e("Sqlbancoe",e.toString());
                    Errores=1;

                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

                ErroresGen+=Errores;
                Log.e("error4",String.valueOf(ErroresGen));

                CargarDatos();
            }
        }).start();
    }


    private void CargarBancosCuenta() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="";
                    Script = " select BanFinCod, BanFinPucSec,PucNom,puccod from " +
                            " bancossectorfinancierocuentas b left join puc " +
                            " on BanFinPucSec = PucSec ";

                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from BancoCuenta");

                    while (rsImport.next()){

                        String InsertScript = "insert into BancoCuenta ( BANFINCOD, BanFinPucSec,PucNom,puccod) values ("
                                + rsImport.getInt("BANFINCOD")+ ",'"
                                + rsImport.getString("BanFinPucSec").trim() + "','"
                                + rsImport.getString("PucNom").trim() + "','"
                                + rsImport.getString("puccod")+ "')" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Log.e("Sqlbancocuenta",ex.toString());
                            Errores+=1;
                        }
//*/
                    }
                } catch (Exception e) {
                    Log.e("Sqlbancoecuenta",e.toString());
                    Errores=1;

                }
                finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }
                ErroresGen+=Errores;
                Log.e("error4",String.valueOf(ErroresGen));

                CargarDatos();
            }
        }).start();
    }

    private void CargarPreciosEsp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="";
                    Script="select NitSec, CliArtSec, CliPreven from NegociacionClientesDetalle where CliPreFecFin >= CONVERT(date, GETDATE()) ";

                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from PreciosEspeciales");

                    while (rsImport.next()){


                        String InsertScript = "insert into PreciosEspeciales (peArtSec,precioesp,peNitSec) values ('"
                                + rsImport.getString("CliArtSec").trim() + "',"
                                + rsImport.getString("CliPreven").trim() + ",'"
                                + rsImport.getString("NitSec").trim() + "')" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Log.e("Erorors ",ex.toString());
                            Errores+=1;

                        }
//*/
                    }
                } catch (Exception e) {
                    Log.e("Eroror ",e.toString());

                    Errores=1;

                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

                ErroresGen+=Errores;
                Log.e("error5",String.valueOf(ErroresGen));

                CargarDatos();
            }
        }).start();
    }
    private void CargarDescGrupo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();

                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="";
                    Script="select NitSec, CliSec, clidesinvgrucod, clidesdcto from clientesnewgrupodesc ";

                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from DescGrupo");

                    while (rsImport.next()){


                        String InsertScript = "insert into DescGrupo ( NitSec, CliSec, clidesinvgrucod, clidesdcto) values ('"
                                + rsImport.getString("NitSec").trim() + "',"
                                + rsImport.getInt("CliSec")+ ",'"
                                + rsImport.getString("clidesinvgrucod").trim() + "',"
                                + rsImport.getInt("clidesdcto")+ ")" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Log.e("Erorors ",ex.toString());
                            Errores+=1;

                        }
//*/
                    }
                } catch (Exception e) {
                    Log.e("Eroror ",e.toString());

                    Errores=1;

                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

                ErroresGen+=Errores;
                Log.e("error5",String.valueOf(ErroresGen));

                CargarDatos();
            }
        }).start();
    }




    public void Puente(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {
                    BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                    SQLiteDatabase BdSql = vBaseDeDatos.getReadableDatabase();
                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script = "select year(GETDATE()) ano,month(GETDATE()) mes,day(GETDATE()) dia from empresa";
                     rsImport = comm.executeQuery(Script);

                    String Dia="";
                    String Mes="";
                    String Ano="";
                    while (rsImport.next()) {
                        Ano=rsImport.getString("ano").trim();
                        Mes=rsImport.getString("mes").trim();
                        Dia=rsImport.getString("dia").trim();

                    }


                    final String finalDia = Dia;
                    final String finalMes = Mes;
                    final String finalAno = Ano;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            final BaseDatos BaseDeDatos;
                            BaseDeDatos = new BaseDatos(thisContext, "MantisMovil", null, 5);
                            final Time time = new Time();
                            time.setToNow();

                            if (ErroresGen == 0) {
                                AlertDialog.Builder Alerta = new AlertDialog.Builder(thisContext, R.style.MyDialogTheme);
                                Alerta.setMessage("Sincronizacion Exitosa 100%");
                                Alerta.setTitle("Alerta");
                                Alerta.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogo1, int id) {
                                        String consulta = "insert into Sincronizaciones(SinAno,SinMes,SinDay,SinHor,SinMin,SinSeg,SerDay,SerMes,SerAno,HorCie)" +
                                                "values(" + time.year + "," + (time.month + 1) + "," + time.monthDay + "," + time.hour + "," + time.minute + "," + time.second +","+ finalDia +","+ finalMes +","+ finalAno +")";
                                        try {
                                            BaseDeDatos.getReadableDatabase().execSQL("delete from Sincronizaciones where SinAno=" + time.year + " and SinMes=" + (time.month + 1) + " and SinDay=" + time.monthDay);
                                            BaseDeDatos.getReadableDatabase().execSQL(consulta);

                                            finish();
                                        } catch (Exception e) {
                                            Log.e("Pressses",e.toString());

                                            int kk = 0;
                                        }
                                        //  finish();
                                    }
                                });
                                Alerta.setCancelable(false);
                                Alerta.create().show();
                            } else {
                                AlertDialog.Builder Alerta = new AlertDialog.Builder(thisContext, R.style.MyDialogThemeRojo); //
                                Alerta.setMessage("Sincronizacion Fallida Errores =" + ErroresGen); //
                                Alerta.setTitle("Alerta");
                                //   Alerta.setPositiveButton("Aceptar", null);
                                Alerta.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogo1, int id) {
                                        BaseDeDatos.getReadableDatabase().execSQL("delete from Sincronizaciones where SinAno=" + time.year + " and SinMes=" + (time.month + 1) + " and SinDay=" + time.monthDay);
                                        finish();
                                    }
                                });
                                Alerta.setCancelable(false);
                                Alerta.create().show();
                            }
                            // vPrBar_Clientes.setProgress(Vueltas);
                        }
                    });

                } catch (Exception e) {
                    Log.e("Preses",e.toString());
                    int ll=0;
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

            }}).start();
    }
    public void msjpopup(){

        final BaseDatos BaseDeDatos ;
        BaseDeDatos =new BaseDatos(thisContext,"MantisMovil", null, 5);
        final Time time = new Time();
        time.setToNow();

        if (ErroresGen == 0) {
            AlertDialog.Builder Alerta = new AlertDialog.Builder(thisContext,R.style.MyDialogTheme);
            Alerta.setMessage("Sincronizacion Exitosa 100%");
            Alerta.setTitle("Alerta");
            Alerta.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    String consulta = "insert into Sincronizaciones(SinDay,SinMes,SinAno)" +
                            "values("+time.year+","+(time.month+1)+","+time.monthDay+")";
                    BaseDeDatos.getReadableDatabase().execSQL(consulta);
                  //  finish();
                }
            });
            Alerta.setCancelable(false);
            Alerta.create().show();
        }else{
            AlertDialog.Builder Alerta = new AlertDialog.Builder(thisContext,R.style.MyDialogThemeRojo); //
            Alerta.setMessage("Sincronizacion Fallida Errores ="+String.valueOf(ErroresGen)); //
            Alerta.setTitle("Alerta");
            //   Alerta.setPositiveButton("Aceptar", null);
            Alerta.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    BaseDeDatos.getReadableDatabase().execSQL("delete from Sincronizaciones where SinAno=" + time.year + " and SinMes=" + (time.month + 1) + " and SinDay=" + time.monthDay);
                  //  finish();
                }
            });
            Alerta.setCancelable(false);
            Alerta.create().show();
        }
    }

    public  int[] CargarClientes(){
        int hh=0;
        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntClientes);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkClientes);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrClientes);
        vPrBar_Import= (ProgressBar)findViewById(R.id.prBar_Clientes);
        vPrBar_Import.setProgress(0);
        //vPrBar_Import.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        //int TotalFilasCiudades=0;
        //int InsertadosCiudades=0;
        //int ErroresCiudades=0;
        //int VueltasCiudades=0;
        final int[] Resultado = new int[]{0, 0, 0};

       new Thread(new Runnable() {
        @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();
        //    '" + vUsuario + "'
        String vEmpresa=vGlobalVariables.getEmpresa();
        vEmpresa=vEmpresa.toUpperCase();

            Connection conn = null;
            Statement comm = null;
            ResultSet rsClientes  = null;
        try {

            ConBd conbd = new ConBd();
             conn = conbd.CargarConexion(getApplicationContext());
             comm = conn.createStatement();
            String Script="";
            vEmpresa=vEmpresa.toUpperCase();

            if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") || vEmpresa.trim().equalsIgnoreCase("IBANEZPRU")) {
                Script  = "select * from(select  ROW_NUMBER() OVER(ORDER BY cv.nitsec ASC) AS Row,isnull(CliTamCan,0) CliTamCan,c.nitsec,c.clisec,isnull(NitCom,'')NitCom,isnull(CliNom,'')CliNom,isnull(NitIde,'')NitIde" +
                        ",isnull(CiuCod,'') CiuCod,isnull(CiuNom,'')+' '+CONVERT(VARCHAR(24),isnull(clifecactdat,''),110)  CiuNom,isnull(b.BarCod,0) BarCod,isnull(b.BarNom,'')BarNom,isnull(rtrim(ltrim(b.BarNom)),'')+' '+CliDir CliDir,isnull(VENLISCOD,case when CLILISCLI='CLI' THEN isnull(lisprecod,1) ELSE isnull(CIUlisprecod,isnull(lisprecod,1)) END) lisprecod,1 lisprecodlim,isnull(CliTel,'')CliTel ,isnull(CLICONPAG,0) CLICONPAG,isnull(CliIntLun,'N')CliIntLun,isnull(CliIntMar,'N')CliIntMar" +
                        " ,isnull(CliIntMie,'N')CliIntMie,isnull(CliIntJue,'N')CliIntJue,isnull(CliIntVIE,'N')CliIntVie,isnull(CliInSab,'N') CliIntSab,isnull(CliIntDom,'N') CliIntDom,isnull(PerCliCod,1) PerCliCod," +
                        " isnull(c.CanCod,1) CanCod,isnull(CanNom,'') CanNom,isnull(c.CanSubCod,1) CanSubCod,isnull(CanSubNom,'') CanSubNom,isnull(CliCup,0) CliCup,isnull(CliVenCup,0) CliVenCup," +
                        " (select ISNULL(DATEDIFF(day, max(facfec), getdate()),999) Duration from factura where facnitsec=c.nitsec and facClisec=c.clisec and factiptra='FDV' AND FACEST='A') CliDiasUltVen ," + //and facClisec=c.clisec
                        " isnull(cliintfre,'') cliintfre,isnull(frenom,'') frenom,isnull(CliIntTiempo,0) CliIntTiempo,isnull(CliIntOrdDet,0) CliIntOrdDet,isnull(c.InaCod,0) InaCod,isnull(CliSinIva,'S') CliSinIva,isnull(CliNoReen,'N') CliNoReen " +
                        " , isnull(tricliica,'') tricliica,isnull(triCliIcaPucSec,'') triCliIcaPucSec, isnull(p.pucVal,0) icapucVal,isnull(p.PucPor,0)  icapucpor  " +
                        " , isnull(TriCliRet,'') TriCliRet,isnull(TriCliPucSec,'')  TriCliPucSec, isnull( r.pucVal,0) retpucVal,isnull( r.PucPor,0) retpucpor  " +
                        " , isnull(TriCliIva,'') TriCliIva,isnull(TriCliivaPucSec,'') TriCliivaPucSec, isnull( i.pucVal,0) ivapucVal,isnull(i.PucPor,0) ivapucpor,isnull(CliCartCom,'N') CliCartCom, 0 clidespagcont, 0 clidespagcre " +
                        " from ClientesVendedores cv "+
                        " left join clientes c on cv.NitSec=C.NitSec and cv.CliSec=c.CliSec" +
                        " left join nit n on n.nitsec=c.nitsec" +
                        " left join vendedores vv on vv.vencod=cv.vencod" +
                        " left join frecuencia f on f.frecod=cv.cliintfre" +
                        " left join ciudad on cliciucod=ciucod" +
                        " left join Inactivos ii on c.InaCod=ii.InaCod" +
                        " left join Canales cn on cn.cancod=c.cancod" +
                        " left join CanalesSubCanales cnsc  on cnsc.cancod=c.cancod and cnsc.cansubcod=c.cansubcod" +
                        " left join Barrio B ON B.BarCod=C.BarCod" +
                        " left join puc p on triCliIcaPucSec = p.pucsec "+
                        " left join tricli t on TriCliNitSec = c.nitsec "+
                        " left join puc r on TriCliPucSec = r.PucSec "+
                        " left join puc i on TriCliivaPucSec = i.PucSec "+

                        " where cv.VenCod='" + vUsuario + "' and isnull(InaInaCli,'N')<>'S' and clivenest<>'I') Consulta order by Row desc";
            }else if (vEmpresa.trim().equalsIgnoreCase("SURTIMARCAS")) {
                Script  = "select * from(select  ROW_NUMBER() OVER(ORDER BY cv.nitsec ASC) AS Row,isnull(CliTamCan,0) CliTamCan,c.nitsec,c.clisec,isnull(NitCom,'')NitCom,isnull(CliNom,'')CliNom,isnull(NitIde,'')NitIde" +
                        ",isnull(CiuCod,'') CiuCod,isnull(CiuNom,'')+' '+CONVERT(VARCHAR(24),isnull(clifecactdat,''),110)  CiuNom,isnull(b.BarCod,0) BarCod,isnull(b.BarNom,'')BarNom,isnull(rtrim(ltrim(b.BarNom)),'')+' '+CliDir CliDir," +
                        // "isnull(CIUlisprecod,isnull(lisprecod,1)) lisprecod," +
                        " case when CLILISCLI='CLI' THEN isnull(lisprecod,1) ELSE isnull(CIUlisprecod,isnull(lisprecod,1)) END lisprecod, "+
                        " 1 lisprecodlim,isnull(CliTel,'')CliTel ,isnull(CLICONPAG,0) CLICONPAG,isnull(CliIntLun,'N')CliIntLun,isnull(CliIntMar,'N')CliIntMar" +
                        " ,isnull(CliIntMie,'N')CliIntMie,isnull(CliIntJue,'N')CliIntJue,isnull(CliIntVIE,'N')CliIntVie,isnull(CliInSab,'N') CliIntSab,isnull(CliIntDom,'N') CliIntDom,isnull(PerCliCod,1) PerCliCod," +
                        " isnull(c.CanCod,1) CanCod,isnull(CanNom,'') CanNom,isnull(c.CanSubCod,1) CanSubCod,isnull(CanSubNom,'') CanSubNom,isnull(CliCup,0) CliCup,isnull(CliVenCup,0) CliVenCup," +
                        " (select ISNULL(DATEDIFF(day, max(facfec), getdate()),999) Duration from factura where facnitsec=c.nitsec and facClisec=c.clisec and factiptra='FDV' AND FACEST='A') CliDiasUltVen ," + //and facClisec=c.clisec
                        " isnull(cliintfre,'') cliintfre,isnull(frenom,'') frenom,isnull(CliIntTiempo,0) CliIntTiempo,isnull(CliIntOrdDet,0) CliIntOrdDet,isnull(c.InaCod,0) InaCod,isnull(CliSinIva,'S') CliSinIva, 'N' CliNoReen " +
                        " , '' tricliica, '' triCliIcaPucSec, 0 icapucVal, 0  icapucpor  " +
                        " , '' TriCliRet, ''  TriCliPucSec, 0 retpucVal, 0 retpucpor  " +
                        " , '' TriCliIva,'' TriCliivaPucSec, 0 ivapucVal, 0 ivapucpor ,'N' CliCartCom, 0 clidespagcont, 0 clidespagcre " +
                        " from ClientesVendedores cv " +
                        " left join clientes c on cv.NitSec=C.NitSec and cv.CliSec=c.CliSec" +
                        " left join nit n on n.nitsec=c.nitsec" +
                        " left join frecuencia f on f.frecod=cv.cliintfre" +
                        " left join ciudad on cliciucod=ciucod" +
                        " left join Inactivos ii on c.InaCod=ii.InaCod" +
                        " left join Canales cn on cn.cancod=c.cancod" +
                        " left join CanalesSubCanales cnsc  on cnsc.cancod=c.cancod and cnsc.cansubcod=c.cansubcod" +
                        " left join Barrio B ON B.BarCod=C.BarCod" +
                        " where VenCod='" + vUsuario + "' and cliest<>'I' and isnull(InaInaCli,'N')<>'S' and clivenest<>'I') Consulta order by Row desc";



            }else if (vEmpresa.trim().equalsIgnoreCase("TODORAPIDAS")) {
                 Script = "select * from(select  ROW_NUMBER() OVER(ORDER BY cv.nitsec ASC) AS Row,isnull(CliTamCan,0) CliTamCan,c.nitsec,c.clisec,isnull(NitCom,'')NitCom,isnull(CliNom,'')CliNom,isnull(NitIde,'')NitIde" +
                        ",isnull(CiuCod,'') CiuCod,isnull(CiuNom,'')+'('+isnull(lisprenom,'')+')' CiuNom,isnull(b.BarCod,0) BarCod,isnull(b.BarNom,'')BarNom,CliDir,case when c.lisprecod=4 then 4 else 1 end lisprecod,isnull(c.lisprecod,1) lisprecodlim,isnull(CliTel,'')CliTel ,isnull(CLICONPAG,0) CLICONPAG,isnull(CliIntLun,'N')CliIntLun,isnull(CliIntMar,'N')CliIntMar" +
                        " ,isnull(CliIntMie,'N')CliIntMie,isnull(CliIntJue,'N')CliIntJue,isnull(CliIntVIE,'N')CliIntVie,isnull(CliInSab,'N') CliIntSab,isnull(CliIntDom,'N') CliIntDom,isnull(PerCliCod,1) PerCliCod," +
                        " isnull(c.CanCod,1) CanCod,isnull(CanNom,'') CanNom,isnull(c.CanSubCod,1) CanSubCod,isnull(CanSubNom,'') CanSubNom,isnull(CliCup,0) CliCup,isnull(CliVenCup,0) CliVenCup," +
                        " (select ISNULL(DATEDIFF(day, max(facfec), getdate()),999) Duration from factura where facnitsec=c.nitsec and facClisec=c.clisec and factiptra='FDV' AND FACEST='A') CliDiasUltVen ," + //and facClisec=c.clisec
                        " isnull(cliintfre,'') cliintfre,isnull(frenom,'') frenom,isnull(CliIntTiempo,0) CliIntTiempo,isnull(CliIntOrdDet,0) CliIntOrdDet,isnull(c.InaCod,0) InaCod,isnull(CliSinIva,'S') CliSinIva,'N' CliNoReen " +
                         " , '' tricliica, '' triCliIcaPucSec, 0 icapucVal, 0  icapucpor  " +
                         " , '' TriCliRet, ''  TriCliPucSec, 0 retpucVal, 0 retpucpor  " +
                         " , '' TriCliIva,'' TriCliivaPucSec, 0 ivapucVal, 0 ivapucpor ,'N' CliCartCom, 0 clidespagcont, 0 clidespagcre  " +
                         " from ClientesVendedores cv " +
                        " left join clientes c on cv.NitSec=C.NitSec and cv.CliSec=c.CliSec" +
                        " left join nit n on n.nitsec=c.nitsec" +
                        " left join frecuencia f on f.frecod=cv.cliintfre" +
                         " left join listasprecios ll on ll.lisprecod=c.lisprecod" +
                        " left join ciudad on cliciucod=ciucod" +
                        " left join Canales cn on cn.cancod=c.cancod" +
                        " left join CanalesSubCanales cnsc  on cnsc.cancod=c.cancod and cnsc.cansubcod=c.cansubcod" +
                        " left join Barrio B ON B.BarCod=C.BarCod" +
                        " where VenCod='" + vUsuario + "') Consulta order by Row desc";
            }else if (vEmpresa.trim().equalsIgnoreCase("GELVEZARA")) {
                Script = "select * from(select  ROW_NUMBER() OVER(ORDER BY cv.nitsec ASC) AS Row,isnull(CliTamCan,0) CliTamCan,c.nitsec,c.clisec,isnull(NitCom,'')NitCom,isnull(CliNom,'') CliNom,isnull(NitIde,'')NitIde" +
                        ",isnull(CiuCod,'') CiuCod,isnull(CiuNom,'')+'('+isnull(lisprenom,'')+')' CiuNom,isnull(b.BarCod,0) BarCod,isnull(b.BarNom,'')BarNom,isnull(rtrim(ltrim(b.BarNom)),'')+' '+CliDir CliDir,1 lisprecod,1 lisprecodlim,isnull(CliTel,'')CliTel ,isnull(CLICONPAG,0) CLICONPAG,isnull(CliIntLun,'N')CliIntLun,isnull(CliIntMar,'N')CliIntMar" +
                        " ,isnull(CliIntMie,'N')CliIntMie,isnull(CliIntJue,'N')CliIntJue,isnull(CliIntVIE,'N')CliIntVie,isnull(CliInSab,'N') CliIntSab,isnull(CliIntDom,'N') CliIntDom,isnull(PerCliCod,1) PerCliCod," +
                        " isnull(c.CanCod,1) CanCod,isnull(CanNom,'') CanNom,isnull(c.CanSubCod,1) CanSubCod,isnull(CanSubNom,'') CanSubNom,isnull(CliCup,0) CliCup,isnull(CliVenCup,0) CliVenCup," +
                        " ISNULL((select ISNULL(DATEDIFF(day, max(facfec), getdate()),999) Duration from factura where facnitsec=c.nitsec and facClisec=c.clisec and factiptra='FDV' AND FACEST='A'),0) CliDiasUltVen ," + //and facClisec=c.clisec
                        " isnull(cliintfre,'') cliintfre,isnull(frenom,'') frenom,isnull(CliIntTiempo,0) CliIntTiempo,isnull(CliIntOrdDet,0) CliIntOrdDet,isnull(c.InaCod,0) InaCod,isnull(CliSinIva,'S') CliSinIva, 'N' CliNoReen " +
                        " , '' tricliica, '' triCliIcaPucSec, 0 icapucVal, 0  icapucpor  " +
                        " , '' TriCliRet, ''  TriCliPucSec, 0 retpucVal, 0 retpucpor  " +
                        " , '' TriCliIva,'' TriCliivaPucSec, 0 ivapucVal, 0 ivapucpor  ,'N' CliCartCom , 0 clidespagcont, 0 clidespagcre " +
                        " from ClientesVendedores cv " +
                        " left join clientes c on cv.NitSec=C.NitSec and cv.CliSec=c.CliSec" +
                        " left join nit n on n.nitsec=c.nitsec" +
                        " left join frecuencia f on f.frecod=cv.cliintfre" +
                        " left join listasprecios ll on ll.lisprecod=c.lisprecod" +
                        " left join ciudad on cliciucod=ciucod" +
                        " left join Inactivos ii on c.InaCod=ii.InaCod" +
                        " left join Canales cn on cn.cancod=c.cancod" +
                        " left join CanalesSubCanales cnsc  on cnsc.cancod=c.cancod and cnsc.cansubcod=c.cansubcod" +
                        " left join Barrio B ON B.BarCod=C.BarCod" +
                        " where VenCod='" + vUsuario + "' and isnull(InaInaCli,'N')<>'S' and cliest<>'I' ) Consulta order by Row desc";



            }else if (vEmpresa.trim().equalsIgnoreCase("GELVEZSUCARA")) {
                Script = "select * from(select  ROW_NUMBER() OVER(ORDER BY cv.nitsec ASC) AS Row,isnull(CliTamCan,0) CliTamCan,c.nitsec,c.clisec,isnull(NitCom,'')NitCom,isnull(CliNom,'') CliNom,isnull(NitIde,'')NitIde" +
                        ",isnull(CiuCod,'') CiuCod,isnull(CiuNom,'')+'('+isnull(lisprenom,'')+')' CiuNom,isnull(b.BarCod,0) BarCod,isnull(b.BarNom,'')BarNom,isnull(rtrim(ltrim(b.BarNom)),'')+' '+CliDir CliDir,isnull(c.lisprecod,1) lisprecod,1 lisprecodlim,isnull(CliTel,'')CliTel ,isnull(CLICONPAG,0) CLICONPAG,isnull(CliIntLun,'N')CliIntLun,isnull(CliIntMar,'N')CliIntMar" +
                        " ,isnull(CliIntMie,'N')CliIntMie,isnull(CliIntJue,'N')CliIntJue,isnull(CliIntVIE,'N')CliIntVie,isnull(CliInSab,'N') CliIntSab,isnull(CliIntDom,'N') CliIntDom,isnull(PerCliCod,1) PerCliCod," +
                        " isnull(c.CanCod,1) CanCod,isnull(CanNom,'') CanNom,isnull(c.CanSubCod,1) CanSubCod,isnull(CanSubNom,'') CanSubNom,isnull(CliCup,0) CliCup,isnull(CliVenCup,0) CliVenCup," +
                        " ISNULL((select ISNULL(DATEDIFF(day, max(facfec), getdate()),999) Duration from factura where facnitsec=c.nitsec and facClisec=c.clisec and factiptra='FDV' AND FACEST='A'),0) CliDiasUltVen ," + //and facClisec=c.clisec
                        " isnull(cliintfre,'') cliintfre,isnull(frenom,'') frenom,isnull(CliIntTiempo,0) CliIntTiempo,isnull(CliIntOrdDet,0) CliIntOrdDet,isnull(c.InaCod,0) InaCod,isnull(CliSinIva,'S') CliSinIva,'N' CliNoReen " +
                        " , '' tricliica, '' triCliIcaPucSec, 0 icapucVal, 0  icapucpor  " +
                        " , '' TriCliRet, ''  TriCliPucSec, 0 retpucVal, 0 retpucpor  " +
                        " , '' TriCliIva,'' TriCliivaPucSec, 0 ivapucVal, 0 ivapucpor  ,'N' CliCartCom , 0 clidespagcont, 0 clidespagcre " +
                        " from ClientesVendedores cv " +
                        " left join clientes c on cv.NitSec=C.NitSec and cv.CliSec=c.CliSec" +
                        " left join nit n on n.nitsec=c.nitsec" +
                        " left join frecuencia f on f.frecod=cv.cliintfre" +
                        " left join listasprecios ll on ll.lisprecod=c.lisprecod" +
                        " left join ciudad on cliciucod=ciucod" +
                        " left join Inactivos ii on c.InaCod=ii.InaCod" +
                        " left join Canales cn on cn.cancod=c.cancod" +
                        " left join CanalesSubCanales cnsc  on cnsc.cancod=c.cancod and cnsc.cansubcod=c.cansubcod" +
                        " left join Barrio B ON B.BarCod=C.BarCod" +
                        " where VenCod='" + vUsuario + "' and isnull(InaInaCli,'N')<>'S' and cliest<>'I' ) Consulta order by Row desc";
            }else if (vEmpresa.trim().equalsIgnoreCase("FARMA")) {
                Script = "select * from(select  ROW_NUMBER() OVER(ORDER BY cv.nitsec ASC) AS Row,isnull(CliTamCan,0) CliTamCan,c.nitsec,c.clisec,isnull(NitCom,'')NitCom,isnull(CliNom,'') CliNom,isnull(NitIde,'')NitIde" +
                        ",isnull(CiuCod,'') CiuCod,isnull(CiuNom,'')+'('+isnull(lisprenom,'')+')' CiuNom,isnull(b.BarCod,0) BarCod,isnull(b.BarNom,'')BarNom,isnull(rtrim(ltrim(b.BarNom)),'')+' '+CliDir CliDir,isnull(c.lisprecod,1) lisprecod,1 lisprecodlim,isnull(CliTel,'')CliTel ,isnull(CLICONPAG,0) CLICONPAG,isnull(CliIntLun,'N')CliIntLun,isnull(CliIntMar,'N')CliIntMar" +
                        " ,isnull(CliIntMie,'N')CliIntMie,isnull(CliIntJue,'N')CliIntJue,isnull(CliIntVIE,'N')CliIntVie,isnull(CliInSab,'N') CliIntSab,isnull(CliIntDom,'N') CliIntDom,isnull(PerCliCod,1) PerCliCod," +
                        " isnull(c.CanCod,1) CanCod,isnull(CanNom,'') CanNom,isnull(c.CanSubCod,1) CanSubCod,isnull(CanSubNom,'') CanSubNom,isnull(CliCup,0) CliCup,isnull(CliVenCup,0) CliVenCup," +
                        " ISNULL((select ISNULL(DATEDIFF(day, max(facfec), getdate()),999) Duration from factura where facnitsec=c.nitsec and facClisec=c.clisec and factiptra='FDV' AND FACEST='A'),0) CliDiasUltVen ," + //and facClisec=c.clisec
                        " isnull(cliintfre,'') cliintfre,isnull(frenom,'') frenom,isnull(CliIntTiempo,0) CliIntTiempo,isnull(CliIntOrdDet,0) CliIntOrdDet,isnull(c.InaCod,0) InaCod,isnull(CliSinIva,'S') CliSinIva,'N' CliNoReen " +
                        " , '' tricliica, '' triCliIcaPucSec, 0 icapucVal, 0  icapucpor  " +
                        " , '' TriCliRet, ''  TriCliPucSec, 0 retpucVal, 0 retpucpor  " +
                        " , '' TriCliIva,'' TriCliivaPucSec, 0 ivapucVal, 0 ivapucpor ,'N' CliCartCom, 0 clidespagcont, 0 clidespagcre   " +
                        " from ClientesVendedores cv " +
                        " left join clientes c on cv.NitSec=C.NitSec and cv.CliSec=c.CliSec" +
                        " left join nit n on n.nitsec=c.nitsec" +
                        " left join frecuencia f on f.frecod=cv.cliintfre" +
                        " left join listasprecios ll on ll.lisprecod=c.lisprecod" +
                        " left join ciudad on cliciucod=ciucod" +
                        " left join Inactivos ii on c.InaCod=ii.InaCod" +
                        " left join Canales cn on cn.cancod=c.cancod" +
                        " left join CanalesSubCanales cnsc  on cnsc.cancod=c.cancod and cnsc.cansubcod=c.cansubcod" +
                        " left join Barrio B ON B.BarCod=C.BarCod" +
                        " where VenCod='" + vUsuario + "' and isnull(InaInaCli,'N')<>'S' and cliest<>'I' ) Consulta order by Row desc";
            }else if (vEmpresa.trim().equalsIgnoreCase("MEDIVALLE")) {
                Script = "select * from(select  ROW_NUMBER() OVER(ORDER BY cv.nitsec ASC) AS Row,isnull(CliTamCan,0) CliTamCan,c.nitsec,c.clisec,isnull(NitCom,'')NitCom,isnull(CliNom,'') CliNom,isnull(NitIde,'')NitIde" +
                        ",isnull(CiuCod,'') CiuCod,isnull(CiuNom,'')+'('+isnull(lisprenom,'')+')' CiuNom,isnull(b.BarCod,0) BarCod,isnull(b.BarNom,'')BarNom,isnull(rtrim(ltrim(b.BarNom)),'')+' '+CliDir CliDir,isnull(case when c.lisprecod=7 then 1 when c.lisprecod=9 then 2 when c.lisprecod=10 then 3  when c.lisprecod=11 then 4 when c.lisprecod=12 then 5 else 1 end ,9) lisprecod,1 lisprecodlim,isnull(CliTel,'')CliTel ,isnull(CLICONPAG,0) CLICONPAG,isnull(CliIntLun,'N')CliIntLun,isnull(CliIntMar,'N')CliIntMar" +
                        " ,isnull(CliIntMie,'N')CliIntMie,isnull(CliIntJue,'N')CliIntJue,isnull(CliIntVIE,'N')CliIntVie,isnull(CliInSab,'N') CliIntSab,isnull(CliIntDom,'N') CliIntDom,isnull(PerCliCod,1) PerCliCod," +
                        " isnull(c.CanCod,1) CanCod,isnull(CanNom,'') CanNom,isnull(c.CanSubCod,1) CanSubCod,isnull(CanSubNom,'') CanSubNom,isnull(CliCup,0) CliCup,isnull(CliVenCup,0) CliVenCup," +
                        " ISNULL((select ISNULL(DATEDIFF(day, max(facfec), getdate()),999) Duration from factura where facnitsec=c.nitsec and facClisec=c.clisec and factiptra='FDV' AND FACEST='A'),0) CliDiasUltVen ," + //and facClisec=c.clisec
                        " isnull(cliintfre,'') cliintfre,isnull(frenom,'') frenom,isnull(CliIntTiempo,0) CliIntTiempo,isnull(CliIntOrdDet,0) CliIntOrdDet,isnull(c.InaCod,0) InaCod,isnull(CliSinIva,'S') CliSinIva,'N' CliNoReen " +
                        " , '' tricliica, '' triCliIcaPucSec, 0 icapucVal, 0  icapucpor  " +
                        " , '' TriCliRet, ''  TriCliPucSec, 0 retpucVal, 0 retpucpor  " +
                        " , '' TriCliIva,'' TriCliivaPucSec, 0 ivapucVal, 0 ivapucpor   ,'N' CliCartCom , 0 clidespagcont, 0 clidespagcre " +
                        " from ClientesVendedores cv " +
                        " left join clientes c on cv.NitSec=C.NitSec and cv.CliSec=c.CliSec" +
                        " left join nit n on n.nitsec=c.nitsec" +
                        " left join frecuencia f on f.frecod=cv.cliintfre" +
                        " left join listasprecios ll on ll.lisprecod=c.lisprecod" +
                        " left join ciudad on cliciucod=ciucod" +
                        " left join Inactivos ii on c.InaCod=ii.InaCod" +
                        " left join Canales cn on cn.cancod=c.cancod" +
                        " left join CanalesSubCanales cnsc  on cnsc.cancod=c.cancod and cnsc.cansubcod=c.cansubcod" +
                        " left join Barrio B ON B.BarCod=C.BarCod" +
                        " where VenCod='" + vUsuario + "' and isnull(InaInaCli,'N')<>'S' and cliest<>'I' ) Consulta order by Row desc";
            }else if (vEmpresa.trim().equalsIgnoreCase("MENTAHAIR") || vEmpresa.trim().equalsIgnoreCase("MENTAHAIRCOT")  ) {
                Script = "select * from(select  ROW_NUMBER() OVER(ORDER BY cv.nitsec ASC) AS Row,isnull(CliTamCan,0) CliTamCan,c.nitsec,c.clisec,isnull(NitCom,'')NitCom,isnull(CliNom,'') CliNom,isnull(NitIde,'')NitIde" +
                        ",isnull(CiuCod,'') CiuCod,isnull(CiuNom,'')+'('+isnull(lisprenom,'')+')' CiuNom,isnull(b.BarCod,0) BarCod,isnull(b.BarNom,'')BarNom,isnull(rtrim(ltrim(b.BarNom)),'')+' '+CliDir CliDir," +
                        " isnull(case when c.lisprecod=9 then 1 when c.lisprecod=10 then 2 when c.lisprecod=11 then 3  when c.lisprecod=12 then 4 when c.lisprecod=13 then 5 when c.lisprecod=15 then 15 else 1 end ,9) lisprecod," +
                        " 1 lisprecodlim,isnull(CliTel,'')CliTel ,isnull(CLICONPAG,0) CLICONPAG,isnull(CliIntLun,'N')CliIntLun,isnull(CliIntMar,'N')CliIntMar" +
                        " ,isnull(CliIntMie,'N')CliIntMie,isnull(CliIntJue,'N')CliIntJue,isnull(CliIntVIE,'N')CliIntVie,isnull(CliInSab,'N') CliIntSab,isnull(CliIntDom,'N') CliIntDom,isnull(PerCliCod,1) PerCliCod," +
                        " isnull(c.CanCod,1) CanCod,isnull(CanNom,'') CanNom,isnull(c.CanSubCod,1) CanSubCod,isnull(CanSubNom,'') CanSubNom,isnull(CliCup,0) CliCup,isnull(CliVenCup,0) CliVenCup," +
                        " ISNULL((select ISNULL(DATEDIFF(day, max(facfec), getdate()),999) Duration from factura where facnitsec=c.nitsec and facClisec=c.clisec and factiptra='FDV' AND FACEST='A'),0) CliDiasUltVen ," + //and facClisec=c.clisec
                        " isnull(cliintfre,'') cliintfre,isnull(frenom,'') frenom,isnull(CliIntTiempo,0) CliIntTiempo,isnull(CliIntOrdDet,0) CliIntOrdDet,isnull(c.InaCod,0) InaCod,isnull(CliSinIva,'S') CliSinIva,'N' CliNoReen " +
                        " , '' tricliica, '' triCliIcaPucSec, 0 icapucVal, 0  icapucpor  " +
                        " , '' TriCliRet, ''  TriCliPucSec, 0 retpucVal, 0 retpucpor  " +
                        " , '' TriCliIva,'' TriCliivaPucSec, 0 ivapucVal, 0 ivapucpor  ,'N' CliCartCom, isnull(clidespagcont,0) clidespagcont, isnull(clidespagcre,0)  clidespagcre  " +
                        " from ClientesVendedores cv " +
                        " left join clientes c on cv.NitSec=C.NitSec and cv.CliSec=c.CliSec" +
                        " left join nit n on n.nitsec=c.nitsec" +
                        " left join frecuencia f on f.frecod=cv.cliintfre" +
                        " left join listasprecios ll on ll.lisprecod=c.lisprecod" +
                        " left join ciudad on cliciucod=ciucod" +
                        " left join Inactivos ii on c.InaCod=ii.InaCod" +
                        " left join Canales cn on cn.cancod=c.cancod" +
                        " left join CanalesSubCanales cnsc  on cnsc.cancod=c.cancod and cnsc.cansubcod=c.cansubcod" +
                        " left join Barrio B ON B.BarCod=C.BarCod" +
                        " where VenCod='" + vUsuario + "' and isnull(InaInaCli,'N')<>'S' and cliest<>'I' ) Consulta order by Row desc";
            }else{
                Script = "select * from(select  ROW_NUMBER() OVER(ORDER BY cv.nitsec ASC) AS Row,isnull(CliTamCan,0) CliTamCan,c.nitsec,c.clisec,isnull(NitCom,'')NitCom,isnull(CliNom,'') CliNom,isnull(NitIde,'')NitIde" +
                        ",isnull(CiuCod,'') CiuCod,isnull(CiuNom,'')+'('+isnull(lisprenom,'')+')' CiuNom,isnull(b.BarCod,0) BarCod,isnull(b.BarNom,'')BarNom,isnull(rtrim(ltrim(b.BarNom)),'')+' '+CliDir CliDir,isnull(c.lisprecod,1) lisprecod,1 lisprecodlim,isnull(CliTel,'')CliTel ,isnull(CLICONPAG,0) CLICONPAG,isnull(CliIntLun,'N')CliIntLun,isnull(CliIntMar,'N')CliIntMar" +
                        " ,isnull(CliIntMie,'N')CliIntMie,isnull(CliIntJue,'N')CliIntJue,isnull(CliIntVIE,'N')CliIntVie,isnull(CliInSab,'N') CliIntSab,isnull(CliIntDom,'N') CliIntDom,isnull(PerCliCod,1) PerCliCod," +
                        " isnull(c.CanCod,1) CanCod,isnull(CanNom,'') CanNom,isnull(c.CanSubCod,1) CanSubCod,isnull(CanSubNom,'') CanSubNom,isnull(CliCup,0) CliCup,isnull(CliVenCup,0) CliVenCup," +
                        " ISNULL((select ISNULL(DATEDIFF(day, max(facfec), getdate()),999) Duration from factura where facnitsec=c.nitsec and facClisec=c.clisec and factiptra='FDV' AND FACEST='A'),0) CliDiasUltVen ," + //and facClisec=c.clisec
                        " isnull(cliintfre,'') cliintfre,isnull(frenom,'') frenom,isnull(CliIntTiempo,0) CliIntTiempo,isnull(CliIntOrdDet,0) CliIntOrdDet,isnull(c.InaCod,0) InaCod,isnull(CliSinIva,'S') CliSinIva,'N' CliNoReen " +
                        " , '' tricliica, '' triCliIcaPucSec, 0 icapucVal, 0  icapucpor  " +
                        " , '' TriCliRet, ''  TriCliPucSec, 0 retpucVal, 0 retpucpor  " +
                        " , '' TriCliIva,'' TriCliivaPucSec, 0 ivapucVal, 0 ivapucpor ,'N' CliCartCom, 0 clidespagcont, 0 clidespagcre   " +
                        " from ClientesVendedores cv " +
                        " left join clientes c on cv.NitSec=C.NitSec and cv.CliSec=c.CliSec" +
                        " left join nit n on n.nitsec=c.nitsec" +
                        " left join frecuencia f on f.frecod=cv.cliintfre" +
                        " left join listasprecios ll on ll.lisprecod=c.lisprecod" +
                        " left join ciudad on cliciucod=ciucod" +
                        " left join Inactivos ii on c.InaCod=ii.InaCod" +
                        " left join Canales cn on cn.cancod=c.cancod" +
                        " left join CanalesSubCanales cnsc  on cnsc.cancod=c.cancod and cnsc.cansubcod=c.cansubcod" +
                        " left join Barrio B ON B.BarCod=C.BarCod" +
                        " where VenCod='" + vUsuario + "' and isnull(InaInaCli,'N')<>'S' and cliest<>'I' ) Consulta order by Row desc";
            }
           Log.e("script Cliente", Script);
             rsClientes = comm.executeQuery(Script);
            BdSql.execSQL("Delete from Clientes");

            while (rsClientes.next()){
                if(TotalFilas==0) {
                    vPrBar_Import.setMax(rsClientes.getInt("Row"));
                    TotalFilas=rsClientes.getInt("Row");
                    String Filas=rsClientes.getString("Row").trim();
                }
                Vueltas+=1;
                vPrBar_Import.setMax(TotalFilas);
                vPrBar_Import.setProgress(Vueltas);

                String InsertScript = "insert into clientes (nitsec,"
                        + "clisec,"
                        + "NitCom,"
                        + "CliNom,"
                        + "NitIde,"
                        + "CliDir,"
                        + "CliTel,"
                        + "Lisprecod,"
                        + "Lisprecodlim,"
                        + "CliConPag,"
                        + "cliintlun,"
                        + "cliintmar,"
                        + "cliintmie,"
                        + "cliintjue,"
                        + "cliintvie,"
                        + "cliintsab,"
                        + "cliintdom,"
                        + "CliIntTiempo,"
                        + "CliIntOrdDet,"
                        + "cliintfre,"
                        + "frenom,"
                        + "PerCliCod,"
                        + "CanCod,"
                        + "CanNom,"
                        + "CiuCod,"
                        + "CiuNom,"
                        + "BarCod,"
                        + "BarNom,"
                        + "CanSubCod,"
                        + "CanSubNom,"
                        + "CliCup,"
                        + "CliVenCup,CliTamCan,"
                        + "CliDiasUltVen,InaCod,CliIva,CliNoree,"
                        +"tricliica,TriCliRet,TriCliIva,triCliIcaPucSec,TriCliPucSec,TriCliivaPucSec,retpucVal,icapucVal,ivapucVal,"
                        +"retpucpor,icapucpor,ivapucpor,clidespagcont,clidespagcre,CliCartCom "
                        + " ) values ('"
                        + rsClientes.getString("nitsec").trim() + "',"
                        + rsClientes.getString("clisec").trim() + ",'"
                        + rsClientes.getString("NitCom").trim().replace("'","") + "','"
                        + rsClientes.getString("CliNom").trim().replace("'","") + "','"
                        + rsClientes.getString("NitIde").trim().replace("'","") + "','"
                        + rsClientes.getString("CliDir").trim().replace("'","") +  "','"
                        + rsClientes.getString("CliTel").trim().replace("'","")  + "',"
                        + rsClientes.getString("lisprecod").trim() + ","
                        + rsClientes.getString("lisprecodlim").trim() + ","
                        + rsClientes.getString("CLICONPAG").trim() + ",'"
                        +rsClientes.getString("CliIntLun").trim() + "','"
                        +rsClientes.getString("CliIntMar").trim() + "','"
                        +rsClientes.getString("CliIntMie").trim() + "','"
                        +rsClientes.getString("CliIntJue").trim() + "','"
                        +rsClientes.getString("CliIntVie").trim() + "','"
                        +rsClientes.getString("CliIntSab").trim() + "','"
                        +rsClientes.getString("CliIntDom").trim() + "',"
                        +rsClientes.getString("CliIntTiempo").trim() + ","
                        +rsClientes.getString("CliIntOrdDet").trim() + ",'"
                        +rsClientes.getString("cliintfre").trim() + "','"
                        +rsClientes.getString("frenom").trim() + "',"
                        +rsClientes.getString("PerCliCod").trim() + ","
                        +rsClientes.getString("CanCod").trim() + ",'"
                        +rsClientes.getString("CanNom").trim() + "','"
                        +rsClientes.getString("CiuCod").trim() + "','"
                        +rsClientes.getString("CiuNom").trim() + "',"
                        +rsClientes.getString("BarCod").trim() + ",'"
                        +rsClientes.getString("BarNom").trim() + "',"
                        +rsClientes.getString("CanSubCod").trim() + ",'"
                        +rsClientes.getString("CanSubNom").trim() + "',"
                        +rsClientes.getString("CliCup").trim() + ","
                        +rsClientes.getString("CliVenCup").trim() + ","
                        +rsClientes.getString("CliTamCan").trim() + ","
                        +rsClientes.getString("CliDiasUltVen").trim() + ","
                        +rsClientes.getString("InaCod").trim() + ",'"
                        +rsClientes.getString("CliSinIva").trim() + "','"
                        +rsClientes.getString("CliNoReen").trim() + "','"
                        +rsClientes.getString("tricliica").trim() + "','"
                        +rsClientes.getString("TriCliRet").trim() + "','"
                        +rsClientes.getString("TriCliIva").trim() + "','"
                        +rsClientes.getString("triCliIcaPucSec").trim() + "','"
                        +rsClientes.getString("TriCliPucSec").trim() + "','"
                        +rsClientes.getString("TriCliivaPucSec").trim() + "',"
                        +rsClientes.getString("retpucVal").trim() + ","
                        +rsClientes.getString("icapucVal").trim() + ","
                        +rsClientes.getString("ivapucVal").trim() + ","
                        +rsClientes.getString("retpucpor").trim() + ","
                        +rsClientes.getString("icapucpor").trim() + ","
                        +rsClientes.getString("ivapucpor").trim() + ","
                        +rsClientes.getString("clidespagcont").trim() + ","
                        +rsClientes.getString("clidespagcre").trim() + ",'"
                        +rsClientes.getString("CliCartCom").trim() + "')" ;
                try {
                    BdSql.execSQL(InsertScript);
                    Insertados+=1;

                }catch (Exception ex){
                    Log.e("Errocliente",ex.toString());
                    Errores+=1;


                }
                try {

                    // vPrBar_Clientes.setMax(TotalFilas);
                    final int finalTotalFilas = TotalFilas;
                    final int finalInsertados = Insertados;
                    final int finalErrores = Errores;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                            vtxt_OkImport.setText(String.valueOf(finalInsertados));
                            vtxt_ErrImport.setText(String.valueOf(finalErrores));
                            // vPrBar_Clientes.setProgress(Vueltas);
                        }
                    });
                }catch (Exception e){
                    Log.e("Errocliente",e.toString());
                    ErrorTxt += "Errocliente: "+ e.toString();
                    int hh=0;
                }
            }//while(rsClientes.next());

         //   while (rsClientes.next()){

         //   }
        } catch (Exception e) {
            Log.e("\n Errocliente2",e.toString());
            ErrorTxt += "\n Errocliente2"+e.toString();
            Errores=1;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    vtxt_CntImport.setText("N/N");
                    vtxt_OkImport.setText("N/N");
                    vtxt_ErrImport.setText("1");
                }
            });
        } /*finally { // Cerramos las conexiones, en orden inverso a su apertura
            try { if (rsClientes != null) rsClientes.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
            try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
        }*/


            ErroresGen+=Errores;
            Log.e("error1",String.valueOf(ErroresGen));
       // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //Resultado[0] = new int[]{Insertados,Errores,TotalFilas};
               // int[] Resultado = //CargarDatos();
            CargarDatos();
            }
        }).start();


        return Resultado;
    }
    public  int[] CargarProveedores(){
        int hh=0;
        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntProveedores);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkProveedores);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrProveedores);
        vPrBar_Import= (ProgressBar)findViewById(R.id.prBar_Proveedores);
        vPrBar_Import.setProgress(0);
        //vPrBar_Import.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        //int TotalFilasCiudades=0;
        //int InsertadosCiudades=0;
        //int ErroresCiudades=0;
        //int VueltasCiudades=0;
        final int[] Resultado = new int[]{0, 0, 0};

        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                //    '" + vUsuario + "'
                String vEmpresa=vGlobalVariables.getEmpresa();
                vEmpresa=vEmpresa.toUpperCase();

                Connection conn = null;
                Statement comm = null;
                ResultSet rsClientes  = null;
                try {

                    ConBd conbd = new ConBd();
                    conn = conbd.CargarConexion(getApplicationContext());
                    comm = conn.createStatement();
                    String Script="";
                    vEmpresa=vEmpresa.toUpperCase();
                    Script  = "select * from(select  ROW_NUMBER() OVER(ORDER BY NitSec ASC) AS Row, NitSec,Nitide,NitCom  from nit where NitIndPro = 'S' and ProCheckMov = 'S') Consulta order by Row desc ";
                    rsClientes = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from Proveedores");

                    while (rsClientes.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsClientes.getInt("Row"));
                            TotalFilas=rsClientes.getInt("Row");
                            String Filas=rsClientes.getString("Row").trim();
                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);

                        String InsertScript = "insert into Proveedores (NitSec,Nitide,NitCom"
                                + " ) values ('"
                                + rsClientes.getString("NitSec").trim() + "','"
                                + rsClientes.getString("Nitide").trim() + "','"
                                +rsClientes.getString("NitCom").trim() + "')" ;
                        try {
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Log.e("Erroproveedpr",ex.toString());
                            Errores+=1;


                        }
                        try {

                            // vPrBar_Clientes.setMax(TotalFilas);
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Clientes.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            Log.e("Errocliente",e.toString());
                            int hh=0;
                        }
                    }//while(rsClientes.next());

                    //   while (rsClientes.next()){

                    //   }
                } catch (Exception e) {
                    Log.e("Errocliente",e.toString());
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsClientes != null) rsClientes.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }


                ErroresGen+=Errores;
                Log.e("error1",String.valueOf(ErroresGen));
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //Resultado[0] = new int[]{Insertados,Errores,TotalFilas};
                // int[] Resultado = //CargarDatos();
                CargarDatos();
            }
        }).start();


        return Resultado;
    }
    public  int[] CargarctaProveedores(){
        int hh=0;
        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntctaProveedores);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkctaProveedores);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrctaProveedores);
        vPrBar_Import= (ProgressBar)findViewById(R.id.prBar_ctaProveedores);
        vPrBar_Import.setProgress(0);
        //vPrBar_Import.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        //int TotalFilasCiudades=0;
        //int InsertadosCiudades=0;
        //int ErroresCiudades=0;
        //int VueltasCiudades=0;
        final int[] Resultado = new int[]{0, 0, 0};

        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                //    '" + vUsuario + "'
                String vEmpresa=vGlobalVariables.getEmpresa();
                vEmpresa=vEmpresa.toUpperCase();

                Connection conn = null;
                Statement comm = null;
                ResultSet rsClientes  = null;
                try {

                    ConBd conbd = new ConBd();
                    conn = conbd.CargarConexion(getApplicationContext());
                    comm = conn.createStatement();
                    String Script="";
                    vEmpresa=vEmpresa.toUpperCase();
                    Script  = "select * from(" +
                            " select  ROW_NUMBER() OVER(ORDER BY n.NitSec ASC) AS Row, n.NitSec,isnull(NitProBanCod,'') NitProBanCod,isnull(NitProBanInf,'') NitProBanInf, isnull(BanNom,'') BanNom from nit n left join ProveedoresNitProBan p " +
                            " on n.NitSec = p.NitSec left join Bancos b on B.BanCod = p.NitProBanCod " +
                            " where NitIndPro = 'S' and ProCheckMov = 'S' ) " +
                            " Consulta order by Row desc";
                    rsClientes = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from BancoProv");

                    while (rsClientes.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsClientes.getInt("Row"));
                            TotalFilas=rsClientes.getInt("Row");
                            String Filas=rsClientes.getString("Row").trim();
                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);

                        String InsertScript = "insert into BancoProv (NitSec,NitProBanCod,BanNom,NitProBanInf"
                                + " ) values ('"
                                + rsClientes.getString("NitSec").trim() + "',"
                                + rsClientes.getString("NitProBanCod").trim() + ",'"
                                + rsClientes.getString("BanNom").trim() + "','"
                                +rsClientes.getString("NitProBanInf").trim() + "')" ;

                        try {
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Log.e("Erroctaproveedpr",ex.toString());
                            Errores+=1;


                        }
                        try {

                            // vPrBar_Clientes.setMax(TotalFilas);
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Clientes.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            Log.e("Errocliente",e.toString());
                            int hh=0;
                        }
                    }//while(rsClientes.next());

                    //   while (rsClientes.next()){

                    //   }
                } catch (Exception e) {
                    Log.e("Errocliente",e.toString());
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsClientes != null) rsClientes.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }


                ErroresGen+=Errores;
                Log.e("error1",String.valueOf(ErroresGen));
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //Resultado[0] = new int[]{Insertados,Errores,TotalFilas};
                // int[] Resultado = //CargarDatos();
                CargarDatos();
            }
        }).start();


        return Resultado;
    }



    public  int[] CargarBonificacionesProducto(){
        int hh=0;
        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntBonificadosProd);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkBonificadosProd);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrBonificadosProd);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_BonificadosProd);
        vPrBar_Import.setProgress(0);
        //vPrBar_Import.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        //int TotalFilasCiudades=0;
        //int InsertadosCiudades=0;
        //int ErroresCiudades=0;
        //int VueltasCiudades=0;
        final int[] Resultado = new int[]{0, 0, 0};

        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                String vEmpresa=vGlobalVariables.getEmpresa();
                vEmpresa=vEmpresa.toUpperCase();

                Connection conn = null;
                Statement comm = null;
                ResultSet rsClientes  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="";
                    vEmpresa=vEmpresa.toUpperCase();


                    Script  = "select * from(select ROW_NUMBER() OVER(ORDER BY bonprosec ASC) AS Row, * from(\n" +
                            " select bp.BonProSec,BonProEscLin BonProSecLin,'ESCALA' bontipo,BonProGrupo,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProSubGruCod as varchar))) FROM BonificacionesProductoSubGrupo TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As SUBGRUPOS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProFamCod as varchar))) FROM BonificacionesProductoFamilia TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As FAMILIAS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProArtSec as varchar))) FROM BonificacionesProductoDet TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As ARTICULOS,\n" +

                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProDetCanCod as varchar))) FROM BonificacionesProductoCanal TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As CANALES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProSubCanCod as varchar)))+'-'+rtrim(ltrim(cast(BonProSubCanSubCod as varchar))) FROM BonificacionesProductoSubCanal TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As SUBCANALES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProTamCantamCod as varchar))) FROM BonificacionesProductoTamano TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As TAMANOS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProSucCod as varchar))) FROM BonificacionesProducto1Sucursa TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As SUCURSALES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProVenCod as varchar))) FROM BonificacionesProductoVendedor TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As VENDEDORES,\n "+
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BONPROALINEGCOD as varchar))) FROM BonificacionesProductoUnidades TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As UNIDADES\n"+
                            ",isnull(BonProEscArtSec,0) BonProEscArtSec \n" +
                            ",isnull(BonProEscUniDes,0) BonProEscUniDes\n" +
                            ",isnull(BonProEscUniHas,0) BonProEscUniHas\n" +
                            ",isnull(BonProEscCajDes,0) BonProEscCajDes\n" +
                            ",isnull(BonProEscCajHas,0) BonProEscCajHas\n" +
                            ",0 BonProDesVal\n" +
                            ",0 BonProHasVal\n" +
                            ",BonProEscBonArtSec\n" +
                            ",BonProEscBonUni\n" +
                            ",BonProEscBonCaj\n" +
                            ",isnull(BomProMaxMixPeri,0) BomProMaxMixPeri\n" +
                            ",isnull(BonProMaxCli,0) BonProMaxCli\n" +
                            ",isnull(BomProMixRefDis,0) BomProMixRefDis\n" +
                            ",isnull(BonProCanOpc,0) BonProCanOpc\n" +
                            ",isnull(BonProMovPor,'TRA') BonProMovPor " +
                            "from BonificacionesProductoEscala bp left join BonificacionesProducto b on b.bonprosec=bp.bonprosec\n" +
                            "where BONPROEST='A' AND BonProDesFec<=CONVERT(date, GETDATE()) and BonProHasFec>=CONVERT(date, GETDATE()) UNION\n" +
                            " select bp.BonProSec,BonProEscGruLin BonProSecLin,'ESCGRU' bontipo,BonProGrupo,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProSubGruCod as varchar))) FROM BonificacionesProductoSubGrupo TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As SUBGRUPOS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProFamCod as varchar))) FROM BonificacionesProductoFamilia TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As FAMILIAS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProArtSec as varchar))) FROM BonificacionesProductoDet TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As ARTICULOS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProDetCanCod as varchar))) FROM BonificacionesProductoCanal TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As CANALES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProSubCanCod as varchar)))+'-'+rtrim(ltrim(cast(BonProSubCanSubCod as varchar))) FROM BonificacionesProductoSubCanal TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As SUBCANALES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProTamCantamCod as varchar))) FROM BonificacionesProductoTamano TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As TAMANOS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProSucCod as varchar))) FROM BonificacionesProducto1Sucursa TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As SUCURSALES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProVenCod as varchar))) FROM BonificacionesProductoVendedor TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As VENDEDORES,\n "+
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BONPROALINEGCOD as varchar))) FROM BonificacionesProductoUnidades TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As UNIDADES\n"+
                            ",'' BonProEscArtSec \n" +
                            ",isnull(BonProEscGruUniDes,0) BonProEscUniDes\n" +
                            ",isnull(BonProEscGruUniHas,0) BonProEscUniHas\n" +
                            ",isnull(BonProEscGruCajDes,0) BonProEscCajDes\n" +
                            ",isnull(BonProEscGruCajHas,0) BonProEscCajHas\n" +
                            ",0 BonProDesVal\n" +
                            ",0 BonProHasVal\n" +
                            ",BonProEscGruBonArtSec\n" +
                            " ,BonProEscGruBonUni\n" +
                            ",BonProEscGruBonCaj\n" +
                            ",isnull(BomProMaxMixPeri,0) BomProMaxMixPeri\n" +
                            ",isnull(BonProMaxCli,0) BonProMaxCli\n" +
                            ",isnull(BomProMixRefDis,0) BomProMixRefDis\n" +
                            ",isnull(BonProCanOpc,0) BonProCanOpc\n " +
                            ",isnull(BonProMovPor,'TRA') BonProMovPor " +
                            "from BonificacionesProductoEscalaGr bp left join BonificacionesProducto b on b.bonprosec=bp.bonprosec\n" +
                            "where  BONPROEST='A' AND BonProDesFec<=CONVERT(date, GETDATE()) and BonProHasFec>=CONVERT(date, GETDATE())\n" +
                            " union\n" +
                            " select bp.BonProSec,BonProDetLin BonProSecLin,'DETALLE' bontipo,BonProGrupo,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProSubGruCod as varchar))) FROM BonificacionesProductoSubGrupo TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As SUBGRUPOS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProFamCod as varchar))) FROM BonificacionesProductoFamilia TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As FAMILIAS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProArtSec as varchar))) FROM BonificacionesProductoDet TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As ARTICULOS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProDetCanCod as varchar))) FROM BonificacionesProductoCanal TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As CANALES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProSubCanCod as varchar)))+'-'+rtrim(ltrim(cast(BonProSubCanSubCod as varchar))) FROM BonificacionesProductoSubCanal TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As SUBCANALES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProTamCantamCod as varchar))) FROM BonificacionesProductoTamano TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As TAMANOS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProSucCod as varchar))) FROM BonificacionesProducto1Sucursa TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As SUCURSALES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProVenCod as varchar))) FROM BonificacionesProductoVendedor TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As VENDEDORES,\n "+
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BONPROALINEGCOD as varchar))) FROM BonificacionesProductoUnidades TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As UNIDADES\n"+
                            ",BonProArtSec\n" +
                            ",CASE WHEN BomProDetDesUni = 0 THEN BonProVtaUni ELSE BomProDetDesUni END BonProVtaUni \n" +
                            ",CASE WHEN BomProDetDesUni = 0 THEN BonProVtaUni ELSE BomProDetDesUni END BonProVtaUni \n" +
                            ",CASE WHEN BonProDetDesCaj = 0 THEN BonProVtaCaj ELSE BonProDetDesCaj END BonProVtaCaj \n" +
                            ",CASE WHEN BonProDetDesCaj = 0 THEN BonProVtaCaj ELSE BonProDetDesCaj END BonProVtaCaj \n" +
                           // ",ISNULL(BomProDetDesUni,BonProVtaUni) BonProVtaUni\n" +
                           //",ISNULL(BomProDetDesUni,BonProVtaUni) BonProVtaUni\n" +
                            //",ISNULL(BonProDetDesCaj,BonProVtaCaj) BonProVtaCaj\n" +
                            //",ISNULL(BonProDetDesCaj,BonProVtaCaj) BonProVtaCaj\n" +
                            ",0 BonProDesVal\n" +
                            ",0 BonProHasVal\n" +
                            ",BonProGraArtSec\n" +
                            ",isnull(BonProGraUni,0) BonProGraUni\n" +
                            ",isnull(BonProGraCaj,0) BonProGraCaj\n" +
                            ",isnull(BomProMaxMixPeri,0) BomProMaxMixPeri\n" +
                            ",isnull(BonProMaxCli,0) BonProMaxCli\n" +
                            ",isnull(BomProMixRefDis,0) BomProMixRefDis\n" +
                            ",isnull(BonProCanOpc,0) BonProCanOpc\n " +
                            ",isnull(BonProMovPor,'TRA') BonProMovPor " +
                            "from  BonificacionesProductoDet bpd left join  BonificacionesProducto bp on bpd.BonProSec=bp.BonProSec\n" +
                            "where  BONPROEST='A' AND BonProGraArtSec is not null\n" +
                            "and (SELECT COUNT(*) FROM BonificacionesProductoEscalaGr GG WHERE bpd.BonProSec=GG.BonProSec)=0 AND  BonProDesFec<=CONVERT(date, GETDATE()) and BonProHasFec>=CONVERT(date, GETDATE())\n" +
                            "union\n" +
                            " select bp.BonProSec,1 BonProSecLin,'GENERAL' bontipo,BonProGrupo,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProSubGruCod as varchar))) FROM BonificacionesProductoSubGrupo TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As SUBGRUPOS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProFamCod as varchar))) FROM BonificacionesProductoFamilia TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As FAMILIAS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProArtSec as varchar))) FROM BonificacionesProductoDet TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As ARTICULOS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProDetCanCod as varchar))) FROM BonificacionesProductoCanal TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As CANALES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProSubCanCod as varchar)))+'-'+rtrim(ltrim(cast(BonProSubCanSubCod as varchar))) FROM BonificacionesProductoSubCanal TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As SUBCANALES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProTamCantamCod as varchar))) FROM BonificacionesProductoTamano TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As TAMANOS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProSucCod as varchar))) FROM BonificacionesProducto1Sucursa TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As SUCURSALES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BonProVenCod as varchar))) FROM BonificacionesProductoVendedor TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As VENDEDORES,\n "+
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(BONPROALINEGCOD as varchar))) FROM BonificacionesProductoUnidades TABLA WHERE TABLA.BonProSec=bp.BonProSec FOR XML PATH('')),'XX'),' ','')+',' As UNIDADES\n"+
                            ",'' BonProArtSec\n" +
                            ",isnull(BonProVtaUni,0)\n" +
                            ",isnull(BonProVtaUni,0)\n" +
                            ",isnull(BonProVtaCaj,0)\n" +
                            ",isnull(BonProVtaCaj,0)\n" +
                            ",0 BonProDesVal\n" +
                            ",0 BonProHasVal\n" +
                            ",BonProGraArtSec\n" +
                            ",0 BonProGraUni\n" +
                            ",0 BonProGraCaj\n" +
                            ",isnull(BomProMaxMixPeri,0) BomProMaxMixPeri\n" +
                            ",isnull(BonProMaxCli,0) BonProMaxCli\n" +
                            ",isnull(BomProMixRefDis,0) BomProMixRefDis\n" +
                            ",isnull(BonProCanOpc,0) BonProCanOpc\n " +
                            ",isnull(BonProMovPor,'TRA') BonProMovPor " +
                            "from BonificacionesProducto bp \n" +
                            "WHERE  BONPROEST='A' AND (SELECT COUNT(*) FROM BonificacionesProductoDet B WHERE b.bonprosec=bp.bonprosec)=0 and BonProGraArtSec<>'' and BonProGraArtSec is not null\n" +
                            "and  BonProDesFec<=CONVERT(date, GETDATE()) and BonProHasFec>=CONVERT(date, GETDATE())\n" +
                            ") ll ) jj order by Row desc\n";

                     rsClientes = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from BonificacionesProducto");

                    while (rsClientes.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsClientes.getInt("Row"));
                            TotalFilas=rsClientes.getInt("Row");
                            String Filas=rsClientes.getString("Row").trim();
                        }


                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);
                        String BonProMovPor = rsClientes.getString("BonProMovPor");
                      if(rsClientes.getString("BonProMovPor").equalsIgnoreCase("")){
                          BonProMovPor = "TRA";
                      }


                        String InsertScript = "insert into BonificacionesProducto (BonProSec,BonProSecLin,bontipo,BonProGrupo,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,SUCURSALES,VENDEDORES,UNIDADES,BonProEscArtSec,BonProEscUniDes,BonProEscUniHas,BonProEscCajDes,BonProEscCajHas,BonProDesVal,BonProHasVal,BonProEscBonArtSec,BonProEscBonUni,BonProEscBonCaj,BomProMaxMixPeri,BonProMaxCli,BomProMixRefDis,BonProCanOpc,BonMovParTra) values ("
                                +rsClientes.getInt("BonProSec") + ","
                                +rsClientes.getInt("BonProSecLin") + ",'"
                                +rsClientes.getString("bontipo") + "','"
                                +rsClientes.getString("BonProGrupo") + "','"
                                +rsClientes.getString("SUBGRUPOS") + "','"
                                +rsClientes.getString("FAMILIAS") + "','"
                                +rsClientes.getString("ARTICULOS") + "','"
                                +rsClientes.getString("CANALES") + "','"
                                +rsClientes.getString("SUBCANALES") + "','"
                                +rsClientes.getString("TAMANOS") + "','"
                                +rsClientes.getString("SUCURSALES") + "','"
                                +rsClientes.getString("VENDEDORES") + "','"
                                +rsClientes.getString("UNIDADES") + "','"
                                +rsClientes.getString("BonProEscArtSec") + "',"
                                +rsClientes.getInt("BonProEscUniDes") + ","
                                +rsClientes.getInt("BonProEscUniHas") + ","
                                +rsClientes.getInt("BonProEscCajDes") + ","
                                +rsClientes.getInt("BonProEscCajHas") + ","
                                +rsClientes.getDouble("BonProDesVal") + ","
                                +rsClientes.getDouble("BonProHasVal") + ",'"
                                +rsClientes.getString("BonProEscBonArtSec") + "',"
                                +rsClientes.getInt("BonProEscBonUni") + ","
                                +rsClientes.getInt("BonProEscBonCaj") + ","
                                +rsClientes.getInt("BomProMaxMixPeri") + ","
                                +rsClientes.getInt("BonProMaxCli") + ","
                                +rsClientes.getInt("BomProMixRefDis") + ","
                                +rsClientes.getInt("BonProCanOpc") + ",'"
                                +BonProMovPor + "')";
                        try {
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;

                        }
                        try {

                            // vPrBar_Clientes.setMax(TotalFilas);
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Clientes.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                        }
                    }//while(rsClientes.next());

                    //   while (rsClientes.next()){

                    //   }
                } catch (Exception e) {
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsClientes != null) rsClientes.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

                ErroresGen+=Errores;
                Log.e("error133",String.valueOf(ErroresGen));

                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //Resultado[0] = new int[]{Insertados,Errores,TotalFilas};
                // int[] Resultado = //CargarDatos();
                CargarDatos();
            }
        }).start();


        return Resultado;
    }

    public int[] CargarBonificacionesProductoDet(){
        int hh=0;
        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntBonificadosdet);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkBonificadosdet);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrBonificadosdet);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_Bonificadosdet);
        vPrBar_Import.setProgress(0);
        //vPrBar_Import.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        //int TotalFilasCiudades=0;
        //int InsertadosCiudades=0;
        //int ErroresCiudades=0;
        //int VueltasCiudades=0;
        final int[] Resultado = new int[]{0, 0, 0};

        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                String vEmpresa=vGlobalVariables.getEmpresa();
                vEmpresa=vEmpresa.toUpperCase();

                Connection conn = null;
                Statement comm = null;
                ResultSet rsClientes  = null;

                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="";
                    vEmpresa=vEmpresa.toUpperCase();


                    Script  = "select * from(select  ROW_NUMBER() OVER(ORDER BY bpd.BonProSec ASC) AS Row,bpd.BonProSec,BonProArtSec,BomProDetDesUni,BonProDetDesCaj,isnull(BonProDetIndOpc,'N') BonProDetIndOpc,ArtEmb from BonificacionesProductoDet  bpd\n" +
                            "left join  BonificacionesProducto bp on bpd.BonProSec=bp.BonProSec left join articulos on bonproartsec=artsec              \n" +
                            "where BonProDesFec<=CONVERT(date, GETDATE()) and BonProHasFec>=CONVERT(date, GETDATE()) )Consulta order by Row desc";

                     rsClientes = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from BonificacionesProductoDet");

                    while (rsClientes.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsClientes.getInt("Row"));
                            TotalFilas=rsClientes.getInt("Row");
                            String Filas=rsClientes.getString("Row").trim();
                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);

                        String InsertScript = "insert into BonificacionesProductoDet (BonProSec,BonProArtSec,BomProDetDesUni,BonProDetDesCaj,BonProDetEmb,BonProDetIndOpc) values ("
                                +rsClientes.getInt("BonProSec") + ",'"
                                +rsClientes.getString("BonProArtSec") + "',"
                                +rsClientes.getInt("BomProDetDesUni") + ","
                                +rsClientes.getInt("BonProDetDesCaj") + ","
                                +rsClientes.getInt("ArtEmb") + ",'"
                                +rsClientes.getString("BonProDetIndOpc") + "')";
                        try {
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;

                        }
                        try {

                            // vPrBar_Clientes.setMax(TotalFilas);
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Clientes.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                        }
                    }//while(rsClientes.next());

                    //   while (rsClientes.next()){

                    //   }
                } catch (Exception e) {

                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsClientes != null) rsClientes.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

                ErroresGen+=Errores;
                Log.e("errorsds1",String.valueOf(ErroresGen));

                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //Resultado[0] = new int[]{Insertados,Errores,TotalFilas};
                // int[] Resultado = //CargarDatos();
                CargarDatos();
            }
        }).start();


        return Resultado;
    }
    public  int[] CargarClientesDcto(){
        int hh=0;
        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntClientesDcto);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkClientesDcto);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrClientesDcto);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_ClientesDcto);
        vPrBar_Import.setProgress(0);
        //vPrBar_Import.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        //int TotalFilasCiudades=0;
        //int InsertadosCiudades=0;
        //int ErroresCiudades=0;
        //int VueltasCiudades=0;
        final int[] Resultado = new int[]{0, 0, 0};

        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                String vEmpresa=vGlobalVariables.getEmpresa();
                vEmpresa=vEmpresa.toUpperCase();

                Connection conn = null;
                Statement comm = null;
                ResultSet rsClientes  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="";
                    vEmpresa=vEmpresa.toUpperCase();


                    Script  = "select * from(select  ROW_NUMBER() OVER(ORDER BY nitsec ASC) AS Row, nitsec,clisec,clidesinvgrucod,isnull(clidesdcto,0) clidesdcto,isnull(clidesfin,0) clidesfin from clientesnewgrupodesc) Consulta order by Row desc";

                     rsClientes = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from ClientesDcto");

                    while (rsClientes.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsClientes.getInt("Row"));
                            TotalFilas=rsClientes.getInt("Row");
                            String Filas=rsClientes.getString("Row").trim();
                        }
                        Vueltas+=1;
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);

                        String InsertScript = "insert into ClientesDcto (nitsec,"
                                + "clisec,"
                                + "CliDesInvGruCod,"
                                + "CLiDesDcto,"
                                + "CliDesFin) values ('"
                                + rsClientes.getString("nitsec").trim() + "',"
                                + rsClientes.getString("clisec").trim() + ",'"
                                + rsClientes.getString("clidesinvgrucod").trim() + "',"
                                +rsClientes.getString("clidesdcto").trim() + ","
                                +rsClientes.getString("clidesfin").trim() + ")";
                        try {
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;

                        }
                        try {

                            // vPrBar_Clientes.setMax(TotalFilas);
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Clientes.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                        }
                    }//while(rsClientes.next());

                    //   while (rsClientes.next()){

                    //   }
                } catch (Exception e) {
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsClientes != null) rsClientes.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }
                ErroresGen+=Errores;
                Log.e("e333rror1",String.valueOf(ErroresGen));

                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //Resultado[0] = new int[]{Insertados,Errores,TotalFilas};
                // int[] Resultado = //CargarDatos();
                CargarDatos();
            }
        }).start();


        return Resultado;
    }

    public  int[] CargarDescuentos(){
        int hh=0;
        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntDescuentos);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkDescuentos);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrDescuentos);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_Descuentos);
        vPrBar_Import.setProgress(0);
        //vPrBar_Import.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        //int TotalFilasCiudades=0;
        //int InsertadosCiudades=0;
        //int ErroresCiudades=0;
        //int VueltasCiudades=0;
        final int[] Resultado = new int[]{0, 0, 0};

        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                String vEmpresa=vGlobalVariables.getEmpresa();
                vEmpresa=vEmpresa.toUpperCase();

                Connection conn = null;
                Statement comm = null;
                ResultSet rsClientes  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="";
                    vEmpresa=vEmpresa.toUpperCase();


                    Script  = "select * from(select  ROW_NUMBER() OVER(ORDER BY tipodesc ASC) AS Row,\n" +
                            "* from (\n" +
                            "SELECT D.DESCSEC,'LIN' tipodesc,ISNULL(DESCAGRU,'ART') DESCAGRU,descgru,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuSubGruCod as varchar))) FROM DescuentosSUBGRUPOS TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As SUBGRUPOS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuFamCod as varchar))) FROM DescuentosFAMILIAS TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As FAMILIAS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(descuartsec as varchar))) FROM DescuentosARTICULOS TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As ARTICULOS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DscuCanCod as varchar))) FROM DescuentosCANALES TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As CANALES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuCanCod as varchar)))+'-'+rtrim(ltrim(cast(DescuCanSubCod as varchar))) FROM DescuentosSUBCANALES TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As SUBCANALES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuTamCanCod as varchar))) FROM DescuentosTAMANOS TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As TAMANOS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuAliNegCod as varchar))) FROM DescuentosUnidadNeg TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As UNIDADNEG,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuCiuCod as varchar))) FROM DescuentosCIUDADES TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As CIUDADES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(descubodsucccsec as varchar))) FROM DescuentosBodegas TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As BODEGAS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DESincNITSEC as varchar)))+'-'+rtrim(ltrim(cast(DESincclisec as varchar))) FROM DescuentosIncCliente TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As CLIENTES\t,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuNitSec as varchar)))+'-'+rtrim(ltrim(cast(DescuCliSec as varchar))) FROM DescuentosClientes TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As EXCLIENTES\t,\n" + //andres
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuVenCod as varchar))) FROM DescuentosVendedores TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As VENDEDORES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuSucCod as varchar))) FROM DescuentosSucursales TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As SUCURSALES\n" +
                            ",isnull(DescLinDesUni,0)DescLinDesUni \n" +
                            ",isnull(DescLinHasUni,0) DescLinHasUni\n" +
                            ",isnull(DescLin1CajMen1,'N') DescLin1CajMen1\n" +
                            ",isnull(DescLinDesCaj,0) DescLinDesCaj\n" +
                            ",isnull(DescLinHasCaj,0)DescLinHasCaj \n" +
                            ",isnull(DescLinDesVal,0) DescLinDesVal \n" +
                            ",isnull(DescLinHasVal,0) DescLinHasVal \n" +
                            ",isnull(DescLinPorDesLin,0) DescLinPorDesLin \n" +
                            ",DescLinDesFec\n" +
                            ",DescLinHasFec\n" +
                            ",'' as DescArtDes\n" +
                            ",isnull(DescLinSec,0) linea \n" +
                            ",isnull(d.DescPorMov,'TRA') DescPorMov \n" +
                            " FROM DescuentosdescLin DG left join descuentos d on d.descsec=DG.descsec where DescLinDesFec<=CONVERT(date, GETDATE()) and DescLinHasFec>=CONVERT(date, GETDATE()) and DESCEST='A' \n" +
                            " union\n" +
                            "SELECT D.DESCSEC,'PRO' tipodesc,ISNULL(DESCAGRU,'ART') DESCAGRU,descgru,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuSubGruCod as varchar))) FROM DescuentosSUBGRUPOS TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As SUBGRUPOS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuFamCod as varchar))) FROM DescuentosFAMILIAS TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As FAMILIAS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(descuartsec as varchar))) FROM DescuentosARTICULOS TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As ARTICULOS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DscuCanCod as varchar))) FROM DescuentosCANALES TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As CANALES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuCanCod as varchar)))+'-'+rtrim(ltrim(cast(DescuCanSubCod as varchar))) FROM DescuentosSUBCANALES TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As SUBCANALES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuTamCanCod as varchar))) FROM DescuentosTAMANOS TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As TAMANOS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuAliNegCod as varchar))) FROM DescuentosUnidadNeg TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As UNIDADNEG,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuCiuCod as varchar))) FROM DescuentosCIUDADES TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As CIUDADES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(descubodsucccsec as varchar))) FROM DescuentosBodegas TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As BODEGAS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DESincNITSEC as varchar)))+'-'+rtrim(ltrim(cast(DESincclisec as varchar))) FROM DescuentosIncCliente TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As CLIENTES\t,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuNitSec as varchar)))+'-'+rtrim(ltrim(cast(DescuCliSec as varchar))) FROM DescuentosClientes TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As EXCLIENTES\t,\n" + //andres
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuVenCod as varchar))) FROM DescuentosVendedores TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As VENDEDORES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuSucCod as varchar))) FROM DescuentosSucursales TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As SUCURSALES\n" +
                            ",isnull(DescProDesUni,0)\n" +
                            ",isnull(DescProHasUni,0)\n" +
                            ",isnull(DescPro1CajMen1,'N')\n" +
                            ",isnull(DescProDesCaj,0)\n" +
                            ",isnull(DescProHasCaj,0)\n" +
                            ",isnull(DescProDesVal,0)\n" +
                            ",isnull(DescProHasVal,0)\n" +
                            ",isnull(DescProPorDesLin,0)\n" +
                            ",DescProDesFec\n" +
                            ",DescProHasFec\n" +
                            ",'' as DescArtDes\n" +
                            ",isnull(DescProSec,0) linea \n" +
                            ",isnull(d.DescPorMov,'TRA') DescPorMov \n" +
                            " FROM DescuentosdescPRO DG left join descuentos d on d.descsec=DG.descsec where  DescProDesFec<=CONVERT(date, GETDATE()) and DescProHasFec>=CONVERT(date, GETDATE())  and DESCEST='A' \n" +
                            " union\n" +
                            " SELECT D.DESCSEC,'EMP' tipodesc,ISNULL(DESCAGRU,'ART') DESCAGRU,descgru,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuSubGruCod as varchar))) FROM DescuentosSUBGRUPOS TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As SUBGRUPOS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuFamCod as varchar))) FROM DescuentosFAMILIAS TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As FAMILIAS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(descuartsec as varchar))) FROM DescuentosARTICULOS TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As ARTICULOS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DscuCanCod as varchar))) FROM DescuentosCANALES TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As CANALES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuCanCod as varchar)))+'-'+rtrim(ltrim(cast(DescuCanSubCod as varchar))) FROM DescuentosSUBCANALES TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As SUBCANALES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuTamCanCod as varchar))) FROM DescuentosTAMANOS TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As TAMANOS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuAliNegCod as varchar))) FROM DescuentosUnidadNeg TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As UNIDADNEG,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuCiuCod as varchar))) FROM DescuentosCIUDADES TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As CIUDADES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(descubodsucccsec as varchar))) FROM DescuentosBodegas TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As BODEGAS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DESincNITSEC as varchar)))+'-'+rtrim(ltrim(cast(DESincclisec as varchar))) FROM DescuentosIncCliente TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As CLIENTES\t,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuNitSec as varchar)))+'-'+rtrim(ltrim(cast(DescuCliSec as varchar))) FROM DescuentosClientes TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As EXCLIENTES\t,\n" + //andres
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuVenCod as varchar))) FROM DescuentosVendedores TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As VENDEDORES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuSucCod as varchar))) FROM DescuentosSucursales TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As SUCURSALES\n" +
                            ",isnull(DescEmpDesUni,0)\n" +
                            ",isnull(DescEmpHasUni,0)\n" +
                            ",isnull(DescEmp1CajMen1,'N')\n" +
                            ",isnull(DescEmpDesCaj,0)\n" +
                            ",isnull(DescEmpHasCaj,0)\n" +
                            ",isnull(DescEmpDesVal,0)\n" +
                            ",isnull(DescEmpHasVal,0)\n" +
                            ",isnull(DescEmpEmp,0)\n" +
                            ",DescEmpDesFec\n" +
                            ",DescEmpHasFec\n" +
                            ",'' as DescArtDes\n" +
                            ",isnull(DescEmpSec,0) linea \n" +
                            ",isnull(d.DescPorMov,'TRA') DescPorMov \n" +
                            " FROM DescuentosdescEMP DG left join descuentos d on d.descsec=DG.descsec where  DescEmpDesFec<=CONVERT(date, GETDATE()) and DescEmpHasFec>=CONVERT(date, GETDATE())  and DESCEST='A' \n" +
                            " union\n" +
                            " SELECT D.DESCSEC,'ART' tipodesc,ISNULL(DESCAGRU,'ART') DESCAGRU,descgru,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuSubGruCod as varchar))) FROM DescuentosSUBGRUPOS TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As SUBGRUPOS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuFamCod as varchar))) FROM DescuentosFAMILIAS TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As FAMILIAS,\n" +
                            "','+isnull(DESCARTARTSEC,'XX')+',' As ARTICULOS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DscuCanCod as varchar))) FROM DescuentosCANALES TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As CANALES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuCanCod as varchar)))+'-'+rtrim(ltrim(cast(DescuCanSubCod as varchar))) FROM DescuentosSUBCANALES TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As SUBCANALES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuTamCanCod as varchar))) FROM DescuentosTAMANOS TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As TAMANOS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuAliNegCod as varchar))) FROM DescuentosUnidadNeg TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As UNIDADNEG,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuCiuCod as varchar))) FROM DescuentosCIUDADES TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As CIUDADES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(descubodsucccsec as varchar))) FROM DescuentosBodegas TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As BODEGAS,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DESincNITSEC as varchar)))+'-'+rtrim(ltrim(cast(DESincclisec as varchar))) FROM DescuentosIncCliente TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As CLIENTES\t,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuNitSec as varchar)))+'-'+rtrim(ltrim(cast(DescuCliSec as varchar))) FROM DescuentosClientes TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As EXCLIENTES\t,\n" + //andres
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuVenCod as varchar))) FROM DescuentosVendedores TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As VENDEDORES,\n" +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(DescuSucCod as varchar))) FROM DescuentosSucursales TABLA WHERE TABLA.DESCSEC=DG.DESCSEC FOR XML PATH('')),'XX'),' ','')+',' As SUCURSALES\n" +
                            ",isnull(DescArtDesUni,0)\n" +
                            ",isnull(DescArtHasUni,0)\n" +
                            ",'N'\n" +
                            ",isnull(DescArtDesCaj,0)\n" +
                            ",isnull(DescArtHasCaj,0)\n" +
                            ",0\n" +
                            ",0\n" +
                            ",isnull(DescArtPorDes,0)\n" +
                            ",DescArtDesFec\n" +
                            ",DescArtHasFec\n" +
                            ",isnull(DescArtDes,'')\n" +
                            ",isnull(DescArtLin,0) linea \n" +
                            ",isnull(d.DescPorMov,'TRA') DescPorMov \n" +
                            " FROM DESCUENTOscDescArticulo DG left join descuentos d on d.descsec=DG.descsec where DescArtDesFec<=CONVERT(date, GETDATE()) and DescArtHasFec>=CONVERT(date, GETDATE())  and DESCEST='A' \n" +
                            " ) kk where (unidadneg='XX,' OR unidadneg like '%,"+ vAliNegCod +",%')\n" +
                            " ) Consulta order by Row desc";
                      mensajeslargos("scriptdes",Script);
                     rsClientes = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from Descuentos");

                    while (rsClientes.next()){ if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsClientes.getInt("Row"));
                            TotalFilas=rsClientes.getInt("Row");
                            String Filas=rsClientes.getString("Row").trim();
                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);


                        String InsertScript = "insert into Descuentos (DESCSEC,tipodesc,DESCAGRU,descgru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,BODEGAS,UNIDADNEG,CIUDADES,CLIENTES,EXCLIENTES,VENDEDORES,SUCURSALES,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal, "
                                + "DescLinPorDesLin,DescArtDes,DesSecLin,DescPorMov) values ("
                                + rsClientes.getInt("DESCSEC") + ",'"
                                + rsClientes.getString("tipodesc").trim() + "','"
                                + rsClientes.getString("DESCAGRU").trim() + "','"
                                + rsClientes.getString("descgru").trim() + "','"
                                + rsClientes.getString("SUBGRUPOS").trim() + "','"
                                + rsClientes.getString("FAMILIAS").trim() + "','"
                                + rsClientes.getString("ARTICULOS").trim() + "','"
                                + rsClientes.getString("CANALES").trim() + "','"
                                + rsClientes.getString("SUBCANALES").trim() + "','"
                                + rsClientes.getString("TAMANOS").trim() + "','"
                                + rsClientes.getString("BODEGAS").trim() + "','"
                                + rsClientes.getString("UNIDADNEG").trim() + "','"
                                + rsClientes.getString("CIUDADES").trim() + "','"
                                + rsClientes.getString("CLIENTES").trim() + "','"
                                + rsClientes.getString("EXCLIENTES").trim() + "','"
                                + rsClientes.getString("VENDEDORES").trim() + "','"
                                + rsClientes.getString("SUCURSALES").trim() + "',"
                                + rsClientes.getInt("DescLinDesUni") + ","
                                + rsClientes.getInt("DescLinHasUni") + ",'"
                                + rsClientes.getString("DescLin1CajMen1").trim() + "',"
                                + rsClientes.getInt("DescLinDesCaj") + ","
                                + rsClientes.getInt("DescLinHasCaj")+ ","
                                + rsClientes.getDouble("DescLinDesVal") + ","
                                + rsClientes.getDouble("DescLinHasVal") + ","
                                + rsClientes.getDouble("DescLinPorDesLin") + ",'"
                                + rsClientes.getString("DescArtDes").trim() + "',"
                                + rsClientes.getInt("linea")+ ",'"
                                + rsClientes.getString("DescPorMov")+ "')";
                        try {
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;
                            Log.e("Errordsct ",ex.toString());

                        }
                        try {

                            // vPrBar_Clientes.setMax(TotalFilas);
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Clientes.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                            Log.e("Errordsct1 ",e.toString());

                        }
                    }//while(rsClientes.next());

                    //   while (rsClientes.next()){

                    //   }
                } catch (Exception e) {
                    Errores=1;
                    Log.e("Errordsct2 ",e.toString());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }
                finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsClientes != null) rsClientes.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }
                ErroresGen+=Errores;
                Log.e("error12221q",String.valueOf(ErroresGen));

                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //Resultado[0] = new int[]{Insertados,Errores,TotalFilas};
                // int[] Resultado = //CargarDatos();
                CargarDatos();
            }
        }).start();


        return Resultado;
    }
    public  int[] CargarControlVentas(){
        int hh=0;
        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_Cntcontrolventas);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_Okcontrolventas);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_Errcontrolventas);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_controlventas);
        vPrBar_Import.setProgress(0);
        //vPrBar_Import.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        //int TotalFilasCiudades=0;
        //int InsertadosCiudades=0;
        //int ErroresCiudades=0;
        //int VueltasCiudades=0;
        final int[] Resultado = new int[]{0, 0, 0};

        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                String vEmpresa=vGlobalVariables.getEmpresa();
                vEmpresa=vEmpresa.toUpperCase();

                Connection conn = null;
                Statement comm = null;
                ResultSet rsClientes  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="";
                    vEmpresa=vEmpresa.toUpperCase();


                    //Script  = "select * from(select  ROW_NUMBER() OVER(ORDER BY nitsec ASC) AS Row, nitsec,clisec,clidesinvgrucod,isnull(clidesdcto,0) clidesdcto,isnull(clidesfin,0) clidesfin from clientesnewgrupodesc) Consulta order by Row desc";
                    Script  = "select * from(select  ROW_NUMBER() OVER(ORDER BY ConVenGruSec ASC) AS Row, ConVenGruSec,ConVenGruGrup,"+
                    "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(ConVenGruCanCod as varchar))) "+
                            "FROM ControlVentasGruposCanal cana WHERE cana.ConVenGruSec=cg.ConVenGruSec  and cana.ConVenGruAno = cg.ConVenGruAno "+
                            "FOR XML PATH('')),'XX'),' ','')+',' As Canales, "+
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(ConVenGruSubCanSubCod as varchar))) "+
                            "FROM ControlVentasGruposSubcanal suca WHERE suca.ConVenGruSec=cg.ConVenGruSec  and suca.ConVenGruAno = cg.ConVenGruAno "+
                            "FOR XML PATH('')),'XX'),' ','')+',' As SubCanales, "+
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(ConVenGruTamCod as varchar))) "+
                            "FROM ControlVentasGruposTamano tama WHERE tama.ConVenGruSec=cg.ConVenGruSec and tama.ConVenGruAno = cg.ConVenGruAno "+
                            "FOR XML PATH('')),'XX'),' ','')+',' As Tamano, "+
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(ConVenGruCiuCod as varchar))) "+
                            "FROM ControlVentasGruposCiudad ciud WHERE ciud.ConVenGruSec=cg.ConVenGruSec and ciud.ConVenGruAno = cg.ConVenGruAno "+
                            "FOR XML PATH('')),'XX'),' ','')+',' As Ciudad, "+
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(ConVenGruUniNegCod as varchar))) "+
                            "FROM ControlVentasGruposUnidadNegoc unid WHERE unid.ConVenGruSec=cg.ConVenGruSec and unid.ConVenGruAno = cg.ConVenGruAno "+
                            "FOR XML PATH('')),'XX'),' ','')+',' As UnidadNegoc, "+
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(ConVenGruDetZonaCod as varchar))) "+
                            "FROM ControlVentasGruposZona zona WHERE zona.ConVenGruSec=cg.ConVenGruSec and zona.ConVenGruAno = cg.ConVenGruAno "+
                            "FOR XML PATH('')),'XX'),' ','')+',' As Vendedores, "+
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(ConVenGruDetSubGruCod as varchar))) "+
                            "FROM ControlVentasGruposSubgrupo subg WHERE subg.ConVenGruSec=cg.ConVenGruSec and subg.ConVenGruAno = cg.ConVenGruAno  "+
                            "FOR XML PATH('')),'XX'),' ','')+',' As Subgrupo, "+
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(ConVenGruDetFamCod as varchar))) "+
                            "FROM ControlVentasGruposFamilia fami WHERE fami.ConVenGruSec=cg.ConVenGruSec  and fami.ConVenGruAno = cg.ConVenGruAno "+
                            "FOR XML PATH('')),'XX'),' ','')+',' As Familia, "+
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(ConVenGruNitSec))+'-'+rtrim(ltrim(cast(ConVenGruCliSec as varchar))) "+
                            "FROM ControlVentasGruposCliente clie WHERE clie.ConVenGruSec=cg.ConVenGruSec  and clie.ConVenGruAno = cg.ConVenGruAno "+
                            "FOR XML PATH('')),'XX'),' ','')+',' As Cliente, "+
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(ConVenGruDetArtSec as varchar))) "+
                            "FROM ControlVentasGruposArticulo arti WHERE arti.ConVenGruSec=cg.ConVenGruSec and arti.ConVenGruAno = cg.ConVenGruAno "+
                            "FOR XML PATH('')),'XX'),' ','')+',' As Articulos, "+
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(Convengrusuccod as varchar))) "+
                            "FROM ControlVentasGruposSucursal sucu WHERE sucu.ConVenGruSec=cg.ConVenGruSec and sucu.ConVenGruAno = cg.ConVenGruAno "+
                            "FOR XML PATH('')),'XX'),' ','')+',' As Sucursales "+
                            "from ControlVentasGrupos cg where  convengruest='A') jj order by Row desc";

                     rsClientes = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from ControlVentas");

                    while (rsClientes.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsClientes.getInt("Row"));
                            TotalFilas=rsClientes.getInt("Row");
                            String Filas=rsClientes.getString("Row").trim();
                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);

                        String InsertScript = "insert into ControlVentas (ConVenGruSec,"
                                + "ConVenGruGrup,"
                                + "Canales,"
                                + "SubCanales,"
                                + "Tamano,"
                                + "Ciudad,"
                                + "UnidadNegoc,"
                                + "Vendedores,"
                                + "Subgrupo,"
                                + "Familia,"
                                + "Cliente,"
                                + "Articulos,"
                                + "Sucursales) values ("
                                + rsClientes.getString("ConVenGruSec").trim() + ",'"
                                + rsClientes.getString("ConVenGruGrup").trim() + "','"
                                + rsClientes.getString("Canales").trim() + "','"
                                + rsClientes.getString("SubCanales").trim() + "','"
                                + rsClientes.getString("Tamano").trim() + "','"
                                + rsClientes.getString("Ciudad").trim() + "','"
                                + rsClientes.getString("UnidadNegoc").trim() + "','"
                                + rsClientes.getString("Vendedores").trim() + "','"
                                + rsClientes.getString("Subgrupo").trim() + "','"
                                + rsClientes.getString("Familia").trim() + "','"
                                + rsClientes.getString("Cliente").trim() + "','"
                                + rsClientes.getString("Articulos").trim() + "','"
                                + rsClientes.getString("Sucursales").trim() + "')";
                        try {
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;

                        }
                        try {

                            // vPrBar_Clientes.setMax(TotalFilas);
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Clientes.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                        }
                    }//while(rsClientes.next());

                    //   while (rsClientes.next()){

                    //   }
                } catch (Exception e) {
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsClientes != null) rsClientes.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

                ErroresGen+=Errores;
                Log.e("error1ssss",String.valueOf(ErroresGen));

                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //Resultado[0] = new int[]{Insertados,Errores,TotalFilas};
                // int[] Resultado = //CargarDatos();
                CargarDatos();
            }
        }).start();


        return Resultado;
    }

    public int[] CargarListasProGrupo(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntListasProGrupo);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkListasProGrupo);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrListasProGrupo);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_ListasProGrupo);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();

                    String Script="";
                    if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") ) {
                        Script= "select * from(select ROW_NUMBER() OVER(ORDER BY NitSec ASC) AS Row,NitSec,CliSec,Grupo,Subgrupo,Lista from ( " +
                                "select NitSec,CliSec,clilissubinvgrucod Grupo,CliLisSubInvSubGruCod Subgrupo,CliLisSubLisPreCod Lista from ClientesNewLisSubGruCod" +
                                " union " +
                                "select NitSec,CliSec,CliLisInvGruCod Grupo,'0' Subgrupo,CliLisLisPreCod Lista from ClientesNewLisGru " +
                                " union " +
                                "select  cv.NitSec, cv.CliSec,'301' Grupo,'0' Subgrupo,CiuLisPreCodLiq Lista from clientesvendedores cv " +
                                "left join Clientes c on c.nitsec=cv.nitsec and c.clisec=cv.clisec " +
                                "left join ciudad ci on c.cliciucod=ci.ciucod where CiuLisPreCodLiq is not null and cv.vencod= '" + vUsuario + "'  and CiuLisPreCodLiq<>CiuLisPreCod" +
                                " union " +
                                "select  cv.NitSec, cv.CliSec,'315' Grupo,'0' Subgrupo,CiuLisPreCodLiq Lista from clientesvendedores cv " +
                                "left join Clientes c on c.nitsec=cv.nitsec and c.clisec=cv.clisec " +
                                "left join ciudad ci on c.cliciucod=ci.ciucod where CiuLisPreCodLiq is not null and cv.vencod= '" + vUsuario + "'  and CiuLisPreCodLiq<>CiuLisPreCod" +
                                " union " +
                                "select  cv.NitSec, cv.CliSec,'312' Grupo,'0' Subgrupo,CiuLisPreCodLiq Lista from clientesvendedores cv " +
                                "left join Clientes c on c.nitsec=cv.nitsec and c.clisec=cv.clisec " +
                                "left join ciudad ci on c.cliciucod=ci.ciucod where CiuLisPreCodLiq is not null and cv.vencod= '" + vUsuario + "'  and CiuLisPreCodLiq<>CiuLisPreCod" +
                                ") tablauni) jj order by Row desc ";
                    }else {
                        Script= "select * from(select ROW_NUMBER() OVER(ORDER BY NitSec ASC) AS Row,NitSec,CliSec,Grupo,Subgrupo,Lista from ( " +
                                "select NitSec,CliSec,clilissubinvgrucod Grupo,CliLisSubInvSubGruCod Subgrupo,CliLisSubLisPreCod Lista from ClientesNewLisSubGruCod" +
                                " union " +
                                "select NitSec,CliSec,CliLisInvGruCod Grupo,'0' Subgrupo,CliLisLisPreCod Lista from ClientesNewLisGru " +
                                " union " +
                                "select  cv.NitSec, cv.CliSec,'300' Grupo,'0' Subgrupo,CiuLisPreCodLiq Lista from clientesvendedores cv " +
                                "left join Clientes c on c.nitsec=cv.nitsec and c.clisec=cv.clisec " +
                                "left join ciudad ci on c.cliciucod=ci.ciucod where CiuLisPreCodLiq is not null and cv.vencod= '" + vUsuario + "'  and CiuLisPreCodLiq<>CiuLisPreCod" +
                                " union " +
                                "select  cv.NitSec, cv.CliSec,'310' Grupo,'0' Subgrupo,CiuLisPreCodLiq Lista from clientesvendedores cv " +
                                "left join Clientes c on c.nitsec=cv.nitsec and c.clisec=cv.clisec " +
                                "left join ciudad ci on c.cliciucod=ci.ciucod where CiuLisPreCodLiq is not null and cv.vencod= '" + vUsuario + "'  and CiuLisPreCodLiq<>CiuLisPreCod" +
                                " union " +
                                "select  cv.NitSec, cv.CliSec,'400' Grupo,'0' Subgrupo,CiuLisPreCodLiq Lista from clientesvendedores cv " +
                                "left join Clientes c on c.nitsec=cv.nitsec and c.clisec=cv.clisec " +
                                "left join ciudad ci on c.cliciucod=ci.ciucod where CiuLisPreCodLiq is not null and cv.vencod= '" + vUsuario + "'  and CiuLisPreCodLiq<>CiuLisPreCod" +
                                " union " +
                                "select  cv.NitSec, cv.CliSec,'500' Grupo,'0' Subgrupo,CiuLisPreCodLiq Lista from clientesvendedores cv " +
                                "left join Clientes c on c.nitsec=cv.nitsec and c.clisec=cv.clisec " +
                                "left join ciudad ci on c.cliciucod=ci.ciucod where CiuLisPreCodLiq is not null and cv.vencod= '" + vUsuario + "'  and CiuLisPreCodLiq<>CiuLisPreCod" +
                                ") tablauni) jj order by Row desc ";
                    }
                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from ListaPorGrupoSubgrupo");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);



                        //vPrBar_Ciudades.

                        String InsertScript = "insert into ListaPorGrupoSubgrupo(NitSec,CliSec,InvGruCod,InvSubGruCod,LisPreCod) values ('"
                                + rsImport.getString("NitSec").trim() + "',"
                                + rsImport.getString("CliSec").trim() + ",'"
                                + rsImport.getString("Grupo").trim() + "','"
                                + rsImport.getString("Subgrupo").trim() + "',"
                                + rsImport.getString("Lista").trim() + ")" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;

                        }
                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                        }
//*/
                    }
                } catch (Exception e) {
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }


                ErroresGen+=Errores;
                Log.e("error1zzzzs",String.valueOf(ErroresGen));

                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        }).start();



        return Resultado;
    }

    public  int[] CargarCiudades(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntCiudades);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkCiudades);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrCiudades);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_Ciudades);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="select * from(select ROW_NUMBER() OVER(ORDER BY CiuCod ASC) AS Row,CiuCod,CiuNom from ciudad) jj order by Row desc";
                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from ciudades");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);



                        //vPrBar_Ciudades.

                        String InsertScript = "insert into Ciudades (CiuCod,CiuNom) values ('"
                                + rsImport.getString("CiuCod").trim() + "','"
                                + rsImport.getString("CiuNom").trim() + "')" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;

                        }
                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                        }
//*/
                    }
                } catch (Exception e) {
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }
                finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }
                ErroresGen+=Errores;
                Log.e("erroqqqq1",String.valueOf(ErroresGen));

                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        }).start();



        return Resultado;
    }
    public  int[] CargarVersiones(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntVersiones);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkVersiones);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrVersiones);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_Versiones);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="select * from(select ROW_NUMBER() OVER(ORDER BY VerMovSec ASC) AS Row,VerMovSec,VerMovVersion,VerMovEstado from VersionMovil) jj order by Row desc";
                     rsImport = comm.executeQuery(Script);
                        BdSql.execSQL("Delete from VersionMovil");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);

                        String InsertScript = "insert into VersionMovil (VerMovSec,VerMovVersion,VerMovEstado) values ("
                                + rsImport.getString("VerMovSec").trim() + ",'"
                                + rsImport.getString("VerMovVersion").trim() + "','"
                                + rsImport.getString("VerMovEstado").trim() + "')" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;

                        }
                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                        }
//*/
                    }
                } catch (Exception e) {
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }


                ErroresGen+=Errores;
                Log.e("errornnnn1",String.valueOf(ErroresGen));

                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        }).start();



        return Resultado;
    }

    public  int[] CargarListasPrecios(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntListasPrecios);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkListasPrecios);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrListasPrecios);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_ListasPrecios);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {



                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="";
                    if (vEmpresa.trim().equalsIgnoreCase("MENTAHAIR") || vEmpresa.trim().equalsIgnoreCase("MENTAHAIRCOT")) {
                         Script="select * from(select ROW_NUMBER() OVER(ORDER BY LisPreCod ASC) AS Row," +
                                "case when LisPreCod=9 then 1 when LisPreCod=10 then 2 when LisPreCod=11 then 3 when LisPreCod=12 then 4 when LisPreCod=13 then 5 when LisPreCod=1 then 6 when LisPreCod=2 then 7 when LisPreCod=3 then 8 when LisPreCod=4 then 9  when LisPreCod=15 then 15 else 1 end LisPreCod,LisPreNom" +
                                " from ListasPrecios) jj order by Row desc";
                    }else{
                          Script="select * from(select ROW_NUMBER() OVER(ORDER BY LisPreCod ASC) AS Row,LisPreCod,LisPreNom from ListasPrecios) jj order by Row desc";
                    }
                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from ListasPrecios");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);

                        String InsertScript = "insert into ListasPrecios (LisPreCod,LisPreNom) values ("
                                + rsImport.getString("LisPreCod").trim() + ",'"
                                + rsImport.getString("LisPreNom").trim() + "')" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;

                        }
                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                        }
//*/
                    }
                } catch (Exception e) {
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }
                finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }
                ErroresGen+=Errores;
                Log.e("errqqqwqwor1",String.valueOf(ErroresGen));

                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        }).start();



        return Resultado;
    }
    public  int[] CargarBarrios(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntBarrios);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkBarrios);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrBarrios);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_Barrios);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="";
                    if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") || vEmpresa.trim().equalsIgnoreCase("IBANEZPRU")|| vEmpresa.trim().equalsIgnoreCase("SURTIMARCAS")) {
                        Script="select * from(select ROW_NUMBER() OVER(ORDER BY BarCod ASC) AS Row,BarCod,BarNom," +
                                "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(BarCiuCod )) FROM BarrioCiudad TABLA WHERE TABLA.BarCod=Barrio.BarCod FOR XML PATH('')),'XX'),' ','')+',' As Ciudades" +
                                " from Barrio ) jj order by Row desc";
                    }else{
                        Script="select * from(select ROW_NUMBER() OVER(ORDER BY BarCod ASC) AS Row,BarCod,BarNom," +
                                "'XX' Ciudades" +
                                " from Barrio ) jj order by Row desc";
                    }

                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from Barrios");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);


//*/
                        //vPrBar_Ciudades.

                        String InsertScript = "insert into Barrios (BarCod,BarNom,Ciudades) values ('"
                                + rsImport.getString("BarCod").trim() + "','"
                                + rsImport.getString("BarNom").trim() + "','"
                                + rsImport.getString("Ciudades").trim() + "')" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                                //   Errores+=1;
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;

                        }
                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                        }
                    }
                } catch (Exception e) {
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};
                CargarDatos();

            }
        }).start();


        return Resultado;
    }

    public  int[] CargarClaseXPerf(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntClaseXPerf);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkClaseXPerf);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrClaseXPerf);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_ClaseXPerf);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="select * from(select ROW_NUMBER() OVER(ORDER BY perclicod ASC) AS Row,PerCliCod,PerClarArtCod PerClaArtCod,PerCliDetDes1 from PerfilClientesClase) jj order by Row desc";
                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from PerfilClientesClase");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);


//*/
                        //vPrBar_Ciudades.

                        String InsertScript = "insert into PerfilClientesClase (PerCliCod,ClaArtCod,PerCliDetDes1) values ("
                                + rsImport.getString("PerCliCod").trim() + ","
                                + rsImport.getString("PerClaArtCod").trim() + ","
                                + rsImport.getString("PerCliDetDes1").trim() + ")";
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                                //   Errores+=1;
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;

                        }
                    }
                    try {
                        final int finalTotalFilas = TotalFilas;
                        final int finalInsertados = Insertados;
                        final int finalErrores = Errores;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                // vPrBar_Ciudades.setMax(TotalFilas);
                                // vPrBar_Ciudades.setProgress(Vueltas);
                            }
                        });
                    }catch (Exception e){
                        int hh=0;
                    }


                } catch (Exception e) {
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }
                finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }



                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};
                ErroresGen+=Errores;
                Log.e("errdafasfdvor1",String.valueOf(ErroresGen));

                CargarDatos();

            }
        }).start();


        return Resultado;
    }

    public  int[] CargarMovCauPed(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntCausal);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkCausal);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrCausal);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_Causal);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="select * from(select ROW_NUMBER() OVER(ORDER BY MovCauSec ASC) AS Row,MovCauSec,MovCauNom,ISNULL(MovConPed,'S') MovConPed,ISNULL(MovPidFot,'N') MovPidFot,ISNULL(MovPidObs,'S') MovPidObs  from MovCauPed) Consulta order by Row desc";
                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from MovCauPed");
    int banentro = 0;
                    while (rsImport.next()){
                        banentro = 1;
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);

                        String InsertScript ="";
                             InsertScript = "insert into MovCauPed (MovCauSec,MovCauNom,MovConPed,MovPidFot,MovPidObs) values ("
                                    + rsImport.getString("MovCauSec").trim() + ",'"
                                    + rsImport.getString("MovCauNom").trim() + "','"
                                    + rsImport.getString("MovConPed").trim() + "','"
                                    + rsImport.getString("MovPidFot").trim() + "','"
                                    + rsImport.getString("MovPidObs").trim() + "')";





                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                                //   Errores+=1;
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;

                        }

                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                        }
                    }

                    if(banentro == 0){
                        String InsertScript = "insert into MovCauPed (MovCauSec,MovCauNom,MovConPed,MovPidFot,MovPidObs) values ( 1 ,'1. Iniciar visita','N','N','N')";
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                                //   Errores+=1;
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;

                        }
                    }

                } catch (Exception e) {
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};
                ErroresGen+=Errores;
                Log.e("error1jjnnb",String.valueOf(ErroresGen));

                CargarDatos();

            }
        }).start();


        return Resultado;
    }

    public  int[] CargarArticulos(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntArticulos);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkArticulos);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrArticulos);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_Articulos);
        vPrBar_Import.setProgress(0);



        int[] Resultado= new int[]{1,3,5};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;

                try {
                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                    String vUsuario=vGlobalVariables.getUsuario();
                    String vEmpresa=vGlobalVariables.getEmpresa();
                    vEmpresa=vEmpresa.toUpperCase();
                    String Script="";

                    if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") || vEmpresa.trim().equalsIgnoreCase("IBANEZPRU") ) {
                        //isnull((select SUM(((karcaj*KARARTEMB)+karuni)*(case when (karnat='+') then 1 else -1 end)) saldo "+
                        // "from Kardex  k inner join Factura f on f.FacSec=k.facsec where facest='A' and k.ArtSec=a.ArtSec AND  SUBBODSUCCCSEC in(select bodsucccsec from bodegasucursalcc where succod=1)),0) Exist " +

                        Script = "select * from (select ROW_NUMBER() OVER(ORDER BY a.ArtSec ASC) AS Row,a.ArtSec,ArtCod,replace(ArtNom,'''','')+' '+isnull((select top 1 preartnom from artpre ap left join presentacionarticulos p on ap.preartcod=p.preartcod where preartfaccon='S' and ap.artsec=a.artsec ),'') ArtNom,isnull(ArtMedNomCom,'') ArtMedNomCom,g.InvGruCod,g.InvGruNom," +
                                "s.InvSubGruCod,s.InvSubGruNom,a.InvFamCod,InvFamNom,ParConIva,0 PrePreFijCosPro," +
                                "isnull((p1.PrePreFijVal),0) precio1," +
                                "isnull((p2.PrePreFijVal),0) precio2," +
                                "isnull((p3.PrePreFijVal),0) precio3," +
                                "isnull((p4.PrePreFijVal),0) precio4," +
                                "isnull((p5.PrePreFijVal),0) precio5," +
                                "isnull((p6.PrePreFijVal),0) precio6," +
                                "isnull((p7.PrePreFijVal),0) precio7," +
                                "isnull((p8.PrePreFijVal),0) precio8," +
                                "isnull((p9.PrePreFijVal),0) precio9," +
                                "0 precio10," +
                                "0 precio11," +
                                "0 precio12," +
                                "0 precio13," +
                                "0 precio14," +
                                "0 precio15," +
                                "0 precio16," +
                                "0 precio17," +
                                "0 precio18," +
                                "0 precio19," +
                                "0 precio20," +
                                "0 precio21," +
                                "0 precio22," +
                                "0 precio23," +
                                "0 precio24," +
                                "0 precio25," +
                                "0 precio26," +
                                "0 precio27," +
                                "0 precio28," +
                                "0 precio29," +
                                "0 precio30," +
                                "isnull((p1.PrePorVal),0) PrePorVal1," +
                                "isnull((p2.PrePorVal),0) PrePorVal2," +
                                "isnull((p3.PrePorVal),0) PrePorVal3," +
                                "isnull((p4.PrePorVal),0) PrePorVal4," +
                                "isnull((p5.PrePorVal),0) PrePorVal5," +
                                "isnull((p6.PrePorVal),0) PrePorVal6," +
                                "isnull((p7.PrePorVal),0) PrePorVal7," +
                                "isnull((p8.PrePorVal),0) PrePorVal8," +
                                "isnull((p9.PrePorVal),0) PrePorVal9," +
                                "isnull((select top 1 movparlindes from MovParLineaGrupos ff left join movparlinea ml on ml.movparlinsec=ff.movparlinsec where movparlinencinvgrucod=s.invgrucod and MOVPARLINFECINI<=CONVERT(date, GETDATE()) and MOVPARLINFECFIN>=CONVERT(date, GETDATE()) ),0) desc1," +   //
                                "isnull((select top 1 MovParArtDetDesc from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc2," +
                                "isnull((select top 1 MovParArtDetDesc2 from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc2<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc5," +
                                "isnull((select top 1 MovParArtFecMod from MovParArtArticulos where MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) ),'') MovParArtFecMod" +
                                ",0 desc3,0 desc4,0 desc6,1000 Exist2 " +
                                ",(isnull((select sum(artexiact) from articulosexi ae where ae.artsec=a.artsec and artbodcod in(select alinegbodsucccsec from carunidadesalinegbodsuc where alinegcod="+vAliNegCod+")),0) - (select isnull(sum(artuniminapa),0) from articulosunidad ae left join carunidades c on ae.Artalinegcod=c.alinegcod where  CarUniSucCod="+vSucCod+" and ae.artsec=a.artsec and AliNegTat='S' and (select AliNegTat from carunidades n where n.alinegcod="+vAliNegCod+")<>'S' )) Exist " +
                                ",isnull((select sum(artexiact) from articulosexi ae where ae.artsec=a.artsec " +
                                "and artbodcod=(select bodsucccsec from bodegasucursalcc where  BodProVen='S' and succod="+vSucCod+")),0) ExistFec " +
                                ",isnull(c.ClaArtCod,0) ClaArtCod,isnull(c.ClaArtNom,'') ClaArtNom,isnull(ArtValImp,0) ArtValImp,ArtEmb,isnull(ArtIndMpm,'') ArtIndMpm  " +
                                ",0 ArtRen,0 ArtLim, g.InvGruDifCheck, s.InvSubDifCheck, f.InvFamDifCheck,ArtCodBar " +
                                "from articulos a " +
                                "left join inventariofamilia f WITH (NOLOCK)  on a.invfamcod=f.invfamcod " +
                                "left join inventariosubgrupo s WITH (NOLOCK) on f.invsubgrucod= s.invsubgrucod " +
                                "left join inventariogrupo g WITH (NOLOCK) on g.invgrucod=s.invgrucod " +
                                "left join clasearticulo c WITH (NOLOCK) on c.claartcod=a.claartcod " +
                                "left join parametrocontable p WITH (NOLOCK) on p.parconcod=a.parconcod " +
                                "left join artpre pp WITH (NOLOCK) on a.artsec=pp.artsec and PreArtFacCon='S' " +
                                " left join PreciosDetalle p1 WITH (NOLOCK) on p1.ArtSec=a.ArtSec and p1.succod=1 and  p1.LisPreCod=1 and p1.PreEst='A' and P1.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p2 WITH (NOLOCK) on p2.ArtSec=a.ArtSec and p2.succod=1 and  p2.LisPreCod=2 and p2.PreEst='A' and P2.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p3 WITH (NOLOCK) on p3.ArtSec=a.ArtSec and p3.succod=1 and  p3.LisPreCod=3 and p3.PreEst='A' and P3.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p4 WITH (NOLOCK) on p4.ArtSec=a.ArtSec and p4.succod=1 and  p4.LisPreCod=4 and p4.PreEst='A' and P4.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p5 WITH (NOLOCK) on p5.ArtSec=a.ArtSec and p5.succod=1 and  p5.LisPreCod=5 and p5.PreEst='A' and P5.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p6 WITH (NOLOCK) on p6.ArtSec=a.ArtSec and p6.succod=1 and  p6.LisPreCod=6 and p6.PreEst='A' and P6.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p7 WITH (NOLOCK) on p7.ArtSec=a.ArtSec and p7.succod=1 and  p7.LisPreCod=7 and p7.PreEst='A' and P7.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p8 WITH (NOLOCK) on p8.ArtSec=a.ArtSec and p8.succod=1 and  p8.LisPreCod=8 and p8.PreEst='A' and P8.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p9 WITH (NOLOCK) on p9.ArtSec=a.ArtSec and p9.succod=1 and  p9.LisPreCod=9 and p9.PreEst='A' and P9.PreArtCod=PP.PreArtCod" +
                                " WHERE (a.artdes<>'S' or (select sum(artexiact) from articulosexi ae WITH (NOLOCK) where ae.artsec=a.artsec and artbodcod in(select alinegbodsucccsec from carunidadesalinegbodsuc WITH (NOLOCK) where alinegcod="+vAliNegCod+")) <> 0 ) " +
                                " and " +
                                "" +
                                "" +
                                "" +
                                "((" +
                                "" +
                                " s.invgrucod in(select invgrucod from VendedoresGruposInventarios WITH (NOLOCK) where rtrim(VenCod)='" + vUsuario + "')  " +
                                "  ) or a.artsec in(select subvenartsec from vendedoressubvenart WITH (NOLOCK) where rtrim(VenCod)='" + vUsuario + "' and venartdes<>'S')) AND a.artsec NOT in(select subvenartsec from vendedoressubvenart WITH (NOLOCK) where rtrim(VenCod)='" + vUsuario + "' and venartdes='S')) Consulta order by Row desc";

                        Log.e("Sql art",Script);


                    }else if (vEmpresa.trim().equalsIgnoreCase("SURTIMARCAS")) {
                        //isnull((select SUM(((karcaj*KARARTEMB)+karuni)*(case when (karnat='+') then 1 else -1 end)) saldo "+
                        // "from Kardex  k inner join Factura f on f.FacSec=k.facsec where facest='A' and k.ArtSec=a.ArtSec AND  SUBBODSUCCCSEC in(select bodsucccsec from bodegasucursalcc where succod=1)),0) Exist " +

                        Script = "select * from (select ROW_NUMBER() OVER(ORDER BY a.ArtSec ASC) AS Row,a.ArtSec,ArtCod,replace(ArtNom,'''','')+' '+isnull((select top 1 preartnom from artpre ap left join presentacionarticulos p on ap.preartcod=p.preartcod where preartfaccon='S' and ap.artsec=a.artsec ),'') ArtNom,isnull(ArtMedNomCom,'') ArtMedNomCom,g.InvGruCod,g.InvGruNom," +
                                "s.InvSubGruCod,s.InvSubGruNom,a.InvFamCod,InvFamNom,ParConIva,0 PrePreFijCosPro," +
                                "isnull((p1.PrePreFijVal),0) precio1," +
                                "isnull((p2.PrePreFijVal),0) precio2," +
                                "isnull((p3.PrePreFijVal),0) precio3," +
                                "isnull((p4.PrePreFijVal),0) precio4," +
                                "isnull((p5.PrePreFijVal),0) precio5," +
                                "isnull((p6.PrePreFijVal),0) precio6," +
                                "isnull((p7.PrePreFijVal),0) precio7," +
                                "isnull((p8.PrePreFijVal),0) precio8," +
                                "isnull((p9.PrePreFijVal),0) precio9," +
                                "0 precio10," +
                                "0 precio11," +
                                "0 precio12," +
                                "0 precio13," +
                                "0 precio14," +
                                "0 precio15," +
                                "0 precio16," +
                                "0 precio17," +
                                "0 precio18," +
                                "0 precio19," +
                                "0 precio20," +
                                "0 precio21," +
                                "0 precio22," +
                                "0 precio23," +
                                "0 precio24," +
                                "0 precio25," +
                                "0 precio26," +
                                "0 precio27," +
                                "0 precio28," +
                                "0 precio29," +
                                "0 precio30," +
                                "isnull((p1.PrePorVal),0) PrePorVal1," +
                                "isnull((p2.PrePorVal),0) PrePorVal2," +
                                "isnull((p3.PrePorVal),0) PrePorVal3," +
                                "isnull((p4.PrePorVal),0) PrePorVal4," +
                                "isnull((p5.PrePorVal),0) PrePorVal5," +
                                "isnull((p6.PrePorVal),0) PrePorVal6," +
                                "isnull((p7.PrePorVal),0) PrePorVal7," +
                                "isnull((p8.PrePorVal),0) PrePorVal8," +
                                "isnull((p9.PrePorVal),0) PrePorVal9," +
                                "isnull((select top 1 movparlindes from MovParLineaGrupos ff left join movparlinea ml on ml.movparlinsec=ff.movparlinsec where movparlinencinvgrucod=s.invgrucod and MOVPARLINDETFECINI<=CONVERT(date, GETDATE()) and MOVPARLINDETFECFIN>=CONVERT(date, GETDATE()) ),0) desc1," +   //
                                "isnull((select top 1 MovParArtDetDesc from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc2," +
                                "isnull((select top 1 MovParArtDetDesc2 from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc2<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc5," +
                                "isnull((select top 1 MovParArtFecMod from MovParArtArticulos where MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) ),'') MovParArtFecMod" +
                                ",0 desc3,0 desc4,0 desc6,1000 Exist2 " +
                                ",isnull((select sum(artexiact) from articulosexi ae where ae.artsec=a.artsec and artbodcod in(select alinegbodsucccsec from carunidadesalinegbodsuc where alinegcod="+vAliNegCod+")),0) Exist " +
                                ",0 ExistFec " +
                                ",isnull(c.ClaArtCod,0) ClaArtCod,isnull(c.ClaArtNom,'') ClaArtNom,isnull(ArtValImp,0) ArtValImp,ArtEmb,isnull(ArtIndMpm,'N') ArtIndMpm  " +
                                ",0 ArtRen,0 ArtLim,'N' InvGruDifCheck,'N' InvSubDifCheck,'N' InvFamDifCheck,a.ArtCodBar " +
                                "from articulos a " +
                                "left join inventariofamilia f on a.invfamcod=f.invfamcod " +
                                "left join inventariosubgrupo s on f.invsubgrucod= s.invsubgrucod " +
                                "left join inventariogrupo g on g.invgrucod=s.invgrucod " +
                                "left join clasearticulo c on c.claartcod=a.claartcod " +
                                "left join parametrocontable p on p.parconcod=a.parconcod " +
                                "left join artpre pp on a.artsec=pp.artsec and PreArtFacCon='S' " +
                                " left join PreciosDetalle p1 on p1.ArtSec=a.ArtSec and p1.succod=1 and  p1.LisPreCod=1 and p1.PreEst='A' and P1.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p2 on p2.ArtSec=a.ArtSec and p2.succod=1 and  p2.LisPreCod=2 and p2.PreEst='A' and P2.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p3 on p3.ArtSec=a.ArtSec and p3.succod=1 and  p3.LisPreCod=3 and p3.PreEst='A' and P3.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p4 on p4.ArtSec=a.ArtSec and p4.succod=1 and  p4.LisPreCod=4 and p4.PreEst='A' and P4.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p5 on p5.ArtSec=a.ArtSec and p5.succod=1 and  p5.LisPreCod=15 and p5.PreEst='A' and P5.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p6 on p6.ArtSec=a.ArtSec and p6.succod=1 and  p6.LisPreCod=16 and p6.PreEst='A' and P6.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p7 on p7.ArtSec=a.ArtSec and p7.succod=1 and  p7.LisPreCod=17 and p7.PreEst='A' and P7.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p8 on p8.ArtSec=a.ArtSec and p8.succod=1 and  p8.LisPreCod=18 and p8.PreEst='A' and P8.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p9 on p9.ArtSec=a.ArtSec and p9.succod=1 and  p9.LisPreCod=19 and p9.PreEst='A' and P9.PreArtCod=PP.PreArtCod" +
                                " WHERE a.artdes<>'S' " +
                                " and ( s.invgrucod in(select invgrucod from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "') or " +
                                " (select count(*) from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "')=0 ) or a.artsec in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes<>'S') AND a.artsec NOT in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes='S')) Consulta order by Row desc";
                        Log.e("SQLSURTI",Script);
                    } else if (vEmpresa.trim().equalsIgnoreCase("GELVEZ") || vEmpresa.trim().equalsIgnoreCase("GELVEZREM")) {
                        //isnull((select SUM(((karcaj*KARARTEMB)+karuni)*(case when (karnat='+') then 1 else -1 end)) saldo "+
                        // "from Kardex  k inner join Factura f on f.FacSec=k.facsec where facest='A' and k.ArtSec=a.ArtSec AND  SUBBODSUCCCSEC in(select bodsucccsec from bodegasucursalcc where succod=1)),0) Exist " +

                        Script = "select * from (select ROW_NUMBER() OVER(ORDER BY a.ArtSec ASC) AS Row,a.ArtSec,ArtCod,replace(ArtNom,'''','')+' '+isnull((select top 1 preartnom from artpre ap left join presentacionarticulos p on ap.preartcod=p.preartcod where preartfaccon='S' and ap.artsec=a.artsec ),'') ArtNom,isnull(ArtMedNomCom,'') ArtMedNomCom,g.InvGruCod,g.InvGruNom," +
                                "s.InvSubGruCod,s.InvSubGruNom,a.InvFamCod,InvFamNom,ParConIva,0 PrePreFijCosPro," +
                                "isnull((p1.PrePreFijVal),0) precio1," +
                                "isnull((p2.PrePreFijVal),0) precio2," +
                                "isnull((p3.PrePreFijVal),0) precio3," +
                                "isnull((p4.PrePreFijVal),0) precio4," +
                                "isnull((p5.PrePreFijVal),0) precio5," +
                                "isnull((p6.PrePreFijVal),0) precio6," +
                                "isnull((p7.PrePreFijVal),0) precio7," +
                                "isnull((p8.PrePreFijVal),0) precio8," +
                                "isnull((p9.PrePreFijVal),0) precio9," +
                                "isnull((p10.PrePreFijVal),0) precio10," +
                                "isnull((p11.PrePreFijVal),0) precio11," +
                                "isnull((p12.PrePreFijVal),0) precio12," +
                                "isnull((p13.PrePreFijVal),0) precio13," +
                                "isnull((p14.PrePreFijVal),0) precio14," +
                                "isnull((p15.PrePreFijVal),0) precio15," +
                                "isnull((p16.PrePreFijVal),0) precio16," +
                                "isnull((p17.PrePreFijVal),0) precio17," +
                                "isnull((p18.PrePreFijVal),0) precio18," +
                                "isnull((p19.PrePreFijVal),0) precio19," +
                                "isnull((p20.PrePreFijVal),0) precio20," +
                                "isnull((p21.PrePreFijVal),0) precio21," +
                                "isnull((p22.PrePreFijVal),0) precio22," +
                                "isnull((p23.PrePreFijVal),0) precio23," +
                                "isnull((p24.PrePreFijVal),0) precio24," +
                                "isnull((p25.PrePreFijVal),0) precio25," +
                                "isnull((p26.PrePreFijVal),0) precio26," +
                                "isnull((p27.PrePreFijVal),0) precio27," +
                                "isnull((p28.PrePreFijVal),0) precio28," +
                                "isnull((p29.PrePreFijVal),0) precio29," +
                                "isnull((p30.PrePreFijVal),0) precio30," +
                                "isnull((p1.PrePorVal),0) PrePorVal1," +
                                "isnull((p2.PrePorVal),0) PrePorVal2," +
                                "isnull((p3.PrePorVal),0) PrePorVal3," +
                                "isnull((p4.PrePorVal),0) PrePorVal4," +
                                "isnull((p5.PrePorVal),0) PrePorVal5," +
                                "isnull((p6.PrePorVal),0) PrePorVal6," +
                                "isnull((p7.PrePorVal),0) PrePorVal7," +
                                "isnull((p8.PrePorVal),0) PrePorVal8," +
                                "isnull((p9.PrePorVal),0) PrePorVal9," +
                                "isnull((select top 1 movparlindes from MovParLineaGrupos ff where movparlininvgrucod=s.invgrucod ),0) desc1," +   //and MOVPARLINDETFECINI<=getdate() and MOVPARLINDETFECFIN>=getdate()
                                "isnull((select top 1 MovParArtDetDesc from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc2," +
                                "isnull((select top 1 MovParArtDetDesc2 from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc2<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc5," +
                                "isnull((select top 1 MovParArtFecMod from MovParArtArticulos where MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) ),'') MovParArtFecMod" +
                                ",0 desc3,0 desc4,0 desc6,1000 Exist2 " +
                                ",isnull((select sum(artexiact) from articulosexi ae where ae.artsec=a.artsec and artbodcod in(select alinegbodsucccsec from carunidadesalinegbodsuc where alinegcod="+vAliNegCod+")),0) Exist " +
                                ",0 ExistFec " +
                                ",isnull(c.ClaArtCod,0) ClaArtCod,isnull(c.ClaArtNom,'') ClaArtNom,isnull(ArtValImp,0) ArtValImp,ArtEmb,isnull('N','') ArtIndMpm  " +
                                ",0 ArtRen,0 ArtLim" +
                                "from articulos a " +
                                "left join inventariofamilia f on a.invfamcod=f.invfamcod " +
                                "left join inventariosubgrupo s on f.invsubgrucod= s.invsubgrucod " +
                                "left join inventariogrupo g on g.invgrucod=s.invgrucod " +
                                "left join clasearticulo c on c.claartcod=a.claartcod " +
                                "left join parametrocontable p on p.parconcod=a.parconcod " +
                                "left join artpre pp on a.artsec=pp.artsec and PreArtFacCon='S' " +
                                " left join PreciosDetalle p1 on p1.ArtSec=a.ArtSec and p1.succod=1 and  p1.LisPreCod=1 and p1.PreEst='A' and P1.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p2 on p2.ArtSec=a.ArtSec and p2.succod=1 and  p2.LisPreCod=2 and p2.PreEst='A' and P2.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p3 on p3.ArtSec=a.ArtSec and p3.succod=1 and  p3.LisPreCod=3 and p3.PreEst='A' and P3.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p4 on p4.ArtSec=a.ArtSec and p4.succod=1 and  p4.LisPreCod=4 and p4.PreEst='A' and P4.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p5 on p5.ArtSec=a.ArtSec and p5.succod=1 and  p5.LisPreCod=5 and p5.PreEst='A' and P5.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p6 on p6.ArtSec=a.ArtSec and p6.succod=1 and  p6.LisPreCod=6 and p6.PreEst='A' and P6.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p7 on p7.ArtSec=a.ArtSec and p7.succod=1 and  p7.LisPreCod=7 and p7.PreEst='A' and P7.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p8 on p8.ArtSec=a.ArtSec and p8.succod=1 and  p8.LisPreCod=8 and p8.PreEst='A' and P8.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p9 on p9.ArtSec=a.ArtSec and p9.succod=1 and  p9.LisPreCod=9 and p9.PreEst='A' and P9.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p10 on p10.ArtSec=a.ArtSec and p10.succod=1 and  p10.LisPreCod=10 and p10.PreEst='A' and P10.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p11 on p11.ArtSec=a.ArtSec and p11.succod=1 and  p11.LisPreCod=11 and p11.PreEst='A' and P11.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p12 on p12.ArtSec=a.ArtSec and p12.succod=1 and  p12.LisPreCod=12 and p12.PreEst='A' and P12.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p13 on p13.ArtSec=a.ArtSec and p13.succod=1 and  p13.LisPreCod=13 and p13.PreEst='A' and P13.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p14 on p14.ArtSec=a.ArtSec and p14.succod=1 and  p14.LisPreCod=14 and p14.PreEst='A' and P14.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p15 on p15.ArtSec=a.ArtSec and p15.succod=1 and  p15.LisPreCod=15 and p15.PreEst='A' and P15.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p16 on p16.ArtSec=a.ArtSec and p16.succod=1 and  p16.LisPreCod=16 and p16.PreEst='A' and P16.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p17 on p17.ArtSec=a.ArtSec and p17.succod=1 and  p17.LisPreCod=17 and p17.PreEst='A' and P17.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p18 on p18.ArtSec=a.ArtSec and p18.succod=1 and  p18.LisPreCod=18 and p18.PreEst='A' and P18.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p19 on p19.ArtSec=a.ArtSec and p19.succod=1 and  p19.LisPreCod=19 and p19.PreEst='A' and P19.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p20 on p20.ArtSec=a.ArtSec and p20.succod=1 and  p20.LisPreCod=20 and p20.PreEst='A' and P20.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p21 on p21.ArtSec=a.ArtSec and p21.succod=1 and  p21.LisPreCod=21 and p21.PreEst='A' and P21.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p22 on p22.ArtSec=a.ArtSec and p22.succod=1 and  p22.LisPreCod=22 and p22.PreEst='A' and P22.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p23 on p23.ArtSec=a.ArtSec and p23.succod=1 and  p23.LisPreCod=23 and p23.PreEst='A' and P23.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p24 on p24.ArtSec=a.ArtSec and p24.succod=1 and  p24.LisPreCod=24 and p24.PreEst='A' and P24.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p25 on p25.ArtSec=a.ArtSec and p25.succod=1 and  p25.LisPreCod=25 and p25.PreEst='A' and P25.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p26 on p26.ArtSec=a.ArtSec and p26.succod=1 and  p26.LisPreCod=26 and p26.PreEst='A' and P26.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p27 on p27.ArtSec=a.ArtSec and p27.succod=1 and  p27.LisPreCod=27 and p27.PreEst='A' and P27.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p28 on p28.ArtSec=a.ArtSec and p28.succod=1 and  p28.LisPreCod=28 and p28.PreEst='A' and P28.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p29 on p29.ArtSec=a.ArtSec and p29.succod=1 and  p29.LisPreCod=29 and p29.PreEst='A' and P29.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p30 on p30.ArtSec=a.ArtSec and p30.succod=1 and  p30.LisPreCod=30 and p30.PreEst='A' and P30.PreArtCod=PP.PreArtCod" +
                                " WHERE a.artdes<>'S' " +
                                " and (( s.invgrucod in(select invgrucod from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "') or " +
                                " (select count(*) from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "')=0 ) or a.artsec in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes<>'S')) AND a.artsec NOT in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes='S')) Consulta order by Row desc";

                    } else if (vEmpresa.trim().equalsIgnoreCase("GELVEZGIR") || vEmpresa.trim().equalsIgnoreCase("GELVEZGIRREM")) {
                        //isnull((select SUM(((karcaj*KARARTEMB)+karuni)*(case when (karnat='+') then 1 else -1 end)) saldo "+
                        // "from Kardex  k inner join Factura f on f.FacSec=k.facsec where facest='A' and k.ArtSec=a.ArtSec AND  SUBBODSUCCCSEC in(select bodsucccsec from bodegasucursalcc where succod=1)),0) Exist " +

                        Script = "select * from (select ROW_NUMBER() OVER(ORDER BY a.ArtSec ASC) AS Row,a.ArtSec,ArtCod,replace(ArtNom,'''','')+' '+isnull((select top 1 preartnom from artpre ap left join presentacionarticulos p on ap.preartcod=p.preartcod where preartfaccon='S' and ap.artsec=a.artsec ),'') ArtNom,isnull(ArtMedNomCom,'') ArtMedNomCom,g.InvGruCod,g.InvGruNom," +
                                "s.InvSubGruCod,s.InvSubGruNom,a.InvFamCod,InvFamNom,ParConIva,0 PrePreFijCosPro," +
                                "isnull((p1.PrePreFijVal),0) precio1," +
                                "isnull((p2.PrePreFijVal),0) precio2," +
                                "isnull((p3.PrePreFijVal),0) precio3," +
                                "isnull((p4.PrePreFijVal),0) precio4," +
                                "isnull((p5.PrePreFijVal),0) precio5," +
                                "isnull((p6.PrePreFijVal),0) precio6," +
                                "isnull((p7.PrePreFijVal),0) precio7," +
                                "isnull((p8.PrePreFijVal),0) precio8," +
                                "isnull((p9.PrePreFijVal),0) precio9," +
                                "isnull((p10.PrePreFijVal),0) precio10," +
                                "isnull((p11.PrePreFijVal),0) precio11," +
                                "isnull((p12.PrePreFijVal),0) precio12," +
                                "isnull((p13.PrePreFijVal),0) precio13," +
                                "isnull((p14.PrePreFijVal),0) precio14," +
                                "isnull((p15.PrePreFijVal),0) precio15," +
                                "isnull((p16.PrePreFijVal),0) precio16," +
                                "isnull((p17.PrePreFijVal),0) precio17," +
                                "isnull((p18.PrePreFijVal),0) precio18," +
                                "isnull((p19.PrePreFijVal),0) precio19," +
                                "isnull((p20.PrePreFijVal),0) precio20," +
                                "isnull((p21.PrePreFijVal),0) precio21," +
                                "isnull((p22.PrePreFijVal),0) precio22," +
                                "isnull((p23.PrePreFijVal),0) precio23," +
                                "isnull((p24.PrePreFijVal),0) precio24," +
                                "isnull((p25.PrePreFijVal),0) precio25," +
                                "isnull((p26.PrePreFijVal),0) precio26," +
                                "isnull((p27.PrePreFijVal),0) precio27," +
                                "isnull((p28.PrePreFijVal),0) precio28," +
                                "isnull((p29.PrePreFijVal),0) precio29," +
                                "isnull((p30.PrePreFijVal),0) precio30," +
                                "isnull((p1.PrePorVal),0) PrePorVal1," +
                                "isnull((p2.PrePorVal),0) PrePorVal2," +
                                "isnull((p3.PrePorVal),0) PrePorVal3," +
                                "isnull((p4.PrePorVal),0) PrePorVal4," +
                                "isnull((p5.PrePorVal),0) PrePorVal5," +
                                "isnull((p6.PrePorVal),0) PrePorVal6," +
                                "isnull((p7.PrePorVal),0) PrePorVal7," +
                                "isnull((p8.PrePorVal),0) PrePorVal8," +
                                "isnull((p9.PrePorVal),0) PrePorVal9," +
                                "isnull((select top 1 movparlindes from MovParLineaGrupos ff where movparlininvgrucod=s.invgrucod ),0) desc1," +   //and MOVPARLINDETFECINI<=getdate() and MOVPARLINDETFECFIN>=getdate()
                                "isnull((select top 1 MovParArtDetDesc from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc2," +
                                "isnull((select top 1 MovParArtDetDesc2 from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc2<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc5," +
                                "isnull((select top 1 MovParArtFecMod from MovParArtArticulos where MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) ),'') MovParArtFecMod" +
                                ",0 desc3,0 desc4,0 desc6,1000 Exist2 " +
                                ",isnull((select sum(artexiact) from articulosexi ae where ae.artsec=a.artsec and artbodcod in(select alinegbodsucccsec from carunidadesalinegbodsuc where alinegcod="+vAliNegCod+")),0) Exist " +
                                ",0 ExistFec " +
                                ",isnull(c.ClaArtCod,0) ClaArtCod,isnull(c.ClaArtNom,'') ClaArtNom,isnull(ArtValImp,0) ArtValImp,ArtEmb,isnull('N','') ArtIndMpm  " +
                                ",0 ArtRen,0 ArtLim " +
                                "from articulos a " +
                                "left join inventariofamilia f on a.invfamcod=f.invfamcod " +
                                "left join inventariosubgrupo s on f.invsubgrucod= s.invsubgrucod " +
                                "left join inventariogrupo g on g.invgrucod=s.invgrucod " +
                                "left join clasearticulo c on c.claartcod=a.claartcod " +
                                "left join parametrocontable p on p.parconcod=a.parconcod " +
                                "left join artpre pp on a.artsec=pp.artsec and PreArtFacCon='S' " +
                                " left join PreciosDetalle p1 on p1.ArtSec=a.ArtSec and p1.succod=3 and  p1.LisPreCod=1 and p1.PreEst='A' and P1.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p2 on p2.ArtSec=a.ArtSec and p2.succod=3 and  p2.LisPreCod=2 and p2.PreEst='A' and P2.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p3 on p3.ArtSec=a.ArtSec and p3.succod=3 and  p3.LisPreCod=3 and p3.PreEst='A' and P3.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p4 on p4.ArtSec=a.ArtSec and p4.succod=3 and  p4.LisPreCod=4 and p4.PreEst='A' and P4.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p5 on p5.ArtSec=a.ArtSec and p5.succod=3 and  p5.LisPreCod=5 and p5.PreEst='A' and P5.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p6 on p6.ArtSec=a.ArtSec and p6.succod=3 and  p6.LisPreCod=6 and p6.PreEst='A' and P6.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p7 on p7.ArtSec=a.ArtSec and p7.succod=3 and  p7.LisPreCod=7 and p7.PreEst='A' and P7.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p8 on p8.ArtSec=a.ArtSec and p8.succod=3 and  p8.LisPreCod=8 and p8.PreEst='A' and P8.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p9 on p9.ArtSec=a.ArtSec and p9.succod=3 and  p9.LisPreCod=9 and p9.PreEst='A' and P9.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p10 on p10.ArtSec=a.ArtSec and p10.succod=3 and  p10.LisPreCod=10 and p10.PreEst='A' and P10.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p11 on p11.ArtSec=a.ArtSec and p11.succod=3 and  p11.LisPreCod=11 and p11.PreEst='A' and P11.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p12 on p12.ArtSec=a.ArtSec and p12.succod=3 and  p12.LisPreCod=12 and p12.PreEst='A' and P12.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p13 on p13.ArtSec=a.ArtSec and p13.succod=3 and  p13.LisPreCod=13 and p13.PreEst='A' and P13.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p14 on p14.ArtSec=a.ArtSec and p14.succod=3 and  p14.LisPreCod=14 and p14.PreEst='A' and P14.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p15 on p15.ArtSec=a.ArtSec and p15.succod=3 and  p15.LisPreCod=15 and p15.PreEst='A' and P15.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p16 on p16.ArtSec=a.ArtSec and p16.succod=3 and  p16.LisPreCod=16 and p16.PreEst='A' and P16.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p17 on p17.ArtSec=a.ArtSec and p17.succod=3 and  p17.LisPreCod=17 and p17.PreEst='A' and P17.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p18 on p18.ArtSec=a.ArtSec and p18.succod=3 and  p18.LisPreCod=18 and p18.PreEst='A' and P18.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p19 on p19.ArtSec=a.ArtSec and p19.succod=3 and  p19.LisPreCod=19 and p19.PreEst='A' and P19.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p20 on p20.ArtSec=a.ArtSec and p20.succod=3 and  p20.LisPreCod=20 and p20.PreEst='A' and P20.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p21 on p21.ArtSec=a.ArtSec and p21.succod=3 and  p21.LisPreCod=21 and p21.PreEst='A' and P21.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p22 on p22.ArtSec=a.ArtSec and p22.succod=3 and  p22.LisPreCod=22 and p22.PreEst='A' and P22.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p23 on p23.ArtSec=a.ArtSec and p23.succod=3 and  p23.LisPreCod=23 and p23.PreEst='A' and P23.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p24 on p24.ArtSec=a.ArtSec and p24.succod=3 and  p24.LisPreCod=24 and p24.PreEst='A' and P24.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p25 on p25.ArtSec=a.ArtSec and p25.succod=3 and  p25.LisPreCod=25 and p25.PreEst='A' and P25.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p26 on p26.ArtSec=a.ArtSec and p26.succod=3 and  p26.LisPreCod=26 and p26.PreEst='A' and P26.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p27 on p27.ArtSec=a.ArtSec and p27.succod=3 and  p27.LisPreCod=27 and p27.PreEst='A' and P27.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p28 on p28.ArtSec=a.ArtSec and p28.succod=3 and  p28.LisPreCod=28 and p28.PreEst='A' and P28.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p29 on p29.ArtSec=a.ArtSec and p29.succod=3 and  p29.LisPreCod=29 and p29.PreEst='A' and P29.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p30 on p30.ArtSec=a.ArtSec and p30.succod=3 and  p30.LisPreCod=30 and p30.PreEst='A' and P30.PreArtCod=PP.PreArtCod" +
                                " WHERE a.artdes<>'S' " +
                                " and (( s.invgrucod in(select invgrucod from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "') or " +
                                " (select count(*) from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "')=0 ) or a.artsec in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes<>'S')) AND a.artsec NOT in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes='S')) Consulta order by Row desc";
                    } else if (vEmpresa.trim().equalsIgnoreCase("GELVEZCAL") || vEmpresa.trim().equalsIgnoreCase("GELVEZCALREM")) {
                        //isnull((select SUM(((karcaj*KARARTEMB)+karuni)*(case when (karnat='+') then 1 else -1 end)) saldo "+
                        // "from Kardex  k inner join Factura f on f.FacSec=k.facsec where facest='A' and k.ArtSec=a.ArtSec AND  SUBBODSUCCCSEC in(select bodsucccsec from bodegasucursalcc where succod=1)),0) Exist " +

                        Script = "select * from (select ROW_NUMBER() OVER(ORDER BY a.ArtSec ASC) AS Row,a.ArtSec,ArtCod,replace(ArtNom,'''','')+' '+isnull((select top 1 preartnom from artpre ap left join presentacionarticulos p on ap.preartcod=p.preartcod where preartfaccon='S' and ap.artsec=a.artsec ),'') ArtNom,isnull(ArtMedNomCom,'') ArtMedNomCom,g.InvGruCod,g.InvGruNom," +
                                "s.InvSubGruCod,s.InvSubGruNom,a.InvFamCod,InvFamNom,ParConIva,0 PrePreFijCosPro," +
                                "isnull((p1.PrePreFijVal),0) precio1," +
                                "isnull((p2.PrePreFijVal),0) precio2," +
                                "isnull((p3.PrePreFijVal),0) precio3," +
                                "isnull((p4.PrePreFijVal),0) precio4," +
                                "isnull((p5.PrePreFijVal),0) precio5," +
                                "isnull((p6.PrePreFijVal),0) precio6," +
                                "isnull((p7.PrePreFijVal),0) precio7," +
                                "isnull((p8.PrePreFijVal),0) precio8," +
                                "isnull((p9.PrePreFijVal),0) precio9," +
                                "isnull((p10.PrePreFijVal),0) precio10," +
                                "isnull((p11.PrePreFijVal),0) precio11," +
                                "isnull((p12.PrePreFijVal),0) precio12," +
                                "isnull((p13.PrePreFijVal),0) precio13," +
                                "isnull((p14.PrePreFijVal),0) precio14," +
                                "isnull((p15.PrePreFijVal),0) precio15," +
                                "isnull((p16.PrePreFijVal),0) precio16," +
                                "isnull((p17.PrePreFijVal),0) precio17," +
                                "isnull((p18.PrePreFijVal),0) precio18," +
                                "isnull((p19.PrePreFijVal),0) precio19," +
                                "isnull((p20.PrePreFijVal),0) precio20," +
                                "isnull((p21.PrePreFijVal),0) precio21," +
                                "isnull((p22.PrePreFijVal),0) precio22," +
                                "isnull((p23.PrePreFijVal),0) precio23," +
                                "isnull((p24.PrePreFijVal),0) precio24," +
                                "isnull((p25.PrePreFijVal),0) precio25," +
                                "isnull((p26.PrePreFijVal),0) precio26," +
                                "isnull((p27.PrePreFijVal),0) precio27," +
                                "isnull((p28.PrePreFijVal),0) precio28," +
                                "isnull((p29.PrePreFijVal),0) precio29," +
                                "isnull((p30.PrePreFijVal),0) precio30," +
                                "isnull((p1.PrePorVal),0) PrePorVal1," +
                                "isnull((p2.PrePorVal),0) PrePorVal2," +
                                "isnull((p3.PrePorVal),0) PrePorVal3," +
                                "isnull((p4.PrePorVal),0) PrePorVal4," +
                                "isnull((p5.PrePorVal),0) PrePorVal5," +
                                "isnull((p6.PrePorVal),0) PrePorVal6," +
                                "isnull((p7.PrePorVal),0) PrePorVal7," +
                                "isnull((p8.PrePorVal),0) PrePorVal8," +
                                "isnull((p9.PrePorVal),0) PrePorVal9," +
                                "isnull((select top 1 movparlindes from MovParLineaGrupos ff where movparlininvgrucod=s.invgrucod ),0) desc1," +   //and MOVPARLINDETFECINI<=getdate() and MOVPARLINDETFECFIN>=getdate()
                                "isnull((select top 1 MovParArtDetDesc from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc2," +
                                "isnull((select top 1 MovParArtDetDesc2 from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc2<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc5," +
                                "isnull((select top 1 MovParArtFecMod from MovParArtArticulos where MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) ),'') MovParArtFecMod" +
                                ",0 desc3,0 desc4,0 desc6,1000 Exist2 " +
                                ",isnull((select sum(artexiact) from articulosexi ae where ae.artsec=a.artsec and artbodcod in(select alinegbodsucccsec from carunidadesalinegbodsuc where alinegcod="+vAliNegCod+")),0) Exist " +
                                ",0 ExistFec " +
                                ",isnull(c.ClaArtCod,0) ClaArtCod,isnull(c.ClaArtNom,'') ClaArtNom,isnull(ArtValImp,0) ArtValImp,ArtEmb,isnull('N','') ArtIndMpm  " +
                                ",0 ArtRen,0 ArtLim " +
                                "from articulos a " +
                                "left join inventariofamilia f on a.invfamcod=f.invfamcod " +
                                "left join inventariosubgrupo s on f.invsubgrucod= s.invsubgrucod " +
                                "left join inventariogrupo g on g.invgrucod=s.invgrucod " +
                                "left join clasearticulo c on c.claartcod=a.claartcod " +
                                "left join parametrocontable p on p.parconcod=a.parconcod " +
                                "left join artpre pp on a.artsec=pp.artsec and PreArtFacCon='S' " +
                                " left join PreciosDetalle p1 on p1.ArtSec=a.ArtSec and p1.succod=4 and  p1.LisPreCod=1 and p1.PreEst='A' and P1.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p2 on p2.ArtSec=a.ArtSec and p2.succod=4 and  p2.LisPreCod=2 and p2.PreEst='A' and P2.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p3 on p3.ArtSec=a.ArtSec and p3.succod=4 and  p3.LisPreCod=3 and p3.PreEst='A' and P3.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p4 on p4.ArtSec=a.ArtSec and p4.succod=4 and  p4.LisPreCod=4 and p4.PreEst='A' and P4.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p5 on p5.ArtSec=a.ArtSec and p5.succod=4 and  p5.LisPreCod=5 and p5.PreEst='A' and P5.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p6 on p6.ArtSec=a.ArtSec and p6.succod=4 and  p6.LisPreCod=6 and p6.PreEst='A' and P6.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p7 on p7.ArtSec=a.ArtSec and p7.succod=4 and  p7.LisPreCod=7 and p7.PreEst='A' and P7.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p8 on p8.ArtSec=a.ArtSec and p8.succod=4 and  p8.LisPreCod=8 and p8.PreEst='A' and P8.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p9 on p9.ArtSec=a.ArtSec and p9.succod=4 and  p9.LisPreCod=9 and p9.PreEst='A' and P9.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p10 on p10.ArtSec=a.ArtSec and p10.succod=4 and  p10.LisPreCod=10 and p10.PreEst='A' and P10.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p11 on p11.ArtSec=a.ArtSec and p11.succod=4 and  p11.LisPreCod=11 and p11.PreEst='A' and P11.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p12 on p12.ArtSec=a.ArtSec and p12.succod=4 and  p12.LisPreCod=12 and p12.PreEst='A' and P12.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p13 on p13.ArtSec=a.ArtSec and p13.succod=4 and  p13.LisPreCod=13 and p13.PreEst='A' and P13.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p14 on p14.ArtSec=a.ArtSec and p14.succod=4 and  p14.LisPreCod=14 and p14.PreEst='A' and P14.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p15 on p15.ArtSec=a.ArtSec and p15.succod=4 and  p15.LisPreCod=15 and p15.PreEst='A' and P15.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p16 on p16.ArtSec=a.ArtSec and p16.succod=4 and  p16.LisPreCod=16 and p16.PreEst='A' and P16.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p17 on p17.ArtSec=a.ArtSec and p17.succod=4 and  p17.LisPreCod=17 and p17.PreEst='A' and P17.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p18 on p18.ArtSec=a.ArtSec and p18.succod=4 and  p18.LisPreCod=18 and p18.PreEst='A' and P18.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p19 on p19.ArtSec=a.ArtSec and p19.succod=4 and  p19.LisPreCod=19 and p19.PreEst='A' and P19.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p20 on p20.ArtSec=a.ArtSec and p20.succod=4 and  p20.LisPreCod=20 and p10.PreEst='A' and P20.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p21 on p21.ArtSec=a.ArtSec and p21.succod=4 and  p21.LisPreCod=21 and p11.PreEst='A' and P21.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p22 on p22.ArtSec=a.ArtSec and p22.succod=4 and  p22.LisPreCod=22 and p12.PreEst='A' and P22.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p23 on p23.ArtSec=a.ArtSec and p23.succod=4 and  p23.LisPreCod=23 and p13.PreEst='A' and P23.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p24 on p24.ArtSec=a.ArtSec and p24.succod=4 and  p24.LisPreCod=24 and p14.PreEst='A' and P24.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p25 on p25.ArtSec=a.ArtSec and p25.succod=4 and  p25.LisPreCod=25 and p15.PreEst='A' and P25.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p26 on p26.ArtSec=a.ArtSec and p26.succod=4 and  p26.LisPreCod=26 and p16.PreEst='A' and P26.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p27 on p27.ArtSec=a.ArtSec and p27.succod=4 and  p27.LisPreCod=27 and p17.PreEst='A' and P27.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p28 on p28.ArtSec=a.ArtSec and p28.succod=4 and  p28.LisPreCod=28 and p18.PreEst='A' and P28.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p29 on p29.ArtSec=a.ArtSec and p29.succod=4 and  p29.LisPreCod=29 and p19.PreEst='A' and P29.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p30 on p30.ArtSec=a.ArtSec and p30.succod=4 and  p30.LisPreCod=30 and p30.PreEst='A' and P30.PreArtCod=PP.PreArtCod" +
                                " WHERE a.artdes<>'S' " +
                                " and (( s.invgrucod in(select invgrucod from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "') or " +
                                " (select count(*) from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "')=0 ) or a.artsec in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes<>'S')) AND a.artsec NOT in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes='S')) Consulta order by Row desc";
                    } else if (vEmpresa.trim().equalsIgnoreCase("GELVEZEJE")) {
                            //isnull((select SUM(((karcaj*KARARTEMB)+karuni)*(case when (karnat='+') then 1 else -1 end)) saldo "+
                            // "from Kardex  k inner join Factura f on f.FacSec=k.facsec where facest='A' and k.ArtSec=a.ArtSec AND  SUBBODSUCCCSEC in(select bodsucccsec from bodegasucursalcc where succod=1)),0) Exist " +

                            Script = "select * from (select ROW_NUMBER() OVER(ORDER BY a.ArtSec ASC) AS Row,a.ArtSec,ArtCod,replace(ArtNom,'''','')+' '+isnull((select top 1 preartnom from artpre ap left join presentacionarticulos p on ap.preartcod=p.preartcod where preartfaccon='S' and ap.artsec=a.artsec ),'') ArtNom,isnull(ArtMedNomCom,'') ArtMedNomCom,g.InvGruCod,g.InvGruNom," +
                                    "s.InvSubGruCod,s.InvSubGruNom,a.InvFamCod,InvFamNom,ParConIva,0 PrePreFijCosPro," +
                                    "isnull((p1.PrePreFijVal),0) precio1," +
                                    "isnull((p2.PrePreFijVal),0) precio2," +
                                    "isnull((p3.PrePreFijVal),0) precio3," +
                                    "isnull((p4.PrePreFijVal),0) precio4," +
                                    "isnull((p5.PrePreFijVal),0) precio5," +
                                    "isnull((p6.PrePreFijVal),0) precio6," +
                                    "isnull((p7.PrePreFijVal),0) precio7," +
                                    "isnull((p8.PrePreFijVal),0) precio8," +
                                    "isnull((p9.PrePreFijVal),0) precio9," +
                                    "isnull((p10.PrePreFijVal),0) precio10," +
                                    "isnull((p11.PrePreFijVal),0) precio11," +
                                    "isnull((p12.PrePreFijVal),0) precio12," +
                                    "isnull((p13.PrePreFijVal),0) precio13," +
                                    "isnull((p14.PrePreFijVal),0) precio14," +
                                    "isnull((p15.PrePreFijVal),0) precio15," +
                                    "isnull((p16.PrePreFijVal),0) precio16," +
                                    "isnull((p17.PrePreFijVal),0) precio17," +
                                    "isnull((p18.PrePreFijVal),0) precio18," +
                                    "isnull((p19.PrePreFijVal),0) precio19," +
                                    "isnull((p20.PrePreFijVal),0) precio20," +
                                    "isnull((p21.PrePreFijVal),0) precio21," +
                                    "isnull((p22.PrePreFijVal),0) precio22," +
                                    "isnull((p23.PrePreFijVal),0) precio23," +
                                    "isnull((p24.PrePreFijVal),0) precio24," +
                                    "isnull((p25.PrePreFijVal),0) precio25," +
                                    "isnull((p26.PrePreFijVal),0) precio26," +
                                    "isnull((p27.PrePreFijVal),0) precio27," +
                                    "isnull((p28.PrePreFijVal),0) precio28," +
                                    "isnull((p29.PrePreFijVal),0) precio29," +
                                    "isnull((p30.PrePreFijVal),0) precio30," +
                                    "isnull((p1.PrePorVal),0) PrePorVal1," +
                                    "isnull((p2.PrePorVal),0) PrePorVal2," +
                                    "isnull((p3.PrePorVal),0) PrePorVal3," +
                                    "isnull((p4.PrePorVal),0) PrePorVal4," +
                                    "isnull((p5.PrePorVal),0) PrePorVal5," +
                                    "isnull((p6.PrePorVal),0) PrePorVal6," +
                                    "isnull((p7.PrePorVal),0) PrePorVal7," +
                                    "isnull((p8.PrePorVal),0) PrePorVal8," +
                                    "isnull((p9.PrePorVal),0) PrePorVal9," +
                                    "isnull((select top 1 movparlindes from MovParLineaGrupos ff where movparlininvgrucod=s.invgrucod ),0) desc1," +   //and MOVPARLINDETFECINI<=getdate() and MOVPARLINDETFECFIN>=getdate()
                                    "isnull((select top 1 MovParArtDetDesc from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                    "),0) desc2," +
                                    "isnull((select top 1 MovParArtDetDesc2 from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc2<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                    "),0) desc5," +
                                    "isnull((select top 1 MovParArtFecMod from MovParArtArticulos where MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) ),'') MovParArtFecMod" +
                                    ",0 desc3,0 desc4,0 desc6,1000 Exist2 " +
                                    ",isnull((select sum(artexiact) from articulosexi ae where ae.artsec=a.artsec and artbodcod in(select alinegbodsucccsec from carunidadesalinegbodsuc where alinegcod="+vAliNegCod+")),0) Exist " +
                                    ",0 ExistFec " +
                                    ",isnull(c.ClaArtCod,0) ClaArtCod,isnull(c.ClaArtNom,'') ClaArtNom,isnull(ArtValImp,0) ArtValImp,ArtEmb,isnull('N','') ArtIndMpm  " +
                                    ",0 ArtRen,0 ArtLim " +
                                    "from articulos a " +
                                    "left join inventariofamilia f on a.invfamcod=f.invfamcod " +
                                    "left join inventariosubgrupo s on f.invsubgrucod= s.invsubgrucod " +
                                    "left join inventariogrupo g on g.invgrucod=s.invgrucod " +
                                    "left join clasearticulo c on c.claartcod=a.claartcod " +
                                    "left join parametrocontable p on p.parconcod=a.parconcod " +
                                    "left join artpre pp on a.artsec=pp.artsec and PreArtFacCon='S' " +
                                    " left join PreciosDetalle p1 on p1.ArtSec=a.ArtSec and p1.succod=5 and  p1.LisPreCod=1 and p1.PreEst='A' and P1.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p2 on p2.ArtSec=a.ArtSec and p2.succod=5 and  p2.LisPreCod=2 and p2.PreEst='A' and P2.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p3 on p3.ArtSec=a.ArtSec and p3.succod=5 and  p3.LisPreCod=3 and p3.PreEst='A' and P3.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p4 on p4.ArtSec=a.ArtSec and p4.succod=5 and  p4.LisPreCod=4 and p4.PreEst='A' and P4.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p5 on p5.ArtSec=a.ArtSec and p5.succod=5 and  p5.LisPreCod=5 and p5.PreEst='A' and P5.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p6 on p6.ArtSec=a.ArtSec and p6.succod=5 and  p6.LisPreCod=6 and p6.PreEst='A' and P6.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p7 on p7.ArtSec=a.ArtSec and p7.succod=5 and  p7.LisPreCod=7 and p7.PreEst='A' and P7.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p8 on p8.ArtSec=a.ArtSec and p8.succod=5 and  p8.LisPreCod=8 and p8.PreEst='A' and P8.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p9 on p9.ArtSec=a.ArtSec and p9.succod=5 and  p9.LisPreCod=9 and p9.PreEst='A' and P9.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p10 on p10.ArtSec=a.ArtSec and p10.succod=5 and  p10.LisPreCod=10 and p10.PreEst='A' and P10.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p11 on p11.ArtSec=a.ArtSec and p11.succod=5 and  p11.LisPreCod=11 and p11.PreEst='A' and P11.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p12 on p12.ArtSec=a.ArtSec and p12.succod=5 and  p12.LisPreCod=12 and p12.PreEst='A' and P12.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p13 on p13.ArtSec=a.ArtSec and p13.succod=5 and  p13.LisPreCod=13 and p13.PreEst='A' and P13.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p14 on p14.ArtSec=a.ArtSec and p14.succod=5 and  p14.LisPreCod=14 and p14.PreEst='A' and P14.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p15 on p15.ArtSec=a.ArtSec and p15.succod=5 and  p15.LisPreCod=15 and p15.PreEst='A' and P15.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p16 on p16.ArtSec=a.ArtSec and p16.succod=5 and  p16.LisPreCod=16 and p16.PreEst='A' and P16.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p17 on p17.ArtSec=a.ArtSec and p17.succod=5 and  p17.LisPreCod=17 and p17.PreEst='A' and P17.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p18 on p18.ArtSec=a.ArtSec and p18.succod=5 and  p18.LisPreCod=18 and p18.PreEst='A' and P18.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p19 on p19.ArtSec=a.ArtSec and p19.succod=5 and  p19.LisPreCod=19 and p19.PreEst='A' and P19.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p20 on p20.ArtSec=a.ArtSec and p20.succod=5 and  p20.LisPreCod=20 and p10.PreEst='A' and P20.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p21 on p21.ArtSec=a.ArtSec and p21.succod=5 and  p21.LisPreCod=21 and p11.PreEst='A' and P21.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p22 on p22.ArtSec=a.ArtSec and p22.succod=5 and  p22.LisPreCod=22 and p12.PreEst='A' and P22.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p23 on p23.ArtSec=a.ArtSec and p23.succod=5 and  p23.LisPreCod=23 and p13.PreEst='A' and P23.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p24 on p24.ArtSec=a.ArtSec and p24.succod=5 and  p24.LisPreCod=24 and p14.PreEst='A' and P24.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p25 on p25.ArtSec=a.ArtSec and p25.succod=5 and  p25.LisPreCod=25 and p15.PreEst='A' and P25.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p26 on p26.ArtSec=a.ArtSec and p26.succod=5 and  p26.LisPreCod=26 and p16.PreEst='A' and P26.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p27 on p27.ArtSec=a.ArtSec and p27.succod=5 and  p27.LisPreCod=27 and p17.PreEst='A' and P27.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p28 on p28.ArtSec=a.ArtSec and p28.succod=5 and  p28.LisPreCod=28 and p18.PreEst='A' and P28.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p29 on p29.ArtSec=a.ArtSec and p29.succod=5 and  p29.LisPreCod=29 and p19.PreEst='A' and P29.PreArtCod=PP.PreArtCod" +
                                    " left join PreciosDetalle p30 on p30.ArtSec=a.ArtSec and p30.succod=5 and  p30.LisPreCod=30 and p30.PreEst='A' and P30.PreArtCod=PP.PreArtCod" +
                                    " WHERE a.artdes<>'S' " +
                                    " and (( s.invgrucod in(select invgrucod from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "') or " +
                                    " (select count(*) from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "')=0 ) or a.artsec in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes<>'S')) AND a.artsec NOT in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes='S')) Consulta order by Row desc";
                        } else if (vEmpresa.trim().equalsIgnoreCase("GELVEZARA")) {
                        //isnull((select SUM(((karcaj*KARARTEMB)+karuni)*(case when (karnat='+') then 1 else -1 end)) saldo "+
                        // "from Kardex  k inner join Factura f on f.FacSec=k.facsec where facest='A' and k.ArtSec=a.ArtSec AND  SUBBODSUCCCSEC in(select bodsucccsec from bodegasucursalcc where succod=1)),0) Exist " +

                        Script = "select * from (select ROW_NUMBER() OVER(ORDER BY a.ArtSec ASC) AS Row,a.ArtSec,ArtCod,replace(ArtNom,'''','')+' '+isnull((select top 1 preartnom from artpre ap left join presentacionarticulos p on ap.preartcod=p.preartcod where preartfaccon='S' and ap.artsec=a.artsec ),'') ArtNom,isnull(ArtMedNomCom,'') ArtMedNomCom,g.InvGruCod,g.InvGruNom," +
                                "s.InvSubGruCod,s.InvSubGruNom,a.InvFamCod,InvFamNom,ParConIva,0 PrePreFijCosPro," +
                                "isnull((p1.PrePreFijVal),0) precio1," +
                                "isnull((p2.PrePreFijVal),0) precio2," +
                                "isnull((p3.PrePreFijVal),0) precio3," +
                                "isnull((p4.PrePreFijVal),0) precio4," +
                                "isnull((p5.PrePreFijVal),0) precio5," +
                                "isnull((p6.PrePreFijVal),0) precio6," +
                                "isnull((p7.PrePreFijVal),0) precio7," +
                                "isnull((p8.PrePreFijVal),0) precio8," +
                                "isnull((p9.PrePreFijVal),0) precio9," +
                                "isnull((p10.PrePreFijVal),0) precio10," +
                                "isnull((p11.PrePreFijVal),0) precio11," +
                                "isnull((p12.PrePreFijVal),0) precio12," +
                                "isnull((p13.PrePreFijVal),0) precio13," +
                                "isnull((p14.PrePreFijVal),0) precio14," +
                                "isnull((p15.PrePreFijVal),0) precio15," +
                                "isnull((p16.PrePreFijVal),0) precio16," +
                                "isnull((p17.PrePreFijVal),0) precio17," +
                                "isnull((p18.PrePreFijVal),0) precio18," +
                                "isnull((p19.PrePreFijVal),0) precio19," +
                                "isnull((p20.PrePreFijVal),0) precio20," +
                                "isnull((p21.PrePreFijVal),0) precio21," +
                                "isnull((p22.PrePreFijVal),0) precio22," +
                                "isnull((p23.PrePreFijVal),0) precio23," +
                                "isnull((p24.PrePreFijVal),0) precio24," +
                                "isnull((p25.PrePreFijVal),0) precio25," +
                                "isnull((p26.PrePreFijVal),0) precio26," +
                                "isnull((p27.PrePreFijVal),0) precio27," +
                                "isnull((p28.PrePreFijVal),0) precio28," +
                                "isnull((p29.PrePreFijVal),0) precio29," +
                                "isnull((p30.PrePreFijVal),0) precio30," +
                                "isnull((p1.PrePorVal),0) PrePorVal1," +
                                "isnull((p2.PrePorVal),0) PrePorVal2," +
                                "isnull((p3.PrePorVal),0) PrePorVal3," +
                                "isnull((p4.PrePorVal),0) PrePorVal4," +
                                "isnull((p5.PrePorVal),0) PrePorVal5," +
                                "isnull((p6.PrePorVal),0) PrePorVal6," +
                                "isnull((p7.PrePorVal),0) PrePorVal7," +
                                "isnull((p8.PrePorVal),0) PrePorVal8," +
                                "isnull((p9.PrePorVal),0) PrePorVal9," +
                                "isnull((select top 1 movparlindes from MovParLineaGrupos ff where movparlininvgrucod=s.invgrucod ),0) desc1," +   //and MOVPARLINDETFECINI<=getdate() and MOVPARLINDETFECFIN>=getdate()
                                "isnull((select top 1 MovParArtDetDesc from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc2," +
                                "isnull((select top 1 MovParArtDetDesc2 from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc2<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc5," +
                                "isnull((select top 1 MovParArtFecMod from MovParArtArticulos where MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) ),'') MovParArtFecMod" +
                                ",0 desc3,0 desc4,0 desc6,1000 Exist2 " +
                                ",isnull((select sum(artexiact) from articulosexi ae where ae.artsec=a.artsec and artbodcod in(select alinegbodsucccsec from carunidadesalinegbodsuc where alinegcod="+vAliNegCod+")),0) Exist " +
                                ",0 ExistFec " +
                                ",isnull(c.ClaArtCod,0) ClaArtCod,isnull(c.ClaArtNom,'') ClaArtNom,isnull(ArtValImp,0) ArtValImp,ArtEmb,isnull('N','') ArtIndMpm  " +
                                ",0 ArtRen,0 ArtLim,'N' InvGruDifCheck,'N' InvSubDifCheck,'N' InvFamDifCheck ,ArtCodBar " +
                                "from articulos a " +
                                "left join inventariofamilia f on a.invfamcod=f.invfamcod " +
                                "left join inventariosubgrupo s on f.invsubgrucod= s.invsubgrucod " +
                                "left join inventariogrupo g on g.invgrucod=s.invgrucod " +
                                "left join clasearticulo c on c.claartcod=a.claartcod " +
                                "left join parametrocontable p on p.parconcod=a.parconcod " +
                                "left join artpre pp on a.artsec=pp.artsec and PreArtFacCon='S' " +
                                " left join PreciosDetalle p1 on p1.ArtSec=a.ArtSec and p1.succod=1 and  p1.LisPreCod=1 and p1.PreEst='A' and P1.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p2 on p2.ArtSec=a.ArtSec and p2.succod=1 and  p2.LisPreCod=2 and p2.PreEst='A' and P2.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p3 on p3.ArtSec=a.ArtSec and p3.succod=1 and  p3.LisPreCod=3 and p3.PreEst='A' and P3.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p4 on p4.ArtSec=a.ArtSec and p4.succod=1 and  p4.LisPreCod=4 and p4.PreEst='A' and P4.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p5 on p5.ArtSec=a.ArtSec and p5.succod=1 and  p5.LisPreCod=5 and p5.PreEst='A' and P5.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p6 on p6.ArtSec=a.ArtSec and p6.succod=1 and  p6.LisPreCod=6 and p6.PreEst='A' and P6.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p7 on p7.ArtSec=a.ArtSec and p7.succod=1 and  p7.LisPreCod=7 and p7.PreEst='A' and P7.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p8 on p8.ArtSec=a.ArtSec and p8.succod=1 and  p8.LisPreCod=8 and p8.PreEst='A' and P8.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p9 on p9.ArtSec=a.ArtSec and p9.succod=1 and  p9.LisPreCod=9 and p9.PreEst='A' and P9.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p10 on p10.ArtSec=a.ArtSec and p10.succod=1 and  p10.LisPreCod=10 and p10.PreEst='A' and P10.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p11 on p11.ArtSec=a.ArtSec and p11.succod=1 and  p11.LisPreCod=11 and p11.PreEst='A' and P11.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p12 on p12.ArtSec=a.ArtSec and p12.succod=1 and  p12.LisPreCod=12 and p12.PreEst='A' and P12.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p13 on p13.ArtSec=a.ArtSec and p13.succod=1 and  p13.LisPreCod=13 and p13.PreEst='A' and P13.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p14 on p14.ArtSec=a.ArtSec and p14.succod=1 and  p14.LisPreCod=14 and p14.PreEst='A' and P14.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p15 on p15.ArtSec=a.ArtSec and p15.succod=1 and  p15.LisPreCod=15 and p15.PreEst='A' and P15.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p16 on p16.ArtSec=a.ArtSec and p16.succod=1 and  p16.LisPreCod=16 and p16.PreEst='A' and P16.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p17 on p17.ArtSec=a.ArtSec and p17.succod=1 and  p17.LisPreCod=17 and p17.PreEst='A' and P17.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p18 on p18.ArtSec=a.ArtSec and p18.succod=1 and  p18.LisPreCod=18 and p18.PreEst='A' and P18.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p19 on p19.ArtSec=a.ArtSec and p19.succod=1 and  p19.LisPreCod=19 and p19.PreEst='A' and P19.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p20 on p20.ArtSec=a.ArtSec and p20.succod=1 and  p20.LisPreCod=20 and p10.PreEst='A' and P20.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p21 on p21.ArtSec=a.ArtSec and p21.succod=1 and  p21.LisPreCod=21 and p11.PreEst='A' and P21.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p22 on p22.ArtSec=a.ArtSec and p22.succod=1 and  p22.LisPreCod=22 and p12.PreEst='A' and P22.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p23 on p23.ArtSec=a.ArtSec and p23.succod=1 and  p23.LisPreCod=23 and p13.PreEst='A' and P23.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p24 on p24.ArtSec=a.ArtSec and p24.succod=1 and  p24.LisPreCod=24 and p14.PreEst='A' and P24.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p25 on p25.ArtSec=a.ArtSec and p25.succod=1 and  p25.LisPreCod=25 and p15.PreEst='A' and P25.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p26 on p26.ArtSec=a.ArtSec and p26.succod=1 and  p26.LisPreCod=26 and p16.PreEst='A' and P26.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p27 on p27.ArtSec=a.ArtSec and p27.succod=1 and  p27.LisPreCod=27 and p17.PreEst='A' and P27.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p28 on p28.ArtSec=a.ArtSec and p28.succod=1 and  p28.LisPreCod=28 and p18.PreEst='A' and P28.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p29 on p29.ArtSec=a.ArtSec and p29.succod=1 and  p29.LisPreCod=29 and p19.PreEst='A' and P29.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p30 on p30.ArtSec=a.ArtSec and p30.succod=1 and  p30.LisPreCod=30 and p30.PreEst='A' and P30.PreArtCod=PP.PreArtCod" +
                                " WHERE a.artdes<>'S' " +
                                " and (( s.invgrucod in(select invgrucod from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "') or " +
                                " (select count(*) from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "')=0 ) or a.artsec in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes<>'S')) AND a.artsec NOT in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes='S')) Consulta order by Row desc";





                    }else if (vEmpresa.trim().equalsIgnoreCase("GELVEZSUCARA")) {
                        //isnull((select SUM(((karcaj*KARARTEMB)+karuni)*(case when (karnat='+') then 1 else -1 end)) saldo "+
                        // "from Kardex  k inner join Factura f on f.FacSec=k.facsec where facest='A' and k.ArtSec=a.ArtSec AND  SUBBODSUCCCSEC in(select bodsucccsec from bodegasucursalcc where succod=1)),0) Exist " +

                        Script = "select * from (select ROW_NUMBER() OVER(ORDER BY a.ArtSec ASC) AS Row,a.ArtSec,ArtCod,replace(ArtNom,'''','')+' '+isnull((select top 1 preartnom from artpre ap left join presentacionarticulos p on ap.preartcod=p.preartcod where preartfaccon='S' and ap.artsec=a.artsec ),'') ArtNom,isnull(ArtMedNomCom,'') ArtMedNomCom,g.InvGruCod,g.InvGruNom," +
                                "s.InvSubGruCod,s.InvSubGruNom,a.InvFamCod,InvFamNom,ParConIva,0 PrePreFijCosPro," +
                                "isnull((p1.PrePreFijVal),0) precio1," +
                                "isnull((p2.PrePreFijVal),0) precio2," +
                                "isnull((p3.PrePreFijVal),0) precio3," +
                                "isnull((p4.PrePreFijVal),0) precio4," +
                                "isnull((p5.PrePreFijVal),0) precio5," +
                                "isnull((p6.PrePreFijVal),0) precio6," +
                                "isnull((p7.PrePreFijVal),0) precio7," +
                                "isnull((p8.PrePreFijVal),0) precio8," +
                                "isnull((p9.PrePreFijVal),0) precio9," +
                                "isnull((p10.PrePreFijVal),0) precio10," +
                                "isnull((p11.PrePreFijVal),0) precio11," +
                                "isnull((p12.PrePreFijVal),0) precio12," +
                                "isnull((p13.PrePreFijVal),0) precio13," +
                                "isnull((p14.PrePreFijVal),0) precio14," +
                                "isnull((p15.PrePreFijVal),0) precio15," +
                                "isnull((p16.PrePreFijVal),0) precio16," +
                                "isnull((p17.PrePreFijVal),0) precio17," +
                                "isnull((p18.PrePreFijVal),0) precio18," +
                                "isnull((p19.PrePreFijVal),0) precio19," +
                                "isnull((p20.PrePreFijVal),0) precio20," +
                                "isnull((p21.PrePreFijVal),0) precio21," +
                                "isnull((p22.PrePreFijVal),0) precio22," +
                                "isnull((p23.PrePreFijVal),0) precio23," +
                                "isnull((p24.PrePreFijVal),0) precio24," +
                                "isnull((p25.PrePreFijVal),0) precio25," +
                                "isnull((p26.PrePreFijVal),0) precio26," +
                                "isnull((p27.PrePreFijVal),0) precio27," +
                                "isnull((p28.PrePreFijVal),0) precio28," +
                                "isnull((p29.PrePreFijVal),0) precio29," +
                                "isnull((p30.PrePreFijVal),0) precio30," +
                                "isnull((p1.PrePorVal),0) PrePorVal1," +
                                "isnull((p2.PrePorVal),0) PrePorVal2," +
                                "isnull((p3.PrePorVal),0) PrePorVal3," +
                                "isnull((p4.PrePorVal),0) PrePorVal4," +
                                "isnull((p5.PrePorVal),0) PrePorVal5," +
                                "isnull((p6.PrePorVal),0) PrePorVal6," +
                                "isnull((p7.PrePorVal),0) PrePorVal7," +
                                "isnull((p8.PrePorVal),0) PrePorVal8," +
                                "isnull((p9.PrePorVal),0) PrePorVal9," +
                                "isnull((select top 1 movparlindes from MovParLineaGrupos ff where movparlininvgrucod=s.invgrucod ),0) desc1," +   //and MOVPARLINDETFECINI<=getdate() and MOVPARLINDETFECFIN>=getdate()
                                "isnull((select top 1 MovParArtDetDesc from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc2," +
                                "isnull((select top 1 MovParArtDetDesc2 from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc2<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc5," +
                                "isnull((select top 1 MovParArtFecMod from MovParArtArticulos where MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) ),'') MovParArtFecMod" +
                                ",0 desc3,0 desc4,0 desc6,1000 Exist2 " +
                                ",isnull((select sum(artexiact) from articulosexi ae where ae.artsec=a.artsec and artbodcod in(select alinegbodsucccsec from carunidadesalinegbodsuc where alinegcod="+vAliNegCod+")),0) Exist " +
                                ",0 ExistFec " +
                                ",isnull(c.ClaArtCod,0) ClaArtCod,isnull(c.ClaArtNom,'') ClaArtNom,isnull(ArtValImp,0) ArtValImp,ArtEmb,isnull('N','') ArtIndMpm  " +
                                ",0 ArtRen,0 ArtLim " +
                                "from articulos a " +
                                "left join inventariofamilia f on a.invfamcod=f.invfamcod " +
                                "left join inventariosubgrupo s on f.invsubgrucod= s.invsubgrucod " +
                                "left join inventariogrupo g on g.invgrucod=s.invgrucod " +
                                "left join clasearticulo c on c.claartcod=a.claartcod " +
                                "left join parametrocontable p on p.parconcod=a.parconcod " +
                                "left join artpre pp on a.artsec=pp.artsec and PreArtFacCon='S' " +
                                " left join PreciosDetalle p1 on p1.ArtSec=a.ArtSec and p1.succod=1 and  p1.LisPreCod=1 and p1.PreEst='A' and P1.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p2 on p2.ArtSec=a.ArtSec and p2.succod=1 and  p2.LisPreCod=2 and p2.PreEst='A' and P2.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p3 on p3.ArtSec=a.ArtSec and p3.succod=1 and  p3.LisPreCod=3 and p3.PreEst='A' and P3.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p4 on p4.ArtSec=a.ArtSec and p4.succod=1 and  p4.LisPreCod=4 and p4.PreEst='A' and P4.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p5 on p5.ArtSec=a.ArtSec and p5.succod=1 and  p5.LisPreCod=5 and p5.PreEst='A' and P5.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p6 on p6.ArtSec=a.ArtSec and p6.succod=1 and  p6.LisPreCod=6 and p6.PreEst='A' and P6.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p7 on p7.ArtSec=a.ArtSec and p7.succod=1 and  p7.LisPreCod=7 and p7.PreEst='A' and P7.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p8 on p8.ArtSec=a.ArtSec and p8.succod=1 and  p8.LisPreCod=8 and p8.PreEst='A' and P8.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p9 on p9.ArtSec=a.ArtSec and p9.succod=1 and  p9.LisPreCod=9 and p9.PreEst='A' and P9.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p10 on p10.ArtSec=a.ArtSec and p10.succod=1 and  p10.LisPreCod=10 and p10.PreEst='A' and P10.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p11 on p11.ArtSec=a.ArtSec and p11.succod=1 and  p11.LisPreCod=11 and p11.PreEst='A' and P11.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p12 on p12.ArtSec=a.ArtSec and p12.succod=1 and  p12.LisPreCod=12 and p12.PreEst='A' and P12.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p13 on p13.ArtSec=a.ArtSec and p13.succod=1 and  p13.LisPreCod=13 and p13.PreEst='A' and P13.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p14 on p14.ArtSec=a.ArtSec and p14.succod=1 and  p14.LisPreCod=14 and p14.PreEst='A' and P14.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p15 on p15.ArtSec=a.ArtSec and p15.succod=1 and  p15.LisPreCod=15 and p15.PreEst='A' and P15.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p16 on p16.ArtSec=a.ArtSec and p16.succod=1 and  p16.LisPreCod=16 and p16.PreEst='A' and P16.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p17 on p17.ArtSec=a.ArtSec and p17.succod=1 and  p17.LisPreCod=17 and p17.PreEst='A' and P17.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p18 on p18.ArtSec=a.ArtSec and p18.succod=1 and  p18.LisPreCod=18 and p18.PreEst='A' and P18.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p19 on p19.ArtSec=a.ArtSec and p19.succod=1 and  p19.LisPreCod=19 and p19.PreEst='A' and P19.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p20 on p20.ArtSec=a.ArtSec and p20.succod=1 and  p20.LisPreCod=20 and p10.PreEst='A' and P20.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p21 on p21.ArtSec=a.ArtSec and p21.succod=1 and  p21.LisPreCod=21 and p11.PreEst='A' and P21.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p22 on p22.ArtSec=a.ArtSec and p22.succod=1 and  p22.LisPreCod=22 and p12.PreEst='A' and P22.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p23 on p23.ArtSec=a.ArtSec and p23.succod=1 and  p23.LisPreCod=23 and p13.PreEst='A' and P23.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p24 on p24.ArtSec=a.ArtSec and p24.succod=1 and  p24.LisPreCod=24 and p14.PreEst='A' and P24.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p25 on p25.ArtSec=a.ArtSec and p25.succod=1 and  p25.LisPreCod=25 and p15.PreEst='A' and P25.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p26 on p26.ArtSec=a.ArtSec and p26.succod=1 and  p26.LisPreCod=26 and p16.PreEst='A' and P26.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p27 on p27.ArtSec=a.ArtSec and p27.succod=1 and  p27.LisPreCod=27 and p17.PreEst='A' and P27.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p28 on p28.ArtSec=a.ArtSec and p28.succod=1 and  p28.LisPreCod=28 and p18.PreEst='A' and P28.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p29 on p29.ArtSec=a.ArtSec and p29.succod=1 and  p29.LisPreCod=29 and p19.PreEst='A' and P29.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p30 on p30.ArtSec=a.ArtSec and p30.succod=1 and  p30.LisPreCod=30 and p30.PreEst='A' and P30.PreArtCod=PP.PreArtCod" +
                                " WHERE a.artdes<>'S' " +
                                " and (( s.invgrucod in(select invgrucod from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "') or " +
                                " (select count(*) from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "')=0 ) or a.artsec in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes<>'S')) AND a.artsec NOT in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes='S')) Consulta order by Row desc";

                    } else if (vEmpresa.trim().equalsIgnoreCase("TODORAPIDAS")) {
                        //isnull((select .
                        //
                        // SUM(((karcaj*KARARTEMB)+karuni)*(case when (karnat='+') then 1 else -1 end)) saldo "+
                        // "from Kardex  k inner join Factura f on f.FacSec=k.facsec where facest='A' and k.ArtSec=a.ArtSec AND  SUBBODSUCCCSEC in(select bodsucccsec from bodegasucursalcc where succod=1)),0) Exist " +

                        Script = "select * from (select ROW_NUMBER() OVER(ORDER BY a.ArtSec ASC) AS Row,a.ArtSec,ArtCod,isnull((select top 1 case when artboddescu='S' then '*' else '' end artboddescu from artxbodsubbodsuccc cc where cc.artsec=a.artsec),'')+' '+replace(ArtNom,'''','')+' '+isnull((select top 1 preartnom from artpre ap left join presentacionarticulos p on ap.preartcod=p.preartcod where preartfaccon='S' and ap.artsec=a.artsec  ),'') ArtNom,isnull(ArtMedNomCom,'') ArtMedNomCom,g.InvGruCod,g.InvGruNom," +
                                "s.InvSubGruCod,s.InvSubGruNom,a.InvFamCod,InvFamNom,ParConIva,isnull(p1.PrePreFijCosPro,0) PrePreFijCosPro," +
                                "isnull((p1.PrePreFijVal)/(1+(ParConIva/100)),0) precio1," +
                                "isnull((p2.PrePreFijVal)/(1+(ParConIva/100)),0) precio2," +
                                "isnull((p3.PrePreFijVal)/(1+(ParConIva/100)),0) precio3," +
                                "isnull((p4.PrePreFijVal)/(1+(ParConIva/100)),0) precio4," +
                                "isnull((p5.PrePreFijVal)/(1+(ParConIva/100)),0) precio5," +
                                "isnull((p6.PrePreFijVal)/(1+(ParConIva/100)),0) precio6," +
                                "isnull((p7.PrePreFijVal)/(1+(ParConIva/100)),0) precio7," +
                                "isnull((p8.PrePreFijVal)/(1+(ParConIva/100)),0) precio8," +
                                "isnull((p9.PrePreFijVal)/(1+(ParConIva/100)),0) precio9," +
                                "0 precio10," +
                                "0 precio11," +
                                "0 precio12," +
                                "0 precio13," +
                                "0 precio14," +
                                "0 precio15," +
                                "0 precio16," +
                                "0 precio17," +
                                "0 precio18," +
                                "0 precio19," +
                                "0 precio20," +
                                "0 precio21," +
                                "0 precio22," +
                                "0 precio23," +
                                "0 precio24," +
                                "0 precio25," +
                                "0 precio26," +
                                "0 precio27," +
                                "0 precio28," +
                                "0 precio29," +
                                "0 precio30," +
                                "isnull((p1.PrePorVal),0) PrePorVal1," +
                                "isnull((p2.PrePorVal),0) PrePorVal2," +
                                "isnull((p3.PrePorVal),0) PrePorVal3," +
                                "isnull((p4.PrePorVal),0) PrePorVal4," +
                                "isnull((p5.PrePorVal),0) PrePorVal5," +
                                "isnull((p6.PrePorVal),0) PrePorVal6," +
                                "isnull((p7.PrePorVal),0) PrePorVal7," +
                                "isnull((p8.PrePorVal),0) PrePorVal8," +
                                "isnull((p9.PrePorVal),0) PrePorVal9," +
                                "isnull((select top 1 movparlindes from MovParLineaGrupos ff where movparlininvgrucod=s.invgrucod ),0) desc1," +   //and MOVPARLINDETFECINI<=getdate() and MOVPARLINDETFECFIN>=getdate()
                                "isnull((select top 1 MovParArtDetDesc from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc2," +
                                "isnull((select top 1 MovParArtDetDesc2 from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc2<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc5," +
                                "isnull((select top 1 MovParArtFecMod from MovParArtArticulos where MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) ),'') MovParArtFecMod" +
                                ",0 desc3,0 desc4,0 desc6,1000 Exist2 " +
                                ",isnull((select  SUM((((karcaj*KARARTEMB)+karuni)*KarPreFacCon)*(case when (karnat='+') then 1 when (karnat='-') then -1 else 0 end)) saldo "+
                                " from Kardex  k inner join Factura f on f.FacSec=k.facsec where facest='A' and k.ArtSec=a.ArtSec AND  SUBBODSUCCCSEC in(select bodsucccsec from bodegasucursalcc where succod=1)),0) Exist " +
                               // " ,0 Exist "+
                                ",0 ExistFec " +
                                ",isnull(c.ClaArtCod,0) ClaArtCod,isnull(c.ClaArtNom,'') ClaArtNom,isnull(ArtValImp,0) ArtValImp,ArtEmb,isnull(ArtIndMpm,'') ArtIndMpm  " +
                                ",case when artporren>0 then artporren when ISNULL(artporren,0)=0 and ISNULL(invfamren,0)>0 then invfamren when ISNULL(artporren,0)=0 and ISNULL(invfamren,0)=0 and ISNULL(invsubgruren,0)>0 then invsubgruren when ISNULL(artporren,0)=0 and ISNULL(invfamren,0)=0 and ISNULL(invsubgruren,0)=0 and invgruporrent>0 then invgruporrent else 0 end ArtRen  " +
                                ",case when artporlim>0 then artporlim when ISNULL(artporlim,0)=0 and ISNULL(invfamlim,0)>0 then invfamlim when ISNULL(artporlim,0)=0 and ISNULL(invfamlim,0)=0 and ISNULL(invsubgrulim,0)>0 then invsubgrulim when ISNULL(artporlim,0)=0 and ISNULL(invfamlim,0)=0 and ISNULL(invsubgrulim,0)=0 and invgruporlimit>0 then invgruporlimit else 0 end Artlim ,'N' InvGruDifCheck,'N' InvSubDifCheck,'N' InvFamDifCheck ,ArtCodBar " +
                                "from articulos a " +
                                "left join inventariofamilia f on a.invfamcod=f.invfamcod " +
                                "left join inventariosubgrupo s on f.invsubgrucod= s.invsubgrucod " +
                                "left join inventariogrupo g on g.invgrucod=s.invgrucod " +
                                "left join clasearticulo c on c.claartcod=a.claartcod " +
                                "left join parametrocontable p on p.parconcod=a.parconcod " +
                                "left join artpre pp on a.artsec=pp.artsec and PreArtFacCon='S' " +
                                " left join PreciosDetalle p1 on p1.ArtSec=a.ArtSec and p1.succod=1 and  p1.LisPreCod=1 and p1.PreEst='A' and P1.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p2 on p2.ArtSec=a.ArtSec and p2.succod=1 and  p2.LisPreCod=2 and p2.PreEst='A' and P2.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p3 on p3.ArtSec=a.ArtSec and p3.succod=1 and  p3.LisPreCod=3 and p3.PreEst='A' and P3.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p4 on p4.ArtSec=a.ArtSec and p4.succod=1 and  p4.LisPreCod=4 and p4.PreEst='A' and P4.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p5 on p5.ArtSec=a.ArtSec and p5.succod=1 and  p5.LisPreCod=5 and p5.PreEst='A' and P5.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p6 on p6.ArtSec=a.ArtSec and p6.succod=1 and  p6.LisPreCod=6 and p6.PreEst='A' and P6.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p7 on p7.ArtSec=a.ArtSec and p7.succod=1 and  p7.LisPreCod=7 and p7.PreEst='A' and P7.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p8 on p8.ArtSec=a.ArtSec and p8.succod=1 and  p8.LisPreCod=8 and p8.PreEst='A' and P8.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p9 on p9.ArtSec=a.ArtSec and p9.succod=1 and  p9.LisPreCod=9 and p9.PreEst='A' and P9.PreArtCod=PP.PreArtCod" +
                                " WHERE a.artdes<>'S' " +
                                " and (( s.invgrucod in(select invgrucod from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "') or " +
                                " (select count(*) from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "')=0 ) or a.artsec in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes<>'S') )AND a.artsec NOT in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes='S')) Consulta order by Row desc";
                        mensajeslargos("SQLTODO",Script);

                    } else if (vEmpresa.trim().equalsIgnoreCase("TATENDEMOS")) {
                        //isnull((select SUM(((karcaj*KARARTEMB)+karuni)*(case when (karnat='+') then 1 else -1 end)) saldo "+
                        // "from Kardex  k inner join Factura f on f.FacSec=k.facsec where facest='A' and k.ArtSec=a.ArtSec AND  SUBBODSUCCCSEC in(select bodsucccsec from bodegasucursalcc where succod=1)),0) Exist " +

                        Script = "select * from (select ROW_NUMBER() OVER(ORDER BY a.ArtSec ASC) AS Row,a.ArtSec,ArtCod,replace(ArtNom,'''','')+' '+isnull((select top 1 preartnom from artpre ap left join presentacionarticulos p on ap.preartcod=p.preartcod where preartfaccon='S' and ap.artsec=a.artsec  ),'') ArtNom,isnull(ArtMedNomCom,'') ArtMedNomCom,g.InvGruCod,g.InvGruNom," +
                                "s.InvSubGruCod,s.InvSubGruNom,a.InvFamCod,InvFamNom,ParConIva,isnull(p1.PrePreFijCosPro,0) PrePreFijCosPro," +
                                "isnull((a.artbalbas)/(1-(p1.preporval/100)),0) precio1," +
                                "isnull((a.artbalbas)/(1-(p2.preporval/100)),0) precio2," +
                                "isnull((a.artbalbas)/(1-(p3.preporval/100)),0) precio3," +
                                "isnull((a.artbalbas)/(1-(p4.preporval/100)),0) precio4," +
                                "isnull((a.artbalbas)/(1-(p5.preporval/100)),0) precio5," +
                                "isnull((a.artbalbas)/(1-(p6.preporval/100)),0) precio6," +
                                "isnull((a.artbalbas)/(1-(p7.preporval/100)),0) precio7," +
                                "isnull((a.artbalbas)/(1-(p8.preporval/100)),0) precio8," +
                                "isnull((a.artbalbas)/(1-(p9.preporval/100)),0) precio9," +
                                "0 precio10," +
                                "0 precio11," +
                                "0 precio12," +
                                "0 precio13," +
                                "0 precio14," +
                                "0 precio15," +
                                "0 precio16," +
                                "0 precio17," +
                                "0 precio18," +
                                "0 precio19," +
                                "0 precio20," +
                                "0 precio21," +
                                "0 precio22," +
                                "0 precio23," +
                                "0 precio24," +
                                "0 precio25," +
                                "0 precio26," +
                                "0 precio27," +
                                "0 precio28," +
                                "0 precio29," +
                                "0 precio30," +
                                "isnull((p1.PrePorVal),0) PrePorVal1," +
                                "isnull((p2.PrePorVal),0) PrePorVal2," +
                                "isnull((p3.PrePorVal),0) PrePorVal3," +
                                "isnull((p4.PrePorVal),0) PrePorVal4," +
                                "isnull((p5.PrePorVal),0) PrePorVal5," +
                                "isnull((p6.PrePorVal),0) PrePorVal6," +
                                "isnull((p7.PrePorVal),0) PrePorVal7," +
                                "isnull((p8.PrePorVal),0) PrePorVal8," +
                                "isnull((p9.PrePorVal),0) PrePorVal9," +
                                "isnull((select top 1 movparlindes from MovParLineaGrupos ff where movparlininvgrucod=s.invgrucod ),0) desc1," +   //and MOVPARLINDETFECINI<=getdate() and MOVPARLINDETFECFIN>=getdate()
                                "isnull((select top 1 MovParArtDetDesc from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc2," +
                                "isnull((select top 1 MovParArtDetDesc2 from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc2<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc5," +
                                "isnull((select top 1 MovParArtFecMod from MovParArtArticulos where MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) ),'') MovParArtFecMod" +
                                ",0 desc3,0 desc4,0 desc6,1000 Exist2 " +
                                ",isnull((select  SUM((((karcaj*KARARTEMB)+karuni)*KarPreFacCon)*(case when (karnat='+') then 1 when (karnat='-') then -1 else 0 end)) saldo "+
                                " from Kardex  k inner join Factura f on f.FacSec=k.facsec where facest='A' and k.ArtSec=a.ArtSec AND  SUBBODSUCCCSEC in(select alinegbodsucccsec from carunidadesalinegbodsuc where alinegcod="+vAliNegCod+")),0) Exist " +
                                ",0 ExistFec " +
                                ",isnull(c.ClaArtCod,0) ClaArtCod,isnull(c.ClaArtNom,'') ClaArtNom,isnull(ArtValImp,0) ArtValImp,ArtEmb,isnull(ArtIndMpm,'') ArtIndMpm  " +
                                ",case when artporren>0 then artporren when ISNULL(artporren,0)=0 and ISNULL(invfamren,0)>0 then invfamren when ISNULL(artporren,0)=0 and ISNULL(invfamren,0)=0 and ISNULL(invsubgruren,0)>0 then invsubgruren when ISNULL(artporren,0)=0 and ISNULL(invfamren,0)=0 and ISNULL(invsubgruren,0)=0 and invgruporrent>0 then invgruporrent else 0 end ArtRen  " +
                                ",case when artporlim>0 then artporlim when ISNULL(artporlim,0)=0 and ISNULL(invfamlim,0)>0 then invfamlim when ISNULL(artporlim,0)=0 and ISNULL(invfamlim,0)=0 and ISNULL(invsubgrulim,0)>0 then invsubgrulim when ISNULL(artporlim,0)=0 and ISNULL(invfamlim,0)=0 and ISNULL(invsubgrulim,0)=0 and invgruporlimit>0 then invgruporlimit else 0 end Artlim  " +
                                "from articulos a " +
                                "left join inventariofamilia f on a.invfamcod=f.invfamcod " +
                                "left join inventariosubgrupo s on f.invsubgrucod= s.invsubgrucod " +
                                "left join inventariogrupo g on g.invgrucod=s.invgrucod " +
                                "left join clasearticulo c on c.claartcod=a.claartcod " +
                                "left join parametrocontable p on p.parconcod=a.parconcod " +
                                "left join artpre pp on a.artsec=pp.artsec and PreArtFacCon='S' " +
                                " left join PreciosDetalle p1 on p1.ArtSec=a.ArtSec and p1.succod=1 and  p1.LisPreCod=1 and p1.PreEst='A' and P1.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p2 on p2.ArtSec=a.ArtSec and p2.succod=1 and  p2.LisPreCod=2 and p2.PreEst='A' and P2.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p3 on p3.ArtSec=a.ArtSec and p3.succod=1 and  p3.LisPreCod=3 and p3.PreEst='A' and P3.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p4 on p4.ArtSec=a.ArtSec and p4.succod=1 and  p4.LisPreCod=4 and p4.PreEst='A' and P4.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p5 on p5.ArtSec=a.ArtSec and p5.succod=1 and  p5.LisPreCod=5 and p5.PreEst='A' and P5.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p6 on p6.ArtSec=a.ArtSec and p6.succod=1 and  p6.LisPreCod=6 and p6.PreEst='A' and P6.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p7 on p7.ArtSec=a.ArtSec and p7.succod=1 and  p7.LisPreCod=7 and p7.PreEst='A' and P7.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p8 on p8.ArtSec=a.ArtSec and p8.succod=1 and  p8.LisPreCod=8 and p8.PreEst='A' and P8.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p9 on p9.ArtSec=a.ArtSec and p9.succod=1 and  p9.LisPreCod=9 and p9.PreEst='A' and P9.PreArtCod=PP.PreArtCod" +
                                " WHERE a.artdes<>'S' " +
                                " and (( s.invgrucod in(select invgrucod from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "') or " +
                                " (select count(*) from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "')=0 ) or a.artsec in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes<>'S') )AND a.artsec NOT in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes='S')) Consulta order by Row desc";
                    } else if (vEmpresa.trim().equalsIgnoreCase("IMPACTAMOS")) {
                    //isnull((select SUM(((karcaj*KARARTEMB)+karuni)*(case when (karnat='+') then 1 else -1 end)) saldo "+
                    // "from Kardex  k inner join Factura f on f.FacSec=k.facsec where facest='A' and k.ArtSec=a.ArtSec AND  SUBBODSUCCCSEC in(select bodsucccsec from bodegasucursalcc where succod=1)),0) Exist " +

                    Script = "select * from (select ROW_NUMBER() OVER(ORDER BY a.ArtSec ASC) AS Row,a.ArtSec,ArtCod,replace(ArtNom,'''','')+' '+isnull((select top 1 preartnom from artpre ap left join presentacionarticulos p on ap.preartcod=p.preartcod where preartfaccon='S' and ap.artsec=a.artsec  ),'') ArtNom,isnull(ArtMedNomCom,'') ArtMedNomCom,g.InvGruCod,g.InvGruNom," +
                            "s.InvSubGruCod,s.InvSubGruNom,a.InvFamCod,InvFamNom,ParConIva,isnull(p1.PrePreFijCosPro,0) PrePreFijCosPro," +
                            "isnull((a.artbalbas)/(1-(p1.preporval/100)),0) precio1," +
                            "isnull((a.artbalbas)/(1-(p2.preporval/100)),0) precio2," +
                            "isnull((a.artbalbas)/(1-(p3.preporval/100)),0) precio3," +
                            "isnull((a.artbalbas)/(1-(p4.preporval/100)),0) precio4," +
                            "isnull((a.artbalbas)/(1-(p5.preporval/100)),0) precio5," +
                            "isnull((a.artbalbas)/(1-(p6.preporval/100)),0) precio6," +
                            "isnull((a.artbalbas)/(1-(p7.preporval/100)),0) precio7," +
                            "isnull((a.artbalbas)/(1-(p8.preporval/100)),0) precio8," +
                            "isnull((a.artbalbas)/(1-(p9.preporval/100)),0) precio9," +
                            "0 precio10," +
                            "0 precio11," +
                            "0 precio12," +
                            "0 precio13," +
                            "0 precio14," +
                            "0 precio15," +
                            "0 precio16," +
                            "0 precio17," +
                            "0 precio18," +
                            "0 precio19," +
                            "0 precio20," +
                            "0 precio21," +
                            "0 precio22," +
                            "0 precio23," +
                            "0 precio24," +
                            "0 precio25," +
                            "0 precio26," +
                            "0 precio27," +
                            "0 precio28," +
                            "0 precio29," +
                            "0 precio30," +
                            "isnull((p1.PrePorVal),0) PrePorVal1," +
                            "isnull((p2.PrePorVal),0) PrePorVal2," +
                            "isnull((p3.PrePorVal),0) PrePorVal3," +
                            "isnull((p4.PrePorVal),0) PrePorVal4," +
                            "isnull((p5.PrePorVal),0) PrePorVal5," +
                            "isnull((p6.PrePorVal),0) PrePorVal6," +
                            "isnull((p7.PrePorVal),0) PrePorVal7," +
                            "isnull((p8.PrePorVal),0) PrePorVal8," +
                            "isnull((p9.PrePorVal),0) PrePorVal9," +
                            "isnull((select top 1 movparlindes from MovParLineaGrupos ff where movparlininvgrucod=s.invgrucod ),0) desc1," +   //and MOVPARLINDETFECINI<=getdate() and MOVPARLINDETFECFIN>=getdate()
                            "isnull((select top 1 MovParArtDetDesc from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                            "),0) desc2," +
                            "isnull((select top 1 MovParArtDetDesc2 from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc2<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                            "),0) desc5," +
                            "isnull((select top 1 MovParArtFecMod from MovParArtArticulos where MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) ),'') MovParArtFecMod" +
                            ",0 desc3,0 desc4,0 desc6,1000 Exist2 " +
                            ",isnull((select  SUM((((karcaj*KARARTEMB)+karuni)*KarPreFacCon)*(case when (karnat='+') then 1 when (karnat='-') then -1 else 0 end)) saldo "+
                            " from Kardex  k inner join Factura f on f.FacSec=k.facsec where facest='A' and k.ArtSec=a.ArtSec AND  SUBBODSUCCCSEC in(select alinegbodsucccsec from carunidadesalinegbodsuc where alinegcod="+vAliNegCod+")),0) Exist " +
                            ",0 ExistFec " +
                            ",isnull(c.ClaArtCod,0) ClaArtCod,isnull(c.ClaArtNom,'') ClaArtNom,isnull(ArtValImp,0) ArtValImp,ArtEmb,isnull(ArtIndMpm,'') ArtIndMpm  " +
                            ",case when artporren>0 then artporren when ISNULL(artporren,0)=0 and ISNULL(invfamren,0)>0 then invfamren when ISNULL(artporren,0)=0 and ISNULL(invfamren,0)=0 and ISNULL(invsubgruren,0)>0 then invsubgruren when ISNULL(artporren,0)=0 and ISNULL(invfamren,0)=0 and ISNULL(invsubgruren,0)=0 and invgruporrent>0 then invgruporrent else 0 end ArtRen  " +
                            ",case when artporlim>0 then artporlim when ISNULL(artporlim,0)=0 and ISNULL(invfamlim,0)>0 then invfamlim when ISNULL(artporlim,0)=0 and ISNULL(invfamlim,0)=0 and ISNULL(invsubgrulim,0)>0 then invsubgrulim when ISNULL(artporlim,0)=0 and ISNULL(invfamlim,0)=0 and ISNULL(invsubgrulim,0)=0 and invgruporlimit>0 then invgruporlimit else 0 end Artlim  " +
                            "from articulos a " +
                            "left join inventariofamilia f on a.invfamcod=f.invfamcod " +
                            "left join inventariosubgrupo s on f.invsubgrucod= s.invsubgrucod " +
                            "left join inventariogrupo g on g.invgrucod=s.invgrucod " +
                            "left join clasearticulo c on c.claartcod=a.claartcod " +
                            "left join parametrocontable p on p.parconcod=a.parconcod " +
                            "left join artpre pp on a.artsec=pp.artsec and PreArtFacCon='S' " +
                            " left join PreciosDetalle p1 on p1.ArtSec=a.ArtSec and p1.succod=1 and  p1.LisPreCod=1 and p1.PreEst='A' and P1.PreArtCod=PP.PreArtCod" +
                            " left join PreciosDetalle p2 on p2.ArtSec=a.ArtSec and p2.succod=1 and  p2.LisPreCod=2 and p2.PreEst='A' and P2.PreArtCod=PP.PreArtCod" +
                            " left join PreciosDetalle p3 on p3.ArtSec=a.ArtSec and p3.succod=1 and  p3.LisPreCod=3 and p3.PreEst='A' and P3.PreArtCod=PP.PreArtCod" +
                            " left join PreciosDetalle p4 on p4.ArtSec=a.ArtSec and p4.succod=1 and  p4.LisPreCod=4 and p4.PreEst='A' and P4.PreArtCod=PP.PreArtCod" +
                            " left join PreciosDetalle p5 on p5.ArtSec=a.ArtSec and p5.succod=1 and  p5.LisPreCod=5 and p5.PreEst='A' and P5.PreArtCod=PP.PreArtCod" +
                            " left join PreciosDetalle p6 on p6.ArtSec=a.ArtSec and p6.succod=1 and  p6.LisPreCod=6 and p6.PreEst='A' and P6.PreArtCod=PP.PreArtCod" +
                            " left join PreciosDetalle p7 on p7.ArtSec=a.ArtSec and p7.succod=1 and  p7.LisPreCod=7 and p7.PreEst='A' and P7.PreArtCod=PP.PreArtCod" +
                            " left join PreciosDetalle p8 on p8.ArtSec=a.ArtSec and p8.succod=1 and  p8.LisPreCod=8 and p8.PreEst='A' and P8.PreArtCod=PP.PreArtCod" +
                            " left join PreciosDetalle p9 on p9.ArtSec=a.ArtSec and p9.succod=1 and  p9.LisPreCod=9 and p9.PreEst='A' and P9.PreArtCod=PP.PreArtCod" +
                            " WHERE a.artdes<>'S' " +
                            " and (( s.invgrucod in(select invgrucod from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "') or " +
                            " (select count(*) from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "')=0 ) or a.artsec in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes<>'S') )AND a.artsec NOT in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes='S')) Consulta order by Row desc";
                }
                    else if (vEmpresa.trim().equalsIgnoreCase("BEHNER")) {
                        //isnull((select SUM(((karcaj*KARARTEMB)+karuni)*(case when (karnat='+') then 1 else -1 end)) saldo "+
                        // "from Kardex  k inner join Factura f on f.FacSec=k.facsec where facest='A' and k.ArtSec=a.ArtSec AND  SUBBODSUCCCSEC in(select bodsucccsec from bodegasucursalcc where succod=1)),0) Exist " +

                        Script = "select * from (select ROW_NUMBER() OVER(ORDER BY a.ArtSec ASC) AS Row,a.ArtSec,ArtCod,replace(ArtNom,'''','')+' '+isnull((select top 1 preartnom from artpre ap left join presentacionarticulos p on ap.preartcod=p.preartcod where preartfaccon='S' and ap.artsec=a.artsec ),'') ArtNom,isnull(ArtMedNomCom,'') ArtMedNomCom,g.InvGruCod,g.InvGruNom," +
                                "s.InvSubGruCod,s.InvSubGruNom,a.InvFamCod,InvFamNom,ParConIva,0 PrePreFijCosPro," +
                                "isnull((p1.PrePreFijVal),0) precio1," +
                                "isnull((p2.PrePreFijVal),0) precio2," +
                                "isnull((p3.PrePreFijVal),0) precio3," +
                                "isnull((p4.PrePreFijVal),0) precio4," +
                                "isnull((p5.PrePreFijVal),0) precio5," +
                                "isnull((p6.PrePreFijVal),0) precio6," +
                                "isnull((p7.PrePreFijVal),0) precio7," +
                                "isnull((p8.PrePreFijVal),0) precio8," +
                                "isnull((p9.PrePreFijVal),0) precio9," +
                                "0 precio10," +
                                "0 precio11," +
                                "0 precio12," +
                                "0 precio13," +
                                "0 precio14," +
                                "0 precio15," +
                                "0 precio16," +
                                "0 precio17," +
                                "0 precio18," +
                                "0 precio19," +
                                "0 precio20," +
                                "0 precio21," +
                                "0 precio22," +
                                "0 precio23," +
                                "0 precio24," +
                                "0 precio25," +
                                "0 precio26," +
                                "0 precio27," +
                                "0 precio28," +
                                "0 precio29," +
                                "0 precio30," +
                                "isnull((p1.PrePorVal),0) PrePorVal1," +
                                "isnull((p2.PrePorVal),0) PrePorVal2," +
                                "isnull((p3.PrePorVal),0) PrePorVal3," +
                                "isnull((p4.PrePorVal),0) PrePorVal4," +
                                "isnull((p5.PrePorVal),0) PrePorVal5," +
                                "isnull((p6.PrePorVal),0) PrePorVal6," +
                                "isnull((p7.PrePorVal),0) PrePorVal7," +
                                "isnull((p8.PrePorVal),0) PrePorVal8," +
                                "isnull((p9.PrePorVal),0) PrePorVal9," +
                                "isnull((select top 1 movparlindes from MovParLineaGrupos ff where movparlininvgrucod=s.invgrucod ),0) desc1," +   //and MOVPARLINDETFECINI<=getdate() and MOVPARLINDETFECFIN>=getdate()
                                "isnull((select top 1 MovParArtDetDesc from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc2," +
                                "isnull((select top 1 MovParArtDetDesc2 from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc2<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc5," +
                                "isnull((select top 1 MovParArtFecMod from MovParArtArticulos where MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) ),'') MovParArtFecMod" +
                                ",0 desc3,0 desc4,0 desc6,1000 Exist2 " +
                                ",isnull((select sum(artexiact) from articulosexi ae where ae.artsec=a.artsec and artbodcod in(select alinegbodsucccsec from carunidadesalinegbodsuc where alinegcod="+vAliNegCod+")),0)+10000 Exist " +
                                ",0 ExistFec " +
                                ",isnull(c.ClaArtCod,0) ClaArtCod,isnull(c.ClaArtNom,'') ClaArtNom,isnull(ArtValImp,0) ArtValImp,ArtEmb,isnull(ArtIndMpm,'') ArtIndMpm  " +
                                ",0 ArtRen,0 ArtLim " +
                                "from articulos a " +
                                "left join inventariofamilia f on a.invfamcod=f.invfamcod " +
                                "left join inventariosubgrupo s on f.invsubgrucod= s.invsubgrucod " +
                                "left join inventariogrupo g on g.invgrucod=s.invgrucod " +
                                "left join clasearticulo c on c.claartcod=a.claartcod " +
                                "left join parametrocontable p on p.parconcod=a.parconcod " +
                                "left join artpre pp on a.artsec=pp.artsec and PreArtFacCon='S' " +
                                " left join PreciosDetalle p1 on p1.ArtSec=a.ArtSec and p1.succod=1 and  p1.LisPreCod=7 and p1.PreEst='A' and P1.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p2 on p2.ArtSec=a.ArtSec and p2.succod=1 and  p2.LisPreCod=7 and p2.PreEst='A' and P2.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p3 on p3.ArtSec=a.ArtSec and p3.succod=1 and  p3.LisPreCod=7 and p3.PreEst='A' and P3.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p4 on p4.ArtSec=a.ArtSec and p4.succod=1 and  p4.LisPreCod=7 and p4.PreEst='A' and P4.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p5 on p5.ArtSec=a.ArtSec and p5.succod=1 and  p5.LisPreCod=7 and p5.PreEst='A' and P5.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p6 on p6.ArtSec=a.ArtSec and p6.succod=1 and  p6.LisPreCod=7 and p6.PreEst='A' and P6.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p7 on p7.ArtSec=a.ArtSec and p7.succod=1 and  p7.LisPreCod=7 and p7.PreEst='A' and P7.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p8 on p8.ArtSec=a.ArtSec and p8.succod=1 and  p8.LisPreCod=7 and p8.PreEst='A' and P8.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p9 on p9.ArtSec=a.ArtSec and p9.succod=1 and  p9.LisPreCod=7 and p9.PreEst='A' and P9.PreArtCod=PP.PreArtCod" +
                                " WHERE a.artdes<>'S' " +
                                " and ( s.invgrucod in(select invgrucod from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "') or " +
                                " (select count(*) from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "')=0 ) or a.artsec in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes<>'S') AND a.artsec NOT in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes='S')) Consulta order by Row desc";
                    } else if (vEmpresa.trim().equalsIgnoreCase("DYD")) {
                        //isnull((select SUM(((karcaj*KARARTEMB)+karuni)*(case when (karnat='+') then 1 else -1 end)) saldo "+
                        // "from Kardex  k inner join Factura f on f.FacSec=k.facsec where facest='A' and k.ArtSec=a.ArtSec AND  SUBBODSUCCCSEC in(select bodsucccsec from bodegasucursalcc where succod=1)),0) Exist " +

                        Script = "select * from (select ROW_NUMBER() OVER(ORDER BY a.ArtSec ASC) AS Row,a.ArtSec,ArtCod,replace(ArtNom,'''','')+' '+isnull((select top 1 preartnom from artpre ap left join presentacionarticulos p on ap.preartcod=p.preartcod where preartfaccon='S' and ap.artsec=a.artsec ),'') ArtNom,isnull(ArtMedNomCom,'') ArtMedNomCom,g.InvGruCod,g.InvGruNom," +
                                "s.InvSubGruCod,s.InvSubGruNom,a.InvFamCod,InvFamNom,ParConIva,0 PrePreFijCosPro," +
                                "isnull((p1.PrePreFijValMOV),0) precio1," +
                                "isnull((p2.PrePreFijValMOV),0) precio2," +
                                "isnull((p3.PrePreFijValMOV),0) precio3," +
                                "isnull((p4.PrePreFijValMOV),0) precio4," +
                                "isnull((p5.PrePreFijValMOV),0) precio5," +
                                "isnull((p6.PrePreFijValMOV),0) precio6," +
                                "isnull((p7.PrePreFijValMOV),0) precio7," +
                                "isnull((p8.PrePreFijValMOV),0) precio8," +
                                "isnull((p9.PrePreFijValMOV),0) precio9," +
                                "0 precio10," +
                                "0 precio11," +
                                "0 precio12," +
                                "0 precio13," +
                                "0 precio14," +
                                "0 precio15," +
                                "0 precio16," +
                                "0 precio17," +
                                "0 precio18," +
                                "0 precio19," +
                                "0 precio20," +
                                "0 precio21," +
                                "0 precio22," +
                                "0 precio23," +
                                "0 precio24," +
                                "0 precio25," +
                                "0 precio26," +
                                "0 precio27," +
                                "0 precio28," +
                                "0 precio29," +
                                "0 precio30," +
                                "isnull((p1.PrePorVal),0) PrePorVal1," +
                                "isnull((p2.PrePorVal),0) PrePorVal2," +
                                "isnull((p3.PrePorVal),0) PrePorVal3," +
                                "isnull((p4.PrePorVal),0) PrePorVal4," +
                                "isnull((p5.PrePorVal),0) PrePorVal5," +
                                "isnull((p6.PrePorVal),0) PrePorVal6," +
                                "isnull((p7.PrePorVal),0) PrePorVal7," +
                                "isnull((p8.PrePorVal),0) PrePorVal8," +
                                "isnull((p9.PrePorVal),0) PrePorVal9," +
                                "isnull((select top 1 movparlindes from MovParLineaGrupos ff where movparlininvgrucod=s.invgrucod ),0) desc1," +   //and MOVPARLINDETFECINI<=getdate() and MOVPARLINDETFECFIN>=getdate()
                                "isnull((select top 1 MovParArtDetDesc from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc2," +
                                "isnull((select top 1 MovParArtDetDesc2 from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc2<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc5," +
                                "isnull((select top 1 MovParArtFecMod from MovParArtArticulos where MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) ),'') MovParArtFecMod" +
                                ",0 desc3,0 desc4,0 desc6,1000 Exist2 " +
                                ",isnull((select sum(artexiact) from articulosexi ae where ae.artsec=a.artsec and artbodcod in(select alinegbodsucccsec from carunidadesalinegbodsuc where alinegcod="+vAliNegCod+")),0) Exist " +
                                ",0 ExistFec " +
                                ",isnull(c.ClaArtCod,0) ClaArtCod,isnull(c.ClaArtNom,'') ClaArtNom,isnull(ArtValImp,0) ArtValImp,ArtEmb,isnull(ArtIndMpm,'') ArtIndMpm  " +
                                ",0 ArtRen,0 ArtLim " +
                                "from articulos a " +
                                "left join inventariofamilia f on a.invfamcod=f.invfamcod " +
                                "left join inventariosubgrupo s on f.invsubgrucod= s.invsubgrucod " +
                                "left join inventariogrupo g on g.invgrucod=s.invgrucod " +
                                "left join clasearticulo c on c.claartcod=a.claartcod " +
                                "left join parametrocontable p on p.parconcod=a.parconcod " +
                                "left join artpre pp on a.artsec=pp.artsec and PreArtFacCon='S' " +
                                " left join PreciosDetalle p1 on p1.ArtSec=a.ArtSec and p1.succod=1 and  p1.LisPreCod=1 and p1.PreEst='A' and P1.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p2 on p2.ArtSec=a.ArtSec and p2.succod=1 and  p2.LisPreCod=2 and p2.PreEst='A' and P2.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p3 on p3.ArtSec=a.ArtSec and p3.succod=1 and  p3.LisPreCod=3 and p3.PreEst='A' and P3.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p4 on p4.ArtSec=a.ArtSec and p4.succod=1 and  p4.LisPreCod=4 and p4.PreEst='A' and P4.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p5 on p5.ArtSec=a.ArtSec and p5.succod=1 and  p5.LisPreCod=5 and p5.PreEst='A' and P5.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p6 on p6.ArtSec=a.ArtSec and p6.succod=1 and  p6.LisPreCod=6 and p6.PreEst='A' and P6.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p7 on p7.ArtSec=a.ArtSec and p7.succod=1 and  p7.LisPreCod=7 and p7.PreEst='A' and P7.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p8 on p8.ArtSec=a.ArtSec and p8.succod=1 and  p8.LisPreCod=8 and p8.PreEst='A' and P8.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p9 on p9.ArtSec=a.ArtSec and p9.succod=1 and  p9.LisPreCod=9 and p9.PreEst='A' and P9.PreArtCod=PP.PreArtCod" +
                                " WHERE a.artdes<>'S' " +
                                " and ( s.invgrucod in(select invgrucod from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "') or " +
                                " (select count(*) from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "')=0 ) or a.artsec in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes<>'S') AND a.artsec NOT in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes='S')) Consulta order by Row desc";

                    }else if (vEmpresa.trim().equalsIgnoreCase("BRILLO")) {
                        Script = "select * from (select ROW_NUMBER() OVER(ORDER BY a.ArtSec ASC) AS Row,a.ArtSec,ArtCod,replace(ArtNom,'''','')+' '+isnull((select top 1 preartnom from artpre ap left join presentacionarticulos p on ap.preartcod=p.preartcod where preartfaccon='S' and ap.artsec=a.artsec ),'') ArtNom,isnull(ArtMedNomCom,'') ArtMedNomCom,g.InvGruCod,g.InvGruNom," +
                                "s.InvSubGruCod,s.InvSubGruNom,a.InvFamCod,InvFamNom,ParConIva,0 PrePreFijCosPro," +
                                "isnull((p1.PrePreFijVal),0) precio1," +
                                "isnull((p2.PrePreFijVal),0) precio2," +
                                "isnull((p3.PrePreFijVal),0) precio3," +
                                "isnull((p4.PrePreFijVal),0) precio4," +
                                "isnull((p5.PrePreFijVal),0) precio5," +
                                "isnull((p6.PrePreFijVal),0) precio6," +
                                "isnull((p7.PrePreFijVal),0) precio7," +
                                "isnull((p8.PrePreFijVal),0) precio8," +
                                "isnull((p9.PrePreFijVal),0) precio9," +
                                "0 precio10," +
                                "0 precio11," +
                                "0 precio12," +
                                "0 precio13," +
                                "0 precio14," +
                                "0 precio15," +
                                "0 precio16," +
                                "0 precio17," +
                                "0 precio18," +
                                "0 precio19," +
                                "0 precio20," +
                                "0 precio21," +
                                "0 precio22," +
                                "0 precio23," +
                                "0 precio24," +
                                "0 precio25," +
                                "0 precio26," +
                                "0 precio27," +
                                "0 precio28," +
                                "0 precio29," +
                                "0 precio30," +
                                "isnull((p1.PrePorVal),0) PrePorVal1," +
                                "isnull((p2.PrePorVal),0) PrePorVal2," +
                                "isnull((p3.PrePorVal),0) PrePorVal3," +
                                "isnull((p4.PrePorVal),0) PrePorVal4," +
                                "isnull((p5.PrePorVal),0) PrePorVal5," +
                                "isnull((p6.PrePorVal),0) PrePorVal6," +
                                "isnull((p7.PrePorVal),0) PrePorVal7," +
                                "isnull((p8.PrePorVal),0) PrePorVal8," +
                                "isnull((p9.PrePorVal),0) PrePorVal9," +
                                //"isnull((select top 1 movparlindes from MovParLineaGrupos ff left join movparlinea ml on ml.movparlinsec=ff.movparlinsec where movparlinencinvgrucod=s.invgrucod and MOVPARLINDETFECINI<=CONVERT(date, GETDATE()) and MOVPARLINDETFECFIN>=CONVERT(date, GETDATE()) ),0) desc1," +   //
                                "isnull(p1.PreDetDes1,0) desc1," +
                                "isnull((select top 1 MovParArtDetDesc from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc2," +
                                "isnull((select top 1 MovParArtDetDesc2 from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc2<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc5," +
                                "isnull((select top 1 MovParArtFecMod from MovParArtArticulos where MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) ),'') MovParArtFecMod" +
                                ",0 desc3,0 desc4,0 desc6,1000 Exist2 " +
                                ",isnull((select sum(artexiact) from articulosexi ae where ae.artsec=a.artsec and artbodcod in(select alinegbodsucccsec from carunidadesalinegbodsuc where alinegcod="+vAliNegCod+")),0) Exist " +
                                ",0 ExistFec " +
                                ",isnull(c.ClaArtCod,0) ClaArtCod,isnull(c.ClaArtNom,'') ClaArtNom,isnull(ArtValImp,0) ArtValImp,ArtEmb,isnull(ArtIndMpm,'') ArtIndMpm  " +
                                ",0 ArtRen,0 ArtLim ,'N' InvGruDifCheck,'N' InvSubDifCheck,'N' InvFamDifCheck , ArtCodBar " +
                                "from articulos a " +
                                "left join inventariofamilia f on a.invfamcod=f.invfamcod " +
                                "left join inventariosubgrupo s on f.invsubgrucod= s.invsubgrucod " +
                                "left join inventariogrupo g on g.invgrucod=s.invgrucod " +
                                "left join clasearticulo c on c.claartcod=a.claartcod " +
                                "left join parametrocontable p on p.parconcod=a.parconcod " +
                                "left join artpre pp on a.artsec=pp.artsec and PreArtFacCon='S' " +
                                " left join PreciosDetalle p1 on p1.ArtSec=a.ArtSec and p1.succod=1 and  p1.LisPreCod=1 and p1.PreEst='A' and P1.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p2 on p2.ArtSec=a.ArtSec and p2.succod=1 and  p2.LisPreCod=2 and p2.PreEst='A' and P2.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p3 on p3.ArtSec=a.ArtSec and p3.succod=1 and  p3.LisPreCod=3 and p3.PreEst='A' and P3.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p4 on p4.ArtSec=a.ArtSec and p4.succod=1 and  p4.LisPreCod=4 and p4.PreEst='A' and P4.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p5 on p5.ArtSec=a.ArtSec and p5.succod=1 and  p5.LisPreCod=5 and p5.PreEst='A' and P5.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p6 on p6.ArtSec=a.ArtSec and p6.succod=1 and  p6.LisPreCod=6 and p6.PreEst='A' and P6.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p7 on p7.ArtSec=a.ArtSec and p7.succod=1 and  p7.LisPreCod=7 and p7.PreEst='A' and P7.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p8 on p8.ArtSec=a.ArtSec and p8.succod=1 and  p8.LisPreCod=8 and p8.PreEst='A' and P8.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p9 on p9.ArtSec=a.ArtSec and p9.succod=1 and  p9.LisPreCod=9 and p9.PreEst='A' and P9.PreArtCod=PP.PreArtCod" +
                                " WHERE a.artdes<>'S' " +
                                " and ( s.invgrucod in(select invgrucod from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "') or " +
                                " (select count(*) from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "')=0 ) or a.artsec in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes<>'S') AND a.artsec NOT in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes='S')) Consulta order by Row desc";
                    }else if (vEmpresa.trim().equalsIgnoreCase("REDEMOTOS")) {
                        Script = "select * from (select ROW_NUMBER() OVER(ORDER BY a.ArtSec ASC) AS Row,a.ArtSec,ArtCod,'*'+a.ArtSec+'* '+replace(ArtNom,'''','')+' '+isnull((select top 1 preartnom from artpre ap left join presentacionarticulos p on ap.preartcod=p.preartcod where preartfaccon='S' and ap.artsec=a.artsec ),'') ArtNom,isnull(ArtMedNomCom,'') ArtMedNomCom,g.InvGruCod,g.InvGruNom," +
                                "s.InvSubGruCod,s.InvSubGruNom,a.InvFamCod,InvFamNom,ParConIva,0 PrePreFijCosPro," +
                                "isnull((p1.PrePreFijVal),0) precio1," +
                                "isnull((p2.PrePreFijVal),0) precio2," +
                                "isnull((p3.PrePreFijVal),0) precio3," +
                                "isnull((p4.PrePreFijVal),0) precio4," +
                                "isnull((p5.PrePreFijVal),0) precio5," +
                                "isnull((p6.PrePreFijVal),0) precio6," +
                                "isnull((p7.PrePreFijVal),0) precio7," +
                                "isnull((p8.PrePreFijVal),0) precio8," +
                                "isnull((p9.PrePreFijVal),0) precio9," +
                                "0 precio10," +
                                "0 precio11," +
                                "0 precio12," +
                                "0 precio13," +
                                "0 precio14," +
                                "0 precio15," +
                                "0 precio16," +
                                "0 precio17," +
                                "0 precio18," +
                                "0 precio19," +
                                "0 precio20," +
                                "0 precio21," +
                                "0 precio22," +
                                "0 precio23," +
                                "0 precio24," +
                                "0 precio25," +
                                "0 precio26," +
                                "0 precio27," +
                                "0 precio28," +
                                "0 precio29," +
                                "0 precio30," +
                                "isnull((p1.PrePorVal),0) PrePorVal1," +
                                "isnull((p2.PrePorVal),0) PrePorVal2," +
                                "isnull((p3.PrePorVal),0) PrePorVal3," +
                                "isnull((p4.PrePorVal),0) PrePorVal4," +
                                "isnull((p5.PrePorVal),0) PrePorVal5," +
                                "isnull((p6.PrePorVal),0) PrePorVal6," +
                                "isnull((p7.PrePorVal),0) PrePorVal7," +
                                "isnull((p8.PrePorVal),0) PrePorVal8," +
                                "isnull((p9.PrePorVal),0) PrePorVal9," +
                                //"isnull((select top 1 movparlindes from MovParLineaGrupos ff where movparlininvgrucod=s.invgrucod ),0) desc1," +
                                "isnull((select top 1 movparlindes from MovParLineaGrupos ff left join movparlinea ml on ml.movparlinsec=ff.movparlinsec where movparlinencinvgrucod=s.invgrucod and MOVPARLINDETFECINI<=CONVERT(date, GETDATE()) and MOVPARLINDETFECFIN>=CONVERT(date, GETDATE()) ),0) desc1," +
                                "isnull((select top 1 MovParArtDetDesc from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc2," +
                                "isnull((select top 1 MovParArtDetDesc2 from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc2<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc5," +
                                "isnull((select top 1 MovParArtFecMod from MovParArtArticulos where MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) ),'') MovParArtFecMod" +
                                ",0 desc3,0 desc4,0 desc6,1000 Exist2 " +
                                ",isnull((select sum(artexiact) from articulosexi ae where ae.artsec=a.artsec and artbodcod in(select alinegbodsucccsec from carunidadesalinegbodsuc where alinegcod="+vAliNegCod+")),0) Exist " +
                                ",0 ExistFec " +
                                ",isnull(c.ClaArtCod,0) ClaArtCod,isnull(c.ClaArtNom,'') ClaArtNom,isnull(ArtValImp,0) ArtValImp,ArtEmb,isnull(ArtIndMpm,'') ArtIndMpm  " +
                                ",0 ArtRen,0 ArtLim " +
                                "from articulos a " +
                                "left join inventariofamilia f on a.invfamcod=f.invfamcod " +
                                "left join inventariosubgrupo s on f.invsubgrucod= s.invsubgrucod " +
                                "left join inventariogrupo g on g.invgrucod=s.invgrucod " +
                                "left join clasearticulo c on c.claartcod=a.claartcod " +
                                "left join parametrocontable p on p.parconcod=a.parconcod " +
                                "left join artpre pp on a.artsec=pp.artsec and PreArtFacCon='S' " +
                                " left join PreciosDetalle p1 on p1.ArtSec=a.ArtSec and p1.succod=1 and  p1.LisPreCod=1 and p1.PreEst='A' and P1.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p2 on p2.ArtSec=a.ArtSec and p2.succod=1 and  p2.LisPreCod=2 and p2.PreEst='A' and P2.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p3 on p3.ArtSec=a.ArtSec and p3.succod=1 and  p3.LisPreCod=3 and p3.PreEst='A' and P3.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p4 on p4.ArtSec=a.ArtSec and p4.succod=1 and  p4.LisPreCod=4 and p4.PreEst='A' and P4.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p5 on p5.ArtSec=a.ArtSec and p5.succod=1 and  p5.LisPreCod=5 and p5.PreEst='A' and P5.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p6 on p6.ArtSec=a.ArtSec and p6.succod=1 and  p6.LisPreCod=6 and p6.PreEst='A' and P6.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p7 on p7.ArtSec=a.ArtSec and p7.succod=1 and  p7.LisPreCod=7 and p7.PreEst='A' and P7.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p8 on p8.ArtSec=a.ArtSec and p8.succod=1 and  p8.LisPreCod=8 and p8.PreEst='A' and P8.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p9 on p9.ArtSec=a.ArtSec and p9.succod=1 and  p9.LisPreCod=9 and p9.PreEst='A' and P9.PreArtCod=PP.PreArtCod" +
                                " WHERE a.artdes<>'S' " +
                                " and ( s.invgrucod in(select invgrucod from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "') or " +
                                " (select count(*) from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "')=0 ) or a.artsec in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes<>'S') AND a.artsec NOT in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes='S')) Consulta order by Row desc";
                    }else if (vEmpresa.trim().equalsIgnoreCase("BOSCONIA")) {
                        Script = "select * from (select ROW_NUMBER() OVER(ORDER BY a.ArtSec ASC) AS Row,a.ArtSec,ArtCod,replace(ArtNom,'''','')+' '+isnull((select top 1 preartnom from artpre ap left join presentacionarticulos p on ap.preartcod=p.preartcod where preartfaccon='S' and ap.artsec=a.artsec ),'') ArtNom,isnull(ArtMedNomCom,'') ArtMedNomCom,g.InvGruCod,g.InvGruNom," +
                                "s.InvSubGruCod,s.InvSubGruNom,a.InvFamCod,InvFamNom,ParConIva,0 PrePreFijCosPro," +
                                "isnull((p1.PrePreFijVal),0) precio1," +
                                "isnull((p2.PrePreFijVal),0) precio2," +
                                "isnull((p3.PrePreFijVal),0) precio3," +
                                "isnull((p4.PrePreFijVal),0) precio4," +
                                "isnull((p5.PrePreFijVal),0) precio5," +
                                "isnull((p6.PrePreFijVal),0) precio6," +
                                "isnull((p7.PrePreFijVal),0) precio7," +
                                "isnull((p8.PrePreFijVal),0) precio8," +
                                "isnull((p9.PrePreFijVal),0) precio9," +
                                "0 precio10," +
                                "0 precio11," +
                                "0 precio12," +
                                "0 precio13," +
                                "0 precio14," +
                                "0 precio15," +
                                "0 precio16," +
                                "0 precio17," +
                                "0 precio18," +
                                "0 precio19," +
                                "0 precio20," +
                                "0 precio21," +
                                "0 precio22," +
                                "0 precio23," +
                                "0 precio24," +
                                "0 precio25," +
                                "0 precio26," +
                                "0 precio27," +
                                "0 precio28," +
                                "0 precio29," +
                                "0 precio30," +
                                "isnull((p1.PrePorVal),0) PrePorVal1," +
                                "isnull((p2.PrePorVal),0) PrePorVal2," +
                                "isnull((p3.PrePorVal),0) PrePorVal3," +
                                "isnull((p4.PrePorVal),0) PrePorVal4," +
                                "isnull((p5.PrePorVal),0) PrePorVal5," +
                                "isnull((p6.PrePorVal),0) PrePorVal6," +
                                "isnull((p7.PrePorVal),0) PrePorVal7," +
                                "isnull((p8.PrePorVal),0) PrePorVal8," +
                                "isnull((p9.PrePorVal),0) PrePorVal9," +
                                //"isnull((select top 1 movparlindes from MovParLineaGrupos ff where movparlininvgrucod=s.invgrucod ),0) desc1," +
                                "isnull((select top 1 movparlindes from MovParLineaGrupos ff left join movparlinea ml on ml.movparlinsec=ff.movparlinsec where movparlinencinvgrucod=s.invgrucod and MOVPARLINDETFECINI<=CONVERT(date, GETDATE()) and MOVPARLINDETFECFIN>=CONVERT(date, GETDATE()) ),0) desc1," +
                                "isnull((select top 1 MovParArtDetDesc from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc2," +
                                "isnull((select top 1 MovParArtDetDesc2 from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc2<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc5," +
                                "isnull((select top 1 MovParArtFecMod from MovParArtArticulos where MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) ),'') MovParArtFecMod" +
                                ",0 desc3,0 desc4,0 desc6,1000 Exist2 " +
                                ",isnull((select sum(artexiact) from articulosexi ae where ae.artsec=a.artsec and artbodcod in(select alinegbodsucccsec from carunidadesalinegbodsuc where alinegcod="+vAliNegCod+")),0) Exist " +
                                ",0 ExistFec " +
                                ",isnull(c.ClaArtCod,0) ClaArtCod,isnull(c.ClaArtNom,'') ClaArtNom,isnull(ArtValImp,0) ArtValImp,ArtEmb,isnull(ArtIndMpm,'') ArtIndMpm  " +
                                ",0 ArtRen,0 ArtLim " +
                                "from articulos a " +
                                "left join inventariofamilia f on a.invfamcod=f.invfamcod " +
                                "left join inventariosubgrupo s on f.invsubgrucod= s.invsubgrucod " +
                                "left join inventariogrupo g on g.invgrucod=s.invgrucod " +
                                "left join clasearticulo c on c.claartcod=a.claartcod " +
                                "left join parametrocontable p on p.parconcod=a.parconcod " +
                                "left join artpre pp on a.artsec=pp.artsec and PreArtFacCon='S' " +
                                " left join PreciosDetalle p1 on p1.ArtSec=a.ArtSec and p1.succod=1 and  p1.LisPreCod=1 and p1.PreEst='A' and P1.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p2 on p2.ArtSec=a.ArtSec and p2.succod=1 and  p2.LisPreCod=2 and p2.PreEst='A' and P2.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p3 on p3.ArtSec=a.ArtSec and p3.succod=1 and  p3.LisPreCod=3 and p3.PreEst='A' and P3.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p4 on p4.ArtSec=a.ArtSec and p4.succod=1 and  p4.LisPreCod=4 and p4.PreEst='A' and P4.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p5 on p5.ArtSec=a.ArtSec and p5.succod=1 and  p5.LisPreCod=5 and p5.PreEst='A' and P5.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p6 on p6.ArtSec=a.ArtSec and p6.succod=1 and  p6.LisPreCod=6 and p6.PreEst='A' and P6.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p7 on p7.ArtSec=a.ArtSec and p7.succod=1 and  p7.LisPreCod=7 and p7.PreEst='A' and P7.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p8 on p8.ArtSec=a.ArtSec and p8.succod=1 and  p8.LisPreCod=8 and p8.PreEst='A' and P8.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p9 on p9.ArtSec=a.ArtSec and p9.succod=1 and  p9.LisPreCod=9 and p9.PreEst='A' and P9.PreArtCod=PP.PreArtCod" +
                                " WHERE a.artdes<>'S'  AND isnull((select sum(artexiact) from articulosexi ae where ae.artsec=a.artsec and artbodcod in(select alinegbodsucccsec from carunidadesalinegbodsuc where alinegcod="+vAliNegCod+")),0)>0 " +
                                " and ( s.invgrucod in(select invgrucod from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "') or " +
                                " (select count(*) from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "')=0 ) or a.artsec in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes<>'S') AND a.artsec NOT in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes='S') ) Consulta order by Row desc";
                    }else if (vEmpresa.trim().equalsIgnoreCase("PROMEFAR")) {
                        Script = "select * from (select ROW_NUMBER() OVER(ORDER BY a.ArtSec ASC) AS Row,a.ArtSec,ArtCod,replace(ArtNom,'''','')+' '+isnull((select top 1 preartnom from artpre ap left join presentacionarticulos p on ap.preartcod=p.preartcod where preartfaccon='S' and ap.artsec=a.artsec ),'') ArtNom,isnull(ArtMedNomCom,'') ArtMedNomCom,g.InvGruCod,g.InvGruNom," +
                                "s.InvSubGruCod,s.InvSubGruNom,a.InvFamCod,InvFamNom,ParConIva,0 PrePreFijCosPro," +
                                "isnull((p1.PrePreFijValMOV),0) precio1," +
                                "isnull((p2.PrePreFijValMOV),0) precio2," +
                                "isnull((p3.PrePreFijValMOV),0) precio3," +
                                "isnull((p4.PrePreFijValMOV),0) precio4," +
                                "isnull((p5.PrePreFijValMOV),0) precio5," +
                                "isnull((p6.PrePreFijValMOV),0) precio6," +
                                "isnull((p7.PrePreFijValMOV),0) precio7," +
                                "isnull((p8.PrePreFijValMOV),0) precio8," +
                                "isnull((p9.PrePreFijValMOV),0) precio9," +
                                "0 precio10," +
                                "0 precio11," +
                                "0 precio12," +
                                "0 precio13," +
                                "0 precio14," +
                                "0 precio15," +
                                "0 precio16," +
                                "0 precio17," +
                                "0 precio18," +
                                "0 precio19," +
                                "0 precio20," +
                                "0 precio21," +
                                "0 precio22," +
                                "0 precio23," +
                                "0 precio24," +
                                "0 precio25," +
                                "0 precio26," +
                                "0 precio27," +
                                "0 precio28," +
                                "0 precio29," +
                                "0 precio30," +
                                "isnull((p1.PrePorVal),0) PrePorVal1," +
                                "isnull((p2.PrePorVal),0) PrePorVal2," +
                                "isnull((p3.PrePorVal),0) PrePorVal3," +
                                "isnull((p4.PrePorVal),0) PrePorVal4," +
                                "isnull((p5.PrePorVal),0) PrePorVal5," +
                                "isnull((p6.PrePorVal),0) PrePorVal6," +
                                "isnull((p7.PrePorVal),0) PrePorVal7," +
                                "isnull((p8.PrePorVal),0) PrePorVal8," +
                                "isnull((p9.PrePorVal),0) PrePorVal9," +
                                //"isnull((select top 1 movparlindes from MovParLineaGrupos ff where movparlininvgrucod=s.invgrucod ),0) desc1," +
                                "isnull((select top 1 movparlindes from MovParLineaGrupos ff left join movparlinea ml on ml.movparlinsec=ff.movparlinsec where movparlinencinvgrucod=s.invgrucod and MOVPARLINDETFECINI<=CONVERT(date, GETDATE()) and MOVPARLINDETFECFIN>=CONVERT(date, GETDATE()) ),0) desc1," +
                                "isnull((select top 1 MovParArtDetDesc from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc2," +
                                "isnull((select top 1 MovParArtDetDesc2 from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc2<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc5," +
                                "isnull((select top 1 MovParArtFecMod from MovParArtArticulos where MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) ),'') MovParArtFecMod" +
                                ",0 desc3,0 desc4,0 desc6,1000 Exist2 " +
                                ",isnull((select sum(artexiact) from articulosexi ae where ae.artsec=a.artsec and artbodcod in(select alinegbodsucccsec from carunidadesalinegbodsuc where alinegcod="+vAliNegCod+")),0) Exist " +
                                ",0 ExistFec " +
                                ",isnull(c.ClaArtCod,0) ClaArtCod,isnull(c.ClaArtNom,'') ClaArtNom,isnull(ArtValImp,0) ArtValImp,ArtEmb,isnull(ArtIndMpm,'') ArtIndMpm  " +
                                ",0 ArtRen,0 ArtLim " +
                                "from articulos a " +
                                "left join inventariofamilia f on a.invfamcod=f.invfamcod " +
                                "left join inventariosubgrupo s on f.invsubgrucod= s.invsubgrucod " +
                                "left join inventariogrupo g on g.invgrucod=s.invgrucod " +
                                "left join clasearticulo c on c.claartcod=a.claartcod " +
                                "left join parametrocontable p on p.parconcod=a.parconcod " +
                                "left join artpre pp on a.artsec=pp.artsec and PreArtFacCon='S' " +
                                " left join PreciosDetalle p1 on p1.ArtSec=a.ArtSec and p1.succod=1 and  p1.LisPreCod=1 and p1.PreEst='A' and P1.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p2 on p2.ArtSec=a.ArtSec and p2.succod=1 and  p2.LisPreCod=2 and p2.PreEst='A' and P2.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p3 on p3.ArtSec=a.ArtSec and p3.succod=1 and  p3.LisPreCod=3 and p3.PreEst='A' and P3.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p4 on p4.ArtSec=a.ArtSec and p4.succod=1 and  p4.LisPreCod=4 and p4.PreEst='A' and P4.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p5 on p5.ArtSec=a.ArtSec and p5.succod=1 and  p5.LisPreCod=5 and p5.PreEst='A' and P5.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p6 on p6.ArtSec=a.ArtSec and p6.succod=1 and  p6.LisPreCod=6 and p6.PreEst='A' and P6.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p7 on p7.ArtSec=a.ArtSec and p7.succod=1 and  p7.LisPreCod=7 and p7.PreEst='A' and P7.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p8 on p8.ArtSec=a.ArtSec and p8.succod=1 and  p8.LisPreCod=8 and p8.PreEst='A' and P8.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p9 on p9.ArtSec=a.ArtSec and p9.succod=1 and  p9.LisPreCod=9 and p9.PreEst='A' and P9.PreArtCod=PP.PreArtCod" +
                                " WHERE a.artdes<>'S'  AND isnull((select sum(artexiact) from articulosexi ae where ae.artsec=a.artsec and artbodcod in(select alinegbodsucccsec from carunidadesalinegbodsuc where alinegcod="+vAliNegCod+")),0)>0 " +
                                " and ( s.invgrucod in(select invgrucod from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "') or " +
                                " (select count(*) from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "')=0 ) or a.artsec in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes<>'S') AND a.artsec NOT in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes='S') ) Consulta order by Row desc";
                    }else if (vEmpresa.trim().equalsIgnoreCase("MEDIVALLE"))
                    {
                        Script = "select * from (select ROW_NUMBER() OVER(ORDER BY a.ArtSec ASC) AS Row,a.ArtSec,ArtCod,replace(ArtNom,'''','')+' '+isnull((select top 1 preartnom from artpre ap left join presentacionarticulos p on ap.preartcod=p.preartcod where preartfaccon='S' and ap.artsec=a.artsec ),'') ArtNom,isnull(ArtMedNomCom,'') ArtMedNomCom,g.InvGruCod,g.InvGruNom," +
                                "s.InvSubGruCod,s.InvSubGruNom,a.InvFamCod,InvFamNom,ParConIva,0 PrePreFijCosPro," +
                                "isnull((p1.PrePreFijVal/(((100-p1.preporval)/100))),0) precio1," +
                                "isnull((p2.PrePreFijVal/(((100-p2.preporval)/100))),0) precio2," +
                                "isnull((p3.PrePreFijVal/(((100-p3.preporval)/100))),0) precio3," +
                                "isnull((p4.PrePreFijVal/(((100-p4.preporval)/100))),0) precio4," +
                                "isnull((p5.PrePreFijVal/(((100-p5.preporval)/100))),0) precio5," +
                                "isnull((p6.PrePreFijVal/(((100-p6.preporval)/100))),0) precio6," +
                                "isnull((p7.PrePreFijVal/(((100-p7.preporval)/100))),0) precio7," +
                                "isnull((p8.PrePreFijVal/(((100-p8.preporval)/100))),0) precio8," +
                                "isnull((p9.PrePreFijVal/(((100-p9.preporval)/100))),0) precio9," +
                                "0 precio10," +
                                "0 precio11," +
                                "0 precio12," +
                                "0 precio13," +
                                "0 precio14," +
                                "0 precio15," +
                                "0 precio16," +
                                "0 precio17," +
                                "0 precio18," +
                                "0 precio19," +
                                "0 precio20," +
                                "0 precio21," +
                                "0 precio22," +
                                "0 precio23," +
                                "0 precio24," +
                                "0 precio25," +
                                "0 precio26," +
                                "0 precio27," +
                                "0 precio28," +
                                "0 precio29," +
                                "0 precio30," +
                                "isnull((p1.PrePorVal),0) PrePorVal1," +
                                "isnull((p2.PrePorVal),0) PrePorVal2," +
                                "isnull((p3.PrePorVal),0) PrePorVal3," +
                                "isnull((p4.PrePorVal),0) PrePorVal4," +
                                "isnull((p5.PrePorVal),0) PrePorVal5," +
                                "isnull((p6.PrePorVal),0) PrePorVal6," +
                                "isnull((p7.PrePorVal),0) PrePorVal7," +
                                "isnull((p8.PrePorVal),0) PrePorVal8," +
                                "isnull((p9.PrePorVal),0) PrePorVal9," +
                                //"isnull((select top 1 movparlindes from MovParLineaGrupos ff where movparlininvgrucod=s.invgrucod ),0) desc1," +
                                "isnull((select top 1 movparlindes from MovParLineaGrupos ff left join movparlinea ml on ml.movparlinsec=ff.movparlinsec where movparlinencinvgrucod=s.invgrucod and MOVPARLINDETFECINI<=CONVERT(date, GETDATE()) and MOVPARLINDETFECFIN>=CONVERT(date, GETDATE()) ),0)+isnull((select DesProPorDes from descuentosprogramadosdetalle dd left join  descuentosprogramados d on d.desprocod=dd.desprocod where desprofecini<=CONVERT(date, GETDATE()) and desprofecfin>=CONVERT(date, GETDATE()) and DesProArtSec=a.artsec),0) desc1," +
                                "isnull((select top 1 MovParArtDetDesc from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc2," +
                                "isnull((select top 1 MovParArtDetDesc2 from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc2<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc5," +
                                "isnull((select top 1 MovParArtFecMod from MovParArtArticulos where MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) ),'') MovParArtFecMod" +
                                ",0 desc3,0 desc4,0 desc6,1000 Exist2 " +
                                ",isnull((select sum(artexiact) from articulosexi ae where ae.artsec=a.artsec and artbodcod in(select alinegbodsucccsec from carunidadesalinegbodsuc where alinegcod="+vAliNegCod+")),0) Exist " +
                                ",0 ExistFec " +
                                ",isnull(c.ClaArtCod,0) ClaArtCod,isnull(c.ClaArtNom,'') ClaArtNom,isnull(ArtValImp,0) ArtValImp,isnull(ArtEmb,1) ArtEmb,isnull(ArtIndMpm,'') ArtIndMpm  " +
                                ",0 ArtRen,0 ArtLim " +
                                "from articulos a " +
                                "left join inventariofamilia f on a.invfamcod=f.invfamcod " +
                                "left join inventariosubgrupo s on f.invsubgrucod= s.invsubgrucod " +
                                "left join inventariogrupo g on g.invgrucod=s.invgrucod " +
                                "left join clasearticulo c on c.claartcod=a.claartcod " +
                                "left join parametrocontable p on p.parconcod=a.parconcod " +
                                "left join artpre pp on a.artsec=pp.artsec and PreArtFacCon='S' " +
                                " left join PreciosDetalle p1 on p1.ArtSec=a.ArtSec and p1.succod=1 and  p1.LisPreCod=7 and p1.PreEst='A' and P1.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p2 on p2.ArtSec=a.ArtSec and p2.succod=1 and  p2.LisPreCod=9 and p2.PreEst='A' and P2.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p3 on p3.ArtSec=a.ArtSec and p3.succod=1 and  p3.LisPreCod=10 and p3.PreEst='A' and P3.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p4 on p4.ArtSec=a.ArtSec and p4.succod=1 and  p4.LisPreCod=11 and p4.PreEst='A' and P4.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p5 on p5.ArtSec=a.ArtSec and p5.succod=1 and  p5.LisPreCod=12 and p5.PreEst='A' and P5.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p6 on p6.ArtSec=a.ArtSec and p6.succod=1 and  p6.LisPreCod=6 and p6.PreEst='A' and P6.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p7 on p7.ArtSec=a.ArtSec and p7.succod=1 and  p7.LisPreCod=7 and p7.PreEst='A' and P7.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p8 on p8.ArtSec=a.ArtSec and p8.succod=1 and  p8.LisPreCod=8 and p8.PreEst='A' and P8.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p9 on p9.ArtSec=a.ArtSec and p9.succod=1 and  p9.LisPreCod=9 and p9.PreEst='A' and P9.PreArtCod=PP.PreArtCod" +
                                " WHERE a.artdes<>'S' " +
                                " and ( s.invgrucod in(select invgrucod from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "') or " +
                                " (select count(*) from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "')=0 ) or a.artsec in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes<>'S') AND a.artsec NOT in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes='S')) Consulta order by Row desc";
                    }else if (vEmpresa.trim().equalsIgnoreCase("MENTAHAIR") || vEmpresa.trim().equalsIgnoreCase("MENTAHAIRCOT") )
                    {
                        Script = "select * from (select ROW_NUMBER() OVER(ORDER BY a.ArtSec ASC) AS Row,a.ArtSec,ArtCod,replace(ArtNom,'''','')+' '+isnull((select top 1 preartnom from artpre ap left join presentacionarticulos p on ap.preartcod=p.preartcod where preartfaccon='S' and ap.artsec=a.artsec ),'') ArtNom,isnull(ArtMedNomCom,'') ArtMedNomCom,g.InvGruCod,g.InvGruNom," +
                                "s.InvSubGruCod,s.InvSubGruNom,a.InvFamCod,InvFamNom,ParConIva,0 PrePreFijCosPro," +
                                "isnull((p1.PrePreFijVal),0) precio1," +
                                "isnull((p2.PrePreFijVal),0) precio2," +
                                "isnull((p3.PrePreFijVal),0) precio3," +
                                "isnull((p4.PrePreFijVal),0) precio4," +
                                "isnull((p5.PrePreFijVal),0) precio5," +
                                "isnull((p6.PrePreFijVal),0) precio6," +
                                "isnull((p7.PrePreFijVal),0) precio7," +
                                "isnull((p8.PrePreFijVal),0) precio8," +
                                "isnull((p9.PrePreFijVal),0) precio9," +
                                "0 precio10," +
                                "0 precio11," +
                                "0 precio12," +
                                "0 precio13," +
                                "0 precio14," +
                                "isnull((p15.PrePreFijVal),0) precio15," +
                                "0 precio16," +
                                "0 precio17," +
                                "0 precio18," +
                                "0 precio19," +
                                "0 precio20," +
                                "0 precio21," +
                                "0 precio22," +
                                "0 precio23," +
                                "0 precio24," +
                                "0 precio25," +
                                "0 precio26," +
                                "0 precio27," +
                                "0 precio28," +
                                "0 precio29," +
                                "0 precio30," +
                                "isnull((p1.PrePorVal),0) PrePorVal1," +
                                "isnull((p2.PrePorVal),0) PrePorVal2," +
                                "isnull((p3.PrePorVal),0) PrePorVal3," +
                                "isnull((p4.PrePorVal),0) PrePorVal4," +
                                "isnull((p5.PrePorVal),0) PrePorVal5," +
                                "isnull((p6.PrePorVal),0) PrePorVal6," +
                                "isnull((p7.PrePorVal),0) PrePorVal7," +
                                "isnull((p8.PrePorVal),0) PrePorVal8," +
                                "isnull((p9.PrePorVal),0) PrePorVal9," +
                                "isnull((p15.PrePorVal),0) PrePorVal15," +
                                //"isnull((select top 1 movparlindes from MovParLineaGrupos ff where movparlininvgrucod=s.invgrucod ),0) desc1," +
                                //"isnull((select top 1 movparlindes from MovParLineaGrupos ff left join movparlinea ml on ml.movparlinsec=ff.movparlinsec where movparlinencinvgrucod=s.invgrucod and MOVPARLINDETFECINI<=CONVERT(date, GETDATE()) and MOVPARLINDETFECFIN>=CONVERT(date, GETDATE()) ),0)+isnull((select DesProPorDes from descuentosprogramadosdetalle dd left join  descuentosprogramados d on d.desprocod=dd.desprocod where desprofecini<=CONVERT(date, GETDATE()) and desprofecfin>=CONVERT(date, GETDATE()) and DesProArtSec=a.artsec),0) desc1," +
                                "isnull((select top 1 movparlindes from MovParLineaGrupos ff left join movparlinea ml on ml.movparlinsec=ff.movparlinsec where movparlinencinvgrucod=s.invgrucod and MOVPARLINDETFECINI<=CONVERT(date, GETDATE()) and MOVPARLINDETFECFIN>=CONVERT(date, GETDATE()) ),0) desc1," +
                                "isnull((select top 1 MovParArtDetDesc from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc2," +
                                "isnull((select top 1 MovParArtDetDesc2 from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc2<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc5," +
                                "isnull((select top 1 MovParArtFecMod from MovParArtArticulos where MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) ),'') MovParArtFecMod" +
                                ",0 desc3,0 desc4,0 desc6,1000 Exist2 " +
                                ",isnull((select sum(artexiact) from articulosexi ae where ae.artsec=a.artsec and artbodcod in(select alinegbodsucccsec from carunidadesalinegbodsuc where alinegcod="+vAliNegCod+")),0) Exist " +
                                ",0 ExistFec " +
                                ",isnull(c.ClaArtCod,0) ClaArtCod,isnull(c.ClaArtNom,'') ClaArtNom,isnull(ArtValImp,0) ArtValImp,isnull(ArtEmb,1) ArtEmb,isnull(ArtIndMpm,'') ArtIndMpm  " +
                                ",0 ArtRen,0 ArtLim,'N' InvGruDifCheck,'N' InvSubDifCheck,'N' InvFamDifCheck, ArtCodBar  " +
                                "from articulos a " +
                                "left join inventariofamilia f on a.invfamcod=f.invfamcod " +
                                "left join inventariosubgrupo s on f.invsubgrucod= s.invsubgrucod " +
                                "left join inventariogrupo g on g.invgrucod=s.invgrucod " +
                                "left join clasearticulo c on c.claartcod=a.claartcod " +
                                "left join parametrocontable p on p.parconcod=a.parconcod " +
                                "left join artpre pp on a.artsec=pp.artsec and PreArtFacCon='S' " +
                                " left join PreciosDetalle p1 on p1.ArtSec=a.ArtSec and p1.succod=1 and  p1.LisPreCod=9 and p1.PreEst='A' and P1.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p2 on p2.ArtSec=a.ArtSec and p2.succod=1 and  p2.LisPreCod=10 and p2.PreEst='A' and P2.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p3 on p3.ArtSec=a.ArtSec and p3.succod=1 and  p3.LisPreCod=11 and p3.PreEst='A' and P3.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p4 on p4.ArtSec=a.ArtSec and p4.succod=1 and  p4.LisPreCod=12 and p4.PreEst='A' and P4.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p5 on p5.ArtSec=a.ArtSec and p5.succod=1 and  p5.LisPreCod=13 and p5.PreEst='A' and P5.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p6 on p6.ArtSec=a.ArtSec and p6.succod=1 and  p6.LisPreCod=1 and p6.PreEst='A' and P6.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p7 on p7.ArtSec=a.ArtSec and p7.succod=1 and  p7.LisPreCod=2 and p7.PreEst='A' and P7.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p8 on p8.ArtSec=a.ArtSec and p8.succod=1 and  p8.LisPreCod=3 and p8.PreEst='A' and P8.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p9 on p9.ArtSec=a.ArtSec and p9.succod=1 and  p9.LisPreCod=4 and p9.PreEst='A' and P9.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p15 on p15.ArtSec=a.ArtSec and p15.succod=1 and  p15.LisPreCod=15 and p15.PreEst='A' and p15.PreArtCod=PP.PreArtCod" +
                                " WHERE a.artdes<>'S' and PREARTVISDIS = 'S' " +
                                " and ( s.invgrucod in(select invgrucod from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "') or " +
                                " (select count(*) from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "')=0 ) or a.artsec in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes<>'S') AND a.artsec NOT in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes='S')) Consulta order by Row desc";







                    }else if (vEmpresa.trim().equalsIgnoreCase("SNACKS")) {


                        Script = "select * from (select ROW_NUMBER() OVER(ORDER BY a.ArtSec ASC) AS Row,a.ArtSec,ArtCod,replace(ArtNom,'''','')+' '+isnull((select top 1 preartnom from artpre ap left join presentacionarticulos p on ap.preartcod=p.preartcod where preartfaccon='S' and ap.artsec=a.artsec ),'') ArtNom,isnull(ArtMedNomCom,'') ArtMedNomCom,g.InvGruCod,g.InvGruNom," +
                                "s.InvSubGruCod,s.InvSubGruNom,a.InvFamCod,InvFamNom,ParConIva,0 PrePreFijCosPro," +
                                "isnull((p1.PrePreFijVal),0) precio1," +
                                "isnull((p2.PrePreFijVal),0) precio2," +
                                "isnull((p3.PrePreFijVal),0) precio3," +
                                "isnull((p4.PrePreFijVal),0) precio4," +
                                "isnull((p5.PrePreFijVal),0) precio5," +
                                "isnull((p6.PrePreFijVal),0) precio6," +
                                "isnull((p7.PrePreFijVal),0) precio7," +
                                "isnull((p8.PrePreFijVal),0) precio8," +
                                "isnull((p9.PrePreFijVal),0) precio9," +
                                "0 precio10," +
                                "0 precio11," +
                                "0 precio12," +
                                "0 precio13," +
                                "0 precio14," +
                                "0 precio15," +
                                "0 precio16," +
                                "0 precio17," +
                                "0 precio18," +
                                "0 precio19," +
                                "0 precio20," +
                                "0 precio21," +
                                "0 precio22," +
                                "0 precio23," +
                                "0 precio24," +
                                "0 precio25," +
                                "0 precio26," +
                                "0 precio27," +
                                "0 precio28," +
                                "0 precio29," +
                                "0 precio30," +
                                "isnull((p1.PrePorVal),0) PrePorVal1," +
                                "isnull((p2.PrePorVal),0) PrePorVal2," +
                                "isnull((p3.PrePorVal),0) PrePorVal3," +
                                "isnull((p4.PrePorVal),0) PrePorVal4," +
                                "isnull((p5.PrePorVal),0) PrePorVal5," +
                                "isnull((p6.PrePorVal),0) PrePorVal6," +
                                "isnull((p7.PrePorVal),0) PrePorVal7," +
                                "isnull((p8.PrePorVal),0) PrePorVal8," +
                                "isnull((p9.PrePorVal),0) PrePorVal9," +
                                //"isnull((select top 1 movparlindes from MovParLineaGrupos ff where movparlininvgrucod=s.invgrucod ),0) desc1," +
                                "0 desc1," +
                                "0 desc2," +
                                "isnull((select top 1 MovParArtDetDesc2 from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc2<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc5," +
                                "isnull((select top 1 MovParArtFecMod from MovParArtArticulos where MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) ),'') MovParArtFecMod" +
                                ",0 desc3,0 desc4,0 desc6,1000 Exist2 " +
                                ",isnull((select sum(artexiact) from articulosexi ae where ae.artsec=a.artsec and artbodcod in(select alinegbodsucccsec from carunidadesalinegbodsuc where alinegcod="+vAliNegCod+")),0) Exist " +
                                ",0 ExistFec " +
                                ",isnull(c.ClaArtCod,0) ClaArtCod,isnull(c.ClaArtNom,'') ClaArtNom,isnull(ArtValImp,0) ArtValImp,isnull(ArtEmb,1) ArtEmb,isnull(ArtIndMpm,'') ArtIndMpm  " +
                                ",0 ArtRen,0 ArtLim,'N' InvGruDifCheck,'N' InvSubDifCheck,'N' InvFamDifCheck , ArtCodBar " +
                                "from articulos a " +
                                "left join inventariofamilia f on a.invfamcod=f.invfamcod " +
                                "left join inventariosubgrupo s on f.invsubgrucod= s.invsubgrucod " +
                                "left join inventariogrupo g on g.invgrucod=s.invgrucod " +
                                "left join clasearticulo c on c.claartcod=a.claartcod " +
                                "left join parametrocontable p on p.parconcod=a.parconcod " +
                                "left join artpre pp on a.artsec=pp.artsec and PreArtFacCon='S' " +
                                " left join PreciosDetalle p1 on p1.ArtSec=a.ArtSec and p1.succod=1 and  p1.LisPreCod=1 and p1.PreEst='A' and P1.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p2 on p2.ArtSec=a.ArtSec and p2.succod=1 and  p2.LisPreCod=2 and p2.PreEst='A' and P2.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p3 on p3.ArtSec=a.ArtSec and p3.succod=1 and  p3.LisPreCod=3 and p3.PreEst='A' and P3.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p4 on p4.ArtSec=a.ArtSec and p4.succod=1 and  p4.LisPreCod=4 and p4.PreEst='A' and P4.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p5 on p5.ArtSec=a.ArtSec and p5.succod=1 and  p5.LisPreCod=5 and p5.PreEst='A' and P5.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p6 on p6.ArtSec=a.ArtSec and p6.succod=1 and  p6.LisPreCod=6 and p6.PreEst='A' and P6.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p7 on p7.ArtSec=a.ArtSec and p7.succod=1 and  p7.LisPreCod=7 and p7.PreEst='A' and P7.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p8 on p8.ArtSec=a.ArtSec and p8.succod=1 and  p8.LisPreCod=8 and p8.PreEst='A' and P8.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p9 on p9.ArtSec=a.ArtSec and p9.succod=1 and  p9.LisPreCod=9 and p9.PreEst='A' and P9.PreArtCod=PP.PreArtCod" +
                                " WHERE a.artdes<>'S' " +
                                " and ( s.invgrucod in(select invgrucod from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "') or " +
                                " (select count(*) from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "')=0 ) or a.artsec in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes<>'S') AND a.artsec NOT in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes='S')) Consulta order by Row desc";


                        mensajeslargos("consultaSNACKSSS",Script);
                    }else if (vEmpresa.trim().equalsIgnoreCase("SUMMEDSAN")) {

                        Script = "select * from (select ROW_NUMBER() OVER(ORDER BY a.ArtSec ASC) AS Row,a.ArtSec,ArtCod,replace(ArtNom,'''','')+' '+isnull((select top 1 preartnom from artpre ap left join presentacionarticulos p on ap.preartcod=p.preartcod where preartfaccon='S' and ap.artsec=a.artsec ),'') ArtNom,isnull(ArtMedNomCom,'') ArtMedNomCom,g.InvGruCod,g.InvGruNom," +
                                "s.InvSubGruCod,s.InvSubGruNom,a.InvFamCod,InvFamNom,ParConIva,0 PrePreFijCosPro," +
                                "isnull((p1.PrePreFijVal),0) precio1," +
                                "isnull((p2.PrePreFijVal),0) precio2," +
                                "isnull((p3.PrePreFijVal),0) precio3," +
                                "isnull((p4.PrePreFijVal),0) precio4," +
                                "isnull((p5.PrePreFijVal),0) precio5," +
                                "isnull((p6.PrePreFijVal),0) precio6," +
                                "isnull((p7.PrePreFijVal),0) precio7," +
                                "isnull((p8.PrePreFijVal),0) precio8," +
                                "isnull((p9.PrePreFijVal),0) precio9," +
                                "isnull((p10.PrePreFijVal),0)  precio10," +
                                "isnull((p11.PrePreFijVal),0)  precio11," +
                                "isnull((p12.PrePreFijVal),0)  precio12," +
                                "isnull((p13.PrePreFijVal),0)  precio13," +
                                "isnull((p14.PrePreFijVal),0)  precio14," +
                                "isnull((p15.PrePreFijVal),0)  precio15," +
                                "isnull((p16.PrePreFijVal),0) precio16," +
                                "isnull((p17.PrePreFijVal),0)  precio17," +
                                "0 precio18," +
                                "0 precio19," +
                                "0 precio20," +
                                "0 precio21," +
                                "0 precio22," +
                                "0 precio23," +
                                "0 precio24," +
                                "0 precio25," +
                                "0 precio26," +
                                "0 precio27," +
                                "0 precio28," +
                                "0 precio29," +
                                "0 precio30," +
                                "isnull((p1.PrePorVal),0) PrePorVal1," +
                                "isnull((p2.PrePorVal),0) PrePorVal2," +
                                "isnull((p3.PrePorVal),0) PrePorVal3," +
                                "isnull((p4.PrePorVal),0) PrePorVal4," +
                                "isnull((p5.PrePorVal),0) PrePorVal5," +
                                "isnull((p6.PrePorVal),0) PrePorVal6," +
                                "isnull((p7.PrePorVal),0) PrePorVal7," +
                                "isnull((p8.PrePorVal),0) PrePorVal8," +
                                "isnull((p9.PrePorVal),0) PrePorVal9," +
                                "isnull((p10.PrePorVal),0) PrePorVal10," +
                                "isnull((p11.PrePorVal),0) PrePorVal11," +
                                "isnull((p12.PrePorVal),0) PrePorVal12," +
                                "isnull((p13.PrePorVal),0) PrePorVal13," +
                                "isnull((p14.PrePorVal),0) PrePorVal14," +
                                "isnull((p15.PrePorVal),0) PrePorVal15," +
                                "isnull((p16.PrePorVal),0) PrePorVal16," +
                                "isnull((p17.PrePorVal),0) PrePorVal17," +
                                //"isnull((select top 1 movparlindes from MovParLineaGrupos ff where movparlininvgrucod=s.invgrucod ),0) desc1," +
                                "isnull((select top 1 movparlindes from MovParLineaGrupos ff left join movparlinea ml on ml.movparlinsec=ff.movparlinsec where movparlinencinvgrucod=s.invgrucod and MOVPARLINDETFECINI<=CONVERT(date, GETDATE()) and MOVPARLINDETFECFIN>=CONVERT(date, GETDATE()) ),0)+isnull((select top 1 DesProPorDes from descuentosprogramadosdetalle dd left join  descuentosprogramados d on d.desprocod=dd.desprocod where desprofecini<=CONVERT(date, GETDATE()) and desprofecfin>=CONVERT(date, GETDATE()) and DesProArtSec=a.artsec),0) desc1," +
                                "isnull((select top 1 MovParArtDetDesc from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc2," +
                                "isnull((select top 1 MovParArtDetDesc2 from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc2<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                                "),0) desc5," +
                                "isnull((select top 1 MovParArtFecMod from MovParArtArticulos where MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) ),'') MovParArtFecMod" +
                                ",0 desc3,0 desc4,0 desc6,1000 Exist2 " +
                                ",isnull((select sum(artexiact) from articulosexi ae where ae.artsec=a.artsec and artbodcod in(select alinegbodsucccsec from carunidadesalinegbodsuc where alinegcod="+vAliNegCod+")),0) Exist " +
                                ",0 ExistFec " +
                                ",isnull(c.ClaArtCod,0) ClaArtCod,isnull(c.ClaArtNom,'') ClaArtNom,isnull(ArtValImp,0) ArtValImp,isnull(ArtEmb,1) ArtEmb,isnull(ArtIndMpm,'') ArtIndMpm  " +
                                ",0 ArtRen,0 ArtLim,'N' InvGruDifCheck,'N' InvSubDifCheck,'N' InvFamDifCheck , replace(ArtCodBar,'''','') ArtCodBar " +
                                "from articulos a " +
                                "left join inventariofamilia f on a.invfamcod=f.invfamcod " +
                                "left join inventariosubgrupo s on f.invsubgrucod= s.invsubgrucod " +
                                "left join inventariogrupo g on g.invgrucod=s.invgrucod " +
                                "left join clasearticulo c on c.claartcod=a.claartcod " +
                                "left join parametrocontable p on p.parconcod=a.parconcod " +
                                "left join artpre pp on a.artsec=pp.artsec and PreArtFacCon='S' " +
                                " left join PreciosDetalle p1 on p1.ArtSec=a.ArtSec and p1.succod=1 and  p1.LisPreCod=1 and p1.PreEst='A' and P1.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p2 on p2.ArtSec=a.ArtSec and p2.succod=1 and  p2.LisPreCod=2 and p2.PreEst='A' and P2.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p3 on p3.ArtSec=a.ArtSec and p3.succod=1 and  p3.LisPreCod=3 and p3.PreEst='A' and P3.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p4 on p4.ArtSec=a.ArtSec and p4.succod=1 and  p4.LisPreCod=4 and p4.PreEst='A' and P4.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p5 on p5.ArtSec=a.ArtSec and p5.succod=1 and  p5.LisPreCod=5 and p5.PreEst='A' and P5.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p6 on p6.ArtSec=a.ArtSec and p6.succod=1 and  p6.LisPreCod=6 and p6.PreEst='A' and P6.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p7 on p7.ArtSec=a.ArtSec and p7.succod=1 and  p7.LisPreCod=7 and p7.PreEst='A' and P7.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p8 on p8.ArtSec=a.ArtSec and p8.succod=1 and  p8.LisPreCod=8 and p8.PreEst='A' and P8.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p9 on p9.ArtSec=a.ArtSec and p9.succod=1 and  p9.LisPreCod=9 and p9.PreEst='A' and P9.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p10 on p10.ArtSec=a.ArtSec and p10.succod=1 and  p10.LisPreCod=10 and p10.PreEst='A' and p10.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p11 on p11.ArtSec=a.ArtSec and p11.succod=1 and  p11.LisPreCod=11 and p11.PreEst='A' and p11.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p12 on p12.ArtSec=a.ArtSec and p12.succod=1 and  p12.LisPreCod=12 and p12.PreEst='A' and p12.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p13 on p13.ArtSec=a.ArtSec and p13.succod=1 and  p13.LisPreCod=13 and p13.PreEst='A' and p13.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p14 on p14.ArtSec=a.ArtSec and p14.succod=1 and  p14.LisPreCod=14 and p14.PreEst='A' and p14.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p15 on p15.ArtSec=a.ArtSec and p15.succod=1 and  p15.LisPreCod=15 and p15.PreEst='A' and p15.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p16 on p16.ArtSec=a.ArtSec and p16.succod=1 and  p16.LisPreCod=16 and p16.PreEst='A' and p16.PreArtCod=PP.PreArtCod" +
                                " left join PreciosDetalle p17 on p17.ArtSec=a.ArtSec and p17.succod=1 and  p17.LisPreCod=17 and p17.PreEst='A' and p17.PreArtCod=PP.PreArtCod" +
                                " WHERE a.artdes<>'S' " +
                                " and ( s.invgrucod in(select invgrucod from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "') or " +
                                " (select count(*) from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "')=0 ) or a.artsec in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes<>'S') AND a.artsec NOT in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes='S')) Consulta order by Row desc";

                    }

                    else
                    {
                        Script = "select * from (select ROW_NUMBER() OVER(ORDER BY a.ArtSec ASC) AS Row,a.ArtSec,ArtCod,replace(ArtNom,'''','')+' '+isnull((select top 1 preartnom from artpre ap left join presentacionarticulos p on ap.preartcod=p.preartcod where preartfaccon='S' and ap.artsec=a.artsec ),'') ArtNom,isnull(ArtMedNomCom,'') ArtMedNomCom,g.InvGruCod,g.InvGruNom," +
                            "s.InvSubGruCod,s.InvSubGruNom,a.InvFamCod,InvFamNom,ParConIva,0 PrePreFijCosPro," +
                            "isnull((p1.PrePreFijVal),0) precio1," +
                            "isnull((p2.PrePreFijVal),0) precio2," +
                            "isnull((p3.PrePreFijVal),0) precio3," +
                            "isnull((p4.PrePreFijVal),0) precio4," +
                            "isnull((p5.PrePreFijVal),0) precio5," +
                            "isnull((p6.PrePreFijVal),0) precio6," +
                            "isnull((p7.PrePreFijVal),0) precio7," +
                            "isnull((p8.PrePreFijVal),0) precio8," +
                            "isnull((p9.PrePreFijVal),0) precio9," +
                            "0 precio10," +
                            "0 precio11," +
                            "0 precio12," +
                            "0 precio13," +
                            "0 precio14," +
                            "0 precio15," +
                            "0 precio16," +
                            "0 precio17," +
                            "0 precio18," +
                            "0 precio19," +
                            "0 precio20," +
                            "0 precio21," +
                            "0 precio22," +
                            "0 precio23," +
                            "0 precio24," +
                            "0 precio25," +
                            "0 precio26," +
                            "0 precio27," +
                            "0 precio28," +
                            "0 precio29," +
                            "0 precio30," +
                            "isnull((p1.PrePorVal),0) PrePorVal1," +
                            "isnull((p2.PrePorVal),0) PrePorVal2," +
                            "isnull((p3.PrePorVal),0) PrePorVal3," +
                            "isnull((p4.PrePorVal),0) PrePorVal4," +
                            "isnull((p5.PrePorVal),0) PrePorVal5," +
                            "isnull((p6.PrePorVal),0) PrePorVal6," +
                            "isnull((p7.PrePorVal),0) PrePorVal7," +
                            "isnull((p8.PrePorVal),0) PrePorVal8," +
                            "isnull((p9.PrePorVal),0) PrePorVal9," +
                            //"isnull((select top 1 movparlindes from MovParLineaGrupos ff where movparlininvgrucod=s.invgrucod ),0) desc1," +
                            "isnull((select top 1 movparlindes from MovParLineaGrupos ff left join movparlinea ml on ml.movparlinsec=ff.movparlinsec where movparlinencinvgrucod=s.invgrucod and MOVPARLINDETFECINI<=CONVERT(date, GETDATE()) and MOVPARLINDETFECFIN>=CONVERT(date, GETDATE()) ),0)+isnull((select top 1 DesProPorDes from descuentosprogramadosdetalle dd left join  descuentosprogramados d on d.desprocod=dd.desprocod where desprofecini<=CONVERT(date, GETDATE()) and desprofecfin>=CONVERT(date, GETDATE()) and DesProArtSec=a.artsec),0) desc1," +
                            "isnull((select top 1 MovParArtDetDesc from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                            "),0) desc2," +
                            "isnull((select top 1 MovParArtDetDesc2 from MovParArtArticulos  left join MovParArtUnidadesNeg mn on mn.movparartsec=MovParArtArticulos.movparartsec where alinegcod="+  vAliNegCod +" and  MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) and MovParArtDetDesc2<>0 " +  //and MovParArtFecIni<=getdate()  and MovParArtFecFin>=getdate()
                            "),0) desc5," +
                            "isnull((select top 1 MovParArtFecMod from MovParArtArticulos where MovParArtDetArtSec=a.artsec and MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) ),'') MovParArtFecMod" +
                            ",0 desc3,0 desc4,0 desc6,1000 Exist2 " +
                            ",isnull((select sum(artexiact) from articulosexi ae where ae.artsec=a.artsec and artbodcod in(select alinegbodsucccsec from carunidadesalinegbodsuc where alinegcod="+vAliNegCod+")),0) Exist " +
                            ",0 ExistFec " +
                            ",isnull(c.ClaArtCod,0) ClaArtCod,isnull(c.ClaArtNom,'') ClaArtNom,isnull(ArtValImp,0) ArtValImp,isnull(ArtEmb,1) ArtEmb,isnull(ArtIndMpm,'') ArtIndMpm  " +
                            ",0 ArtRen,0 ArtLim,'N' InvGruDifCheck,'N' InvSubDifCheck,'N' InvFamDifCheck , replace(ArtCodBar,'''','') ArtCodBar " +
                            "from articulos a " +
                            "left join inventariofamilia f on a.invfamcod=f.invfamcod " +
                            "left join inventariosubgrupo s on f.invsubgrucod= s.invsubgrucod " +
                            "left join inventariogrupo g on g.invgrucod=s.invgrucod " +
                            "left join clasearticulo c on c.claartcod=a.claartcod " +
                            "left join parametrocontable p on p.parconcod=a.parconcod " +
                            "left join artpre pp on a.artsec=pp.artsec and PreArtFacCon='S' " +
                            " left join PreciosDetalle p1 on p1.ArtSec=a.ArtSec and p1.succod=1 and  p1.LisPreCod=1 and p1.PreEst='A' and P1.PreArtCod=PP.PreArtCod" +
                            " left join PreciosDetalle p2 on p2.ArtSec=a.ArtSec and p2.succod=1 and  p2.LisPreCod=2 and p2.PreEst='A' and P2.PreArtCod=PP.PreArtCod" +
                            " left join PreciosDetalle p3 on p3.ArtSec=a.ArtSec and p3.succod=1 and  p3.LisPreCod=3 and p3.PreEst='A' and P3.PreArtCod=PP.PreArtCod" +
                            " left join PreciosDetalle p4 on p4.ArtSec=a.ArtSec and p4.succod=1 and  p4.LisPreCod=4 and p4.PreEst='A' and P4.PreArtCod=PP.PreArtCod" +
                            " left join PreciosDetalle p5 on p5.ArtSec=a.ArtSec and p5.succod=1 and  p5.LisPreCod=5 and p5.PreEst='A' and P5.PreArtCod=PP.PreArtCod" +
                            " left join PreciosDetalle p6 on p6.ArtSec=a.ArtSec and p6.succod=1 and  p6.LisPreCod=6 and p6.PreEst='A' and P6.PreArtCod=PP.PreArtCod" +
                            " left join PreciosDetalle p7 on p7.ArtSec=a.ArtSec and p7.succod=1 and  p7.LisPreCod=7 and p7.PreEst='A' and P7.PreArtCod=PP.PreArtCod" +
                            " left join PreciosDetalle p8 on p8.ArtSec=a.ArtSec and p8.succod=1 and  p8.LisPreCod=8 and p8.PreEst='A' and P8.PreArtCod=PP.PreArtCod" +
                            " left join PreciosDetalle p9 on p9.ArtSec=a.ArtSec and p9.succod=1 and  p9.LisPreCod=9 and p9.PreEst='A' and P9.PreArtCod=PP.PreArtCod" +
                            " WHERE a.artdes<>'S' " +
                            " and ( s.invgrucod in(select invgrucod from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "') or " +
                            " (select count(*) from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "')=0 ) or a.artsec in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes<>'S') AND a.artsec NOT in(select subvenartsec from vendedoressubvenart where rtrim(VenCod)='" + vUsuario + "' and venartdes='S')) Consulta order by Row desc";
                    }


                     rsImport = comm.executeQuery(Script);

                    BdSql.execSQL("Delete from articulos");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();
                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);

                        String codigo=rsImport.getString("ArtCod").trim();
                        int jj=1;
                    //    if (codigo.equalsIgnoreCase("102285")){
                    //        jj=2;
                    //    }
                        //String Existencia=TraerExistencia(rsImport.getString("ArtSec").trim());

                        String InsertScript = "insert into articulos ("
                                + "ArtSec,"
                                + "ArtCod,"
                                + "ArtNom,"
                                + "ArtMedNomCom,"
                                + "InvGruCod,"
                                + "InvGruNom,"
                                + "InvSubGruCod,"
                                + "InvSubGruNom,"
                                + "InvFamCod,"
                                + "InvFamNom,"
                                + "ParConIva,"
                                + "artren,"
                                + "artlim,"
                                + "precio1,"
                                + "precio2,"
                                + "precio3,"
                                + "precio4,"
                                + "precio5,"
                                + "precio6,"
                                + "precio7,"
                                + "precio8,"
                                + "precio9,"
                                + "precio10,"
                                + "precio11,"
                                + "precio12,"
                                + "precio13,"
                                + "precio14,"
                                + "precio15,"
                                + "precio16,"
                                + "precio17,"
                                + "precio18,"
                                + "precio19,"
                                + "precio20,"
                                + "precio21,"
                                + "precio22,"
                                + "precio23,"
                                + "precio24,"
                                + "precio25,"
                                + "precio26,"
                                + "precio27,"
                                + "precio28,"
                                + "precio29,"
                                + "precio30,"
                                + "precio1PorRen,"
                                + "precio2PorRen,"
                                + "precio3PorRen,"
                                + "precio4PorRen,"
                                + "precio5PorRen,"
                                + "precio6PorRen,"
                                + "precio7PorRen,"
                                + "precio8PorRen,"
                                + "precio9PorRen,"
                                + "desc1,"
                                + "desc2,"
                                + "desc3,"
                                + "desc4,"
                                + "desc5,"
                                + "desc6,"
                                + "Exist,"
                                + "ExistFec,"
                                + "ClaArtCod,"
                                + "ClaArtNom,artemb,ArtIndMpm,MovParArtFecMod,PrePreFijCosPro,"
                                + "ArtValImp,GruCheck,SubCheck,FamCheck,ArtCodBar)values('"
                                + rsImport.getString("ArtSec").trim() + "','"
                                + rsImport.getString("ArtCod").trim() + "','"
                                + rsImport.getString("ArtNom").trim().replace("'","-") + "','"
                                + rsImport.getString("ArtMedNomCom").trim().replace("'","-") + "','"
                                + rsImport.getString("InvGruCod").trim() + "','"
                                + rsImport.getString("InvGruNom").trim().replace("'","-") + "','"
                                + rsImport.getString("InvSubGruCod").trim() + "','"
                                + rsImport.getString("InvSubGruNom").trim().replace("'","-") + "','"
                                + rsImport.getString("InvFamCod").trim() + "','"
                                + rsImport.getString("InvFamNom").trim().replace("'","-") + "',"
                                + rsImport.getString("ParConIva").trim() + ","
                                + rsImport.getString("ArtRen").trim() + ","
                                + rsImport.getString("ArtLim").trim() + ","
                                + rsImport.getString("precio1").trim() + ","
                                + rsImport.getString("precio2").trim() + ","
                                + rsImport.getString("precio3").trim() + ","
                                + rsImport.getString("precio4").trim() + ","
                                + rsImport.getString("precio5").trim() + ","
                                + rsImport.getString("precio6").trim() + ","
                                + rsImport.getString("precio7").trim() + ","
                                + rsImport.getString("precio8").trim() + ","
                                + rsImport.getString("precio9").trim() + ","
                                + rsImport.getString("precio10").trim() + ","
                                + rsImport.getString("precio11").trim() + ","
                                + rsImport.getString("precio12").trim() + ","
                                + rsImport.getString("precio13").trim() + ","
                                + rsImport.getString("precio14").trim() + ","
                                + rsImport.getString("precio15").trim() + ","
                                + rsImport.getString("precio16").trim() + ","
                                + rsImport.getString("precio17").trim() + ","
                                + rsImport.getString("precio18").trim() + ","
                                + rsImport.getString("precio19").trim() + ","
                                + rsImport.getString("precio20").trim() + ","
                                + rsImport.getString("precio21").trim() + ","
                                + rsImport.getString("precio22").trim() + ","
                                + rsImport.getString("precio23").trim() + ","
                                + rsImport.getString("precio24").trim() + ","
                                + rsImport.getString("precio25").trim() + ","
                                + rsImport.getString("precio26").trim() + ","
                                + rsImport.getString("precio27").trim() + ","
                                + rsImport.getString("precio28").trim() + ","
                                + rsImport.getString("precio29").trim() + ","
                                + rsImport.getString("precio30").trim() + ","
                                + rsImport.getString("PrePorVal1").trim() + ","
                                + rsImport.getString("PrePorVal2").trim() + ","
                                + rsImport.getString("PrePorVal3").trim() + ","
                                + rsImport.getString("PrePorVal4").trim() + ","
                                + rsImport.getString("PrePorVal5").trim() + ","
                                + rsImport.getString("PrePorVal6").trim() + ","
                                + rsImport.getString("PrePorVal7").trim() + ","
                                + rsImport.getString("PrePorVal8").trim() + ","
                                + rsImport.getString("PrePorVal9").trim() + ","
                                + rsImport.getString("desc1").trim() + ","
                                + rsImport.getString("desc2").trim() + ","
                                + rsImport.getString("desc3").trim() + ","
                                + rsImport.getString("desc4").trim() + ","
                                + rsImport.getString("desc5").trim() + ","
                                + rsImport.getString("desc6").trim() + ","
                                + rsImport.getString("Exist").trim()+ ","
                                + rsImport.getString("ExistFec").trim()+ ","
                                + rsImport.getString("ClaArtCod").trim() + ",'"
                                + rsImport.getString("ClaArtNom").trim() + "',"
                                + rsImport.getString("ArtEmb").trim() + ",'"
                                + rsImport.getString("ArtIndMpm").trim() + "','"
                                + rsImport.getString("MovParArtFecMod").trim() + "',"
                                + rsImport.getString("PrePreFijCosPro").trim() + ","
                                + rsImport.getString("ArtValImp").trim() + ",'"
                                + rsImport.getString("InvGruDifCheck").trim() + "','"
                                + rsImport.getString("InvSubDifCheck").trim() + "','"
                                + rsImport.getString("InvFamDifCheck").trim() + "','"
                                + rsImport.getString("ArtCodBar").trim() + "')";

                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                                Errores+=1;
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Log.e("Art",ex.toString());
                            Errores+=1;

                        }

                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            Log.e("ErrorAr2t",e.toString());

                            int hh=0;
                        }

                    }

                } catch (Exception e) {
                    Log.e("ErrorArt",e.toString());
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }/*finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }
*/

                ErroresGen+=Errores;
                Log.e("error1poll",String.valueOf(ErroresGen));

                //TraerPrecios();

                //BaseDatos vBaseDeDatos;
//                vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
//                String Script = "select count(*) from articulos";
//                Cursor vCursorUsuarios = vBaseDeDatos.getReadableDatabase().rawQuery(Script, null);
//                vCursorUsuarios.moveToFirst();
//                vtxt_OkImport.setText(vCursorUsuarios.getInt(0));
//                vCursorUsuarios.moveToFirst();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};
                CargarDatos();

                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};


            }
        }).start();


        return Resultado;
    }

    public  int[] CargarCartera(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntCartera);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkCartera);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrCartera);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_Cartera);
        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();




                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();

                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="";
   if(vEmpresa.equalsIgnoreCase("MENTAHAIR") || vEmpresa.equalsIgnoreCase("MENTAHAIRCOT")){
       Script="select *,isnull(FacTotalImpuestos,0)-isnull(facsaldo,0) FacAbono from(select ROW_NUMBER() OVER(ORDER BY facsec ASC) AS Row,FacSec,isnull(FacVenCod,'') FacVenCod,MovNitSec,isnull(MovCliSec,0) MovCliSec,MovFacSec,isnull((select sum(karvaltotMenDes+KarArtIva) from kardex k where k.facsec=f.facsec),0) FacTotalImpuestos,isnull((select sum(karvaltotMenDes) from kardex k where k.facsec=f.facsec),0) karvaltotmendes, " +
               "(debito-credito) FacSaldo,CONVERT(DATE,isnull(FacFec,Getdate())) FacFec,isnull(FacConPag,0) FacConPag,CONVERT(DATE,(isnull(FacFec,Getdate())+isnull(FacConPag,0))) FacVen," +
               " CASE FacTipcod when 'NC'  then 0 else\n" +
               " DATEDIFF(day,(isnull(FacFec,Getdate())+isnull(FacConPag,0)),GETDATE()) end FacMora" +
               ",isnull(FacPedCon,'') FacPedCon ,  f.FacSecDev,isnull((select facnro from factura where facsec = f.facsecdev), '') FacNroDev , isnull((select ConNotNom from conceptoncnd where connotcod = f.connotcod),'') ConNotNom from ("+
               "select compucsec,movnitsec,movclisec,movfacsec,sum(isnull(movdeb,0)) debito,sum(isnull(MovCre,0)) credito "+
               "from comprobantedetalle cd left join comprobante c on c.comsec=cd.comsec "+
               "left join Factura on facnitsec=MovNitSec and FacCliSec=MovCliSec and MovFacSec=FacNro "+
               "left join clientesvendedores cv on facnitsec=cv.nitsec and cv.clisec=facclisec "+
               "where compucsec IN(select pucsec from puc where PucCod IN("+conbd.getCuentaCartera()+")) "+ //1305050101  //13050501 dyd
               "and cv.VenCod='"+vUsuario+"' and comestado='A'  "+
               "group by compucsec,movnitsec,movclisec,movfacsec ) cartera "+
               "left join Factura f on facnro=MovFacSec and facnitsec=movNitSec and FacCliSec=MovCliSec "+
               "where debito-credito<>0) Consulta order by Row desc ";
   }else{
       Script="select *,isnull(FacTotalImpuestos,0)-isnull(facsaldo,0) FacAbono from(select ROW_NUMBER() OVER(ORDER BY facsec ASC) AS Row,FacSec,isnull(FacVenCod,'') FacVenCod,MovNitSec,isnull(MovCliSec,0) MovCliSec,MovFacSec,isnull((select sum(karvaltotMenDes+KarArtIva) from kardex k where k.facsec=f.facsec),0) FacTotalImpuestos,isnull((select sum(karvaltotMenDes) from kardex k where k.facsec=f.facsec),0) karvaltotmendes, " +
               "(debito-credito) FacSaldo,CONVERT(DATE,isnull(FacFec,Getdate())) FacFec,isnull(FacConPag,0) FacConPag,CONVERT(DATE,(isnull(FacFec,Getdate())+isnull(FacConPag,0))) FacVen,DATEDIFF(day,(isnull(FacFec,Getdate())+isnull(FacConPag,0)),GETDATE()) FacMora,isnull(FacPedCon,'') FacPedCon ,  f.FacSecDev,isnull((select facnro from factura where facsec = f.facsecdev), '') FacNroDev , isnull((select ConNotNom from conceptoncnd where connotcod = f.connotcod),'') ConNotNom from ("+
               "select compucsec,movnitsec,movclisec,movfacsec,sum(isnull(movdeb,0)) debito,sum(isnull(MovCre,0)) credito "+
               "from comprobantedetalle cd WITH (NOLOCK) left join comprobante c WITH (NOLOCK) on c.comsec=cd.comsec "+
               "left join Factura WITH (NOLOCK) on facnitsec=MovNitSec and FacCliSec=MovCliSec and MovFacSec=FacNro "+
               "left join clientesvendedores cv WITH (NOLOCK) on facnitsec=cv.nitsec and cv.clisec=facclisec "+
               "where compucsec IN(select pucsec from puc WITH (NOLOCK) where PucCod IN("+conbd.getCuentaCartera()+")) "+ //1305050101  //13050501 dyd
               "and cv.VenCod='"+vUsuario+"' and comestado='A'  "+
               "group by compucsec,movnitsec,movclisec,movfacsec ) cartera "+
               "left join Factura f  WITH (NOLOCK) on facnro=MovFacSec and facnitsec=movNitSec and FacCliSec=MovCliSec "+
               "where debito-credito<>0) Consulta order by Row desc ";
   }

Log.e("CarteraSql: ",Script);
//                    String Script="select * from(select ROW_NUMBER() OVER(ORDER BY perclicod ASC) AS Row,PerCliCod,PerClaArtCod,PerCliDetDes1 from PerfilClientesClase) jj order by Row desc";
                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from Cartera");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);



                        //vPrBar_Ciudades.

                        String InsertScript ="";
                        try {
                         InsertScript = "insert into Cartera (FacSec,MovNitSec,MovCliSec,MovFacSec,FacTotalImpuestos,FacAbonos,FacSaldo,FacFec,FacConPag,FacVen,FacVenCod,FacPedCon,FacMora,FacNroDev,ConNotNom,karvaltotmendes) values ("
                                + rsImport.getString("FacSec").trim() + ",'"
                                + rsImport.getString("MovNitSec").trim() + "',"
                                + rsImport.getString("MovCliSec").trim() + ",'"
                                + rsImport.getString("MovFacSec").trim() + "',"
                                + rsImport.getString("FacTotalImpuestos").trim() + ","
                                + rsImport.getString("FacAbono").trim() + ","
                                + rsImport.getString("FacSaldo").trim() + ",'"
                                + rsImport.getString("FacFec").trim() + "',"
                                + rsImport.getString("FacConPag").trim() + ",'"
                                + rsImport.getString("FacVen").trim() + "','"
                                 + rsImport.getString("FacVenCod").trim() + "','"
                                 + rsImport.getString("FacPedCon").trim() + "',"
                                 + rsImport.getString("FacMora").trim() + ",'"
                                 + rsImport.getString("FacNroDev").trim() + "','"
                                 + rsImport.getString("ConNotNom").trim() + "',"
                                  + rsImport.getString("karvaltotmendes").trim() + ")";

                            try {
                                BdSql.execSQL(InsertScript);
                                Insertados+=1;

                            }catch (Exception ex){
                                Errores+=1;

                            }

                            try {
                                final int finalTotalFilas = TotalFilas;
                                final int finalInsertados = Insertados;
                                final int finalErrores = Errores;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                        vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                        vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                        // vPrBar_Ciudades.setMax(TotalFilas);
                                        // vPrBar_Ciudades.setProgress(Vueltas);
                                    }
                                });
                            }catch (Exception e){
                                Log.e("1eCartera",e.toString());
                                int hh=0;
                            }

                        }catch (Exception ex){
                        //    String InsertScript ="";
                            Log.e("exCartera",ex.toString());
                            Errores+=1;

                        }

                    }
                } catch (Exception e) {
                    Log.e("eCartera",e.toString());
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }
                finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }


                ErroresGen+=Errores;
                Log.e("error1oiol",String.valueOf(ErroresGen));

                CargarDatos();
            }

        }).start();


        return Resultado;
    }

    public  int[] CargarGrupo(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntGrupo);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkGrupo);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrGrupo);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_Grupo);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();

                String vEmpresa=vGlobalVariables.getEmpresa();
                vEmpresa=vEmpresa.toUpperCase();

                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String IbCan="";
                    if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") ||  vEmpresa.trim().equalsIgnoreCase("SURTIMARCAS") ) {
                         IbCan = "REPLACE(" +
                                "    ISNULL((SELECT ', '  + rtrim(ltrim(cast(invcancod as varchar)))" +
                                "    FROM [InventarioGrupoCanal] IC WHERE IC.INVGRUCOD=InventarioGrupo.InvGruCod" +
                                "    FOR XML PATH('')),'XX'),' ','') As InvCanCodStr";
                    }else{
                         IbCan = "'XX' InvCanCodStr";
                    }
                    String Script="select * from(select ROW_NUMBER() OVER(ORDER BY InvGruCod ASC) AS Row,InvGruCod,InvGruNom,"+IbCan+" from InventarioGrupo) jj order by Row desc";
                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from InventarioGrupo");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);


                        //vPrBar_Ciudades.

                        String InsertScript = "insert into InventarioGrupo (InvGruCod,InvCanCodStr,InvGruNom) values ('"
                                + rsImport.getString("InvGruCod").trim() + "','"
                                + rsImport.getString("InvCanCodStr").trim() + "','"
                                + rsImport.getString("InvGruNom").trim().replace("'","") + "')" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;

                        }
                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            Log.e("3122",e.toString());

                            int hh=0;
                        }
//*/

                    }
                } catch (Exception e) {
                    Log.e("2312",e.toString());

                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }
                finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

                ErroresGen+=Errores;
                Log.e("error1lñlñl",String.valueOf(ErroresGen));

                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        }).start();



        return Resultado;
    }

    public  int[] CargarSubGrupo(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntSubGrupo);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkSubGrupo);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrSubGrupo);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_SubGrupo);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="select * from(select ROW_NUMBER() OVER(ORDER BY InvSubGruCod ASC) AS Row,InvSubGruCod,InvGruCod,InvSubGruNom from inventariosubgrupo) jj order by Row desc";
                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from inventariosubgrupo");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);

                        String InsertScript = "insert into inventariosubgrupo (InvSubGruCod,InvGruCod,InvSubGruNom) values ('"
                                + rsImport.getString("InvSubGruCod").trim() + "','"
                                + rsImport.getString("InvGruCod").trim() + "','"
                                + rsImport.getString("InvSubGruNom").trim() + "')" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;

                        }

                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                        }
                    }
                } catch (Exception e) {
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }



                ErroresGen+=Errores;
                Log.e("error1rtr45",String.valueOf(ErroresGen));

                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        }).start();
        return Resultado;
    }

    public  int[] CargarFamilia(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntFamilia);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkFamilia);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrFamilia);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_Familia);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="select * from(select ROW_NUMBER() OVER(ORDER BY InvFamCod ASC) AS Row,InvFamCod,InvSubGruCod,InvFamNom from inventariofamilia) jj order by Row desc";
                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from inventariofamilia");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);


//*/

                        //vPrBar_Ciudades.

                        String InsertScript = "insert into inventariofamilia (InvFamCod,InvSubGruCod,InvFamNom) values ('"
                                + rsImport.getString("InvFamCod").trim() + "','"
                                + rsImport.getString("InvSubGruCod").trim() + "','"
                                + rsImport.getString("InvFamNom").trim().replace("'","-") + "')" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;

                        }
                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                        }
                    }
                } catch (Exception e) {
                    Log.e("ErrorMovfam",e.toString());
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }
                ErroresGen+=Errores;
                Log.e("error1fgdsw",String.valueOf(ErroresGen));

                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        }).start();



        return Resultado;
    }

    public  int[] CargarMovParBonProdBon(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntMovparbonProdBon);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkMovparbonProdBon);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrMovparbonProdBon);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_MovparbonProdBon);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="select * from(select ROW_NUMBER() OVER(ORDER BY MovParBonProdBon.MovParBonSec ASC) AS Row,MovParBonProdBon.MovParBonSec,MovParBonEncArtSec,MovParBonCant,isnull(MovParBonCantCaj,0) MovParBonCantCaj,(select artemb from articulos where artsec=MovParBonEncArtSec) MovParBonEmb, isnull(convert(varchar(10),MovParBonFecMod,120),'') MovParBonFecMod " +
                            ",REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(MovParBonCanCod as varchar))) FROM MovParBonCanales TABLA WHERE TABLA.MovParBonSec=mpb.MovParBonSec and MOVPARBONCANNO<>'S' FOR XML PATH('')),'XX'),' ','')+',' As CANALES " +
                            ",REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(movparbonnit)) FROM MovParBonClientes TABLA WHERE TABLA.MovParBonSec=mpb.MovParBonSec AND MovParBonNitNo <> 'S'  FOR XML PATH('')),'XX'),' ','')+',' As CLIENTES " +
                            ",REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(movparbonnit)) FROM MovParBonClientes TABLA WHERE TABLA.MovParBonSec=mpb.MovParBonSec AND MovParBonNitNo = 'S' FOR XML PATH('')),'XX'),' ','')+',' As CLIENTESEXCLU " +
                            " from MovParBonProdBon left join MovParBon mpb on MovParBonProdBon.MovParBonSec= mpb.MovParBonSec  where CAST(MovParBonFecIni AS DATE) <= CAST(GETDATE() AS DATE) and CAST(MovParBonFecFin AS DATE) >= CAST(GETDATE() AS DATE) and MovParBonResUni like '%("+vAliNegCod+")%' ) jj order by Row desc";
                     rsImport = comm.executeQuery(Script);

                     Log.e("Saqlbonifi",Script);
                    BdSql.execSQL("Delete from MovParBonProdBon");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);



                        //vPrBar_Ciudades.

                        String InsertScript = "insert into MovParBonProdBon (MovParBonSec,MovParBonEncArtSec,MovParBonCanales,MovParBonClientes,MovParBonClientesExlu,MovParBonFecMod,MovParBonEmb,MovParBonCantCaj,MovParBonCant) values ("
                                + rsImport.getString("MovParBonSec").trim() + ",'"
                                + rsImport.getString("MovParBonEncArtSec").trim() + "','"
                                + rsImport.getString("CANALES").trim() + "','"
                                + rsImport.getString("CLIENTES").trim() + "','"
                                + rsImport.getString("CLIENTESEXCLU").trim() + "','"
                                + rsImport.getString("MovParBonFecMod").trim() + "',"
                                + rsImport.getString("MovParBonEmb").trim() + ","
                                + rsImport.getString("MovParBonCantCaj").trim() + ","
                                + rsImport.getString("MovParBonCant").trim() + ")" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;

                        }
                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                        }
//*/
                    }
                } catch (Exception e) {
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }
                ErroresGen+=Errores;
                Log.e("error1sfsvcxxcv",String.valueOf(ErroresGen));

                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        }).start();



        return Resultado;
    }


    public  int[] CargarMovParBonBonificados(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntMovparbonbonificados);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkMovparbonbonificados);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrMovparbonbonificados);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_Movparbonbonificados);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();

                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="select * from(select ROW_NUMBER() OVER(ORDER BY MovParBonBonificados.MovParBonSec ASC) AS Row,MovParBonBonificados.MovParBonSec,MovParBonArtSec,MovParBonDetCant " +
                            " from MovParBonBonificados left join MovParBon mpb on MovParBonBonificados.MovParBonSec= mpb.MovParBonSec  where CAST(MovParBonFecIni AS DATE) <= CAST(GETDATE() AS DATE) and CAST(MovParBonFecFin AS DATE) >= CAST(GETDATE() AS DATE) and  MovParbonResUni like '%("+vAliNegCod+")%' ) jj order by Row desc";
                     rsImport = comm.executeQuery(Script);
                     Log.e("sqlboni",Script);
                    BdSql.execSQL("Delete from MovParBonBonificados");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);


//*/

                        //vPrBar_Ciudades.

                        String InsertScript = "insert into MovParBonBonificados (MovParBonSec,MovParBonArtSec,MovParBonDetCant) values ("
                                + rsImport.getString("MovParBonSec").trim() + ",'"
                                + rsImport.getString("MovParBonArtSec").trim() + "',"
                                + rsImport.getString("MovParBonDetCant").trim() + ")" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;

                        }

                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                        }

                    }
                } catch (Exception e) {
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }



                ErroresGen+=Errores;
                Log.e("err23324343535or1",String.valueOf(ErroresGen));

                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        }).start();



        return Resultado;
    }


    public  int[] VentasPorLinea(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_Cntventaxlinea);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_Okventaxlinea);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_Errventaxlinea);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_ventaxlinea);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="select * from( " +
                            "select ROW_NUMBER() OVER(ORDER BY facnitsec ASC) AS Row,* " +
                            "from ( " +
                            "select isnull(facnitsec,'') facnitsec, isnull(facclisec,0) facclisec,isnull(k.artsec,'') artsec,isnull(fa.invfamcod,'') invfamcod,isnull(sb.invsubgrucod,'') invsubgrucod,isnull(sb.invgrucod,'') invgrucod,cast(sum(karvaltotmendes) as int) karvaltotmendes " +
                            "from Kardex k WITH (NOLOCK) " +
                            "left join articulos a WITH (NOLOCK) on a.artsec=k.artsec " +
                            "left join inventariofamilia fa WITH (NOLOCK) on a.invfamcod=fa.invfamcod " +
                            "left join inventariosubgrupo sb WITH (NOLOCK) on sb.invsubgrucod=fa.invsubgrucod " +
                            "left join factura f WITH (NOLOCK) on f.facsec=k.facsec  " +
                            "where  facfec>=(convert(datetime,CONVERT(varchar(10), GETDATE(), 103),103)-day(getdate())+1) and f.facVenCod='"+vUsuario+"' and FacTipTra='FDV' and (karuni+(karcaj*KarArtEmb))<>0  " +
                            "group by facnitsec,facclisec,k.artsec,fa.invfamcod,sb.invsubgrucod,sb.invgrucod) fk ) jj  order by Row desc ";
                     rsImport = comm.executeQuery(Script);
                     Log.e("sql;: ",Script);
                    BdSql.execSQL("Delete from VentasCliente");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);

                        String InsertScript = "insert into VentasCliente (nitsec,clisec,artsec,karvaltotmendes,invfamcod,invsubgrucod,invgrucod) values ('"
                                + rsImport.getString("facnitsec").trim() + "',"
                                + rsImport.getString("facclisec").trim() + ",'"
                                + rsImport.getString("artsec").trim() + "',"
                                + rsImport.getString("karvaltotmendes").trim() + ",'"
                                + rsImport.getString("invfamcod").trim() + "','"
                                + rsImport.getString("invsubgrucod").trim() + "','"
                                + rsImport.getString("invgrucod").trim() + "')";
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Log.e("e ventas",ex.toString());
                            Errores+=1;
                        }

                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                            Log.e("e2 ventas",e.toString());
                        }

                    }
                } catch (Exception e) {
                    Errores=1;
                    Log.e("e3 ventas",e.toString());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }


                ErroresGen+=Errores;
                Log.e("error134242342342323",String.valueOf(ErroresGen));

                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
      }).start();



        return Resultado;
    }


    public  int[] CargarHistorialNotas(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_Cnthistorialnot);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_Okhistorialnot);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_Errhistorialnot);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_historialnot);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();

                    String vEmpresa=vGlobalVariables.getEmpresa();
                    vEmpresa=vEmpresa.toUpperCase();
                    String Script="";
                    if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") ) {

                       // Script= "select * from( " +
                       //        "select ROW_NUMBER() OVER(ORDER BY BloNitSec ASC) AS Row,*,( select BloNitSec,BloCliSec,BloArtSec,0 karuni,0 precio,0 dias from MovBloqueoCliete where Blovencod='"+vUsuario+"' ) jj order by Row desc";
                        Script= "select * from( " +
                               "select ROW_NUMBER() OVER(ORDER BY facnitsec ASC) AS Row,*,(" +
                                "select top 1 cast(karvaltotMenDes/(karuni+(karcaj*KarArtEmb)) as int) precio  from Kardex kk " +
                                "left join factura ff on ff.facsec=kk.facsec  " +
                                "where ff.facnitsec=f.facnitsec and ff.facclisec=f.facclisec and kk.ArtSec=f.ArtSec and (karuni+(karcaj*KarArtEmb))<>0) precio " +
                                "from( " +
                                "select facnitsec,facclisec,artsec,cast(sum((karuni+(karcaj*KarArtEmb))) as int) KarUni,DATEDIFF (DAY,  CONVERT (date, FacFec),CONVERT (date, GETDATE()) ) dias " +
                                "from Kardex k " +
                                "left join factura f on f.facsec=k.facsec  " +
                                "where karprepub<=10 and facfec>='01/07/2020' and f.facVenCod='" + vUsuario + "' and FacTipTra='FDV' and (karuni+(karcaj*KarArtEmb))<>0  " +
                                "group by facnitsec,facclisec,artsec,facfec) f ) jj order by Row desc";
                    }else{

                         Script = "select * from( " +
                                "select ROW_NUMBER() OVER(ORDER BY facnitsec ASC) AS Row,*,(" +
                                "select top 1 cast(karvaltotMenDes/(karuni+(karcaj*KarArtEmb)) as int) precio  from Kardex kk " +
                                "left join factura ff on ff.facsec=kk.facsec  " +
                                "where ff.facnitsec=f.facnitsec and ff.BloCliSec=f.facclisec and kk.ArtSec=f.BloArtSec and (karuni+(karcaj*KarArtEmb))<>0) precio " +
                                "from( " +
                                "select facnitsec BloNitSec,facclisec BloCliSec,artsec BloArtSec,cast(sum((karuni+(karcaj*KarArtEmb))) as int) KarUni,DATEDIFF (DAY,  CONVERT (date, FacFec),CONVERT (date, GETDATE()) ) dias " +
                                "from Kardex k " +
                                "left join factura f on f.facsec=k.facsec  " +
                                "where 1=2 and f.facVenCod='" + vUsuario + "' and FacTipTra='FDV' and (karuni+(karcaj*KarArtEmb))<>0  " +
                                "group by facnitsec,facclisec,artsec,facfec) f ) jj order by Row desc";

                        Script= "select * from( " +
                                "select ROW_NUMBER() OVER(ORDER BY facnitsec ASC) AS Row,*,(" +
                                "select top 1 cast(karvaltotMenDes/(karuni+(karcaj*KarArtEmb)) as int) precio  from Kardex kk " +
                                "left join factura ff on ff.facsec=kk.facsec  " +
                                "where ff.facnitsec=f.facnitsec and ff.facclisec=f.facclisec and kk.ArtSec=f.ArtSec and (karuni+(karcaj*KarArtEmb))<>0) precio " +
                                "from( " +
                                "select facnitsec,facclisec,artsec,cast(sum((karuni+(karcaj*KarArtEmb))) as int) KarUni,DATEDIFF (DAY,  CONVERT (date, FacFec),CONVERT (date, GETDATE()) ) dias " +
                                "from Kardex k " +
                                "left join factura f on f.facsec=k.facsec  " +
                                "where 1=2 and f.facVenCod='" + vUsuario + "' and FacTipTra='FDV' and (karuni+(karcaj*KarArtEmb))<>0  " +
                                "group by facnitsec,facclisec,artsec,facfec) f ) jj order by Row desc";
                    }

                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from ClientesDevoluciones");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);


                        String InsertScript = "insert into ClientesDevoluciones (nitsec,clisec,artsec,karuni,karprepub,dias) values ('"
                                + rsImport.getString("facnitsec").trim() + "',"
                                + rsImport.getString("facclisec").trim() + ",'"
                                + rsImport.getString("artsec").trim() + "',"
                                + rsImport.getString("karuni").trim() + ","
                                + rsImport.getString("precio").trim() + ","
                                  + rsImport.getString("dias").trim()+")" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Log.e("ex historial",ex.toString());
                            Errores+=1;

                        }

                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                            Log.e("e historial",e.toString());
                        }

                    }
                } catch (Exception e) {
                    Log.e("ErrorMovhisd",e.toString());
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                } /*finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }*/


                ErroresGen+=Errores;
                Log.e("errdsfaerr233or1",String.valueOf(ErroresGen));

                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        }).start();



        return Resultado;
    }


    public  int[] CargarConceptosNotas(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntConceptosNotas);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkConceptosNotas);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrConceptosNotas);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_ConceptosNotas);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="select * from(select ROW_NUMBER() OVER(ORDER BY condevmersec ASC) AS Row,condevmersec,condevmerdes from conceptodevmer where condevmertip='N' ) jj order by Row desc";
                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from ConceptoNCND");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);


                        String InsertScript = "insert into ConceptoNCND(ConNotCod,ConNotNom) values ("
                                + rsImport.getString("condevmersec").trim() + ",'"
                                + rsImport.getString("condevmerdes").trim() + "')" ;

                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;

                        }

                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                        }

                    }
                } catch (Exception e) {
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }



                ErroresGen+=Errores;
                Log.e("errortert341",String.valueOf(ErroresGen));

                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        }).start();



        return Resultado;
    }

    public  int[] CargarCanalSubCanal(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntCanalSubCanal);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkCanalSubCanal);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrCanalSubCanal);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_CanalSubCanal);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="select * from(select ROW_NUMBER() OVER(ORDER BY c.cancod ASC) AS Row,c.cancod,c.cannom,cansubcod,cansubnom,isnull((select contamclicon from condiciontamcli where contamclicancod=sb.cancod and contamclicantamcod=1 and contamclicancod=sb.cancod  and contamclicansubcod=sb.cansubcod   ),'') cona,isnull((select contamclicon from condiciontamcli where contamclicancod=sb.cancod and contamclicantamcod=2 and contamclicancod=sb.cancod  and contamclicansubcod=sb.cansubcod ),'') conb,isnull((select contamclicon from condiciontamcli where contamclicancod=sb.cancod and contamclicantamcod=3 and contamclicancod=sb.cancod  and contamclicansubcod=sb.cansubcod ),'') conc from canalessubcanales sb left join canales c on c.cancod=sb.cancod) jj order by Row desc";
                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from Canales");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);


                        String InsertScript = "insert into Canales(cancod,cansubcod,cannom,cansubnom,cancona,canconb,canconc) values ("
                                + rsImport.getString("cancod").trim() + ","
                                + rsImport.getString("cansubcod").trim() + ",'"
                                + rsImport.getString("cannom").trim() + "','"
                                + rsImport.getString("cansubnom").trim() + "','"
                                + rsImport.getString("cona").trim() + "','"
                                + rsImport.getString("conb").trim() + "','"
                                + rsImport.getString("conc").trim() + "')" ;


                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;

                        }

                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                        }

                    }
                } catch (Exception e) {
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }



                ErroresGen+=Errores;
                Log.e("errorrr4545ggfbg1",String.valueOf(ErroresGen));

                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        }).start();



        return Resultado;
    }


    public  int[] MovParMixBonificados(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntMovParMixBonificados);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkMovParMixBonificados);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrMovParMixBonificados);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_MovParMixBonificados);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="select * from(select ROW_NUMBER() OVER(ORDER BY MovParMixBonificados.MovParMixSec ASC) AS Row,MovParMixBonificados.MovParMixSec,MovParMixBonArtSec,MovParMixBonCant from MovParMixBonificados  left join MovParMix mxb on MovParMixBonificados.MovParMixSec=mxb.MovParMixSec where MovParMixResUni like '%("+vAliNegCod+")%'  ) jj order by Row desc";
                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from MovParMixBonificados");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();
                        }

                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);



                        try {
                            String InsertScript = "insert into MovParMixBonificados(MovParMixSec,MovParMixBonArtSec,MovParMixBonCant) values ('"
                                    + rsImport.getString("MovParMixSec").trim() + "','"
                                    + rsImport.getString("MovParMixBonArtSec").trim() + "',"
                                    + rsImport.getString("MovParMixBonCant").trim() + ")";
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;
                        }catch (Exception ex){
                            Errores+=1;
                        }

                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                        }

                    }
                } catch (Exception e) {
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }
                ErroresGen+=Errores;
                Log.e("error5522ere1",String.valueOf(ErroresGen));

                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};
            }
        }).start();



        return Resultado;
    }

    public  int[] MovParArt(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_Cntmovparart);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_Okmovparart);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_Errmovparart);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_movparart);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();

                    String Script="select * from(select ROW_NUMBER() OVER(ORDER BY mpa.MovParArtSec ASC) AS Row," +
                            " isnull(mpa.MovParArtSec,0) MovParArtSec," +
                            " isnull(MovParResCan,'') MovParResCan," +
                            " isnull(MovParNumDcto,'') MovParNumDcto ," +
                            " isnull(MovParNoOtor,'') MovParNoOtor," +
                            " isnull(MovParViaDir,'') MovParViaDir," +
                            " isnull(MovParArtDetArtSec,'') MovParArtDetArtSec," +
                            " isnull(MovParArtDetDesc,0)+isnull(MovParArtDetDesc2,0) MovParArtDetDesc," +
                            " isnull(convert(varchar(10),MovParArtFecMod,120),'') MovParArtFecMod," +
                            " REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(cast(MovParArtCanCod as varchar))) FROM MovParArtCanales TABLA " +
                            " WHERE TABLA.MovParartsec= mpaa.MovParartsec and MovParArtCanNo<>'S' FOR XML PATH('')),'XX'),' ','')+',' As CANALES ," +
                            "   REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(MovParArtCliNitSec)) FROM MovParArtClientes TABLA WHERE TABLA.MovParartsec = mpaa.MovParartsec " +
                            "   FOR XML PATH('')),'XX'),' ','')+',' As CLIENTES ," +
                            "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(MovParArtCliNitSec)) FROM MovParArtClientes " +
                            "TABLA WHERE TABLA.MovParartsec=mpaa.MovParartsec AND MovParArtCliNo='S' FOR XML PATH('')),'XX'),' ','')+',' As CLIENTESEXCLU,  " +
                            " isnull(" +
                            " (select case CARUNISUCCOD  when 1 then '68001' when 2 then '54001'end MovParArtCiucod   from carunidades where Alinegcod = 23),'68001' )  MovParArtCiucod  " +
                            " from MovParArtArticulos mpaa  left join movparart mpa  on mpa.MovParartsec=mpaa.MovParartsec left join movparartciudades mc on mc.MovParArtSec = mpaa.MovParartsec where MovParResUni like '%("+vAliNegCod+")%' and  MovParArtFecIni<=CONVERT(date, GETDATE()) and MovParArtFecFin>=CONVERT(date, GETDATE()) ) jj order by Row desc";
                    Log.e("SQLMOVART",Script);

                    rsImport = comm.executeQuery(Script);

                    BdSql.execSQL("Delete from MovParArt");


                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();
                        }

                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);

                        try {
                            String InsertScript = "insert into MovParArt(MovParArtSec,MovParResCan,MovParNumDcto,MovParNoOtor,MovParViaDir,MovParArtDetArtSec,MovParArtDetDesc,MovParArtFecMod,MovParArtciucod,CLIENTES,CANALES,CLIENTESEX) values ("
                                    + rsImport.getString("MovParArtSec").trim() + ",'"
                                    + rsImport.getString("MovParResCan").trim() + "','"
                                    + rsImport.getString("MovParNumDcto").trim() + "','"
                                    + rsImport.getString("MovParNoOtor").trim() + "','"
                                    + rsImport.getString("MovParViaDir").trim() + "','"
                                    + rsImport.getString("MovParArtDetArtSec").trim() + "',"
                                    + rsImport.getString("MovParArtDetDesc").trim() + ",'"
                                    + rsImport.getString("MovParArtFecMod").trim() + "','"
                                    + rsImport.getString("MovParArtCiucod").trim() + "','"
                                    + rsImport.getString("CLIENTES").trim() + "','"
                                    + rsImport.getString("CANALES").trim() + "','"
                                    + rsImport.getString("CLIENTESEXCLU").trim() + "')";


                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;
                        }catch (Exception ex){
                            Log.e("Erromovart",ex.toString());
                            Errores+=1;
                        }

                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                            Log.e("Erromovart",e.toString());

                        }

                    }
                } catch (Exception e) {
                    Errores=1;
                    Log.e("Erromovart",e.toString());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

                ErroresGen+=Errores;
                Log.e("errofdfsdf31",String.valueOf(ErroresGen));

                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};
            }
        }).start();



        return Resultado;
    }

    public  int[] MovParDespro(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_Cntmovparart);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_Okmovparart);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_Errmovparart);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_movparart);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                    conn = conbd.CargarConexion(getApplicationContext());
                    comm = conn.createStatement();

                    String Script=" select * from(select ROW_NUMBER() OVER(ORDER BY mpaa.DesProArtSec ASC) AS Row,\n" +
                            "  isnull(mpaa.DesProArtSec,0) DesProArtSec,\n" +
                            "  isnull(mpa.DesProCod,0) DesProCod,\n" +
                            "  isnull(mpaa.DesProPorDes,0) DesProPorDes,\n" +
                            "  REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(DesNitSec)) FROM DescuentosProgramadosClientes TABLA WHERE TABLA.DesProCod = mpaa.DesProCod \n" +
                            "  FOR XML PATH('')),'XX'),' ','')+',' As CLIENTES ,\n" +
                            "  REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(DesProLisPreCod)) FROM DESCUENTOSPROGRAMADOSLISTAS TABLA WHERE TABLA.DesProCod=mpaa.DesProCod \n" +
                            "  FOR XML PATH('')),'XX'),' ','')+',' As LISTAS\n" +
                            "  from DescuentosProgramadosDetalle mpaa  \n" +
                            "  left join DescuentosProgramados mpa  on mpa.DesProCod=mpaa.DesProCod \n" +
                            "   where  DesProEst = 'A' and    CONVERT(date,DesProFecIni) <=CONVERT(date, GETDATE()) \n" +
                            " and CONVERT(date,DesProFecFin) >=CONVERT(date, GETDATE())  )   jj order by Row desc ";


                    Log.e("SQLMOVART",Script);

                    rsImport = comm.executeQuery(Script);

                    BdSql.execSQL("Delete from MovParDesPro");


                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();
                        }

                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);

                        try {
                            String InsertScript = "insert into MovParDesPro(MovParDesArtSec,DesProCod,LISTAS,CLIENTES,MovParArtDetDesc) values ('"
                                    + rsImport.getString("DesProArtSec").trim() + "',"
                                    + rsImport.getString("DesProCod").trim() + ",'"
                                    + rsImport.getString("LISTAS").trim() + "','"
                                    + rsImport.getString("CLIENTES").trim() + "',"
                                    + rsImport.getString("DesProPorDes").trim() + ")";


                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;
                        }catch (Exception ex){
                            Log.e("Erromovart",ex.toString());
                            Errores+=1;
                        }

                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                            Log.e("Erromovart",e.toString());

                        }

                    }
                } catch (Exception e) {
                    Errores=1;
                    Log.e("Erromovart",e.toString());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

                ErroresGen+=Errores;
                Log.e("errofdfsdf31",String.valueOf(ErroresGen));

                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};
            }
        }).start();



        return Resultado;
    }





    public  int[] MovParVal(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_Cntmovparval);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_Okmovparval);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_Errmovparval);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_movparval);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="select * from(select ROW_NUMBER() OVER(ORDER BY mpl.MovParValSec ASC) AS Row,mpl.MovParValSec,MovParValDetRan1,MovParValDetRan2,MovParValDetDesc from movparvalrango mpl  left join MovParval mplg on mpl.MovParvalSec=mplg.MovParvalSec where MovParvalResUni like '%("+vAliNegCod+")%'  ) jj order by Row desc";
                    BdSql.execSQL("Delete from movparvalrango");
                     rsImport = comm.executeQuery(Script);
                    try {
                        while (rsImport.next()){
                            if(TotalFilas==0) {
                                vPrBar_Import.setMax(rsImport.getInt("Row"));
                                TotalFilas=rsImport.getInt("Row");
                                String Filas=rsImport.getString("Row").trim();
                            }

                            Vueltas+=1;
                            vPrBar_Import.setMax(TotalFilas);
                            vPrBar_Import.setProgress(Vueltas);

                            try {
                                String InsertScript = "insert into movparvalrango(MovParValSec,MovParValDetRan1,MovParValDetRan2,MovParValDetDesc) values ("
                                        + rsImport.getString("MovParValSec").trim() + ","
                                        + rsImport.getString("MovParValDetRan1").trim() + ","
                                        + rsImport.getString("MovParValDetRan2").trim() + ","
                                        + rsImport.getString("MovParValDetDesc").trim() + ")";

                                if (BdSql.isDbLockedByCurrentThread()){
                                    BdSql.endTransaction();
                                }
                                BdSql.execSQL(InsertScript);
                                Insertados+=1;
                            }catch (Exception ex){
                                Errores+=1;
                            }

                            try {
                                final int finalTotalFilas = TotalFilas;
                                final int finalInsertados = Insertados;
                                final int finalErrores = Errores;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                        vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                        vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                        // vPrBar_Ciudades.setMax(TotalFilas);
                                        // vPrBar_Ciudades.setProgress(Vueltas);
                                    }
                                });
                            }catch (Exception e){
                                int hh=0;
                            }

                        }
                    }catch (Exception e){
                        int hh=0;
                    }
                } catch (Exception e) {
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }
                ErroresGen+=Errores;
                Log.e("errorewe34e31",String.valueOf(ErroresGen));

                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};
            }
        }).start();



        return Resultado;
    }


    public  int[] MovParLinea(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_Cntmovparlinea);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_Okmovparlinea);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_Errmovparlinea);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_movparlinea);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;

                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();



                    String Script="select * from(select ROW_NUMBER() OVER(ORDER BY mpl.MovParLinSec ASC) AS Row,mpl.MovParLinSec,isnull(MovParLinResCan,'') MovParLinResCan,isnull(MovParLinViaDir,'') MovParLinViaDir,isnull(MovParLinNoOtor,'') MovParLinNoOtor,isnull(MovParLinEncInvGruCod,'') MovParLinInvGruCod,isnull(MovParLinInvSubGruCod,'') MovParLinInvSubGruCod,isnull(MovParLinInvInvFamCod,'') MovParLinInvInvFamCod,MovParLinDes,isnull(convert(varchar(10),MovParLinfecmod,120),'') MovParLinfecmod,ArtSec ";
                    Script+="from articulos a ";
                    Script+="left join inventariofamilia f on a.invfamcod=f.invfamcod ";
                    Script+="left join inventariosubgrupo s on f.invsubgrucod= s.invsubgrucod ";
                    Script+="left join inventariogrupo g on g.invgrucod=s.invgrucod ";
                    Script+="left join MovParLinea mpl on mpl.MovParLinEncInvGruCod=s.invgrucod ";
                    Script+=" left join MovParLineaUnidadNeg mpe on mpe.movparlinsec= mpl.movparlinsec " ;
                    Script+="inner join MovParLineaGrupos mplg on mpl.movparlinsec=mplg.movparlinsec  or mplg.movparlininvsubgrucod=s.InvsubGruCod or mplg.movparlininvinvfamcod=a.InvFamCod WHERE  mpe.AliNegCod=" + vAliNegCod + " ";
                    Script+=" ) jj order by Row desc";

                    BdSql.execSQL("Delete from MovParLinea");
                     rsImport = comm.executeQuery(Script);
                    try {
                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();
                        }

                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);

                        try {
                            String InsertScript = "insert into MovParLinea(MovParLinSec,MovParLinResCan,MovParLinViaDir,MovParLinNoOtor,MovParLinInvGruCod,MovParLinInvSubGruCod,MovParLinInvFamCod,MovParLinfecmod,MovParLinArtSec,MovParLinDes) values ("
                                    + rsImport.getString("MovParLinSec").trim() + ",'"
                                    + rsImport.getString("MovParLinResCan").trim() + "','"
                                    + rsImport.getString("MovParLinViaDir").trim() + "','"
                                    + rsImport.getString("MovParLinNoOtor").trim() + "','"
                                    + rsImport.getString("MovParLinInvGruCod").trim() + "','"
                                    + rsImport.getString("MovParLinInvSubGruCod").trim() + "','"
                                    + rsImport.getString("MovParLinInvInvFamCod").trim() + "','"
                                    + rsImport.getString("MovParLINfecmod").trim() + "','"
                                    + rsImport.getString("ArtSec").trim() + "',"
                                    + rsImport.getString("MovParLinDes").trim() + ")";

                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;
                        }catch (Exception ex){
                            Errores+=1;
                        }


                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                        }

                    }
                    }catch (Exception e){
                        int hh=0;
                    }
                } catch (Exception e) {
                    Log.e("Linea : ",String.valueOf(e.toString()));
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

                ErroresGen+=Errores;
                Log.e("errozvzdvvvr1",String.valueOf(ErroresGen));

                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};
            }
        }).start();



        return Resultado;
    }

    public  int[] MovParMixArticulos(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntMovParMixArticulos);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkMovParMixArticulos);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrMovParMixArticulos);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_MovParMixArticulos);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="select * from(select ROW_NUMBER() OVER(ORDER BY MovParMixArticulos.MovParMixSec ASC) AS Row,MovParMixArticulos.MovParMixSec,MovParMixDetArtSec,(select artemb from articulos where artsec=MovParMixDetArtSec) MovParMixDetArtEmb,MovParMixDetCntObl,MovParMixDetCntDes from MovParMixArticulos  left join MovParMix mxb on MovParMixArticulos.MovParMixSec=mxb.MovParMixSec where MovParMixResUni like '%("+vAliNegCod+"%)'  ) jj order by Row desc";
                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from MovParMixArticulos");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);

                        try {
                            String InsertScript = "insert into MovParMixArticulos(MovParMixSec,MovParMixDetArtSec,MovParMixDetArtEmb,MovParMixDetCntObl,MovParMixDetCntDes) values ('"
                                    + rsImport.getString("MovParMixSec").trim() + "','"
                                    + rsImport.getString("MovParMixDetArtSec").trim() + "',"
                                    + rsImport.getString("MovParMixDetArtEmb").trim() + ","
                                    + rsImport.getString("MovParMixDetCntObl").trim() + ","
                                    + rsImport.getString("MovParMixDetCntDes").trim() + ")";

                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;
                        }catch (Exception ex){
                            Errores+=1;
                        }

                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                        }

                    }
                } catch (Exception e) {
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }



                ErroresGen+=Errores;
                Log.e("errdddcvdsqor1",String.valueOf(ErroresGen));

                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        }).start();



        return Resultado;
    }

    public  int[] MovParMix(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntMovParMix);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkMovParMix);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrMovParMix);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_MovParMix);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();

                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="select * from(select ROW_NUMBER() OVER(ORDER BY MovParMix.MovParMixSec ASC) AS Row,MovParMix.MovParMixSec,MovParMixNom,MovParMixRefDis,MovParMixCntTotal,isnull(MovParMixConic,0) MovParMixConic,isnull(MovParMixResCan,'') MovParMixResCan,isnull(MovParMixNoOtor,'') MovParMixNoOtor,isnull(MovParMixViaDir,'') MovParMixViaDir,isnull(convert(varchar(10),MovParMixFecMod,120),'') MovParMixFecMod,isnull(MovParMixPeri,30) MovParMixPeri from MovParMix  where MovParMixResUni like '%("+vAliNegCod+")%' ) jj order by Row desc";
                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from MovParMix");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);
                        try {
                        String InsertScript = "insert into MovParMix(MovParMixSec,MovParMixNom,MovParMixRefDis,MovParMixCntTotal,MovParMixResCan,MovParMixNoOtor,MovParMixViaDir,MovParMixFecMod,MovParMixConic,MovParMixPeri) values ('"
                                + rsImport.getString("MovParMixSec").trim() + "','"
                                + rsImport.getString("MovParMixNom").trim() + "',"
                                + rsImport.getString("MovParMixRefDis").trim() + ","
                                + rsImport.getString("MovParMixCntTotal").trim() + ",'"
                                + rsImport.getString("MovParMixResCan").trim() + "','"
                                + rsImport.getString("MovParMixNoOtor").trim() + "','"
                                + rsImport.getString("MovParMixViaDir").trim() + "','"
                                + rsImport.getString("MovParMixFecMod").trim() + "',"
                                + rsImport.getString("MovParMixConic").trim() +","
                                + rsImport.getString("MovParMixPeri").trim() + ")";



                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;
                        }catch (Exception ex){
                            Log.e("ErrorMov",ex.toString());
                            Errores+=1;
                        }

                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            Log.e("ErrorMov",e.toString());
                            int hh=0;
                        }

                    }
                } catch (Exception e) {
                    Log.e("ErrorMov",e.toString());
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }


                ErroresGen+=Errores;
                Log.e("error1232wdsdd",String.valueOf(ErroresGen));

                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        }).start();



        return Resultado;
    }

    public  int[] MovParEsc(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntMovParEsc);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkMovParEsc);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrMovParEsc);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_MovParEsc);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                String vEmpresa=vGlobalVariables.getEmpresa();
                vEmpresa=vEmpresa.toUpperCase();

                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="";
                    if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") || vEmpresa.trim().equalsIgnoreCase("IBANEZPRU")) {
                        Script = "select * from(select ROW_NUMBER() OVER(ORDER BY me.MovParEscSec ASC) AS Row,me.MovParEscSec,artsec MovParEscArtSec,MovParEscDe,isnull(MovParEscDeCaj,0) MovParEscDeCaj,MovParEscHasta,isnull(MovParEscHastaCaj,0) MovParEscHastaCaj, " +
                                "isnull(MovParEscDesc1,0) MovParEscDesc1,isnull(MovParEscDesc2,0) MovParEscDesc2,isnull(MovParEscRanArtSec,'') MovParEscRanArtSec,isnull(MovParEscRanCant,0) MovParEscRanCant,isnull(MovParEscRanCantCaj,0) MovParEscRanCantCaj,ArtEmb MovParEscArtEmb,isnull(MovParEscInd,'') MovParEscInd,isnull(convert(varchar(10),MovParEscfecmod,120),'') MovParEscfecmod,isnull(MovParEscDesc,'') MovParEscDesc,Isnull(MovParEscResCan,'') MovParEscResCan,Isnull(MovParEscNoOtor,'') MovParEscNoOtor,Isnull(MovParEscViaDir,'') MovParEscViaDir FROM MovParEscEscala me  " +
                                "left join  ARTICULOS a on 1=1 " +
                                "left join MovParEsc mc on mc.MovParEscSec=me.MovParEscSec " +
                                "left join InventarioFamilia f on f.InvFamCod=a.InvFamCod " +
                                "left join InventarioSubgrupo s on s.InvSubGruCod=f.InvSubGruCod " +
                                "left join MovParEscUnidadNeg mpe on mpe.MovParEscSec= me.MovParEscSec " +
                                "WHERE  mpe.AliNegCod=" + vAliNegCod + " and  (f.InvFamCod IN(SELECT MovParEscInvFamCod FROM MovParEscDetalle md where md.MovParEscSec= me.MovParEscSec) " +
                               // "WHERE  mc.AliNegCod=" + vAliNegCod + " and  (f.InvFamCod IN(SELECT MovParEscInvFamCod FROM MovParEscDetalle md where md.MovParEscSec= me.MovParEscSec) " +
                                "or " +
                                "f.InvSubGruCod IN(SELECT MovParEscInvSubGruCod FROM MovParEscDetalle md where md.MovParEscSec= me.MovParEscSec) " +
                                "or " +
                                "s.InvGruCod IN(SELECT MovParEscInvGruCod FROM MovParEscDetalle md where md.MovParEscSec= me.MovParEscSec) " +
                                "or artsec in(select MovParEscartsec from MovParEscArticulos md where md.MovParEscSec= me.MovParEscSec)) " +
                                "and ArtIndMpm<>'S' and artsec not in (select MovParEscartsecNo from MovParEscArticulosnO md where md.MovParEscSec= me.MovParEscSec))jj order by Row desc ";
                    }else if(vEmpresa.trim().equalsIgnoreCase("TODORAPIDAS")) {
                        Script = "select ROW_NUMBER() OVER(ORDER BY MovParEscSec ASC) AS Row,* from(select "+
                        "me.MovParEscSec,artsec MovParEscArtSec,MovParEscDe, "+
                        "isnull(MovParEscDeCaj,0) MovParEscDeCaj,isnull(MovParEscHasta,0) MovParEscHasta,isnull(MovParEscHastaCaj,0) MovParEscHastaCaj, "+
                        "isnull(MovParEscDesc1,0) MovParEscDesc1,isnull(MovParEscDesc2,0) MovParEscDesc2,isnull(MovParEscRanArtSec,'') MovParEscRanArtSec, "+
                        "isnull(MovParEscRanCant,0) MovParEscRanCant,isnull(MovParEscRanCantCaj,0) MovParEscRanCantCaj,ArtEmb MovParEscArtEmb "+
                        ",MovParEscInd,isnull(convert(varchar(10),MovParEscfecmod,120),'') MovParEscfecmod,isnull(MovParEscDesc,'') MovParEscDesc, "+
                        "Isnull(MovParEscResCan,'') MovParEscResCan,Isnull(MovParEscNoOtor,'') MovParEscNoOtor,Isnull(MovParEscViaDir,'') MovParEscViaDir "+
                        "FROM MovParEscEscala me "+
                        "left join  ARTICULOS a on 1=1 "+
                        "left join MovParEsc mc on mc.MovParEscSec=me.MovParEscSec "+
                        "left join InventarioFamilia f on f.InvFamCod=a.InvFamCod "+
                        "left join InventarioSubgrupo s on s.InvSubGruCod=f.InvSubGruCod "+
                        "left join MovParEscUnidadNeg mpe on mpe.MovParEscSec= me.MovParEscSec "+
                        "WHERE  mpe.AliNegCod=1 "+
                        "and  (f.InvFamCod IN(SELECT MovParEscInvFamCod FROM MovParEscDetalle md where md.MovParEscSec= me.MovParEscSec) "+
                        "       or "+
                        "       f.InvSubGruCod IN(SELECT MovParEscInvSubGruCod FROM MovParEscDetalle md where md.MovParEscSec= me.MovParEscSec) "+
                        "        or "+
                        "        s.InvGruCod IN(SELECT MovParEscInvGruCod FROM MovParEscDetalle md where md.MovParEscSec= me.MovParEscSec) "+
                        "        or artsec in(select MovParEscartsec from MovParEscArticulos md where md.MovParEscSec= me.MovParEscSec)) "+
                        " and ArtIndMpm<>'S' and artsec not in (select MovParEscartsecNo from MovParEscArticulosnO md where md.MovParEscSec= me.MovParEscSec) "+
                        " union "+
                        " select * from ( "+
                        " select MovParEscSec,MovParEscArtSec,MovParEscDe,MovParEscDeCaj,isnull(MovParEscHasta,0) MovParEscHasta,MovParEscHastaCaj,round(((1-(lis/ori))*100),2) MovParEscDesc1,0 MovParEscDesc2, "+
                        " '' MovParEscRanArtSec,0 MovParEscRanCant,0 MovParEscRanCantCaj,0 MovParEscArtEmb,'' MovParEscInd,isnull(convert(varchar(10),getdate(),120),'') MovParEscfecmod "+
                        " ,0 MovParEscDesc,'' MovParEscResCan,'' MovParEscNoOtor, '' MovParEscViaDir "+
                        " from ( "+
                        " select artcod,ArtNom,ROW_NUMBER() OVER(ORDER BY a.SubPreLisArtSec ASC)+9999 MovParEscSec,a.SubPreLisArtSec MovParEscArtSec "+
                                ",artliscan1 MovParEscDe,0 MovParEscDeCaj,ArtLisCan2 MovParEscHasta, 0 MovParEscHastaCaj , "+
                                " isnull((select preprefijval from PreciosDetalle pd where pd.ArtSec=a.SubPreLIsArtSec and pd.PreArtCod=a.SubPreLisPreArtCod and LisPreCod=a.ArtLisCanLisPre),0) lis,"+
                        "isnull((select preprefijval from PreciosDetalle pd where pd.ArtSec=a.SubPreLIsArtSec and pd.PreArtCod=a.SubPreLisPreArtCod and LisPreCod=1),0) ori "+
                        " from ArtListaCanArtCanLisDetalle a "+
                        " left join articulos ar on ar.artsec=a.SubPreLisArtSec "+
                        " left join ArtListaCan b on a.SubPreLisArtSec=b.SubPreLisArtSec and a.SubPreLisPreArtCod=b.SubPreLisPreArtCod "+
                        " where isnull((select preprefijval from PreciosDetalle pd where pd.ArtSec=a.SubPreLIsArtSec and pd.PreArtCod=a.SubPreLisPreArtCod and LisPreCod=1),0) <>0 "+
                        " ) hh where lis<>ori and ori<>0 and lis<>0 ) kk "+
                        " where MovParEscDesc1>0 "+
                        " )jj order by Row desc ";
                        Log.e("SQLERORRTODO",Script);
                    }else{
                         Script = "select * from(select ROW_NUMBER() OVER(ORDER BY me.MovParEscSec ASC) AS Row,me.MovParEscSec,artsec MovParEscArtSec,MovParEscDe,isnull(MovParEscDeCaj,0) MovParEscDeCaj,isnull(MovParEscHasta,0) MovParEscHasta,isnull(MovParEscHastaCaj,0) MovParEscHastaCaj, " +
                                "isnull(MovParEscDesc1,0) MovParEscDesc1,isnull(MovParEscDesc2,0) MovParEscDesc2,isnull(MovParEscRanArtSec,'') MovParEscRanArtSec,isnull(MovParEscRanCant,0) MovParEscRanCant,isnull(MovParEscRanCantCaj,0) MovParEscRanCantCaj,ArtEmb MovParEscArtEmb,MovParEscInd,isnull(convert(varchar(10),MovParEscfecmod,120),'') MovParEscfecmod,isnull(MovParEscDesc,'') MovParEscDesc,Isnull(MovParEscResCan,'') MovParEscResCan,Isnull(MovParEscNoOtor,'') MovParEscNoOtor,Isnull(MovParEscViaDir,'') MovParEscViaDir FROM MovParEscEscala me  " +
                                "left join  ARTICULOS a on 1=1 " +
                                "left join MovParEsc mc on mc.MovParEscSec=me.MovParEscSec " +
                                "left join InventarioFamilia f on f.InvFamCod=a.InvFamCod " +
                                "left join InventarioSubgrupo s on s.InvSubGruCod=f.InvSubGruCod " +
                                "left join MovParEscUnidadNeg mpe on mpe.MovParEscSec= me.MovParEscSec " +
                                "WHERE  mpe.AliNegCod=" + vAliNegCod + " and  (f.InvFamCod IN(SELECT MovParEscInvFamCod FROM MovParEscDetalle md where md.MovParEscSec= me.MovParEscSec) " +
                               // "WHERE  mc.AliNegCod=" + vAliNegCod + " and  (f.InvFamCod IN(SELECT MovParEscInvFamCod FROM MovParEscDetalle md where md.MovParEscSec= me.MovParEscSec) " +
                                "or " +
                                "f.InvSubGruCod IN(SELECT MovParEscInvSubGruCod FROM MovParEscDetalle md where md.MovParEscSec= me.MovParEscSec) " +
                                "or " +
                                "s.InvGruCod IN(SELECT MovParEscInvGruCod FROM MovParEscDetalle md where md.MovParEscSec= me.MovParEscSec) " +
                                "or artsec in(select MovParEscartsec from MovParEscArticulos md where md.MovParEscSec= me.MovParEscSec)) " +
                                "and ArtIndMpm<>'S' and artsec not in (select MovParEscartsecNo from MovParEscArticulosnO md where md.MovParEscSec= me.MovParEscSec))jj order by Row desc  ";
                    }
                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from MovParEsc");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);
                        try {

                            String InsertScript = "insert into MovParEsc(MovParEscSec,MovParEscArtSec,MovParEscDe,MovParEscDeCaj,MovParEscHasta,MovParEscHastaCaj,MovParEscDesc1,MovParEscDesc2,MovParEscRanArtSec,MovParEscRanCant,MovParEscRanCantCaj,MovParEscArtEmb,MovParEscfecmod,MovParEscDesc,MovParEscResCan,MovParEscNoOtor,MovParEscViaDir,MovParEscInd) values ("
                                    + rsImport.getString("MovParEscSec").trim() + ",'"
                                    + rsImport.getString("MovParEscArtSec").trim() + "',"
                                    + rsImport.getString("MovParEscDe").trim() + ","
                                    + rsImport.getString("MovParEscDeCaj").trim() + ","
                                    + rsImport.getString("MovParEscHasta").trim() + ","
                                    + rsImport.getString("MovParEscHastaCaj").trim() + ","
                                    + rsImport.getString("MovParEscDesc1").trim() + ","
                                    + rsImport.getString("MovParEscDesc2").trim() + ",'"
                                    + rsImport.getString("MovParEscRanArtSec").trim() + "',"
                                    + rsImport.getString("MovParEscRanCant").trim() + ","
                                    + rsImport.getString("MovParEscRanCantCaj").trim() + ","
                                    + rsImport.getString("MovParEscArtEmb").trim() + ",'"
                                    + rsImport.getString("MovParEscfecmod").trim() + "','"
                                    + rsImport.getString("MovParEscDesc").trim() + "','"
                                    + rsImport.getString("MovParEscResCan").trim() + "','"
                                    + rsImport.getString("MovParEscNoOtor").trim() + "','"
                                    + rsImport.getString("MovParEscViaDir").trim() + "','"
                                    + rsImport.getString("MovParEscInd").trim() + "')";


                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;
                        }catch (Exception ex){
                            Errores+=1;

                            Log.e("Erroex",ex.toString());
                        }

                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                            Log.e("Erroe2x",e.toString());
                        }

                    }
                } catch (Exception e) {
                    Log.e("SQLException",e.toString());
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }




                ErroresGen+=Errores;
                Log.e("error123343344",String.valueOf(ErroresGen));

                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        }).start();



        return Resultado;
    }

    public  int[] CargarTipoDireccion(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntMovTipDir);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkMovTipDir);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrMovTipDir);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_MovTipDir);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="select * from(select ROW_NUMBER() OVER(ORDER BY MovTipDirCod ASC) AS Row,MovTipDirCod,MovTipDirNom from MovTipDiR ) jj order by Row desc";
                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from MovTipDir");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);


                        String InsertScript = "insert into MovTipDir(MovTipDirCod,MovTipDirNom) values ('"
                                + rsImport.getString("MovTipDirCod").trim() + "','"
                                + rsImport.getString("MovTipDirNom").trim() + "')";

                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;

                        }

                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                        }

                    }
                } catch (Exception e) {
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }
                finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }



                ErroresGen+=Errores;
                Log.e("error1sfasfasfsf",String.valueOf(ErroresGen));

                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        }).start();



        return Resultado;
    }

    public String TraerExistencia(String pArtSec){
            String sql = "http://181.51.253.237:8080/MantisWeb20apps/rest/pGetExistenciaPrecioWs";
            String sExistencia="0.0";
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            URL url = null;
            HttpURLConnection conn;

            try {
                url = new URL(sql);

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/json");//; utf-8
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("POST");

                StringBuilder result = new StringBuilder();
             //   result.append(URLEncoder.encode("SDTArtSecWS2", "UTF-8"));
             //   result.append("=");
                result.append("{\"SDTArtSecWS2\":{\"ArtSec\":\""+pArtSec+"\"}}"); //URLEncoder.encode(  , "UTF-8")


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
                    sExistencia=jsonObject.optString("Existencia");
                }
               // sal.setText(mensaje);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return sExistencia;
    }









//Empieza los metodos para ficc -------------------------------------------------------------

    public  int[] CargarBonificacionesProductoFicc(){
        int hh=0;
        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntBonificadosProd);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkBonificadosProd);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrBonificadosProd);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_BonificadosProd);
        vPrBar_Import.setProgress(0);
        //vPrBar_Import.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        //int TotalFilasCiudades=0;
        //int InsertadosCiudades=0;
        //int ErroresCiudades=0;
        //int VueltasCiudades=0;
        final int[] Resultado = new int[]{0, 0, 0};

        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;

                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(),"MantisMovil",null,6); //new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();


                /*BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(),"MantisMovil",null,6); //new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();*/



                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                String vEmpresa=vGlobalVariables.getEmpresa();
                vEmpresa=vEmpresa.toUpperCase();

                Connection conn = null;
                Statement comm = null;
                ResultSet rsClientes  = null;
                try {

                    ConBd conbd = new ConBd();
                    conn = conbd.CargarConexion(getApplicationContext());
                    comm = conn.createStatement();
                    String Script="";
                    vEmpresa=vEmpresa.toUpperCase();


                    Script  =  "SELECT *\n" +
                            "FROM (\n" +
                            "    SELECT\n" +
                            "        ROW_NUMBER() OVER (ORDER BY BonTipo ASC) AS Row,\n" +
                            "        *\n" +
                            "    FROM (\n" +
                            "        SELECT\n" +
                            "            b.ParBonSec AS BonProSec,\n" +
                            "            ArtParBonDetIde AS BonProSecLin, isnull(PARBONAPLCANGEN,'N') PARBONAPLCANGEN ," +
                            "            isnull( PARBONCANGEN,0) PARBONCANGEN, \n" +
                            "            'ESCGRU' AS BonTipo,ParBonDes, \n" +
                            "\n" +
                            "            -- GRUPOS\n" +
                            "            'XX,' AS BonProGrupo,\n" +
                            "\n" +
                            "            -- SUBGRUPOS\n" +
                            "            'XX,' AS SUBGRUPOS,\n" +
                            "\n" +
                            "            -- FAMILIAS\n" +
                            "            'XX,' AS FAMILIAS,\n" +
                            "\n" +
                            "            -- LABORATORIO\n" +
                            "            'XX,' AS LABORATORIO,\n" +
                            "\n" +
                            "            -- CLASE\n" +
                            "            'XX,' AS CLASE,\n" +
                            "\n" +
                            "            -- SECCION\n" +
                            "            'XX,' AS SECCION,\n" +
                            "\n" +
                            "            -- MARCA\n" +
                            "            'XX,' AS MARCA,\n" +
                            "\n" +
                            "            -- LINEA\n" +
                            "            'XX,' AS LINEART,\n" +
                            "\n" +
                            "            -- CATEGORIA\n" +
                            "            'XX,' AS CATEGORIA,\n" +
                            "\n" +
                            "            -- SUBCATEGORIA\n" +
                            "            'XX,' AS SUBCATEGORIA,\n" +
                            "\n" +
                            "            -- CAMPOS FIJOS\n" +
                            "            'XX,' AS CANALES,\n" +
                            "                   REPLACE(\n" +
                            "                ISNULL((\n" +
                            "                    SELECT ', ' + RTRIM(LTRIM(ArtParBonArtSec))\n" +
                            "                    FROM ParametrizacionBonificadosArti t\n" +
                            "                    WHERE t.ParBonSec = bp.ParBonSec\n" +
                            "                    FOR XML PATH('')\n" +
                            "                ), 'XX'),\n" +
                            "                ' ',\n" +
                            "                ''\n" +
                            "            ) + ',' AS ARTICULOS, \n" +
                            "            'XX,' AS SUBCANALES,\n" +
                            "            'XX,' AS TAMANOS,\n" +
                            "            'XX,' AS SUCURSALES,\n" +
                            "            'XX,' AS VENDEDORES,\n" +
                            "            'XX,' AS UNIDADES,\n" +
                            "\n" +
                            "            -- TIPO CLIENTE\n" +
                            "            REPLACE(\n" +
                            "                ISNULL((\n" +
                            "                    SELECT ', ' + RTRIM(LTRIM(TipParBonTipCliCod))\n" +
                            "                    FROM ParametrizacionBonificadosTipo t\n" +
                            "                    WHERE t.ParBonSec = bp.ParBonSec\n" +
                            "                      AND (ParBonExcTipCli <> 'S' OR ParBonExcTipCli IS NULL)\n" +
                            "                    FOR XML PATH('')\n" +
                            "                ), 'XX'),\n" +
                            "                ' ',\n" +
                            "                ''\n" +
                            "            ) + ',' AS TIPOCLIENTE,\n" +
                            "\n" +
                            "            -- PERFIL\n" +
                            "            REPLACE(\n" +
                            "                ISNULL((\n" +
                            "                    SELECT ', ' + RTRIM(LTRIM(PerParBonPerCliCod))\n" +
                            "                    FROM ParametrizacionBonificadosPerf p\n" +
                            "                    WHERE p.ParBonSec = bp.ParBonSec\n" +
                            "                      AND (ParBonExcPerCli <> 'S' OR ParBonExcPerCli IS NULL)\n" +
                            "                    FOR XML PATH('')\n" +
                            "                ), 'XX'),\n" +
                            "                ' ',\n" +
                            "                ''\n" +
                            "            ) + ',' AS PERFIL,\n" +
                            "\n" +
                            "            -- CLIENTE\n" +
                            "            REPLACE(\n" +
                            "                ISNULL((\n" +
                            "                    SELECT ', ' +\n" +
                            "                           RTRIM(LTRIM(\n" +
                            "                               CAST(CliParBonNitSec AS VARCHAR(50)) + '-' +\n" +
                            "                               CAST(CliParBonCliSec AS VARCHAR(50))\n" +
                            "                           ))\n" +
                            "                    FROM ParametrizacionBonificadosClie c\n" +
                            "                    WHERE c.ParBonSec = bp.ParBonSec\n" +
                            "                      AND (ParBonExcCli <> 'S' OR ParBonExcCli IS NULL)\n" +
                            "                    FOR XML PATH('')\n" +
                            "                ), 'XX'),\n" +
                            "                ' ',\n" +
                            "                ''\n" +
                            "            ) + ',' AS CLIENTE,\n" +
                            "\n" +
                            "            -- EX TIPO CLIENTE\n" +
                            "            REPLACE(\n" +
                            "                ISNULL((\n" +
                            "                    SELECT ', ' + RTRIM(LTRIM(TipParBonTipCliCod))\n" +
                            "                    FROM ParametrizacionBonificadosTipo t\n" +
                            "                    WHERE t.ParBonSec = bp.ParBonSec\n" +
                            "                      AND ParBonExcTipCli = 'S'\n" +
                            "                    FOR XML PATH('')\n" +
                            "                ), 'XX'),\n" +
                            "                ' ',\n" +
                            "                ''\n" +
                            "            ) + ',' AS EXTIPOCLIENTE,\n" +
                            "\n" +
                            "            -- EX PERFIL\n" +
                            "            REPLACE(\n" +
                            "                ISNULL((\n" +
                            "                    SELECT ', ' + RTRIM(LTRIM(PerParBonPerCliCod))\n" +
                            "                    FROM ParametrizacionBonificadosPerf p\n" +
                            "                    WHERE p.ParBonSec = bp.ParBonSec\n" +
                            "                      AND ParBonExcPerCli = 'S'\n" +
                            "                    FOR XML PATH('')\n" +
                            "                ), 'XX'),\n" +
                            "                ' ',\n" +
                            "                ''\n" +
                            "            ) + ',' AS EXPERFIL,\n" +
                            "\n" +
                            "            -- EX CLIENTE\n" +
                            "            REPLACE(\n" +
                            "                ISNULL((\n" +
                            "                    SELECT ', ' +\n" +
                            "                           RTRIM(LTRIM(\n" +
                            "                               CAST(CliParBonNitSec AS VARCHAR(50)) + '-' +\n" +
                            "                               CAST(CliParBonCliSec AS VARCHAR(50))\n" +
                            "                           ))\n" +
                            "                    FROM ParametrizacionBonificadosClie c\n" +
                            "                    WHERE c.ParBonSec = bp.ParBonSec\n" +
                            "                      AND ParBonExcCli = 'S'\n" +
                            "                    FOR XML PATH('')\n" +
                            "                ), 'XX'),\n" +
                            "                ' ',\n" +
                            "                ''\n" +
                            "            ) + ',' AS EXCLIENTE\n" +
                            "\n" +
                            "        FROM ParametrizacionBonificadosArti bp\n" +
                            "        LEFT JOIN ParametrizacionBonificados b\n" +
                            "               ON b.ParBonSec = bp.ParBonSec\n" +
                            "        WHERE CONVERT(DATE,ParBonFecIni) <= CONVERT(DATE, GETDATE())\n" +
                            "          AND CONVERT(DATE,ParBonFecFin) >= CONVERT(DATE, GETDATE())\n" +
                            "    ) kk\n" +
                            ") Consulta\n" +
                            "ORDER BY Row DESC;";



                            /*"SELECT *\n" +
                            "FROM (\n" +
                            "    SELECT \n" +
                            "        ROW_NUMBER() OVER (ORDER BY BonTipo ASC) AS Row,\n" +
                            "        *\n" +
                            "    FROM (\n" +
                            "        SELECT  \n" +
                            "            bp.DesoBonSec,\n" +
                            "            DesoBonEscSec AS BonProSecLin,\n" +
                            "            'ESCGRU' AS BonTipo,\n" +
                            "            -- Grupos\n" +
                            "            REPLACE(\n" +
                            "                ISNULL((\n" +
                            "                    SELECT ', ' + RTRIM(LTRIM(CAST(DesoBonGruCod AS VARCHAR)))\n" +
                            "                    FROM DescuentosoBonificadosGrupo TABLA\n" +
                            "                    WHERE TABLA.DesoBonSec = bp.DesoBonSec\n" +
                            "                    FOR XML PATH('')\n" +
                            "                ), 'XX'), ' ', ''\n" +
                            "            ) + ',' AS BonProGrupo,\n" +
                            "\n" +
                            "            -- SUBGRUPOS\n" +
                            "         REPLACE(\n" +
                            "                ISNULL((\n" +
                            "                    SELECT ', ' + RTRIM(LTRIM(CAST(DesoBonSubGruCod AS VARCHAR)))\n" +
                            "                    FROM DescuentosoBonificadosGrupo TABLA\n" +
                            "                    WHERE TABLA.DesoBonSec = bp.DesoBonSec\n" +
                            "                    FOR XML PATH('')\n" +
                            "                ), 'XX'), ' ', ''\n" +
                            "            ) + ',' AS SUBGRUPOS,\n" +
                            "\n" +
                            "            -- FAMILIAS\n" +
                            "        REPLACE(\n" +
                            "                ISNULL((\n" +
                            "                    SELECT ', ' + RTRIM(LTRIM(CAST(DesoBonFamCod AS VARCHAR)))\n" +
                            "                    FROM DescuentosoBonificadosGrupo TABLA\n" +
                            "                    WHERE TABLA.DesoBonSec = bp.DesoBonSec\n" +
                            "                    FOR XML PATH('')\n" +
                            "                ), 'XX'), ' ', ''\n" +
                            "            ) + ',' AS FAMILIAS,\n" +
                            "\n" +
                            "\t\t\tREPLACE(\n" +
                            "                ISNULL((\n" +
                            "                    SELECT ', ' + RTRIM(LTRIM(CAST(DesoBonLabCod AS VARCHAR)))\n" +
                            "                    FROM DescuentosoBonificadosLaborato TABLA\n" +
                            "                    WHERE TABLA.DesoBonSec = bp.DesoBonSec\n" +
                            "                    FOR XML PATH('')\n" +
                            "                ), 'XX'), ' ', ''\n" +
                            "            ) + ',' AS LABORATORIO,\n" +
                            "\n" +
                            "\t\t\tREPLACE(\n" +
                            "                ISNULL((\n" +
                            "                    SELECT ', ' + RTRIM(LTRIM(CAST(DesoBonClaCod AS VARCHAR)))\n" +
                            "                    FROM DescuentosoBonificadosClase TABLA\n" +
                            "                    WHERE TABLA.DesoBonSec = bp.DesoBonSec\n" +
                            "                    FOR XML PATH('')\n" +
                            "                ), 'XX'), ' ', ''\n" +
                            "            ) + ',' AS CLASE,\n" +
                            "\n" +
                            "\t\t\t\tREPLACE(\n" +
                            "                ISNULL((\n" +
                            "                    SELECT ', ' + RTRIM(LTRIM(CAST(DesoBonSecCod AS VARCHAR)))\n" +
                            "                    FROM DescuentosoBonificadosSeccion TABLA\n" +
                            "                    WHERE TABLA.DesoBonSec = bp.DesoBonSec\n" +
                            "                    FOR XML PATH('')\n" +
                            "                ), 'XX'), ' ', ''\n" +
                            "            ) + ',' AS SECCION,\n" +
                            "\n" +
                            "\t\t\tREPLACE(\n" +
                            "                ISNULL((\n" +
                            "                    SELECT ', ' + RTRIM(LTRIM(CAST(DesoBonMarCod AS VARCHAR)))\n" +
                            "                    FROM DescuentosoBonificadosmarca TABLA\n" +
                            "                    WHERE TABLA.DesoBonSec = bp.DesoBonSec\n" +
                            "                    FOR XML PATH('')\n" +
                            "                ), 'XX'), ' ', ''\n" +
                            "            ) + ',' AS MARCA,\n" +
                            "\n" +
                            "\t\t\t\tREPLACE(\n" +
                            "                ISNULL((\n" +
                            "                    SELECT ', ' + RTRIM(LTRIM(CAST(DESOBONLinCod AS VARCHAR)))\n" +
                            "                    FROM DescuentosoBonificadosLinea TABLA\n" +
                            "                    WHERE TABLA.DesoBonSec = bp.DesoBonSec\n" +
                            "                    FOR XML PATH('')\n" +
                            "                ), 'XX'), ' ', ''\n" +
                            "            ) + ',' AS LINEART,\n" +
                            "\n" +
                            "\t\t\t\t\tREPLACE(\n" +
                            "                ISNULL((\n" +
                            "                    SELECT ', ' + RTRIM(LTRIM(CAST(DesoBonCatCod AS VARCHAR)))\n" +
                            "                    FROM DescuentosoBonificadosCategori TABLA\n" +
                            "                    WHERE TABLA.DesoBonSec = bp.DesoBonSec\n" +
                            "                    FOR XML PATH('')\n" +
                            "                ), 'XX'), ' ', ''\n" +
                            "            ) + ',' AS CATEGORIA,\n" +
                            "\n" +
                            "\t\t\t\tREPLACE(\n" +
                            "                ISNULL((\n" +
                            "                    SELECT ', ' + RTRIM(LTRIM(CAST(DesoBonSubCatCod AS VARCHAR)))\n" +
                            "                    FROM DescuentosoBonificadosCategori TABLA\n" +
                            "                    WHERE TABLA.DesoBonSec = bp.DesoBonSec\n" +
                            "                    FOR XML PATH('')\n" +
                            "                ), 'XX'), ' ', ''\n" +
                            "            ) + ',' AS SUBCATEGORIA,\n" +
                            "\n" +
                            "            -- CANALES\n" +
                            "        'XX,' AS CANALES,\n" +
                            "         'XX,' AS ARTICULOS,\n" +
                            "\n" +
                            "            -- SUBCANALES\n" +
                            "          'XX,' AS  SUBCANALES,\n" +
                            "\n" +
                            "            -- TAMAÑOS\n" +
                            "           'XX,' AS TAMANOS,\n" +
                            "\n" +
                            "            -- SUCURSALES\n" +
                            "            'XX,' ASSUCURSALES,\n" +
                            "\n" +
                            "            -- VENDEDORES\n" +
                            "           'XX,' AS VENDEDORES,\n" +
                            "\n" +
                            "            -- UNIDADES\n" +
                            "           'XX,' AS UNIDADES\n" +
                            "\n" +
                            "        FROM DescuentosoBonificadosEscala bp\n" +
                            "        LEFT JOIN DescuentosoBonificados b \n" +
                            "            ON b.DesoBonSec = bp.DesoBonSec\n" +
                            "            WHERE  DesoBontipo = 'BON' and\n" +
                            "            DesoBonFecIni <= CONVERT(date, GETDATE())\n" +
                            "            AND DesoBonFecFin >= CONVERT(date, GETDATE())\n" +
                            "    )  kk\n" +
                            " \n" +
                            ") Consulta\n" +
                            "ORDER BY Row DESC;\n";*/

                    rsClientes = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from BonificacionesProducto");

                    while (rsClientes.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsClientes.getInt("Row"));
                            TotalFilas=rsClientes.getInt("Row");
                            String Filas=rsClientes.getString("Row").trim();
                        }


                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);
                        //String BonProMovPor = rsClientes.getString("BonProMovPor");
                        //if(rsClientes.getString("BonProMovPor").equalsIgnoreCase("")){
                        String BonProMovPor = "TRA";
                        //}


                        String InsertScript = "insert into BonificacionesProducto (BonProSec,BonProSecLin,bontipo,BonProGrupo,SUBGRUPOS,FAMILIAS,ARTICULOS," +
                                " LABORATORIO,CLASE,SECCION,MARCA,LINEART,CATEGORIA,SUBCATEGORIA,CANALES,SUBCANALES,TAMANOS,SUCURSALES,VENDEDORES,TIPOCLIENTE," +
                                " PERFIL,CLIENTE,EXTIPOCLIENTE,EXPERFIL,EXCLIENTE,UNIDADES,PARBONAPLCANGEN,PARBONCANGEN,ParBonDes) values ("
                                +rsClientes.getInt("BonProSec") + ","
                                +rsClientes.getInt("BonProSecLin") + ",'"
                                +rsClientes.getString("bontipo") + "','"
                                +rsClientes.getString("BonProGrupo") + "','"
                                +rsClientes.getString("SUBGRUPOS") + "','"
                                +rsClientes.getString("FAMILIAS") + "','"
                                +rsClientes.getString("ARTICULOS") + "','"
                                +rsClientes.getString("LABORATORIO") + "','"
                                +rsClientes.getString("CLASE") + "','"
                                +rsClientes.getString("SECCION") + "','"
                                +rsClientes.getString("MARCA") + "','"
                                +rsClientes.getString("LINEART") + "','"
                                +rsClientes.getString("CATEGORIA") + "','"
                                +rsClientes.getString("SUBCATEGORIA") + "','"
                                +rsClientes.getString("CANALES") + "','"
                                +rsClientes.getString("SUBCANALES") + "','"
                                +rsClientes.getString("TAMANOS") + "','"
                                +rsClientes.getString("SUCURSALES") + "','"
                                +rsClientes.getString("VENDEDORES") + "','"
                                +rsClientes.getString("TIPOCLIENTE") + "','"
                                +rsClientes.getString("PERFIL") + "','"
                                +rsClientes.getString("CLIENTE") + "','"
                                +rsClientes.getString("EXTIPOCLIENTE") + "','"
                                +rsClientes.getString("EXPERFIL") + "','"
                                +rsClientes.getString("EXCLIENTE") + "','"
                                +rsClientes.getString("UNIDADES") + "'," +
                                " '"+rsClientes.getString("PARBONAPLCANGEN")+"',"+rsClientes.getInt("PARBONCANGEN")+",'"+rsClientes.getString("ParBonDes")+"' )";


                        try {
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;
                            Log.e("ErroBonificado", ex.toString());

                        }
                        try {

                            // vPrBar_Clientes.setMax(TotalFilas);
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Clientes.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            Log.e("ErroBonificado1", e.toString());

                            int hh=0;
                        }
                    }//while(rsClientes.next());

                    //   while (rsClientes.next()){

                    //   }
                } catch (Exception e) {
                    Log.e("ErroBonificado2", e.toString());
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsClientes != null) rsClientes.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

                ErroresGen+=Errores;
                Log.e("error133",String.valueOf(ErroresGen));

                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //Resultado[0] = new int[]{Insertados,Errores,TotalFilas};
                // int[] Resultado = //CargarDatos();
                CargarBonificacionesProductoDetFicc();
                CargarBonificacionesProductoDetBonFicc();
                CargarDatos();
            }
        }).start();


        return Resultado;
    }

    public int[] CargarArticulosPreFicc(){
        int hh=0;
        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntBonificadosdet);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkBonificadosdet);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrBonificadosdet);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_Bonificadosdet);
        vPrBar_Import.setProgress(0);
        //vPrBar_Import.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        //int TotalFilasCiudades=0;
        //int InsertadosCiudades=0;
        //int ErroresCiudades=0;
        //int VueltasCiudades=0;
        final int[] Resultado = new int[]{0, 0, 0};

        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                String vEmpresa=vGlobalVariables.getEmpresa();
                vEmpresa=vEmpresa.toUpperCase();

                Connection conn = null;
                Statement comm = null;
                ResultSet rsClientes  = null;

                try {

                    ConBd conbd = new ConBd();
                    conn = conbd.CargarConexion(getApplicationContext());
                    comm = conn.createStatement();
                    String Script="";
                    vEmpresa=vEmpresa.toUpperCase();



                    Script = "SELECT *\n" +
                            "FROM (\n" +
                            "    SELECT \n" +
                            "        ROW_NUMBER() OVER (ORDER BY a.ArtSec ASC) AS Row,\n" +
                            "        a.ArtSec,\n" +
                            "        p.PreArtCod,\n" +
                            "        p.PreArtNom,\n" +
                            "        ISNULL(d.preprefijval, 0) AS preprefijval,\n" +
                            "        d.LisPrecod, ISNULL(PreArtFacConVal,0) PreArtFacConVal  \n" +
                            "    FROM articulospresentacion a\n" +
                            "    LEFT JOIN presentacionarticulos p \n" +
                            "        ON a.preartcod = p.preartcod\n" +
                            "    LEFT JOIN preciosdetalle d \n" +
                            "        ON a.artsec = d.artsec \n" +
                            "        AND p.preartcod = d.preartcod\n" +
                            "    WHERE PreArtEst = 'A'\n" +
                            ") AS Consulta\n" +
                            "ORDER BY Row DESC;\n ";


                    rsClientes = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from ArticulosPresentacion");

                    while (rsClientes.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsClientes.getInt("Row"));
                            TotalFilas=rsClientes.getInt("Row");
                            String Filas=rsClientes.getString("Row").trim();
                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);

                        String InsertScript = "insert into ArticulosPresentacion (ArtSec,PreArtCod,PreArtNom,lisprecod, PrePrefijval,PreArtFacConVal)values('"
                                + rsClientes.getString("ArtSec").trim() + "',"
                                + rsClientes.getString("PreArtCod").trim() + ",'"+rsClientes.getString("PreArtNom")+"', "+rsClientes.getString("lisprecod")+","+rsClientes.getString("PrePrefijval")+" , "+rsClientes.getString("PreArtFacConVal")+" )";
                        try {
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;
                            Log.e("ArticulosPresentacion", ex.toString());
                        }
                        try {

                            // vPrBar_Clientes.setMax(TotalFilas);
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Clientes.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                            Log.e("ArticulosPresentacion1", e.toString());
                        }
                    }//while(rsClientes.next());

                    //   while (rsClientes.next()){

                    //   }
                } catch (Exception e) {
                    Log.e("ArticulosPresentacion3", e.toString());
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsClientes != null) rsClientes.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

                ErroresGen+=Errores;
                Log.e("ArticulosPresentacion1",String.valueOf(ErroresGen));

                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //Resultado[0] = new int[]{Insertados,Errores,TotalFilas};
                // int[] Resultado = //CargarDatos();

            }
        }).start();


        return Resultado;
    }



    public int[] CargarArticulosPreFiccant(){
        int hh=0;
        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntBonificadosdet);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkBonificadosdet);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrBonificadosdet);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_Bonificadosdet);
        vPrBar_Import.setProgress(0);
        //vPrBar_Import.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        //int TotalFilasCiudades=0;
        //int InsertadosCiudades=0;
        //int ErroresCiudades=0;
        //int VueltasCiudades=0;
        final int[] Resultado = new int[]{0, 0, 0};

        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                /*BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(),"MantisMovil",null,6); //new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();*/


                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                String vEmpresa=vGlobalVariables.getEmpresa();
                vEmpresa=vEmpresa.toUpperCase();

                Connection conn = null;
                Statement comm = null;
                ResultSet rsClientes  = null;

                try {

                    ConBd conbd = new ConBd();
                    conn = conbd.CargarConexion(getApplicationContext());
                    comm = conn.createStatement();
                    String Script="";
                    vEmpresa=vEmpresa.toUpperCase();



                        Script = "select * from (select ROW_NUMBER() OVER(ORDER BY a.ArtSec ASC) AS Row, ArtSec,p.PreArtCod, p.PreArtNom " +
                                " from articulospresentacion a WITH (NOLOCK) left join presentacionarticulos p WITH (NOLOCK) \n" +
                                "on a.preartcod = p.preartcod  where PreArtEst = 'A' " +
                                " ) Consulta order by Row desc";


                    rsClientes = comm.executeQuery(Script);
                    BdSql.beginTransaction();

                    try{
                        BdSql.execSQL("Delete from ArticulosPresentacion");

                        String sql = "INSERT INTO ArticulosPresentacion (ArtSec,PreArtCod,PreArtNom) VALUES (?,?,?)";
                        SQLiteStatement stmt = BdSql.compileStatement(sql);



                        while (rsClientes.next()){
                            if(TotalFilas==0) {
                                vPrBar_Import.setMax(rsClientes.getInt("Row"));
                                TotalFilas=rsClientes.getInt("Row");
                                String Filas=rsClientes.getString("Row").trim();
                            }
                            Vueltas+=1;
                            vPrBar_Import.setMax(TotalFilas);
                            vPrBar_Import.setProgress(Vueltas);


                            stmt.clearBindings();
                            stmt.bindString(1, rsClientes.getString("ArtSec").trim());
                            stmt.bindString(2, rsClientes.getString("PreArtCod").trim());
                            stmt.bindString(3, rsClientes.getString("PreArtNom"));

                            stmt.executeInsert();
                            Insertados++;

                        /*try {


                            BdSql.execSQL(InsertScript);
                            Insertados+=1;




                        }catch (Exception ex){
                            Errores+=1;
                            Log.e("ArticulosPresentacion", ex.toString());
                        }*/




                            /*try {

                                // vPrBar_Clientes.setMax(TotalFilas);
                                final int finalTotalFilas = TotalFilas;
                                final int finalInsertados = Insertados;
                                final int finalErrores = Errores;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                        vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                        vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                        // vPrBar_Clientes.setProgress(Vueltas);
                                    }
                                });
                            }catch (Exception e){
                                int hh=0;
                                Log.e("ArticulosPresentacion1", e.toString());
                            }*/
                        }//while(rsClientes.next());





                        BdSql.setTransactionSuccessful();


                    } catch (Exception e) {
                        Log.e("ArticulosPresentacion", e.toString());
                        Errores++;
                    }
                    finally {
                      /*  BdSql.endTransaction();
                        BdSql.close();*/
                    }





                    //   while (rsClientes.next()){

                    //   }
                } catch (Exception e) {
                    Log.e("ArticulosPresentacion3", e.toString());
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsClientes != null) rsClientes.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

                ErroresGen+=Errores;
                Log.e("ArticulosPresentacion1",String.valueOf(ErroresGen));

                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //Resultado[0] = new int[]{Insertados,Errores,TotalFilas};
                // int[] Resultado = //CargarDatos();

            }
        }).start();


        return Resultado;
    }


    public int[] CargarBonificacionesProductoDetFicc(){
        int hh=0;
        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntBonificadosdet);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkBonificadosdet);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrBonificadosdet);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_Bonificadosdet);
        vPrBar_Import.setProgress(0);
        //vPrBar_Import.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        //int TotalFilasCiudades=0;
        //int InsertadosCiudades=0;
        //int ErroresCiudades=0;
        //int VueltasCiudades=0;
        final int[] Resultado = new int[]{0, 0, 0};

        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
               BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                /*BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(),"MantisMovil",null,6); //new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();*/

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                String vEmpresa=vGlobalVariables.getEmpresa();
                vEmpresa=vEmpresa.toUpperCase();

                Connection conn = null;
                Statement comm = null;
                ResultSet rsClientes  = null;

                try {

                    ConBd conbd = new ConBd();
                    conn = conbd.CargarConexion(getApplicationContext());
                    comm = conn.createStatement();
                    String Script="";
                    vEmpresa=vEmpresa.toUpperCase();


                    Script  = "SELECT *\n" +
                            "                            FROM (\n" +
                            "                                SELECT\n" +
                            "                                    ROW_NUMBER() OVER (ORDER BY bp.ParBonSec ASC) AS Row,\n" +
                            "                                    bp.ParBonSec as DesoBonSec,\n" +
                            "                                    bpd.ArtParBonArtSec as DesoBonArtSec,\n" +
                            "                                    bpd.ArtParBonUni as DesoBonArtUniReq,\n" +
                            "                                    0 AS BonProDetDesCaj,\n" +
                            "                                    'N' AS BonProDetIndOpc,\n" +
                            "                                    0 AS ArtEmb,\n" +
                            " isnull(PARBONAPLCANGEN,'N') PARBONAPLCANGEN,\n" +
                            " isnull(PARBONCANGEN,0) PARBONCANGEN, ArtParBonPreArtCod  \n" +
                            "                                FROM ParametrizacionBonificadosArti bpd\n" +
                            "                                LEFT JOIN ParametrizacionBonificados bp\n" +
                            "                                    ON bp.ParBonSec = bpd.ParBonSec\n" +
                            "                                LEFT JOIN Articulos a\n" +
                            "                                    ON a.ArtSec = bpd.ArtParBonArtSec\n" +
                            "                                WHERE\n" +
                            "                                        CONVERT(DATE,ParBonFecIni) <= CONVERT(DATE, GETDATE())\n" +
                            "                                        AND CONVERT(DATE,ParBonFecFin)  >= CONVERT(DATE, GETDATE())\n" +
                            "                            ) AS Consulta\n" +
                            "                            ORDER BY Row DESC;";

                    rsClientes = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from BonificacionesProductoDet");

                    while (rsClientes.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsClientes.getInt("Row"));
                            TotalFilas=rsClientes.getInt("Row");
                            String Filas=rsClientes.getString("Row").trim();
                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);

                        String InsertScript = "insert into BonificacionesProductoDet (BonProSec,BonProArtSec,BomProDetDesUni,BonProDetDesCaj,BonProDetEmb,BonProDetIndOpc,PARBONAPLCANGEN,PARBONCANGEN,ArtParBonPreArtCod) values ("
                                +rsClientes.getInt("DesoBonSec") + ",'"
                                +rsClientes.getString("DesoBonArtSec") + "',"
                                +rsClientes.getInt("DesoBonArtUniReq") + ","
                                +rsClientes.getInt("BonProDetDesCaj") + ","
                                +rsClientes.getInt("ArtEmb") + ",'"
                                +rsClientes.getString("BonProDetIndOpc") + "','"+rsClientes.getString("PARBONAPLCANGEN")+"'," +
                                " "+rsClientes.getInt("PARBONCANGEN") +", "+rsClientes.getInt("ArtParBonPreArtCod") +")";
                        try {
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;
                            Log.e("cargarbonificadet", ex.toString());
                        }
                        try {

                            // vPrBar_Clientes.setMax(TotalFilas);
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Clientes.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                            Log.e("cargarbonificadet1", e.toString());
                        }
                    }//while(rsClientes.next());

                    //   while (rsClientes.next()){

                    //   }
                } catch (Exception e) {
                    Log.e("cargarbonificadet3", e.toString());
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsClientes != null) rsClientes.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

                ErroresGen+=Errores;
                Log.e("errorsds1",String.valueOf(ErroresGen));

                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //Resultado[0] = new int[]{Insertados,Errores,TotalFilas};
                // int[] Resultado = //CargarDatos();

            }
        }).start();


        return Resultado;
    }


    public int[] CargarBonificacionesProductoDetBonFicc(){
        int hh=0;
        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntBonificadosdet);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkBonificadosdet);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrBonificadosdet);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_Bonificadosdet);
        vPrBar_Import.setProgress(0);
        //vPrBar_Import.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        //int TotalFilasCiudades=0;
        //int InsertadosCiudades=0;
        //int ErroresCiudades=0;
        //int VueltasCiudades=0;
        final int[] Resultado = new int[]{0, 0, 0};

        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;

              BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                /*BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(),"MantisMovil",null,6); //new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();*/



                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                String vEmpresa=vGlobalVariables.getEmpresa();
                vEmpresa=vEmpresa.toUpperCase();

                Connection conn = null;
                Statement comm = null;
                ResultSet rsClientes  = null;

                try {

                    ConBd conbd = new ConBd();
                    conn = conbd.CargarConexion(getApplicationContext());
                    comm = conn.createStatement();
                    String Script="";
                    vEmpresa=vEmpresa.toUpperCase();


                    Script  = "SELECT *\n" +
                            "FROM (\n" +
                            "    SELECT\n" +
                            "        ROW_NUMBER() OVER (ORDER BY bpd.ParBonSec ASC) AS Row,\n" +
                            "        bpd.ParBonSec AS DesoBonSec,\n" +
                            "        bpd.BonParBonDetIde AS DesoBonEscSec,\n" +
                            "         bpd.BonParBonArtSec as DesoBonEscArtBonif,\n" +
                            "\n" +
                            "        CASE \n" +
                            "            WHEN ISNULL(bp.PARBONAPLCANGEN, 'N') = 'S' \n" +
                            "                THEN ISNULL(bp.PARBONCANGENBON, 0)\n" +
                            "            ELSE bpd.BonParBonUni\n" +
                            "        END AS DesoBonEscBonif,\n" +
                            "\n" +
                            "        ISNULL(\n" +
                            "            bpd.BonParBonPreArtCod,\n" +
                            "            ap.PreArtcod\n" +
                            "        ) AS BonParBonPreArtCod\n" +
                            "\n" +
                            "    FROM ParametrizacionBonificadosBoni bpd\n" +
                            "    LEFT JOIN ParametrizacionBonificados bp\n" +
                            "        ON bp.ParBonSec = bpd.ParBonSec\n" +
                            "    LEFT JOIN articulospresentacion ap\n" +
                            "        ON ap.artsec = bpd.BonParBonArtSec\n" +
                            "        AND ap.PreArtFacCon = 'S'\n" +
                            "\n" +
                            "    WHERE\n" +
                            "        CONVERT(DATE,bp.ParBonFecIni) <= CONVERT(DATE, GETDATE())  \n" +
                            "        AND CONVERT(DATE,bp.ParBonFecFin) >= CONVERT(DATE, GETDATE()) \n" +


                            "\n" +
                            ") AS Consulta\n" +
                            "ORDER BY Row DESC;";/*"SELECT *\n" +
                            "FROM (\n" +
                            "    SELECT\n" +
                            "        ROW_NUMBER() OVER (ORDER BY bpd.ParBonSec ASC) AS Row,\n" +
                            "        bpd.ParBonSec as DesoBonSec,\n" +
                            "        bpd.BonParBonDetIde as DesoBonEscSec,\n" +
                            "        bpd.BonParBonArtSec as DesoBonEscArtBonif,\n" +
                            "\t\tbpd.BonParBonUni as DesoBonEscBonif, isnull(BonParBonPreArtCod,( select PreArtcod from articulospresentacion ap \n" +
                            " where ap.artsec = bpd.BonParBonArtSec  and PreArtFacCon = 'S'  )) BonParBonPreArtCod \n" +
                            "    FROM ParametrizacionBonificadosBoni bpd\n" +
                            "    LEFT JOIN ParametrizacionBonificados bp\n" +
                            "        ON bp.ParBonSec = bpd.ParBonSec\n" +
                            "    WHERE\n" +
                            "\n" +
                            "           CONVERT(DATE,ParBonFecIni)  <= CONVERT(DATE, GETDATE())\n" +
                            "        AND CONVERT(DATE,ParBonFecFin)   >= CONVERT(DATE, GETDATE())\n" +
                            ") AS Consulta\n" +
                            "ORDER BY Row DESC;\n";*/

                    rsClientes = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from BonificacionesProductoDetBon");

                    while (rsClientes.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsClientes.getInt("Row"));
                            TotalFilas=rsClientes.getInt("Row");
                            String Filas=rsClientes.getString("Row").trim();
                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);

                        String InsertScript = "insert into BonificacionesProductoDetBon (DesoBonSec,DesoBonEscSec,DesoBonEscArtBonif,DesoBonEscBonif,BonParBonPreArtCod) values ("
                                +rsClientes.getInt("DesoBonSec") + ","
                                +rsClientes.getInt("Row") + ",'"
                                +rsClientes.getString("DesoBonEscArtBonif") + "',"
                                +rsClientes.getInt("DesoBonEscBonif") + ", "+rsClientes.getInt("BonParBonPreArtCod") +")";
                        try {
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;
                            Log.e("cargarbonificadetfic1", ex.toString());
                        }
                        try {

                            // vPrBar_Clientes.setMax(TotalFilas);
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Clientes.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                            Log.e("cargarbonificadetfic3", e.toString());
                        }
                    }//while(rsClientes.next());

                    //   while (rsClientes.next()){

                    //   }
                } catch (Exception e) {
                    Log.e("cargarbonificadetfic32", e.toString());
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsClientes != null) rsClientes.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

                ErroresGen+=Errores;
                Log.e("errorsds1",String.valueOf(ErroresGen));

                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //Resultado[0] = new int[]{Insertados,Errores,TotalFilas};
                // int[] Resultado = //CargarDatos();

            }
        }).start();


        return Resultado;
    }

    public  int[] cargarclientesFicc(){
    int hh=0;
    final ProgressBar vPrBar_Import;
    final TextView vtxt_CntImport;
    final TextView vtxt_OkImport;
    final TextView vtxt_ErrImport;
    vtxt_CntImport= (TextView)findViewById(R.id.txt_CntClientes);
    vtxt_OkImport= (TextView)findViewById(R.id.txt_OkClientes);
    vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrClientes);
    vPrBar_Import= (ProgressBar)findViewById(R.id.prBar_Clientes);
    vPrBar_Import.setProgress(0);
    //vPrBar_Import.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
    //int TotalFilasCiudades=0;
    //int InsertadosCiudades=0;
    //int ErroresCiudades=0;
    //int VueltasCiudades=0;
    final int[] Resultado = new int[]{0, 0, 0};

        Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            int TotalFilas=0;
            int Insertados=0;
            int Errores=0;
            int Vueltas=0;
           /* BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
            SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();*/

            BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(),"MantisMovil",null,6); //new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
            SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();


            GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
            String vUsuario=vGlobalVariables.getUsuario();
            //    '" + vUsuario + "'
            String vEmpresa=vGlobalVariables.getEmpresa();
            vEmpresa=vEmpresa.toUpperCase();
        int cantpros = 0;
            Cursor clinpro = vBaseDeDatos.getReadableDatabase().rawQuery("select nit from prospecto",null);
            String coleccion = "";
             clinpro.moveToFirst();
             if(clinpro.getCount()  > 0){
                 clinpro.moveToFirst();
                 coleccion = "(";
                 do{
                     cantpros +=1;
                     coleccion += "'"+clinpro.getString(0)+"',";
                 }while (clinpro.moveToNext());
                 coleccion += ")";
                 coleccion = coleccion.replace(",)",")");
             }






            Connection conn = null;
            Statement comm = null;
            ResultSet rsClientes  = null;
            try {

                ConBd conbd = new ConBd();
                classbd classbd = new classbd();
                conn = conbd.CargarConexion(getApplicationContext());
                comm = conn.createStatement();
                String Script="";
                vEmpresa=vEmpresa.toUpperCase();
            if(vEmpresa.toUpperCase().contains("LIMENA")){

                /*Script = "SELECT * FROM (\n" +
                        "    SELECT ROW_NUMBER() OVER(ORDER BY Cv.NitSec ASC) AS Roww, \n" +
                        "           0 CliTamCan, \n" +
                        "           C.NitSec, \n" +
                        "           C.CliSec, \n" +
                        "           ISNULL(NitCom,'') NitCom, \n" +
                        "           ISNULL(CliNom,'') CliNom, \n" +
                        "           ISNULL(NitIde,'') NitIde,\n" +
                        "           ISNULL(CiuCod,'') CiuCod, \n" +
                        "           ISNULL(CiuNom,'') + '(' + ISNULL(LisPreNom,'') + ')' CiuNom, \n" +
                        "           ISNULL(B.BarCod, 0) BarCod, \n" +
                        "           ISNULL(B.BarNom,'') BarNom, \n" +
                        "           ISNULL(RTRIM(LTRIM(B.BarNom)),'') + ' ' + CliDir CliDir, \n" +
                        "           ISNULL(C.LisPreCod, 1) LisPreCod, \n" +
                        "           1 LisPreCodLim, \n" +
                        "           ISNULL(CliTel,'') CliTel, \n" +
                        "           ISNULL(CLICONPLA,0)  CLICONPAG, \n" +
                        "           ISNULL(CliVenlun,'N')  CliIntLun, \n" +
                        "           ISNULL(CliVenmar,'N') CliIntMar, \n" +
                        "           ISNULL(CliVenmie,'N') CliIntMie, \n" +
                        "           ISNULL(CliVenju,'N') CliIntJue, \n" +
                        "           ISNULL(CliVenvie,'N') CliIntVie, \n" +
                        "           ISNULL(CliVenSab,'N') CliIntSab, \n" +
                        "           ISNULL(CliVenDom,'N') CliIntDom, \n" +
                        "           ISNULL(C.PerCliCod, 1) PerCliCod,\n" +
                        "           ISNULL(Cnsc.CanCod, 1) CanCod, \n" +
                        "           ISNULL(Cn.CanNom,'') CanNom, \n" +
                        "           ISNULL(C.CanSubCod, 1) CanSubCod, \n" +
                        "           ISNULL(CanSubNom,'') CanSubNom, \n" +
                        "           ISNULL(CliCup, 0)  CliCup, \n" +
                        "            0 CliPorAdi, \n" +
                        "           0 CliVenCup,\n" +
                        "           0 CliDiasUltVen,\n" +
                        "           ISNULL(CliVenVisFre,'')  CliIntFre, \n" +
                        "           ISNULL(CliVenVisFre,'') FreNom, \n" +
                        "           0 CliIntTiempo, \n" +
                        "           0 CliIntOrdDet, \n" +
                        "           0 InaCod, \n" +
                        "          CASE CliExtFacIva When 'N' Then 'S' When 'S' Then 'N'else 'S' end CliSinIva, \n" +
                        "           'N' CliNoReen, isnull(tc.TipCliNom,'')TipCliNom ,isnull(p.PerCliNom,'') PerCliNom, '' ZonNom, isnull(rt.RutDes,'') RutDes, isnull(cat.CatCliNom,'') CatCliNom" +
                        "    FROM Clientes C  \n" +
                        "    LEFT JOIN ClientesVendedores Cv ON Cv.NitSec = C.NitSec AND Cv.CliSec = C.CliSec\n" +
                        "    LEFT JOIN Nit N ON N.NitSec = C.NitSec\n" +
                        "    LEFT JOIN ListaPrecios Ll ON Ll.LisPreCod = C.LisPreCod\n" +
                        "    LEFT JOIN Ciudades ON CliCiuCod = CiuCod\n" +
                        "    LEFT JOIN SubCanal Cnsc ON Cnsc.CanSubCod = C.CanSubCod\n" +
                        "    LEFT JOIN Canales Cn ON Cn.CanCod = Cnsc.CanCod\n" +
                        "    LEFT JOIN Barrio B ON B.BarCod = C.BarCod\n" +
                        "    LEFT JOIN TipodeClientes tc ON tc.TipCliCod = C.TipCliCod\n" +
                        "    LEFT JOIN PerfilDeClientes p ON p.PerCliCod = C.PerCliCod\n" +
                        //"    LEFT JOIN Zona z ON z.ZonCod = 100\n" +
                        "    LEFT JOIN Ruta rt ON rt.RutCod = C.RutCod " +
                        "    LEFT JOIN Vendedores  v on v.VenCod = '"+vUsuario+"' \n" +
                        "    LEFT JOIN CategoriaCliente cat ON cat.CatCliCod = C.CatCliCod\n" +
                        "    WHERE ( v.VenModoSup = 'S' or Cv.VenCod = '" + vUsuario + "' ) " +
                        ") Consulta \n" +
                        "ORDER BY Roww DESC; ";*/

                Script = "SELECT *, " +
                        "ROW_NUMBER() OVER(ORDER BY NitSec ASC, CliSec ASC) AS Roww " +
                        "FROM ( " +

                        "    SELECT * FROM ( " +

                        "        SELECT " +
                        "               0 CliTamCan, " +
                        "               C.NitSec, " +
                        "               C.CliSec, " +
                        "               ISNULL(NitCom,'') NitCom, " +
                        "               ISNULL(CliNom,'') CliNom, " +
                        "               ISNULL(NitIde,'') NitIde, " +
                        "               ISNULL(CiuCod,'') CiuCod, " +
                        "               ISNULL(CiuNom,'') + '(' + ISNULL(LisPreNom,'') + ')' CiuNom, " +
                        "               ISNULL(B.BarCod, 0) BarCod, " +
                        "               ISNULL(B.BarNom,'') BarNom, " +
                        "               ISNULL(RTRIM(LTRIM(B.BarNom)),'') + ' ' + CliDir CliDir, " +
                        "               ISNULL(C.LisPreCod, 1) LisPreCod, " +
                        "               1 LisPreCodLim, " +
                        "               ISNULL(CliTel,'') CliTel, " +
                        "               ISNULL(CLICONPLA,0) CLICONPAG, " +
                        "               ISNULL(CliVenlun,'N') CliIntLun, " +
                        "               ISNULL(CliVenmar,'N') CliIntMar, " +
                        "               ISNULL(CliVenmie,'N') CliIntMie, " +
                        "               ISNULL(CliVenju,'N') CliIntJue, " +
                        "               ISNULL(CliVenvie,'N') CliIntVie, " +
                        "               ISNULL(CliVenSab,'N') CliIntSab, " +
                        "               ISNULL(CliVenDom,'N') CliIntDom, " +
                        "               ISNULL(C.PerCliCod, 1) PerCliCod, " +
                        "               ISNULL(Cnsc.CanCod, 1) CanCod, " +
                        "               ISNULL(Cn.CanNom,'') CanNom, " +
                        "               ISNULL(C.CanSubCod, 1) CanSubCod, " +
                        "               ISNULL(CanSubNom,'') CanSubNom, " +
                        "               ISNULL(CliCup, 0) CliCup, " +
                        "               0 CliPorAdi, " +
                        "               0 CliVenCup, " +
                        "               0 CliDiasUltVen, " +
                        "               ISNULL(CliVenVisFre,'') CliIntFre, " +
                        "               ISNULL(CliVenVisFre,'') FreNom, " +
                        "               0 CliIntTiempo, " +
                        "               0 CliIntOrdDet, " +
                        "               0 InaCod, " +
                        "               CASE CliExtFacIva " +
                        "                   WHEN 'N' THEN 'S' " +
                        "                   WHEN 'S' THEN 'N' " +
                        "                   ELSE 'S' " +
                        "               END CliSinIva, " +
                        "               'N' CliNoReen, " +
                        "               ISNULL(tc.TipCliNom,'') TipCliNom, " +
                        "               ISNULL(p.PerCliNom,'') PerCliNom, " +
                        "               '' ZonNom, " +
                        "               ISNULL(rt.RutDes,'') RutDes, " +
                        "               ISNULL(cat.CatCliNom,'') CatCliNom, ISNULL( CliBloCup,'N') CliBloCup , " +

                        // Identifica duplicados por NitSec + CliSec
                        "               ROW_NUMBER() OVER( " +
                        "                   PARTITION BY C.NitSec, C.CliSec " +
                        "                   ORDER BY Cv.VenCod " +
                        "               ) AS rn  " +

                        "        FROM Clientes C " +
                        "        LEFT JOIN ClientesVendedores Cv " +
                        "            ON Cv.NitSec = C.NitSec " +
                        "           AND Cv.CliSec = C.CliSec " +

                        "        LEFT JOIN Nit N " +
                        "            ON N.NitSec = C.NitSec " +

                        "        LEFT JOIN ListaPrecios Ll " +
                        "            ON Ll.LisPreCod = C.LisPreCod " +

                        "        LEFT JOIN Ciudades " +
                        "            ON CliCiuCod = CiuCod " +

                        "        LEFT JOIN SubCanal Cnsc " +
                        "            ON Cnsc.CanSubCod = C.CanSubCod " +

                        "        LEFT JOIN Canales Cn " +
                        "            ON Cn.CanCod = Cnsc.CanCod " +

                        "        LEFT JOIN Barrio B " +
                        "            ON B.BarCod = C.BarCod " +

                        "        LEFT JOIN TipodeClientes tc " +
                        "            ON tc.TipCliCod = C.TipCliCod " +

                        "        LEFT JOIN PerfilDeClientes p " +
                        "            ON p.PerCliCod = C.PerCliCod " +

                        "        LEFT JOIN Ruta rt " +
                        "            ON rt.RutCod = C.RutCod " +

                        "        LEFT JOIN Vendedores v " +
                        "            ON v.VenCod = '"+vUsuario+"' " +

                        "        LEFT JOIN CategoriaCliente cat " +
                        "            ON cat.CatCliCod = C.CatCliCod " +

                        "        WHERE ( " +
                        "               v.VenModoSup = 'S' " +
                        "               OR Cv.VenCod = '" + vUsuario + "' " +
                        "        ) and CliEst = 'A'  " +

                        "    ) ConsultaDuplicados " +

                        "    WHERE rn = 1 " +

                        ") ConsultaFinal " +

                        "ORDER BY Roww DESC;";


            }else{

                Script = "SELECT * FROM (\n" +
                        "    SELECT ROW_NUMBER() OVER(ORDER BY Cv.NitSec ASC) AS Roww, \n" +
                        "           0 CliTamCan, \n" +
                        "           C.NitSec, \n" +
                        "           C.CliSec, \n" +
                        "           ISNULL(NitCom,'') NitCom, \n" +
                        "           ISNULL(CliNom,'') CliNom, \n" +
                        "           ISNULL(NitIde,'') NitIde,\n" +
                        "           ISNULL(CiuCod,'') CiuCod, \n" +
                        "           ISNULL(CiuNom,'') + '(' + ISNULL(LisPreNom,'') + ')' CiuNom, \n" +
                        "           ISNULL(B.BarCod, 0) BarCod, \n" +
                        "           ISNULL(B.BarNom,'') BarNom, \n" +
                        "           ISNULL(RTRIM(LTRIM(B.BarNom)),'') + ' ' + CliDir CliDir, \n" +
                        "           ISNULL(C.LisPreCod, 1) LisPreCod, \n" +
                        "           1 LisPreCodLim, \n" +
                        "           ISNULL(CliTel,'') CliTel, \n" +
                        "           ISNULL(CLICONPLA,0)  CLICONPAG, \n" +
                        "           ISNULL(CliVenlun,'N')  CliIntLun, \n" +
                        "           ISNULL(CliVenmar,'N') CliIntMar, \n" +
                        "           ISNULL(CliVenmie,'N') CliIntMie, \n" +
                        "           ISNULL(CliVenju,'N') CliIntJue, \n" +
                        "           ISNULL(CliVenvie,'N') CliIntVie, \n" +
                        "           ISNULL(CliVenSab,'N') CliIntSab, \n" +
                        "           ISNULL(CliVenDom,'N') CliIntDom, \n" +
                        "           ISNULL(C.PerCliCod, 1) PerCliCod,\n" +
                        "           ISNULL(Cnsc.CanCod, 1) CanCod, \n" +
                        "           ISNULL(Cn.CanNom,'') CanNom, \n" +
                        "           ISNULL(C.CanSubCod, 1) CanSubCod, \n" +
                        "           ISNULL(CanSubNom,'') CanSubNom, \n" +
                        "           ISNULL(CliCup, 0) * (1 + (ISNULL(CliPorAdi, 0) / 100)) CliCup, \n" +
                        "           ISNULL(CliPorAdi, 0) CliPorAdi, \n" +
                        "           0 CliVenCup,\n" +
                        "           0 CliDiasUltVen,\n" +
                        "           ISNULL(CliVenVisFre,'')  CliIntFre, \n" +
                        "           ISNULL(CliVenVisFre,'') FreNom, \n" +
                        "           0 CliIntTiempo, \n" +
                        "           0 CliIntOrdDet, \n" +
                        "           0 InaCod, \n" +
                        "          CASE CliExtFacIva When 'N' Then 'S' When 'S' Then 'N'else 'S' end CliSinIva, \n" +
                        "           'N' CliNoReen, isnull(tc.TipCliNom,'')TipCliNom ,isnull(p.PerCliNom,'') PerCliNom, isNull(z.ZonNom,'') ZonNom, isnull(rt.RutDes,'') RutDes, isnull(cat.CatCliNom,'') CatCliNom" +
                        "    FROM ClientesVendedores Cv \n" +
                        "    LEFT JOIN Clientes C ON Cv.NitSec = C.NitSec AND Cv.CliSec = C.CliSec\n" +
                        "    LEFT JOIN Nit N ON N.NitSec = C.NitSec\n" +
                        "    LEFT JOIN ListaPrecios Ll ON Ll.LisPreCod = C.LisPreCod\n" +
                        "    LEFT JOIN Ciudades ON CliCiuCod = CiuCod\n" +
                        "    LEFT JOIN SubCanal Cnsc ON Cnsc.CanSubCod = C.CanSubCod\n" +
                        "    LEFT JOIN Canales Cn ON Cn.CanCod = Cnsc.CanCod\n" +
                        "    LEFT JOIN Barrio B ON B.BarCod = C.BarCod\n" +
                        "    LEFT JOIN TipodeClientes tc ON tc.TipCliCod = C.TipCliCod\n" +
                        "    LEFT JOIN PerfilDeClientes p ON p.PerCliCod = C.PerCliCod\n" +
                        "    LEFT JOIN Zona z ON z.ZonCod = C.ZonCod\n" +
                        "    LEFT JOIN Ruta rt ON rt.RutCod = C.RutCod\n" +
                        "    LEFT JOIN CategoriaCliente cat ON cat.CatCliCod = C.CatCliCod\n" +
                        "    WHERE VenCod = '" + vUsuario + "' " +
                        ") Consulta \n" +
                        "ORDER BY Roww DESC; ";
            }


                Log.e("SQLCLIEMTESFICC",Script);

                 rsClientes = comm.executeQuery(classbd.FormatearMysql(Script));
                 if(cantpros > 0)
                 {
                     BdSql.execSQL("Delete from Clientes where nitsec not in "+coleccion);

                 }else{
                     BdSql.execSQL("Delete from Clientes");

                 }

                while (rsClientes.next()){
                    if(TotalFilas==0) {
                        vPrBar_Import.setMax(rsClientes.getInt("Roww"));
                        TotalFilas=rsClientes.getInt("Roww");
                        String Filas=rsClientes.getString("Roww").trim();
                    }
                    Vueltas+=1;
                    vPrBar_Import.setMax(TotalFilas);
                    vPrBar_Import.setProgress(Vueltas);

                    String InsertScript = "insert into clientes (nitsec,"
                            + "clisec,"
                            + "NitCom,"
                            + "CliNom,"
                            + "NitIde,"
                            + "CliDir,"
                            + "CliTel,"
                            + "Lisprecod,"
                            + "Lisprecodlim,"
                            + "CliConPag,CliBloCup,"
                            + "cliintlun,"
                            + "cliintmar,"
                            + "cliintmie,"
                            + "cliintjue,"
                            + "cliintvie,"
                            + "cliintsab,"
                            + "cliintdom,"
                            + "CliIntTiempo,"
                            + "CliIntOrdDet,"
                            + "cliintfre,"
                            + "frenom,"
                            + "PerCliCod,"
                            + "CanCod,"
                            + "CanNom,"
                            + "CiuCod,"
                            + "CiuNom,"
                            + "BarCod,"
                            + "BarNom,"
                            + "CanSubCod,"
                            + "CanSubNom,"
                            + "CliCup,"
                            + "CliPorAdi,"
                            + "CliVenCup,CliTamCan,"
                            + "CliDiasUltVen,InaCod,CliIva,CliNoree,TipoCliente,perfilcliente,zona,ruta,categoria) values ('"
                            + rsClientes.getString("nitsec").trim() + "',"
                            + rsClientes.getString("clisec").trim() + ",'"
                            + rsClientes.getString("NitCom").trim().replace("'","") + "','"
                            + rsClientes.getString("CliNom").trim().replace("'","") + "','"
                            + rsClientes.getString("NitIde").trim().replace("'","") + "','"
                            + rsClientes.getString("CliDir").trim().replace("'","") +  "','"
                            + rsClientes.getString("CliTel").trim().replace("'","")  + "',"
                            + rsClientes.getString("lisprecod").trim() + ","
                            + rsClientes.getString("lisprecodlim").trim() + ","
                            + rsClientes.getString("CLICONPAG").trim() + ",'"
                            + rsClientes.getString("CliBloCup").trim() + "','"
                            +rsClientes.getString("CliIntLun").trim() + "','"
                            +rsClientes.getString("CliIntMar").trim() + "','"
                            +rsClientes.getString("CliIntMie").trim() + "','"
                            +rsClientes.getString("CliIntJue").trim() + "','"
                            +rsClientes.getString("CliIntVie").trim() + "','"
                            +rsClientes.getString("CliIntSab").trim() + "','"
                            +rsClientes.getString("CliIntDom").trim() + "',"
                            +rsClientes.getString("CliIntTiempo").trim() + ","
                            +rsClientes.getString("CliIntOrdDet").trim() + ",'"
                            +rsClientes.getString("cliintfre").trim() + "','"
                            +rsClientes.getString("frenom").trim() + "',"
                            +rsClientes.getString("PerCliCod").trim() + ","
                            +rsClientes.getString("CanCod").trim() + ",'"
                            +rsClientes.getString("CanNom").trim() + "','"
                            +rsClientes.getString("CiuCod").trim() + "','"
                            +rsClientes.getString("CiuNom").trim() + "',"
                            +rsClientes.getString("BarCod").trim() + ",'"
                            +rsClientes.getString("BarNom").trim() + "',"
                            +rsClientes.getString("CanSubCod").trim() + ",'"
                            +rsClientes.getString("CanSubNom").trim() + "',"
                            +rsClientes.getString("CliCup").trim() + ","
                            +rsClientes.getString("CliPorAdi").trim() + ","
                            +rsClientes.getString("CliVenCup").trim() + ","
                            +rsClientes.getString("CliTamCan").trim() + ","
                            +rsClientes.getString("CliDiasUltVen").trim() + ","
                            +rsClientes.getString("InaCod").trim() + ",'"
                            +rsClientes.getString("CliSinIva").trim() + "','"
                            +rsClientes.getString("CliNoReen").trim() + "','"
                            +rsClientes.getString("TipCliNom").trim() + "','"
                            +rsClientes.getString("PerCliNom").trim() + "','"
                            +rsClientes.getString("ZonNom").trim() + "','"
                            +rsClientes.getString("RutDes").trim() + "','"
                            +rsClientes.getString("CatCliNom").trim() + "')" ;
                    try {
                        BdSql.execSQL(InsertScript);
                        Insertados+=1;

                    }catch (Exception ex){
                        Errores+=1;
                        Log.e("Errorclificcex",ex.toString());


                    }
                    try {

                        // vPrBar_Clientes.setMax(TotalFilas);
                        final int finalTotalFilas = TotalFilas;
                        final int finalInsertados = Insertados;
                        final int finalErrores = Errores;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                // vPrBar_Clientes.setProgress(Vueltas);
                            }
                        });
                    }catch (Exception e){
                        int hh=0;
                        Log.e("",e.toString());

                    }
                }//while(rsClientes.next());

                //   while (rsClientes.next()){

                //   }
            } catch (Exception e) {
                Log.e("Errorclif2icce",e.toString());
                Errores=1;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        vtxt_CntImport.setText("N/N");
                        vtxt_OkImport.setText("N/N");
                        vtxt_ErrImport.setText("1");
                    }
                });
            }finally { // Cerramos las conexiones, en orden inverso a su apertura
                try { if (rsClientes != null) rsClientes.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
            }



            Log.e("Cliebntes",String.valueOf(Errores));
            ErroresGen+=Errores;
            // vtxt_ErrClientes.setText(+String.valueOf(Errores));
            //Resultado[0] = new int[]{Insertados,Errores,TotalFilas};
            // int[] Resultado = //CargarDatos();
            CargarDatos();
        }
    });
        thread.start();
        thread.interrupt();

    return Resultado;
}
    public  int[] CargarCiudadesFicc(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntCiudades);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkCiudades);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrCiudades);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_Ciudades);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();
                BdSql.enableWriteAheadLogging();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {








                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="";
                   Script ="SELECT * FROM ( " +
                           " SELECT ROW_NUMBER() OVER(ORDER BY CiuCod ASC) AS Roww, CiuCod, CiuNom \n" +
                           "    FROM Ciudades " +
                           ") JJ " +
                           "ORDER BY Roww DESC;";
                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from ciudades");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Roww"));
                            TotalFilas=rsImport.getInt("Roww");
                            String Filas=rsImport.getString("Roww").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);



                        //vPrBar_Ciudades.

                        String InsertScript = "insert into Ciudades (CiuCod,CiuNom) values ('"
                                + rsImport.getString("CiuCod").trim() + "','"
                                + rsImport.getString("CiuNom").trim() + "')" ;


                       /* try {

                            BdSql.beginTransaction();

                            //db.execSQL("DELETE FROM ciudades");

                            while (rsImport.next()) {

                                BdSql.execSQL(
                                        "INSERT INTO Ciudades (CiuCod, CiuNom) VALUES (?,?)",
                                        new Object[]{
                                                rsImport.getString("CiuCod").trim(),
                                                rsImport.getString("CiuNom").trim()
                                        }
                                );

                                Insertados++;

                            }

                            BdSql.setTransactionSuccessful();
                            vPrBar_Import.setProgress(TotalFilas);

                        } catch (Exception e) {
                            Errores++;
                            Log.e("Error ciudad ",e.toString());

                        } finally {

                            if (BdSql.inTransaction())
                                BdSql.endTransaction();

                            BdSql.close();
                            vBaseDeDatos.close();
                        }
*/


                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;










                        }catch (Exception ex){
                            Errores+=1;

                        }
                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                        }
//*/
                    }
                } catch (Exception e) {
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

                ErroresGen+=Errores;
                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        });
        thread.start();
        thread.interrupt();


        return Resultado;
    }
    public int[] CargarCiudadesFiccant() {

        final ProgressBar vPrBar_Import = findViewById(R.id.PrBar_Ciudades);
        final TextView vtxt_CntImport = findViewById(R.id.txt_CntCiudades);
        final TextView vtxt_OkImport = findViewById(R.id.txt_OkCiudades);
        final TextView vtxt_ErrImport = findViewById(R.id.txt_ErrCiudades);

        vPrBar_Import.setProgress(0);

        int[] Resultado = new int[]{0,0,0};

        new Thread(() -> {

            int TotalFilas = 0;
            int Insertados = 0;
            int Errores = 0;
            int Vueltas = 0;

           BaseDatos helper = new BaseDatos(getApplicationContext(),"MantisMovil",null,6);
            SQLiteDatabase db = helper.getReadableDatabase();

           /*BaseDatos helper =  BaseDatos.getInstance(getApplicationContext()); //new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
            SQLiteDatabase db = helper.getReadableDatabase();*/


            Connection conn = null;
            Statement comm = null;
            ResultSet rs = null;

            try {

                conn = new ConBd().CargarConexion(getApplicationContext());
                comm = conn.createStatement();

                String script =
                        "SELECT ROW_NUMBER() OVER(ORDER BY CiuCod ASC) AS Roww, CiuCod, CiuNom FROM Ciudades";

                rs = comm.executeQuery(script);

                db.beginTransaction();

                db.execSQL("DELETE FROM Ciudades");




                while (rs.next()) {

                    if (TotalFilas == 0) {
                        TotalFilas = rs.getInt("Roww");

                        int finalTotalFilas = TotalFilas;
                        handler.post(() -> vPrBar_Import.setMax(finalTotalFilas));
                    }

                    db.execSQL(
                            "INSERT INTO Ciudades (CiuCod, CiuNom) VALUES (?,?)",
                            new Object[]{
                                    rs.getString("CiuCod").trim(),
                                    rs.getString("CiuNom").trim()
                            }
                    );

                    Insertados++;
                    Vueltas++;

                    int finalVueltas = Vueltas;
                    int finalInsertados = Insertados;
                    int finalErrores = Errores;
                    int finalTotalFilas = TotalFilas;

                    handler.post(() -> {
                        vPrBar_Import.setProgress(finalVueltas);
                        vtxt_CntImport.setText(String.valueOf(finalTotalFilas));
                        vtxt_OkImport.setText(String.valueOf(finalInsertados));
                        vtxt_ErrImport.setText(String.valueOf(finalErrores));
                    });
                }

                db.setTransactionSuccessful();

            } catch (Exception e) {

                Errores++;
                Log.e("CIUDADES", e.toString());

            } finally {

               // if (db.inTransaction()) db.endTransaction();

               // db.close();
                ///helper.close();

                try { if (rs != null) rs.close(); } catch (Exception ignored){}
                try { if (comm != null) comm.close(); } catch (Exception ignored){}
                try { if (conn != null) conn.close(); } catch (Exception ignored){}
            }

            ErroresGen += Errores;
            CargarDatos();

        }).start();

        return Resultado;
    }



    public  int[] CargarBarriosFicc(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntBarrios);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkBarrios);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrBarrios);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_Barrios);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        Thread thread =  new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;

                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql= vBaseDeDatos.getReadableDatabase();

               /* /*BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(),"MantisMovil",null,6); //new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();*/

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();

                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="";
                    conbd.Variables();
                    String mysql = conbd.Mysql;
                    if(mysql.equalsIgnoreCase("S")){
                        Script="select * from(select ROW_NUMBER() OVER(ORDER BY BarCod ASC) AS Roww,BarCod,BarNom, " +
                                "( select IFnull(CONCAT (CONCAT (',', GROUP_CONCAT(BarCiuCod)),','),'XX,') FROM Barrio TABLA WHERE TABLA.BarCod=Barrio.BarCod)AS Ciudades " +
                                "from Barrio ) jj order by Roww desc ";
                    }else{
                        Script="select * from(select ROW_NUMBER() OVER(ORDER BY BarCod ASC) AS Roww,BarCod,BarNom,\n" +
                                "REPLACE(ISNULL((SELECT ', '  + rtrim(ltrim(BarCiuCod )) FROM Barrio TABLA WHERE TABLA.BarCod=Barrio.BarCod FOR XML PATH('')),'XX'),' ','')+',' As Ciudades\n" +
                                "from Barrio ) jj order by Roww desc";
                    }






                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from Barrios");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Roww"));
                            TotalFilas=rsImport.getInt("Roww");
                            String Filas=rsImport.getString("Roww").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);


//*/
                        //vPrBar_Ciudades.

                        String InsertScript = "insert into Barrios (BarCod,BarNom,Ciudades) values ('"
                                + rsImport.getString("BarCod").trim() + "','"
                                + rsImport.getString("BarNom").trim() + "','"
                                + rsImport.getString("Ciudades").trim() + "')" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                                //   Errores+=1;
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;
                            Log.e("error Varrio",ex.toString());

                        }
                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            Log.e("error Varrio w",e.toString());

                            int hh=0;
                        }
                    }
                } catch (Exception e) {
                    Log.e("error Varrio",e.toString());

                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }
                finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }


                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};
                CargarDatos();

            }
        });
        thread.start();
        thread.interrupt();

        return Resultado;
    }
    public  int[] CargarArticulosFicc(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntArticulos);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkArticulos);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrArticulos);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_Articulos);
        vPrBar_Import.setProgress(0);



        int[] Resultado= new int[]{1,3,5};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;

                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

              /*  /*BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(),"MantisMovil",null,6); //new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();*/

                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {
                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                    conbd.Variables();
                    String mysql = conbd.Mysql;
                     comm = conn.createStatement();
                    GlobalVariables vGlobalVariables = GlobalVariables.getInstance();
                    String vUsuario = vGlobalVariables.getUsuario();
                    String PrecioMov = conbd.PrecioMov;
                    String vEmpresa = vGlobalVariables.getEmpresa();
                    vEmpresa = vEmpresa.toUpperCase();
                    String Script = "";

                              if(vEmpresa.toUpperCase().contains("LIMENA") ||vEmpresa.equalsIgnoreCase("DISTRIJN") ){
                                  Script = "select * from (select ROW_NUMBER() OVER(ORDER BY a.ArtSec ASC) AS Roww,a.ArtSec,ArtCod,replace(ArtNom,'''','') ArtNom,'' ArtMedNomCom,isnull(g.InvGruCod,'') InvGruCod ,isnull(g.InvGruNom,'')InvGruNom ," +
                                          "isnull(s.InvSubGruCod,'')InvSubGruCod,isnull(s.InvSubGruNom,'')InvSubGruNom,isnull(a.InvFamCod,'')InvFamCod ,isnull(InvFamNom,'')InvFamNom,isnull(ParConIva,0)ParConIva,0 PrePreFijCosPro,isnull(ArtCodBar1,'') ArtCodBar1 ," +
                                          "isnull((p1.PrePreFijVal),0) precio1," +
                                          "isnull((p2.PrePreFijVal),0) precio2," +
                                          "isnull((p3.PrePreFijVal),0) precio3," +
                                          "isnull((p4.PrePreFijVal),0) precio4," +
                                          "isnull((p5.PrePreFijVal),0) precio5," +
                                          "isnull((p6.PrePreFijVal),0) precio6," +
                                          "isnull((p7.PrePreFijVal),0) precio7," +
                                          "isnull((p8.PrePreFijVal),0) precio8," +
                                          "isnull((p9.PrePreFijVal),0) precio9," +
                                          "isnull((p10.PrePreFijVal),0)  precio10," +
                                          "isnull((p11.PrePreFijVal),0)  precio11," +
                                          "isnull((p12.PrePreFijVal),0) precio12," +
                                          "isnull((p13.PrePreFijVal),0)  precio13," +
                                          "isnull((p14.PrePreFijVal),0)  precio14," +
                                          "isnull((p15.PrePreFijVal),0)  precio15," +
                                          "isnull((p16.PrePreFijVal),0)  precio16," +
                                          "isnull((p17.PrePreFijVal),0)  precio17," +
                                          "isnull((p18.PrePreFijVal),0)  precio18," +
                                          "isnull((p19.PrePreFijVal),0)  precio19," +
                                          "isnull((p20.PrePreFijVal),0)  precio20," +
                                          "isnull((p21.PrePreFijVal),0)  precio21," +
                                          "isnull((p22.PrePreFijVal),0)  precio22," +
                                          "isnull((p23.PrePreFijVal),0)  precio23," +
                                          "isnull((p24.PrePreFijVal),0)  precio24," +
                                          "isnull((p25.PrePreFijVal),0)  precio25," +
                                          "isnull((p26.PrePreFijVal),0)  precio26," +
                                          "isnull((p27.PrePreFijVal),0) precio27," +
                                          "isnull((p28.PrePreFijVal),0) precio28," +
                                          "isnull((p29.PrePreFijVal),0) precio29," +
                                          "isnull((p30.PrePreFijVal),0) precio30," +
                                          "isnull((p31.PrePreFijVal),0) precio31," +
                                          "isnull((p32.PrePreFijVal),0) precio32," +
                                          "isnull((p33.PrePreFijVal),0) precio33," +
                                          "isnull((p34.PrePreFijVal),0) precio34," +
                                          "isnull((p35.PrePreFijVal),0) precio35," +
                                          "isnull((p36.PrePreFijVal),0) precio36," +
                                          "isnull((p37.PrePreFijVal),0) precio37," +
                                          "isnull((p38.PrePreFijVal),0) precio38," +
                                          "isnull((p39.PrePreFijVal),0) precio39," +
                                          "isnull((p40.PrePreFijVal),0) precio40," +

                                          "isnull((p1.PrePorVal),0) PrePorVal1," +
                                          "isnull((p2.PrePorVal),0) PrePorVal2," +
                                          "isnull((p3.PrePorVal),0) PrePorVal3," +
                                          "isnull((p4.PrePorVal),0) PrePorVal4," +
                                          "isnull((p5.PrePorVal),0) PrePorVal5," +
                                          "isnull((p6.PrePorVal),0) PrePorVal6," +
                                          "isnull((p7.PrePorVal),0) PrePorVal7," +
                                          "isnull((p8.PrePorVal),0) PrePorVal8," +
                                          "isnull((p9.PrePorVal),0) PrePorVal9," +
                                          "isnull((p10.PrePorVal),0) PrePorVal10," +
                                          "isnull((p11.PrePorVal),0) PrePorVal11," +
                                          "isnull((p12.PrePorVal),0) PrePorVal12," +
                                          "isnull((p13.PrePorVal),0) PrePorVal13," +
                                          "isnull((p14.PrePorVal),0) PrePorVal14," +
                                          "isnull((p15.PrePorVal),0) PrePorVal15," +
                                          "isnull((p16.PrePorVal),0) PrePorVal16," +
                                          "isnull((p17.PrePorVal),0) PrePorVal17," +
                                          "isnull((p18.PrePorVal),0) PrePorVal18," +
                                          "isnull((p19.PrePorVal),0) PrePorVal19," +
                                          "isnull((p20.PrePorVal),0) PrePorVal20," +
                                          "isnull((p21.PrePorVal),0) PrePorVal21," +
                                          "isnull((p22.PrePorVal),0) PrePorVal22," +
                                          "isnull((p23.PrePorVal),0) PrePorVal23," +
                                          "isnull((p24.PrePorVal),0) PrePorVal24," +
                                          "isnull((p25.PrePorVal),0) PrePorVal25," +
                                          "isnull((p26.PrePorVal),0) PrePorVal26," +
                                          "isnull((p27.PrePorVal),0) PrePorVal27," +
                                          "isnull((p28.PrePorVal),0) PrePorVal28," +
                                          "isnull((p29.PrePorVal),0) PrePorVal29," +
                                          "isnull((p30.PrePorVal),0) PrePorVal30," +
                                          "isnull((p31.PrePorVal),0) PrePorVal31," +
                                          "isnull((p32.PrePorVal),0) PrePorVal32," +
                                          "isnull((p33.PrePorVal),0) PrePorVal33," +
                                          "isnull((p34.PrePorVal),0) PrePorVal34," +
                                          "isnull((p35.PrePorVal),0) PrePorVal35," +
                                          "isnull((p36.PrePorVal),0) PrePorVal36," +
                                          "isnull((p37.PrePorVal),0) PrePorVal37," +
                                          "isnull((p38.PrePorVal),0) PrePorVal38," +
                                          "isnull((p39.PrePorVal),0) PrePorVal39," +
                                          "isnull((p40.PrePorVal),0) PrePorVal40," +
                                          "0 desc1," +
                                          "0 desc2," +
                                          "0 desc3," +
                                          "0 desc4," +
                                          "0 desc5," +
                                          "0 desc6," +
                                          "1000 Exist2" +
                                          ",'' MovParArtFecMod" +
                                          ",1 Exist " +
                                          ",0 ExistFec " +
                                          ",0 ClaArtCod,'' ClaArtNom,isnull(ArtValImp,0) ArtValImp,1 ArtEmb,isnull(a.artdesven,'N') ArtIndMpm  " +
                                          ",0 ArtRen,0 ArtLim,'N' InvGruDifCheck,'N' InvSubDifCheck,'N' InvFamDifCheck,isnull(gc.InvCatCod,0) InvCatCod ,'N' ArtPesFac," +
                                          " isnull(a.LabCod,0) labcod,isnull(a.invClaCod,0) invClaCod , isnull(a.invseccod,0) invseccod , isnull(a.invmarcod,0) invmarcod, " +
                                          "  isnull(a.invlincod,0) invlincod,  isnull(a.invsubcatcod,0) invsubcatcod, " +
                                          "  isnull((select top 1 preartnom from presentacionarticulos ap  where PreArtFacCon='S' and ap.preartcod=pp.preartcod ),'') Presentacion   " +
                                          " ,isnull(master.dbo.fn_varbintohexstr(a.ArtImgBlob),'0x') as ArtImgBlob," +
                                          " isnull((select top 1 PreArtCod from presentacionarticulos ap  where PreArtFacCon='S' and ap.preartcod=pp.preartcod ),'') PreArtCod," +
                                          " isnull(ArtSolEnt,'N') ArtSolEnt  " +
                                          " from articulos a WITH (NOLOCK) " +
                                         // " ,'0x' as ArtImgBlob, isnull((select top 1 PreArtCod from presentacionarticulos ap  where PreArtFacCon='S' and ap.preartcod=pp.preartcod ),'') PreArtCod  from articulos a WITH (NOLOCK) " +
                                          "left join inventariofamilia f WITH (NOLOCK) on a.invfamcod=f.invfamcod " +
                                          "left join inventariosubgrupo s WITH (NOLOCK) on f.invsubgrucod= s.invsubgrucod " +
                                          "left join inventariogrupo g WITH (NOLOCK) on g.invgrucod=s.invgrucod " +
                                          " left join InventarioCategoria gc WITH (NOLOCK) on gc.InvCatCod=a.InvCatCod " + //andres
                                          "left join parametrocontable p WITH (NOLOCK)  on p.parconcod=a.parconcod " +
                                          "left join ArticulosPresentacion pp WITH (NOLOCK)  on a.artsec=pp.artsec and PreArtFaccon = 'S' " +
                                          "left join Presentacionarticulos pa WITH (NOLOCK) on pa.preartcod=pp.preartcod " +
                                          " left join PreciosDetalle p1 WITH (NOLOCK) on p1.ArtSec=a.ArtSec  and  p1.LisPreCod=1  and P1.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p2 WITH (NOLOCK) on p2.ArtSec=a.ArtSec  and  p2.LisPreCod=2  and P2.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p3 WITH (NOLOCK) on p3.ArtSec=a.ArtSec  and  p3.LisPreCod=3  and P3.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p4 WITH (NOLOCK) on p4.ArtSec=a.ArtSec  and  p4.LisPreCod=4  and P4.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p5 WITH (NOLOCK)  on p5.ArtSec=a.ArtSec  and  p5.LisPreCod=5  and P5.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p6 WITH (NOLOCK) on p6.ArtSec=a.ArtSec  and  p6.LisPreCod=6  and P6.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p7 WITH (NOLOCK) on p7.ArtSec=a.ArtSec  and  p7.LisPreCod=7  and P7.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p8 WITH (NOLOCK) on p8.ArtSec=a.ArtSec  and  p8.LisPreCod=8  and P8.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p9 WITH (NOLOCK) on p9.ArtSec=a.ArtSec  and  p9.LisPreCod=9  and P9.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p10 WITH (NOLOCK) on p10.ArtSec=a.ArtSec  and  p10.LisPreCod=10  and P10.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p11 WITH (NOLOCK) on p11.ArtSec=a.ArtSec  and  p11.LisPreCod=11  and P11.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p12 WITH (NOLOCK) on p12.ArtSec=a.ArtSec  and  p12.LisPreCod=12  and P12.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p13 WITH (NOLOCK) on p13.ArtSec=a.ArtSec  and  p13.LisPreCod=13  and P13.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p14 WITH (NOLOCK) on p14.ArtSec=a.ArtSec  and  p14.LisPreCod=14  and P14.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p15 WITH (NOLOCK) on p15.ArtSec=a.ArtSec  and  p15.LisPreCod=15  and P15.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p16 WITH (NOLOCK) on p16.ArtSec=a.ArtSec  and  p16.LisPreCod=16  and P16.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p17 WITH (NOLOCK) on p17.ArtSec=a.ArtSec  and  p17.LisPreCod=17  and P17.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p18 WITH (NOLOCK) on p18.ArtSec=a.ArtSec  and  p18.LisPreCod=18  and P18.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p19 WITH (NOLOCK) on p19.ArtSec=a.ArtSec  and  p19.LisPreCod=19  and P19.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p20 WITH (NOLOCK) on p20.ArtSec=a.ArtSec  and  p20.LisPreCod=20  and P20.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p21 WITH (NOLOCK) on p21.ArtSec=a.ArtSec  and  p21.LisPreCod=21  and P21.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p22 WITH (NOLOCK) on p22.ArtSec=a.ArtSec  and  p22.LisPreCod=22  and P22.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p23 WITH (NOLOCK) on p23.ArtSec=a.ArtSec  and  p23.LisPreCod=23  and P23.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p24 WITH (NOLOCK) on p24.ArtSec=a.ArtSec  and  p24.LisPreCod=24  and P24.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p25 WITH (NOLOCK) on p25.ArtSec=a.ArtSec  and  p25.LisPreCod=25  and P25.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p26 WITH (NOLOCK) on p26.ArtSec=a.ArtSec  and  p26.LisPreCod=26  and P26.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p27 WITH (NOLOCK) on p27.ArtSec=a.ArtSec  and  p27.LisPreCod=27  and P27.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p28 WITH (NOLOCK) on p28.ArtSec=a.ArtSec  and  p28.LisPreCod=28  and P28.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p29 WITH (NOLOCK) on p29.ArtSec=a.ArtSec  and  p29.LisPreCod=29  and P29.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p30 WITH (NOLOCK) on p30.ArtSec=a.ArtSec  and  p30.LisPreCod=30  and P30.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p31 WITH (NOLOCK) on p31.ArtSec=a.ArtSec  and  p31.LisPreCod=31  and P31.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p32 WITH (NOLOCK) on p32.ArtSec=a.ArtSec  and  p32.LisPreCod=32  and P32.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p33 WITH (NOLOCK) on p33.ArtSec=a.ArtSec  and  p33.LisPreCod=33  and P33.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p34 WITH (NOLOCK) on p34.ArtSec=a.ArtSec  and  p34.LisPreCod=34  and P34.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p35 WITH (NOLOCK) on p35.ArtSec=a.ArtSec  and  p35.LisPreCod=35  and P35.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p36 WITH (NOLOCK) on p36.ArtSec=a.ArtSec  and  p36.LisPreCod=36  and P36.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p37 WITH (NOLOCK) on p37.ArtSec=a.ArtSec  and  p37.LisPreCod=37  and P37.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p38 WITH (NOLOCK) on p38.ArtSec=a.ArtSec  and  p38.LisPreCod=38  and P38.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p39 WITH (NOLOCK) on p39.ArtSec=a.ArtSec  and  p39.LisPreCod=39  and P39.PreArtCod=PP.PreArtCod" +
                                          " left join PreciosDetalle p40 WITH (NOLOCK) on p40.ArtSec=a.ArtSec  and  p40.LisPreCod=40  and P40.PreArtCod=PP.PreArtCod"+
                                          " WHERE a.artdesven <> 'S' " +
                                          //" and ( s.invgrucod in(select invgrucod from VendedoresGruposInventarios where rtrim(VenCod)='" + vUsuario + "') or " +
                                          " ) Consulta order by Roww desc";




                    }


                    classbd classbd = new classbd();
                     rsImport = comm.executeQuery(classbd.FormatearMysql(Script));
                    mensajeslargos("Consulta articulosss",Script);



                    BdSql.execSQL("Delete from articulos");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Roww"));
                            TotalFilas=rsImport.getInt("Roww");
                            String Filas=rsImport.getString("Roww").trim();
                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);

                        String codigo=rsImport.getString("ArtCod").trim();
                        int jj=1;
                        //    if (codigo.equalsIgnoreCase("102285")){
                        //        jj=2;
                        //    }
                        //String Existencia=TraerExistencia(rsImport.getString("ArtSec").trim());



                        String InsertScript = "insert into articulos ("
                                + "ArtSec,"
                                + "ArtCod,"
                                + "ArtNom,"
                                + "ArtMedNomCom,"
                                + "InvGruCod,"
                                + "InvGruNom,"
                                + "InvSubGruCod,"
                                + "InvSubGruNom,"
                                + "InvFamCod,"
                                + "InvFamNom,"
                                + "ParConIva,"
                                + "artren,"
                                + "artlim,"
                                + "precio1,"
                                + "precio2,"
                                + "precio3,"
                                + "precio4,"
                                + "precio5,"
                                + "precio6,"
                                + "precio7,"
                                + "precio8,"
                                + "precio9,"
                                + "precio10,"
                                + "precio11,"
                                + "precio12,"
                                + "precio13,"
                                + "precio14,"
                                + "precio15,"
                                + "precio16,"
                                + "precio17,"
                                + "precio18,"
                                + "precio19,"
                                + "precio20,"
                                + "precio21,"
                                + "precio22,"
                                + "precio23,"
                                + "precio24,"
                                + "precio25,"
                                + "precio26,"
                                + "precio27,"
                                + "precio28,"
                                + "precio29,"
                                + "precio30,"
                                + "precio31,"
                                + "precio32,"
                                + "precio33,"
                                + "precio34,"
                                + "precio35,"
                                + "precio36,"
                                + "precio37,"
                                + "precio38,"
                                + "precio39,"
                                + "precio40,"
                                + "precio1PorRen,"
                                + "precio2PorRen,"
                                + "precio3PorRen,"
                                + "precio4PorRen,"
                                + "precio5PorRen,"
                                + "precio6PorRen,"
                                + "precio7PorRen,"
                                + "precio8PorRen,"
                                + "precio9PorRen,"
                                + "precio10PorRen,"
                                + "precio11PorRen,"
                                + "precio12PorRen,"
                                + "precio13PorRen,"
                                + "precio14PorRen,"
                                + "precio15PorRen,"
                                + "precio16PorRen,"
                                + "precio17PorRen,"
                                + "precio18PorRen,"
                                + "precio19PorRen,"
                                + "precio20PorRen,"
                                + "precio21PorRen,"
                                + "precio22PorRen,"
                                + "precio23PorRen,"
                                + "precio24PorRen,"
                                + "precio25PorRen,"
                                + "precio26PorRen,"
                                + "precio27PorRen,"
                                + "precio28PorRen,"
                                + "precio29PorRen,"
                                + "precio30PorRen,"
                                + "precio31PorRen,"
                                + "precio32PorRen,"
                                + "precio33PorRen,"
                                + "precio34PorRen,"
                                + "precio35PorRen,"
                                + "precio36PorRen,"
                                + "precio37PorRen,"
                                + "precio38PorRen,"
                                + "precio39PorRen,"
                                + "precio40PorRen,"
                                + "desc1,"
                                + "desc2,"
                                + "desc3,"
                                + "desc4,"
                                + "desc5,"
                                + "desc6,"
                                + "Exist,"
                                + "ExistFec,"
                                + "ClaArtCod,"
                                + "ClaArtNom,artemb,ArtIndMpm,MovParArtFecMod,PrePreFijCosPro,"
                                + "ArtValImp,GruCheck,SubCheck,FamCheck,InvCatCod,LabCod,invClaCod,invseccod,invmarcod,invlincod,invsubcatcod, " +
                                " ArtCantInf,ArtcodBar,Presentacion, ArtImgBlob, PreArtCod,ArtSolEnt)values('"
                                + rsImport.getString("ArtSec").trim() + "','"
                                + rsImport.getString("ArtCod").trim() + "','"
                                + rsImport.getString("ArtNom").trim().replace("'","-") + "','"
                                + rsImport.getString("ArtMedNomCom").trim().replace("'","-") + "','"
                                + rsImport.getString("InvGruCod").trim() + "','"
                                + rsImport.getString("InvGruNom").trim().replace("'","-") + "','"
                                + rsImport.getString("InvSubGruCod").trim() + "','"
                                + rsImport.getString("InvSubGruNom").trim().replace("'","-") + "','"
                                + rsImport.getString("InvFamCod").trim() + "','"
                                + rsImport.getString("InvFamNom").trim().replace("'","-") + "',"
                                + rsImport.getString("ParConIva").trim() + ","
                                + rsImport.getString("ArtRen").trim() + ","
                                + rsImport.getString("ArtLim").trim() + ","
                                + rsImport.getString("precio1").trim() + ","
                                + rsImport.getString("precio2").trim() + ","
                                + rsImport.getString("precio3").trim() + ","
                                + rsImport.getString("precio4").trim() + ","
                                + rsImport.getString("precio5").trim() + ","
                                + rsImport.getString("precio6").trim() + ","
                                + rsImport.getString("precio7").trim() + ","
                                + rsImport.getString("precio8").trim() + ","
                                + rsImport.getString("precio9").trim() + ","
                                + rsImport.getString("precio10").trim() + ","
                                + rsImport.getString("precio11").trim() + ","
                                + rsImport.getString("precio12").trim() + ","
                                + rsImport.getString("precio13").trim() + ","
                                + rsImport.getString("precio14").trim() + ","
                                + rsImport.getString("precio15").trim() + ","
                                + rsImport.getString("precio16").trim() + ","
                                + rsImport.getString("precio17").trim() + ","
                                + rsImport.getString("precio18").trim() + ","
                                + rsImport.getString("precio19").trim() + ","
                                + rsImport.getString("precio20").trim() + ","
                                + rsImport.getString("precio21").trim() + ","
                                + rsImport.getString("precio22").trim() + ","
                                + rsImport.getString("precio23").trim() + ","
                                + rsImport.getString("precio24").trim() + ","
                                + rsImport.getString("precio25").trim() + ","
                                + rsImport.getString("precio26").trim() + ","
                                + rsImport.getString("precio27").trim() + ","
                                + rsImport.getString("precio28").trim() + ","
                                + rsImport.getString("precio29").trim() + ","
                                + rsImport.getString("precio30").trim() + ","
                                + rsImport.getString("precio31").trim() + ","
                                + rsImport.getString("precio32").trim() + ","
                                + rsImport.getString("precio33").trim() + ","
                                + rsImport.getString("precio34").trim() + ","
                                + rsImport.getString("precio35").trim() + ","
                                + rsImport.getString("precio36").trim() + ","
                                + rsImport.getString("precio37").trim() + ","
                                + rsImport.getString("precio38").trim() + ","
                                + rsImport.getString("precio39").trim() + ","
                                + rsImport.getString("precio40").trim() + ","
                                + rsImport.getString("PrePorVal1").trim() + ","
                                + rsImport.getString("PrePorVal2").trim() + ","
                                + rsImport.getString("PrePorVal3").trim() + ","
                                + rsImport.getString("PrePorVal4").trim() + ","
                                + rsImport.getString("PrePorVal5").trim() + ","
                                + rsImport.getString("PrePorVal6").trim() + ","
                                + rsImport.getString("PrePorVal7").trim() + ","
                                + rsImport.getString("PrePorVal8").trim() + ","
                                + rsImport.getString("PrePorVal9").trim() + ","
                                + rsImport.getString("PrePorVal10").trim() + ","
                                + rsImport.getString("PrePorVal11").trim() + ","
                                + rsImport.getString("PrePorVal12").trim() + ","
                                + rsImport.getString("PrePorVal13").trim() + ","
                                + rsImport.getString("PrePorVal14").trim() + ","
                                + rsImport.getString("PrePorVal15").trim() + ","
                                + rsImport.getString("PrePorVal16").trim() + ","
                                + rsImport.getString("PrePorVal17").trim() + ","
                                + rsImport.getString("PrePorVal18").trim() + ","
                                + rsImport.getString("PrePorVal19").trim() + ","
                                + rsImport.getString("PrePorVal20").trim() + ","
                                + rsImport.getString("PrePorVal21").trim() + ","
                                + rsImport.getString("PrePorVal22").trim() + ","
                                + rsImport.getString("PrePorVal23").trim() + ","
                                + rsImport.getString("PrePorVal24").trim() + ","
                                + rsImport.getString("PrePorVal25").trim() + ","
                                + rsImport.getString("PrePorVal26").trim() + ","
                                + rsImport.getString("PrePorVal27").trim() + ","
                                + rsImport.getString("PrePorVal28").trim() + ","
                                + rsImport.getString("PrePorVal29").trim() + ","
                                + rsImport.getString("PrePorVal30").trim() + ","
                                + rsImport.getString("PrePorVal31").trim() + ","
                                + rsImport.getString("PrePorVal32").trim() + ","
                                + rsImport.getString("PrePorVal33").trim() + ","
                                + rsImport.getString("PrePorVal34").trim() + ","
                                + rsImport.getString("PrePorVal35").trim() + ","
                                + rsImport.getString("PrePorVal36").trim() + ","
                                + rsImport.getString("PrePorVal37").trim() + ","
                                + rsImport.getString("PrePorVal38").trim() + ","
                                + rsImport.getString("PrePorVal39").trim() + ","
                                + rsImport.getString("PrePorVal40").trim() + ","
                                + rsImport.getString("desc1").trim() + ","
                                + rsImport.getString("desc2").trim() + ","
                                + rsImport.getString("desc3").trim() + ","
                                + rsImport.getString("desc4").trim() + ","
                                + rsImport.getString("desc5").trim() + ","
                                + rsImport.getString("desc6").trim() + ","
                                + rsImport.getString("Exist").trim()+ ","
                                + rsImport.getString("ExistFec").trim()+ ","
                                + rsImport.getString("ClaArtCod").trim() + ",'"
                                + rsImport.getString("ClaArtNom").trim() + "',"
                                + rsImport.getString("ArtEmb").trim() + ",'"
                                + rsImport.getString("ArtIndMpm").trim() + "','"
                                + rsImport.getString("MovParArtFecMod").trim() + "',"
                                + rsImport.getString("PrePreFijCosPro").trim() + ","
                                + rsImport.getString("ArtValImp").trim() + ",'"
                                + rsImport.getString("InvGruDifCheck").trim() + "','"
                                + rsImport.getString("InvSubDifCheck").trim() + "','"
                                + rsImport.getString("InvFamDifCheck").trim() + "','"
                                + rsImport.getString("InvCatCod").trim() + "','"
                                + rsImport.getString("LabCod").trim() + "','"
                                + rsImport.getString("invClaCod").trim() + "','"
                                + rsImport.getString("invseccod").trim() + "','"
                                + rsImport.getString("invmarcod").trim() + "','"
                                + rsImport.getString("invlincod").trim() + "','"
                                + rsImport.getString("invsubcatcod").trim() + "','"
                                + rsImport.getString("ArtPesFac").trim() + "','"
                                + rsImport.getString("ArtCodBar1").trim() + "'," +
                                " '"+rsImport.getString("Presentacion").trim() + "'," +
                                " '"+rsImport.getString("ArtImgBlob")+"'," +
                                "  '"+rsImport.getString("PreArtCod")+"'," +
                                " '"+rsImport.getString("ArtSolEnt")+"')";

                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                                Errores+=1;
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;
                            Log.e("ErrosqlArtFicc",ex.toString());

                        }

                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                            Log.e("ErrosqlArtFiccw",e.toString());

                        }

                    }

                } catch (Exception e) {
                    Log.e("ErrosqlArtFiccww",e.toString());
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }
                ErroresGen+=Errores;
                CargarArticulosPreFicc();
                CargarConceptosNotasFicc();
                CargarDatos();



            }
        });
        thread.start();
        thread.interrupt();
        return Resultado;
    }



    public  int[] CargarMovCauPedFicc() {

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntCausal);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkCausal);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrCausal);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_Causal);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;

                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                /*/*BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(),"MantisMovil",null,6); //new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();*/

              /*  try {

                    ConBd conbd = new ConBd();
                    Connection conn = conbd.CargarConexion();
                    Statement comm = conn.createStatement();
                    String Script="select * from(select ROW_NUMBER() OVER(ORDER BY MovCauSec ASC) AS Row,MovCauSec,MovCauNom,ISNULL(MovConPed,'S') MovConPed,ISNULL(MovPidFot,'N') MovPidFot,ISNULL(MovPidObs,'S') MovPidObs  from MovCauPed) Consulta order by Row desc";
                    ResultSet rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from MovCauPed");
*/
                   // while (rsImport.next()){
                  /*      if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);
                     */

                        String InsertScript = "insert into MovCauPed (MovCauSec,MovCauNom,MovConPed,MovPidFot,MovPidObs) values ( 1 ,'1. Iniciar visita','N','N','N')";
                       try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                                //   Errores+=1;
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;

                        }

                      /*  try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                        }*/


               /* } catch (SQLException e) {
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }*/
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};
                ErroresGen+=Errores;
                CargarDatos();

            }
        });
        thread.start();
        thread.interrupt();

        return Resultado;
    }
    public  int[] CargarGrupoFicc(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntGrupo);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkGrupo);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrGrupo);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_Grupo);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;

                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();
                SQLiteDatabase delBdSql=vBaseDeDatos.getWritableDatabase();

               /* /*BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(),"MantisMovil",null,6); //new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();*/


                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();

                String vEmpresa=vGlobalVariables.getEmpresa();
                vEmpresa=vEmpresa.toUpperCase();

                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String IbCan="";

                    String Script="select * from(select ROW_NUMBER() OVER(ORDER BY InvGruCod ASC) AS Roww,InvGruCod,InvGruNom, '' InvCanCodStr from InventarioGrupo) jj order by Roww desc";
                     rsImport = comm.executeQuery(Script);
                    delBdSql.execSQL("Delete from InventarioGrupo");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Roww"));
                            TotalFilas=rsImport.getInt("Roww");
                            String Filas=rsImport.getString("Roww").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);


                        //vPrBar_Ciudades.

                        String InsertScript = "insert into InventarioGrupo (InvGruCod,InvCanCodStr,InvGruNom) values ('"
                                + rsImport.getString("InvGruCod").trim() + "','XX','"
                                + rsImport.getString("InvGruNom").trim().replace("'","") + "')" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Log.e("ERROR1",ex.toString());
                            Errores+=1;

                        }
                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                            Log.e("ERROR2",e.toString());

                        }
//*/

                    }
                } catch (Exception e) {
                    Log.e("ERROR3",e.toString());
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

                ErroresGen+=Errores;
                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        });

        thread.start();
        thread.interrupt();

        return Resultado;
    }
    public  int[] CargarSubGrupoFicc(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntSubGrupo);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkSubGrupo);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrSubGrupo);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_SubGrupo);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;

               BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();
                SQLiteDatabase deldSql=vBaseDeDatos.getWritableDatabase();

                /*BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(),"MantisMovil",null,6); //new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();*/



                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="select * from(select ROW_NUMBER() OVER(ORDER BY InvSubGruCod ASC) AS Roww,InvSubGruCod,InvGruCod,InvSubGruNom from InventarioSubGrupo) jj order by Roww desc";
                     rsImport = comm.executeQuery(Script);
                    deldSql.execSQL("Delete from inventariosubgrupo");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Roww"));
                            TotalFilas=rsImport.getInt("Roww");
                            String Filas=rsImport.getString("Roww").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);

                        String InsertScript = "insert into inventariosubgrupo (InvSubGruCod,InvGruCod,InvSubGruNom) values ('"
                                + rsImport.getString("InvSubGruCod").trim() + "','"
                                + rsImport.getString("InvGruCod").trim() + "','"
                                + rsImport.getString("InvSubGruNom").trim() + "')" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;
                            Log.e("ERROR1",ex.toString());
                        }

                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                            Log.e("ERROR2",e.toString());
                        }
                    }
                } catch (Exception e) {
                    Errores=1;
                    Log.e("ERROR3",e.toString());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }



                ErroresGen+=Errores;
                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        }).start();
        return Resultado;
    }
    public  int[] CargarFamiliaFicc(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntFamilia);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkFamilia);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrFamilia);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_Familia);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
               BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();
                /*BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(),"MantisMovil",null,6); //new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();*/


                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="select * from(select ROW_NUMBER() OVER(ORDER BY InvFamCod ASC) AS Roww,InvFamCod,InvSubGruCod,InvFamNom from InventarioFamilia) jj order by Roww desc";
                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from inventariofamilia");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Roww"));
                            TotalFilas=rsImport.getInt("Roww");
                            String Filas=rsImport.getString("Roww").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);


//*/

                        //vPrBar_Ciudades.

                        String InsertScript = "insert into inventariofamilia (InvFamCod,InvSubGruCod,InvFamNom) values ('"
                                + rsImport.getString("InvFamCod").trim() + "','"
                                + rsImport.getString("InvSubGruCod").trim() + "','"
                                + rsImport.getString("InvFamNom").trim().replace("'","-") + "')" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;
                            Log.e("ERROR3",ex.toString());
                        }
                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                            Log.e("ERROR3",e.toString());
                        }
                    }
                } catch (Exception e) {
                    Errores=1;
                    Log.e("ERROR3",e.toString());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }
                ErroresGen+=Errores;
                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        }).start();



        return Resultado;
    }
    public  int[] CargarCanalSubCanalFicc(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntCanalSubCanal);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkCanalSubCanal);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrCanalSubCanal);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_CanalSubCanal);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;

              BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                /*BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(),"MantisMovil",null,6); //new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();*/


                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="select * from (select ROW_NUMBER() OVER(ORDER BY c.cancod ASC) AS Roww,c.cancod,c.cannom,cansubcod,cansubnom,'' cona,'' conb,''conc from SubCanal sb left join Canales c on c.cancod=sb.cancod) jj order by Roww desc";
                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from Canales");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Roww"));
                            TotalFilas=rsImport.getInt("Roww");
                            String Filas=rsImport.getString("Roww").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);


                        String InsertScript = "insert into Canales(cancod,cansubcod,cannom,cansubnom,cancona,canconb,canconc) values ("
                                + rsImport.getString("cancod").trim() + ","
                                + rsImport.getString("cansubcod").trim() + ",'"
                                + rsImport.getString("cannom").trim() + "','"
                                + rsImport.getString("cansubnom").trim() + "','"
                                + rsImport.getString("cona").trim() + "','"
                                + rsImport.getString("conb").trim() + "','"
                                + rsImport.getString("conc").trim() + "')" ;


                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;
                            Log.e("ERROR3",ex.toString());

                        }

                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                            Log.e("ERROR3",e.toString());
                        }

                    }
                } catch (Exception e) {
                    Log.e("ERROR3",e.toString());
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }



                ErroresGen+=Errores;
                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        }).start();



        return Resultado;
    }
    public  int[] CargarListasPreciosFicc(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntListasPrecios);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkListasPrecios);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrListasPrecios);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_ListasPrecios);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;

                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                /*BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(),"MantisMovil",null,6); //new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();*/


                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();

                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {
                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="";
                if(vEmpresa.equalsIgnoreCase("SUHOGAR")){
                    Script="select * from(select ROW_NUMBER() OVER(ORDER BY LisPreCod ASC) AS Roww,LisPreCod,LisPreNom,isnull(LisPrebloqDes,'N') LisPrebloqDes from ListaPrecios where LisPreEst = 'A') jj order by Roww desc";

                }else{
                    Script="select * from(select ROW_NUMBER() OVER(ORDER BY LisPreCod ASC) AS Roww,LisPreCod,LisPreNom,'N' LisPrebloqDes from ListaPrecios where LisPreEst = 'A') jj order by Roww desc";

                }
                      rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from ListasPrecios");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Roww"));
                            TotalFilas=rsImport.getInt("Roww");
                            String Filas=rsImport.getString("Roww").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);

                        String InsertScript = "insert into ListasPrecios (LisPreCod,LisPreNom,LisPrebloqDes) values ("
                                + rsImport.getString("LisPreCod").trim() + ",'"
                                + rsImport.getString("LisPreNom").trim() + "','"
                                + rsImport.getString("LisPrebloqDes").trim() + "')" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Log.e("ERROR3",ex.toString());
                            Errores+=1;

                        }
                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                            Log.e("ERROR3",e.toString());
                        }
//*/
                    }
                } catch (Exception e) {
                    Errores=1;
                    Log.e("ERROR3",e.toString());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

                ErroresGen+=Errores;
                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        }).start();



        return Resultado;
    }
    private void CargarBodegasFicc() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;

                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                /*BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(),"MantisMovil",null,6); //new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();*/


                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="";


                    Script= "select  Bodsucccnom , UniNegBodSucccSec as BodCodapp, UniNegBodPre as BodCheckPred from UnidadNegocioBodegasMovil v left join Vendedores c  on v.Alinegcod = c.AliNegCod left join Bodegas n  on UniNegBodSucccSec = n.Bodsucccsec where vencod = '"+vUsuario+"'  union all select  Bodsucccnom , BodSucccSec as BodCodapp, 'S' as BodCheckPred  from Bodegas where BodSucPri = 'S' and BodReaAudProLog = 'S'  and NOT EXISTS (SELECT *FROM UnidadNegocioBodegasMovil c left join Vendedores v   on v.Alinegcod = c.AliNegCod where vencod =  '"+vUsuario+"'  ) order by BodCodapp asc";




                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from Bodegas");

                    while (rsImport.next()){

                        String InsertScript = "insert into Bodegas (BodCod,BodCheckPred,BodNom) values ("
                                + rsImport.getString("BodCodapp").trim() + ",'"
                                + rsImport.getString("BodCheckPred").trim() + "','"
                                + rsImport.getString("Bodsucccnom").trim() + "')" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Log.e("ERROR3",ex.toString());
                            Errores+=1;

                        }
//*/
                    }
                } catch (Exception e) {
                    Errores=1;
                    Log.e("ERROR3",e.toString());

                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

                ErroresGen+=Errores;
                CargarDatos();
            }
        }).start();
    }
    private void CargarExistenciaFicc() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;

                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();
                /*BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(),"MantisMovil",null,6); //new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();*/


                Cursor traerArt = vBaseDeDatos.getReadableDatabase().rawQuery("select BodCod from Bodegas", null);
                String bodega = "ExiBodSucCCsec in (";
                if(traerArt.getCount() > 0){
                    traerArt.moveToFirst();
                    do{
                        bodega += traerArt.getString(0).trim()+",";
                    }while (traerArt.moveToNext());
                    bodega +=")";
                    bodega = bodega.replace(",)", ")");
                }else{
                    bodega = "ExiAct > 0";
                }

                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="";
                    Script="select ExiArtSec,ExiBodSucCCsec,ExiAct  from ExistenciaActual e WITH (NOLOCK) where  "+bodega ;
                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from ArticulosExi");

                    while (rsImport.next()){


                        String InsertScript = "insert into ArticulosExi (ArtSec,ArtBodCod,ArtExiAct) values ('"
                                + rsImport.getString("ExiArtSec").trim() + "',"
                                + rsImport.getString("ExiBodSucCCsec").trim() + ","
                                + rsImport.getString("ExiAct").trim() + ")" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Log.e("ERROR3",ex.toString());
                            Errores+=1;

                        }
//*/
                    }
                } catch (Exception e) {

                    Log.e("ERROR3", e.toString());
                    Errores = 1;

                }
                // }while (traerArt.moveToNext());
                //}



                ErroresGen+=Errores;
                CargarDatos();
            }
        }).start();
    }




    private void CargarCategoriaFicc() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
               BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();
                BdSql.enableWriteAheadLogging();

                /*BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(),"MantisMovil",null,6); //new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();*/


                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="";
                    Script="select InvCatCod, InvCatNom from InventarioCategoria ";
                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from InvCategoria");

                    while (rsImport.next()){

                        Log.e("error InvCatNom ",rsImport.getString("InvCatNom").trim());
                        String InsertScript = "insert into InvCategoria (InvCatCod,InvCatNom) values ('"
                                + rsImport.getString("InvCatCod").trim() + "','"
                                + rsImport.getString("InvCatNom").trim() + "')" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Log.e("error ex cat ",ex.toString());
                            Errores+=1;
                            Log.e("ERROR3",ex.toString());
                        }
//*/
                    }
                } catch (Exception e) {
                    Log.e("error categoria cat",e.toString());
                    Log.e("ERROR3",e.toString());
                    Errores=1;

                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }
                // }while (traerArt.moveToNext());
                //}



                ErroresGen+=Errores;
                CargarDatos();
            }
        }).start();
    }
    private void CargarObsMovilFicc() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
               BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();
                BdSql.enableWriteAheadLogging();

                /*BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(),"MantisMovil",null,6); //new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();*/

                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="";
                    Script="select ObsCliMovil, obsCliNitSec,obsCliClisec from ObsClienteMovil ";
                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from InvCategoria");

                    while (rsImport.next()){


                        String InsertScript = "insert into obsCliente (NitSec,CliSec,CliObsMovil) values ('"
                                + rsImport.getString("obsCliNitSec").trim() + "',"
                                + rsImport.getString("obsCliClisec").trim() + ",'"
                                + rsImport.getString("ObsCliMovil").trim() + "')" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){

                            Errores+=1;

                        }
//*/
                    }
                } catch (Exception e) {
                    Log.e("error categoria ",e.toString());

                    Errores=1;

                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

                // }while (traerArt.moveToNext());
                //}



                ErroresGen+=Errores;
                CargarDatos();
            }
        }).start();
    }
    public  int[] CargarDescuentosFicc(){
        int hh=0;
        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntDescuentos);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkDescuentos);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrDescuentos);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_Descuentos);
        vPrBar_Import.setProgress(0);
        //vPrBar_Import.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        //int TotalFilasCiudades=0;
        //int InsertadosCiudades=0;
        //int ErroresCiudades=0;
        //int VueltasCiudades=0;
        final int[] Resultado = new int[]{0, 0, 0};

        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
               BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();
                BdSql.enableWriteAheadLogging();
                /*BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(),"MantisMovil",null,6); //new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();*/

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                String vEmpresa=vGlobalVariables.getEmpresa();
                vEmpresa=vEmpresa.toUpperCase();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsClientes  = null;

                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    String Script="";
                    vEmpresa=vEmpresa.toUpperCase();

                    conbd.Variables();
                    String mysql = conbd.Mysql;

                    Script = " \n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "SELECT *\n" +
                            "FROM (\n" +
                            "\n" +
                            "    SELECT \n" +
                            "        ROW_NUMBER() OVER (ORDER BY tipodesc ASC) AS Roww,\n" +
                            "        *\n" +
                            "    FROM (\n" +
                            "\n" +
                            "        /* =========================\n" +
                            "           \uD83D\uDD39 CASO NORMAL (N)\n" +
                            "        ========================= */\n" +
                            "        SELECT  \n" +
                            "            d.DesProgCod AS DesoBonSec,\n" +
                            "            ISNULL(DESPROGAPLESCTOT,'N') AS DESPROGAPLESCTOT,\n" +
                            "            'ART' AS tipodesc,\n" +
                            "            DESPROGDES,\n" +
                            "            'XX,' AS DesoBonAgru,\n" +
                            "\n" +
                            "            'XX,' AS DesoBonGrupo,\n" +
                            "            'XX,' AS SUBGRUPOS,\n" +
                            "            'XX,' AS FAMILIAS,\n" +
                            "\n" +
                            "            REPLACE(\n" +
                            "                ISNULL(',' + CONVERT(VARCHAR(50), CAST(DG.DesProArtSec AS VARCHAR) + CAST(DG.DesProPreArtCod AS VARCHAR)) + ',', 'XX'),\n" +
                            "                ' ',\n" +
                            "                ''\n" +
                            "            ) AS ARTICULOS,\n" +
                            "\n" +
                            "            'XX,' AS LABORATORIO,\n" +
                            "            'XX,' AS CLASE,\n" +
                            "            'XX,' AS SECCION,\n" +
                            "            'XX,' AS MARCA,\n" +
                            "            'XX,' AS LINEART,\n" +
                            "            'XX,' AS CATEGORIA,\n" +
                            "            'XX,' AS SUBCATEGORIA,\n" +
                            "            'XX,' AS CANALES,\n" +
                            "            'XX,' AS SUBCANALES,\n" +
                            "            'XX,' AS TAMANOS,\n" +
                            "            'XX,' AS UNIDADNEG,\n" +
                            "            'XX,' AS CIUDADES,\n" +
                            "\n" +
                            "            -- BODEGAS\n" +
                            "            REPLACE(ISNULL((\n" +
                            "                SELECT ', ' + CAST(DesProBodSucCcSec AS VARCHAR)\n" +
                            "                FROM DescuentosProgramadosBodegas B\n" +
                            "                WHERE B.DesProgCod = DG.DesProgCod\n" +
                            "                FOR XML PATH('')\n" +
                            "            ), 'XX'), ' ', '') + ',' AS BODEGAS,\n" +
                            "\n" +
                            "            -- CLIENTES\n" +
                            "            REPLACE(ISNULL((\n" +
                            "                SELECT ', ' + CAST(DesProNitSec AS VARCHAR)\n" +
                            "                FROM DescuentosProgramadosTerceros T\n" +
                            "                WHERE T.DesProgCod = DG.DesProgCod\n" +
                            "                FOR XML PATH('')\n" +
                            "            ), 'XX'), ' ', '') + ',' AS CLIENTES," +
                            "      /* LISTAS */\n" +
                            "            REPLACE(\n" +
                            "                ISNULL(\n" +
                            "                    (\n" +
                            "                        SELECT ', ' + CAST(lp.DesProLisPreCod AS VARCHAR)\n" +
                            "                        FROM DescuentosProgramadosListaPrec lp\n" +
                            "                        WHERE lp.DesProgCod = DG.DesProgCod\n" +
                            "                        FOR XML PATH('')\n" +
                            "                    ),\n" +
                            "                    'XX'\n" +
                            "                ),\n" +
                            "                ' ',\n" +
                            "                ''\n" +
                            "            ) + ',' AS LISTAS,    " +
                            "" +
                            "" +
                            "" +
                            "\n" +
                            "            'XX,' AS EXCLIENTES,\n" +
                            "            'XX,' AS VENDEDORES,\n" +
                            "            'XX,' AS SUCURSALES,\n" +
                            "            'XX,' AS PERFILES,\n" +
                            "\n" +
                            "            ISNULL(DESPRODETCANDES, 0) AS DESOBONESCRAN,\n" +
                            "            ISNULL(DESPRODETCANHAS, 0) AS DESOBONESCHASTA,\n" +
                            "\n" +
                            "            'N' AS DescPro1CajMen1,\n" +
                            "            0 AS DescProDesCaj,\n" +
                            "            0 AS DescProHasCaj,\n" +
                            "            0 AS DescProDesVal,\n" +
                            "            0 AS DescProHasVal,\n" +
                            "\n" +
                            "            ISNULL(DESPRODESPRO, 0) AS DesoBonEscDesc,\n" +
                            "\n" +
                            "            DESPROGFECINI,\n" +
                            "            DESPROGFECFIN,\n" +
                            "\n" +
                            "            '' AS DescArtDes,\n" +
                            "            ISNULL(DesProDetArtSec, 0) AS linea,\n" +
                            "            'TRA' AS DescPorMov\n" +
                            "\n" +
                            "        FROM DescuentosProgramadosArticulos DG\n" +
                            "        LEFT JOIN DescuentosProgramados d\n" +
                            "            ON d.DesProgCod = DG.DesProgCod\n" +
                            "\n" +
                            "        WHERE \n" +
                            "            ISNULL(DESPROGAPLESCTOT,'N') = 'N' " +
                            "            and DesProgEst <> 'I'   \n " +
                            "            AND DesProgEstFoL not in ('F','L') " +
                            "            AND CONVERT(date, DESPROGFECINI) <= CONVERT(date, GETDATE())\n" +
                            "            AND CONVERT(date, DESPROGFECFIN) >= CONVERT(date, GETDATE())\n" +
                            "\n" +
                            "\n" +
                            "        UNION ALL\n" +
                            "\n" +
                            "\n" +
                            "        /* =========================\n" +
                            "           \uD83D\uDD25 CASO AGRUPADO (S)\n" +
                            "        ========================= */\n" +
                            "        SELECT  \n" +
                            "            d.DesProgCod AS DesoBonSec,\n" +
                            "            'S' AS DESPROGAPLESCTOT,\n" +
                            "            'ART' AS tipodesc,\n" +
                            "            DESPROGDES,\n" +
                            "            'XX,' AS DesoBonAgru,\n" +
                            "\n" +
                            "            'XX,' AS DesoBonGrupo,\n" +
                            "            'XX,' AS SUBGRUPOS,\n" +
                            "            'XX,' AS FAMILIAS,\n" +
                            "\n" +
                            "            -- \uD83D\uDD25 TODOS LOS ARTICULOS AGRUPADOS\n" +
                            "            REPLACE(ISNULL((\n" +
                            "                SELECT ',' + CONVERT(VARCHAR(50), CAST(A2.DesProArtSec AS VARCHAR) + CAST(A2.DesProPreArtCod AS VARCHAR))\n" +
                            "                FROM DescuentosProgramadosArticulos A2\n" +
                            "                WHERE A2.DesProgCod = d.DesProgCod\n" +
                            "                FOR XML PATH('')\n" +
                            "            ), 'XX'), ' ', '') + ',' AS ARTICULOS,\n" +
                            "\n" +
                            "            'XX,' AS LABORATORIO,\n" +
                            "            'XX,' AS CLASE,\n" +
                            "            'XX,' AS SECCION,\n" +
                            "            'XX,' AS MARCA,\n" +
                            "            'XX,' AS LINEART,\n" +
                            "            'XX,' AS CATEGORIA,\n" +
                            "            'XX,' AS SUBCATEGORIA,\n" +
                            "            'XX,' AS CANALES,\n" +
                            "            'XX,' AS SUBCANALES,\n" +
                            "            'XX,' AS TAMANOS,\n" +
                            "            'XX,' AS UNIDADNEG,\n" +
                            "            'XX,' AS CIUDADES,\n" +
                            "\n" +
                            "            -- BODEGAS\n" +
                            "            REPLACE(ISNULL((\n" +
                            "                SELECT ', ' + CAST(DesProBodSucCcSec AS VARCHAR)\n" +
                            "                FROM DescuentosProgramadosBodegas B\n" +
                            "                WHERE B.DesProgCod = d.DesProgCod\n" +
                            "                FOR XML PATH('')\n" +
                            "            ), 'XX'), ' ', '') + ',' AS BODEGAS,\n" +
                            "\n" +
                            "            -- CLIENTES\n" +
                            "            REPLACE(ISNULL((\n" +
                            "                SELECT ', ' + CAST(DesProNitSec AS VARCHAR)\n" +
                            "                FROM DescuentosProgramadosTerceros T\n" +
                            "                WHERE T.DesProgCod = d.DesProgCod\n" +
                            "                FOR XML PATH('')\n" +
                            "            ), 'XX'), ' ', '') + ',' AS CLIENTES,  " +
                            "    /* LISTAS */\n" +
                            "            REPLACE(\n" +
                            "                ISNULL(\n" +
                            "                    (\n" +
                            "                        SELECT ', ' + CAST(lp.DesProLisPreCod AS VARCHAR)\n" +
                            "                        FROM DescuentosProgramadosListaPrec lp\n" +
                            "                        WHERE lp.DesProgCod = D.DesProgCod\n" +
                            "                        FOR XML PATH('')\n" +
                            "                    ),\n" +
                            "                    'XX'\n" +
                            "                ),\n" +
                            "                ' ',\n" +
                            "                ''\n" +
                            "            ) + ',' AS LISTAS,  " +
                            "\n" +
                            "            'XX,' AS EXCLIENTES,\n" +
                            "            'XX,' AS VENDEDORES,\n" +
                            "            'XX,' AS SUCURSALES,\n" +
                            "            'XX,' AS PERFILES,\n" +
                            "\n" +
                            "            -- \uD83D\uDD25 ESCALAS DESDE OTRA TABLA\n" +
                            "            ISNULL(E.DesProEscGenCanDes, 0) AS DESOBONESCRAN,\n" +
                            "            ISNULL(E.DesProEscGenCanHas, 0) AS DESOBONESCHASTA,\n" +
                            "\n" +
                            "            'N' AS DescPro1CajMen1,\n" +
                            "            0 AS DescProDesCaj,\n" +
                            "            0 AS DescProHasCaj,\n" +
                            "            0 AS DescProDesVal,\n" +
                            "            0 AS DescProHasVal,\n" +
                            "\n" +
                            "            ISNULL(E.DESPROESCGENPRO, 0) AS DesoBonEscDesc,\n" +
                            "\n" +
                            "            DESPROGFECINI,\n" +
                            "            DESPROGFECFIN,\n" +
                            "\n" +
                            "            '' AS DescArtDes,\n" +
                            "            0 AS linea,\n" +
                            "            'TRA' AS DescPorMov\n" +
                            "\n" +
                            "        FROM DescuentosProgramados d\n" +
                            "        LEFT JOIN DescuentosProgramadosEscalasGe E\n" +
                            "            ON E.DesProgCod = d.DesProgCod\n" +
                            "\n" +
                            "        WHERE \n" +
                            "            ISNULL(DESPROGAPLESCTOT,'N') = 'S'  " +
                            "            and DesProgEst <> 'I'  " +
                            "            AND DesProgEstFoL not in ('F','L')  \n" +
                            "            AND CONVERT(date, DESPROGFECINI) <= CONVERT(date, GETDATE())\n" +
                            "            AND CONVERT(date, DESPROGFECFIN) >= CONVERT(date, GETDATE())\n" +
                            "\n" +
                            "    ) kk\n" +
                            ") Consulta\n" +
                            "ORDER BY Roww DESC;  ";









                    Log.e("descuentos :",Script);
                 classbd classbd = new classbd();
                     rsClientes = comm.executeQuery(classbd.FormatearMysql(Script));
                    BdSql.execSQL("Delete from Descuentos");

                    while (rsClientes.next()){ if(TotalFilas==0) {
                        vPrBar_Import.setMax(rsClientes.getInt("Roww"));
                        TotalFilas=rsClientes.getInt("Roww");
                        String Filas=rsClientes.getString("Roww").trim();
                    }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);


                        String InsertScript = "insert into Descuentos (DESCSEC,tipodesc,DESCAGRU,descgru,SUBGRUPOS," +
                                " FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,BODEGAS,LABORATORIO,CLASE,SECCION,MARCA,LINEART,CATEGORIA,SUBCATEGORIA, " +
                                " UNIDADNEG,CIUDADES,CLIENTES,LISTAS,EXCLIENTES," +
                                " VENDEDORES,SUCURSALES,PERFILES,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal, "
                                + "DescLinPorDesLin,DescArtDes,DesSecLin,DescPorMov,DESPROGAPLESCTOT) values ( "
                                        + rsClientes.getInt("DesoBonSec") + ",'"
                                + rsClientes.getString("tipodesc").trim() + "','"
                                + rsClientes.getString("tipodesc").trim() + "','"

                                        + rsClientes.getString("DesoBonGrupo").trim() + "','"
                                        + rsClientes.getString("SUBGRUPOS").trim() + "','"
                                        + rsClientes.getString("FAMILIAS").trim() + "','"
                                        + rsClientes.getString("ARTICULOS").trim() + "','"
                                        + rsClientes.getString("CANALES").trim() + "','"
                                        + rsClientes.getString("SUBCANALES").trim() + "','"
                                        + rsClientes.getString("TAMANOS").trim() + "','"
                                + rsClientes.getString("BODEGAS").trim() + "','"
                                + rsClientes.getString("LABORATORIO").trim() + "','"
                                + rsClientes.getString("CLASE").trim() + "','"
                                + rsClientes.getString("SECCION").trim() + "','"
                                + rsClientes.getString("MARCA").trim() + "','"
                                + rsClientes.getString("LINEART").trim() + "','"
                                + rsClientes.getString("CATEGORIA").trim() + "','"
                                + rsClientes.getString("SUBCATEGORIA").trim() + "','"

                                        + rsClientes.getString("UNIDADNEG").trim() + "','"
                                        + rsClientes.getString("CIUDADES").trim() + "','"
                                + rsClientes.getString("CLIENTES").trim() + "','"
                                + rsClientes.getString("LISTAS").trim() + "','"
                                        + rsClientes.getString("EXCLIENTES").trim() + "','"
                                        + rsClientes.getString("VENDEDORES").trim() + "','"
                                        + rsClientes.getString("SUCURSALES").trim() + "','"
                                        + rsClientes.getString("PERFILES").trim() + "',"
                                        + rsClientes.getInt("DESOBONESCRAN") + ","
                                        + rsClientes.getInt("DESOBONESCHASTA") + ",'"
                                        + rsClientes.getString("DescPro1CajMen1").trim() + "',"
                                        + rsClientes.getInt("DescProDesCaj") + ","
                                        + rsClientes.getInt("DescProHasCaj")+ ","
                                        + rsClientes.getDouble("DescProDesVal") + ","
                                        + rsClientes.getDouble("DescProHasVal") + ","
                                        + rsClientes.getDouble("DesoBonEscDesc") + ",'"
                                        + rsClientes.getString("DescArtDes").trim() + "',"
                                        + rsClientes.getInt("linea")+ ",'"
                                        + rsClientes.getString("DescPorMov")+ "', '" + rsClientes.getString("DESPROGAPLESCTOT")+ "'  )";
                        try {
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;
                            Log.e("ErrorDESv",ex.toString());

                        }
                        try {

                            // vPrBar_Clientes.setMax(TotalFilas);
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Clientes.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            Log.e("ErrorDEeSv",e.toString());
                            int hh=0;
                        }
                    }//while(rsClientes.next());

                    //   while (rsClientes.next()){

                    //   }
                } catch (Exception e) {
                    Log.e("ErrorDEeSv",e.toString());
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsClientes != null) rsClientes.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }


                ErroresGen+=Errores;
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //Resultado[0] = new int[]{Insertados,Error
                // es,TotalFilas};
                // int[] Resultado = //CargarDatos();
                CargarDescuentoDetalle();
                CargarDatos();
            }
        }).start();


        return Resultado;
    }

    public  int[] CargarDescuentoDetalle(){

       /* final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntListasPrecios);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkListasPrecios);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrListasPrecios);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_ListasPrecios);
        vPrBar_Import.setProgress(0);*/
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();
                BdSql.enableWriteAheadLogging();
                /*BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(),"MantisMovil",null,6); //new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();*/

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();

                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {
                    ConBd conbd = new ConBd();
                    conn = conbd.CargarConexion(getApplicationContext());
                    comm = conn.createStatement();
                    String Script="";

                    Script="select * from(\n" +
                            "select ROW_NUMBER() OVER(ORDER BY a.DesProgCod ASC) AS Roww,\n" +
                            "a.DesProgCod, (CAST(DesProArtSec AS VARCHAR) + CAST(DesProPreArtCod AS VARCHAR)) as DesProArtSec from DescuentosProgramadosArticulos a \n" +
                            "left join  DescuentosProgramados d on a.DesProgCod = d.DesProgCod\n" +
                            "where DESPROGAPLESCTOT = 'S'\n" +
                            ") jj order by Roww desc\n ";
                    rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from DescuentosDetalle");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            //     vPrBar_Import.setMax(rsImport.getInt("Roww"));
                            TotalFilas=rsImport.getInt("Roww");
                            String Filas=rsImport.getString("Roww").trim();

                        }
                        Vueltas+=1;
                     /*   vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);
*/
                        String InsertScript = "insert into DescuentosDetalle (DESCSEC,ArtSec) values ("
                                + rsImport.getString("DesProgCod").trim() + ",'"
                                + rsImport.getString("DesProArtSec").trim() + "')" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Log.e("Errordecdetalle",ex.toString());

                            Errores+=1;

                        }
                     /*   try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                        }*/
//*/
                    }
                } catch (Exception e) {
                    Log.e("Errordecdetalle",e.toString());
                    Errores=1;
                   /* handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });*/
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

                ErroresGen+=Errores;

                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        }).start();



        return Resultado;
    }



    public  int[] CargarCarteraFicc(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntCartera);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkCartera);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrCartera);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_Cartera);
        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();
                BdSql.enableWriteAheadLogging();

                /*BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(),"MantisMovil",null,6); //new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();*/

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;


                String bloqnota = "N";
                Cursor bloquecar = vBaseDeDatos.getReadableDatabase().rawQuery("select  ParMovBloqNot from Usuarios where Vencod ='"+ vUsuario+"'" , null);

                if (bloquecar.getCount()>0) {
                    bloquecar.moveToFirst();
                    do {
                        bloqnota = bloquecar.getString(0);
                    }while (bloquecar.moveToNext());
                }



                try {

                    ConBd conbd = new ConBd();
                     conn = conbd.CargarConexion(getApplicationContext());
                     comm = conn.createStatement();
                    conbd.Variables();
                    String mysql = conbd.Mysql;
                    String Script = "";
                    if(mysql.equalsIgnoreCase("S")){
                        Script = " select ROW_NUMBER() OVER(ORDER BY facsec ASC) AS Roww,ifnull(FacTotalImpuestos,0)-ifnull(facsaldo,0) FacAbono ,FacSec,MovNitSec,MovCliSec,MovFacSec,FacTotalImpuestos "+
                                " ,FacSaldo,FacFec,FacConPag,ifnull(FacVen,curdate()) FacVen,FacVenCod,FacPedCon,ifnull(FacMora,0) FacMora " +
                                "                            from (" +
                                "                            select ROW_NUMBER() OVER(ORDER BY facsec ASC)  facsec,ifnull(FacVenCod,'') FacVenCod,MovNitSec,ifnull(MovCliSec,0) MovCliSec,MovFacSec,ifnull((select sum(karvaltotMenDes+KarArtIva) from FacturaKardex k where k.facsec=f.facsec),0) FacTotalImpuestos," +
                                "                                    (debito-credito) FacSaldo,CAST(ifnull(FacFec,CURDATE()) as DATE) FacFec,ifnull(FacConPLA,0) FacConPag,CAST((ifnull(FacFec,CURDATE())+ifnull(FacConPLA,0)) as DATE)  FacVen,DATEDIFF((ifnull(FacFec,CURDATE())+ifnull(FacConPLA,0)),CURDATE()) FacMora,ifnull('','') FacPedCon from ( " +
                                "                            select compucsec,movnitsec,movclisec,movfacsec,sum(ifnull(movdeb,0)) debito,sum(ifnull(MovCre,0)) credito " +
                                "                            from ComprobanteDetalle cd left join Comprobante c on c.comsec=cd.comsec " +
                                "                            left join Factura on facnitsec=MovNitSec and FacCliSec=MovCliSec and MovFacSec=FacNro " +
                                "                            left join ClientesVendedores cv on facnitsec=cv.nitsec and cv.clisec=facclisec " +
                                "                            where compucsec IN(select pucsec from Puc where PucCod IN("+conbd.getCuentaCartera()+")) " +
                                "                                and comesT='A'  and cv.VenCod='"+vUsuario+"'" +
                                "                            group by compucsec,movnitsec,movclisec,movfacsec ) cartera " +
                                "                            left join Factura f on facnro=MovFacSec and facnitsec=movNitSec and FacCliSec=MovCliSec " +
                                "                            where debito-credito<>0 " +
                                "                            union " +
                                "                            select  ROW_NUMBER() OVER(ORDER BY pd.pedsec ASC)+1000  facsec,PedVenCod FacVenCod,PedNitSec MovNitSec,PedCliSec MovCliSec,PedNum MovFacSec,sum(pedkarvaltotmendes+PedArtIva) FacTotalImpuestos,sum(pedkarvaltotmendes+PedArtIva) Saldo,pedfecha FacFec,PedPla FacConPag, CAST((ifnull(pedfecha,curdate())+ifnull(PedPla,0)) as DATE) FacVen, " +
                                "                            DATEDIFF((ifnull(pedfecha,curdate())+ifnull(pedpla,0)),CURDATE()) FacMora,'' FacPedCon " +
                                "                            from PedidosDetalle pd " +
                                "                            left join Pedidos p on p.pedsec=pd.pedsec " +
                                "                            where pd.pedsec+rtrim(ltrim(CAST(peddetsec as char))) not in(select facsecrem+rtrim(ltrim(CAST(karsecrem as char))) from FacturaKardex where karsecrem is not null ) " +
                                "                            group by pd.PedSec,PedVenCod,PedNitSec,PedCliSec,PedNum,PedPla,pedfecha " +
                                "                            ) Consulta order by Roww desc  ";
                    }else{

                            Script="select ROW_NUMBER() OVER(ORDER BY facsec ASC) AS Roww,*,isnull(FacTotalImpuestos,0)-isnull(facsaldo,0) FacAbono " +
                            "from (\n" +
                            "select ROW_NUMBER() OVER(ORDER BY facsec ASC)  facsec,isnull(FacVenCod,'') FacVenCod,MovNitSec,isnull(MovCliSec,0) MovCliSec,MovFacSec,isnull((select sum(karvaltotMenDes+KarArtIva) from FACTURAkardex k where k.facsec=f.facsec),0) FacTotalImpuestos,\n" +
                            "        (debito-credito) FacSaldo,CONVERT(DATE,isnull(FacFec,Getdate())) FacFec,isnull(FacConPLA,0) FacConPag,CONVERT(DATE,(isnull(FacFec,Getdate())+isnull(FacConPLA,0))) FacVen,DATEDIFF(day,(isnull(FacFec,Getdate())+isnull(FacConPLA,0)),GETDATE())+isnull(FacConPLA,0) FacMora,isnull('','') FacPedCon from (\n" +
                            "select compucsec,movnitsec,movclisec,movfacsec,sum(isnull(movdeb,0)) debito,sum(isnull(MovCre,0)) credito \n" +
                            "from comprobantedetalle cd left join comprobante c on c.comsec=cd.comsec \n" +
                            "left join Factura on facnitsec=MovNitSec and FacCliSec=MovCliSec and MovFacSec=FacNro \n" +
                            "left join clientesvendedores cv on facnitsec=cv.nitsec and cv.clisec=facclisec \n" +
                            "where compucsec IN(select pucsec from puc where PucCod IN("+conbd.getCuentaCartera()+")) \n" +
                            "    and comesT='A'  and cv.VenCod='"+vUsuario+"'\n" +
                            "group by compucsec,movnitsec,movclisec,movfacsec ) cartera \n" +
                            "left join Factura f on facnro=MovFacSec and facnitsec=movNitSec and FacCliSec=MovCliSec \n" +
                            "where debito-credito<>0\t\t\t\t\t\n ) Consulta order by Roww desc  " ;
                          /*  "union\n" +
                            "select  ROW_NUMBER() OVER(ORDER BY pd.pedsec ASC)+1000  facsec,PedVenCod FacVenCod,PedNitSec MovNitSec,PedCliSec MovCliSec,PedNum MovFacSec,sum(pedkarvaltotmendes+PedArtIva) FacTotalImpuestos,sum(pedkarvaltotmendes+PedArtIva) Saldo,pedfecha FacFec,PedPla FacConPag,CONVERT(DATE,(isnull(pedfecha,Getdate())+isnull(PedPla,0))) FacVen,\n" +
                            "DATEDIFF(day,(isnull(pedfecha,Getdate())+isnull(pedpla,0)),GETDATE()) FacMora,'' FacPedCon\n" +
                            "from pedidosdetalle pd\n" +
                            "left join pedidos p on p.pedsec=pd.pedsec\n" +
                            "where pd.pedsec+rtrim(ltrim(str(peddetsec))) not in(select facsecrem+rtrim(ltrim(str(karsecrem))) from facturakardex where karsecrem is not null ) -- and pednitsec=\n" +
                            "group by pd.PedSec,PedVenCod,PedNitSec,PedCliSec,PedNum,PedPla,pedfecha\t\t\t\t\t\n" +
                            ") Consulta order by Roww desc  ";*/
                    }
//                    String Script="select * from(select ROW_NUMBER() OVER(ORDER BY perclicod ASC) AS Row,PerCliCod,PerClaArtCod,PerCliDetDes1 from PerfilClientesClase) jj order by Row desc";
                     rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from Cartera");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Roww"));
                            TotalFilas=rsImport.getInt("Roww");
                            String Filas=rsImport.getString("Roww").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);



                        //vPrBar_Ciudades.

                        String InsertScript ="";
                        try {

                            InsertScript = "insert into Cartera (FacSec,MovNitSec,MovCliSec,MovFacSec,FacTotalImpuestos,FacAbonos,FacSaldo,FacFec,FacConPag,FacVen,FacVenCod,FacPedCon,FacMora) values ("
                                    + rsImport.getString("FacSec").trim() + ",'"
                                    + rsImport.getString("MovNitSec").trim() + "',"
                                    + rsImport.getString("MovCliSec").trim() + ",'"
                                    + rsImport.getString("MovFacSec").trim() + "',"
                                    + rsImport.getString("FacTotalImpuestos").trim() + ","
                                    + rsImport.getString("FacAbono").trim() + ","
                                    + rsImport.getString("FacSaldo").trim() + ",'"
                                    + rsImport.getString("FacFec").trim() + "',"
                                    + rsImport.getString("FacConPag").trim() + ",'"
                                    + rsImport.getString("FacVen").trim() + "','"
                                    + rsImport.getString("FacVenCod").trim() + "','"
                                    + rsImport.getString("FacPedCon").trim() + "',"
                                    + rsImport.getString("FacMora").trim() + ")";






                            try {
                                if(bloqnota.equalsIgnoreCase("S")){

                                    if (rsImport.getDouble("FacSaldo") > 0){
                                        BdSql.execSQL(InsertScript);
                                    }

                                }else{
                                    BdSql.execSQL(InsertScript);
                                }

                                Insertados+=1;

                            }catch (Exception ex){
                                Log.e("excartet",ex.toString());
                                Errores+=1;
                                Log.e("ERROR3",ex.toString());
                            }

                            try {
                                final int finalTotalFilas = TotalFilas;
                                final int finalInsertados = Insertados;
                                final int finalErrores = Errores;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                        vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                        vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                        // vPrBar_Ciudades.setMax(TotalFilas);
                                        // vPrBar_Ciudades.setProgress(Vueltas);
                                    }
                                });
                            }catch (Exception e){
                                int hh=0;      Log.e("ecartet",e.toString());
                                Log.e("ERROR3",e.toString());
                            }

                        }catch (Exception ex){
                            //    String InsertScript ="";
                            Errores+=1;
                            Log.e("exfcartet",ex.toString());
                        }

                    }
                } catch (Exception e) {
                    Errores=1;
                    Log.e("ERROR3",e.toString());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

                Log.e("ErroresGen",String.valueOf(Errores));
                ErroresGen+=Errores;

                CargarDatos();
            }

        }).start();


        return Resultado;
    }

    public  int[] CargarTipodeClientesFicc(){

       /* final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntListasPrecios);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkListasPrecios);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrListasPrecios);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_ListasPrecios);
        vPrBar_Import.setProgress(0);*/
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();
                BdSql.enableWriteAheadLogging();

                /*BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(),"MantisMovil",null,6); //new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();*/


                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();

                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {
                    ConBd conbd = new ConBd();
                    conn = conbd.CargarConexion(getApplicationContext());
                    comm = conn.createStatement();
                    String Script="";

                    Script="select * from(select ROW_NUMBER() OVER(ORDER BY TipCliCod ASC) AS Roww,TipCliCod,TipCliNom from TipodeClientes) jj order by Roww desc";
                    rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from TipodeClientes");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            //     vPrBar_Import.setMax(rsImport.getInt("Roww"));
                            TotalFilas=rsImport.getInt("Roww");
                            String Filas=rsImport.getString("Roww").trim();

                        }
                        Vueltas+=1;
                     /*   vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);
*/
                        String InsertScript = "insert into TipodeClientes (TipCliCod,TipCliNom) values ("
                                + rsImport.getString("TipCliCod").trim() + ",'"
                                + rsImport.getString("TipCliNom").trim() + "')" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;

                        }
                     /*   try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                        }*/
//*/
                    }
                } catch (Exception e) {
                    Errores=1;
                   /* handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });*/
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

                ErroresGen+=Errores;
                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        }).start();



        return Resultado;
    }
    public  int[] CargarZonaFicc(){

       /* final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntListasPrecios);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkListasPrecios);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrListasPrecios);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_ListasPrecios);
        vPrBar_Import.setProgress(0);*/
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
               BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();
                BdSql.enableWriteAheadLogging();
                /*BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(),"MantisMovil",null,6); //new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();*/

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();

                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {
                    ConBd conbd = new ConBd();
                    conn = conbd.CargarConexion(getApplicationContext());
                    comm = conn.createStatement();
                    String Script="";

                    Script="select * from(select ROW_NUMBER() OVER(ORDER BY ZonCod ASC) AS Roww,ZonCod,ZonNom from Zona) jj order by Roww desc";
                    rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from Zona");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            //     vPrBar_Import.setMax(rsImport.getInt("Roww"));
                            TotalFilas=rsImport.getInt("Roww");
                            String Filas=rsImport.getString("Roww").trim();

                        }
                        Vueltas+=1;
                     /*   vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);
*/
                        String InsertScript = "insert into Zona (ZonCod,ZonNom) values ("
                                + rsImport.getString("ZonCod").trim() + ",'"
                                + rsImport.getString("ZonNom").trim() + "')" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;

                        }
                     /*   try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                        }*/
//*/
                    }
                } catch (Exception e) {
                    Errores=1;
                   /* handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });*/
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

                ErroresGen+=Errores;
                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        }).start();



        return Resultado;
    }
    public  int[] CargarPerfildeClientesFicc(){

       /* final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntListasPrecios);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkListasPrecios);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrListasPrecios);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_ListasPrecios);
        vPrBar_Import.setProgress(0);*/
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
               BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();
                BdSql.enableWriteAheadLogging();
                /*BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(),"MantisMovil",null,6); //new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();*/

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();

                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {
                    ConBd conbd = new ConBd();
                    conn = conbd.CargarConexion(getApplicationContext());
                    comm = conn.createStatement();
                    String Script="";

                    Script="select * from(select ROW_NUMBER() OVER(ORDER BY PerCliCod ASC) AS Roww,PerCliCod,PerCliNom from PerfilDeClientes) jj order by Roww desc";
                    rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from PerfildeClientes");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            //     vPrBar_Import.setMax(rsImport.getInt("Roww"));
                            TotalFilas=rsImport.getInt("Roww");
                            String Filas=rsImport.getString("Roww").trim();

                        }
                        Vueltas+=1;
                     /*   vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);
*/
                        String InsertScript = "insert into PerfildeClientes (PerCliCod,PerCliNom) values ("
                                + rsImport.getString("PerCliCod").trim() + ",'"
                                + rsImport.getString("PerCliNom").trim() + "')" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;

                        }
                     /*   try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                        }*/
//*/
                    }
                } catch (Exception e) {
                    Errores=1;
                   /* handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });*/
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

                ErroresGen+=Errores;
                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        }).start();



        return Resultado;
    }



    public  int[] CargarHistorialNotasFicc(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_Cnthistorialnot);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_Okhistorialnot);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_Errhistorialnot);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_historialnot);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
               BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();
                BdSql.enableWriteAheadLogging();
                /*BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(),"MantisMovil",null,6); //new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();*/

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                    conn = conbd.CargarConexion(getApplicationContext());
                    comm = conn.createStatement();

                    String vEmpresa=vGlobalVariables.getEmpresa();
                    vEmpresa=vEmpresa.toUpperCase();
                    String Script="";

                        Script= "SELECT \n" +
                                "    ROW_NUMBER() OVER (\n" +
                                "        ORDER BY t.facnitsec, t.facclisec, t.facfec DESC\n" +
                                "    ) AS Row,\n" +
                                "    t.nitide,\n" +
                                "    t.facnitsec,\n" +
                                "    t.facclisec,\n" +
                                "    FORMAT(t.facfec, 'd/M/yy') AS facfec,\n" +
                                "    t.artsec,\n" +
                                "    t.karuni,\n" +
                                "    t.karprepub,\n" +
                                "    t.artcod\n" +
                                "FROM (\n" +
                                "    SELECT \n" +
                                "        n.nitide,\n" +
                                "        f.facsec,\n" +
                                "        f.facnitsec,\n" +
                                "        f.facclisec,\n" +
                                "        f.facfec,\n" +
                                "        fk.artsec,\n" +
                                "        fk.karuni,\n" +
                                "        fk.karprepub,\n" +
                                "        a.artcod,\n" +
                                "        ROW_NUMBER() OVER (\n" +
                                "            PARTITION BY f.facnitsec, f.facclisec, fk.artsec\n" +
                                "            ORDER BY f.facfec DESC\n" +
                                "        ) AS rn\n" +
                                "    FROM articulos a\n" +
                                "    INNER JOIN facturakardex fk \n" +
                                "        ON a.artsec = fk.artsec\n" +
                                "    INNER JOIN factura f \n" +
                                "        ON fk.facsec = f.facsec\n" +
                                "    LEFT JOIN nit n \n" +
                                "        ON n.nitsec = f.facnitsec\n" +
                                "    LEFT JOIN tipos t \n" +
                                "        ON f.factipcod = t.tipcod\n" +
                                "    WHERE f.facest = 'A'\n" +
                                "      AND fuecod = 'FACT'\n" +
                                ") t\n" +
                                "WHERE t.rn <= 3\n" +
                                "ORDER BY \n" +
                                "    t.facnitsec,\n" +
                                "    t.facclisec,\n" +
                                "    t.artsec,\n" +
                                "    t.facfec DESC;";

                    rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from ClientesDevoluciones");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);


                        String InsertScript = "insert into ClientesDevoluciones (nitsec,clisec,artsec,karuni,karprepub,Fecha) values ('"
                                + rsImport.getString("facnitsec").trim() + "',"
                                + rsImport.getString("facclisec").trim() + ",'"
                                + rsImport.getString("artsec").trim() + "',"
                                + rsImport.getString("karuni").trim() + ","
                                + rsImport.getString("karprepub").trim() + ",'"
                                + rsImport.getString("facfec").trim()+"')" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Log.e("ex historial",ex.toString());
                            Errores+=1;

                        }

                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                            Log.e("e historial",e.toString());
                        }

                    }
                } catch (Exception e) {
                    Log.e("ErrorMovhisd",e.toString());
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                } /*finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }*/


                ErroresGen+=Errores;
                Log.e("errdsfaerr233or1",String.valueOf(ErroresGen));

                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        }).start();



        return Resultado;
    }


    public  int[] CargarConceptosNotasFicc(){

        final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntConceptosNotas);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkConceptosNotas);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrConceptosNotas);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_ConceptosNotas);
        vPrBar_Import.setProgress(0);
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
                BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();
                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {

                    ConBd conbd = new ConBd();
                    conn = conbd.CargarConexion(getApplicationContext());
                    comm = conn.createStatement();
                    String Script="select * from(select ROW_NUMBER() OVER(ORDER BY ConNotCod ASC) AS Row," +
                            "   ConNotCod,ConNotNoAfeInv,ConNotNom from ConceptoNC  where connotcod in ('11','13','15')  ) jj order " +
                            " by Row desc";

                    Log.e("ErrorCocnetp Script", Script);
                    rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from ConceptoNCND");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            vPrBar_Import.setMax(rsImport.getInt("Row"));
                            TotalFilas=rsImport.getInt("Row");
                            String Filas=rsImport.getString("Row").trim();

                        }
                        Vueltas+=1;
                        vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);


                        String InsertScript = "insert into ConceptoNCND(ConNotCod,ConNotNoAfeInv,ConNotNom) values ('"
                                + rsImport.getString("ConNotCod").trim() + "','"
                                + rsImport.getString("ConNotNoAfeInv").trim() + "'," +
                                " '"+rsImport.getString("ConNotNom").trim() +"' )" ;

                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;
                            Log.e("ErrorCocnetp", ex.toString());

                        }

                        try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            Log.e("ErrorCocnetp", e.toString());

                            int hh=0;
                        }

                    }
                } catch (Exception e) {
                    Log.e("ErrorCocnetp", e.toString());
                    Errores=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }



                ErroresGen+=Errores;
                Log.e("errortert341",String.valueOf(ErroresGen));

              // CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        }).start();



        return Resultado;
    }

    public  int[] CargarCategoriaClienteFicc(){

       /* final ProgressBar vPrBar_Import;
        final TextView vtxt_CntImport;
        final TextView vtxt_OkImport;
        final TextView vtxt_ErrImport;
        vtxt_CntImport= (TextView)findViewById(R.id.txt_CntListasPrecios);
        vtxt_OkImport= (TextView)findViewById(R.id.txt_OkListasPrecios);
        vtxt_ErrImport= (TextView)findViewById(R.id.txt_ErrListasPrecios);
        vPrBar_Import= (ProgressBar)findViewById(R.id.PrBar_ListasPrecios);
        vPrBar_Import.setProgress(0);*/
        int[] Resultado= new int[]{0,0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int TotalFilas=0;
                int Insertados=0;
                int Errores=0;
                int Vueltas=0;
               BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();
                BdSql.enableWriteAheadLogging();

                /*BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(),"MantisMovil",null,6); //new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();*/

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();

                Connection conn = null;
                Statement comm = null;
                ResultSet rsImport  = null;
                try {
                    ConBd conbd = new ConBd();
                    conn = conbd.CargarConexion(getApplicationContext());
                    comm = conn.createStatement();
                    String Script="";

                    Script="select * from(select ROW_NUMBER() OVER(ORDER BY CatCliCod ASC) AS Roww,CatCliCod,CatCliNom from CategoriaCliente) jj order by Roww desc";
                    rsImport = comm.executeQuery(Script);
                    BdSql.execSQL("Delete from CategoriaCliente");

                    while (rsImport.next()){
                        if(TotalFilas==0) {
                            //     vPrBar_Import.setMax(rsImport.getInt("Roww"));
                            TotalFilas=rsImport.getInt("Roww");
                            String Filas=rsImport.getString("Roww").trim();

                        }
                        Vueltas+=1;
                     /*   vPrBar_Import.setMax(TotalFilas);
                        vPrBar_Import.setProgress(Vueltas);
*/
                        String InsertScript = "insert into CategoriaCliente (CatCliCod,CatCliNom) values ("
                                + rsImport.getString("CatCliCod").trim() + ",'"
                                + rsImport.getString("CatCliNom").trim() + "')" ;
                        try {
                            if (BdSql.isDbLockedByCurrentThread()){
                                BdSql.endTransaction();
                            }
                            BdSql.execSQL(InsertScript);
                            Insertados+=1;

                        }catch (Exception ex){
                            Errores+=1;

                        }
                     /*   try {
                            final int finalTotalFilas = TotalFilas;
                            final int finalInsertados = Insertados;
                            final int finalErrores = Errores;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vtxt_CntImport.setText( String.valueOf(finalTotalFilas));
                                    vtxt_OkImport.setText(String.valueOf(finalInsertados));
                                    vtxt_ErrImport.setText(String.valueOf(finalErrores));
                                    // vPrBar_Ciudades.setMax(TotalFilas);
                                    // vPrBar_Ciudades.setProgress(Vueltas);
                                }
                            });
                        }catch (Exception e){
                            int hh=0;
                        }*/
//*/
                    }
                } catch (Exception e) {
                    Errores=1;
                   /* handler.post(new Runnable() {
                        @Override
                        public void run() {
                            vtxt_CntImport.setText("N/N");
                            vtxt_OkImport.setText("N/N");
                            vtxt_ErrImport.setText("1");
                        }
                    });*/
                }finally { // Cerramos las conexiones, en orden inverso a su apertura
                    try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
                    try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                    try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                }

                ErroresGen+=Errores;
                CargarDatos();
                // vtxt_ErrClientes.setText(+String.valueOf(Errores));
                //int[] Resultado= new int[]{Insertados,Errores,TotalFilas};

            }
        }).start();



        return Resultado;
    }

/*Fin Ficc ======================================================================================*/
    public void mensajeslargos(String tag, String Script){
        int maxLogSize = 4000;
        for (int i = 0; i <= Script.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = Math.min((i + 1) * maxLogSize, Script.length());
            Log.e(tag, Script.substring(start, end));
        }
    }




    public Boolean isOnlineNet() {

       ConBd conbd = new ConBd();
        conbd.Variables();
        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String Empresa=vGlobalVariables.getEmpresa();
        if (!Empresa.equalsIgnoreCase("IBANEZ") || !Empresa.equalsIgnoreCase("SUHOGAR")){
            return true;
        }else{
            try {
                Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 "+conbd.IpEmpresa);
                int val           = p.waitFor();
                boolean reachable = (val == 0);
                return reachable;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return false;
        }


    }

}


