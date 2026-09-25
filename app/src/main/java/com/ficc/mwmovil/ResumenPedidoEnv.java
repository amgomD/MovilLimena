package com.ficc.mwmovil;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.security.cert.Extension;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ResumenPedidoEnv extends AppCompatActivity  {

    Bundle Extras;
    SDTProductos[] SDTProductos ;
    ListViewAdapterProductosEnv ListViewAdapterProductos;
    Integer UltimaPosicion=0;
    String nitsec;
    Integer clisec;
    String prefijo;
    String invfamcod;
    TextView txt_subtotal;
    TextView txt_iva;
    TextView txt_impoconsumo;
    TextView txt_total,numarticulos;
    ListView listview_productos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_pedidoenv);
        getSupportActionBar().hide();
        Extras=this.getIntent().getExtras();
        nitsec=Extras.getString("nitsec");
        invfamcod=Extras.getString("invfamcod");
        String invsubgrucod=Extras.getString("invsubgrucod");
        String invgrucod=Extras.getString("invgrucod");
        clisec=Extras.getInt("clisec");
        String Numpedido = Extras.getString("Numped");
        Integer lisprecod=0; //Extras.getInt("lisprecod");
        prefijo=Extras.getString("prefijo");

        int dayOfMonth =  AppGlobals.dayOfMonth; // Extras.getInt("dia");
        int month = AppGlobals.month;//Extras.getInt("mes");
        int year = AppGlobals.year;//Extras.getInt("ano");


        final Time time = new Time();
        time.setToNow();
        time.set(time.second, time.minute, time.hour,dayOfMonth , month, year);



        ConBd conbd = new ConBd();
        Connection conn = conbd.CargarConexion(getApplicationContext());
        Statement comm = null;

        try {
            comm = conn.createStatement();
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }

        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);

        try{
            Cursor Clientes = BaseDeDatos.getWritableDatabase().rawQuery("select Lisprecod from Clientes where nitsec='"+nitsec+"' and clisec="+clisec, null); //order by nombre

            if (Clientes.getCount()>0){
                int vuelta=0;
                Clientes.moveToFirst();
                do {
                    lisprecod=Clientes.getInt(0);
                    vuelta=vuelta+1;
                } while (Clientes.moveToNext());
            }
        }catch (Exception e)
        {
            int h=0;
        }


        Cursor cursorNCND = BaseDeDatos.getWritableDatabase().rawQuery("select ConNotCod,ConNotNom from ConceptoNCND ", null); //order by nombre

        final String[] CausalNombre;
        CausalNombre = new String[cursorNCND.getCount()];
        if (cursorNCND.getCount()>0){
            int vuelta=0;
            cursorNCND.moveToFirst();
            do {
                CausalNombre[vuelta]=cursorNCND.getString(1);
                vuelta=vuelta+1;
            } while (cursorNCND.moveToNext());
        }

        String WhereConsulta="";
        if(!invfamcod.isEmpty()){
            WhereConsulta+=" where invfamcod='"+invfamcod+"' ";
        }
      /*  if(!invsubgrucod.isEmpty()){
            WhereConsulta+=" and invsubgrucod='"+invsubgrucod+"' ";
        }
        if(!invgrucod.isEmpty()){
            WhereConsulta+=" and invgrucod='"+invgrucod+"' ";
        }*/

        String Empresa=vGlobalVariables.getEmpresa();
        Empresa=Empresa.toUpperCase();

        String Exist;
        String Precio;
        if (prefijo.equalsIgnoreCase("NC") ){
            Exist="KarUni";
            Precio="KarPrePub";
            if (WhereConsulta.isEmpty()) {
                WhereConsulta += " where KarUni>0 ";
            }else{
                WhereConsulta += " and KarUni>0 ";
            }
        }else{
            Exist="Exist";
            Precio="Precio"+lisprecod;
        }
        try {


            String Consulta = "select * from(select a.ArtSec,ArtCod,ArtNom, precio,Desc1,Desc2," + Exist + " Exist,artemb," +
                    " case  cliiva when 'S' then ParConIva else 0 end ParConIva,ArtValImp,cant,InvGruNom,artmednomcom,0 KarUni,0 KarPrePub,cantcaj," +
                    " ifNULL(pordesc,Desc1) pordesc,ifNULL(pordesc2,Desc2) pordesc2,ifNULL(pordesc3,0.0) pordesc3,ifNULL(pordesc4,0.0) pordesc4,ifNULL(pordesc5,0.0) pordesc5," +
                    "ifNULL(pordesc6,0.0) pordesc6,confemp,confprov,confvend,p.bodcod,ifNULL(p.plazo,0) plazo,ifNULL(p.ConPagnom,'') plazonom,0 MovParPremSec," +
                    " cantinf, ifnull(NotaInv,'N') NotaInv , ifnull(NotaCar,'N') NotaCar,ap.PreArtNom from Articulos a " +
                   // "left join ClientesDevoluciones d on d.nitsec='" + nitsec + "' and d.clisec='" + clisec + "' and d.artsec=a.artsec " +
                    " left join Clientes cc on cc.nitsec='" + nitsec + "' and cc.clisec='" + clisec + "'  " +
                    "left join pedido p on prefijo='" + prefijo + "' and p.nitsec='" + nitsec + "' and p.clisec=" + clisec + " and p.artsec=a.artsec" +
                    " left join articulospresentacion ap on  ap.ArtSec=a.artsec and ap.PreArtcod = ifnull(p.PreArtCod,a.PreArtcod) and ap.LisPrecod =" + lisprecod + " " +
                    "  where (cant+ifnull(cantinf,0)) <>0 and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " " +
                    " union "+
                    "select m.MovParPremArtSec,ArtCod,ArtNom,0 precio,0.0,0.0,0 Exist, a.artemb,0 ParConIva,0 ArtValImp,MovParPremCant cant,'' InvGruNom,'' artmednomcom,0 KarUni,0 KarPrePub,ifNULL(MovParPremCantCaj,0) cantcaj,0.0 pordesc,0.0 pordesc2,0.0 pordesc3,0.0 pordesc4,0.0 pordesc5,0.0 pordesc6,0.0 confemp,0.0 confprov,0.0 confvend,0 bodcod,0 plazo,'' plazonom,MovParPremSec," +
                    " 0 cantinf , 'N' NotaInv, 'N' NotaCar,  PreArtNom from MovParPrem M " +
                    "left join articulos a on a.artsec=m.MovParPremArtSec " +
                    " left join articulospresentacion ap" +
                    " on  ap.ArtSec=MovParPremArtSec and ap.PreArtcod = ifnull(M.BonParBonPreArtCod,a.PreArtcod) " +
                    "and ap.LisPrecod =" + lisprecod + " " +
                    " where MovParPremCant > 0 and  Prefijo='"+prefijo+"' and MovParNitSec='"+nitsec+"' and MovParCliSec="+clisec+" and MovParPremAno=" + time.year + " and MovParPremMes=" + (time.month + 1) + " and MovParPremDia=" + time.monthDay+") jj order by artcod,precio desc,cantcaj,cant ";
            Cursor cursor = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null); //order by nombre

            int vuelta = 0;
            SDTProductos = new SDTProductos[cursor.getCount()]; //+cursor2.getCount()
            if (cursor.getCount() > 0) {

                cursor.moveToFirst();
                do {
                    SDTProductos SDTProductosItem = new SDTProductos();
                    SDTProductosItem.ArtSec = cursor.getString(0);
                    SDTProductosItem.Codigo = cursor.getString(1);
                    SDTProductosItem.Nombre = cursor.getString(2) + " (" + cursor.getString(11) + ")";
                    SDTProductosItem.Nombrecomercial = cursor.getString(12);
                    SDTProductosItem.Precio = cursor.getDouble(3);
                    SDTProductosItem.PrecioIva = cursor.getDouble(3) * (1 + (Double.valueOf(cursor.getInt(8)) / 100));
                    SDTProductosItem.PrecioNeto = ((cursor.getDouble(3) * (1 + (Double.valueOf(cursor.getInt(8)) / 100)) )*(1-(cursor.getDouble(16)/100)) *(1-(cursor.getDouble(17)/100)) *(1-(cursor.getDouble(18)/100)) *(1-(cursor.getDouble(19)/100)) *(1-(cursor.getDouble(20)/100)) *(1-(cursor.getDouble(21)/100))   )+cursor.getDouble(9) ;
                    SDTProductosItem.ConfEmp = cursor.getDouble(22);
                    SDTProductosItem.ConfProv = cursor.getDouble(23);
                    SDTProductosItem.ConfVend = cursor.getDouble(24);
                    SDTProductosItem.bodcod = cursor.getInt(25);
                    SDTProductosItem.pNitSec = nitsec;
                    SDTProductosItem.pCliSec = clisec;
                    SDTProductosItem.Existencia = cursor.getDouble(6);
                    SDTProductosItem.Embalaje = cursor.getInt(7);
                    SDTProductosItem.Cajas = cursor.getInt(15); // cursor.getInt(0);
                    SDTProductosItem.Unidades = cursor.getDouble(10); //cursor.getDouble(0);
                    SDTProductosItem.Iva = cursor.getInt(8);
                    SDTProductosItem.Impoconsumo = cursor.getDouble(9);
                    SDTProductosItem.Presentacion = cursor.getString(32);
                    SDTProductosItem.Dct1 = cursor.getDouble(16);
                    SDTProductosItem.Dct2 = cursor.getDouble(17);
                    SDTProductosItem.Dct3 = cursor.getDouble(18);
                    SDTProductosItem.Dct4 = cursor.getDouble(19);
                    SDTProductosItem.Dct5 = cursor.getDouble(20);
                    SDTProductosItem.Dct6 = cursor.getDouble(21);
                    SDTProductosItem.Dct7 = 0.0; // cursor.getDouble(0);
                    SDTProductosItem.Dct8 = 0.0; // cursor.getDouble(0);
                    SDTProductosItem.Dct1no = cursor.getDouble(4);
                    SDTProductosItem.Dct2no = cursor.getDouble(5);
                    SDTProductosItem.Dct3no = 0.0; // cursor.getDouble(0);
                    SDTProductosItem.Dct4no = 0.0; // cursor.getDouble(0);
                    SDTProductosItem.Dct5no = 0.0; // cursor.getDouble(0);
                    SDTProductosItem.Dct6no = 0.0; // cursor.getDouble(0);
                    SDTProductosItem.Dct7no = 0.0; // cursor.getDouble(0);
                    SDTProductosItem.Dct8no = 0.0; // cursor.getDouble(0);
                    SDTProductosItem.Prefijo = prefijo;
                    SDTProductosItem.LisPreCod = Extras.getInt("lisprecod");
                    SDTProductosItem.Plazo = cursor.getInt(26);
                    SDTProductosItem.plazoNom = cursor.getString(27);
                    SDTProductosItem.NitSec = nitsec;
                    SDTProductosItem.CliSec = clisec;
                    SDTProductosItem.Unidadesinf = cursor.getDouble(29);
                    SDTProductosItem.NotaCar = cursor.getString(31);
                    SDTProductosItem.NotaInv = cursor.getString(30);
                    String NumPed = prefijo+nitsec +  clisec +  time.year +"-"+  (time.month + 1) +"-"+  time.monthDay  ;
                    String sExistencia="0.00";


                    try {
                        try {
                            ResultSet rsImport;
                            if (cursor.getInt(3)==1){
                                rsImport= comm.executeQuery("select cast(isnull(sum(((((CotArtCaj*CotArtEmb)+CotArtUni)*(isnull(CotArtValImp,0)+CotArtPrecio))*(1-(CotArtDesUno/100))*(1-(CotArtDesDos/100))*(1-(CotArtDesTre/100))*(1-(CotArtDesCua/100)))*(1+(CotPorIva/100))),0) as numeric(18,2)) val from CotizacionesDetalle1 cd left join  Cotizaciones1 c on c.CotSec=cd.CotSec \n" +
                                        "where cotnum='"+Numpedido+"' and cotsubvencod='"+vUsuario.trim()+"' and artsec='"+cursor.getString(0)+"' and CotArtPrecio=1");
                            }else{
                                rsImport = comm.executeQuery("select cast(isnull(sum(((((CotArtCaj*CotArtEmb)+CotArtUni)*(isnull(CotArtValImp,0)+CotArtPrecio))*(1-(CotArtDesUno/100))*(1-(CotArtDesDos/100))*(1-(CotArtDesTre/100))*(1-(CotArtDesCua/100)))*(1+(CotPorIva/100))),0) as numeric(18,2)) val from CotizacionesDetalle1 cd left join  Cotizaciones1 c on c.CotSec=cd.CotSec \n" +
                                        "where cotnum='"+Numpedido+"' and cotsubvencod='"+vUsuario.trim()+"' and artsec='"+cursor.getString(0)+"' and CotArtPrecio<>1");
                            }
                            while (rsImport.next()) {
                                sExistencia = rsImport.getString("val").trim();
                            }

                            if (conbd.ActulizaOnline=="S" && !Empresa.trim().equalsIgnoreCase("FARMA")) {
                                ResultSet rsImport2;
                                if (cursor.getInt(3)==1){
                                    rsImport2 = comm.executeQuery("select cast(ISNULL(SUM(karvaltotmendes+karartiva),0) as numeric(18,2)) val from kardex cd left join  factura c on c.facsec=cd.facsec \n" +
                                            "where facnro='" + NumPed + "' and facvencod='"+vUsuario.trim()+"' and artsec='"+cursor.getString(0)+"' and karprepub=1");
                                }else{
                                    rsImport2 = comm.executeQuery("select cast(ISNULL(SUM(karvaltotmendes+karartiva),0) as numeric(18,2)) val from kardex cd left join  factura c on c.facsec=cd.facsec \n" +
                                            "where facnro='" + NumPed + "' and facvencod='"+vUsuario.trim()+"' and artsec='"+cursor.getString(0)+"' and karprepub<>1");
                                }
                                while (rsImport2.next()) {
                                    sExistencia = rsImport2.getString("val").trim();
                                }
                            }
                        }catch (Exception e){
                            int hh=0;
                            Log.e("Error1",e.toString());
                        }



                    }catch (Exception e){
                        int hh=0;
                        Log.e("Error1",e.toString());

                    }


                    SDTProductosItem.ValorPedidoEnviado=Double.valueOf(sExistencia);
                    SDTProductosItem.ValorEnviado=sExistencia;
                    SDTProductosItem.pContext = getApplicationContext();
                    SDTProductosItem.Calcular();
                    SDTProductos[vuelta] = SDTProductosItem;
                    vuelta = vuelta + 1;
                } while (cursor.moveToNext());
            }


            listview_productos= (ListView) findViewById(R.id.listview_productos);
            ListViewAdapterProductos = new ListViewAdapterProductosEnv(this, SDTProductos);
            listview_productos.setAdapter(ListViewAdapterProductos);

            listview_productos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView adapterView, final View view, int i, long l) {
                    UltimaPosicion=i;

                   /* Intent intent = new Intent(getApplicationContext(), EditarCantidad.class);
                    intent.putExtra("nitsec", Extras.getString("nitsec"));
                    intent.putExtra("clisec", Extras.getInt("clisec"));
                    intent.putExtra("lisprecod", Extras.getInt("lisprecod"));
                    intent.putExtra("invgrucod", "");
                    intent.putExtra("invsubgrucod", "");
                    intent.putExtra("invfamcod", "");
                    intent.putExtra("prefijo", Extras.getString("prefijo"));
                    intent.putExtra("artsec", SDTProductos[i].ArtSec);
                    startActivityForResult(intent, 3);*/

                }
            });

        }catch (Exception e){
            int hh=1;
        }

        Totalizar();


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1105) {
            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra("position", 1);
                Log.e("position", String.valueOf(position));
                SDTProductos[position].ValorPedidoEnviado = SDTProductos[position].Total;
                SDTProductos[position].ActualizarDescuentos();
                SDTProductos[position].ActualizarCantidad();
                SDTProductos[position].Calcular();
                ListViewAdapterProductos.notifyDataSetChanged();
                Totalizar();
            }
        }

        else{
            SDTProductos[requestCode].ActualizarDescuentos();
            SDTProductos[requestCode].ActualizarCantidad();
            SDTProductos[requestCode].Calcular();
            ListViewAdapterProductos.notifyDataSetChanged();
            Totalizar();
        }


    }



    public void Totalizar(){


        TextView txt_subtotal=(TextView)findViewById(R.id.txt_abonoori);
        TextView txt_iva=(TextView)findViewById(R.id.txt_retencion);
        TextView txt_impoconsumo=(TextView)findViewById(R.id.txt_retencionica);
        TextView txt_total=(TextView)findViewById(R.id.txt_neto);
        TextView numarticulos=(TextView)findViewById(R.id.numarticulos);

        GestorPedidos GestorPedidos=new GestorPedidos();
        SDTResumenPedidos SDTResumenPedidos= GestorPedidos.TotalesPedido(getApplicationContext(),prefijo,nitsec,clisec,"","","");

        txt_subtotal.setText(String.format("%.2f",SDTResumenPedidos.Subtotal));
        txt_iva.setText(String.format("%.2f",SDTResumenPedidos.Iva));
        txt_impoconsumo.setText(String.format("%.2f",SDTResumenPedidos.Impoconsumo));
        txt_total.setText(String.format("%.2f",SDTResumenPedidos.Total));
        numarticulos.setText(String.valueOf(SDTProductos.length));
    }
    public void onRestart() {
        super.onRestart();
        super.onResume();
        finish();
        startActivity(getIntent());
    }
}
