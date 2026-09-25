package com.ficc.mwmovil;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatosCliente extends AppCompatActivity {

    Bundle Extras;
    String mantisficc;
    private Handler handler;
    private  Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_cliente);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Extras=this.getIntent().getExtras();


        EditText obscliente = findViewById(R.id.obscliente);
        String nitsec=Extras.getString("nitsec");
        Integer clisec=Extras.getInt("clisec");
        Integer dias=Extras.getInt("dias");
        Integer lisprecod=Extras.getInt("lisprecod");
        ConBd conBd = new ConBd();
        conBd.Variables();
        mantisficc = conBd.MantisFicc;
        handler = new Handler();


        try {
            GestorCartera gestorCartera = new GestorCartera();
            gestorCartera.TotalesCatera(getApplicationContext(), nitsec, clisec);


        TextView txt_nit = (TextView) findViewById(R.id.txt_nit);
        TextView txt_Nombre = (TextView) findViewById(R.id.txt_nombre);
        TextView txt_razonsocial = (TextView) findViewById(R.id.txt_razonsocial);
        TextView txt_direccion = (TextView) findViewById(R.id.txt_direccion);
        TextView txt_ciudad = (TextView) findViewById(R.id.txt_ciudad);
        TextView txt_barrio = (TextView) findViewById(R.id.txt_barrio);
        TextView txt_canal = (TextView) findViewById(R.id.txt_canal);
        TextView txt_subcanal = (TextView) findViewById(R.id.txt_subcanal);
        TextView txt_cupovendedor = (TextView) findViewById(R.id.txt_cupovendedor);
            TextView txt_cupogeneral = (TextView) findViewById(R.id.txt_cupogeneral);
            TextView txt_cupopor = (TextView) findViewById(R.id.txt_cupopor);
        TextView txt_moravendedor = (TextView) findViewById(R.id.txt_moravendedor);
        TextView txt_morageneral = (TextView) findViewById(R.id.txt_morageneral);
        TextView txt_carterageneral = (TextView) findViewById(R.id.txt_carterageneral);
            TextView txt_carteravendedor = (TextView) findViewById(R.id.txt_carteravendedor);
            TextView tipocliente = (TextView) findViewById(R.id.tipocliente);
            TextView perfilcliente = (TextView) findViewById(R.id.perfilcliente);
            TextView zona = (TextView) findViewById(R.id.zona);
            TextView ruta = (TextView) findViewById(R.id.ruta);
            TextView categoria = (TextView) findViewById(R.id.categoria);


            TextView txtObs = (TextView) findViewById(R.id.txtObs);
            TextView txttipocliente = (TextView) findViewById(R.id.txttipocliente);
            TextView txtperfilcliente = (TextView) findViewById(R.id.txtperfilcliente);
            TextView txtzona = (TextView) findViewById(R.id.txtzona);
            TextView txtruta = (TextView) findViewById(R.id.txtruta);
            TextView txtcategoria = (TextView) findViewById(R.id.txtcategoria);

        txt_carterageneral.setText(String.format("%,d",gestorCartera.CarteraGeneral) );
        txt_carteravendedor.setText(String.format("%,d",gestorCartera.CarteraVendedor));
            if(mantisficc.equalsIgnoreCase("S")){
                obscliente.setVisibility(View.VISIBLE);
                tipocliente.setVisibility(View.VISIBLE);
                perfilcliente.setVisibility(View.VISIBLE);
                zona.setVisibility(View.VISIBLE);
                categoria.setVisibility(View.VISIBLE);
                ruta.setVisibility(View.VISIBLE);
                obscliente.setVisibility(View.VISIBLE);
                txttipocliente.setVisibility(View.VISIBLE);
                txtperfilcliente.setVisibility(View.VISIBLE);
                txtzona.setVisibility(View.VISIBLE);
                txtruta.setVisibility(View.VISIBLE);
                txtcategoria.setVisibility(View.VISIBLE);
                txtObs.setVisibility(View.VISIBLE);
            }else{
                obscliente.setVisibility(View.GONE);
                tipocliente.setVisibility(View.GONE);
                perfilcliente.setVisibility(View.GONE);
                zona.setVisibility(View.GONE);
                categoria.setVisibility(View.GONE);
                ruta.setVisibility(View.GONE);
                obscliente.setVisibility(View.GONE);
                txttipocliente.setVisibility(View.GONE);
                txtperfilcliente.setVisibility(View.GONE);
                txtzona.setVisibility(View.GONE);
                txtruta.setVisibility(View.GONE);
                txtcategoria.setVisibility(View.GONE);
                txtObs.setVisibility(View.GONE);
            }

        //TextView txt_morageneral = (TextView) findViewById(R.id.txt_ca);

        GestorCartera gestorcartera=new GestorCartera();

        BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);



            Cursor cursorobs = BaseDeDatos.getReadableDatabase().rawQuery("select  CliObsMovil from obsCliente where nitsec='"+nitsec+"' and clisec="+clisec, null);

            if (cursorobs.getCount()>0) {
                cursorobs.moveToFirst();
                do {
                    obscliente.setText(cursorobs.getString(0));
                }while (cursorobs.moveToNext());
            }
            GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
            String xVendedor=vGlobalVariables.getUsuario();


            Button btn_cartera = (Button) findViewById(R.id.btn_cartera);
            Button btnEnvioCartera = (Button) findViewById(R.id.btnEnvioCartera);


            Cursor bloquecar = BaseDeDatos.getReadableDatabase().rawQuery("select  ParMovBloqCar from Usuarios where Vencod ='"+ xVendedor+"'" , null);

            if (bloquecar.getCount()>0) {
                bloquecar.moveToFirst();
                do {
              Log.e("Paemov",bloquecar.getString(0));
                    if(bloquecar.getString(0).equalsIgnoreCase("S")){
                        btn_cartera.setVisibility(View.GONE);
                        btnEnvioCartera.setVisibility(View.GONE);
                    }

                }while (bloquecar.moveToNext());
            }


          /*  Connection conn = conBd.CargarConexion();
            if(conn != null){
                try {
                    Statement st = conn.createStatement();
                    String nconsulta = "select  ObsCliMovil from ObsClienteMovil where obsCliNitSec='"+nitsec+"' and obsCliClisec="+clisec+" ";
                    ResultSet rsImport = st.executeQuery(nconsulta);

                    while (rsImport.next()){
                        obscliente.setText(rsImport.getString("ObsCliMovil"));
                    }

                }catch (Exception e){
                    Log.e("error obs",e.toString());
                }
            }*/





            Cursor cursor = BaseDeDatos.getReadableDatabase().rawQuery("select NitSec,CliSec,NitIde,NitCom,CliNom,CliDir,CiuNom,BarNom,CanNom,CanSubNom,CliVenCup,CliCup,cliporadi,TipoCliente,perfilcliente,zona,ruta,categoria from clientes where nitsec='"+nitsec+"' and clisec="+clisec, null);
        Integer vuelta=0;
        if (cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                try {
                     gestorcartera.TotalesCatera(getApplicationContext(),cursor.getString(0),0);
                     txt_nit.setText(cursor.getString(2));
                     txt_Nombre.setText(cursor.getString(3));
                     txt_razonsocial.setText(cursor.getString(4));
                     txt_direccion.setText(cursor.getString(5));
                     txt_ciudad.setText(cursor.getString(6));
                     txt_barrio.setText(cursor.getString(7));
                     txt_canal.setText(cursor.getString(8));
                     txt_subcanal.setText(cursor.getString(9));
                     txt_cupovendedor.setText(String.format("%,d",cursor.getInt(10)));
                     txt_cupogeneral.setText(String.format("%,d",cursor.getInt(11)));
                    txt_cupopor.setText('%'+String.format("%,d",cursor.getInt(12)));
                     txt_moravendedor.setText(String.format("%,d",gestorcartera.MoraVendedor));
                     txt_morageneral.setText(String.format("%,d",gestorcartera.MoraGeneral));

                    tipocliente.setText(cursor.getString(13));
                    categoria.setText(cursor.getString(17));
                    ruta.setText(cursor.getString(16));
                    zona.setText(cursor.getString(15));
                    perfilcliente.setText(cursor.getString(14));



                }catch (Exception e){
                    Log.e("Error",e.toString());
                    Integer Error=1;
                }
                vuelta=vuelta+1;
            } while (cursor.moveToNext());
        }

        }catch (Exception e)
        {
            int hh=0;
        }

        Button btn_cartera = (Button) findViewById(R.id.btn_cartera);
        Button btn_historialrem = (Button) findViewById(R.id.btn_historialrem);

        Button btnEnvioCartera = (Button) findViewById(R.id.btnEnvioCartera);
        Button btnvolver = (Button) findViewById(R.id.btnvolver);
        btnvolver.setVisibility(View.GONE);
        btnEnvioCartera.setVisibility(View.GONE);

        btnvolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.no_anim, R.anim.slide_out_right);
            }
        });
        btnEnvioCartera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(view.getContext(), EnvioCartera.class);
               // intent.putExtra("nitsec", Extras.getString("nitsec"));
                //intent.putExtra("clisec", Extras.getInt("clisec"));
                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String Vendedor=vGlobalVariables.getUsuario();
                String ulr="http://161.18.225.175:8080/MantisFiccGx2Diagnostimax/wpcarteraclientemovil?NitSec="+nitsec+"&CliSec="+clisec+"&VenCod="+Vendedor;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(ulr));
                startActivity(i);
            }
        });

        btn_cartera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(view.getContext(), Cartera.class);
                intent.putExtra("nitsec", Extras.getString("nitsec"));
                intent.putExtra("clisec", Extras.getInt("clisec"));
                startActivity(intent);
            }
        });

        Button btn_pedido = (Button) findViewById(R.id.btn_pedido);
        btn_pedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(view.getContext(), visitacliente.class);
                intent.putExtra("nitsec", Extras.getString("nitsec"));
                intent.putExtra("clisec", Extras.getInt("clisec"));
                Integer lista=Extras.getInt("lisprecod");
             //   if (lista>10){
             //       lista=lista-10;
            //    }
                intent.putExtra("lisprecod",lista );
                startActivity(intent);
            }
        });

        obscliente.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                handler.removeCallbacksAndMessages(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                handler.removeCallbacksAndMessages(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                   String CliObsMovil = editable.toString().trim();
                BaseDatos BaseDeDatos;
                BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                String consulta = "delete from obsCliente where NitSec = '"+nitsec+"' and clisec = "+clisec+" ";
                BaseDeDatos.getWritableDatabase().execSQL(consulta);

                 consulta = "Insert into obsCliente (NitSec,CliSec,CliObsMovil) values ( " +
                        " '"+nitsec+"',"+clisec+",'"+CliObsMovil+"')";
                BaseDeDatos.getWritableDatabase().execSQL(consulta);

                Runnable runaccion = new Runnable() {
                    @Override
                    public void run() {



                ConBd conBd = new ConBd();
                Connection conn = conBd.CargarConexion(getApplicationContext());
                if(conn != null){
                    try {
                        Statement st = conn.createStatement();
                        String nconsulta = "delete from ObsClienteMovil where obsCliNitSec = '"+nitsec+"' and obsCliClisec = "+clisec+" ";
                        st.executeUpdate(nconsulta);
                         nconsulta = "Insert into ObsClienteMovil (obsCliNitSec,obsCliClisec,ObsCliMovil) values ( " +
                                " '"+nitsec+"',"+clisec+",'"+CliObsMovil+"')";
                        st.executeUpdate(nconsulta);

                    }catch (Exception e){
                        Log.e("error obs",e.toString());
                    }
                }

                    }
                };




                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(runaccion,700);

            }
        });








        GlobalVariables gGlobalVariables = GlobalVariables.getInstance();
        String vEmpresa=gGlobalVariables.getEmpresa();
        Button btn_historialcliente = (Button) findViewById(R.id.btn_historialcliente);

        if(vEmpresa.equalsIgnoreCase("DIAGNOSTIMAX") || vEmpresa.equalsIgnoreCase("DIAGNOSTIMAXALT")){
            btnEnvioCartera.setVisibility(View.VISIBLE);
        }
        if(dias == 1005238220){
            btn_pedido.setVisibility(View.GONE);
            btn_cartera.setVisibility(View.GONE);
            btn_historialcliente.setVisibility(View.GONE);
            btnvolver.setVisibility(View.VISIBLE);
        }


        btn_historialrem.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {


                    Intent intent = new Intent(view.getContext(), HistorialClientev2.class);
                    intent.putExtra("nitsec", Extras.getString("nitsec"));
                    intent.putExtra("clisec", Extras.getInt("clisec"));
                intent.putExtra("lisprecod", Extras.getInt("lisprecod"));
                intent.putExtra("tipo", "REM");
                    startActivity(intent);


            }
        });
        btn_historialcliente.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

     if(vEmpresa.equalsIgnoreCase("IBANEZ") || vEmpresa.equalsIgnoreCase("IBANEZPRU")){
         Intent intent = new Intent(view.getContext(), Historial.class);
         intent.putExtra("nitsec", Extras.getString("nitsec"));
         intent.putExtra("clisec", Extras.getInt("clisec"));
         intent.putExtra("lisprecod", Extras.getInt("lisprecod"));
         startActivity(intent);
     }else{
         Intent intent = new Intent(view.getContext(), HistorialClientev2.class);
         intent.putExtra("nitsec", Extras.getString("nitsec"));
         intent.putExtra("clisec", Extras.getInt("clisec"));
         intent.putExtra("lisprecod", Extras.getInt("lisprecod"));
         intent.putExtra("tipo", "FAC");

         startActivity(intent);
     }

            }
        });

    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_right);
    }
}
