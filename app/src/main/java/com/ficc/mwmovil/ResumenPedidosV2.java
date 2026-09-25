package com.ficc.mwmovil;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ResumenPedidosV2 extends AppCompatActivity {
    static int OrdenImportar;
    String modorevision = "N";
    SDTPedidosEnviados[] SDTPedidosEnviados ;
    ListViewAdapterPedidosEnviadosV2 ListViewAdapterPedidosEnviadosNew;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_pedidos_dia);


        try{

            getSupportActionBar().hide();
           // int val = 5/0;
            final GestorPedidos GestorPedidos = new GestorPedidos();
            final BaseDatos BaseDeDatos;
            OrdenImportar  =1;
            BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
            Cursor SqlUsuarios = BaseDeDatos.getWritableDatabase().rawQuery("select ifnull(ParModoRev,'N') ParModoRev from usuarios where VenCnt=1", null); //order by nombre
            if (SqlUsuarios.getCount() > 0) {
                int vuelta = 0;
                SqlUsuarios.moveToFirst();
                do {
                    modorevision = SqlUsuarios.getString(0);
                } while (SqlUsuarios.moveToNext());
            }
            if(modorevision.equalsIgnoreCase("S")){

                AlertDialog.Builder Alerta = new AlertDialog.Builder(ResumenPedidosV2.this);
                Alerta.setMessage( "Esta en modo revision, los mensajes de error serán visibles");
                Alerta.setTitle("Modo Revision");
                Alerta.setPositiveButton("OK", null);
                Alerta.setCancelable(true);
                Alerta.create().show();
            }

            final Time time = new Time();
            time.setToNow();
            GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
            String vUsuario=vGlobalVariables.getUsuario();
            String Empresa=vGlobalVariables.getEmpresa();
            Empresa=Empresa.toUpperCase();


            ConBd conbd = new ConBd();
            conbd.Variables();
            //Connection conn = conbd.CargarConexion(getApplicationContext());


            String MantisFicc = conbd.MantisFicc;
            //Statement comm = null;
            String Sinbd="N";
            ImageButton recargar = findViewById(R.id.recargar);

            cargarDatos();
             cargaenvio();
            final Button btn_enviarpedidos = (Button) findViewById(R.id.btn_enviarpedidos);
            btn_enviarpedidos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                    String Empresa=vGlobalVariables.getEmpresa();
                    String Mensaje = "";
                    if(banderadia().equalsIgnoreCase("S")){
                        if(Empresa.equalsIgnoreCase("IBANEZ") || Empresa.equalsIgnoreCase("IBANEZPRU")){

                            Intent intent = new Intent(getApplicationContext(), ConfirmarEnvio.class);
                            intent.putExtra("total",SDTPedidosEnviados.length);
                            intent.putExtra("clisec",0);
                            intent.putExtra("nitsec","");
                            intent.putExtra("artsec","");

                            Cursor permisoenvalt =  BaseDeDatos.getReadableDatabase().rawQuery("select EnvAlt from usuarios where VenCnt=1  ", null);
                            if(permisoenvalt.getCount()> 0){
                                permisoenvalt.moveToFirst();
                                if(permisoenvalt.getString(0).equalsIgnoreCase("S")){
                                    intent.putExtra("envioalt",1);
                                }else{
                                    intent.putExtra("envioalt",0);
                                }
                            }

                            intent.putExtra("prefijo","");
                            startActivityForResult(intent, 3);


                        }else{
                            if(MantisFicc.equalsIgnoreCase("S")){
                                Mensaje = GestorPedidos.EnviarPedidosFicc(getApplicationContext(), "", "", 0, 0);
                            }else {
                                Mensaje = GestorPedidos.EnviarPedidos(getApplicationContext(), "", "", 0, 0,"");
                                try {
                                    Mensaje += " Vis=" + GestorPedidos.EnviarVisitas(getApplicationContext());
                                } catch (Exception e) {
                                    String hh = "";
                                }
                            }


                            AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                            Alerta.setMessage("Pedidos y notas enviadas "+Mensaje);
                            Alerta.setTitle("Notificacion");
                            Alerta.setPositiveButton("OK", null);
                            Alerta.setCancelable(true);
                            Alerta.create().show();
                        }

                    }else{
                        Mensaje = "No tiene permitido enviar, fecha del dispositivo equivocada"  ;
                    }
                }
            });
            recargar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cargaenvio();
                }
            });

        }catch (Exception e){

            AlertDialog.Builder Alerta = new AlertDialog.Builder(this);
            Alerta.setMessage(e.getMessage());
            Alerta.setTitle("Error");
            Alerta.setPositiveButton("OK", null);

            Alerta.setCancelable(true);
            Alerta.create().show();
        }



    }

    public void cargarDatos(){
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {

                       final GestorPedidos GestorPedidos = new GestorPedidos();
                       final BaseDatos BaseDeDatos;
                       BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
                       final Time time = new Time();
                       time.setToNow();
                       GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                       String vUsuario=vGlobalVariables.getUsuario();
                       String Empresa=vGlobalVariables.getEmpresa();
                       Empresa=Empresa.toUpperCase();
                       ConBd conbd = new ConBd();
                       conbd.Variables();
                       Connection conn = conbd.CargarConexion(getApplicationContext());
                       String MantisFicc = conbd.MantisFicc;
                       Statement comm = null;
                       String Sinbd="N";
                       if(conn != null){
                           try {
                               comm = conn.createStatement();
                           } catch (Exception throwables) {
                               throwables.printStackTrace();
                               Sinbd="S";
                               if(modorevision.equalsIgnoreCase("S")){

                                   AlertDialog.Builder Alerta = new AlertDialog.Builder(ResumenPedidosV2.this);
                                   Alerta.setMessage(throwables.getMessage());
                                   Alerta.setTitle("Error"+"Linea179");
                                   Alerta.setPositiveButton("OK", null);
                                   Alerta.setCancelable(true);
                                   Alerta.create().show();
                               }

                           }
                       }
                       try {

                           Double pedidoMinimo=0.0;

                           Cursor vCursorUsuarios = BaseDeDatos.getReadableDatabase().rawQuery("select ParMovPedMin from usuarios where VenCnt=1  ", null);
                           vCursorUsuarios.moveToFirst();
                           int lisprecod = 1;
                           if (vCursorUsuarios.getCount() >0) {
                               pedidoMinimo=vCursorUsuarios.getDouble(0);
                           }
                           final Cursor cursor = BaseDeDatos.getWritableDatabase().rawQuery("select p.NitSec,ifNULL(c.CliSec,0) CliSec,ifNULL(c.NitCom,'') NitCom,ifNULL(c.CliNom,'') CliNom,ifNULL(prefijo,'') prefijo, ifNull(ConPagnom, '') ConPagnom ,ifnull(pedenviado,'') pedenviado,ifnull(ValEnviado,'0.00') ValEnviado,ifnull(ValEnvAlt,'0.00') ValEnvAlt ,c.Lisprecod from pedido p left join clientes c on p.nitsec=c.nitsec and p.clisec=c.clisec " +
                                   " where IFNULL(NotaInv,'N') = 'N' and  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and ifNULL(cant,0)+ifNULL(cantcaj,0)<>0 group by p.NitSec,p.CliSec,c.NitCom,c.CliNom,prefijo,ConPagnom,c.Lisprecod  ", null); //order by nombre
                           int vuelta = 0;
                           Double TotalEnviado=0.0;
                           Double TotalSinExi=0.0;
                           final String[] InvGruCod;
                           SDTPedidosEnviados = new SDTPedidosEnviados[cursor.getCount()];
                           if (cursor.getCount() > 0) {
                               cursor.moveToFirst();
                               do {
                                   lisprecod = cursor.getInt(9);
                                   String NumPed = cursor.getString(4)+vUsuario.trim()+cursor.getString(0) +  cursor.getInt(1) +  time.year +"-"+  (time.month + 1) +"-"+  time.monthDay  ;
                                   final SDTPedidosEnviados SDTPedidosEnviadosItem = new SDTPedidosEnviados();
                                   SDTPedidosEnviadosItem.NombreCliente = cursor.getString(2);
                                   SDTPedidosEnviadosItem.NombreNegocio = cursor.getString(3);
                                   SDTPedidosEnviadosItem.BodNom = cursor.getString(5); //condicion de pago
                                   SDTPedidosEnviadosItem.NumeroPedido = NumPed ;
                                   SDTPedidosEnviadosItem.sNitSec = cursor.getString(0);
                                   SDTPedidosEnviadosItem.clisec= cursor.getInt(1);
                                   SDTPedidosEnviadosItem.prefijo = cursor.getString(4);
                                   Log.e("checkenviado: ",cursor.getString(6));
                                   SDTPedidosEnviadosItem.checkenviado = cursor.getString(6); //andres


                                   SDTPedidosEnviadosItem.LisPreCod = lisprecod;
                                   SDTPedidosEnviadosItem.ValorPedido = GestorPedidos.TotalesPedido(getApplicationContext(), cursor.getString(4), cursor.getString(0), cursor.getInt(1),"","","").Total;
                                   String sExistencia= cursor.getString(7);
                                   String sExistenciaAlt = cursor.getString(8);
                                   SDTPedidosEnviadosItem.sEnviado = sExistencia; //GestorPedidos.PeidoEnviado(getApplicationContext(), cursor.getString(4), cursor.getString(0), cursor.getInt(1));
                                   SDTPedidosEnviadosItem.ValorPedidoEnviado=Double.valueOf(sExistencia);

                                   //Nuevo andres --------------- envio alt
                                   SDTPedidosEnviadosItem.valenvalt = sExistenciaAlt; //GestorPedidos.PeidoEnviado(getApplicationContext(), cursor.getString(4), cursor.getString(0), cursor.getInt(1));
                                   SDTPedidosEnviadosItem.ValorPedidoEnvialt=Double.valueOf(sExistenciaAlt);
                                   //---------------------------------------

                                   SDTPedidosEnviadosItem.sEnviadoExi=GestorPedidos.SinExistencia;
                                   TotalEnviado+=SDTPedidosEnviadosItem.ValorPedidoEnviado;
                                   TotalSinExi+=Double.valueOf(GestorPedidos.SinExistencia);

                                   Double ValPed= GestorPedidos.TotalesPedido(getApplicationContext(), cursor.getString(4), cursor.getString(0), cursor.getInt(1),"","","").Subtotal;
                                   Double ValPedCli = GestorPedidos.TotalesPedidocliente(getApplicationContext(), cursor.getString(4), cursor.getString(0), cursor.getInt(1),"","","").Total;

                                   if (ValPedCli<pedidoMinimo && pedidoMinimo>0){
                                       SDTPedidosEnviadosItem.NumeroPedido = NumPed+" ERROR (PEDIDO POR DEBAJO DEL MINIMO) valor: "+ValPedCli.toString().trim()+"- MIN = "+pedidoMinimo.toString();
                                   }

                                   Double TotalPedido= GestorPedidos.TotalesPedido(getApplicationContext(),cursor.getString(4), cursor.getString(0), cursor.getInt(1),"","","").Total;
                                   int sincupo=0;

                                   if ((Empresa.trim().equalsIgnoreCase("MENTAHAIR") ) || MantisFicc.equalsIgnoreCase("S") ) {
                                       GestorCartera gestorcartera=new GestorCartera();
                                       gestorcartera.TotalesCatera(getApplicationContext(), cursor.getString(0), cursor.getInt(1));
                                       Integer CarteraGeneral =gestorcartera.CarteraGeneral;
                                       Cursor cursorclientes = BaseDeDatos.getReadableDatabase().rawQuery("select CliCup, CliConPag from clientes where nitsec='" + cursor.getString(0) + "' and clisec=" + cursor.getInt(1), null);
                                       Integer avuelta = 0;
                                       Integer cupo=0;
                                       Integer CliConPag = 0;
                                       if (cursorclientes.getCount() > 0) {
                                           cursorclientes.moveToFirst();
                                           do {
                                               cupo= cursorclientes.getInt(0);
                                               CliConPag = cursorclientes.getInt(1);
                                           } while (cursorclientes.moveToNext());
                                       }
                                       if (cupo-CarteraGeneral-TotalPedido.intValue()<0 && (CliConPag != 0 && cupo != 0 ) ){
                                           sincupo=1;
                                       }
                                   }
                                   if (sincupo==1){
                                       SDTPedidosEnviadosItem.NumeroPedido = NumPed+" \r\n ¡¡¡ ERROR (PEDIDO SUPERA EL CUPO) !!!";
                                   }

                                   //SDTPedidosEnviadosItem.Enviado = cursor.getDouble(2);
                                   SDTPedidosEnviados[vuelta] = SDTPedidosEnviadosItem;
                                   vuelta = vuelta + 1;
                               } while (cursor.moveToNext());
                           }

                           final ListView listview_pedidosenviados = (ListView) findViewById(R.id.listview_pedidosenviados);
                           ListViewAdapterPedidosEnviadosNew = new ListViewAdapterPedidosEnviadosV2(ResumenPedidosV2.this, SDTPedidosEnviados);
                           listview_pedidosenviados.setAdapter(ListViewAdapterPedidosEnviadosNew);
                           SDTResumenPedidos SDTResumenPedidos = GestorPedidos.TotalesPedido(getApplicationContext(), "", "", 0,"","","");
                           TextView txt_subtotal = (TextView) findViewById(R.id.txt_abonoori);
                           TextView txt_iva = (TextView) findViewById(R.id.txt_retencion);
                           TextView txt_impoconsumo = (TextView) findViewById(R.id.txt_retencionica);
                           TextView txt_total = (TextView) findViewById(R.id.txt_neto);
                           TextView txt_porenvio = (TextView) findViewById(R.id.txt_porenvio);
                           TextView txt_porenvio7 = (TextView) findViewById(R.id.txt_porenvio7);

                           TextView txt_numpedidos = (TextView) findViewById(R.id.txt_numpedidos);

                           txt_subtotal.setText(String.format("%,d", SDTResumenPedidos.Subtotal.intValue()));
                           txt_iva.setText(String.format("%,d", SDTResumenPedidos.Iva.intValue()));
                           txt_impoconsumo.setText(String.format("%,d", SDTResumenPedidos.Impoconsumo.intValue()));
                           txt_total.setText(String.format("%,d", SDTResumenPedidos.Total.intValue()));
                           txt_numpedidos.setText(String.format("%,d",vuelta));

                           Double PorEnvio=0.0;
                           Integer jj =SDTResumenPedidos.Total.intValue();
                           if(SDTResumenPedidos.Total.intValue()>0) {
                               PorEnvio= (TotalEnviado / SDTResumenPedidos.Total)*100;
                               txt_porenvio7.setText(String.format("%,d",TotalSinExi.intValue())+"");
                               txt_porenvio.setText(String.format("%,d",PorEnvio.intValue())+"%");
                           }else{
                               txt_porenvio.setText("0%");
                           }
                           if(PorEnvio>=98) {

                           }else{

                           }

                       }catch (Exception e){
                           if(modorevision.equalsIgnoreCase("S")){

                               AlertDialog.Builder Alerta = new AlertDialog.Builder(ResumenPedidosV2.this);
                               Alerta.setMessage( e.getMessage());
                               Alerta.setTitle("Error"+"Linea 310");
                               Alerta.setPositiveButton("OK", null);
                               Alerta.setCancelable(true);
                               Alerta.create().show();
                           }
                           int hh=0;
                       }
                   }
               });
    }

    public void cargaenvio(){

/*
        new Thread(new Runnable() {
            @Override
            public void run() {
        final GestorPedidos GestorPedidos = new GestorPedidos();
        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
        final Time time = new Time();
        time.setToNow();
        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();
        String Empresa=vGlobalVariables.getEmpresa();
        String NumPed = "";
        String sExistencia = "";




         Cursor pedido = BaseDeDatos.getWritableDatabase().rawQuery("select p.NitSec,ifNULL(c.CliSec,0) CliSec,ifNULL(c.NitCom,'') NitCom,ifNULL(c.CliNom,'') CliNom,ifNULL(prefijo,'') prefijo, ifNull(ConPagnom, '') ConPagnom , pedenviado  from pedido p left join clientes c on p.nitsec=c.nitsec and p.clisec=c.clisec " +
                " where pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and ifNULL(cant,0)+ifNULL(cantcaj,0)<>0 group by p.NitSec,p.CliSec,c.NitCom,c.CliNom,prefijo,ConPagnom,pedenviado ", null); //order by nombre
          pedido.moveToFirst();
                  if(pedido.getCount() > 0){
                      NumPed = "(";
                      pedido.moveToFirst();
                      do{
                          String prefijo = pedido.getString(4)+vUsuario.trim()+pedido.getString(0) +  pedido.getInt(1) +  time.year +"-"+  (time.month + 1) +"-"+  time.monthDay  ;
                          String consulta = "update pedido set pedenviado ='P',ValEnvAlt='0.00',ValEnviado='0.00'  where (prefijo||'"+vUsuario+"'||NitSec||CliSec||"+time.year+"||'-'||"+(time.month + 1)+"||'-'||"+time.monthDay+")  = '"+prefijo+"' ";
                          BaseDeDatos.getWritableDatabase().execSQL(consulta);


                          NumPed += "'"+prefijo+"',";
                      }while(pedido.moveToNext());
                      NumPed += ")";
                      NumPed = NumPed.replace("',)","')");
                  }


                OrdenImportar += 1;
                cargarDatos();
            }
        }).start();*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                String returnString = data.getStringExtra("keyName");
                if(returnString.equalsIgnoreCase("200")){
                    Intent intent = getIntent();
                    overridePendingTransition(0, 0);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(intent);
                }else{
                    AlertDialog.Builder Alerta = new AlertDialog.Builder(this, R.style.MyDialogsinconRojo); //
                    Alerta.setMessage(returnString);
                    Alerta.setTitle("Error");
                    Alerta.setPositiveButton("OK", null);

                    Alerta.setCancelable(true);
                    Alerta.create().show();
                }

            }

        }

        if (requestCode == 1700) {
            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra("position",0);
                String valorenv = data.getStringExtra("valorenv");
                Log.e("sEnviado",valorenv);

                SDTPedidosEnviados[position].sEnviado = valorenv;
                SDTPedidosEnviados[position].valenvalt = valorenv;
                SDTPedidosEnviados[position].ValorPedidoEnviado = Double.valueOf(valorenv);
                SDTPedidosEnviados[position].ValorPedidoEnvialt = Double.valueOf(valorenv);

                ListViewAdapterPedidosEnviadosNew.notifyDataSetChanged();
            }
        }

        if (requestCode == 1600) {
            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra("position",0);
                String Mensaje = data.getStringExtra("Mensaje");


                if( Mensaje.contains("Error de conexion") ){
                    AlertDialog.Builder Alerta = new AlertDialog.Builder(this, R.style.MyDialogsinconRojo); //
                    Alerta.setMessage(Mensaje);
                    Alerta.setTitle("Error");
                    Alerta.setPositiveButton("OK", null);

                    Alerta.setCancelable(true);
                    Alerta.create().show();
                }else{
                    AlertDialog.Builder Alerta = new AlertDialog.Builder(this);
                    Alerta.setMessage(Mensaje);
                    Alerta.setTitle("Enviado");
                    Alerta.setPositiveButton("OK", null);

                    Alerta.setCancelable(true);
                    Alerta.create().show();
                }






      /*          String valorenv = data.getStringExtra("valorenv");

                SDTPedidosEnviados[position].valenvalt = valorenv;
                SDTPedidosEnviados[position].ValorPedidoEnvialt = Double.valueOf(valorenv);

                ListViewAdapterPedidosEnviadosNew.notifyDataSetChanged();*/
            }
        }



    }
    public String banderadia(){
        String bander = "N";
        try {
            ConBd conbd = new ConBd();
            conbd.Variables();
            String sincroniza = conbd.sincronizalinea;
            Time time = new Time();
            int Anio = 0;
            int mes = 0;
            int Dia = 0;
            int Anioant = 0;
            int mesant = 0;
            int Diaant = 0;
            time.setToNow();
            Connection conn = conbd.CargarConexion(getApplicationContext());

            if(conn != null){
                Statement comm = conn.createStatement();
                String  Script="SELECT YEAR(GETDATE()) AS Anio, MONTH(GETDATE()) AS Mes, DAY(GETDATE()) AS Dia, YEAR(GETDATE()-2) AS Anioant, MONTH(GETDATE()-2) AS Mesant, DAY(GETDATE()-2) AS Diaant;";
                ResultSet rsImport = comm.executeQuery(Script);
                while (rsImport.next()) {
                    Anio = rsImport.getInt("Anio");
                    mes = rsImport.getInt("Mes");
                    Dia = rsImport.getInt("Dia");
                    Anioant = rsImport.getInt("Anioant");
                    mesant = rsImport.getInt("Mesant");
                    Diaant = rsImport.getInt("Diaant");
                }
                if(Anio == time.year && mes == (time.month + 1) && Dia == time.monthDay){
                    bander = "S";
                }
                if(Anioant == time.year && mesant == (time.month + 1) && Diaant == time.monthDay){
                    bander = "S";
                }
                if( time.year > Anio && (time.month + 1 ) > mes && time.monthDay > Dia ){
                    bander = "S";
                }
                //bander = "S";
            }else{
                bander = "S";
            }






        } catch (Exception e) {
            if(modorevision.equalsIgnoreCase("S")){

                AlertDialog.Builder Alerta = new AlertDialog.Builder(ResumenPedidosV2.this);
                Alerta.setMessage( e.getMessage());
                Alerta.setTitle("Error"+"Linea 588");
                Alerta.setPositiveButton("OK", null);
                Alerta.setCancelable(true);
                Alerta.create().show();
            }
        }


        return bander;
    }


}
