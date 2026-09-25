package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;

import android.text.format.Time;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GestorPedidos {
    String SinExistencia="0.00";
    int dayOfMonth =  AppGlobals.dayOfMonth; // Extras.getInt("dia");
    int month = AppGlobals.month;//Extras.getInt("mes");
    int year = AppGlobals.year;//Extras.getInt("ano");

    public void CalcularBonificados(Context pContext, String ArtSec, String Prefijo, String NitSec, Integer CliSec, Double Unidades, Integer Cajas, Integer EFmbalaje) {

        Cursor ConMovPar = null;
        Time time = new Time();
        time.setToNow();

        try {  // valcula bonificados de descuentso v1
            final BaseDatos BaseDeDatos;
            BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 5);
            //String Pedido = Extras.getString("tipdoc") + Extras.getString("Codvend") + "-" + time.year + "-" + (time.month + 1) + "-" + time.monthDay + "-" + Extras.getString("nitsec") + "-" + Extras.getInt("clisec");
            Integer Tcant=0;
            Integer MovParPremSec=0;
            String MovParBonEncArtSec="";
            String Consulta = "select MovParBonSec,MovParBonEncArtSec,MovParBonCant,(MovParBonCantCaj*MovParBonEmb)+MovParBonCant Totuni from MovParBonProdBon left join Clientes  c on NitSec='"+NitSec+"' and CliSec="+CliSec+" "+
                    "where MovParBonEncArtSec='"+ArtSec+"'  " +
                    "   and (MovParBonCanales='XX,' OR MovParBonCanales like '%,'|| c.CanCod ||',%') " +
                    "   and (MovParBonClientes='XX,' OR MovParBonClientes like '%,'|| NitSec ||',%') " +
                    "   and (MovParBonClientesExlu='XX,' OR MovParBonClientesExlu NOT like '%,'|| NitSec ||',%') ";

            Log.e("Entro acaaaaaaaaaaaaa",NitSec);

            Cursor MovParBonProdBon = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null);
            if (MovParBonProdBon.getCount() > 0) {
                Log.e("Entro aca","==========================================");
                MovParBonProdBon.moveToFirst();
                Integer MovParBonSec=MovParBonProdBon.getInt(0);
                BaseDeDatos.getWritableDatabase().execSQL("Delete from MovParPrem where Prefijo='" + Prefijo + "' and MovParNitSec='"+NitSec+"' and MovParCliSec="+CliSec+" and MovParPremSec=" + MovParBonSec+ " and MovParPremTip='BNP' and MovParPremArtSecOri='"+ArtSec+"'");
                do {
                    if (MovParBonProdBon.getInt(2)>0) {
                        MovParBonSec=MovParBonProdBon.getInt(0);
                        BaseDeDatos.getWritableDatabase().execSQL("Delete from MovParPrem where Prefijo='" + Prefijo + "' and MovParNitSec='"+NitSec+"' and MovParCliSec="+CliSec+" and MovParPremSec=" + MovParBonSec+ " and MovParPremTip='BNP' and MovParPremArtSecOri='"+ArtSec+"'");
                        Tcant = (Unidades.intValue()+(Cajas*Cajas))/ MovParBonProdBon.getInt(3);
                        if (Tcant>=1){
                            String TempBon = "select MovParBonSec,MovParBonArtSec,MovParBonDetCant,MovParBonDetCantCaj from MovParBonBonificados where MovParBonSec="+MovParBonSec;
                            Cursor MovParBonBonificados = BaseDeDatos.getWritableDatabase().rawQuery(TempBon, null);
                            if (MovParBonBonificados.getCount() > 0) {
                                MovParBonBonificados.moveToFirst();
                                do {
                                    if (MovParBonBonificados.getInt(2) > 0) {
                                        String MovParBonArtSec=MovParBonBonificados.getString(1);
                                        String Consulta2 = "insert into MovParPrem(Prefijo,MovParNitSec,MovParCliSec,MovParPremSec,MovParPremTip,MovParPremArtSec,MovParPremCant,MovParPremCantCaj,MovParPremDesc,MovParPremArtSecOri,MovParPremAno,MovParPremMes,MovParPremDia)values('" + Prefijo + "','"+NitSec+"',"+CliSec+"," + MovParBonSec + ",'BNP','" + MovParBonArtSec.trim() + "'," + ((MovParBonBonificados.getInt(2)) * Tcant) + ","+MovParBonBonificados.getInt(3)+",0,'"+ArtSec+"',"+time.year + "," + (time.month + 1) + "," +time.monthDay +")";
                                        try {
                                            BaseDeDatos.getWritableDatabase().execSQL(Consulta2);
                                        } catch (Exception e) {
                                            int jj = 0;
                                        }
                                    }
                                }while (MovParBonBonificados.moveToNext()) ;
                            }
                        }
                    }
                } while (MovParBonProdBon.moveToNext());
            }
        } catch (Exception e) {
            int pp = 0;
        }

    }




    public Integer TotalClientesDia(Context pContext){

        BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 6);

        Time time = new Time();
        time.setToNow();
        final int day=time.weekDay;

        String OrdenConsulta=" order by ";
        String WhereConsulta="";
        switch(day) {
            case 1 :
                OrdenConsulta+=" cliintlun desc ,CliIntOrdDet ";
                WhereConsulta+=" cliintlun='S' ";
                break;
            case 2 :
                OrdenConsulta+=" cliintmar desc,CliIntOrdDet ";
                WhereConsulta+=" cliintmar='S' ";
                break;
            case 3 :
                OrdenConsulta+=" cliintmie desc,CliIntOrdDet ";
                WhereConsulta+=" cliintmie='S' ";
                break;
            case 4 :
                OrdenConsulta+=" cliintjue desc,CliIntOrdDet ";
                WhereConsulta+=" cliintjue='S' ";
                break;
            case 5 :
                OrdenConsulta+=" cliintvie desc,CliIntOrdDet ";
                WhereConsulta+=" cliintvie='S' ";
                break;
            case 6 :
                OrdenConsulta+=" cliintsab desc,CliIntOrdDet " ;
                WhereConsulta+=" cliintsab='S' ";
                break;
            case 7 :
                OrdenConsulta+=" cliintdom,CliIntOrdDet desc";
                WhereConsulta+=" cliintdom='S' ";
                break;
            default :
                OrdenConsulta="";
        }

        WhereConsulta=" where "+WhereConsulta;

        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();
        String vEmpresa = vGlobalVariables.getEmpresa();
        String Consulta="";

      if(vEmpresa.equalsIgnoreCase("SURTIMARCAS")){
          Consulta="select count(*) from clientes c "+WhereConsulta+ " Group by NitSec, clisec " ;
      }else{
          Consulta="select count(*) from clientes c "+WhereConsulta+ " Group by NitSec " ;
      }

        Cursor cursor = BaseDeDatos.getReadableDatabase().rawQuery(Consulta, null);

        Integer vuelta=0;
        if (cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                vuelta+=cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        return vuelta;
    }
    public SDTResumenPedidos TotalesPedidocliente(Context pContext,String Prefijo,String NitSec,Integer CliSec,String InvGruCod,String InvSubGruCod,String InvFamCod){

        SDTResumenPedidos SDTResumenPedidos=new SDTResumenPedidos();

        SDTResumenPedidos.Subtotal=0.0;
        SDTResumenPedidos.Iva=0.0;
        SDTResumenPedidos.Impoconsumo=0.0;
        SDTResumenPedidos.Total=0.0;
        SDTResumenPedidos.NumPedidos=0;

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 5);

        Time time = new Time();
        time.setToNow();


        String WhereAdicional="";
      /*  if(!Prefijo.isEmpty()){
            WhereAdicional+=" and prefijo='"+Prefijo+"' ";
        }*/
        if(!NitSec.isEmpty()){
            WhereAdicional+=" and p.nitsec='"+NitSec+"' ";
        }if(CliSec>0){
            WhereAdicional+=" and p.clisec="+CliSec+" ";
        }
        if(!InvGruCod.isEmpty()){
            WhereAdicional+=" and InvGruCod='"+InvGruCod+"' ";
        }
        if(!InvSubGruCod.isEmpty()){
            WhereAdicional+=" and InvSubGruCod='"+InvSubGruCod+"' ";
        }
        if(!InvFamCod.isEmpty()){
            WhereAdicional+=" and InvFamCod='"+InvFamCod+"' ";
        }
        Integer LisPreCod=1;
        Cursor cursor = BaseDeDatos.getReadableDatabase().rawQuery("select LisPreCod from clientes where nitsec='"+NitSec+"' and clisec="+CliSec, null);
        Integer vuelta=0;
        if (cursor.getCount()>0) {
            cursor.moveToFirst();
            do {
                LisPreCod=cursor.getInt(0);
            }while (cursor.moveToNext());
        }

        //      if (LisPreCod>10){
        //         LisPreCod=LisPreCod-10;
        //      }
        String List="Precio"+LisPreCod;
        try {
            //List="(case when LisPreCod=1  then precio1 else 0 end+case when LisPreCod=2 then precio2 else 0 end+case when LisPreCod=3 then precio3 else 0 end+case when LisPreCod=4 then precio4 else 0 end+case when LisPreCod=5 then precio5 else 0 end+case when LisPreCod=6 then precio6 else 0 end+case when LisPreCod=7  then precio7 else 0 end+case when LisPreCod=8 then precio8 else 0 end+case when LisPreCod=9 then precio9 else 0 end+case when LisPreCod=10 then precio10 else 0 end+case when LisPreCod=11 then precio11 else 0 end+case when LisPreCod=12 then precio12 else 0 end+case when LisPreCod=13 then precio13 else 0 end+case when LisPreCod=14 then precio14 else 0 end+case when LisPreCod=15 then precio15 else 0 end+case when LisPreCod=16 then precio16 else 0 end+case when LisPreCod=17 then precio17 else 0 end+case when LisPreCod=18 then precio18 else 0 end+case when LisPreCod=19 then precio19 else 0 end+case when LisPreCod=20 then precio20 else 0 end+case when LisPreCod=21 then precio21 else 0 end+case when LisPreCod=22 then precio22 else 0 end+case when LisPreCod=23 then precio23 else 0 end+case when LisPreCod=24 then precio24 else 0 end+case when LisPreCod=25 then precio25 else 0 end+case when LisPreCod=26 then precio26 else 0 end+case when LisPreCod=27 then precio27 else 0 end+case when LisPreCod=28 then precio28 else 0 end+case when LisPreCod=29 then precio29 else 0 end+case when LisPreCod=30 then precio30 else 0 end)";
            List="Precio";
            String Consulta = "select total(((( (("+List+" * ((100.00-p.pordesc)/100.00) * ((100.00-p.pordesc2)/100.00) * ((100.00-p.pordesc3)/100.00)* ((100.00-p.pordesc4)/100.00) * ((100.00-p.pordesc5)/100.00)* ((100.00-p.pordesc6)/100.00)  ))  *(p.cant+(ifnull(cantcaj,0)*a.ArtEmb)))*(case  cliiva when 'S' then ParConIva else 0 end))/100)+( ((  "+List+" * ((100.00-p.pordesc)/100.00) * ((100.00-p.pordesc2)/100.00) * ((100.00-p.pordesc3)/100.00)* ((100.00-p.pordesc4)/100.00) * ((100.00-p.pordesc5)/100.00)* ((100.00-p.pordesc6)/100.00) ) ) *(p.cant+(ifnull(cantcaj,0)*a.ArtEmb)))+arttotimp) total,total(( ((  "+List+" * ((100.00-p.pordesc)/100.00) * ((100.00-p.pordesc2)/100.00) * ((100.00-p.pordesc3)/100.00) * ((100.00-p.pordesc4)/100.00) * ((100.00-p.pordesc5)/100.00)* ((100.00-p.pordesc6)/100.00)   ) ) *(p.cant+(ifnull(cantcaj,0)*a.ArtEmb)))) subtotal,total(ifnull(arttotimp,0)) Totalimpoconsumo,total(((( (("+List+" * ((100.00-p.pordesc)/100.00) * ((100.00-p.pordesc2)/100.00) * ((100.00-p.pordesc3)/100.00) * ((100.00-p.pordesc4)/100.00) * ((100.00-p.pordesc5)/100.00) * ((100.00-p.pordesc6)/100.00)   ))  *(p.cant+(ifnull(cantcaj,0)*a.ArtEmb)))*(case  cliiva when 'S' then ParConIva else 0 end))/100)) totaliva,prefijo,p.nitsec,p.clisec from pedido p left join articulos a on rtrim(a.artsec)=rtrim(p.artsec) left join  clientes c on c.nitsec=p.NitSec and c.clisec=p.CliSec where " +
                    " pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and cant+ifnull(cantcaj,0)<>0 " + WhereAdicional + " Group by prefijo,p.nitsec,p.clisec";
            Cursor Clientes = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null);

        /*Double TotPed = 0.00;
        Double TotNumPed = 0.00;
        Double TotPedIva = 0.00;*/

            Double Subtotal = 0.00;
            Double Iva = 0.00;
            Double Impoconsumo = 0.00;
            Double Total = 0.00;
            Integer TotNumPedGen = 0;
            if (Clientes.getCount() > 0) {
                Clientes.moveToFirst();
                do {
                /*if (Clientes.getDouble(0)>=15000) {
                    TotPed += Clientes.getDouble(1);
                    TotPedIva += Clientes.getDouble(0);
                    TotNumPed+=1;
                }*/

                    if(Clientes.getString(4).equalsIgnoreCase("NC")){
                        Subtotal -= Clientes.getDouble(1);
                        Iva -= Clientes.getDouble(3);
                        Impoconsumo -= Clientes.getDouble(2);
                        Total -= Clientes.getDouble(0);
                        TotNumPedGen += 1;
                    }else{
                        Subtotal += Clientes.getDouble(1);
                        Iva += Clientes.getDouble(3);
                        Impoconsumo += Clientes.getDouble(2);
                        Total += Clientes.getDouble(0);
                        TotNumPedGen += 1;
                    }

                } while (Clientes.moveToNext());
            }

            SDTResumenPedidos.Subtotal=Subtotal;
            SDTResumenPedidos.Iva=Iva;
            SDTResumenPedidos.Impoconsumo=Impoconsumo;
            SDTResumenPedidos.Total=Total;
            SDTResumenPedidos.NumPedidos=TotNumPedGen;

        }catch (Exception e){
            Integer jj=0;
        }
        return SDTResumenPedidos;
    }

    public SDTResumenPedidos TotalesPedido(Context pContext,String Prefijo,String NitSec,Integer CliSec,String InvGruCod,String InvSubGruCod,String InvFamCod){

        SDTResumenPedidos SDTResumenPedidos=new SDTResumenPedidos();

        SDTResumenPedidos.Subtotal=0.0;
        SDTResumenPedidos.Iva=0.0;
        SDTResumenPedidos.Impoconsumo=0.0;
        SDTResumenPedidos.Total=0.0;
        SDTResumenPedidos.NumPedidos=0;
        SDTResumenPedidos.CreditoBueno =0.0;
        SDTResumenPedidos.CreditoMalo  =0.0;

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 5);

        Time time = new Time();
        time.setToNow();

        if(dayOfMonth > 0){
            time.set(time.second, time.minute, time.hour,dayOfMonth , month, year);
        }




        String WhereAdicional="";
        if(!Prefijo.isEmpty()){
            WhereAdicional+=" and prefijo='"+Prefijo+"' ";
        }
        if(!NitSec.isEmpty()){
            WhereAdicional+=" and p.nitsec='"+NitSec+"' ";

        }if(CliSec>0){
            WhereAdicional+=" and p.clisec="+CliSec+" ";
        }
        if(!InvGruCod.isEmpty()){
            WhereAdicional+=" and InvGruCod='"+InvGruCod+"' ";
        }
        if(!InvSubGruCod.isEmpty()){
            WhereAdicional+=" and InvSubGruCod='"+InvSubGruCod+"' ";
        }
        if(!InvFamCod.isEmpty()){
            WhereAdicional+=" and InvFamCod='"+InvFamCod+"' ";
        }
        Integer LisPreCod=1;
        Cursor cursor = BaseDeDatos.getReadableDatabase().rawQuery("select LisPreCod from clientes where nitsec='"+NitSec+"' and clisec="+CliSec, null);
        Integer vuelta=0;
        if (cursor.getCount()>0) {
            cursor.moveToFirst();
            do {
                LisPreCod=cursor.getInt(0);
            }while (cursor.moveToNext());
        }

  //      if (LisPreCod>10){
   //         LisPreCod=LisPreCod-10;
  //      }
        String List="Precio"+LisPreCod;
try {
    //List="(case when LisPreCod=1  then precio1 else 0 end+case when LisPreCod=2 then precio2 else 0 end+case when LisPreCod=3 then precio3 else 0 end+case when LisPreCod=4 then precio4 else 0 end+case when LisPreCod=5 then precio5 else 0 end+case when LisPreCod=6 then precio6 else 0 end+case when LisPreCod=7  then precio7 else 0 end+case when LisPreCod=8 then precio8 else 0 end+case when LisPreCod=9 then precio9 else 0 end+case when LisPreCod=10 then precio10 else 0 end+case when LisPreCod=11 then precio11 else 0 end+case when LisPreCod=12 then precio12 else 0 end+case when LisPreCod=13 then precio13 else 0 end+case when LisPreCod=14 then precio14 else 0 end+case when LisPreCod=15 then precio15 else 0 end+case when LisPreCod=16 then precio16 else 0 end+case when LisPreCod=17 then precio17 else 0 end+case when LisPreCod=18 then precio18 else 0 end+case when LisPreCod=19 then precio19 else 0 end+case when LisPreCod=20 then precio20 else 0 end+case when LisPreCod=21 then precio21 else 0 end+case when LisPreCod=22 then precio22 else 0 end+case when LisPreCod=23 then precio23 else 0 end+case when LisPreCod=24 then precio24 else 0 end+case when LisPreCod=25 then precio25 else 0 end+case when LisPreCod=26 then precio26 else 0 end+case when LisPreCod=27 then precio27 else 0 end+case when LisPreCod=28 then precio28 else 0 end+case when LisPreCod=29 then precio29 else 0 end+case when LisPreCod=30 then precio30 else 0 end)";
    List="Precio";
    String Consulta = "select " +
            "  total(((( (("+List+" " +
            " * ((100.00-p.pordesc)/100.00)  " +
            " * ((100.00-p.pordesc2)/100.00) " +
            " * ((100.00-p.pordesc3)/100.00)" +
            " * ((100.00-p.pordesc4)/100.00) " +
            " * ((100.00-p.pordesc5)/100.00)" +
            " * ((100.00-p.pordesc6)/100.00)  ))  " +
            " * (p.cant+0))*(case  cliiva when 'S' then ParConIva else 0 end))/100)" +
            " +( ((  "+List+"" +
            " * ((100.00-p.pordesc)/100.00) " +
            " * ((100.00-p.pordesc2)/100.00) " +
            " * ((100.00-p.pordesc3)/100.00)" +
            " * ((100.00-p.pordesc4)/100.00) " +
            " * ((100.00-p.pordesc5)/100.00)" +
            " * ((100.00-p.pordesc6)/100.00) ) ) " +
            " *(p.cant+0))+arttotimp)  total," +
            "" +
            " total(( ((  "+List+" " +
            " * ((100.00-p.pordesc)/100.00) " +
            " * ((100.00-p.pordesc2)/100.00) " +
            " * ((100.00-p.pordesc3)/100.00) " +
            " * ((100.00-p.pordesc4)/100.00) " +
            " * ((100.00-p.pordesc5)/100.00) " +
            " * ((100.00-p.pordesc6)/100.00)   ) ) " +
            " *(p.cant+0))) subtotal," +
            "  " +
            " total(ifnull(arttotimp,0)) Totalimpoconsumo," +
            "" +
            " total(((( (("+List+" " +
            " * ((100.00-p.pordesc)/100.00) " +
            " * ((100.00-p.pordesc2)/100.00) " +
            " * ((100.00-p.pordesc3)/100.00) " +
            " * ((100.00-p.pordesc4)/100.00) " +
            " * ((100.00-p.pordesc5)/100.00) " +
            " * ((100.00-p.pordesc6)/100.00)   ))  " +
            " *(p.cant+0))" +
            " *(case  cliiva when 'S' then ParConIva else 0 end))/100))  totaliva," +
            "" +
            " prefijo," +
            " p.nitsec," +
            " p.clisec "+
            " from pedido p " +
            " left join articulos a on rtrim(a.artsec)=rtrim(p.artsec) " +
            " left join  clientes c on c.nitsec=p.NitSec and c.clisec=p.CliSec " +
            " where" +
            " ifnull(NotaInv,'N') = 'N' and ifnull(NotaCar,'N') = 'N'  and " +
            "   pdyear=" + time.year + " " +
            " and pdmonth=" + (time.month + 1) + " " +
            " and pdday=" + time.monthDay + " " +
            " and cant+ifnull(cantinf,0)<>0 " + WhereAdicional + " " +
            " Group by prefijo,p.nitsec,p.clisec";



    String ConsultaMalo = "select " +
            "  total(((( (("+List+" " +
            " * ((100.00-p.pordesc)/100.00)  " +
            " * ((100.00-p.pordesc2)/100.00) " +
            " * ((100.00-p.pordesc3)/100.00)" +
            " * ((100.00-p.pordesc4)/100.00) " +
            " * ((100.00-p.pordesc5)/100.00)" +
            " * ((100.00-p.pordesc6)/100.00)  ))  " +
            " * (p.cant+0))*(case  cliiva when 'S' then ParConIva else 0 end))/100)" +
            " +( ((  "+List+"" +
            " * ((100.00-p.pordesc)/100.00) " +
            " * ((100.00-p.pordesc2)/100.00) " +
            " * ((100.00-p.pordesc3)/100.00)" +
            " * ((100.00-p.pordesc4)/100.00) " +
            " * ((100.00-p.pordesc5)/100.00)" +
            " * ((100.00-p.pordesc6)/100.00) ) ) " +
            " *(p.cant+0))+arttotimp)  total " +
            " from pedido p " +
            " left join articulos a on rtrim(a.artsec)=rtrim(p.artsec) " +
            " left join  clientes c on c.nitsec=p.NitSec and c.clisec=p.CliSec " +
            " where" +
            " ifnull(NotaInv,'N') = 'N' and ifnull(NotaCar,'N') = 'S'  and " +
            "   pdyear=" + time.year + " " +
            " and pdmonth=" + (time.month + 1) + " " +
            " and pdday=" + time.monthDay + " " +
            " and cant+ifnull(cantinf,0)<>0 " + WhereAdicional + " " +
            " Group by prefijo,p.nitsec,p.clisec";


    String ConsultaBueno = "select " +
            "  total(((( (("+List+" " +
            " * ((100.00-p.pordesc)/100.00)  " +
            " * ((100.00-p.pordesc2)/100.00) " +
            " * ((100.00-p.pordesc3)/100.00)" +
            " * ((100.00-p.pordesc4)/100.00) " +
            " * ((100.00-p.pordesc5)/100.00)" +
            " * ((100.00-p.pordesc6)/100.00)  ))  " +
            " * (p.cant+0))*(case  cliiva when 'S' then ParConIva else 0 end))/100)" +
            " +( ((  "+List+"" +
            " * ((100.00-p.pordesc)/100.00) " +
            " * ((100.00-p.pordesc2)/100.00) " +
            " * ((100.00-p.pordesc3)/100.00)" +
            " * ((100.00-p.pordesc4)/100.00) " +
            " * ((100.00-p.pordesc5)/100.00)" +
            " * ((100.00-p.pordesc6)/100.00) ) ) " +
            " *(p.cant+0))+arttotimp)  total " +
            " from pedido p " +
            " left join articulos a on rtrim(a.artsec)=rtrim(p.artsec) " +
            " left join  clientes c on c.nitsec=p.NitSec and c.clisec=p.CliSec " +
            " where" +
            " ifnull(NotaInv,'N') = 'S' and ifnull(NotaCar,'N') = 'N'  and " +
            "   pdyear=" + time.year + " " +
            " and pdmonth=" + (time.month + 1) + " " +
            " and pdday=" + time.monthDay + " " +
            " and cant+ifnull(cantinf,0)<>0 " + WhereAdicional + " " +
            " Group by prefijo,p.nitsec,p.clisec";


    Cursor Clientes = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null);






        /*Double TotPed = 0.00;
        Double TotNumPed = 0.00;
        Double TotPedIva = 0.00;*/


    Double Subtotal = 0.00;
    Double Iva = 0.00;
    Double Impoconsumo = 0.00;
    Double Total = 0.00;
    Integer TotNumPedGen = 0;
    Integer TotNumPedGenCli = 0;
    double CreditoMalo  =  0.00;
    double CreditoBueno =  0.00;
    if (Clientes.getCount() > 0) {
        Clientes.moveToFirst();
        do {
                /*if (Clientes.getDouble(0)>=15000) {
                    TotPed += Clientes.getDouble(1);
                    TotPedIva += Clientes.getDouble(0);
                    TotNumPed+=1;
                }*/
            double TotalNormal  = Clientes.getDouble(0);


            if ("NC".equalsIgnoreCase(Clientes.getString(4))) {
                Subtotal -= Clientes.getDouble(1);
                Iva -= Clientes.getDouble(3);
                Impoconsumo -= Clientes.getDouble(2);
                Total -= TotalNormal;
                TotNumPedGen += 1;
            }else{
                Subtotal += Clientes.getDouble(1);
                Iva += Clientes.getDouble(3);
                Impoconsumo += Clientes.getDouble(2);
                Total += TotalNormal;
                TotNumPedGen += 1;
            }

        } while (Clientes.moveToNext());
    }



     Clientes = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaMalo, null);

    if (Clientes.getCount() > 0) {
        Clientes.moveToFirst();
        do {
             CreditoMalo  = Clientes.getDouble(0);
        } while (Clientes.moveToNext());
    }


    Clientes = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaBueno, null);


    if (Clientes.getCount() > 0) {
        Clientes.moveToFirst();
        do {
            CreditoBueno  = Clientes.getDouble(0);
        } while (Clientes.moveToNext());
    }


    SDTResumenPedidos.Subtotal=Subtotal;
    SDTResumenPedidos.Iva=Iva;
    SDTResumenPedidos.Impoconsumo=Impoconsumo;
    SDTResumenPedidos.Total=Total;
    SDTResumenPedidos.NumPedidos=TotNumPedGen;
    SDTResumenPedidos.CreditoBueno = CreditoBueno;
    SDTResumenPedidos.CreditoMalo  = CreditoMalo;



}catch (Exception e){
    Integer jj=0;
    Log.e("TotalesPedido", "ERROR EN TotalesPedido", e);
}
        return SDTResumenPedidos;
    }


    public String TraerExistencia(String pArtSec){
        ConBd conbd = new ConBd();
      //  Connection connGen = conbd.CargarConexion();
        String sql= conbd.UrlExistencia;
        //String sql = "http://181.51.253.237:8080/MantisWeb20apps/rest/pGetExistenciaPrecioWs";
        String sExistencia="0.0";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        URL url = null;
        HttpURLConnection conn;

        try {
            url = new URL(sql);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");//; utf-8
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(300000);

            StringBuilder result = new StringBuilder();
            //   result.append(URLEncoder.encode("SDTArtSecWS2", "UTF-8"));
            //   result.append("=");
            result.append("{\"SDTArtSecWS2\":{\"ArtSec\":\""+pArtSec+"\"}}"); //URLEncoder.encode(  , "UTF-8")


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os)); //, "UTF-8"

            writer.write(result.toString());
            writer.flush();
            writer.close();
            os.close();

            conn.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine;

            StringBuffer response = new StringBuffer();

            String json = "";

            while((inputLine = in.readLine()) != null){
                response.append(inputLine);
            }

            json = "["+response.toString()+"]";

            JSONArray jsonArr = null;

            jsonArr = new JSONArray(json);
            String mensaje = "";
            for(int i = 0;i<jsonArr.length();i++){
                JSONObject jsonObject = jsonArr.getJSONObject(i);
                sExistencia=jsonObject.optString("Existencia");
            }
            // sal.setText(mensaje);
        } catch (MalformedURLException e) {
            sExistencia="999999.0";
            e.printStackTrace();
        } catch (IOException e) {
            sExistencia="999999.0";
            e.printStackTrace();
        } catch (JSONException e) {
            sExistencia="999999.0";
            e.printStackTrace();
        }

        return sExistencia;
    }

    public String PeidoEnviado(Context pContext, final String prefijo, final String nitsec, final Integer clisec){
    String sExistencia="0.00";

        try {

            GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
            String vUsuario=vGlobalVariables.getUsuario();
            int vAliNegCod=vGlobalVariables.getAliNegCod();

            Time time = new Time();
            time.setToNow();
            String NumPed=prefijo+vUsuario.trim()+nitsec + clisec+time.year + (time.month + 1) + time.monthDay ;
            ConBd conbd = new ConBd();
            Connection conn = conbd.CargarConexion(pContext);
            Statement comm = conn.createStatement();

                    try {
                        ResultSet rsImport = comm.executeQuery("select isnull(sum((((((CotArtCaj*CotArtEmb)+CotArtUni)*CotPreFacCon)*CotArtPrecio)*(1-(CotArtDesUno/100))*(1-(CotArtDesDos/100))*(1-(CotArtDesTre/100))*(1-(CotArtDesCua/100)))*(1+(CotPorIva/100))),0) val from CotizacionesDetalle1 cd left join  Cotizaciones1 c on c.CotSec=cd.CotSec \n" +
                                "where cotnum='"+NumPed+"'");

                        while (rsImport.next()) {
                            sExistencia = rsImport.getString("val").trim();

                        }
                    }catch (Exception e){
                        int hh=0;
                       // vSDTClientesNuevosItem.Respuesta = "Error de comunicacion con el servidor (servidor no disponible)";
                    }

                } catch (SQLException throwables) {
            throwables.printStackTrace();

        }catch (Exception e){
            int hh=0;
        }

   /*
        String sExistencia = "0.0";
        String sSinExi = "0.0";

        ConBd conbd = new ConBd();
        Connection connGen = conbd.CargarConexion();

        Time time = new Time();
        time.setToNow();

        String NumPed=prefijo+nitsec + clisec+time.year + (time.month + 1) + time.monthDay ;

        String sql= conbd.UrlGetEnvio;
        //String sql = "http://181.49.42.34:8080/pruebas9/rest/pGetPedidoEnviado";
        //String sql = "http://181.49.42.34:8080/MantisWeb20apps3/rest/pGetPedidoEnviado";


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        URL url = null;
        HttpURLConnection conn;

        try {
            url = new URL(sql);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");//; utf-8
            //conn.setRequestProperty("Accept-Encoding", "");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");

            StringBuilder result = new StringBuilder();
            //   result.append(URLEncoder.encode("SDTArtSecWS2", "UTF-8"));
            //   result.append("=");
            result.append("{\"SDTArtSecWS2\":{\"ArtSec\":\""+NumPed+"\"}}"); //URLEncoder.encode(  , "UTF-8")


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os)); //, "UTF-8"

            writer.write(result.toString());
            writer.flush();
            writer.close();
            os.close();

            conn.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine;

            StringBuffer response = new StringBuffer();

            String json = "";

            while((inputLine = in.readLine()) != null){
                response.append(inputLine);
            }

            json = "["+response.toString()+"]";

            JSONArray jsonArr = null;

            jsonArr = new JSONArray(json);
            String mensaje = "";

            for(int i = 0;i<jsonArr.length();i++){
                JSONObject jsonObject = jsonArr.getJSONObject(i);
                sExistencia =jsonObject.optString("Existencia");
                sSinExi = jsonObject.optString("SinExi");

            }
            int PUREBA=0;
            // sal.setText(mensaje);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        // if (sExistencia.trim().equalsIgnoreCase("1.00")){
        return sExistencia;
        //  }else {
        //    return false;
        //  }
    }
public  double Recibocajaenviado(Context pContext,String numpedido,String nitsec,Integer clisec)  {
    double exis = 0.0;
        ConBd conbd = new ConBd();
    Connection conn = conbd.CargarConexion(pContext);
    if(conn != null){
        try{
            Statement comm = conn.createStatement();
            String Script = "select isnull(sum(CASE RecSalNat " +
                    "When 'C' Then RecDet20*-1 " +
                    "When 'D' Then RecDet20 " +
                    "else " +
                    "RecDet20 " +
                    "end " +
                    ")+RecPagAntCli,0) valor " +
                    " from reciboscajadetalle rd left join reciboscaja1 r on rd.recsec = r.recsec  where recnro = '"+numpedido+"' group by RecPagAntCli ";
            ResultSet rsClientes = comm.executeQuery(Script);

            while (rsClientes.next()) {
                exis  =  rsClientes.getDouble("valor");
            }
        }catch (SQLException e){
            Log.e("Errorsql",e.toString());
        }
    }


    return exis;
}

    public String ReciboEnviado(Context pContext,String prefijo,String nitsec,Integer clisec){

        ConBd conbd = new ConBd();
     ///   Connection connGen = conbd.CargarConexion();

        Time time = new Time();
        time.setToNow();

        String NumPed=prefijo+nitsec + clisec+time.year + (time.month + 1) + time.monthDay ;

        String sql= conbd.UrlGetEnvioREcibos;
        //String sql = "http://181.49.42.34:8080/pruebas9/rest/pGetPedidoEnviado";
        //String sql = "http://181.49.42.34:8080/MantisWeb20apps3/rest/pGetPedidoEnviado";
        String sExistencia="0.0";
        String sSinExi="0.0";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        URL url = null;
        HttpURLConnection conn;

        try {
            url = new URL(sql);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");//; utf-8
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");

            StringBuilder result = new StringBuilder();
            //   result.append(URLEncoder.encode("SDTArtSecWS2", "UTF-8"));
            //   result.append("=");
            result.append("{\"SDTArtSecWS2\":{\"ArtSec\":\""+NumPed+"\"}}"); //URLEncoder.encode(  , "UTF-8")


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os)); //, "UTF-8"

            writer.write(result.toString());
            writer.flush();
            writer.close();
            os.close();

            conn.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine;

            StringBuffer response = new StringBuffer();

            String json = "";

            while((inputLine = in.readLine()) != null){
                response.append(inputLine);
            }

            json = "["+response.toString()+"]";

            JSONArray jsonArr = null;

            jsonArr = new JSONArray(json);
            String mensaje = "";

            for(int i = 0;i<jsonArr.length();i++){
                JSONObject jsonObject = jsonArr.getJSONObject(i);
                sExistencia=jsonObject.optString("Existencia");
                sSinExi = jsonObject.optString("SinExi");

            }
            int PUREBA=0;
            // sal.setText(mensaje);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // if (sExistencia.trim().equalsIgnoreCase("1.00")){
        return sExistencia;
        //  }else {
        //    return false;
        //  }
    }
    public String EnviarVisitas(Context pContext) {

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 5);

        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();
        String Error="";

        ConBd conbd = new ConBd();
        Connection conn = conbd.CargarConexion(pContext);
        if(conn != null){
            Statement comm = null;


            try {
                comm = conn.createStatement();


                String ClientesNuevos = "select VenCod,NitSec,CliSec,VisObs,VisFot,VisAno,VisMes,VisDia,VisHor,VisMin,VisSeg,VisHorFin,VisMinFin,VisSegFin,MovCauPed,MovCauNom,VisLatitud,VisLongitud from Visita" ;



                Cursor ClientesNew=null;
                try {
                    ClientesNew = BaseDeDatos.getWritableDatabase().rawQuery(ClientesNuevos, null);
                }catch (Exception e){
                    int jj=0;
                }
                String JsonEnvio = "";
                int hh= ClientesNew.getCount();
                if (ClientesNew.getCount() > 0) {
                    ClientesNew.moveToFirst();
                    do {




                        String Script="delete from visitamov where vismovnitsec='"+ClientesNew.getString(1)+"' and vismovclisec="+ClientesNew.getString(2)+" and year(vismovini)="+ClientesNew.getString(5)+" and month(vismovini)="+ClientesNew.getString(6)+" and day(vismovini)="+ClientesNew.getString(7);
                        //  ResultSet rsImport =
                        comm.execute(Script);

                        int vuelta=0;
                  /*
                    while (rsImport.next()) {
                        vuelta+=1;

                    }
*/
                        if(vuelta==0){
                            String Consulta = "INSERT INTO [visitamov]"
                                    + "([vismovnitsec]"
                                    + ",[vismovclisec]"
                                    + ",[vismovobs]"
                                    + ",[vismovini]"
                                    + ",[vismovfin]"
                                    + ",[vismovcauped]"
                                    + ",[vismovvencod])"
                                    + "VALUES"
                                    + " ('" + ClientesNew.getString(1) + "'," + ClientesNew.getString(2) + ","
                                    + "         '" + ClientesNew.getString(3) + "',"
                                    +"DATEADD(second, " + ClientesNew.getString(10) + ",DATEADD(minute, " + ClientesNew.getString(9) + ",DATEADD(hour, " + ClientesNew.getString(8) + ",CONVERT(datetime,CONVERT (date, GETDATE()))))),"
                                    +"DATEADD(second, isnull(" + ClientesNew.getString(13) + ",0),DATEADD(minute, isnull(" + ClientesNew.getString(12) + ",0),DATEADD(hour, isnull(" + ClientesNew.getString(11) + ",0),CONVERT(datetime,CONVERT (date, GETDATE()))))),"
                                    + "         " + ClientesNew.getString(14) + ","
                                    + "         '" + vUsuario + "')";

                            //  comm.execute(Consulta);
                        }

                    } while (ClientesNew.moveToNext());
                }

            } catch (SQLException e) {
                Error=e.getMessage();
                e.printStackTrace();
            }
        }else{

        }


        return Error;
    }
    public String EnviarClientes(Context pContext) {

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 5);

        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();


        ConBd conbd = new ConBd();
        Connection conn = conbd.CargarConexion(pContext);
        Statement comm = null;
        Statement comm2 = null;
        Statement comm3 = null;
        String Error="";
        try {
            comm = conn.createStatement();
            comm2 = conn.createStatement();



        String ClientesNuevos = "select nit,nombre,direccion,establecimiento,ltrim(rtrim(ciudad)),ltrim(rtrim(barrio)),telefono,celular,lun,mar,mie,jue,vie,sab,dom,frecuencia,canal,subcanal,tamano,observacion,imagen,prinom,segnom,priape,segape,vencod,prosimg,prosimg_gxi,correo,ciucod,Barcod from prospecto" ;



        Cursor ClientesNew=null;
        try {
            ClientesNew = BaseDeDatos.getWritableDatabase().rawQuery(ClientesNuevos, null);
        }catch (Exception e){
            int jj=0;
            Error += "primer try: "+e.toString();
        }
        String JsonEnvio = "";
        if (ClientesNew.getCount() > 0) {
            ClientesNew.moveToFirst();
            do {

                String Script="select * from ProspectoCliente where ProsCliNit='"+ClientesNew.getString(0)+"'";
                ResultSet rsImport = comm.executeQuery(Script);

                String ciucod = ClientesNew.getString(29);
                int barcod = ClientesNew.getInt(30);


                String depcod ="";
                String Script2 ="select DepCod from ciudad where CiuCod ='"+ciucod+"' ";
                ResultSet rsImport2 = comm2.executeQuery(Script2 );
                while (rsImport2.next()) {
                    depcod = rsImport2.getString("DepCod");
                }




                int vuelta=0;

                while (rsImport.next()) {
                    vuelta+=1;
                }


                String path = ClientesNew.getString(26);
                String CliTempImg64 = "";
                ///   CliTempImg64 = CliTempImg64.replace("\n","");
                //   Log.e("CliTempImg64 : ",path);

                try{
                    if(path.contains("/storage/emulated/")){
                        File bitmapFile = new File(path);
                        Bitmap bitmap = BitmapFactory.decodeFile(bitmapFile.toString());
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        CliTempImg64 = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
                        CliTempImg64 = CliTempImg64.replace("\n","");
                    }else{
                        CliTempImg64= "No cargo foto";
                    }
                }catch(Exception e){
                    CliTempImg64= e.toString();
                    Error += "Error img: "+ e.toString();
                }




                if(vuelta==0){
                    String Consulta = "INSERT INTO [ProspectoCliente]"
                            + "([ProsCliNit]"
                            + "   ,[ProsCliNom]"
                            + ",[ProsCliPriNom]"
                            + ",[ProsCliSegNom]"
                            + ",[ProsCliPriApe]"
                            + ",[ProsCliSegApe]"
                            + ",[ProsCliDir]"
                            + ",[ProsCliSig]"
                            + ",[ProsCliCiu]"
                            + ",[ProsCliBar]"
                            + ",[ProsCliCiucod]"
                            + ",[ProsCliBarcod]"
                            + ",[ProsCliDepcod]"
                            + ",[ProsCliTel]"
                            + ",[ProsCliCel]"
                            + ",[ProsCliLun]"
                            + ",[ProsCliMar]"
                            + ",[ProsCliMie]"
                            + ",[ProsCliJue]"
                            + ",[ProsCliVie]"
                            + ",[ProsCliSab]"
                            + ",[ProsCliDom]"
                            + ",[ProsCliFre]"
                            + ",[ProsCliCan]"
                            + ",[ProsCliSub]"
                            + ",[ProsCliTam]"
                            + ",[ProsCliObs]"
                            + ",[ProCliImg],ProsCliHor,ProsImgTxt,ProsVenCod,ProsCorreo)"
                            + "VALUES"
                            + " ('" + ClientesNew.getString(0) + "','" + ClientesNew.getString(1) + "',"
                            + "         '" + ClientesNew.getString(21) + "',"
                            + "         '" + ClientesNew.getString(22) + "',"
                            + "         '" + ClientesNew.getString(23) + "',"
                            + "        '" + ClientesNew.getString(24) + "',"
                            + "        '" + ClientesNew.getString(2) + "',"
                            + "         '" + ClientesNew.getString(3) + "','" + ClientesNew.getString(4).trim() + "','" + ClientesNew.getString(5).trim() + "','" + ciucod + "'," + barcod + ",'" + depcod + "','" + ClientesNew.getString(6) + "','" + ClientesNew.getString(7) + "','" + ClientesNew.getString(8) + "','" + ClientesNew.getString(9) + "','" + ClientesNew.getString(10) + "','" + ClientesNew.getString(11) + "','" + ClientesNew.getString(12) + "','" + ClientesNew.getString(13) + "','" + ClientesNew.getString(14) + "','" + ClientesNew.getString(15) + "','" + ClientesNew.getString(16) + "','" + ClientesNew.getString(17) + "','" + ClientesNew.getString(18) + "','" + ClientesNew.getString(19) + "','" + ClientesNew.getString(20)
                            + "',getdate(),'"
                            + CliTempImg64+ "','"
                            + vUsuario + "','" + ClientesNew.getString(28) + "')";

                    comm.execute(Consulta);
                }

            } while (ClientesNew.moveToNext());
        }

        } catch (SQLException e) {
          //  Error=e.getMessage();
            e.printStackTrace();
            Error += "segundo try: "+e.toString();

        }

        return Error;
    }




    public String EnviarClientesJson(Context pContext) {

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 5);

        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();


        ConBd conbd = new ConBd();
        conbd.Variables();
    //    Connection conn = conbd.CargarConexion(pContext);
        Statement comm = null;
        Statement comm2 = null;
        Statement comm3 = null;
        String Error="";

        String ClientesNuevos = "select nit,nombre,direccion,establecimiento,ltrim(rtrim(ciudad)),ltrim(rtrim(barrio)),telefono,celular,lun,mar,mie,jue,vie,sab,dom,frecuencia,canal,subcanal,tamano,observacion,imagen,prinom,segnom,priape,segape,vencod,prosimg,prosimg_gxi,correo,ciucod,Barcod from prospecto where Enviado <> 'S' or Enviado is null" ;
        Cursor ClientesNew=null;
        try {
            ClientesNew = BaseDeDatos.getWritableDatabase().rawQuery(ClientesNuevos, null);
        }catch (Exception e){
            int jj=0;
            Error += "primer try: "+e.toString();
        }

        String JsonEnvio = "";
        if (ClientesNew.getCount() > 0) {
            ClientesNew.moveToFirst();
            do {


                String ciucod = ClientesNew.getString(29);
                int barcod = ClientesNew.getInt(30);

                String depcod ="";




                String path = ClientesNew.getString(26);
                String CliTempImg64 = "";


                try{
                    if(path.contains("/storage/emulated/")){
                        File bitmapFile = new File(path);
                        Bitmap bitmap = BitmapFactory.decodeFile(bitmapFile.toString());
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        CliTempImg64 = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
                        CliTempImg64 = CliTempImg64.replace("\n","");
                    }else{
                        CliTempImg64= "No cargo foto";
                    }
                }catch(Exception e){
                    CliTempImg64= e.toString();
                    Error += "Error img: "+ e.toString();
                }

                String Json = "";
                String ProsCorreo = ClientesNew.getString(28);
                ProsCorreo = ProsCorreo.replace("\n",",");

                Json =    "{\"SDTProspecto\": " +
                        "{\"ProsCliNit\":\""+ClientesNew.getString(0)+"\",\"ProsCliNom\":\""+ClientesNew.getString(1)+"\",\"ProsCliPriNom\":\""+ClientesNew.getString(21)+"\",\"ProsCliSegNom\":\""+ ClientesNew.getString(22) +"\",\"ProsCliPriApe\":\""+ClientesNew.getString(23)+"\",\"ProsCliSegApe\":\""+ClientesNew.getString(24)+"\",\"ProsCliDir\":\""+ClientesNew.getString(2)+"\", " +
                        "\"ProsCliSig\":\""+ClientesNew.getString(3)+"\",\"ProsCliCiu\":\""+ ClientesNew.getString(4).trim() +"\",\"ProsCliBar\":\""+ClientesNew.getString(5).trim()+"\",\"ProsCliciucod\":\""+ciucod+"\",\"ProsCLiBarcod\":\""+barcod+"\",\"ProsCliDepCod\":\""+depcod+"\", " +
                        "\"ProsCliTel\":\""+ ClientesNew.getString(6)+"\",\"ProsCliCel\":\""+ClientesNew.getString(7) +"\",\"ProsCliLun\":\""+ClientesNew.getString(8) +"\",\"ProsCliMar\":\""+ClientesNew.getString(9)+"\",\"ProsCliMie\":\""+ClientesNew.getString(10)+"\", " +
                        "\"ProsCliJue\":\""+ClientesNew.getString(11)+"\",\"ProsCLiVie\":\""+ClientesNew.getString(12)+"\",\"ProsCliSab\":\""+ClientesNew.getString(13)+"\",\"ProsCliDom\":\""+ClientesNew.getString(14)+"\", " +
                        "\"ProsCLiFre\":\""+ClientesNew.getString(15)+"\",\"ProsCLiCan\":\""+ClientesNew.getString(16)+"\",\"ProsCliSub\":\""+ClientesNew.getString(17)+"\",\"ProsCliTam\":\""+ClientesNew.getString(18)+"\" ,\"PRosCliObs\":\""+ClientesNew.getString(19)+"\",\"ProCliImg\":\""+ClientesNew.getString(20)+"\" ,\"ProsImgTxt\":\""+CliTempImg64+"\",\"ProsVenCod\":\""+vUsuario+"\" ,\"PRosCorreo\":\""+ProsCorreo+"\"       " +
                        "}" +
                        "}";

                String sql = conbd.UrlEnvioCliente;
                String sExistencia2 = "0.0";
                String sExistencia = "0.0";
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                URL url = null;
                HttpURLConnection conn;
                String Mensaje = "";
                try {
                    url = new URL(sql);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Content-Type", "application/json");//; utf-8
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestMethod("POST");
                    StringBuilder result = new StringBuilder();
                    result.append(Json);
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
                    writer.write(result.toString());
                    writer.flush();
                    writer.close();
                    os.close();
                    conn.connect();
                    int statusCode = conn.getResponseCode();
                    InputStream inputstream = null;
                    if (statusCode >= 200 && statusCode < 400) {
                        // Create an InputStream in order to extract the response object
                        inputstream = conn.getInputStream();
                    } else {
                        inputstream = conn.getErrorStream();
                    }

                    BufferedReader in = new BufferedReader(new InputStreamReader(inputstream));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    String json = "";

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    String consulta ="";
                    String error ="";

                    json = "[" + response.toString() + "]";
                    JSONArray jsonArr = null;
                    jsonArr = new JSONArray(json);
                    String status = "";

                    for (int i = 0; i < jsonArr.length(); i++) {
                        JSONObject jsonObject = jsonArr.getJSONObject(i);
                        status = jsonObject.optString("Mensaje");
                        error = jsonObject.optString("error");
                        consulta = "update prospecto set Enviado = '"+status+"' where nit ='"+ClientesNew.getString(0)+"' ";
                        BaseDeDatos.getWritableDatabase().execSQL(consulta);
                        if(status.equalsIgnoreCase("S")){
                            Error = "Envio exitoso: "+String.valueOf(ClientesNew.getCount());
                        }else{
                            Error = error;
                        }

                    }


                } catch (MalformedURLException e) {
                    Mensaje = "Error";
                    Error +=  e.getMessage();
                    e.printStackTrace();
                } catch (IOException e) {
                    Mensaje = "Error";
                    Error +=  e.getMessage();
                    e.printStackTrace();
                } catch (JSONException e) {
                    Mensaje = "Error";
                    Error +=  e.getMessage();
                    e.printStackTrace();
                }





            } while (ClientesNew.moveToNext());
        }

        return Error;
    }








    public String EnviarClientesFicc(Context pContext) {

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 5);

        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();


        ConBd conbd = new ConBd();
        conbd.Variables();
        /*Connection conn = conbd.CargarConexion();
        Statement comm = null;
        Statement comm2 = null;
        Statement comm3 = null;*/
        String Error="";
        try {


           String jsoncli = "";

            String ClientesNuevos = "select nit,nombre,direccion,establecimiento,ltrim(rtrim(ciudad)),ltrim(rtrim(barrio)),telefono,celular,lun,mar,mie,jue,vie,sab,dom,frecuencia,canal,subcanal,tamano,observacion,imagen,prinom,segnom,priape,segape,vencod,prosimg,prosimg_gxi,correo,ciucod,Barcod,lisprecod,TipoCliente,perfilcliente,zona,categoria,CliCup,plazo from prospecto where Enviado <> 'S' or Enviado is null" ;







            Cursor ClientesNew=null;
            try {
                ClientesNew = BaseDeDatos.getWritableDatabase().rawQuery(ClientesNuevos, null);
            }catch (Exception e){
                int jj=0;
            }
            String JsonEnvio = "";
            if (ClientesNew.getCount() > 0) {
                ClientesNew.moveToFirst();
                do {
                    String NitCom = ClientesNew.getString(21)+" "+ClientesNew.getString(22)+" "+ClientesNew.getString(23)+" "+ClientesNew.getString(24);
                    String ciucod = ClientesNew.getString(29);
                    int barcod = ClientesNew.getInt(30);
                    String Plazo = ClientesNew.getString(37); // ----------------------------------------------------------
                    String lisprecod =  ClientesNew.getString(31);
                    String PerfildeClientes =  ClientesNew.getString(33);
                    String TipodeClientes =  ClientesNew.getString(32);
                    String Zona =  ClientesNew.getString(34);
                    String CategoriaCliente =  ClientesNew.getString(35);
                    double clicup = ClientesNew.getDouble(36);


                    String path = ClientesNew.getString(26);
                    String CliTempImg64 = "";
                 ///   CliTempImg64 = CliTempImg64.replace("\n","");
               //   Log.e("CliTempImg64 : ",path);
                   if(path.contains("/storage/emulated/")){
                       File bitmapFile = new File(path);
                       Bitmap bitmap = BitmapFactory.decodeFile(bitmapFile.toString());
                       ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                       bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                       CliTempImg64 = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
                       CliTempImg64 = CliTempImg64.replace("\n","");
                   }




                    jsoncli = "{\"SDTClienteTemporalMov\": " +
                            "{\"CliTemNitIde\":\""+ClientesNew.getString(0)+"\",\"CliTemNitPriNom\":\""+ClientesNew.getString(21)+"\",\"CliTemNitSegNom\":\""+ClientesNew.getString(22)+"\",\"CliTemNitPriApe\":\""+ ClientesNew.getString(23) +"\",\"CliTemNitSegApe\":\""+ClientesNew.getString(24)+"\",\"CliTemNitCom\":\""+NitCom+"\",\"CliTemCliCel\":\""+ClientesNew.getString(7)+"\", " +
                            "\"CliTemCliCiuCod\":\""+ciucod+"\",\"CliTemCliCorEle\":\""+ ClientesNew.getString(28)+"\",\"CliTemCliDir\":\""+ClientesNew.getString(2)+"\",\"CliTemCliNom\":\""+ClientesNew.getString(3)+"\",\"CliTemCliTel\":\""+ClientesNew.getString(6)+"\",\"CliTemLisPreCod\":\""+lisprecod+"\", " +
                            "\"CliTemNitTipPer\":\"a\",\"CliTemDocCod\":\"1\",\"CliTemBarCod\":\""+barcod+"\",\"CliTemLun\":\""+ClientesNew.getString(8)+"\",\"CliTemMar\":\""+ClientesNew.getString(9)+"\", " +
                            "\"CliTemMie\":\""+ClientesNew.getString(10)+"\",\"CliTemJue\":\""+ClientesNew.getString(11)+"\",\"CliTemVie\":\""+ClientesNew.getString(12)+"\",\"CliTemSab\":\""+ClientesNew.getString(13)+"\", " +
                            "\"CliTemFre\":\""+ClientesNew.getString(15)+"\",\"CliTemSubCa\":\""+ClientesNew.getString(17)+"\",\"CliTemCan\":\""+ClientesNew.getString(16)+"\",\"CliTempImg64\":\""+CliTempImg64+"\",\"CliTemVencod\":\""+vUsuario+"\" ,\"CliTempTipclicod\":\""+TipodeClientes+"\",\"CliTempPerCliCod\":\""+PerfildeClientes+"\" ,\"CliTempzonCod\":\""+Zona+"\",\"CliTempCatCod\":\""+CategoriaCliente+"\",\"CliTempCliCup\":\""+clicup+"\",\"CliTemPla\":\""+Plazo+"\"  " +
                            "}" +
                            "}";

                    String sql = conbd.UrlEnvioCliente;
                    String sExistencia2 = "0.0";
                    String sExistencia = "0.0";
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    Log.e("jsoncli",jsoncli);
                    Log.e("sqllink",sql);
                    URL url = null;
                    HttpURLConnection conn;
                    String Mensaje = "";
                    try {
                        url = new URL(sql);
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestProperty("Content-Type", "application/json");//; utf-8
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.setRequestMethod("POST");
                        StringBuilder result = new StringBuilder();
                        result.append(jsoncli);
                        OutputStream os = conn.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
                        writer.write(result.toString());
                        writer.flush();
                        writer.close();
                        os.close();
                        conn.connect();
                        int statusCode = conn.getResponseCode();
                        InputStream inputstream = null;
                        if (statusCode >= 200 && statusCode < 400) {
                            // Create an InputStream in order to extract the response object
                            inputstream = conn.getInputStream();
                        } else {
                            inputstream = conn.getErrorStream();
                        }

                        BufferedReader in = new BufferedReader(new InputStreamReader(inputstream));
                        String inputLine;
                        StringBuffer response = new StringBuffer();
                        String json = "";

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        String consulta ="";
                        String error ="";

                        json = "[" + response.toString() + "]";
                        JSONArray jsonArr = null;
                        jsonArr = new JSONArray(json);
                        String status = "";

                        for (int i = 0; i < jsonArr.length(); i++) {
                            JSONObject jsonObject = jsonArr.getJSONObject(i);
                            status = jsonObject.optString("Mensaje");
                            error = jsonObject.optString("error");
                            consulta = "update prospecto set Enviado = '"+status+"' where nit ='"+ClientesNew.getString(0)+"' ";
                            BaseDeDatos.getWritableDatabase().execSQL(consulta);
                            if(status.equalsIgnoreCase("S")){
                                Error = "Envio exitoso: "+String.valueOf(ClientesNew.getCount());
                            }else{
                                Error = error;
                            }

                        }


                    } catch (MalformedURLException e) {
                        Mensaje = "Error";
                        Error +=  e.getMessage();
                        e.printStackTrace();
                    } catch (IOException e) {
                        Mensaje = "Error";
                        Error +=  e.getMessage();
                        e.printStackTrace();
                    } catch (JSONException e) {
                        Mensaje = "Error";
                        Error +=  e.getMessage();
                        e.printStackTrace();
                    }


                } while (ClientesNew.moveToNext());
            }

        } catch (Exception e) {
            Error=e.getMessage();
            e.printStackTrace();
        }

        return Error;
    }

    public String EnviarRecibos(Context pContext,String prefijo,String nitsec,Integer clisec) {

        ConBd conbd = new ConBd();
    //    Connection connGen = conbd.CargarConexion();
        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();
        String vEmpresa = vGlobalVariables.getEmpresa();
        Integer vPedidoMinimo=vGlobalVariables.getParMovSec();
        Time time = new Time();
        time.setToNow();
        String sExistencia="";
        String sExistencia2="";
        String NumPed = prefijo ;//+nitsec + clisec +time.year + (time.month + 1) + time.monthDay;
        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 5);
        Double pedidoMinimo=0.0;
        String ParMovTatTra = "TRA";
        Cursor vCursorUsuarios = BaseDeDatos.getReadableDatabase().rawQuery("select ParMovPedMin, ParMovTatTra from usuarios where VenCnt=1  ", null);
        vCursorUsuarios.moveToFirst();
        if (vCursorUsuarios.getCount() >0) {
            pedidoMinimo=vCursorUsuarios.getDouble(0);
            ParMovTatTra = vCursorUsuarios.getString(1);
        }
        String Consulta = "";
        GlobalVariables gGlobalVariables = GlobalVariables.getInstance();
        Cursor Clientes = null;

                Consulta = "select p.nitsec,p.clisec,'' artsec,p.vencod,0 cant,rcyear pdyear,rcmonth pdmonth,rcday pdday,abono+retefue+retica+retiva+descuento+dctoprov+dctonooto+dctoconf-aprove precio1,0 plazo,retefue pordesc,retica pordesc2,descuento pordesc3,abono pordesc4,facnro visobs,'REC' prefijo,0 descno,0 confemp,0 confprov,0 confvend,0 cantcaj,'' ConNotCod,'' VisLatitud,'' VisLongitud,0 VisHor,0 VisMin,0 VisSeg,0 VisHorFin,0 VisMinFin,0 VisSegFin,retiva,dctoprov,dctonooto,aprove,dctoconf,Observacion,Justificacion,saldo,RecNro from recibo p " +
                        " left join clientes n on ltrim(rtrim(n.nitsec))=ltrim(rtrim(p.nitsec)) and n.clisec=p.clisec " +
                        " where  rcyear=" + time.year + " and rcmonth=" + (time.month + 1) + " and rcday=" + time.monthDay;

        if(nitsec.isEmpty()){

        }else{
            Consulta+=" and p.nitsec='"+nitsec+"'";
        }

        if(clisec > 0 ){
            Consulta+=" and p.clisec = "+clisec+"";
        }

        if(prefijo.isEmpty()){

        }else{
            Consulta+=" and p.RecNro='"+prefijo+"'";
        }

            try {
                Clientes = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null);
            } catch (Exception e) {
                int jj = 0;
            }



        String JsonEnvio = "";
        if (Clientes.getCount() > 0) {
            Clientes.moveToFirst();
           do {

                   JsonEnvio = "";
                   String NumPedido = Clientes.getString(15) + Clientes.getString(0) + Clientes.getInt(1) + time.year +"-"+ (time.month + 1) +"-"+ time.monthDay;
                   SDTResumenPedidos SDTResumenPedidos = new SDTResumenPedidos();
                   SDTResumenPedidos = TotalesPedido(pContext, Clientes.getString(15), Clientes.getString(0), Clientes.getInt(1), "", "", "");
                  if(vEmpresa.equalsIgnoreCase("IBANEZ")||vEmpresa.equalsIgnoreCase("IBANEZPRU") ||vEmpresa.equalsIgnoreCase("SURTIMARCAS") ){
                      JsonEnvio = "{\"SDTRecibo\":{\"Vencod\":\"" + vUsuario + "\",\"RecNro\":\"" + NumPedido + "\",\"NitSec\":" + Clientes.getInt(0) + ",\"CliSec\":\"" + Clientes.getString(1) + "\",\"rcYear\":\"" + Clientes.getInt(5) + "\",\"rcMes\":\"" + (time.month + 1)  + "\",\"rcDia\":\"" + Clientes.getInt(7) + "\",\"RecDetAbo\":" + Clientes.getDouble(8) + ",\"RecDet1\":" + Clientes.getDouble(10) + ",\"RecDet2\":" + Clientes.getDouble(30) + ",\"RecDet3\":" + Clientes.getDouble(11) + ",\"RecDet8\":" + Clientes.getDouble(12) + ",\"RecDet9\":" + Clientes.getDouble(34) + ",\"RecDet11\":" + Clientes.getDouble(32) + ",\"RecDet13\":" + Clientes.getDouble(31) + ",\"RecDetDes\":" + 0 + ",\"RecDet4\":" + Clientes.getDouble(33) + ",\"RecDet20\":" + Clientes.getDouble(13) + ",\"RecObs\":\"" + Clientes.getString(14) + "\",\"RecDetObsAbo\":\"" + Clientes.getString(35) + "\",\"RecdetJusSec\":\"" + Clientes.getInt(36) + "\" ,\"RecDerValFac\":\"" + Clientes.getDouble(37) + "\" }}";
                  }else{
                      JsonEnvio = "{\"Pedido\":{\"pedido\":\"" + NumPedido + "\",\"nitsec\":\"" + Clientes.getString(0) + "\",\"clisec\":" + Clientes.getInt(1) + ",\"artsec\":\"" + Clientes.getString(2) + "\",\"vencod\":\"" + vUsuario + "\",\"cant\":\"" + Clientes.getDouble(4) + "\",\"cantcaj\":\"" + Clientes.getInt(20) + "\",\"pdyear\":" + Clientes.getInt(5) + ",\"pdmonth\":" + (time.month + 1) + ",\"pdday\":" + Clientes.getInt(7) + ",\"precio\":\"" + Clientes.getDouble(8) + "\",\"plazo\":" + Clientes.getInt(9) + ",\"contreg\":" + Clientes.getInt(21) + ",\"desc\":\"" + Clientes.getDouble(10) + "\",\"desc2\":\"" + Clientes.getDouble(11) + "\",\"desc3\":\"" + Clientes.getDouble(12) + "\",\"desc4\":\"" + Clientes.getDouble(13) + "\",\"confdesc\":\"" + Clientes.getDouble(16) + "\",\"confemp\":\"" + Clientes.getDouble(17) + "\",\"confprov\":\"" + Clientes.getDouble(18) + "\",\"confvend\":\"" + Clientes.getDouble(19) + "\",\"pox\":\"" + Clientes.getString(22) + "\",\"posy\":\"" + Clientes.getString(23) + "\",\"fechor\":\"\",\"obs\":\"" + Clientes.getString(14) + "\",\"papa\":\"\",\"prenotsinped\":\"N\",\"vishor\":\"" + Clientes.getInt(24) + "\",\"vismin\":\"" + Clientes.getInt(25) + "\",\"visseg\":\"" + Clientes.getInt(26) + "\",\"vishorfin\":\"" + Clientes.getInt(27) + "\",\"visminfin\":\"" + Clientes.getInt(28) + "\",\"vissegfin\":\"" + Clientes.getInt(29) + "\"}}";
                  }

               conbd.Variables();
                  Log.e("JsonEnvio", JsonEnvio);
                String sql = conbd.UrlEnvioRecibos;
                   sExistencia = "0.0";
                   StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                   StrictMode.setThreadPolicy(policy);

                   URL url = null;
                   HttpURLConnection conn;
                   try {
                       url = new URL(sql);
                       conn = (HttpURLConnection) url.openConnection();
                       conn.setRequestProperty("Content-Type", "application/json");//; utf-8
                       conn.setDoOutput(true);
                       conn.setDoInput(true);
                       conn.setRequestMethod("POST");
                       StringBuilder result = new StringBuilder();
                       result.append(JsonEnvio);
                       OutputStream os = conn.getOutputStream();
                       BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
                       writer.write(result.toString());
                       writer.flush();
                       writer.close();
                       os.close();
                       conn.connect();
                       int statusCode = conn.getResponseCode();
                       InputStream inputstream = null;
                       if (statusCode >= 200 && statusCode < 400) {
                           // Create an InputStream in order to extract the response object
                           inputstream = conn.getInputStream();
                       } else {
                           inputstream = conn.getErrorStream();
                       }

                       BufferedReader in = new BufferedReader(new InputStreamReader(inputstream));
                       String inputLine;
                       StringBuffer response = new StringBuffer();
                       String json = "";

                       while ((inputLine = in.readLine()) != null) {
                           response.append(inputLine);
                       }
                       json = "[" + response.toString() + "]";
                       JSONArray jsonArr = null;
                       jsonArr = new JSONArray(json);
                       String mensaje = "";
                       for (int i = 0; i < jsonArr.length(); i++) {
                           JSONObject jsonObject = jsonArr.getJSONObject(i);
                           sExistencia = jsonObject.optString("Mensaje");
                       }





                   } catch (MalformedURLException e) {
                       sExistencia2 +=  e.getMessage();
                       e.printStackTrace();
                   } catch (IOException e) {
                       sExistencia2 +=  e.getMessage();
                       e.printStackTrace();
                   } catch (JSONException e) {
                       sExistencia2 +=  e.getMessage();
                       e.printStackTrace();
                   }

            } while (Clientes.moveToNext());




            String jsondet = "";
String Recnro = "";

String consultan = "select p.NitSec,p.CliSec,facnro,NroCheque,CodBanco,TCNSEC,Valor,Ciudad,CtaBco, " +
        " Tipo, ifnull(aldia,'N') aldia,  ifnull(postfecha,'N') postfecha , rcyear , rcmonth, rcday,RecNro,TipoConsigna,PucBanco from reciboforma p left join clientes c on p.nitsec=c.nitsec and p.clisec=c.clisec " +
        " where recicyear=" + time.year + " and recicmonth=" + (time.month + 1) + " and recicday=" + time.monthDay + " ";



            if(nitsec.isEmpty()){

            }else{
                consultan+=" and p.nitsec='"+nitsec+"'";
            }

            if(clisec > 0 ){
                consultan+=" and p.clisec = "+clisec+"";
            }

            if(prefijo.isEmpty()){

            }else{
                consultan+=" and RecNro='"+prefijo+"'";
            }







            Cursor cursor =  BaseDeDatos.getWritableDatabase().rawQuery(consultan, null); //order by nombr


           if(cursor.moveToFirst()){
                cursor.moveToFirst();
                do{
                    Recnro =  cursor.getString(15);
                    jsondet  ="{\"SDTFormaPagoRec\":{\"Facnro\":\"" + cursor.getString(2) + "\", \"RecNro\":\"" +cursor.getString(15)+ "\" , \"Tipo\":\"" + cursor.getString(9) + "\",\"RecPagVal\":\""+ cursor.getInt(6) + "\" ,\"RecPagCheque\":\""+ cursor.getString(3) + "\", " +
                            "  \"RecYear\":\"" + cursor.getInt(12) + "\", \"RecMes\":\"" + cursor.getInt(13) + "\" , \"RecDay\":\"" + cursor.getInt(14) + "\" , \"ReCPagBanInfCod\":\"" + cursor.getString(4) + "\", " +
                            "  \"RecPagTcnSec\":\"" + cursor.getString(5) + "\",\"RecPagCiuCod\":\"" + cursor.getString(7) + "\",\"CtaBco\":\"" + cursor.getString(8) + "\",\"Aldia\":\"" + cursor.getString(10) + "\", " +
                            "  \"PostFecha\":\"" + cursor.getString(11) + "\",\"TipoCons\":\"" + cursor.getString(16) + "\",\"RecPagBanPucSec\":\""+cursor.getString(17)+"\", \"Fotos\":[";

                    Cursor cursorfotos =  BaseDeDatos.getWritableDatabase().rawQuery("select NitSec,CliSec,NroOp,imgBase,ValorOp from ReciboFormaFotos  " +
                            " where NitSec = '"+cursor.getString(0)+"' and clisec= "+cursor.getInt(1)+" and facnro= '"+cursor.getString(2)+"' ", null); //order by nombr
                   String jsonfotos ="";
                   int sec =0;
                    if(cursorfotos.moveToFirst()){
                        cursorfotos.moveToFirst();
                        do{
                            sec+=1;

                            String imgblo = cursorfotos.getString(3);
                         //  imgblo = imgblo.replace("/","+");
                            //imgblo = "" ;
                            //imgblo.replace("\n","");
                            imgblo = imgblo.replace("\n","");

                            jsonfotos += "{\"ReForPagFotosSec\":\""+sec+"\",\"ReForPagFotosDesc\":\""+cursorfotos.getString(2)+"\",\"ReForPagFotoVal\":\""+cursorfotos.getDouble(4)+"\",\"ReForPagFotosLongVarchar\":\""+imgblo+"\" },";

                        }while(cursorfotos.moveToNext());
                    }
                    jsonfotos = jsonfotos+"]}}";
                    jsonfotos = jsonfotos.replace("},]", "}]");
                    jsondet += jsonfotos;

                    Log.e("jsondet",jsondet);



                    String sql = conbd.UrlEnvioRecibosformas;

                    //String sql = "http://181.49.42.34:8086/Pruebas/rest/pSetReciboFormas";
                    sExistencia = "0.0";
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    URL url = null;
                    HttpURLConnection conn;
                    try {
                        url = new URL(sql);
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestProperty("Content-Type", "application/json");//; utf-8
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.setRequestMethod("POST");
                        StringBuilder result = new StringBuilder();
                        result.append(jsondet);
                        OutputStream os = conn.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
                        writer.write(result.toString());
                        writer.flush();
                        writer.close();
                        os.close();
                        conn.connect();
                        int statusCode = conn.getResponseCode();
                        InputStream inputstream = null;
                        if (statusCode >= 200 && statusCode < 400) {
                            // Create an InputStream in order to extract the response object
                            sExistencia2 = "Envio exitoso";
                            inputstream = conn.getInputStream();
                        } else {
                            inputstream = conn.getErrorStream();
                        }

                        BufferedReader in = new BufferedReader(new InputStreamReader(inputstream));
                        String inputLine;
                        StringBuffer response = new StringBuffer();
                        String json = "";

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        json = "[" + response.toString() + "]";
                        JSONArray jsonArr = null;
                        jsonArr = new JSONArray(json);
                        String consulta = "";
                        String status = "";
                        for (int i = 0; i < jsonArr.length(); i++) {
                            JSONObject jsonObject = jsonArr.getJSONObject(i);
                            sExistencia = jsonObject.optString("Mensaje");
                            status = jsonObject.optString("status");
                            Log.e("status",status);
                            Log.e("jsonObject",jsonObject.toString());
                            consulta = "update Recibo set Enviado = '"+status+"' where RecNro ='"+Recnro+"' ";
                            BaseDeDatos.getWritableDatabase().execSQL(consulta);
                        }
                        Log.e("sExistencia  ",sExistencia);
                    } catch (MalformedURLException e) {
                        sExistencia2 +=  e.getMessage();
                        Log.e("sExistencia2  ",sExistencia2);
                        e.printStackTrace();
                    } catch (IOException e) {
                        sExistencia2 +=  e.getMessage();
                        Log.e("sExistencia2  ",sExistencia2);
                        e.printStackTrace();
                    } catch (JSONException e) {
                        sExistencia2 +=  e.getMessage();
                        Log.e("sExistencia2  ",sExistencia2);
                        e.printStackTrace();
                    }

                }while(cursor.moveToNext());
            }

        }

        return sExistencia2;

    }

    public String EnviarConsigna(Context pContext,String ConNro){
        String Mensaje = "";
        ConBd conbd = new ConBd();
        conbd.Variables();
        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();
        String vEmpresa = vGlobalVariables.getEmpresa();
        String Json = "";
        String JsonRecibo = "";
        String JsonFoto = "";
        Time time = new Time();
        time.setToNow();
        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 6);
        String consulta = "select VenCod, ConNro,Obs,CodBanco,TCNSEC,Ciudad,PucSec ,Valor,conyear,conmonth,conday,rconyear,rconmonth,rconday,TipoConsigna,NitSec" +
                " from ConsignaRecibo where  Valor > 0 and conyear="+ time.year+" and conmonth = "+(time.month + 1) +" and conday = "+time.monthDay+" ";
      if(!ConNro.isEmpty()){
          consulta += " and ConNro = '"+ConNro+"' ";
      }
        Cursor consgina = BaseDeDatos.getReadableDatabase().rawQuery(consulta, null);

        if(consgina.getCount() > 0){




            consgina.moveToFirst();
            do{
                String ConRecEfeNrro = consgina.getString(1);
                String ConRecEfeVenCod = consgina.getString(0);
                String ConRecEfeObs = consgina.getString(2);
                String ConRecEfeCodBanco = consgina.getString(3);
                String ConRecEfeTcnSec = consgina.getString(4);
                String ConRecEfeCiudad = consgina.getString(5);
                String ConRecEfePucsec = consgina.getString(6);
                String ConRecefeValor = consgina.getString(7);
                Double dConRecefeValor = consgina.getDouble(7);
                String ConRecefeYear = consgina.getString(8);
                String ConRecEfeMonth = consgina.getString(9);
                String ConRecEfeDay = consgina.getString(10);
                String ConRecefeconYear = consgina.getString(11);
                String ConRecefeConMonth = consgina.getString(12);
                String ConRecefeconDay = consgina.getString(13);
                String TipoCons =  consgina.getString(14);
                String NitSec =  consgina.getString(15);

                Double totalFot = 0.0;
                Cursor fnotos= BaseDeDatos.getWritableDatabase().rawQuery("select  ConNro,Valorfoto from ConsignaRecibofoto  where ConNro = '"+ConRecEfeNrro+"'  ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR
                if(fnotos.getCount()> 0){
                    fnotos.moveToFirst();
                    do{
                        totalFot+= fnotos.getDouble(1);
                    }while(fnotos.moveToNext());
                }else{
                    totalFot = 0.0;
                }





                if(totalFot.intValue() == dConRecefeValor.intValue()){
                    Json = "{\"SDTConsignaRecibo\": {\"ConRecEfeSec\":\"\",\"ConRecEfeNrro\":\""+ConRecEfeNrro+"\",\"ConRecEfeVenCod\":\""+ConRecEfeVenCod+"\"," +
                            " \"ConRecEfeObs\":\""+ConRecEfeObs+"\",\"ConRecEfeCodBanco\":\""+ConRecEfeCodBanco+"\",\"ConRecEfeTcnSec\":\""+ConRecEfeTcnSec+"\",\"ConRecEfeCiudad\":\""+ConRecEfeCiudad+"\"," +
                            " \"ConRecEfePucsec\":\""+ConRecEfePucsec+"\",\"ConRecefeValor\":\""+ConRecefeValor+"\",\"ConRecefeYear\":\""+ConRecefeYear+"\",\"ConRecEfeMonth\":\""+ConRecEfeMonth+"\"," +
                            " \"ConRecEfeDay\":\""+ConRecEfeDay+"\",\"ConRecefeconYear\":\""+ConRecefeconYear+"\",\"ConRecefeConMonth\":\""+ConRecefeConMonth+"\",\"ConRecefeconDay\":\""+ConRecefeconDay+"\",\"TipoCons\":\""+TipoCons+"\", \"NitSec\":\""+NitSec+"\", ";

                    String consultadet = " select rf.RecNro , rf.Tipo , ifnull(rf.postfecha,'N') postfecha,rf.Valor from Reciboforma rf where ConNro = '"+ConRecEfeNrro+"' group by rf.Tipo,rf.aldia,rf.Valor, rf.RecNro ";
                    Cursor recibos = BaseDeDatos.getReadableDatabase().rawQuery(consultadet, null);
                    JsonRecibo = "\"Recibos\":[ ";
                    recibos.moveToFirst();
                    if(recibos.getCount() > 0){
                        recibos.moveToFirst();
                        do{
                            String ConRecefeRecNro = recibos.getString(0);
                            String ConRecEfeRecTipo = recibos.getString(1);
                            String ConRecefecReCCheck = recibos.getString(2);
                            String ConRecEfeRecValor = recibos.getString(3);
                            JsonRecibo+=" {\"ConRecefeRecNro\":\""+ConRecefeRecNro+"\",\"ConRecEfeRecTipo\":\""+ConRecEfeRecTipo+"\",\"ConRecefecReCCheck\":\""+ConRecefecReCCheck+"\",\"ConRecEfeRecValor\":\""+ConRecEfeRecValor+"\"},";
                        }while (recibos.moveToNext());
                        JsonRecibo = JsonRecibo+"],";
                        JsonRecibo = JsonRecibo.replace("},]", "}]");
                        Json += JsonRecibo;
                        Log.e("sdtJson",Json);
                    }
                    consultadet = " select descr,foto,Valorfoto from ConsignaRecibofoto  where ConNro = '"+ConRecEfeNrro+"' ";
                    Cursor fotos = BaseDeDatos.getReadableDatabase().rawQuery(consultadet, null);
                    JsonFoto = "\"Foto\":[ ";
                    fotos.moveToFirst();
                    if(fotos.getCount() > 0){
                        fotos.moveToFirst();
                        do{
                            String ConRecEfeRecImg64 = fotos.getString(1);
                            String ConRecEfeRecNom = fotos.getString(0);
                            String ConRecEfeRecfotoValor = fotos.getString(2);
                            ConRecEfeRecImg64 = ConRecEfeRecImg64.replace("\n","");
                            JsonFoto+=" {\"ConRecEfeRecImg64\":\""+ConRecEfeRecImg64+"\",\"ConRecEfeRecNom\":\""+ConRecEfeRecNom+"\" ,\"ConRecEfeRecfotoValor\":\""+ConRecEfeRecfotoValor+"\"   },";

                        }while (fotos.moveToNext());

                    }
                    JsonFoto = JsonFoto+"]}}";
                    JsonFoto = JsonFoto.replace("},]", "}]");
                    Json += JsonFoto;
                    conbd.Variables();
                    String sql = conbd.UrlEnvioConsigna;
                    String sExistencia2 = "0.0";
                    String sExistencia = "0.0";
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    Log.e("Json",Json);
                    URL url = null;
                    HttpURLConnection conn;
                    try {
                        url = new URL( sql);
                        //   url = new URL("http://181.49.42.34:8080/WebServiceMovilesPru/rest/pSetConsignaEfe");
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestProperty("Content-Type", "application/json");//; utf-8
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.setRequestMethod("POST");
                        StringBuilder result = new StringBuilder();
                        result.append(Json);
                        OutputStream os = conn.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
                        writer.write(result.toString());
                        writer.flush();
                        writer.close();
                        os.close();
                        conn.connect();
                        int statusCode = conn.getResponseCode();
                        InputStream inputstream = null;
                        if (statusCode >= 200 && statusCode < 400) {
                            // Create an InputStream in order to extract the response object
                            inputstream = conn.getInputStream();
                        } else {
                            inputstream = conn.getErrorStream();
                        }

                        BufferedReader in = new BufferedReader(new InputStreamReader(inputstream));
                        String inputLine;
                        StringBuffer response = new StringBuffer();
                        String json = "";

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }

                        json = "[" + response.toString() + "]";
                        JSONArray jsonArr = null;
                        jsonArr = new JSONArray(json);
                        String status = "";
                        for (int i = 0; i < jsonArr.length(); i++) {
                            JSONObject jsonObject = jsonArr.getJSONObject(i);
                            status = jsonObject.optString("status");
                            consulta = "update ConsignaRecibo set Enviado = '"+status+"' where ConNro ='"+ConRecEfeNrro+"' ";
                            BaseDeDatos.getWritableDatabase().execSQL(consulta);
                            if(status.equalsIgnoreCase("S")){
                                Mensaje = "Envio exitoso: "+String.valueOf(consgina.getCount());
                            }else{
                                Mensaje = "Registro no creado";
                            }

                        }


                    } catch (MalformedURLException e) {
                        Mensaje = "Error";
                        sExistencia2 +=  e.getMessage();
                        e.printStackTrace();
                    } catch (IOException e) {
                        Mensaje = "Error";
                        sExistencia2 +=  e.getMessage();
                        e.printStackTrace();
                    } catch (JSONException e) {
                        Mensaje = "Error";
                        sExistencia2 +=  e.getMessage();
                        e.printStackTrace();
                    }

                }else{
                    Mensaje = "Valor comprobantes diferente al seleccionado";
                }






            }while (consgina.moveToNext());
        }else{
            Mensaje = "sin registro";
        }



        return Mensaje;
    }

    public String EnviarPedidosV2(Context pContext,String prefijo,String nitsec,Integer clisec) {

        ConBd conbd = new ConBd();
      //  Connection connGen = conbd.CargarConexion();

        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();
        Integer vPedidoMinimo=vGlobalVariables.getParMovSec();

        Time time = new Time();
        time.setToNow();

        String sExistencia="";
        String sExistencia2="";

        String NumPed = prefijo+nitsec + clisec +time.year + (time.month + 1) + time.monthDay;

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 5);


        Double pedidoMinimo=0.0;

        Cursor vCursorUsuarios = BaseDeDatos.getReadableDatabase().rawQuery("select ParMovPedMin from usuarios where VenCnt=1  ", null);
        vCursorUsuarios.moveToFirst();

        if (vCursorUsuarios.getCount() >0) {
            pedidoMinimo=vCursorUsuarios.getDouble(0);
        }


        String Consulta = "";
        GlobalVariables gGlobalVariables = GlobalVariables.getInstance();
        String vEmpresa=gGlobalVariables.getEmpresa();
        String mantisficc =  conbd.MantisFicc;
        Cursor Clientes = null;
        if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") || vEmpresa.trim().equalsIgnoreCase("IBANEZPRU")) {
            // case lisprecod when 1 then a.precio1 when 2 then a.precio2 when 3 then a.precio3  when 4  then a.precio4 when 5 then a.precio5 when 6 then a.precio6 when 7 then a.precio7 when 8 then a.precio8 when 9 then a.precio9 when 15 then a.precio5 when 16 then a.precio6  when 17 then a.precio7 when 18 then a.precio8 when 19 then a.precio9 else a.precio1 end
            Consulta = "select p.nitsec,p.clisec,p.artsec,p.vencod,cant,pdyear,pdmonth,pdday,precio  precio1,plazo,pordesc,pordesc2,pordesc3+pordesc6 pordesc3,pordesc4+pordesc5 pordesc4,ifnull(visobs,'') visobs,p.prefijo,(ifnull(pordescno,0)+ifnull(pordesc2no,0)+ifnull(pordesc3no,0)+ifnull(pordesc4no,0)+ifnull(pordesc5no,0)+ifnull(pordesc6no,0))-(ifnull(pordesc,0)+ifnull(pordesc2,0)+ifnull(pordesc3,0)+ifnull(pordesc4,0)+ifnull(pordesc5,0)+ifnull(pordesc6,0)) descno,confemp,confprov,confvend,cantcaj,ConNotCod,ifnull(VisLatitud,'') VisLatitud,ifnull(VisLongitud,'') VisLongitud,ifnull(VisHor,0) VisHor,ifnull(VisMin,0) VisMin,ifnull(VisSeg,0) VisSeg,ifnull(VisHor,0) VisHorFin,ifnull(VisMin,0) VisMinFin,ifnull(VisSeg,0) VisSegFin from pedido p " +
                    " left join articulos a on rtrim(a.artsec)=rtrim(p.artsec) " +
                    " left join clientes n on ltrim(rtrim(n.nitsec))=ltrim(rtrim(p.nitsec)) and n.clisec=p.clisec " +
                    " left join visita v on ltrim(rtrim(v.nitsec))=ltrim(rtrim(p.nitsec)) and v.clisec=p.clisec and VisAno=pdyear and pdmonth=VisMes  and visdia=pdday" +
                    " where a.precio1>0 and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay ;
            if(nitsec.isEmpty()){

            }else{
                Consulta+=" and p.nitsec='"+nitsec+"'";
            }
            //" where  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay +
            Consulta+=" union all " +
                    " select MovParNitSec,MovParCliSec,m.MovParPremArtSec,'" + vUsuario + "' vencod,MovParPremCant cant,MovParPremAno,MovParPremMes,MovParPremDia,1 precio,0 plazo,0.0 pordesc,0.0 pordesc2,0.0 pordesc3,0.0 pordesc4,'' obs,'P1' prefi,0.0,0.0,0.0,0.0,0,0,'','',0,0,0,0,0,0  from MovParPrem M " +
                    " left join articulos a on a.artsec=m.MovParPremArtSec where  MovParPremAno=" + time.year + " and MovParPremMes=" + (time.month + 1) + " and MovParPremDia=" + time.monthDay;

            try {
                Clientes = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null);
            } catch (Exception e) {
                int jj = 0;
            }

        }else {
            if (vEmpresa.trim().equalsIgnoreCase("GELVEZ") || vEmpresa.trim().equalsIgnoreCase("GELVEZGIR") || vEmpresa.trim().equalsIgnoreCase("GELVEZGIRREM") || vEmpresa.trim().equalsIgnoreCase("GELVEZCAL") || vEmpresa.trim().equalsIgnoreCase("GELVEZEJE")) {
                Consulta = "select p.nitsec,p.clisec,p.artsec,p.vencod,cant,pdyear,pdmonth,pdday,case lisprecod when 1 then a.precio1 when 2 then a.precio2 when 3 then a.precio3 when 4 then a.precio4 when 5 then a.precio5 when 6 then a.precio6 when 7 then a.precio7 when 8 then a.precio8 when 9 then a.precio9 when 10 then a.precio10 when 11 then a.precio11 when 12 then a.precio12  when 13 then a.precio13  when 14 then a.precio14  when 15 then a.precio15 when 16 then a.precio16  when 17 then a.precio17 when 18 then a.precio18 when 19 then a.precio19 when 20 then a.precio20 when 21 then a.precio21 when 22 then a.precio22 when 23 then a.precio23 when 24 then a.precio24 when 25 then a.precio25 when 26 then a.precio26 when 27 then a.precio27 when 28 then a.precio28 when 29 then a.precio29 when 30 then a.precio30 else a.precio1 end  precio1,plazo,pordesc,pordesc2,pordesc3+pordesc6 pordesc3,pordesc4+pordesc5 pordesc4,ifnull(visobs,'') visobs,p.prefijo,(ifnull(pordescno,0)+ifnull(pordesc2no,0)+ifnull(pordesc3no,0)+ifnull(pordesc4no,0)+ifnull(pordesc5no,0)+ifnull(pordesc6no,0))-(ifnull(pordesc,0)+ifnull(pordesc2,0)+ifnull(pordesc3,0)+ifnull(pordesc4,0)+ifnull(pordesc5,0)+ifnull(pordesc6,0)) descno,confemp,confprov,confvend,cantcaj,ConNotCod,ifnull(VisLatitud,'') VisLatitud,ifnull(VisLongitud,'') VisLongitud,ifnull(VisHor,0) VisHor,ifnull(VisMin,0) VisMin,ifnull(VisSeg,0) VisSeg,ifnull(VisHor,0) VisHorFin,ifnull(VisMin,0) VisMinFin,ifnull(VisSeg,0) VisSegFin from pedido p " +
                        " left join articulos a on rtrim(a.artsec)=rtrim(p.artsec) " +
                        " left join clientes n on ltrim(rtrim(n.nitsec))=ltrim(rtrim(p.nitsec)) and n.clisec=p.clisec " +
                        " left join visita v on ltrim(rtrim(v.nitsec))=ltrim(rtrim(p.nitsec)) and v.clisec=p.clisec  and VisAno=pdyear and pdmonth=VisMes  and visdia=pdday" +
                        //" where a.precio1>0 and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay +
                        " where  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay +
                        " union all " +
                        "   select MovParNitSec,MovParCliSec,m.MovParPremArtSec,'" + vUsuario + "' vencod,MovParPremCant cant,MovParPremAno,MovParPremMes,MovParPremDia,1 precio,0 plazo,0.0 pordesc,0.0 pordesc2,0.0 pordesc3,0.0 pordesc4,'' obs,'P1' prefi,0.0,0.0,0.0,0.0,0,0,'','',0,0,0,0,0,0  from MovParPrem M " +
                        " left join articulos a on a.artsec=m.MovParPremArtSec where  MovParPremAno=" + time.year + " and MovParPremMes=" + (time.month + 1) + " and MovParPremDia=" + time.monthDay;
                //      +" union all " +
                //      "  select NitSec,CliSec,'VISITA','" + vUsuario + "' vencod,MovCauPed cant,VisAno,VisMes,VisDia,1 precio,0 plazo,0.0 pordesc,0.0 pordesc2,0.0 pordesc3,0.0 pordesc4,'' obs,'VI' prefi,0.0,0.0,0.0,0.0,0,0,'','',VisHor,VisMin,VisSeg,VisHorFin,VisMinFin,VisSegFin  from Visita M " +
                //        " where  VisAno=" + time.year + " and VisMes=" + (time.month + 1) + " and VisDia=" + time.monthDay;
            }else {

                Consulta = "select p.nitsec,p.clisec,p.artsec,p.vencod,cant,pdyear,pdmonth,pdday,case lisprecod when 1 then a.precio1 when 2 then a.precio2 when 3 then a.precio3 when 4 then a.precio4 when 5 then a.precio5 when 7 then a.precio7 when 8 then a.precio8 when 15 then a.precio5 when 16 then a.precio6  when 17 then a.precio7 when 18 then a.precio8 when 19 then a.precio9 else a.precio1 end  precio1,plazo,pordesc,pordesc2,pordesc3+pordesc6 pordesc3,pordesc4+pordesc5 pordesc4,ifnull(visobs,'') visobs,p.prefijo,(ifnull(pordescno,0)+ifnull(pordesc2no,0)+ifnull(pordesc3no,0)+ifnull(pordesc4no,0)+ifnull(pordesc5no,0)+ifnull(pordesc6no,0))-(ifnull(pordesc,0)+ifnull(pordesc2,0)+ifnull(pordesc3,0)+ifnull(pordesc4,0)+ifnull(pordesc5,0)+ifnull(pordesc6,0)) descno,confemp,confprov,confvend,cantcaj,ConNotCod,ifnull(VisLatitud,'') VisLatitud,ifnull(VisLongitud,'') VisLongitud,ifnull(VisHor,0) VisHor,ifnull(VisMin,0) VisMin,ifnull(VisSeg,0) VisSeg,ifnull(VisHor,0) VisHorFin,ifnull(VisMin,0) VisMinFin,ifnull(VisSeg,0) VisSegFin from pedido p " +
                        " left join articulos a on rtrim(a.artsec)=rtrim(p.artsec) " +
                        " left join clientes n on ltrim(rtrim(n.nitsec))=ltrim(rtrim(p.nitsec)) and n.clisec=p.clisec " +
                        " left join visita v on ltrim(rtrim(v.nitsec))=ltrim(rtrim(p.nitsec)) and v.clisec=p.clisec  and VisAno=pdyear and pdmonth=VisMes  and visdia=pdday" +
                        //" where a.precio1>0 and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay +
                        " where  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay +
                        " union all " +
                        "   select MovParNitSec,MovParCliSec,m.MovParPremArtSec,'" + vUsuario + "' vencod,MovParPremCant cant,MovParPremAno,MovParPremMes,MovParPremDia,1 precio,0 plazo,0.0 pordesc,0.0 pordesc2,0.0 pordesc3,0.0 pordesc4,'BON' obs,'P1' prefi,0.0,0.0,0.0,0.0,0,0,'','',0,0,0,0,0,0  from MovParPrem M " +
                        "left join articulos a on a.artsec=m.MovParPremArtSec where  MovParPremAno=" + time.year + " and MovParPremMes=" + (time.month + 1) + " and MovParPremDia=" + time.monthDay;
            }
            try {
                Clientes = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null);
            } catch (Exception e) {
                int jj = 0;
            }
        }


        String JsonEnvio = "";
        if (Clientes.getCount() > 0) {
            Clientes.moveToFirst();
            do {

                Double TotalPedido= TotalesPedido(pContext,Clientes.getString(15),Clientes.getString(0),Clientes.getInt(1),"","","").Subtotal;

                //if ((pedidoMinimo!=0 && TotalPedido>pedidoMinimo ) || pedidoMinimo==0) {
                Double CantUni = 0.00;
                Integer CantCajas = 0;
                if ((pedidoMinimo!=0 && TotalPedido>pedidoMinimo ) || pedidoMinimo==0) {
                    CantUni = Clientes.getDouble(4);
                    CantCajas = Clientes.getInt(20);
                }

                JsonEnvio = "";
                //Clientes.getString(3)
                String NumPedido = Clientes.getString(15)+vUsuario.trim()+ Clientes.getString(0) + Clientes.getInt(1) + time.year +"-"+ (time.month + 1) +"-"+ time.monthDay;

                SDTResumenPedidos SDTResumenPedidos = new SDTResumenPedidos();
                SDTResumenPedidos = TotalesPedido(pContext, Clientes.getString(15), Clientes.getString(0), Clientes.getInt(1), "", "", "");
                JsonEnvio = "{\"Pedido\":{\"pedido\":\"" + NumPedido + "\",\"nitsec\":\"" + Clientes.getString(0) + "\",\"clisec\":" + Clientes.getInt(1) + ",\"artsec\":\"" + Clientes.getString(2) + "\",\"vencod\":\"" + vUsuario + "\",\"cant\":\"" + CantUni + "\",\"cantcaj\":\"" + CantCajas + "\",\"pdyear\":" + Clientes.getInt(5) + ",\"pdmonth\":" + (time.month + 1) + ",\"pdday\":" + Clientes.getInt(7) + ",\"precio\":\"" + Clientes.getDouble(8) + "\",\"plazo\":" + Clientes.getInt(9) + ",\"contreg\":" + Clientes.getInt(21) + ",\"desc\":\"" + Clientes.getDouble(10) + "\",\"desc2\":\"" + Clientes.getDouble(11) + "\",\"desc3\":\"" + Clientes.getDouble(12) + "\",\"desc4\":\"" + Clientes.getDouble(13) + "\",\"confdesc\":\"" + Clientes.getDouble(16) + "\",\"confemp\":\"" + Clientes.getDouble(17) + "\",\"confprov\":\"" + Clientes.getDouble(18) + "\",\"confvend\":\"" + Clientes.getDouble(19) + "\",\"pox\":\"" + Clientes.getString(22) + "\",\"posy\":\"" + Clientes.getString(23) + "\",\"fechor\":\"\",\"obs\":\"" + Clientes.getString(14) + "\",\"papa\":\"\",\"prenotsinped\":\"N\",\"vishor\":\"" + Clientes.getInt(24) + "\",\"vismin\":\"" + Clientes.getInt(25) + "\",\"visseg\":\"" + Clientes.getInt(26) + "\",\"vishorfin\":\"" + Clientes.getInt(27) + "\",\"visminfin\":\"" + Clientes.getInt(28) + "\",\"vissegfin\":\"" + Clientes.getInt(29) + "\"}}";
                //JsonEnvio="{\"Pedido\":{\"pedido\":\""+NumPedido+"\",\"nitsec\":\""+Clientes.getString(0)+"\",\"clisec\":"+Clientes.getInt(1)+",\"artsec\":\""+Clientes.getString(2)+"\",\"vencod\":\""+vUsuario+"\",\"cant\":\""+Clientes.getDouble(4)+"\",\"cantcaj\":\""+Clientes.getInt(20)+"\",\"pdyear\":"+Clientes.getInt(5)+",\"pdmonth\":"+(time.month + 1)+",\"pdday\":"+Clientes.getInt(7)+",\"precio\":\""+Clientes.getDouble(8)+"\",\"plazo\":"+Clientes.getInt(9)+",\"contreg\":"+Clientes.getInt(21)+",\"desc\":\""+Clientes.getDouble(10)+"\",\"desc2\":\""+Clientes.getDouble(11)+"\",\"desc3\":\""+Clientes.getDouble(12)+"\",\"desc4\":\""+Clientes.getDouble(13)+"\",\"confdesc\":\""+Clientes.getDouble(16)+"\",\"confemp\":\""+Clientes.getDouble(17)+"\",\"confprov\":\""+Clientes.getDouble(18)+"\",\"confvend\":\""+Clientes.getDouble(19)+"\",\"pox\":\""+Clientes.getString(22)+"\",\"posy\":\""+Clientes.getString(23)+"\",\"fechor\":\"\",\"obs\":\""+Clientes.getString(14)+"\",\"papa\":\"\",\"prenotSinPed\":\"\"}}";
                // JsonEnvio="{\"pedido\":\""+Clientes.getString(15)+"\",\"nitsec\":\""+nitsec+"\",\"clisec\":"+clisec+",\"artsec\":\""+Clientes.getString(2)+"\",\"vencod\":\""+4+"\",\"cant\":\""+Clientes.getDouble(4)+"\",\"pdyear\":"+Clientes.getInt(5)+",\"pdmonth\":"+Clientes.getInt(6)+",\"pdday\":"+Clientes.getInt(7)+",\"precio\":\""+Clientes.getDouble(8)+"\",\"plazo\":"+Clientes.getInt(9)+",\"contreg\":0,\"desc\":\""+Clientes.getDouble(10)+"\",\"desc2\":\""+Clientes.getDouble(11)+"\",\"desc3\":\""+Clientes.getDouble(12)+"\",\"desc4\":\""+Clientes.getDouble(13)+"\",\"pox\":\"\",\"posy\":\"\",\"fechor\":\"\",\"obs\":\""+Clientes.getString(14)+"\",\"papa\":\"\"}";
                // JsonEnvio="{\"Pedido\":{\"pedido\":\"\",\"nitsec\":\"\",\"clisec\":0,\"artsec\":\"\",\"vencod\":\"\",\"cant\":\"0\",\"pdyear\":0,\"pdmonth\":0,\"pdday\":0,\"precio\":\"0\",\"plazo\":0,\"contreg\":0,\"desc\":\"0.0000\",\"desc2\":\"0.0000\",\"desc3\":\"0.0000\",\"desc4\":\"0.0000\",\"pox\":\"\",\"posy\":\"\",\"fechor\":\"\",\"obs\":\"\",\"papa\":\"\"}}";
                // JsonEnvio="{\"SDTArtSecWS2\":{\"ArtSec\":\"1\"}}";
                //{"Pedido":{"pedido":"P1366712020123","nitsec":"3667","clisec":1,"artsec":"10049","vencod":"7","cant":"1.0","cantcaj":"0","pdyear":2020,"pdmonth":1,"pdday":23,"precio":"209000.0","plazo":60,"contreg":0,"desc":"0.0","desc2":"0.0","desc3":"0.0","desc4":"0.0","confdesc":"0.0","confemp":"0.0","confprov":"0.0","confvend":"0.0","pox":"0.0","posy":"0.0","fechor":"","obs":"","papa":""}}
                //if(SDTResumenPedidos.Total>=vPedidoMinimo) {
                conbd.Variables();
                String sql = conbd.UrlEnvio;  // "http://181.49.42.34:8080/pruebas9/rest/pSetNotaIbanez2";
                //String sql = "http://181.49.42.34:8080/MantisWeb20apps3/rest/pSetPedidoMoviles";
                // String sql = "http://181.49.42.34:8080/MantisWeb20apps3/rest/pGetExistenciaPrecioWs";
                // String sql = "http://181.49.42.34:8080/MantisWeb20apps3/rest/pSetNotaIbanez";
                sExistencia = "0.0";
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                URL url = null;
                HttpURLConnection conn;

                try {
                    url = new URL(sql);

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Content-Type", "application/json");//; utf-8
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestMethod("POST");

                    StringBuilder result = new StringBuilder();
                    //   result.append(URLEncoder.encode("SDTArtSecWS2", "UTF-8"));
                    //   result.append("=");
                    result.append(JsonEnvio); //URLEncoder.encode(  , "UTF-8")


                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os)); //, "UTF-8"

                    writer.write(result.toString());
                    writer.flush();
                    writer.close();
                    os.close();

                    conn.connect();

                    int statusCode = conn.getResponseCode();
                    InputStream inputstream = null;
                    if (statusCode >= 200 && statusCode < 400) {
                        // Create an InputStream in order to extract the response object
                        inputstream = conn.getInputStream();
                    } else {
                        inputstream = conn.getErrorStream();
                    }

                    BufferedReader in = new BufferedReader(new InputStreamReader(inputstream));

                    String inputLine;

                    StringBuffer response = new StringBuffer();

                    String json = "";

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    json = "[" + response.toString() + "]";


                    JSONArray jsonArr = null;

                    jsonArr = new JSONArray(json);
                    String mensaje = "";

                    for (int i = 0; i < jsonArr.length(); i++) {
                        JSONObject jsonObject = jsonArr.getJSONObject(i);
                        sExistencia = jsonObject.optString("Mensaje");
                    }


                    // sal.setText(mensaje);
                } catch (MalformedURLException e) {

                    //   String consulta = "insert into Bitacora(TxtLargo)" +
                    //         "values('"+e.getMessage()+ "')";
                    // BaseDeDatos.getWritableDatabase().execSQL(consulta);
                    sExistencia2 +=  e.getMessage();

                    e.printStackTrace();
                } catch (IOException e) {
                    // String consulta = "insert into Bitacora(TxtLargo)" +
                    //        "values('"+e.getMessage()+ "')";
                    sExistencia2 +=  e.getMessage();
                    e.printStackTrace();
                } catch (JSONException e) {
                    //String consulta = "insert into Bitacora(TxtLargo)" +
                    //       "values('"+e.getMessage()+ "')";
                    sExistencia2 +=  e.getMessage();
                    e.printStackTrace();
                }
                //   }
                //}


            } while (Clientes.moveToNext());
        }
        return sExistencia2;

    }
    public String EnviarPedidos(Context pContext,String prefijo,String nitsec,Integer clisec,Integer env,String pArtSec) {

        ConBd conbd = new ConBd();
      //  Connection connGen = conbd.CargarConexion();

        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();
        Integer vPedidoMinimo=vGlobalVariables.getParMovSec();

        Time time = new Time();
        time.setToNow();

        String sExistencia="";
        String sExistencia2="";

        String NumPed = prefijo+nitsec + clisec +time.year + (time.month + 1) + time.monthDay;

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 5);


        Double pedidoMinimo=0.0;

        Cursor vCursorUsuarios = BaseDeDatos.getReadableDatabase().rawQuery("select ParMovPedMin from usuarios where VenCnt=1  ", null);
        vCursorUsuarios.moveToFirst();

        if (vCursorUsuarios.getCount() >0) {
            pedidoMinimo=vCursorUsuarios.getDouble(0);
        }


        String Consulta = "";
        GlobalVariables gGlobalVariables = GlobalVariables.getInstance();
        String vEmpresa=gGlobalVariables.getEmpresa();
        String mantisficc =  conbd.MantisFicc;
        Cursor Clientes = null;
        String resSql = "";
        if(env == 1) {
            resSql = " and CliNoRee ='N' ";
        }

        if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") || vEmpresa.trim().equalsIgnoreCase("IBANEZPRU")|| vEmpresa.trim().equalsIgnoreCase("SURTIMARCAS")) {
            // case lisprecod when 1 then a.precio1 when 2 then a.precio2 when 3 then a.precio3  when 4  then a.precio4 when 5 then a.precio5 when 6 then a.precio6 when 7 then a.precio7 when 8 then a.precio8 when 9 then a.precio9 when 15 then a.precio5 when 16 then a.precio6  when 17 then a.precio7 when 18 then a.precio8 when 19 then a.precio9 else a.precio1 end
            Consulta = "select p.nitsec,p.clisec,p.artsec,p.vencod,cant,pdyear,pdmonth,pdday,precio  precio1,plazo,pordesc,pordesc2,pordesc3+pordesc6 pordesc3,pordesc4+pordesc5 pordesc4,ifnull(visobs,'') visobs,p.prefijo,(ifnull(pordescno,0)+ifnull(pordesc2no,0)+ifnull(pordesc3no,0)+ifnull(pordesc4no,0)+ifnull(pordesc5no,0)+ifnull(pordesc6no,0))-(ifnull(pordesc,0)+ifnull(pordesc2,0)+ifnull(pordesc3,0)+ifnull(pordesc4,0)+ifnull(pordesc5,0)+ifnull(pordesc6,0)) descno,confemp,confprov,confvend,cantcaj,ConNotCod,ifnull(VisLatitud,'') VisLatitud,ifnull(VisLongitud,'') VisLongitud,ifnull(VisHor,0) VisHor,ifnull(VisMin,0) VisMin,ifnull(VisSeg,0) VisSeg,ifnull(VisHor,0) VisHorFin,ifnull(VisMin,0) VisMinFin,ifnull(VisSeg,0) VisSegFin,0 MovParPremSecLin,PedLisPreCod,cantinf,cantcajinf, '' MovParPremtipo,0 MovParPremSec,ifnull(bodcod,1) bodcod,'N' MovParPremcheckmax, ifNull(ConPagnom, '') ConPagnom  from pedido p " +
                    " left join articulos a on rtrim(a.artsec)=rtrim(p.artsec) " +
                    " left join clientes n on ltrim(rtrim(n.nitsec))=ltrim(rtrim(p.nitsec)) and n.clisec=p.clisec " +
                    " left join visita v on ltrim(rtrim(v.nitsec))=ltrim(rtrim(p.nitsec)) and v.clisec=p.clisec and VisAno=pdyear and pdmonth=VisMes  and visdia=pdday and  Vispref = p.prefijo  " +
                    " where a.precio1>0 and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay+resSql ;



            if(nitsec.isEmpty()){

            }else{
                Consulta+=" and p.nitsec='"+nitsec+"'";
            }
            if(clisec > 0 ){
                Consulta+=" and p.clisec = "+clisec+" ";
            }
            if(pArtSec.isEmpty()) {
            }else{
                Consulta+=" and p.artsec='"+pArtSec+"'";
            }
            if(prefijo.isEmpty()) {
            }else{
                Consulta+=" and p.prefijo='"+prefijo+"'";
            }




                    //" where  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay +MovParPremtipo
            Consulta+=" union all " +
                    " select MovParNitSec,MovParCliSec,m.MovParPremArtSec,'" + vUsuario + "' vencod,MovParPremCant cant,MovParPremAno,MovParPremMes,MovParPremDia,1 precio,0 plazo,0.0 pordesc,0.0 pordesc2,0.0 pordesc3,0.0 pordesc4,'' obs,ifnull(prefijo,'') prefi,0.0,0.0,0.0,0.0,ifnull(MovParPremCantCaj,0) MovParPremCantCaj,0,'','',0,0,0,0,0,0,ifnull(MovParPremSecLin,0) MovParPremSecLin,1 PedLisPreCod,0 infcant,0 infcaj,ifnull(MovParPremtipo,'') MovParPremtipo,ifnull(MovParPremSec,0) MovParPremSec,0 bodcod,ifnull(MovParPremcheckmax,'N') MovParPremcheckmax,'' ConPagnom  from MovParPrem M " +
                    " left join articulos a on a.artsec=m.MovParPremArtSec where MovParPremAno=" + time.year + " and MovParPremMes=" + (time.month + 1) + " and MovParPremDia=" + time.monthDay;

            try {
                Clientes = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null);
            } catch (Exception e) {
                int jj = 0;
            }

        }else {
            if (vEmpresa.trim().equalsIgnoreCase("GELVEZ") || vEmpresa.trim().equalsIgnoreCase("GELVEZGIR") || vEmpresa.trim().equalsIgnoreCase("GELVEZGIRREM") || vEmpresa.trim().equalsIgnoreCase("GELVEZCAL") || vEmpresa.trim().equalsIgnoreCase("GELVEZEJE")) {
                //case lisprecod when 1 then a.precio1 when 2 then a.precio2 when 3 then a.precio3 when 4 then a.precio4 when 5 then a.precio5 when 6 then a.precio6 when 7 then a.precio7 when 8 then a.precio8 when 9 then a.precio9 when 10 then a.precio10 when 11 then a.precio11 when 12 then a.precio12  when 13 then a.precio13  when 14 then a.precio14  when 15 then a.precio15 when 16 then a.precio16  when 17 then a.precio17 when 18 then a.precio18 when 19 then a.precio19 when 20 then a.precio20 when 21 then a.precio21 when 22 then a.precio22 when 23 then a.precio23 when 24 then a.precio24 when 25 then a.precio25 when 26 then a.precio26 when 27 then a.precio27 when 28 then a.precio28 when 29 then a.precio29 when 30 then a.precio30 else a.precio1 end
                Consulta = "select p.nitsec,p.clisec,p.artsec,p.vencod,cant,pdyear,pdmonth,pdday,precio  precio1,plazo,pordesc,pordesc2,pordesc3+pordesc6 pordesc3,pordesc4+pordesc5 pordesc4,ifnull(visobs,'') visobs,p.prefijo,(ifnull(pordescno,0)+ifnull(pordesc2no,0)+ifnull(pordesc3no,0)+ifnull(pordesc4no,0)+ifnull(pordesc5no,0)+ifnull(pordesc6no,0))-(ifnull(pordesc,0)+ifnull(pordesc2,0)+ifnull(pordesc3,0)+ifnull(pordesc4,0)+ifnull(pordesc5,0)+ifnull(pordesc6,0)) descno,confemp,confprov,confvend,cantcaj,ConNotCod,ifnull(VisLatitud,'') VisLatitud,ifnull(VisLongitud,'') VisLongitud,ifnull(VisHor,0) VisHor,ifnull(VisMin,0) VisMin,ifnull(VisSeg,0) VisSeg,ifnull(VisHor,0) VisHorFin,ifnull(VisMin,0) VisMinFin,ifnull(VisSeg,0) VisSegFin,0 MovParPremSecLin,1 PedLisPreCod, 0 cantinf,0 cantcajinf,0 MovParPremtipo,0 MovParPremSec,0 bodcod,'N' MovParPremcheckmax,'' ConPagnom    from pedido p " +
                        " left join articulos a on rtrim(a.artsec)=rtrim(p.artsec) " +
                        " left join clientes n on ltrim(rtrim(n.nitsec))=ltrim(rtrim(p.nitsec)) and n.clisec=p.clisec " +
                        " left join visita v on ltrim(rtrim(v.nitsec))=ltrim(rtrim(p.nitsec)) and v.clisec=p.clisec  and VisAno=pdyear and pdmonth=VisMes  and visdia=pdday" +
                        //" where a.precio1>0 and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay +
                        " where  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay +
                        " union all " +
                        "   select MovParNitSec,MovParCliSec,m.MovParPremArtSec,'" + vUsuario + "' vencod,MovParPremCant cant,MovParPremAno,MovParPremMes,MovParPremDia,1 precio,0 plazo,0.0 pordesc,0.0 pordesc2,0.0 pordesc3,0.0 pordesc4,'' obs,prefijo prefi,0.0,0.0,0.0,0.0,0,0,'','',0,0,0,0,0,0,0 MovParPremSecLin,1 PedLisPreCod,0 cantinf, 0 cantcajinf,0 bodcod,MovParPremcheckmax ,'' ConPagnom  from MovParPrem M " +
                        " left join articulos a on a.artsec=m.MovParPremArtSec where  MovParPremAno=" + time.year + " and MovParPremMes=" + (time.month + 1) + " and MovParPremDia=" + time.monthDay;
                //      +" union all " +
                //      "  select NitSec,CliSec,'VISITA','" + vUsuario + "' vencod,MovCauPed cant,VisAno,VisMes,VisDia,1 precio,0 plazo,0.0 pordesc,0.0 pordesc2,0.0 pordesc3,0.0 pordesc4,'' obs,'VI' prefi,0.0,0.0,0.0,0.0,0,0,'','',VisHor,VisMin,VisSeg,VisHorFin,VisMinFin,VisSegFin  from Visita M " +
                //        " where  VisAno=" + time.year + " and VisMes=" + (time.month + 1) + " and VisDia=" + time.monthDay;
            }else {

                if(vEmpresa.trim().equalsIgnoreCase("DINGLESA")){
                    Consulta = "select p.nitsec,p.clisec,p.artsec,p.vencod,cant,pdyear,pdmonth,pdday,case lisprecod when 1 then a.precio1 when 2 then a.precio2 when 3 then a.precio3 when 4 then a.precio4  when 5 then a.precio5 when 7 then a.precio7 when 8 then a.precio8 when 15 then a.precio5 when 16 then a.precio6  when 17 then a.precio7 when 18 then a.precio8 when 19 then a.precio9 else precio end  precio1,plazo,pordesc,pordesc2,pordesc3+pordesc6 pordesc3,pordesc4+pordesc5 pordesc4,ifnull(visobs,'') visobs,p.prefijo,(ifnull(pordescno,0)+ifnull(pordesc2no,0)+ifnull(pordesc3no,0)+ifnull(pordesc4no,0)+ifnull(pordesc5no,0)+ifnull(pordesc6no,0))-(ifnull(pordesc,0)+ifnull(pordesc2,0)+ifnull(pordesc3,0)+ifnull(pordesc4,0)+ifnull(pordesc5,0)+ifnull(pordesc6,0)) descno,confemp,confprov,confvend,cantcaj,ConNotCod,ifnull(VisLatitud,'') VisLatitud,ifnull(VisLongitud,'') VisLongitud,ifnull(VisHor,0) VisHor,ifnull(VisMin,0) VisMin,ifnull(VisSeg,0) VisSeg,ifnull(VisHor,0) VisHorFin,ifnull(VisMin,0) VisMinFin,ifnull(VisSeg,0) VisSegFin,0 MovParPremSecLin,1 PedLisPreCod,0 cantinf,0 cantcajinf,0 MovParPremtipo,0 MovParPremSec,bodcod,'N' MovParPremcheckmax ,'' ConPagnom  from pedido p " +
                            " left join articulos a on rtrim(a.artsec)=rtrim(p.artsec) " +
                            " left join clientes n on ltrim(rtrim(n.nitsec))=ltrim(rtrim(p.nitsec)) and n.clisec=p.clisec " +
                            " left join visita v on ltrim(rtrim(v.nitsec))=ltrim(rtrim(p.nitsec)) and v.clisec=p.clisec  and VisAno=pdyear and pdmonth=VisMes  and visdia=pdday" +
                            //" where a.precio1>0 and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay +
                            " where (Bloqueo <> 'S' or Bloqueo is null) and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay ;

                }else{
                    Consulta = "select p.nitsec,p.clisec,p.artsec,p.vencod,cant,pdyear,pdmonth,pdday,case lisprecod when 1 then a.precio1 when 2 then a.precio2 when 3 then a.precio3 when 4 then a.precio4  when 5 then a.precio5 when 7 then a.precio7 when 8 then a.precio8 when 15 then a.precio5 when 16 then a.precio6  when 17 then a.precio7 when 18 then a.precio8 when 19 then a.precio9 else precio end  precio1,plazo,pordesc,pordesc2,pordesc3+pordesc6 pordesc3,pordesc4+pordesc5 pordesc4,ifnull(visobs,'') visobs,p.prefijo,(ifnull(pordescno,0)+ifnull(pordesc2no,0)+ifnull(pordesc3no,0)+ifnull(pordesc4no,0)+ifnull(pordesc5no,0)+ifnull(pordesc6no,0))-(ifnull(pordesc,0)+ifnull(pordesc2,0)+ifnull(pordesc3,0)+ifnull(pordesc4,0)+ifnull(pordesc5,0)+ifnull(pordesc6,0)) descno,confemp,confprov,confvend,cantcaj,ConNotCod,ifnull(VisLatitud,'') VisLatitud,ifnull(VisLongitud,'') VisLongitud,ifnull(VisHor,0) VisHor,ifnull(VisMin,0) VisMin,ifnull(VisSeg,0) VisSeg,ifnull(VisHor,0) VisHorFin,ifnull(VisMin,0) VisMinFin,ifnull(VisSeg,0) VisSegFin,0 MovParPremSecLin,1 PedLisPreCod,0 cantinf,0 cantcajinf,0 MovParPremtipo,0 MovParPremSec,bodcod,'N' MovParPremcheckmax ,'' ConPagnom  from pedido p " +
                            " left join articulos a on rtrim(a.artsec)=rtrim(p.artsec) " +
                            " left join clientes n on ltrim(rtrim(n.nitsec))=ltrim(rtrim(p.nitsec)) and n.clisec=p.clisec " +
                            " left join visita v on ltrim(rtrim(v.nitsec))=ltrim(rtrim(p.nitsec)) and v.clisec=p.clisec  and VisAno=pdyear and pdmonth=VisMes  and visdia=pdday" +
                            //" where a.precio1>0 and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay +
                            " where  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay ;

                }



                if(nitsec.isEmpty()){

                }else{
                    Consulta+=" and p.nitsec='"+nitsec+"'";
                }

                Consulta+=" union all " +
                        "   select MovParNitSec,MovParCliSec,m.MovParPremArtSec,'" + vUsuario + "' vencod,MovParPremCant cant,MovParPremAno,MovParPremMes,MovParPremDia,1 precio,0 plazo,0.0 pordesc,0.0 pordesc2,0.0 pordesc3,0.0 pordesc4,'BON' obs,prefijo prefi,0.0,0.0,0.0,0.0,0,0,'','',0,0,0,0,0,0 ,0 MovParPremSecLin,1 PedLisPreCod,0 cantinf,0 cantcajinf,0 MovParPremtipo,0 MovParPremSec ,0 bodcod,MovParPremcheckmax,'' ConPagnom  from MovParPrem M " +
                        "left join articulos a on a.artsec=m.MovParPremArtSec where MovParPremCant > 0 and MovParPremAno=" + time.year + " and MovParPremMes=" + (time.month + 1) + " and MovParPremDia=" + time.monthDay;
            }



            try {
                Clientes = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null);
            } catch (Exception e) {
                int jj = 0;
            }
        }


        String JsonEnvio = "";
        String JsonDetalle = "";
        if (Clientes.getCount() > 0) {
            Clientes.moveToFirst();
            do {

                Double TotalPedido= TotalesPedido(pContext,Clientes.getString(15),Clientes.getString(0),Clientes.getInt(1),"","","").Total;
                Double TotalPedidoCli = TotalesPedidocliente(pContext,Clientes.getString(15),Clientes.getString(0),Clientes.getInt(1),"","","").Total;

                //if ((pedidoMinimo!=0 && TotalPedido>pedidoMinimo ) || pedidoMinimo==0) {
                int sincupo=0;
                if ((vEmpresa.trim().equalsIgnoreCase("MENTAHAIR") ) || (vEmpresa.trim().equalsIgnoreCase("MENTAHAIRCOT") )|| (vEmpresa.trim().equalsIgnoreCase("DINGLESA") )) {
                    GestorCartera gestorcartera=new GestorCartera();
                    gestorcartera.TotalesCatera(pContext, nitsec, clisec);
                    Integer CarteraGeneral =gestorcartera.CarteraGeneral;
                    String tmpNitSec=Clientes.getString(0);



                    Integer tmpCliSec=Clientes.getInt(1);
                    Cursor cursorclientes = BaseDeDatos.getReadableDatabase().rawQuery("select CliCup,CliConPag from clientes where nitsec='" + tmpNitSec + "' and clisec=" + tmpCliSec, null);
                    Integer vuelta = 0;
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
                if (sincupo==0){
                    Double CantUni = 0.00;
                    Double cantinf = 0.00;
                    Integer CantCajas = 0;
                    Integer cantcajinf = 0;
                    if ((pedidoMinimo!=0 && TotalPedidoCli>pedidoMinimo ) || pedidoMinimo==0.0) {
                         CantUni = Clientes.getDouble(4);
                         CantCajas = Clientes.getInt(20);
                        cantinf = Clientes.getDouble(32);
                        cantcajinf = Clientes.getInt(33);
                    }

                        JsonEnvio = "";
                        //Clientes.getString(3)
                        String NumPedido = Clientes.getString(15)+vUsuario.trim()+ Clientes.getString(0) + Clientes.getInt(1) + time.year +"-"+ (time.month + 1) +"-"+ time.monthDay;

                        SDTResumenPedidos SDTResumenPedidos = new SDTResumenPedidos();
                        SDTResumenPedidos = TotalesPedido(pContext, Clientes.getString(15), Clientes.getString(0), Clientes.getInt(1), "", "", "");

                        String Observacion = Clientes.getString(14);
                        int BonProSec =Clientes.getInt(35);
                        int BONPRODETLIN = Clientes.getInt(30);
                        int ArtBodCod =  Clientes.getInt(36);
                        String BonProTipo =  Clientes.getString(34);


                        Observacion=Observacion.replaceAll("[^\\w ]+", "");


                   //  = precioesp(pContext,Clientes.getString(0),Clientes.getString(2), Clientes.getDouble(8) );

                    double precioNu = 0;
                        precioNu = 0;
                        String consultaart = "select precioesp from PreciosEspeciales where peNitSec = '"+nitsec+"' and peArtSec = '"+Clientes.getString(2).trim()+"'";
                        Cursor cursorart = BaseDeDatos.getWritableDatabase().rawQuery(consultaart, null);
                        cursorart.moveToFirst();
                        if(cursorart.getCount() > 0){
                            cursorart.moveToFirst();
                            precioNu = cursorart.getDouble(0);
                        }else{
                            precioNu = Clientes.getDouble(8);
                        }



                    Double preciocal = precioNu;


                    if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") || vEmpresa.trim().equalsIgnoreCase("IBANEZPRU") || vEmpresa.trim().equalsIgnoreCase("SURTIMARCAS") ) {

                        try{
                            descuentosAplicado(pContext,Clientes.getString(0),Clientes.getString(2),Clientes.getString(15),NumPedido,Clientes.getInt(1));
                        }catch (SQLException e){

                        }

                        Cursor pedidoen = BaseDeDatos.getWritableDatabase().rawQuery("select Obs,planpuente from Pedidoenc where prefijo = '"+Clientes.getString(15)+"' and nitsec = '" + Clientes.getString(0) + "' and clisec = " + Clientes.getInt(1) + " and  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  " /*and VisPref='" + Prefijo+"'"*/, null);
                        pedidoen.moveToFirst();
                        String PlanPuente = "N";
                        if (pedidoen.getCount() > 0) {
                            pedidoen.moveToFirst();
                            Observacion = pedidoen.getString(0);
                            PlanPuente = pedidoen.getString(1);
                        }


                        JsonEnvio = "{\"pedidoV7\":{\"pedido\":\"" + NumPedido + "\",\"nitsec\":\"" + Clientes.getString(0) + "\",\"clisec\":" + Clientes.getInt(1) + ",\"artsec\":\"" + Clientes.getString(2) + "\",\"vencod\":\"" + vUsuario + "\",\"cant\":\"" + CantUni + "\",\"cantcaj\":\"" + CantCajas + "\",\"cantinf\":\"" + cantinf + "\",\"cantcajinf\":\"" + cantcajinf + "\",\"ArtBodCod\":\"" + ArtBodCod + "\",\"BonProSec\":\"" + BonProSec + "\",\"BONPRODETLIN\":\"" + BONPRODETLIN + "\",\"BonProTipo\":\"" + BonProTipo + "\",\"pdyear\":" + Clientes.getInt(5) + ",\"pdmonth\":" + (time.month + 1) + ",\"pdday\":" + Clientes.getInt(7) + ",\"precio\":\"" + preciocal + "\",\"plazo\":" + Clientes.getInt(9) + ",\"contreg\":" + Clientes.getInt(21) + ",\"desc\":\"" + Clientes.getDouble(10) + "\",\"desc2\":\"" + Clientes.getDouble(11) + "\",\"desc3\":\"" + Clientes.getDouble(12) + "\",\"desc4\":\"" + Clientes.getDouble(13) + "\",\"confdesc\":\"" + Clientes.getDouble(16) + "\",\"confemp\":\"" + Clientes.getDouble(17) + "\",\"confprov\":\"" + Clientes.getDouble(18) + "\",\"confvend\":\"" + Clientes.getDouble(19) + "\",\"pox\":\"" + Clientes.getString(22) + "\",\"posy\":\"" + Clientes.getString(23) + "\",\"fechor\":\"\",\"obs\":\"" + Observacion + "\",\"papa\":\"\",\"prenotsinped\":\"N\",\"vishor\":\"" + Clientes.getInt(24) + "\",\"vismin\":\"" + Clientes.getInt(25) + "\",\"visseg\":\"" + Clientes.getInt(26) + "\",\"vishorfin\":\"" + Clientes.getInt(27) + "\",\"visminfin\":\"" + Clientes.getInt(28) + "\",\"vissegfin\":\"" + Clientes.getInt(29) + "\",\"vissegfin\":\"" + Clientes.getInt(29) + "\",\"totalped\":\"" + TotalPedido + "\",\"CotDetCheckMax\":\""+Clientes.getString(37)+"\",\"PlazoNom\":\""+Clientes.getString(38)+"\",\"pedlisprecod\":\"" + Clientes.getInt(31) + "\",\"PlanPuente\":\"" + PlanPuente + "\"}}";


                    }else{

                        if (vEmpresa.trim().equalsIgnoreCase("DINGLESA") || vEmpresa.trim().equalsIgnoreCase("SUMMEDSAN") ){
                            JsonEnvio = "{\"pedido2\":{\"pedido\":\"" + NumPedido + "\",\"nitsec\":\"" + Clientes.getString(0) + "\",\"clisec\":" + Clientes.getInt(1) + ",\"artsec\":\"" + Clientes.getString(2) + "\",\"vencod\":\"" + vUsuario + "\",\"cant\":\"" + CantUni + "\",\"cantcaj\":\"" + CantCajas + "\",\"pdyear\":" + Clientes.getInt(5) + ",\"pdmonth\":" + (time.month + 1) + ",\"pdday\":" + Clientes.getInt(7) + ",\"precio\":\"" + Clientes.getDouble(8) + "\",\"plazo\":" + Clientes.getInt(9) + ",\"contreg\":" + Clientes.getInt(21) + ",\"desc\":\"" + Clientes.getDouble(10) + "\",\"desc2\":\"" + Clientes.getDouble(11) + "\",\"desc3\":\"" + Clientes.getDouble(12) + "\",\"desc4\":\"" + Clientes.getDouble(13) + "\",\"confdesc\":\"" + Clientes.getDouble(16) + "\",\"confemp\":\"" + Clientes.getDouble(17) + "\",\"confprov\":\"" + Clientes.getDouble(18) + "\",\"confvend\":\"" + Clientes.getDouble(19) + "\",\"pox\":\"" + Clientes.getString(22) + "\",\"posy\":\"" + Clientes.getString(23) + "\",\"fechor\":\"\",\"obs\":\"" + Observacion + "\",\"papa\":\"\",\"prenotsinped\":\"N\",\"vishor\":\"" + Clientes.getInt(24) + "\",\"vismin\":\"" + Clientes.getInt(25) + "\",\"visseg\":\"" + Clientes.getInt(26) + "\",\"vishorfin\":\"" + Clientes.getInt(27) + "\",\"visminfin\":\"" + Clientes.getInt(28) + "\",\"vissegfin\":\"" + Clientes.getInt(29) + "\",\"vissegfin\":\"" + Clientes.getInt(29) + "\",\"BodSucccSec\":\"" + ArtBodCod + "\"}}";
                        }else{


                            Cursor pedidoen = BaseDeDatos.getWritableDatabase().rawQuery("select Obs,planpuente from Pedidoenc where prefijo = '"+Clientes.getString(15)+"' and nitsec = '" + Clientes.getString(0) + "' and clisec = " + Clientes.getInt(1) + " and  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  " /*and VisPref='" + Prefijo+"'"*/, null);
                            pedidoen.moveToFirst();
                           // String PlanPuente = "N";
                            if (pedidoen.getCount() > 0) {
                                pedidoen.moveToFirst();
                                Observacion = pedidoen.getString(0);
                               // PlanPuente = pedidoen.getString(1);
                            }


                            Log.e("ENTROS SURTI: ","AAAAAAAAAAAAAAAAAAAAAAAA");

                            JsonEnvio = "{\"Pedido\":{\"pedido\":\"" + NumPedido + "\",\"nitsec\":\"" + Clientes.getString(0) + "\",\"clisec\":" + Clientes.getInt(1) + ",\"artsec\":\"" + Clientes.getString(2) + "\",\"vencod\":\"" + vUsuario + "\",\"cant\":\"" + CantUni + "\",\"cantcaj\":\"" + CantCajas + "\",\"pdyear\":" + Clientes.getInt(5) + ",\"pdmonth\":" + (time.month + 1) + ",\"pdday\":" + Clientes.getInt(7) + ",\"precio\":\"" + Clientes.getDouble(8) + "\",\"plazo\":" + Clientes.getInt(9) + ",\"contreg\":" + Clientes.getInt(21) + ",\"desc\":\"" + Clientes.getDouble(10) + "\",\"desc2\":\"" + Clientes.getDouble(11) + "\",\"desc3\":\"" + Clientes.getDouble(12) + "\",\"desc4\":\"" + Clientes.getDouble(13) + "\",\"confdesc\":\"" + Clientes.getDouble(16) + "\",\"confemp\":\"" + Clientes.getDouble(17) + "\",\"confprov\":\"" + Clientes.getDouble(18) + "\",\"confvend\":\"" + Clientes.getDouble(19) + "\",\"pox\":\"" + Clientes.getString(22) + "\",\"posy\":\"" + Clientes.getString(23) + "\",\"fechor\":\"\",\"obs\":\"" + Observacion + "\",\"papa\":\"\",\"prenotsinped\":\"N\",\"vishor\":\"" + Clientes.getInt(24) + "\",\"vismin\":\"" + Clientes.getInt(25) + "\",\"visseg\":\"" + Clientes.getInt(26) + "\",\"vishorfin\":\"" + Clientes.getInt(27) + "\",\"visminfin\":\"" + Clientes.getInt(28) + "\",\"vissegfin\":\"" + Clientes.getInt(29) + "\",\"vissegfin\":\"" + Clientes.getInt(29) + "\"}}";

                        }
                   }



                        //JsonEnvio="{\"Pedido\":{\"pedido\":\""+NumPedido+"\",\"nitsec\":\""+Clientes.getString(0)+"\",\"clisec\":"+Clientes.getInt(1)+",\"artsec\":\""+Clientes.getString(2)+"\",\"vencod\":\""+vUsuario+"\",\"cant\":\""+Clientes.getDouble(4)+"\",\"cantcaj\":\""+Clientes.getInt(20)+"\",\"pdyear\":"+Clientes.getInt(5)+",\"pdmonth\":"+(time.month + 1)+",\"pdday\":"+Clientes.getInt(7)+",\"precio\":\""+Clientes.getDouble(8)+"\",\"plazo\":"+Clientes.getInt(9)+",\"contreg\":"+Clientes.getInt(21)+",\"desc\":\""+Clientes.getDouble(10)+"\",\"desc2\":\""+Clientes.getDouble(11)+"\",\"desc3\":\""+Clientes.getDouble(12)+"\",\"desc4\":\""+Clientes.getDouble(13)+"\",\"confdesc\":\""+Clientes.getDouble(16)+"\",\"confemp\":\""+Clientes.getDouble(17)+"\",\"confprov\":\""+Clientes.getDouble(18)+"\",\"confvend\":\""+Clientes.getDouble(19)+"\",\"pox\":\""+Clientes.getString(22)+"\",\"posy\":\""+Clientes.getString(23)+"\",\"fechor\":\"\",\"obs\":\""+Clientes.getString(14)+"\",\"papa\":\"\",\"prenotSinPed\":\"\"}}";
                        // JsonEnvio="{\"pedido\":\""+Clientes.getString(15)+"\",\"nitsec\":\""+nitsec+"\",\"clisec\":"+clisec+",\"artsec\":\""+Clientes.getString(2)+"\",\"vencod\":\""+4+"\",\"cant\":\""+Clientes.getDouble(4)+"\",\"pdyear\":"+Clientes.getInt(5)+",\"pdmonth\":"+Clientes.getInt(6)+",\"pdday\":"+Clientes.getInt(7)+",\"precio\":\""+Clientes.getDouble(8)+"\",\"plazo\":"+Clientes.getInt(9)+",\"contreg\":0,\"desc\":\""+Clientes.getDouble(10)+"\",\"desc2\":\""+Clientes.getDouble(11)+"\",\"desc3\":\""+Clientes.getDouble(12)+"\",\"desc4\":\""+Clientes.getDouble(13)+"\",\"pox\":\"\",\"posy\":\"\",\"fechor\":\"\",\"obs\":\""+Clientes.getString(14)+"\",\"papa\":\"\"}";
                        // JsonEnvio="{\"Pedido\":{\"pedido\":\"\",\"nitsec\":\"\",\"clisec\":0,\"artsec\":\"\",\"vencod\":\"\",\"cant\":\"0\",\"pdyear\":0,\"pdmonth\":0,\"pdday\":0,\"precio\":\"0\",\"plazo\":0,\"contreg\":0,\"desc\":\"0.0000\",\"desc2\":\"0.0000\",\"desc3\":\"0.0000\",\"desc4\":\"0.0000\",\"pox\":\"\",\"posy\":\"\",\"fechor\":\"\",\"obs\":\"\",\"papa\":\"\"}}";
                        // JsonEnvio="{\"SDTArtSecWS2\":{\"ArtSec\":\"1\"}}";
                        //{"Pedido":{"pedido":"P1366712020123","nitsec":"3667","clisec":1,"artsec":"10049","vencod":"7","cant":"1.0","cantcaj":"0","pdyear":2020,"pdmonth":1,"pdday":23,"precio":"209000.0","plazo":60,"contreg":0,"desc":"0.0","desc2":"0.0","desc3":"0.0","desc4":"0.0","confdesc":"0.0","confemp":"0.0","confprov":"0.0","confvend":"0.0","pox":"0.0","posy":"0.0","fechor":"","obs":"","papa":""}}
                        //if(SDTResumenPedidos.Total>=vPedidoMinimo) {
                    conbd.Variables();
                        String sql = conbd.UrlEnvio;  // "http://181.49.42.34:8080/pruebas9/rest/pSetNotaIbanez2";
                        //String sql = "http://181.49.42.34:8080/MantisWeb20apps3/rest/pSetPedidoMoviles";
                        // String sql = "http://181.49.42.34:8080/MantisWeb20apps3/rest/pGetExistenciaPrecioWs";
                        // String sql = "http://181.49.42.34:8080/MantisWeb20apps3/rest/pSetNotaIbanez";

                    Log.e("UrlEnvio",sql);
                    Log.e("JsonEnvio",JsonEnvio);

                    sExistencia = "0.0";
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);

                        URL url = null;
                        HttpURLConnection conn;

                        try {
                            url = new URL(sql);

                            conn = (HttpURLConnection) url.openConnection();
                            conn.setConnectTimeout(300000);
                            conn.setRequestProperty("Content-Type", "application/json");//; utf-8
                            conn.setDoOutput(true);
                            conn.setDoInput(true);
                            conn.setRequestMethod("POST");

                            StringBuilder result = new StringBuilder();
                            //   result.append(URLEncoder.encode("SDTArtSecWS2", "UTF-8"));
                            //   result.append("=");
                            result.append(JsonEnvio); //URLEncoder.encode(  , "UTF-8")


                            OutputStream os = conn.getOutputStream();
                            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(os)); //, "UTF-8"

                            writer.write(result.toString());
                            writer.flush();
                            writer.close();
                            os.close();

                            conn.connect();

                            int statusCode = conn.getResponseCode();
                            InputStream inputstream = null;
                            if (statusCode >= 200 && statusCode < 400) {
                                // Create an InputStream in order to extract the response object




                                inputstream = conn.getInputStream();



                            } else {
                                //actualizarestado(pContext,Clientes.getString(0),Clientes.getInt(1),vUsuario,NumPedido,"N");
                                inputstream = conn.getErrorStream();

                            }

                            BufferedReader in = new BufferedReader(new InputStreamReader(inputstream));

                            String inputLine;

                            StringBuffer response = new StringBuffer();

                            String json = "";

                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }

                            json = "[" + response.toString() + "]";


                            JSONArray jsonArr = null;

                            jsonArr = new JSONArray(json);
                            String mensaje = "";
                            String nstatus = "";
                            String Error = "";
                            for (int i = 0; i < jsonArr.length(); i++) {
                                JSONObject jsonObject = jsonArr.getJSONObject(i);
                                sExistencia = jsonObject.optString("Mensaje");
                                nstatus = jsonObject.optString("status");
                                nstatus = jsonObject.optString("error");
                                Log.e("sExistenciasExistencia",sExistencia);

                                actualizarestado(pContext,Clientes.getString(0),Clientes.getInt(1),vUsuario,NumPedido,nstatus);



                            }


                            // sal.setText(mensaje);
                        } catch (MalformedURLException e) {
                            Log.e("Error",e.toString());
                            sExistencia2 +=  e.getMessage();
                            e.printStackTrace();
                        } catch (IOException e) {
                            Log.e("Error",e.toString());

                            sExistencia2 +=  e.getMessage();
                            e.printStackTrace();
                        } catch (JSONException e) {

                            sExistencia2 +=  e.getMessage();
                            e.printStackTrace();
                        }
                        //   }
                    //}
                }

            } while (Clientes.moveToNext());
        }
        return sExistencia2;

    }
    //creado andres devuelve el numero de enviados
    public String EnviarPedidosint(Context pContext,String prefijo,String nitsec,Integer clisec,Integer env,String pArtSec,int ptotalped) {

        ConBd conbd = new ConBd();
       // Connection connGen = conbd.CargarConexion();

        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();
        Integer vPedidoMinimo=vGlobalVariables.getParMovSec();

        Time time = new Time();
        time.setToNow();

        String sExistencia="";
        String sExistencia2="";
        int enviado = 0 ;
        String valorEnv  ="0.00";
        String NumPed = prefijo+nitsec + clisec +time.year + (time.month + 1) + time.monthDay;

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 5);


        Double pedidoMinimo=0.0;

        Cursor vCursorUsuarios = BaseDeDatos.getReadableDatabase().rawQuery("select ParMovPedMin from usuarios where VenCnt=1  ", null);
        vCursorUsuarios.moveToFirst();

        if (vCursorUsuarios.getCount() >0) {
            pedidoMinimo=vCursorUsuarios.getDouble(0);
        }


        String Consulta = "";
        GlobalVariables gGlobalVariables = GlobalVariables.getInstance();
        String vEmpresa=gGlobalVariables.getEmpresa();
        String mantisficc =  conbd.MantisFicc;
        Cursor Clientes = null;
        String resSql = "";
        if(env == 1) {
            resSql = " and CliNoRee ='N' ";
        }

        if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") || vEmpresa.trim().equalsIgnoreCase("IBANEZPRU") || vEmpresa.trim().equalsIgnoreCase("SURTIMARCAS")) {
            // case lisprecod when 1 then a.precio1 when 2 then a.precio2 when 3 then a.precio3  when 4  then a.precio4 when 5 then a.precio5 when 6 then a.precio6 when 7 then a.precio7 when 8 then a.precio8 when 9 then a.precio9 when 15 then a.precio5 when 16 then a.precio6  when 17 then a.precio7 when 18 then a.precio8 when 19 then a.precio9 else a.precio1 end
            Consulta = "select p.nitsec,p.clisec,p.artsec,p.vencod,cant,pdyear,pdmonth,pdday,precio  precio1,plazo,pordesc,pordesc2,pordesc3+pordesc6 pordesc3,pordesc4+pordesc5 pordesc4,ifnull(visobs,'') visobs,p.prefijo,(ifnull(pordescno,0)+ifnull(pordesc2no,0)+ifnull(pordesc3no,0)+ifnull(pordesc4no,0)+ifnull(pordesc5no,0)+ifnull(pordesc6no,0))-(ifnull(pordesc,0)+ifnull(pordesc2,0)+ifnull(pordesc3,0)+ifnull(pordesc4,0)+ifnull(pordesc5,0)+ifnull(pordesc6,0)) descno,confemp,confprov,confvend,cantcaj,ConNotCod,ifnull(VisLatitud,'') VisLatitud,ifnull(VisLongitud,'') VisLongitud,ifnull(VisHor,0) VisHor,ifnull(VisMin,0) VisMin,ifnull(VisSeg,0) VisSeg,ifnull(VisHor,0) VisHorFin,ifnull(VisMin,0) VisMinFin,ifnull(VisSeg,0) VisSegFin,0 MovParPremSecLin,PedLisPreCod,cantinf,cantcajinf, '' MovParPremtipo,0 MovParPremSec,ifnull(bodcod,1) bodcod,'N' MovParPremcheckmax, ifNull(ConPagnom, '') ConPagnom  from pedido p " +
                    " left join articulos a on rtrim(a.artsec)=rtrim(p.artsec) " +
                    " left join clientes n on ltrim(rtrim(n.nitsec))=ltrim(rtrim(p.nitsec)) and n.clisec=p.clisec " +
                    " left join visita v on ltrim(rtrim(v.nitsec))=ltrim(rtrim(p.nitsec)) and v.clisec=p.clisec and VisAno=pdyear and pdmonth=VisMes  and visdia=pdday and  Vispref = p.prefijo  " +
                    " where a.precio1>0 and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay+resSql ;
            if(nitsec.isEmpty()){

            }else{
                Consulta+=" and p.nitsec='"+nitsec+"'";
            }
            if(clisec > 0 ){
                Consulta+=" and p.clisec = "+clisec+" ";
            }
            if(pArtSec.isEmpty()) {
            }else{
                Consulta+=" and p.artsec='"+pArtSec+"'";
            }
            if(prefijo.isEmpty()) {
            }else{
                Consulta+=" and p.prefijo='"+prefijo+"'";
            }
          //  Log.e("PReijo",Consulta);

            //" where  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay +MovParPremtipo
            Consulta+=" union all " +
                    " select MovParNitSec,MovParCliSec,m.MovParPremArtSec,'" + vUsuario + "' vencod,MovParPremCant cant,MovParPremAno,MovParPremMes,MovParPremDia,1 precio,0 plazo,0.0 pordesc,0.0 pordesc2,0.0 pordesc3,0.0 pordesc4,'' obs,ifnull(prefijo,'') prefi,0.0,0.0,0.0,0.0,ifnull(MovParPremCantCaj,0) MovParPremCantCaj,0,'','',0,0,0,0,0,0,ifnull(MovParPremSecLin,0) MovParPremSecLin,1 PedLisPreCod,0 infcant,0 infcaj,ifnull(MovParPremtipo,'') MovParPremtipo,ifnull(MovParPremSec,0) MovParPremSec,0 bodcod,ifnull(MovParPremcheckmax,'N') MovParPremcheckmax,'' ConPagnom  from MovParPrem M " +
                    " left join articulos a on a.artsec=m.MovParPremArtSec where MovParPremAno=" + time.year + " and MovParPremMes=" + (time.month + 1) + " and MovParPremDia=" + time.monthDay+" order by p.prefijo,p.nitsec,p.clisec ";

            try {
                Clientes = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null);
            } catch (Exception e) {
                int jj = 0;
            }

        }else {
            if (vEmpresa.trim().equalsIgnoreCase("GELVEZ") || vEmpresa.trim().equalsIgnoreCase("GELVEZGIR") || vEmpresa.trim().equalsIgnoreCase("GELVEZGIRREM") || vEmpresa.trim().equalsIgnoreCase("GELVEZCAL") || vEmpresa.trim().equalsIgnoreCase("GELVEZEJE")) {
                //case lisprecod when 1 then a.precio1 when 2 then a.precio2 when 3 then a.precio3 when 4 then a.precio4 when 5 then a.precio5 when 6 then a.precio6 when 7 then a.precio7 when 8 then a.precio8 when 9 then a.precio9 when 10 then a.precio10 when 11 then a.precio11 when 12 then a.precio12  when 13 then a.precio13  when 14 then a.precio14  when 15 then a.precio15 when 16 then a.precio16  when 17 then a.precio17 when 18 then a.precio18 when 19 then a.precio19 when 20 then a.precio20 when 21 then a.precio21 when 22 then a.precio22 when 23 then a.precio23 when 24 then a.precio24 when 25 then a.precio25 when 26 then a.precio26 when 27 then a.precio27 when 28 then a.precio28 when 29 then a.precio29 when 30 then a.precio30 else a.precio1 end
                Consulta = "select p.nitsec,p.clisec,p.artsec,p.vencod,cant,pdyear,pdmonth,pdday,precio  precio1,plazo,pordesc,pordesc2,pordesc3+pordesc6 pordesc3,pordesc4+pordesc5 pordesc4,ifnull(visobs,'') visobs,p.prefijo,(ifnull(pordescno,0)+ifnull(pordesc2no,0)+ifnull(pordesc3no,0)+ifnull(pordesc4no,0)+ifnull(pordesc5no,0)+ifnull(pordesc6no,0))-(ifnull(pordesc,0)+ifnull(pordesc2,0)+ifnull(pordesc3,0)+ifnull(pordesc4,0)+ifnull(pordesc5,0)+ifnull(pordesc6,0)) descno,confemp,confprov,confvend,cantcaj,ConNotCod,ifnull(VisLatitud,'') VisLatitud,ifnull(VisLongitud,'') VisLongitud,ifnull(VisHor,0) VisHor,ifnull(VisMin,0) VisMin,ifnull(VisSeg,0) VisSeg,ifnull(VisHor,0) VisHorFin,ifnull(VisMin,0) VisMinFin,ifnull(VisSeg,0) VisSegFin,0 MovParPremSecLin,1 PedLisPreCod, 0 cantinf,0 cantcajinf,0 MovParPremtipo,0 MovParPremSec,0 bodcod,'N' MovParPremcheckmax,'' ConPagnom    from pedido p " +
                        " left join articulos a on rtrim(a.artsec)=rtrim(p.artsec) " +
                        " left join clientes n on ltrim(rtrim(n.nitsec))=ltrim(rtrim(p.nitsec)) and n.clisec=p.clisec " +
                        " left join visita v on ltrim(rtrim(v.nitsec))=ltrim(rtrim(p.nitsec)) and v.clisec=p.clisec  and VisAno=pdyear and pdmonth=VisMes  and visdia=pdday" +
                        //" where a.precio1>0 and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay +
                        " where  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay +
                        " union all " +
                        "   select MovParNitSec,MovParCliSec,m.MovParPremArtSec,'" + vUsuario + "' vencod,MovParPremCant cant,MovParPremAno,MovParPremMes,MovParPremDia,1 precio,0 plazo,0.0 pordesc,0.0 pordesc2,0.0 pordesc3,0.0 pordesc4,'' obs,prefijo prefi,0.0,0.0,0.0,0.0,0,0,'','',0,0,0,0,0,0,0 MovParPremSecLin,1 PedLisPreCod,0 cantinf, 0 cantcajinf,0 bodcod,MovParPremcheckmax ,'' ConPagnom  from MovParPrem M " +
                        " left join articulos a on a.artsec=m.MovParPremArtSec where  MovParPremAno=" + time.year + " and MovParPremMes=" + (time.month + 1) + " and MovParPremDia=" + time.monthDay;
                //      +" union all " +
                //      "  select NitSec,CliSec,'VISITA','" + vUsuario + "' vencod,MovCauPed cant,VisAno,VisMes,VisDia,1 precio,0 plazo,0.0 pordesc,0.0 pordesc2,0.0 pordesc3,0.0 pordesc4,'' obs,'VI' prefi,0.0,0.0,0.0,0.0,0,0,'','',VisHor,VisMin,VisSeg,VisHorFin,VisMinFin,VisSegFin  from Visita M " +
                //        " where  VisAno=" + time.year + " and VisMes=" + (time.month + 1) + " and VisDia=" + time.monthDay;
            }else {

                Consulta = "select p.nitsec,p.clisec,p.artsec,p.vencod,cant,pdyear,pdmonth,pdday,case lisprecod when 1 then a.precio1 when 2 then a.precio2 when 3 then a.precio3 when 4 then a.precio4  when 5 then a.precio5 when 7 then a.precio7 when 8 then a.precio8 when 15 then a.precio5 when 16 then a.precio6  when 17 then a.precio7 when 18 then a.precio8 when 19 then a.precio9 else precio end  precio1,plazo,pordesc,pordesc2,pordesc3+pordesc6 pordesc3,pordesc4+pordesc5 pordesc4,ifnull(visobs,'') visobs,p.prefijo,(ifnull(pordescno,0)+ifnull(pordesc2no,0)+ifnull(pordesc3no,0)+ifnull(pordesc4no,0)+ifnull(pordesc5no,0)+ifnull(pordesc6no,0))-(ifnull(pordesc,0)+ifnull(pordesc2,0)+ifnull(pordesc3,0)+ifnull(pordesc4,0)+ifnull(pordesc5,0)+ifnull(pordesc6,0)) descno,confemp,confprov,confvend,cantcaj,ConNotCod,ifnull(VisLatitud,'') VisLatitud,ifnull(VisLongitud,'') VisLongitud,ifnull(VisHor,0) VisHor,ifnull(VisMin,0) VisMin,ifnull(VisSeg,0) VisSeg,ifnull(VisHor,0) VisHorFin,ifnull(VisMin,0) VisMinFin,ifnull(VisSeg,0) VisSegFin,0 MovParPremSecLin,1 PedLisPreCod,0 cantinf,0 cantcajinf,0 MovParPremtipo,0 MovParPremSec,bodcod,'N' MovParPremcheckmax ,'' ConPagnom  from pedido p " +
                        " left join articulos a on rtrim(a.artsec)=rtrim(p.artsec) " +
                        " left join clientes n on ltrim(rtrim(n.nitsec))=ltrim(rtrim(p.nitsec)) and n.clisec=p.clisec " +
                        " left join visita v on ltrim(rtrim(v.nitsec))=ltrim(rtrim(p.nitsec)) and v.clisec=p.clisec  and VisAno=pdyear and pdmonth=VisMes  and visdia=pdday" +
                        //" where a.precio1>0 and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay +
                        " where  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay ;

                if(nitsec.isEmpty()){

                }else{
                    Consulta+=" and p.nitsec='"+nitsec+"'";
                }

                Consulta+=" union all " +
                        "   select MovParNitSec,MovParCliSec,m.MovParPremArtSec,'" + vUsuario + "' vencod,MovParPremCant cant,MovParPremAno,MovParPremMes,MovParPremDia,1 precio,0 plazo,0.0 pordesc,0.0 pordesc2,0.0 pordesc3,0.0 pordesc4,'BON' obs,prefijo prefi,0.0,0.0,0.0,0.0,0,0,'','',0,0,0,0,0,0 ,0 MovParPremSecLin,1 PedLisPreCod,0 cantinf,0 cantcajinf,0 MovParPremtipo,0 MovParPremSec ,0 bodcod,MovParPremcheckmax,'' ConPagnom  from MovParPrem M " +
                        "left join articulos a on a.artsec=m.MovParPremArtSec where MovParPremCant > 0 and MovParPremAno=" + time.year + " and MovParPremMes=" + (time.month + 1) + " and MovParPremDia=" + time.monthDay;
            }
            try {
                Clientes = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null);
            } catch (Exception e) {
                int jj = 0;
            }
        }


        String JsonEnvio = "";
        String JsonDetalle = "";
        String antNumpedido ="";

        String numpedido[] =new String[ptotalped];

        if (Clientes.getCount() > 0) {
            Clientes.moveToFirst();
            do {

                Double TotalPedido= TotalesPedido(pContext,Clientes.getString(15),Clientes.getString(0),Clientes.getInt(1),"","","").Total;
                Double TotalPedidoCli = TotalesPedidocliente(pContext,Clientes.getString(15),Clientes.getString(0),Clientes.getInt(1),"","","").Total;

                //if ((pedidoMinimo!=0 && TotalPedido>pedidoMinimo ) || pedidoMinimo==0) {
                int sincupo=0;
                if ((vEmpresa.trim().equalsIgnoreCase("MENTAHAIR") ) || (vEmpresa.trim().equalsIgnoreCase("MENTAHAIRCOT") ) || (vEmpresa.trim().equalsIgnoreCase("DINGLESA") ) ) {
                    GestorCartera gestorcartera=new GestorCartera();
                    gestorcartera.TotalesCatera(pContext, nitsec, clisec);
                    Integer CarteraGeneral =gestorcartera.CarteraGeneral;
                    String tmpNitSec=Clientes.getString(0);




                    Integer tmpCliSec=Clientes.getInt(1);
                    Cursor cursorclientes = BaseDeDatos.getReadableDatabase().rawQuery("select CliCup,CliConPag from clientes where nitsec='" + tmpNitSec + "' and clisec=" + tmpCliSec, null);
                    Integer vuelta = 0;
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



                if (sincupo==0){
                    Double CantUni = 0.00;
                    Double cantinf = 0.00;
                    Integer CantCajas = 0;
                    Integer cantcajinf = 0;
                    if ((pedidoMinimo!=0 && TotalPedidoCli>pedidoMinimo ) || pedidoMinimo==0.0) {
                        CantUni = Clientes.getDouble(4);
                        CantCajas = Clientes.getInt(20);
                        cantinf = Clientes.getDouble(32);
                        cantcajinf = Clientes.getInt(33);
                    }

                    JsonEnvio = "";
                    //Clientes.getString(3)
                    String NumPedido = Clientes.getString(15)+vUsuario.trim()+ Clientes.getString(0) + Clientes.getInt(1) + time.year +"-"+ (time.month + 1) +"-"+ time.monthDay;

                    SDTResumenPedidos SDTResumenPedidos = new SDTResumenPedidos();
                    SDTResumenPedidos = TotalesPedido(pContext, Clientes.getString(15), Clientes.getString(0), Clientes.getInt(1), "", "", "");

                    String Observacion = Clientes.getString(14);
                    int BonProSec =Clientes.getInt(35);
                    int BONPRODETLIN = Clientes.getInt(30);
                    int ArtBodCod =  Clientes.getInt(36);
                    String BonProTipo =  Clientes.getString(34);


                    Observacion=Observacion.replaceAll("[^\\w ]+", "");


                    //  = precioesp(pContext,Clientes.getString(0),Clientes.getString(2), Clientes.getDouble(8) );

                    double precioNu = 0;
                    precioNu = 0;
                    String consultaart = "select precioesp from PreciosEspeciales where peNitSec = '"+nitsec+"' and peArtSec = '"+Clientes.getString(2).trim()+"'";
                    Cursor cursorart = BaseDeDatos.getWritableDatabase().rawQuery(consultaart, null);
                    cursorart.moveToFirst();
                    if(cursorart.getCount() > 0){
                        cursorart.moveToFirst();
                        precioNu = cursorart.getDouble(0);
                    }else{
                        precioNu = Clientes.getDouble(8);
                    }



                    Double preciocal = precioNu;


                    if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") || vEmpresa.trim().equalsIgnoreCase("IBANEZPRU") || vEmpresa.trim().equalsIgnoreCase("SURTIMARCAS")) {
                        try{
                            descuentosAplicado(pContext,Clientes.getString(0),Clientes.getString(2),Clientes.getString(15),NumPedido,Clientes.getInt(1));
                        }catch (SQLException e){

                        }
                        if(NumPedido.equalsIgnoreCase(antNumpedido)){

                        }else{
                            antNumpedido = NumPedido;
                            enviado+=1;
                        }


                        Cursor pedidoen = BaseDeDatos.getWritableDatabase().rawQuery("select Obs,planpuente from Pedidoenc where prefijo = '"+Clientes.getString(15)+"' and nitsec = '" + Clientes.getString(0) + "' and clisec = " + Clientes.getInt(1) + " and  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  " /*and VisPref='" + Prefijo+"'"*/, null);
                        pedidoen.moveToFirst();
                        String PlanPuente = "N";
                        if (pedidoen.getCount() > 0) {
                            pedidoen.moveToFirst();
                            Observacion = pedidoen.getString(0);
                            PlanPuente = pedidoen.getString(1);
                        }


                      JsonEnvio = "{\"pedidoV7\":{\"pedido\":\"" + NumPedido + "\",\"nitsec\":\"" + Clientes.getString(0) + "\",\"clisec\":" + Clientes.getInt(1) + ",\"artsec\":\"" + Clientes.getString(2) + "\",\"vencod\":\"" + vUsuario + "\",\"cant\":\"" + CantUni + "\",\"cantcaj\":\"" + CantCajas + "\",\"cantinf\":\"" + cantinf + "\",\"cantcajinf\":\"" + cantcajinf + "\",\"ArtBodCod\":\"" + ArtBodCod + "\",\"BonProSec\":\"" + BonProSec + "\",\"BONPRODETLIN\":\"" + BONPRODETLIN + "\",\"BonProTipo\":\"" + BonProTipo + "\",\"pdyear\":" + Clientes.getInt(5) + ",\"pdmonth\":" + (time.month + 1) + ",\"pdday\":" + Clientes.getInt(7) + ",\"precio\":\"" + preciocal + "\",\"plazo\":" + Clientes.getInt(9) + ",\"contreg\":" + Clientes.getInt(21) + ",\"desc\":\"" + Clientes.getDouble(10) + "\",\"desc2\":\"" + Clientes.getDouble(11) + "\",\"desc3\":\"" + Clientes.getDouble(12) + "\",\"desc4\":\"" + Clientes.getDouble(13) + "\",\"confdesc\":\"" + Clientes.getDouble(16) + "\",\"confemp\":\"" + Clientes.getDouble(17) + "\",\"confprov\":\"" + Clientes.getDouble(18) + "\",\"confvend\":\"" + Clientes.getDouble(19) + "\",\"pox\":\"" + Clientes.getString(22) + "\",\"posy\":\"" + Clientes.getString(23) + "\",\"fechor\":\"\",\"obs\":\"" + Observacion + "\",\"papa\":\"\",\"prenotsinped\":\"N\",\"vishor\":\"" + Clientes.getInt(24) + "\",\"vismin\":\"" + Clientes.getInt(25) + "\",\"visseg\":\"" + Clientes.getInt(26) + "\",\"vishorfin\":\"" + Clientes.getInt(27) + "\",\"visminfin\":\"" + Clientes.getInt(28) + "\",\"vissegfin\":\"" + Clientes.getInt(29) + "\",\"vissegfin\":\"" + Clientes.getInt(29) + "\",\"totalped\":\"" + TotalPedido + "\",\"CotDetCheckMax\":\""+Clientes.getString(37)+"\",\"PlazoNom\":\""+Clientes.getString(38)+"\",\"pedlisprecod\":\"" + Clientes.getInt(31) + "\",\"PlanPuente\":\"" + PlanPuente + "\"}}";

                    }else{
                        JsonEnvio = "{\"Pedido\":{\"pedido\":\"" + NumPedido + "\",\"nitsec\":\"" + Clientes.getString(0) + "\",\"clisec\":" + Clientes.getInt(1) + ",\"artsec\":\"" + Clientes.getString(2) + "\",\"vencod\":\"" + vUsuario + "\",\"cant\":\"" + CantUni + "\",\"cantcaj\":\"" + CantCajas + "\",\"pdyear\":" + Clientes.getInt(5) + ",\"pdmonth\":" + (time.month + 1) + ",\"pdday\":" + Clientes.getInt(7) + ",\"precio\":\"" + Clientes.getDouble(8) + "\",\"plazo\":" + Clientes.getInt(9) + ",\"contreg\":" + Clientes.getInt(21) + ",\"desc\":\"" + Clientes.getDouble(10) + "\",\"desc2\":\"" + Clientes.getDouble(11) + "\",\"desc3\":\"" + Clientes.getDouble(12) + "\",\"desc4\":\"" + Clientes.getDouble(13) + "\",\"confdesc\":\"" + Clientes.getDouble(16) + "\",\"confemp\":\"" + Clientes.getDouble(17) + "\",\"confprov\":\"" + Clientes.getDouble(18) + "\",\"confvend\":\"" + Clientes.getDouble(19) + "\",\"pox\":\"" + Clientes.getString(22) + "\",\"posy\":\"" + Clientes.getString(23) + "\",\"fechor\":\"\",\"obs\":\"" + Observacion + "\",\"papa\":\"\",\"prenotsinped\":\"N\",\"vishor\":\"" + Clientes.getInt(24) + "\",\"vismin\":\"" + Clientes.getInt(25) + "\",\"visseg\":\"" + Clientes.getInt(26) + "\",\"vishorfin\":\"" + Clientes.getInt(27) + "\",\"visminfin\":\"" + Clientes.getInt(28) + "\",\"vissegfin\":\"" + Clientes.getInt(29) + "\",\"vissegfin\":\"" + Clientes.getInt(29) + "\"}}";
                    }

                    conbd.Variables();
                    String sql = conbd.UrlEnvio;
                    sExistencia = "0.0";
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    URL url = null;
                    HttpURLConnection conn;
                    Log.e("JSONSURTI: ",JsonEnvio);
                    Log.e("urlsql: ",sql);
                    try {
                        url = new URL(sql);

                        conn = (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(300000);
                        conn.setRequestProperty("Content-Type", "application/json");//; utf-8
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.setRequestMethod("POST");

                        StringBuilder result = new StringBuilder();
                        //   result.append(URLEncoder.encode("SDTArtSecWS2", "UTF-8"));
                        //   result.append("=");
                        result.append(JsonEnvio); //URLEncoder.encode(  , "UTF-8")


                        OutputStream os = conn.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(
                                new OutputStreamWriter(os)); //, "UTF-8"

                        writer.write(result.toString());
                        writer.flush();
                        writer.close();
                        os.close();

                        conn.connect();

                        int statusCode = conn.getResponseCode();
                        InputStream inputstream = null;
                        if (statusCode >= 200 && statusCode < 400) {
                            // Create an InputStream in order to extract the response object
                            inputstream = conn.getInputStream();
                        } else {
                            //actualizarestado(pContext,Clientes.getString(0),Clientes.getInt(1),vUsuario,NumPedido,"N");
                            enviado = enviado-1;
                            inputstream = conn.getErrorStream();
                        }

                        BufferedReader in = new BufferedReader(new InputStreamReader(inputstream));

                        String inputLine;

                        StringBuffer response = new StringBuffer();

                        String json = "";

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }

                        json = "[" + response.toString() + "]";


                        JSONArray jsonArr = null;

                        jsonArr = new JSONArray(json);
                        String mensaje = "";
                        String nstatus = "";
                        Log.e("Atatus",jsonArr.toString());
                        for (int i = 0; i < jsonArr.length(); i++) {
                            JSONObject jsonObject = jsonArr.getJSONObject(i);
                            sExistencia = jsonObject.optString("Mensaje");
                            nstatus = jsonObject.optString("status");
                            valorEnv = jsonObject.optString("CotValTot");
                            Log.e("sExistenciasExistencia",sExistencia);
                            actualizarestado(pContext, Clientes.getString(0), Clientes.getInt(1), vUsuario, NumPedido, nstatus);
                        }


                        // sal.setText(mensaje);
                    } catch (MalformedURLException e) {

                        //   String consulta = "insert into Bitacora(TxtLargo)" +
                        //         "values('"+e.getMessage()+ "')";
                        // BaseDeDatos.getWritableDatabase().execSQL(consulta);
                        sExistencia2 +=  e.getMessage();
                        Log.e("Errorenv1",e.toString());
                        e.printStackTrace();
                    } catch (IOException e) {
                        // String consulta = "insert into Bitacora(TxtLargo)" +
                        //        "values('"+e.getMessage()+ "')";
                        sExistencia2 +=  e.getMessage();
                        Log.e("Errorenv2",e.toString());
                        e.printStackTrace();
                    } catch (JSONException e) {
                        Log.e("Errorenv3",e.toString());
                        //String consulta = "insert into Bitacora(TxtLargo)" +
                        //       "values('"+e.getMessage()+ "')";
                        sExistencia2 +=  e.getMessage();
                        e.printStackTrace();
                        Log.e("Errorenv4",e.toString());
                    }
                    //   }
                    //}
                }

            } while (Clientes.moveToNext());
        }
        return valorEnv;

    }


    public String EnviarPedidosmenta(Context pContext,String prefijo,String nitsec,Integer clisec,Integer env,String pArtSec,int ptotalped) {

        ConBd conbd = new ConBd();
        // Connection connGen = conbd.CargarConexion();

        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();
        Integer vPedidoMinimo=vGlobalVariables.getParMovSec();

        Time time = new Time();
        time.setToNow();
        if(dayOfMonth > 0){
            time.set(time.second, time.minute, time.hour,dayOfMonth , month, year);
        }


        String sExistencia="";
        String sExistencia2="";
        int enviado = 0 ;
        String valorEnv  ="0.00";
        String NumPed = prefijo+nitsec + clisec +time.year + (time.month + 1) + time.monthDay;

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 5);


        Double pedidoMinimo=0.0;

        Cursor vCursorUsuarios = BaseDeDatos.getReadableDatabase().rawQuery("select ParMovPedMin from usuarios where VenCnt=1  ", null);
        vCursorUsuarios.moveToFirst();

        if (vCursorUsuarios.getCount() >0) {
            pedidoMinimo=vCursorUsuarios.getDouble(0);
        }


        String Consulta = "";
        GlobalVariables gGlobalVariables = GlobalVariables.getInstance();
        String vEmpresa=gGlobalVariables.getEmpresa();
        String mantisficc =  conbd.MantisFicc;
        Cursor Clientes = null;
        String resSql = "";
        if(env == 1) {
            resSql = " and CliNoRee ='N' ";
        }

        if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") || vEmpresa.trim().equalsIgnoreCase("IBANEZPRU")) {
            // case lisprecod when 1 then a.precio1 when 2 then a.precio2 when 3 then a.precio3  when 4  then a.precio4 when 5 then a.precio5 when 6 then a.precio6 when 7 then a.precio7 when 8 then a.precio8 when 9 then a.precio9 when 15 then a.precio5 when 16 then a.precio6  when 17 then a.precio7 when 18 then a.precio8 when 19 then a.precio9 else a.precio1 end
            Consulta = "select p.nitsec,p.clisec,p.artsec,p.vencod,cant,pdyear,pdmonth,pdday,precio  precio1,plazo,pordesc,pordesc2,pordesc3+pordesc6 pordesc3,pordesc4+pordesc5 pordesc4,ifnull(visobs,'') visobs,p.prefijo,(ifnull(pordescno,0)+ifnull(pordesc2no,0)+ifnull(pordesc3no,0)+ifnull(pordesc4no,0)+ifnull(pordesc5no,0)+ifnull(pordesc6no,0))-(ifnull(pordesc,0)+ifnull(pordesc2,0)+ifnull(pordesc3,0)+ifnull(pordesc4,0)+ifnull(pordesc5,0)+ifnull(pordesc6,0)) descno,confemp,confprov,confvend,cantcaj,ConNotCod,ifnull(VisLatitud,'') VisLatitud,ifnull(VisLongitud,'') VisLongitud,ifnull(VisHor,0) VisHor,ifnull(VisMin,0) VisMin,ifnull(VisSeg,0) VisSeg,ifnull(VisHor,0) VisHorFin,ifnull(VisMin,0) VisMinFin,ifnull(VisSeg,0) VisSegFin,0 MovParPremSecLin,PedLisPreCod,cantinf,cantcajinf, '' MovParPremtipo,0 MovParPremSec,ifnull(bodcod,1) bodcod,'N' MovParPremcheckmax, ifNull(ConPagnom, '') ConPagnom  from pedido p " +
                    " left join articulos a on rtrim(a.artsec)=rtrim(p.artsec) " +
                    " left join clientes n on ltrim(rtrim(n.nitsec))=ltrim(rtrim(p.nitsec)) and n.clisec=p.clisec " +
                    " left join visita v on ltrim(rtrim(v.nitsec))=ltrim(rtrim(p.nitsec)) and v.clisec=p.clisec and VisAno=pdyear and pdmonth=VisMes  and visdia=pdday and  Vispref = p.prefijo  " +
                    " where a.precio1>0 and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay+resSql ;
            if(nitsec.isEmpty()){

            }else{
                Consulta+=" and p.nitsec='"+nitsec+"'";
            }
            if(clisec > 0 ){
                Consulta+=" and p.clisec = "+clisec+" ";
            }
            if(pArtSec.isEmpty()) {
            }else{
                Consulta+=" and p.artsec='"+pArtSec+"'";
            }

            //" where  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay +MovParPremtipo
            Consulta+=" union all " +
                    " select MovParNitSec,MovParCliSec,m.MovParPremArtSec,'" + vUsuario + "' vencod,MovParPremCant cant,MovParPremAno,MovParPremMes,MovParPremDia,1 precio,0 plazo,0.0 pordesc,0.0 pordesc2,0.0 pordesc3,0.0 pordesc4,'' obs,ifnull(prefijo,'') prefi,0.0,0.0,0.0,0.0,ifnull(MovParPremCantCaj,0) MovParPremCantCaj,0,'','',0,0,0,0,0,0,ifnull(MovParPremSecLin,0) MovParPremSecLin,1 PedLisPreCod,0 infcant,0 infcaj,ifnull(MovParPremtipo,'') MovParPremtipo,ifnull(MovParPremSec,0) MovParPremSec,0 bodcod,ifnull(MovParPremcheckmax,'N') MovParPremcheckmax,'' ConPagnom  from MovParPrem M " +
                    " left join articulos a on a.artsec=m.MovParPremArtSec where MovParPremAno=" + time.year + " and MovParPremMes=" + (time.month + 1) + " and MovParPremDia=" + time.monthDay+" order by p.prefijo,p.nitsec,p.clisec ";

            try {
                Clientes = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null);
            } catch (Exception e) {
                int jj = 0;
            }

        }else {
            if (vEmpresa.trim().equalsIgnoreCase("GELVEZ") || vEmpresa.trim().equalsIgnoreCase("GELVEZGIR") || vEmpresa.trim().equalsIgnoreCase("GELVEZGIRREM") || vEmpresa.trim().equalsIgnoreCase("GELVEZCAL") || vEmpresa.trim().equalsIgnoreCase("GELVEZEJE")) {
                //case lisprecod when 1 then a.precio1 when 2 then a.precio2 when 3 then a.precio3 when 4 then a.precio4 when 5 then a.precio5 when 6 then a.precio6 when 7 then a.precio7 when 8 then a.precio8 when 9 then a.precio9 when 10 then a.precio10 when 11 then a.precio11 when 12 then a.precio12  when 13 then a.precio13  when 14 then a.precio14  when 15 then a.precio15 when 16 then a.precio16  when 17 then a.precio17 when 18 then a.precio18 when 19 then a.precio19 when 20 then a.precio20 when 21 then a.precio21 when 22 then a.precio22 when 23 then a.precio23 when 24 then a.precio24 when 25 then a.precio25 when 26 then a.precio26 when 27 then a.precio27 when 28 then a.precio28 when 29 then a.precio29 when 30 then a.precio30 else a.precio1 end
                Consulta = "select p.nitsec,p.clisec,p.artsec,p.vencod,cant,pdyear,pdmonth,pdday,precio  precio1,plazo,pordesc,pordesc2,pordesc3+pordesc6 pordesc3,pordesc4+pordesc5 pordesc4,ifnull(visobs,'') visobs,p.prefijo,(ifnull(pordescno,0)+ifnull(pordesc2no,0)+ifnull(pordesc3no,0)+ifnull(pordesc4no,0)+ifnull(pordesc5no,0)+ifnull(pordesc6no,0))-(ifnull(pordesc,0)+ifnull(pordesc2,0)+ifnull(pordesc3,0)+ifnull(pordesc4,0)+ifnull(pordesc5,0)+ifnull(pordesc6,0)) descno,confemp,confprov,confvend,cantcaj,ConNotCod,ifnull(VisLatitud,'') VisLatitud,ifnull(VisLongitud,'') VisLongitud,ifnull(VisHor,0) VisHor,ifnull(VisMin,0) VisMin,ifnull(VisSeg,0) VisSeg,ifnull(VisHor,0) VisHorFin,ifnull(VisMin,0) VisMinFin,ifnull(VisSeg,0) VisSegFin,0 MovParPremSecLin,1 PedLisPreCod, 0 cantinf,0 cantcajinf,0 MovParPremtipo,0 MovParPremSec,0 bodcod,'N' MovParPremcheckmax,'' ConPagnom    from pedido p " +
                        " left join articulos a on rtrim(a.artsec)=rtrim(p.artsec) " +
                        " left join clientes n on ltrim(rtrim(n.nitsec))=ltrim(rtrim(p.nitsec)) and n.clisec=p.clisec " +
                        " left join visita v on ltrim(rtrim(v.nitsec))=ltrim(rtrim(p.nitsec)) and v.clisec=p.clisec  and VisAno=pdyear and pdmonth=VisMes  and visdia=pdday" +
                        //" where a.precio1>0 and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay +
                        " where  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay +
                        " union all " +
                        "   select MovParNitSec,MovParCliSec,m.MovParPremArtSec,'" + vUsuario + "' vencod,MovParPremCant cant,MovParPremAno,MovParPremMes,MovParPremDia,1 precio,0 plazo,0.0 pordesc,0.0 pordesc2,0.0 pordesc3,0.0 pordesc4,'' obs,prefijo prefi,0.0,0.0,0.0,0.0,0,0,'','',0,0,0,0,0,0,0 MovParPremSecLin,1 PedLisPreCod,0 cantinf, 0 cantcajinf,0 bodcod,MovParPremcheckmax ,'' ConPagnom  from MovParPrem M " +
                        " left join articulos a on a.artsec=m.MovParPremArtSec where  MovParPremAno=" + time.year + " and MovParPremMes=" + (time.month + 1) + " and MovParPremDia=" + time.monthDay;
                //      +" union all " +
                //      "  select NitSec,CliSec,'VISITA','" + vUsuario + "' vencod,MovCauPed cant,VisAno,VisMes,VisDia,1 precio,0 plazo,0.0 pordesc,0.0 pordesc2,0.0 pordesc3,0.0 pordesc4,'' obs,'VI' prefi,0.0,0.0,0.0,0.0,0,0,'','',VisHor,VisMin,VisSeg,VisHorFin,VisMinFin,VisSegFin  from Visita M " +
                //        " where  VisAno=" + time.year + " and VisMes=" + (time.month + 1) + " and VisDia=" + time.monthDay;
            }else {

                Consulta = "select p.nitsec,p.clisec,p.artsec,p.vencod,cant,pdyear,pdmonth,pdday,case PedLisPreCod when 1 then a.precio1 when 2 then a.precio2 when 3 then a.precio3 when 4 then a.precio4  when 5 then a.precio5 when 7 then a.precio7 when 8 then a.precio8 when 15 then a.precio15 when 16 then a.precio6  when 17 then a.precio7 when 18 then a.precio8 when 19 then a.precio9 else precio end  precio1,plazo,pordesc,pordesc2,pordesc3+pordesc6 pordesc3,pordesc4+pordesc5 pordesc4,ifnull(visobs,'') visobs,p.prefijo,(ifnull(pordescno,0)+ifnull(pordesc2no,0)+ifnull(pordesc3no,0)+ifnull(pordesc4no,0)+ifnull(pordesc5no,0)+ifnull(pordesc6no,0))-(ifnull(pordesc,0)+ifnull(pordesc2,0)+ifnull(pordesc3,0)+ifnull(pordesc4,0)+ifnull(pordesc5,0)+ifnull(pordesc6,0)) descno,confemp,confprov,confvend,cantcaj,ConNotCod,ifnull(VisLatitud,'') VisLatitud,ifnull(VisLongitud,'') VisLongitud,ifnull(VisHor,0) VisHor,ifnull(VisMin,0) VisMin,ifnull(VisSeg,0) VisSeg,ifnull(VisHor,0) VisHorFin,ifnull(VisMin,0) VisMinFin,ifnull(VisSeg,0) VisSegFin,0 MovParPremSecLin, PedLisPreCod,0 cantinf,0 cantcajinf,0 MovParPremtipo,0 MovParPremSec,bodcod,'N' MovParPremcheckmax ,'' ConPagnom  from pedido p " +
                        " left join articulos a on rtrim(a.artsec)=rtrim(p.artsec) " +
                        " left join clientes n on ltrim(rtrim(n.nitsec))=ltrim(rtrim(p.nitsec)) and n.clisec=p.clisec " +
                        " left join visita v on ltrim(rtrim(v.nitsec))=ltrim(rtrim(p.nitsec)) and v.clisec=p.clisec  and VisAno=pdyear and pdmonth=VisMes  and visdia=pdday" +
                        //" where a.precio1>0 and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay +
                        " where  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay ;

                if(nitsec.isEmpty()){

                }else{
                    Consulta+=" and p.nitsec='"+nitsec+"'";
                }

                Consulta+=" union all " +
                        "   select MovParNitSec,MovParCliSec,m.MovParPremArtSec,'" + vUsuario + "' vencod,MovParPremCant cant,MovParPremAno,MovParPremMes,MovParPremDia,1 precio,0 plazo,0.0 pordesc,0.0 pordesc2,0.0 pordesc3,0.0 pordesc4,'BON' obs,prefijo prefi,0.0,0.0,0.0,0.0,0,0,'','',0,0,0,0,0,0 ,0 MovParPremSecLin,1 PedLisPreCod,0 cantinf,0 cantcajinf,0 MovParPremtipo,0 MovParPremSec ,0 bodcod,MovParPremcheckmax,'' ConPagnom  from MovParPrem M " +
                        "left join articulos a on a.artsec=m.MovParPremArtSec where MovParPremCant > 0 and MovParPremAno=" + time.year + " and MovParPremMes=" + (time.month + 1) + " and MovParPremDia=" + time.monthDay;
            }
            try {
                Clientes = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null);
            } catch (Exception e) {
                int jj = 0;
            }
        }


        String JsonEnvio = "";
        String JsonDetalle = "";
        String antNumpedido ="";

        String numpedido[] =new String[ptotalped];

        if (Clientes.getCount() > 0) {
            Clientes.moveToFirst();
            do {

                Double TotalPedido= TotalesPedido(pContext,Clientes.getString(15),Clientes.getString(0),Clientes.getInt(1),"","","").Total;
                Double TotalPedidoCli = TotalesPedidocliente(pContext,Clientes.getString(15),Clientes.getString(0),Clientes.getInt(1),"","","").Total;

                //if ((pedidoMinimo!=0 && TotalPedido>pedidoMinimo ) || pedidoMinimo==0) {
                int sincupo=0;
                String dNitide ="";
                if ((vEmpresa.trim().equalsIgnoreCase("MENTAHAIR") )) {
                    GestorCartera gestorcartera=new GestorCartera();
                    gestorcartera.TotalesCatera(pContext, nitsec, clisec);
                    Integer CarteraGeneral =gestorcartera.CarteraGeneral;
                    String tmpNitSec=Clientes.getString(0);




                    Integer tmpCliSec=Clientes.getInt(1);
                    Cursor cursorclientes = BaseDeDatos.getReadableDatabase().rawQuery("select CliCup,CliConPag,NitIde from clientes where nitsec='" + tmpNitSec + "' and clisec=" + tmpCliSec, null);
                    Integer vuelta = 0;
                    Integer cupo=0;
                    Integer CliConPag = 0;
                    if (cursorclientes.getCount() > 0) {
                        cursorclientes.moveToFirst();
                        do {
                            cupo= cursorclientes.getInt(0);
                            CliConPag = cursorclientes.getInt(1);
                            dNitide = cursorclientes.getString(2);
                        } while (cursorclientes.moveToNext());
                    }
                    if (cupo-CarteraGeneral-TotalPedido.intValue()<0 && (CliConPag != 0 && cupo != 0 ) ){
                        sincupo=1;
                    }
                }

              if (vEmpresa.trim().equalsIgnoreCase("MENTAHAIRCOT") ){
                  sincupo=0 ;
              }

                if (sincupo==0){
                    Double CantUni = 0.00;
                    Double cantinf = 0.00;
                    Integer CantCajas = 0;
                    Integer cantcajinf = 0;
                    if ((pedidoMinimo!=0 && TotalPedidoCli>pedidoMinimo ) || pedidoMinimo==0.0) {
                        CantUni = Clientes.getDouble(4);
                        CantCajas = Clientes.getInt(20);
                        cantinf = Clientes.getDouble(32);
                        cantcajinf = Clientes.getInt(33);
                    }

                    JsonEnvio = "";
                    //Clientes.getString(3)
                    String NumPedido = Clientes.getString(15)+vUsuario.trim()+ Clientes.getString(0) + Clientes.getInt(1) + time.year +"-"+ (time.month + 1) +"-"+ time.monthDay;

                    SDTResumenPedidos SDTResumenPedidos = new SDTResumenPedidos();
                    SDTResumenPedidos = TotalesPedido(pContext, Clientes.getString(15), Clientes.getString(0), Clientes.getInt(1), "", "", "");

                    String Observacion = Clientes.getString(14);
                    int BonProSec =Clientes.getInt(35);
                    int BONPRODETLIN = Clientes.getInt(30);
                    int ArtBodCod =  Clientes.getInt(36);
                    String BonProTipo =  Clientes.getString(34);


                    Observacion=Observacion.replaceAll("[^\\w ]+", "");


                    //  = precioesp(pContext,Clientes.getString(0),Clientes.getString(2), Clientes.getDouble(8) );

                    double precioNu = 0;
                    precioNu = 0;
                    String consultaart = "select precioesp from PreciosEspeciales where peNitSec = '"+nitsec+"' and peArtSec = '"+Clientes.getString(2).trim()+"'";
                    Cursor cursorart = BaseDeDatos.getWritableDatabase().rawQuery(consultaart, null);
                    cursorart.moveToFirst();
                    if(cursorart.getCount() > 0){
                        cursorart.moveToFirst();
                        precioNu = cursorart.getDouble(0);
                    }else{
                        precioNu = Clientes.getDouble(8);
                    }



                    Double preciocal = precioNu;


                    if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") || vEmpresa.trim().equalsIgnoreCase("IBANEZPRU")) {
                        try{
                            descuentosAplicado(pContext,Clientes.getString(0),Clientes.getString(2),Clientes.getString(15),NumPedido,Clientes.getInt(1));
                        }catch (SQLException e){

                        }
                        Log.e("numpedido",NumPedido);
                        Log.e("antNumpedido",antNumpedido);
                        Log.e("enviado",String.valueOf(enviado));
                        if(NumPedido.equalsIgnoreCase(antNumpedido)){

                        }else{
                            antNumpedido = NumPedido;
                            enviado+=1;
                        }
                        JsonEnvio = "{\"pedidoV6\":{\"pedido\":\"" + NumPedido + "\",\"nitsec\":\"" + Clientes.getString(0) + "\",\"clisec\":" + Clientes.getInt(1) + ",\"artsec\":\"" + Clientes.getString(2) + "\",\"vencod\":\"" + vUsuario + "\",\"cant\":\"" + CantUni + "\",\"cantcaj\":\"" + CantCajas + "\",\"cantinf\":\"" + cantinf + "\",\"cantcajinf\":\"" + cantcajinf + "\",\"ArtBodCod\":\"" + ArtBodCod + "\",\"BonProSec\":\"" + BonProSec + "\",\"BONPRODETLIN\":\"" + BONPRODETLIN + "\",\"BonProTipo\":\"" + BonProTipo + "\",\"pdyear\":" + Clientes.getInt(5) + ",\"pdmonth\":" + (time.month + 1) + ",\"pdday\":" + Clientes.getInt(7) + ",\"precio\":\"" + preciocal + "\",\"plazo\":" + Clientes.getInt(9) + ",\"contreg\":" + Clientes.getInt(21) + ",\"desc\":\"" + Clientes.getDouble(10) + "\",\"desc2\":\"" + Clientes.getDouble(11) + "\",\"desc3\":\"" + Clientes.getDouble(12) + "\",\"desc4\":\"" + Clientes.getDouble(13) + "\",\"confdesc\":\"" + Clientes.getDouble(16) + "\",\"confemp\":\"" + Clientes.getDouble(17) + "\",\"confprov\":\"" + Clientes.getDouble(18) + "\",\"confvend\":\"" + Clientes.getDouble(19) + "\",\"pox\":\"" + Clientes.getString(22) + "\",\"posy\":\"" + Clientes.getString(23) + "\",\"fechor\":\"\",\"obs\":\"" + Observacion + "\",\"papa\":\"\",\"prenotsinped\":\"N\",\"vishor\":\"" + Clientes.getInt(24) + "\",\"vismin\":\"" + Clientes.getInt(25) + "\",\"visseg\":\"" + Clientes.getInt(26) + "\",\"vishorfin\":\"" + Clientes.getInt(27) + "\",\"visminfin\":\"" + Clientes.getInt(28) + "\",\"vissegfin\":\"" + Clientes.getInt(29) + "\",\"vissegfin\":\"" + Clientes.getInt(29) + "\",\"totalped\":\"" + TotalPedido + "\",\"CotDetCheckMax\":\""+Clientes.getString(37)+"\",\"PlazoNom\":\""+Clientes.getString(38)+"\",\"pedlisprecod\":\"" + Clientes.getInt(31) + "\"}}";
                        // JsonEnvio = "{\"pedidoV6\":{\"pedido\":\"" + NumPedido + "\",\"nitsec\":\"" + Clientes.getString(0) + "\",\"clisec\":" + Clientes.getInt(1) + ",\"artsec\":\"" + Clientes.getString(2) + "\",\"vencod\":\"" + vUsuario + "\",\"cant\":\"" + CantUni + "\",\"cantcaj\":\"" + CantCajas + "\",\"cantinf\":\"" + cantinf + "\",\"cantcajinf\":\"" + cantcajinf + "\",\"ArtBodCod\":\"" + ArtBodCod + "\",\"BonProSec\":\"" + BonProSec + "\",\"BONPRODETLIN\":\"" + BONPRODETLIN + "\",\"BonProTipo\":\"" + BonProTipo + "\",\"pdyear\":" + Clientes.getInt(5) + ",\"pdmonth\":" + (time.month + 1) + ",\"pdday\":" + Clientes.getInt(7) + ",\"precio\":\"" + preciocal + "\",\"plazo\":" + Clientes.getInt(9) + ",\"contreg\":" + Clientes.getInt(21) + ",\"desc\":\"" + Clientes.getDouble(10) + "\",\"desc2\":\"" + Clientes.getDouble(11) + "\",\"desc3\":\"" + Clientes.getDouble(12) + "\",\"desc4\":\"" + Clientes.getDouble(13) + "\",\"confdesc\":\"" + Clientes.getDouble(16) + "\",\"confemp\":\"" + Clientes.getDouble(17) + "\",\"confprov\":\"" + Clientes.getDouble(18) + "\",\"confvend\":\"" + Clientes.getDouble(19) + "\",\"pox\":\"" + Clientes.getString(22) + "\",\"posy\":\"" + Clientes.getString(23) + "\",\"fechor\":\"\",\"obs\":\"" + Observacion + "\",\"papa\":\"\",\"prenotsinped\":\"N\",\"vishor\":\"" + Clientes.getInt(24) + "\",\"vismin\":\"" + Clientes.getInt(25) + "\",\"visseg\":\"" + Clientes.getInt(26) + "\",\"vishorfin\":\"" + Clientes.getInt(27) + "\",\"visminfin\":\"" + Clientes.getInt(28) + "\",\"vissegfin\":\"" + Clientes.getInt(29) + "\",\"vissegfin\":\"" + Clientes.getInt(29) + "\",\"totalped\":\"" + TotalPedido + "\",\"CotDetCheckMax\":\""+Clientes.getString(37)+"\",\"pedlisprecod\":\"" + Clientes.getInt(31) + "\"}}";



                    }else{
                        JsonEnvio = "{\"Pedido\":{\"pedido\":\"" + NumPedido + "\",\"nitsec\":\"" + Clientes.getString(0) + "\",\"clisec\":" + Clientes.getInt(1) + ",\"artsec\":\"" + Clientes.getString(2) + "\",\"vencod\":\"" + vUsuario + "\",\"cant\":\"" + CantUni + "\",\"cantcaj\":\"" + CantCajas + "\",\"pdyear\":" + Clientes.getInt(5) + ",\"pdmonth\":" + (time.month + 1) + ",\"pdday\":" + Clientes.getInt(7) + ",\"precio\":\"" + Clientes.getDouble(8) + "\",\"plazo\":" + Clientes.getInt(9) + ",\"contreg\":" + Clientes.getInt(21) + ",\"desc\":\"" + Clientes.getDouble(10) + "\",\"desc2\":\"" + Clientes.getDouble(11) + "\",\"desc3\":\"" + Clientes.getDouble(12) + "\",\"desc4\":\"" + Clientes.getDouble(13) + "\",\"confdesc\":\"" + Clientes.getDouble(16) + "\",\"confemp\":\"" + Clientes.getDouble(17) + "\",\"confprov\":\"" + Clientes.getDouble(18) + "\",\"confvend\":\"" + Clientes.getDouble(19) + "\",\"pox\":\"" + Clientes.getString(22) + "\",\"posy\":\"" + Clientes.getString(23) + "\",\"fechor\":\"\",\"obs\":\"" + Observacion + "\",\"papa\":\"\",\"prenotsinped\":\"N\",\"vishor\":\"" + Clientes.getInt(24) + "\",\"vismin\":\"" + Clientes.getInt(25) + "\",\"visseg\":\"" + Clientes.getInt(26) + "\",\"vishorfin\":\"" + Clientes.getInt(27) + "\",\"visminfin\":\"" + Clientes.getInt(28) + "\",\"vissegfin\":\"" + Clientes.getInt(29) + "\",\"vissegfin\":\"" + Clientes.getInt(29) + "\"}}";
                    }






                    conbd.Variables();
                    String sql = conbd.UrlEnvio;  // "http://181.49.42.34:8080/pruebas9/rest/pSetNotaIbanez2";
                    sExistencia = "0.0";
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    URL url = null;
                    HttpURLConnection conn;
                    Log.e("enviossssssssssssssss",JsonEnvio);


                    try {
                        url = new URL(sql);

                        conn = (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(300000);
                        conn.setRequestProperty("Content-Type", "application/json");//; utf-8
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.setRequestMethod("POST");

                        StringBuilder result = new StringBuilder();
                        //   result.append(URLEncoder.encode("SDTArtSecWS2", "UTF-8"));
                        //   result.append("=");
                        result.append(JsonEnvio); //URLEncoder.encode(  , "UTF-8")


                        OutputStream os = conn.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(
                                new OutputStreamWriter(os)); //, "UTF-8"

                        writer.write(result.toString());
                        writer.flush();
                        writer.close();
                        os.close();

                        conn.connect();

                        int statusCode = conn.getResponseCode();
                        InputStream inputstream = null;
                        if (statusCode >= 200 && statusCode < 400) {
                            // Create an InputStream in order to extract the response object
                            inputstream = conn.getInputStream();
                        } else {
                            //actualizarestado(pContext,Clientes.getString(0),Clientes.getInt(1),vUsuario,NumPedido,"N");
                            enviado = enviado-1;
                            inputstream = conn.getErrorStream();
                        }

                        BufferedReader in = new BufferedReader(new InputStreamReader(inputstream));

                        String inputLine;

                        StringBuffer response = new StringBuffer();

                        String json = "";

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }

                        json = "[" + response.toString() + "]";


                        JSONArray jsonArr = null;

                        jsonArr = new JSONArray(json);
                        String mensaje = "";
                        String nstatus = "";
                        Log.e("Atatus",jsonArr.toString());
                        Log.e("sql",sql);


                        for (int i = 0; i < jsonArr.length(); i++) {
                            JSONObject jsonObject = jsonArr.getJSONObject(i);
                            mensaje = jsonObject.optString("Mensaje")  ;

                            if(mensaje.contains("Remisión")){
                                if(sExistencia2.contains(NumPedido+": Remisión")){

                                }else{
                                    sExistencia2  += "\n"+NumPedido+": "+mensaje;
                                }

                            }else{
                                sExistencia2  += "\n"+NumPedido+": Enviado";
                            }
                            //sExistencia2  =" Enviado";





                        }


                        // sal.setText(mensaje);
                    } catch (MalformedURLException e) {

                        //   String consulta = "insert into Bitacora(TxtLargo)" +
                        //         "values('"+e.getMessage()+ "')";
                        // BaseDeDatos.getWritableDatabase().execSQL(consulta);
                        sExistencia2 +=  e.getMessage();
                        Log.e("Errorenv",e.toString());
                        e.printStackTrace();
                    } catch (IOException e) {
                        // String consulta = "insert into Bitacora(TxtLargo)" +
                        //        "values('"+e.getMessage()+ "')";
                        sExistencia2 +=  e.getMessage();
                        Log.e("Errorenv",e.toString());
                        e.printStackTrace();
                    } catch (JSONException e) {
                        Log.e("Errorenv",e.toString());
                        //String consulta = "insert into Bitacora(TxtLargo)" +
                        //       "values('"+e.getMessage()+ "')";
                        sExistencia2 +=  e.getMessage();
                        e.printStackTrace();
                        Log.e("Errorenv",e.toString());
                    }

                    //   }
                    //}
                }else{

                }

            } while (Clientes.moveToNext());
        }



        return sExistencia2;

    }




    public String EnviarPedidospendientes(Context pContext,String prefijo,String nitsec,Integer clisec,Integer env,String pArtSec,int ptotalped) {

        ConBd conbd = new ConBd();
       // Connection connGen = conbd.CargarConexion();
        String nerror ="Enviando: ";
        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();
        Integer vPedidoMinimo=vGlobalVariables.getParMovSec();

        Time time = new Time();
        time.setToNow();

        String sExistencia="";
        String sExistencia2="";
        int enviado = 0 ;
        String NumPed = prefijo+nitsec + clisec +time.year + (time.month + 1) + time.monthDay;

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 5);


        Double pedidoMinimo=0.0;

        Cursor vCursorUsuarios = BaseDeDatos.getReadableDatabase().rawQuery("select ParMovPedMin from usuarios where VenCnt=1  ", null);
        vCursorUsuarios.moveToFirst();

        if (vCursorUsuarios.getCount() >0) {
            pedidoMinimo=vCursorUsuarios.getDouble(0);
        }


        String Consulta = "";
        GlobalVariables gGlobalVariables = GlobalVariables.getInstance();
        String vEmpresa=gGlobalVariables.getEmpresa();
        String mantisficc =  conbd.MantisFicc;
        Cursor Clientes = null;
        String resSql = "";
        if(env == 1) {
            resSql = " and CliNoRee ='N' ";
        }

       /// if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") || vEmpresa.trim().equalsIgnoreCase("IBANEZPRU")) {
            // case lisprecod when 1 then a.precio1 when 2 then a.precio2 when 3 then a.precio3  when 4  then a.precio4 when 5 then a.precio5 when 6 then a.precio6 when 7 then a.precio7 when 8 then a.precio8 when 9 then a.precio9 when 15 then a.precio5 when 16 then a.precio6  when 17 then a.precio7 when 18 then a.precio8 when 19 then a.precio9 else a.precio1 end





            Consulta = "select ifnull(p.nitsec,'') nitsec,ifnull(p.clisec,0) clisec,ifnull(p.artsec,'') artsec,ifnull(p.vencod,'') vencod,ifnull(cant,0) cant,ifnull(pdyear,0) pdyear,ifnull(pdmonth,0) pdmonth,ifnull(pdday,0)  pdday,ifnull(precio,0)  precio1,ifnull(plazo,0) plazo,ifnull(pordesc,0) pordesc,ifnull(pordesc2,0) pordesc2,( ifnull(pordesc3,0)+ifnull(pordesc6,0) ) pordesc3,(ifnull(pordesc4 ,0)+ifnull(pordesc5,0)) pordesc4,ifnull(visobs,'') visobs,ifnull(p.prefijo,'') prefijo,(ifnull(pordescno,0)+ifnull(pordesc2no,0)+ifnull(pordesc3no,0)+ifnull(pordesc4no,0)+ifnull(pordesc5no,0)+ifnull(pordesc6no,0))-(ifnull(pordesc,0)+ifnull(pordesc2,0)+ifnull(pordesc3,0)+ifnull(pordesc4,0)+ifnull(pordesc5,0)+ifnull(pordesc6,0)) descno,ifnull(confemp,0) confemp,ifnull(confprov,0) confprov,ifnull(confvend,0) confvend,ifnull(cantcaj,0) cantcaj,ifnull(ConNotCod,0) ConNotCod,ifnull(VisLatitud,'') VisLatitud,ifnull(VisLongitud,'') VisLongitud,ifnull(VisHor,0) VisHor,ifnull(VisMin,0) VisMin,ifnull(VisSeg,0) VisSeg,ifnull(VisHor,0) VisHorFin,ifnull(VisMin,0) VisMinFin,ifnull(VisSeg,0) VisSegFin,0 MovParPremSecLin,ifnull(PedLisPreCod,0) PedLisPreCod,ifnull(cantinf,0) cantinf,ifnull(cantcajinf,0) cantcajinf, '' MovParPremtipo,0 MovParPremSec,ifnull(bodcod,1) bodcod,'N' MovParPremcheckmax, ifNull(ConPagnom, '') ConPagnom,ifNull(pedartemb,0) pedartemb,(case  n.cliiva when 'S' then a.ParConIva else 0 end) totaliva  from pedido p " +
                    " left join articulos a on rtrim(a.artsec)=rtrim(p.artsec) " +
                    " left join clientes n on ltrim(rtrim(n.nitsec))=ltrim(rtrim(p.nitsec)) and n.clisec=p.clisec " +
                    " left join visita v on ltrim(rtrim(v.nitsec))=ltrim(rtrim(p.nitsec)) and v.clisec=p.clisec and VisAno=pdyear and pdmonth=VisMes  and visdia=pdday and  Vispref = p.prefijo  " +
                    " where a.precio1>0 and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay+resSql ;
            if(nitsec.isEmpty()){

            }else{
                Consulta+=" and p.nitsec='"+nitsec+"'";
            }
            if(clisec > 0 ){
                Consulta+=" and p.clisec = "+clisec+" ";
            }
            if(pArtSec.isEmpty()) {
            }else{
                Consulta+=" and p.artsec='"+pArtSec+"'";
            }
            if(prefijo.isEmpty()) {
            }else{
                Consulta+=" and p.prefijo='"+prefijo+"'";
            }


            //" where  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay +MovParPremtipo
            Consulta+=" union all " +
                    " select MovParNitSec,MovParCliSec,m.MovParPremArtSec,'" + vUsuario + "' vencod,MovParPremCant cant,MovParPremAno,MovParPremMes,MovParPremDia,1 precio,0 plazo,0.0 pordesc,0.0 pordesc2,0.0 pordesc3,0.0 pordesc4,'' obs,ifnull(prefijo,'') prefi,0.0,0.0,0.0,0.0,ifnull(MovParPremCantCaj,0) MovParPremCantCaj,0,'','',0,0,0,0,0,0,ifnull(MovParPremSecLin,0) MovParPremSecLin,1 PedLisPreCod,0 infcant,0 infcaj,ifnull(MovParPremtipo,'') MovParPremtipo,ifnull(MovParPremSec,0) MovParPremSec,0 bodcod,ifnull(MovParPremcheckmax,'N') MovParPremcheckmax,'' ConPagnom,0 pedartemb ,0 iva from MovParPrem M " +
                    " left join articulos a on a.artsec=m.MovParPremArtSec where MovParPremAno=" + time.year + " and MovParPremMes=" + (time.month + 1) + " and MovParPremDia=" + time.monthDay+" order by prefijo,nitsec,clisec ";
         //primer try
            try {
                Clientes = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null);
            } catch (Exception e) {
                int jj = 0;
                Log.e("Error consiulta ",e.toString());
                nerror += "primer try "+e.toString();
            }

       // }


        String JsonEnvio = "";
        String JsonDetalle = "";
        String antNumpedido ="";

        String numpedido[] =new String[ptotalped];
      int bandel = 0 ;
        if (Clientes.getCount() > 0) {
            Log.e("Entro Clientes2",String.valueOf(Clientes.getCount()));
            Clientes.moveToFirst();
            do {

                Double TotalPedido= TotalesPedido(pContext,Clientes.getString(15),Clientes.getString(0),Clientes.getInt(1),"","","").Total;
                Double TotalPedidoCli = TotalesPedidocliente(pContext,Clientes.getString(15),Clientes.getString(0),Clientes.getInt(1),"","","").Total;




                //if ((pedidoMinimo!=0 && TotalPedido>pedidoMinimo ) || pedidoMinimo==0) {
                int sincupo=0;

                if (sincupo==0){
                    Double CantUni = 0.00;
                    Double cantinf = 0.00;
                    Integer CantCajas = 0;
                    Integer cantcajinf = 0;
                    Integer PedPenArtEmb = 0;
                    Double iva = 0.0;
                    if ((pedidoMinimo!=0 && TotalPedidoCli>pedidoMinimo ) || pedidoMinimo==0.0) {
                        CantUni = Clientes.getDouble(4);
                        CantCajas = Clientes.getInt(20);
                        cantinf = Clientes.getDouble(32);
                        cantcajinf = Clientes.getInt(33);
                        PedPenArtEmb = Clientes.getInt(39);
                        iva = Clientes.getDouble(40);
                    }

                    JsonEnvio = "";
                    //Clientes.getString(3)

                    String NumPedido = Clientes.getString(15)+vUsuario.trim()+ Clientes.getString(0) + Clientes.getInt(1) + time.year +"-"+ (time.month + 1) +"-"+ time.monthDay;
                    Connection conn = null;
                    Statement comm =null;

                    if (TotalPedido==0){
                        JsonEnvio = "delete from PedidoPendientes where PedPenPedido = '"+NumPedido+"'  ";
                        try {
                            conn = conbd.CargarConexion(pContext);
                            Statement delcomm = conn.createStatement();
                            delcomm.execute(JsonEnvio);

                        }catch (Exception e){
                            Log.e("Errordel sql",e.toString());
                        }
                    }
                    //

                  // if(bandel==0){
                     //  bandel = 1;
              /*      try {
                        Statement delcomm = conn.createStatement();
                         delcomm.execute(JsonEnvio);

                    }catch (SQLException e){
                        Log.e("Errordel sql",e.toString());
                    }
                  // }
*/






                    SDTResumenPedidos SDTResumenPedidos = new SDTResumenPedidos();
                    SDTResumenPedidos = TotalesPedido(pContext, Clientes.getString(15), Clientes.getString(0), Clientes.getInt(1), "", "", "");

                    String Observacion = Clientes.getString(14);
                    int BonProSec =Clientes.getInt(35);
                    int BONPRODETLIN = Clientes.getInt(30);
                    int ArtBodCod =  Clientes.getInt(36);
                    String BonProTipo =  Clientes.getString(34);


                    Observacion=Observacion.replaceAll("[^\\w ]+", "");


                    //  = precioesp(pContext,Clientes.getString(0),Clientes.getString(2), Clientes.getDouble(8) );

                    double precioNu = 0;
                    precioNu = 0;
                    String consultaart = "select precioesp from PreciosEspeciales where peNitSec = '"+nitsec+"' and peArtSec = '"+Clientes.getString(2).trim()+"'";
                    Cursor cursorart = BaseDeDatos.getWritableDatabase().rawQuery(consultaart, null);
                    cursorart.moveToFirst();
                    if(cursorart.getCount() > 0){
                        cursorart.moveToFirst();
                        precioNu = cursorart.getDouble(0);
                    }else{
                        precioNu = Clientes.getDouble(8);
                    }



                    Double preciocal = precioNu;


                    if (vEmpresa.trim().equalsIgnoreCase("IBANEZ") || vEmpresa.trim().equalsIgnoreCase("IBANEZPRU")|| vEmpresa.trim().equalsIgnoreCase("SURTIMARCAS")) {

                       //segundo try
                        try{
                            descuentosAplicado(pContext,Clientes.getString(0),Clientes.getString(2),Clientes.getString(15),NumPedido,Clientes.getInt(1));
                        }catch (SQLException e){
                            nerror += "segundo try "+e.toString();
                        }

                        if(NumPedido.equalsIgnoreCase(antNumpedido)){

                        }else{
                            antNumpedido = NumPedido;
                            enviado+=1;
                        }


                        Cursor pedidoen = BaseDeDatos.getWritableDatabase().rawQuery("select Obs,planpuente from Pedidoenc where prefijo = '"+Clientes.getString(15)+"' and nitsec = '" + Clientes.getString(0) + "' and clisec = " + Clientes.getInt(1) + " and  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  " /*and VisPref='" + Prefijo+"'"*/, null);
                        pedidoen.moveToFirst();
                        String PlanPuente = "N";
                        if (pedidoen.getCount() > 0) {
                            pedidoen.moveToFirst();
                            Observacion = pedidoen.getString(0);
                            PlanPuente = pedidoen.getString(1);
                        }



                        enviado = TotalPedido.intValue();
                        ResultSet rsClientes  = null;
                        conn = conbd.CargarConexion(pContext);
                        try{
                            comm = conn.createStatement();
                        }catch (Exception e){
                            nerror+= "conexion try "+e.toString();
                        }
                        int exite = 0;
                        int eliminar = 0;

                        String PedidoJson ="";

                     /*   SDTProductos SDTProductos = new SDTProductos() ;
                        SDTProductos.ArtSec = Clientes.getString(2);
                        SDTProductos.pContext = pContext;
                        SDTProductos.Prefijo = Clientes.getString(15);
                        SDTProductos.NitSec =Clientes.getString(0);
                        SDTProductos.CliSec = Clientes.getInt(1);
                        SDTProductos.Unidades = CantUni;
                        SDTProductos.Cajas = CantCajas;
                        SDTProductos.Embalaje =PedPenArtEmb;
                        SDTProductos.EvaluarDescuentos();
                        SDTProductos.GuardarDcto(1);*/



                        PedidoJson += "{";
                        PedidoJson += " \"PedPenPedido\": \"" + NumPedido + "\",";
                        PedidoJson += " \"PedPenNitSec\": \"" + Clientes.getString(0) + "\",";
                        PedidoJson += " \"PedPenCliSec\": " + Clientes.getInt(1) + ",";
                        PedidoJson += " \"PedPenArtSec\": \"" + Clientes.getString(2) + "\",";
                        PedidoJson += " \"PedPenVenCod\": \"" + vUsuario + "\",";
                        PedidoJson += " \"PedPencant\": " + CantUni + ",";
                        PedidoJson += " \"PedPencantcaj\": " + CantCajas + ",";
                        PedidoJson += " \"PedPencantinf\": " + cantinf + ",";
                        PedidoJson += " \"PedPenpdyear\": " + Clientes.getInt(5) + ",";
                        PedidoJson += " \"PedPenpdmonth\": " + (time.month + 1) + ",";
                        PedidoJson += " \"PedPenpdday\": " + Clientes.getInt(7) + ",";
                        PedidoJson += " \"PedPenPrecio\": " + preciocal + ",";
                        PedidoJson += " \"PedPenPlazo\": " + Clientes.getInt(9) + ",";
                        PedidoJson += " \"PedPenPlazoNom\": \"" + Clientes.getString(38) + "\",";
                        PedidoJson += " \"PedPenContreg\": " + Clientes.getInt(21) + ",";
                        PedidoJson += " \"PedPendesc\": " + Clientes.getDouble(10) + ",";
                        PedidoJson += " \"PedPendesc2\": " + Clientes.getDouble(11) + ",";
                        PedidoJson += " \"PedPendesc3\": " + Clientes.getDouble(12) + ",";
                        PedidoJson += " \"PedPendesc4\": " + Clientes.getDouble(13) + ",";
                        PedidoJson += " \"PedPenConfDesc\": " + Clientes.getDouble(16) + ",";
                        PedidoJson += " \"PedPenConfEmp\": " + Clientes.getDouble(17) + ",";
                        PedidoJson += " \"PedPenConfProv\": " + Clientes.getDouble(18) + ",";
                        PedidoJson += " \"PedPenConfVend\": " + Clientes.getDouble(19) + ",";
                        PedidoJson += " \"PedPenPox\": \"" + Clientes.getString(22) + "\",";
                        PedidoJson += " \"PedPenPosy\": \"" + Clientes.getString(23) + "\",";
                        PedidoJson += " \"PedPenObs\": \"" + Observacion + "\",";
                        PedidoJson += " \"PedPenVisHor\": " + Clientes.getInt(24) + ",";
                        PedidoJson += " \"PedPenVisMin\": " + Clientes.getInt(25) + ",";
                        PedidoJson += " \"PedPenVisSeg\": " + Clientes.getInt(26) + ",";
                        PedidoJson += " \"PedPenVisHorFin\": " + Clientes.getInt(27) + ",";
                        PedidoJson += " \"PedPenVisMinFin\": " + Clientes.getInt(28) + ",";
                        PedidoJson += " \"PedPenVissegFin\": " + Clientes.getInt(29) + ",";
                        PedidoJson += " \"PedPenTotalPed\": " + TotalPedido + ",";
                        PedidoJson += " \"PedPenPedLisPreCod\": " + Clientes.getInt(31) + ",";
                        PedidoJson += " \"PedPenBonProSec\": " + BonProSec + ",";
                        PedidoJson += " \"PedPenBonProDetLin\": " + BONPRODETLIN + ",";
                        PedidoJson += " \"PedPenBonProTipo\": \"" + BonProTipo + "\",";
                        PedidoJson += " \"PedPenCotDetCheckMax\": \"" + Clientes.getString(37) + "\",";
                        PedidoJson += " \"PedPencantcajinf\": " + cantcajinf + ",";
                        PedidoJson += " \"PedPenArtBodcod\": " + ArtBodCod + ",";
                        PedidoJson += " \"PedPenPlanPuente\": \"" + PlanPuente +  "\",";
                        PedidoJson += " \"PedPenArtEmb\": \"" + PedPenArtEmb + "\",";
                        PedidoJson += " \"PedPenIVA\": " + iva + ",";
                        PedidoJson += " \"Tipo\": \" $TIPO$ \""; // Último elemento sin coma al final
                        PedidoJson += "}";

                       String SqlPedidoJSON = "insert into HistorialEnvioMovil (HisEnvMovFechor,HisMovPedNum,HisMovDatos) values (getDate(),'"+NumPedido+"','"+PedidoJson+"')";


                        if((CantUni+CantCajas+cantinf+cantcajinf) > 0){

                            JsonEnvio = " select count(*) as numero from  PedidoPendientes where PedPenPedido = '"+NumPedido+"' and PedPenArtSec = '"+ Clientes.getString(2)+"' " +
                                        " and PedPenBonProSec = " + BonProSec + " and PedPenBonProDetLin ="+BONPRODETLIN+" ";
                            try{
                                rsClientes = comm.executeQuery(JsonEnvio);
                                while (rsClientes.next()) {
                                    exite = rsClientes.getInt("numero");
                                }
                            }catch (Exception e){
                                nerror+= "select try "+e.toString();
                            }


                            /*JsonEnvio = "Update PedidoPendientes set PedPenIVA = "+iva+" , PedPenArtEmb = "+PedPenArtEmb+" , PedPenNumIntentos = 0, PedPenEstServ='P', PedPencant ="+CantUni+"  ,PedPencantcaj="+CantCajas+"  ,PedPencantinf= "+cantinf+" ,PedPenPrecio="+preciocal+",PedPenPlazo="+Clientes.getInt(9)+"," +
                                    " PedPendesc="+Clientes.getDouble(10)+", PedPendesc2="+Clientes.getDouble(11)+",PedPendesc3="+Clientes.getDouble(12) +"," +
                                    " PedPendesc4="+Clientes.getDouble(13) +",PedPenConfDesc="+Clientes.getDouble(16) +",PedPenConfEmp="+Clientes.getDouble(17) +",PedPenConfProv="+Clientes.getDouble(18) +",PedPenConfVend="+Clientes.getDouble(19) +", " +
                                    " PedPenObs='"+Observacion+"' ,PedPenTotalPed="+TotalPedido+", PedPenBonProSec="+BonProSec+", PedPenBonProDetLin="+BONPRODETLIN+",PedPenBonProTipo='"+BonProTipo+"',PedPenArtBodcod="+ArtBodCod+", PedPencantcajinf="+cantcajinf+", PedPenPlanPuente='"+PlanPuente+"' " +
                                    " where PedPenPedido = '"+NumPedido+"' and PedPenArtSec = '"+ Clientes.getString(2)+"' " +
                                    " and PedPenBonProSec = " + BonProSec + " and PedPenBonProDetLin ="+BONPRODETLIN+" ";*/

                            JsonEnvio = "Update PedidoPendientes set   PedPencant ="+CantUni+"  ,PedPencantcaj="+CantCajas+", " +
                                    " PedPencantinf= "+cantinf+" ,PedPenPrecio= "+preciocal+",PedPenPlazo= "+Clientes.getInt(9)+"," +
                                    " PedPendesc= "+Clientes.getDouble(10)+", PedPendesc2= "+Clientes.getDouble(11)+"," +
                                    " PedPendesc3= "+Clientes.getDouble(12) +", " +
                                    " PedPendesc4= "+Clientes.getDouble(13) +", " +
                                    " PedPenConfDesc= "+Clientes.getDouble(16) +",PedPenConfEmp="+Clientes.getDouble(17) +", " +
                                    " PedPenConfProv= "+Clientes.getDouble(18) +",PedPenConfVend="+Clientes.getDouble(19) +", " +
                                    " PedPenObs='"+Observacion+"' ,PedPenTotalPed="+TotalPedido+",PedPenPedLisPreCod = "+Clientes.getInt(31)+", PedPenBonProSec="+BonProSec+", " +
                                    " PedPenBonProDetLin="+BONPRODETLIN+",PedPenBonProTipo='"+BonProTipo+"',PedPencantcajinf="+cantcajinf+", " +
                                    " PedPenArtBodcod="+ArtBodCod+", " +
                                    " PedPenEstServ='P', PedPenPlanPuente='"+PlanPuente+"',PedPenNumIntentos = 0, " +
                                    " PedPenArtEmb = "+PedPenArtEmb+" ,PedPenIVA = "+iva+", PedPenFechorMovEnv = getDate() "+
                                    " where PedPenPedido = '"+NumPedido+"' and PedPenArtSec = '"+ Clientes.getString(2)+"' " +
                                    " and PedPenBonProSec = " + BonProSec + " and PedPenBonProDetLin ="+BONPRODETLIN+" ";

                                    PedidoJson = PedidoJson.replace("$TIPO$","Actualizar");

                        }else{
                            JsonEnvio = "Delete from  PedidoPendientes where PedPenPedido = '"+NumPedido+"' and PedPenArtSec ='"+ Clientes.getString(2)+"' " +
                                    " and PedPenBonProSec = " + BonProSec + " and PedPenBonProDetLin ="+BONPRODETLIN+" ";
                            eliminar = 1;
                                    PedidoJson = PedidoJson.replace("$TIPO$","Eliminar");

                        }


                        int actualizo = 0;
                        //tercer try
                        try {

                            if (exite > 0){
                                actualizo = comm.executeUpdate(JsonEnvio);
                                Log.e("SQLHistorico","insert into HistorialEnvioMovil (HisEnvMovFechor,HisMovPedNum,HisMovDatos) values (getDate(),'"+NumPedido+"','"+PedidoJson+"') ");
                                int historial = comm.executeUpdate(SqlPedidoJSON);
                                Log.e("SqlPedidoJSact",SqlPedidoJSON);


                            if(actualizo > 0){
                                actualizarestado(pContext,Clientes.getString(0),Clientes.getInt(1),vUsuario,NumPedido,"A");
                            }
                            nerror += "Enviado correctamente\n ";
                            }


                        }catch (Exception e){
                            nerror += "tercer try "+e.toString();
                        }

                        if(actualizo == 0 && eliminar == 0 ){
                        JsonEnvio = " INSERT INTO PedidoPendientes " +
                                "           ( PedPenPedido " +
                                "           , PedPenNitSec " +
                                "           , PedPenCliSec " +
                                "           , PedPenArtSec " +
                                "           , PedPenVenCod " +
                                "           , PedPencant " +
                                "           , PedPencantcaj " +
                                "           , PedPencantinf " +
                                "           , PedPenpdyear " +
                                "           , PedPenpdmonth " +
                                "           , PedPenpdday " +
                                "           , PedPenPrecio " +
                                "           , PedPenPlazo " +
                                "           , PedPenPlazoNom " +
                                "           , PedPenContreg " +
                                "           , PedPendesc " +
                                "           , PedPendesc2 " +
                                "           , PedPendesc3 " +
                                "           , PedPendesc4 " +
                                "           , PedPenConfDesc " +
                                "           , PedPenConfEmp " +
                                "           , PedPenConfProv " +
                                "           , PedPenConfVend " +
                                "           , PedPenPox " +
                                "           , PedPenPosy " +
                                "           , PedPenFechor " +
                                "           , PedPenObs " +
                                "           , PedPenPapa " +
                                "           , PedPenPrenotsinped " +
                                "           , PedPenVisHor " +
                                "           , PedPenVisMin " +
                                "           , PedPenVisSeg " +
                                "           , PedPenVisHorFin " +
                                "           , PedPenVisMinFin " +
                                "           , PedPenVissegFin " +
                                "           , PedPenTotalPed " +
                                "           , PedPenPedLisPreCod " +
                                "           , PedPenBonProSec " +
                                "           , PedPenBonProDetLin " +
                                "           , PedPenBonProTipo " +
                                "           , PedPenCotDetCheckMax  " +
                                "           , PedPencantcajinf  " +
                                "           , PedPenArtBodcod, PedPenEstServ,PedPenPlanPuente,PedPenArtEmb,PedPenNumIntentos,PedPenIVA,PedPenFechorMovEnv)" +
                                "           VALUES (" +
                                "            '" + NumPedido + "' " +
                                "           , '" + Clientes.getString(0) +"' " +
                                "           , " + Clientes.getInt(1) + "  " +
                                "           , '" + Clientes.getString(2) + "' " +
                                "           , '" + vUsuario + "' " +
                                "           , " + CantUni + " " +
                                "           , " + CantCajas + " " +
                                "           , " + cantinf + " " +
                                "           , " + Clientes.getInt(5)  + " " +
                                "           , " +  (time.month + 1)  + " " +
                                "           , " + Clientes.getInt(7) + " "  +
                                "           , " + preciocal + " " +
                                "           , " + Clientes.getInt(9) + " " +
                                "           , '"+Clientes.getString(38)+"' " +
                                "           , "+Clientes.getInt(21)+" " +
                                "           , " + Clientes.getDouble(10) + " " +
                                "           , " + Clientes.getDouble(11) + " " +
                                "           , " + Clientes.getDouble(12) + " " +
                                "           , " + Clientes.getDouble(13) + " " +
                                "           , " + Clientes.getDouble(16) + " " +
                                "           , " + Clientes.getDouble(17) + " " +
                                "           , " + Clientes.getDouble(18) + " " +
                                "           , " + Clientes.getDouble(19) + " " +
                                "           , '" + Clientes.getString(22) + "' " +
                                "           , '" + Clientes.getString(23) + "' " +
                                "           , '' " +
                                "           , '" + Observacion + "' " +
                                "           , '' " +
                                "           , 'N' " +
                                "           , " + Clientes.getInt(24) + " " +
                                "           , " + Clientes.getInt(25) + " " +
                                "           , " + Clientes.getInt(26) + " " +
                                "           , " + Clientes.getInt(27) + " " +
                                "           , " + Clientes.getInt(28) + " " +
                                "           , " + Clientes.getInt(29) + " " +
                                "           , " + TotalPedido + " " +
                                "           , " + Clientes.getInt(31) + " " +
                                "           , " + BonProSec + " "  +
                                "           , " + BONPRODETLIN + " " +
                                "           , '" + BonProTipo + "' " +
                                "           , '"+Clientes.getString(37)+"' " +
                                "           , " + cantcajinf + " " +
                                "           , " + ArtBodCod + ",'P','"+PlanPuente+"',"+PedPenArtEmb+",0,"+iva+",GetDate() ) ";

//quinto try
                        try {
                            conn = conbd.CargarConexion(pContext);
                            comm = conn.createStatement();
                            comm.execute(JsonEnvio);
                            PedidoJson = PedidoJson.replace("$TIPO$","Nuevo");
                            PedidoJson = PedidoJson.replace("Actualizar","Nuevo");
                            SqlPedidoJSON = "insert into HistorialEnvioMovil (HisEnvMovFechor,HisMovPedNum,HisMovDatos) values (getDate(),'"+NumPedido+"','"+PedidoJson+"')";
                            try {
                            int historial = comm.executeUpdate(SqlPedidoJSON);
                            Log.e("SqlPedidoJSON-actualizar",SqlPedidoJSON);
                            }catch (Exception e){
                                Log.e("Error sql",e.toString());
                                nerror+= "sexto try "+e.toString();
                            }
                            //Log.e("SQLHistorico","insert into HistorialEnvioMovil (HisEnvMovFechor,HisMovPedNum,HisMovDatos) values (getDate(),'"+NumPedido+"','"+PedidoJson+"') ");
                           // Log.e("JSONHISTORICO",PedidoJson);
                            nerror += "Enviado correctamente\n ";
                            actualizarestado(pContext,Clientes.getString(0),Clientes.getInt(1),vUsuario,NumPedido,"A");

                        }catch (Exception e){
                            Log.e("Error sql",e.toString());

                               nerror+= "quinto try "+e.toString();

                        }/*finally {
                            try { if (comm != null) comm.close(); } catch (Exception errorST) { errorST.printStackTrace(); }
                            try { if (conn != null) conn.close(); } catch (Exception errorCONN) { errorCONN.printStackTrace(); }
                        }*/

                        }

                    }


                }

            } while (Clientes.moveToNext());
        }

      //Log.e("nerrornerror",nerror);
        return nerror;

    }




    @SuppressLint("SuspiciousIndentation")
    public String EnviarPedidosFicc(Context pContext, String prefijo, String nitsec, Integer clisec, Integer env) {
  Log.e("Entro ´pedido",prefijo);
        ConBd conbd = new ConBd();
       // Connection connGen = conbd.CargarConexion();
        conbd.Variables();
        String MantisFicc = conbd.MantisFicc;
        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();
        Integer vPedidoMinimo=vGlobalVariables.getParMovSec();

        Time time = new Time();
        time.setToNow();

        String sExistencia="";
        String sExistencia2="";
        String mensajePed="";
        String NumPed = prefijo+nitsec + clisec +time.year + (time.month + 1) + time.monthDay;

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 5);


        Double pedidoMinimo=0.0;

        Cursor vCursorUsuarios = BaseDeDatos.getReadableDatabase().rawQuery("select ParMovPedMin from usuarios where VenCnt=1  ", null);
        vCursorUsuarios.moveToFirst();

        if (vCursorUsuarios.getCount() >0) {
            pedidoMinimo=vCursorUsuarios.getDouble(0);
        }


        String Consulta = "";
        String consultaEncabezado = "";
        GlobalVariables gGlobalVariables = GlobalVariables.getInstance();
        String vEmpresa=gGlobalVariables.getEmpresa();
        conbd.Variables();
        String mantisficc =  conbd.MantisFicc;
        String BloqueoCupo =  conbd.BloqueaCupo;
        String Remision = conbd.FiccRem;
        Cursor Clientes = null;
        Cursor PedidoEn = null;
        String resSql = "";
        String where = "";
        if(env == 1) {
            resSql = " and CliNoRee ='N' ";
        }
        if(nitsec.isEmpty()){
        }else{
            where = "and p.nitsec='"+nitsec+"' ";
        }

        if(clisec > 0){
            where += "and p.CliSec="+clisec+" ";
        }


        if(vEmpresa.equalsIgnoreCase("SUHOGAR")|| vEmpresa.equalsIgnoreCase("SUHOGARPRU")){
            consultaEncabezado=  "select p.NitSec,ifNULL(c.CliSec,0) CliSec,ifNULL(c.NitCom,'') NitCom,ifNULL(c.CliNom,'') CliNom,ifNULL(prefijo,'') prefijo, ifNull(ConPagnom, '') ConPagnom, pedLisPrecod, Nitcom,Clinom,ConPagnom from pedido p left join clientes c on p.nitsec=c.nitsec and p.clisec=c.clisec " +
                    " where pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and ifNULL(cant,0)+ifNULL(cantcaj,0)<>0 "+where+" group by p.NitSec,p.CliSec,c.NitCom,c.CliNom,prefijo,ConPagnom,pedLisPrecod,Nitcom,Clinom,ConPagnom";

        }else {
            consultaEncabezado=  "select p.NitSec,ifNULL(c.CliSec,0) CliSec,ifNULL(c.NitCom,'') NitCom,ifNULL(c.CliNom,'') CliNom,ifNULL(prefijo,'') prefijo, ifNull(ConPagnom, '') ConPagnom, LisPrecod, Nitcom,Clinom,ConPagnom from pedido p left join clientes c on p.nitsec=c.nitsec and p.clisec=c.clisec " +
                    " where pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and ifNULL(cant,0)+ifNULL(cantcaj,0)<>0 "+where+" group by p.NitSec,p.CliSec,c.NitCom,c.CliNom,prefijo,ConPagnom,LisPrecod,Nitcom,Clinom,ConPagnom";
        }

            try {
                PedidoEn = BaseDeDatos.getWritableDatabase().rawQuery(consultaEncabezado, null);
            } catch (Exception e) {
                int jj = 0;
            }


        String JsonEnvio = "";
        String LisPrecod = "";
        String jNitSec  ="";
        Integer jClisec = 0;
        String NumPedido = "";

if(PedidoEn.getCount() > 0) {
    PedidoEn.moveToFirst();
    do {
        String enNitSec = PedidoEn.getString(0);
        String enprefijo = PedidoEn.getString(4);
        LisPrecod = PedidoEn.getString(6);
        Integer enclisec = PedidoEn.getInt(1);
        NumPedido = enprefijo+ vUsuario.trim() + enNitSec + enclisec + time.year + "-" + (time.month + 1) + "-" + time.monthDay;

        Log.e("Entro gestor: lista ",String.valueOf(LisPrecod));
        String TipCod = "";
        if(Remision.equalsIgnoreCase("S")){
            TipCod  = "RPE";
        }else{
            if(vEmpresa.equalsIgnoreCase("INDULAC")){
                TipCod  = "PEMO";
            }else{
                TipCod  = "OPHG";
            }
        }


        String JsonDetalle = "";
        JsonEnvio = "";
        SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss.SSS",
                Locale.getDefault()
        );
        String fechaFormateada = sdf.format(new Date());

        fechaFormateada = fechaFormateada.replace(" ", "---");
        JsonEnvio = "{\"pedido\" :[{\"PedNum\":  \"" + NumPedido + "\",\"TipCod\":  \""+TipCod+"\",\"PedFecha\":  \"" +fechaFormateada + "\",\"PedNitSec\":  \"" + PedidoEn.getString(0) + "\",\"PedCliSec\": \"" + PedidoEn.getInt(1) + "\",\"PedVenCod\": \"" + vUsuario + "\", \"PedPla\": \"$plazo$\",\"PedEst\": \"A\",\"PedObs\": \"$observacion\",\"Detalle\": [";
        Consulta = "";

        String precio = "a.precio"+LisPrecod;



        Consulta = "select p.nitsec,p.clisec,p.artsec,p.vencod,cant,pdyear,pdmonth,pdday,"+precio+",plazo,pordesc,pordesc2,pordesc3+pordesc6 pordesc3,pordesc4+pordesc5 pordesc4,ifnull(visobs,'') visobs,p.prefijo,(ifnull(pordescno,0)+ifnull(pordesc2no,0)+ifnull(pordesc3no,0)+ifnull(pordesc4no,0)+ifnull(pordesc5no,0)+ifnull(pordesc6no,0))-(ifnull(pordesc,0)+ifnull(pordesc2,0)+ifnull(pordesc3,0)+ifnull(pordesc4,0)+ifnull(pordesc5,0)+ifnull(pordesc6,0)) descno,confemp,confprov,confvend,cantcaj,ConNotCod,ifnull(VisLatitud,'') VisLatitud,ifnull(VisLongitud,'') VisLongitud,ifnull(VisHor,0) VisHor,ifnull(VisMin,0) VisMin,ifnull(VisSeg,0) VisSeg,ifnull(VisHor,0) VisHorFin,ifnull(VisMin,0) VisMinFin,ifnull(VisSeg,0) VisSegFin,0 MovParPremSecLin, "+LisPrecod+" PedLisPreCod,0 cantinf,0 cantcajinf,0 MovParPremtipo,0 MovParPremSec,bodcod  from pedido p " +
                " left join articulos a on rtrim(a.artsec)=rtrim(p.artsec) " +
                " left join clientes n on ltrim(rtrim(n.nitsec))=ltrim(rtrim(p.nitsec)) and n.clisec=p.clisec " +
                " left join visita v on ltrim(rtrim(v.nitsec))=ltrim(rtrim(p.nitsec)) and v.clisec=p.clisec  and VisAno=pdyear and pdmonth=VisMes  and visdia=pdday and VisPref= p.prefijo" +
                //" where a.precio1>0 and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay +
                " where  p.nitsec = '"+enNitSec+"' and p.clisec = "+enclisec+" and prefijo = '"+enprefijo+"' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay   ;

        Consulta+=" union all " +
                "   select MovParNitSec,MovParCliSec,m.MovParPremArtSec,'" + vUsuario + "' vencod,MovParPremCant cant,MovParPremAno,MovParPremMes,MovParPremDia,1 precio,0 plazo,0.0 pordesc,0.0 pordesc2,0.0 pordesc3,0.0 pordesc4,'BON' obs,prefijo prefi,0.0,0.0,0.0,0.0,0,0,'','',0,0,0,0,0,0 ,0 MovParPremSecLin,1 PedLisPreCod,0 cantinf,0 cantcajinf,0 MovParPremtipo,0 MovParPremSec ,0 bodcod from MovParPrem M " +
                "left join articulos a on a.artsec=m.MovParPremArtSec where MovParPremCant > 0 and MovParPremAno=" + time.year + " and MovParPremMes=" + (time.month + 1) + " and MovParPremDia=" + time.monthDay;
        try {
            Log.e("Consulta art",Consulta);
            Clientes = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null);
        } catch (Exception e) {


            int jj = 0;
        }

    if (Clientes.getCount() > 0) {

        Clientes.moveToFirst();
        String Observacion ="";
        int nplazo = 0;
        int conta = 0;
        do {

            Double TotalPedido = TotalesPedido(pContext, Clientes.getString(15), Clientes.getString(0), Clientes.getInt(1), "", "", "").Total;
            int sincupo = 0;

            if (BloqueoCupo.equalsIgnoreCase("N")) {


                GestorCartera gestorcartera = new GestorCartera();
                gestorcartera.TotalesCatera(pContext, nitsec, clisec);
                Integer CarteraGeneral = gestorcartera.CarteraGeneral;
                String tmpNitSec = Clientes.getString(0);


                jNitSec = Clientes.getString(0);
                jClisec = Clientes.getInt(1);
                Integer tmpCliSec = Clientes.getInt(1);
                Cursor cursorclientes = BaseDeDatos.getReadableDatabase().rawQuery("select CliCup,CliConPag from clientes where nitsec='" + tmpNitSec + "' and clisec=" + tmpCliSec, null);

                Integer vuelta = 0;
                Integer cupo = 0;
                Integer CliConPag = 0;
                if (cursorclientes.getCount() > 0) {
                    cursorclientes.moveToFirst();
                    do {
                        cupo = cursorclientes.getInt(0);
                        CliConPag = cursorclientes.getInt(1);

                    } while (cursorclientes.moveToNext());
                }
                if (cupo - CarteraGeneral - TotalPedido.intValue() < 0 && (CliConPag != 0 && cupo != 0)) {
                    sincupo = 1;
                }
            }

            if (sincupo == 0) {
                Double CantUni = 0.00;
                Double cantinf = 0.00;
                Integer CantCajas = 0;
                Integer cantcajinf = 0;
                if ((pedidoMinimo != 0 && TotalPedido > pedidoMinimo) || pedidoMinimo == 0) {
                    CantUni = Clientes.getDouble(4);
                    CantCajas = Clientes.getInt(20);
                    cantinf = Clientes.getDouble(32);
                    cantcajinf = Clientes.getInt(33);
                }


                //Clientes.getString(3)

                SDTResumenPedidos SDTResumenPedidos = new SDTResumenPedidos();
                SDTResumenPedidos = TotalesPedido(pContext, Clientes.getString(15), Clientes.getString(0), Clientes.getInt(1), "", "", "");

                 Observacion = Clientes.getString(14);
                nplazo = Clientes.getInt(9);
                int BonProSec = Clientes.getInt(35);
                int BONPRODETLIN = Clientes.getInt(30);
                int ArtBodCod = Clientes.getInt(36);
                String BonProTipo = Clientes.getString(34);


                Observacion = Observacion.replaceAll("[^\\w ]+", "");


                //  = precioesp(pContext,Clientes.getString(0),Clientes.getString(2), Clientes.getDouble(8) );

                double precioNu = 0;
                precioNu = 0;
                String consultaart = "select precioesp from PreciosEspeciales where peNitSec = '" + nitsec + "' and peArtSec = '" + Clientes.getString(2).trim() + "'";
                Cursor cursorart = BaseDeDatos.getWritableDatabase().rawQuery(consultaart, null);
                cursorart.moveToFirst();
                if (cursorart.getCount() > 0) {
                    cursorart.moveToFirst();
                    precioNu = cursorart.getDouble(0);
                } else {
                    precioNu = Clientes.getDouble(8);
                }

                Double preciocal = precioNu;


                if(vEmpresa.equalsIgnoreCase("INDULAC")){


                    String ArtPedFac = "";
                    Cursor detcantidad = BaseDeDatos.getWritableDatabase().rawQuery("Select Cantidad,CantInf,secuencia from PedidoInf where prefijo='" + enprefijo + "' and nitsec='" + enNitSec + "' and clisec='" + enclisec + "' and artsec='" + Clientes.getString(2) + "' order by secuencia ASC", null);
                    if(detcantidad.getCount()>0){
                        detcantidad.moveToFirst();
                        int vuelta =0;
                        do{
                            ArtPedFac+=detcantidad.getString(0)+";"+detcantidad.getString(1)+",";
                            vuelta += 1;
                        }while(detcantidad.moveToNext());
                    }

                    JsonDetalle += "{\"ArtSec\": \"" + Clientes.getString(2) + "\",\"PedUni\":\"" + CantUni + "\",\"PedPrePub\": \"" + preciocal + "\",\"PedKarValTotMenDes\":  \"" + (preciocal * CantUni) + "\" ,\"PedDesuno\": \"" + Clientes.getDouble(10) + "\", \"PedDesDos\": \"" + Clientes.getDouble(11) + "\",\"PedDesTres\": \"" + Clientes.getDouble(12) + "\",\"PedDesCua\": \"" + Clientes.getDouble(13) + "\", \"PedLisPrecod\": \"" + LisPrecod + "\",\"PedBodSucccSec\": \"" + ArtBodCod + "\",\"PedCantidades\": \"" + ArtPedFac + "\"    },";

                }else{
                    if(CantUni > 0){
                        Log.e("LISTA LOGdet",LisPrecod);
                        JsonDetalle += "{\"ArtSec\": \"" + Clientes.getString(2) + "\",\"PedUni\":\"" + CantUni + "\",\"PedPrePub\": \"" + preciocal + "\",\"PedKarValTotMenDes\":  \"" + (preciocal * CantUni) + "\" ,\"PedDesuno\": \"" + Clientes.getDouble(10) + "\", \"PedDesDos\": \"" + Clientes.getDouble(11) + "\",\"PedDesTres\": \"" + Clientes.getDouble(12) + "\",\"PedDesCua\": \"" + Clientes.getDouble(13) + "\", \"PedLisPrecod\": \"" + LisPrecod + "\",\"PedBodSucccSec\": \"" + ArtBodCod + "\"},";
                        Log.e("JsonDetalle: ",JsonDetalle);
                    }
                }

          }else{
                sExistencia2 = "SIN CUPO";
                return sExistencia2;
            }

        } while (Clientes.moveToNext());

        JsonEnvio += JsonDetalle + " ] } ] }";
        JsonEnvio = JsonEnvio.replace(", ]", "]");
        JsonEnvio = JsonEnvio.replace(" ", "");
        JsonEnvio = JsonEnvio.replace("---", " ");
        JsonEnvio = JsonEnvio.replace("$observacion", Observacion);
        JsonEnvio = JsonEnvio.replace("$plazo$", String.valueOf(nplazo).trim());
        JsonEnvio = JsonEnvio.trim();
        JsonDetalle  ="";

        Log.e("JsonEnvio: ",JsonEnvio);

        String sql = conbd.UrlEnvio;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        URL url = null;
        HttpURLConnection conn;

        try {
            url = new URL(sql);

            Log.e("jsonvisita", JsonEnvio);
            Log.e("url", url.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setRequestProperty("Content-Type", "application/json");//; utf-8
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            StringBuilder result = new StringBuilder();
            result.append(JsonEnvio);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os)); //, "UTF-8"

            writer.write(result.toString());
            writer.flush();
            writer.close();
            os.close();

            conn.connect();

            int statusCode = conn.getResponseCode();
            InputStream inputstream = null;


            if (statusCode >= 200 && statusCode < 400) {
                inputstream = conn.getInputStream();
            } else {
                inputstream = conn.getErrorStream();
            }



            BufferedReader in = new BufferedReader(new InputStreamReader(inputstream));
            String inputLine;
            StringBuffer response = new StringBuffer();
            String json = "";

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            Log.e("jsonvisita", response.toString());
            JSONObject xjson = new JSONObject(response.toString());
            // Accedemos al array "SDTRespuestaApp"
            JSONArray jsonArr = xjson.getJSONArray("SDTRespuestaApp");

            // Iteramos por el array
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonObject = jsonArr.getJSONObject(i);

                String pedNum = jsonObject.optString("PedNum");
                int status = jsonObject.optInt("Status");
                String txtestado = "Error al enviar";
                String estado = "P";
                if(status == 200){
                    txtestado = "Enviado";
                    estado = "S";
                  }
                mensajePed += "\n Pedido: "+pedNum+" "+txtestado+" ";
                System.out.println("estado: " + estado);
                actualizarestado(pContext,"",0,vUsuario,pedNum,estado);

                System.out.println("Status: " + status);
            }



            /*json = "[" + response.toString() + "]";
            JSONArray jsonArr = null;
            jsonArr = new JSONArray(json);
            String mensaje = "";
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonObject = jsonArr.getJSONObject(i);
                mensajePed = jsonObject.optString("SDTRespuestaApp");




            }*/


        } catch (MalformedURLException e) {
            sExistencia2 += e.getMessage();
            Log.e("MalformedURLException",sExistencia2);
            e.printStackTrace();
        } catch (IOException e) {
            sExistencia2 += e.getMessage();
            e.printStackTrace();
            Log.e("IOException",sExistencia2);

        } catch (JSONException e) {
            sExistencia2 += e.getMessage();
            e.printStackTrace();
            Log.e("JSONException",sExistencia2);

        }

    }

    } while (PedidoEn.moveToNext());
}

if(MantisFicc.equalsIgnoreCase("S")){

    Cursor visita = null;

    if(nitsec.isEmpty()){
    }else{
        where = "and NitSec='"+nitsec+"'";
    }

    String consultaVis = "Select VenCod,NitSec,CliSec,VisObs,VisAno,VisMes,VisDia,VisHor,VisMin,VisSeg,VisHorFin,VisMinFin,VisSegFin,VisFotoimg from Visita where VisAno = "+time.year+" and VisMes = "+(time.month + 1) +" and VisDia ="+time.monthDay+" "+where+" ";


    try {
        visita = BaseDeDatos.getWritableDatabase().rawQuery(consultaVis, null);
    } catch (Exception e) {

        int jj = 0;
    }

    if(visita.getCount() > 0){
        visita.moveToFirst();
        do{
            String VisPedVenCod = vUsuario;
            String visPedNitsec = visita.getString(1);
            int visPedCliSec = visita.getInt(2);
            String VisPedObs = visita.getString(3) ;
            int VisPedHor = visita.getInt(7) ;
            int VisPedMin = visita.getInt(8);
            int VisPedSeg = visita.getInt(9)  ;
            int VisPedAno = visita.getInt(4) ;
            int VisPedMes = visita.getInt(5) ;
            int ViPedDia = visita.getInt(6) ;
            int VisPedHorFin = visita.getInt(10) ;
            int VisPedMinFin = visita.getInt(11) ;
            int VisSegFin = visita.getInt(12) ;


            String Path = visita.getString(13) ;
            String VisPedImg = "";
            ///   CliTempImg64 = CliTempImg64.replace("\n","");
            Log.e("VisPedImg : ",Path);
            if(Path.contains("/storage/emulated/")){
                File bitmapFile = new File(Path);
                Bitmap bitmap = BitmapFactory.decodeFile(bitmapFile.toString());
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                VisPedImg = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
                VisPedImg = VisPedImg.replace("\n","");
            }




            String VisPedNum = "VIS"+VisPedVenCod+visPedNitsec+visPedCliSec+VisPedAno+"-"+VisPedMes+"-"+ViPedDia;


            String jsonvisita = "{\"Visita\": {\"VisPedNum\":\""+VisPedNum+"\",\"VisPedVenCod\":\""+VisPedVenCod+"\",\"VisPedNitsec\":\""+visPedNitsec+"\",\"VisPedclisec\":\""+visPedCliSec+"\",\"VisPedObs\":\""+VisPedObs+"\",\"VisPedHor\":\""+VisPedHor+"\", " +
                    " \"VisPedMin\":\""+VisPedMin+"\",\"VisPedSeg\":\""+VisPedSeg+"\",\"VisPedAno\":\""+VisPedAno+"\", \"VisPedMes\":\""+VisPedMes+"\",\"ViPedDia\":\""+ViPedDia+"\",\"VisPedHorFin\":\""+VisPedHorFin+"\",\"VisPedMinFin\":\""+VisPedMinFin+"\", " +
                    " \"VisPedSegFin\":\""+VisSegFin+"\",\"VisPedImg\":\""+VisPedImg+"\" " +
                    "}}";


            Log.e("jsonvisita", jsonvisita);
            conbd.Variables();
            String sql = conbd.UrlEnvioVisita;
           // sExistencia = "0.0";
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            URL url = null;
            HttpURLConnection conn;
            try {
                url = new URL(sql);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/json");//; utf-8
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("POST");
                StringBuilder result = new StringBuilder();
                result.append(jsonvisita);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
                writer.write(result.toString());
                writer.flush();
                writer.close();
                os.close();
                conn.connect();
                int statusCode = conn.getResponseCode();
                InputStream inputstream = null;
                if (statusCode >= 200 && statusCode < 400) {
                    // Create an InputStream in order to extract the response object
                    inputstream = conn.getInputStream();
                } else {
                    inputstream = conn.getErrorStream();
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(inputstream));
                String inputLine;
                StringBuffer response = new StringBuffer();
                String json = "";

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                json = "[" + response.toString() + "]";
                JSONArray jsonArr = null;
                jsonArr = new JSONArray(json);
                String mensaje = "";



                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObject = jsonArr.getJSONObject(i);
                    sExistencia = jsonObject.optString("Mensaje");
                    Log.e("SEcistneics",jsonObject.toString());
                    if(sExistencia.equalsIgnoreCase("S")){
                        sExistencia2 += "Se enviaron las visitas correctamente";
                    }else{
                        sExistencia2= sExistencia;
                    }
                    sExistencia2= sExistencia;
                }





            } catch (MalformedURLException e) {
                sExistencia2 +=  e.getMessage();
                e.printStackTrace();
                Log.e("MalformedURLExceptvis",sExistencia2);
            } catch (IOException e) {
                sExistencia2 +=  e.getMessage();
                Log.e("MIOExceptiontvis",sExistencia2);

                e.printStackTrace();
            } catch (JSONException e) {
                sExistencia2 +=  e.getMessage();
                Log.e("JSONExceptionvis",sExistencia2);

                e.printStackTrace();
            }

        }while (visita.moveToNext());
    }

}


        return mensajePed;

    }
    public String EnviarPedidosFiccGx5(Context pContext, String prefijo, String nitsec, Integer clisec, Integer env) {
        Log.e("Entro ´pedido",prefijo);
        ConBd conbd = new ConBd();
        // Connection connGen = conbd.CargarConexion();
        conbd.Variables();
        String MantisFicc = conbd.MantisFicc;
        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();
        Integer vPedidoMinimo=vGlobalVariables.getParMovSec();

        Time time = new Time();
        time.setToNow();


        if(dayOfMonth > 0){
            time.set(time.second, time.minute, time.hour,dayOfMonth , month, year);
        }



        String sExistencia="";
        String sExistencia2="";
        String mensajePed="";
        String NumPed = prefijo+nitsec + clisec +time.year + (time.month + 1) + time.monthDay;

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 5);


        Double pedidoMinimo=0.0;

        Cursor vCursorUsuarios = BaseDeDatos.getReadableDatabase().rawQuery("select ParMovPedMin from usuarios where VenCnt=1  ", null);
        vCursorUsuarios.moveToFirst();

        if (vCursorUsuarios.getCount() >0) {
            pedidoMinimo=vCursorUsuarios.getDouble(0);
        }


        String Consulta = "";
        String consultaEncabezado = "";
        GlobalVariables gGlobalVariables = GlobalVariables.getInstance();
        String vEmpresa=gGlobalVariables.getEmpresa();
        conbd.Variables();
        String mantisficc =  conbd.MantisFicc;
        String BloqueoCupo =  conbd.BloqueaCupo;
        String Remision = conbd.FiccRem;
        Cursor Clientes = null;
        Cursor PedidoEn = null;
        String resSql = "";
        String where = "";
        int bodega = 0;


        if(env == 1) {
            resSql = " and CliNoRee ='N' ";
        }
        if(nitsec.isEmpty()){
        }else{
            where = "and p.nitsec='"+nitsec+"' ";
        }

        if(clisec > 0){
            where += "and p.CliSec="+clisec+" ";
        }

        if(prefijo.isEmpty()) {
        } else{


            where += " and prefijo = '"+prefijo+"' ";
        }


        consultaEncabezado=  "select p.NitSec,ifNULL(c.CliSec,0) CliSec,ifNULL(c.NitCom,'') NitCom,ifNULL(c.CliNom,'') CliNom,ifNULL(prefijo,'') prefijo," +
                "    ifNull(ConPagnom, '') ConPagnom, LisPrecod, Nitcom,Clinom,ConPagnom,FacFecEnt from pedido p " +
                    "  left join clientes c on p.nitsec=c.nitsec and p.clisec=c.clisec " +
                    " where pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " " +
                    " and ifNULL(cant,0)+ifNULL(cantinf,0)<>0 "+where+" " +
                    " group by p.NitSec,p.CliSec,c.NitCom,c.CliNom,prefijo,ConPagnom,LisPrecod,Nitcom,Clinom,ConPagnom";

   Log.e("Encabezado: ",consultaEncabezado);
        try {
            PedidoEn = BaseDeDatos.getWritableDatabase().rawQuery(consultaEncabezado, null);
        } catch (Exception e) {
            int jj = 0;
        }


        String JsonEnvio = "";
        String LisPrecod = "";
        String FacFecEnt = "";
        String jNitSec  ="";
        Integer jClisec = 0;
        String NumPedido = "";

        if(PedidoEn.getCount() > 0) {
            PedidoEn.moveToFirst();
            do {
                String enNitSec = PedidoEn.getString(0);
                String enprefijo = PedidoEn.getString(4);
                LisPrecod = PedidoEn.getString(6);
                Integer enclisec = PedidoEn.getInt(1);
                FacFecEnt = PedidoEn.getString(10);
                NumPedido = enprefijo+ vUsuario.trim() + enNitSec + enclisec + time.year + "-" + (time.month + 1) + "-" + time.monthDay;

                Log.e("Entro gestor: lista ",String.valueOf(LisPrecod));
                String TipCod = "";
                if(conbd.TipoPedido.equalsIgnoreCase("REM")){
                    TipCod  = "RPE";
                }else{

                        TipCod  = "OPHG";

                }

                SimpleDateFormat sdf = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss.SSS",
                        Locale.getDefault()
                );
                String fechaFormateada = sdf.format(new Date());
                fechaFormateada = fechaFormateada.replace(" ", "---");
                String JsonDetalle = "";
                JsonEnvio = "";
                JsonEnvio = "[{\"PedNum\":  \"" + NumPedido + "\",\"TipCod\":  \""+TipCod+"\"," +
                        " \"PedFecha\":  \"" +fechaFormateada + "\",\"PedNitSec\":  \"" + PedidoEn.getString(0) + "\"," +
                        " \"PedCliSec\": \"" + PedidoEn.getInt(1) + "\",\"PedVenCod\": \"" + vUsuario + "\", \"PedPla\": \"$plazo$\"," +
                        " \"PedEst\": \"A\",\"PedObs\": \"$observacion\",\"FacFecEnt\":\""+FacFecEnt+"\"," +
                        " \"Detalle\": [";
                Consulta = "";

                String precio = "a.precio"+LisPrecod;



                Consulta = "select p.nitsec,p.clisec,p.artsec,p.vencod,cant,pdyear,pdmonth,pdday,precio,plazo,pordesc,pordesc2,pordesc3+pordesc6 pordesc3,pordesc4+pordesc5 pordesc4,ifnull(visobs,'') visobs,p.prefijo," +
                        " (ifnull(pordescno,0)+ifnull(pordesc2no,0)+ifnull(pordesc3no,0)+ifnull(pordesc4no,0)+ifnull(pordesc5no,0)+ifnull(pordesc6no,0))-(ifnull(pordesc,0)+ifnull(pordesc2,0)+ifnull(pordesc3,0)+ifnull(pordesc4,0)+ifnull(pordesc5,0)+ifnull(pordesc6,0)) descno," +
                        " confemp,confprov,confvend,cantcaj,ConNotCod,ifnull(VisLatitud,'') VisLatitud,ifnull(VisLongitud,'') VisLongitud,ifnull(VisHor,0) VisHor,ifnull(VisMin,0) VisMin," +
                        " ifnull(VisSeg,0) VisSeg,ifnull(VisHor,0) VisHorFin,ifnull(VisMin,0) VisMinFin,ifnull(VisSeg,0) VisSegFin,0 MovParPremSecLin, "+LisPrecod+" PedLisPreCod," +
                        " cantinf,0 cantcajinf,0 MovParPremtipo,0 MovParPremSec,bodcod,NotaInv,NotaCar, ifnull(p.PreArtCod,'') PreArtCod, " +
                        " ifnull(ConNotCod,'') ConNotCod, 'N' KarBonGen, 'N' KarBonificado   from pedido p " +
                        " left join articulos a on rtrim(a.artsec)=rtrim(p.artsec) " +
                        " left join clientes n on ltrim(rtrim(n.nitsec))=ltrim(rtrim(p.nitsec)) and n.clisec=p.clisec " +
                        " left join visita v on ltrim(rtrim(v.nitsec))=ltrim(rtrim(p.nitsec)) and v.clisec=p.clisec  and VisAno=pdyear and pdmonth=VisMes  and visdia=pdday and VisPref= p.prefijo" +
                        //" where a.precio1>0 and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay +
                        " where  p.nitsec = '"+enNitSec+"' and p.clisec = "+enclisec+" and prefijo = '"+enprefijo+"' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay   ;

                Consulta+=" union all " +
                        "   select MovParNitSec,MovParCliSec,m.MovParPremArtSec,'" + vUsuario + "' vencod,MovParPremCant cant,MovParPremAno,MovParPremMes," +
                        "  MovParPremDia,"+precio+" as precio,0 plazo,100 pordesc,0.0 pordesc2,0.0 pordesc3,0.0 pordesc4,'BON' obs,prefijo prefi,0.0,0.0,0.0,0.0,0,0,'','',0,0,0,0,0,0 ," +
                        " 0 MovParPremSecLin,"+LisPrecod+" PedLisPreCod,0 cantinf,0 cantcajinf,0 MovParPremtipo,0 MovParPremSec ,0 bodcod,'N' NotaInv,'N' NotaCar, " +
                        "  ifnull(BonParBonPreArtCod,'') PreArtCod, '' ConNotCod, ifnull(MovParPremApli,'N') KarBonGen, 'S' KarBonificado  from MovParPrem M " +
                        " left join articulos a on a.artsec=m.MovParPremArtSec " +
                        " where MovParPremCant > 0  and Prefijo='" + enprefijo + "' and MovParNitSec='" + enNitSec + "' and MovParCliSec=" + enclisec + " and MovParPremAno=" + time.year + " and MovParPremMes=" + (time.month + 1) + " and MovParPremDia=" + time.monthDay;


                Log.e("Consulta art",Consulta);
                try {

                    Clientes = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null);
                } catch (Exception e) {

                    Log.e("Consulta art",e.toString());
                    int jj = 0;
                }
                Log.e("Consulta Clientes.getCount()",String.valueOf(Clientes.getCount()));
                if (Clientes.getCount() > 0) {

                    Clientes.moveToFirst();
                    String Observacion ="";
                    int nplazo = 0;
                    int conta = 0;
                    Double TotalPedido = 0.0;
                    Double TotalCreditoMalo = 0.0;
                    Double TotalCreditoBueno= 0.0;
                    String CliBloCup = "N";



                    do {


                       SDTResumenPedidos resumen =
                                TotalesPedido(
                                        pContext,
                                        Clientes.getString(15),
                                        Clientes.getString(0),
                                        Clientes.getInt(1),
                                        "",
                                        "",
                                        ""
                                );




                        TotalPedido = resumen.Total;//TotalesPedido(pContext, Clientes.getString(15), Clientes.getString(0), Clientes.getInt(1), "", "", "").Total;
                        TotalCreditoMalo = resumen.CreditoMalo;//TotalesPedido(pContext, Clientes.getString(15), Clientes.getString(0), Clientes.getInt(1), "", "", "").CreditoMalo;
                        TotalCreditoBueno =resumen.CreditoBueno; //TotalesPedido(pContext, Clientes.getString(15), Clientes.getString(0), Clientes.getInt(1), "", "", "").CreditoBueno;



                        int sincupo = 0;

                        if (BloqueoCupo.equalsIgnoreCase("N")) {


                            GestorCartera gestorcartera = new GestorCartera();
                            gestorcartera.TotalesCatera(pContext, nitsec, clisec);
                            Integer CarteraGeneral = gestorcartera.CarteraGeneral;
                            String tmpNitSec = Clientes.getString(0);


                            jNitSec = Clientes.getString(0);
                            jClisec = Clientes.getInt(1);
                            Integer tmpCliSec = Clientes.getInt(1);

                            Cursor cursorclientes = BaseDeDatos.getReadableDatabase().rawQuery("select CliCup,CliConPag,CliBloCup from clientes where nitsec='" + tmpNitSec + "' and clisec=" + tmpCliSec, null);

                            Integer vuelta = 0;
                            Integer cupo = 0;
                            Integer CliConPag = 0;
                            if (cursorclientes.getCount() > 0) {
                                cursorclientes.moveToFirst();
                                do {
                                    cupo = cursorclientes.getInt(0);
                                    CliConPag = cursorclientes.getInt(1);
                                    CliBloCup = cursorclientes.getString(2);

                                } while (cursorclientes.moveToNext());
                            }
                            if (cupo - CarteraGeneral - TotalPedido.intValue() < 0 && (CliConPag != 0 && cupo != 0)) {
                                sincupo = 1;
                            }
                        }

                        if(CliBloCup.equalsIgnoreCase("S")){
                            if (sincupo != 0 ) {
                                sExistencia2 = "SIN CUPO v2> "+CliBloCup;
                                return sExistencia2;
                            }
                        }

                            Double CantUni = 0.00;
                            Double cantinf = 0.00;
                            Integer CantCajas = 0;
                            Integer cantcajinf = 0;
                            if ((pedidoMinimo != 0 && TotalPedido > pedidoMinimo) || pedidoMinimo == 0) {
                                CantUni = Clientes.getDouble(4);
                                CantCajas = Clientes.getInt(20);
                                cantinf = Clientes.getDouble(32);
                                cantcajinf = Clientes.getInt(33);
                            }


                            //Clientes.getString(3)

                            SDTResumenPedidos SDTResumenPedidos = new SDTResumenPedidos();
                            SDTResumenPedidos = TotalesPedido(pContext, Clientes.getString(15), Clientes.getString(0), Clientes.getInt(1), "", "", "");

                            Cursor pedidoen = BaseDeDatos.getWritableDatabase().rawQuery("select Obs,planpuente from Pedidoenc where prefijo = '"+Clientes.getString(15)+"' and nitsec = '" + Clientes.getString(0) + "' and clisec = " + Clientes.getInt(1) + " and  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  " /*and VisPref='" + Prefijo+"'"*/, null);
                            pedidoen.moveToFirst();
                            String PlanPuente = "N";
                            if (pedidoen.getCount() > 0) {
                                pedidoen.moveToFirst();
                                Observacion = pedidoen.getString(0);
                                PlanPuente = pedidoen.getString(1);
                            }



                            nplazo = Clientes.getInt(9);
                            int BonProSec = Clientes.getInt(35);
                            int BONPRODETLIN = Clientes.getInt(30);

                            String NotaInv = Clientes.getString(37);
                            String NotaCar = Clientes.getString(38);
                            String PreArtCod = Clientes.getString(39);
                            String ConNotCod = Clientes.getString(40);



                            String BonProTipo = Clientes.getString(34);

                            //  = precioesp(pContext,Clientes.getString(0),Clientes.getString(2), Clientes.getDouble(8) );

                            double precioNu = 0;
                            precioNu = 0;

                            String KarBonificado ="N";
                            String KarBonGen ="N";
                             KarBonificado = Clientes.getString(41);
                             KarBonGen = Clientes.getString(42);


                            String consultaart = "select precioesp from PreciosEspeciales where peNitSec = '" + nitsec + "' and peArtSec = '" + Clientes.getString(2).trim() + "'";
                            Cursor cursorart = BaseDeDatos.getWritableDatabase().rawQuery(consultaart, null);
                            cursorart.moveToFirst();
                            if (cursorart.getCount() > 0) {
                                cursorart.moveToFirst();
                                precioNu = cursorart.getDouble(0);
                            } else {
                                precioNu = Clientes.getDouble(8);
                            }

                            Double preciocal = precioNu;

                            if(Clientes.getInt(36)>0){
                                bodega = Clientes.getInt(36);
                            }
                            int ArtBodCod = Clientes.getInt(36);

                            if(ArtBodCod == 0){
                                ArtBodCod = bodega;

                            }



                            Observacion = Observacion.replaceAll("[^\\w ]+", "");








                                if(CantUni+cantinf >= 1 ){
                                    Log.e("LISTA LOGdet",LisPrecod);

                                    JsonDetalle += "{\"ArtSec\": \"" + Clientes.getString(2) + "\",\"PedUni\":\"" + CantUni + "\",\"KaruniCp\":\"" + cantinf + "\", \"PreArtCod\": \"" + PreArtCod + "\" ,  \"PedPrePub\": \"" + preciocal + "\"," +
                                            " \"PedKarValTotMenDes\":  \"" + (preciocal * CantUni) + "\" ,\"PedDesuno\": \"" + Clientes.getDouble(13) + "\", " +
                                            " \"PedDesDos\": \"" + Clientes.getDouble(11) + "\",\"PedDesTres\": \"" + Clientes.getDouble(12) + "\"," +
                                            "\"PedDesCua\": \"" + Clientes.getDouble(10) + "\", \"PedLisPrecod\": \"" + LisPrecod + "\",\"PedBodSucccSec\": \"" + ArtBodCod + "\"," +
                                            "\"NotaInv\": \"" + NotaInv+ "\",\"NotaCar\": \"" + NotaCar + "\", \"ConNotCod\": \"" + ConNotCod + "\"" +
                                            " , \"KarBonGen\": \"" + KarBonGen + "\", \"KarBonificado\": \"" + KarBonificado + "\"},";
                                    Log.e("JsonDetalle: ",JsonDetalle);
                                }




                    } while (Clientes.moveToNext());

                    JsonEnvio += JsonDetalle + " ] } ]";
                    JsonEnvio = JsonEnvio.replace(", ]", "]");
                    JsonEnvio = JsonEnvio.replace(" ", "");
                    JsonEnvio = JsonEnvio.replace("---", " ");
                    JsonEnvio = JsonEnvio.replace("$observacion", Observacion);
                    JsonEnvio = JsonEnvio.replace("$plazo$", String.valueOf(nplazo).trim());
                    JsonEnvio = JsonEnvio.trim();
                    JsonDetalle  ="";

                    Log.e("JsonEnvio: ",JsonEnvio);

                    String sql = conbd.UrlEnvio;
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    Connection conn = null;
                    Statement comm =null;
                    ResultSet rsClientes  = null;
                    String nerror = "";
                    conn = conbd.CargarConexion(pContext);
                    try{
                        comm = conn.createStatement();


                    String xsql =
                                "MERGE PedidoPendiente AS tgt " +
                                        "USING (SELECT " +
                                        " ? AS pedPenNro, " +
                                        " ? AS pedPenJSON, " +
                                        " ? AS pedPenStatus, " +
                                        " ? AS pedPenValor, " +
                                        " ? AS pedPenValorNotMal, " +
                                        " ? AS pedPenValorNotBue, " +
                                        " ? AS pedPenNitSec, " +
                                        " ? AS  pedPenCliSec, " +
                                        " ? AS pedPenTipo) AS src " +
                                        "ON tgt.pedPenNro = src.pedPenNro " +
                                        "WHEN MATCHED THEN UPDATE SET " +
                                        "tgt.pedPenFechmod = GETDATE(), " +
                                        "tgt.pedPenJSON = src.pedPenJSON, " +
                                        "tgt.pedPenStatus = src.pedPenStatus, " +
                                        "tgt.pedPenValor = src.pedPenValor, " +
                                        "tgt.pedPenValorNotMal = src.pedPenValorNotMal, " +
                                        "tgt.pedPenValorNotBue = src.pedPenValorNotBue, " +
                                        "tgt.pedPenNitSec = src.pedPenNitSec, " +
                                        "tgt.pedPenCliSec = src.pedPenCliSec, " +
                                        "tgt.pedPenTipo = src.pedPenTipo " +
                                        "WHEN NOT MATCHED THEN INSERT " +
                                        "(pedPenNro, pedPenFechor, pedPenFechmod, pedPenJSON, pedPenStatus,pedPenValor,pedPenValorNotMal,pedPenValorNotBue,pedPenNitSec,pedPenCliSec,pedPenTipo) " +
                                        "VALUES " +
                                        "(src.pedPenNro, GETDATE(), GETDATE(), src.pedPenJSON, src.pedPenStatus,src.pedPenValor,src.pedPenValorNotMal,src.pedPenValorNotBue,src.pedPenNitSec,src.pedPenCliSec,src.pedPenTipo);";

                        PreparedStatement ps = conn.prepareStatement(xsql);
                        ps.setString(1, NumPedido);
                        ps.setString(2, JsonEnvio);
                        ps.setString(3,"P");
                        ps.setDouble(4,TotalPedido);
                        ps.setDouble(5,TotalCreditoMalo);
                        ps.setDouble(6,TotalCreditoBueno);
                        ps.setString(7,PedidoEn.getString(0));
                        ps.setInt(8,PedidoEn.getInt(1));
                        ps.setString(9,conbd.TipoPedido);

                       // ps.executeUpdate();

                      int filas =  ps.executeUpdate();
            if(filas > 0){
                 mensajePed += "\n Pedido: "+NumPedido+" Generado ";

                 actualizarestado(pContext,"",0,vUsuario,NumPedido,"S");
             }



                    //int actualizo = comm.executeUpdate(JsonEnvio);

                    }catch (Exception e){
                        mensajePed += "conexion try "+e.toString();
                    }




                }

            } while (PedidoEn.moveToNext());
        }

        if(MantisFicc.equalsIgnoreCase("S")){

            Cursor visita = null;

            if(nitsec.isEmpty()){
            }else{
                where = "and NitSec='"+nitsec+"'";
            }

            String consultaVis = "Select VenCod,NitSec,CliSec,VisObs,VisAno,VisMes,VisDia,VisHor,VisMin,VisSeg,VisHorFin,VisMinFin,VisSegFin,VisFotoimg from Visita where VisAno = "+time.year+" and VisMes = "+(time.month + 1) +" and VisDia ="+time.monthDay+" "+where+" ";


            try {
                visita = BaseDeDatos.getWritableDatabase().rawQuery(consultaVis, null);
            } catch (Exception e) {

                int jj = 0;
            }

            if(visita.getCount() > 0){
                visita.moveToFirst();
                do{
                    String VisPedVenCod = vUsuario;
                    String visPedNitsec = visita.getString(1);
                    int visPedCliSec = visita.getInt(2);
                    String VisPedObs = visita.getString(3) ;
                    int VisPedHor = visita.getInt(7) ;
                    int VisPedMin = visita.getInt(8);
                    int VisPedSeg = visita.getInt(9)  ;
                    int VisPedAno = visita.getInt(4) ;
                    int VisPedMes = visita.getInt(5) ;
                    int ViPedDia = visita.getInt(6) ;
                    int VisPedHorFin = visita.getInt(10) ;
                    int VisPedMinFin = visita.getInt(11) ;
                    int VisSegFin = visita.getInt(12) ;


                    String Path = visita.getString(13) ;
                    String VisPedImg = "";
                    ///   CliTempImg64 = CliTempImg64.replace("\n","");
                    Log.e("VisPedImg : ",Path);
                    if(Path.contains("/storage/emulated/")){
                        File bitmapFile = new File(Path);
                        Bitmap bitmap = BitmapFactory.decodeFile(bitmapFile.toString());
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        VisPedImg = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
                        VisPedImg = VisPedImg.replace("\n","");
                    }




                    String VisPedNum = "VIS"+VisPedVenCod+visPedNitsec+visPedCliSec+VisPedAno+"-"+VisPedMes+"-"+ViPedDia;


                    String jsonvisita = "{\"Visita\": {\"VisPedNum\":\""+VisPedNum+"\",\"VisPedVenCod\":\""+VisPedVenCod+"\",\"VisPedNitsec\":\""+visPedNitsec+"\",\"VisPedclisec\":\""+visPedCliSec+"\",\"VisPedObs\":\""+VisPedObs+"\",\"VisPedHor\":\""+VisPedHor+"\", " +
                            " \"VisPedMin\":\""+VisPedMin+"\",\"VisPedSeg\":\""+VisPedSeg+"\",\"VisPedAno\":\""+VisPedAno+"\", \"VisPedMes\":\""+VisPedMes+"\",\"ViPedDia\":\""+ViPedDia+"\",\"VisPedHorFin\":\""+VisPedHorFin+"\",\"VisPedMinFin\":\""+VisPedMinFin+"\", " +
                            " \"VisPedSegFin\":\""+VisSegFin+"\",\"VisPedImg\":\""+VisPedImg+"\" " +
                            "}}";


                    Log.e("jsonvisita", jsonvisita);
                    conbd.Variables();
                    String sql = conbd.UrlEnvioVisita;
                    // sExistencia = "0.0";
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    URL url = null;
                    HttpURLConnection conn;
                    try {
                        url = new URL(sql);
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestProperty("Content-Type", "application/json");//; utf-8
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.setRequestMethod("POST");
                        conn.setConnectTimeout(5000);
                        StringBuilder result = new StringBuilder();
                        result.append(jsonvisita);
                        OutputStream os = conn.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
                        writer.write(result.toString());
                        writer.flush();
                        writer.close();
                        os.close();
                        conn.connect();
                        int statusCode = conn.getResponseCode();
                        InputStream inputstream = null;
                        if (statusCode >= 200 && statusCode < 400) {
                            // Create an InputStream in order to extract the response object
                            inputstream = conn.getInputStream();
                        } else {
                            inputstream = conn.getErrorStream();
                        }

                        BufferedReader in = new BufferedReader(new InputStreamReader(inputstream));
                        String inputLine;
                        StringBuffer response = new StringBuffer();
                        String json = "";

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        json = "[" + response.toString() + "]";
                        JSONArray jsonArr = null;
                        jsonArr = new JSONArray(json);
                        String mensaje = "";



                        for (int i = 0; i < jsonArr.length(); i++) {
                            JSONObject jsonObject = jsonArr.getJSONObject(i);
                            sExistencia = jsonObject.optString("Mensaje");
                            Log.e("SEcistneics",jsonObject.toString());
                            if(sExistencia.equalsIgnoreCase("S")){
                                sExistencia2 += "Se enviaron las visitas correctamente";
                            }else{
                                sExistencia2= sExistencia;
                            }
                            sExistencia2= sExistencia;
                        }





                    } catch (MalformedURLException e) {
                        sExistencia2 +=  e.getMessage();
                        e.printStackTrace();
                        Log.e("MalformedURLExceptvis",sExistencia2);
                    } catch (IOException e) {
                        sExistencia2 +=  e.getMessage();
                        Log.e("MIOExceptiontvis",sExistencia2);

                        e.printStackTrace();
                    } catch (JSONException e) {
                        sExistencia2 +=  e.getMessage();
                        Log.e("JSONExceptionvis",sExistencia2);

                        e.printStackTrace();
                    }

                }while (visita.moveToNext());
            }

        }


        return mensajePed;

    }

    public String EnviarPedidosFiccGx5Json(Context pContext, String prefijo, String nitsec, Integer clisec, Integer env) {
        Log.e("Entro ´pedido",prefijo);
        ConBd conbd = new ConBd();
        // Connection connGen = conbd.CargarConexion();
        conbd.Variables();
        String MantisFicc = conbd.MantisFicc;
        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();
        Integer vPedidoMinimo=vGlobalVariables.getParMovSec();

        Time time = new Time();
        time.setToNow();


        if(dayOfMonth > 0){
            time.set(time.second, time.minute, time.hour,dayOfMonth , month, year);
        }



        String sExistencia="";
        String sExistencia2="";
        String mensajePed="";
        String NumPed = prefijo+nitsec + clisec +time.year + (time.month + 1) + time.monthDay;

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 5);


        Double pedidoMinimo=0.0;

        Cursor vCursorUsuarios = BaseDeDatos.getReadableDatabase().rawQuery("select ParMovPedMin from usuarios where VenCnt=1  ", null);
        vCursorUsuarios.moveToFirst();

        if (vCursorUsuarios.getCount() >0) {
            pedidoMinimo=vCursorUsuarios.getDouble(0);
        }


        String Consulta = "";
        String consultaEncabezado = "";
        GlobalVariables gGlobalVariables = GlobalVariables.getInstance();
        String vEmpresa=gGlobalVariables.getEmpresa();
        conbd.Variables();
        String mantisficc =  conbd.MantisFicc;
        String BloqueoCupo =  conbd.BloqueaCupo;
        String Remision = conbd.FiccRem;
        Cursor Clientes = null;
        Cursor PedidoEn = null;
        String resSql = "";
        String where = "";
        int bodega = 0;


        if(env == 1) {
            resSql = " and CliNoRee ='N' ";
        }
        if(nitsec.isEmpty()){
        }else{
            where = "and p.nitsec='"+nitsec+"' ";
        }

        if(clisec > 0){
            where += "and p.CliSec="+clisec+" ";
        }

        if(prefijo.isEmpty()) {
        } else{


            where += " and prefijo = '"+prefijo+"' ";
        }


        consultaEncabezado=  "select p.NitSec,ifNULL(c.CliSec,0) CliSec,ifNULL(c.NitCom,'') NitCom,ifNULL(c.CliNom,'') CliNom,ifNULL(prefijo,'') prefijo, " +
                " ifNull(ConPagnom, '') ConPagnom, LisPrecod, Nitcom,Clinom,ConPagnom,FacFecEnt from pedido p " +
                "  left join clientes c on p.nitsec=c.nitsec and p.clisec=c.clisec " +
                " where pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " " +
                " and ifNULL(cant,0)+ifNULL(cantinf,0)<>0 "+where+" " +
                " group by p.NitSec,p.CliSec,c.NitCom,c.CliNom,prefijo,ConPagnom,LisPrecod,Nitcom,Clinom,ConPagnom";

        Log.e("Encabezado: ",consultaEncabezado);
        try {
            PedidoEn = BaseDeDatos.getWritableDatabase().rawQuery(consultaEncabezado, null);
        } catch (Exception e) {
            int jj = 0;
        }


        String JsonEnvio = "";
        String LisPrecod = "";
        String jNitSec  ="";
        Integer jClisec = 0;
        String NumPedido = "";

        if(PedidoEn.getCount() > 0) {
            PedidoEn.moveToFirst();
            do {
                String enNitSec = PedidoEn.getString(0);
                String enprefijo = PedidoEn.getString(4);
                LisPrecod = PedidoEn.getString(6);
                Integer enclisec = PedidoEn.getInt(1);
                String FacFecEnt = PedidoEn.getString(10);
                NumPedido = enprefijo+ vUsuario.trim() + enNitSec + enclisec + time.year + "-" + (time.month + 1) + "-" + time.monthDay;

                Log.e("Entro gestor: lista ",String.valueOf(LisPrecod));
                String TipCod = "";
                if(conbd.TipoPedido.equalsIgnoreCase("REM")){
                    TipCod  = "RPE";
                }else{

                    TipCod  = "OPHG";

                }

                SimpleDateFormat sdf = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss.SSS",
                        Locale.getDefault()
                );
                String fechaFormateada = sdf.format(new Date());
                fechaFormateada = fechaFormateada.replace(" ", "---");
                String JsonDetalle = "";
                JsonEnvio = "";
                JsonEnvio = "[{\"PedNum\":  \"" + NumPedido + "\",\"TipCod\":  \""+TipCod+"\"," +
                        " \"PedFecha\":  \"" +fechaFormateada + "\",\"PedNitSec\":  \"" + PedidoEn.getString(0) + "\"," +
                        " \"PedCliSec\": \"" + PedidoEn.getInt(1) + "\",\"PedVenCod\": \"" + vUsuario + "\", \"PedPla\": \"$plazo$\"," +
                        " \"PedEst\": \"A\",\"PedObs\": \"$observacion\",\"FacFecEnt\":\""+FacFecEnt+"\"," +
                        " \"Detalle\": [";


                Consulta = "";

                String precio = "a.precio"+LisPrecod;



                Consulta = "select p.nitsec,p.clisec,p.artsec,p.vencod,cant,pdyear,pdmonth,pdday,precio,plazo,pordesc,pordesc2,pordesc3+pordesc6 pordesc3,pordesc4+pordesc5 pordesc4,ifnull(visobs,'') visobs,p.prefijo," +
                        " (ifnull(pordescno,0)+ifnull(pordesc2no,0)+ifnull(pordesc3no,0)+ifnull(pordesc4no,0)+ifnull(pordesc5no,0)+ifnull(pordesc6no,0))-(ifnull(pordesc,0)+ifnull(pordesc2,0)+ifnull(pordesc3,0)+ifnull(pordesc4,0)+ifnull(pordesc5,0)+ifnull(pordesc6,0)) descno," +
                        " confemp,confprov,confvend,cantcaj,ConNotCod,ifnull(VisLatitud,'') VisLatitud,ifnull(VisLongitud,'') VisLongitud,ifnull(VisHor,0) VisHor,ifnull(VisMin,0) VisMin," +
                        " ifnull(VisSeg,0) VisSeg,ifnull(VisHor,0) VisHorFin,ifnull(VisMin,0) VisMinFin,ifnull(VisSeg,0) VisSegFin,0 MovParPremSecLin, "+LisPrecod+" PedLisPreCod," +
                        " cantinf,0 cantcajinf,0 MovParPremtipo,0 MovParPremSec,bodcod,NotaInv,NotaCar, ifnull(p.PreArtCod,'') PreArtCod, " +
                        " ifnull(ConNotCod,'') ConNotCod, 'N' KarBonGen, 'N' KarBonificado   from pedido p " +
                        " left join articulos a on rtrim(a.artsec)=rtrim(p.artsec) " +
                        " left join clientes n on ltrim(rtrim(n.nitsec))=ltrim(rtrim(p.nitsec)) and n.clisec=p.clisec " +
                        " left join visita v on ltrim(rtrim(v.nitsec))=ltrim(rtrim(p.nitsec)) and v.clisec=p.clisec  and VisAno=pdyear and pdmonth=VisMes  and visdia=pdday and VisPref= p.prefijo" +
                        //" where a.precio1>0 and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay +
                        " where  p.nitsec = '"+enNitSec+"' and p.clisec = "+enclisec+" and prefijo = '"+enprefijo+"' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay   ;

                Consulta+=" union all " +
                        "   select MovParNitSec,MovParCliSec,m.MovParPremArtSec,'" + vUsuario + "' vencod,MovParPremCant cant,MovParPremAno,MovParPremMes," +
                        "  MovParPremDia,"+precio+",0 plazo,0 pordesc,0.0 pordesc2,0.0 pordesc3,100 pordesc4,'BON' obs,prefijo prefi,0.0,0.0,0.0,0.0,0,0,'','',0,0,0,0,0,0 ," +
                        " 0 MovParPremSecLin,"+LisPrecod+" PedLisPreCod,0 cantinf,0 cantcajinf,0 MovParPremtipo,0 MovParPremSec ,0 bodcod,'N' NotaInv,'N' NotaCar, " +
                        "  ifnull(BonParBonPreArtCod,'') PreArtCod, '' ConNotCod, ifnull(MovParPremApli,'N') KarBonGen, 'S' KarBonificado  from MovParPrem M " +
                        " left join articulos a on a.artsec=m.MovParPremArtSec " +
                        " where MovParPremCant > 0  and Prefijo='" + enprefijo + "' and MovParNitSec='" + enNitSec + "' and MovParCliSec=" + enclisec + " and MovParPremAno=" + time.year + " and MovParPremMes=" + (time.month + 1) + " and MovParPremDia=" + time.monthDay;


                Log.e("Consulta art",Consulta);
                try {

                    Clientes = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null);
                } catch (Exception e) {

                    Log.e("Consulta art",e.toString());
                    int jj = 0;
                }
                Log.e("Consulta Clientes.getCount()",String.valueOf(Clientes.getCount()));
                if (Clientes.getCount() > 0) {

                    Clientes.moveToFirst();
                    String Observacion ="";
                    int nplazo = 0;
                    int conta = 0;
                    Double TotalPedido = 0.0;
                    Double TotalCreditoMalo = 0.0;
                    Double TotalCreditoBueno= 0.0;
                    String CliBloCup = "N";



                    do {


                        SDTResumenPedidos resumen =
                                TotalesPedido(
                                        pContext,
                                        Clientes.getString(15),
                                        Clientes.getString(0),
                                        Clientes.getInt(1),
                                        "",
                                        "",
                                        ""
                                );




                        TotalPedido = resumen.Total;//TotalesPedido(pContext, Clientes.getString(15), Clientes.getString(0), Clientes.getInt(1), "", "", "").Total;
                        TotalCreditoMalo = resumen.CreditoMalo;//TotalesPedido(pContext, Clientes.getString(15), Clientes.getString(0), Clientes.getInt(1), "", "", "").CreditoMalo;
                        TotalCreditoBueno =resumen.CreditoBueno; //TotalesPedido(pContext, Clientes.getString(15), Clientes.getString(0), Clientes.getInt(1), "", "", "").CreditoBueno;



                        int sincupo = 0;

                        if (BloqueoCupo.equalsIgnoreCase("N")) {


                            GestorCartera gestorcartera = new GestorCartera();
                            gestorcartera.TotalesCatera(pContext, nitsec, clisec);
                            Integer CarteraGeneral = gestorcartera.CarteraGeneral;
                            String tmpNitSec = Clientes.getString(0);


                            jNitSec = Clientes.getString(0);
                            jClisec = Clientes.getInt(1);
                            Integer tmpCliSec = Clientes.getInt(1);

                            Cursor cursorclientes = BaseDeDatos.getReadableDatabase().rawQuery("select CliCup,CliConPag,CliBloCup from clientes where nitsec='" + tmpNitSec + "' and clisec=" + tmpCliSec, null);

                            Integer vuelta = 0;
                            Integer cupo = 0;
                            Integer CliConPag = 0;
                            if (cursorclientes.getCount() > 0) {
                                cursorclientes.moveToFirst();
                                do {
                                    cupo = cursorclientes.getInt(0);
                                    CliConPag = cursorclientes.getInt(1);
                                    CliBloCup = cursorclientes.getString(2);

                                } while (cursorclientes.moveToNext());
                            }
                            if (cupo - CarteraGeneral - TotalPedido.intValue() < 0 && (CliConPag != 0 && cupo != 0)) {
                                sincupo = 1;
                            }
                        }

                        if(CliBloCup.equalsIgnoreCase("S")){
                            if (sincupo != 0 ) {
                                sExistencia2 = "SIN CUPO v2> "+CliBloCup;
                                return sExistencia2;
                            }
                        }

                        Double CantUni = 0.00;
                        Double cantinf = 0.00;
                        Integer CantCajas = 0;
                        Integer cantcajinf = 0;
                        if ((pedidoMinimo != 0 && TotalPedido > pedidoMinimo) || pedidoMinimo == 0) {
                            CantUni = Clientes.getDouble(4);
                            CantCajas = Clientes.getInt(20);
                            cantinf = Clientes.getDouble(32);
                            cantcajinf = Clientes.getInt(33);
                        }


                        //Clientes.getString(3)

                        SDTResumenPedidos SDTResumenPedidos = new SDTResumenPedidos();
                        SDTResumenPedidos = TotalesPedido(pContext, Clientes.getString(15), Clientes.getString(0), Clientes.getInt(1), "", "", "");

                        Cursor pedidoen = BaseDeDatos.getWritableDatabase().rawQuery("select Obs,planpuente from Pedidoenc where prefijo = '"+Clientes.getString(15)+"' and nitsec = '" + Clientes.getString(0) + "' and clisec = " + Clientes.getInt(1) + " and  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  " /*and VisPref='" + Prefijo+"'"*/, null);
                        pedidoen.moveToFirst();
                        String PlanPuente = "N";
                        if (pedidoen.getCount() > 0) {
                            pedidoen.moveToFirst();
                            Observacion = pedidoen.getString(0);
                            PlanPuente = pedidoen.getString(1);
                        }



                        nplazo = Clientes.getInt(9);
                        int BonProSec = Clientes.getInt(35);
                        int BONPRODETLIN = Clientes.getInt(30);

                        String NotaInv = Clientes.getString(37);
                        String NotaCar = Clientes.getString(38);
                        String PreArtCod = Clientes.getString(39);
                        String ConNotCod = Clientes.getString(40);



                        String BonProTipo = Clientes.getString(34);

                        //  = precioesp(pContext,Clientes.getString(0),Clientes.getString(2), Clientes.getDouble(8) );

                        double precioNu = 0;
                        precioNu = 0;

                        String KarBonificado ="N";
                        String KarBonGen ="N";
                        KarBonificado = Clientes.getString(41);
                        KarBonGen = Clientes.getString(42);


                        String consultaart = "select precioesp from PreciosEspeciales where peNitSec = '" + nitsec + "' and peArtSec = '" + Clientes.getString(2).trim() + "'";
                        Cursor cursorart = BaseDeDatos.getWritableDatabase().rawQuery(consultaart, null);
                        cursorart.moveToFirst();
                        if (cursorart.getCount() > 0) {
                            cursorart.moveToFirst();
                            precioNu = cursorart.getDouble(0);
                        } else {
                            precioNu = Clientes.getDouble(8);
                        }

                        Double preciocal = precioNu;

                        if(Clientes.getInt(36)>0){
                            bodega = Clientes.getInt(36);
                        }
                        int ArtBodCod = Clientes.getInt(36);

                        if(ArtBodCod == 0){
                            ArtBodCod = bodega;

                        }



                        Observacion = Observacion.replaceAll("[^\\w ]+", "");








                        if(CantUni+cantinf >= 1 ){
                            Log.e("LISTA LOGdet",LisPrecod);

                            JsonDetalle += "{\"ArtSec\": \"" + Clientes.getString(2) + "\",\"PedUni\":\"" + CantUni + "\",\"KaruniCp\":\"" + cantinf + "\", \"PreArtCod\": \"" + PreArtCod + "\" ,  \"PedPrePub\": \"" + preciocal + "\"," +
                                    " \"PedKarValTotMenDes\":  \"" + (preciocal * CantUni) + "\" ,\"PedDesuno\": \"" + Clientes.getDouble(13) + "\", " +
                                    " \"PedDesDos\": \"" + Clientes.getDouble(11) + "\",\"PedDesTres\": \"" + Clientes.getDouble(12) + "\"," +
                                    "\"PedDesCua\": \"" + Clientes.getDouble(10) + "\", \"PedLisPrecod\": \"" + LisPrecod + "\",\"PedBodSucccSec\": \"" + ArtBodCod + "\"," +
                                    "\"NotaInv\": \"" + NotaInv+ "\",\"NotaCar\": \"" + NotaCar + "\", \"ConNotCod\": \"" + ConNotCod + "\"" +
                                    " , \"KarBonGen\": \"" + KarBonGen + "\", \"KarBonificado\": \"" + KarBonificado + "\"},";
                            Log.e("JsonDetalle: ",JsonDetalle);
                        }




                    } while (Clientes.moveToNext());

                    JsonEnvio += JsonDetalle + " ] } ]";
                    JsonEnvio = JsonEnvio.replace(", ]", "]");
                    JsonEnvio = JsonEnvio.replace(" ", "");
                    JsonEnvio = JsonEnvio.replace("---", " ");
                    JsonEnvio = JsonEnvio.replace("$observacion", Observacion);
                    JsonEnvio = JsonEnvio.replace("$plazo$", String.valueOf(nplazo).trim());
                    JsonEnvio = JsonEnvio.trim();
                    JsonDetalle  ="";

                    Log.e("JsonEnvio: ",JsonEnvio);

                    String sql = conbd.UrlEnvio;
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    Connection conn = null;
                    Statement comm =null;
                    ResultSet rsClientes  = null;
                    String nerror = "";
                    conn = conbd.CargarConexion(pContext);
                    try{
                        comm = conn.createStatement();


                        String xsql =
                                "MERGE PedidoPendiente AS tgt " +
                                        "USING (SELECT " +
                                        " ? AS pedPenNro, " +
                                        " ? AS pedPenJSON, " +
                                        " ? AS pedPenStatus, " +
                                        " ? AS pedPenValor, " +
                                        " ? AS pedPenValorNotMal, " +
                                        " ? AS pedPenValorNotBue, " +
                                        " ? AS pedPenNitSec, " +
                                        " ? AS  pedPenCliSec, " +
                                        " ? AS pedPenTipo) AS src " +
                                        "ON tgt.pedPenNro = src.pedPenNro " +
                                        "WHEN MATCHED THEN UPDATE SET " +
                                        "tgt.pedPenFechmod = GETDATE(), " +
                                        "tgt.pedPenJSON = src.pedPenJSON, " +
                                        "tgt.pedPenStatus = src.pedPenStatus, " +
                                        "tgt.pedPenValor = src.pedPenValor, " +
                                        "tgt.pedPenValorNotMal = src.pedPenValorNotMal, " +
                                        "tgt.pedPenValorNotBue = src.pedPenValorNotBue, " +
                                        "tgt.pedPenNitSec = src.pedPenNitSec, " +
                                        "tgt.pedPenCliSec = src.pedPenCliSec, " +
                                        "tgt.pedPenTipo = src.pedPenTipo " +
                                        "WHEN NOT MATCHED THEN INSERT " +
                                        "(pedPenNro, pedPenFechor, pedPenFechmod, pedPenJSON, pedPenStatus,pedPenValor,pedPenValorNotMal,pedPenValorNotBue,pedPenNitSec,pedPenCliSec,pedPenTipo) " +
                                        "VALUES " +
                                        "(src.pedPenNro, GETDATE(), GETDATE(), src.pedPenJSON, src.pedPenStatus,src.pedPenValor,src.pedPenValorNotMal,src.pedPenValorNotBue,src.pedPenNitSec,src.pedPenCliSec,src.pedPenTipo);";

                       /* PreparedStatement ps = conn.prepareStatement(xsql);
                        ps.setString(1, NumPedido);
                        ps.setString(2, JsonEnvio);
                        ps.setString(3,"P");
                        ps.setDouble(4,TotalPedido);
                        ps.setDouble(5,TotalCreditoMalo);
                        ps.setDouble(6,TotalCreditoBueno);
                        ps.setString(7,PedidoEn.getString(0));
                        ps.setInt(8,PedidoEn.getInt(1));
                        ps.setString(9,conbd.TipoPedido);*/

                        // ps.executeUpdate();

                       // int filas =  ps.executeUpdate();

                            mensajePed = JsonEnvio;




                        //int actualizo = comm.executeUpdate(JsonEnvio);

                    }catch (Exception e){
                        mensajePed += "conexion try "+e.toString();
                    }




                }

            } while (PedidoEn.moveToNext());
        }
















        return mensajePed;

    }


    public String EnviarPedidosTexto(Context pContext, String prefijo, String nitsec, Integer clisec, Integer env) {

        ConBd conbd = new ConBd();
        conbd.Variables();
        String MantisFicc = conbd.MantisFicc;
        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();
        Integer vPedidoMinimo=vGlobalVariables.getParMovSec();

        Time time = new Time();
        time.setToNow();

        String sExistencia="";
        String sExistencia2="";
        String mensajePed="";
        String NumPed = prefijo+nitsec + clisec +time.year + (time.month + 1) + time.monthDay;

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 5);

        DecimalFormat decimalFormat = new DecimalFormat("#,###.00");

        Double pedidoMinimo=0.0;

        Cursor vCursorUsuarios = BaseDeDatos.getReadableDatabase().rawQuery("select ParMovPedMin from usuarios where VenCnt=1  ", null);
        vCursorUsuarios.moveToFirst();

        if (vCursorUsuarios.getCount() >0) {
            pedidoMinimo=vCursorUsuarios.getDouble(0);
        }
        Double TotalPed = 0.0;
        String JsonDetalle = "";
        String Consulta = "";
        String consultaEncabezado = "";
        GlobalVariables gGlobalVariables = GlobalVariables.getInstance();
        String vEmpresa=gGlobalVariables.getEmpresa();
        conbd.Variables();
        String mantisficc =  conbd.MantisFicc;
        String BloqueoCupo =  conbd.BloqueaCupo;
        String Remision = conbd.FiccRem;
        Cursor Clientes = null;
        Cursor PedidoEn = null;
        String resSql = "";
        String where = "";
        if(env == 1) {
            resSql = " and CliNoRee ='N' ";
        }
        if(nitsec.isEmpty()){
        }else{
            where = "and p.nitsec='"+nitsec+"'";
        }


        if(clisec > 0){
            where += "and p.CliSec="+clisec+" ";
        }


        Log.e("WHRE: ",where);


         if(vEmpresa.equalsIgnoreCase("SUHOGAR")){
             consultaEncabezado=  "select p.NitSec,ifNULL(c.CliSec,0) CliSec,ifNULL(c.NitCom,'') NitCom,ifNULL(c.CliNom,'') CliNom,ifNULL(prefijo,'') prefijo, ifNull(ConPagnom, '') ConPagnom, pedLisPrecod, Nitcom,Clinom,ConPagnom from pedido p left join clientes c on p.nitsec=c.nitsec and p.clisec=c.clisec " +
                     " where pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and ifNULL(cant,0)+ifNULL(cantcaj,0)<>0 "+where+" group by p.NitSec,p.CliSec,c.NitCom,c.CliNom,prefijo,ConPagnom,LisPrecod,Nitcom,Clinom,ConPagnom";

         }else {
             consultaEncabezado=  "select p.NitSec,ifNULL(c.CliSec,0) CliSec,ifNULL(c.NitCom,'') NitCom,ifNULL(c.CliNom,'') CliNom,ifNULL(prefijo,'') prefijo, ifNull(ConPagnom, '') ConPagnom, LisPrecod, Nitcom,Clinom,ConPagnom from pedido p left join clientes c on p.nitsec=c.nitsec and p.clisec=c.clisec " +
                     " where pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and ifNULL(cant,0)+ifNULL(cantcaj,0)<>0 "+where+" group by p.NitSec,p.CliSec,c.NitCom,c.CliNom,prefijo,ConPagnom,LisPrecod,Nitcom,Clinom,ConPagnom";

         }



        try {
            PedidoEn = BaseDeDatos.getWritableDatabase().rawQuery(consultaEncabezado, null);
        } catch (Exception e) {

            int jj = 0;
        }



        String JsonEnvio = "";
        String LisPrecod = "";
        String jNitSec  ="";
        Integer jClisec = 0;
        String NumPedido = "";

        if(PedidoEn.getCount() > 0) {
            PedidoEn.moveToFirst();
            do {
                String enNitSec = PedidoEn.getString(0);
                String enprefijo = PedidoEn.getString(4);
                String NitCom = PedidoEn.getString(7);
                String Clinom = PedidoEn.getString(8);
                String ConPagnom = PedidoEn.getString(9);


                LisPrecod = PedidoEn.getString(6);
                Integer enclisec = PedidoEn.getInt(1);
                NumPedido = enprefijo+ vUsuario.trim() + enNitSec + enclisec + time.year + "-" + (time.month + 1) + "-" + time.monthDay;


                String TipCod = "";
                if(Remision.equalsIgnoreCase("S")){
                    TipCod  = "RPE";
                }else{
                    if(vEmpresa.equalsIgnoreCase("INDULAC")){
                        TipCod  = "PEMO";
                    }else{
                        TipCod  = "OPHG";
                    }
                }


                JsonDetalle = "*PEDIDO N° "+NumPedido+"* \n";
                JsonDetalle += "● *Nombre cliente:* "+Clinom+"\n";
                JsonDetalle += "● *Plazo:* "+ConPagnom+"\n";
                JsonDetalle += "● *Observaciones:* $observacion"+"\n";
                JsonDetalle += "● $totalpedido$"+"\n";
                JsonDetalle += "===========*Artículos*:  ====================="+"\n";

                String Articulos  ="";
                String precio = "a.precio"+LisPrecod;



                Consulta = "select p.nitsec,p.clisec,p.artsec,p.vencod,cant,pdyear,pdmonth,pdday,"+precio+",plazo,pordesc,pordesc2,pordesc3+pordesc6 pordesc3,pordesc4+pordesc5 pordesc4,ifnull(visobs,'') visobs,p.prefijo,(ifnull(pordescno,0)+ifnull(pordesc2no,0)+ifnull(pordesc3no,0)+ifnull(pordesc4no,0)+ifnull(pordesc5no,0)+ifnull(pordesc6no,0))-(ifnull(pordesc,0)+ifnull(pordesc2,0)+ifnull(pordesc3,0)+ifnull(pordesc4,0)+ifnull(pordesc5,0)+ifnull(pordesc6,0)) descno,confemp,confprov,confvend,cantcaj,ConNotCod,ifnull(VisLatitud,'') VisLatitud,ifnull(VisLongitud,'') VisLongitud,ifnull(VisHor,0) VisHor,ifnull(VisMin,0) VisMin,ifnull(VisSeg,0) VisSeg,ifnull(VisHor,0) VisHorFin,ifnull(VisMin,0) VisMinFin,ifnull(VisSeg,0) VisSegFin,0 MovParPremSecLin,1 PedLisPreCod,0 cantinf,0 cantcajinf,0 MovParPremtipo,0 MovParPremSec,bodcod,ArtCod,ArtNom,case  cliiva when 'S' then ParConIva else 0 end ParConIva , ArtCodBar from pedido p " +
                        " left join articulos a on rtrim(a.artsec)=rtrim(p.artsec) " +
                        " left join clientes n on ltrim(rtrim(n.nitsec))=ltrim(rtrim(p.nitsec)) and n.clisec=p.clisec " +
                        " left join visita v on ltrim(rtrim(v.nitsec))=ltrim(rtrim(p.nitsec)) and v.clisec=p.clisec  and VisAno=pdyear and pdmonth=VisMes  and visdia=pdday and VisPref= p.prefijo" +
                        //" where a.precio1>0 and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay +
                        " where  p.nitsec = '"+enNitSec+"' and p.clisec = "+enclisec+" and prefijo = '"+enprefijo+"' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay   ;

                Consulta+=" union all " +
                        "   select MovParNitSec,MovParCliSec,m.MovParPremArtSec,'" + vUsuario + "' vencod,MovParPremCant cant,MovParPremAno,MovParPremMes,MovParPremDia,1 precio,0 plazo,0.0 pordesc,0.0 pordesc2,0.0 pordesc3,0.0 pordesc4,'BON' obs,prefijo prefi,0.0,0.0,0.0,0.0,0,0,'','',0,0,0,0,0,0 ,0 MovParPremSecLin,1 PedLisPreCod,0 cantinf,0 cantcajinf,0 MovParPremtipo,0 MovParPremSec ,0 bodcod,ArtCod,ArtNom, 0 PedIva,ArtCodBar from MovParPrem M " +
                        "left join articulos a on a.artsec=m.MovParPremArtSec where MovParPremCant > 0 and MovParPremAno=" + time.year + " and MovParPremMes=" + (time.month + 1) + " and MovParPremDia=" + time.monthDay;
                try {
                    Clientes = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null);
                } catch (Exception e) {


                    int jj = 0;
                }

                if (Clientes.getCount() > 0) {

                    Clientes.moveToFirst();
                    String Observacion ="";
                    int nplazo = 0;
                    int conta = 0;
                    do {

                        Double TotalPedido = TotalesPedido(pContext, Clientes.getString(15), Clientes.getString(0), Clientes.getInt(1), "", "", "").Total;
                            Double CantUni = 0.00;
                            Double cantinf = 0.00;
                            Integer CantCajas = 0;
                            Integer cantcajinf = 0;
                            if ((pedidoMinimo != 0 && TotalPedido > pedidoMinimo) || pedidoMinimo == 0) {
                                CantUni = Clientes.getDouble(4);
                                CantCajas = Clientes.getInt(20);
                                cantinf = Clientes.getDouble(32);
                                cantcajinf = Clientes.getInt(33);
                            }


                            //Clientes.getString(3)

                            SDTResumenPedidos SDTResumenPedidos = new SDTResumenPedidos();
                            SDTResumenPedidos = TotalesPedido(pContext, Clientes.getString(15), Clientes.getString(0), Clientes.getInt(1), "", "", "");

                            Observacion = Clientes.getString(14);
                            nplazo = Clientes.getInt(9);
                            int BonProSec = Clientes.getInt(35);
                            int BONPRODETLIN = Clientes.getInt(30);
                            int ArtBodCod = Clientes.getInt(36);
                            String ArtCod = Clientes.getString(37);
                            String ArtCodBar = Clientes.getString(40);
                            Double pediva = Clientes.getDouble(39);
                            String ArtNom=  Clientes.getString(38);
                            String BonProTipo = Clientes.getString(34);


                            Observacion = Observacion.replaceAll("[^\\w ]+", "");


                            //  = precioesp(pContext,Clientes.getString(0),Clientes.getString(2), Clientes.getDouble(8) );

                            double precioNu = 0;
                            precioNu = 0;
                            String consultaart = "select precioesp from PreciosEspeciales where peNitSec = '" + nitsec + "' and peArtSec = '" + Clientes.getString(2).trim() + "'";
                            Cursor cursorart = BaseDeDatos.getWritableDatabase().rawQuery(consultaart, null);
                            cursorart.moveToFirst();


                            if (cursorart.getCount() > 0) {
                                cursorart.moveToFirst();
                                precioNu = cursorart.getDouble(0);
                            } else {
                                precioNu = Clientes.getDouble(8);
                            }

                            Double preciocal = precioNu;

                                if(CantUni > 0){
                                    Double descto = Clientes.getDouble(10)+Clientes.getDouble(11)+Clientes.getDouble(12)+Clientes.getDouble(13);
                                    preciocal = (preciocal*(1+(pediva/100)));
                                    preciocal = preciocal-(preciocal*(descto/100));
                                    Double subtotal = (preciocal * CantUni);


                                    String total = decimalFormat.format(subtotal);
                                    TotalPed += subtotal;
                                    if(ArtCod != null){


                                    Articulos += "*"+ArtCod+"* - "+ArtNom+"\n";
                                  //  Articulos += "  - *IVA:* "+decimalFormat.format(pediva)+"\n";
                                   Articulos += "  - *Cantidad:* "+decimalFormat.format(CantUni)+"\n";
                                    Articulos += "  - *Precio:* "+decimalFormat.format(preciocal)+"\n";
                                    Articulos += "  - *Total:* "+total+"\n";
                                    Articulos += "--------------------------------------\n";

                                    }
                                }






                    } while (Clientes.moveToNext());

                    JsonDetalle += Articulos ;
                    JsonDetalle = JsonDetalle.replace("$observacion", Observacion);
                    JsonDetalle = JsonDetalle.replace("$totalpedido$", "\uD83D\uDCB8 *TOTAL DEL PEDIDO:* "+decimalFormat.format(TotalPed));
                    //JsonDetalle += ;

                }

            } while (PedidoEn.moveToNext());
        }

        return JsonDetalle;

    }


    public String EnviarRemision(Context pContext,String prefijo,String nitsec,Integer clisec) {

        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();

        Time time = new Time();
        time.setToNow();

        String sExistencia="";

        String NumPed = prefijo+nitsec + clisec +time.year + (time.month + 1) + time.monthDay;

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 5);

        String Consulta = "";
        Consulta = "select p.nitsec,p.clisec,p.artsec,p.vencod,cant,pdyear,pdmonth,pdday,precio,plazo,pordesc,pordesc2,pordesc3,pordesc4,ifnull(visobs,'') visobs,p.prefijo from pedido p " +
                " left join articulos a on rtrim(a.artsec)=rtrim(p.artsec) " +
                " left join visita v on v.nitsec=p.nitsec and v.clisec=p.clisec" +
                " where pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay ;
        Cursor Clientes=null;
        try {
            Clientes = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null);
        }catch (Exception e){
            int jj=0;
        }
        String JsonEnvio = "";
        if (Clientes.getCount() > 0) {
            Clientes.moveToFirst();
            do {
                JsonEnvio = "";
                //Clientes.getString(3)
                String NumPedido=Clientes.getString(15)+Clientes.getString(0)+Clientes.getInt(1)+time.year+"-"+(time.month + 1)+"-"+time.monthDay ;
                JsonEnvio="{\"Pedido\":{\"pedido\":\""+NumPedido+"\",\"nitsec\":\""+Clientes.getString(0)+"\",\"clisec\":"+Clientes.getInt(1)+",\"artsec\":\""+Clientes.getString(2)+"\",\"vencod\":\""+vUsuario+"\",\"cant\":\""+Clientes.getDouble(4)+"\",\"pdyear\":"+Clientes.getInt(5)+",\"pdmonth\":"+(time.month + 1)+",\"pdday\":"+Clientes.getInt(7)+",\"precio\":\""+Clientes.getDouble(8)+"\",\"plazo\":"+Clientes.getInt(9)+",\"contreg\":0,\"desc\":\""+Clientes.getDouble(10)+"\",\"desc2\":\""+Clientes.getDouble(11)+"\",\"desc3\":\""+Clientes.getDouble(12)+"\",\"desc4\":\""+Clientes.getDouble(13)+"\",\"pox\":\"\",\"posy\":\"\",\"fechor\":\"\",\"obs\":\""+Clientes.getString(14)+"\",\"papa\":\"\"}}";
                // JsonEnvio="{\"pedido\":\""+Clientes.getString(15)+"\",\"nitsec\":\""+nitsec+"\",\"clisec\":"+clisec+",\"artsec\":\""+Clientes.getString(2)+"\",\"vencod\":\""+4+"\",\"cant\":\""+Clientes.getDouble(4)+"\",\"pdyear\":"+Clientes.getInt(5)+",\"pdmonth\":"+Clientes.getInt(6)+",\"pdday\":"+Clientes.getInt(7)+",\"precio\":\""+Clientes.getDouble(8)+"\",\"plazo\":"+Clientes.getInt(9)+",\"contreg\":0,\"desc\":\""+Clientes.getDouble(10)+"\",\"desc2\":\""+Clientes.getDouble(11)+"\",\"desc3\":\""+Clientes.getDouble(12)+"\",\"desc4\":\""+Clientes.getDouble(13)+"\",\"pox\":\"\",\"posy\":\"\",\"fechor\":\"\",\"obs\":\""+Clientes.getString(14)+"\",\"papa\":\"\"}";
                // JsonEnvio="{\"Pedido\":{\"pedido\":\"\",\"nitsec\":\"\",\"clisec\":0,\"artsec\":\"\",\"vencod\":\"\",\"cant\":\"0\",\"pdyear\":0,\"pdmonth\":0,\"pdday\":0,\"precio\":\"0\",\"plazo\":0,\"contreg\":0,\"desc\":\"0.0000\",\"desc2\":\"0.0000\",\"desc3\":\"0.0000\",\"desc4\":\"0.0000\",\"pox\":\"\",\"posy\":\"\",\"fechor\":\"\",\"obs\":\"\",\"papa\":\"\"}}";
                // JsonEnvio="{\"SDTArtSecWS2\":{\"ArtSec\":\"1\"}}";
                String sql = "http://181.51.253.237:8080/MantisWeb20apps2/rest/pSetRemiDYD";
                // String sql = "http://181.49.42.34:8080/MantisWeb20apps3/rest/pGetExistenciaPrecioWs";
                // String sql = "http://181.49.42.34:8080/MantisWeb20apps3/rest/pSetNotaIbanez";
                sExistencia = "0.0";
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                URL url = null;
                HttpURLConnection conn;

                try {
                    url = new URL(sql);

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Content-Type", "application/json");//; utf-8
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestMethod("POST");

                    StringBuilder result = new StringBuilder();
                    //   result.append(URLEncoder.encode("SDTArtSecWS2", "UTF-8"));
                    //   result.append("=");
                    result.append(JsonEnvio); //URLEncoder.encode(  , "UTF-8")


                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os)); //, "UTF-8"

                    writer.write(result.toString());
                    writer.flush();
                    writer.close();
                    os.close();

                    conn.connect();

                    int statusCode = conn.getResponseCode();
                    InputStream inputstream = null;
                    if (statusCode >= 200 && statusCode < 400) {
                        // Create an InputStream in order to extract the response object
                        inputstream = conn.getInputStream();
                    }
                    else {
                        inputstream = conn.getErrorStream();
                    }

                    BufferedReader in = new BufferedReader(new InputStreamReader(inputstream));

                    String inputLine;

                    StringBuffer response = new StringBuffer();

                    String json = "";

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    json = "[" + response.toString() + "]";

                    JSONArray jsonArr = null;

                    jsonArr = new JSONArray(json);
                    String mensaje = "";
                    for (int i = 0; i < jsonArr.length(); i++) {
                        JSONObject jsonObject = jsonArr.getJSONObject(i);
                        sExistencia = jsonObject.optString("Mensaje");
                    }
                    // sal.setText(mensaje);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } while (Clientes.moveToNext());
        }
        return sExistencia;

    }

    public String EnviarNota(Context pContext,String prefijo,String nitsec,Integer clisec) {





        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();

        Time time = new Time();
        time.setToNow();

        String sExistencia="";

        String NumPed = prefijo + "-" + time.year + "-" + (time.month + 1) + "-" + time.monthDay + "-" + nitsec + "-" + clisec;

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 5);

        String Consulta = "";
                Consulta = "select p.nitsec,p.clisec,p.artsec,p.vencod,cant,pdyear,pdmonth,pdday,precio,plazo,pordesc,pordesc2,pordesc3,pordesc4,ifnull(visobs,'') visobs,p.prefijo from pedido p " +
                       " left join articulos a on rtrim(a.artsec)=rtrim(p.artsec) " +
                       " left join visita v on v.nitsec=p.nitsec and v.clisec=p.clisec " +
                       " where pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay ;
        Cursor Clientes=null;
        try {
             Clientes = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null);
        }catch (Exception e){
            int jj=0;
        }
        String JsonEnvio = "";
        if (Clientes.getCount() > 0) {
            Clientes.moveToFirst();
            do {
                JsonEnvio = "";
                //Clientes.getString(3)
                String NumPedido=Clientes.getString(15)+Clientes.getString(0)+Clientes.getInt(1)+time.year+(time.month + 1)+time.monthDay ;
                JsonEnvio="{\"Pedido\":{\"pedido\":\""+NumPedido+"\",\"nitsec\":\""+Clientes.getString(0)+"\",\"clisec\":"+Clientes.getInt(1)+",\"artsec\":\""+Clientes.getString(2)+"\",\"vencod\":\""+vUsuario+"\",\"cant\":\""+Clientes.getDouble(4)+"\",\"pdyear\":"+Clientes.getInt(5)+",\"pdmonth\":"+Clientes.getInt(6)+",\"pdday\":"+Clientes.getInt(7)+",\"precio\":\""+Clientes.getDouble(8)+"\",\"plazo\":"+Clientes.getInt(9)+",\"contreg\":0,\"desc\":\""+Clientes.getDouble(10)+"\",\"desc2\":\""+Clientes.getDouble(11)+"\",\"desc3\":\""+Clientes.getDouble(12)+"\",\"desc4\":\""+Clientes.getDouble(13)+"\",\"pox\":\"\",\"posy\":\"\",\"fechor\":\"\",\"obs\":\""+Clientes.getString(14)+"\",\"papa\":\"\"}}";
               // JsonEnvio="{\"pedido\":\""+Clientes.getString(15)+"\",\"nitsec\":\""+nitsec+"\",\"clisec\":"+clisec+",\"artsec\":\""+Clientes.getString(2)+"\",\"vencod\":\""+4+"\",\"cant\":\""+Clientes.getDouble(4)+"\",\"pdyear\":"+Clientes.getInt(5)+",\"pdmonth\":"+Clientes.getInt(6)+",\"pdday\":"+Clientes.getInt(7)+",\"precio\":\""+Clientes.getDouble(8)+"\",\"plazo\":"+Clientes.getInt(9)+",\"contreg\":0,\"desc\":\""+Clientes.getDouble(10)+"\",\"desc2\":\""+Clientes.getDouble(11)+"\",\"desc3\":\""+Clientes.getDouble(12)+"\",\"desc4\":\""+Clientes.getDouble(13)+"\",\"pox\":\"\",\"posy\":\"\",\"fechor\":\"\",\"obs\":\""+Clientes.getString(14)+"\",\"papa\":\"\"}";
               // JsonEnvio="{\"Pedido\":{\"pedido\":\"\",\"nitsec\":\"\",\"clisec\":0,\"artsec\":\"\",\"vencod\":\"\",\"cant\":\"0\",\"pdyear\":0,\"pdmonth\":0,\"pdday\":0,\"precio\":\"0\",\"plazo\":0,\"contreg\":0,\"desc\":\"0.0000\",\"desc2\":\"0.0000\",\"desc3\":\"0.0000\",\"desc4\":\"0.0000\",\"pox\":\"\",\"posy\":\"\",\"fechor\":\"\",\"obs\":\"\",\"papa\":\"\"}}";
               // JsonEnvio="{\"SDTArtSecWS2\":{\"ArtSec\":\"1\"}}";
                //String sql = "http://181.51.253.237:8080/MantisWeb20apps2/rest/pSetRemiDYD";
               // String sql = "http://181.49.42.34:8080/MantisWeb20apps3/rest/pGetExistenciaPrecioWs";
                String sql = "http://181.49.42.34:8080/pruebas9/rest/pSetNotaIbanez";
                sExistencia = "0.0";
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                URL url = null;
                HttpURLConnection conn;

                try {
                    url = new URL(sql);

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Content-Type", "application/json");//; utf-8
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestMethod("POST");

                    StringBuilder result = new StringBuilder();
                    //   result.append(URLEncoder.encode("SDTArtSecWS2", "UTF-8"));
                    //   result.append("=");
                       result.append(JsonEnvio); //URLEncoder.encode(  , "UTF-8")


                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os)); //, "UTF-8"

                    writer.write(result.toString());
                    writer.flush();
                    writer.close();
                    os.close();

                    conn.connect();

                    int statusCode = conn.getResponseCode();
                    InputStream inputstream = null;
                    if (statusCode >= 200 && statusCode < 400) {
                        // Create an InputStream in order to extract the response object
                        inputstream = conn.getInputStream();
                    }
                    else {
                        inputstream = conn.getErrorStream();
                    }

                    BufferedReader in = new BufferedReader(new InputStreamReader(inputstream));

                    String inputLine;

                    StringBuffer response = new StringBuffer();

                    String json = "";

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    json = "[" + response.toString() + "]";

                    JSONArray jsonArr = null;

                    jsonArr = new JSONArray(json);
                    String mensaje = "";
                    for (int i = 0; i < jsonArr.length(); i++) {
                        JSONObject jsonObject = jsonArr.getJSONObject(i);
                        sExistencia = jsonObject.optString("Mensaje");
                    }
                    // sal.setText(mensaje);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } while (Clientes.moveToNext());
        }
        return sExistencia;

    }


    public boolean Evaluar(Context pContext, String ArtSec, String Prefijo, String NitSec, Integer CliSec, Double Unidades, Integer Cajas, Integer Embalaje) {
           // String ArtSec, String Prefijo, String NitSec, Integer CliSec, Double Unidades


            String Codigo=ArtSec;
            String aCantidad=Unidades.toString();
            Integer Cantidad=Double.valueOf(aCantidad).intValue();
            Time time = new Time();
            time.setToNow();
            GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
            String vEmpresa=vGlobalVariables.getEmpresa();
            vEmpresa=vEmpresa.toUpperCase();
            String vUsuario=vGlobalVariables.getUsuario();
            int bodcod = 0;
            int eCancod = 0;
            int elisprecod = 0;
            final BaseDatos BaseDeDatos;
            BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 5);

            Cursor cursorpedido = BaseDeDatos.getWritableDatabase().rawQuery("select bodcod from pedido where prefijo = '"+Prefijo+"' and artsec = '"+ArtSec+"' and nitsec = '"+NitSec+"' and clisec = "+CliSec+" and  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay+" ", null);
            if(cursorpedido.getCount()>0){
                cursorpedido.moveToFirst();
                bodcod = cursorpedido.getInt(0);
            }
            Cursor canalCli = BaseDeDatos.getWritableDatabase().rawQuery("Select CanCod,Lisprecod from clientes where NitSec = '"+NitSec+"' and clisec = "+CliSec+" ",null);
            if(canalCli.getCount()>0){
                canalCli.moveToFirst();
                elisprecod = canalCli.getInt(1);
                eCancod = canalCli.getInt(0);

            }
            //============================================= Descuento V1 ====================================================

            String TempJJ;
            //String Pedido = Prefijo + Extras.getString("Codvend").trim() + "-" + time.year + "-" + (time.month + 1) + "-" + time.monthDay + "-" + Extras.getString("nitsec") + "-" + Extras.getInt("clisec");
            String VersionDescuentosV2=vGlobalVariables.getParMovDescV2();
            Log.e("VersionDes",VersionDescuentosV2);
            //VersionDescuentosV2 = "N"; // -- validacionSURTIMARCAS
            if (VersionDescuentosV2.equalsIgnoreCase("S")==false){

                TempJJ = "select MovParMixRefDis,MovParMixCntTotal,MovParMixConic,MovParMixSec,MovParMixCntTotalCaj,MovParMixPeri from movparmix " +
                        "where MovParMixSec in(select MovParMixSec from MovParMixArticulos where MovParMixDetArtSec='"+Codigo+"')";
                Cursor movparmix = BaseDeDatos.getWritableDatabase().rawQuery(TempJJ, null);
                if (movparmix.getCount() > 0) {
                    movparmix.moveToFirst();
                    do {

                        //"prefijo='"+Prefijo+"' and MovParNitSec='"+NitSec+"'  and MovParCliSec="+CliSec;

                        BaseDeDatos.getWritableDatabase().execSQL("Delete from MovParPrem where prefijo='"+Prefijo+"' and MovParNitSec='"+NitSec+"'  and MovParCliSec="+CliSec+" and MovParPremSec=" + movparmix.getInt(3) + " and (MovParPremTip='MPX' OR MovParPremTip='MPD')");

                        Double RefDis=0.00;
                        Double CantTot=0.00;
                        Double CantObl=999.0;
                        if (movparmix.getInt(0)>0) {
                            String ConsultaTxt = "select count(*) conteto from pedido where rtrim(nitsec) = rtrim('" + NitSec + "') and clisec=" + CliSec + " and artsec in(select MovParMixDetArtSec from MovParMIxarticulos where movparmixsec="+movparmix.getInt(3)+") and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and cant+cantcaj>0 and prefijo = '"+Prefijo+"'";
                            Cursor MovParMixRefDis = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaTxt, null);
                            if (MovParMixRefDis.getCount() > 0){
                                MovParMixRefDis.moveToFirst();
                                if(MovParMixRefDis.getInt(0) >= movparmix.getInt(0)){
                                    RefDis= Double.valueOf(MovParMixRefDis.getInt(0));/// movparmix.getInt(0));
                                }
                            }
                        }else{
                            RefDis= Double.valueOf(999);
                        }
                        if (movparmix.getInt(1)>0 || movparmix.getInt(4)>0) {
                            String ConsultaTxt = "select total(cant) conteto,total((cant+(cantcaj*pedartemb))/pedartemb) contetocaj from pedido where rtrim(nitsec) = rtrim('" +NitSec+ "') and clisec=" + CliSec+ " and artsec in(select MovParMixDetArtSec from MovParMIxarticulos where movparmixsec="+movparmix.getInt(3)+")  and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and cant+cantcaj>0 and prefijo = '"+Prefijo+"'";
                            Cursor MovParMixCntTotal = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaTxt, null);
                            if (MovParMixCntTotal.getCount() > 0){
                                MovParMixCntTotal.moveToFirst();
                                int gg2=MovParMixCntTotal.getInt(0);
                                if(MovParMixCntTotal.getInt(0) >= movparmix.getInt(1) && movparmix.getInt(1)>0){
                                    CantTot= Double.valueOf(MovParMixCntTotal.getInt(0) / movparmix.getInt(1));
                                }
                                if(MovParMixCntTotal.getInt(1) >= movparmix.getInt(4) && movparmix.getInt(4)>0){
                                    CantTot= Double.valueOf(MovParMixCntTotal.getInt(0) / movparmix.getInt(4));
                                }
                            }
                        }else{
                            CantTot= Double.valueOf(0);
                        }

                        TempJJ = "select MovParMixDetArtSec,MovParMixDetCntObl,MovParMixDetCntOblCaj,MovParMixDetArtEmb,(MovParMixDetCntObl+(MovParMixDetCntOblCaj*MovParMixDetArtEmb)) totuni from MovParMIxarticulos where MovParMixDetCntObl+MovParMixDetCntOblCaj<>0 and MovParMixSec="+movparmix.getInt(3)+"";
                        Cursor MovParMIxarticulos=null;
                        try {
                            MovParMIxarticulos = BaseDeDatos.getWritableDatabase().rawQuery(TempJJ, null);
                        }catch (Exception e){
                            int jj=0;
                        }
                        if (MovParMIxarticulos.getCount() > 0){
                            MovParMIxarticulos.moveToFirst();
                            do {
                                String ConsultaTxt = "select total(cant) conteto,total((cant+(cantcaj*pedartemb))) contetocaj from pedido where rtrim(nitsec) = rtrim('" + NitSec + "') and clisec=" + CliSec+ " and artsec='" + MovParMIxarticulos.getString(0) + "' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and cant+cantcaj>0 and  prefijo = '"+Prefijo+"'";
                                Cursor MovParMixRefObl = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaTxt, null);
                                if (MovParMixRefObl.getCount() > 0){
                                    MovParMixRefObl.moveToFirst();
                                    if(MovParMixRefObl.getInt(1) >= MovParMIxarticulos.getInt(4)){
                                        Double TmpCantObl= Double.valueOf(MovParMixRefObl.getInt(1)/MovParMIxarticulos.getInt(4));
                                        if (TmpCantObl<CantObl){
                                            CantObl=TmpCantObl;
                                        }
                                    }
                                }else{
                                    CantObl= Double.valueOf(999);
                                }
                            } while (MovParMIxarticulos.moveToNext());
                        }else{
                            CantObl= Double.valueOf(999);
                        }
                        Integer Coincidencias=0;
                        if(movparmix.getInt(0)>0)
                        {
                            //    if (CantTot<RefDis) {
                            //        Coincidencias = CantTot.intValue();
                            //    }else{
                            //        Coincidencias = RefDis.intValue();
                            //    }
                            if (RefDis>=movparmix.getInt(0)) {
                                if (CantObl < CantTot) {
                                    Coincidencias = CantObl.intValue();
                                } else {
                                    Coincidencias = CantTot.intValue();
                                }
                            }
                        }else{
                            if (CantObl < CantTot) {
                                Coincidencias = CantObl.intValue();
                            } else {
                                Coincidencias = CantTot.intValue();
                            }
                        }
                        if(movparmix.getInt(0)>0) {
                            if (Coincidencias >= movparmix.getInt(0)) {
                                Coincidencias=movparmix.getInt(0);
                            }
                        }


                        if (Coincidencias>=1) {
                            // String Pedido = Extras.getString("tipdoc") + Extras.getString("Codvend") + "-" + time.year + "-" + (time.month + 1) + "-" + time.monthDay + "-" + Extras.getString("nitsec") + "-" + Extras.getInt("clisec");

                            Integer Coincidencias2=Coincidencias;
                            TempJJ = "select MovParMixSec,MovParMixBonArtSec,MovParMixBonCant,MovParMixBonCantCaj from MovParMixBonificados where MovParMixBonCant<>0 and MovParMixSec=" + movparmix.getInt(3) + "";
                            Cursor MovParMixBonificados = BaseDeDatos.getWritableDatabase().rawQuery(TempJJ, null);
                            if (MovParMixBonificados.getCount() > 0) {
                                MovParMixBonificados.moveToFirst();
                                do {
                                    Integer Entregados=0;
                                    TempJJ = "select sum(karuni) cantidad from ClientesDevoluciones where nitsec='"+NitSec+"' and CliSec=" + CliSec + " and dias<="+movparmix.getInt(5)+" and artsec='"+ArtSec+"'";
                                    Cursor HistorialBonificados = BaseDeDatos.getWritableDatabase().rawQuery(TempJJ, null);
                                    if (HistorialBonificados.getCount() > 0) {
                                        HistorialBonificados.moveToFirst();
                                        do {
                                            Entregados=HistorialBonificados.getInt(0);
                                        } while (HistorialBonificados.moveToNext());
                                    }

                                    if (movparmix.getInt(2)<Coincidencias2){
                                        Coincidencias2=movparmix.getInt(2);
                                    }
                                    if (Entregados!=0){
                                        Entregados=Entregados;
                                    }
                                    if (movparmix.getInt(2)==0){
                                        Entregados=0;
                                    }
                                    if (Coincidencias2-Entregados>0) {
                                        String bCodigo = MovParMixBonificados.getString(1).trim();
                                        String Consulta = "insert into MovParPrem(prefijo,MovParNitSec,MovParCliSec,MovParPremSec,MovParPremTip,MovParPremArtSec,MovParPremCant,MovParPremCantCaj,MovParPremDesc,MovParPremArtSecOri,MovParPremAno,MovParPremMes,MovParPremDia)values('" + Prefijo + "','" + NitSec + "'," + CliSec + "," + movparmix.getInt(3) + ",'MPX','" + bCodigo + "'," + ((MovParMixBonificados.getInt(2) - Entregados) * Coincidencias2) + "," + ((MovParMixBonificados.getInt(3)) * Coincidencias2) + ",0,'" + Codigo + "'," + time.year + "," + (time.month + 1) + "," + time.monthDay + ")";
                                        try {
                                            BaseDeDatos.getWritableDatabase().execSQL(Consulta);
                                        } catch (Exception e) {
                                            int jj = 0;
                                        }
                                    }
                                } while (MovParMixBonificados.moveToNext());
                            }
                        }

                        TempJJ = "select MovParMixDetArtSec,MovParMixDetCntDes from MovParMixArticulos where MovParMixDetCntDes<>0 and MovParMixSec="+movparmix.getInt(3)+"";
                        Cursor MovParMixArticulos2 = BaseDeDatos.getWritableDatabase().rawQuery(TempJJ, null);
                        if (MovParMixArticulos2.getCount() > 0){
                            MovParMixArticulos2.moveToFirst();
                            do {
                                if (Coincidencias>=1) {
                                    //String Consulta = "insert into MovParPrem(prefijo,MovParNitSec,MovParCliSec,MovParPremSec,MovParPremTip,MovParPremArtSec,MovParPremCant,MovParPremDesc,MovParPremAno,MovParPremMes,MovParPremDia)values('" + Prefijo+ "','"+NitSec  + "'," +CliSec+ "," +  movparmix.getInt(3) + ",'MPD','" + MovParMixArticulos2.getInt(0) + "',0," + MovParMixArticulos2.getInt(1) +","+time.year + "," + (time.month + 1) + "," +time.monthDay +")";
                                    //BaseDeDatos.getWritableDatabase().execSQL(Consulta);


                                    Log.e("Descuentos: pordesc4",String.valueOf(MovParMixArticulos2.getInt(1) ));

                                    BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc4=" + MovParMixArticulos2.getInt(1) + ",pordesc4no=" + MovParMixArticulos2.getInt(1) + " where pordesc4no<>" + MovParMixArticulos2.getInt(1) + "  and Prefijo='"+Prefijo+"' and artsec='"+MovParMixArticulos2.getInt(0)+"'");
                                }   else{
                                    BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc4=0.00,pordesc4no=0.00  where Prefijo='"+Prefijo+"' and artsec='"+MovParMixArticulos2.getInt(0)+"'");
                                }

                            } while (MovParMixArticulos2.moveToNext());
                        }

                    } while (movparmix.moveToNext());

                }

                Cursor ConMovPar = null;

                try {
                    //String Pedido = Extras.getString("tipdoc") + Extras.getString("Codvend") + "-" + time.year + "-" + (time.month + 1) + "-" + time.monthDay + "-" + Extras.getString("nitsec") + "-" + Extras.getInt("clisec");
                    Integer Tcant=0;
                    Integer MovParPremSec=0;
                    String MovParBonEncArtSec="";
                    //TempJJ = "select MovParBonSec,MovParBonEncArtSec,MovParBonCant,MovParBonCantCaj,MovParBonEmb,MovParBonCant+(MovParBonCantCaj*MovParBonEmb) totuni from MovParBonProdBon " +
                      //      "where MovParBonEncArtSec='"+Codigo+"'";
                    TempJJ = "  select MovParBonSec,MovParBonEncArtSec,MovParBonCant,(MovParBonCantCaj*MovParBonEmb)+MovParBonCant Totuni,MovParBonEmb,MovParBonClientes " +
                            "   from MovParBonProdBon left join Clientes  c on NitSec='"+NitSec+"' and CliSec="+CliSec+" "+
                            "   where MovParBonEncArtSec='"+ArtSec+"'  " +
                            "   and (MovParBonCanales='XX,' OR MovParBonCanales like '%,'|| c.CanCod ||',%') " +
                            "   and (MovParBonClientes='XX,' OR MovParBonClientes like '%,'|| NitSec ||',%') " +
                            "   and (MovParBonClientesExlu='XX,' OR MovParBonClientesExlu NOT like '%,'|| NitSec ||',%') ";

                    Log.e("CLIENTEBONPRO: ",NitSec);

                    Cursor MovParBonProdBon = BaseDeDatos.getWritableDatabase().rawQuery(TempJJ, null);
                    if (MovParBonProdBon.getCount() > 0) {
                        Log.e("CLIENTEBOsssssNPRO: ",NitSec);
                        MovParBonProdBon.moveToFirst();
                        Integer MovParBonSec=MovParBonProdBon.getInt(0);
                        String MovParBonClientes=MovParBonProdBon.getString(5);
                        BaseDeDatos.getWritableDatabase().execSQL("Delete from MovParPrem where Prefijo='" + Prefijo + "' and MovParPremSec=" + MovParBonSec+ " and MovParPremTip='BNP' and MovParPremArtSecOri='"+Codigo+"'");
                        do {

                            if (MovParBonProdBon.getInt(3)>0) {
                                MovParBonSec=MovParBonProdBon.getInt(0);
                                BaseDeDatos.getWritableDatabase().execSQL("Delete from MovParPrem where Prefijo='" + Prefijo + "' and MovParPremSec=" + MovParBonSec+ " and MovParPremTip='BNP' and MovParPremArtSecOri='"+Codigo+"'");
                                Tcant = (Unidades.intValue()+(Cajas*Embalaje)) / MovParBonProdBon.getInt(3);
                                if (Tcant>=1){
                                    String TempBon = "select MovParBonSec,MovParBonArtSec,MovParBonDetCant,MovParBonDetCantCaj from MovParBonBonificados where MovParBonSec="+MovParBonSec;
                                    Cursor MovParBonBonificados = BaseDeDatos.getWritableDatabase().rawQuery(TempBon, null);
                                    if (MovParBonBonificados.getCount() > 0) {
                                        MovParBonBonificados.moveToFirst();
                                        do {
                                            if (MovParBonBonificados.getInt(2) > 0) {
                                                String MovParBonArtSec=MovParBonBonificados.getString(1);
                                                String Consulta = "insert into MovParPrem(prefijo,MovParNitSec,MovParCliSec,MovParPremSec,MovParPremTip,MovParPremArtSec,MovParPremCant,MovParPremCantCaj,MovParPremDesc,MovParPremArtSecOri,MovParPremAno,MovParPremMes,MovParPremDia)values('" + Prefijo+ "','"+NitSec  + "'," +CliSec+ "," +  MovParBonSec + ",'BNP','" + MovParBonArtSec.trim() + "'," + ((MovParBonBonificados.getInt(2)) * Tcant) + ","+((MovParBonBonificados.getInt(3)) * Tcant)+",0,'"+Codigo+"',"+time.year + "," + (time.month + 1) + "," +time.monthDay +")";
                                                try {
                                                    BaseDeDatos.getWritableDatabase().execSQL(Consulta);
                                                } catch (Exception e) {
                                                    int jj = 0;
                                                }
                                            }
                                        }while (MovParBonBonificados.moveToNext()) ;
                                    }
                                }
                                //MovParPremSec =movparmix.getInt(0);
                                //MovParBonEncArtSec=MovParBonBonificados.getInt(2);
                            }
                        } while (MovParBonProdBon.moveToNext());

                       /* if (Tcant>=1){
                            String Consulta = "insert into MovParPrem(pedido,MovParPremSec,MovParPremTip,MovParPremArtSec,MovParPremCant,MovParPremDesc)values('" + Pedido + "'," + MovParPremSec + ",'BNP','" + MovParBonEncArtSec + "'," + ((MovParMixBonificados.getInt(2)) * Coincidencias) + ",0)";
                            try {
                                BaseDeDatos.getWritableDatabase().execSQL(Consulta);
                            } catch (Exception e) {
                                int jj = 0;
                            }
                        }*/

                    }
                } catch (Exception e) {
                    int pp = 0;
                }

             //   String ArtSec, String Prefijo, String NitSec, Integer CliSec, Double Unidades
             //   String Codigo=ArtSec;
             //   String aCantidad=Unidades.toString();
             //   Integer Cantidad=Double.valueOf(aCantidad).intValue();

               // String prefijo,String nitsec,Integer clisec
                try {
                    Integer totCantidades = 0;
                    Integer totCantidadesCajas = 0;
                    Cursor hayEscala = BaseDeDatos.getWritableDatabase().rawQuery("select MovParEscArtSec,MovParEscSec from MovParEsc where MovParEscSec in(select MovParEscSec from MovParEsc where MovParEscArtSec='" + ArtSec.trim() + "' )  group by MovParEscArtSec,MovParEscSec", null); //and MOvParEscInd<>'IND'
                    // and MovParEscArtSec<>'" + codigo[position2].trim() + "'
                    if (hayEscala.getCount() > 0) {
                        hayEscala.moveToFirst();
                        do {
                                String kk=hayEscala.getString(1).trim();
                          //  if (!hayEscala.getString(0).toString().trim().equalsIgnoreCase(ArtSec.trim())) {
                                Cursor Cantidades = BaseDeDatos.getWritableDatabase().rawQuery("select cant+(cantcaj*pedartemb),(cant+(cantcaj*pedartemb))/pedartemb cajas from pedido where nitsec='" + NitSec + "' and clisec=" + CliSec + " and artsec='" + hayEscala.getString(0).trim() + "' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo = '" + Prefijo + "'", null);
                                if (Cantidades.getCount() > 0) {
                                    Cantidades.moveToFirst();
                                    totCantidades += Cantidades.getInt(0);
                                    totCantidadesCajas += Cantidades.getInt(1);
                                }
                                Cantidades.close();
                           // }
                        } while (hayEscala.moveToNext());

                        // totCantidades = totCantidades + (Double.valueOf(aCantidad)).intValue();
                        int Entro=0;
                        Cursor Escala = BaseDeDatos.getWritableDatabase().rawQuery("select MovParEscDesc1,MovParEscDesc2 from MovParEsc where MovParEscArtSec='" + ArtSec.trim() + "' and MovParEscDe<=" + totCantidades + " and MovParEscHasta>=" + totCantidades + " and MovParEscDeCaj+MovParEscHastaCaj=0", null);
                        if (Escala.getCount() > 0) {
                            Escala.moveToFirst();
                            do {
                                String ValDesc3 = String.valueOf(Escala.getDouble(0));
                                String ValDesc4 = String.valueOf(Escala.getDouble(1));

                                BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc3=" + Double.valueOf(ValDesc3) + ",pordesc3no=" + Double.valueOf(ValDesc3) + " where pordesc3no<>" + Double.valueOf(ValDesc3) + " and nitsec='" + NitSec + "' and clisec=" + CliSec + " and artsec in(select MovParEscArtSec from MovParEsc where MovParEscSec=(select MovParEscSec from MovParEsc where MovParEscArtSec='" + ArtSec + "' )) and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo like '" + Prefijo + "'");

                                Entro=1;
                                // and MovParEscArtSec<>'" + codigo[position2].trim() + "'
                            } while (Escala.moveToNext());

                        } else {
                            BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc3=0,pordesc3no=0 where nitsec='" + NitSec + "' and clisec=" + CliSec + " and artsec in(select MovParEscArtSec from MovParEsc where MovParEscSec=(select MovParEscSec from MovParEsc where MovParEscArtSec='" + ArtSec + "' )) and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo like '" + Prefijo + "'");
                        }
                        Cursor Escala2 = BaseDeDatos.getWritableDatabase().rawQuery("select MovParEscDesc1,MovParEscDesc2 from MovParEsc where MovParEscArtSec='" + ArtSec.trim() + "' and MovParEscDeCaj<=" + totCantidades + " and MovParEscHastaCaj>=" + totCantidades + " and MovParEscDe+MovParEscHasta=0", null);
                        if (Escala2.getCount() > 0) {
                            Escala2.moveToFirst();
                            do {
                                String ValDesc3 = String.valueOf(Escala2.getDouble(0));
                                String ValDesc4 = String.valueOf(Escala2.getDouble(1));

                                BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc3=" + Double.valueOf(ValDesc3) + ",pordesc3no=" + Double.valueOf(ValDesc3) + " where pordesc3no<>" + Double.valueOf(ValDesc3) + " and nitsec='" + NitSec + "' and clisec=" + CliSec + " and artsec in(select MovParEscArtSec from MovParEsc where MovParEscSec=(select MovParEscSec from MovParEsc where MovParEscArtSec='" + ArtSec + "' )) and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo like '" + Prefijo + "'");
                                // and MovParEscArtSec<>'" + codigo[position2].trim() + "'
                            } while (Escala2.moveToNext());

                        } else {
                            if (Entro==0){
                             BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc3=0,pordesc3no=0 where nitsec='" + NitSec + "' and clisec=" + CliSec + " and artsec in(select MovParEscArtSec from MovParEsc where MovParEscSec=(select MovParEscSec from MovParEsc where MovParEscArtSec='" + ArtSec + "' )) and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo like '" + Prefijo + "'");
                            }
                        }
                        Escala.close();
                    }
                }catch(Exception e) {
                    int pp = 0;
                }

                try {
                    int traerdcto=0;
                    Cursor cursorCla = BaseDeDatos.getReadableDatabase().rawQuery("select * from PerfilClientesClase ", null);
                    if (cursorCla.getCount()>0){
                        traerdcto=1;
                    }


                    Cursor datosarticulo = BaseDeDatos.getWritableDatabase().rawQuery("select invgrucod,invsubgrucod,invfamcod from articulos where artsec='"+Codigo+"' ", null); //and MOvParEscInd<>'IND'
                    if (datosarticulo.getCount() > 0) {
                        datosarticulo.moveToFirst();
                        String Consulta = "";
                        Consulta = "select MovParLinDes,MovParLinResCan,MovParLinViaDir,MovParLinNoOtor from MovParLinea where MovParLinArtSec='" + Codigo + "' ";

                        Double DctoLinea = 0.0;
                        Cursor haydesart = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null); //and MOvParEscInd<>'IND'
                        // and MovParEscArtSec<>'" + codigo[position2].trim() + "'
                        if (haydesart.getCount() > 0) {
                            haydesart.moveToFirst();
                            do {
                                DctoLinea += haydesart.getDouble(0);
                            } while (haydesart.moveToNext());
                        }
                        if (traerdcto == 1  || vEmpresa.trim().equalsIgnoreCase("PROMEFAR") ){

                        }else{
                            BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc=" + DctoLinea + ",pordescno=" + DctoLinea + " where pordescno<>" + DctoLinea + " and nitsec='" + NitSec + "' and clisec=" + CliSec + " and artsec ='" + Codigo + "' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo like '" + Prefijo + "'");
                        }

                    }
                }catch(Exception e) {
                    int pp = 0;
                }

                try {
                    Double DctoArt1=0.0;
                    SDTResumenPedidos vSDTResumenPedidos=TotalesPedido(pContext,Prefijo,NitSec,CliSec,"","","");
                    Cursor MovParArt = BaseDeDatos.getWritableDatabase().rawQuery("select MovParValDetDesc from movparvalrango where MovParValDetRan1<="+vSDTResumenPedidos.Subtotal+" and MovParValDetRan2>="+vSDTResumenPedidos.Subtotal, null); //and MOvParEscInd<>'IND'
                    if (MovParArt.getCount() > 0) {
                        MovParArt.moveToFirst();

                        do{
                            DctoArt1+=MovParArt.getDouble(0);
                        } while (MovParArt.moveToNext());

                        BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc6="+DctoArt1+",pordesc6no="+DctoArt1+" where pordesc6no<>"+DctoArt1+" and nitsec='" + NitSec + "' and clisec=" + CliSec + " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo = '" + Prefijo + "' ");

                    }
                }catch(Exception e) {
                    int pp = 0;
                }

                try {
                    int traerdcto=0;
                    Cursor cursorCla = BaseDeDatos.getReadableDatabase().rawQuery("select * from PerfilClientesClase ", null);
                    if (cursorCla.getCount()>0){
                        traerdcto=1;
                    }
                    Log.e("traerdcto",String.valueOf(traerdcto));
                    Double DctoArt1=0.0;
                    Double DctoArt2=0.0;
                    String ciucod = "";
                    String Tempciu = "select CiuCod from Clientes where nitsec='"+NitSec+"' and CliSec=" + CliSec + " ";
                    Cursor ciudadcli = BaseDeDatos.getWritableDatabase().rawQuery(Tempciu, null);
                    if (ciudadcli.getCount() > 0) {
                        ciudadcli.moveToFirst();
                        do{
                            ciucod = ciudadcli.getString(0);
                        } while (ciudadcli.moveToNext());
                    }

                        //and MovParArtCiucod = '"+ciucod+"'"
                    Cursor MovParArt = BaseDeDatos.getWritableDatabase().rawQuery("select MovParArtDetDesc,MovParNumDcto,MovParViaDir,MovParNoOtor " +
                            " from MovParArt left join Clientes  c on NitSec='"+NitSec+"' and CliSec="+CliSec+" "+
                            " where MovParArtDetArtSec='"+Codigo+"' " +
                            " and (CANALES='XX,' OR CANALES like '%,'|| c.CanCod ||',%')  " +
                            " and (CLIENTES='XX,' OR CLIENTES like '%,'|| "+NitSec+" ||',%') " +
                            " and (CLIENTESEX='XX,' OR CLIENTESEX NOT like '%,'|| "+NitSec+" ||',%') "

                            , null); //and MOvParEscInd<>'IND'



                    if (MovParArt.getCount() > 0) {
                        Log.e("traerdcto",String.valueOf(traerdcto));
                        MovParArt.moveToFirst();

                        do{
                            if (MovParArt.getString(1).equalsIgnoreCase("2")) {
                                DctoArt2 += MovParArt.getDouble(0);
                            }else {
                                DctoArt1 += MovParArt.getDouble(0);
                            }
                        } while (MovParArt.moveToNext());

                        //pordesc2no<>"+DctoArt1+" and

                        Log.e("traerdcto",String.valueOf(traerdcto));


                        if (traerdcto == 1) {

                        }else{
                            Log.e("Desc2evaluar",String.valueOf(DctoArt1));
                            BaseDeDatos.getWritableDatabase().execSQL("update pedido set   pordesc=" + DctoArt1 + ",pordescno=" + DctoArt1 + " ,pordesc2=" + DctoArt2 + ",pordesc2no=" + DctoArt2 + " where pordesc2no<>" + DctoArt1 + " and  nitsec='" + NitSec + "' and clisec=" + CliSec + " and artsec ='" + Codigo + "' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo ='" + Prefijo + "'");
                        }
                        /*Cursor Actualizo =BaseDeDatos.getWritableDatabase().rawQuery("select pordesc2 from pedido where nitsec='" + NitSec + "' and clisec=" + CliSec + " and artsec ='"+Codigo+"' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo ='" + Prefijo + "'",null);

                        if (Actualizo.getCount() > 0) {
                            Actualizo.moveToFirst();
                            DctoArt1 += Actualizo.getDouble(0);
                        }*/

                        BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc6=0,pordesc6no=0,pordesc5="+DctoArt2+",pordesc5no="+DctoArt2+" where  pordesc5no<>"+DctoArt2+" and nitsec='" + NitSec + "' and clisec=" + CliSec + " and artsec ='"+Codigo+"' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo = '" + Prefijo + "'");
                        if (DctoArt2>0) {
                            BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc6=0,pordesc6no=0 where  nitsec='" + NitSec + "' and clisec=" + CliSec + " and artsec ='" + Codigo + "' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo = '" + Prefijo + "'");
                        }

                    }
                }catch(Exception e) {
                    int pp = 0;
                }







                try {
                    Double DctoArt1=0.0;
                    Double DctoArt2=0.0;
                    Cursor MovParArt2 = BaseDeDatos.getWritableDatabase().rawQuery("select nitsec,clisec,CliDesInvGruCod,CLiDesDcto,CliDesFin from ClientesDcto where nitsec='"+NitSec+"' and clisec="+CliSec+" ", null); //and MOvParEscInd<>'IND'
                    if (MovParArt2.getCount() > 0) {
                        MovParArt2.moveToFirst();
                        DctoArt2 = MovParArt2.getDouble(3);
                        String Invgrucod = MovParArt2.getString(2);
                        if (DctoArt2!=0) {
                            BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc4="+DctoArt2+" where artsec in (select artsec from articulos where InvGruCod='"+Invgrucod+"') and  nitsec='" + NitSec + "' and clisec=" + CliSec + " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo ='" + Prefijo + "'");
                        }
                    }








                }catch(Exception e) {
                    int pp = 0;
                }


                if(vEmpresa.equalsIgnoreCase("MENTAHAIR") || vEmpresa.equalsIgnoreCase("MENTAHAIRCOT")){
                    try {
                        Double DctoArt1=0.0;
                        Double DctoArt2=0.0;
                        Double xdescuento =0.0;
                        int condpago = 0;



                        Cursor MovParArt2 = BaseDeDatos.getWritableDatabase().rawQuery("select nitsec,clisec,clidespagcont,clidespagcre,cliconpag from Clientes where nitsec='"+NitSec+"' and clisec="+CliSec+" ", null); //and MOvParEscInd<>'IND'
                        if (MovParArt2.getCount() > 0) {

                            MovParArt2.moveToFirst();
                            DctoArt2 = MovParArt2.getDouble(2);
                            DctoArt1 =  MovParArt2.getDouble(3);
                            condpago = MovParArt2.getInt(4);


                                if(condpago > 0){
                                    xdescuento =DctoArt1;
                                }else{
                                    xdescuento =DctoArt2;
                                }

                            BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc3="+xdescuento+" where  artsec ='" + Codigo + "' and  nitsec='" + NitSec + "' and clisec=" + CliSec + " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo ='" + Prefijo + "'");

                        }

                    }catch(Exception e) {
                        int pp = 0;
                    }



                    try {
                        Double DctoArt1=0.0;
                        Double DctoArt2=0.0;
                        Double xdescuento =0.0;
                        int  condpago = 9;

                        if (elisprecod == 0) {
                            condpago = 9; // valor por defecto si es null
                        } else {
                            switch (elisprecod) {
                                case 1 :
                                    condpago = 9;
                                    break;
                                case 2 :
                                    condpago = 10;
                                    break;
                                case 3 :
                                    condpago = 11;
                                    break;
                                case 4 :
                                    condpago = 12;
                                    break;
                                case 5 :
                                    condpago = 13;
                                    break;
                                case 15:
                                    condpago = 15;
                                    break;
                                default:
                                    condpago = 9; // equivalente al "else 1"
                                    break;
                            }
                        }

                        Cursor MovParArt2 = BaseDeDatos.getWritableDatabase().rawQuery("select MovParArtDetDesc from MovParDesPro where (CLIENTES='XX,' OR CLIENTES like '%,'|| '"+NitSec+"'||',%') and MovParDesArtSec ='" + Codigo + "' and (LISTAS='XX,' OR LISTAS like '%,'|| '"+condpago+"'||',%') ", null); //and MOvParEscInd<>'IND'c
                        if (MovParArt2.getCount() > 0) {

                            MovParArt2.moveToFirst();
                            DctoArt2 = MovParArt2.getDouble(0);
                            xdescuento =DctoArt2;
                            Log.e("Descuento:",String.valueOf(xdescuento));
                            BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordescno="+xdescuento+",  pordesc ="+xdescuento+" where  artsec ='" + Codigo + "' and  nitsec='" + NitSec + "' and clisec=" + CliSec + " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo ='" + Prefijo + "'");

                        }

                        if(Unidades+Cajas == 0){
                            BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordescno=0,pordesc = 0 where  artsec ='" + Codigo + "' and  nitsec='" + NitSec + "' and clisec=" + CliSec + " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo ='" + Prefijo + "'");

                        }

                    }catch(Exception e) {
                        int pp = 0;
                    }



                }






            }

          //============================================= Descuento V2 ====================================================


            String HH=vGlobalVariables.getParMovDescV2();
            Log.e("HH",HH);
            int SucCod=vGlobalVariables.getSucCod();
            ConBd conbd = new ConBd();
            //Connection connGen = conbd.CargarConexion();
            String mantisficc =  conbd.MantisFicc;
            if (HH.equalsIgnoreCase("S") || mantisficc.equalsIgnoreCase("S")){  //DESCUENTOS VERSION 2

             /*   String Bodegas="XX,";
                String teXTO=" and (BODEGAS='XX,' OR BODEGAS like '%,'|| Bodegas ||',%') ";
                if(Prefijo.equalsIgnoreCase("P3") & vEmpresa.trim().equalsIgnoreCase("IBANEZ")){
                    Bodegas="45";
                    //teXTO=" ";
                }

              */
                String bdartsec = "'99999'";


                time.setToNow();

              Log.e("Inicio descuento ",String.valueOf(time));



                try { // Borrar pedidoDesc para abajo volver a insertar los descuentos

                    //BaseDeDatos.getWritableDatabase().execSQL("Delete from pedidoDesc where prefijo='"+Prefijo+"' and nitsec='"+NitSec+"' and clisec="+CliSec+" and artsec='"+Codigo+"' and pdyear="+time.year+" and pdmonth="+(time.month + 1)+" and pdday="+time.monthDay);
                    BaseDeDatos.getWritableDatabase().execSQL("Delete from pedidoDesc where  DesSec not in(select DESCSEC from Descuentos group by DESCSEC)");
                    SDTResumenPedidos vSDTResumenPedidos=TotalesPedido(pContext,Prefijo,NitSec,CliSec,"","","");
                    //tipodesc,descgru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,UNIDADNEG,CIUDADES,CLIENTES,VENDEDORES,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal

                    Log.e("Entro aca: ","Codigo"+Codigo);
                    Log.e("Entro aca: ","bodcod"+bodcod);
                    Log.e("Entro aca: ","SucCod"+SucCod);

                    String ConsultaPremiosBorrar="select DESCSEC,tipodesc,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal,DescLinPorDesLin,descagru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,CIUDADES,CLIENTES, DESPROGAPLESCTOT from Descuentos" +
                            " left join articulos a on artsec="+Codigo+" " +
                            " left join Clientes c on nitsec='"+NitSec+"' and clisec="+CliSec+" " +
                            " where  descgru=InvGruCod and (SUBGRUPOS='XX,' OR SUBGRUPOS like '%,'|| a.InvSubGruCod ||',%') " +
                            " and (FAMILIAS='XX,' OR FAMILIAS like '%,'|| a.InvFamCod ||',%') " +
                            " and (ARTICULOS='XX,' OR ARTICULOS like '%,'|| a.artsec ||',%') " +
                            " and (BODEGAS='XX,' OR BODEGAS like '%,'|| '"+bodcod+"' ||',%') " +
                            " and (CANALES='XX,' OR CANALES like '%,'|| c.CanCod ||',%') " +
                            " and (SUBCANALES='XX,' OR SUBCANALES like '%,'|| c.CanCod ||'-'|| c.CanSubCod || ',%') " +
                            " and (TAMANOS='XX,' OR TAMANOS like '%,'|| c.CliTamCan || ',%') " +
                            " and (CIUDADES='XX,' OR CIUDADES like '%,'|| c.CiuCod ||',%') " +
                            " and (SUCURSALES='XX,' OR SUCURSALES like '%,'||'"+ SucCod +"'||',%') " +
                            " and (CLIENTES='XX,' OR CLIENTES like '%,'|| c.nitsec ||'-'|| c.clisec || ',%') " +
                            " and (VENDEDORES='XX,' OR VENDEDORES like '%,'||'"+ vUsuario+"'|| ',%') " ;

                    Log.e("Entro aca: ","ConsultaPremiosBorrar"+ConsultaPremiosBorrar);





                    Cursor CurConsultaPremiosBorrar = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaPremiosBorrar, null); //and MOvParEscInd<>'IND'

                    if (CurConsultaPremiosBorrar.getCount() > 0) {
                        CurConsultaPremiosBorrar.moveToFirst();
                        do{
                            String DESPROGAPLESCTOT = "";
                            Integer descsec = CurConsultaPremiosBorrar.getInt(0);
                            String tipodesc = CurConsultaPremiosBorrar.getString(10);
                            if (tipodesc.equalsIgnoreCase("ART")){
                                BaseDeDatos.getWritableDatabase().execSQL("Delete from pedidoDesc where artsec='"+Codigo+"'  and nitsec='"+NitSec+"'  and DesSec="+descsec+" and pdyear="+time.year+" and pdmonth="+(time.month + 1)+" and pdday="+time.monthDay);
                                // /* and prefijo='"+Prefijo+"' */
                                // /* and clisec="+CliSec+" */
                            }


                            if (tipodesc.equalsIgnoreCase("GRU")){
                                BaseDeDatos.getWritableDatabase().execSQL("Delete from pedidoDesc where artsec in(select artsec from articulos where InvGruCod=(select InvGruCod from  articulos where artsec= '"+Codigo+"'))  and nitsec='"+NitSec+"'  and DesSec="+descsec+" and pdyear="+time.year+" and pdmonth="+(time.month + 1)+" and pdday="+time.monthDay);
                                // /* and prefijo='"+Prefijo+"' */
                                // /*and clisec="+CliSec+"*/
                            }
                            if (tipodesc.equalsIgnoreCase("SUB")){
                                BaseDeDatos.getWritableDatabase().execSQL("Delete from pedidoDesc where artsec in(select artsec from articulos where InvSubGruCod=(select InvSubGruCod from  articulos where artsec= '"+Codigo+"'))  and nitsec='"+NitSec+"'  and DesSec="+descsec+" and pdyear="+time.year+" and pdmonth="+(time.month + 1)+" and pdday="+time.monthDay);
                                // /* and  prefijo='"+Prefijo+"' */
                                // /* and clisec="+CliSec+" */

                            }

                            if (tipodesc.equalsIgnoreCase("FAM")){
                                String consulta = "Select ArtSec from pedidoDesc where artsec in(select artsec from articulos where InvFamCod=(select InvFamCod from  articulos where artsec= '"+Codigo+"'))  and nitsec='"+NitSec+"' and DesSec="+descsec+" and pdyear="+time.year+" and pdmonth="+(time.month + 1)+" and pdday="+time.monthDay ;
                                Cursor consultaborrar = BaseDeDatos.getWritableDatabase().rawQuery(consulta, null); //and MOvParEscInd<>'IND'
                                Log.e("Entro aca: ","consulta "+consulta);
                                if (consultaborrar.getCount() > 0) {
                                    consultaborrar.moveToFirst();
                                    do {
                                        Log.e("Entro aca: ", "dowjile" + consultaborrar.getString(0));
                                    } while (consultaborrar.moveToNext());
                                }

                                BaseDeDatos.getWritableDatabase().execSQL("Delete from pedidoDesc where artsec in(select artsec from articulos where InvFamCod=(select InvFamCod from  articulos where artsec= '"+Codigo+"'))  and nitsec='"+NitSec+"' and DesSec="+descsec+" and pdyear="+time.year+" and pdmonth="+(time.month + 1)+" and pdday="+time.monthDay);
                                // /*and  prefijo='"+Prefijo+"' */
                                // /* and clisec="+CliSec+" */
                            }

                        } while (CurConsultaPremiosBorrar.moveToNext());
                    }


                    String ConsultaReevaluar="select artsec from pedidoDesc where DesSec="+0 ;
                    Cursor CurConsultaReevaluar = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaReevaluar, null); //and MOvParEscInd<>'IND'
                    if (CurConsultaReevaluar.getCount() > 0) {
                        CurConsultaReevaluar.moveToFirst();
                        do{
                            bdartsec+= ",'"+CurConsultaPremiosBorrar.getString(0).trim()+"'";
                        } while (CurConsultaReevaluar.moveToNext());
                    }

                }catch(Exception e) {
                    int pp = 0;
                }

                Integer TcantFam=0;
                Double TcantFamCaj=0.0;
                Integer TcantSub=0;
                Double TcantSubCaj=0.0;
                Integer TcantGru=0;
                Double TcantGruCaj=0.0;
                Integer TcantArt=0;
                Double TcantArtCaj=0.0;

                String agInvGruCod="";
                String agSubGruCod="";
                String agInvFamCod="";

                String ConsultaArticulo = "select InvGruCod,InvSubGruCod,InvFamCod from articulos where artsec="+Codigo;
                Cursor CurConsultaArticulo = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaArticulo, null);
                if (CurConsultaArticulo.getCount() > 0){
                    CurConsultaArticulo.moveToFirst();
                    agInvGruCod=CurConsultaArticulo.getString(0);
                    agSubGruCod=CurConsultaArticulo.getString(1);
                    agInvFamCod=CurConsultaArticulo.getString(2);
                }


                String ConsultaTxtVen = "select " +
                        "total(case when InvFamCod='"+agInvFamCod+"' then (cant+(cantcaj*pedartemb)) else 0 end) fam," +
                        "total(case when InvFamCod='"+agInvFamCod+"' then ( ((cant+(cantcaj*pedartemb))*1.0/artemb ) ) else 0 end) famcaj," +
                        "total(case when InvSubGruCod='"+agSubGruCod+"' then (cant+(cantcaj*pedartemb)) else 0 end) sub," +
                        "total(case when InvSubGruCod='"+agSubGruCod+"' then ( ((cant+(cantcaj*pedartemb))*1.0/artemb ) ) else 0 end) subcaj," +
                        "total(case when InvGruCod='"+agInvGruCod+"' then (cant+(cantcaj*pedartemb)) else 0 end) gru, " +
                        "total(case when InvGruCod='"+agInvGruCod+"' then ( ((cant+(cantcaj*pedartemb))*1.0/artemb ) ) else 0 end) grucaj, " +
                        "total( case when a.ArtSec='"+Codigo+"'      then (cant+(cantcaj*pedartemb)) else 0 end) tart," +
                        "total( case when a.ArtSec='"+Codigo+"' then ( ((cant+(cantcaj*pedartemb))*1.0/artemb )) else 0 end) tartcaj " +
                        "from pedido p left join articulos a on a.artsec=p.artsec where artemb<>0 and rtrim(nitsec) = rtrim('" +NitSec+ "')  and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and cant+cantcaj>0 "; //and prefijo = '"+Prefijo+"'" //and clisec=" + CliSec+ "
                 Cursor MovParMixCntTotal = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaTxtVen, null);

                if (MovParMixCntTotal.getCount() > 0){
                    MovParMixCntTotal.moveToFirst();

                    TcantFam=MovParMixCntTotal.getInt(0);
                    TcantFamCaj=MovParMixCntTotal.getDouble(1); //+0.01
                    TcantSub=MovParMixCntTotal.getInt(2);
                    TcantSubCaj=MovParMixCntTotal.getDouble(3); //+0.01
                    TcantGru=MovParMixCntTotal.getInt(4);
                    TcantGruCaj=MovParMixCntTotal.getDouble(5); //+0.01
                    TcantArt=MovParMixCntTotal.getInt(6);

                    TcantArtCaj=MovParMixCntTotal.getDouble(7); //+0.01
                }

                Log.e("Total Cajas Fam: ",String.valueOf(TcantFamCaj));
                String ParMovTatTra = "TRA";
                Cursor traCursorUsuarios = BaseDeDatos.getReadableDatabase().rawQuery("select ParMovTatTra from usuarios", null);

                if (traCursorUsuarios.getCount() >0) {
                    traCursorUsuarios.moveToFirst();
                    ParMovTatTra = traCursorUsuarios.getString(0);
                }








                try {  // Descuentos por Articulo
                    Double DctoArt1=0.0;
                    Double DctoArt2=0.0;

                    Integer Tcant = (Unidades.intValue()+(Cajas*Embalaje)) ;

                    SDTResumenPedidos vSDTResumenPedidos=TotalesPedido(pContext,Prefijo,NitSec,0,"","","");
                    //tipodesc,descgru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,UNIDADNEG,CIUDADES,CLIENTES,VENDEDORES,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal



                    String ConsultaDesc="select DESCSEC,tipodesc,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal,DescLinPorDesLin,descagru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,CIUDADES,CLIENTES,DescArtDes,DesSecLin from Descuentos" +
                            " left join articulos a on artsec="+Codigo+" OR artsec in("+bdartsec+")" +
                            " left join Clientes c on nitsec='"+NitSec+"' and clisec="+CliSec+" " +
                            " where DESPROGAPLESCTOT = 'N' and descagru='ART' and DescPorMov = '"+ParMovTatTra+"' and descgru=InvGruCod and (SUBGRUPOS='XX,' OR SUBGRUPOS like '%,'|| a.InvSubGruCod ||',%') " +
                            " and (FAMILIAS='XX,' OR FAMILIAS like '%,'|| a.InvFamCod ||',%') " +
                            //" and (ARTICULOS='XX,' OR ARTICULOS like '%,'|| a.artsec ||',%') " +
                            " and (ARTICULOS='XX,' OR ARTICULOS like '%,'|| a.artsec ||',%') " +
                            " and (BODEGAS='XX,' OR BODEGAS like '%,'|| '"+bodcod+"' ||',%') " +
                            " and (CANALES='XX,' OR CANALES like '%,'|| c.CanCod ||',%') " +
                            " and (SUBCANALES='XX,' OR SUBCANALES like '%,'|| c.CanCod ||'-'|| c.CanSubCod || ',%') " +
                            " and (TAMANOS='XX,' OR TAMANOS like '%,'|| c.CliTamCan || ',%') " +
                            " and (SUCURSALES='XX,' OR SUCURSALES like '%,'||'"+ SucCod +"'||',%') " +
                            " and (CIUDADES='XX,' OR CIUDADES like '%,'|| c.CiuCod ||',%') " +
                            " and (CLIENTES='XX,' OR CLIENTES  like  '%,'|| c.nitsec ||'-'|| c.clisec || ',%') " +
                            " and (EXCLIENTES='XX,' OR EXCLIENTES  not like  '%,'|| c.nitsec ||'-'|| c.clisec || ',%') " + //andres
                            " and (VENDEDORES='XX,' OR VENDEDORES like '%,'||'"+ vUsuario+"'|| ',%') " +
                            " and ((DescLinDesVal<="+vSDTResumenPedidos.Subtotal+" and DescLinHasVal>="+vSDTResumenPedidos.Subtotal+") or DescLinDesVal+DescLinHasVal=0)"  +
                            " and (( ((DescLinDesUni<="+TcantArt+" )) " +
                            " and ( (DescLinHasUni>="+TcantArt+" )) and DescLin1CajMen1<>'S') or DescLinDesUni+DescLinHasUni=0 or DescLin1CajMen1='S' )"  +
                            " and (( ((DescLinDesUni<="+TcantArt+" )  ) " +
                            " and ((ArtEmb>"+TcantArt+" ) ) and DescLin1CajMen1='S') or DescLinDesUni+DescLinHasUni=0 or DescLin1CajMen1<>'S')"  +
                            " and (( ( (DescLinDesCaj*ArtEmb<="+TcantArt+"  )   ) " +
                            " and ( (DescLinHasCaj*ArtEmb>="+TcantArt+" )  )     )  or DescLinDesCaj+DescLinHasCaj=0)";



                    Cursor MovParArt3 = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaDesc, null); //and MOvParEscInd<>'IND'

                    Double DctoArtC1=0.0;
                    Double DctoArtC2=0.0;
                    Double DctoArtC3=0.0;
                    Double DctoArtC4=0.0;
                    Double tmDctoArtC1=0.0;
                    Double tmDctoArtC2=0.0;
                    Double tmDctoArtC3=0.0;
                    Double tmDctoArtC4=0.0;
                    if (MovParArt3.getCount() > 0) {
                        MovParArt3.moveToFirst();
                        do{
                            DctoArt2 = MovParArt3.getDouble(9);
                            Integer descsec = MovParArt3.getInt(0);
                            String DescArtDes = MovParArt3.getString(19); //Andres
                            int DesSecLin = MovParArt3.getInt(20);
                            String tipodesc = MovParArt3.getString(1);
                            String Canales = MovParArt3.getString(14);
                            String SubCanales = MovParArt3.getString(15);
                            tmDctoArtC1=0.0;
                            tmDctoArtC2=0.0;
                            tmDctoArtC3=0.0;
                            tmDctoArtC4=0.0;
                            /*Log.e("entrp Subtotal:11",String.valueOf(descsec));
                            Log.e("Canales: ",MovParArt3.getString(14));
                            Log.e("SubCanales: ",MovParArt3.getString(15));
                            Log.e("clientes: ",MovParArt3.getString(18));
                            Log.e("CanCod: ",MovParArt3.getString(21));
                            Log.e("CanSubCod: ",MovParArt3.getString(22));*/

                             if (tipodesc.equalsIgnoreCase("LIN")){
                                 DctoArtC1+=DctoArt2;
                                 tmDctoArtC1=DctoArt2;
                             }
                            if (tipodesc.equalsIgnoreCase("PRO") || (tipodesc.equalsIgnoreCase("ART") && DescArtDes.equalsIgnoreCase("DESPRO"))){ //Andres
                                DctoArtC2+=DctoArt2;
                                tmDctoArtC2=DctoArt2;
                            }
                            if (tipodesc.equalsIgnoreCase("EMP")){
                                DctoArtC3+=DctoArt2;
                                tmDctoArtC3=DctoArt2;
                            }
                            if (tipodesc.equalsIgnoreCase("ART") && !DescArtDes.equalsIgnoreCase("DESPRO")){//Andres
                                DctoArtC4+=DctoArt2;
                                tmDctoArtC4=DctoArt2;
                            }

                             //Error aca ==========================================================================================================================


                            String ConsultaArticulosRela = "select p.ArtSec,p.CliSec,p.prefijo from pedido p left join articulos a on a.artsec=p.artsec where cant+cantcaj<>0 and p.artsec='"+Codigo+"'  and  nitsec='" + NitSec + "' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay   ;
                            ///* and clisec=" + CliSec  + " */
                            // and prefijo ='" + Prefijo + "'";
//and Clisec in (select CliSec from clientes c where  c.nitsec='" + NitSec + "' and CanCod = "+eCancod+" )
                            Cursor CurConsultaArticulosRela = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaArticulosRela, null);
                            String ArtSecADesc="";
                            Integer CliSecDesc=0;
                            String prefijoDesc="";
                            if (CurConsultaArticulosRela.getCount() > 0){
                                CurConsultaArticulosRela.moveToFirst();
                                do{
                                    ArtSecADesc=CurConsultaArticulosRela.getString(0);
                                    CliSecDesc=CurConsultaArticulosRela.getInt(1);
                                    prefijoDesc=CurConsultaArticulosRela.getString(2);
                                    String InserPedido1 = "insert into pedidoDesc(DesSec,prefijo,nitsec,clisec,artsec,pdyear,pdmonth,pdday,TipoDesc,DesSecLin,pordescapli1,pordescapli2,pordescapli3,pordescapli4) values("+descsec+",'"+prefijoDesc+"','"+NitSec+"',"+CliSecDesc+",'"+ArtSecADesc+"',"+time.year+","+(time.month + 1)+","+time.monthDay+",'"+tipodesc+"',"+DesSecLin+","+tmDctoArtC1+","+tmDctoArtC2+","+tmDctoArtC3+","+tmDctoArtC4+")";
                                    Log.e("InsertPEdido1",InserPedido1);
                                    BaseDeDatos.getWritableDatabase().execSQL(InserPedido1);
                                } while (CurConsultaArticulosRela.moveToNext());
                            }

                       //     BaseDeDatos.getWritableDatabase().execSQL("insert into pedidoDesc(DesSec,prefijo,nitsec,clisec,artsec,pdyear,pdmonth,pdday,pordescapli1,pordescapli2,pordescapli3,pordescapli4) values("+descsec+",'"+Prefijo+"','"+NitSec+"',"+CliSec+",'"+Codigo+"',"+time.year+","+(time.month + 1)+","+time.monthDay+","+tmDctoArtC1+","+tmDctoArtC2+","+tmDctoArtC3+","+tmDctoArtC4+")");

                            //artsec='"+Codigo+"' and  nitsec='" + NitSec + "' and clisec=" + CliSec + "
                            //  pordescapli1 numeric(8,4),pordescapli2 numeric(8,4),pordescapli3 numeric(8,4),pordescapli4 numeric(8,4)
                        } while (MovParArt3.moveToNext());
                    }
                  //  if (DctoArtC1!=0 || DctoArtC2!=0 || DctoArtC3!=0 || DctoArtC4!=0 ) {
 /*                   BaseDeDatos.getWritableDatabase().execSQL("update pedido set " +
                            " pordesc=CASE WHEN "+DctoArtC1+" >= pordesc and pordesc<>0 and pordesc<pordescno  THEN pordesc " +
                            " WHEN  "+DctoArtC1+" < pordesc and pordesc<=pordescno and pordesc<>0 and "+DctoArtC1+"<>0 THEN pordesc ELSE "+DctoArtC1+" END," +
                            " pordescno="+DctoArtC1+"," +
                            " pordesc2=CASE WHEN "+DctoArtC2+" >= pordesc2 and pordesc2<>0 and pordesc2<pordesc2no  THEN pordesc2 " +
                            " WHEN  "+DctoArtC2+" < pordesc2 and pordesc2<=pordesc2no and pordesc2<>0 and "+DctoArtC2+"<>0 THEN pordesc2 ELSE "+DctoArtC2+" END," +
                            " pordesc2no="+DctoArtC2+"," +
                            " pordesc3=CASE WHEN "+DctoArtC3+" >= pordesc3 and pordesc3<>0 and pordesc3<pordesc3no  THEN pordesc3" +
                            " WHEN  "+DctoArtC3+" < pordesc3 and pordesc3<=pordesc3no and pordesc3<>0 and "+DctoArtC3+"<>0 THEN pordesc3 ELSE "+DctoArtC3+" END," +
                            " pordesc3no="+DctoArtC3+"," +
                            " pordesc4=CASE WHEN "+DctoArtC4+" >= pordesc4 and pordesc4<>0 and pordesc4<pordesc4no  THEN pordesc4 " +
                            " WHEN  "+DctoArtC4+" < pordesc4 and pordesc4<=pordesc4no and pordesc4<>0 and "+DctoArtC4+"<>0 THEN pordesc4 ELSE "+DctoArtC4+" END," +
                            " pordesc4no="+DctoArtC4+"" +
                            " where artsec='"+Codigo+"' and  nitsec='" + NitSec + "' and clisec=" + CliSec + " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo ='" + Prefijo + "'");

  /*

  */

                    //BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc="+DctoArtC1+" ,pordesc2="+DctoArtC2+",pordesc3="+DctoArtC3+",pordesc4="+DctoArtC4+" where artsec='"+Codigo+"' and  nitsec='" + NitSec + "' and clisec=" + CliSec + " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo ='" + Prefijo + "'");


                  //  }
                }catch(Exception e) {
                    int pp = 0;
                }








                time.setToNow();

                try {  // Descuentos por GRUPO
                    Double DctoArt1=0.0;
                    Double DctoArt2=0.0;

                    Integer Tcant = (Unidades.intValue()+(Cajas*Embalaje)) ;

                    SDTResumenPedidos vSDTResumenPedidos=TotalesPedido(pContext,Prefijo,NitSec,CliSec,"","","");



                    //tipodesc,descgru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,UNIDADNEG,CIUDADES,CLIENTES,VENDEDORES,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal
                    String ConsultaDesc="select DESCSEC,tipodesc,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal,DescLinPorDesLin,descagru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,CIUDADES,CLIENTES,DescArtDes,DesSecLin from Descuentos" +
                            " left join articulos a on artsec="+Codigo+"" +
                            " left join Clientes c on nitsec='"+NitSec+"' and clisec="+CliSec+" " +
                            " where descagru='GRU"  +
                            "' and DescPorMov = '"+ParMovTatTra+"' and descgru=InvGruCod and (SUBGRUPOS='XX,' OR SUBGRUPOS like '%,'|| a.InvSubGruCod ||',%') " +
                            " and (FAMILIAS='XX,' OR FAMILIAS like '%,'|| a.InvFamCod ||',%') " +
                            " and (ARTICULOS='XX,' OR ARTICULOS like '%,'|| a.artsec ||',%') " +
                            " and (CANALES='XX,' OR CANALES like '%,'|| c.CanCod ||',%') " +
                            " and (BODEGAS='XX,' OR BODEGAS like '%,'|| '"+bodcod+"' ||',%') " +
                            " and (SUBCANALES='XX,' OR SUBCANALES like '%,'|| c.CanCod ||'-'|| c.CanSubCod || ',%') " +
                            " and (TAMANOS='XX,' OR TAMANOS like '%,'|| c.CliTamCan || ',%') " +
                            " and (SUCURSALES='XX,' OR SUCURSALES like '%,'|| '"+ SucCod +"' ||',%') " +
                            " and (CIUDADES='XX,' OR CIUDADES like '%,'|| c.CiuCod ||',%') " +
                            " and (CLIENTES='XX,' OR CLIENTES  like '%,'|| c.nitsec ||'-'|| c.clisec || ',%') " +
                            " and (EXCLIENTES='XX,' OR EXCLIENTES  not like  '%,'|| c.nitsec ||'-'|| c.clisec || ',%') " + //andres
                            " and (VENDEDORES='XX,' OR VENDEDORES like '%,'||'"+ vUsuario+"'|| ',%') " +
                            " and ((DescLinDesVal<="+vSDTResumenPedidos.Subtotal+" and DescLinHasVal>="+vSDTResumenPedidos.Subtotal+") or DescLinDesVal+DescLinHasVal=0)"  +
                            " and ((((DescLinDesUni<="+TcantGru+")) " +
                            " and ((DescLinHasUni>="+TcantGru+" )) and DescLin1CajMen1<>'S') or DescLinDesUni+DescLinHasUni=0 or DescLin1CajMen1='S' )"  +
                            " and ((((DescLinDesUni<="+TcantGru+" )) " +
                            " and (("+TcantGruCaj+"<1 ) ) and DescLin1CajMen1='S') or DescLinDesUni+DescLinHasUni=0 or DescLin1CajMen1<>'S')"  +
                            " and ((((DescLinDesCaj<="+TcantGruCaj+" )) " +
                            " and ((DescLinHasCaj>="+TcantGruCaj+")))  or DescLinDesCaj+DescLinHasCaj=0)";
                    Cursor MovParArt3 = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaDesc, null); //and MOvParEscInd<>'IND'
                    //TcantSub=MovParMixCntTotal.getInt(2);
                    //TcantSubCaj=MovParMixCntTotal.getInt(3);
                    Double DctoArtC1=0.0;
                    Double DctoArtC2=0.0;
                    Double DctoArtC3=0.0;
                    Double DctoArtC4=0.0;
                    Double tmDctoArtC1=0.0;
                    Double tmDctoArtC2=0.0;
                    Double tmDctoArtC3=0.0;
                    Double tmDctoArtC4=0.0;
                    if (MovParArt3.getCount() > 0) {
                        MovParArt3.moveToFirst();
                        do{
                            DctoArt2 = MovParArt3.getDouble(9);
                            Integer descsec = MovParArt3.getInt(0);
                            String tipodesc = MovParArt3.getString(1);
                            String DescArtDes = MovParArt3.getString(19); //Andres
                            int DesSecLin = MovParArt3.getInt(20); //Andres
                            String filSubgrupos = MovParArt3.getString(11);

                            tmDctoArtC1=0.0;
                            tmDctoArtC2=0.0;
                            tmDctoArtC3=0.0;
                            tmDctoArtC4=0.0;


                            Log.e("vSDTResumen: ",String.valueOf(vSDTResumenPedidos.Subtotal));
                            Log.e("DescLinDesUni: ",String.valueOf(TcantGru));
                            Log.e("TcantGruCaj: ",String.valueOf(TcantGruCaj));
                            Log.e("filSubgrupos: ",filSubgrupos);
                            Log.e("SubCanales: ",MovParArt3.getString(15));
                            Log.e("clientes: ",MovParArt3.getString(18));


                            Log.e("entrp Subtotal:2 ",String.valueOf(descsec));
                            if (tipodesc.equalsIgnoreCase("LIN")){
                                DctoArtC1+=DctoArt2;
                                tmDctoArtC1=DctoArt2;
                            }
                            if (tipodesc.equalsIgnoreCase("PRO")|| (tipodesc.equalsIgnoreCase("ART") && DescArtDes.equalsIgnoreCase("DESPRO"))){ //Andres
                                DctoArtC2+=DctoArt2;
                                tmDctoArtC2=DctoArt2;
                            }
                            if (tipodesc.equalsIgnoreCase("EMP")){
                                DctoArtC3+=DctoArt2;
                                tmDctoArtC3=DctoArt2;
                            }
                            if (tipodesc.equalsIgnoreCase("ART") && !DescArtDes.equalsIgnoreCase("DESPRO")){//Andres
                                DctoArtC4+=DctoArt2;
                                tmDctoArtC4=DctoArt2;
                            }
                            String ConsultaArticulosRela = "select p.ArtSec,p.CliSec,p.prefijo, InvSubGruCod from pedido p left join articulos a on a.artsec=p.artsec where cant+cantcaj<>0 and InvGruCod='"+agInvGruCod+"' and ('"+filSubgrupos+"' like '%,'|| InvSubGruCod ||',%' or '"+filSubgrupos+"'='XX,')   and  nitsec='" + NitSec + "' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay   ;
                            ///* and clisec=" + CliSec  + " */
                            // and prefijo ='" + Prefijo + "'";

                            Cursor CurConsultaArticulosRela = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaArticulosRela, null);
                            String ArtSecADesc="";
                            Integer CliSecDesc=0;
                            String prefijoDesc="";


                            if (CurConsultaArticulosRela.getCount() > 0){
                                CurConsultaArticulosRela.moveToFirst();
                                do{
                                    ArtSecADesc=CurConsultaArticulosRela.getString(0);
                                    CliSecDesc=CurConsultaArticulosRela.getInt(1);
                                    prefijoDesc=CurConsultaArticulosRela.getString(2);
                                    String InsertPeido2 ="insert into pedidoDesc(DesSec,prefijo,nitsec,clisec,artsec,pdyear,pdmonth,pdday,TipoDesc,DesSecLin,pordescapli1,pordescapli2,pordescapli3,pordescapli4,kardesgen) values("+descsec+",'"+prefijoDesc+"','"+NitSec+"',"+CliSecDesc+",'"+ArtSecADesc+"',"+time.year+","+(time.month + 1)+","+time.monthDay+",'"+tipodesc+"',"+DesSecLin+","+tmDctoArtC1+","+tmDctoArtC2+","+tmDctoArtC3+","+tmDctoArtC4+",'N')";
                                    Log.e("InserPEdido subgr ",CurConsultaArticulosRela.getString(3));
                                    BaseDeDatos.getWritableDatabase().execSQL(InsertPeido2);
                                } while (CurConsultaArticulosRela.moveToNext());
                            }

                            //artsec='"+Codigo+"' and  nitsec='" + NitSec + "' and clisec=" + CliSec + "
                            //  pordescapli1 numeric(8,4),pordescapli2 numeric(8,4),pordescapli3 numeric(8,4),pordescapli4 numeric(8,4)
                        } while (MovParArt3.moveToNext());
                    }
                    //  if (DctoArtC1!=0 || DctoArtC2!=0 || DctoArtC3!=0 || DctoArtC4!=0 ) {
                    DctoArtC1=0.0;
                    DctoArtC2=0.0;
                    DctoArtC3=0.0;
                    DctoArtC4=0.0;


                    //BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc="+DctoArtC1+" ,pordesc2="+DctoArtC2+",pordesc3="+DctoArtC3+",pordesc4="+DctoArtC4+" where artsec='"+Codigo+"' and  nitsec='" + NitSec + "' and clisec=" + CliSec + " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo ='" + Prefijo + "'");


                    //  }
                }catch(Exception e) {
                    int pp = 0;
                }
                time.setToNow();

                try {  // Descuentos por Familia



                    Log.e("entrp agrupado TcantFam",String.valueOf(TcantFam));
                    Log.e("entrp agrupado TcantFamCaj",String.valueOf(TcantFamCaj));

                    Double DctoArt1=0.0;
                    Double DctoArt2=0.0;

                    Integer Tcant = (Unidades.intValue()+(Cajas*Embalaje)) ;

                    SDTResumenPedidos vSDTResumenPedidos=TotalesPedido(pContext,Prefijo,NitSec,CliSec,"","","");
                    //tipodesc,descgru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,UNIDADNEG,CIUDADES,CLIENTES,VENDEDORES,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal
                    String ConsultaDesc="select DESCSEC,tipodesc,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal,DescLinPorDesLin,descagru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,CIUDADES,CLIENTES, " +
                            " ifnull((select " +
                            " total( (cant+(cantcaj*pedartemb)) ) fam" +
                            " from pedido p inner join articulos a on a.artsec=p.artsec where  InvFamCod='"+agInvFamCod+"' and (ARTICULOS like '%,'|| p.artsec ||',%' or ARTICULOS='XX,') and artemb<>0 and rtrim(nitsec) = rtrim('" +NitSec+ "') and clisec=" + CliSec+ " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and cant+cantcaj>0 and prefijo = '"+Prefijo+"'  ),0) fam, " +
                            " ifnull((select " +
                            " total(( ((cant+(cantcaj*pedartemb))*1.0/artemb ) ) ) famcaj " +
                            " from pedido p inner join articulos a on a.artsec=p.artsec where InvFamCod='"+agInvFamCod+"' and  (ARTICULOS like '%,'|| p.artsec ||',%' or ARTICULOS='XX,') and artemb<>0 and rtrim(nitsec) = rtrim('" +NitSec+ "') and clisec=" + CliSec+ " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and cant+cantcaj>0 and prefijo = '"+Prefijo+"'  ),0) famcaj,DescArtDes,DesSecLin " +
                            " from Descuentos" +
                            " left join articulos a on artsec="+Codigo+"" +
                            " left join Clientes c on nitsec='"+NitSec+"' and clisec="+CliSec+" " +
                         //   " left join (select " +
                         //   " total(cant+(cantcaj*pedartemb)) fam,((cant+(cantcaj*pedartemb))*1.0/artemb ) famcaj " +
                        //    " from pedido p left join articulos a on a.artsec=p.artsec where InvFamCod='"+agInvFamCod+"' (ARTICULOS like '%,'|| p.artsec ||',%' or ARTICULOS='XX,') and artemb<>0 and rtrim(nitsec) = rtrim('" +NitSec+ "') and clisec=" + CliSec+ " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and cant+cantcaj>0 and prefijo = '"+Prefijo+"') cantpedidos " +
                            " where descagru='FAM'" +
                            "  and DescPorMov = '"+ParMovTatTra+"'  and  descgru=InvGruCod and (SUBGRUPOS='XX,' OR SUBGRUPOS like '%,'|| a.InvSubGruCod ||',%') " +
                            " and (FAMILIAS='XX,' OR FAMILIAS like '%,'|| a.InvFamCod ||',%') " +
                            " and (ARTICULOS='XX,' OR ARTICULOS like '%,'|| a.artsec ||',%') " +
                            " and (CANALES='XX,' OR CANALES like '%,'|| c.CanCod ||',%') " +
                            " and (BODEGAS='XX,' OR BODEGAS like '%,'|| '"+bodcod+"' ||',%') " +
                            " and (SUBCANALES='XX,' OR SUBCANALES like '%,'|| c.CanCod ||'-'|| c.CanSubCod || ',%') " +
                            " and (TAMANOS='XX,' OR TAMANOS like '%,'|| c.CliTamCan || ',%') " +
                            " and (SUCURSALES='XX,' OR SUCURSALES like '%,'|| '"+ SucCod +"' ||',%') " +
                            " and (CIUDADES='XX,' OR CIUDADES like '%,'|| c.CiuCod ||',%') " +
                            " and (CLIENTES='XX,' OR CLIENTES  like '%,'|| c.nitsec ||'-'|| c.clisec || ',%') " +
                            " and (EXCLIENTES='XX,' OR EXCLIENTES  not like  '%,'|| c.nitsec ||'-'|| c.clisec || ',%') " + //andres
                            " and (VENDEDORES='XX,' OR VENDEDORES like '%,'||'"+ vUsuario+"'|| ',%') " +
                            " and ((DescLinDesVal<="+vSDTResumenPedidos.Subtotal+" and DescLinHasVal>="+vSDTResumenPedidos.Subtotal+") or DescLinDesVal+DescLinHasVal=0)"  +
                            " and ((((DescLinDesUni<="+TcantFam+")) " +
                            " and ((DescLinHasUni>="+TcantFam+" )) and DescLin1CajMen1<>'S') or DescLinDesUni+DescLinHasUni=0 or DescLin1CajMen1='S' )"  +
                            " and ((((DescLinDesUni<="+TcantFam+" )) " +
                            " and (("+TcantFamCaj+"<1 ) ) and DescLin1CajMen1='S') or DescLinDesUni+DescLinHasUni=0 or DescLin1CajMen1<>'S')"  +
                            " and ((((DescLinDesCaj<="+TcantFamCaj+" )) " +
                            " and ((DescLinHasCaj>="+TcantFamCaj+")))  or DescLinDesCaj+DescLinHasCaj=0)";
                    Cursor MovParArt3 = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaDesc, null); //and MOvParEscInd<>'IND'
                    //TcantSub=MovParMixCntTotal.getInt(2);
                    //TcantSubCaj=MovParMixCntTotal.getInt(3);
                    Double DctoArtC1=0.0;
                    Double DctoArtC2=0.0;
                    Double DctoArtC3=0.0;
                    Double DctoArtC4=0.0;
                    Double tmDctoArtC1=0.0;
                    Double tmDctoArtC2=0.0;
                    Double tmDctoArtC3=0.0;
                    Double tmDctoArtC4=0.0;




                    if (MovParArt3.getCount() > 0) {
                        MovParArt3.moveToFirst();

                        do{
                            DctoArt2 = MovParArt3.getDouble(9);
                            Integer descsec = MovParArt3.getInt(0);
                            String tipodesc = MovParArt3.getString(1);
                            String filArticulos = MovParArt3.getString(13);
                            String DescArtDes = MovParArt3.getString(21); //Andres
                            int DesSecLin = MovParArt3.getInt(22); //Andres
                            tmDctoArtC1=0.0;
                            tmDctoArtC2=0.0;
                            tmDctoArtC3=0.0;
                            tmDctoArtC4=0.0;


                            Log.e("entrp agrupado sec",String.valueOf(descsec));
                            Log.e("entrp agrupado TcantFam2",String.valueOf(TcantFam));
                            Log.e("entrp agrupado TcantFamCaj2",String.valueOf(TcantFamCaj));





                            if (tipodesc.equalsIgnoreCase("LIN")){
                                DctoArtC1+=DctoArt2;
                                tmDctoArtC1=DctoArt2;
                            }
                            if (tipodesc.equalsIgnoreCase("PRO") || (tipodesc.equalsIgnoreCase("ART") && DescArtDes.equalsIgnoreCase("DESPRO"))){ //Andres
                                DctoArtC2+=DctoArt2;
                                tmDctoArtC2=DctoArt2;
                            }
                            if (tipodesc.equalsIgnoreCase("EMP")){
                                DctoArtC3+=DctoArt2;
                                tmDctoArtC3=DctoArt2;
                            }
                            if (tipodesc.equalsIgnoreCase("ART")  && !DescArtDes.equalsIgnoreCase("DESPRO")){//Andres
                                DctoArtC4+=DctoArt2;
                                tmDctoArtC4=DctoArt2;
                            }

                            String ConsultaArticulosRela = "select p.ArtSec,p.CliSec,p.prefijo from pedido p left join articulos a on a.artsec=p.artsec" +
                                    " where cant+cantcaj<>0 and InvFamCod='"+agInvFamCod+"'  and ('"+filArticulos+"' like '%,'|| p.artsec ||',%' or '"+filArticulos+"'='XX,')  " +
                                    " and  nitsec='" + NitSec + "'  and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay  ;


                            Log.e("filArticulos",filArticulos);

                            ///* and clisec=" + CliSec  + " */
                                    //" and prefijo ='" + Prefijo + "'" */ ;
                            Cursor CurConsultaArticulosRela = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaArticulosRela, null);
                            String ArtSecADesc="";
                            Integer CliSecDesc=0;
                            String prefijoDesc="";
                            if (CurConsultaArticulosRela.getCount() > 0){
                                CurConsultaArticulosRela.moveToFirst();
                                do{
                                    ArtSecADesc=CurConsultaArticulosRela.getString(0);
                                    CliSecDesc=CurConsultaArticulosRela.getInt(1);
                                    prefijoDesc=CurConsultaArticulosRela.getString(2);
                                    Log.e("ArtFam",ArtSecADesc);
                                    String InsertPEdido ="insert into pedidoDesc(DesSec,prefijo,nitsec,clisec,artsec,pdyear,pdmonth,pdday,TipoDesc,DesSecLin,pordescapli1,pordescapli2,pordescapli3,pordescapli4) values("+descsec+",'"+prefijoDesc+"','"+NitSec+"',"+CliSecDesc+",'"+ArtSecADesc+"',"+time.year+","+(time.month + 1)+","+time.monthDay+",'"+tipodesc+"',"+DesSecLin+","+tmDctoArtC1+","+tmDctoArtC2+","+tmDctoArtC3+","+tmDctoArtC4+")";
                                    Log.e("InsertPedido3",InsertPEdido);
                                    BaseDeDatos.getWritableDatabase().execSQL(InsertPEdido );
                                } while (CurConsultaArticulosRela.moveToNext());
                            }

                            //artsec='"+Codigo+"' and  nitsec='" + NitSec + "' and clisec=" + CliSec + "
                            //  pordescapli1 numeric(8,4),pordescapli2 numeric(8,4),pordescapli3 numeric(8,4),pordescapli4 numeric(8,4)
                        } while (MovParArt3.moveToNext());
                    }
                    //  if (DctoArtC1!=0 || DctoArtC2!=0 || DctoArtC3!=0 || DctoArtC4!=0 ) {
                    DctoArtC1=0.0;
                    DctoArtC2=0.0;
                    DctoArtC3=0.0;
                    DctoArtC4=0.0;


                    //BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc="+DctoArtC1+" ,pordesc2="+DctoArtC2+",pordesc3="+DctoArtC3+",pordesc4="+DctoArtC4+" where artsec='"+Codigo+"' and  nitsec='" + NitSec + "' and clisec=" + CliSec + " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo ='" + Prefijo + "'");


                    //  }
                }catch(Exception e) {
                    Log.e("Error FAM",e.toString());
                    int pp = 0;
                }
                time.setToNow();

                try {  // Descuentos por Subgrupo
                    Double DctoArt1=0.0;
                    Double DctoArt2=0.0;

                    Integer Tcant = (Unidades.intValue()+(Cajas*Embalaje)) ;

                    SDTResumenPedidos vSDTResumenPedidos=TotalesPedido(pContext,Prefijo,NitSec,CliSec,"","","");
                    //tipodesc,descgru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,UNIDADNEG,CIUDADES,CLIENTES,VENDEDORES,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal
                    String ConsultaDesc="select DESCSEC,tipodesc,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal,DescLinPorDesLin,descagru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,CIUDADES,CLIENTES,DescArtDes, DesSecLin from Descuentos" +
                            " left join articulos a on artsec="+Codigo+"" +
                            " left join Clientes c on nitsec='"+NitSec+"' and clisec="+CliSec+" " +
                            " where descagru='SUB'  and DescPorMov = '"+ParMovTatTra+"'  and descgru=InvGruCod and (SUBGRUPOS='XX,' OR SUBGRUPOS like '%,'|| a.InvSubGruCod ||',%') " +
                            " and (FAMILIAS='XX,' OR FAMILIAS like '%,'|| a.InvFamCod ||',%') " +
                            " and (ARTICULOS='XX,' OR ARTICULOS like '%,'|| a.artsec ||',%') " +
                            " and (CANALES='XX,' OR CANALES like '%,'|| c.CanCod ||',%') " +
                            " and (BODEGAS='XX,' OR BODEGAS like '%,'|| '"+bodcod+"' ||',%') " +
                            " and (SUBCANALES='XX,' OR SUBCANALES like '%,'|| c.CanCod ||'-'|| c.CanSubCod || ',%') " +
                            " and (TAMANOS='XX,' OR TAMANOS like '%,'|| c.CliTamCan || ',%') " +
                            " and (SUCURSALES='XX,' OR SUCURSALES like '%,'|| '"+ SucCod +"' ||',%') " +
                            " and (CIUDADES='XX,' OR CIUDADES like '%,'|| c.CiuCod ||',%') " +
                            " and (CLIENTES='XX,' OR CLIENTES  like '%,'|| c.nitsec ||'-'|| c.clisec || ',%') " +
                            " and (EXCLIENTES='XX,' OR EXCLIENTES  not like  '%,'|| c.nitsec ||'-'|| c.clisec || ',%') " + //andres
                            " and (VENDEDORES='XX,' OR VENDEDORES like '%,'||'"+ vUsuario+"'|| ',%') " +
                            " and ((DescLinDesVal<="+vSDTResumenPedidos.Subtotal+" and DescLinHasVal>="+vSDTResumenPedidos.Subtotal+") or DescLinDesVal+DescLinHasVal=0)"  +
                            " and ((((DescLinDesUni<="+TcantSub+")) " +
                            " and ((DescLinHasUni>="+TcantSub+" )) and DescLin1CajMen1<>'S') or DescLinDesUni+DescLinHasUni=0 or DescLin1CajMen1='S' )"  +
                            " and ((((DescLinDesUni<="+TcantSub+" )) " +
                            " and (("+TcantSubCaj+"<1 ) ) and DescLin1CajMen1='S') or DescLinDesUni+DescLinHasUni=0 or DescLin1CajMen1<>'S')"  +
                            " and ((((DescLinDesCaj<="+TcantSubCaj+" )) " +
                            " and ((DescLinHasCaj>="+TcantSubCaj+")))  or DescLinDesCaj+DescLinHasCaj=0)";
                    Cursor MovParArt3 = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaDesc, null); //and MOvParEscInd<>'IND'
                    //TcantSub=MovParMixCntTotal.getInt(2);
                    //TcantSubCaj=MovParMixCntTotal.getInt(3);
                    Double DctoArtC1=0.0;
                    Double DctoArtC2=0.0;
                    Double DctoArtC3=0.0;
                    Double DctoArtC4=0.0;
                    Double tmDctoArtC1=0.0;
                    Double tmDctoArtC2=0.0;
                    Double tmDctoArtC3=0.0;
                    Double tmDctoArtC4=0.0;
                    if (MovParArt3.getCount() > 0) {
                        MovParArt3.moveToFirst();
                        do{
                            DctoArt2 = MovParArt3.getDouble(9);
                            Integer descsec = MovParArt3.getInt(0);
                            String tipodesc = MovParArt3.getString(1);
                            String filarticulos = MovParArt3.getString(13);
                            String DescArtDes = MovParArt3.getString(19); //Andres
                            Integer DesSecLin = MovParArt3.getInt(20);//Andres

                            Log.e("entrp Subtotal:4 ",String.valueOf(descsec));
                            tmDctoArtC1=0.0;
                            tmDctoArtC2=0.0;
                            tmDctoArtC3=0.0;
                            tmDctoArtC4=0.0;

                            if (tipodesc.equalsIgnoreCase("LIN")){
                                DctoArtC1+=DctoArt2;
                                tmDctoArtC1=DctoArt2;
                            }
                            if (tipodesc.equalsIgnoreCase("PRO") || (tipodesc.equalsIgnoreCase("ART") && DescArtDes.equalsIgnoreCase("DESPRO"))){ //Andres
                                DctoArtC2+=DctoArt2;
                                tmDctoArtC2=DctoArt2;
                            }
                            if (tipodesc.equalsIgnoreCase("EMP")){
                                DctoArtC3+=DctoArt2;
                                tmDctoArtC3=DctoArt2;
                            }
                            if (tipodesc.equalsIgnoreCase("ART")  && !DescArtDes.equalsIgnoreCase("DESPRO")){//Andres
                                DctoArtC4+=DctoArt2;
                                tmDctoArtC4=DctoArt2;
                            }

                            String ConsultaArticulosRela = "select p.ArtSec,p.CliSec,p.prefijo from pedido p left join articulos a on a.artsec=p.artsec " +
                                    " where cant+cantcaj<>0 and ('"+filarticulos+"'='XX,' OR '"+filarticulos+"' like '%,'|| p.artsec ||',%')  and " +
                                    " InvSubGruCod='"+agSubGruCod+"'  and  nitsec='" + NitSec + "'    and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay   ;



                                    // and clisec=" + CliSec + "   and prefijo ='" + Prefijo + "'";
                            Cursor CurConsultaArticulosRela = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaArticulosRela, null);
                            String ArtSecADesc="";
                            Integer CliSecDesc=0;
                            String prefijoDesc="";
                            if (CurConsultaArticulosRela.getCount() > 0){
                                CurConsultaArticulosRela.moveToFirst();
                                do{
                                ArtSecADesc=CurConsultaArticulosRela.getString(0);
                                CliSecDesc=CurConsultaArticulosRela.getInt(1);
                                prefijoDesc=CurConsultaArticulosRela.getString(2);

                                String insertpedDesc = "insert into pedidoDesc(DesSec,prefijo,nitsec,clisec,artsec,pdyear,pdmonth,pdday,TipoDesc,DesSecLin,pordescapli1,pordescapli2,pordescapli3,pordescapli4) values("+descsec+",'"+prefijoDesc+"','"+NitSec+"',"+CliSecDesc+",'"+ArtSecADesc+"',"+time.year+","+(time.month + 1)+","+time.monthDay+",'"+tipodesc+"',"+DesSecLin+","+tmDctoArtC1+","+tmDctoArtC2+","+tmDctoArtC3+","+tmDctoArtC4+")";
                                    Log.e("InsertPedido4",insertpedDesc);

                                BaseDeDatos.getWritableDatabase().execSQL(insertpedDesc);
                                } while (CurConsultaArticulosRela.moveToNext());
                            }

                            //artsec='"+Codigo+"' and  nitsec='" + NitSec + "' and clisec=" + CliSec + "
                            //  pordescapli1 numeric(8,4),pordescapli2 numeric(8,4),pordescapli3 numeric(8,4),pordescapli4 numeric(8,4)
                        } while (MovParArt3.moveToNext());
                    }
                    //  if (DctoArtC1!=0 || DctoArtC2!=0 || DctoArtC3!=0 || DctoArtC4!=0 ) {

                    //BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc="+DctoArtC1+" ,pordesc2="+DctoArtC2+",pordesc3="+DctoArtC3+",pordesc4="+DctoArtC4+" where artsec='"+Codigo+"' and  nitsec='" + NitSec + "' and clisec=" + CliSec + " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo ='" + Prefijo + "'");

                    String TablaPedidosDesc = "CREATE TABLE IF NOT EXISTS pedidoDesc("
                            + "DesSec INTEGER,"
                            + "pordescapli numeric(8,4) )";

                    //  }
                }catch(Exception e) {
                    int pp = 0;
                }


                Double DctoArtC1=0.0;
                Double DctoArtC2=0.0;
                Double DctoArtC3=0.0;
                Double DctoArtC4=0.0;

                DctoArtC1=0.0;
                DctoArtC2=0.0;
                DctoArtC3=0.0;
                DctoArtC4=0.0;
                Log.e("eCancod: ",String.valueOf(eCancod));
                String ConsultaArticulosRela = "select p.ArtSec,p.CliSec,p.prefijo from pedido p left join articulos a on a.artsec=p.artsec where cant+cantcaj<>0 and InvGruCod='"+agInvGruCod+"' and  p.nitsec='" + NitSec + "' and  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay ;
                // and clisec=" + CliSec + "
                //   and prefijo ='" + Prefijo + "'"
                Cursor CurConsultaArticulosRela = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaArticulosRela, null);
                String ArtSecADesc="";
                Integer CliSecDesc=0;
                String prefijoDesc="";
                if (CurConsultaArticulosRela.getCount() > 0){
                    CurConsultaArticulosRela.moveToFirst();
                    do{
                        ArtSecADesc=CurConsultaArticulosRela.getString(0);
                        CliSecDesc=CurConsultaArticulosRela.getInt(1);

                        Log.e("CLiSecDesc: ",String.valueOf(CliSecDesc));


                        prefijoDesc=CurConsultaArticulosRela.getString(2);
                        String ConsultaArticulosDes=" select total(IFNULL(pordescapli1,0.0)) d1,total(IFNULL(pordescapli2,0.0)) d2,total(IFNULL(pordescapli3,0.0)) d3,total(IFNULL(pordescapli4,0.0)) d4 " +
                                " from pedidoDesc " +
                                " where  artsec='"+ArtSecADesc+"' and nitsec='" + NitSec + "'  and pdyear=" + time.year  +  " and clisec=" + CliSecDesc +
                                " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo ='" + prefijoDesc + "'" ; //
                        Cursor CurConsultaArticulosDesc = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaArticulosDes, null);
                        if (CurConsultaArticulosDesc.getCount() > 0){
                            CurConsultaArticulosDesc.moveToFirst();
                            do{
                                DctoArtC1=CurConsultaArticulosDesc.getDouble(0);
                                DctoArtC2=CurConsultaArticulosDesc.getDouble(1);
                                DctoArtC3=CurConsultaArticulosDesc.getDouble(2);
                                DctoArtC4=CurConsultaArticulosDesc.getDouble(3);
                            } while (CurConsultaArticulosDesc.moveToNext());
                        }

                        Log.e("DctoArtC4:DctoArtC4 ",String.valueOf(DctoArtC4));


                        BaseDeDatos.getWritableDatabase().execSQL("update pedido set " +
                                " pordesc=CASE WHEN  "+DctoArtC1+">= pordesc and pordesc<>0 and pordesc<pordescno and "+DctoArtC1+"<>0  THEN pordesc " +
                                " else "+DctoArtC1+" end,pordescno="+DctoArtC1 +
                                " ,pordesc2=CASE WHEN  "+DctoArtC2+">= pordesc2 and pordesc2<>0 and pordesc2<pordesc2no and "+DctoArtC2+"<>0  THEN pordesc2 " +
                                " else "+DctoArtC2+" end,pordesc2no="+DctoArtC2 +
                                " ,pordesc3=CASE WHEN  "+DctoArtC3+">= pordesc3 and pordesc3<>0 and pordesc3<pordesc3no and "+DctoArtC3+"<>0  THEN pordesc3 " +
                                " else "+DctoArtC3+" end,pordesc3no="+DctoArtC3 +
                                " ,pordesc4=CASE WHEN  "+DctoArtC4+">= pordesc4 and pordesc4<>0 and pordesc4<pordesc4no and "+DctoArtC4+"<>0  THEN pordesc4 " +
                                " else "+DctoArtC4+" end,pordesc4no="+DctoArtC4 +
                                " where artsec='"+ArtSecADesc+"' and  nitsec='" + NitSec + "'  and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay+ "  and prefijo ='" + prefijoDesc + "' and clisec=" + CliSecDesc  ); // //

                        BaseDeDatos.getWritableDatabase().execSQL("update pedido set " +
                                " pordesc=CASE WHEN  pordescCero=1 then 0 else pordesc end,"+
                                " pordesc2=CASE WHEN  pordesc2Cero=1 then 0 else pordesc2 end,"+
                                " pordesc3=CASE WHEN  pordesc3Cero=1 then 0 else pordesc3 end,"+
                                " pordesc4=CASE WHEN  pordesc4Cero=1 then 0 else pordesc4 end,"+
                                " pordesc5=CASE WHEN  pordesc5Cero=1 then 0 else pordesc5 end"+
                                " where artsec='"+ArtSecADesc+"' and  nitsec='" + NitSec + "'  and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay +" and clisec=" + CliSecDesc + " and prefijo ='" + prefijoDesc + "'"  ); //+ //
/*
                        BaseDeDatos.getWritableDatabase().execSQL("update pedido set " +
                                " pordesc=CASE WHEN "+DctoArtC1+" >= pordesc and pordesc<>0 and pordesc<pordescno  THEN pordesc " +
                                " WHEN  "+DctoArtC1+" < pordesc and pordesc<=pordescno and pordesc<>0 and "+DctoArtC1+"<>0 THEN pordesc ELSE "+DctoArtC1+" END," +
                                " pordescno="+DctoArtC1+"," +
                                " pordesc2=CASE WHEN "+DctoArtC2+" >= pordesc2 and pordesc2<>0 and pordesc2<pordesc2no  THEN pordesc2 " +
                                " WHEN  "+DctoArtC2+" < pordesc2 and pordesc2<=pordesc2no and pordesc2<>0 and "+DctoArtC2+"<>0 THEN pordesc2 ELSE "+DctoArtC2+" END," +
                                " pordesc2no="+DctoArtC2+"," +
                                " pordesc3=CASE WHEN "+DctoArtC3+" >= pordesc3 and pordesc3<>0 and pordesc3<pordesc3no  THEN pordesc3" +
                                " WHEN  "+DctoArtC3+" < pordesc3 and pordesc3<=pordesc3no and pordesc3<>0 and "+DctoArtC3+"<>0 THEN pordesc3 ELSE "+DctoArtC3+" END," +
                                " pordesc3no="+DctoArtC3+"," +
                                " pordesc4=CASE WHEN "+DctoArtC4+" >= pordesc4 and pordesc4<>0 and pordesc4<pordesc4no  THEN pordesc4 " +
                                " WHEN  "+DctoArtC4+" < pordesc4 and pordesc4<=pordesc4no and pordesc4<>0 and "+DctoArtC4+"<>0 THEN pordesc4 ELSE "+DctoArtC4+" END," +
                                " pordesc4no="+DctoArtC4+"" +
                                " where artsec='"+ArtSecADesc+"' and  nitsec='" + NitSec + "' and clisec=" + CliSec + " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo ='" + Prefijo + "'");

 */
                    } while (CurConsultaArticulosRela.moveToNext());

                    time.setToNow();
                }else{
                    BaseDeDatos.getWritableDatabase().execSQL("update pedido set  pordesc=0,pordesc2=0,pordesc3=0,pordesc4=0  where artsec='"+Codigo+"' and  nitsec='" + NitSec + "'  and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay );
                    //and clisec=" + CliSec + " + "  and prefijo ='" + Prefijo + "'"
                }

                GlobalVariables gGlobalVariables=null;
                gGlobalVariables = GlobalVariables.getInstance();
                int vAliNegCod=gGlobalVariables.getAliNegCod();
                try { // Bonificados     -----------------------------------------------------------------------------------------------------------------------------------------




                Double DctoArt1=0.0;
                Double DctoArt2=0.0;
                Integer Tcant = 0 ;
                //Integer Digitado = (Unidades.intValue()+(Cajas*Embalaje)) ;
                Integer Digitado =TcantArt; //+(TcantArtCaj.intValue()*Embalaje);

                SDTResumenPedidos vSDTResumenPedidos=TotalesPedido(pContext,Prefijo,NitSec,CliSec,"","","");
                //tipodesc,descgru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,UNIDADNEG,CIUDADES,CLIENTES,VENDEDORES,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal
                Cursor MovParBonProdBon = BaseDeDatos.getWritableDatabase().rawQuery("select BonProSec,bontipo,BonProGrupo,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,BonProEscArtSec,BonProEscUniDes,BonProEscUniHas,BonProEscCajDes*a.artemb BonProEscCajDes,BonProEscCajHas*a.artemb BonProEscCajHas,BonProEscUniDes+(BonProEscCajDes*a.artemb) CajConEmb,BonProDesVal,BonProHasVal,BonProEscBonArtSec,BonProEscBonUni,BonProEscBonCaj,BomProMaxMixPeri,BonProMaxCli,BomProMixRefDis,BonProSecLin , c.CanCod,a.artemb from BonificacionesProducto" +
                        " inner join articulos a on artsec='"+Codigo+"'" +
                        " left join Clientes c on nitsec='"+NitSec+"' and clisec="+CliSec+" " +
                        " where BonProGrupo=InvGruCod and BonMovParTra = '"+ParMovTatTra+"' and (SUBGRUPOS='XX,' OR SUBGRUPOS like '%,'|| a.InvSubGruCod ||',%') " +
                        " and (FAMILIAS='XX,' OR FAMILIAS like '%,'|| a.InvFamCod ||',%') " +
                        " and (ARTICULOS='XX,' OR ARTICULOS like '%,'|| a.artsec ||',%') " +
                        " and (SUCURSALES='XX,' OR SUCURSALES like '%,'|| '"+ SucCod +"' ||',%') " +
                        " and (CANALES='XX,' OR CANALES like '%,'|| c.CanCod ||',%') " +
                        " and (SUBCANALES='XX,' OR SUBCANALES like '%,'|| c.CanCod ||'-'|| c.CanSubCod || ',%') " +
                        " and (TAMANOS='XX,' OR TAMANOS like '%,'|| c.CliTamCan || ',%')  " +
                        " and (VENDEDORES='XX,' OR VENDEDORES like '%,'||'"+ vUsuario+"'|| ',%') " +
                        " and (UNIDADES='XX,' OR UNIDADES like '%,'||'"+ vAliNegCod+"'|| ',%') " +
                        "  AND ((bontipo='ESCALA' and BonProEscArtSec="+Codigo+" ) or bontipo='DETALLE') and BonProEscUniDes+BonProEscUniHas+BonProEscCajDes+BonProEscCajHas>0 " +
//                      " and (CIUDADES='XX,' OR CIUDADES like '%,'|| c.CiuCod ||',%') " +
//                      " and (CLIENTES='XX,' OR CLIENTES like '%,'|| c.nitsec ||'-'|| c.clisec || ',%') " +
//                      " and ((DescLinDesVal<="+vSDTResumenPedidos.Subtotal+" and DescLinHasVal>="+vSDTResumenPedidos.Subtotal+") or DescLinDesVal+DescLinHasVal=0)"  +
//                      " and (( BonProEscUniDes<="+Tcant+" and (BonProEscUniHas>="+Tcant+" or bontipo='DETALLE' ) ) or BonProEscCajDes<>0)"  +
//                      " and (( BonProEscCajDes*ArtEmb<="+Tcant+"  and (BonProEscCajHas*ArtEmb>="+Tcant+" or bontipo='DETALLE') ) or BonProEscUniDes<>0) AND (bontipo='ESCALA' or bontipo='DETALLE')"  +

                        "", null); //and MOvParEscInd<>'IND'
                //BaseDeDatos.getWritableDatabase().execSQL("Delete from MovParPrem where Prefijo='" + Prefijo + "' and MovParPremTip='NNP' and MovParPremArtSecOri='"+Codigo+"'");
                if (MovParBonProdBon.getCount() > 0) {
                    MovParBonProdBon.moveToFirst();
                    int unidadesfin =1;
                    do {



                        Integer BonProSec=MovParBonProdBon.getInt(0);
                        Integer BonProSecLin=MovParBonProdBon.getInt(23);
                        BaseDeDatos.getWritableDatabase().execSQL("Delete from MovParPrem where MovParNitSec = '"+NitSec+"' and   Prefijo='" + Prefijo + "' and MovParPremSec=" + BonProSec+ " and MovParPremSecLin=" + BonProSecLin+ " and MovParPremTip='NNP'"  ); //and MovParPremArtSecOri='"+Codigo+"'"

                        String MovParBonArtSec=MovParBonProdBon.getString(17);
                        if (MovParBonProdBon.getInt(14)>0) {
                            String bontipo=MovParBonProdBon.getString(1);
                            int BonProEscUniDes=MovParBonProdBon.getInt(10);
                            int BonProEscUniHas=MovParBonProdBon.getInt(11);
                            int BonProEscCajDes=MovParBonProdBon.getInt(12);
                            int BonProEscCajHas=MovParBonProdBon.getInt(13);
                            int BonProLin=MovParBonProdBon.getInt(23);
                            int CajConEmb=MovParBonProdBon.getInt(14);


                            if (BonProEscUniDes==BonProEscUniHas && BonProEscCajDes==BonProEscCajHas && CajConEmb>0 ){
                                if (BonProEscUniDes>0 ){
                                    //TcantArt
                                    //TcantArtCaj
                                    //int aTcant = ((Unidades.intValue()+(Cajas*Embalaje)) )/BonProEscUniDes;
                                    //Tcant = ((TcantArt+(TcantArtCaj.intValue()*Embalaje)) )/BonProEscUniDes;
                                    Tcant = TcantArt/BonProEscUniDes;
                                }else{
                                    if (BonProEscCajDes>0 ){
                                    Tcant = TcantArt / CajConEmb;
                                      //  Tcant = ((TcantArt+(TcantArtCaj.intValue()*Embalaje)) / CajConEmb);
                                      //  int aTcant = ((Unidades.intValue()+(Cajas*Embalaje)) / CajConEmb);
                                    }
                                }
                            }else{
                                if ((((Digitado>=BonProEscUniDes && Digitado<=BonProEscUniHas) || BonProEscUniDes+BonProEscUniHas==0)) && (((Digitado)>=BonProEscCajDes && (Digitado)<=BonProEscCajHas ) || BonProEscCajDes+BonProEscCajHas==0)){
                                    Tcant = 1;
                                }else{
                                    Tcant = 0;
                                }
                            }
                            //BonProEscUniDes
                            //BonProEscUniHas
                            //BonProEscCajDes
                            //BonProEscCajHas
                            //BonProEscUniDes+(BonProEscCajDes*a.artemb) CajConEmb
                            //int CantBonificados=MovParBonProdBon.getInt(14);


                            //20/21
                            Integer Entregados=0;
                            TempJJ = "select sum(karuni) cantidad from ClientesDevoluciones where nitsec='"+NitSec+"' and CliSec=" + CliSec + " and dias<="+MovParBonProdBon.getInt(20)+" and artsec='"+MovParBonArtSec+"'";
                            Cursor HistorialBonificados = BaseDeDatos.getWritableDatabase().rawQuery(TempJJ, null);
                            if (HistorialBonificados.getCount() > 0) {
                                HistorialBonificados.moveToFirst();
                                do {
                                    Entregados=HistorialBonificados.getInt(0);
                                } while (HistorialBonificados.moveToNext());
                            }

                            if (MovParBonProdBon.getInt(21)<Tcant && MovParBonProdBon.getInt(21)>0){
                                Tcant=MovParBonProdBon.getInt(21);
                            }
                            if (Entregados!=0){
                                Entregados=Entregados;
                            }
                            if (MovParBonProdBon.getInt(21)==0){
                                Entregados=0;
                            }


                            if (Tcant>=1){
                                int embalaje = MovParBonProdBon.getInt(25);
                                int unidades  = ((MovParBonProdBon.getInt(18)) * Tcant)+(((MovParBonProdBon.getInt(19)) * Tcant)*embalaje);
                                int canal = MovParBonProdBon.getInt(24);
                                unidadesfin = validaroferta(pContext,NitSec,BonProSec,BonProSecLin,unidades,bontipo,canal);

                               String MovParPremcheckmax = "N";
                                int unidadbon =(MovParBonProdBon.getInt(18)) * Tcant;
                                 int cajabon = (MovParBonProdBon.getInt(19)) * Tcant;

                                 if(unidadesfin < unidadbon){
                                     MovParPremcheckmax = "S";
                                 }
                                if((unidadesfin/embalaje) < cajabon) {
                                    MovParPremcheckmax = "S";
                                }
                                if(unidadesfin <= 0){
                                    unidadbon = unidadesfin;
                                    MovParPremcheckmax = "S";
                                }else{
                                    if(cajabon > 0){
                                        cajabon = unidadesfin/embalaje;
                                    }else{
                                        unidadbon = unidadesfin;
                                    }
                                }
                                            Integer BonificadosCant=MovParBonProdBon.getInt(18);
                                            String Consulta = "insert into MovParPrem(prefijo,MovParNitSec,MovParCliSec,MovParPremSec,MovParPremTip,MovParPremArtSec,MovParPremCant,MovParPremCantCaj,MovParPremDesc,MovParPremArtSecOri,MovParPremAno,MovParPremMes,MovParPremDia,MovParPremSecLin,MovParPremtipo,MovParPremcheckmax)values('" + Prefijo+ "','"+NitSec  + "'," +CliSec+ "," +  BonProSec + ",'NNP','" + MovParBonArtSec.trim() + "'," + unidadbon + ","+cajabon+",0,'"+Codigo+"',"+time.year + "," + (time.month + 1) + "," +time.monthDay +","+BonProLin+",'"+bontipo+"','"+MovParPremcheckmax+"')";
                                            try {
                                                BaseDeDatos.getWritableDatabase().execSQL(Consulta);
                                            } catch (Exception e) {
                                                int jj = 0;
                                            }

                            }
                        }
                    } while (MovParBonProdBon.moveToNext());
                   /* if(bandera == 0) {
                        Toast.makeText(pContext,"limite maximo de la oferta superado",Toast.LENGTH_LONG).show();
                    }*/
                }
            }catch(Exception e) {
                int pp = 0;
                }
                //ESCGRU
                try {
                    Integer Tcant = (Unidades.intValue()+(Cajas*Embalaje)) ;
                    Integer VenCantTotCajEmb=0;



                    SDTResumenPedidos vSDTResumenPedidos=TotalesPedido(pContext,Prefijo,NitSec,CliSec,"","","");
                    //tipodesc,descgru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,UNIDADNEG,CIUDADES,CLIENTES,VENDEDORES,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal


                    Log.e("ParMovTatTra",ParMovTatTra);
                    Log.e("Codigo",Codigo);
                    Log.e("NitSec",NitSec);
                    Log.e("CliSec",CliSec.toString());
                    Cursor MovParBonProdBon = BaseDeDatos.getWritableDatabase().rawQuery("select BonProSec,bontipo,BonProGrupo,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,BonProEscArtSec,BonProEscUniDes,BonProEscUniHas,BonProEscCajDes,BonProEscCajHas,BonProEscUniDes+(BonProEscCajDes*a.artemb) CajConEmb,BonProDesVal,BonProHasVal,BonProEscBonArtSec,BonProEscBonUni,BonProEscBonCaj,BomProMaxMixPeri,BonProMaxCli,BomProMixRefDis,BonProCanOpc,CASE WHEN BonProEscUniDes=BonProEscUniHas and BonProEscCajDes=BonProEscCajHas THEN 1 ELSE 0 END multiplicador,BonProSecLin,c.CanCod,a.artemb from BonificacionesProducto" +
                            " inner join articulos a on artsec='"+Codigo+"'" +
                            " left join Clientes c on nitsec='"+NitSec+"' and clisec="+CliSec+" " +
                            " where BonProGrupo=InvGruCod and BonMovParTra = '"+ParMovTatTra+"' and (SUBGRUPOS='XX,' OR SUBGRUPOS like '%,'|| a.InvSubGruCod ||',%') " +
                            " and (FAMILIAS='XX,' OR FAMILIAS like '%,'|| a.InvFamCod ||',%') " +
                            " and (ARTICULOS='XX,' OR ARTICULOS like '%,'|| a.artsec ||',%') " +
                            " and (SUCURSALES='XX,' OR SUCURSALES like '%,'|| '"+ SucCod +"' ||',%') " +
                            " and (CANALES='XX,' OR CANALES like '%,'|| c.CanCod ||',%') " +
                            " and (VENDEDORES='XX,' OR VENDEDORES like '%,'||'"+ vUsuario+"'|| ',%') " +
                              " and (UNIDADES='XX,' OR UNIDADES like '%,'||'"+ vAliNegCod+"'|| ',%') " +
                            " and (SUBCANALES='XX,' OR SUBCANALES like '%,'|| c.CanCod ||'-'|| c.CanSubCod || ',%') " +
                            " and (TAMANOS='XX,' OR TAMANOS like '%,'|| c.CliTamCan || ',%') " +
                            " and bontipo='ESCGRU' "  +
                            "", null); //and MOvParEscInd<>'IND'
                    if (MovParBonProdBon.getCount() > 0) {
                        MovParBonProdBon.moveToFirst();
                        Integer vueltas=0;
                        int unidadesfin = 1;
                        do {
                            vueltas+=1;

                            String BonProGrupo=MovParBonProdBon.getString(2);
                            String SUBGRUPOS=MovParBonProdBon.getString(3);
                            String FAMILIAS=MovParBonProdBon.getString(4);
                            String ARTICULOS=MovParBonProdBon.getString(5);
                            Integer BonProSec=MovParBonProdBon.getInt(0);
                            Integer BonProSecLin=MovParBonProdBon.getInt(25);
                            String bontipo = MovParBonProdBon.getString(1);

                            String BonProArtSec=MovParBonProdBon.getString(17);
                            //BaseDeDatos.getWritableDatabase().execSQL("Delete from MovParPrem where Prefijo='" + Prefijo + "' and MovParPremTip='NNP' and llaveProm="+BonProSec);
                            Integer RefDistintas=MovParBonProdBon.getInt(22);
                            Integer RefOpcionales=MovParBonProdBon.getInt(23);
                            Integer Multiplicador=MovParBonProdBon.getInt(24);
                            Integer BonProEscUniDes=MovParBonProdBon.getInt(10);
                            Integer BonProEscUniHas=MovParBonProdBon.getInt(11);
                            Integer BonProEscCajDes=MovParBonProdBon.getInt(12);
                            Integer BonProEscCajHas=MovParBonProdBon.getInt(13);
                            String MovParBonArtSec=MovParBonProdBon.getString(17);
                            BaseDeDatos.getWritableDatabase().execSQL("Delete from MovParPrem where MovParNitSec = '"+NitSec+"'   and  MovParPremSec=" + BonProSec+ " and  MovParPremSecLin="+BonProSecLin+" and MovParPremTip='NNP' ");
                            //Prefijo='" + Prefijo + "' and

                            if (MovParBonProdBon.getInt(18)+MovParBonProdBon.getInt(19)>0) {

                               // Tcant = (Unidades.intValue()+(Cajas*Embalaje)) / CantBonificados;

                                /// cantidad vendidas de esa promo, referencias distintas y referencias opcionales

/*0
                          Integer VenConOps=0;
                    Integer VenConNor=0;
                                String ConsultaTxt = "select total(cant) conteto,total(cantcaj) contetocaj,total((cant+(cantcaj*pedartemb))) contetototuni,total((cant+(cantcaj*pedartemb))/pedartemb) contetocaj,total(CASE BonProDetIndOpc WHEN 'S' THEN 1 ELSE 0 END) ProOps,total(CASE BonProDetIndOpc WHEN 'S' THEN 0 ELSE 1 END) ProNor from pedido where rtrim(nitsec) = rtrim('" +NitSec+ "') and clisec=" + CliSec+ " and artsec in(select BonProArtSec from BonificacionesProductoDet where BonProSec="+BonProSec+")  and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and cant+cantcaj>0 and prefijo = '"+Prefijo+"'";
                                Cursor pedidoventa = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaTxt, null);
                                if (pedidoventa.getCount() > 0){
                                    pedidoventa.moveToFirst();
//                                    VenCantTotCaUni=pedidoventa.getInt(0);
//                                    VenCantTotCaj= pedidoventa.getInt(1);
//                                    VenCantTotCajEmb= pedidoventa.getInt(2);
                                    VenConOps=pedidoventa.getInt(3);
                                    VenConNor=pedidoventa.getInt(4);
                                }


                                Integer Opcionales=0;
                                if(VenConOps>=RefOpcionales || RefOpcionales==0){
                                    Opcionales=1;
                                }
                                Integer Distintas=0;
                                if(VenConNor>=RefDistintas || RefDistintas==0){
                                    Distintas=1;
                                }
                                 */

                                Integer VenCantTotCaj=0;
                                Integer VenCantTotCaUni=0;
                                Integer ELEMBB=0;
                                Integer LASCAJAS=0;
                                // Validar Escala
//                                String BonProGrupo=MovParBonProdBon.getString(2);
//                                String SUBGRUPOS=MovParBonProdBon.getString(3);
//                                String FAMILIAS=MovParBonProdBon.getString(4);
                                String EscConsultaTxt = "select CAST(total((cant+(cantcaj*pedartemb))) AS INTEGER) unidades,CAST(total(( ( (cant+(cantcaj*pedartemb))*1.00 ) /(pedartemb*1.00))*1.00) AS INTEGER) cajas from pedido where rtrim(nitsec) = rtrim('" +NitSec+ "')  and artsec in(select artsec from articulos a  " + //and clisec=" + CliSec+ "
                                        " where InvGruCod='"+BonProGrupo+"' and ('"+SUBGRUPOS+"'='XX,' OR '"+SUBGRUPOS+"' like '%,'|| a.InvSubGruCod ||',%') " +
                                        " and ('"+FAMILIAS+"'='XX,' OR '"+FAMILIAS+"' like '%,'|| a.InvFamCod ||',%') " +
                                        " and ('"+ARTICULOS+"'='XX,' OR '"+ARTICULOS+"' like '%,'|| a.artsec ||',%') )" +
                                        " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and cant+cantcaj>0 "; //and prefijo = '"+Prefijo+"'"
                                Cursor EscalaCursor = BaseDeDatos.getWritableDatabase().rawQuery(EscConsultaTxt, null);
                                if (EscalaCursor.getCount() > 0){
                                    EscalaCursor.moveToFirst();
                                    VenCantTotCaUni=EscalaCursor.getInt(0);
                                    VenCantTotCaj= EscalaCursor.getInt(1);

                                }
                                Log.e("VenCantTotCaUni ",String.valueOf(VenCantTotCaUni));
                                Log.e("VenCantTotCaj ",String.valueOf(VenCantTotCaj));
//                                Integer Multiplicador=MovParBonProdBon.getInt(24);
//                                Integer BonProEscUniDes=MovParBonProdBon.getInt(10);
//                                Integer BonProEscUniHas=MovParBonProdBon.getInt(11);
//                                Integer BonProEscCajDes=MovParBonProdBon.getInt(12);
//                                Integer BonProEscCajHas=MovParBonProdBon.getInt(13);

                                Integer CumpleEscala=0;




                                if (VenCantTotCaUni>=BonProEscUniDes && (VenCantTotCaUni<=BonProEscUniHas || BonProEscUniHas==0 ) && VenCantTotCaj>=BonProEscCajDes && (VenCantTotCaj<=BonProEscCajHas || BonProEscCajHas==0)){
                                    CumpleEscala=1;
                                }
                                if (Multiplicador==1){
                                    if (BonProEscUniDes==BonProEscUniHas && BonProEscUniDes+BonProEscUniHas>0 && VenCantTotCaUni>BonProEscUniDes && BonProEscUniDes>0){
                                        Double Veces= VenCantTotCaUni.doubleValue()/BonProEscUniDes.doubleValue();
                                        CumpleEscala=Veces.intValue();
                                    }
                                    if (BonProEscCajDes==BonProEscCajHas && BonProEscCajDes+BonProEscCajHas>0 && VenCantTotCaj>BonProEscCajDes && BonProEscCajDes>0){
                                        Double Veces= VenCantTotCaj.doubleValue()/BonProEscCajDes.doubleValue();
                                        CumpleEscala=Veces.intValue();
                                    }
                                }

                                Log.e("Escala : ",String.valueOf(CumpleEscala));


                                Integer VecesCumpleOpcionales=9999;
                                Integer VecesCumpleObligatorios=9999;
                                Integer sVecesCumpleObligatorios=0;
                                /// cantidad obligatoria en referencias distintas
                                Integer TotalOpcionalesValidos=0;
                                Integer TotalObligatoriosValidos=0;
                                Integer cumplecondiciones=0;
                                String ConRefDis = "select BonProArtSec,BomProDetDesUni,BonProDetDesCaj,(BomProDetDesUni+(BonProDetDesCaj*BonProDetEmb)),BonProDetIndOpc from BonificacionesProductoDet where bonprosec="+BonProSec;
                                Cursor CurRefDis = BaseDeDatos.getWritableDatabase().rawQuery(ConRefDis, null);
                                if (CurRefDis.getCount() > 0){
                                    CurRefDis.moveToFirst();
                                    do {
                                    String CurArtSec =CurRefDis.getString(0);
                                    Integer Curcant= CurRefDis.getInt(1);
                                    Integer Curcantcaj= CurRefDis.getInt(2);
                                    Integer Curcantemb= CurRefDis.getInt(3);


                                    String BonProDetIndOpc= CurRefDis.getString(4);

                                    Log.e("Boni productodet : ",String.valueOf(Curcantemb) );


                                    Integer CurConcant= 0;
                                    Integer CurConcantcaj=0;
                                    Integer CurConcantcajemb=0;
                                    Integer Encontro=0;

                                        Log.e("Bonif NitSec :",NitSec );
                                        Log.e("Bonif CurArtSec :",CurArtSec );
                                        Log.e("Bonif Prefijo :",Prefijo );

                                    String ConsultaRefDis = "select artsec,cant,cantcaj,(cant+(cantcaj*pedartemb)) from pedido where rtrim(nitsec) = rtrim('" +NitSec+ "')  and artsec ='"+CurArtSec+"'  and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and cant+cantcaj>0 and prefijo = '"+Prefijo+"'"; //and clisec=" + CliSec+ "
                                    Cursor CurConsultaRefDis = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaRefDis, null);
                                    if (CurConsultaRefDis.getCount() > 0){
                                        CurConsultaRefDis.moveToFirst();
                                        Encontro=1;
                                        //String CurConArtSec=CurConsultaRefDis.getString(0);
                                        CurConcant= CurConsultaRefDis.getInt(1);
                                        CurConcantcaj= CurConsultaRefDis.getInt(2);
                                        CurConcantcajemb= CurConsultaRefDis.getInt(3);
                                    }
                                        Log.e("Bonif pedido :",String.valueOf(CurConcantcajemb) );
                                        Log.e("Bonif CurConcant :",String.valueOf(Curcantemb) );


                                        Log.e("Bonif BonProDetIndOpc :",BonProDetIndOpc) ;
                                        if (BonProDetIndOpc.equalsIgnoreCase("S") &&  CurConcantcajemb>=Curcantemb && CurConcantcajemb>0){
                                            if(Curcantemb>0){

                                                Double Veces= CurConcantcajemb.doubleValue()/Curcantemb.doubleValue();
                                                if (Veces.intValue()<VecesCumpleOpcionales){
                                                    VecesCumpleOpcionales=Veces.intValue();
                                                }
                                            }else{
                                                VecesCumpleOpcionales=9999;
                                            }
                                            TotalOpcionalesValidos+=1;
                                        }
                                        Log.e("VCumpleOpcionales  ",String.valueOf(VecesCumpleOpcionales) );

                                        if (BonProDetIndOpc.equalsIgnoreCase("S")==false && CurConcantcajemb>=Curcantemb && CurConcantcajemb>0 ){
                                            if(Curcantemb>0){
                                                Double Veces= CurConcantcajemb.doubleValue()/Curcantemb.doubleValue();
                                                if (Veces.intValue()<VecesCumpleObligatorios){
                                                    VecesCumpleObligatorios=Veces.intValue();
                                                }
                                            }else{
                                                sVecesCumpleObligatorios += 1;
                                                VecesCumpleObligatorios=9999;
                                            }
                                            TotalObligatoriosValidos+=1;
                                        }else{
                                            VecesCumpleObligatorios = 0;
                                        }
                                        Log.e("VeCumpleObligatorios  ",String.valueOf(VecesCumpleObligatorios) );
                                    } while (CurRefDis.moveToNext());
                                }
                                     if(sVecesCumpleObligatorios > 0){
                                         VecesCumpleObligatorios=9999;
                                     }

                                Log.e("VeCumpleObls afuera ",String.valueOf(VecesCumpleObligatorios) );

                                //Integer RefDistintas=MovParBonProdBon.getInt(22);
                                //Integer RefOpcionales=MovParBonProdBon.getInt(23);

                                Integer CumpleOpcionales=0;
                                Integer CumpleObligatorios=0;
                                if (RefOpcionales>0){
                                    if (TotalOpcionalesValidos>=RefOpcionales){
                                        Double Veces= TotalOpcionalesValidos.doubleValue()/RefOpcionales.doubleValue();
                                        CumpleOpcionales=Veces.intValue();
                                    }else{
                                        CumpleOpcionales=0;
                                    }
                                }else{
                                    CumpleOpcionales=9999;
                                }

                                if (RefDistintas>0){
                                    if (RefDistintas>=TotalObligatoriosValidos){
                                        Double Veces= TotalObligatoriosValidos.doubleValue()/RefOpcionales.doubleValue();  // antes RefOpcionales preguntar
                                        CumpleObligatorios=Veces.intValue();
                                    }else{
                                        CumpleObligatorios=0;
                                    }
                                }else{
                                    CumpleObligatorios=9999;
                                 }


                                Tcant=0;


                                if (CumpleEscala<VecesCumpleObligatorios  ){
                                    Tcant=CumpleEscala;

                                }else{
                                    Tcant=VecesCumpleObligatorios;


                                }
                                if (VecesCumpleOpcionales<Tcant){
                                    Tcant=VecesCumpleOpcionales;


                                }
                                if (CumpleObligatorios<Tcant){
                                    Tcant=CumpleObligatorios;


                                }
                                if (CumpleOpcionales<Tcant){


                                    Tcant=CumpleOpcionales;
                                }





                                if (Tcant>=1 && Tcant!=9999){
                                    int embalaje = MovParBonProdBon.getInt(27);
                                    int unidades  = ((MovParBonProdBon.getInt(18)) * Tcant)+(((MovParBonProdBon.getInt(19)) * Tcant)*embalaje);
                                    int canal = MovParBonProdBon.getInt(26);

                                    String MovParPremcheckmax = "N";
                                    unidadesfin = validaroferta(pContext,NitSec,BonProSec,BonProSecLin,unidades,bontipo,canal);
                                     int unidadbon =(MovParBonProdBon.getInt(18)) * Tcant;
                                    int cajabon = (MovParBonProdBon.getInt(19)) * Tcant;

                                    if(unidadesfin < unidadbon){
                                        MovParPremcheckmax = "S";
                                    }
                                    if((unidadesfin/embalaje) < cajabon) {
                                        MovParPremcheckmax = "S";
                                    }

                                    if(unidadesfin <= 0){
                                        unidadbon = unidadesfin;
                                        MovParPremcheckmax = "S";
                                    }else{
                                        if(cajabon > 0){
                                            cajabon = unidadesfin/embalaje;
                                        }else{
                                            unidadbon = unidadesfin;
                                        }
                                    }

                                        String Consulta = "insert into MovParPrem(prefijo,MovParNitSec,MovParCliSec,MovParPremSec,MovParPremSecLin,MovParPremTip,MovParPremArtSec,MovParPremCant,MovParPremCantCaj,MovParPremDesc,MovParPremArtSecOri,MovParPremAno,MovParPremMes,MovParPremDia,MovParPremSecLin,MovParPremtipo,MovParPremcheckmax)values('" + Prefijo + "','" + NitSec + "'," + CliSec + "," + BonProSec + "," + BonProSecLin + ",'NNP','" + MovParBonArtSec.trim() + "'," + unidadbon+ "," + cajabon + ",0,'" + Codigo + "'," + time.year + "," + (time.month + 1) + "," + time.monthDay + "," + BonProSecLin + ",'" + bontipo + "','"+MovParPremcheckmax+"')";
                                        try {
                                            BaseDeDatos.getWritableDatabase().execSQL(Consulta);
                                        } catch (Exception e) {
                                            int jj = 0;
                                        }

                                }
                            }

                        } while (MovParBonProdBon.moveToNext());
                        /*if(bandera == 0) {
                            Toast.makeText(pContext,"limite maximo de la oferta superado",Toast.LENGTH_LONG).show();
                        }*/
                    }
                }catch(Exception e) {
                    int pp = 0;
                }
                try {
                    Double DctoArt1=0.0;
                    Double DctoArt2=0.0;
                    //Integer Tcant = (Unidades.intValue()+(Cajas*Embalaje)) ;
                    Integer Tcant = TcantArt; //(+(TcantArtCaj.intValue()*Embalaje)) ;
                    SDTResumenPedidos vSDTResumenPedidos=TotalesPedido(pContext,Prefijo,NitSec,CliSec,"","","");
                    //tipodesc,descgru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,UNIDADNEG,CIUDADES,CLIENTES,VENDEDORES,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal
                    Cursor MovParBonProdBon = BaseDeDatos.getWritableDatabase().rawQuery(
                            "select BonProSec,bontipo,BonProGrupo,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,BonProEscArtSec,BonProEscUniDes,BonProEscUniHas,BonProEscCajDes,BonProEscCajHas,(BonProEscCajDes*a.artemb) CajConEmb,BonProDesVal,BonProHasVal,BonProEscBonArtSec,BonProEscBonUni,BonProEscBonCaj,BomProMaxMixPeri,BonProMaxCli,BomProMixRefDis,BonProSecLin,c.CanCod,a.artemb from BonificacionesProducto" +
                            " inner join articulos a on artsec="+Codigo+"" +
                            " left join Clientes c on nitsec='"+NitSec+"' and clisec="+CliSec+" " +
                            " where BonProGrupo=InvGruCod and BonMovParTra = '"+ParMovTatTra+"' and (SUBGRUPOS='XX,' OR SUBGRUPOS like '%,'|| a.InvSubGruCod ||',%') " +
                            " and (FAMILIAS='XX,' OR FAMILIAS like '%,'|| a.InvFamCod ||',%') " +
                            " and (ARTICULOS='XX,' OR ARTICULOS like '%,'|| a.artsec ||',%') " +
                            " and (SUCURSALES='XX,' OR SUCURSALES like '%,'|| '"+ SucCod +"' ||',%') " +
                            " and (CANALES='XX,' OR CANALES like '%,'|| c.CanCod ||',%') " +
                            " and (SUBCANALES='XX,' OR SUBCANALES like '%,'|| c.CanCod ||'-'|| c.CanSubCod || ',%') " +
                            " and (TAMANOS='XX,' OR TAMANOS like '%,'|| c.CliTamCan || ',%') " +
                            " and (VENDEDORES='XX,' OR VENDEDORES like '%,'||'"+ vUsuario+"'|| ',%') " +
                          " and (UNIDADES='XX,' OR UNIDADES like '%,'||'"+ vAliNegCod+"'|| ',%') " +
    //                        " and (CIUDADES='XX,' OR CIUDADES like '%,'|| c.CiuCod ||',%') " +
    //                        " and (CLIENTES='XX,' OR CLIENTES like '%,'|| c.nitsec ||'-'|| c.clisec || ',%') " +
    //                        " and (VENDEDORES='XX,' OR VENDEDORES like '%,'||'"+ vUsuario+"'|| ',%') " +
    //                        " and ((DescLinDesVal<="+vSDTResumenPedidos.Subtotal+" and DescLinHasVal>="+vSDTResumenPedidos.Subtotal+") or DescLinDesVal+DescLinHasVal=0)"  +
                            " and (( BonProEscUniDes>="+Tcant+" ) or BonProEscCajDes<>0)"  +
                            " and (( BonProEscCajDes*ArtEmb>="+Tcant+" ) or BonProEscUniDes<>0) AND bontipo='GENERAL'"  +

                            "", null); //and MOvParEscInd<>'IND'

                    if (MovParBonProdBon.getCount() > 0) {
                        MovParBonProdBon.moveToFirst();
                        Integer BonProSec=MovParBonProdBon.getInt(0);
                        Integer BonProSecLin = MovParBonProdBon.getInt(23);
                        String bontipo =MovParBonProdBon.getString(1);

                        BaseDeDatos.getWritableDatabase().execSQL("Delete from MovParPrem where Prefijo='" + Prefijo + "' and MovParPremSec=" + BonProSec+ " and MovParPremTip='BNP' and MovParPremArtSecOri='"+Codigo+"'");
                        int unidadesfin =0;
                        do {

                            if (MovParBonProdBon.getInt(10)>0) {

                                Tcant = (Unidades.intValue()+(Cajas*Embalaje)) / MovParBonProdBon.getInt(14);

                                if (Tcant>=1){

                                    int embalaje = MovParBonProdBon.getInt(25);
                                    int unidades  = ((MovParBonProdBon.getInt(18)) * Tcant)+(((MovParBonProdBon.getInt(19)) * Tcant)*embalaje);
                                    int canal = MovParBonProdBon.getInt(24);
                                    String MovParPremcheckmax = "N";
                                    unidadesfin = validaroferta(pContext,NitSec,BonProSec,BonProSecLin,unidades,bontipo,canal);

                                    int unidadbon =(MovParBonProdBon.getInt(18)) * Tcant;
                                    int cajabon = (MovParBonProdBon.getInt(19)) * Tcant;

                                    if(unidadesfin < unidadbon){
                                        MovParPremcheckmax = "S";
                                    }
                                    if((unidadesfin/embalaje) < cajabon) {
                                        MovParPremcheckmax = "S";
                                    }
                                    if(unidadesfin <= 0){
                                        unidadbon = -1;
                                    }else{
                                       if(cajabon > 0){
                                           cajabon = unidadesfin/embalaje;
                                       }else{
                                           unidadbon = unidadesfin;
                                       }
                                    }

                                    String MovParBonArtSec=MovParBonProdBon.getString(17);
                                    String Consulta = "insert into MovParPrem(prefijo,MovParNitSec,MovParCliSec,MovParPremSec,MovParPremTip,MovParPremArtSec,MovParPremCant,MovParPremCantCaj,MovParPremDesc,MovParPremArtSecOri,MovParPremAno,MovParPremMes,MovParPremDia,MovParPremSecLin,MovParPremtipo,MovParPremcheckmax)values('" + Prefijo+ "','"+NitSec  + "'," +CliSec+ "," +  BonProSec + ",'BNP','" + MovParBonArtSec.trim() + "'," + unidadbon + ","+cajabon+",0,'"+Codigo+"',"+time.year + "," + (time.month + 1) + "," +time.monthDay +","+BonProSecLin+",'"+bontipo+"','"+MovParPremcheckmax+"')";
                                    try {
                                        BaseDeDatos.getWritableDatabase().execSQL(Consulta);
                                    } catch (Exception e) {
                                        int jj = 0;
                                    }


                                }
                            }while (MovParBonProdBon.moveToNext()) ;
                            //       }
                            //    }
                            //MovParPremSec =movparmix.getInt(0);
                            //MovParBonEncArtSec=MovParBonBonificados.getInt(2);
                            // }
                        } while (MovParBonProdBon.moveToNext());
                     /*   if(bandera == 0){
                            Toast.makeText(pContext,"limite maximo de la oferta superado",Toast.LENGTH_LONG).show();
                        }*/
                    }
                }catch(Exception e) {
                    int pp = 0;
                }
            }


            return true;
        }
    public boolean EvaluarFicc(Context pContext,String PreArtCod,
                                String ArtSec, String Prefijo, String NitSec, Integer CliSec, Double Unidades,
                               Integer Cajas, Integer Embalaje,Integer xlisprecod) {
        // String ArtSec, String Prefijo, String NitSec, Integer CliSec, Double Unidades


        String Codigo=ArtSec;
        String aCantidad=Unidades.toString();
        Integer Cantidad=Double.valueOf(aCantidad).intValue();
        Time time = new Time();
        time.setToNow();
        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vEmpresa=vGlobalVariables.getEmpresa();
        vEmpresa=vEmpresa.toUpperCase();
        String vUsuario=vGlobalVariables.getUsuario();
        int bodcod = 0;
        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 5);

        Cursor cursorpedido = BaseDeDatos.getWritableDatabase().rawQuery("select bodcod from pedido p where ifnull(NotaInv,'N') <> 'S' and ifnull(NotaCar,'N')  <> 'S' and p.PreArtcod = '"+PreArtCod+"' and prefijo = '"+Prefijo+"' and artsec = '"+ArtSec+"' and nitsec = '"+NitSec+"' and clisec = "+CliSec+" and  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay+" ", null);
        if(cursorpedido.getCount()>0){
            cursorpedido.moveToFirst();
            bodcod = cursorpedido.getInt(0);
        }

        Log.e("Trajo el bodcod: ",ArtSec);

        String TempJJ;
        //String Pedido = Prefijo + Extras.getString("Codvend").trim() + "-" + time.year + "-" + (time.month + 1) + "-" + time.monthDay + "-" + Extras.getString("nitsec") + "-" + Extras.getInt("clisec");
        String VersionDescuentosV2=vGlobalVariables.getParMovDescV2();
        if (VersionDescuentosV2.equalsIgnoreCase("S")==false){

            TempJJ = "select MovParMixRefDis,MovParMixCntTotal,MovParMixConic,MovParMixSec,MovParMixCntTotalCaj,MovParMixPeri from movparmix " +
                    "where MovParMixSec in(select MovParMixSec from MovParMixArticulos where MovParMixDetArtSec='"+Codigo+"')";
            Cursor movparmix = BaseDeDatos.getWritableDatabase().rawQuery(TempJJ, null);
            if (movparmix.getCount() > 0) {
                movparmix.moveToFirst();
                do {

                    //"prefijo='"+Prefijo+"' and MovParNitSec='"+NitSec+"'  and MovParCliSec="+CliSec;

                    BaseDeDatos.getWritableDatabase().execSQL("Delete from MovParPrem where prefijo='"+Prefijo+"' and MovParNitSec='"+NitSec+"'  and MovParCliSec="+CliSec+" and MovParPremSec=" + movparmix.getInt(3) + " and (MovParPremTip='MPX' OR MovParPremTip='MPD')");

                    Double RefDis=0.00;
                    Double CantTot=0.00;
                    Double CantObl=999.0;
                    if (movparmix.getInt(0)>0) {
                        String ConsultaTxt = "select count(*) conteto from pedido p where rtrim(nitsec) = rtrim('" + NitSec + "') and clisec=" + CliSec + " and artsec in(select MovParMixDetArtSec from MovParMIxarticulos where ifnull(NotaInv,'N') <> 'S' and ifnull(NotaCar,'N')  <> 'S' and p.PreArtcod = '"+PreArtCod+"' and PreArtcod = '"+PreArtCod+"' and movparmixsec="+movparmix.getInt(3)+") and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and cant+cantcaj>0 and prefijo = '"+Prefijo+"'";
                        Cursor MovParMixRefDis = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaTxt, null);
                        if (MovParMixRefDis.getCount() > 0){
                            MovParMixRefDis.moveToFirst();
                            if(MovParMixRefDis.getInt(0) >= movparmix.getInt(0)){
                                RefDis= Double.valueOf(MovParMixRefDis.getInt(0));/// movparmix.getInt(0));
                            }
                        }
                    }else{
                        RefDis= Double.valueOf(999);
                    }
                    if (movparmix.getInt(1)>0 || movparmix.getInt(4)>0) {
                        String ConsultaTxt = "select total(cant) conteto,total((cant+(cantcaj*pedartemb))/pedartemb) contetocaj from pedido p where ifnull(NotaInv,'N') <> 'S' and ifnull(NotaCar,'N')  <> 'S' and p.PreArtcod = '"+PreArtCod+"' and rtrim(nitsec) = rtrim('" +NitSec+ "') and clisec=" + CliSec+ " and artsec in(select MovParMixDetArtSec from MovParMIxarticulos where movparmixsec="+movparmix.getInt(3)+")  and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and cant+cantcaj>0 and prefijo = '"+Prefijo+"'";
                        Cursor MovParMixCntTotal = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaTxt, null);
                        if (MovParMixCntTotal.getCount() > 0){
                            MovParMixCntTotal.moveToFirst();
                            int gg2=MovParMixCntTotal.getInt(0);
                            if(MovParMixCntTotal.getInt(0) >= movparmix.getInt(1) && movparmix.getInt(1)>0){
                                CantTot= Double.valueOf(MovParMixCntTotal.getInt(0) / movparmix.getInt(1));
                            }
                            if(MovParMixCntTotal.getInt(1) >= movparmix.getInt(4) && movparmix.getInt(4)>0){
                                CantTot= Double.valueOf(MovParMixCntTotal.getInt(0) / movparmix.getInt(4));
                            }
                        }
                    }else{
                        CantTot= Double.valueOf(0);
                    }

                    TempJJ = "select MovParMixDetArtSec,MovParMixDetCntObl,MovParMixDetCntOblCaj,MovParMixDetArtEmb,(MovParMixDetCntObl+(MovParMixDetCntOblCaj*MovParMixDetArtEmb)) totuni from MovParMIxarticulos where MovParMixDetCntObl+MovParMixDetCntOblCaj<>0 and MovParMixSec="+movparmix.getInt(3)+"";
                    Cursor MovParMIxarticulos=null;
                    try {
                        MovParMIxarticulos = BaseDeDatos.getWritableDatabase().rawQuery(TempJJ, null);
                    }catch (Exception e){
                        int jj=0;
                    }
                    if (MovParMIxarticulos.getCount() > 0){
                        MovParMIxarticulos.moveToFirst();
                        do {
                            String ConsultaTxt = "select total(cant) conteto,total((cant+(cantcaj*pedartemb))) contetocaj from pedido p where ifnull(NotaInv,'N') <> 'S' and ifnull(NotaCar,'N')  <> 'S' and p.PreArtcod = '"+PreArtCod+"' and rtrim(nitsec) = rtrim('" + NitSec + "') and clisec=" + CliSec+ " and artsec='" + MovParMIxarticulos.getString(0) + "' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and cant+cantcaj>0 and  prefijo = '"+Prefijo+"'";
                            Cursor MovParMixRefObl = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaTxt, null);
                            if (MovParMixRefObl.getCount() > 0){
                                MovParMixRefObl.moveToFirst();
                                if(MovParMixRefObl.getInt(1) >= MovParMIxarticulos.getInt(4)){
                                    Double TmpCantObl= Double.valueOf(MovParMixRefObl.getInt(1)/MovParMIxarticulos.getInt(4));
                                    if (TmpCantObl<CantObl){
                                        CantObl=TmpCantObl;
                                    }
                                }
                            }else{
                                CantObl= Double.valueOf(999);
                            }
                        } while (MovParMIxarticulos.moveToNext());
                    }else{
                        CantObl= Double.valueOf(999);
                    }
                    Integer Coincidencias=0;
                    if(movparmix.getInt(0)>0)
                    {
                        //    if (CantTot<RefDis) {
                        //        Coincidencias = CantTot.intValue();
                        //    }else{
                        //        Coincidencias = RefDis.intValue();
                        //    }
                        if (RefDis>=movparmix.getInt(0)) {
                            if (CantObl < CantTot) {
                                Coincidencias = CantObl.intValue();
                            } else {
                                Coincidencias = CantTot.intValue();
                            }
                        }
                    }else{
                        if (CantObl < CantTot) {
                            Coincidencias = CantObl.intValue();
                        } else {
                            Coincidencias = CantTot.intValue();
                        }
                    }
                    if(movparmix.getInt(0)>0) {
                        if (Coincidencias >= movparmix.getInt(0)) {
                            Coincidencias=movparmix.getInt(0);
                        }
                    }


                    if (Coincidencias>=1) {
                        // String Pedido = Extras.getString("tipdoc") + Extras.getString("Codvend") + "-" + time.year + "-" + (time.month + 1) + "-" + time.monthDay + "-" + Extras.getString("nitsec") + "-" + Extras.getInt("clisec");

                        Integer Coincidencias2=Coincidencias;
                        TempJJ = "select MovParMixSec,MovParMixBonArtSec,MovParMixBonCant,MovParMixBonCantCaj from MovParMixBonificados where MovParMixBonCant<>0 and MovParMixSec=" + movparmix.getInt(3) + "";
                        Cursor MovParMixBonificados = BaseDeDatos.getWritableDatabase().rawQuery(TempJJ, null);
                        if (MovParMixBonificados.getCount() > 0) {
                            MovParMixBonificados.moveToFirst();
                            do {
                                Integer Entregados=0;
                                TempJJ = "select sum(karuni) cantidad from ClientesDevoluciones where nitsec='"+NitSec+"' and CliSec=" + CliSec + " and dias<="+movparmix.getInt(5)+" and artsec='"+ArtSec+"'";
                                Cursor HistorialBonificados = BaseDeDatos.getWritableDatabase().rawQuery(TempJJ, null);
                                if (HistorialBonificados.getCount() > 0) {
                                    HistorialBonificados.moveToFirst();
                                    do {
                                        Entregados=HistorialBonificados.getInt(0);
                                    } while (HistorialBonificados.moveToNext());
                                }

                                if (movparmix.getInt(2)<Coincidencias2){
                                    Coincidencias2=movparmix.getInt(2);
                                }
                                if (Entregados!=0){
                                    Entregados=Entregados;
                                }
                                if (movparmix.getInt(2)==0){
                                    Entregados=0;
                                }
                                if (Coincidencias2-Entregados>0) {
                                    String bCodigo = MovParMixBonificados.getString(1).trim();
                                    String Consulta = "insert into MovParPrem(prefijo,MovParNitSec,MovParCliSec,MovParPremSec,MovParPremTip,MovParPremArtSec,MovParPremCant,MovParPremCantCaj,MovParPremDesc,MovParPremArtSecOri,MovParPremAno,MovParPremMes,MovParPremDia)values('" + Prefijo + "','" + NitSec + "'," + CliSec + "," + movparmix.getInt(3) + ",'MPX','" + bCodigo + "'," + ((MovParMixBonificados.getInt(2) - Entregados) * Coincidencias2) + "," + ((MovParMixBonificados.getInt(3)) * Coincidencias2) + ",0,'" + Codigo + "'," + time.year + "," + (time.month + 1) + "," + time.monthDay + ")";
                                    try {
                                        BaseDeDatos.getWritableDatabase().execSQL(Consulta);
                                    } catch (Exception e) {
                                        int jj = 0;
                                    }
                                }
                            } while (MovParMixBonificados.moveToNext());
                        }
                    }

                    TempJJ = "select MovParMixDetArtSec,MovParMixDetCntDes from MovParMixArticulos where MovParMixDetCntDes<>0 and MovParMixSec="+movparmix.getInt(3)+"";
                    Cursor MovParMixArticulos2 = BaseDeDatos.getWritableDatabase().rawQuery(TempJJ, null);
                    if (MovParMixArticulos2.getCount() > 0){
                        MovParMixArticulos2.moveToFirst();
                        do {


                                if (Coincidencias>=1) {
                                    //String Consulta = "insert into MovParPrem(prefijo,MovParNitSec,MovParCliSec,MovParPremSec,MovParPremTip,MovParPremArtSec,MovParPremCant,MovParPremDesc,MovParPremAno,MovParPremMes,MovParPremDia)values('" + Prefijo+ "','"+NitSec  + "'," +CliSec+ "," +  movparmix.getInt(3) + ",'MPD','" + MovParMixArticulos2.getInt(0) + "',0," + MovParMixArticulos2.getInt(1) +","+time.year + "," + (time.month + 1) + "," +time.monthDay +")";
                                    //BaseDeDatos.getWritableDatabase().execSQL(Consulta);
                                    BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc4=" + MovParMixArticulos2.getInt(1) + ",pordesc4no=" + MovParMixArticulos2.getInt(1) + " ifnull(NotaInv,'N') <> 'S' and ifnull(NotaCar,'N')  <> 'S' and PreArtcod = '"+PreArtCod+"' and pordesc4no<>" + MovParMixArticulos2.getInt(1) + "  and Prefijo='"+Prefijo+"' and artsec='"+MovParMixArticulos2.getInt(0)+"'");
                                }   else{
                                    BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc4=0.00,pordesc4no=0.00  where ifnull(NotaInv,'N') <> 'S' and ifnull(NotaCar,'N')  <> 'S' and PreArtcod = '"+PreArtCod+"' and  Prefijo='"+Prefijo+"' and artsec='"+MovParMixArticulos2.getInt(0)+"'");
                                }




                        } while (MovParMixArticulos2.moveToNext());
                    }

                } while (movparmix.moveToNext());

            }

            Cursor ConMovPar = null;

            try {
                //String Pedido = Extras.getString("tipdoc") + Extras.getString("Codvend") + "-" + time.year + "-" + (time.month + 1) + "-" + time.monthDay + "-" + Extras.getString("nitsec") + "-" + Extras.getInt("clisec");
                Integer Tcant=0;
                Integer MovParPremSec=0;
                String MovParBonEncArtSec="";
                //TempJJ = "select MovParBonSec,MovParBonEncArtSec,MovParBonCant,MovParBonCantCaj,MovParBonEmb,MovParBonCant+(MovParBonCantCaj*MovParBonEmb) totuni from MovParBonProdBon " +
                //      "where MovParBonEncArtSec='"+Codigo+"'";
                TempJJ = "select MovParBonSec,MovParBonEncArtSec,MovParBonCant,(MovParBonCantCaj*MovParBonEmb)+MovParBonCant Totuni,MovParBonEmb,MovParBonClientes from MovParBonProdBon left join Clientes  c on NitSec='"+NitSec+"' and CliSec="+CliSec+" "+
                        "where MovParBonEncArtSec='"+ArtSec+"'  and (MovParBonCanales='XX,' OR MovParBonCanales like '%,'|| c.CanCod ||',%') and (MovParBonClientes='XX,' OR MovParBonClientes like '%,'|| NitSec ||',%') and (MovParBonClientesExlu='XX,' OR MovParBonClientesExlu NOT like '%,'|| NitSec ||',%') ";
               Log.e("entogelve","S");

                Cursor MovParBonProdBon = BaseDeDatos.getWritableDatabase().rawQuery(TempJJ, null);
                if (MovParBonProdBon.getCount() > 0) {
                    MovParBonProdBon.moveToFirst();
                    Integer MovParBonSec=MovParBonProdBon.getInt(0);
                    String MovParBonClientes=MovParBonProdBon.getString(5);
                    BaseDeDatos.getWritableDatabase().execSQL("Delete from MovParPrem where Prefijo='" + Prefijo + "' and MovParPremSec=" + MovParBonSec+ " and MovParPremTip='BNP' and MovParPremArtSecOri='"+Codigo+"'");
                    do {

                        if (MovParBonProdBon.getInt(3)>0) {
                            MovParBonSec=MovParBonProdBon.getInt(0);
                            BaseDeDatos.getWritableDatabase().execSQL("Delete from MovParPrem where Prefijo='" + Prefijo + "' and MovParPremSec=" + MovParBonSec+ " and MovParPremTip='BNP' and MovParPremArtSecOri='"+Codigo+"'");
                            Tcant = (Unidades.intValue()+(Cajas*Embalaje)) / MovParBonProdBon.getInt(3);
                            if (Tcant>=1){
                                String TempBon = "select MovParBonSec,MovParBonArtSec,MovParBonDetCant,MovParBonDetCantCaj from MovParBonBonificados where MovParBonSec="+MovParBonSec;
                                Cursor MovParBonBonificados = BaseDeDatos.getWritableDatabase().rawQuery(TempBon, null);
                                if (MovParBonBonificados.getCount() > 0) {
                                    MovParBonBonificados.moveToFirst();
                                    do {
                                        if (MovParBonBonificados.getInt(2) > 0) {
                                            String MovParBonArtSec=MovParBonBonificados.getString(1);
                                            String Consulta = "insert into MovParPrem(prefijo,MovParNitSec,MovParCliSec,MovParPremSec,MovParPremTip,MovParPremArtSec,MovParPremCant,MovParPremCantCaj,MovParPremDesc,MovParPremArtSecOri,MovParPremAno,MovParPremMes,MovParPremDia)values('" + Prefijo+ "','"+NitSec  + "'," +CliSec+ "," +  MovParBonSec + ",'BNP','" + MovParBonArtSec.trim() + "'," + ((MovParBonBonificados.getInt(2)) * Tcant) + ","+((MovParBonBonificados.getInt(3)) * Tcant)+",0,'"+Codigo+"',"+time.year + "," + (time.month + 1) + "," +time.monthDay +")";
                                            try {
                                                BaseDeDatos.getWritableDatabase().execSQL(Consulta);
                                            } catch (Exception e) {
                                                int jj = 0;
                                            }
                                        }
                                    }while (MovParBonBonificados.moveToNext()) ;
                                }
                            }
                            //MovParPremSec =movparmix.getInt(0);
                            //MovParBonEncArtSec=MovParBonBonificados.getInt(2);
                        }
                    } while (MovParBonProdBon.moveToNext());

                       /* if (Tcant>=1){
                            String Consulta = "insert into MovParPrem(pedido,MovParPremSec,MovParPremTip,MovParPremArtSec,MovParPremCant,MovParPremDesc)values('" + Pedido + "'," + MovParPremSec + ",'BNP','" + MovParBonEncArtSec + "'," + ((MovParMixBonificados.getInt(2)) * Coincidencias) + ",0)";
                            try {
                                BaseDeDatos.getWritableDatabase().execSQL(Consulta);
                            } catch (Exception e) {
                                int jj = 0;
                            }
                        }*/

                }
            } catch (Exception e) {
                int pp = 0;
            }

            //   String ArtSec, String Prefijo, String NitSec, Integer CliSec, Double Unidades
            //   String Codigo=ArtSec;
            //   String aCantidad=Unidades.toString();
            //   Integer Cantidad=Double.valueOf(aCantidad).intValue();

            // String prefijo,String nitsec,Integer clisec
            try {
                Integer totCantidades = 0;
                Integer totCantidadesCajas = 0;
                Cursor hayEscala = BaseDeDatos.getWritableDatabase().rawQuery("select MovParEscArtSec,MovParEscSec from MovParEsc where MovParEscSec in(select MovParEscSec from MovParEsc where MovParEscArtSec='" + ArtSec.trim() + "' )  group by MovParEscArtSec,MovParEscSec", null); //and MOvParEscInd<>'IND'
                // and MovParEscArtSec<>'" + codigo[position2].trim() + "'
                if (hayEscala.getCount() > 0) {
                    hayEscala.moveToFirst();
                    do {
                        String kk=hayEscala.getString(1).trim();
                        //  if (!hayEscala.getString(0).toString().trim().equalsIgnoreCase(ArtSec.trim())) {
                        Cursor Cantidades = BaseDeDatos.getWritableDatabase().rawQuery("select cant+(cantcaj*pedartemb),(cant+(cantcaj*pedartemb))/pedartemb cajas from pedido p where nitsec='" + NitSec + "' and clisec=" + CliSec + " and artsec='" + hayEscala.getString(0).trim() + "' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo = '" + Prefijo + "'", null);
                        if (Cantidades.getCount() > 0) {
                            Cantidades.moveToFirst();
                            totCantidades += Cantidades.getInt(0);
                            totCantidadesCajas += Cantidades.getInt(1);
                        }
                        Cantidades.close();
                        // }
                    } while (hayEscala.moveToNext());

                    // totCantidades = totCantidades + (Double.valueOf(aCantidad)).intValue();
                    int Entro=0;
                    Cursor Escala = BaseDeDatos.getWritableDatabase().rawQuery("select MovParEscDesc1,MovParEscDesc2 from MovParEsc where MovParEscArtSec='" + ArtSec.trim() + "' and MovParEscDe<=" + totCantidades + " and MovParEscHasta>=" + totCantidades + " and MovParEscDeCaj+MovParEscHastaCaj=0", null);
                    if (Escala.getCount() > 0) {
                        Escala.moveToFirst();
                        do {
                            String ValDesc3 = String.valueOf(Escala.getDouble(0));
                            String ValDesc4 = String.valueOf(Escala.getDouble(1));

                            BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc3=" + Double.valueOf(ValDesc3) + ",pordesc3no=" + Double.valueOf(ValDesc3) + " where pordesc3no<>" + Double.valueOf(ValDesc3) + " and nitsec='" + NitSec + "' and clisec=" + CliSec + " and artsec in(select MovParEscArtSec from MovParEsc where MovParEscSec=(select MovParEscSec from MovParEsc where MovParEscArtSec='" + ArtSec + "' )) and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo like '" + Prefijo + "'");

                            Entro=1;
                            // and MovParEscArtSec<>'" + codigo[position2].trim() + "'
                        } while (Escala.moveToNext());

                    } else {
                        BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc3=0,pordesc3no=0 where nitsec='" + NitSec + "' and clisec=" + CliSec + " and artsec in(select MovParEscArtSec from MovParEsc where MovParEscSec=(select MovParEscSec from MovParEsc where MovParEscArtSec='" + ArtSec + "' )) and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo like '" + Prefijo + "'");
                    }
                    Cursor Escala2 = BaseDeDatos.getWritableDatabase().rawQuery("select MovParEscDesc1,MovParEscDesc2 from MovParEsc where MovParEscArtSec='" + ArtSec.trim() + "' and MovParEscDeCaj<=" + totCantidades + " and MovParEscHastaCaj>=" + totCantidades + " and MovParEscDe+MovParEscHasta=0", null);
                    if (Escala2.getCount() > 0) {
                        Escala2.moveToFirst();
                        do {
                            String ValDesc3 = String.valueOf(Escala2.getDouble(0));
                            String ValDesc4 = String.valueOf(Escala2.getDouble(1));

                            BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc3=" + Double.valueOf(ValDesc3) + ",pordesc3no=" + Double.valueOf(ValDesc3) + " where pordesc3no<>" + Double.valueOf(ValDesc3) + " and nitsec='" + NitSec + "' and clisec=" + CliSec + " and artsec in(select MovParEscArtSec from MovParEsc where MovParEscSec=(select MovParEscSec from MovParEsc where MovParEscArtSec='" + ArtSec + "' )) and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo like '" + Prefijo + "'");
                            // and MovParEscArtSec<>'" + codigo[position2].trim() + "'
                        } while (Escala2.moveToNext());

                    } else {
                        if (Entro==0){
                            BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc3=0,pordesc3no=0 where nitsec='" + NitSec + "' and clisec=" + CliSec + " and artsec in(select MovParEscArtSec from MovParEsc where MovParEscSec=(select MovParEscSec from MovParEsc where MovParEscArtSec='" + ArtSec + "' )) and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo like '" + Prefijo + "'");
                        }
                    }
                    Escala.close();
                }
            }catch(Exception e) {
                int pp = 0;
            }

            try {
                int traerdcto=0;
                Cursor cursorCla = BaseDeDatos.getReadableDatabase().rawQuery("select * from PerfilClientesClase ", null);
                if (cursorCla.getCount()>0){
                    traerdcto=1;
                }


                Cursor datosarticulo = BaseDeDatos.getWritableDatabase().rawQuery("select invgrucod,invsubgrucod,invfamcod from articulos where artsec='"+Codigo+"' ", null); //and MOvParEscInd<>'IND'
                if (datosarticulo.getCount() > 0) {
                    datosarticulo.moveToFirst();
                    String Consulta = "";
                    Consulta = "select MovParLinDes,MovParLinResCan,MovParLinViaDir,MovParLinNoOtor from MovParLinea where MovParLinArtSec='" + Codigo + "' ";

                    Double DctoLinea = 0.0;
                    Cursor haydesart = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null); //and MOvParEscInd<>'IND'
                    // and MovParEscArtSec<>'" + codigo[position2].trim() + "'
                    if (haydesart.getCount() > 0) {
                        haydesart.moveToFirst();
                        do {
                            DctoLinea += haydesart.getDouble(0);
                        } while (haydesart.moveToNext());
                    }
                    if (traerdcto == 1  || vEmpresa.trim().equalsIgnoreCase("PROMEFAR") ){

                    }else{
                        BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc=" + DctoLinea + ",pordescno=" + DctoLinea + " where pordescno<>" + DctoLinea + " and nitsec='" + NitSec + "' and clisec=" + CliSec + " and artsec ='" + Codigo + "' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo like '" + Prefijo + "'");
                    }

                }
            }catch(Exception e) {
                int pp = 0;
            }

            try {
                Double DctoArt1=0.0;
                SDTResumenPedidos vSDTResumenPedidos=TotalesPedido(pContext,Prefijo,NitSec,CliSec,"","","");
                Cursor MovParArt = BaseDeDatos.getWritableDatabase().rawQuery("select MovParValDetDesc from movparvalrango where MovParValDetRan1<="+vSDTResumenPedidos.Subtotal+" and MovParValDetRan2>="+vSDTResumenPedidos.Subtotal, null); //and MOvParEscInd<>'IND'
                if (MovParArt.getCount() > 0) {
                    MovParArt.moveToFirst();

                    do{
                        DctoArt1+=MovParArt.getDouble(0);
                    } while (MovParArt.moveToNext());

                    BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc6="+DctoArt1+",pordesc6no="+DctoArt1+" where pordesc6no<>"+DctoArt1+" and nitsec='" + NitSec + "' and clisec=" + CliSec + " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo = '" + Prefijo + "' ");

                }
            }catch(Exception e) {
                int pp = 0;
            }

            try {
                int traerdcto=0;
                Cursor cursorCla = BaseDeDatos.getReadableDatabase().rawQuery("select * from PerfilClientesClase ", null);
                if (cursorCla.getCount()>0){
                    traerdcto=1;
                }

                Double DctoArt1=0.0;
                Double DctoArt2=0.0;
                String ciucod = "";
                String Tempciu = "select CiuCod from Clientes where nitsec='"+NitSec+"' and CliSec=" + CliSec + " ";
                Cursor ciudadcli = BaseDeDatos.getWritableDatabase().rawQuery(Tempciu, null);
                if (ciudadcli.getCount() > 0) {
                    ciudadcli.moveToFirst();
                    do{
                        ciucod = ciudadcli.getString(0);
                    } while (ciudadcli.moveToNext());
                }


                Cursor MovParArt = BaseDeDatos.getWritableDatabase().rawQuery("select MovParArtDetDesc,MovParNumDcto,MovParViaDir,MovParNoOtor from MovParArt where MovParArtDetArtSec='"+Codigo+"' and MovParArtCiucod = '"+ciucod+"'", null); //and MOvParEscInd<>'IND'
                if (MovParArt.getCount() > 0) {
                    MovParArt.moveToFirst();

                    do{
                        if (MovParArt.getString(1).equalsIgnoreCase("2")) {
                            DctoArt2 += MovParArt.getDouble(0);
                        }else {
                            DctoArt1 += MovParArt.getDouble(0);
                        }
                    } while (MovParArt.moveToNext());

                    //pordesc2no<>"+DctoArt1+" and
                    if (traerdcto == 1) {

                    }else{
                        BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc2=" + DctoArt1 + ",pordesc2no=" + DctoArt1 + " where pordesc2no<>" + DctoArt1 + " and  nitsec='" + NitSec + "' and clisec=" + CliSec + " and artsec ='" + Codigo + "' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo ='" + Prefijo + "'");
                    }
                        /*Cursor Actualizo =BaseDeDatos.getWritableDatabase().rawQuery("select pordesc2 from pedido where nitsec='" + NitSec + "' and clisec=" + CliSec + " and artsec ='"+Codigo+"' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo ='" + Prefijo + "'",null);

                        if (Actualizo.getCount() > 0) {
                            Actualizo.moveToFirst();
                            DctoArt1 += Actualizo.getDouble(0);
                        }*/

                    BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc6=0,pordesc6no=0,pordesc5="+DctoArt2+",pordesc5no="+DctoArt2+" where  pordesc5no<>"+DctoArt2+" and nitsec='" + NitSec + "' and clisec=" + CliSec + " and artsec ='"+Codigo+"' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo = '" + Prefijo + "'");
                    if (DctoArt2>0) {
                        BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc6=0,pordesc6no=0 where  ifnull(NotaInv,'N') <> 'S' and ifnull(NotaCar,'N')  <> 'S' and PreArtcod = '"+PreArtCod+"' and  nitsec='" + NitSec + "' and clisec=" + CliSec + " and artsec ='" + Codigo + "' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo = '" + Prefijo + "'");
                    }

                }
            }catch(Exception e) {
                int pp = 0;
            }

            try {
                Double DctoArt1=0.0;
                Double DctoArt2=0.0;
                Cursor MovParArt2 = BaseDeDatos.getWritableDatabase().rawQuery("select nitsec,clisec,CliDesInvGruCod,CLiDesDcto,CliDesFin from ClientesDcto where nitsec='"+NitSec+"' and clisec="+CliSec+" ", null); //and MOvParEscInd<>'IND'
                if (MovParArt2.getCount() > 0) {
                    MovParArt2.moveToFirst();
                    DctoArt2 = MovParArt2.getDouble(3);
                    String Invgrucod = MovParArt2.getString(2);

                    if(vEmpresa.equalsIgnoreCase("SUHOGAR")){
                        if (DctoArt2!=0) {
                            BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc4no="+DctoArt2+" where ifnull(NotaInv,'N') <> 'S' and ifnull(NotaCar,'N')  <> 'S' and PreArtcod = '"+PreArtCod+"' and  artsec in (select artsec from articulos where InvGruCod='"+Invgrucod+"') and  nitsec='" + NitSec + "' and clisec=" + CliSec + " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo ='" + Prefijo + "'");
                        }
                    }else{
                        if (DctoArt2!=0) {
                            BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc4="+DctoArt2+" where ifnull(NotaInv,'N') <> 'S' and ifnull(NotaCar,'N')  <> 'S' and PreArtcod = '"+PreArtCod+"' and artsec in (select artsec from articulos where InvGruCod='"+Invgrucod+"') and  nitsec='" + NitSec + "' and clisec=" + CliSec + " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo ='" + Prefijo + "'");
                        }
                    }


                }
            }catch(Exception e) {
                int pp = 0;
            }
        }
        String HH=vGlobalVariables.getParMovDescV2();

        int SucCod=vGlobalVariables.getSucCod();
        ConBd conbd = new ConBd();
        conbd.Variables();
//        Connection connGen = conbd.CargarConexion();
        String mantisficc =  conbd.MantisFicc;


        if (HH.equalsIgnoreCase("S") || mantisficc.equalsIgnoreCase("S")){  //DESCUENTOS VERSION 2


            String bdartsec = "'99999'";
            try { // Borrar pedidoDesc para abajo volver a insertar los descuentos
                Log.e("Trajo el  bdartsec: ",bdartsec);
                //BaseDeDatos.getWritableDatabase().execSQL("Delete from pedidoDesc where prefijo='"+Prefijo+"' and nitsec='"+NitSec+"' and clisec="+CliSec+" and artsec='"+Codigo+"' and pdyear="+time.year+" and pdmonth="+(time.month + 1)+" and pdday="+time.monthDay);
                BaseDeDatos.getWritableDatabase().execSQL("Delete from pedidoDesc where  DesSec not in(select DESCSEC from Descuentos group by DESCSEC)");
                //clisec
                SDTResumenPedidos vSDTResumenPedidos=TotalesPedido(pContext,Prefijo,NitSec,0,"","","");
                //tipodesc,descgru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,UNIDADNEG,CIUDADES,CLIENTES,VENDEDORES,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal
                String ConsultaPremiosBorrar="select DESCSEC,tipodesc,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal,DescLinPorDesLin,descagru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,CIUDADES,CLIENTES from Descuentos d " +
                        " left join articulos a on artsec="+Codigo+"" +
                        " left join Clientes c on nitsec='"+NitSec+"' and clisec="+CliSec+" " +
                        " where (descgru='XX,' OR descgru like '%,'|| InvGruCod ||',%')  " +
                        " and (SUBGRUPOS='XX,' OR SUBGRUPOS like '%,'|| a.InvSubGruCod ||',%') " +
                        " and (FAMILIAS='XX,' OR FAMILIAS like '%,'|| a.InvFamCod ||',%') " +
                        " and (ARTICULOS='XX,' OR ARTICULOS like '%,'|| a.artsec||"+PreArtCod+" ||',%') " +
                        " and (BODEGAS='XX,' OR BODEGAS like '%,'|| '"+bodcod+"' ||',%') " +
                        " and (LABORATORIO='XX,' OR LABORATORIO like '%,'||  a.LabCod  ||',%') " +
                        " and (CLASE='XX,' OR CLASE like '%,'||  a.invClaCod  ||',%') " +
                        " and (SECCION='XX,' OR SECCION like '%,'|| a.invseccod   ||',%') " +
                        " and (MARCA='XX,' OR MARCA like '%,'||  a.invmarcod ||',%') " +
                        " and (LINEART='XX,' OR LINEART like '%,'||  a.invlincod  ||',%') " +
                        " and (d.CATEGORIA='XX,' OR d.CATEGORIA like '%,'|| a.invcatcod  ||',%') " +
                        " and (SUBCATEGORIA='XX,' OR SUBCATEGORIA like '%,'||  a.invsubcatcod  ||',%') " +
                        " and (CANALES='XX,' OR CANALES like '%,'|| c.CanCod ||',%') " +
                        " and (SUBCANALES='XX,' OR SUBCANALES like '%,'|| c.CanCod ||'-'|| c.CanSubCod || ',%') " +
                        " and (TAMANOS='XX,' OR TAMANOS like '%,'|| c.CliTamCan || ',%') " +
                        " and (CIUDADES='XX,' OR CIUDADES like '%,'|| c.CiuCod ||',%') " +
                        " and (SUCURSALES='XX,' OR SUCURSALES like '%,'||'"+ SucCod +"'||',%') " +
                        " and (PERFILES='XX,' OR PERFILES like '%,'|| c.PerCliCod ||',%') " +
                        " and (CLIENTES='XX,' OR CLIENTES like '%,'|| c.nitsec ||',%') " +
                        " and (VENDEDORES='XX,' OR VENDEDORES like '%,'||'"+ vUsuario+"'|| ',%') " ;
                Cursor CurConsultaPremiosBorrar = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaPremiosBorrar, null); //and MOvParEscInd<>'IND'

                if (CurConsultaPremiosBorrar.getCount() > 0) {
                    CurConsultaPremiosBorrar.moveToFirst();
                    do{
                        Integer descsec = CurConsultaPremiosBorrar.getInt(0);
                        String tipodesc = CurConsultaPremiosBorrar.getString(1);
                        if (tipodesc.equalsIgnoreCase("ART")){
                            BaseDeDatos.getWritableDatabase().execSQL("Delete from pedidoDesc where dPreArtCod = '"+PreArtCod+"' and artsec='"+Codigo+"'  and nitsec='"+NitSec+"'  and DesSec="+descsec+" and pdyear="+time.year+" and pdmonth="+(time.month + 1)+" and pdday="+time.monthDay);
                            // /* and prefijo='"+Prefijo+"' */
                            // /* and clisec="+CliSec+" */
                        }
                        if (tipodesc.equalsIgnoreCase("GRU")){
                            BaseDeDatos.getWritableDatabase().execSQL("Delete from pedidoDesc where dPreArtCod = '"+PreArtCod+"' and artsec in(select artsec from articulos where InvGruCod=(select InvGruCod from  articulos where artsec= '"+Codigo+"'))  and nitsec='"+NitSec+"'  and DesSec="+descsec+" and pdyear="+time.year+" and pdmonth="+(time.month + 1)+" and pdday="+time.monthDay);
                            // /* and prefijo='"+Prefijo+"' */
                            // /*and clisec="+CliSec+"*/
                        }
                        if (tipodesc.equalsIgnoreCase("SUB")){
                            BaseDeDatos.getWritableDatabase().execSQL("Delete from pedidoDesc where dPreArtCod = '"+PreArtCod+"' and artsec in(select artsec from articulos where InvSubGruCod=(select InvSubGruCod from  articulos where artsec= '"+Codigo+"'))  and nitsec='"+NitSec+"'  and DesSec="+descsec+" and pdyear="+time.year+" and pdmonth="+(time.month + 1)+" and pdday="+time.monthDay);
                            // /* and  prefijo='"+Prefijo+"' */
                            // /* and clisec="+CliSec+" */

                        }
                        if (tipodesc.equalsIgnoreCase("FAM")){
                            BaseDeDatos.getWritableDatabase().execSQL("Delete from pedidoDesc where dPreArtCod = '"+PreArtCod+"' and artsec in(select artsec from articulos where InvFamCod=(select InvFamCod from  articulos where artsec= '"+Codigo+"'))  and nitsec='"+NitSec+"' and DesSec="+descsec+" and pdyear="+time.year+" and pdmonth="+(time.month + 1)+" and pdday="+time.monthDay);
                            // /*and  prefijo='"+Prefijo+"' */
                            // /* and clisec="+CliSec+" */
                        }

                    } while (CurConsultaPremiosBorrar.moveToNext());
                }


                String ConsultaReevaluar="select artsec from pedidoDesc where DesSec="+0 ;
                Cursor CurConsultaReevaluar = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaReevaluar, null); //and MOvParEscInd<>'IND'
                if (CurConsultaReevaluar.getCount() > 0) {
                    CurConsultaReevaluar.moveToFirst();
                    do{
                        bdartsec+= ",'"+CurConsultaPremiosBorrar.getString(0).trim()+"'";
                    } while (CurConsultaReevaluar.moveToNext());
                }

            }catch(Exception e) {

                int pp = 0;
            }

            Integer TcantFam=0;
            Double TcantFamCaj=0.0;
            Integer TcantSub=0;
            Double TcantSubCaj=0.0;
            Integer TcantGru=0;
            Double TcantGruCaj=0.0;
            Integer TcantArt=0;
            Double TcantArtCaj=0.0;

            String agInvGruCod="";
            String agSubGruCod="";
            String agInvFamCod="";

            String ConsultaArticulo = "select InvGruCod,InvSubGruCod,InvFamCod from articulos where artsec="+Codigo;
            Cursor CurConsultaArticulo = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaArticulo, null);
            if (CurConsultaArticulo.getCount() > 0){
                CurConsultaArticulo.moveToFirst();
                agInvGruCod=CurConsultaArticulo.getString(0);
                agSubGruCod=CurConsultaArticulo.getString(1);
                agInvFamCod=CurConsultaArticulo.getString(2);
            }

            String ConsultaTxtVen = "select " +
                    "total(case when InvFamCod='"+agInvFamCod+"' then (cant+(cantcaj*pedartemb)) else 0 end) fam," +
                    "total(case when InvFamCod='"+agInvFamCod+"' then ( ((cant+(cantcaj*pedartemb))*1.0/artemb ) ) else 0 end) famcaj," +
                    "total(case when InvSubGruCod='"+agSubGruCod+"' then (cant+(cantcaj*pedartemb)) else 0 end) sub," +
                    "total(case when InvSubGruCod='"+agSubGruCod+"' then ( ((cant+(cantcaj*pedartemb))*1.0/artemb ) ) else 0 end) subcaj," +
                    "total(case when InvGruCod='"+agInvGruCod+"' then (cant+(cantcaj*pedartemb)) else 0 end) gru, " +
                    "total(case when InvGruCod='"+agInvGruCod+"' then ( ((cant+(cantcaj*pedartemb))*1.0/artemb ) ) else 0 end) grucaj, " +
                    "total( case when a.ArtSec='"+Codigo+"'      then (cant+(cantcaj*pedartemb)) else 0 end) tart," +
                    "total( case when a.ArtSec='"+Codigo+"' then ( ((cant+(cantcaj*pedartemb))*1.0/artemb )) else 0 end) tartcaj " +
                    "from pedido p left join articulos a on a.artsec=p.artsec where ifnull(NotaInv,'N') <> 'S' and ifnull(NotaCar,'N')  <> 'S' and p.PreArtcod = '"+PreArtCod+"' and  artemb<>0 and rtrim(nitsec) = rtrim('" +NitSec+ "')  and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and cant+cantcaj>0 "; //and prefijo = '"+Prefijo+"'" //and clisec=" + CliSec+ "
            Cursor MovParMixCntTotal = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaTxtVen, null);
            if (MovParMixCntTotal.getCount() > 0){
                MovParMixCntTotal.moveToFirst();

                TcantFam=MovParMixCntTotal.getInt(0);
                TcantFamCaj=MovParMixCntTotal.getDouble(1); //+0.01
                TcantSub=MovParMixCntTotal.getInt(2);
                TcantSubCaj=MovParMixCntTotal.getDouble(3); //+0.01
                TcantGru=MovParMixCntTotal.getInt(4);
                TcantGruCaj=MovParMixCntTotal.getDouble(5); //+0.01
                TcantArt=MovParMixCntTotal.getInt(6);

                TcantArtCaj=MovParMixCntTotal.getDouble(7); //+0.01
            }
            String ParMovTatTra = "TRA";
            Cursor traCursorUsuarios = BaseDeDatos.getReadableDatabase().rawQuery("select ParMovTatTra from usuarios", null);

            if (traCursorUsuarios.getCount() >0) {
                traCursorUsuarios.moveToFirst();
                ParMovTatTra = traCursorUsuarios.getString(0);
            }




            try {  // Descuentos por Articulo
                Double DctoArt1=0.0;
                Double DctoArt2=0.0;

                Integer Tcant = (Unidades.intValue()+(Cajas*Embalaje)) ;
                String invgrucod = "";
                String consultades = "Select InvGruCod from articulos where artsec = "+Codigo+" ";
                Cursor grupo = BaseDeDatos.getWritableDatabase().rawQuery(consultades, null); //and MOvParEscInd<>'IND'
                if (grupo.getCount() > 0) {
                    grupo.moveToFirst();
                    do {
                        invgrucod = grupo.getString(0);
                    }while (grupo.moveToNext());
                }
//clisec
                Log.e("Trajo el grupo: ",invgrucod);
                SDTResumenPedidos vSDTResumenPedidos=TotalesPedido(pContext,Prefijo,NitSec,0,invgrucod,"","");
                //tipodesc,descgru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,UNIDADNEG,CIUDADES,CLIENTES,VENDEDORES,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal


                Log.e("TcantArt : ", String.valueOf(TcantArt));
                String ConsultaDesc="select DESCSEC,tipodesc,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal,DescLinPorDesLin,descagru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,CIUDADES,CLIENTES,DescArtDes,DesSecLin,c.PerCliCod from Descuentos d " +
                        " left join articulos a on artsec="+Codigo+" OR artsec in("+bdartsec+")" +
                        " left join Clientes c on nitsec='"+NitSec+"' and clisec="+CliSec+" " +
                        " where  DESPROGAPLESCTOT = 'S' and tipodesc='ART' " +
                        " and (descgru='XX,' OR descgru like '%,'|| InvGruCod ||',%')  " +
                        " and (SUBGRUPOS='XX,' OR SUBGRUPOS like '%,'|| a.InvSubGruCod ||',%') " +
                        " and (FAMILIAS='XX,' OR FAMILIAS like '%,'|| a.InvFamCod ||',%') " +
                        //" and (ARTICULOS='XX,' OR ARTICULOS like '%,'|| a.artsec ||',%') " +
                        " and (ARTICULOS='XX,' OR ARTICULOS like '%,'|| a.artsec||"+PreArtCod+" ||',%') " +
                        " and (BODEGAS='XX,' OR BODEGAS like '%,'|| '"+bodcod+"' ||',%') " +
                        " and (LABORATORIO='XX,' OR LABORATORIO like '%,'||  a.LabCod  ||',%') " +
                        " and (CLASE='XX,' OR CLASE like '%,'||  a.invClaCod  ||',%') " +
                        " and (SECCION='XX,' OR SECCION like '%,'|| a.invseccod   ||',%') " +
                        " and (MARCA='XX,' OR MARCA like '%,'||  a.invmarcod ||',%') " +
                        " and (LINEART='XX,' OR LINEART like '%,'||  a.invlincod  ||',%') " +
                        " and (d.CATEGORIA='XX,' OR d.CATEGORIA like '%,'|| a.invcatcod  ||',%') " +
                        " and (SUBCATEGORIA='XX,' OR SUBCATEGORIA like '%,'||  a.invsubcatcod  ||',%') " +
                        " and (CANALES='XX,' OR CANALES like '%,'|| c.CanCod ||',%') " +
                        " and (SUBCANALES='XX,' OR SUBCANALES like '%,'|| c.CanCod ||'-'|| c.CanSubCod || ',%') " +
                        " and (TAMANOS='XX,' OR TAMANOS like '%,'|| c.CliTamCan || ',%') " +
                        " and (SUCURSALES='XX,' OR SUCURSALES like '%,'||'"+ SucCod +"'||',%') " +
                        " and (PERFILES='XX,' OR PERFILES like '%,'|| c.PerCliCod ||',%') " +
                        " and (CIUDADES='XX,' OR CIUDADES like '%,'|| c.CiuCod ||',%') " +
                        " and (CLIENTES='XX,' OR CLIENTES  like  '%,'|| c.nitsec ||',%') " +
                        " and (LISTAS='XX,' OR LISTAS  like  '%,'|| c.LisPreCod ||',%') " +
                        " and (EXCLIENTES='XX,' OR EXCLIENTES  not like  '%,'|| c.nitsec || ',%') " + //andres
                        " and (VENDEDORES='XX,' OR VENDEDORES like '%,'||'"+ vUsuario+"'|| ',%') ";
                       // " and  DescLinDesUni<="+TcantArt+"  and DescLinHasUni>="+TcantArt+" ";

                Log.e("ConsultaDesc DESPROGAPLESCTOT: ", ConsultaDesc);


/*
                    " and ((DescLinDesVal<="+vSDTResumenPedidos.Subtotal+" and DescLinHasVal>="+vSDTResumenPedidos.Subtotal+") or DescLinDesVal+DescLinHasVal=0)"  +
                            " and (( ((DescLinDesUni<="+Tcant+" ) or (DescLinDesUni<="+TcantFam+"  and descagru='FAM') or (DescLinDesUni<="+TcantSub+"  and descagru='SUB') or (DescLinDesUni<="+TcantGru+"  and descagru='GRU') ) " +
                            " and ( (DescLinHasUni>="+Tcant+" and descagru='ART') or (DescLinHasUni>="+TcantFam+" and descagru='FAM') or (DescLinHasUni>="+TcantSub+" and descagru='SUB') or (DescLinHasUni>="+TcantGru+" and descagru='GRU') ) and DescLin1CajMen1<>'S') or DescLinDesUni+DescLinHasUni=0 or DescLin1CajMen1='S' )"  +
                            " and (( ((DescLinDesUni<="+Tcant+" and descagru='ART') or (DescLinDesUni<="+TcantFam+"  and descagru='FAM') or (DescLinDesUni<="+TcantSub+"  and descagru='SUB') or (DescLinDesUni<="+TcantGru+"  and descagru='GRU') ) " +
                            " and ((ArtEmb>"+Tcant+" and descagru='ART') or (ArtEmb>"+TcantFam+" and descagru='FAM') or (ArtEmb>"+TcantSub+" and descagru='SUB') or (ArtEmb>"+TcantGru+" and descagru='GRU')) and DescLin1CajMen1='S') or DescLinDesUni+DescLinHasUni=0 or DescLin1CajMen1<>'S')"  +
                            " and (( ( (DescLinDesCaj*ArtEmb<="+Tcant+" and descagru='ART' ) or (DescLinDesCaj*ArtEmb<="+TcantFam+" and descagru='FAM') or (DescLinDesCaj*ArtEmb<="+TcantSub+" and descagru='SUB' ) or (DescLinDesCaj*ArtEmb<="+TcantGru+" and descagru='GRU' )  ) " +
                            " and ( (DescLinHasCaj*ArtEmb>="+Tcant+" and descagru='ART' ) or (DescLinHasCaj*ArtEmb>="+TcantFam+" and descagru='FAM' )or (DescLinHasCaj*ArtEmb>="+TcantSub+" and descagru='SUB' )or (DescLinHasCaj*ArtEmb>="+TcantGru+" and descagru='GRU' )   )     )  or DescLinDesCaj+DescLinHasCaj=0)"*/

                Cursor MovParArt3 = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaDesc, null); //and MOvParEscInd<>'IND'

                Double DctoArtC1=0.0;
                Double DctoArtC2=0.0;
                Double DctoArtC3=0.0;
                Double DctoArtC4=0.0;
                Double tmDctoArtC1=0.0;
                Double tmDctoArtC2=0.0;
                Double tmDctoArtC3=0.0;
                Double tmDctoArtC4=0.0;

                if (MovParArt3.getCount() > 0) {
                    MovParArt3.moveToFirst();
                    do{

                        Log.e("ConsultaDesc DESPROGAPLESCTOT: ", ConsultaDesc);
                        Log.e("Entro al descuento: ",MovParArt3.getString(19));

                        DctoArt2 = MovParArt3.getDouble(9);
                        Integer descsec = MovParArt3.getInt(0);
                        String DescArtDes = MovParArt3.getString(19); //Andres
                        int DesSecLin = MovParArt3.getInt(20);
                        int PerCliCod = MovParArt3.getInt(21);
                        String tipodesc = MovParArt3.getString(1);
                        int valordesde = MovParArt3.getInt(2);
                        int valorhasta = MovParArt3.getInt(3);






                        String sqarticulos = "  SELECT\n" +
                                "  '(' || GROUP_CONCAT(QUOTE(artsec)) || ')' AS lista_articulos\n" +
                                "   FROM (\n" +
                                "   SELECT artsec\n" +
                                "   FROM DescuentosDetalle\n" +
                                "    WHERE DESCSEC = "+descsec+" \n" +
                                "  ORDER BY artsec\n" +
                                "   );";
                        String inArticulos = "";
                        Cursor strinart = BaseDeDatos.getWritableDatabase().rawQuery(sqarticulos, null);
                        if (strinart.getCount() > 0){
                            strinart.moveToFirst();
                            do{
                                inArticulos=strinart.getString(0);
                            } while (strinart.moveToNext());
                        }

                        Log.e("inArticulos: ",inArticulos);
                        String validarpedido = "select sum(ifnull(cant,0)) cantidad  " +
                                " from pedido p " +
                                " where   cant+cantcaj<>0 and p.artsec||p.preartcod in "+inArticulos+"  " +
                                " and  nitsec='" + NitSec + "' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " " +
                                " and pdday=" + time.monthDay   ;




                        Integer valcantidadart = 0;
                        Cursor cantidadart = BaseDeDatos.getWritableDatabase().rawQuery(validarpedido, null);
                        if (cantidadart.getCount() > 0){
                            cantidadart.moveToFirst();
                            do{
                                valcantidadart=cantidadart.getInt(0);
                            } while (cantidadart.moveToNext());
                        }





                        tmDctoArtC1=0.0;
                        tmDctoArtC2=0.0;
                        tmDctoArtC3=0.0;
                        tmDctoArtC4=0.0;

                        Log.e("valcantidadart: ",String.valueOf(valcantidadart));
                        Log.e("valordesde: ",String.valueOf(valordesde));
                        Log.e("valcantidadart: ",String.valueOf(valcantidadart));


                        if(valordesde <= valcantidadart && valorhasta >= valcantidadart ){
                            Log.e("entro: ",inArticulos);

                            BaseDeDatos.getWritableDatabase().execSQL("Delete from pedidoDesc where kardesgen ='S' and artsec||dpreartcod in "+inArticulos+"  " +
                                    " and nitsec='"+NitSec+"'  and DesSec="+descsec+" " +
                                    " and pdyear="+time.year+" and pdmonth="+(time.month + 1)+" and pdday="+time.monthDay);


                          DctoArtC2+=DctoArt2;
                            tmDctoArtC2=DctoArt2;



                        Log.e("EnDctoArtC4DctoArtC2",String.valueOf(DctoArtC2));
                        String ConsultaArticulosRela = "select p.ArtSec,p.CliSec,p.prefijo,p.PreArtcod " +
                                " from pedido p left join articulos a on a.artsec=p.artsec where " +
                                " ifnull(NotaInv,'N') <> 'S' and ifnull(NotaCar,'N')  <> 'S' " +
                                //" and p.PreArtcod = '"+PreArtCod+"' and cant+cantcaj<>0 and " +
                                "  and cant+cantcaj<>0 and " +
                                "  p.artsec||p.preartcod in "+inArticulos+"  and  nitsec='" + NitSec + "' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay   ;
                        ///* and clisec=" + CliSec  + " */
                        // and prefijo ='" + Prefijo + "'";

                        Cursor CurConsultaArticulosRela = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaArticulosRela, null);
                        String ArtSecADesc="";
                        Integer CliSecDesc=0;
                            String prefijoDesc="";
                            String preartDesc="";
                        if (CurConsultaArticulosRela.getCount() > 0){
                            CurConsultaArticulosRela.moveToFirst();
                            do{



                                ArtSecADesc=CurConsultaArticulosRela.getString(0);
                                CliSecDesc=CurConsultaArticulosRela.getInt(1);
                                prefijoDesc=CurConsultaArticulosRela.getString(2);
                                preartDesc=CurConsultaArticulosRela.getString(3);
                                Log.e("insertoArtSecADesc: ",ArtSecADesc);
                                BaseDeDatos.getWritableDatabase().execSQL("insert into pedidoDesc(DesSec,dPreArtCod,prefijo,nitsec,clisec,artsec,pdyear,pdmonth,pdday,TipoDesc,DesSecLin,pordescapli1,pordescapli2,pordescapli3,pordescapli4,kardesgen) values("+descsec+",'"+preartDesc+"','"+prefijoDesc+"','"+NitSec+"',"+CliSecDesc+",'"+ArtSecADesc+"',"+time.year+","+(time.month + 1)+","+time.monthDay+",'"+tipodesc+"',"+DesSecLin+","+tmDctoArtC1+","+tmDctoArtC2+","+tmDctoArtC3+","+tmDctoArtC4+",'S')");
                            } while (CurConsultaArticulosRela.moveToNext());
                        }

                        }


                        //     BaseDeDatos.getWritableDatabase().execSQL("insert into pedidoDesc(DesSec,prefijo,nitsec,clisec,artsec,pdyear,pdmonth,pdday,pordescapli1,pordescapli2,pordescapli3,pordescapli4) values("+descsec+",'"+Prefijo+"','"+NitSec+"',"+CliSec+",'"+Codigo+"',"+time.year+","+(time.month + 1)+","+time.monthDay+","+tmDctoArtC1+","+tmDctoArtC2+","+tmDctoArtC3+","+tmDctoArtC4+")");

                        //artsec='"+Codigo+"' and  nitsec='" + NitSec + "' and clisec=" + CliSec + "
                        //  pordescapli1 numeric(8,4),pordescapli2 numeric(8,4),pordescapli3 numeric(8,4),pordescapli4 numeric(8,4)
                    } while (MovParArt3.moveToNext());
                }
                //  if (DctoArtC1!=0 || DctoArtC2!=0 || DctoArtC3!=0 || DctoArtC4!=0 ) {
 /*                   BaseDeDatos.getWritableDatabase().execSQL("update pedido set " +
                            " pordesc=CASE WHEN "+DctoArtC1+" >= pordesc and pordesc<>0 and pordesc<pordescno  THEN pordesc " +
                            " WHEN  "+DctoArtC1+" < pordesc and pordesc<=pordescno and pordesc<>0 and "+DctoArtC1+"<>0 THEN pordesc ELSE "+DctoArtC1+" END," +
                            " pordescno="+DctoArtC1+"," +
                            " pordesc2=CASE WHEN "+DctoArtC2+" >= pordesc2 and pordesc2<>0 and pordesc2<pordesc2no  THEN pordesc2 " +
                            " WHEN  "+DctoArtC2+" < pordesc2 and pordesc2<=pordesc2no and pordesc2<>0 and "+DctoArtC2+"<>0 THEN pordesc2 ELSE "+DctoArtC2+" END," +
                            " pordesc2no="+DctoArtC2+"," +
                            " pordesc3=CASE WHEN "+DctoArtC3+" >= pordesc3 and pordesc3<>0 and pordesc3<pordesc3no  THEN pordesc3" +
                            " WHEN  "+DctoArtC3+" < pordesc3 and pordesc3<=pordesc3no and pordesc3<>0 and "+DctoArtC3+"<>0 THEN pordesc3 ELSE "+DctoArtC3+" END," +
                            " pordesc3no="+DctoArtC3+"," +
                            " pordesc4=CASE WHEN "+DctoArtC4+" >= pordesc4 and pordesc4<>0 and pordesc4<pordesc4no  THEN pordesc4 " +
                            " WHEN  "+DctoArtC4+" < pordesc4 and pordesc4<=pordesc4no and pordesc4<>0 and "+DctoArtC4+"<>0 THEN pordesc4 ELSE "+DctoArtC4+" END," +
                            " pordesc4no="+DctoArtC4+"" +
                            " where artsec='"+Codigo+"' and  nitsec='" + NitSec + "' and clisec=" + CliSec + " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo ='" + Prefijo + "'");

  /*

  */

                //BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc="+DctoArtC1+" ,pordesc2="+DctoArtC2+",pordesc3="+DctoArtC3+",pordesc4="+DctoArtC4+" where artsec='"+Codigo+"' and  nitsec='" + NitSec + "' and clisec=" + CliSec + " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo ='" + Prefijo + "'");


                //  }
            }catch(Exception e) {
                Log.e("Error decuento generar",e.toString());
                int pp = 0;
            }


            try {  // Descuentos por Articulo
                Double DctoArt1=0.0;
                Double DctoArt2=0.0;

                Integer Tcant = (Unidades.intValue()+(Cajas*Embalaje)) ;
                String invgrucod = "";
              String consultades = "Select InvGruCod from articulos where artsec = "+Codigo+" ";
                Cursor grupo = BaseDeDatos.getWritableDatabase().rawQuery(consultades, null); //and MOvParEscInd<>'IND'
                if (grupo.getCount() > 0) {
                    grupo.moveToFirst();
                    do {
                        invgrucod = grupo.getString(0);
                    }while (grupo.moveToNext());
                }
//clisec
                Log.e("Trajo el grupo: ",invgrucod);
                SDTResumenPedidos vSDTResumenPedidos=TotalesPedido(pContext,Prefijo,NitSec,0,invgrucod,"","");
                //tipodesc,descgru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,UNIDADNEG,CIUDADES,CLIENTES,VENDEDORES,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal


                Log.e("TcantArt : ", String.valueOf(TcantArt));
                String ConsultaDesc="select DESCSEC,tipodesc,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal," +
                        " DescLinPorDesLin,descagru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,CIUDADES,CLIENTES,DescArtDes,DesSecLin,c.PerCliCod " +
                        " from Descuentos d " +
                        " left join articulos a on a.preartcod = '"+PreArtCod+"' and artsec="+Codigo+" OR artsec in("+bdartsec+")" +
                        " left join Clientes c on nitsec='"+NitSec+"' and clisec="+CliSec+" " +
                        " where DESPROGAPLESCTOT = 'N' and tipodesc='ART' " +
                        " and (descgru='XX,' OR descgru like '%,'|| InvGruCod ||',%')  " +
                        " and (SUBGRUPOS='XX,' OR SUBGRUPOS like '%,'|| a.InvSubGruCod ||',%') " +
                        " and (FAMILIAS='XX,' OR FAMILIAS like '%,'|| a.InvFamCod ||',%') " +
                        //" and (ARTICULOS='XX,' OR ARTICULOS like '%,'|| a.artsec ||',%') " +
                        " and (ARTICULOS='XX,' OR ARTICULOS like '%,'|| a.artsec||a.preartcod  ||',%') " +
                        " and (BODEGAS='XX,' OR BODEGAS like '%,'|| '"+bodcod+"' ||',%') " +
                        " and (LABORATORIO='XX,' OR LABORATORIO like '%,'||  a.LabCod  ||',%') " +
                        " and (CLASE='XX,' OR CLASE like '%,'||  a.invClaCod  ||',%') " +
                        " and (SECCION='XX,' OR SECCION like '%,'|| a.invseccod   ||',%') " +
                        " and (MARCA='XX,' OR MARCA like '%,'||  a.invmarcod ||',%') " +
                        " and (LINEART='XX,' OR LINEART like '%,'||  a.invlincod  ||',%') " +
                        " and (d.CATEGORIA='XX,' OR d.CATEGORIA like '%,'|| a.invcatcod  ||',%') " +
                        " and (SUBCATEGORIA='XX,' OR SUBCATEGORIA like '%,'||  a.invsubcatcod  ||',%') " +
                        " and (CANALES='XX,' OR CANALES like '%,'|| c.CanCod ||',%') " +
                        " and (SUBCANALES='XX,' OR SUBCANALES like '%,'|| c.CanCod ||'-'|| c.CanSubCod || ',%') " +
                        " and (TAMANOS='XX,' OR TAMANOS like '%,'|| c.CliTamCan || ',%') " +
                        " and (SUCURSALES='XX,' OR SUCURSALES like '%,'||'"+ SucCod +"'||',%') " +
                        " and (PERFILES='XX,' OR PERFILES like '%,'|| c.PerCliCod ||',%') " +
                        " and (CIUDADES='XX,' OR CIUDADES like '%,'|| c.CiuCod ||',%') " +
                        " and (CLIENTES='XX,' OR CLIENTES  like  '%,'|| c.nitsec ||',%') " +
                        " and (LISTAS='XX,' OR LISTAS  like  '%,'|| c.LisPreCod ||',%') " +

                        " and (EXCLIENTES='XX,' OR EXCLIENTES  not like  '%,'|| c.nitsec || ',%') " + //andres
                        " and (VENDEDORES='XX,' OR VENDEDORES like '%,'||'"+ vUsuario+"'|| ',%') " +
                        " and  DescLinDesUni<="+TcantArt+"  and DescLinHasUni>="+TcantArt+" ";

                Log.e("ConsultaDesc : ", ConsultaDesc);


/*
                    " and ((DescLinDesVal<="+vSDTResumenPedidos.Subtotal+" and DescLinHasVal>="+vSDTResumenPedidos.Subtotal+") or DescLinDesVal+DescLinHasVal=0)"  +
                            " and (( ((DescLinDesUni<="+Tcant+" ) or (DescLinDesUni<="+TcantFam+"  and descagru='FAM') or (DescLinDesUni<="+TcantSub+"  and descagru='SUB') or (DescLinDesUni<="+TcantGru+"  and descagru='GRU') ) " +
                            " and ( (DescLinHasUni>="+Tcant+" and descagru='ART') or (DescLinHasUni>="+TcantFam+" and descagru='FAM') or (DescLinHasUni>="+TcantSub+" and descagru='SUB') or (DescLinHasUni>="+TcantGru+" and descagru='GRU') ) and DescLin1CajMen1<>'S') or DescLinDesUni+DescLinHasUni=0 or DescLin1CajMen1='S' )"  +
                            " and (( ((DescLinDesUni<="+Tcant+" and descagru='ART') or (DescLinDesUni<="+TcantFam+"  and descagru='FAM') or (DescLinDesUni<="+TcantSub+"  and descagru='SUB') or (DescLinDesUni<="+TcantGru+"  and descagru='GRU') ) " +
                            " and ((ArtEmb>"+Tcant+" and descagru='ART') or (ArtEmb>"+TcantFam+" and descagru='FAM') or (ArtEmb>"+TcantSub+" and descagru='SUB') or (ArtEmb>"+TcantGru+" and descagru='GRU')) and DescLin1CajMen1='S') or DescLinDesUni+DescLinHasUni=0 or DescLin1CajMen1<>'S')"  +
                            " and (( ( (DescLinDesCaj*ArtEmb<="+Tcant+" and descagru='ART' ) or (DescLinDesCaj*ArtEmb<="+TcantFam+" and descagru='FAM') or (DescLinDesCaj*ArtEmb<="+TcantSub+" and descagru='SUB' ) or (DescLinDesCaj*ArtEmb<="+TcantGru+" and descagru='GRU' )  ) " +
                            " and ( (DescLinHasCaj*ArtEmb>="+Tcant+" and descagru='ART' ) or (DescLinHasCaj*ArtEmb>="+TcantFam+" and descagru='FAM' )or (DescLinHasCaj*ArtEmb>="+TcantSub+" and descagru='SUB' )or (DescLinHasCaj*ArtEmb>="+TcantGru+" and descagru='GRU' )   )     )  or DescLinDesCaj+DescLinHasCaj=0)"*/

                Cursor MovParArt3 = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaDesc, null); //and MOvParEscInd<>'IND'

                Double DctoArtC1=0.0;
                Double DctoArtC2=0.0;
                Double DctoArtC3=0.0;
                Double DctoArtC4=0.0;
                Double tmDctoArtC1=0.0;
                Double tmDctoArtC2=0.0;
                Double tmDctoArtC3=0.0;
                Double tmDctoArtC4=0.0;

                if (MovParArt3.getCount() > 0) {
                    MovParArt3.moveToFirst();
                    do{
                        Log.e("Entro al descuento: ",MovParArt3.getString(19));

                        DctoArt2 = MovParArt3.getDouble(9);
                        Integer descsec = MovParArt3.getInt(0);
                        String DescArtDes = MovParArt3.getString(19); //Andres
                        int DesSecLin = MovParArt3.getInt(20);
                        int PerCliCod = MovParArt3.getInt(21);
                        String tipodesc = MovParArt3.getString(1);

                        tmDctoArtC1=0.0;
                        tmDctoArtC2=0.0;
                        tmDctoArtC3=0.0;
                        tmDctoArtC4=0.0;

                        if (tipodesc.equalsIgnoreCase("LIN")){
                            DctoArtC1+=DctoArt2;
                            tmDctoArtC1=DctoArt2;
                        }
                        if (tipodesc.equalsIgnoreCase("PRO") || (tipodesc.equalsIgnoreCase("ART") && DescArtDes.equalsIgnoreCase("DESPRO"))){ //Andres
                            DctoArtC2+=DctoArt2;
                            tmDctoArtC2=DctoArt2;
                        }
                        if (tipodesc.equalsIgnoreCase("EMP")){
                            DctoArtC3+=DctoArt2;
                            tmDctoArtC3=DctoArt2;
                        }
                        if (tipodesc.equalsIgnoreCase("ART") && !DescArtDes.equalsIgnoreCase("DESPRO")){//Andres
                            DctoArtC4+=DctoArt2;
                            tmDctoArtC4=DctoArt2;
                        }

                        Log.e("EnDctoArtC4",String.valueOf(DctoArtC4));
                        String ConsultaArticulosRela = "select p.ArtSec,p.CliSec,p.prefijo from pedido p left join articulos a on a.artsec=p.artsec where ifnull(NotaInv,'N') <> 'S' and ifnull(NotaCar,'N')  <> 'S' and p.PreArtcod = '"+PreArtCod+"' and cant+cantcaj<>0 and p.artsec='"+Codigo+"'  and  nitsec='" + NitSec + "' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay   ;
                        ///* and clisec=" + CliSec  + " */
                        // and prefijo ='" + Prefijo + "'";

                        Cursor CurConsultaArticulosRela = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaArticulosRela, null);
                        String ArtSecADesc="";
                        Integer CliSecDesc=0;
                        String prefijoDesc="";
                        if (CurConsultaArticulosRela.getCount() > 0){
                            CurConsultaArticulosRela.moveToFirst();
                            do{
                                ArtSecADesc=CurConsultaArticulosRela.getString(0);
                                CliSecDesc=CurConsultaArticulosRela.getInt(1);
                                prefijoDesc=CurConsultaArticulosRela.getString(2);

                                BaseDeDatos.getWritableDatabase().execSQL("insert into pedidoDesc(DesSec,dPreArtCod,prefijo,nitsec,clisec,artsec,pdyear,pdmonth,pdday,TipoDesc,DesSecLin,pordescapli1,pordescapli2,pordescapli3,pordescapli4) values("+descsec+",'"+PreArtCod+"','"+prefijoDesc+"','"+NitSec+"',"+CliSecDesc+",'"+ArtSecADesc+"',"+time.year+","+(time.month + 1)+","+time.monthDay+",'"+tipodesc+"',"+DesSecLin+","+tmDctoArtC1+","+tmDctoArtC2+","+tmDctoArtC3+","+tmDctoArtC4+")");
                            } while (CurConsultaArticulosRela.moveToNext());
                        }

                        //     BaseDeDatos.getWritableDatabase().execSQL("insert into pedidoDesc(DesSec,prefijo,nitsec,clisec,artsec,pdyear,pdmonth,pdday,pordescapli1,pordescapli2,pordescapli3,pordescapli4) values("+descsec+",'"+Prefijo+"','"+NitSec+"',"+CliSec+",'"+Codigo+"',"+time.year+","+(time.month + 1)+","+time.monthDay+","+tmDctoArtC1+","+tmDctoArtC2+","+tmDctoArtC3+","+tmDctoArtC4+")");

                        //artsec='"+Codigo+"' and  nitsec='" + NitSec + "' and clisec=" + CliSec + "
                        //  pordescapli1 numeric(8,4),pordescapli2 numeric(8,4),pordescapli3 numeric(8,4),pordescapli4 numeric(8,4)
                    } while (MovParArt3.moveToNext());
                }
                //  if (DctoArtC1!=0 || DctoArtC2!=0 || DctoArtC3!=0 || DctoArtC4!=0 ) {
 /*                   BaseDeDatos.getWritableDatabase().execSQL("update pedido set " +
                            " pordesc=CASE WHEN "+DctoArtC1+" >= pordesc and pordesc<>0 and pordesc<pordescno  THEN pordesc " +
                            " WHEN  "+DctoArtC1+" < pordesc and pordesc<=pordescno and pordesc<>0 and "+DctoArtC1+"<>0 THEN pordesc ELSE "+DctoArtC1+" END," +
                            " pordescno="+DctoArtC1+"," +
                            " pordesc2=CASE WHEN "+DctoArtC2+" >= pordesc2 and pordesc2<>0 and pordesc2<pordesc2no  THEN pordesc2 " +
                            " WHEN  "+DctoArtC2+" < pordesc2 and pordesc2<=pordesc2no and pordesc2<>0 and "+DctoArtC2+"<>0 THEN pordesc2 ELSE "+DctoArtC2+" END," +
                            " pordesc2no="+DctoArtC2+"," +
                            " pordesc3=CASE WHEN "+DctoArtC3+" >= pordesc3 and pordesc3<>0 and pordesc3<pordesc3no  THEN pordesc3" +
                            " WHEN  "+DctoArtC3+" < pordesc3 and pordesc3<=pordesc3no and pordesc3<>0 and "+DctoArtC3+"<>0 THEN pordesc3 ELSE "+DctoArtC3+" END," +
                            " pordesc3no="+DctoArtC3+"," +
                            " pordesc4=CASE WHEN "+DctoArtC4+" >= pordesc4 and pordesc4<>0 and pordesc4<pordesc4no  THEN pordesc4 " +
                            " WHEN  "+DctoArtC4+" < pordesc4 and pordesc4<=pordesc4no and pordesc4<>0 and "+DctoArtC4+"<>0 THEN pordesc4 ELSE "+DctoArtC4+" END," +
                            " pordesc4no="+DctoArtC4+"" +
                            " where artsec='"+Codigo+"' and  nitsec='" + NitSec + "' and clisec=" + CliSec + " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo ='" + Prefijo + "'");

  /*

  */

                //BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc="+DctoArtC1+" ,pordesc2="+DctoArtC2+",pordesc3="+DctoArtC3+",pordesc4="+DctoArtC4+" where artsec='"+Codigo+"' and  nitsec='" + NitSec + "' and clisec=" + CliSec + " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo ='" + Prefijo + "'");


                //  }
            }catch(Exception e) {
                int pp = 0;
            }
            try {  // Descuentos por GRUPO
                Double DctoArt1=0.0;
                Double DctoArt2=0.0;

                Integer Tcant = (Unidades.intValue()+(Cajas*Embalaje)) ;
                //CliSec
                SDTResumenPedidos vSDTResumenPedidos=TotalesPedido(pContext,Prefijo,NitSec,0,"","","");


                //tipodesc,descgru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,UNIDADNEG,CIUDADES,CLIENTES,VENDEDORES,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal
                String ConsultaDesc="select DESCSEC,tipodesc,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal,DescLinPorDesLin,descagru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,CIUDADES,CLIENTES,DescArtDes,DesSecLin from Descuentos d " +
                        " left join articulos a on artsec="+Codigo+"" +
                        " left join Clientes c on nitsec='"+NitSec+"' and clisec="+CliSec+" " +
                        " where descagru='GRU"  +
                        "' and DescPorMov = '"+ParMovTatTra+"' and " +
                        " (descgru='XX,' OR descgru like '%,'|| InvGruCod ||',%')  and (SUBGRUPOS='XX,' OR SUBGRUPOS like '%,'|| a.InvSubGruCod ||',%') " +
                        " and (FAMILIAS='XX,' OR FAMILIAS like '%,'|| a.InvFamCod ||',%') " +
                        " and (ARTICULOS='XX,' OR ARTICULOS like '%,'|| a.artsec ||',%') " +
                        " and (CANALES='XX,' OR CANALES like '%,'|| c.CanCod ||',%') " +
                        " and (BODEGAS='XX,' OR BODEGAS like '%,'|| '"+bodcod+"' ||',%') " +
                        " and (LABORATORIO='XX,' OR LABORATORIO like '%,'||  a.LabCod  ||',%') " +
                        " and (CLASE='XX,' OR CLASE like '%,'||  a.invClaCod  ||',%') " +
                        " and (SECCION='XX,' OR SECCION like '%,'|| a.invseccod   ||',%') " +
                        " and (MARCA='XX,' OR MARCA like '%,'||  a.invmarcod ||',%') " +
                        " and (LINEART='XX,' OR LINEART like '%,'||  a.invlincod  ||',%') " +
                        " and (d.CATEGORIA='XX,' OR d.CATEGORIA like '%,'|| a.invcatcod  ||',%') " +
                        " and (SUBCATEGORIA='XX,' OR SUBCATEGORIA like '%,'||  a.invsubcatcod  ||',%') " +
                        " and (SUBCANALES='XX,' OR SUBCANALES like '%,'|| c.CanCod ||'-'|| c.CanSubCod || ',%') " +
                        " and (TAMANOS='XX,' OR TAMANOS like '%,'|| c.CliTamCan || ',%') " +
                        " and (SUCURSALES='XX,' OR SUCURSALES like '%,'|| '"+ SucCod +"' ||',%') " +
                        " and (PERFILES='XX,' OR PERFILES like '%,'|| c.PerCliCod ||',%') " +
                        " and (CIUDADES='XX,' OR CIUDADES like '%,'|| c.CiuCod ||',%') " +
                        " and (CLIENTES='XX,' OR CLIENTES  like '%,'|| c.nitsec ||'-'|| c.clisec || ',%') " +
                        " and (LISTAS='XX,' OR LISTAS  like  '%,'|| c.LisPreCod ||',%') " +
                        " and (EXCLIENTES='XX,' OR EXCLIENTES  not like  '%,'|| c.nitsec ||'-'|| c.clisec || ',%') " + //andres
                        " and (VENDEDORES='XX,' OR VENDEDORES like '%,'||'"+ vUsuario+"'|| ',%') " +
                        " and ((DescLinDesVal<="+vSDTResumenPedidos.Subtotal+" and DescLinHasVal>="+vSDTResumenPedidos.Subtotal+") or DescLinDesVal+DescLinHasVal=0)"  +
                        " and ((((DescLinDesUni<="+TcantGru+")) " +
                        " and ((DescLinHasUni>="+TcantGru+" )) and DescLin1CajMen1<>'S') or DescLinDesUni+DescLinHasUni=0 or DescLin1CajMen1='S' )"  +
                        " and ((((DescLinDesUni<="+TcantGru+" )) " +
                        " and (("+TcantGruCaj+"<1 ) ) and DescLin1CajMen1='S') or DescLinDesUni+DescLinHasUni=0 or DescLin1CajMen1<>'S')"  +
                        " and ((((DescLinDesCaj<="+TcantGruCaj+" )) " +
                        " and ((DescLinHasCaj>="+TcantGruCaj+")))  or DescLinDesCaj+DescLinHasCaj=0)";
                Cursor MovParArt3 = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaDesc, null); //and MOvParEscInd<>'IND'
                //TcantSub=MovParMixCntTotal.getInt(2);
                //TcantSubCaj=MovParMixCntTotal.getInt(3);
                Double DctoArtC1=0.0;
                Double DctoArtC2=0.0;
                Double DctoArtC3=0.0;
                Double DctoArtC4=0.0;
                Double tmDctoArtC1=0.0;
                Double tmDctoArtC2=0.0;
                Double tmDctoArtC3=0.0;
                Double tmDctoArtC4=0.0;
                if (MovParArt3.getCount() > 0) {
                    MovParArt3.moveToFirst();
                    do{
                        DctoArt2 = MovParArt3.getDouble(9);
                        Integer descsec = MovParArt3.getInt(0);
                        String tipodesc = MovParArt3.getString(1);
                        String DescArtDes = MovParArt3.getString(19); //Andres
                        int DesSecLin = MovParArt3.getInt(20); //Andres
                        String filSubgrupos = MovParArt3.getString(11);

                        tmDctoArtC1=0.0;
                        tmDctoArtC2=0.0;
                        tmDctoArtC3=0.0;
                        tmDctoArtC4=0.0;

                        if (tipodesc.equalsIgnoreCase("LIN")){
                            DctoArtC1+=DctoArt2;
                            tmDctoArtC1=DctoArt2;
                        }
                        if (tipodesc.equalsIgnoreCase("PRO")|| (tipodesc.equalsIgnoreCase("ART") && DescArtDes.equalsIgnoreCase("DESPRO"))){ //Andres
                            DctoArtC2+=DctoArt2;
                            tmDctoArtC2=DctoArt2;
                        }
                        if (tipodesc.equalsIgnoreCase("EMP")){
                            DctoArtC3+=DctoArt2;
                            tmDctoArtC3=DctoArt2;
                        }
                        if (tipodesc.equalsIgnoreCase("ART") && !DescArtDes.equalsIgnoreCase("DESPRO")){//Andres
                            DctoArtC4+=DctoArt2;
                            tmDctoArtC4=DctoArt2;
                        }
                        String ConsultaArticulosRela = "select p.ArtSec,p.CliSec,p.prefijo from pedido p left join articulos a on a.artsec=p.artsec where cant+cantcaj<>0 and InvGruCod='"+agInvGruCod+"' and ('"+filSubgrupos+"' like '%,'|| InvSubGruCod ||',%' or '"+filSubgrupos+"'='XX,')   and  nitsec='" + NitSec + "' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay   ;
                        ///* and clisec=" + CliSec  + " */
                        // and prefijo ='" + Prefijo + "'";

                        Cursor CurConsultaArticulosRela = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaArticulosRela, null);
                        String ArtSecADesc="";
                        Integer CliSecDesc=0;
                        String prefijoDesc="";
                        if (CurConsultaArticulosRela.getCount() > 0){
                            CurConsultaArticulosRela.moveToFirst();
                            do{
                                ArtSecADesc=CurConsultaArticulosRela.getString(0);
                                CliSecDesc=CurConsultaArticulosRela.getInt(1);
                                prefijoDesc=CurConsultaArticulosRela.getString(2);
                                BaseDeDatos.getWritableDatabase().execSQL("insert into pedidoDesc(DesSec,dPreArtCod,prefijo,nitsec,clisec,artsec,pdyear,pdmonth,pdday,TipoDesc,DesSecLin,pordescapli1,pordescapli2,pordescapli3,pordescapli4) values("+descsec+",'"+PreArtCod+"',''"+prefijoDesc+"','"+NitSec+"',"+CliSecDesc+",'"+ArtSecADesc+"',"+time.year+","+(time.month + 1)+","+time.monthDay+",'"+tipodesc+"',"+DesSecLin+","+tmDctoArtC1+","+tmDctoArtC2+","+tmDctoArtC3+","+tmDctoArtC4+")");
                            } while (CurConsultaArticulosRela.moveToNext());
                        }

                        //artsec='"+Codigo+"' and  nitsec='" + NitSec + "' and clisec=" + CliSec + "
                        //  pordescapli1 numeric(8,4),pordescapli2 numeric(8,4),pordescapli3 numeric(8,4),pordescapli4 numeric(8,4)
                    } while (MovParArt3.moveToNext());
                }
                //  if (DctoArtC1!=0 || DctoArtC2!=0 || DctoArtC3!=0 || DctoArtC4!=0 ) {
                DctoArtC1=0.0;
                DctoArtC2=0.0;
                DctoArtC3=0.0;
                DctoArtC4=0.0;


                //BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc="+DctoArtC1+" ,pordesc2="+DctoArtC2+",pordesc3="+DctoArtC3+",pordesc4="+DctoArtC4+" where artsec='"+Codigo+"' and  nitsec='" + NitSec + "' and clisec=" + CliSec + " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo ='" + Prefijo + "'");


                //  }
            }catch(Exception e) {
                int pp = 0;
            }

            try {  // Descuentos por Familia






                Double DctoArt1=0.0;
                Double DctoArt2=0.0;

                Integer Tcant = (Unidades.intValue()+(Cajas*Embalaje)) ;
//CliSec
                SDTResumenPedidos vSDTResumenPedidos=TotalesPedido(pContext,Prefijo,NitSec,0,"","","");
                //tipodesc,descgru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,UNIDADNEG,CIUDADES,CLIENTES,VENDEDORES,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal
                String ConsultaDesc="select DESCSEC,tipodesc,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal,DescLinPorDesLin,descagru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,CIUDADES,CLIENTES, " +
                        " ifnull((select " +
                        " total( (cant+(cantcaj*pedartemb)) ) fam" +
                        " from pedido p inner join articulos a on a.artsec=p.artsec where  InvFamCod='"+agInvFamCod+"' and (ARTICULOS like '%,'|| p.artsec ||',%' or ARTICULOS='XX,') and artemb<>0 and rtrim(nitsec) = rtrim('" +NitSec+ "') and clisec=" + CliSec+ " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and cant+cantcaj>0 and prefijo = '"+Prefijo+"'  ),0) fam, " +
                        " ifnull((select " +
                        " total(( ((cant+(cantcaj*pedartemb))*1.0/artemb ) ) ) famcaj " +
                        " from pedido p inner join articulos a on a.artsec=p.artsec where InvFamCod='"+agInvFamCod+"' and  (ARTICULOS like '%,'|| p.artsec ||',%' or ARTICULOS='XX,') and artemb<>0 and rtrim(nitsec) = rtrim('" +NitSec+ "') and clisec=" + CliSec+ " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and cant+cantcaj>0 and prefijo = '"+Prefijo+"'  ),0) famcaj,DescArtDes,DesSecLin " +
                        " from Descuentos d " +
                        " left join articulos a on artsec="+Codigo+"" +
                        " left join Clientes c on nitsec='"+NitSec+"' and clisec="+CliSec+" " +
                        //   " left join (select " +
                        //   " total(cant+(cantcaj*pedartemb)) fam,((cant+(cantcaj*pedartemb))*1.0/artemb ) famcaj " +
                        //    " from pedido p left join articulos a on a.artsec=p.artsec where InvFamCod='"+agInvFamCod+"' (ARTICULOS like '%,'|| p.artsec ||',%' or ARTICULOS='XX,') and artemb<>0 and rtrim(nitsec) = rtrim('" +NitSec+ "') and clisec=" + CliSec+ " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and cant+cantcaj>0 and prefijo = '"+Prefijo+"') cantpedidos " +
                        " where descagru='FAM'" +
                        "  and DescPorMov = '"+ParMovTatTra+"'  and " +
                        " (descgru='XX,' OR descgru like '%,'|| InvGruCod ||',%')  and (SUBGRUPOS='XX,' OR SUBGRUPOS like '%,'|| a.InvSubGruCod ||',%') " +
                        " and (FAMILIAS='XX,' OR FAMILIAS like '%,'|| a.InvFamCod ||',%') " +
                        " and (ARTICULOS='XX,' OR ARTICULOS like '%,'|| a.artsec ||',%') " +
                        " and (CANALES='XX,' OR CANALES like '%,'|| c.CanCod ||',%') " +
                        " and (BODEGAS='XX,' OR BODEGAS like '%,'|| '"+bodcod+"' ||',%') " +
                        " and (LABORATORIO='XX,' OR LABORATORIO like '%,'||  a.LabCod  ||',%') " +
                        " and (CLASE='XX,' OR CLASE like '%,'||  a.invClaCod  ||',%') " +
                        " and (SECCION='XX,' OR SECCION like '%,'|| a.invseccod   ||',%') " +
                        " and (MARCA='XX,' OR MARCA like '%,'||  a.invmarcod ||',%') " +
                        " and (LINEART='XX,' OR LINEART like '%,'||  a.invlincod  ||',%') " +
                        " and (d.CATEGORIA='XX,' OR d.CATEGORIA like '%,'|| a.invcatcod  ||',%') " +
                        " and (SUBCATEGORIA='XX,' OR SUBCATEGORIA like '%,'||  a.invsubcatcod  ||',%') " +
                        " and (SUBCANALES='XX,' OR SUBCANALES like '%,'|| c.CanCod ||'-'|| c.CanSubCod || ',%') " +
                        " and (TAMANOS='XX,' OR TAMANOS like '%,'|| c.CliTamCan || ',%') " +
                        " and (SUCURSALES='XX,' OR SUCURSALES like '%,'|| '"+ SucCod +"' ||',%') " +
                        " and (PERFILES='XX,' OR PERFILES like '%,'|| c.PerCliCod ||',%') " +
                        " and (CIUDADES='XX,' OR CIUDADES like '%,'|| c.CiuCod ||',%') " +
                        " and (CLIENTES='XX,' OR CLIENTES  like '%,'|| c.nitsec ||'-'|| c.clisec || ',%') " +
                        " and (LISTAS='XX,' OR LISTAS  like  '%,'|| c.LisPreCod ||',%') " +
                        " and (EXCLIENTES='XX,' OR EXCLIENTES  not like  '%,'|| c.nitsec ||'-'|| c.clisec || ',%') " + //andres
                        " and (VENDEDORES='XX,' OR VENDEDORES like '%,'||'"+ vUsuario+"'|| ',%') " +
                        " and ((DescLinDesVal<="+vSDTResumenPedidos.Subtotal+" and DescLinHasVal>="+vSDTResumenPedidos.Subtotal+") or DescLinDesVal+DescLinHasVal=0)"  +
                        " and ((((DescLinDesUni<="+TcantFam+")) " +
                        " and ((DescLinHasUni>="+TcantFam+" )) and DescLin1CajMen1<>'S') or DescLinDesUni+DescLinHasUni=0 or DescLin1CajMen1='S' )"  +
                        " and ((((DescLinDesUni<="+TcantFam+" )) " +
                        " and (("+TcantFamCaj+"<1 ) ) and DescLin1CajMen1='S') or DescLinDesUni+DescLinHasUni=0 or DescLin1CajMen1<>'S')"  +
                        " and ((((DescLinDesCaj<="+TcantFamCaj+" )) " +
                        " and ((DescLinHasCaj>="+TcantFamCaj+")))  or DescLinDesCaj+DescLinHasCaj=0)";
                Cursor MovParArt3 = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaDesc, null); //and MOvParEscInd<>'IND'
                //TcantSub=MovParMixCntTotal.getInt(2);
                //TcantSubCaj=MovParMixCntTotal.getInt(3);
                Double DctoArtC1=0.0;
                Double DctoArtC2=0.0;
                Double DctoArtC3=0.0;
                Double DctoArtC4=0.0;
                Double tmDctoArtC1=0.0;
                Double tmDctoArtC2=0.0;
                Double tmDctoArtC3=0.0;
                Double tmDctoArtC4=0.0;
                if (MovParArt3.getCount() > 0) {
                    MovParArt3.moveToFirst();

                    do{
                        DctoArt2 = MovParArt3.getDouble(9);
                        Integer descsec = MovParArt3.getInt(0);
                        String tipodesc = MovParArt3.getString(1);
                        String filArticulos = MovParArt3.getString(13);
                        String DescArtDes = MovParArt3.getString(21); //Andres
                        int DesSecLin = MovParArt3.getInt(22); //Andres
                        tmDctoArtC1=0.0;
                        tmDctoArtC2=0.0;
                        tmDctoArtC3=0.0;
                        tmDctoArtC4=0.0;

                        if (tipodesc.equalsIgnoreCase("LIN")){
                            DctoArtC1+=DctoArt2;
                            tmDctoArtC1=DctoArt2;
                        }
                        if (tipodesc.equalsIgnoreCase("PRO") || (tipodesc.equalsIgnoreCase("ART") && DescArtDes.equalsIgnoreCase("DESPRO"))){ //Andres
                            DctoArtC2+=DctoArt2;
                            tmDctoArtC2=DctoArt2;
                        }
                        if (tipodesc.equalsIgnoreCase("EMP")){
                            DctoArtC3+=DctoArt2;
                            tmDctoArtC3=DctoArt2;
                        }
                        if (tipodesc.equalsIgnoreCase("ART")  && !DescArtDes.equalsIgnoreCase("DESPRO")){//Andres
                            DctoArtC4+=DctoArt2;
                            tmDctoArtC4=DctoArt2;
                        }

                        String ConsultaArticulosRela = "select p.ArtSec,p.CliSec,p.prefijo from pedido p left join articulos a on a.artsec=p.artsec where cant+cantcaj<>0 and InvFamCod='"+agInvFamCod+"'  and ('"+filArticulos+"' like '%,'|| p.artsec ||',%' or '"+filArticulos+"'='XX,')  and  nitsec='" + NitSec + "'  and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay  ;
                        ///* and clisec=" + CliSec  + " */
                        //" and prefijo ='" + Prefijo + "'" */ ;
                        Cursor CurConsultaArticulosRela = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaArticulosRela, null);
                        String ArtSecADesc="";
                        Integer CliSecDesc=0;
                        String prefijoDesc="";
                        if (CurConsultaArticulosRela.getCount() > 0){
                            CurConsultaArticulosRela.moveToFirst();
                            do{
                                ArtSecADesc=CurConsultaArticulosRela.getString(0);
                                CliSecDesc=CurConsultaArticulosRela.getInt(1);
                                prefijoDesc=CurConsultaArticulosRela.getString(2);
                                BaseDeDatos.getWritableDatabase().execSQL("insert into pedidoDesc(DesSec,dPreArtCod,prefijo,nitsec,clisec,artsec,pdyear,pdmonth,pdday,TipoDesc,DesSecLin,pordescapli1,pordescapli2,pordescapli3,pordescapli4) values("+descsec+",'"+PreArtCod+"',''"+prefijoDesc+"','"+NitSec+"',"+CliSecDesc+",'"+ArtSecADesc+"',"+time.year+","+(time.month + 1)+","+time.monthDay+",'"+tipodesc+"',"+DesSecLin+","+tmDctoArtC1+","+tmDctoArtC2+","+tmDctoArtC3+","+tmDctoArtC4+")");
                            } while (CurConsultaArticulosRela.moveToNext());
                        }

                        //artsec='"+Codigo+"' and  nitsec='" + NitSec + "' and clisec=" + CliSec + "
                        //  pordescapli1 numeric(8,4),pordescapli2 numeric(8,4),pordescapli3 numeric(8,4),pordescapli4 numeric(8,4)
                    } while (MovParArt3.moveToNext());
                }
                //  if (DctoArtC1!=0 || DctoArtC2!=0 || DctoArtC3!=0 || DctoArtC4!=0 ) {
                DctoArtC1=0.0;
                DctoArtC2=0.0;
                DctoArtC3=0.0;
                DctoArtC4=0.0;


                //BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc="+DctoArtC1+" ,pordesc2="+DctoArtC2+",pordesc3="+DctoArtC3+",pordesc4="+DctoArtC4+" where artsec='"+Codigo+"' and  nitsec='" + NitSec + "' and clisec=" + CliSec + " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo ='" + Prefijo + "'");


                //  }
            }catch(Exception e) {
                int pp = 0;
            }

            try {  // Descuentos por Subgrupo
                Double DctoArt1=0.0;
                Double DctoArt2=0.0;

                Integer Tcant = (Unidades.intValue()+(Cajas*Embalaje)) ;
//CliSec
                SDTResumenPedidos vSDTResumenPedidos=TotalesPedido(pContext,Prefijo,NitSec,0,"","","");
                //tipodesc,descgru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,UNIDADNEG,CIUDADES,CLIENTES,VENDEDORES,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal
                String ConsultaDesc="select DESCSEC,tipodesc,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal,DescLinPorDesLin,descagru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,CIUDADES,CLIENTES,DescArtDes, DesSecLin from Descuentos d " +
                        " left join articulos a on artsec="+Codigo+"" +
                        " left join Clientes c on nitsec='"+NitSec+"' and clisec="+CliSec+" " +
                        " where descagru='SUB'  and DescPorMov = '"+ParMovTatTra+"'  and " +
                        " (descgru='XX,' OR descgru like '%,'|| InvGruCod ||',%')  and (SUBGRUPOS='XX,' OR SUBGRUPOS like '%,'|| a.InvSubGruCod ||',%') " +
                        " and (FAMILIAS='XX,' OR FAMILIAS like '%,'|| a.InvFamCod ||',%') " +
                        " and (ARTICULOS='XX,' OR ARTICULOS like '%,'|| a.artsec ||',%') " +
                        " and (CANALES='XX,' OR CANALES like '%,'|| c.CanCod ||',%') " +
                        " and (BODEGAS='XX,' OR BODEGAS like '%,'|| '"+bodcod+"' ||',%') " +
                        " and (LABORATORIO='XX,' OR LABORATORIO like '%,'||  a.LabCod  ||',%') " +
                        " and (CLASE='XX,' OR CLASE like '%,'||  a.invClaCod  ||',%') " +
                        " and (SECCION='XX,' OR SECCION like '%,'|| a.invseccod   ||',%') " +
                        " and (MARCA='XX,' OR MARCA like '%,'||  a.invmarcod ||',%') " +
                        " and (LINEART='XX,' OR LINEART like '%,'||  a.invlincod  ||',%') " +
                        " and (d.CATEGORIA='XX,' OR d.CATEGORIA like '%,'|| a.invcatcod  ||',%') " +
                        " and (SUBCATEGORIA='XX,' OR SUBCATEGORIA like '%,'||  a.invsubcatcod  ||',%') " +
                        " and (SUBCANALES='XX,' OR SUBCANALES like '%,'|| c.CanCod ||'-'|| c.CanSubCod || ',%') " +
                        " and (TAMANOS='XX,' OR TAMANOS like '%,'|| c.CliTamCan || ',%') " +
                        " and (SUCURSALES='XX,' OR SUCURSALES like '%,'|| '"+ SucCod +"' ||',%') " +
                        " and (PERFILES='XX,' OR PERFILES like '%,'|| c.PerCliCod ||',%') " +
                        " and (CIUDADES='XX,' OR CIUDADES like '%,'|| c.CiuCod ||',%') " +
                        " and (CLIENTES='XX,' OR CLIENTES  like '%,'|| c.nitsec ||'-'|| c.clisec || ',%') " +
                        " and (LISTAS='XX,' OR LISTAS  like  '%,'|| c.LisPreCod ||',%') " +
                        " and (EXCLIENTES='XX,' OR EXCLIENTES  not like  '%,'|| c.nitsec ||'-'|| c.clisec || ',%') " + //andres
                        " and (VENDEDORES='XX,' OR VENDEDORES like '%,'||'"+ vUsuario+"'|| ',%') " +
                        " and ((DescLinDesVal<="+vSDTResumenPedidos.Subtotal+" and DescLinHasVal>="+vSDTResumenPedidos.Subtotal+") or DescLinDesVal+DescLinHasVal=0)"  +
                        " and ((((DescLinDesUni<="+TcantSub+")) " +
                        " and ((DescLinHasUni>="+TcantSub+" )) and DescLin1CajMen1<>'S') or DescLinDesUni+DescLinHasUni=0 or DescLin1CajMen1='S' )"  +
                        " and ((((DescLinDesUni<="+TcantSub+" )) " +
                        " and (("+TcantSubCaj+"<1 ) ) and DescLin1CajMen1='S') or DescLinDesUni+DescLinHasUni=0 or DescLin1CajMen1<>'S')"  +
                        " and ((((DescLinDesCaj<="+TcantSubCaj+" )) " +
                        " and ((DescLinHasCaj>="+TcantSubCaj+")))  or DescLinDesCaj+DescLinHasCaj=0)";
                Cursor MovParArt3 = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaDesc, null); //and MOvParEscInd<>'IND'
                //TcantSub=MovParMixCntTotal.getInt(2);
                //TcantSubCaj=MovParMixCntTotal.getInt(3);
                Double DctoArtC1=0.0;
                Double DctoArtC2=0.0;
                Double DctoArtC3=0.0;
                Double DctoArtC4=0.0;
                Double tmDctoArtC1=0.0;
                Double tmDctoArtC2=0.0;
                Double tmDctoArtC3=0.0;
                Double tmDctoArtC4=0.0;
                if (MovParArt3.getCount() > 0) {
                    MovParArt3.moveToFirst();
                    do{
                        DctoArt2 = MovParArt3.getDouble(9);
                        Integer descsec = MovParArt3.getInt(0);
                        String tipodesc = MovParArt3.getString(1);
                        String filarticulos = MovParArt3.getString(13);
                        String DescArtDes = MovParArt3.getString(19); //Andres
                        Integer DesSecLin = MovParArt3.getInt(20);//Andres
                        Log.e("descuento:DctoBDAsec",String.valueOf(descsec));
                        tmDctoArtC1=0.0;
                        tmDctoArtC2=0.0;
                        tmDctoArtC3=0.0;
                        tmDctoArtC4=0.0;

                        if (tipodesc.equalsIgnoreCase("LIN")){
                            DctoArtC1+=DctoArt2;
                            tmDctoArtC1=DctoArt2;
                        }
                        if (tipodesc.equalsIgnoreCase("PRO") || (tipodesc.equalsIgnoreCase("ART") && DescArtDes.equalsIgnoreCase("DESPRO"))){ //Andres
                            DctoArtC2+=DctoArt2;
                            tmDctoArtC2=DctoArt2;
                        }
                        if (tipodesc.equalsIgnoreCase("EMP")){
                            DctoArtC3+=DctoArt2;
                            tmDctoArtC3=DctoArt2;
                        }
                        if (tipodesc.equalsIgnoreCase("ART")  && !DescArtDes.equalsIgnoreCase("DESPRO")){//Andres
                            DctoArtC4+=DctoArt2;
                            tmDctoArtC4=DctoArt2;
                        }

                        String ConsultaArticulosRela = "select p.ArtSec,p.CliSec,p.prefijo from pedido p left join articulos a on a.artsec=p.artsec where cant+cantcaj<>0 and ('"+filarticulos+"'='XX,' OR '"+filarticulos+"' like '%,'|| p.artsec ||',%')  and InvSubGruCod='"+agSubGruCod+"'  and  nitsec='" + NitSec + "'    and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay   ;
                        // and clisec=" + CliSec + "   and prefijo ='" + Prefijo + "'";
                        Cursor CurConsultaArticulosRela = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaArticulosRela, null);
                        String ArtSecADesc="";
                        Integer CliSecDesc=0;
                        String prefijoDesc="";
                        if (CurConsultaArticulosRela.getCount() > 0){
                            CurConsultaArticulosRela.moveToFirst();
                            do{
                                ArtSecADesc=CurConsultaArticulosRela.getString(0);
                                CliSecDesc=CurConsultaArticulosRela.getInt(1);
                                prefijoDesc=CurConsultaArticulosRela.getString(2);
                                BaseDeDatos.getWritableDatabase().execSQL("insert into pedidoDesc(DesSec,dPreArtCod,prefijo,nitsec,clisec,artsec,pdyear,pdmonth,pdday,TipoDesc,DesSecLin,pordescapli1,pordescapli2,pordescapli3,pordescapli4) values("+descsec+",'"+PreArtCod+"',''"+prefijoDesc+"','"+NitSec+"',"+CliSecDesc+",'"+ArtSecADesc+"',"+time.year+","+(time.month + 1)+","+time.monthDay+",'"+tipodesc+"',"+DesSecLin+","+tmDctoArtC1+","+tmDctoArtC2+","+tmDctoArtC3+","+tmDctoArtC4+")");
                            } while (CurConsultaArticulosRela.moveToNext());
                        }

                        //artsec='"+Codigo+"' and  nitsec='" + NitSec + "' and clisec=" + CliSec + "
                        //  pordescapli1 numeric(8,4),pordescapli2 numeric(8,4),pordescapli3 numeric(8,4),pordescapli4 numeric(8,4)
                    } while (MovParArt3.moveToNext());
                }
                //  if (DctoArtC1!=0 || DctoArtC2!=0 || DctoArtC3!=0 || DctoArtC4!=0 ) {

                //BaseDeDatos.getWritableDatabase().execSQL("update pedido set pordesc="+DctoArtC1+" ,pordesc2="+DctoArtC2+",pordesc3="+DctoArtC3+",pordesc4="+DctoArtC4+" where artsec='"+Codigo+"' and  nitsec='" + NitSec + "' and clisec=" + CliSec + " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo ='" + Prefijo + "'");

                String TablaPedidosDesc = "CREATE TABLE IF NOT EXISTS pedidoDesc("
                        + "DesSec INTEGER,"
                        + "pordescapli numeric(8,4) )";

                //  }
            }catch(Exception e) {
                int pp = 0;
            }
            Double DctoArtC1=0.0;
            Double DctoArtC2=0.0;
            Double DctoArtC3=0.0;
            Double DctoArtC4=0.0;

            DctoArtC1=0.0;
            DctoArtC2=0.0;
            DctoArtC3=0.0;
            DctoArtC4=0.0;
            Log.e("agInvGruCod: ",String.valueOf(agInvGruCod));

            String ConsultaArticulosRela = "select p.ArtSec,p.CliSec,p.prefijo from pedido p left join articulos a on a.artsec=p.artsec where ifnull(NotaInv,'N') <> 'S' and ifnull(NotaCar,'N')  <> 'S'  and cant+cantcaj<>0 and InvGruCod='"+agInvGruCod+"' and  nitsec='" + NitSec + "' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay ;
            // and clisec=" + CliSec + "
            //   and prefijo ='" + Prefijo + "'"
            Cursor CurConsultaArticulosRela = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaArticulosRela, null);
            String ArtSecADesc="";
            Integer CliSecDesc=0;
            String prefijoDesc="";
            if (CurConsultaArticulosRela.getCount() > 0){
                CurConsultaArticulosRela.moveToFirst();
                do{
                    ArtSecADesc=CurConsultaArticulosRela.getString(0);
                    Log.e("ArtSecADesc: ",String.valueOf(ArtSecADesc));
                    Log.e("prefijoDesc: ",String.valueOf(prefijoDesc));

                    CliSecDesc=CurConsultaArticulosRela.getInt(1);
                    prefijoDesc=CurConsultaArticulosRela.getString(2);
                    String ConsultaArticulosDes=" select " +
                            " total(IFNULL(pordescapli1,0.0)) d1," +
                            " total(IFNULL(pordescapli2,0.0)) d2," +
                            " total(IFNULL(pordescapli3,0.0)) d3," +
                            " total(IFNULL(pordescapli4,0.0)) d4 " +
                            " from pedidoDesc " +
                            " where  dPreArtCod = '"+PreArtCod+"' " +
                            " and artsec='"+ArtSecADesc+"' " +
                            " and nitsec='" + NitSec + "'  " +
                            " and pdyear=" + time.year  +  " " +
                            " and clisec=" + CliSecDesc +
                            " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo ='" + prefijoDesc + "'" ; //
                    Cursor CurConsultaArticulosDesc = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaArticulosDes, null);
                    if (CurConsultaArticulosDesc.getCount() > 0){
                        CurConsultaArticulosDesc.moveToFirst();
                        do{
                          //  Log.e("DesSecsssss: ",String.valueOf(CurConsultaArticulosDesc.getDouble(4)));
                            DctoArtC1=CurConsultaArticulosDesc.getDouble(0);
                            DctoArtC2=CurConsultaArticulosDesc.getDouble(1);
                            DctoArtC3=CurConsultaArticulosDesc.getDouble(2);
                            DctoArtC4=CurConsultaArticulosDesc.getDouble(3);
                            Log.e("DesSecsDctoArtC4: ",String.valueOf(CurConsultaArticulosDesc.getDouble(3)));
                            Log.e("DesSecsDctoArtC2: ",String.valueOf(DctoArtC2));
                        } while (CurConsultaArticulosDesc.moveToNext());
                    }





                        BaseDeDatos.getWritableDatabase().execSQL("update pedido set " +
                                " pordesc=CASE WHEN  "+DctoArtC1+">= pordesc and pordesc<>0 and pordesc<pordescno and "+DctoArtC1+"<>0  THEN pordesc " +
                                " else "+DctoArtC1+" end,pordescno="+DctoArtC1 +
                                " ,pordesc2=CASE WHEN  "+DctoArtC2+">= pordesc2 and pordesc2<>0 and pordesc2<pordesc2no and "+DctoArtC2+"<>0  THEN pordesc2 " +
                                " else "+DctoArtC2+" end,pordesc2no="+DctoArtC2 +
                                " ,pordesc3=CASE WHEN  "+DctoArtC3+">= pordesc3 and pordesc3<>0 and pordesc3<pordesc3no and "+DctoArtC3+"<>0  THEN pordesc3 " +
                                " else "+DctoArtC3+" end,pordesc3no="+DctoArtC3 +
                                " ,pordesc4=CASE WHEN  "+DctoArtC4+">= pordesc4 and pordesc4<>0 and pordesc4<pordesc4no and "+DctoArtC4+"<>0  THEN pordesc4 " +
                                " else "+DctoArtC4+" end,pordesc4no="+DctoArtC4 +
                                " where ifnull(NotaInv,'N') <> 'S' and ifnull(NotaCar,'N')  <> 'S' and " +
                                " PreArtcod = '"+PreArtCod+"' and artsec='"+ArtSecADesc+"' and  " +
                                " nitsec='" + NitSec + "'  and pdyear=" + time.year + " and " +
                                " pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay+ "  and prefijo ='" + prefijoDesc + "' and clisec=" + CliSecDesc  ); // //




                        BaseDeDatos.getWritableDatabase().execSQL("update pedido set " +
                                " pordesc=CASE WHEN  pordescCero=1 then 0 else pordesc end,"+
                                " pordesc2=CASE WHEN  pordesc2Cero=1 then 0 else pordesc2 end,"+
                                " pordesc3=CASE WHEN  pordesc3Cero=1 then 0 else pordesc3 end,"+
                                " pordesc4=CASE WHEN  pordesc4Cero=1 then 0 else pordesc4 end,"+
                                " pordesc5=CASE WHEN  pordesc5Cero=1 then 0 else pordesc5 end"+
                                " where ifnull(NotaInv,'N') <> 'S' and ifnull(NotaCar,'N')  <> 'S' and PreArtcod = '"+PreArtCod+"' and artsec='"+ArtSecADesc+"' and  nitsec='" + NitSec + "'  and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay +" and clisec=" + CliSecDesc + " and prefijo ='" + prefijoDesc + "'"  ); //+ //



                } while (CurConsultaArticulosRela.moveToNext());
            }else{
                BaseDeDatos.getWritableDatabase().execSQL("update pedido set  pordesc=0,pordesc2=0,pordesc3=0,pordesc4=0  where ifnull(NotaInv,'N') <> 'S' and ifnull(NotaCar,'N')  <> 'S' and PreArtcod = '"+PreArtCod+"' and artsec='"+Codigo+"' and  nitsec='" + NitSec + "'  and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay );
                //and clisec=" + CliSec + " + "  and prefijo ='" + Prefijo + "'"
            }

            GlobalVariables gGlobalVariables=null;
            gGlobalVariables = GlobalVariables.getInstance();
            int vAliNegCod=gGlobalVariables.getAliNegCod();
            // BonificadosFiccLimena    -----------------------------------------------------------------------------------------------------------------------------------------


            String Consultalisprecod = "select PedLisPreCod from pedido p where rtrim(nitsec)=rtrim('" + NitSec + "')  " +
                    " and artsec = '"+Codigo+"' " +
                    " and pdyear=" + time.year +
                    " and pdmonth=" + (time.month + 1) +
                    " and pdday=" + time.monthDay +
                    " and cant+cantcaj>0 and prefijo='" + Prefijo + "'";


            Cursor cursorlisprecod = BaseDeDatos.getWritableDatabase().rawQuery(Consultalisprecod, null);
            int lisprecodx = 0;

            if (cursorlisprecod.getCount() > 0) {
                cursorlisprecod.moveToFirst();
                do {
                    lisprecodx += cursorlisprecod.getInt(0);
                } while (cursorlisprecod.moveToNext());

                cursorlisprecod.close();
            }




            //GENERAL
            try {
                Integer Tcant = (Unidades.intValue()+(Cajas*Embalaje)) ;
                Integer VenCantTotCajEmb=0;
                SDTResumenPedidos vSDTResumenPedidos=TotalesPedido(pContext,Prefijo,NitSec,0,"","","");
                //tipodesc,descgru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,UNIDADNEG,CIUDADES,CLIENTES,VENDEDORES,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal
                Cursor MovParBonProdBon = BaseDeDatos.getWritableDatabase().rawQuery("select BonProSec,bontipo,BonProGrupo,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,BonProEscArtSec,BonProEscUniDes,BonProEscUniHas,BonProEscCajDes,BonProEscCajHas,BonProEscUniDes+(BonProEscCajDes*a.artemb) CajConEmb,BonProDesVal,BonProHasVal,BonProEscBonArtSec,BonProEscBonUni,BonProEscBonCaj,BomProMaxMixPeri,BonProMaxCli,BomProMixRefDis,BonProCanOpc,CASE WHEN BonProEscUniDes=BonProEscUniHas and BonProEscCajDes=BonProEscCajHas THEN 1 ELSE 0 END multiplicador,BonProSecLin,c.CanCod,a.artemb from BonificacionesProducto d " +
                        " inner join articulos a on artsec='"+Codigo+"'" +
                        " left join Clientes c on nitsec='"+NitSec+"' and clisec="+CliSec+" " +
                        " where  PARBONAPLCANGEN = 'S' and   (BonProGrupo='XX,' OR BonProGrupo like '%,'|| a.InvGruCod ||',%') " +
                        " and (SUBGRUPOS='XX,' OR SUBGRUPOS like '%,'|| a.InvSubGruCod ||',%') " +
                        " and (FAMILIAS='XX,' OR FAMILIAS like '%,'|| a.InvFamCod ||',%') " +
                        " and (ARTICULOS='XX,' OR ARTICULOS like '%,'|| a.artsec ||',%') " +
                        " and (LABORATORIO='XX,' OR LABORATORIO like '%,'||  a.LabCod  ||',%') " +
                        " and (CLASE='XX,' OR CLASE like '%,'||  a.invClaCod  ||',%') " +
                        " and (SECCION='XX,' OR SECCION like '%,'|| a.invseccod   ||',%') " +
                        " and (MARCA='XX,' OR MARCA like '%,'||  a.invmarcod ||',%') " +
                        " and (LINEART='XX,' OR LINEART like '%,'||  a.invlincod  ||',%') " +
                        " and (d.CATEGORIA='XX,' OR d.CATEGORIA like '%,'|| a.invcatcod  ||',%') " +
                        " and (SUBCATEGORIA='XX,' OR SUBCATEGORIA like '%,'||  a.invsubcatcod  ||',%') " +
                        " and (SUCURSALES='XX,' OR SUCURSALES like '%,'|| '"+ SucCod +"' ||',%') " +
                        " and (CANALES='XX,' OR CANALES like '%,'|| c.CanCod ||',%') " +
                        " and (VENDEDORES='XX,' OR VENDEDORES like '%,'||'"+ vUsuario+"'|| ',%') " +
                        " and (UNIDADES='XX,' OR UNIDADES like '%,'||'"+ vAliNegCod+"'|| ',%') " +
                        " and (SUBCANALES='XX,' OR SUBCANALES like '%,'|| c.CanCod ||'-'|| c.CanSubCod || ',%') " +
                        " and (TAMANOS='XX,' OR TAMANOS like '%,'|| c.CliTamCan || ',%') " +
                        " and bontipo='ESCGRU' "  +
                        "", null); //and MOvParEscInd<>'IND'
                if (MovParBonProdBon.getCount() > 0) {
                    MovParBonProdBon.moveToFirst();
                    Integer vueltas=0;
                    int unidadesfin = 1;
                    do {
                        vueltas+=1;
                        Integer BonProSec=MovParBonProdBon.getInt(0);
                        Integer BonProSecLin=MovParBonProdBon.getInt(25);
                        String bontipo = MovParBonProdBon.getString(1);
                        Log.e("EntroBonificadoFiccc",String.valueOf(BonProSec));




                        String ConRefDis = "SELECT " +
                                "'(' || GROUP_CONCAT(\"'\" || BonProArtSec ||ArtParBonPreArtCod|| \"'\") || ')' AS ListaArticulos, " +
                                "MAX(PARBONCANGEN) AS PARBONCANGEN " +
                                "FROM BonificacionesProductoDet " +
                                "WHERE bonprosec = " + BonProSec;


                        Cursor CurRefDis = BaseDeDatos.getWritableDatabase().rawQuery(ConRefDis, null);
                        String inArticulos = "";
                        int reqUniAplica = 0;
                        int vecesPromo = Integer.MAX_VALUE; // aquí guardaremos el mínimo


                        if (CurRefDis.getCount() > 0){
                            CurRefDis.moveToFirst();

                            do {
                                inArticulos = CurRefDis.getString(0);
                                reqUniAplica   = CurRefDis.getInt(1); // unidades requeridas
                            } while (CurRefDis.moveToNext());

                            CurRefDis.close();
                        }

                        Log.e("inarticulosbon",inArticulos);
                        Log.e("inarticulo",ArtSec+PreArtCod);



                        String ConsultaRefDis = "select (cant+(cantcaj*pedartemb)) as total " +
                                "from pedido p where rtrim(nitsec)=rtrim('" + NitSec + "')  " +
                                "and artsec||p.preartcod in " + inArticulos + " " +
                                "and pdyear=" + time.year +
                                " and pdmonth=" + (time.month + 1) +
                                " and pdday=" + time.monthDay +
                                " and cant+cantcaj>0 and prefijo='" + Prefijo + "'";

                        Cursor CurConsultaRefDis = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaRefDis, null);
                        int compradoTotal = 0;

                        if (CurConsultaRefDis.getCount() > 0) {
                            CurConsultaRefDis.moveToFirst();
                            do {
                                compradoTotal += CurConsultaRefDis.getInt(0);
                            } while (CurConsultaRefDis.moveToNext());

                            CurConsultaRefDis.close();
                        }
                        int veces = compradoTotal / reqUniAplica;


                        String sqlExiste2 = "SELECT  MovParPremCant FROM MovParPrem WHERE " +
                                "MovParNitSec = '" + NitSec + "' " +
                                "AND MovParCliSec = " + CliSec + " " +
                                "AND MovParPremSec = " + BonProSec + " " +
                                "AND MovParPremAno = " + time.year + " " +
                                "AND MovParPremMes = " + (time.month + 1) + " " +
                                "AND MovParPremDia = " + time.monthDay;

                        Cursor c2 = BaseDeDatos.getWritableDatabase().rawQuery(sqlExiste2, null);
                        int alexiste=0;
                        if (c2.moveToFirst()) {
                            do {
                            alexiste += c2.getInt(0);
                            } while (c2.moveToNext());
                        }

                        if (veces<alexiste){
                                 BaseDeDatos.getWritableDatabase().execSQL("Delete from " +
                                "  MovParPrem where  " +
                                " MovParPremSec=" + BonProSec);  //+" and MovParPremApli <> 'S' "
                            alexiste=0;
                        }


                        Log.e("lisprecod bonificado: ",String.valueOf(lisprecodx));
                        String sqlBon = "SELECT DesoBonEscArtBonif, DesoBonEscBonif,DesoBonEscSec, PrePrefijval, ifnull(BonParBonPreArtCod,a.PreArtcod) " +
                                " FROM BonificacionesProductoDetBon b " +
                                " left join articulos a on b.DesoBonEscArtBonif = a.ArtSec " +
                                " left join articulospresentacion ap on  ap.ArtSec=a.artsec and ap.PreArtcod = ifnull(BonParBonPreArtCod,a.PreArtcod) " +
                                " and ap.LisPrecod ="+lisprecodx+"   " +
                                " WHERE DesoBonSec = " + BonProSec+" order by PrePrefijval asc ";

                        Log.e("sqlBon,",sqlBon);
                        Cursor curBon = BaseDeDatos.getWritableDatabase().rawQuery(sqlBon, null);
                        int hh=curBon.getCount();
                        if (veces != alexiste){
                            int faltantesboni=0;
                            if (veces<alexiste){
                                faltantesboni=veces;
                            }else{
                                faltantesboni=veces-alexiste;
                            }
                        if (curBon.moveToFirst()) {
                            double preciomen =0.0;

                            do {


                                double precio = curBon.getDouble(3);
                                preciomen += 1;
                                String MovParPremApli = "N";
                                int BonParBonPreArtCod = curBon.getInt(4);
                                String artBonificado = curBon.getString(0);
                                Log.e("artBonificado,",artBonificado);
                                int DesoBonEscSec = curBon.getInt(2);
                                int cantidadBonificadaPorPromo = curBon.getInt(1);


                                // Aquí agregas el artículo bonificado al pedido
                             //   BaseDeDatos.getWritableDatabase().execSQL("Delete from " +
                             //           "  MovParPrem where  " +
                             //           " MovParPremSec=" + BonProSec+ " and  " +
                             //           " MovParPremSecLin="+DesoBonEscSec+" " +
                             //           " and MovParPremApli <> 'S' ");



                                int totalBonificar = faltantesboni; //cantidadBonificadaPorPromo*veces;




                                if (compradoTotal >= reqUniAplica){


                                //if (totalBonificar > 0){
                                    int embalaje = 1;
                                    int unidades  = totalBonificar;
                                    int canal = 0;



                                    int unidadbon =totalBonificar;
                                    int MovParPremCantGen = 0;
                                    int cajabon = 0;

                                    MovParPremCantGen =totalBonificar;
                                    unidadbon = 0;
                                    int existe =0;

                                    String sqlExiste = "SELECT  MovParPremCant FROM MovParPrem WHERE " +
                                            "MovParNitSec = '" + NitSec + "' " +
                                            "AND MovParCliSec = " + CliSec + " " +
                                            "AND MovParPremSec = " + BonProSec + " " +
                                            "AND MovParPremSecLin = " + DesoBonEscSec + " " +
                                            "AND MovParPremAno = " + time.year + " " +
                                            "AND MovParPremMes = " + (time.month + 1) + " " +
                                            "AND MovParPremDia = " + time.monthDay;

                                    Cursor c = BaseDeDatos.getWritableDatabase().rawQuery(sqlExiste, null);
                                    int tieneregistro=0;
                                    if (c.moveToFirst()) {
                                        do {
                                            existe = +c.getInt(0);
                                        } while (c.moveToNext());

                                        tieneregistro=1;
                                    }

                                    c.close();


                                      int principal = 0;
                                    String sqlExisteapli = "SELECT COUNT(*) FROM MovParPrem WHERE " +
                                            "MovParNitSec = '" + NitSec + "' " +
                                            "AND MovParCliSec = " + CliSec + " " +
                                            "AND MovParPremSec = " + BonProSec + " " +
                                            "AND MovParPremApli = 'S' " +
                                            "AND MovParPremAno = " + time.year + " " +
                                            "AND MovParPremMes = " + (time.month + 1) + " " +
                                            "AND MovParPremDia = " + time.monthDay;

                                    Cursor cx = BaseDeDatos.getWritableDatabase().rawQuery(sqlExisteapli, null);
                                    if (cx.moveToFirst()) {
                                        principal = cx.getInt(0);
                                    }

                                    cx.close();




                                if (faltantesboni==0)
                                {
                                    if(existe > 0) {
                                         unidadbon = existe;
                                    }else{
                                        unidadbon = 0;
                                    }
                                    MovParPremApli = "S";
                                }else{
                                    if(existe > 0) {
                                        MovParPremCantGen =totalBonificar;
                                      //  if(existe > MovParPremCantGen){
                                        //    existe = existe +MovParPremCantGen;
                                       // }else{
                                            unidadbon = existe+totalBonificar; //existe ; //-principal
                                      //  }
                                        faltantesboni=faltantesboni-totalBonificar;
                                        MovParPremApli = "S";
                                    }else{

                                      //  if( principal > 0){
                                            MovParPremCantGen =totalBonificar;
                                            unidadbon = totalBonificar ;
                                            MovParPremApli = "S";
                                        faltantesboni=faltantesboni-totalBonificar;
                                       // }else{
                                       //     if(preciomen == 1){
                                       //         MovParPremCantGen =totalBonificar;
                                       //         unidadbon = totalBonificar ;
                                      //          MovParPremApli = "S";
                                      //      }
                                      //  }

                                    }
                                }





                                    String update = "UPDATE MovParPrem SET " +
                                            " MovParPremCant = " + unidadbon + ", " +
                                            " MovParPremArtSecOri = '"+Codigo+"', " +
                                            " BonParBonPreArtCod = "+BonParBonPreArtCod+", MovParPremCantGen = "+veces+"  " +
                                            "WHERE MovParNitSec = '" + NitSec + "' " +
                                            "AND MovParCliSec = " + CliSec + " " +
                                            "AND MovParPremSec = " + BonProSec + " " +
                                            "AND MovParPremSecLin = " + DesoBonEscSec + " " +
                                            "AND MovParPremAno = " + time.year + " " +
                                            "AND MovParPremMes = " + (time.month + 1) + " " +
                                            "AND MovParPremDia = " + time.monthDay;


                                    String Consulta = "insert into MovParPrem(prefijo,MovParNitSec," +
                                            "MovParCliSec,MovParPremSec,MovParPremSecLin,MovParPremTip," +
                                            "MovParPremArtSec,MovParPremCant,MovParPremCantCaj,MovParPremDesc," +
                                            "MovParPremArtSecOri,MovParPremAno,MovParPremMes,MovParPremDia,MovParPremSecLin," +
                                            "MovParPremtipo,MovParPremcheckmax,MovParPremCantGen, MovParPremApli, BonParBonPreArtCod)" +
                                            "values('" + Prefijo + "','" + NitSec + "'," + CliSec + "," + BonProSec + "," + DesoBonEscSec + ",'NNP'," +
                                            " '" + artBonificado.trim() + "'," + unidadbon+ "," + cajabon + ",0,'" + Codigo + "'," + time.year + "," + (time.month + 1) + "," + time.monthDay + "," +
                                            " " + DesoBonEscSec + ",'" + bontipo + "','N',"+veces+",'"+MovParPremApli+"',"+BonParBonPreArtCod+")";

                                    try {


                                        if (tieneregistro > 0) {
                                            Log.e("Update: ,",update);
                                            BaseDeDatos.getWritableDatabase().execSQL(update);
                                        } else {
                                            Log.e("Insert Consulta: ,",Consulta);
                                            BaseDeDatos.getWritableDatabase().execSQL(Consulta); // tu INSERT
                                        }


                                    } catch (Exception e) {
                                        Log.e("ErrorBon,",e.toString());
                                        int jj = 0;
                                    }

                                //}




                                }else{
                                    BaseDeDatos.getWritableDatabase().execSQL("Delete from " +
                                            "  MovParPrem where  " +
                                            " MovParPremSec=" + BonProSec+ " and  " +
                                            " MovParPremSecLin="+DesoBonEscSec+" " +
                                            " ");
                                }
                                //break;
                            } while (curBon.moveToNext());

                        curBon.close();

                    }
                        }

                    } while (MovParBonProdBon.moveToNext());
                        /*if(bandera == 0) {
                            Toast.makeText(pContext,"limite maximo de la oferta superado",Toast.LENGTH_LONG).show();
                        }*/
                }
            }catch(Exception e) {
                int pp = 0;
            }
            // NO GENERAL
            try {
                Integer Tcant = (Unidades.intValue()+(Cajas*Embalaje)) ;
                Integer VenCantTotCajEmb=0;
                SDTResumenPedidos vSDTResumenPedidos=TotalesPedido(pContext,Prefijo,NitSec,0,"","","");
                //tipodesc,descgru,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,UNIDADNEG,CIUDADES,CLIENTES,VENDEDORES,DescLinDesUni,DescLinHasUni,DescLin1CajMen1,DescLinDesCaj,DescLinHasCaj,DescLinDesVal,DescLinHasVal
                Cursor MovParBonProdBon = BaseDeDatos.getWritableDatabase().rawQuery("select BonProSec,bontipo,BonProGrupo,SUBGRUPOS,FAMILIAS,ARTICULOS,CANALES,SUBCANALES,TAMANOS,BonProEscArtSec,BonProEscUniDes,BonProEscUniHas,BonProEscCajDes,BonProEscCajHas,BonProEscUniDes+(BonProEscCajDes*a.artemb) CajConEmb,BonProDesVal,BonProHasVal,BonProEscBonArtSec,BonProEscBonUni,BonProEscBonCaj,BomProMaxMixPeri,BonProMaxCli,BomProMixRefDis,BonProCanOpc,CASE WHEN BonProEscUniDes=BonProEscUniHas and BonProEscCajDes=BonProEscCajHas THEN 1 ELSE 0 END multiplicador,BonProSecLin,c.CanCod,a.artemb from BonificacionesProducto d " +
                        " inner join articulos a on artsec='"+Codigo+"'" +
                        " left join Clientes c on nitsec='"+NitSec+"' and clisec="+CliSec+" " +
                        " where PARBONAPLCANGEN = 'N' and  (BonProGrupo='XX,' OR BonProGrupo like '%,'|| a.InvGruCod ||',%') " +
                        " and (SUBGRUPOS='XX,' OR SUBGRUPOS like '%,'|| a.InvSubGruCod ||',%') " +
                        " and (FAMILIAS='XX,' OR FAMILIAS like '%,'|| a.InvFamCod ||',%') " +
                        " and (ARTICULOS='XX,' OR ARTICULOS like '%,'|| a.artsec ||',%') " +
                        " and (LABORATORIO='XX,' OR LABORATORIO like '%,'||  a.LabCod  ||',%') " +
                        " and (CLASE='XX,' OR CLASE like '%,'||  a.invClaCod  ||',%') " +
                        " and (SECCION='XX,' OR SECCION like '%,'|| a.invseccod   ||',%') " +
                        " and (MARCA='XX,' OR MARCA like '%,'||  a.invmarcod ||',%') " +
                        " and (LINEART='XX,' OR LINEART like '%,'||  a.invlincod  ||',%') " +
                        " and (d.CATEGORIA='XX,' OR d.CATEGORIA like '%,'|| a.invcatcod  ||',%') " +
                        " and (SUBCATEGORIA='XX,' OR SUBCATEGORIA like '%,'||  a.invsubcatcod  ||',%') " +
                        " and (SUCURSALES='XX,' OR SUCURSALES like '%,'|| '"+ SucCod +"' ||',%') " +
                        " and (CANALES='XX,' OR CANALES like '%,'|| c.CanCod ||',%') " +
                        " and (VENDEDORES='XX,' OR VENDEDORES like '%,'||'"+ vUsuario+"'|| ',%') " +
                        " and (UNIDADES='XX,' OR UNIDADES like '%,'||'"+ vAliNegCod+"'|| ',%') " +
                        " and (SUBCANALES='XX,' OR SUBCANALES like '%,'|| c.CanCod ||'-'|| c.CanSubCod || ',%') " +
                        " and (TAMANOS='XX,' OR TAMANOS like '%,'|| c.CliTamCan || ',%') " +
                        " and bontipo='ESCGRU' "  +
                        "", null); //and MOvParEscInd<>'IND'
                if (MovParBonProdBon.getCount() > 0) {
                    MovParBonProdBon.moveToFirst();
                    Integer vueltas=0;
                    int unidadesfin = 1;
                    do {
                        vueltas+=1;
                        Integer BonProSec=MovParBonProdBon.getInt(0);
                        Integer BonProSecLin=MovParBonProdBon.getInt(25);
                        String bontipo = MovParBonProdBon.getString(1);
                        Log.e("EntroBonificadoFiccc",String.valueOf(BonProSec));




                        String ConRefDis = "select BonProArtSec,BomProDetDesUni,BonProDetDesCaj,(BomProDetDesUni+(BonProDetDesCaj*BonProDetEmb))," +
                                " BonProDetIndOpc, PARBONAPLCANGEN, PARBONCANGEN " +
                                "  from BonificacionesProductoDet where bonprosec="+BonProSec;
                        Cursor CurRefDis = BaseDeDatos.getWritableDatabase().rawQuery(ConRefDis, null);
                        String aplicagen = "N";
                        int vecesPromo = Integer.MAX_VALUE; // aquí guardaremos el mínimo
                        if (CurRefDis.getCount() > 0){
                            CurRefDis.moveToFirst();

                            do {
                                String CurArtSec = CurRefDis.getString(0);

                                int reqUniAplica   = CurRefDis.getInt(6); // unidades requeridas
                                int reqCaj   = CurRefDis.getInt(2); // cajas requeridas
                                int reqTotal = CurRefDis.getInt(3); // total en unidades (uni + cajas * emb)
                                aplicagen = CurRefDis.getString(5);
                                if(aplicagen.equalsIgnoreCase("S")){
                                    reqTotal = reqUniAplica;
                                }


                                int compradoTotal = 0;
                                boolean encontroArticulo = false;


                                String ConsultaRefDis = "select (cant+(cantcaj*pedartemb)) as total " +
                                        "from pedido p where rtrim(nitsec)=rtrim('" + NitSec + "')  " +
                                        "and artsec='" + CurArtSec + "' " +
                                        "and pdyear=" + time.year +
                                        " and pdmonth=" + (time.month + 1) +
                                        " and pdday=" + time.monthDay +
                                        " and cant+cantcaj>0 and prefijo='" + Prefijo + "'";

                                Cursor CurConsultaRefDis = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaRefDis, null);

                                if (CurConsultaRefDis.moveToFirst()) {
                                    compradoTotal = CurConsultaRefDis.getInt(0);
                                    encontroArticulo = true;
                                }

                                Log.e("compradoTotal",String.valueOf(compradoTotal));
                                Log.e("reqTotal",String.valueOf(reqTotal));
                                if (!encontroArticulo) {
                                    vecesPromo = 0; // No cumple la promo
                                }


                                if (reqTotal > 0) {
                                    int vecesArticulo = compradoTotal / reqTotal;

                                    if (vecesArticulo < vecesPromo) {
                                        vecesPromo = vecesArticulo;
                                    }
                                }
                                if (vecesPromo == 0) {
                                    break; // ya no puede haber bonificación
                                }

                                CurConsultaRefDis.close();

                            } while (CurRefDis.moveToNext());

                            CurRefDis.close();
                        }
                        Log.e("lisprecod bonificado: ",String.valueOf(xlisprecod));
                        String sqlBon = "SELECT DesoBonEscArtBonif, DesoBonEscBonif,DesoBonEscSec, PrePrefijval, ifnull(BonParBonPreArtCod,a.PreArtcod) " +
                                " FROM BonificacionesProductoDetBon b " +
                                " left join articulos a on b.DesoBonEscArtBonif = a.ArtSec " +
                                " left join articulospresentacion ap on  ap.ArtSec=a.artsec and ap.PreArtcod = ifnull(BonParBonPreArtCod,a.PreArtcod) " +
                                "and ap.LisPrecod ="+xlisprecod+"   " +
                                " WHERE DesoBonSec = " + BonProSec+" order by PrePrefijval asc ";


                        Cursor curBon = BaseDeDatos.getWritableDatabase().rawQuery(sqlBon, null);

                        if (curBon.moveToFirst()) {
                            double preciomen =0.0;
                            do {

                                double precio = curBon.getDouble(3);
                                preciomen += 1;
                                String MovParPremApli = "N";
                                int BonParBonPreArtCod = curBon.getInt(4);


                                String artBonificado = curBon.getString(0);
                                int DesoBonEscSec = curBon.getInt(2);
                                int cantidadBonificadaPorPromo = curBon.getInt(1);

                                int totalBonificar = cantidadBonificadaPorPromo * vecesPromo;

                                // Aquí agregas el artículo bonificado al pedido

                                BaseDeDatos.getWritableDatabase().execSQL("Delete from MovParPrem " +
                                        " where  MovParPremSec=" + BonProSec+ " and  " +
                                        " MovParPremSecLin="+DesoBonEscSec+" and MovParPremTip='NNP' ");

                                //agregarArticuloBonificado(artBonificado, totalBonificar);

                                Log.e("totalBonificar",String.valueOf(totalBonificar));
                                if (vecesPromo > 0 && totalBonificar > 0){
                                    int embalaje = 1;
                                    int unidades  = totalBonificar;
                                    int canal = 0;



                                    int unidadbon =totalBonificar;
                                    int MovParPremCantGen = 0;
                                    int cajabon = 0;

                                    if(aplicagen.equalsIgnoreCase("S")){
                                        MovParPremCantGen =totalBonificar;
                                        unidadbon = 0;
                                    }else{
                                        MovParPremApli = "N";
                                    }

                                    if(preciomen == 1){
                                        if(aplicagen.equalsIgnoreCase("S")){
                                            MovParPremCantGen =0;
                                            unidadbon = totalBonificar ;
                                            MovParPremApli = "N";
                                        }
                                    }


                                    String Consulta = "insert into MovParPrem(prefijo,MovParNitSec," +
                                            "MovParCliSec,MovParPremSec,MovParPremSecLin,MovParPremTip," +
                                            "MovParPremArtSec,MovParPremCant,MovParPremCantCaj,MovParPremDesc," +
                                            "MovParPremArtSecOri,MovParPremAno,MovParPremMes,MovParPremDia,MovParPremSecLin," +
                                            "MovParPremtipo,MovParPremcheckmax,MovParPremCantGen, MovParPremApli, BonParBonPreArtCod)" +
                                            "values('" + Prefijo + "','" + NitSec + "'," + CliSec + "," + BonProSec + "," + DesoBonEscSec + ",'NNP'," +
                                            " '" + artBonificado.trim() + "'," + unidadbon+ "," + cajabon + ",0,'" + Codigo + "'," + time.year + "," + (time.month + 1) + "," + time.monthDay + "," +
                                            " " + DesoBonEscSec + ",'" + bontipo + "','N',"+MovParPremCantGen+",'N',"+BonParBonPreArtCod+")";
                                    try {
                                        BaseDeDatos.getWritableDatabase().execSQL(Consulta);
                                    } catch (Exception e) {
                                        int jj = 0;
                                    }

                                }



                            } while (curBon.moveToNext());
                        }
                        curBon.close();




                    } while (MovParBonProdBon.moveToNext());
                        /*if(bandera == 0) {
                            Toast.makeText(pContext,"limite maximo de la oferta superado",Toast.LENGTH_LONG).show();
                        }*/
                }
            }catch(Exception e) {
                int pp = 0;
            }


        }


        return true;
    }

    public int validaroferta(Context pContext,String NitSec,Integer bonProSec,Integer BonProSecLin, int unidades,String bontipo,int canal) {
        Log.e("unidadesvaloferta : ",String.valueOf(unidades));
       int unidadrestantes = unidades;
        BaseDatos vBaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 6);

        String updatetScript = "select (maxcan*puntos) as maxoferta , veces from CanalOferta where FacNitSec = '"+NitSec+"' and bonProSec = "+bonProSec+" and BonProLinsec = "+BonProSecLin+" and Tipo = '"+bontipo+"' and canal = "+canal+"  and maxcan > 0";
        Cursor canaloferta = vBaseDeDatos.getWritableDatabase().rawQuery(updatetScript,null);
        if (canaloferta.getCount() > 0){
            canaloferta.moveToFirst();
            do{
                Log.e("bonProSec : ",String.valueOf(bonProSec));
                if(canaloferta.getInt(1)+unidades > canaloferta.getInt(0)){
                    unidadrestantes = canaloferta.getInt(0)-canaloferta.getInt(1);
                    if (unidadrestantes <= 0){
                        unidadrestantes= 0;
                    }
                }else{
                    unidadrestantes = unidades;
                }
            }while (canaloferta.moveToNext());
        }
        Log.e("unidadrestantes : ",String.valueOf(unidadrestantes));
        return  unidadrestantes ;

    }

    public Double precioesp(Context pContext,String NitSec,String Artsec,Double precioArt){
        Double precioNu = 0.0;
       /* BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 5);
        String consulta = "select precioesp from PreciosEspeciales where peNitSec = '"+NitSec+"' and peArtSec = '"+Artsec+"'";
        Cursor cursor = BaseDeDatos.getWritableDatabase().rawQuery(consulta, null);
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            precioNu = cursor.getDouble(0);
        }else{
            precioNu = precioArt;
        }
*/precioNu = precioArt;
        return precioNu;
    }

    public void descuentosAplicado(Context pContext,String NitSec,String Artsec,String Prefijo,String pedido, Integer clisec) throws SQLException {
        ConBd conbd = new ConBd();
        Connection conn = conbd.CargarConexion(pContext);
       if(conn != null){
           try{
               Statement comm = conn.createStatement();

               BaseDatos BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 6);
               SQLiteDatabase BdSql = BaseDeDatos.getReadableDatabase();
               String consultadesc ="select DesSec,DesSecLin,TipoDesc from PedidoDesc where prefijo = '"+Prefijo+"' and nitsec='"+NitSec+"' and clisec = "+clisec+" and artsec = '"+Artsec+"'  ";
               Cursor cursorDesc = BaseDeDatos.getWritableDatabase().rawQuery(consultadesc, null);
               Integer DesSec = 0;
               Integer DesSecLin = 0;
               String TipoDesc = "";
               if(cursorDesc.getCount() > 0){
                   cursorDesc.moveToFirst();
                   do{
                       DesSec = cursorDesc.getInt(0);
                       DesSecLin = cursorDesc.getInt(1);
                       TipoDesc = cursorDesc.getString(2);

                       String Consulta =
                               "IF NOT EXISTS (" +
                                       "   SELECT 1 FROM DescuentosAplicados " +
                                       "   WHERE ApDescSec = " + DesSec +
                                       "   AND ApDescSecLin = " + DesSecLin +
                                       "   AND ApDescArtSec = '" + Artsec + "' " +
                                       "   AND ApDesSecCotNum = '" + pedido + "'" +
                                       ") " +
                                       "BEGIN " +
                                       "   INSERT INTO DescuentosAplicados " +
                                       "   (ApDescSec, ApDescSecLin, ApDescArtSec, ApDescSecTip, ApDesSecCotNum, ApDesTipo) " +
                                       "   VALUES (" +
                                       DesSec + ", " +
                                       DesSecLin + ", '" +
                                       Artsec + "', '" +
                                       TipoDesc + "', '" +
                                       pedido + "', 'Enviado')" +
                                       "END";



                       /*String Consulta = "insert into DescuentosAplicados (ApDescSec,ApDescSecLin,ApDescArtSec,ApDescSecTip,ApDesSecCotNum,ApDesTipo)"+
                               " values ( "+DesSec+","+DesSecLin+",'"+Artsec+"','"+TipoDesc+"','"+pedido+"','Enviado')";*/









                       comm.execute(Consulta);
                   }while (cursorDesc.moveToNext());
               }
           }catch (Exception e){
               Log.e("errordesc",e.toString());
           }
       }

    }


    public void actualizarestado(Context pContext,String nitsec, Integer clisec,String vUsuario,String numpedido,String estado ){
        final Time time = new Time();
        time.setToNow();
        String fecha =  time.year +"-"+  (time.month + 1) +"-"+  time.monthDay;
        BaseDatos vBaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 6);
        SQLiteDatabase BdSql = vBaseDeDatos.getReadableDatabase();
        String updatetScript = "update pedido set pedenviado = '"+estado+"'  where  (prefijo||'"+vUsuario+"'||nitsec||clisec||'"+fecha+"') = '"+numpedido+"' ";
        Log.e("updatetScript: ",updatetScript);
        try {
            if (BdSql.isDbLockedByCurrentThread()) {
                BdSql.endTransaction();
            }
            BdSql.execSQL(updatetScript);

        } catch (Exception ex) {
            Log.e("Exception upd: ",ex.toString());

            ex.printStackTrace();
        }
    }




}
