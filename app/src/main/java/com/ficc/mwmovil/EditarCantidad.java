package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.bouncycastle.jcajce.provider.symmetric.ARC4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditarCantidad extends AppCompatActivity {

    Bundle Extras;
    private Handler handler;
    private  Runnable runnable;
    String nitsec;
    Integer clisec;

    double existenciaGlobal;
    Integer plazo, PreArtFacConVal;
    Boolean userIsInteracting = true;
    boolean isFirstSelection = true;

    String prefijo, plazoNom;
    String invfamcod;
     String[] CausalNombre;
     String[] concepto;

    SDTProductos[] SDTProductos ;
    TextView txt_subtotal;
    TextView txt_iva;
    TextView txt_impoconsumo;
    TextView txt_total;
    TextView textTitBon;

    TextView textTitDesc, textUnidadesPen;
    Double antunidades = 0.0;
    Double antunidadescre = 0.0;
    Double antunidadesinf = 0.0;
    Double antPrecioNeto = 0.0;
    Double antPrecio = 0.0;
    Double dpedido = 0.0;

    String antPreArtCod ="";


    TextView cambio,credito;
    TextView textCantBon,maxof;
    TextView textCantBonCaj,tituloboncant,txt_lista;
    LinearLayout informativocontent;
    String vEmpresa = "" ;
    int vecesrep =0;
    BaseDatos BaseDeDatos;

    double dctoContado = 0.0;
    double dctoCredito = 0.0;
    int conpago = 0;
    Spinner spinner_causal;
    Spinner spinner_presentacion;
    String antNotaInv = "N";
    String antNotaCar = "N";
    String priPreArtcod = "";
    Button editarbon;
    Integer lisprecod = 0;

    String ArtSolEnt = "N";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_cantidad);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }



        informativocontent = findViewById(R.id.informativocontent);
        //    DisplayMetrics dm=new DisplayMetrics();
        //  getWindowManager().getDefaultDisplay().getMetrics(dm);
        handler = new Handler();

        Time time = new Time();
        time.setToNow();

        int dayOfMonth =  AppGlobals.dayOfMonth; // Extras.getInt("dia");
        int month = AppGlobals.month;//Extras.getInt("mes");
        int year = AppGlobals.year;//Extras.getInt("ano");

        if(dayOfMonth > 0){
            time.set(time.second, time.minute, time.hour,dayOfMonth , month, year);
        }
        View fondo = findViewById(R.id.fondo);
        LinearLayout panel = findViewById(R.id.panel);

        fondo.setOnClickListener(v -> cerrarPanel());
        //getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.TrasparenteGris));
        //   getWindow().setLayout(dm.widthPixels*90/100,dm.heightPixels*90/100);


       /* panel.post(() -> {
            int width = (int) (panel.getRootView().getWidth() * 0.8);
            panel.getLayoutParams().width = width;
            panel.requestLayout();
        });*/


        final TextView txt_existencia = (TextView) findViewById(R.id.txt_existencia);

        try {
            txt_subtotal = (TextView) findViewById(R.id.txt_abonoori);
            tituloboncant = (TextView) findViewById(R.id.textTitBoxn);
            txt_iva = (TextView) findViewById(R.id.txt_retencion);
            textUnidadesPen = (TextView) findViewById(R.id.textUnidadesPen);
            txt_impoconsumo = (TextView) findViewById(R.id.txt_retencionica);
            txt_total = (TextView) findViewById(R.id.txt_neto);
            textTitBon = (TextView) findViewById(R.id.textTitBon);
            maxof = (TextView) findViewById(R.id.maxof);
            textTitDesc = (TextView) findViewById(R.id.textTitDesc);
            credito = (TextView) findViewById(R.id.credito);
            spinner_presentacion  = (Spinner) findViewById(R.id.spinner_presentacion);
            textCantBon = (TextView) findViewById(R.id.textCantBon);
            textCantBonCaj = (TextView) findViewById(R.id.textCantBon2);
            spinner_causal = (Spinner) findViewById(R.id.spinner_causal);
            Extras = this.getIntent().getExtras();
            editarbon = findViewById(R.id.editarbon);
            txt_lista = findViewById(R.id.txt_lista);
            invfamcod = Extras.getString("invfamcod");
            String invsubgrucod = Extras.getString("invsubgrucod");
            String invgrucod = Extras.getString("invgrucod");
            antNotaInv = Extras.getString("NotaInv");
            antNotaCar = Extras.getString("NotaCar");
            antPreArtCod = Extras.getString("PreArtCod");
            nitsec = Extras.getString("nitsec");
            clisec = Extras.getInt("clisec");
            plazo = Extras.getInt("plazo");
            plazoNom = Extras.getString("plazoNom");
             lisprecod = Extras.getInt("lisprecod");

            txt_lista.setText("Lista:"+String.valueOf(lisprecod));
            Log.e("edcanLista: ",String.valueOf(lisprecod));

            prefijo = Extras.getString("prefijo");
            Integer bodcod = Extras.getInt("bodega");
            Integer bodcodbon = 0;

            String artsec = Extras.getString("artsec");
            String clisiniva = "S";
            int perfil=99;
            double valortotales = totales(prefijo,nitsec,clisec,this);


            BaseDatos vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
            SQLiteDatabase BdSql=vBaseDeDatos.getReadableDatabase();
            Cursor traerArt = vBaseDeDatos.getWritableDatabase().rawQuery("select BodCod,BodCheckPred from Bodegas where BodCheckPred = 'N' ", null);
            if(traerArt.getCount() > 0){
                traerArt.moveToFirst();
                do{
                    bodcodbon = traerArt.getInt(0);
                }while (traerArt.moveToNext());
                traerArt.close();
            }


            List<Presentacion> lista = new ArrayList<>();


            String artPreArtCod ="";
            String where = " And PreArtCod <> 9 ";


            Cursor cprart = vBaseDeDatos.getWritableDatabase().rawQuery(
                    "SELECT preartcod  FROM Articulos where ArtSec = '"+artsec+"' ", null);

            while (cprart.moveToNext()) {
                artPreArtCod = cprart.getString(0);
            }

            if(  "S".equalsIgnoreCase(antNotaInv)
                    || "S".equalsIgnoreCase(antNotaCar)
                    || "9".equalsIgnoreCase(artPreArtCod)){
                where = "";
            }


            cprart.close();
            Cursor c = vBaseDeDatos.getWritableDatabase().rawQuery(
                    "SELECT preartcod, preartnom, PrePrefijval,PreArtFacConVal FROM articulospresentacion where ArtSec = '"+artsec+"' and lisprecod = "+lisprecod+" "+where+"  ", null);

            while (c.moveToNext()) {
                lista.add(new Presentacion(
                        c.getString(0),
                        c.getString(1), c.getDouble(2),c.getInt(3)
                ));
            }
            c.close();

            ArrayAdapter<Presentacion> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    lista
            );

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_presentacion.setAdapter(adapter);



            editarbon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mostrarPopup(view);
                }
            });


            String whereconcepto = " where ConNotNoAfeInv = 'X' ";

            if(antNotaInv.equalsIgnoreCase("S")){
                whereconcepto = " where ConNotNoAfeInv = 'S' ";
            }

            if(antNotaCar.equalsIgnoreCase("S")){
                whereconcepto = " where ConNotNoAfeInv = 'N' ";
            }


            BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
            Cursor cursorNCND = BaseDeDatos.getWritableDatabase().rawQuery("select ConNotCod,ConNotNom from ConceptoNCND "+whereconcepto, null); //order by nombre
            int possel = 0;
            CausalNombre = new String[cursorNCND.getCount()];
            concepto = new String[cursorNCND.getCount()];
            if (cursorNCND.getCount()>0){
                int vuelta=0;
                cursorNCND.moveToFirst();
                do {
                    concepto[vuelta] = cursorNCND.getString(0);
                    CausalNombre[vuelta]=cursorNCND.getString(1);

                    if(cursorNCND.getString(0).equalsIgnoreCase("11")){
                        possel = vuelta;
                    }
                    vuelta=vuelta+1;
                } while (cursorNCND.moveToNext());
                cursorNCND.close();
            }
            spinner_causal.setAdapter(new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,CausalNombre)); // simple_spinner_item //support_simple_spinner_dropdown_item
            spinner_causal.setSelection(possel);




            int traerdcto=0;
            Cursor cursorCla = BaseDeDatos.getReadableDatabase().rawQuery("select * from PerfilClientesClase ", null);
            if (cursorCla.getCount()>0){
                traerdcto=1;
                cursorCla.close();
            }


            Cursor cursorCli = BaseDeDatos.getReadableDatabase().rawQuery("select PerCliCod,ifnull(CliIva,'S') CliIva from clientes where nitsec='"+nitsec+"' and clisec="+clisec+" ", null);
            if (cursorCli.getCount()>0){
                cursorCli.moveToFirst();
                do {
                    try {
                        perfil=cursorCli.getInt(0);
                        clisiniva = cursorCli.getString(1);
                    }catch (Exception e){
                        Integer Error=1;
                    }
                } while (cursorCli.moveToNext());
                cursorCli.close();
            }

            ConBd conbd = new ConBd();
            conbd.Variables();
            GlobalVariables gGlobalVariables = GlobalVariables.getInstance();
            vEmpresa=gGlobalVariables.getEmpresa();
            if(vEmpresa  == null ){
                vEmpresa = gGlobalVariables.getEmpresa();
            }

            String    mantisficc  =    conbd.MantisFicc;

            if("S".equalsIgnoreCase(conbd.sincronizalinea)) {
                informativocontent.setVisibility(View.VISIBLE);
            }

            try {
                if (!"NC".equalsIgnoreCase(prefijo) &&
                        "S".equalsIgnoreCase(conbd.ActulizaOnline) ) {  //
                    GestorPedidos GestorPedidos = new GestorPedidos();
                    String Existencia = GestorPedidos.TraerExistencia(artsec);
                    if (Existencia!="999999.0"){
                        BaseDeDatos.getWritableDatabase().execSQL("update Articulos set Exist=" + Existencia + " where artsec='" + artsec
                                + "'");
                    }
                    //txt_existencia.setText(Existencia);
                }
            }catch (Exception e) {
                AlertDialog.Builder Alerta = new AlertDialog.Builder(getApplicationContext());
                Alerta.setMessage("Error "+e.getMessage());
                Alerta.setTitle("Alerta existencia");
                Alerta.setPositiveButton("OK", null);
                Alerta.setCancelable(true);
                Alerta.create().show();
                txt_existencia.setText("N/D");
            }

            String Exist = "Exist";

            String Precio = "Precio" + lisprecod;
            String PrecioPorRen = "precio" + lisprecod+"PorRen";
            if(lisprecod==0) {
                lisprecod = 1;
            }

            if("SUHOGAR".equalsIgnoreCase(vEmpresa) || "SUHOGARPRU".equalsIgnoreCase(vEmpresa)){
                if(lisprecod<30) {
                    lisprecod = 35;
                }
            }
            Log.e("edcanLista2: ",String.valueOf(lisprecod));

            if ("NC".equalsIgnoreCase(prefijo)){
                Exist = "KarUni";
                Precio = "KarPrePub";
                PrecioPorRen= "0 ";
            }else{
                Exist = "Exist";
                Precio = "Precio" + lisprecod;
                PrecioPorRen = "precio" + lisprecod+"PorRen";
            }
            if("V15".equalsIgnoreCase(prefijo) &
                    "IBANEZ".equalsIgnoreCase(vEmpresa.trim())){
                Exist = "ExistFec";
            }


            //"left join ClientesDevoluciones d on d.nitsec='" + nitsec + "' and d.clisec='" + clisec + "' and d.artsec=a.artsec " +
            String Consulta = "";

            Consulta = "select a.ArtSec,ArtCod,ArtNom," + Precio + "  precio,Desc1,(ifNULL(Desc2,0)+ifNULL((select PerCliDetDes1 from PerfilClientesClase pc where pc.ClaArtCod=a.ClaArtCod and pc.PerCliCod="+perfil+"),0)) Desc2," + Exist + " Exist,artemb,case  cliiva when 'S' then ParConIva else 0 end ParConIva,ArtValImp,0 cant,0 cantcaj,InvGruNom,Presentacion,0 KarUni,0 KarPrePub,ifNULL(Desc1,0) pordesc,ifNULL(Desc2,0) pordesc2,ifNULL(Desc3,0) pordesc3,ifNULL(Desc4,0) pordesc4,0.0 pordesc5,0.0 pordesc6,ArtRen,ArtLim,PrePreFijCosPro," + PrecioPorRen + " PrecioPorRen, " +
                    " case lgs.LisPreCod when 1 then 1 when 2 then 2 when 3 then 3 when 4 then 4 when 5 then 5 when 6 then 6 when 7 then 7 when 8 then 8 when 9 then 9  else " + lisprecod + " end listafin, ifnull(ArtExiAct,0) ArtExiAct ,ifnull(GruCheck,'N')GruCheck,ifnull(SubCheck,'N')SubCheck,ifnull(FamCheck,'N')FamCheck, a.invgrucod " +
                    " ,0 clidespagcont,0 clidespagcre,CLICONPAG,ArtcodBar,PreArtCod,ArtSolEnt  from Articulos a " +
                    " left join Clientes cc on cc.nitsec='" + nitsec + "' and cc.clisec='" + clisec + "'  " +
                    "left join ArticulosExi ex on ex.ArtSec=a.ArtSec and ArtBodCod = "+bodcod+" " +
                    " left join ListaPorGrupoSubgrupo lgs on lgs.nitsec='" + nitsec + "' and lgs.clisec='" + clisec + "' and  ((lgs.invgrucod=a.invgrucod and lgs.invsubgrucod =a.invsubgrucod ) or (lgs.invgrucod=a.invgrucod and lgs.invsubgrucod ='0')) " +
                    " where rtrim(ltrim(a.artsec))='"+artsec.trim()+"'  order by Exist desc ";





            String ConsultaPed="select cant,cantcaj,pordesc,pordesc2,pordesc3,pordesc4,pordesc5,pordesc6,confemp,confprov,confvend,pordescNo,pordesc2no,pordesc3no,pordesc4no,pordesc5no,pordesc6no " +
                    " ,ifNULL(pordescCero,0),ifNULL(pordesc2Cero,0),ifNULL(pordesc3Cero,0),ifNULL(pordesc4Cero,0),ifNULL(pordesc5Cero,0),ifNULL(cantinf,0),ifNULL(cantcajinf,0), NotaInv, NotaCar, " +
                    " ifnull(PreArtCod,'') PreArtCod, ifnull(precio,0) precio,  ifnull(ConNotCod,'') ConNotCod  "  +
                    "from pedido p where ifnull(p.PreArtCod,'') = '"+antPreArtCod+"' and ifnull(NotaInv,'N') = '"+antNotaInv+"' and ifnull(NotaCar,'N')  ='"+antNotaCar+"' and prefijo='" + prefijo + "' and p.nitsec='" + nitsec + "' and p.clisec='" + clisec + "' and p.artsec='"+artsec+"' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " ";

            Cursor cursor = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null); //order by nombre
            Cursor cursorped = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaPed, null); //order by nombre
            String checkdif = "N";
            SDTProductos = new SDTProductos[cursor.getCount()];
            if (cursor.getCount() > 0) {
                int vuelta = 0;

                double precioNu =0;

                cursor.moveToFirst();
                do {

                    precioNu = cursor.getDouble(3);
                   /* try{
                        String consultaart = "select precioesp from PreciosEspeciales where peNitSec = '"+nitsec+"' and peArtSec = '"+cursor.getString(0).trim()+"'";
                        Cursor cursorart = BaseDeDatos.getWritableDatabase().rawQuery(consultaart, null);
                        cursorart.moveToFirst();
                        if(cursorart.getCount() > 0){
                            cursorart.moveToFirst();
                            precioNu = cursorart.getDouble(0);
                            cursorart.close();
                        }else {
                            precioNu = cursor.getDouble(3);
                        }
                    }catch (Exception e){
                        precioNu = cursor.getDouble(3);
                    }


                    try{
                        String consultardesc = "select clidesdcto from DescGrupo where NitSec = '"+nitsec+"' and Clisec = "+clisec+" and clidesinvgrucod = '"+cursor.getString(31) +"' ";
                        Cursor cursordesc = BaseDeDatos.getWritableDatabase().rawQuery(consultardesc, null);
                        cursordesc.moveToFirst();
                        if(cursordesc.getCount() > 0){
                            cursordesc.moveToFirst();
                            precioNu = precioNu/(1-(cursordesc.getDouble(0)/100));
                            cursordesc.close();
                        }

                    }catch (Exception e){
                        Log.e("eror precio",e.toString());
                    }*/


                    dctoContado = cursor.getDouble(32);
                    dctoCredito = cursor.getDouble(33);
                    conpago     = cursor.getInt(34);
                    ArtSolEnt = cursor.getString(37);

                    SDTProductos SDTProductosItem = new SDTProductos();
                    SDTProductosItem.ArtSec = cursor.getString(0);
                    SDTProductosItem.Codigo = cursor.getString(1);
                    SDTProductosItem.Nombre = cursor.getString(2) ;//+ " (" + cursor.getString(12) + ")";
                    SDTProductosItem.Presentacion = cursor.getString(13);

                    SDTProductosItem.Nombrecomercial = cursor.getString(12);

                    Double precio = precioNu;

                    if("S".equalsIgnoreCase(clisiniva)){//Nuevo andres
                        SDTProductosItem.Iva = cursor.getInt(8);
                    }else{
                        SDTProductosItem.Iva = 0;
                    }

                    SDTProductosItem.Precio = precio;
                    SDTProductosItem.PrecioIva = precio * (1 + (Double.valueOf(cursor.getInt(8)) / 100));
                    SDTProductosItem.PrecioNeto = (  (precio * (1 + (Double.valueOf(cursor.getInt(8)) / 100))) *(1-(cursor.getDouble(16)/100)) *(1-(cursor.getDouble(17)/100)) *(1-(cursor.getDouble(18)/100)) *(1-(cursor.getDouble(19)/100)) *(1-(cursor.getDouble(20)/100)) *(1-(cursor.getDouble(21)/100))   )   +cursor.getDouble(9);
                    SDTProductosItem.ConfEmp = 0.0;// cursor.getDouble(0);
                    SDTProductosItem.ConfProv = 0.0; // cursor.getDouble(0);

                    if(cursor.getDouble(27)< 0.0 ){
                        SDTProductosItem.Existencia = 0.0;
                    }else{
                        SDTProductosItem.Existencia = cursor.getDouble(27);
                    }


                    existenciaGlobal = SDTProductosItem.Existencia;
                    SDTProductosItem.bodcod = bodcod;
                    SDTProductosItem.ArtCodBar =cursor.getString(35);
                    priPreArtcod =cursor.getString(36);

                    SDTProductosItem.Embalaje = cursor.getInt(7);
                    SDTProductosItem.ArtRen = cursor.getDouble(22);
                    SDTProductosItem.ArtLim = cursor.getDouble(23);
                    SDTProductosItem.CostoPro = cursor.getDouble(24);
                    SDTProductosItem.RentLis = cursor.getDouble(25);
                    if("IBANEZ".equalsIgnoreCase(vEmpresa)
                            || "SURTIMARCAS".equalsIgnoreCase(vEmpresa)
                            || "IBANEZPRU".equalsIgnoreCase(vEmpresa))
                    {
                        SDTProductosItem.LisPreCod = cursor.getInt(26);
                    }else{
                        SDTProductosItem.LisPreCod = lisprecod;
                    }


                    if("S".equalsIgnoreCase(cursor.getString(30))){
                        checkdif = "S";
                    } else if ("S".equalsIgnoreCase(cursor.getString(29))) {
                        checkdif = "S";
                    } else if ("S".equalsIgnoreCase(cursor.getString(28))) {
                        checkdif = "S";
                    }




                    SDTProductosItem.Impoconsumo = cursor.getDouble(9);

                    if (traerdcto==1 || "PROMEFAR".equalsIgnoreCase(vEmpresa.trim()))  {
                        SDTProductosItem.PrecioNeto = (  (precio * (1 + (Double.valueOf(cursor.getInt(8)) / 100))) *(1-(cursor.getDouble(16)/100)) *(1-(cursor.getDouble(5)/100)) *(1-(cursor.getDouble(18)/100)) *(1-(cursor.getDouble(19)/100)) *(1-(cursor.getDouble(20)/100)) *(1-(cursor.getDouble(21)/100))   )   +cursor.getDouble(9);
                        SDTProductosItem.Dct1 = cursor.getDouble(4); //cursor.getDouble(16)

                        SDTProductosItem.Dct2 = cursor.getDouble(5); //cursor.getDouble(17)
                    }else{

                        SDTProductosItem.Dct1 = 0.0; //cursor.getDouble(18)
                        SDTProductosItem.Dct2 = 0.0; //cursor.getDouble(19);

                    }

                    //  if (traerdcto==1) {

                    // }






                    SDTProductosItem.Dct3 = 0.0; //cursor.getDouble(18)

                    SDTProductosItem.Dct4 = 0.0; //cursor.getDouble(19);
                    SDTProductosItem.Dct5 = 0.0; //cursor.getDouble(20);
                    SDTProductosItem.Dct6 = 0.0; //cursor.getDouble(21);

                    SDTProductosItem.Dct1no = 0.0;
                    SDTProductosItem.Dct2no = 0.0;
                    SDTProductosItem.Dct3no = 0.0; // cursor.getDouble(0);
                    SDTProductosItem.Dct4no = 0.0; // cursor.getDouble(0);
                    SDTProductosItem.Dct5no = 0.0; // cursor.getDouble(0);
                    SDTProductosItem.Dct6no = 0.0; // cursor.getDouble(0);
                    SDTProductosItem.Dct7no = 0.0; // cursor.getDouble(0);
                    SDTProductosItem.Dct8no = 0.0; // cursor.getDouble(0);

                    if (cursorped.getCount() > 0) {
                        cursorped.moveToFirst();
                        do {
                            precio = cursorped.getDouble(27);
                            SDTProductosItem.Cajas = cursorped.getInt(1); ; // cursor.getInt(0);
                            SDTProductosItem.Unidades = cursorped.getDouble(0); //cursor.getDouble(0);

                            SDTProductosItem.Dct1 = cursorped.getDouble(2);


                            SDTProductosItem.Dct2 = cursorped.getDouble(3);

                            SDTProductosItem.Dct4 = cursorped.getDouble(5);
                            dpedido = cursorped.getDouble(5);


                            if("S".equalsIgnoreCase(cursorped.getString(24))
                                    ||"S".equalsIgnoreCase( cursorped.getString(25)) ){
                                SDTProductosItem.Precio = precio;
                                SDTProductosItem.PrecioIva = precio * (1 + (Double.valueOf(cursor.getInt(8)) / 100));
                                SDTProductosItem.PrecioNeto = (  (precio * (1 + (Double.valueOf(cursor.getInt(8)) / 100))) *(1-(cursor.getDouble(16)/100)) *(1-(cursor.getDouble(17)/100)) *(1-(cursor.getDouble(18)/100)) *(1-(cursor.getDouble(19)/100)) *(1-(cursor.getDouble(20)/100)) *(1-(cursor.getDouble(21)/100))   )   +cursor.getDouble(9);
                                SDTProductosItem.Dct4 = 0.0;

                            }else{
                                SDTProductosItem.PrecioNeto =  (precio  *(1-(dpedido/100)) );

                            }








                            Log.e("dcta4 ",String.valueOf(cursorped.getDouble(5)));
                            Log.e("PrecioNeto ",SDTProductosItem.PrecioNeto.toString());
                            SDTProductosItem.Dct5 =cursorped.getDouble(6);
                            SDTProductosItem.Dct6 = cursorped.getDouble(7);

                            SDTProductosItem.Dct1no = cursorped.getDouble(11);
                            SDTProductosItem.Dct2no = cursorped.getDouble(12);
                            SDTProductosItem.Dct3no = cursorped.getDouble(13); // cursor.getDouble(0);
                            SDTProductosItem.Dct4no = cursorped.getDouble(14); // cursor.getDouble(0);
                            SDTProductosItem.Dct5no = cursorped.getDouble(15); // cursor.getDouble(0);
                            SDTProductosItem.Dct6no = cursorped.getDouble(16); // cursor.getDouble(0);

                            SDTProductosItem.ConfEmp = cursorped.getDouble(8);
                            SDTProductosItem.ConfProv = cursorped.getDouble(9);

                            SDTProductosItem.fijDto1= cursorped.getInt(17);
                            SDTProductosItem.fijDto2= cursorped.getInt(18);
                            SDTProductosItem.fijDto3= cursorped.getInt(19);
                            SDTProductosItem.fijDto4= cursorped.getInt(20);
                            SDTProductosItem.fijDto5= cursorped.getInt(21);
                            SDTProductosItem.Unidadesinf = cursorped.getDouble(22);
                            SDTProductosItem.Cajasinf = cursorped.getInt(23);
                            SDTProductosItem.NotaInv = cursorped.getString(24) ;
                            SDTProductosItem.NotaCar = cursorped.getString(25) ;

                            SDTProductosItem.PreArtCod = cursorped.getString(26) ;
                            SDTProductosItem.CausalNombreSel = cursorped.getString(28) ;







                            Double pConfVend=cursorped.getDouble(10);
                            SDTProductosItem.ConfVend =pConfVend ;
                        } while (cursorped.moveToNext());
                        cursorped.close();
                    }else{
                        SDTProductosItem.PreArtCod = "";
                        SDTProductosItem.NotaInv = antNotaInv;
                        SDTProductosItem.NotaCar = antNotaCar ;
                    }

                    spinner_presentacion.setSelection(0);
                    //spinner_causal.setSelection(0);

                    for (int i = 0; i < spinner_presentacion.getCount(); i++) {
                        Presentacion p = (Presentacion) spinner_presentacion.getItemAtPosition(i);

                        if (p.getPreartcod().equals(antPreArtCod)) {
                            if(p.getPreArtFacConVal() == 0){
                                SDTProductosItem.Existencia = existenciaGlobal;
                            }else{
                                SDTProductosItem.Existencia = existenciaGlobal/p.getPreArtFacConVal();
                            }

                            spinner_presentacion.setSelection(i);
                            break;
                        }


                    }

                    for (int i = 0; i < spinner_causal.getCount(); i++) {
                        String p = concepto[i] ;
                        if (p.equals(SDTProductosItem.CausalNombreSel)) {
                            spinner_causal.setSelection(i);
                            break;
                        }
                    }







                    SDTProductosItem.Dct6no = cursor.getDouble(25);

                    SDTProductosItem.Dct7 = 0.0; // cursor.getDouble(0);
                    SDTProductosItem.Dct8 = 0.0; // cursor.getDouble(0);

                    SDTProductosItem.Prefijo = prefijo;
                    SDTProductosItem.Plazo = plazo;
                    SDTProductosItem.plazoNom = plazoNom;
                    SDTProductosItem.NitSec = nitsec;
                    SDTProductosItem.CliSec = clisec;
                    SDTProductosItem.pContext = getApplicationContext();

                    SDTProductosItem.EvaluarDescuentos();
                    SDTProductosItem.Calcular();


                    SDTProductos[vuelta] = SDTProductosItem;
                    textUnidadesPen.setText("Solicitadas: "+String.valueOf(SDTProductosItem.Unidadesinf));


                    vuelta = vuelta + 1;
                    Log.e("vuelta ",String.valueOf(vuelta));
                } while (cursor.moveToNext());
                cursor.close();
            }


            /*txt_subtotal.setText(String.format("%,d", SDTProductos[0].TotSubtotal));
            txt_iva.setText(String.format("%,d", SDTProductos[0].TotIva));
            txt_impoconsumo.setText(String.format("%,d", SDTProductos[0].TotImpoconsumo));
            txt_total.setText(String.format("%,d", SDTProductos[0].Total));*/


            txt_subtotal.setText(String.format( "%.2f", SDTProductos[0].TotSubtotal));
            txt_iva.setText(String.format( "%.2f", SDTProductos[0].TotIva));
            txt_impoconsumo.setText(String.format( "%.2f",SDTProductos[0].TotImpoconsumo));
            txt_total.setText(String.format( "%.2f", SDTProductos[0].Total));


            GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
            String DesConf=vGlobalVariables.getParMovManDesConf();
            String NoOtorgar=vGlobalVariables.getParMovNoOtorgar();
            String ManejaCajas=vGlobalVariables.getParMovManCanCaj();
            String ManejaDctoV2=vGlobalVariables.getParMovDescV2();
            if(ManejaDctoV2 == null){
                ManejaDctoV2 = "N";
            }

            TextView txt_codigo = (TextView) findViewById(R.id.txt_codigo);
            final TextView txt_nombre = (TextView) findViewById(R.id.txt_nombre);
            final TextView txt_Categoria = (TextView) findViewById(R.id.txt_Categoria);


            final  TextView txtpredcto = (TextView) findViewById(R.id.txtpredcto);

            TextView txt_precioneto = (TextView) findViewById(R.id.txt_precioneto);
            TextView txt_confemp = (TextView) findViewById(R.id.txt_confemp);
            TextView txt_confprov = (TextView) findViewById(R.id.txt_confprov);
            TextView txt_confvend = (TextView) findViewById(R.id.txt_confvend);


            final TextView edit_dcto1 = (TextView) findViewById(R.id.edit_dcto1);
            final TextView edit_dcto2 = (TextView) findViewById(R.id.edit_dcto2);
            final TextView edit_dcto3 = (TextView) findViewById(R.id.edit_dcto3);
            final TextView edit_dcto4 = (TextView) findViewById(R.id.edit_dcto4);
            final TextView edit_dcto5 = (TextView) findViewById(R.id.edit_dcto5);
            final TextView edit_dcto6 = (TextView) findViewById(R.id.edit_dcto6);
            final TextView edit_dcto1no = (TextView) findViewById(R.id.edit_dcto1no);
            final TextView edit_dcto2no = (TextView) findViewById(R.id.edit_dcto2no);
            final TextView edit_dcto3no = (TextView) findViewById(R.id.edit_dcto3no);
            final TextView edit_dcto4no = (TextView) findViewById(R.id.edit_dcto4no);
            final TextView txt_CodBarra = (TextView) findViewById(R.id.txt_CodBarra);
            final TextView txt_Presentacion = (TextView) findViewById(R.id.txt_Presentacion);
            final Button Aprobar = (Button) findViewById(R.id.Aprobar);

            final TextView edit_unidades = (TextView) findViewById(R.id.edit_unidades);
            final  TextView unidadesinf = (TextView) findViewById(R.id.cantinf);
            final  TextView cajasinf  = (TextView) findViewById(R.id.cantcajinf);
            final LinearLayout liner_conf = (LinearLayout) findViewById(R.id.liner_conf);
            Button btnsumar = findViewById(R.id.btnsumar);
            Button btnrestar = findViewById(R.id.btnrestar);
            antunidades = SDTProductos[0].Unidades;
            antunidadesinf = SDTProductos[0].Unidadesinf;
            antPrecioNeto = SDTProductos[0].PrecioNeto;
            antPrecio = SDTProductos[0].Precio;
           /* antNotaInv =   SDTProductos[0].NotaInv ;
            antNotaCar =  SDTProductos[0].NotaCar;*/
            if("S".equalsIgnoreCase(SDTProductos[0].NotaInv)){
                //Aprobar.setVisibility(View.GONE);
            }
            if("S".equalsIgnoreCase(SDTProductos[0].NotaCar)){
                credito.setVisibility(View.GONE);
            }
            if ("S".equals(ArtSolEnt)) {
                edit_unidades.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else {
                edit_unidades.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }
            descuentos();

            if(SDTProductos[0].NotaInv.equalsIgnoreCase("S")) {
                if (SDTProductos[0].Historico.isEmpty()) {
                    edit_unidades.setKeyListener(null);
                    edit_unidades.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Notificacion.aviso(
                                    getApplicationContext(),
                                    "Se está realizando un credito bueno a un producto que el cliente no ha comprado: " +
                                            " "+SDTProductos[0].Nombre
                            );
                        }
                    });


                    }
                    //Toast.makeText(context, "Se está realizando un credito bueno a un producto que el cliente no ha comprado", Toast.LENGTH_LONG).show();


                }




            Aprobar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {




                    Presentacion item = (Presentacion) spinner_presentacion.getSelectedItem();
                    String preartcodSeleccionado = item.getPreartcod();
                    String p = "";
                    if(concepto.length >0){
                         p = concepto[spinner_causal.getSelectedItemPosition()] ;
                    }



                    double PrePefijval = item.getPrePefijval();

                    if(PrePefijval > 0){
                        //SDTProductos[0].NotaInv = "N";


                        SDTProductos[0].CausalNombreSel =p;
                        SDTProductos[0].Precio = PrePefijval;
                        SDTProductos[0].PreArtCod  = preartcodSeleccionado;
                        SDTProductos[0].PrecioNeto = ( PrePefijval * (1-(SDTProductos[0].Dct1/100)) *  (1-(SDTProductos[0].Dct2/100))
                                *  (1-(SDTProductos[0].Dct3/100)) * (1-(SDTProductos[0].Dct4/100))* (1-(SDTProductos[0].Dct5/100)));
                        SDTProductos[0].Guardar(1);






                        Intent data = new Intent();
                        data.putExtra("posicion", Extras.getInt("position"));

                        if(antunidadesinf > 0 && antunidades == 0){

                            data.putExtra("Cantidad", SDTProductos[0].Unidadesinf);
                        }else{
                            data.putExtra("Cantidad", SDTProductos[0].Unidades);
                        }
                        data.putExtra("PrecioNeto", SDTProductos[0].PrecioNeto);
                        data.putExtra("NotaInv", SDTProductos[0].NotaInv);
                        data.putExtra("NotaCar", SDTProductos[0].NotaCar);
                        data.putExtra("Credito", "N");
                        setResult(RESULT_OK, data);


                        finish();
                        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_right);
                    }else{
                        Toast.makeText(getApplicationContext(), "Precio de la presentación "+item.getPreartnom()+" en 0", Toast.LENGTH_LONG).show();

                    }

                }
            });


            spinner_presentacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (isFirstSelection) {
                        isFirstSelection = false;

                    }else{
                        edit_unidades.setText(String.valueOf("0"));
                    }
                    try{
                        Presentacion seleccion = lista.get(position);

                        String codigo = seleccion.getPreartcod();
                        String nombre = seleccion.getPreartnom();
                        double valor = seleccion.getPrePefijval();
                        txt_precioneto.setText(String.format("%.2f",valor));
                        txtpredcto.setText(String.format("%.2f",( valor * (1-(dpedido/100)))));
                        txt_existencia.setText(String.format("%.2f",existenciaGlobal/seleccion.getPreArtFacConVal()));
                        if(valor == 0){
                            Toast.makeText(getApplicationContext(), "Precio de la presentación en 0", Toast.LENGTH_LONG).show();
                        }else{

                            SDTProductos[0].Existencia = existenciaGlobal/seleccion.getPreArtFacConVal();
                            SDTProductos[0].Precio = valor;
                            SDTProductos[0].PreArtCod = codigo;
                            SDTProductos[0].PrecioNeto = ( valor * (1-(dpedido/100)));


                            SDTProductos[0].Guardar(1666);
                            descuentos();
                            // Aquí haces lo que necesites
                            Log.d("SPINNER", "Seleccionado: " + nombre + " - " + valor);
                        }


                    }catch (Exception e){
                        // Aquí haces lo que necesites
                        Log.d("Exception presentacion" , e.toString());

                        AlertDialog.Builder Alerta = new AlertDialog.Builder(EditarCantidad.this);
                        Alerta.setMessage("Error "+e.getMessage()+ e.getCause()+e.getLocalizedMessage());
                        Alerta.setTitle("Alerta");
                        Alerta.setPositiveButton("OK", null);
                        Alerta.setCancelable(true);
                        Alerta.create().show();

                    }







                    // Ejemplo: actualizar un TextView
                    // txtPrecio.setText(String.valueOf(valor));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Opcional
                }
            });

            credito.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {



                    Presentacion item = (Presentacion) spinner_presentacion.getSelectedItem();
                    String preartcodSeleccionado = item.getPreartcod();
                    double PrePefijval = item.getPrePefijval();


                    SDTProductos[0].Precio = PrePefijval;
                    SDTProductos[0].PrecioNeto = PrePefijval;
                    SDTProductos[0].PreArtCod  = preartcodSeleccionado;
                    SDTProductos[0].NotaInv = "S";
                    SDTProductos[0].NotaCar = "N";
                    SDTProductos[0].Unidades = antunidadescre;
                    SDTProductos[0].Unidadesinf = 0.0;

                    SDTProductos[0].Guardar(1);



                    Intent data = new Intent();
                    data.putExtra("posicion", Extras.getInt("position"));
                    data.putExtra("Cantidad", SDTProductos[0].Unidades);
                    data.putExtra("NotaInv", SDTProductos[0].NotaInv);
                    data.putExtra("Credito", "S");
                    setResult(RESULT_OK, data);
                    if("N".equalsIgnoreCase(antNotaInv)){
                        SDTProductos[0].Unidades = antunidades;
                        SDTProductos[0].PreArtCod  = antPreArtCod;
                        SDTProductos[0].Unidadesinf= antunidadesinf;
                        SDTProductos[0].NotaInv = antNotaInv;
                        SDTProductos[0].NotaCar = antNotaCar;
                        SDTProductos[0].Guardar(1);
                    }

                    finish();
                    overridePendingTransition(R.anim.no_anim, R.anim.slide_out_right);
                }
            });

            btnsumar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    double Unidades  =0.0;


                    if(edit_unidades.getText().toString().equals("")){
                    }else{
                        Unidades = Double.valueOf(edit_unidades.getText().toString());
                    }

                    Unidades +=1;


                    if(SDTProductos[0].NotaInv.equalsIgnoreCase("S")) {
                        if (SDTProductos[0].Historico.isEmpty()) {
                            if(Unidades>0){
                                Notificacion.aviso(
                                        getApplicationContext(),
                                        "Se está realizando un credito bueno a un producto que el cliente no ha comprado: " +
                                                " "+SDTProductos[0].Nombre
                                );
                                Unidades = 0.0;
                            }
                            //Toast.makeText(context, "Se está realizando un credito bueno a un producto que el cliente no ha comprado", Toast.LENGTH_LONG).show();


                        }
                    }




                    edit_unidades.setText(String.valueOf(Unidades));

                }
            });


            btnrestar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    double Unidades  =0.0;
                    double Existencia = 0.0;

                    if(txt_existencia.getText().toString().equals("")){

                    }else{
                        Existencia = Double.valueOf(txt_existencia.getText().toString().replace(",","."));
                    }

                    if(edit_unidades.getText().toString().equals("")){

                    }else{
                        Unidades = Double.valueOf(edit_unidades.getText().toString());
                    }
                    if(Unidades == 0){

                    }else{
                        Unidades -=1;
                    }


                    if(SDTProductos[0].NotaInv.equalsIgnoreCase("S")) {
                        if (SDTProductos[0].Historico.isEmpty()) {
                            if(Unidades>0){
                                Notificacion.aviso(
                                        getApplicationContext(),
                                        "Se está realizando un credito bueno a un producto que el cliente no ha comprado: " +
                                                " "+SDTProductos[0].Nombre
                                );
                                Unidades = 0.0;
                            }
                            //Toast.makeText(context, "Se está realizando un credito bueno a un producto que el cliente no ha comprado", Toast.LENGTH_LONG).show();


                        }
                    }


                    edit_unidades.setText(String.valueOf(Unidades));
               /* SDTProductos[position].Unidades = Unidades;
                SDTProductos[position].Guardar(1);*/
                }
            });




            TextView textView58 = (TextView) findViewById(R.id.textView58);

            Switch toggle = (Switch) findViewById(R.id.switch1);
            Switch toggle2 = (Switch) findViewById(R.id.switch2);
            Switch toggle3 = (Switch) findViewById(R.id.switch3);
            Switch toggle4 = (Switch) findViewById(R.id.switch4);
            Switch toggle5 = (Switch) findViewById(R.id.switch5);

            if (SDTProductos[0].fijDto1==1){toggle.setChecked(true);}
            if (SDTProductos[0].fijDto2==1){toggle2.setChecked(true);}
            if (SDTProductos[0].fijDto3==1){toggle3.setChecked(true);}
            if (SDTProductos[0].fijDto4==1){toggle4.setChecked(true);}
            if (SDTProductos[0].fijDto5==1){toggle5.setChecked(true);}


            if ("S".equalsIgnoreCase(ManejaDctoV2)==false) {
                toggle.setVisibility(View.GONE);
                toggle2.setVisibility(View.GONE);
                toggle3.setVisibility(View.GONE);
                toggle4.setVisibility(View.GONE);
                toggle5.setVisibility(View.GONE);
                textView58.setVisibility(View.GONE);
            }
            if("S".equalsIgnoreCase(mantisficc)){
                toggle.setVisibility(View.VISIBLE);
                toggle2.setVisibility(View.VISIBLE);
                toggle3.setVisibility(View.VISIBLE);
                toggle4.setVisibility(View.VISIBLE);
                toggle5.setVisibility(View.VISIBLE);
                textView58.setVisibility(View.VISIBLE);
            }
            if("S".equalsIgnoreCase(ManejaCajas)
                    || "N".equalsIgnoreCase(mantisficc)) {

            }else{

            }
            if("S".equalsIgnoreCase(mantisficc)){

            }

            if("S".equalsIgnoreCase(DesConf)){
                txt_confprov.setEnabled(true);
                txt_confvend.setEnabled(true);
                txt_confemp.setEnabled(true);
            }else{
                txt_confprov.setEnabled(false);
                txt_confvend.setEnabled(false);
                txt_confemp.setEnabled(false);
                liner_conf.setVisibility(View.GONE);
            }

            if("S".equalsIgnoreCase(NoOtorgar)){
                edit_dcto1.setEnabled(true);
                edit_dcto2.setEnabled(true);
                edit_dcto3.setEnabled(true);
                edit_dcto4.setEnabled(true);
                edit_dcto5.setEnabled(true);
                edit_dcto6.setEnabled(true);
            }else{
                edit_dcto1.setEnabled(false);
                edit_dcto2.setEnabled(false);
                edit_dcto3.setEnabled(false);
                edit_dcto4.setEnabled(false);
                edit_dcto5.setEnabled(false);
                edit_dcto6.setEnabled(false);
            }
            if ("BRILLO".equalsIgnoreCase(vEmpresa.trim())) {
                edit_dcto1.setEnabled(true);
                edit_dcto2.setEnabled(false);
                edit_dcto3.setEnabled(false);
                edit_dcto4.setEnabled(false);
                edit_dcto5.setEnabled(false);
                edit_dcto6.setEnabled(false);
            }

            String bloqdes = "N";
            if ("SUHOGAR".equalsIgnoreCase(vEmpresa.trim())) {
                Cursor permiso =  BaseDeDatos.getWritableDatabase().rawQuery("Select modDcto from usuarios",null);
                permiso.moveToFirst();
                if(permiso.getCount()>0){
                    if("N".equalsIgnoreCase(permiso.getString(0))){
                        //permiso
                        edit_dcto1.setEnabled(false);
                        edit_dcto2.setEnabled(false);
                        edit_dcto3.setEnabled(false);
                        edit_dcto4.setEnabled(true);
                        edit_dcto5.setEnabled(false);
                        edit_dcto6.setEnabled(false);
                    }

                }

                Cursor bloqlista =  BaseDeDatos.getWritableDatabase().rawQuery("Select LisPrebloqDes from ListasPrecios where lisprecod = "+lisprecod,null);
                bloqlista.moveToFirst();

                if(bloqlista.getCount()>0){
                    bloqdes = bloqlista.getString(0);
                }

            }



            if("S".equalsIgnoreCase(checkdif)){
                edit_dcto6.setEnabled(false);
            }

            if("S".equalsIgnoreCase(bloqdes)){
                //permiso
                edit_dcto1.setEnabled(false);
                edit_dcto2.setEnabled(false);
                edit_dcto3.setEnabled(false);
                edit_dcto4.setEnabled(false);
                edit_dcto5.setEnabled(false);
                edit_dcto6.setEnabled(false);
            }


            if (SDTProductos.length > 0) {







                //final TextView txt_subtotal = (TextView) findViewById(R.id.txt_Subtotal);
                //final TextView txt_iva = (TextView) findViewById(R.id.txt_iva);
                //final TextView txt_impoconsumo = (TextView) findViewById(R.id.txt_impoconsumo);
                //final TextView txt_total = (TextView) findViewById(R.id.txt_neto);

               /* txt_subtotal.setText("0");
                txt_iva.setText("0");
                txt_impoconsumo.setText("0");
                txt_total.setText("0");*/

                if("S".equalsIgnoreCase(antNotaCar)){

                    txt_codigo.setText("C"+SDTProductos[0].Codigo);
                }else{
                    txt_codigo.setText(SDTProductos[0].Codigo);
                }

                txt_nombre.setText(SDTProductos[0].Nombre);
                txt_Categoria.setText(SDTProductos[0].Nombrecomercial);
                txt_CodBarra.setText(SDTProductos[0].ArtCodBar);

                ImageView imgCodigo = findViewById(R.id.imgCodigo);

                Bitmap bitmap = generarCodigoBarras(SDTProductos[0].ArtCodBar);

                imgCodigo.setImageBitmap(bitmap);



                txt_precioneto.setText(String.format("%.2f",SDTProductos[0].PrecioNeto));
                Double preciounit =( SDTProductos[0].PrecioIva * (1-(SDTProductos[0].Dct1/100)) *  (1-(SDTProductos[0].Dct2/100))
                        *  (1-(SDTProductos[0].Dct3/100)) * (1-(SDTProductos[0].Dct4/100))* (1-(SDTProductos[0].Dct5/100)));

                txtpredcto.setText(String.format( "%.2f",preciounit));
                txt_confemp.setText(SDTProductos[0].ConfEmp.toString());
                txt_confprov.setText(SDTProductos[0].ConfProv.toString());
                txt_confvend.setText(SDTProductos[0].ConfVend.toString());
                txt_existencia.setText(SDTProductos[0].Existencia.toString());
                txt_Presentacion.setText(SDTProductos[0].Presentacion);

                textTitDesc.setText(SDTProductos[0].TxtDescuentos);
                textTitBon.setText(SDTProductos[0].TxtBonificado);
                tituloboncant.setText("Bonificados ("+SDTProductos[0].totalBon+")");
                textCantBon.setText(SDTProductos[0].TxtBonificadoUni);
                textCantBonCaj.setText(SDTProductos[0].TxtBonificadoCaj);

                if("S".equalsIgnoreCase(SDTProductos[0].checkmax)){
                    textTitBon.setBackgroundColor(Color.parseColor("#ef9a9a"));
                    maxof.setVisibility(View.VISIBLE);
                }else{
                    textTitBon.setBackgroundColor(Color.parseColor("#F7F8CD"));
                    maxof.setVisibility(View.GONE);
                }

                Integer kk=SDTProductos[0].Cajas;
                Double jj=SDTProductos[0].Unidades;



                if (SDTProductos[0].Cajas==0) {

                }else{

                }
                if (SDTProductos[0].Cajasinf==0) {
                    cajasinf.setText("0");
                }else{
                    cajasinf.setText(SDTProductos[0].Cajasinf.toString());
                }

                // edit_unidades.setText(SDTProductos[0].Unidades.toString());
                if (SDTProductos[0].Unidades==0) {
                    edit_unidades.setText("");
                }else{
                    Double Tmp1=Double.valueOf(SDTProductos[0].Unidades);
                    Double Tmp2=SDTProductos[0].Unidades;
                    if ("SNACKS".equalsIgnoreCase(vEmpresa.trim()  )
                            ||"INDULAC" .equalsIgnoreCase(vEmpresa.trim() )
                            || "TODORAPIDAS".equalsIgnoreCase( vEmpresa.trim())
                            || "TOTALFOOD".equalsIgnoreCase( vEmpresa.trim())) {
                        int hh=Tmp1.compareTo(Tmp2);
                        if (hh==0.00) {
                            edit_unidades.setText(String.valueOf(SDTProductos[0].Unidades));
                        }else{
                            edit_unidades.setText(String.valueOf(SDTProductos[0].Unidades));
                        }
                    }else{
                        edit_unidades.setText(String.valueOf(SDTProductos[0].Unidades));
                    }
                }

                if (SDTProductos[0].Unidadesinf==0) {
                    textUnidadesPen.setText("Solicitadas: 0");
                }else{
                    Double Tmp1=Double.valueOf(SDTProductos[0].Unidadesinf);
                    Double Tmp2=SDTProductos[0].Unidadesinf;
                    if ("SNACKS".equalsIgnoreCase(vEmpresa.trim())
                            ||"INDULAC".equalsIgnoreCase( vEmpresa.trim() )
                            ||  "TODORAPIDAS".equalsIgnoreCase(vEmpresa.trim())
                            || "TOTALFOOD".equalsIgnoreCase(vEmpresa.trim()) ) {
                        int hh=Tmp1.compareTo(Tmp2);
                        if (hh==0.00) {
                            textUnidadesPen.setText("Solicitadas: "+String.valueOf(SDTProductos[0].Unidadesinf));
                        }else{
                            textUnidadesPen.setText("Solicitadas: "+String.valueOf(SDTProductos[0].Unidadesinf));
                        }
                    }else{
                        textUnidadesPen.setText("Solicitadas: "+String.valueOf(SDTProductos[0].Unidadesinf));
                    }
                }

                edit_dcto1.setText(SDTProductos[0].Dct1.toString());
                edit_dcto2.setText(SDTProductos[0].Dct2.toString());
                edit_dcto3.setText(SDTProductos[0].Dct3.toString());
                edit_dcto4.setText(SDTProductos[0].Dct4.toString());
                Log.e("edit_dcto41 ",SDTProductos[0].Dct4.toString());


                edit_dcto5.setText(SDTProductos[0].Dct5.toString());
                edit_dcto6.setText(SDTProductos[0].Dct6.toString());

                if(SDTProductos[0].Dct1no-SDTProductos[0].Dct1 > 0){
                    edit_dcto1no.setText(String.valueOf((SDTProductos[0].Dct1no-SDTProductos[0].Dct1)));
                }else{
                    edit_dcto1no.setText(String.valueOf((0)));
                }





                if(SDTProductos[0].Dct2no-SDTProductos[0].Dct2 > 0){
                    edit_dcto2no.setText(String.valueOf((SDTProductos[0].Dct2no-SDTProductos[0].Dct2)));
                }else{
                    edit_dcto2no.setText(String.valueOf((0)));
                }

                if(SDTProductos[0].Dct3no-SDTProductos[0].Dct3 > 0){
                    edit_dcto3no.setText(String.valueOf((SDTProductos[0].Dct3no-SDTProductos[0].Dct3)));
                }else{
                    edit_dcto3no.setText(String.valueOf((0)));
                }

                if(SDTProductos[0].Dct4no-SDTProductos[0].Dct4 > 0){
                    edit_dcto4no.setText(String.valueOf((SDTProductos[0].Dct4no-SDTProductos[0].Dct4)));
                }else{

                    edit_dcto4no.setText(String.valueOf((0)));
                }
                if(SDTProductos[0].Dct4 == 0){
                    edit_dcto4.setText(String.valueOf((0)));
                    Log.e("edit_dcto412",SDTProductos[0].Dct4.toString());
                }








                toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            //Toast.makeText(getApplicationContext(), "Switch on!", Toast.LENGTH_LONG).show();
                            SDTProductos[0].fijDto1=1;

                        } else {
                            SDTProductos[0].fijDto1=0;
                            //Toast.makeText(getApplicationContext(), "Switch off!", Toast.LENGTH_LONG).show();
                        }
                        SDTProductos[0].Guardar(1);

                        edit_dcto1.setText(SDTProductos[0].Dct1.toString());
                        Double preciounit =( SDTProductos[0].PrecioIva * (1-(SDTProductos[0].Dct1/100)) *  (1-(SDTProductos[0].Dct2/100))
                                *  (1-(SDTProductos[0].Dct3/100)) * (1-(SDTProductos[0].Dct4/100))* (1-(SDTProductos[0].Dct5/100)));

                        txtpredcto.setText(String.format( "%.2f",preciounit));
                        txt_subtotal.setText(String.format( "%.2f", SDTProductos[0].TotSubtotal));
                        txt_iva.setText(String.format( "%.2f", SDTProductos[0].TotIva));
                        txt_impoconsumo.setText(String.format( "%.2f",SDTProductos[0].TotImpoconsumo));
                        txt_total.setText(String.format( "%.2f", SDTProductos[0].Total));
                    }
                });

                toggle2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            //Toast.makeText(getApplicationContext(), "Switch on!", Toast.LENGTH_LONG).show();
                            SDTProductos[0].fijDto2=1;

                        } else {
                            SDTProductos[0].fijDto2=0;
                            //Toast.makeText(getApplicationContext(), "Switch off!", Toast.LENGTH_LONG).show();
                        }
                        SDTProductos[0].Guardar(1);

                        Double preciounit =( SDTProductos[0].PrecioIva * (1-(SDTProductos[0].Dct1/100)) *  (1-(SDTProductos[0].Dct2/100))
                                *  (1-(SDTProductos[0].Dct3/100)) * (1-(SDTProductos[0].Dct4/100))* (1-(SDTProductos[0].Dct5/100)));

                        txtpredcto.setText(String.format( "%.2f",preciounit));
                        txt_subtotal.setText(String.format( "%.2f", SDTProductos[0].TotSubtotal));
                        txt_iva.setText(String.format( "%.2f", SDTProductos[0].TotIva));
                        txt_impoconsumo.setText(String.format( "%.2f",SDTProductos[0].TotImpoconsumo));
                        txt_total.setText(String.format( "%.2f", SDTProductos[0].Total));
                    }
                });

                toggle3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            //Toast.makeText(getApplicationContext(), "Switch on!", Toast.LENGTH_LONG).show();
                            SDTProductos[0].fijDto3=1;

                        } else {
                            SDTProductos[0].fijDto3=0;
                            //Toast.makeText(getApplicationContext(), "Switch off!", Toast.LENGTH_LONG).show();
                        }
                        SDTProductos[0].Guardar(1);
                        SDTProductos[0].Calcular();
                        Double preciounit =( SDTProductos[0].PrecioIva * (1-(SDTProductos[0].Dct1/100)) *  (1-(SDTProductos[0].Dct2/100))
                                *  (1-(SDTProductos[0].Dct3/100)) * (1-(SDTProductos[0].Dct4/100))* (1-(SDTProductos[0].Dct5/100)));

                        txtpredcto.setText(String.format( "%.2f",preciounit));
                        txt_subtotal.setText(String.format( "%.2f", SDTProductos[0].TotSubtotal));
                        txt_iva.setText(String.format( "%.2f", SDTProductos[0].TotIva));
                        txt_impoconsumo.setText(String.format( "%.2f",SDTProductos[0].TotImpoconsumo));
                        txt_total.setText(String.format( "%.2f", SDTProductos[0].Total));
                    }
                });

                toggle4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            //Toast.makeText(getApplicationContext(), "Switch on!", Toast.LENGTH_LONG).show();
                            SDTProductos[0].fijDto4=1;

                        } else {
                            SDTProductos[0].fijDto4=0;
                            //Toast.makeText(getApplicationContext(), "Switch off!", Toast.LENGTH_LONG).show();
                        }
                        SDTProductos[0].Guardar(1);

                        Double preciounit =( SDTProductos[0].PrecioIva * (1-(SDTProductos[0].Dct1/100)) *  (1-(SDTProductos[0].Dct2/100))
                                *  (1-(SDTProductos[0].Dct3/100)) * (1-(SDTProductos[0].Dct4/100))* (1-(SDTProductos[0].Dct5/100)));

                        txtpredcto.setText(String.format( "%.2f",preciounit));
                        txt_subtotal.setText(String.format( "%.2f", SDTProductos[0].TotSubtotal));
                        txt_iva.setText(String.format( "%.2f", SDTProductos[0].TotIva));
                        txt_impoconsumo.setText(String.format( "%.2f",SDTProductos[0].TotImpoconsumo));
                        txt_total.setText(String.format( "%.2f", SDTProductos[0].Total));
                    }
                });

                toggle5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            //Toast.makeText(getApplicationContext(), "Switch on!", Toast.LENGTH_LONG).show();
                            SDTProductos[0].fijDto5=1;

                        } else {
                            SDTProductos[0].fijDto5=0;
                            //Toast.makeText(getApplicationContext(), "Switch off!", Toast.LENGTH_LONG).show();
                        }
                        SDTProductos[0].Guardar(1);
                        //SDTProductos[0].Calcular();
                        Double preciounit =( SDTProductos[0].PrecioIva * (1-(SDTProductos[0].Dct1/100)) *  (1-(SDTProductos[0].Dct2/100))
                                *  (1-(SDTProductos[0].Dct3/100)) * (1-(SDTProductos[0].Dct4/100))* (1-(SDTProductos[0].Dct5/100)));

                        txtpredcto.setText(String.format( "%.2f",preciounit));
                        txt_subtotal.setText(String.format( "%.2f", SDTProductos[0].TotSubtotal));
                        txt_iva.setText(String.format( "%.2f", SDTProductos[0].TotIva));
                        txt_impoconsumo.setText(String.format( "%.2f",SDTProductos[0].TotImpoconsumo));
                        txt_total.setText(String.format( "%.2f", SDTProductos[0].Total));

                    }
                });






                txt_confemp.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        Double Descuento;
                        if (s.toString().trim().isEmpty()) {
                            Descuento = 0.0;
                        } else {
                            Descuento = Double.valueOf(s.toString());
                        }

                        SDTProductos[0].ConfEmp = Descuento;
                        SDTProductos[0].Calcular();
                        SDTProductos[0].Guardar(0);

                    }
                });

                txt_confprov.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        Double Descuento;
                        if (s.toString().trim().isEmpty()) {
                            Descuento = 0.0;
                        } else {
                            Descuento = Double.valueOf(s.toString());
                        }

                        SDTProductos[0].ConfProv = Descuento;
                        SDTProductos[0].Calcular();
                        SDTProductos[0].Guardar(0);

                    }
                });

                txt_confvend.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        Double Descuento;
                        if (s.toString().trim().isEmpty()) {
                            Descuento = 0.0;
                        } else {
                            Descuento = Double.valueOf(s.toString());
                        }

                        SDTProductos[0].ConfVend = Descuento;
                        SDTProductos[0].Calcular();
                        SDTProductos[0].Guardar(0);
                    }
                });

                edit_dcto1.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        Double Descuento;
                        Log.e("entroDescuento",s.toString());
                        if (s.toString().trim().isEmpty()) {
                            Descuento = 0.0;
                        } else {
                            Descuento = Double.valueOf(s.toString());
                        }




                        if (Descuento > SDTProductos[0].Dct1no && "S".equalsIgnoreCase(mantisficc)
                                && SDTProductos[0].Dct1no > 0) {
                            Descuento = SDTProductos[0].Dct1no;
                            Log.e("entroaaaaaaaa1a",String.valueOf(Descuento));
                            edit_dcto1.setText(String.valueOf(SDTProductos[0].Dct1no));
                        }else{
                            if(!"GELVEZARA".equalsIgnoreCase(vEmpresa)
                                    && !"MENTAHAIR".equalsIgnoreCase(vEmpresa)
                                    && !"MENTAHAIRCOT".equalsIgnoreCase(vEmpresa)
                                    && !"SURTIMARCAS".equalsIgnoreCase(vEmpresa)
                                    && !"BRILLO".equalsIgnoreCase(vEmpresa)
                                    && "N".equalsIgnoreCase(mantisficc)){

                                if (Descuento > SDTProductos[0].Dct1no) {
                                    Descuento = SDTProductos[0].Dct1no;
                                    Log.e("entroaaaa2aaaa", String.valueOf(Descuento));
                                    edit_dcto1.setText(String.valueOf(SDTProductos[0].Dct1no));
                                }

                            }

                        }




                        if (SDTProductos[0].Dct1 != Descuento) {
                            SDTProductos[0].Dct1 = Descuento;
                            SDTProductos[0].Calcular();
                            SDTProductos[0].Guardar(0);
                        }





                        Double preciounit =( SDTProductos[0].PrecioIva * (1-(SDTProductos[0].Dct1/100)) *  (1-(SDTProductos[0].Dct2/100))
                                *  (1-(SDTProductos[0].Dct3/100)) * (1-(SDTProductos[0].Dct4/100))* (1-(SDTProductos[0].Dct5/100)));


                        txtpredcto.setText(String.format( "%.2f",preciounit));
                        txt_subtotal.setText(String.format( "%.2f", SDTProductos[0].TotSubtotal));
                        txt_iva.setText(String.format( "%.2f", SDTProductos[0].TotIva));
                        txt_impoconsumo.setText(String.format( "%.2f",SDTProductos[0].TotImpoconsumo));
                        txt_total.setText(String.format( "%.2f", SDTProductos[0].Total));



                        edit_dcto1no.setText(String.valueOf((SDTProductos[0].Dct1no-SDTProductos[0].Dct1)));
                        Log.e("entroadcto",String.valueOf(SDTProductos[0].Dct1));

                        if(SDTProductos[0].Dct1no-SDTProductos[0].Dct1 > 0){
                            Log.e("entroaaaa2aaaa",String.valueOf(SDTProductos[0].Dct1no-SDTProductos[0].Dct1));
                            edit_dcto1no.setText(String.valueOf((SDTProductos[0].Dct1no-SDTProductos[0].Dct1)));
                        }else{
                            edit_dcto1no.setText(String.valueOf((0)));
                            Log.e("entroaasssssssssaa2aaaa","d");
                        }


                    }
                });

                edit_dcto2.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        Double Descuento;
                        if (s.toString().trim().isEmpty()) {
                            Descuento = 0.0;
                        } else {
                            Descuento = Double.valueOf(s.toString());
                        }



                        if(!"GELVEZARA".equalsIgnoreCase(vEmpresa)
                                && !"SURTIMARCAS".equalsIgnoreCase(vEmpresa)
                                && !"BRILLO".equalsIgnoreCase(vEmpresa)
                                && "N".equalsIgnoreCase(mantisficc)){

                            if (Descuento > SDTProductos[0].Dct2no) {
                                Descuento = SDTProductos[0].Dct2no;
                                edit_dcto2.setText(String.valueOf(SDTProductos[0].Dct2no));
                            }
                        }

                        if (Descuento > SDTProductos[0].Dct2no
                                && "S".equalsIgnoreCase(mantisficc)
                                && SDTProductos[0].Dct2no > 0) {

                            Descuento = SDTProductos[0].Dct2no;
                            edit_dcto2.setText(String.valueOf(SDTProductos[0].Dct2no));
                        }


                        if (SDTProductos[0].Dct2 != Descuento) {
                            SDTProductos[0].Dct2 = Descuento;
                            SDTProductos[0].Calcular();
                            SDTProductos[0].Guardar(0);

                        }
                        Double preciounit =( SDTProductos[0].PrecioIva * (1-(SDTProductos[0].Dct1/100)) *  (1-(SDTProductos[0].Dct2/100))
                                *  (1-(SDTProductos[0].Dct3/100)) * (1-(SDTProductos[0].Dct4/100))* (1-(SDTProductos[0].Dct5/100)));

                        txtpredcto.setText(String.format( "%.2f",preciounit));
                        txt_subtotal.setText(String.format( "%.2f", SDTProductos[0].TotSubtotal));
                        txt_iva.setText(String.format( "%.2f", SDTProductos[0].TotIva));
                        txt_impoconsumo.setText(String.format( "%.2f",SDTProductos[0].TotImpoconsumo));
                        txt_total.setText(String.format( "%.2f", SDTProductos[0].Total));

                        if(SDTProductos[0].Dct2no-SDTProductos[0].Dct2 > 0){
                            edit_dcto2no.setText(String.valueOf((SDTProductos[0].Dct2no-SDTProductos[0].Dct2)));
                        }else{
                            edit_dcto2no.setText(String.valueOf((0)));
                        }
                    }
                });

                edit_dcto3.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.e("ENTRO32 DCTOP3",s.toString());
                        Double Descuento;
                        if (s.toString().trim().isEmpty()) {
                            Descuento = 0.0;
                        } else {
                            Descuento = Double.valueOf(s.toString());
                        }


                        if(!"GELVEZARA".equalsIgnoreCase(vEmpresa)
                                && !"MENTAHAIR".equalsIgnoreCase(vEmpresa)
                                && !"MENTAHAIRCOT".equalsIgnoreCase(vEmpresa)
                                && !"SURTIMARCAS".equalsIgnoreCase(vEmpresa)
                                && !"BRILLO".equalsIgnoreCase(vEmpresa)
                                && "N".equalsIgnoreCase(mantisficc)) {

                            if (Descuento > SDTProductos[0].Dct3no) {
                                Descuento = SDTProductos[0].Dct3no;
                                Log.e("ENTRO32 DCTOP3", String.valueOf(SDTProductos[0].Dct3no));
                                edit_dcto3.setText(String.valueOf(SDTProductos[0].Dct3no));
                            }
                        }

                        if (Descuento > SDTProductos[0].Dct3no
                                && "S".equalsIgnoreCase(mantisficc)
                                && SDTProductos[0].Dct3no > 0) {

                            Descuento = SDTProductos[0].Dct3no;
                            edit_dcto3.setText(String.valueOf(SDTProductos[0].Dct3no));
                        }





                        if (!SDTProductos[0].Dct3.equals(Descuento)  ){
                            SDTProductos[0].Dct3 = Descuento;
                            SDTProductos[0].Calcular();
                            SDTProductos[0].Guardar(0);
                        }
                        Double preciounit =( SDTProductos[0].PrecioIva * (1-(SDTProductos[0].Dct1/100)) *  (1-(SDTProductos[0].Dct2/100))
                                *  (1-(SDTProductos[0].Dct3/100)) * (1-(SDTProductos[0].Dct4/100))* (1-(SDTProductos[0].Dct5/100)));

                        txtpredcto.setText(String.format( "%.2f",preciounit));
                        txt_subtotal.setText(String.format( "%.2f", SDTProductos[0].TotSubtotal));
                        txt_iva.setText(String.format( "%.2f", SDTProductos[0].TotIva));
                        txt_impoconsumo.setText(String.format( "%.2f",SDTProductos[0].TotImpoconsumo));
                        txt_total.setText(String.format( "%.2f", SDTProductos[0].Total));
                        edit_dcto3no.setText(String.valueOf((SDTProductos[0].Dct3no-SDTProductos[0].Dct3)));
                        if(SDTProductos[0].Dct3no-SDTProductos[0].Dct3 > 0){
                            edit_dcto3no.setText(String.valueOf((SDTProductos[0].Dct3no-SDTProductos[0].Dct3)));
                        }else{
                            edit_dcto3no.setText(String.valueOf((0)));
                        }
                    }
                });

               /* edit_dcto4.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        Double Descuento;
                        if (s.toString().trim().isEmpty()) {
                            Descuento = 0.0;
                        } else {
                            Descuento = Double.valueOf(s.toString());
                        }


                        if (Descuento > SDTProductos[0].Dct4no && mantisficc.equalsIgnoreCase("S") && SDTProductos[0].Dct4no > 0) {
                            Descuento = SDTProductos[0].Dct4no;
                            edit_dcto4.setText(String.valueOf(SDTProductos[0].Dct4no));

                            Log.e("edit_dcto423 ",SDTProductos[0].Dct4.toString());
                            Log.e("edit_dcto423v ",SDTProductos[0].Dct4no.toString());
                            Log.e("edit_dcto423v ",String.valueOf(Descuento));
                        }



                        if (SDTProductos[0].Dct4 != Descuento) {
                            SDTProductos[0].Dct4 = Descuento;
                            SDTProductos[0].Calcular();
                            SDTProductos[0].Guardar(0);
                        }
                        Double preciounit =( SDTProductos[0].PrecioIva * (1-(SDTProductos[0].Dct1/100)) *  (1-(SDTProductos[0].Dct2/100))
                                *  (1-(SDTProductos[0].Dct3/100)) * (1-(SDTProductos[0].Dct4/100))* (1-(SDTProductos[0].Dct5/100)));
                        txtpredcto.setText(String.format( "%.2f",preciounit));
                        txt_subtotal.setText(String.format( "%.2f", SDTProductos[0].TotSubtotal));
                        txt_iva.setText(String.format( "%.2f", SDTProductos[0].TotIva));
                        txt_impoconsumo.setText(String.format( "%.2f",SDTProductos[0].TotImpoconsumo));
                        txt_total.setText(String.format( "%.2f", SDTProductos[0].Total));

                        edit_dcto4no.setText(String.valueOf((SDTProductos[0].Dct4no-SDTProductos[0].Dct4)));

                        if(SDTProductos[0].Dct4no-SDTProductos[0].Dct4 > 0){
                            edit_dcto4no.setText(String.valueOf((SDTProductos[0].Dct4no-SDTProductos[0].Dct4)));
                        }else{
                            edit_dcto4no.setText(String.valueOf((0)));
                        }
                    }
                });


                */
                edit_dcto5.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        Double Descuento;
                        if (s.toString().trim().isEmpty()) {
                            Descuento = 0.0;
                        } else {
                            Descuento = Double.valueOf(s.toString());
                        }



                        if(!"GELVEZARA".equalsIgnoreCase(vEmpresa)
                                && !"SURTIMARCAS".equalsIgnoreCase(vEmpresa)
                                && !"BRILLO".equalsIgnoreCase(vEmpresa)) {

                            if (Descuento > SDTProductos[0].Dct5no) {
                                Descuento = SDTProductos[0].Dct5no;
                                edit_dcto5.setText(String.valueOf(SDTProductos[0].Dct5no));
                            }
                        }

                        if (SDTProductos[0].Dct5 != Descuento) {
                            SDTProductos[0].Dct5 = Descuento;
                            SDTProductos[0].Calcular();
                            SDTProductos[0].Guardar(0);
                        }








                        Double preciounit =( SDTProductos[0].PrecioIva * (1-(SDTProductos[0].Dct1/100)) *  (1-(SDTProductos[0].Dct2/100))
                                *  (1-(SDTProductos[0].Dct3/100)) * (1-(SDTProductos[0].Dct4/100))* (1-(SDTProductos[0].Dct5/100)));

                        txtpredcto.setText(String.format( "%.2f",preciounit));
                        txt_subtotal.setText(String.format( "%.2f", SDTProductos[0].TotSubtotal));
                        txt_iva.setText(String.format( "%.2f", SDTProductos[0].TotIva));
                        txt_impoconsumo.setText(String.format( "%.2f",SDTProductos[0].TotImpoconsumo));
                        txt_total.setText(String.format( "%.2f", SDTProductos[0].Total));
                    }
                });

                edit_dcto6.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        Double Descuento;
                        if (s.toString().trim().isEmpty()) {
                            Descuento = 0.0;
                        } else {
                            Descuento = Double.valueOf(s.toString());
                        }
                        if (Descuento > SDTProductos[0].RentLis) {
                            Descuento = SDTProductos[0].RentLis;
                            edit_dcto6.setText(String.valueOf(SDTProductos[0].RentLis));
                        }
                        if (SDTProductos[0].Dct6 != Descuento) {
                            SDTProductos[0].Dct6 = Descuento;
                            SDTProductos[0].Calcular();
                            SDTProductos[0].Guardar(0);
                        }
                        Double preciounit =( SDTProductos[0].PrecioIva * (1-(SDTProductos[0].Dct1/100)) *  (1-(SDTProductos[0].Dct2/100))
                                *  (1-(SDTProductos[0].Dct3/100)) * (1-(SDTProductos[0].Dct4/100))* (1-(SDTProductos[0].Dct5/100)));

                        txtpredcto.setText(String.format( "%.2f",preciounit));
                        txt_subtotal.setText(String.format( "%.2f", SDTProductos[0].TotSubtotal));
                        txt_iva.setText(String.format( "%.2f", SDTProductos[0].TotIva));
                        txt_impoconsumo.setText(String.format( "%.2f",SDTProductos[0].TotImpoconsumo));
                        txt_total.setText(String.format( "%.2f", SDTProductos[0].Total));
                    }
                });




                edit_unidades.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        Double Unidades = 0.0;
                        if (s.toString().trim().isEmpty()) {

                            Unidades = 0.00;

                        } else {
                            Unidades = Double.valueOf(s.toString());
                        }


                        Runnable runaccion = new Runnable() {
                            @Override
                            public void run() {
                                vecesrep += 1;
                                //cambio.setText(String.valueOf(vecesrep));

                                String pendiente ="";
                                Double Unidades;
                                Double UnidadInf = 0.0;

                                Double Existencia = Double.valueOf(txt_existencia.getText().toString().replace(",","."));

                                if(Existencia < 0){
                                    Existencia = 0.0;
                                }


                                Double quitar = 0.00;

                                if (s.toString().trim().isEmpty()) {

                                    Unidades = 0.00;
                                    UnidadInf = 0.0;
                                    textUnidadesPen.setText("Solicitadas: "+String.valueOf(UnidadInf));
                                    SDTProductos[0].Unidadesinf = Double.valueOf(UnidadInf);
                                    SDTProductos[0].Unidades = Double.valueOf(Unidades);
                                    //SDTProductos[0].Calcular();
                                    SDTProductos[0].Guardar(1);
                                } else {
                                    Unidades = Double.valueOf(s.toString());
                                    Log.e("Unidades: ", s.toString());
                                    if (s.toString().trim().equals("0")) {
                                        edit_unidades.setText("");
                                    }

                                    if (s.toString().trim().equals("0.")) {
                                        edit_unidades.setText("");
                                    }





                                    quitar = Double.valueOf(s.toString());
                                }
                                Double CantidadTxt = Unidades + (SDTProductos[0].Cajas * SDTProductos[0].Embalaje);
                                Double preciounit =  0.0;
                                antunidadescre =  Unidades;

                                if("S".equalsIgnoreCase(SDTProductos[0].NotaInv) ||
                                       "S".equalsIgnoreCase( SDTProductos[0].NotaCar)){

                                    SDTProductos[0].Dct4 = 0.0;
                                    SDTProductos[0].Dct4no = 0.0;

                                        /*if (CantidadTxt < Existencia) {
                                            UnidadInf = 0.0;
                                            pendiente = "0";
                                        }*/
                                }else{
                                    if (CantidadTxt > Existencia && CantidadTxt > 0) {
                                        UnidadInf = quitar - Existencia;
                                        if (UnidadInf < 0) {
                                            UnidadInf = UnidadInf*-1;
                                        }else{
                                            UnidadInf = Unidades;
                                        }

                                        /*Log.e("primer unidadesinf",String.valueOf(unidadesinf));
                                        Log.e("primer Unidades",String.valueOf(Unidades));*/
                                       // Unidades = Existencia;
                                        pendiente =String.valueOf(UnidadInf);
                                    }else{
                                        if (CantidadTxt < Existencia) {
                                            UnidadInf = 0.0;
                                            pendiente = "0";
                                        }
                                    }
                                }


                              /*   if(SDTProductos[0].NotaCar.equalsIgnoreCase("S")) {
                                    if ((valortotales - (Unidades * SDTProductos[0].Precio)) < 0) {
                                        Unidades = antunidades;
                                        UnidadInf = antunidadesinf;
                                        Toast.makeText(EditarCantidad.this, "Credito mayor al valor del pedido, genere una nota credito", Toast.LENGTH_LONG).show();
                                    }
                                }*/



                                SDTProductos[0].Unidadesinf = Double.valueOf(UnidadInf);
                                SDTProductos[0].Unidades = Double.valueOf(Unidades);
                                SDTProductos[0].Calcular();
                                SDTProductos[0].Guardar(55);
                                Log.e("edit_dcto423edit ",SDTProductos[0].Dct4.toString());
                                edit_dcto1.setText(SDTProductos[0].Dct1.toString());
                                edit_dcto2.setText(SDTProductos[0].Dct2.toString());
                                edit_dcto3.setText(SDTProductos[0].Dct3.toString());
                                edit_dcto4.setText(SDTProductos[0].Dct4.toString());
                                edit_dcto5.setText(SDTProductos[0].Dct5.toString());
                                edit_dcto6.setText(SDTProductos[0].Dct6.toString());
                                edit_dcto1no.setText(String.valueOf((SDTProductos[0].Dct1no - SDTProductos[0].Dct1)));
                                edit_dcto2no.setText(String.valueOf((SDTProductos[0].Dct2no - SDTProductos[0].Dct2)));
                                edit_dcto3no.setText(String.valueOf((SDTProductos[0].Dct3no - SDTProductos[0].Dct3)));
                                edit_dcto4no.setText(String.valueOf((SDTProductos[0].Dct4no - SDTProductos[0].Dct4)));

                                preciounit =   (SDTProductos[0].PrecioIva * (1 - (SDTProductos[0].Dct1 / 100)) * (1 - (SDTProductos[0].Dct2 / 100))
                                        * (1 - (SDTProductos[0].Dct3 / 100)) * (1 - (SDTProductos[0].Dct4 / 100)) * (1 - (SDTProductos[0].Dct5 / 100)));

                                txtpredcto.setText(String.format( "%.2f",preciounit));
                                txt_subtotal.setText(String.format( "%.2f", SDTProductos[0].TotSubtotal));
                                txt_iva.setText(String.format( "%.2f", SDTProductos[0].TotIva));
                                txt_impoconsumo.setText(String.format( "%.2f",SDTProductos[0].TotImpoconsumo));
                                txt_total.setText(String.format( "%.2f", SDTProductos[0].Total));
                                textUnidadesPen.setText("Solicitadas:"+pendiente);





                                textTitDesc.setText(SDTProductos[0].TxtDescuentos);

                                textTitBon.setText(SDTProductos[0].TxtBonificado);
                                tituloboncant.setText("Bonificados ("+SDTProductos[0].totalBon+")");

                                textCantBon.setText(SDTProductos[0].TxtBonificadoUni);
                                textCantBonCaj.setText(SDTProductos[0].TxtBonificadoCaj);
                                if ("S".equalsIgnoreCase(SDTProductos[0].checkmax)) {
                                    textTitBon.setBackgroundColor(Color.parseColor("#ef9a9a"));
                                    maxof.setVisibility(View.VISIBLE);
                                } else {
                                    textTitBon.setBackgroundColor(Color.parseColor("#F7F8CD"));
                                    maxof.setVisibility(View.GONE);
                                }
                                // }

                            }
                        };


                        handler.removeCallbacksAndMessages(null);
                        handler.postDelayed(runaccion, 300);
                        if (vecesrep > 1) {
                            handler.removeCallbacksAndMessages(null);
                        }


                    }



                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        vecesrep = 0;
                        handler.removeCallbacksAndMessages(null);
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        handler.removeCallbacksAndMessages(null);

                    }
                });



            }










        }catch (Exception e){
            AlertDialog.Builder Alerta = new AlertDialog.Builder(this);
            Alerta.setMessage("Error "+e.getMessage()+ e.getCause()+e.getLocalizedMessage());
            Alerta.setTitle("Alerta");
            Alerta.setPositiveButton("OK", null);
            Alerta.setCancelable(true);
            Alerta.create().show();
            int hh=0;
        }

    }

    private Double traerpreciobd(String preartcodSeleccionado) {
        Double preciopresentacion = 0.0;




        return preciopresentacion;
    }


    public Bitmap generarCodigoBarras(String texto) {

        try {
            MultiFormatWriter writer = new MultiFormatWriter();

            BitMatrix bitMatrix = writer.encode(
                    texto,
                    BarcodeFormat.CODE_128, // tipo de código
                    600,
                    100
            );

            BarcodeEncoder encoder = new BarcodeEncoder();
            return encoder.createBitmap(bitMatrix);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private void cerrarPanel() {
        Intent data = new Intent();
        data.putExtra("posicion", Extras.getInt("position"));
        if(antunidadesinf > 0 && antunidades == 0){

            data.putExtra("Cantidad", antunidadesinf);
        }else{
            data.putExtra("Cantidad", antunidades);
        }
        data.putExtra("PrecioNeto", antPrecioNeto);
        data.putExtra("NotaInv", antNotaInv);
        data.putExtra("Credito", "N");
        setResult(RESULT_OK, data);
        SDTProductos[0].Unidades = antunidades;
        SDTProductos[0].Unidadesinf= antunidadesinf;
        SDTProductos[0].NotaInv = antNotaInv;
        SDTProductos[0].NotaCar = antNotaCar;
        SDTProductos[0].Guardar(1);
        finish();
        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {



        Intent data = new Intent();
        data.putExtra("posicion", Extras.getInt("position"));
        if(antunidadesinf > 0 && antunidades == 0){

            data.putExtra("Cantidad", antunidadesinf);
        }else{
            data.putExtra("Cantidad", antunidades);
        }

        data.putExtra("PrecioNeto", antPrecioNeto);
        data.putExtra("NotaInv", antNotaInv);
        data.putExtra("Credito", "N");
        setResult(RESULT_OK, data);

        SDTProductos[0].Unidades = antunidades;
        SDTProductos[0].Unidadesinf= antunidadesinf;
        SDTProductos[0].NotaInv = antNotaInv;
        SDTProductos[0].NotaCar = antNotaCar;
        SDTProductos[0].Guardar(1);
        finish();
        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_right);
    }

    public void cerrar(View v){
        Intent data = new Intent();
        data.putExtra("posicion", Extras.getInt("position"));
        data.putExtra("Cantidad", antunidades);
        data.putExtra("PrecioNeto", antPrecioNeto);
        data.putExtra("NotaInv", antNotaInv);
        data.putExtra("Credito", "N");

        setResult(RESULT_OK, data);
        SDTProductos[0].Unidades = antunidades;
        SDTProductos[0].Unidadesinf= antunidadesinf;
        SDTProductos[0].NotaInv = antNotaInv;
        SDTProductos[0].NotaCar = antNotaCar;
        SDTProductos[0].Guardar(1);
        finish();
        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_right);
    }


    public Double precioesp(String NitSec,String Artsec,Double precioArt){
        Double precioNu = 0.0;
      /*  BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
        String consulta = "select precioesp from PreciosEspeciales where peNitSec = '"+NitSec+"' and peArtSec = '"+Artsec+"'";
        Cursor cursor = BaseDeDatos.getWritableDatabase().rawQuery(consulta, null);
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            precioNu = cursor.getDouble(0);
        }else{
            precioNu = precioArt;
        }
*/
        precioNu = precioArt;
        return precioNu;
    }

    public void descuentos(){
        TextView descuentostext = findViewById(R.id.descuentostext);
        TextView Historialtext = findViewById(R.id.Historialtext);

        Presentacion item = (Presentacion) spinner_presentacion.getSelectedItem();
        String preartcodSeleccionado = item.getPreartcod();


        String Artsec = Extras.getString("artsec");
        String Consultades = "select DescLinDesUni, DescLinHasUni, DescLinPorDesLin from descuentos where ARTICULOS like '%,"+Artsec+preartcodSeleccionado+",%' ";
        String consultaHistorial = "select KarUni,KarPrePub, Fecha from ClientesDevoluciones where ArtSec = "+Artsec+" and NitSec = '"+nitsec+"' and CliSec = "+clisec+" ";

        Cursor cursorartdes = BaseDeDatos.getWritableDatabase().rawQuery(Consultades, null);
        cursorartdes.moveToFirst();
        String ventasDcto = "";
        if(cursorartdes.getCount() > 0){
            cursorartdes.moveToFirst();
            do{
                Double preciounit =( SDTProductos[0].PrecioIva * (1-(cursorartdes.getDouble(2)/100)) );

                ventasDcto += "($"+ String.format("%.2f",preciounit) +"): desde: "+cursorartdes.getInt(0)+" - Hasta: "+cursorartdes.getInt(1)+"\n";

            } while (cursorartdes.moveToNext());

        }else{


        }
        cursorartdes.close();


        Cursor cursorhistorial = BaseDeDatos.getWritableDatabase().rawQuery(consultaHistorial, null);
        cursorhistorial.moveToFirst();
        String historial = "";
        if(cursorhistorial.getCount() > 0){
            cursorhistorial.moveToFirst();
            do{

                historial += "("+ cursorhistorial.getString(2)+"): "+cursorhistorial.getInt(0)+" @: "+cursorhistorial.getDouble(1)+"\n";

            } while (cursorhistorial.moveToNext());

        }else{


        }




        descuentostext.setVisibility(View.VISIBLE);
        Historialtext.setVisibility(View.VISIBLE);
        if(historial.isEmpty()){
            Historialtext.setVisibility(View.GONE);
        }
        SDTProductos[0].Historico = historial;
        Historialtext.setText(historial);
        if(ventasDcto.isEmpty()){
            descuentostext.setVisibility(View.GONE);
        }
        descuentostext.setText(ventasDcto);
    }

    public double  totales(String Prefijo, String nitsec, int clisec, Context context){
        double carritoTotal = 0.0;
        try {
            Time time = new Time();
            time.setToNow();

            GestorPedidos gestorpedidos = new GestorPedidos();
            SDTResumenPedidos sdtResumenPedidos = gestorpedidos.TotalesPedido(context, Prefijo, nitsec, clisec,"","","");

            BaseDatos BaseDeDatos;
            BaseDeDatos = new BaseDatos(context, "MantisMovil", null, 5);

            String SelectClienteImpactado = "Select count(*) as total,    sum(\n" +
                    "        precio\n" +
                    "        * (1-(pordesc/100))\n" +
                    "        * (1-(pordesc2/100))\n" +
                    "        * (1-(pordesc3/100))\n" +
                    "        * (1-(pordesc4/100))\n" +
                    "        * (1-(pordesc5/100))\n" +
                    "        * (1-(pordesc6/100))\n" +
                    "        * cant\n" +
                    "    ) as valor  " +
                    " from pedido p where   cant+ifnull(cantinf,0)<>0 and prefijo='" + Prefijo+ "' and p.nitsec='" + nitsec + "' and p.clisec='" + clisec + "' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "" +
                    " ";

            try{
                Cursor xClientes = BaseDeDatos.getWritableDatabase().rawQuery(SelectClienteImpactado, null);
                if (xClientes.getCount() > 0) {
                    xClientes.moveToFirst();
                    do {
                        carritoTotal =xClientes.getDouble(1);
                    } while (xClientes.moveToNext());
                }
            }catch (Exception e){
                Log.e("EROOOR",e.toString());
            }


        }catch (Exception e){
            Integer error=0;
        }
        return  carritoTotal;
    }



    public List<SDTProductosBon> cargarBonificados(String ArtSec,int lisprecod){
        Time time = new Time();
        time.setToNow();
        List<SDTProductosBon> listaBon = new ArrayList<>();
        SDTProductosBon[] SDTProductosBon ;
        Cursor cursor = null;

        Log.e("listapreciobonificado ",String.valueOf(lisprecod));

                String consulta = "select * from (select prefijo,MovParPremSec,MovParPremArtSec,artcod,artnom,MovParPremCant," +
                        " ifnull(mp.BonParBonPreArtCod,a.PreArtcod) PreArtcod, PreArtNom,bd.PARBONAPLCANGEN,MovParPremCantGen,MovParPremApli," +
                        "  MovParPremSecLin,PrePrefijval," +
                        "  ifnull((select ParBonDes from BonificacionesProducto where BonProSec=MovParPremSec LIMIT 1),'') ParBonDes   ,  " +
                " ifnull((select bontipo from BonificacionesProducto where BonProSec=MovParPremSec LIMIT 1),'X') tipo " +
                " from MovParPrem mp" +
                " left join articulos a on a.artsec=MovParPremArtSec " +
                        " left join BonificacionesProductoDet bd on bd.BonProSec=MovParPremSec and BonProArtSec = MovParPremArtSecOri " +
                " left join articulospresentacion ap" +
                        " on  ap.ArtSec=MovParPremArtSec and ap.PreArtcod = ifnull(mp.BonParBonPreArtCod,a.PreArtcod) " +
                        "and ap.LisPrecod =" + lisprecod + " " +
                " where bd.PARBONAPLCANGEN = 'S' and MovParNitSec='" + nitsec + "' and " +
                "  MovParCliSec=" + clisec + " and MovParPremAno=" + time.year + " and " +
                "  MovParPremMes=" + (time.month + 1) + " and " +
                "  MovParPremDia=" + time.monthDay + "  and " +
                "  prefijo='" + prefijo+ "'" +
                "  and MovParPremArtSecOri='"+ArtSec+"' ) jj order by ParBonDes, MovParPremSec desc ";

        Log.e("consultaconsultaconsultaconsulta ",String.valueOf(consulta));
        try{
        try{
            cursor = BaseDeDatos.getWritableDatabase().rawQuery(consulta, null); //order by nombre
            SDTProductosBon = new SDTProductosBon[cursor.getCount()];
        }catch (Exception e){
            Log.e("Errore",e.toString());
        }
        if (cursor.getCount() > 0) {
            int vuelta = 0;
            double precioNu = 0;
            cursor.moveToFirst();
            do {
                //PARBONAPLCANGEN 8
                SDTProductosBon SDTProductosItem = new SDTProductosBon();
                SDTProductosItem.ArtSec = cursor.getString(2);
                SDTProductosItem.ArtCod = cursor.getString(3);
                SDTProductosItem.ArtNom = cursor.getString(4);
                SDTProductosItem.MovParPremSec = cursor.getInt(1);
                SDTProductosItem.PreArtcod =  cursor.getInt(6);
                SDTProductosItem.PreArtNom = cursor.getString(7);
                SDTProductosItem.esgeneral = cursor.getString(8);
                SDTProductosItem.Cantidad = cursor.getInt(5);
                SDTProductosItem.CantidadGen =  cursor.getInt(9);
                SDTProductosItem.aplicadobon =  cursor.getString(10);
                SDTProductosItem.DesoBonEscSec = cursor.getInt(11);
                SDTProductosItem.PrePrefijval = cursor.getDouble(12);
                SDTProductosItem.BonDesc = cursor.getString(13);

                Log.d("BONsdtcargar", "Articulo: " + cursor.getString(3) +
                        " PreArtcod: " + cursor.getInt(6) +
                        " PrePrefijval: " + cursor.getDouble(12)+ " Cantidad : "+ cursor.getString(5));
                listaBon.add(SDTProductosItem);


            } while (cursor.moveToNext());
            cursor.close();
        }
        }catch (Exception e){
            Log.e("Errore",e.toString());
        }

return listaBon;

    }

    public void mostrarPopup(View anchorView) {

        View fondo = findViewById(R.id.fondoOscuro);
        fondo.setVisibility(View.VISIBLE);
        List<SDTProductosBon> listaBon = cargarBonificados(Extras.getString("artsec"),Extras.getInt("lisprecod"));


        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.wpeditarbonificado, null);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
        );

        popupWindow.setElevation(10);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setFocusable(true);
        // Mostrar debajo del botón
        //popupWindow.showAsDropDown(anchorView);
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);


        ListView listView = popupView.findViewById(R.id.bonificadoslist);
        AdapterProductosBon adapter = new AdapterProductosBon(this, listaBon);
        listView.setAdapter(adapter);



        // Botones del popup
     /*   Button btn1 = popupView.findViewById(R.id.btnOpcion1);
        Button btn2 = popupView.findViewById(R.id.btnOpcion2);

        btn1.setOnClickListener(v -> {
            Toast.makeText(this, "Opción 1", Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
        });

        btn2.setOnClickListener(v -> {
            Toast.makeText(this, "Opción 2", Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
        });*/

        Button btnGuardar = popupView.findViewById(R.id.btnguardarbon);

        btnGuardar.setOnClickListener(v -> {
            int tieneprincipal = 0;
            Map<Integer, List<SDTProductosBon>> grupos = new HashMap<>();

            // 1. Agrupar por MovParPremSec
            for (SDTProductosBon item : listaBon) {
                if (!grupos.containsKey(item.MovParPremSec)) {
                    grupos.put(item.MovParPremSec, new ArrayList<>());
                }
                grupos.get(item.MovParPremSec).add(item);
            }

            // 2. Recorrer grupos
            for (Integer grupo : grupos.keySet()) {

                List<SDTProductosBon> itemsGrupo = grupos.get(grupo);
                boolean tieneSeleccionado = false;

                // Verificar si alguno está seleccionado
                for (SDTProductosBon item : itemsGrupo) {
                    if ("S".equals(item.aplicadobon)) {
                        tieneSeleccionado = true;
                        break;
                    }
                }

                // 3. Si el grupo tiene seleccionado → guardar TODOS los del grupo
                if (tieneSeleccionado) {
                    for (SDTProductosBon item : itemsGrupo) {
                        actualizarBonificado(item);
                    }
                }
            }


            /*for (SDTProductosBon item : listaBon) {

                if ("S".equals(item.aplicadobon)) {
                    // Este es el seleccionado del grupo
                    Log.d("BON", "Articulo: " + item.ArtCod +
                            " Cantidad: " + item.Cantidad +
                            " MovParPremSec: " + item.MovParPremSec);
                }

                // Aquí puedes guardar en BD o enviar
                actualizarBonificado(item);
            }*/
            antunidades = SDTProductos[0].Unidades;
            antunidadesinf = SDTProductos[0].Unidadesinf;
            antNotaInv = SDTProductos[0].NotaInv  ;
            antNotaCar =  SDTProductos[0].NotaCar ;
            SDTProductos[0].ActualizarDescuentos();
            SDTProductos[0].Guardar(1);

            textTitBon.setText(SDTProductos[0].TxtBonificado);
            tituloboncant.setText("Bonificados ("+SDTProductos[0].totalBon+")");
            popupWindow.dismiss();



        });


        popupWindow.setOnDismissListener(() -> {
            fondo.setVisibility(View.GONE);
        });


    }


    public void actualizarBonificado(SDTProductosBon item) {
        // Ejemplo
        Time time = new Time();
        time.setToNow();

        Log.d("BONupdate", "Articulo: " + item.ArtCod +
                " MovParPremSec: " + item.MovParPremSec +
                " DesoBonEscSec: " + item.DesoBonEscSec+
                " MovParPremCant: "+item.Cantidad);


        String sql = "UPDATE MovParPrem SET MovParPremApli = '"+item.aplicadobon+"', MovParPremCant = '"+item.Cantidad+"' " +
                " WHERE MovParPremSec = "+item.MovParPremSec+" " +
                " AND MovParPremSecLin = "+item.DesoBonEscSec+" " +
                " AND MovParNitSec='" + nitsec + "' " +
                "  and MovParCliSec=" + clisec + " " +
                "  and MovParPremAno=" + time.year + "  " +
                "  and MovParPremMes=" + (time.month + 1) + "  " +
                "  and MovParPremDia=" + time.monthDay + "   " +
                "  and prefijo='" + prefijo+ "'" +
                "  ";
        BaseDeDatos.getWritableDatabase().execSQL(sql);

        //SQLiteDatabase db = BaseDeDatos.getWritableDatabase();

    }






}
