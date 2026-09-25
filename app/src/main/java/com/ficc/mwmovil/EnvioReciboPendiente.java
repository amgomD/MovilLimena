package com.ficc.mwmovil;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ficc.mwmovil.databinding.ActivityEnvioReciboPendienteBinding;

public class EnvioReciboPendiente extends AppCompatActivity {


    Bundle Extras;
    String nitsec = "";
    ProgressBar barrapendientes;
    Integer clisec = 0;
    private Handler handler ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_envio_recibo_pendiente);
        getSupportActionBar().hide();
        Extras=this.getIntent().getExtras();
        barrapendientes = findViewById(R.id.barrapendientes);
        handler = new Handler();
        nitsec = Extras.getString("nitsec");
        final GestorPedidos GestorPedidos = new GestorPedidos();
        clisec = Extras.getInt("clisec");
       int pendientess = pendientes();
       if(pendientess == 0){
           pasar();
       }

        TextView cantidadpen = findViewById(R.id.cantidadpen);
        TextView cantidadintent = findViewById(R.id.cantidadintent);


       Button btn_reenviar = findViewById(R.id.btn_reenviar);
       cantidadpen.setText(String.valueOf(pendientess));

        btn_reenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_reenviar.setVisibility(View.GONE);
                int intentos = Integer.valueOf(cantidadintent.getText().toString().trim());
                intentos+= 1;
                cantidadintent.setText(String.valueOf(intentos));
                final TextView   cantidadenv;
                cantidadenv = findViewById(R.id.cantidadenv);
                if(intentos > 2){
                    pasar();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String[] nnit = pendientesarray();
                                Log.e("Stiren[",nnit.toString());
                                String Mensaje ="";
                                int vuelta = 0;
                              for (int i=0; i < nnit.length;i++) {
                                  Log.e("Stiren[",nnit[i]);
                                    barrapendientes.setMax(nnit.length);
                                    if(!nnit[i].isEmpty()){
                                        vuelta += 1;
                                        Mensaje += GestorPedidos.EnviarRecibos(getApplicationContext(),nnit[i],"",0);

                                        barrapendientes.setProgress(vuelta);
                                        try {
                                            final int finalTotalFilas = vuelta;
                                            final String mensa = Mensaje;
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if(mensa.length() > 0){
                                                        btn_reenviar.setVisibility(View.VISIBLE);
                                                    }
                                                    cantidadenv.setText(String.valueOf(finalTotalFilas));
                                                }
                                            });
                                        } catch (Exception e) {
                                            Log.e("Errorhandler",e.toString());
                                        }


                                    }
                                }

                                int npendientess = pendientes();
                                Log.e("npendientess",String.valueOf(npendientess));
                                if(npendientess == 0){
                                    pasar();
                                }
                            }
                            catch (Exception e){
                                Log.e("erroreddddnd",e.toString());
                              int npendientess = pendientes();
                                if(npendientess == 0){
                                    pasar();
                                }
                            }
                        }




                    }).start();
                }
            }
        });

    }

    public void pasar(){
        Intent intent = new Intent(EnvioReciboPendiente.this, AbonarCartera.class);
        intent.putExtra("nitsec", nitsec);
        intent.putExtra("clisec", clisec);
        intent.putExtra("check",Extras.getBoolean("check"));
        finish();
        startActivity(intent);
    }


    public String[] pendientesarray(){

        int bandera = 0;
        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
        final Time time = new Time();
        time.setToNow();
        final GestorPedidos GestorPedidos = new GestorPedidos();
        String conCliente = nitsec+"-"+clisec;
        Cursor cursor = BaseDeDatos.getWritableDatabase().rawQuery("select p.NitSec,RecNro,p.CliSec,c.NitCom,c.CliNom,sum(abono) abono,sum(retefue) retfue,sum(retica) retica,sum(descuento) retdes,sum(abono-retefue-retica-descuento) efectivo from recibo p left join clientes c on p.nitsec=c.nitsec and p.clisec=c.clisec " +
        " where Enviado <> 'S' or Enviado is null and  (p.NitSec||'-'||p.Clisec)  <> '"+conCliente+"' and rcyear=" + time.year + " and rcmonth=" + (time.month + 1) + " and rcday=" + time.monthDay + " and ifNULL(abono,0)<>0 group by p.NitSec,RecNro,p.CliSec,c.NitCom,c.CliNom ", null); //order by nombr
        String[] pendientes  = new String[pendientes()];
    if (cursor.getCount() > 0){
        cursor.moveToFirst();
        int cont = 0;
        do{
              pendientes[cont] = cursor.getString(1);
              cont +=1;
        }while (cursor.moveToNext());
    }
        Log.e("Stiren[",pendientes.toString());
           return pendientes;
    }











    public int pendientes(){
        int bandera = 0;
        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
        final Time time = new Time();
        time.setToNow();
        final GestorPedidos GestorPedidos = new GestorPedidos();
        String conCliente = nitsec+"-"+clisec;
        Cursor cursor = BaseDeDatos.getWritableDatabase().rawQuery("select p.NitSec,RecNro,p.CliSec,c.NitCom,c.CliNom,sum(abono) abono,sum(retefue) retfue,sum(retica) retica,sum(descuento) retdes,sum(abono-retefue-retica-descuento) efectivo from recibo p left join clientes c on p.nitsec=c.nitsec and p.clisec=c.clisec " +
                " where Enviado <> 'S' or Enviado is null and (p.NitSec||'-'||p.Clisec)  <> '"+conCliente+"'  and rcyear=" + time.year + " and rcmonth=" + (time.month + 1) + " and rcday=" + time.monthDay + " and ifNULL(abono,0)<>0 group by p.NitSec,RecNro,p.CliSec,c.NitCom,c.CliNom ", null); //order by nombr


   if (cursor.getCount() > 0){
            cursor.moveToFirst();
            do{
            bandera +=1;
            }while (cursor.moveToNext());
        }
        return bandera;
    }


}