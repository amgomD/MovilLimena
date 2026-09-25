package com.ficc.mwmovil;

import static com.ficc.mwmovil.VersionResponse.consultarVersion;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itextpdf.kernel.geom.Line;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Principal extends AppCompatActivity {
    String vEmpresa ="";
    ImageView actualizcion ;
    VersionResponse resp;
    private boolean cancelarDescarga = false;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wp_homescreen);
        getSupportActionBar().hide();
        GlobalVariables vGlobalVariables2= GlobalVariables.getInstance();
        Time time = new Time();
        time.setToNow();
        ConBd conbd = new ConBd();
        conbd.Variables();
        BaseDatos vBaseDeDatos;
        AppGlobals.year = time.year;
        AppGlobals.dayOfMonth =(time.monthDay) ;
        AppGlobals.month = (time.month);

         String nEmpresa = "";
        vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);



       /* String Script = "select empcod from empresaglobal";
        Cursor vCursorempresa = vBaseDeDatos.getReadableDatabase().rawQuery(Script, null);
        vCursorempresa.moveToFirst();

        if (vCursorempresa.getCount() == 1) {
            nEmpresa = vCursorempresa.getString(0);
        }*/
        try {
            String consulta = "alter table Descuentos add LISTAS text(100000);";
            vBaseDeDatos.getWritableDatabase().execSQL(consulta);
        } catch (SQLiteException ex) {
            Log.e("Erro2r",ex.toString());
        }

        try {
            String consulta = "alter table Clientes add CliBloCup text(1);";
            vBaseDeDatos.getWritableDatabase().execSQL(consulta);
        } catch (SQLiteException ex) {
            Log.e("Erro2r",ex.toString());
        }

        try {
            String consulta = "alter table Descuentos add DESPROGAPLESCTOT text(1);";
            vBaseDeDatos.getWritableDatabase().execSQL(consulta);
        } catch (SQLiteException ex) {
            Log.e("Erro2r",ex.toString());
        }

        try {
            String consulta = "alter table pedidoDesc add kardesgen text(1);";
            vBaseDeDatos.getWritableDatabase().execSQL(consulta);
        } catch (SQLiteException ex) {
            Log.e("Erro2r",ex.toString());
        }

        try {
            String consulta = "alter table articulos add ArtSolEnt text(1);";
            vBaseDeDatos.getWritableDatabase().execSQL(consulta);
        } catch (SQLiteException ex) {
            Log.e("Erro2r",ex.toString());
        }
        try {
            String consulta = "alter table pedido add FacFecEnt text(100);";
            vBaseDeDatos.getWritableDatabase().execSQL(consulta);
        } catch (SQLiteException ex) {
            Log.e("Erro2r",ex.toString());
        }

        String Script = "select empcod from empresaglobal";
        Cursor vCursorempresa = vBaseDeDatos.getReadableDatabase().rawQuery(Script, null);
        vCursorempresa.moveToFirst();
        if (vCursorempresa.getCount() == 1) {
            vEmpresa = vCursorempresa.getString(0);
            vGlobalVariables2.setEmpresa(vEmpresa,getApplicationContext());

        }

        String vendedor ="select Vencod, Vennom from usuarios";
        Cursor infovendedor = vBaseDeDatos.getReadableDatabase().rawQuery(vendedor , null);
        infovendedor.moveToFirst();

        TextView VenCod = findViewById(R.id.VenCod);
        actualizcion =findViewById(R.id.actualizcion);
                TextView txtvendedor = findViewById(R.id.vendedor);
        if (infovendedor.getCount() == 1) {
            VenCod.setText("Ver."+BuildConfig.VERSION_NAME+"."+infovendedor.getString(0));
            txtvendedor.setText(infovendedor.getString(1));
        }


        String mantisFicc = "N";
        mantisFicc =    conbd.MantisFicc;

        Button btn_soporte= (Button)findViewById(R.id.btn_soporte);
        Button btn_actualizar= (Button)findViewById(R.id.btn_actualizar);
        Button revisionalistamiento= (Button)findViewById(R.id.revisionalistamiento);




        new Thread(() -> {

             resp = consultarVersion(BuildConfig.VERSION_NAME);

            if ("S".equals(resp.actualizar)) {
                runOnUiThread(() -> {

                    actualizcion.setImageTintList(ColorStateList.valueOf(Color.RED));
                    View root = findViewById(android.R.id.content);
                    SharedPreferences prefs = getSharedPreferences("config", MODE_PRIVATE);
                    boolean noMostrar = prefs.getBoolean("no_mostrar_actualizacion", false);

                  if(noMostrar){

                  }else{
                      mostrarPopup(root,resp);
                  }



                    actualizcion.post(() -> {
                        mostrarTooltip(actualizcion);
                    });

                    actualizcion.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mostrarPopup(root,resp);
                        }
                    });


                });
            }
        }).start();






        revisionalistamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), wp_gestionpedido.class);
                startActivity(i);
            }
        });




        btn_actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vBaseDeDatos.sincronizartodo(vBaseDeDatos.getWritableDatabase());
            }
        });

        btn_soporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Soporte.class);
                startActivity(i);
            }
        });
        Button btn_Sincronizar= (Button)findViewById(R.id.btn_Sincronizar);
        btn_Sincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Sincronizar.class);
                startActivity(i);

            }
        });
        Button btn_clientespedido= (Button)findViewById(R.id.btn_clientespedido);
        btn_clientespedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ClientesPedido.class);
                i.putExtra("ruta", "S");
                startActivity(i);
            }
        });
        Button btn_extraruta= (Button)findViewById(R.id.btn_extraruta);
        btn_extraruta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ClientesPedido.class);
                i.putExtra("ruta", "N");
                startActivity(i);
            }
        });
        Button btn_enviarpedidos= (Button)findViewById(R.id.btn_enviarpedidos);
        btn_enviarpedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalVariables gGlobalVariables = GlobalVariables.getInstance();
                 vEmpresa=gGlobalVariables.getEmpresa(); //erorrrrrrrrrrrrrrrrrrrrrrrr


                    Intent i = new Intent(getApplicationContext(), Wp_ResumenPedidosnew.class);
                i.putExtra("nitsec", "");
                i.putExtra("clisec",0);
                i.putExtra("prefijo", "");
                    startActivity(i);


            }
        });







        GlobalVariables gGlobalVariables = GlobalVariables.getInstance();
         vEmpresa=gGlobalVariables.getEmpresa(); //erorrrrrrrrrrrrrrrrrrrrrrrr





        Button btn_informediario= (Button)findViewById(R.id.btn_informediario);
        btn_informediario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(getApplicationContext(), InformeDiario.class);
                    startActivity(i);
                }catch (Exception e){
                    Integer Error=0;
                }
            }
        });

        Button btn_crearcliente= (Button)findViewById(R.id.btn_crearcliente);
        btn_crearcliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ClientesNuevos.class);
                startActivity(i);
            }
        });



        Button btn_reset= (Button)findViewById(R.id.btn_reset);
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Configuraciones.class);
                startActivity(i);
            }
        });


        String Empresa2="";
        if(vGlobalVariables2.getEmpresa().isEmpty()){
            Empresa2 =   "a";
        }else{
            Empresa2 =  vGlobalVariables2.getEmpresa();
        }


        if (Empresa2.trim().equalsIgnoreCase("IBANEZ") || Empresa2.trim().equalsIgnoreCase("IBANEZPRU")|| Empresa2.trim().equalsIgnoreCase("SURTIMARCAS") || Empresa2.trim().equalsIgnoreCase("SUHOGAR")|| Empresa2.equalsIgnoreCase("SUHOGARPRU") ) {
            btn_crearcliente.setVisibility(View.VISIBLE);

        }else{
            btn_crearcliente.setVisibility(View.GONE);


        }


        try {
            String sDia="";
            String sMes="";
            String sAno="";
            String Dia="";
            String Mes="";
            String Ano="";
            String Hor="";
            String Min="";
            String Seg="";
            Integer horcie=0;

            BaseDatos BaseDeDatos;
            BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
            Cursor Clientes = BaseDeDatos.getWritableDatabase().rawQuery("select SinAno,SinMes,SinDay,SinHor,SinMin,SinSeg,SerDay,SerMes,SerAno,horcie from Sincronizaciones where SinAno=" + time.year + " and SinMes=" + (time.month + 1) + " and SinDay=" + (time.monthDay) , null);
           // if (Clientes.getCount() == 0) {

           // }

            Double pedidoMinimo=0.0;

            Cursor vCursorUsuarios = BaseDeDatos.getReadableDatabase().rawQuery("select ParMovPedMin from usuarios where VenCnt=1  ", null);
            vCursorUsuarios.moveToFirst();

            if (vCursorUsuarios.getCount() >0) {
                pedidoMinimo=vCursorUsuarios.getDouble(0);
            }

            TextView txt_pedmin = (TextView) findViewById(R.id.txt_pedmin);

            txt_pedmin.setText(String.format("%,d",pedidoMinimo.intValue()));


            if (Clientes.getCount() == 0) {

                btn_crearcliente.setEnabled(false);
                btn_crearcliente.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
                btn_informediario.setEnabled(false);
                btn_informediario.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
                btn_enviarpedidos.setEnabled(false);
                btn_enviarpedidos.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
                btn_extraruta.setEnabled(false);
                btn_extraruta.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
                btn_clientespedido.setEnabled(false);
                btn_clientespedido.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));


            }else {
                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String Version=vGlobalVariables.getVersion();
                String Empresa=vGlobalVariables.getEmpresa();

                Cursor curVersiones = BaseDeDatos.getWritableDatabase().rawQuery("select VerMovVersion,VerMovEstado from VersionMovil where VerMovEstado='A' order by VerMovSec" , null);
                int valido=0;
                String masreciente="";
                if (Empresa.trim().equalsIgnoreCase("IBANEZ") || Empresa.trim().equalsIgnoreCase("IBANEZ")) {
                    if (curVersiones.getCount() > 0) {
                        curVersiones.moveToFirst();
                        do {
                            if (Version.equalsIgnoreCase(curVersiones.getString(0))) {
                                valido = 1;

                            }
                            masreciente = curVersiones.getString(0);
                        } while (curVersiones.moveToNext());
                    }
                    if (valido == 0) {
                        btn_crearcliente.setEnabled(false);
                        btn_crearcliente.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
                        btn_informediario.setEnabled(false);
                        btn_informediario.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
                        btn_enviarpedidos.setEnabled(false);
                        btn_enviarpedidos.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
                        btn_extraruta.setEnabled(false);
                        btn_extraruta.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
                        btn_clientespedido.setEnabled(false);
                        btn_clientespedido.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));



                    }
                }
                Clientes.moveToFirst();
                do {
                    Ano=Clientes.getString(0);
                    Mes=Clientes.getString(1);
                    Dia=Clientes.getString(2);
                    Hor=Clientes.getString(3);
                    Min=Clientes.getString(4);
                    Seg=Clientes.getString(5);
                    sDia=Clientes.getString(6);
                    sMes=Clientes.getString(7);
                    sAno=Clientes.getString(8);
                    horcie=Clientes.getInt(9);
                } while (Clientes.moveToNext());

                TextView txt_fecser = (TextView) findViewById(R.id.txt_fecser);
                TextView txt_ultsin = (TextView) findViewById(R.id.txt_ultsin);
                TextView txt_horenv = (TextView) findViewById(R.id.txt_horenv);

                txt_ultsin.setText("Ultima Sincronizacion : "+Dia + "/" + Mes + "/" + Ano + " " + Hor + ":" + Min + ":" + Seg);
                txt_fecser.setText("Fecha Servidor : "+sDia + "/" + sMes + "/" + sAno);

                String jornada="";
                if (horcie>12){
                    horcie=horcie-12;
                    jornada=" pm";
                }else{
                    jornada=" am";
                }
                txt_horenv.setText("Hora limite de envio : "+horcie +jornada);
            }

            Clientes = BaseDeDatos.getWritableDatabase().rawQuery("select SinAno,SinMes,SinDay,SinHor,SinMin,SinSeg,SerDay,SerMes,SerAno,horcie from Sincronizaciones order by SinAno desc,SinMes desc,SinDay desc limit 1"  , null);
            if (Clientes.getCount() > 0) {
                Clientes.moveToFirst();
                do {
                    Ano=Clientes.getString(0);
                    Mes=Clientes.getString(1);
                    Dia=Clientes.getString(2);
                    Hor=Clientes.getString(3);
                    Min=Clientes.getString(4);
                    Seg=Clientes.getString(5);
                    sDia=Clientes.getString(6);
                    sMes=Clientes.getString(7);
                    sAno=Clientes.getString(8);
                    horcie=Clientes.getInt(9);
                } while (Clientes.moveToNext());

                TextView txt_fecser = (TextView) findViewById(R.id.txt_fecser);
                TextView txt_ultsin = (TextView) findViewById(R.id.txt_ultsin);
                TextView txt_horenv = (TextView) findViewById(R.id.txt_horenv);

                txt_ultsin.setText("Ultima Sincronizacion : "+Dia + "/" + Mes + "/" + Ano + " " + Hor + ":" + Min + ":" + Seg);
                txt_fecser.setText("Fecha Servidor : "+sDia + "/" + sMes + "/" + sAno);

                String jornada="";
                if (horcie>12){
                    horcie=horcie-12;
                    jornada=" pm";
                }else{
                    jornada=" am";
                }
                txt_horenv.setText("Hora limite de envio : "+horcie +jornada);

            }

        }catch (Exception e){
            int jj=0;
        }
    }
    @Override
    public void onRestart() {
        super.onRestart();
        super.onResume();

        startActivity(getIntent());
        finish();
    }

    public void mostrarPopup(View anchorView, VersionResponse resp) {
        cancelarDescarga = false;
        View fondo = findViewById(R.id.fondoOscuro);
        fondo.setVisibility(View.VISIBLE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.wpactualizador, null);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
        );

        popupWindow.setElevation(10);
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setFocusable(true);
        // Mostrar debajo del botón
        //popupWindow.showAsDropDown(anchorView);
        View rootView = getWindow().getDecorView().getRootView();
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);

        LinearLayout contendorapi  = popupView.findViewById(R.id.contendorapi);
        LinearLayout contenedorinstalando  = popupView.findViewById(R.id.contenedorinstalando);
        TextView txtversion = popupView.findViewById(R.id.txtversion);
        TextView txtnotas = popupView.findViewById(R.id.txtnotas);
        TextView txtEstado = popupView.findViewById(R.id.txtEstado);
        TextView txtProgreso = popupView.findViewById(R.id.txtProgreso);
        ProgressBar progressBar = popupView.findViewById(R.id.progressBar);
        Button btndescargar = popupView.findViewById(R.id.btndescargar);
        Button btnAccion = popupView.findViewById(R.id.btnAccion);
        CheckBox ignorar = popupView.findViewById(R.id.ignorarmensaje);
        SharedPreferences prefs = getSharedPreferences("config", MODE_PRIVATE);

// Cargar estado guardado
        boolean noMostrar = prefs.getBoolean("no_mostrar_actualizacion", false);
        ignorar.setChecked(noMostrar);

// Guardar cuando cambie
        ignorar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("no_mostrar_actualizacion", isChecked);
            editor.apply();
        });

        txtversion.setText(BuildConfig.VERSION_NAME + " -> " + resp.nuevaVersion );
        txtnotas.setText(resp.notas);





        btndescargar.setOnClickListener(v -> {
            txtEstado.setText("Iniciando descarga...");
            progressBar.setIndeterminate(true);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("no_mostrar_actualizacion", false);
                editor.apply();

            descargarAPK(resp.ruta, txtEstado, txtProgreso, progressBar);
            contendorapi.setVisibility(View.GONE);
            contenedorinstalando.setVisibility(View.VISIBLE);
        });

        btnAccion.setOnClickListener(v -> {
            cancelarDescarga = true;
            popupWindow.dismiss();
        });

        popupWindow.setOnDismissListener(() -> {
            cancelarDescarga = true;
            fondo.setVisibility(View.GONE);
        });


    }





    private void descargarAPK(String urlArchivo,
                              TextView txtEstado,
                              TextView txtProgreso,
                              ProgressBar progressBar) {

        new Thread(() -> {
            try {
                URL url = new URL(urlArchivo);
                HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                conexion.connect();

                int tamaño = conexion.getContentLength();

                InputStream input = conexion.getInputStream();
                File archivo = new File(getExternalFilesDir(null), "update.apk");
                FileOutputStream output = new FileOutputStream(archivo);

                byte[] buffer = new byte[4096];
                int bytes;
                int total = 0;

                runOnUiThread(() -> progressBar.setIndeterminate(false));

                while ((bytes = input.read(buffer)) != -1) {
                    if (cancelarDescarga) {
                        //
                        input.close();
                        output.close();
                        archivo.delete();

                        runOnUiThread(() -> {
                            txtEstado.setText("Descarga cancelada");
                            txtProgreso.setText("0%");
                            progressBar.setProgress(0);
                        });
                        return;
                    }
                    total += bytes;
                    output.write(buffer, 0, bytes);

                    int progreso = (int) (total * 100 / tamaño);

                    runOnUiThread(() -> {
                        txtEstado.setText("Descargando...");
                        txtProgreso.setText(progreso + "%");
                        progressBar.setProgress(progreso);
                    });
                }

                output.close();
                input.close();

                runOnUiThread(() -> {
                    txtEstado.setText("Instalando...");
                    progressBar.setIndeterminate(true);
                    instalarAPK(archivo);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> txtEstado.setText("Error en descarga"));
            }
        }).start();
    }





    private void mostrarTooltip(View anchorView) {
        View tooltipView = getLayoutInflater().inflate(R.layout.tooltip, null);

        PopupWindow popupWindow = new PopupWindow(
                tooltipView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(false);

        // Mostrar encima del ImageView
        tooltipView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int xOffset = 0;
        int yOffset = -anchorView.getHeight() - tooltipView.getMeasuredHeight();

        popupWindow.showAsDropDown(anchorView, 0, 10);

        // Ocultar después de 3 segundos
        new Handler().postDelayed(popupWindow::dismiss, 3000);
    }


    private void instalarAPK(File archivo) {
        try {
            Uri uri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider", archivo);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
