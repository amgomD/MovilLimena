package com.ficc.mwmovil;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ficc.mwmovil.databinding.ActivityResumenConsignacionBinding;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ResumenConsignacion extends AppCompatActivity {

    BaseDatos vBaseDeDatos;
    SDTConsigna[] SDTConsigna;
    int cons =0;
    int sinenviar = 0;
    Double tvalor = 0.0;
    ListView listaconsignas;
    TextView numerocon,Total,txtsinenviar;
    ListViewAdapterConsgina ListViewAdapterConsgina;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_consignacion);
        getSupportActionBar().hide();
        listaconsignas = findViewById(R.id.listaconsignas);
        Button nuevacon = findViewById(R.id.nuevacon);
         numerocon = findViewById(R.id.numerocon);
         Total = findViewById(R.id.Total);
         txtsinenviar = findViewById(R.id.sinenviar);
        nuevacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ConsignacionEfectivo.class);
                i.putExtra("ConNro","");
                startActivity(i);
                //finish();

            }
        });

        Button enviartodos = findViewById(R.id.enviartodos);
        Time time = new Time();
        time.setToNow();
        vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
        Cursor consignaciones =  vBaseDeDatos.getWritableDatabase().rawQuery("select  c.ConNro,ifnull(Valor,0) Valor, ifnull((select count(*) from ConsignaReciboFoto where ConNro = c.ConNro ),0) NroFotos,Enviado,ifnull((select count(*) from Reciboforma where ConNro = c.ConNro ),0) NroRec,ifnull(Recibido,'N') Recibido from ConsignaRecibo c  where c.Valor > 0 and c.conyear="+ time.year+" and c.conmonth = "+(time.month + 1) +" and c.conday = "+time.monthDay+" ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR
        SDTConsigna=new SDTConsigna[consignaciones.getCount()];
        if(consignaciones.getCount()>0){
             consignaciones.moveToFirst();
            Integer vuelta=0;
             do{
                 SDTConsigna    SDTConsignaItem= new SDTConsigna();
                 SDTConsignaItem.ConNro = consignaciones.getString(0);
                 SDTConsignaItem.ValorCon = consignaciones.getDouble(1);
                 SDTConsignaItem.nroFoto = consignaciones.getInt(2);
                 SDTConsignaItem.numRec = consignaciones.getInt(4);
                 SDTConsignaItem.enviado = consignaciones.getString(3);
                 SDTConsignaItem.Recibido = consignaciones.getString(5);
                 cons += 1;
                 if(consignaciones.getString(3).equalsIgnoreCase("S")){
                     sinenviar +=1 ;
                 }


                 Double totalFot = 0.0;
                 Cursor fotos= vBaseDeDatos.getWritableDatabase().rawQuery("select  ConNro,Valorfoto from ConsignaRecibofoto  where ConNro = '"+consignaciones.getString(0)+"'  ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR
                 if(fotos.getCount()> 0){
                     fotos.moveToFirst();
                     do{
                         totalFot+= fotos.getDouble(1);
                     }while(fotos.moveToNext());
                 }else{
                     totalFot = 0.0;
                 }
                 Log.e("totalFot: ",String.valueOf(totalFot));
                 SDTConsignaItem.valorFoto = totalFot;

                tvalor += SDTConsignaItem.ValorCon;
                 SDTConsigna[vuelta]=SDTConsignaItem;
                 vuelta+=1;
             }while (consignaciones.moveToNext());
         }

        ListViewAdapterConsgina = new ListViewAdapterConsgina(ResumenConsignacion.this, SDTConsigna);
        listaconsignas.setAdapter(ListViewAdapterConsgina);
        numerocon.setText(String.valueOf(cons));
        Total.setText(String.format("%,d",tvalor.intValue()));
        txtsinenviar.setText(String.valueOf(sinenviar));
        cargarEstado();


        enviartodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ConfirmarEnvioCon.class);
                intent.putExtra("ConNro","");
                intent.putExtra("Sinenviar",0);
                startActivityForResult(intent,300);


            }
        });
    }

    private void cargarEstado() {
        new Thread(new Runnable() {
            @Override
            public void run() {
        String colCnNro = "(";
        for(int i = 0;i<SDTConsigna.length;i++){
         colCnNro += "'"+SDTConsigna[i].ConNro+"',";
        }
        colCnNro += ")";
        colCnNro = colCnNro.replace(",)",")");

        ConBd conbd = new ConBd();
        Connection conn = conbd.CargarConexion(getApplicationContext());
        Statement comm = null;
        ResultSet rsImport = null;



     try{
         comm = conn.createStatement();
         String consulta = "select isnull(ConRecEfeEstado,'N')ConRecEfeEstado ,ConRecEfeNrro from consignaRecibo where ConRecEfeNrro in "+colCnNro;
         comm.setQueryTimeout(5);
         rsImport = comm.executeQuery(consulta);
         while (rsImport.next()) {
             String ConRecEfeEstado = rsImport.getString("ConRecEfeEstado").trim();
             String cnro =  rsImport.getString("ConRecEfeNrro").trim();
             for(int i = 0;i<SDTConsigna.length;i++){
                 if(SDTConsigna[i].ConNro.equalsIgnoreCase(cnro)){
                     SDTConsigna[i].Recibido = ConRecEfeEstado;
                     consulta = "update ConsignaRecibo set Recibido = '"+ConRecEfeEstado+"' where ConNro ='"+SDTConsigna[i].ConNro+"' ";
                     vBaseDeDatos.getWritableDatabase().execSQL(consulta);

                 }
             }

         }
         runOnUiThread(new Runnable() {

             @Override
             public void run() {
                 ListViewAdapterConsgina.notifyDataSetChanged();
             }        });


     }catch (Exception e){
 Log.e("Exce",e.toString());
     }finally { // Cerramos las conexiones, en orden inverso a su apertura
         try { if (rsImport != null) rsImport.close(); } catch (Exception errorRS) { errorRS.printStackTrace(); }
         try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
         try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
     }

            }
        }).start();
    }

    @Override
    public void onResume(){
        super.onResume();
        Time time = new Time();
        time.setToNow();
        vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
        Cursor consignaciones =  vBaseDeDatos.getWritableDatabase().rawQuery("select  c.ConNro,ifnull(Valor,0) Valor, ifnull((select count(*) from ConsignaReciboFoto where ConNro = c.ConNro ),0) NroFotos,Enviado,ifnull((select count(*) from Reciboforma where ConNro = c.ConNro ),0) NroRec,ifnull(Recibido,'N')Recibido from ConsignaRecibo c  where c.Valor > 0 and c.conyear="+ time.year+" and c.conmonth = "+(time.month + 1) +" and c.conday = "+time.monthDay+" ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR
        SDTConsigna=new SDTConsigna[consignaciones.getCount()];
        tvalor = 0.0;
        cons = 0;
        sinenviar = 0;
        if(consignaciones.getCount()>0){
            consignaciones.moveToFirst();
            Integer vuelta=0;
            do{
                SDTConsigna    SDTConsignaItem= new SDTConsigna();
                SDTConsignaItem.ConNro = consignaciones.getString(0);
                SDTConsignaItem.ValorCon = consignaciones.getDouble(1);
                SDTConsignaItem.nroFoto = consignaciones.getInt(2);
                SDTConsignaItem.numRec = consignaciones.getInt(4);
                SDTConsignaItem.enviado = consignaciones.getString(3);
                SDTConsignaItem.Recibido = consignaciones.getString(5);
                cons += 1;
                if(consignaciones.getString(3).equalsIgnoreCase("S")){
                    sinenviar +=1 ;
                }
                Double totalFot = 0.0;
                Cursor fotos= vBaseDeDatos.getWritableDatabase().rawQuery("select  ConNro,Valorfoto from ConsignaRecibofoto  where ConNro = '"+consignaciones.getString(0)+"'  ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR
                if(fotos.getCount()> 0){
                    fotos.moveToFirst();
                    do{
                        totalFot+= fotos.getDouble(1);
                    }while(fotos.moveToNext());
                }else{
                    totalFot = 0.0;
                }
                Log.e("totalFot: ",String.valueOf(totalFot));
                SDTConsignaItem.valorFoto = totalFot;

                tvalor += SDTConsignaItem.ValorCon;

                SDTConsigna[vuelta]=SDTConsignaItem;
                vuelta+=1;
            }while (consignaciones.moveToNext());
        }

        ListViewAdapterConsgina = new ListViewAdapterConsgina(ResumenConsignacion.this, SDTConsigna);
        listaconsignas.setAdapter(ListViewAdapterConsgina);
        numerocon.setText(String.valueOf(cons));
        Total.setText(String.format("%,d",tvalor.intValue()));
        txtsinenviar.setText(String.valueOf(sinenviar));
        cargarEstado();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==300){
            if(resultCode == RESULT_OK){
                int status = data.getIntExtra("status",0);
                String Mensaje = data.getStringExtra("Mensaje");
                if(status == 200){
                    AlertDialog.Builder Alerta = new AlertDialog.Builder(ResumenConsignacion.this);
                    Alerta.setMessage(Mensaje);
                    Alerta.setTitle("Notificacion");
                    Alerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = getIntent();
                            overridePendingTransition(0, 0);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            finish();

                        }
                    });
                    Alerta.setCancelable(true);
                    Alerta.create().show();
                }
            }

        }
    }

}