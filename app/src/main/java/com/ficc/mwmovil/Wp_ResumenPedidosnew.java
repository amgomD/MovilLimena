package com.ficc.mwmovil;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Locale;

public class Wp_ResumenPedidosnew extends AppCompatActivity {
    static int OrdenImportar;
    String modorevision = "N";
    String Tipopedido = "PED";
    String nitsec;
    Bundle Extras;
    String prefijo;
    int clisec;
    SDTPedidosEnviados[] SDTPedidosEnviados ;
     Time time = new Time();
    ListViewAdapterPedidosEnviadosNew ListViewAdapterPedidosEnviadosNew;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_pedidos_dia);
        Log.e("Entro pedidosnew",modorevision);
        Extras=this.getIntent().getExtras();

        nitsec=Extras.getString("nitsec");
        clisec=Extras.getInt("clisec");
        prefijo=Extras.getString("prefijo");
        ImageButton btnFechaConsulta = findViewById(R.id.btnFechaConsulta);
        TextView txtFecha = findViewById(R.id.txtFecha);

        //btnFechaConsulta.setOnClickListener(v -> abrirCalendario());

        Calendar calendario = Calendar.getInstance();

        String fechaHoy = String.format(
                Locale.US,
                "%04d-%02d-%02d",
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH) + 1,
                calendario.get(Calendar.DAY_OF_MONTH)
        );

        txtFecha.setText(fechaHoy);

        btnFechaConsulta.setOnClickListener(v -> {



            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {

                        String fecha = String.format(
                                Locale.US,
                                "%04d-%02d-%02d",
                                year,
                                month + 1,
                                dayOfMonth
                        );

                        txtFecha.setText(fecha);

                        AppGlobals.year = year;
                        AppGlobals.dayOfMonth =  dayOfMonth;
                        AppGlobals.month = month ;


                        if(dayOfMonth > 0){
                            time.set(time.second, time.minute, time.hour,dayOfMonth , month, year);
                        }
                        Log.e("Time: ",String.valueOf(time.month));
                        Log.e("Time: ",String.valueOf(time.monthDay));
                        Log.e("Time: ",String.valueOf(time.year));
                        cargarDatos();

                    },
                    calendario.get(Calendar.YEAR),
                    calendario.get(Calendar.MONTH),
                    calendario.get(Calendar.DAY_OF_MONTH)





            );

            dialog.show();
        });







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

            if ("S".equalsIgnoreCase(modorevision)) {

                AlertDialog.Builder Alerta = new AlertDialog.Builder(Wp_ResumenPedidosnew.this);
                Alerta.setMessage( "Esta en modo revision, los mensajes de error serán visibles");
                Alerta.setTitle("Modo Revision");
                Alerta.setPositiveButton("OK", null);
                Alerta.setCancelable(true);
                Alerta.create().show();
            }


            time.setToNow();
            GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
            String vUsuario=vGlobalVariables.getUsuario();
            String Empresa=vGlobalVariables.getEmpresa();
            Empresa=Empresa.toUpperCase();
            ConBd conbd = new ConBd();
            conbd.Variables();
            String MantisFicc = conbd.MantisFicc;
            Tipopedido = conbd.TipoPedido;
            String Sinbd="N";
            ImageButton recargar = findViewById(R.id.recargar);

            cargarDatos();
             cargaenvio();
            final Button btn_enviarpedidos = (Button) findViewById(R.id.btn_enviarpedidos);
            btn_enviarpedidos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    boolean hayFechasVacias = false;

                    for (int i = 0; i < SDTPedidosEnviados.length; i++) {

                        if (SDTPedidosEnviados[i].FacFecEnt == null ||
                                SDTPedidosEnviados[i].FacFecEnt.trim().isEmpty()) {

                            hayFechasVacias = true;
                            break;
                        }
                    }

                    if (hayFechasVacias) {

                        new AlertDialog.Builder(Wp_ResumenPedidosnew.this)
                                .setTitle("Fecha Promesa de entrega")
                                .setMessage("Hay pedidos que no tienen fecha promesa de entrega.")
                                .setPositiveButton("Aceptar", null)
                                .show();
                        return;
                    }



                    GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                    String Empresa=vGlobalVariables.getEmpresa();
                    String Mensaje = "";

                       // if(Empresa.equalsIgnoreCase("IBANEZ") || Empresa.equalsIgnoreCase("IBANEZPRU")){

                            Intent intent = new Intent(getApplicationContext(), ConfirmarEnvio.class);
                            intent.putExtra("total",SDTPedidosEnviados.length);
                            intent.putExtra("clisec",clisec);
                            intent.putExtra("nitsec",nitsec);
                            intent.putExtra("artsec","");
                            intent.putExtra("envioalt",0);
                            intent.putExtra("prefijo",prefijo);
                            startActivityForResult(intent, 3);


                        /*}else{
                            Mensaje = GestorPedidos.EnviarPedidosFiccGx5(getApplicationContext(), "", "", 0, 0);
                            AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                            Alerta.setMessage("Pedidos y notas enviadas "+Mensaje);
                            Alerta.setTitle("Notificacion");
                            Alerta.setPositiveButton("OK", null);
                            Alerta.setCancelable(true);
                            Alerta.create().show();
                        }*/


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






                       GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                       String vUsuario=vGlobalVariables.getUsuario();
                       if (vUsuario == null) {
                           vUsuario = "";
                       }
                       vUsuario = vUsuario.trim();
                       String Empresa = vGlobalVariables.getEmpresa();

                       if (Empresa == null) {
                           Empresa = "";
                       } else {
                           Empresa = Empresa.toUpperCase();
                       }
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
                               if ("S".equalsIgnoreCase(modorevision)) {

                                   AlertDialog.Builder Alerta = new AlertDialog.Builder(Wp_ResumenPedidosnew.this);
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
                            Cursor cursor =null;

                           if(nitsec.isEmpty()){
                                 cursor = BaseDeDatos.getWritableDatabase().rawQuery("select p.NitSec,ifNULL(c.CliSec,0) CliSec,ifNULL(c.NitCom,'') NitCom,ifNULL(c.CliNom,'') CliNom,ifNULL(prefijo,'') prefijo, ifNull(ConPagnom, '') ConPagnom ,ifnull(pedenviado,'') pedenviado,ifnull(ValEnviado,'0.00') ValEnviado,ifnull(ValEnvAlt,'0.00') ValEnvAlt ,c.Lisprecod, ifnull(FacFecEnt,'') FacFecEnt from pedido p left join clientes c on p.nitsec=c.nitsec and p.clisec=c.clisec " +
                                       " where  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and ifNULL(cant,0)+ifNULL(cantinf,0)<>0 group by p.NitSec,p.CliSec,c.NitCom,c.CliNom,prefijo,ConPagnom,c.Lisprecod  ", null); //order by nombre
                           }else{
                                 cursor = BaseDeDatos.getWritableDatabase().rawQuery("select p.NitSec,ifNULL(c.CliSec,0) CliSec,ifNULL(c.NitCom,'') NitCom,ifNULL(c.CliNom,'') CliNom,ifNULL(prefijo,'') prefijo, ifNull(ConPagnom, '') ConPagnom ,ifnull(pedenviado,'') pedenviado,ifnull(ValEnviado,'0.00') ValEnviado,ifnull(ValEnvAlt,'0.00') ValEnvAlt ,c.Lisprecod, ifnull(FacFecEnt,'') FacFecEnt from pedido p left join clientes c on p.nitsec=c.nitsec and p.clisec=c.clisec " +
                                       " where prefijo = '"+prefijo+"' and p.NitSec = '"+nitsec+"' and p.clisec = "+clisec+" and  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and ifNULL(cant,0)+ifNULL(cantinf,0)<>0 group by p.NitSec,p.CliSec,c.NitCom,c.CliNom,prefijo,ConPagnom,c.Lisprecod  ", null); //order by nombre

                           }



                           int vuelta = 0;
                           Double TotalEnviado=0.0;
                           Double TotalSinExi=0.0;
                           final String[] InvGruCod;
                           SDTPedidosEnviados = new SDTPedidosEnviados[cursor.getCount()];
                           if (cursor.getCount() > 0) {
                               cursor.moveToFirst();
                               do {


                                   SDTResumenPedidos resumen =
                                           GestorPedidos.TotalesPedido(
                                                   getApplicationContext(),
                                                   cursor.getString(4),
                                                   cursor.getString(0),
                                                   cursor.getInt(1),
                                                   "",
                                                   "",
                                                   ""
                                           );




                                   double valorEnviado = 0.0;
                                   double valorEnviadoAlt = 0.0;


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
                                   SDTPedidosEnviadosItem.checkenviado = cursor.getString(6); //andres
                                   SDTPedidosEnviadosItem.LisPreCod = lisprecod;
                                   SDTPedidosEnviadosItem.FacFecEnt = cursor.getString(10);
                                   SDTPedidosEnviadosItem.ValorPedido = resumen.Total; //GestorPedidos.TotalesPedido(getApplicationContext(), cursor.getString(4), cursor.getString(0), cursor.getInt(1),"","","").Total;
                                   SDTPedidosEnviadosItem.CreditoBueno = resumen.CreditoBueno;
                                   SDTPedidosEnviadosItem.CreditoMalo= resumen.CreditoMalo;


                                   String sExistencia= cursor.getString(7);
                                   String sExistenciaAlt = cursor.getString(8);
                                   SDTPedidosEnviadosItem.sEnviado = sExistencia; //GestorPedidos.PeidoEnviado(getApplicationContext(), cursor.getString(4), cursor.getString(0), cursor.getInt(1));


                                   try {
                                       valorEnviado = Double.parseDouble(sExistencia);
                                   } catch (Exception e) {
                                       Log.e("CARGARDATOS", "Valor inválido: " + sExistencia);
                                   }

                                   try {
                                       valorEnviadoAlt = Double.parseDouble(sExistenciaAlt);
                                   } catch (Exception e) {
                                       Log.e("CARGARDATOS", "Valor inválido: " + sExistenciaAlt);
                                   }


                                   SDTPedidosEnviadosItem.ValorPedidoEnviado= valorEnviado; //Double.valueOf(sExistencia);

                                   //Nuevo andres --------------- envio alt
                                   SDTPedidosEnviadosItem.valenvalt = sExistenciaAlt; //GestorPedidos.PeidoEnviado(getApplicationContext(), cursor.getString(4), cursor.getString(0), cursor.getInt(1));
                                   SDTPedidosEnviadosItem.ValorPedidoEnvialt= valorEnviadoAlt;//Double.valueOf(sExistenciaAlt);
                                   //---------------------------------------

                                   SDTPedidosEnviadosItem.sEnviadoExi=GestorPedidos.SinExistencia;
                                   TotalEnviado+=SDTPedidosEnviadosItem.ValorPedidoEnviado;
                                   TotalSinExi+=Double.valueOf(GestorPedidos.SinExistencia);

                                   Double ValPed=  resumen.Subtotal;//GestorPedidos.TotalesPedido(getApplicationContext(), cursor.getString(4), cursor.getString(0), cursor.getInt(1),"","","").Subtotal;
                                   Double ValPedCli = GestorPedidos.TotalesPedidocliente(getApplicationContext(), cursor.getString(4), cursor.getString(0), cursor.getInt(1),"","","").Total;

                                   if (ValPedCli<pedidoMinimo && pedidoMinimo>0){
                                       SDTPedidosEnviadosItem.NumeroPedido = NumPed+" ERROR (PEDIDO POR DEBAJO DEL MINIMO) valor: "+ValPedCli.toString().trim()+"- MIN = "+pedidoMinimo.toString();
                                   }

                                   Double TotalPedido= resumen.Total; //GestorPedidos.TotalesPedido(getApplicationContext(),cursor.getString(4), cursor.getString(0), cursor.getInt(1),"","","").Total;
                                   int sincupo=0;


                                   if (sincupo==1){
                                       SDTPedidosEnviadosItem.NumeroPedido = NumPed+" \r\n ¡¡¡ ERROR (PEDIDO SUPERA EL CUPO) !!!";
                                   }

                                   //SDTPedidosEnviadosItem.Enviado = cursor.getDouble(2);
                                   SDTPedidosEnviados[vuelta] = SDTPedidosEnviadosItem;
                                   vuelta = vuelta + 1;
                               } while (cursor.moveToNext());
                           }

                           final ListView listview_pedidosenviados = (ListView) findViewById(R.id.listview_pedidosenviados);
                           ListViewAdapterPedidosEnviadosNew = new ListViewAdapterPedidosEnviadosNew(Wp_ResumenPedidosnew.this, SDTPedidosEnviados);
                           listview_pedidosenviados.setAdapter(ListViewAdapterPedidosEnviadosNew);
                           SDTResumenPedidos SDTResumenPedidos = GestorPedidos.TotalesPedido(getApplicationContext(), prefijo, nitsec, clisec,"","","");
                           TextView txt_subtotal = (TextView) findViewById(R.id.txt_abonoori);
                           TextView txt_iva = (TextView) findViewById(R.id.txt_retencion);
                           TextView txt_impoconsumo = (TextView) findViewById(R.id.txt_retencionica);
                           TextView txt_total = (TextView) findViewById(R.id.txt_neto);
                           TextView txt_porenvio = (TextView) findViewById(R.id.txt_porenvio);
                           TextView txt_porenvio7 = (TextView) findViewById(R.id.txt_porenvio7);

                           TextView xtxt_numpedidos = (TextView) findViewById(R.id.txt_numpedidosnuevo);



                           double total = SDTResumenPedidos.Total != null
                                   ? SDTResumenPedidos.Total
                                   : 0.0;

                           double iva = SDTResumenPedidos.Iva != null
                                   ? SDTResumenPedidos.Iva
                                   : 0.0;

                           double impoconsumo = SDTResumenPedidos.Impoconsumo != null
                                   ? SDTResumenPedidos.Impoconsumo
                                   : 0.0;



                           txt_subtotal.setText(String.format("%.2f", SDTResumenPedidos.Subtotal));
                           xtxt_numpedidos.setText(String.valueOf(vuelta));
                           txt_iva.setText(String.format("%,d",iva));
                           txt_impoconsumo.setText(String.format("%,d",impoconsumo));
                           txt_total.setText(String.format("%.2f", total ));
                         // xtxt_numpedidos.setText(String.valueOf(vuelta));


                           Double PorEnvio=0.0;
                           //Integer jj =SDTResumenPedidos.Total.intValue();



                           if( total >0) {
                               PorEnvio= (TotalEnviado / total)*100;
                               txt_porenvio7.setText(String.format("%,d",TotalSinExi.intValue())+"");
                               txt_porenvio.setText(String.format("%,d",PorEnvio.intValue())+"%");
                           }else{
                               txt_porenvio.setText("0%");

                           }




                           if(PorEnvio>=98) {

                           }else{

                           }

                       }catch (Exception e){
                           if ("S".equalsIgnoreCase(modorevision)) {

                               AlertDialog.Builder Alerta = new AlertDialog.Builder(Wp_ResumenPedidosnew.this);
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

        ConBd conbd = new ConBd();
        Connection conn = conbd.CargarConexion(getApplicationContext());
        Statement comm = null;
                ResultSet rsImport = null;
                ResultSet rsImport3 = null;
        String NumPed = "";
        String sExistencia = "";

        if(conn != null){
            try {
                comm = conn.createStatement();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                if ("S".equalsIgnoreCase(modorevision)) {

                    AlertDialog.Builder Alerta = new AlertDialog.Builder(Wp_ResumenPedidosnew.this);
                    Alerta.setMessage( throwables.getMessage());
                    Alerta.setTitle("Error"+"Linea 354");
                    Alerta.setPositiveButton("OK", null);
                    Alerta.setCancelable(true);
                    Alerta.create().show();
                }

            }
        }



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

        try {
                if(conn != null){
                    //Nuevo andres -------------- envio alt por robot
                            try{
                                String rsConsultapen = "select " +
                                        " PedPenNro,pedPenStatus, " +
                                        " pedpenvalor,  " +
                                        " case when pedpenvalor = 0 then \n" +
                                        "cast(pedPenValorNotMal+pedPenValorNotBue as numeric(18,2))\n" +
                                        "else\n" +
                                        "cast(pedpenvalor as numeric(18,2))\n" +
                                        "end\n" +
                                        " as  total  from PedidoPendiente " +
                                        " where PedPenNro  in "+NumPed+" " +
                                        " group by PedPenNro,pedPenStatus,pedpenvalor,pedPenValorNotMal,pedPenValorNotBue ";



                                Log.e("rsConsultapen",rsConsultapen);
                                comm.setQueryTimeout(5);
                                rsImport3 = comm.executeQuery(rsConsultapen);
                                while (rsImport3.next()) {
                                    Log.e("total",rsImport3.getString("total").trim());


                                    String CotNum = rsImport3.getString("PedPenNro").trim();
                                    sExistencia = rsImport3.getString("total").trim();
                                    String estado = rsImport3.getString("pedPenStatus").trim();
                                    String consulta = "update pedido set pedenviado ='"+estado+"',ValEnvAlt='"+sExistencia+"'  where (prefijo||'"+vUsuario+"'||NitSec||CliSec||"+time.year+"||'-'||"+(time.month + 1)+"||'-'||"+time.monthDay+")  = '"+CotNum+"' ";
                                    BaseDeDatos.getWritableDatabase().execSQL(consulta);
                                }
                            }catch (Exception e){
                                Log.e("ErrorPen",e.toString());
                            }


              // Nuevo andres -------------------------------------------
                    try {

                        String rsConsulta = "";
                        if(Tipopedido.equalsIgnoreCase("PED") ){
                             rsConsulta = "select PedNum as FacSecRel, cast(isnull(sum((((PedUni)*\n" +
                                     " (isnull(pedArtValImp,0)+PedPrePub))*\n" +
                                     "(1-(pedDesuno/100))*(1-(pedDesDos/100))*\n" +
                                     "(1-(pedDesTres/100))*(1-(pedDesCua/100)))*\n" +
                                     "  (1+(pedPoriva/100))),0) as numeric(18,2)) val \n" +
                                     " from PedidosDetalle cd left join  Pedidos c on c.pedsec=cd.pedsec" +
                                    " where PedNum in "+NumPed+" and PedVenCod='"+vUsuario.trim()+"' group by PedNum  ";
                        }else {
                            /*rsConsulta = "select FacSecRel ,\n" +
                                    "cast(isnull(sum((((ABS(KarUni)) *\n" +
                                    "(isnull(karArtValImp,0) + karprepub)) *\n" +
                                    "(1-(KarDesuno/100)) * (1-(KarDesDos/100)) *\n" +
                                    "(1-(KarDesTre/100)) * (1-(KarDesCua/100))) *\n" +
                                    "(1+(KarPoriva/100))),0) as numeric(18,2)) val \n" +
                                    "from FacturaKardex cd \n" +
                                    "left join Factura c on c.FacSec = cd.FacSec \n" +
                                    " left join tipos t on t.Tipcod = FacTipCod \n" +
                                    "where FueCod = 'REMI' and  FacEst = 'A' and FacSecRel in " + NumPed + "\n" +
                                    "and FacVenCod = '" + vUsuario.trim() + "'\n" +
                                    "group by FacSecRel";*/


                            rsConsulta = "select FacSecRel,\n" +
                                    "cast(isnull(sum((((ABS(KarUni)) *\n" +
                                    "(isnull(karArtValImp,0) + karprepub)) *\n" +
                                    "(1-(KarDesuno/100)) * (1-(KarDesDos/100)) *\n" +
                                    "(1-(KarDesTre/100)) * (1-(KarDesCua/100))) *\n" +
                                    "(1+(KarPoriva/100))),0) as numeric(18,2)) val\n" +
                                    "from FacturaKardex cd\n" +
                                    "left join Factura c on c.FacSec = cd.FacSec\n" +
                                    "left join tipos t on t.Tipcod = c.FacTipCod\n" +
                                    "where c.FacEst in ('A','B') \n" +
                                    "and c.FacSecRel in " + NumPed + "\n" +
                                    "and c.FacVenCod = '" + vUsuario.trim() + "'\n" +
                                    "and (\n" +
                                    "    t.FueCod = 'REMI'\n" +
                                    "    or (\n" +
                                    "       t.FueCod = 'NCRE'\n" +
                                    "        and not exists (\n" +
                                    "            select 1\n" +
                                    "            from Factura c2   left join  \n" +
                                    "            tipos t2 on c2.factipcod = t2.tipcod  \n" +
                                    "            where c2.FacSecRel = c.FacSecRel \n" +
                                    "            and c2.FacEst = 'A'\n" +
                                    "            and c2.FacVenCod = c.FacVenCod\n" +
                                    "            and t2.FueCod = 'REMI'\n" +
                                    "        )\n" +
                                    "    )\n" +
                                    ")\n" +
                                    "group by FacSecRel";


                        }

                        Log.e("rsConsultarsConsultanewww",rsConsulta);
                        comm.setQueryTimeout(5);
                        rsImport = comm.executeQuery(rsConsulta);

                        //Log.e("rsConsultarsConsulta",rsConsulta);
                        int banact  =0;
                        while (rsImport.next()) {
                            banact +=1;
                            String CotNum = rsImport.getString("FacSecRel").trim();
                            sExistencia = rsImport.getString("val").trim();
                            String consulta = "update pedido set ValEnviado='"+sExistencia+"'  where (prefijo||'"+vUsuario+"'||NitSec||CliSec||"+time.year+"||'-'||"+(time.month + 1)+"||'-'||"+time.monthDay+")  = '"+CotNum+"' ";
                            Log.e("ErrorPenconsulta",consulta);

                            BaseDeDatos.getWritableDatabase().execSQL(consulta);
                        }



                    }catch (Exception e){
                        int hh=0;
                        Log.e("ErrorPen2",e.toString());
                    }/*finally {
                        conn.close();
                    }*/
                }


            }catch (Exception e){
            if ("S".equalsIgnoreCase(modorevision)) {

                AlertDialog.Builder Alerta = new AlertDialog.Builder(Wp_ResumenPedidosnew.this);
                Alerta.setMessage( e.getMessage());
                Alerta.setTitle("Error"+"Linea 447");
                Alerta.setPositiveButton("OK", null);
                Alerta.setCancelable(true);
                Alerta.create().show();
            }
            }
                OrdenImportar += 1;
                cargarDatos();
            }
        }).start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                String returnString = data.getStringExtra("keyName");

                if(returnString.contains("Generado")){
                    /*Intent intent = getIntent();
                    overridePendingTransition(0, 0);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(intent);*/

                    AlertDialog.Builder Alerta = new AlertDialog.Builder(this);
                    Alerta.setMessage(returnString);
                    Alerta.setTitle("Enviado");
                    Alerta.setPositiveButton("OK", null);

                    Alerta.setCancelable(true);
                    Alerta.create().show();
                    cargaenvio();


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
                    cargaenvio();

                }






      /*          String valorenv = data.getStringExtra("valorenv");

                SDTPedidosEnviados[position].valenvalt = valorenv;
                SDTPedidosEnviados[position].ValorPedidoEnvialt = Double.valueOf(valorenv);

                ListViewAdapterPedidosEnviadosNew.notifyDataSetChanged();*/
            }
        }



    }




}
