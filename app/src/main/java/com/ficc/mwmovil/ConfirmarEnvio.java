package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ficc.mwmovil.databinding.ActivityConfirmarEnvioBinding;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConfirmarEnvio extends AppCompatActivity {

    private Handler handler ;
    Bundle Extras;
    Button btncargue,btnenvioalt;
    ProgressBar spinner;
    String txtvalor = "";
    TextView codigo,pedidos,total;
    int dayOfMonth =  AppGlobals.dayOfMonth; // Extras.getInt("dia");
    int month = AppGlobals.month;//Extras.getInt("mes");
    int year = AppGlobals.year;//Extras.getInt("ano");
    String NitSec;
    String ArtSec;
    String prefijo,MensajeBloqueo;

    int envioalt ;
    int position ;
    int clisec ;
    int tpedidos;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_envio);
        getSupportActionBar().hide();
        getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.TrasparenteGris));
        Extras=this.getIntent().getExtras();

        if (Extras != null) {
         NitSec = Extras.getString("nitsec");
        ArtSec =  Extras.getString("artsec");
        prefijo =  Extras.getString("prefijo");
        clisec = Extras.getInt("clisec");
         envioalt =  Extras.getInt("envioalt",0);
         position = Extras.getInt("position",0);
         tpedidos = Extras.getInt("total");

        } else {
            NitSec = "";
            ArtSec = "";
            prefijo = "";
            clisec = 0;
            envioalt = 0;
            position = 0;
            tpedidos = 0;
        }




        pedidos = findViewById(R.id.pedidos);
        total = findViewById(R.id.total);
        total.setText(String.valueOf(tpedidos));
        spinner = findViewById(R.id.spinner);
        handler = new Handler();
        btncargue = findViewById(R.id.btncargue);
        btnenvioalt = findViewById(R.id.btnenvalt);
        final GestorPedidos GestorPedidos = new GestorPedidos();

        if(envioalt==0){
            btnenvioalt.setVisibility(View.GONE);
        }else{
            btncargue.setVisibility(View.GONE);
        }





        btnenvioalt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnenvioalt.setVisibility(View.GONE);
                spinner.setVisibility(View.VISIBLE);



                new Thread(new Runnable() {
                    @Override
                    public void run() {


                        final BaseDatos BaseDeDatos;
                        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);

                        String Consulta = "select prefijo,nitsec,clisec,ifnull(ValEnvAlt,'0.00') ValEnvAlt ,ifnull(ValEnviado,'0.00') ValEnviado from pedido where 1=1  ";
                        if(NitSec.isEmpty()){

                        }else{
                            Consulta+=" and nitsec='"+NitSec+"'";
                        }
                        if(clisec > 0 ){
                            Consulta+=" and clisec = "+clisec+" ";
                        }
                        if(prefijo.isEmpty()) {
                        }else{
                            Consulta+=" and prefijo='"+prefijo+"' ";
                        }
                        Consulta +=" group by prefijo,nitsec,clisec";

                        try {
                            Cursor Clientes = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null);
                        Clientes = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null);
                        String Mensaje  = " ";
                        String valorenv="";
                        int vuelta = 0;



                            try {
                        if (Clientes.moveToFirst()) {
                            Clientes.moveToFirst();
                            do {
                                Double total = GestorPedidos.TotalesPedido(getApplicationContext(),Clientes.getString(0),Clientes.getString(1),
                                        Clientes.getInt(2),"","","").Total;

                                Double sExistencia = Double.valueOf(Clientes.getString(4));
                                Double nvalorenv = Double.valueOf(Clientes.getString(3));

                                int nclisec  = Clientes.getInt(2);
                                String nNitSec = Clientes.getString(1);

                                String nPrefijo = Clientes.getString(0);


                                Log.e("nNitSec    :", String.valueOf(nNitSec));
                                Log.e("nclisec--- :", String.valueOf(nclisec));
                                Log.e("nPrefijo--- :", String.valueOf(nPrefijo));



                                if(isOnlineNet()) {
                                    Mensaje = GestorPedidos.EnviarPedidosFiccGx5(getApplicationContext(), nPrefijo, nNitSec, nclisec, 0);
                                }else{
                                    Mensaje = "Error de conexion, compruebe su internet";

                                }
                                spinner.setMax(Clientes.getCount());
                                vuelta += 1;
                                spinner.setProgress(vuelta);
                                try {
                                    final int finaltpedidos = tpedidos;
                                    final int finalMensaje = vuelta;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            pedidos.setText(String.valueOf(finalMensaje));
                                            if(finalMensaje == tpedidos){
                                                spinner.setVisibility(View.GONE);
                                                btnenvioalt.setVisibility(View.VISIBLE);

                                            }else{
                                                spinner.setVisibility(View.VISIBLE);
                                                btnenvioalt.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                } catch (Exception e) {
                                    Log.e("Escalt",e.toString());
                                    int hh = 0;
                                }



                            } while (Clientes.moveToNext());
                        }
                            } finally {
                                Clientes.close();
                            }







                        if(Mensaje.length() > 0){

                            Intent intent = new Intent();
                            intent.putExtra("keyName", "200");
                            intent.putExtra("Mensaje", Mensaje);
                            intent.putExtra("position", position);
                            setResult(RESULT_OK, intent);
                            finish();

                        }else{
                        }







                        } catch (Exception e) {
                            Log.e("SQL_CONFIRMAR", "Error SQL", e);
                        }


                    }
                }).start();
            }
        });


        btncargue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btncargue.setVisibility(View.GONE);
                spinner.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        GlobalVariables vGlobalVariables = GlobalVariables.getInstance();
                        String vEmpresa = vGlobalVariables.getEmpresa();
                        vEmpresa = vEmpresa.toUpperCase();
                        String vUsuario = vGlobalVariables.getUsuario();
                        Time time = new Time();
                        time.setToNow();



                        int enviar = 1;


                        final BaseDatos BaseDeDatos;
                        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);

                        String Consulta = "select prefijo,nitsec,clisec,ifnull(ValEnviado,'0.00') ValEnviado from pedido where clisec > 0 ";
                        if (NitSec.isEmpty()) {

                        } else {
                            Consulta += " and nitsec='" + NitSec + "'";
                            enviar = 0;
                        }
                        if (clisec > 0) {
                            Consulta += " and clisec = " + clisec + " ";
                        }
                        if (ArtSec.isEmpty()) {
                        } else {
                            Consulta += " and artsec='" + ArtSec + "'";
                        }

                        if (prefijo.isEmpty()) {

                        } else {
                            Consulta += " and prefijo='" + prefijo + "'";
                        }

                        Consulta += " group by prefijo,nitsec,clisec";

                        try {
                            Cursor Clientes = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null);
                        Clientes = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null);
                        String Mensaje = "";
                        String tValorEnv = "0.00";
                        String estado = "200";
                        int vuelta = 0;
                            try {
                                if (Clientes.moveToFirst()) {

                                        Clientes.moveToFirst();
                                        do {
                                            Double total = GestorPedidos.TotalesPedido(getApplicationContext(), Clientes.getString(0), Clientes.getString(1), Clientes.getInt(2), "", "", "").Total;
                                            Double valorenv = Double.valueOf(Clientes.getString(3));
                                            txtvalor = Clientes.getString(3);

                                            int nclisec = Clientes.getInt(2);
                                            String nNitSec = Clientes.getString(1);
                                            String nPrefijo = Clientes.getString(0);

                                            int dife = valorenv.intValue() - total.intValue();


                                            String Empresa = vGlobalVariables.getEmpresa();

                                            //if(Empresa.equalsIgnoreCase("MENTAHAIR") || Empresa.equalsIgnoreCase("MENTAHAIRCOT") ){
                                            Log.e("Exception  e", "dssdfennddd");

                                            if (isOnlineNet()) {
                                                Log.e("Exception  e", "ennddd");
                                                Mensaje += GestorPedidos.EnviarPedidosFiccGx5(getApplicationContext(), nPrefijo, nNitSec, nclisec, 0);
                                                //Mensaje = "0.00";

                                            } else {
                                                estado = "Error de conexion";

                                            }

                                            spinner.setMax(Clientes.getCount());
                                            vuelta += 1;
                                            spinner.setProgress(vuelta);


                                            try {
                                                final int finaltpedidos = tpedidos;
                                                final int finalMensaje = vuelta;
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        pedidos.setText(String.valueOf(finalMensaje));
                                                        if (finalMensaje == tpedidos) {
                                                            spinner.setVisibility(View.GONE);
                                                            btncargue.setVisibility(View.VISIBLE);

                                                        } else {
                                                            spinner.setVisibility(View.VISIBLE);
                                                            btncargue.setVisibility(View.GONE);
                                                        }
                                                    }
                                                });
                                            } catch (Exception e) {
                                                int hh = 0;
                                                Log.e("Exception  e", e.toString());
                                            }
                                        } while (Clientes.moveToNext());
                                    }


                                } finally {
                                    Clientes.close();
                                }



                        if (Mensaje.contains("Generado")) {
                            Log.e("tValorEnv", tValorEnv);
                            Intent intent = new Intent();
                            intent.putExtra("keyName", Mensaje);
                            intent.putExtra("valorenv", tValorEnv);
                            intent.putExtra("position", position);
                            setResult(RESULT_OK, intent);
                            finish();

                        } else {
                            Log.e("tValorEnv", tValorEnv);
                            Intent intent = new Intent();
                            intent.putExtra("keyName", "Sin pedidos");
                            intent.putExtra("valorenv", "0");
                            intent.putExtra("position", position);
                            setResult(RESULT_OK, intent);
                            finish();
                        }





                        } catch (Exception e) {
                            Log.e("SQL_CONFIRMAR", "Error SQL", e);
                        }

                    }
                }).start();

            }
        });




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







    public Boolean isOnlineNet() {

        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String Empresa=vGlobalVariables.getEmpresa();
        if (!Empresa.equalsIgnoreCase("IBANEZ") || !Empresa.equalsIgnoreCase("SUHOGAR") ){
            return true;
        }else{
            ConBd conbd = new ConBd();
            conbd.Variables();
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