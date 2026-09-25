package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
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

import com.ficc.mwmovil.databinding.ActivityEnviospendientesBinding;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class enviospendientes extends AppCompatActivity {

    Button btn_reenviar;
    TextView cantidadpen,cantidadintent;
    Bundle Extras;
    String nitsec,invgrucod,invsubgrucod,invfamcod,artsec,Prefijo,timeractivo;
    Integer clisec;
    Integer plazo;
    private Handler handler ;

    ProgressBar barrapendientes;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviospendientes);
        getSupportActionBar().hide();

        final GestorPedidos GestorPedidos = new GestorPedidos();
        btn_reenviar = findViewById(R.id.btn_reenviar);
        cantidadpen = findViewById(R.id.cantidadpen);
        cantidadintent = findViewById(R.id.cantidadintent);
        barrapendientes = findViewById(R.id.barrapendientes);
        handler = new Handler();
        Extras=this.getIntent().getExtras();
        nitsec = Extras.getString("nitsec");
        clisec = Extras.getInt("clisec");
        plazo = Extras.getInt("plazo");
        Prefijo = Extras.getString("prefijo");
        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String Empresa=vGlobalVariables.getEmpresa();
        cantidadintent.setText(String.valueOf(0));
        try{
            String[] nit = enviospendientes();
            int pendientes = enviospendientes1();
            mensaje(pendientes);
        }catch (Exception e){
            cantidadpen.setText(e.toString());
        }


        btn_reenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_reenviar.setVisibility(View.GONE);
                int intentos = Integer.valueOf(cantidadintent.getText().toString().trim());
                intentos+= 1;
                cantidadintent.setText(String.valueOf(intentos));
                final TextView   cantidadenv;
                cantidadenv = findViewById(R.id.cantidadenv);
                       if(Empresa.equalsIgnoreCase("ibanezpg")){
                           pasar(view);
                       }else{
                           if(intentos > 2){
                               pasar(view);
                           }else{

                               new Thread(new Runnable() {
                                   @Override
                                   public void run() {
                               try {
                                   String[] nnit = enviospendientes();
                                   String Mensaje ="";
                                   int vuelta = 0;
                                   for (int i=0; i< nnit.length;i++) {
                                       barrapendientes.setMax(nnit.length);
                                       Log.e("nnit[i]",nnit[i]);
                                       if(!nnit[i].isEmpty()){
                                           vuelta += 1;
                                           Mensaje +=GestorPedidos.EnviarPedidos(getApplicationContext(),"",nnit[i],0,1,"");
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
                                   /*AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                                   Alerta.setMessage("Pedidos y notas enviadas "+Mensaje);
                                   Alerta.setTitle("Notificacion");
                                   Alerta.setPositiveButton("OK", null);
                                   Alerta.setCancelable(true);
                                   Alerta.create().show();*/

                                   try{
                                       int pendientes = enviospendientes1();
                                       mensaje(pendientes);
                                       if(pendientes==0){
                                           pasar(view);
                                       }
                                   }
                                   catch (Exception e){
                                       cantidadpen.setText("Error conexión, intente de nuevo");
                                   }
                               }
                               catch (Exception e){
                                   //Log.e("errorend",e.toString());
                                   int pendientes = enviospendientes1();
                                   mensaje(pendientes);
                                   if(pendientes==0){
                                       pasar(view);
                                   }
                               }
                                   }
                               }).start();


                           }
                       }




            }
        });


    }


public void pasar(View view){
    Intent intent ;
    intent = new Intent(view.getContext(), ComportamientoVenta.class);
    intent.putExtra("nitsec", Extras.getString("nitsec"));
    intent.putExtra("clisec", Extras.getInt("clisec"));
    intent.putExtra("lisprecod", Extras.getInt("lisprecod"));
    intent.putExtra("invgrucod", "");
    intent.putExtra("invsubgrucod", "");
    intent.putExtra("invfamcod", "");
    intent.putExtra("artsec", "");
    intent.putExtra("plazo", plazo);
    intent.putExtra("plazoNom",  Extras.getString("plazoNom"));
    intent.putExtra("prefijo", Prefijo);
    intent.putExtra("timeractivo", "S");
    intent.putExtra("bodega",Extras.getInt("bodega"));

    finish();
    startActivity(intent);
}

public void mensaje(int pendientes ){
    String mensaje = "";
    if(pendientes  > 1){
        mensaje = "Tiene "+String.valueOf(pendientes)+" envios pendientes";
    }else{
        mensaje =  "Tiene "+String.valueOf(pendientes)+" envio pendiente";
    }
    cantidadpen.setText(mensaje);
}

    public String[] enviospendientes(){
        final Time time = new Time();
        time.setToNow();
        String fecha =  time.year +"-"+  (time.month + 1) +"-"+  time.monthDay;
        String conNumPed ="";
        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();


      //order by nombre

        BaseDatos vBaseDeDatos = new BaseDatos(this, "MantisMovil", null, 6);
        String selectSrcipt = "select (prefijo||'"+vUsuario+"'||p.nitsec||p.clisec||'"+fecha+"') as pednum,p.nitsec  from pedido p left join clientes c on p.nitsec=c.nitsec and p.clisec=c.clisec " +
                "  where CliNoRee ='N' and (pedenviado = 'N' or pedenviado is null)  and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and ifNULL(cant,0)+ifNULL(cantcaj,0)<>0 group by p.NitSec,p.CliSec,c.NitCom,c.CliNom,prefijo,pedenviado ";
        final Cursor cursor = vBaseDeDatos.getWritableDatabase().rawQuery(selectSrcipt, null);
        String[] pendientes  = new String[cursor.getCount()];
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int cont = 0;
            do {

                conNumPed = cursor.getString(0);
               // if (enviado(cursor.getString(0))>0){
                    pendientes[cont] = cursor.getString(1);
                    cont +=1;
                //}

            } while (cursor.moveToNext());
        }

        return pendientes;
    }


    public int enviospendientes1(){
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
                "  where (p.NitSec||'-'||p.Clisec)  <> '" + conCliente+ "' and CliNoRee ='N' and (pedenviado = 'N' or pedenviado is null) and  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and ifNULL(cant,0)+ifNULL(cantcaj,0)<>0 group by p.NitSec,p.CliSec,c.NitCom,c.CliNom,prefijo,pedenviado ";
        final Cursor cursor = vBaseDeDatos.getWritableDatabase().rawQuery(selectSrcipt, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                conNumPed = cursor.getString(0);
             //   pendientes += enviado(conNumPed);
            } while (cursor.moveToNext());
        }
        pendientes = cursor.getCount();
        Log.e("pendiente21",String.valueOf(pendientes));
        return pendientes;
    }






    public int enviado(String NumPed){
 int bandera = 0;

        ConBd conbd = new ConBd();
        Connection conn = conbd.CargarConexion(getApplicationContext());
        Statement comm = null;
        String Sinbd="N";
        try {
            comm = conn.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Sinbd="S";
        }

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

        return bandera;
    }

}