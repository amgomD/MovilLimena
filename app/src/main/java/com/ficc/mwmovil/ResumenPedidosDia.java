package com.ficc.mwmovil;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ResumenPedidosDia extends AppCompatActivity {

    SDTPedidosEnviados[] SDTPedidosEnviados ;
    Bundle Extras=null;
    ListViewAdapterPedidosEnviados ListViewAdapterPedidosEnviados;

    int dayOfMonth =  AppGlobals.dayOfMonth; // Extras.getInt("dia");
    int month = AppGlobals.month;//Extras.getInt("mes");
    int year = AppGlobals.year;//Extras.getInt("ano");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_pedidos_dia);
        getSupportActionBar().hide();
        Extras=this.getIntent().getExtras();

        final GestorPedidos GestorPedidos = new GestorPedidos();
        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);


        final Time time = new Time();
        time.setToNow();

        if(dayOfMonth > 0){
            time.set(time.second, time.minute, time.hour,dayOfMonth , month, year);
        }
        // final TextView btn_confirmar = (TextView) findViewById(R.id.btn_Capturar);
        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();
        String Empresa=vGlobalVariables.getEmpresa();
        //Log.e("Empresa",Empresa);

        Empresa=Empresa.toUpperCase();
        ConBd conbd = new ConBd();
        conbd.Variables();
        Connection conn = conbd.CargarConexion(getApplicationContext());
        String MantisFicc = conbd.MantisFicc;
        String FacRem = conbd.FiccRem;
        Statement comm = null;
        String Sinbd="N";
        if(conn != null){
            try {
                comm = conn.createStatement();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                Sinbd="S";
            }
        }


        //Sinbd="S";


        try {

            Double pedidoMinimo=0.0;

            Cursor vCursorUsuarios = BaseDeDatos.getReadableDatabase().rawQuery("select ParMovPedMin from usuarios where VenCnt=1  ", null);
            vCursorUsuarios.moveToFirst();

            if (vCursorUsuarios.getCount() >0) {
                pedidoMinimo=vCursorUsuarios.getDouble(0);
            }

            //String nitsec=Extras.getString("nitsec");
            //Integer clisec=Extras.getInt("clisec");
            //String invgrucod=Extras.getString("invgrucod");


            //Cursor cursor = BaseDeDatos.getWritableDatabase().rawQuery("select NitSec,CliSec,NitCom,CliNom from clientes where nitsec in( " +
            //      "select nitsec from pedido where pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and cant<>0) ", null); //order by nombre
            int lisprecod = 1;
            Cursor cursor = null;
            if(Empresa.equalsIgnoreCase("SUHOGAR")){
                cursor = BaseDeDatos.getWritableDatabase().rawQuery("select p.NitSec,ifNULL(c.CliSec,0) CliSec,ifNULL(c.NitCom,'') NitCom,ifNULL(c.CliNom,'') CliNom,ifNULL(prefijo,'') prefijo, ifNull(ConPagnom, '') ConPagnom , pedenviado , pedlisprecod from pedido p left join clientes c on p.nitsec=c.nitsec and p.clisec=c.clisec " +
                        " where pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and ifNULL(cant,0)+ifNULL(cantcaj,0)<>0 group by p.NitSec,p.CliSec,c.NitCom,c.CliNom,prefijo,ConPagnom,pedenviado,c.Lisprecod ", null); //order by nombre
            }else{
                cursor = BaseDeDatos.getWritableDatabase().rawQuery("select p.NitSec,ifNULL(c.CliSec,0) CliSec,ifNULL(c.NitCom,'') NitCom,ifNULL(c.CliNom,'') CliNom,ifNULL(prefijo,'') prefijo, ifNull(ConPagnom, '') ConPagnom , pedenviado , c.lisprecod from pedido p left join clientes c on p.nitsec=c.nitsec and p.clisec=c.clisec " +
                        " where pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and ifNULL(cant,0)+ifNULL(cantcaj,0)<>0 group by p.NitSec,p.CliSec,c.NitCom,c.CliNom,prefijo,ConPagnom,pedenviado,c.Lisprecod ", null); //order by nombre
            }
            int vuelta = 0;
            Double TotalEnviado=0.0;
            Double TotalSinExi=0.0;
            final String[] InvGruCod;
            SDTPedidosEnviados = new SDTPedidosEnviados[cursor.getCount()];
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    lisprecod = cursor.getInt(7);
                    //String NumPedido = Clientes.getString(15) + Clientes.getString(0) + Clientes.getInt(1) + time.year + (time.month + 1) + time.monthDay;
                    String NumPed = cursor.getString(4)+vUsuario.trim()+cursor.getString(0) +  cursor.getInt(1) +  time.year +"-"+  (time.month + 1) +"-"+  time.monthDay  ;
                    final SDTPedidosEnviados SDTPedidosEnviadosItem = new SDTPedidosEnviados();
                    SDTPedidosEnviadosItem.NombreCliente = cursor.getString(2);
                    SDTPedidosEnviadosItem.NombreNegocio = cursor.getString(3);
                    SDTPedidosEnviadosItem.BodNom = cursor.getString(5); //condicion de pago
                    SDTPedidosEnviadosItem.NumeroPedido = NumPed ;
                    SDTPedidosEnviadosItem.LisPreCod = lisprecod;
                    SDTPedidosEnviadosItem.sNitSec = cursor.getString(0);
                    SDTPedidosEnviadosItem.clisec= cursor.getInt(1);
                    SDTPedidosEnviadosItem.prefijo = cursor.getString(4);
                    SDTPedidosEnviadosItem.checkenviado = cursor.getString(6); //andres
                    SDTPedidosEnviadosItem.ValorPedido = GestorPedidos.TotalesPedido(getApplicationContext(), cursor.getString(4), cursor.getString(0), cursor.getInt(1),"","","").Total;
                    SDTPedidosEnviadosItem.selDia = dayOfMonth;
                    SDTPedidosEnviadosItem.selMes = month ;
                    SDTPedidosEnviadosItem.SelAno = year;

                    String sExistencia="0.00";
                    String Bloqueo = "N";
                    String Facturado = "N";

                    if (Sinbd=="N"){
                        try {

                            if(MantisFicc.equalsIgnoreCase("S")){
                                if(FacRem.equalsIgnoreCase("S")){
                                    if(conn != null){
                                        try {
                                            classbd classbd = new classbd();
                                            ResultSet rsImport = comm.executeQuery(classbd.FormatearMysql(" select cast(isnull(sum((((Karuni)*(karprepub))*(1-(karDesuno/100))*\n" +
                                                    "(1-(karDesDos/100))*(1-(karDesTre/100))*(1-(karDesCua/100)))* " +
                                                    "(1+(karPorIva/100))),0) as numeric(18,2)) val \n" +
                                                    "from FacturaKardex cd left join  Factura c on c.FacSec=cd.FacSec " +
                                                    "where (Facnro = '"+NumPed+"' or FacObs3 = '"+NumPed+"' ) and FacVenCod='"+vUsuario.trim()+"'"));
                                            while (rsImport.next()) {
                                                sExistencia = rsImport.getString("val").trim();
                                            }
                                        }catch (Exception e){
                                            int hh=0;
                                        }
                                    }
                                }else{
                                    if(conn != null){
                                        try {
                                            classbd classbd = new classbd();
                                            ResultSet rsImport = comm.executeQuery(classbd.FormatearMysql("select cast(isnull(sum((((PedUni)*(PedPrePub))*(1-(PedDesuno/100))*(1-(PedDesDos/100))*(1-(PedDesTres/100))*(1-(PedDesCua/100)))*(1+(PedPorIva/100))),0) as numeric(18,2)) val from PedidosDetalle cd left join  Pedidos c on c.pedsec=cd.pedsec \n" +
                                                    "where pednum='"+NumPed+"' and PedVenCod='"+vUsuario.trim()+"'"));
                                            while (rsImport.next()) {
                                                sExistencia = rsImport.getString("val").trim();
                                            }
                                        }catch (Exception e){
                                            int hh=0;
                                        }
                                    }
                                }

                            }else{
                                if(conn != null){

                                    try {

                                        ResultSet rsImport = comm.executeQuery("select cast(isnull(sum(((((CotArtCaj*CotArtEmb)+CotArtUni)*(isnull(CotArtValImp,0)+CotArtPrecio))*(1-(CotArtDesUno/100))*(1-(CotArtDesDos/100))*(1-(CotArtDesTre/100))*(1-(CotArtDesCua/100)))*(1+(CotPorIva/100))),0) as numeric(18,2)) val from CotizacionesDetalle1 cd left join  Cotizaciones1 c on c.CotSec=cd.CotSec \n" +
                                                "where cotnum='"+NumPed+"' and cotsubvencod='"+vUsuario.trim()+"'");

                                        while (rsImport.next()) {
                                            sExistencia = rsImport.getString("val").trim();

                                        }



                                        if (conbd.ActulizaOnline=="S" && !Empresa.trim().equalsIgnoreCase("FARMA")&& !Empresa.trim().equalsIgnoreCase("MENTAHAIRCOT")) {

                                            if(Empresa.trim().equalsIgnoreCase("DINGLESA")){
                                                try{
                                                    String consql = "select cast(ISNULL(SUM(karvaltotmendes+karartiva),0) as numeric(18,2)) val,isnull(FacAliBloRem,'N') Bloqueo, isnull((Select 'S' from Kardex nk where nk.FacSecRem = cd.FacSec),'N') facturado  from kardex cd left join  factura c on c.facsec=cd.facsec \n" +
                                                            "where FacEst = 'A' and  facnro='" + NumPed + "' and facvencod='"+vUsuario.trim()+"' group by cd.FacSec,FacNro,FacAliBloRem ";

                                                    ResultSet rsImport2 = comm.executeQuery(consql);

                                                    while (rsImport2.next()) {
                                                        sExistencia = rsImport2.getString("val").trim();
                                                        Bloqueo = rsImport2.getString("Bloqueo").trim();
                                                        Facturado = rsImport2.getString("facturado").trim();

                                                    }
                                                } catch (Exception e) {
                                                    Log.e("Erro a traer",e.toString());
                                                }

                                            }else{

                                                String consql = "select cast(ISNULL(SUM(karvaltotmendes+karartiva),0) as numeric(18,2)) val from kardex cd left join  factura c on c.facsec=cd.facsec \n" +
                                                        "where facnro='" + NumPed + "' and facvencod='"+vUsuario.trim()+"'";

                                                ResultSet rsImport2 = comm.executeQuery(consql);

                                                while (rsImport2.next()) {
                                                    sExistencia = rsImport2.getString("val").trim();

                                                }
                                            }
                                        }
                                    }catch (Exception e){
                                        Log.e("Erros :::",e.toString());
                                        int hh=0;

                                        // vSDTClientesNuevosItem.Respuesta = "Error de comunicacion con el servidor (servidor no disponible)";
                                    }
                                }
                            }


                        }catch (Exception e){
                            int hh=0;
                        }
                    }




                    //SDTPedidosEnviadosItem.NombreCliente = cursor.getString(2)+' '+sExistencia;
                    SDTPedidosEnviadosItem.sEnviado = sExistencia; //GestorPedidos.PeidoEnviado(getApplicationContext(), cursor.getString(4), cursor.getString(0), cursor.getInt(1));
                    SDTPedidosEnviadosItem.ValorPedidoEnviado=Double.valueOf(sExistencia);


                    SDTPedidosEnviadosItem.sEnviadoExi=GestorPedidos.SinExistencia;
                    TotalEnviado+=SDTPedidosEnviadosItem.ValorPedidoEnviado;
                    TotalSinExi+=Double.valueOf(GestorPedidos.SinExistencia);

                    Double ValPed= GestorPedidos.TotalesPedido(getApplicationContext(), cursor.getString(4), cursor.getString(0), cursor.getInt(1),"","","").Subtotal;
                    Double ValPedCli = GestorPedidos.TotalesPedidocliente(getApplicationContext(), cursor.getString(4), cursor.getString(0), cursor.getInt(1),"","","").Total;

                    if (ValPedCli<pedidoMinimo && pedidoMinimo>0){
                        SDTPedidosEnviadosItem.NumeroPedido = NumPed+" ERROR (PEDIDO POR DEBAJO DEL MINIMO) valor: "+ValPedCli.toString().trim()+"- MIN = "+pedidoMinimo.toString();
                    }

                    Double TotalPedido= GestorPedidos.TotalesPedido(getApplicationContext(),cursor.getString(4), cursor.getString(0), cursor.getInt(1),"","","").Total;

                    //if ((pedidoMinimo!=0 && TotalPedido>pedidoMinimo ) || pedidoMinimo==0) {
                    int sincupo=0;
                    Integer Mora = 0;
                    if ((Empresa.trim().equalsIgnoreCase("MENTAHAIR") ) ||(Empresa.trim().equalsIgnoreCase("DINGLESA") ) || MantisFicc.equalsIgnoreCase("S") ) {
                        GestorCartera gestorcartera=new GestorCartera();
                        gestorcartera.TotalesCatera(getApplicationContext(), cursor.getString(0), cursor.getInt(1));
                        Integer CarteraGeneral =gestorcartera.CarteraGeneral;
                        Mora =  gestorcartera.MoraGeneral;
                        Log.e("kmora: ",String.valueOf(Mora));
                        Log.e("cursor.getString(0): ",cursor.getString(0));
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
                        if (cupo-CarteraGeneral-TotalPedido<0 && (CliConPag != 0 && cupo != 0 ) ){
                            sincupo=1;
                        }
                    }
                    SDTPedidosEnviadosItem.Mora ="N";
                    String strbloq = "N";

                    if (sincupo==1){
                        SDTPedidosEnviadosItem.NumeroPedido = NumPed+" \r\n ¡¡¡ ERROR (PEDIDO SUPERA EL CUPO) !!!";
                        strbloq = "S";
                        SDTPedidosEnviadosItem.Mora ="S";
                    }
                    if(Empresa.equalsIgnoreCase("DINGLESA")){
                        if(Mora > 0 ){
                            SDTPedidosEnviadosItem.Mora ="S";
                            strbloq = "S";
                            SDTPedidosEnviadosItem.NumeroPedido +=  "\r\n ¡¡¡ ERROR Cliente con mora !!!";
                        }

                        if(Bloqueo.equalsIgnoreCase("S")) {
                            strbloq = "S";
                            SDTPedidosEnviadosItem.NumeroPedido += " \r\n Remision Bloqueada";
                            SDTPedidosEnviadosItem.Mora ="S";
                        }

                        if
                        (Facturado.equalsIgnoreCase("S")){
                            strbloq = "S";
                            SDTPedidosEnviadosItem.NumeroPedido += "\r\nPedido Facturado";
                            SDTPedidosEnviadosItem.Mora ="S";

                        }
                        Actualizarbloqueo(SDTPedidosEnviadosItem.prefijo,SDTPedidosEnviadosItem.sNitSec,SDTPedidosEnviadosItem.clisec,strbloq);
                    }

                    //SDTPedidosEnviadosItem.Enviado = cursor.getDouble(2);
                    SDTPedidosEnviados[vuelta] = SDTPedidosEnviadosItem;
                    vuelta = vuelta + 1;
                } while (cursor.moveToNext());
            }

            final ListView listview_pedidosenviados = (ListView) findViewById(R.id.listview_pedidosenviados);
            ListViewAdapterPedidosEnviados = new ListViewAdapterPedidosEnviados(this, SDTPedidosEnviados);
            listview_pedidosenviados.setAdapter(ListViewAdapterPedidosEnviados);



            SDTResumenPedidos SDTResumenPedidos = GestorPedidos.TotalesPedido(getApplicationContext(), "", "", 0,"","","");

            TextView txt_subtotal = (TextView) findViewById(R.id.txt_abonoori);
            TextView txt_iva = (TextView) findViewById(R.id.txt_retencion);
            TextView txt_impoconsumo = (TextView) findViewById(R.id.txt_retencionica);
            TextView txt_total = (TextView) findViewById(R.id.txt_neto);
            TextView txt_porenvio = (TextView) findViewById(R.id.txt_porenvio);
            TextView txt_porenvio7 = (TextView) findViewById(R.id.txt_porenvio7);

            TextView txt_numpedidos = (TextView) findViewById(R.id.txt_numpedidos);

            txt_subtotal.setText(String.format("%.2f", SDTResumenPedidos.Subtotal));
            txt_iva.setText(String.format("%.2f", SDTResumenPedidos.Iva));
            txt_impoconsumo.setText(String.format("%.2f", SDTResumenPedidos.Impoconsumo));
            txt_total.setText(String.format("%.2f", SDTResumenPedidos.Total));
            txt_numpedidos.setText(String.format("%.2f",vuelta));




            Double PorEnvio=0.0;
            Integer jj =SDTResumenPedidos.Total.intValue();
            if(SDTResumenPedidos.Total>0) {
                PorEnvio= (TotalEnviado / SDTResumenPedidos.Total)*100;
                txt_porenvio7.setText(String.format("%.2f",TotalSinExi)+"");
                txt_porenvio.setText(String.format("%.2f",PorEnvio)+"%");
            }else{
                txt_porenvio.setText("0%");
            }
            if(PorEnvio>=98) {
                //btn_confirmar.setVisibility(View.VISIBLE);
                //      btn_confirmar.setVisibility(View.GONE);
            }else{
                //    btn_confirmar.setVisibility(View.GONE);
            }

        }catch (Exception e){
            int hh=0;
        }









        final Button btn_enviarpedidos = (Button) findViewById(R.id.btn_enviarpedidos);

        btn_enviarpedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String Empresa=vGlobalVariables.getEmpresa();
                String Mensaje = "";
                if(banderadia().equalsIgnoreCase("S")){
                    if(Empresa.equalsIgnoreCase("IBANEZ") || Empresa.equalsIgnoreCase("IBANEZPRU")
                            || Empresa.equalsIgnoreCase("MENTAHAIR")|| Empresa.equalsIgnoreCase("MENTAHAIRCOT")
                            || Empresa.equalsIgnoreCase("ACOAVANZAR")|| Empresa.equalsIgnoreCase("ACOAVANZARPRU")
                    ){

                        Intent intent = new Intent(getApplicationContext(), ConfirmarEnvio.class);
                        intent.putExtra("total",SDTPedidosEnviados.length);
                        intent.putExtra("clisec",0);
                        intent.putExtra("nitsec","");
                        intent.putExtra("artsec","");
                        intent.putExtra("prefijo","");
                        startActivityForResult(intent, 3);


                    }else{
                        if(MantisFicc.equalsIgnoreCase("S")){
                            Mensaje = GestorPedidos.EnviarPedidosFiccGx5(getApplicationContext(), "", "", 0, 0);
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


                // finish();
                //  startActivity(getIntent());
            }
        });





    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                String returnString = data.getStringExtra("keyName");
                AlertDialog.Builder Alerta = new AlertDialog.Builder(ResumenPedidosDia.this);
                Alerta.setMessage("Respuesta envio: "+returnString);
                Alerta.setTitle("Notificacion");
                Alerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = getIntent();
                        overridePendingTransition(0, 0);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(intent);
                    }
                });
                Alerta.setCancelable(true);
                Alerta.create().show();

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

            }else{
                bander = "S";
            }






        } catch (Exception e) {
            Log.e("error",e.toString());
            bander = "S";
        }


        return bander;
    }


    public void Actualizarbloqueo(String Prefijo,String NitSec,int CliSec,String bloqueo){

        try{
            Time time = new Time();
            time.setToNow();

            if(dayOfMonth > 0){
                time.set(time.second, time.minute, time.hour,dayOfMonth , month, year);
            }
            BaseDatos BaseDeDatos;
            BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
            BaseDeDatos.getWritableDatabase().execSQL("update pedido set  Bloqueo= '"+bloqueo+"'  where nitsec='" +NitSec + "' and clisec=" + CliSec+" and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo='" + Prefijo+ "'");

        } catch (Exception e) {
            Log.e("ErrorUpdate",e.toString());
        }

    }

}
