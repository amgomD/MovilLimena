package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class ConfirmarRecibo extends AppCompatActivity {

    private Handler handler ;
    Bundle Extras;
    Button btncorreo,btnenvioalt;
    ProgressBar spinner;
    TextView codigo,pedidos,total;
    EditText correo ;
    String pcodigo = "";
    String scodigo = "";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_recibo);
        getSupportActionBar().hide();
        getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.TrasparenteGris));
        Extras=this.getIntent().getExtras();
        String NitSec = Extras.getString("nitsec");
        int clisec = Extras.getInt("clisec");
        int modo = Extras.getInt("modo");
        Time time = new Time();
        time.setToNow();
        final GestorPedidos GestorPedidos = new GestorPedidos();

        spinner = findViewById(R.id.spinner);
        spinner.setVisibility(View.GONE);
        handler = new Handler();
        btnenvioalt = findViewById(R.id.btnenviorec);
        btncorreo = findViewById(R.id.btncorreo);
        correo = findViewById(R.id.correo);

       if(modo == 1){
           btnenvioalt.setVisibility(View.VISIBLE);
           btncorreo.setVisibility(View.GONE);
           correo.setVisibility(View.GONE);
       }else{
           btnenvioalt.setVisibility(View.GONE);
           btncorreo.setVisibility(View.VISIBLE);
           correo.setVisibility(View.VISIBLE);
       }


        btnenvioalt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnenvioalt.setVisibility(View.GONE);
                spinner.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String Mensaje ="";
                                 Mensaje = GestorPedidos.EnviarRecibos(ConfirmarRecibo.this,"",NitSec,clisec);
                                 Log.e("Mensaje", Mensaje);
                                if(Mensaje.length() > 0){

                                    Intent intent = new Intent();
                                    intent.putExtra("Mensaje", Mensaje);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }


                            }
                            catch (Exception e){
                                Log.e("errorend",e.toString());
                                Intent intent = new Intent();
                                intent.putExtra("Mensaje", e.toString());
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }

                    }).start();

            }
        });
        btncorreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btncorreo.setVisibility(View.GONE);
                spinner.setVisibility(View.VISIBLE);

                new Thread(new Runnable() {
                    @Override
                    public void run() {


                String NumPedido = "REC"+NitSec+clisec+time.year +"-"+ (time.month + 1) +"-"+ time.monthDay;
                String jsondet = "{\"PedPenPedido\":\""+NumPedido+"\",\"Email\":\""+correo.getText().toString().trim()+"\",\"NitSec\":\""+NitSec+"\",\"cliSec\":\""+clisec+"\"}";
                Log.e("jsoncorreo",jsondet);

                //String sql = "http://181.49.42.34:8086/Pruebas/rest/wsEnviarCorreoRec";
                ConBd conbd = new ConBd();
                        conbd.Variables();
               String sql = conbd.UrlEnvioReciboscorreo;


                String sExistencia = "0.0";
                String sExistencia2 = "0.0";
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
                    result.append(jsondet);
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
                    writer.write(result.toString());
                    writer.flush();
                    writer.close();
                    os.close();
                    conn.connect();
                    int statusCode = conn.getResponseCode();
                    InputStream inputstream = null;
                    if (statusCode >= 200 && statusCode < 400) {
                        // Create an InputStream in order to extract the response object
                        sExistencia2 = "Envio exitoso";
                        Log.e("Envio exitoso",sExistencia2);
                        inputstream = conn.getInputStream();
                    } else {
                        inputstream = conn.getErrorStream();
                    }

                    BufferedReader in = new BufferedReader(new InputStreamReader(inputstream));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    String json = "";

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    json = "[" + response.toString() + "]";
                    JSONArray jsonArr = null;
                    jsonArr = new JSONArray(json);
                    String mensaje ="";
                    for (int i = 0; i < jsonArr.length(); i++) {
                        JSONObject jsonObject = jsonArr.getJSONObject(i);
                        pcodigo = jsonObject.optString("pcodigo");
                        scodigo = jsonObject.optString("scodigo");
                        mensaje = jsonObject.optString("Lineam");

                    }


                    if(pcodigo.equalsIgnoreCase("200")){
                        sExistencia2 = "Enviado al correo principal \n";
                    }else{

                        sExistencia2 = "Error al enviar en el correo principal \n";
                    }

                    if(scodigo.equalsIgnoreCase("200")){
                        sExistencia2 += "Enviado al correo adicional \n";
                    }else{
                        sExistencia2 += "Error al enviar en el correo adicional \n";
                    }
                    if(pcodigo.equalsIgnoreCase("0") && scodigo.equalsIgnoreCase("0")){
                        sExistencia2 = mensaje;
                    }


                    Intent intent = new Intent();
                    intent.putExtra("Mensaje", sExistencia2);
                    setResult(RESULT_OK, intent);
                    finish();




                } catch (MalformedURLException e) {
                    sExistencia2 +=  e.getMessage();
                    //Log.e("sExistencia2  ",sExistencia2);
                    e.printStackTrace();
                    Intent intent = new Intent();
                    intent.putExtra("Mensaje", sExistencia2);
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (IOException e) {
                    sExistencia2 +=  e.getMessage();
                    Intent intent = new Intent();
                    intent.putExtra("Mensaje", sExistencia2);
                    setResult(RESULT_OK, intent);
                    finish();
                    e.printStackTrace();
                } catch (JSONException e) {
                    sExistencia2 +=  e.getMessage();
                    Intent intent = new Intent();
                    intent.putExtra("Mensaje", sExistencia2);
                    setResult(RESULT_OK, intent);
                    finish();
                    e.printStackTrace();
                }

                    }

                }).start();




               /* AlertDialog.Builder Alerta = new AlertDialog.Builder(ConfirmarRecibo.this);
                Alerta.setMessage(sExistencia2);
                Alerta.setTitle("Notificacion");
                Alerta.setPositiveButton("OK",null);
                Alerta.setCancelable(true);
                Alerta.create().show();
*/








            }
        });





    }

}