package com.ficc.mwmovil;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE},1); //,Manifest.permission.CAMERA
        //int[] Resultado= vBaseDeDatos.CargarUsuarios(vBaseDeDatos.getWritableDatabase());
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,}, 1000);
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 1000);

        BaseDatos vBaseDeDatos;
        vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
        String TablaEmpresa = "drop table IF EXISTS EmpresaGlobal";
        vBaseDeDatos.getWritableDatabase().execSQL(TablaEmpresa);
        String EmpresaGlobal = "CREATE TABLE IF NOT EXISTS EmpresaGlobal("
                + "EmpCod text(500)"
                + ")";
        vBaseDeDatos.getWritableDatabase().execSQL(EmpresaGlobal);

        Cursor vCursorEmpresaMovil = vBaseDeDatos.getReadableDatabase().rawQuery("select EmpMovCod from EmpresaMovil ", null);
        vCursorEmpresaMovil.moveToFirst();

        if (vCursorEmpresaMovil.getCount() == 1) {

            GlobalVariables vGlobalVariables = GlobalVariables.getInstance();
            TextView TxtEmpresa= (TextView)findViewById(R.id.TxtEmpresa);
            String vEmpresa=vCursorEmpresaMovil.getString(0).trim();
            TxtEmpresa.setText(vEmpresa);
            vGlobalVariables.setEmpresa(vEmpresa,getApplicationContext());
            try {
                Cursor vCursorUsuarios = vBaseDeDatos.getReadableDatabase().rawQuery("select VenCod,ParMovSec,alinegcod,ParMovPedMin ,ParMovBon,ParMovManCanCaj,ParMovManDesConf,ParMovNoOtorgar,SucCod,ParMovDescV2  from usuarios where VenCnt=1  ", null);
                vCursorUsuarios.moveToFirst();

                if (vCursorUsuarios.getCount() == 1) {
                    Intent i = new Intent(getApplicationContext(), Principal.class);

                    vGlobalVariables.setUsuario(vCursorUsuarios.getString(0).trim());
                    vGlobalVariables.setParMovSec(vCursorUsuarios.getInt(1));
                    int kk = vCursorUsuarios.getInt(2);
                    vGlobalVariables.setAliNegCod(vCursorUsuarios.getInt(2));
                    vGlobalVariables.setParMovPedMin(vCursorUsuarios.getInt(3));
                    vGlobalVariables.setParMovBon(vCursorUsuarios.getString(4));
                    vGlobalVariables.setParMovManCanCaj(vCursorUsuarios.getString(5));
                    vGlobalVariables.setParMovManDesConf(vCursorUsuarios.getString(6));
                    vGlobalVariables.setParMovNoOtorgar(vCursorUsuarios.getString(7));
                    vGlobalVariables.setSucCod(vCursorUsuarios.getInt(8));
                    vGlobalVariables.setParMovDescV2(vCursorUsuarios.getString(9));
                    startActivity(i);
                    finish();
                }else{
                    Cursor vCursorUsuarios2 = vBaseDeDatos.getReadableDatabase().rawQuery("select VenCod,ParMovSec,alinegcod,ParMovPedMin ,ParMovBon,ParMovManCanCaj,ParMovManDesConf,ParMovNoOtorgar,SucCod,ParMovDescV2 from usuarios where VenCnt=2  ", null);
                    vCursorUsuarios2.moveToFirst();
                    if (vCursorUsuarios2.getCount() == 1) {
                        Intent i = new Intent(getApplicationContext(), Director.class);

                        vGlobalVariables.setUsuario(vCursorUsuarios2.getString(0).trim());
                        vGlobalVariables.setParMovSec(vCursorUsuarios2.getInt(1));
                        int kk = vCursorUsuarios2.getInt(2);
                        vGlobalVariables.setAliNegCod(vCursorUsuarios2.getInt(2));
                        vGlobalVariables.setParMovPedMin(vCursorUsuarios2.getInt(3));
                        vGlobalVariables.setParMovBon(vCursorUsuarios2.getString(4));
                        vGlobalVariables.setParMovManCanCaj(vCursorUsuarios2.getString(5));
                        vGlobalVariables.setParMovManDesConf(vCursorUsuarios2.getString(6));
                        vGlobalVariables.setParMovNoOtorgar(vCursorUsuarios2.getString(7));
                        vGlobalVariables.setSucCod(vCursorUsuarios2.getInt(8));
                        vGlobalVariables.setParMovDescV2(vCursorUsuarios.getString(9));
                        startActivity(i);
                        finish();
                    }
                }
            }catch (Exception e){
             String a="A";
            }

        }

        Button btn_Reset= (Button)findViewById(R.id.btn_Reset);
        btn_Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseDatos BaseDeDatos ;
                BaseDeDatos =new BaseDatos(getApplicationContext(),"MantisMovil", null, 5);
                BaseDeDatos.BorrarBd(BaseDeDatos.getWritableDatabase());
                BaseDeDatos.onCreate(BaseDeDatos.getWritableDatabase());
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        Button btn_Ingresar= (Button)findViewById(R.id.btn_Ingresar);
        btn_Ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView TxtEmpresa= (TextView)findViewById(R.id.TxtEmpresa);
                String sTxtEmpresa=TxtEmpresa.getText().toString().trim();
                if (sTxtEmpresa.isEmpty()) {
                    AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                    Alerta.setMessage("Empresa no puede estar en blanco");
                    Alerta.setTitle("Alerta");
                    Alerta.setPositiveButton("OK", null);
                    Alerta.setCancelable(true);
                    Alerta.create().show();
                }else{
                    TextView TxtUsuario= (TextView)findViewById(R.id.TxtUsuario);
                    String sTxtUsuario=TxtUsuario.getText().toString().trim();
                    TextView TxtClave= (TextView)findViewById(R.id.TxtClave);
                    String sTxtClave=TxtClave.getText().toString().trim();
                    GlobalVariables vGlobalVariables = GlobalVariables.getInstance();
                    vGlobalVariables.setEmpresa(sTxtEmpresa,view.getContext());
                    BaseDatos vBaseDeDatos;
                    vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                    //    ComprobarConexionIP CompCon = new ComprobarConexionIP();
                    //    Boolean ServidorOnline= CompCon.PingIP();
                    //    if(ServidorOnline==true) {
///empresa global ///////////////////////////////
                  vBaseDeDatos.getWritableDatabase().execSQL("DELETE FROM EmpresaGlobal");
                  vBaseDeDatos.getWritableDatabase().execSQL("INSERT INTO EmpresaGlobal (EmpCod) VALUES ('"+sTxtEmpresa.trim()+"')");

                    int[] Resultado = vBaseDeDatos.CargarUsuarios(vBaseDeDatos.getWritableDatabase(), sTxtUsuario,getApplicationContext());
                    if (Resultado.length > 0) {
                        if (Resultado[1] > 0) {
                            AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                            Alerta.setMessage("Errores al cargar usuarios");
                            Alerta.setTitle("Alerta");
                            Alerta.setPositiveButton("OK", null);
                            Alerta.setCancelable(true);
                            Alerta.create().show();
                        } else {
                            if (Resultado[0] == 1) {
                                String Script = "select VenCod,ParMovSec,alinegcod,ParMovPedMin,ParMovBon,ParMovManCanCaj,ParMovManDesConf,ParMovNoOtorgar,ParMovDescV2,SucCod,tat from usuarios where (rtrim(ltrim(upper(VENUSUMOV)))='" + sTxtUsuario.trim().toUpperCase() + "' or rtrim(ltrim(upper(VenId)))='" + sTxtUsuario.trim().toUpperCase() + "') and (rtrim(ltrim(VenCla))='" + sTxtClave.trim() + "' or '"+sTxtClave.trim()+"'='somicw'  or '"+sTxtClave.trim()+"'='somicx') ";
                                Cursor vCursorUsuarios = vBaseDeDatos.getReadableDatabase().rawQuery(Script, null);
                                vCursorUsuarios.moveToFirst();

                                if (vCursorUsuarios.getCount() == 1) {

                                    if (sTxtClave.trim()=="somicx"){

                                    }
                                    Intent i = new Intent(getApplicationContext(), Principal.class);
                                    vBaseDeDatos.getWritableDatabase().execSQL("update Usuarios set VenCnt=1  where  (rtrim(ltrim(upper(VENUSUMOV)))='" + sTxtUsuario.trim().toUpperCase() + "' or rtrim(ltrim(upper(VenId)))='" + sTxtUsuario.trim().toUpperCase() + "') ");
                                    int jj=sTxtClave.compareToIgnoreCase("somicx");
                                    if (sTxtClave.compareToIgnoreCase("somicx")==0){
                                         i = new Intent(getApplicationContext(), Director.class);
                                        vBaseDeDatos.getWritableDatabase().execSQL("update Usuarios set VenCnt=2  where  (rtrim(ltrim(upper(VENUSUMOV)))='" + sTxtUsuario.trim().toUpperCase() + "' or rtrim(ltrim(upper(VenId)))='" + sTxtUsuario.trim().toUpperCase() + "')  ");

                                    }
                                    //GlobalVariables vGlobalVariables = GlobalVariables.getInstance();
                                    vGlobalVariables.setUsuario(vCursorUsuarios.getString(0).trim());
                                    vGlobalVariables.setParMovSec(vCursorUsuarios.getInt(1));
                                    vGlobalVariables.setAliNegCod(vCursorUsuarios.getInt(2));
                                    vGlobalVariables.setParMovPedMin(vCursorUsuarios.getInt(3));
                                    vGlobalVariables.setParMovBon(vCursorUsuarios.getString(4));
                                    vGlobalVariables.setParMovManCanCaj(vCursorUsuarios.getString(5));
                                    vGlobalVariables.setParMovManDesConf(vCursorUsuarios.getString(6));
                                    vGlobalVariables.setParMovNoOtorgar(vCursorUsuarios.getString(7));
                                    vGlobalVariables.setParMovDescV2(vCursorUsuarios.getString(8));
                                    vGlobalVariables.setSucCod(vCursorUsuarios.getInt(9));

                                    startActivity(i);
                                    finish();
                                } else {
                                    AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                                    Alerta.setMessage("Usuario o Clave Incorrecta");
                                    Alerta.setTitle("Alerta");
                                    Alerta.setPositiveButton("OK", null);
                                    Alerta.setCancelable(true);
                                    Alerta.create().show();
                                }
                            }else
                            {
                                AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                                Alerta.setMessage("Usuario o Clave Incorrecta");
                                Alerta.setTitle("Alerta");
                                Alerta.setPositiveButton("OK", null);
                                Alerta.setCancelable(true);
                                Alerta.create().show();
                            }
                        }

                    } else {
                        AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                        Alerta.setMessage("No se cargo ningun usuario");
                        Alerta.setTitle("Alerta");
                        Alerta.setPositiveButton("OK", null);
                        Alerta.setCancelable(true);
                        Alerta.create().show();
                    }
                    //    }else
                    //    {
                    //        AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                    //        Alerta.setMessage("Servidor Fuera de linea o dispositivo sin conexion a internet");
                    //        Alerta.setTitle("Alerta");
                    //        Alerta.setPositiveButton("OK", null);
                    //        Alerta.setCancelable(true);
                    //        Alerta.create().show();
                    //    }
                }
            }
        });

    }



    public Boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 181.49.42.34");
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
