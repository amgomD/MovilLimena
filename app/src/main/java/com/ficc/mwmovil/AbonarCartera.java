package com.ficc.mwmovil;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ficc.mwmovil.databinding.ActivityAbonarCarteraBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AbonarCartera extends AppCompatActivity {
    SDTAbono[]  SDTAbono;

    Bundle Extras;
    int TotalSel;
    String nitsec="";
    Integer clisec=0;
    boolean cartcom = false;
    ListViewAdapterAbonoCartera ListViewAdapterAbonoCartera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abonar_cartera);
        Extras=this.getIntent().getExtras();
        CheckBox seltodos = findViewById(R.id.seltodos);
        getSupportActionBar().hide();
         nitsec=Extras.getString("nitsec");
        cartcom = Extras.getBoolean("check");
        Button formapago = findViewById(R.id.formapago);
        Button finrecibo = findViewById(R.id.finrecibo);
        Button enviarcorreo = findViewById(R.id.enviarcorreo);
    Log.e("checkcompartidad",String.valueOf(cartcom));
        Button Pagartodo = findViewById(R.id.Pagartodo);
         clisec=Extras.getInt("clisec");
        Time time = new Time();
        time.setToNow();

        BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);

        Integer TotalCartera=0;
        Double Totaltotalrete=0.0;
        Double Totaltotalreteica=0.0;
        Double Totaltotalreteiva=0.0;
        Double Totaltotaldctofin=0.0;
        Double Totaltotaldctoconf=0.0;
        Double Totaltotaldctoprov=0.0;
        Double Totaltotaldctonotos=0.0;
        Integer TotalAbonos=0;

        String tricliica = "";
        String      TriCliRet = "";
        String       TriCliIva = "";
        String      triCliIcaPucSec = "";
        String        TriCliPucSec = "";
        String        TriCliivaPucSec = "";
        Double      retpucVal = 0.0;
        Double        icapucVal = 0.0;
        Double       ivapucVal = 0.0;
        Double      retpucpor = 0.0;
        Double      icapucpor = 0.0;
        Double   ivapucpor = 0.0;

        try{
            Cursor cursorrete = BaseDeDatos.getReadableDatabase().rawQuery("select tricliica,TriCliRet,TriCliIva,triCliIcaPucSec,TriCliPucSec, TriCliivaPucSec, retpucVal,icapucVal, ivapucVal, retpucpor,icapucpor, ivapucpor from clientes where nitsec='"+nitsec+"' and clisec="+clisec+" ", null);
            if (cursorrete.getCount()>0){
                cursorrete.moveToFirst();
                do {
                    tricliica = cursorrete.getString(0);
                    TriCliRet = cursorrete.getString(1);
                    TriCliIva = cursorrete.getString(2);
                    triCliIcaPucSec = cursorrete.getString(3);
                    TriCliPucSec = cursorrete.getString(4);
                    TriCliivaPucSec = cursorrete.getString(5);
                    retpucVal =  cursorrete.getDouble(6);
                    icapucVal = cursorrete.getDouble(7);
                    ivapucVal = cursorrete.getDouble(8);
                    retpucpor = cursorrete.getDouble(9);
                    icapucpor = cursorrete.getDouble(10);
                    ivapucpor =cursorrete.getDouble(11);

                } while (cursorrete.moveToNext());
            }
        }catch (Exception e){
            Integer Error=1;
        }
        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();


        try {
            Cursor cursor = null;
            if(cartcom){
                cursor = BaseDeDatos.getReadableDatabase().rawQuery("select MovFacSec,FacConPag,FacFec,FacVen,FacMora,FacTotalImpuestos,FacAbonos,CASE  When FacSaldo < 0 Then FacSaldo*-1  else FacSaldo end FacSaldo ,KarValTotMendes,(select ifNULL(sum(abono),0) abono from Recibo where nitsec='" + nitsec + "' and clisec=" + clisec + " and facnro=MovFacSec and rcyear=" + time.year + " and rcmonth=" + (time.month + 1) + " and rcday=" + time.monthDay +"),CASE  When FacNroDev = '' Then 'F'  else 'N' end tipo , NoOto,  Conf, ConfProv , financiero, FacNroDev , ConNotNom, FacVenCod from cartera c left join DescuentosFac d on MovFacSec = FacNro  where movnitsec='"+nitsec+"' and movclisec="+clisec+" and movfacsec <> '' order by facmora desc", null);
            }else{
                cursor = BaseDeDatos.getReadableDatabase().rawQuery("select MovFacSec,FacConPag,FacFec,FacVen,FacMora,FacTotalImpuestos,FacAbonos,CASE  When FacSaldo < 0 Then FacSaldo*-1  else FacSaldo end FacSaldo ,KarValTotMendes,(select ifNULL(sum(abono),0) abono from Recibo where nitsec='" + nitsec + "' and clisec=" + clisec + " and facnro=MovFacSec and rcyear=" + time.year + " and rcmonth=" + (time.month + 1) + " and rcday=" + time.monthDay +"),CASE  When FacNroDev = '' Then 'F'  else 'N' end tipo , NoOto,  Conf, ConfProv , financiero, FacNroDev , ConNotNom ,FacVenCod from cartera c left join DescuentosFac d on MovFacSec = FacNro  where movnitsec='"+nitsec+"' and movclisec="+clisec+" and movfacsec <> ''and FacVenCod ='"+vUsuario+"' order by facmora desc", null);
            }


            SDTAbono=new SDTAbono[cursor.getCount()];
            Integer vuelta=0;
            if (cursor.getCount()>0){
                cursor.moveToFirst();
                do {
                    try {
                        double Retencion = 0.0;
                        double ReteIva =0.0;
                        double ReteIca =0.0;
                        double dcto = 0.0;

                        double dctoprov = 0.0;
                        double dctonooto = 0.0;
                        double aprove = 0.0;
                        double dctoconf = 0.0;

                        double Subtotal =  cursor.getDouble(8);
                        Cursor cursorrete = BaseDeDatos.getReadableDatabase().rawQuery("select ifnull(retefue,0) retefue,ifnull(retica,0) retica,ifnull(retiva,0) retiva,ifnull(descuento,0) descuento,ifnull(pagototal,0) pagototal,ifnull(dctoprovpor,0) dctoprovpor,ifnull(dctonootopor,0) dctonootopor,ifnull(aprove,0) aprove,ifnull(dctoconfpor,0) dctoconfpor from recibo where nitsec ='"+nitsec+"' and clisec = "+clisec+" and facnro = '"+cursor.getString(0)+"' ", null);

                        SDTAbono SDTAbonoItem= new SDTAbono();
                        SDTAbonoItem.FacNro = cursor.getString(0);
                        SDTAbonoItem.Subtotal = cursor.getDouble(8);
                        SDTAbonoItem.Saldo = cursor.getDouble(7);
                        SDTAbonoItem.Total = cursor.getDouble(5);
                        SDTAbonoItem.nota = cursor.getString(10);
                        SDTAbonoItem.FacnroDev = cursor.getString(15);
                        SDTAbonoItem.condev = cursor.getString(16);
                        SDTAbonoItem.Vencod = cursor.getString(17);

                        switch(TriCliRet) {
                            case "S":
                                Retencion = Subtotal*(retpucpor);
                                SDTAbonoItem.PorRet = retpucpor;
                                break;
                            case "SB":
                                if(Subtotal > retpucVal){
                                    Retencion = Subtotal*(retpucpor);
                                    SDTAbonoItem.PorRet = retpucpor;
                                }else{
                                    Retencion = 0;
                                    SDTAbonoItem.PorRet = 0.0;
                                }
                                break;
                            default:
                                Retencion = 0;
                                SDTAbonoItem.PorRet = 0.0;
                                break;
                        }
                        switch(TriCliIva) {
                            case "S":
                                ReteIva = Subtotal*(ivapucpor);
                                SDTAbonoItem.PorRetIva = ivapucpor;
                                break;
                            case "SB":
                                if(Subtotal > ivapucVal){
                                    ReteIva = Subtotal*(ivapucpor);
                                    SDTAbonoItem.PorRetIva = ivapucpor;
                                }else{
                                    ReteIva = 0;
                                    SDTAbonoItem.PorRetIva = 0.0;
                                }
                                break;
                            default:
                                ReteIva = 0;
                                SDTAbonoItem.PorRetIva = 0.0;
                                break;
                        }
                        switch(tricliica) {
                            case "S":
                                ReteIca = (Subtotal*(icapucpor))/1000;
                                SDTAbonoItem.PorRetIca= icapucpor;
                                break;
                            case "SB":
                                if(Subtotal > icapucVal){
                                    ReteIca = (Subtotal*(icapucpor))/1000;
                                    SDTAbonoItem.PorRetIca= icapucpor;
                                }else{
                                    ReteIca = 0;
                                    SDTAbonoItem.PorRetIca= 0.0;
                                }
                                break;
                            default:
                                ReteIca = 0;
                                SDTAbonoItem.PorRetIca= 0.0;
                                break;
                        }

                        Double SisNOto = 0.0;
                        Double SisConf = 0.0;
                        Double SisConfProv = 0.0;
                        Double sisFinan = 0.0;

                        SisNOto = cursor.getDouble(11);
                        SisConf = cursor.getDouble(12);
                        SisConfProv = cursor.getDouble(13);
                        sisFinan = cursor.getDouble(14);



                        SDTAbonoItem.Retencion =Retencion;
                        SDTAbonoItem.ReteIva = ReteIva;
                        SDTAbonoItem.ReteIca = ReteIca;

                        SDTAbonoItem.dctonootosis = SisNOto;
                        SDTAbonoItem.dctoprovsis = SisConfProv;
                        SDTAbonoItem.dctoconfsis = SisConf;
                        SDTAbonoItem.Seleccionado = "N";
                        SDTAbonoItem.Dcto = 0.0;
                        SDTAbonoItem.pagototal = "N";

                        if (cursorrete.getCount()>0) {
                            cursorrete.moveToFirst();
                            do {
                                SDTAbonoItem.Retencion =cursorrete.getDouble(0);
                                SDTAbonoItem.ReteIva = cursorrete.getDouble(2);
                                SDTAbonoItem.ReteIca = cursorrete.getDouble(1);
                                SDTAbonoItem.pagototal = cursorrete.getString(4);
                               // SDTAbonoItem.Dcto = cursorrete.getDouble(3);


                                dcto = cursorrete.getDouble(3);
                                dctoprov = cursorrete.getDouble(5);
                                dctonooto = cursorrete.getDouble(6);
                                aprove = cursorrete.getDouble(7);
                                dctoconf = cursorrete.getDouble(8);


                            } while (cursorrete.moveToNext());
                        }


                        SDTAbonoItem.anRetencion =Retencion;
                        SDTAbonoItem.anReteIva = ReteIva;
                        SDTAbonoItem.anReteIca = ReteIca;
                        SDTAbonoItem.dctoprov = dctoprov;
                        SDTAbonoItem.dctonooto = dctonooto;
                        SDTAbonoItem.aprove = aprove;
                        SDTAbonoItem.dctoconf = dctoconf;
                        SDTAbonoItem.Abono = cursor.getDouble(9);

                        Double NetoPago = cursor.getDouble(7)-Retencion-ReteIva-ReteIca-dcto+aprove;

                        Double valdctoprov = dctoprov;
                        Double valdctoconf=  dctoconf;
                        Double valdctonooto = dctonooto;

                        NetoPago = NetoPago-valdctoprov-valdctoconf-valdctonooto;
                        SDTAbonoItem.NetoPago = NetoPago;

                        SDTAbonoItem.valdctoconf  = dctoconf;
                        SDTAbonoItem.valdctoprov  = dctoprov;
                        SDTAbonoItem.valdctonooto  = dctonooto;
                       SDTAbonoItem.Dcto = sisFinan;


                        Totaltotalrete +=Retencion;
                        Totaltotalreteica +=ReteIca;
                        Totaltotalreteiva +=ReteIva;
                        Totaltotaldctofin +=sisFinan;
                        Totaltotaldctoconf +=valdctoconf;
                        Totaltotaldctoprov +=valdctoprov;
                        Totaltotaldctonotos +=valdctonooto;


                        if(  cursor.getString(10).equalsIgnoreCase("N")){
                            TotalAbonos -=cursor.getInt(9);
                            TotalCartera -=cursor.getInt(7);
                        }else{
                            TotalAbonos +=cursor.getInt(9);
                            TotalCartera +=cursor.getInt(7);
                        }



                        SDTAbono[vuelta]=SDTAbonoItem;
                    }catch (Exception e){
                        Log.e("Error lis",e.toString());
                        Integer Error=1;
                    }
                    vuelta=vuelta+1;
                } while (cursor.moveToNext());
            }




            TextView txt_carteraabonada = (TextView) findViewById(R.id.txt_carteraabonada);

            TextView totalrete = (TextView) findViewById(R.id.totalrete);
            TextView totalreteica = (TextView) findViewById(R.id.totalreteica);
            TextView totalreteiva = (TextView) findViewById(R.id.totalreteiva);
            TextView totaldctofin = (TextView) findViewById(R.id.totaldctofin);
            TextView totaldctoconf = (TextView) findViewById(R.id.totaldctoconf);
            TextView totaldctoprov = (TextView) findViewById(R.id.totaldctoprov);
            TextView totaldctonoto = (TextView) findViewById(R.id.totaldctonoto);

            txt_carteraabonada.setText(String.format("%,d",TotalAbonos));
            totalrete.setText(String.format("%,d",Totaltotalrete.intValue()));
            totalreteica.setText(String.format("%,d",Totaltotalreteica.intValue()));
            totalreteiva.setText(String.format("%,d",Totaltotalreteiva.intValue()));
            totaldctofin.setText(String.format("%,d",Totaltotaldctofin.intValue()));
            totaldctoconf.setText(String.format("%,d",Totaltotaldctoconf.intValue()));
            totaldctoprov.setText(String.format("%,d",Totaltotaldctoprov.intValue()));
            totaldctonoto.setText(String.format("%,d",Totaltotaldctonotos.intValue()));



            TextView txt_carteragen = (TextView) findViewById(R.id.txt_carteragen);
            txt_carteragen.setText(String.format("%,d",TotalCartera));

            final ListView list_clientes = (ListView) findViewById(R.id.ListViewAbono);
            ListViewAdapterAbonoCartera = new ListViewAdapterAbonoCartera(this, SDTAbono);
            list_clientes.setAdapter(ListViewAdapterAbonoCartera);

            list_clientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView adapterView, final View view, int i, long l) {

                    Intent intent = new Intent(getApplicationContext(), EditarAbono.class);
                    intent.putExtra("nitsec", Extras.getString("nitsec"));
                    intent.putExtra("clisec", Extras.getInt("clisec"));
                    intent.putExtra("FacNro", SDTAbono[i].FacNro);
                    intent.putExtra("Subtotal", SDTAbono[i].Subtotal);
                    intent.putExtra("Saldo", SDTAbono[i].Saldo);
                    intent.putExtra("Total", SDTAbono[i].Total);
                    intent.putExtra("Retencion", SDTAbono[i].anRetencion);
                    intent.putExtra("ReteIva", SDTAbono[i].anReteIva);
                    intent.putExtra("ReteIca", SDTAbono[i].anReteIca);
                    intent.putExtra("Abono", SDTAbono[i].Abono);
                    intent.putExtra("NetoPago", SDTAbono[i].NetoPago);
                    intent.putExtra("Financiero", SDTAbono[i].Dcto);
                    intent.putExtra("dctoprovsis", SDTAbono[i].dctoprovsis);
                    intent.putExtra("dctonootosis", SDTAbono[i].dctonootosis);
                    intent.putExtra("dctoconfsis", SDTAbono[i].dctoconfsis);
                    intent.putExtra("Tipo", SDTAbono[i].nota);
                    intent.putExtra("porRet", SDTAbono[i].PorRet);
                    intent.putExtra("porRetiva", SDTAbono[i].PorRetIva);
                    intent.putExtra("porRetica", SDTAbono[i].PorRetIca);
                    startActivityForResult(intent, i);


                }
            });

            list_clientes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {


                    if( SDTAbono[i].Seleccionado.equalsIgnoreCase("S")) {
                        SDTAbono[i].Seleccionado = "N";
                        TotalSel -=1;
                    }else {
                        SDTAbono[i].Seleccionado = "S";
                        TotalSel +=1;
                    }
                    Pagartodo.setText("Pagar Todo ("+String.valueOf(TotalSel)+")");
                    ListViewAdapterAbonoCartera.notifyDataSetChanged();
                   return true;
                }
            });



        }catch (Exception e){
            Integer Error=1;
        }

        formapago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SDTAbono.length>0){

                    Intent intent = new Intent(getApplicationContext(), FormaPago.class);
                    intent.putExtra("nitsec", Extras.getString("nitsec"));
                    intent.putExtra("clisec", Extras.getInt("clisec"));

                    Double TotalAbonos = 0.0;
                    for(int i = 0;i<SDTAbono.length;i++){
                        if(SDTAbono[i].nota.equalsIgnoreCase("N")){
                            TotalAbonos -= SDTAbono[i].Abono;
                        }else{
                            TotalAbonos += SDTAbono[i].Abono;
                        }

                    }
                    intent.putExtra("TotalAbonos",TotalAbonos);
                    startActivity(intent);
                }

            }
        });
        Pagartodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BaseDatos BaseDeDatos;
                BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
                Double TotalAbonos = 0.0;
                Double Totaltotalrete=0.0;
                Double Totaltotaldctofin=0.0;
                Double Totaltotalreteica=0.0;
                Double Totaltotalreteiva=0.0;
                for(int i=0 ; i < SDTAbono.length;i+=1){
                    if(SDTAbono[i].Seleccionado.equalsIgnoreCase("S")){

                        String FacNro = SDTAbono[i].FacNro;
                        Double Saldo = SDTAbono[i].Saldo;
                        Double Subtotal = SDTAbono[i].Subtotal;
                        Double NetoPago = SDTAbono[i].NetoPago;
                        Double Retencion = SDTAbono[i].anRetencion;
                        Double ReteIca = SDTAbono[i].anReteIca;
                        Double ReteIva = SDTAbono[i].anReteIva;
                        Double dctofinxt = SDTAbono[i].Dcto;
                        Double valorprov = 0.0;
                        Double valornoto = 0.0;
                        Double Aprovetxt = 0.0;
                        Double valorconf = 0.0;
                        Double dctoprovtxt = 0.0;
                        Double DctoNoOtotxt = 0.0;
                        Double dctoconftxt = 0.0;
                        String Tipo =SDTAbono[i].nota;
                         NetoPago = NetoPago-dctofinxt;
                        BaseDeDatos.getWritableDatabase().execSQL("delete from Recibo where nitsec ='"+nitsec+"' and clisec = "+clisec+" and facnro = '"+FacNro+"' " );

                        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                        String vUsuario=vGlobalVariables.getUsuario();

                        String consulta = "Insert into Recibo (VenCod,nitsec,clisec,facnro,saldo,abono,retefue,retica,retiva,descuento,rcyear,rcmonth,rcday,dctoprov,dctonooto,aprove,dctoconf,pagototal,dctoprovpor,dctonootopor,dctoconfpor,tipo)" +
                                " values (  '"+vUsuario+"','"+nitsec+"',"+clisec+",'"+FacNro+"',"+Saldo+","+NetoPago+","+Retencion+","+ReteIca+","+ReteIva+","+dctofinxt+","+ time.year+","+(time.month + 1)+","+time.monthDay+", " +
                                " "+valorprov+","+valornoto+","+Aprovetxt+","+valorconf+",'S',"+dctoprovtxt+","+DctoNoOtotxt+","+dctoconftxt+",'"+Tipo+"' )";
                        BaseDeDatos.getWritableDatabase().execSQL(consulta);

                        SDTAbono[i].pagototal  ="S";
                        SDTAbono[i].Abono =NetoPago;
                        SDTAbono[i].Seleccionado ="N";


                        if(SDTAbono[i].nota.equalsIgnoreCase("N")){
                            TotalAbonos -= NetoPago;
                        }else{
                            TotalAbonos += NetoPago;
                        }
                        Totaltotalrete +=SDTAbono[i].Retencion;
                        Totaltotalreteica += SDTAbono[i].ReteIca;
                        Totaltotalreteiva += SDTAbono[i].ReteIva;
                        Totaltotaldctofin+= SDTAbono[i].Dcto;

                    }
                }



                seltodos.setChecked(false);
                TextView txt_carteraabonada = (TextView) findViewById(R.id.txt_carteraabonada);
                TextView totalrete = (TextView) findViewById(R.id.totalrete);
                TextView totalreteica = (TextView) findViewById(R.id.totalreteica);
                TextView totalreteiva = (TextView) findViewById(R.id.totalreteiva);
                TextView totaldctofin = (TextView) findViewById(R.id.totaldctofin);
                totaldctofin.setText(String.format("%,d",Totaltotaldctofin.intValue()));
                txt_carteraabonada.setText(String.format("%,d",TotalAbonos.intValue()));
                totalrete.setText(String.format("%,d",Totaltotalrete.intValue()));
                totalreteica.setText(String.format("%,d",Totaltotalreteica.intValue()));
                TotalSel = 0;
                Pagartodo.setText("Pagar Todo ("+TotalSel+")");
                totalreteiva.setText(String.format("%,d",Totaltotalreteiva.intValue()));
                ListViewAdapterAbonoCartera.notifyDataSetChanged();
            }
        });
        seltodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TotalSel = 0;
                if(seltodos.isChecked()){
                    for(int i=0 ; i < SDTAbono.length;i+=1){
                       SDTAbono[i].Seleccionado = "S";
                        TotalSel+=1;
                    }

                }else{
                    for(int i=0 ; i < SDTAbono.length;i+=1){
                        SDTAbono[i].Seleccionado = "N";
                        TotalSel = 0;
                    }
                }
                Pagartodo.setText("Pagar Todo ("+TotalSel+")");
                ListViewAdapterAbonoCartera.notifyDataSetChanged();
            }
        });


        finrecibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView txt_carteraabonada = (TextView) findViewById(R.id.txt_carteraabonada);
                String scartera = txt_carteraabonada.getText().toString();
               // scartera = scartera.replace(",","");

                int cartera = Integer.valueOf(scartera.replace(",",""));
                int forma = contarformapago();
                if(cartera <= forma){
                    Intent intent = new Intent(getApplicationContext(), ConfirmarRecibo.class);
                    intent.putExtra("nitsec", Extras.getString("nitsec"));
                    intent.putExtra("clisec", Extras.getInt("clisec"));
                    intent.putExtra("modo",1);
                    startActivityForResult(intent, 500);
                }else{
                    AlertDialog.Builder Alerta = new AlertDialog.Builder(AbonarCartera.this);
                    Alerta.setMessage("Debe ingresar las formas de pago, valor ingresado: "+String.format("%,d",forma)+ " Valor a pagar: "+scartera);
                    Alerta.setTitle("Notificacion");
                    Alerta.setPositiveButton("OK",null);
                    Alerta.setCancelable(true);
                    Alerta.create().show();
                }

            }
        });



        enviarcorreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ConfirmarRecibo.class);
                intent.putExtra("nitsec", Extras.getString("nitsec"));
                intent.putExtra("clisec", Extras.getInt("clisec"));
                intent.putExtra("modo",2);
                startActivityForResult(intent, 500);
            }
        });


    }



    public int contarformapago(){
        int resta =0;
        Time time = new Time();
        time.setToNow();
        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(this, "MantisMovil", null, 5);
        Cursor cursor =  BaseDeDatos.getWritableDatabase().rawQuery("select sum(Valor) from reciboforma  " +
                " where NitSec = '"+nitsec+"' and CliSec = "+clisec+" and recicyear=" + time.year + " and recicmonth=" + (time.month + 1) + " and recicday=" + time.monthDay + " ", null);

        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            do{
                resta = cursor.getInt(0);
                Log.e("valor:   d",String.valueOf( cursor.getInt(0)));
            }while (cursor.moveToNext());
        }

        return resta;
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==300){



        }else
        {
            if(requestCode == 500){
                if (resultCode == RESULT_OK) {
                String Mensaje = data.getStringExtra("Mensaje");

                if(Mensaje.length() > 0){

                    AlertDialog.Builder Alerta = new AlertDialog.Builder(AbonarCartera.this);
                    Alerta.setMessage("Respuesta: \n "+Mensaje);
                    Alerta.setTitle("Notificacion");
                    Alerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    Alerta.setCancelable(true);
                    Alerta.create().show();

                }
                }



            }else{
                if (resultCode == RESULT_OK) {
                    Double abono = data.getDoubleExtra("abono", 0.0);
                    Double Dcto = data.getDoubleExtra("Dcto", 0.0);
                    Double dctoprov = data.getDoubleExtra("dctoprov", 0.0);
                    Double dctonooto = data.getDoubleExtra("dctonooto", 0.0);
                    Double aprove = data.getDoubleExtra("aprove", 0.0);
                    Double dctoconf = data.getDoubleExtra("dctoconf", 0.0);

                    Double Retencion = data.getDoubleExtra("Retencion", 0.0);
                    Double ReteIca = data.getDoubleExtra("ReteIca", 0.0);
                    Double ReteIva = data.getDoubleExtra("ReteIva", 0.0);
                    String pagototal = data.getStringExtra("pagototal");


                    SDTAbono[requestCode].ReteIca = ReteIca;
                    SDTAbono[requestCode].Abono = abono;
                    SDTAbono[requestCode].Retencion = Retencion;
                    SDTAbono[requestCode].Dcto = Dcto;
                    SDTAbono[requestCode].ReteIva = ReteIva;
                    SDTAbono[requestCode].pagototal = pagototal;
                    SDTAbono[requestCode].dctoprov = dctoprov;
                    SDTAbono[requestCode].dctoconf = dctoconf;
                    SDTAbono[requestCode].dctonooto = dctonooto;
                    SDTAbono[requestCode].aprove = aprove;
                    Double NetoPago =SDTAbono[requestCode].Saldo-ReteIva-ReteIca-Retencion-Dcto+aprove;

                    Double valdctoprov = dctoprov ;  //(NetoPago*(dctoprov/100));
                    Double valdctoconf= dctoconf ; //(NetoPago*(dctoconf/100));
                    Double valdctonooto = dctonooto ; //(NetoPago*(dctonooto/100));

                    NetoPago = NetoPago-valdctoprov-valdctoconf-valdctonooto;
                    SDTAbono[requestCode].NetoPago = NetoPago;

                    SDTAbono[requestCode].valdctoconf  = valdctoconf;
                    SDTAbono[requestCode].valdctoprov  = valdctoprov;
                    SDTAbono[requestCode].valdctonooto  = valdctonooto;


                    Double TotalAbonos = 0.0;
                    Double Totaltotalrete=0.0;
                    Double Totaltotalreteica=0.0;
                    Double Totaltotalreteiva=0.0;
                    Double Totaltotaldctofin=0.0;
                    Double Totaltotaldctoconf=0.0;
                    Double Totaltotaldctoprov=0.0;
                    Double Totaltotaldctonotos=0.0;

                    for(int i = 0;i<SDTAbono.length;i++){
                        if(SDTAbono[i].nota.equalsIgnoreCase("N")){
                            TotalAbonos -= SDTAbono[i].Abono;
                        }else{
                            TotalAbonos += SDTAbono[i].Abono;
                        }
                        Totaltotalrete +=SDTAbono[i].Retencion;
                        Totaltotalreteica += SDTAbono[i].ReteIca;
                        Totaltotalreteiva += SDTAbono[i].ReteIva;
                        Totaltotaldctofin +=SDTAbono[i].Dcto;
                        Totaltotaldctoconf +=SDTAbono[i].valdctoconf;
                        Totaltotaldctoprov +=SDTAbono[i].valdctoprov;
                        Totaltotaldctonotos +=SDTAbono[i].valdctonooto;
                    }

                    TextView txt_carteraabonada = (TextView) findViewById(R.id.txt_carteraabonada);
                    TextView totalrete = (TextView) findViewById(R.id.totalrete);
                    TextView totalreteica = (TextView) findViewById(R.id.totalreteica);
                    TextView totalreteiva = (TextView) findViewById(R.id.totalreteiva);
                    TextView totaldctofin = (TextView) findViewById(R.id.totaldctofin);
                    TextView totaldctoconf = (TextView) findViewById(R.id.totaldctoconf);
                    TextView totaldctoprov = (TextView) findViewById(R.id.totaldctoprov);
                    TextView totaldctonoto = (TextView) findViewById(R.id.totaldctonoto);

                    txt_carteraabonada.setText(String.format("%,d",TotalAbonos.intValue()));


                    totalrete.setText(String.format("%,d",Totaltotalrete.intValue()));
                    totalreteica.setText(String.format("%,d",Totaltotalreteica.intValue()));
                    totalreteiva.setText(String.format("%,d",Totaltotalreteiva.intValue()));
                    totaldctofin.setText(String.format("%,d",Totaltotaldctofin.intValue()));
                    totaldctoconf.setText(String.format("%,d",Totaltotaldctoconf.intValue()));
                    totaldctoprov.setText(String.format("%,d",Totaltotaldctoprov.intValue()));
                    totaldctonoto.setText(String.format("%,d",Totaltotaldctonotos.intValue()));

                    ListViewAdapterAbonoCartera.notifyDataSetChanged();
                }else{
                    if(resultCode == 166){
                        overridePendingTransition(0, 0);
                        getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                    }

                }
            }

        }
    }


}