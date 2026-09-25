package com.ficc.mwmovil;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

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
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ClientesNuevos extends AppCompatActivity {
    ConBd conbd = new ConBd();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes_nuevos);
        getSupportActionBar().hide();
        final GestorPedidos GestorPedidos = new GestorPedidos();

        conbd.Variables();

        Button btn_crearclientenuevo= (Button)findViewById(R.id.btn_crearclientenuevo);
        btn_crearclientenuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(getApplicationContext(), CreacionCliente.class);
                    startActivity(i);
                }catch (Exception e){
                    Integer Error=0;
                }
            }
        });

        Button btn_EnviarClientes= (Button)findViewById(R.id.btn_EnviarClientes);
        btn_EnviarClientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final GestorPedidos GestorPedidos = new GestorPedidos();
                ConBd conBd = new ConBd();
                conBd.Variables();
                String mantisficc = conBd.MantisFicc;
                String Mensaje = "";
                if(mantisficc.equalsIgnoreCase("S")){
                    Mensaje=GestorPedidos.EnviarClientesFicc(view.getContext());
                }else{
                    GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                    String vUsuario=vGlobalVariables.getUsuario();
                    String vEmpresa = vGlobalVariables.getEmpresa();
                    if(vEmpresa.equalsIgnoreCase("IBANEZ") || vEmpresa.equalsIgnoreCase("SURTIMARCAS") || vEmpresa.equalsIgnoreCase("IBANEZPRU") ){
                        Mensaje=GestorPedidos.EnviarClientesJson(view.getContext());
                    }else{
                        Mensaje=GestorPedidos.EnviarClientes(view.getContext());
                    }

                }


                AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                Alerta.setMessage(Mensaje);
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
            }
        });

        Connection conn = null;
        Statement comm = null;
        ResultSet rsImport  = null;
        try {
            Time time = new Time();
            time.setToNow();

            //String nitsec=Extras.getString("nitsec");
            //Integer clisec=Extras.getInt("clisec");
            //String invgrucod=Extras.getString("invgrucod");

            ConBd conbd = new ConBd();
            conbd.Variables();
            String Mantis = conbd.MantisFicc;
             conn = conbd.CargarConexion(getApplicationContext());
             comm = conn.createStatement();

            final BaseDatos BaseDeDatos;
            BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
            //Cursor cursor = BaseDeDatos.getWritableDatabase().rawQuery("select NitSec,CliSec,NitCom,CliNom from clientes where nitsec in( " +
            //      "select nitsec from pedido where pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and cant<>0) ", null); //order by nombre

            Cursor cursor = BaseDeDatos.getWritableDatabase().rawQuery("select nit,nombre,ifnull(Enviado,'N') Enviado from prospecto p  ", null); //order by nombre

            final String[] InvGruCod;

            SDTClientesNuevos[] vSDTClientesNuevos = new SDTClientesNuevos[cursor.getCount()];
            if (cursor.getCount() > 0) {
                int vuelta = 0;
                cursor.moveToFirst();
                do {
                    //String NumPed = cursor.getString(4) + "-" + time.year + "-" + (time.month + 1) + "-" + time.monthDay + "-" + cursor.getString(0) + "-" + cursor.getInt(1);
                    SDTClientesNuevos vSDTClientesNuevosItem = new SDTClientesNuevos();
                    vSDTClientesNuevosItem.NombreCliente = cursor.getString(1);
                    vSDTClientesNuevosItem.Nit = cursor.getString(0);
                    vSDTClientesNuevosItem.sEnviado = cursor.getString(2);
                    vSDTClientesNuevosItem.sCreado = "";




                    if(Mantis.equalsIgnoreCase("S")){
                        vSDTClientesNuevosItem.sEnviado = cursor.getString(2);
                        try {
                             /*rsImport = comm.executeQuery("SELECT isnull((select NitSec from nit where nitide=cliTemNitide),'') NitSec FROM ClienteTemporal WHERE cliTemNitide='" + cursor.getString(0) + "'");

                            while (rsImport.next()) {
                                String NitSec=rsImport.getString("NitSec").trim();
                                if(rsImport.getString("NitSec").trim().length()>0){
                                    vSDTClientesNuevosItem.sCreado = "1.00";
                                    actualizarNitSec(cursor.getString(0),NitSec);
                                }else{
                                    vSDTClientesNuevosItem.sCreado = "0.00";
                                }



                            }*/
                        }
                        catch (Exception e){
                            Log.e("Error listac",e.toString());
                            //vSDTClientesNuevosItem.Respuesta = "Error de comunicacion con el servidor (servidor no disponible)";
                        }
                    }else{

                        try {



                            String[] resultado = EstadoCliente(cursor.getString(0));

                            vSDTClientesNuevosItem.sEnviado = resultado[0];
                            vSDTClientesNuevosItem.sCreado = resultado[1];

                            /* rsImport = comm.executeQuery("SELECT isnull(ProsCliRes,'') ProsCliRes,isnull((select nitide from nit where nitide=ProsCliNit),'') NitIde FROM ProspectoCliente WHERE ProsCliNit='" + cursor.getString(0) + "'");

                            while (rsImport.next()) {
                                vSDTClientesNuevosItem.Respuesta = rsImport.getString("ProsCliRes").trim();
                                vSDTClientesNuevosItem.sEnviado = "1.00";
                                String nn=rsImport.getString("NitIde").trim();
                                nn=nn;
                                if(rsImport.getString("NitIde").trim().length()>0){
                                    vSDTClientesNuevosItem.sCreado = "1.00";
                                }else{
                                    vSDTClientesNuevosItem.sCreado = "0.00";
                                }
                            }*/
                        }
                        catch (Exception e){
                            Log.e("Erorrr",e.toString());
                            //vSDTClientesNuevosItem.Respuesta = "Error de comunicacion con el servidor (servidor no disponible)";
                        }

                    }



                            // vSDTClientesNuevosItem.sEnviado = GestorPedidos.PeidoEnviado(getApplicationContext(), cursor.getString(4), cursor.getString(0), cursor.getInt(1));
                    //SDTPedidosEnviadosItem.Enviado = cursor.getDouble(2);
                    vSDTClientesNuevos[vuelta] = vSDTClientesNuevosItem;
                    vuelta = vuelta + 1;
                } while (cursor.moveToNext());
            }

            final ListView listview_clientesnuevos = (ListView) findViewById(R.id.listview_clientesnuevos);
            ListViewAdapterClientesNuevos vListViewAdapterClientesNuevos = new ListViewAdapterClientesNuevos(this, vSDTClientesNuevos);
            listview_clientesnuevos.setAdapter(vListViewAdapterClientesNuevos);



        }catch (Exception e){
            int hh=0;
        }finally { // Cerramos las conexiones, en orden inverso a su apertura
            try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
            try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
        }

    }

    private void actualizarNitSec(String Nitide,String NitSec) {

        ConBd conbd = new ConBd();
        conbd.Variables();
        conbd.Variables();
        String Mantis = conbd.MantisFicc;

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);

        String consulta = "Update Clientes set nitsec = '"+NitSec+"' where nitsec = '"+Nitide+"' ";
        BaseDeDatos.getWritableDatabase().execSQL(consulta);

         consulta = "Update pedido set nitsec = '"+NitSec+"' where nitsec = '"+Nitide+"' ";
        BaseDeDatos.getWritableDatabase().execSQL(consulta);

    }


    @Override
    protected void onResume() {
        super.onResume();


        Connection conn = null;
        Statement comm = null;
        ResultSet rsImport  = null;
        try {
            Time time = new Time();
            time.setToNow();

            //String nitsec=Extras.getString("nitsec");
            //Integer clisec=Extras.getInt("clisec");
            //String invgrucod=Extras.getString("invgrucod");

            ConBd conbd = new ConBd();
            conbd.Variables();
            String Mantis = conbd.MantisFicc;
             conn = conbd.CargarConexion(getApplicationContext());
             comm = conn.createStatement();

            final BaseDatos BaseDeDatos;
            BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
            //Cursor cursor = BaseDeDatos.getWritableDatabase().rawQuery("select NitSec,CliSec,NitCom,CliNom from clientes where nitsec in( " +
            //      "select nitsec from pedido where pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and cant<>0) ", null); //order by nombre

            Cursor cursor = BaseDeDatos.getWritableDatabase().rawQuery("select nit,nombre,ifnull(Enviado,'N') Enviado from prospecto p  ", null); //order by nombre

            final String[] InvGruCod;

            SDTClientesNuevos[] vSDTClientesNuevos = new SDTClientesNuevos[cursor.getCount()];
            if (cursor.getCount() > 0) {
                int vuelta = 0;
                cursor.moveToFirst();
                do {
                    //String NumPed = cursor.getString(4) + "-" + time.year + "-" + (time.month + 1) + "-" + time.monthDay + "-" + cursor.getString(0) + "-" + cursor.getInt(1);
                    SDTClientesNuevos vSDTClientesNuevosItem = new SDTClientesNuevos();
                    vSDTClientesNuevosItem.NombreCliente = cursor.getString(1);
                    vSDTClientesNuevosItem.Nit = cursor.getString(0);
                    vSDTClientesNuevosItem.sEnviado = "0.00";
                    vSDTClientesNuevosItem.sCreado = "";
                    vSDTClientesNuevosItem.sEnviado = cursor.getString(2);
                    if(Mantis.equalsIgnoreCase("S")){

                        try {
                            /* rsImport = comm.executeQuery("SELECT isnull((select NitSec from nit where nitide=cliTemNitide),'') NitSec, isnull(CliTemRecha,'N') CliTemRecha FROM ClienteTemporal WHERE cliTemNitide='" + cursor.getString(0) + "'");

                            while (rsImport.next()) {
                                String rechazo = rsImport.getString("CliTemRecha");
                                String NitSec=rsImport.getString("NitSec").trim();
                                if(rsImport.getString("NitSec").trim().length()>0){
                                    vSDTClientesNuevosItem.sCreado = "1.00";
                                    actualizarNitSec(cursor.getString(0),NitSec);
                                }else{
                                    vSDTClientesNuevosItem.sCreado = rechazo;
                                }
                            }*/
                        }
                        catch (Exception e){
                            Log.e("Error listac",e.toString());
                           // vSDTClientesNuevosItem.Respuesta = "Error de comunicacion con el servidor (servidor no disponible)";
                        }
                    }else{
                        try {

                            String[] resultado = EstadoCliente(cursor.getString(0));

                            vSDTClientesNuevosItem.sEnviado = resultado[0];
                            vSDTClientesNuevosItem.sCreado = resultado[1];
                          /*   rsImport = comm.executeQuery("SELECT isnull(ProsCliRes,'') ProsCliRes,isnull((select nitide from nit where nitide=ProsCliNit),'') NitIde FROM ProspectoCliente WHERE ProsCliNit='" + cursor.getString(0) + "'");

                            while (rsImport.next()) {
                                vSDTClientesNuevosItem.Respuesta = rsImport.getString("ProsCliRes").trim();
                                vSDTClientesNuevosItem.sEnviado = "1.00";
                                String nn=rsImport.getString("NitIde").trim();
                                nn=nn;
                                if(rsImport.getString("NitIde").trim().length()>0){
                                    vSDTClientesNuevosItem.sCreado = "1.00";
                                }else{
                                    vSDTClientesNuevosItem.sCreado = "0.00";
                                }
                            }*/
                        }
                        catch (Exception e){
                         //   vSDTClientesNuevosItem.Respuesta = "Error de comunicacion con el servidor (servidor no disponible)";
                        }

                    }




                    // vSDTClientesNuevosItem.sEnviado = GestorPedidos.PeidoEnviado(getApplicationContext(), cursor.getString(4), cursor.getString(0), cursor.getInt(1));
                    //SDTPedidosEnviadosItem.Enviado = cursor.getDouble(2);
                    vSDTClientesNuevos[vuelta] = vSDTClientesNuevosItem;
                    vuelta = vuelta + 1;
                } while (cursor.moveToNext());
            }

            final ListView listview_clientesnuevos = (ListView) findViewById(R.id.listview_clientesnuevos);
            ListViewAdapterClientesNuevos vListViewAdapterClientesNuevos = new ListViewAdapterClientesNuevos(this, vSDTClientesNuevos);
            listview_clientesnuevos.setAdapter(vListViewAdapterClientesNuevos);



        }catch (Exception e){
            int hh=0;
        }finally { // Cerramos las conexiones, en orden inverso a su apertura
            try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
            try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
        }

    }

    public String[] EstadoCliente(String Nit){
        String Enviado = "N";
        String Creado = "N";
         BaseDatos BaseDeDatos =  new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);

        String sql = conbd.UrlrevCliente;
        URL url = null;
        HttpURLConnection conn;
        String Mensaje = "";
        try {
            Log.e("sqlsqlsqlsqlsql ",sql);
            url = new URL(sql);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");//; utf-8
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            StringBuilder result = new StringBuilder();
            result.append("{\"Nit\":\""+Nit+"\"}");
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

            JSONObject xjson = new JSONObject(response.toString());

             Creado = xjson.getString("Creado");
             Enviado = xjson.getString("Enviado");
            String consulta = "update prospecto set Enviado = '"+Enviado+"'  where nit ='"+Nit+"' ";
            BaseDeDatos.getWritableDatabase().execSQL(consulta);

        } catch (MalformedURLException e) {
            Mensaje = e.getMessage();
            Log.e("Catchmalurl ",e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Mensaje +=  e.getMessage();
            e.printStackTrace();
            Log.e("IOException ",e.toString());

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        return new String[]{Enviado, Creado};
    }

}
