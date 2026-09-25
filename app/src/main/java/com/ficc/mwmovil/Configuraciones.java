package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class Configuraciones extends AppCompatActivity {

    Context pContexto=null;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuraciones);
        getSupportActionBar().hide();

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
        Button btnVerificar = (Button)findViewById(R.id.btnVerificar);
        Button btnalter = (Button)findViewById(R.id.btnalter);

        Button btn_reset= (Button)findViewById(R.id.btn_reset);
        pContexto=btn_reset.getContext();

        btnVerificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), VerificacionPedido.class);
                startActivity(i);
            }
        });


        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder Alerta = new AlertDialog.Builder(pContexto);
                Alerta.setMessage("Este proceso eliminara Pedidos y datos sincronizados, reiniciara la aplicacion para ingresar con un nuevo usuario");
                Alerta.setTitle("Alerta");
                Alerta.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        BaseDatos BaseDeDatos ;

                        BaseDeDatos =new BaseDatos(getApplicationContext(),"MantisMovil", null, 5);



                       try {
                           BaseDeDatos.getWritableDatabase().execSQL("delete from usuarios");
                           BaseDeDatos.getWritableDatabase().execSQL("delete from Sincronizaciones");
                       }catch (Exception e){
                           int kk=0;
                       }
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
                Alerta.setCancelable(true);
                Alerta.setNegativeButton("Cancelar",null);
                Alerta.create().show();
            }
        });

        Button btn_borrarpedidos= (Button)findViewById(R.id.btn_borrarpedidos);
        pContexto=btn_reset.getContext();
        btn_borrarpedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder Alerta = new AlertDialog.Builder(pContexto);
                Alerta.setMessage("Este proceso eliminara Pedidos y datos sincronizados, reiniciara la aplicacion para ingresar con un nuevo usuario");
                Alerta.setTitle("Alerta");
                Alerta.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        BaseDatos BaseDeDatos ;
                        BaseDeDatos =new BaseDatos(getApplicationContext(),"MantisMovil", null, 5);
                        BaseDeDatos.BorrarBd(BaseDeDatos.getWritableDatabase());

                        try {
                            BaseDeDatos.getWritableDatabase().execSQL("drop table IF EXISTS pedido");
                            BaseDeDatos.getWritableDatabase().execSQL("drop table IF EXISTS Pedidoenc");
                           BaseDeDatos.getWritableDatabase().execSQL("drop table IF EXISTS Recibo");
                            BaseDeDatos.getWritableDatabase().execSQL("drop table IF EXISTS Reciboforma");
                           BaseDeDatos.getWritableDatabase().execSQL("drop table IF EXISTS ConsignaRecibo");
                            BaseDeDatos.getWritableDatabase().execSQL("drop table IF EXISTS ReciboFormaFotos");
                            BaseDeDatos.getWritableDatabase().execSQL("drop table IF EXISTS PedidoInf");
                        }catch (Exception e){
                            int kk=0;
                        }
                        BaseDeDatos.onCreate(BaseDeDatos.getWritableDatabase());
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
                Alerta.setCancelable(true);
                Alerta.setNegativeButton("Cancelar",null);
                Alerta.create().show();
            }

        }
        );

        Button btn_guardarimp= (Button)findViewById(R.id.btn_guardarimp);
        btn_guardarimp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView txtImpresora = (TextView) findViewById(R.id.txtImpresora);
                String vImpresora=txtImpresora.getText().toString().trim();

                if (vImpresora.isEmpty()){

                }else{
                    BaseDeDatos.getWritableDatabase().execSQL("update EmpresaMovil set EmpImpre='"+vImpresora+"' "); //order by nombre
                }
            }
        });

        btnalter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String consulta = "alter table pedido add ValEnviado text(100); ";
                    BaseDeDatos.getWritableDatabase().execSQL(consulta);
                } catch (SQLiteException ex) {
                    Log.e("Error",ex.toString());
                }

                try {
                    String consulta = "alter table pedido add ValEnvAlt text(100);";
                    BaseDeDatos.getWritableDatabase().execSQL(consulta);
                } catch (SQLiteException ex) {
                    Log.e("Erro2r",ex.toString());
                }


                try {
                    String consulta = "alter table Usuarios add ParModoRev text(1);";
                    BaseDeDatos.getWritableDatabase().execSQL(consulta);
                } catch (SQLiteException ex) {
                    Log.e("Erro2r",ex.toString());
                }
                AlertDialog.Builder Alerta = new AlertDialog.Builder(pContexto);
                Alerta.setMessage("Campos agregados");
                Alerta.setTitle("Alerta");
                Alerta.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        Intent i = new Intent(getApplicationContext(), Principal.class);
                        startActivity(i);
                        finish();
                    }
                });
                Alerta.setCancelable(true);
                Alerta.setNegativeButton("Cancelar",null);
                Alerta.create().show();
            }
        });

        Button btn_borrarbd= (Button)findViewById(R.id.btn_borrarbd);
        btn_borrarbd.setOnClickListener(new View.OnClickListener() {
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

        Switch switch1= (Switch)findViewById(R.id.switch1);
        Switch switch2= (Switch)findViewById(R.id.switch2);
        String GeoRef="N";
        String ParModoRev = "N";
        Cursor SqlUsuarios = BaseDeDatos.getWritableDatabase().rawQuery("select ParGeoRef,ifnull(ParModoRev,'N') ParModoRev from usuarios where VenCnt=1", null); //order by nombre
        if (SqlUsuarios.getCount() > 0) {
            int vuelta = 0;
            SqlUsuarios.moveToFirst();
            do {
                GeoRef = SqlUsuarios.getString(0);
                Log.e("ModoREv",SqlUsuarios.getString(1));
                if (SqlUsuarios.getString(1).equalsIgnoreCase("S")){
                    switch2.setChecked(true);
                }

            } while (SqlUsuarios.moveToNext());
        }
        String Imp="";
        Cursor SqlEmpresa = BaseDeDatos.getWritableDatabase().rawQuery("select EmpImpre from EmpresaMovil ", null); //order by nombre
        if (SqlEmpresa.getCount() > 0) {
            int vuelta = 0;
            SqlEmpresa.moveToFirst();
            do {
                Imp = SqlEmpresa.getString(0);
            } while (SqlEmpresa.moveToNext());
        }
        TextView txtImpresora = (TextView) findViewById(R.id.txtImpresora);
        txtImpresora.setText(Imp);



//        if (GeoRef.equalsIgnoreCase("S")){
  //          switch1.setChecked(true);
    //    }

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked){
                    BaseDeDatos.getWritableDatabase().execSQL("update usuarios set ParGeoRef='S' where VenCnt=1 "); //order by nombre
                }else{
                    BaseDeDatos.getWritableDatabase().execSQL("update usuarios set ParGeoRef='N' where VenCnt=1 "); //order by nombre
                }
                // do something, the isChecked will be
                // true if the switch is in the On position
            }
        });
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked){
                    BaseDeDatos.getWritableDatabase().execSQL("update usuarios set ParModoRev='S' where VenCnt=1 "); //order by nombre
                }else{
                    BaseDeDatos.getWritableDatabase().execSQL("update usuarios set ParModoRev='N' where VenCnt=1 "); //order by nombre
                }
                // do something, the isChecked will be
                // true if the switch is in the On position
            }
        });

    }
}
