package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

public class EditarCantidadInf extends AppCompatActivity {

    Bundle Extras;
    private Handler handler;
    private  Runnable runnable;
    String nitsec;
    Integer clisec;
    Integer plazo;
    Boolean userIsInteracting = true;
    String prefijo, plazoNom;
    String invfamcod;
    SDTProductos[] SDTProductos ;
    TextView txt_subtotal;
    TextView txt_iva;
    TextView txt_impoconsumo;
    TextView txt_total;
    EditText editUnidadIndu,editUnidadInf;
    TextView textTitBon;
    SDTArticuloinf[] SDTArticuloinf;
    TextView textTitDesc;
    ListViewAdapterCantinf ListViewAdapterCantinf;
    TextView cambio;
    TextView textCantBon,maxof;
    TextView textCantBonCaj;
    LinearLayout informativocontent;
    ListView listacantidades;
    String vEmpresa = "" ;
    int vecesrep =0;
   int ultllave = 0;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_cantidadinf);
        getSupportActionBar().hide();
        informativocontent = findViewById(R.id.informativocontent);
    //    DisplayMetrics dm=new DisplayMetrics();
      //  getWindowManager().getDefaultDisplay().getMetrics(dm);
       handler = new Handler();

        getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.TrasparenteGris));
     //   getWindow().setLayout(dm.widthPixels*90/100,dm.heightPixels*90/100);

        final TextView txt_existencia = (TextView) findViewById(R.id.txt_existencia);

        try {
            txt_subtotal = (TextView) findViewById(R.id.txt_abonoori);
            txt_iva = (TextView) findViewById(R.id.txt_retencion);
            editUnidadIndu = findViewById(R.id.editUnidadIndu);
            editUnidadInf = findViewById(R.id.editUnidadInf);
            txt_impoconsumo = (TextView) findViewById(R.id.txt_retencionica);
            txt_total = (TextView) findViewById(R.id.txt_neto);
            textTitBon = (TextView) findViewById(R.id.textTitBon);
            maxof = (TextView) findViewById(R.id.maxof);
            textTitDesc = (TextView) findViewById(R.id.textTitDesc);
            listacantidades  = findViewById(R.id.listacantidades);
            textCantBon = (TextView) findViewById(R.id.textCantBon);
            textCantBonCaj = (TextView) findViewById(R.id.textCantBon2);
           Button agregarlinea = findViewById(R.id.agregarlinea);
            Extras = this.getIntent().getExtras();

            invfamcod = Extras.getString("invfamcod");
            String invsubgrucod = Extras.getString("invsubgrucod");
            String invgrucod = Extras.getString("invgrucod");
            nitsec = Extras.getString("nitsec");
            clisec = Extras.getInt("clisec");
            plazo = Extras.getInt("plazo");
            plazoNom = Extras.getString("plazoNom");
            Integer lisprecod = Extras.getInt("lisprecod");
            prefijo = Extras.getString("prefijo");
            Integer bodcod = Extras.getInt("bodega");
            Integer bodcodbon = 0;

            String artsec = Extras.getString("artsec");
            String clisiniva = "S";
            int perfil=99;

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



            final BaseDatos BaseDeDatos;
            BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
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

            if(conbd.sincronizalinea.equalsIgnoreCase("S")) {
                informativocontent.setVisibility(View.VISIBLE);
            }

            try {
               if (!prefijo.equalsIgnoreCase("NC") && conbd.ActulizaOnline.equalsIgnoreCase("S") ) {  //
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
            if (prefijo.equalsIgnoreCase("NC")){
                Exist = "KarUni";
                Precio = "KarPrePub";
                PrecioPorRen= "0 ";
            }else{
                Exist = "Exist";
                Precio = "Precio" + lisprecod;
                PrecioPorRen = "precio" + lisprecod+"PorRen";
            }
            if(prefijo.equalsIgnoreCase("V15") & vEmpresa.trim().equalsIgnoreCase("IBANEZ")){
                Exist = "ExistFec";
            }

            Time time = new Time();
            time.setToNow();

            //"left join ClientesDevoluciones d on d.nitsec='" + nitsec + "' and d.clisec='" + clisec + "' and d.artsec=a.artsec " +
            String Consulta = "";
            if(vEmpresa.equalsIgnoreCase("SURTIMARCAS")){
                Consulta = "select a.ArtSec,ArtCod,ArtNom," +
                        " case lgs.LisPreCod when 1 then Precio1 when 2 then Precio2 when 3 then Precio3 when 4 then Precio4 when 5 then Precio5 when 6 then Precio6 when 7 then Precio7 when 8 then Precio8 when 9 then Precio9  " +
                        " else " + Precio + " end precio,Desc1,(ifNULL(Desc2,0)+ifNULL((select PerCliDetDes1 from PerfilClientesClase pc where pc.ClaArtCod=a.ClaArtCod and pc.PerCliCod="+perfil+"),0)) Desc2," + Exist + " Exist,artemb,case  cliiva when 'S' then ParConIva else 0 end ParConIva,ArtValImp,0 cant,0 cantcaj,InvGruNom,artmednomcom,0 KarUni,0 KarPrePub,ifNULL(Desc1,0) pordesc,ifNULL(Desc2,0) pordesc2,ifNULL(Desc3,0) pordesc3,ifNULL(Desc4,0) pordesc4,0.0 pordesc5,0.0 pordesc6,ArtRen,ArtLim,PrePreFijCosPro," + PrecioPorRen + " PrecioPorRen, " +
                        " case lgs.LisPreCod when 1 then 1 when 2 then 2 when 3 then 3 when 4 then 4 when 5 then 5 when 6 then 6 when 7 then 7 when 8 then 8 when 9 then 9  else " + lisprecod + " end listafin, ifnull((select sum(ArtExiAct) from ArticulosExi where Artsec = a.ArtSec and (ArtBodCod = "+bodcod+" or ArtBodCod = "+bodcodbon+")),0) ArtExiAct ,GruCheck,SubCheck,FamCheck, a.invgrucod " +
                        " from Articulos a " +
                        " left join Clientes cc on cc.nitsec='" + nitsec + "' and cc.clisec='" + clisec + "'  " +
                        " left join ListaPorGrupoSubgrupo lgs on lgs.nitsec='" + nitsec + "' and lgs.clisec='" + clisec + "' and  ((lgs.invgrucod=a.invgrucod and lgs.invsubgrucod =a.invsubgrucod ) or (lgs.invgrucod=a.invgrucod and lgs.invsubgrucod ='0')) " +
                        " where rtrim(ltrim(a.artsec))='"+artsec.trim()+"' order by Exist desc ";
            }else{
                Consulta = "select a.ArtSec,ArtCod,ArtNom," +
                        " case lgs.LisPreCod when 1 then Precio1 when 2 then Precio2 when 3 then Precio3 when 4 then Precio4 when 5 then Precio5 when 6 then Precio6 when 7 then Precio7 when 8 then Precio8 when 9 then Precio9  " +
                        " else " + Precio + " end precio,Desc1,(ifNULL(Desc2,0)+ifNULL((select PerCliDetDes1 from PerfilClientesClase pc where pc.ClaArtCod=a.ClaArtCod and pc.PerCliCod="+perfil+"),0)) Desc2," + Exist + " Exist,artemb,case  cliiva when 'S' then ParConIva else 0 end ParConIva,ArtValImp,0 cant,0 cantcaj,InvGruNom,artmednomcom,0 KarUni,0 KarPrePub,ifNULL(Desc1,0) pordesc,ifNULL(Desc2,0) pordesc2,ifNULL(Desc3,0) pordesc3,ifNULL(Desc4,0) pordesc4,0.0 pordesc5,0.0 pordesc6,ArtRen,ArtLim,PrePreFijCosPro," + PrecioPorRen + " PrecioPorRen, " +
                        " case lgs.LisPreCod when 1 then 1 when 2 then 2 when 3 then 3 when 4 then 4 when 5 then 5 when 6 then 6 when 7 then 7 when 8 then 8 when 9 then 9  else " + lisprecod + " end listafin, ifnull(ArtExiAct,0) ArtExiAct ,ifnull(GruCheck,'N')GruCheck,ifnull(SubCheck,'N')SubCheck,ifnull(FamCheck,'N')FamCheck, a.invgrucod " +
                        " from Articulos a " +
                        " left join Clientes cc on cc.nitsec='" + nitsec + "' and cc.clisec='" + clisec + "'  " +
                        "left join ArticulosExi ex on ex.ArtSec=a.ArtSec and ArtBodCod = "+bodcod+" " +
                        " left join ListaPorGrupoSubgrupo lgs on lgs.nitsec='" + nitsec + "' and lgs.clisec='" + clisec + "' and  ((lgs.invgrucod=a.invgrucod and lgs.invsubgrucod =a.invsubgrucod ) or (lgs.invgrucod=a.invgrucod and lgs.invsubgrucod ='0')) " +
                        " where rtrim(ltrim(a.artsec))='"+artsec.trim()+"' order by Exist desc ";
            }


            String ConsultaPed="select cant,cantcaj,pordesc,pordesc2,pordesc3,pordesc4,pordesc5,pordesc6,confemp,confprov,confvend,pordescNo,pordesc2no,pordesc3no,pordesc4no,pordesc5no,pordesc6no " +
                    " ,ifNULL(pordescCero,0),ifNULL(pordesc2Cero,0),ifNULL(pordesc3Cero,0),ifNULL(pordesc4Cero,0),ifNULL(pordesc5Cero,0),ifNULL(cantinf,0),ifNULL(cantcajinf,0) "  +
                    "from pedido p where prefijo='" + prefijo + "' and p.nitsec='" + nitsec + "' and p.clisec='" + clisec + "' and p.artsec='"+artsec+"' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " ";

            Cursor cursor = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null); //order by nombre
            Cursor cursorped = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaPed, null); //order by nombre
            String checkdif = "N";
            SDTProductos = new SDTProductos[cursor.getCount()];
            if (cursor.getCount() > 0) {

                int vuelta = 0;
                double precioNu = 0;
                cursor.moveToFirst();
                do {

                    precioNu = 0;
                    try{
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
                    }



                    SDTProductos SDTProductosItem = new SDTProductos();
                    SDTProductosItem.ArtSec = cursor.getString(0);
                    SDTProductosItem.Codigo = cursor.getString(1);
                    SDTProductosItem.Nombre = cursor.getString(2) + " (" + cursor.getString(12) + ")";
                    SDTProductosItem.Nombrecomercial = cursor.getString(13);

                    Double precio = precioNu;

                    if(clisiniva.equalsIgnoreCase("S")){//Nuevo andres
                        SDTProductosItem.Iva = cursor.getInt(8);
                    }else{
                        SDTProductosItem.Iva = 0;
                    }

                    SDTProductosItem.Precio = precio;
                    SDTProductosItem.PrecioIva = precio * (1 + (Double.valueOf(cursor.getInt(8)) / 100));
                    SDTProductosItem.PrecioNeto = (  (precio * (1 + (Double.valueOf(cursor.getInt(8)) / 100))) *(1-(cursor.getDouble(16)/100)) *(1-(cursor.getDouble(17)/100)) *(1-(cursor.getDouble(18)/100)) *(1-(cursor.getDouble(19)/100)) *(1-(cursor.getDouble(20)/100)) *(1-(cursor.getDouble(21)/100))   )   +cursor.getDouble(9);
                    SDTProductosItem.ConfEmp = 0.0;// cursor.getDouble(0);
                    SDTProductosItem.ConfProv = 0.0; // cursor.getDouble(0);
                    SDTProductosItem.Existencia = cursor.getDouble(27);
                    SDTProductosItem.bodcod = bodcod;
                    SDTProductosItem.Embalaje = cursor.getInt(7);
                    SDTProductosItem.ArtRen = cursor.getDouble(22);
                    SDTProductosItem.ArtLim = cursor.getDouble(23);
                    SDTProductosItem.CostoPro = cursor.getDouble(24);
                    SDTProductosItem.RentLis = cursor.getDouble(25);
                    SDTProductosItem.LisPreCod = cursor.getInt(26);

                    if(cursor.getString(30).equalsIgnoreCase("S")){
                        checkdif = "S";
                    } else if (cursor.getString(29).equalsIgnoreCase("S")) {
                        checkdif = "S";
                    } else if (cursor.getString(28).equalsIgnoreCase("S")) {
                        checkdif = "S";
                    }




                    SDTProductosItem.Impoconsumo = cursor.getDouble(9);

                    if (traerdcto==1 || vEmpresa.trim().equalsIgnoreCase("PROMEFAR" ))  {
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

                            SDTProductosItem.Cajas = cursorped.getInt(1); ; // cursor.getInt(0);
                            SDTProductosItem.Unidades = cursorped.getDouble(0); //cursor.getDouble(0);

                                SDTProductosItem.Dct1 = cursorped.getDouble(2);


                                SDTProductosItem.Dct2 = cursorped.getDouble(3);



                            SDTProductosItem.Dct3 = cursorped.getDouble(4);
                            SDTProductosItem.Dct4 = cursorped.getDouble(5);
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

                            Double pConfVend=cursorped.getDouble(10);
                            SDTProductosItem.ConfVend =pConfVend ;
                        } while (cursorped.moveToNext());
                        cursorped.close();
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

                    vuelta = vuelta + 1;
                } while (cursor.moveToNext());
                cursor.close();
            }


            txt_subtotal.setText(String.format("%,d", SDTProductos[0].TotSubtotal.intValue()));
            txt_iva.setText(String.format("%,d", SDTProductos[0].TotIva.intValue()));
            txt_impoconsumo.setText(String.format("%,d", SDTProductos[0].TotImpoconsumo.intValue()));
            txt_total.setText(String.format("%,d", SDTProductos[0].Total.intValue()));
/*            Button btn_guardar = (Button) findViewById(R.id.btn_guardar);
            btn_guardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent();
                    i.putExtra("PRECIO", "1520.0");
                    setResult(Activity.RESULT_OK, i);
                    finish();
                }
            });*/

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
            TextView txt_nombrecomercial = (TextView) findViewById(R.id.txt_nombrecomercial);
            final TextView txt_precio = (TextView) findViewById(R.id.txt_precio);
            final  TextView txtpredcto = (TextView) findViewById(R.id.txtpredcto);
            final TextView edit_precio = (TextView) findViewById(R.id.edit_precio);
            edit_precio.setVisibility(View.GONE);
            TextView txt_precioneto = (TextView) findViewById(R.id.txt_precioneto);
            TextView txt_confemp = (TextView) findViewById(R.id.txt_confemp);
            TextView txt_confprov = (TextView) findViewById(R.id.txt_confprov);
            TextView txt_confvend = (TextView) findViewById(R.id.txt_confvend);
            TextView txt_embalaje = (TextView) findViewById(R.id.txt_embalaje);

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
            final TextView edit_cajas = (TextView) findViewById(R.id.edit_cajas);
            final TextView edit_unidades = (TextView) findViewById(R.id.edit_unidades);
            final  TextView unidadesinf = (TextView) findViewById(R.id.cantinf);
            final  TextView cajasinf  = (TextView) findViewById(R.id.cantcajinf);
            final LinearLayout liner_conf = (LinearLayout) findViewById(R.id.liner_conf);

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


            if (ManejaDctoV2.equalsIgnoreCase("S")==false) {
                toggle.setVisibility(View.GONE);
                toggle2.setVisibility(View.GONE);
                toggle3.setVisibility(View.GONE);
                toggle4.setVisibility(View.GONE);
                toggle5.setVisibility(View.GONE);
                textView58.setVisibility(View.GONE);
            }
            if(mantisficc.equalsIgnoreCase("S")){
                toggle.setVisibility(View.VISIBLE);
                toggle2.setVisibility(View.VISIBLE);
                toggle3.setVisibility(View.VISIBLE);
                toggle4.setVisibility(View.VISIBLE);
                toggle5.setVisibility(View.VISIBLE);
                textView58.setVisibility(View.VISIBLE);
            }
            if(ManejaCajas.equalsIgnoreCase("S") || mantisficc.equalsIgnoreCase("N")) {
                edit_cajas.setEnabled(true);
            }else{
                edit_cajas.setEnabled(false);
                edit_cajas.setVisibility(View.INVISIBLE);
            }
            if(mantisficc.equalsIgnoreCase("S")){
                edit_cajas.setEnabled(false);
                edit_cajas.setVisibility(View.INVISIBLE);
            }

            if(DesConf.equalsIgnoreCase("S")){
                txt_confprov.setEnabled(true);
                txt_confvend.setEnabled(true);
                txt_confemp.setEnabled(true);
            }else{
                txt_confprov.setEnabled(false);
                txt_confvend.setEnabled(false);
                txt_confemp.setEnabled(false);
                liner_conf.setVisibility(View.GONE);
            }

            if(NoOtorgar.equalsIgnoreCase("S")){
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
            if (vEmpresa.trim().equalsIgnoreCase("BRILLO")) {
                edit_dcto1.setEnabled(true);
                edit_dcto2.setEnabled(false);
                edit_dcto3.setEnabled(false);
                edit_dcto4.setEnabled(false);
                edit_dcto5.setEnabled(false);
                edit_dcto6.setEnabled(false);
            }


            if (vEmpresa.trim().equalsIgnoreCase("SUHOGAR")) {
                Cursor permiso =  BaseDeDatos.getWritableDatabase().rawQuery("Select modDcto from usuarios",null);
                permiso.moveToFirst();
                if(permiso.getCount()>0){
                    if(permiso.getString(0).equalsIgnoreCase("N")){
                        //permiso
                        edit_dcto1.setEnabled(false);
                        edit_dcto2.setEnabled(false);
                        edit_dcto3.setEnabled(false);
                        edit_dcto4.setEnabled(true);
                        edit_dcto5.setEnabled(false);
                        edit_dcto6.setEnabled(false);
                    }

                }


            }

            if(checkdif.equalsIgnoreCase("S")){
                edit_dcto6.setEnabled(false);
            }


            if (SDTProductos.length > 0) {

                TextView txt_poriva = (TextView) findViewById(R.id.txt_poriva);
                final Spinner spinner_causal = (Spinner) findViewById(R.id.spinner_causal);

                if (prefijo=="NC"){
                    spinner_causal.setVisibility(View.VISIBLE);
                }else{
                    spinner_causal.setVisibility(View.INVISIBLE);
                }




                //final TextView txt_subtotal = (TextView) findViewById(R.id.txt_Subtotal);
                //final TextView txt_iva = (TextView) findViewById(R.id.txt_iva);
                //final TextView txt_impoconsumo = (TextView) findViewById(R.id.txt_impoconsumo);
                //final TextView txt_total = (TextView) findViewById(R.id.txt_neto);

               /* txt_subtotal.setText("0");
                txt_iva.setText("0");
                txt_impoconsumo.setText("0");
                txt_total.setText("0");*/

                txt_codigo.setText(SDTProductos[0].Codigo);
                txt_nombre.setText(SDTProductos[0].Nombre);
                txt_nombrecomercial.setText(SDTProductos[0].Nombrecomercial);
                txt_precio.setText(String.format("%,d",SDTProductos[0].PrecioIva.intValue()));
                txt_precioneto.setText(String.format("%,d",SDTProductos[0].PrecioNeto.intValue()));
                Double preciounit =( SDTProductos[0].PrecioIva * (1-(SDTProductos[0].Dct1/100)) *  (1-(SDTProductos[0].Dct2/100))
                        *  (1-(SDTProductos[0].Dct3/100)) * (1-(SDTProductos[0].Dct4/100))* (1-(SDTProductos[0].Dct5/100)));

                txtpredcto.setText(String.format("%,d",preciounit.intValue()));
                txt_confemp.setText(SDTProductos[0].ConfEmp.toString());
                txt_confprov.setText(SDTProductos[0].ConfProv.toString());
                txt_confvend.setText(SDTProductos[0].ConfVend.toString());
                txt_existencia.setText(SDTProductos[0].Existencia.toString());
                txt_embalaje.setText(SDTProductos[0].Embalaje.toString());

                textTitDesc.setText(SDTProductos[0].TxtDescuentos);
                textTitBon.setText(SDTProductos[0].TxtBonificado);
                textCantBon.setText(SDTProductos[0].TxtBonificadoUni);
                textCantBonCaj.setText(SDTProductos[0].TxtBonificadoCaj);

                if(SDTProductos[0].checkmax.equalsIgnoreCase("S")){
                    textTitBon.setBackgroundColor(Color.parseColor("#ef9a9a"));
                    maxof.setVisibility(View.VISIBLE);
                }else{
                    textTitBon.setBackgroundColor(Color.parseColor("#F7F8CD"));
                    maxof.setVisibility(View.GONE);
                }

                Integer kk=SDTProductos[0].Cajas;
                Double jj=SDTProductos[0].Unidades;

                if (SDTProductos[0].Cajas==0) {
                    edit_cajas.setText("");
                }else{
                    edit_cajas.setText(SDTProductos[0].Cajas.toString());
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
                    Double Tmp1=Double.valueOf(SDTProductos[0].Unidades.intValue());
                    Double Tmp2=SDTProductos[0].Unidades;
                    if (vEmpresa.trim().equalsIgnoreCase("SNACKS"  ) || vEmpresa.trim().equalsIgnoreCase("TODORAPIDAS")|| vEmpresa.trim().equalsIgnoreCase("TOTALFOOD")) {
                        int hh=Tmp1.compareTo(Tmp2);
                        if (hh==0.00) {
                            edit_unidades.setText(String.valueOf(SDTProductos[0].Unidades.intValue()));
                        }else{
                            edit_unidades.setText(String.valueOf(SDTProductos[0].Unidades));
                        }
                    }else{
                        edit_unidades.setText(String.valueOf(SDTProductos[0].Unidades.intValue()));
                    }
                }

                if (SDTProductos[0].Unidadesinf==0) {
                    unidadesinf.setText("0");
                }else{
                    Double Tmp1=Double.valueOf(SDTProductos[0].Unidadesinf.intValue());
                    Double Tmp2=SDTProductos[0].Unidadesinf;
                    if (vEmpresa.trim().equalsIgnoreCase("SNACKS") || vEmpresa.trim().equalsIgnoreCase("TODORAPIDAS") || vEmpresa.trim().equalsIgnoreCase("TOTALFOOD") ) {
                        int hh=Tmp1.compareTo(Tmp2);
                        if (hh==0.00) {
                            unidadesinf.setText(String.valueOf(SDTProductos[0].Unidadesinf.intValue()));
                        }else{
                            unidadesinf.setText(String.valueOf(SDTProductos[0].Unidadesinf));
                        }
                    }else{
                        unidadesinf.setText(String.valueOf(SDTProductos[0].Unidadesinf.intValue()));
                    }
                }

                edit_dcto1.setText(SDTProductos[0].Dct1.toString());
                edit_dcto2.setText(SDTProductos[0].Dct2.toString());
                edit_dcto3.setText(SDTProductos[0].Dct3.toString());
                edit_dcto4.setText(SDTProductos[0].Dct4.toString());
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
                    cursorNCND.close();
                }

                spinner_causal.setAdapter(new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,CausalNombre)); // simple_spinner_item //support_simple_spinner_dropdown_item

                spinner_causal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        // your code here

                        if (spinner_causal.getSelectedItemPosition() > 0) {
                            SDTProductos[0].CausalNombreSel = spinner_causal.getSelectedItem().toString().trim();
                            SDTProductos[0].Guardar(1);


                        } else {
                           // Scanal.setSelection(0);
                           // sSubCanal = Scanal.getSelectedItem().toString().trim();
                        }

                        //SDTProductos[0].CausalNombreSel = spinner_causal.getim;
                        //txt_nombre.setText(SDTProductos[0].CausalNombreSel);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        SDTProductos[0].CausalNombreSel = "";
                        // your code here
                    }

                });




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

                        txtpredcto.setText(String.format("%,d",preciounit.intValue()));
                        txt_subtotal.setText(String.format("%,d", SDTProductos[0].TotSubtotal.intValue()));
                        txt_iva.setText(String.format("%,d", SDTProductos[0].TotIva.intValue()));
                        txt_impoconsumo.setText(String.format("%,d", SDTProductos[0].TotImpoconsumo.intValue()));
                        txt_total.setText(String.format("%,d", SDTProductos[0].Total.intValue()));
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

                        txtpredcto.setText(String.format("%,d",preciounit.intValue()));
                        edit_dcto2.setText(SDTProductos[0].Dct2.toString());
                        txt_subtotal.setText(String.format("%,d", SDTProductos[0].TotSubtotal.intValue()));
                        txt_iva.setText(String.format("%,d", SDTProductos[0].TotIva.intValue()));
                        txt_impoconsumo.setText(String.format("%,d", SDTProductos[0].TotImpoconsumo.intValue()));
                        txt_total.setText(String.format("%,d", SDTProductos[0].Total.intValue()));
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
                        Double preciounit =( SDTProductos[0].PrecioIva * (1-(SDTProductos[0].Dct1/100)) *  (1-(SDTProductos[0].Dct2/100))
                                *  (1-(SDTProductos[0].Dct3/100)) * (1-(SDTProductos[0].Dct4/100))* (1-(SDTProductos[0].Dct5/100)));

                        txtpredcto.setText(String.format("%,d",preciounit.intValue()));
                        edit_dcto3.setText(SDTProductos[0].Dct3.toString());
                        txt_subtotal.setText(String.format("%,d", SDTProductos[0].TotSubtotal.intValue()));
                        txt_iva.setText(String.format("%,d", SDTProductos[0].TotIva.intValue()));
                        txt_impoconsumo.setText(String.format("%,d", SDTProductos[0].TotImpoconsumo.intValue()));
                        txt_total.setText(String.format("%,d", SDTProductos[0].Total.intValue()));
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

                        txtpredcto.setText(String.format("%,d",preciounit.intValue()));
                        edit_dcto4.setText(SDTProductos[0].Dct4.toString());
                        txt_subtotal.setText(String.format("%,d", SDTProductos[0].TotSubtotal.intValue()));
                        txt_iva.setText(String.format("%,d", SDTProductos[0].TotIva.intValue()));
                        txt_impoconsumo.setText(String.format("%,d", SDTProductos[0].TotImpoconsumo.intValue()));
                        txt_total.setText(String.format("%,d", SDTProductos[0].Total.intValue()));
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
                        Double preciounit =( SDTProductos[0].PrecioIva * (1-(SDTProductos[0].Dct1/100)) *  (1-(SDTProductos[0].Dct2/100))
                                *  (1-(SDTProductos[0].Dct3/100)) * (1-(SDTProductos[0].Dct4/100))* (1-(SDTProductos[0].Dct5/100)));

                        txtpredcto.setText(String.format("%,d",preciounit.intValue()));
                        edit_dcto5.setText(SDTProductos[0].Dct5.toString());
                        txt_subtotal.setText(String.format("%,d", SDTProductos[0].TotSubtotal.intValue()));
                        txt_iva.setText(String.format("%,d", SDTProductos[0].TotIva.intValue()));
                        txt_impoconsumo.setText(String.format("%,d", SDTProductos[0].TotImpoconsumo.intValue()));
                        txt_total.setText(String.format("%,d", SDTProductos[0].Total.intValue()));

                    }
                });

                txt_precio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                        Alerta.setMessage("En esta ventana se digitara el precio, rent "+SDTProductos[0].ArtRen+" limt "+SDTProductos[0].ArtLim);
                        Alerta.setTitle("Alerta");
                        Alerta.setPositiveButton("OK", null);
                        Alerta.setCancelable(true);
                        Alerta.create().show();*/
                        //txt_precio.setVisibility(View.GONE);
                        //edit_precio.setVisibility(View.VISIBLE);



                        final EditText taskEditText = new EditText(view.getContext() );
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                        alertDialogBuilder.setTitle("Precio manual");
                        alertDialogBuilder.setMessage("Porfavor diligencie el descuento:"+SDTProductos[0].CostoPro.toString()+' '+SDTProductos[0].ArtLim+' '+SDTProductos[0].ArtRen);
                        alertDialogBuilder.setView(taskEditText);
                        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) {
                                String s=taskEditText.getText().toString();
                                final Double PrecioNuevo;
                                final Double PrecioDcto;
                                if (s.toString().trim().isEmpty()) {
                                    PrecioDcto = 0.0;
                                } else {
                                    PrecioDcto = Double.valueOf(s.toString());
                                }
                                if (PrecioDcto>SDTProductos[0].PrecioIva)
                                {
                                    AlertDialog.Builder AlertaLim = new AlertDialog.Builder(EditarCantidadInf.this );
                                    AlertaLim.setMessage("Descuento no puede ser mayor al precio");
                                    AlertaLim.setTitle("Alerta Descuento");
                                    AlertaLim.setPositiveButton("OK", null);
                                    AlertaLim.setCancelable(true);
                                    AlertaLim.create().show();
                                }else{
                                    PrecioNuevo=SDTProductos[0].PrecioIva-PrecioDcto;
                                    Double PrecioLimite=SDTProductos[0].CostoPro/(1-(SDTProductos[0].ArtLim/100));
                                    Double PrecioRenta=SDTProductos[0].CostoPro/(1-(SDTProductos[0].ArtRen/100));
                                    if (PrecioNuevo < PrecioRenta) {
                                        //PrecioNuevo = SDTProductos[0].PrecioIva;
                                        //edit_precio.setText(String.valueOf(PrecioNuevo.intValue()));
                                        SDTProductos[0].Dct6=0.0;
                                        edit_dcto6.setText(SDTProductos[0].Dct6.toString());
                                        AlertDialog.Builder AlertaLim = new AlertDialog.Builder(EditarCantidadInf.this );
                                        AlertaLim.setMessage("Descuento no permitido, limite:" + String.format("%,d", PrecioRenta.intValue()));
                                        AlertaLim.setTitle("Alerta ");
                                        AlertaLim.setPositiveButton("OK", null);
                                        AlertaLim.setCancelable(true);
                                        AlertaLim.create().show();
                                    }else {
                                        if (PrecioNuevo < PrecioLimite) {
                                            final Double PrecioNuevoFinal=PrecioNuevo;
                                            AlertDialog.Builder Alerta = new AlertDialog.Builder(EditarCantidadInf.this );
                                            Alerta.setMessage("Este precio, requiere autorizacion, Aceptar para enviar autorizacion, Cancelar para deshacer");
                                            Alerta.setTitle("Alerta");
                                            Alerta.setPositiveButton("Enviar y continuar", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialogo1, int id) {
                                                    if (SDTProductos[0].PrecioIva != 0) {
                                                        SDTProductos[0].Dct6 = 100 - (PrecioNuevoFinal * 100 / SDTProductos[0].PrecioIva);
                                                        SDTProductos[0].Dct6no = SDTProductos[0].Dct6;
                                                        edit_dcto6.setText(SDTProductos[0].Dct6.toString());
                                                        SDTProductos[0].Autorizacion ="S";
                                                    }
                                                }
                                            });
                                            Alerta.setCancelable(true);
                                            Alerta.setNegativeButton("Cancelar", null);
                                            Alerta.create().show();

                                        } else {
                                            if (SDTProductos[0].PrecioIva != 0) {
                                                SDTProductos[0].Dct6 = 100 - (PrecioNuevo * 100 / SDTProductos[0].PrecioIva);
                                                SDTProductos[0].Dct6no = SDTProductos[0].Dct6;
                                                edit_dcto6.setText(SDTProductos[0].Dct6.toString());
                                            }
                                        }
                                    }

                                    SDTProductos[0].Guardar(1);
                                    Double preciounit =( SDTProductos[0].PrecioIva * (1-(SDTProductos[0].Dct1/100)) *  (1-(SDTProductos[0].Dct2/100))
                                            *  (1-(SDTProductos[0].Dct3/100)) * (1-(SDTProductos[0].Dct4/100))* (1-(SDTProductos[0].Dct5/100)));

                                    txtpredcto.setText(String.format("%,d",preciounit.intValue()));
                                    txt_subtotal.setText(String.format("%,d", SDTProductos[0].TotSubtotal.intValue()));
                                    txt_iva.setText(String.format("%,d", SDTProductos[0].TotIva.intValue()));
                                    txt_impoconsumo.setText(String.format("%,d", SDTProductos[0].TotImpoconsumo.intValue()));
                                    txt_total.setText(String.format("%,d", SDTProductos[0].Total.intValue()));
                                }
                            }
                        });
                        alertDialogBuilder.setCancelable(true);
                        alertDialogBuilder.setNegativeButton("Cancelar", null);
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        alertDialog.show();

                    }
                });

                edit_precio.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            // code to execute when EditText loses focus
                            String s=edit_precio.getText().toString();
                            final Double PrecioNuevo;
                            if (s.toString().trim().isEmpty()) {
                                PrecioNuevo = 0.0;
                            } else {
                                PrecioNuevo = Double.valueOf(s.toString());
                            }
                            Double PrecioLimite=SDTProductos[0].CostoPro/(1-(SDTProductos[0].ArtLim/100));
                            Double PrecioRenta=SDTProductos[0].CostoPro/(1-(SDTProductos[0].ArtRen/100));
                                if (PrecioNuevo < PrecioRenta) {

                                    AlertDialog.Builder AlertaLim = new AlertDialog.Builder(v.getContext());
                                    AlertaLim.setMessage(String.format("%,d", PrecioRenta.intValue())+" Precio no permitido, limite:" + String.format("%,d", PrecioRenta.intValue()));
                                    AlertaLim.setTitle("Alerta");
                                    AlertaLim.setPositiveButton("OK", null);
                                    AlertaLim.setCancelable(true);
                                    AlertaLim.create().show();
                                }else {
                                    if (PrecioNuevo < PrecioLimite) {
                                        final Double PrecioNuevoFinal=PrecioNuevo;
                                        AlertDialog.Builder Alerta = new AlertDialog.Builder(v.getContext());
                                        Alerta.setMessage("Este precio, requiere autorizacion, Aceptar para enviar autorizacion, Cancelar para deshacer");
                                        Alerta.setTitle("Alerta");
                                        Alerta.setPositiveButton("Enviar y continuar", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogo1, int id) {
                                                if (SDTProductos[0].PrecioIva != 0) {
                                                    SDTProductos[0].Dct6 = 100 - (PrecioNuevoFinal * 100 / SDTProductos[0].PrecioIva);
                                                    SDTProductos[0].Dct6no = SDTProductos[0].Dct6;
                                                    edit_dcto6.setText(SDTProductos[0].Dct6.toString());
                                                }
                                            }
                                        });
                                        Alerta.setCancelable(true);
                                        Alerta.setNegativeButton("Cancelar", null);
                                        Alerta.create().show();

                                    } else {
                                        if (SDTProductos[0].PrecioIva != 0) {
                                            SDTProductos[0].Dct6 = 100 - (PrecioNuevo * 100 / SDTProductos[0].PrecioIva);
                                            SDTProductos[0].Dct6no = SDTProductos[0].Dct6;
                                            edit_dcto6.setText(SDTProductos[0].Dct6.toString());
                                        }
                                    }
                                }

                            SDTProductos[0].Guardar(1);
                            Double preciounit =( SDTProductos[0].PrecioIva * (1-(SDTProductos[0].Dct1/100)) *  (1-(SDTProductos[0].Dct2/100))
                                    *  (1-(SDTProductos[0].Dct3/100)) * (1-(SDTProductos[0].Dct4/100))* (1-(SDTProductos[0].Dct5/100)));

                            txtpredcto.setText(String.format("%,d",preciounit.intValue()));
                            txt_subtotal.setText(String.format("%,d", SDTProductos[0].TotSubtotal.intValue()));
                            txt_iva.setText(String.format("%,d", SDTProductos[0].TotIva.intValue()));
                            txt_impoconsumo.setText(String.format("%,d", SDTProductos[0].TotImpoconsumo.intValue()));
                            txt_total.setText(String.format("%,d", SDTProductos[0].Total.intValue()));

                        }
                    }
                });

                edit_precio.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {


                    }
                });


                txt_poriva.setText(SDTProductos[0].Iva.toString());

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

                        if (s.toString().trim().isEmpty()) {
                            Descuento = 0.0;
                        } else {
                            Descuento = Double.valueOf(s.toString());
                        }




                        if (Descuento > SDTProductos[0].Dct1no && mantisficc.equalsIgnoreCase("S") && SDTProductos[0].Dct1no > 0) {
                            Descuento = SDTProductos[0].Dct1no;
                            Log.e("entroaaaaaaaa1a",String.valueOf(Descuento));
                            edit_dcto1.setText(String.valueOf(SDTProductos[0].Dct1no));
                        }else{
                            if(!vEmpresa.equalsIgnoreCase("SURTIMARCAS") && !vEmpresa.equalsIgnoreCase("BRILLO")  && mantisficc.equalsIgnoreCase("N")){
                                if (Descuento > SDTProductos[0].Dct1no) {
                                    Descuento = SDTProductos[0].Dct1no;
                                    Log.e("entroaaaa2aaaa",String.valueOf(Descuento));
                                    edit_dcto1.setText(String.valueOf(SDTProductos[0].Dct1no));
                                }

                            }
                        }




                       if (SDTProductos[0].Dct1 != Descuento) {
                            SDTProductos[0].Dct1 = Descuento;
                            SDTProductos[0].Guardar(0);
                        }





                        Double preciounit =( SDTProductos[0].PrecioIva * (1-(SDTProductos[0].Dct1/100)) *  (1-(SDTProductos[0].Dct2/100))
                                *  (1-(SDTProductos[0].Dct3/100)) * (1-(SDTProductos[0].Dct4/100))* (1-(SDTProductos[0].Dct5/100)));



                        txtpredcto.setText(String.format("%,d",preciounit.intValue()));
                        txt_subtotal.setText(String.format("%,d", SDTProductos[0].TotSubtotal.intValue()));
                        txt_iva.setText(String.format("%,d", SDTProductos[0].TotIva.intValue()));
                        txt_impoconsumo.setText(String.format("%,d", SDTProductos[0].TotImpoconsumo.intValue()));
                        txt_total.setText(String.format("%,d", SDTProductos[0].Total.intValue()));



                        if(SDTProductos[0].Dct1no-SDTProductos[0].Dct1 > 0){
                            edit_dcto1no.setText(String.valueOf((SDTProductos[0].Dct1no-SDTProductos[0].Dct1)));
                        }else{
                          edit_dcto1no.setText(String.valueOf((0)));
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



                        if(!vEmpresa.equalsIgnoreCase("SURTIMARCAS")&& !vEmpresa.equalsIgnoreCase("BRILLO")&& mantisficc.equalsIgnoreCase("N")){
                       if (Descuento > SDTProductos[0].Dct2no) {
                           Descuento = SDTProductos[0].Dct2no;
                           edit_dcto2.setText(String.valueOf(SDTProductos[0].Dct2no));
                       }
                        }

                        if (Descuento > SDTProductos[0].Dct2no && mantisficc.equalsIgnoreCase("S") && SDTProductos[0].Dct2no > 0) {
                            Descuento = SDTProductos[0].Dct2no;
                            edit_dcto2.setText(String.valueOf(SDTProductos[0].Dct2no));
                        }


                        if (SDTProductos[0].Dct2 != Descuento) {
                            SDTProductos[0].Dct2 = Descuento;
                            SDTProductos[0].Guardar(0);

                        }
                        Double preciounit =( SDTProductos[0].PrecioIva * (1-(SDTProductos[0].Dct1/100)) *  (1-(SDTProductos[0].Dct2/100))
                                *  (1-(SDTProductos[0].Dct3/100)) * (1-(SDTProductos[0].Dct4/100))* (1-(SDTProductos[0].Dct5/100)));

                        txtpredcto.setText(String.format("%,d",preciounit.intValue()));
                        txt_subtotal.setText(String.format("%,d", SDTProductos[0].TotSubtotal.intValue()));
                        txt_iva.setText(String.format("%,d", SDTProductos[0].TotIva.intValue()));
                        txt_impoconsumo.setText(String.format("%,d", SDTProductos[0].TotImpoconsumo.intValue()));
                        txt_total.setText(String.format("%,d", SDTProductos[0].Total.intValue()));

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

                        Double Descuento;
                        if (s.toString().trim().isEmpty()) {
                            Descuento = 0.0;
                        } else {
                            Descuento = Double.valueOf(s.toString());
                        }
                        if(!vEmpresa.equalsIgnoreCase("SURTIMARCAS")&& !vEmpresa.equalsIgnoreCase("BRILLO")&& mantisficc.equalsIgnoreCase("N")){
                       if (Descuento > SDTProductos[0].Dct3no) {
                           Descuento = SDTProductos[0].Dct3no;
                            edit_dcto3.setText(String.valueOf(SDTProductos[0].Dct3no));
                       }
                        }

                        if (Descuento > SDTProductos[0].Dct3no && mantisficc.equalsIgnoreCase("S") && SDTProductos[0].Dct3no > 0) {
                            Descuento = SDTProductos[0].Dct3no;
                            edit_dcto3.setText(String.valueOf(SDTProductos[0].Dct3no));
                        }

                        if (!SDTProductos[0].Dct3.equals(Descuento)  ){
                            SDTProductos[0].Dct3 = Descuento;
                            SDTProductos[0].Guardar(0);
                        }
                        Double preciounit =( SDTProductos[0].PrecioIva * (1-(SDTProductos[0].Dct1/100)) *  (1-(SDTProductos[0].Dct2/100))
                                *  (1-(SDTProductos[0].Dct3/100)) * (1-(SDTProductos[0].Dct4/100))* (1-(SDTProductos[0].Dct5/100)));

                        txtpredcto.setText(String.format("%,d",preciounit.intValue()));
                        txt_subtotal.setText(String.format("%,d", SDTProductos[0].TotSubtotal.intValue()));
                        txt_iva.setText(String.format("%,d", SDTProductos[0].TotIva.intValue()));
                        txt_impoconsumo.setText(String.format("%,d", SDTProductos[0].TotImpoconsumo.intValue()));
                        txt_total.setText(String.format("%,d", SDTProductos[0].Total.intValue()));
                        edit_dcto3no.setText(String.valueOf((SDTProductos[0].Dct3no-SDTProductos[0].Dct3)));
                        if(SDTProductos[0].Dct3no-SDTProductos[0].Dct3 > 0){
                            edit_dcto3no.setText(String.valueOf((SDTProductos[0].Dct3no-SDTProductos[0].Dct3)));
                        }else{
                            edit_dcto3no.setText(String.valueOf((0)));
                        }
                    }
                });

                edit_dcto4.addTextChangedListener(new TextWatcher() {
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


                        if (Descuento > SDTProductos[0].Dct4no && (vEmpresa.trim().equalsIgnoreCase("IBANEZ") )) {
                            Descuento = SDTProductos[0].Dct4no;
                            edit_dcto4.setText(String.valueOf(SDTProductos[0].Dct4no));
                        }
                        if (Descuento > SDTProductos[0].Dct4no && mantisficc.equalsIgnoreCase("S") && SDTProductos[0].Dct4no > 0) {
                            Descuento = SDTProductos[0].Dct4no;
                            edit_dcto4.setText(String.valueOf(SDTProductos[0].Dct4no));
                        }



                        if (SDTProductos[0].Dct4 != Descuento) {
                            SDTProductos[0].Dct4 = Descuento;
                            SDTProductos[0].Guardar(0);
                        }
                        Double preciounit =( SDTProductos[0].PrecioIva * (1-(SDTProductos[0].Dct1/100)) *  (1-(SDTProductos[0].Dct2/100))
                                *  (1-(SDTProductos[0].Dct3/100)) * (1-(SDTProductos[0].Dct4/100))* (1-(SDTProductos[0].Dct5/100)));

                        txtpredcto.setText(String.format("%,d",preciounit.intValue()));
                        txt_subtotal.setText(String.format("%,d", SDTProductos[0].TotSubtotal.intValue()));
                        txt_iva.setText(String.format("%,d", SDTProductos[0].TotIva.intValue()));
                        txt_impoconsumo.setText(String.format("%,d", SDTProductos[0].TotImpoconsumo.intValue()));
                        txt_total.setText(String.format("%,d", SDTProductos[0].Total.intValue()));
                        edit_dcto4no.setText(String.valueOf((SDTProductos[0].Dct4no-SDTProductos[0].Dct4)));

                        if(SDTProductos[0].Dct4no-SDTProductos[0].Dct4 > 0){
                            edit_dcto4no.setText(String.valueOf((SDTProductos[0].Dct4no-SDTProductos[0].Dct4)));
                        }else{
                            edit_dcto4no.setText(String.valueOf((0)));
                        }
                    }
                });

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
                        if(!vEmpresa.equalsIgnoreCase("SURTIMARCAS")&& !vEmpresa.equalsIgnoreCase("BRILLO")) {
                            if (Descuento > SDTProductos[0].Dct5no) {
                                Descuento = SDTProductos[0].Dct5no;
                                edit_dcto5.setText(String.valueOf(SDTProductos[0].Dct5no));
                            }
                        }
                        if (SDTProductos[0].Dct5 != Descuento) {
                            SDTProductos[0].Dct5 = Descuento;
                            SDTProductos[0].Guardar(0);
                        }
                        Double preciounit =( SDTProductos[0].PrecioIva * (1-(SDTProductos[0].Dct1/100)) *  (1-(SDTProductos[0].Dct2/100))
                                *  (1-(SDTProductos[0].Dct3/100)) * (1-(SDTProductos[0].Dct4/100))* (1-(SDTProductos[0].Dct5/100)));

                        txtpredcto.setText(String.format("%,d",preciounit.intValue()));
                        txt_subtotal.setText(String.format("%,d", SDTProductos[0].TotSubtotal.intValue()));
                        txt_iva.setText(String.format("%,d", SDTProductos[0].TotIva.intValue()));
                        txt_impoconsumo.setText(String.format("%,d", SDTProductos[0].TotImpoconsumo.intValue()));
                        txt_total.setText(String.format("%,d", SDTProductos[0].Total.intValue()));
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
                            SDTProductos[0].Guardar(0);
                        }
                        Double preciounit =( SDTProductos[0].PrecioIva * (1-(SDTProductos[0].Dct1/100)) *  (1-(SDTProductos[0].Dct2/100))
                                *  (1-(SDTProductos[0].Dct3/100)) * (1-(SDTProductos[0].Dct4/100))* (1-(SDTProductos[0].Dct5/100)));

                        txtpredcto.setText(String.format("%,d",preciounit.intValue()));
                        txt_subtotal.setText(String.format("%,d", SDTProductos[0].TotSubtotal.intValue()));
                        txt_iva.setText(String.format("%,d", SDTProductos[0].TotIva.intValue()));
                        txt_impoconsumo.setText(String.format("%,d", SDTProductos[0].TotImpoconsumo.intValue()));
                        txt_total.setText(String.format("%,d", SDTProductos[0].Total.intValue()));
                    }
                });



                edit_cajas.addTextChangedListener(new TextWatcher() {
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
                                if (s.toString().trim().isEmpty()) {
                                    int hh = 0;
                                    if (SDTProductos[0].Cajas != hh) {
                                        SDTProductos[0].Cajas = hh;
                                        SDTProductos[0].Cajasinf = hh;
                                        SDTProductos[0].Guardar(1);
                                        cajasinf.setText(String.valueOf(hh));
                                        edit_dcto1.setText(SDTProductos[0].Dct1.toString());
                                        edit_dcto2.setText(SDTProductos[0].Dct2.toString());
                                        edit_dcto3.setText(SDTProductos[0].Dct3.toString());
                                        edit_dcto4.setText(SDTProductos[0].Dct4.toString());
                                        edit_dcto5.setText(SDTProductos[0].Dct5.toString());
                                        edit_dcto6.setText(SDTProductos[0].Dct6.toString());
                                        edit_dcto1no.setText(String.valueOf((SDTProductos[0].Dct1no-SDTProductos[0].Dct1)));
                                        edit_dcto2no.setText(String.valueOf((SDTProductos[0].Dct2no-SDTProductos[0].Dct2)));
                                        edit_dcto3no.setText(String.valueOf((SDTProductos[0].Dct3no-SDTProductos[0].Dct3)));
                                        edit_dcto4no.setText(String.valueOf((SDTProductos[0].Dct4no-SDTProductos[0].Dct4)));
                                    }
                                }
                                else {


                                    Integer Unidades;
                                    if (edit_unidades.getText().toString().trim().isEmpty()) {
                                        Unidades = 0;
                                    } else {
                                        Unidades = Integer.valueOf(edit_unidades.getText().toString().trim());
                                    }


                                    Integer Cajas = Integer.valueOf(s.toString());
                                    Integer nCajasInf = 0;
                                    Integer quitar = Integer.valueOf(s.toString());
                                    Double Existencia = Double.valueOf(txt_existencia.getText().toString());

                                    if (Unidades + (Cajas * SDTProductos[0].Embalaje) > Existencia.intValue()) {
                                        if (vEmpresa.trim().equalsIgnoreCase("BRILLO") || vEmpresa.trim().equalsIgnoreCase("gACOAVANZAR") || vEmpresa.trim().equalsIgnoreCase("TODORAPIDAS") || vEmpresa.trim().equalsIgnoreCase("MENTAHAIR") || vEmpresa.trim().equalsIgnoreCase("MENTAHAIRCOT") || vEmpresa.trim().equalsIgnoreCase("TOTALFOOD") || mantisficc.equalsIgnoreCase("S")) {
                                        } else {
                                            Cajas =  Existencia.intValue()/SDTProductos[0].Embalaje;
                                            Unidades = 0;
                                            nCajasInf = quitar-Cajas;
                                            if((nCajasInf) < 0){
                                                nCajasInf = 0;
                                            }

                                            unidadesinf.setText(String.valueOf(Unidades));
                                            edit_unidades.setText(String.valueOf(Unidades)); //.intValue()
                                            cajasinf.setText(String.valueOf(nCajasInf));
                                            edit_cajas.setText(String.valueOf(Cajas));
                                            SDTProductos[0].Unidadesinf = Double.valueOf(Unidades);
                                            SDTProductos[0].Unidades = Double.valueOf(Unidades);
                                        }
                                    } else {
                                        if(Cajas < Existencia.intValue()/SDTProductos[0].Embalaje){
                                            nCajasInf = 0;
                                            cajasinf.setText(String.valueOf(0));
                                        }
                                    }

                                    SDTProductos[0].Cajasinf = nCajasInf;
                                    SDTProductos[0].Cajas = Cajas;
                                    SDTProductos[0].Guardar(1);

                                    edit_dcto1.setText(SDTProductos[0].Dct1.toString());
                                    edit_dcto2.setText(SDTProductos[0].Dct2.toString());
                                    edit_dcto3.setText(SDTProductos[0].Dct3.toString());
                                    edit_dcto4.setText(SDTProductos[0].Dct4.toString());
                                    edit_dcto5.setText(SDTProductos[0].Dct5.toString());
                                    edit_dcto6.setText(SDTProductos[0].Dct6.toString());

                                    edit_dcto1no.setText(String.valueOf((SDTProductos[0].Dct1no-SDTProductos[0].Dct1)));
                                    edit_dcto2no.setText(String.valueOf((SDTProductos[0].Dct2no-SDTProductos[0].Dct2)));
                                    edit_dcto3no.setText(String.valueOf((SDTProductos[0].Dct3no-SDTProductos[0].Dct3)));
                                    edit_dcto4no.setText(String.valueOf((SDTProductos[0].Dct4no-SDTProductos[0].Dct4)));

                                }
                                Double preciounit =( SDTProductos[0].PrecioIva * (1-(SDTProductos[0].Dct1/100)) *  (1-(SDTProductos[0].Dct2/100))
                                        *  (1-(SDTProductos[0].Dct3/100)) * (1-(SDTProductos[0].Dct4/100))* (1-(SDTProductos[0].Dct5/100)));

                                txtpredcto.setText(String.format("%,d",preciounit.intValue()));
                                txt_subtotal.setText(String.format("%,d", SDTProductos[0].TotSubtotal.intValue()));
                                txt_iva.setText(String.format("%,d", SDTProductos[0].TotIva.intValue()));
                                txt_impoconsumo.setText(String.format("%,d", SDTProductos[0].TotImpoconsumo.intValue()));
                                txt_total.setText(String.format("%,d", SDTProductos[0].Total.intValue()));
                                //notifyDataSetChanged();
                                textTitDesc.setText(SDTProductos[0].TxtDescuentos);
                                textTitBon.setText(SDTProductos[0].TxtBonificado);
                                textCantBon.setText(SDTProductos[0].TxtBonificadoUni);
                                textCantBonCaj.setText(SDTProductos[0].TxtBonificadoCaj);

                                if(SDTProductos[0].checkmax.equalsIgnoreCase("S")){
                                    textTitBon.setBackgroundColor(Color.parseColor("#ef9a9a"));
                                    maxof.setVisibility(View.VISIBLE);
                                }
                                else{
                                    textTitBon.setBackgroundColor(Color.parseColor("#F7F8CD"));
                                    maxof.setVisibility(View.GONE);
                                }
                            }
                        };


                        handler.removeCallbacksAndMessages(null);

                        if(!vEmpresa.equalsIgnoreCase("IBANEZ")){
                            handler.postDelayed(runaccion, 300);
                        }else{
                        if(Unidades > 9 || Unidades == 0.0 ){
                            handler.postDelayed(runaccion,0);
                        }else{
                            handler.postDelayed(runaccion,700);
                        }

                        if(vecesrep>1){
                            handler.removeCallbacksAndMessages(null);
                        }
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
                                Log.e("nuevomensaje: ", String.valueOf(vecesrep));

                                // if(vecesrep >1 ){
                                if (vEmpresa.trim().equalsIgnoreCase("SNACKS") || vEmpresa.trim().equalsIgnoreCase("TODORAPIDAS") ||  mantisficc.equalsIgnoreCase("S") || vEmpresa.trim().equalsIgnoreCase("TOTALFOOD") ) {
                                    Double Unidades;
                                    if (s.toString().trim().isEmpty()) {

                                        Unidades = 0.00;

                                    } else {
                                        Unidades = Double.valueOf(s.toString());
                                    }
                                    Double UnidadInf = 0.0;
                                    Double Existencia = Double.valueOf(txt_existencia.getText().toString());
                                    Double CantidadTxt = Unidades + (SDTProductos[0].Cajas * SDTProductos[0].Embalaje);
                                    if (mantisficc.equalsIgnoreCase("S")) {

                                    } else {
                                        if ((CantidadTxt > Existencia.intValue() && CantidadTxt > 0)) {
                                            if (vEmpresa.trim().equalsIgnoreCase("BRILLO") || vEmpresa.trim().equalsIgnoreCase("ACOAVANZAR") || vEmpresa.trim().equalsIgnoreCase("TODORAPIDAS")|| vEmpresa.trim().equalsIgnoreCase("TOTALFOOD") || vEmpresa.trim().equalsIgnoreCase("MENTAHAIR") || vEmpresa.trim().equalsIgnoreCase("MENTAHAIRCOT")) {
                                            } else {
                                                Unidades = SDTProductos[0].Unidades; //.intValue();
                                                edit_unidades.setText(String.valueOf(SDTProductos[0].Unidades)); //.intValue()
                                            }
                                        } else {

                                        }
                                    }


                                    SDTProductos[0].Unidades = Double.valueOf(Unidades);
                                    SDTProductos[0].Guardar(1);

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
                                    Double preciounit = (SDTProductos[0].PrecioIva * (1 - (SDTProductos[0].Dct1 / 100)) * (1 - (SDTProductos[0].Dct2 / 100))
                                            * (1 - (SDTProductos[0].Dct3 / 100)) * (1 - (SDTProductos[0].Dct4 / 100)) * (1 - (SDTProductos[0].Dct5 / 100)));

                                    txtpredcto.setText(String.format("%,d", preciounit.intValue()));
                                    txt_subtotal.setText(String.format("%,d", SDTProductos[0].TotSubtotal.intValue()));
                                    txt_iva.setText(String.format("%,d", SDTProductos[0].TotIva.intValue()));
                                    txt_impoconsumo.setText(String.format("%,d", SDTProductos[0].TotImpoconsumo.intValue()));
                                    txt_total.setText(String.format("%,d", SDTProductos[0].Total.intValue()));
                                } else {
                                    Integer Unidades;
                                    Integer UnidadInf = 0;
                                    Integer quitar;

                                    if (s.toString().trim().isEmpty()) {
                                        Unidades = 0;
                                        UnidadInf = 0;
                                        unidadesinf.setText(String.valueOf(UnidadInf));

                                        SDTProductos[0].Unidadesinf = Double.valueOf(UnidadInf);
                                        SDTProductos[0].Unidades = Double.valueOf(Unidades);
                                        SDTProductos[0].Guardar(1);
                                        quitar = 0;
                                    } else {
                                        Log.e("Unidades: ", s.toString());
                                        if (s.toString().trim().equals("0")) {
                                            edit_unidades.setText("");
                                        }
                                        Unidades = Integer.valueOf(s.toString());
                                        quitar = Integer.valueOf(s.toString());
                                    }

                                    Integer cajas = SDTProductos[0].Cajas;

                                    Double Existencia = Double.valueOf(txt_existencia.getText().toString());
                                    Integer CantidadTxt = Unidades + (cajas * SDTProductos[0].Embalaje);

                                    if (CantidadTxt > Existencia.intValue() && CantidadTxt > 0) {
                                        if (vEmpresa.trim().equalsIgnoreCase("BRILLO") || vEmpresa.trim().equalsIgnoreCase("gACOAVANZAR")|| vEmpresa.trim().equalsIgnoreCase("TOTALFOOD") || vEmpresa.trim().equalsIgnoreCase("TODORAPIDAS") || vEmpresa.trim().equalsIgnoreCase("MENTAHAIR") || vEmpresa.trim().equalsIgnoreCase("MENTAHAIRCOT")) {
                                        } else {
                                            Unidades = Existencia.intValue();
                                            cajas = 0;
                                            UnidadInf = quitar - Unidades;
                                            if (UnidadInf < 0) {
                                                UnidadInf = 0;
                                            }

                                            //   //.intValue()
                                            if (cajas == 0) {
                                                cajasinf.setText("");
                                                edit_cajas.setText("");
                                            }


                                            edit_unidades.setText(String.valueOf(Unidades));
                                            unidadesinf.setText(String.valueOf(UnidadInf));
                                            SDTProductos[0].Cajasinf = cajas;
                                            SDTProductos[0].Cajas = cajas;
                                        }
                                    } else {
                                        if (CantidadTxt < Existencia.intValue()) {
                                            UnidadInf = 0;
                                            unidadesinf.setText("");
                                        }

                                    }
                                    SDTProductos[0].Unidadesinf = Double.valueOf(UnidadInf);
                                    SDTProductos[0].Unidades = Double.valueOf(Unidades);
                                    SDTProductos[0].Guardar(1);

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
                                    Double preciounit = (SDTProductos[0].PrecioIva * (1 - (SDTProductos[0].Dct1 / 100)) * (1 - (SDTProductos[0].Dct2 / 100))
                                            * (1 - (SDTProductos[0].Dct3 / 100)) * (1 - (SDTProductos[0].Dct4 / 100)) * (1 - (SDTProductos[0].Dct5 / 100)));

                                    txtpredcto.setText(String.format("%,d", preciounit.intValue()));
                                    txt_subtotal.setText(String.format("%,d", SDTProductos[0].TotSubtotal.intValue()));
                                    txt_iva.setText(String.format("%,d", SDTProductos[0].TotIva.intValue()));
                                    txt_impoconsumo.setText(String.format("%,d", SDTProductos[0].TotImpoconsumo.intValue()));
                                    txt_total.setText(String.format("%,d", SDTProductos[0].Total.intValue()));
                                }
                                textTitDesc.setText(SDTProductos[0].TxtDescuentos);
                                textTitBon.setText(SDTProductos[0].TxtBonificado);
                                textCantBon.setText(SDTProductos[0].TxtBonificadoUni);
                                textCantBonCaj.setText(SDTProductos[0].TxtBonificadoCaj);
                                if (SDTProductos[0].checkmax.equalsIgnoreCase("S")) {
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


                        if(!vEmpresa.equalsIgnoreCase("IBANEZ")){
                            handler.postDelayed(runaccion, 300);
                        }else{
                            if (Unidades > 9 || Unidades == 0.0 ) {
                                handler.postDelayed(runaccion, 0);
                            } else {
                                handler.postDelayed(runaccion, 700);
                            }
                        }

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


            traerDetalle( artsec);

            agregarlinea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    int secuencia = ultllave+1;//SDTArticuloinf.length+1;
                    Double Cantidad = 0.0;
                    Double CantInf = 0.0;
                    if(!editUnidadIndu.getText().toString().isEmpty()){
                        Cantidad=Double.valueOf(editUnidadIndu.getText().toString());
                     }
                    if(!editUnidadInf.getText().toString().isEmpty()){
                        CantInf=Double.valueOf(editUnidadInf.getText().toString());
                    }
                    String insertar = "Insert Into PedidoInf (prefijo,nitsec,clisec,artsec,secuencia,Cantidad,CantInf)" +
                            " values ('"+prefijo+"','"+nitsec+"','"+clisec +"','"+artsec+"',"+secuencia+","+Cantidad+","+CantInf+")";
                   try{
                     BaseDeDatos.getWritableDatabase().execSQL(insertar);
                     traerDetalle(artsec);
                       editUnidadIndu.setText("");
                       editUnidadInf.setText("");


                   }catch (Exception e){
                       Log.e("Error insert",e.toString());

                   }

                }
            });




        }catch (Exception e){
            AlertDialog.Builder Alerta = new AlertDialog.Builder(this);
            Alerta.setMessage("Error "+e.getMessage());
            Alerta.setTitle("Alerta");
            Alerta.setPositiveButton("OK", null);
            Alerta.setCancelable(true);
            Alerta.create().show();
            int hh=0;
        }

    }

public void traerDetalle(String ArtSec) {
    final BaseDatos BaseDeDatos;
    BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
    String consulta = "Select Cantidad,CantInf,secuencia from PedidoInf where prefijo='" + prefijo + "' and nitsec='" + nitsec + "' and clisec='" + clisec + "' and artsec='" + ArtSec + "' order by secuencia ASC ";
    Cursor cursorCli = BaseDeDatos.getReadableDatabase().rawQuery(consulta,null);
    Double unidades = 0.0;
    Double unidadesin = 0.0;
    if(cursorCli.getCount()>0){
        cursorCli.moveToFirst();
        SDTArticuloinf=new SDTArticuloinf[cursorCli.getCount()];
        int vuelta =0;
        do{
            SDTArticuloinf   SDTArticuloinfItem= new SDTArticuloinf();
            SDTArticuloinfItem.Cantidad = cursorCli.getString(0);
            SDTArticuloinfItem.CantInf = cursorCli.getString(1);
            SDTArticuloinfItem.secuencia = cursorCli.getInt(2);
            SDTArticuloinfItem.Prefijo = prefijo;
            SDTArticuloinfItem.Nitsec = nitsec;
            SDTArticuloinfItem.Clisec = clisec;
            SDTArticuloinfItem.ArtSec = ArtSec;
            ultllave = cursorCli.getInt(2);
            SDTArticuloinf[vuelta]=SDTArticuloinfItem;
            unidades += cursorCli.getDouble(0);
            unidadesin +=cursorCli.getDouble(1);
            vuelta += 1;
        }while(cursorCli.moveToNext());

        ListViewAdapterCantinf = new ListViewAdapterCantinf(EditarCantidadInf.this, SDTArticuloinf);
        listacantidades.setAdapter(ListViewAdapterCantinf);
    }
    TextView edit_unidades = (TextView) findViewById(R.id.edit_unidades);
    edit_unidades.setText(String.valueOf(unidades));
    TextView cantinf = (TextView) findViewById(R.id.cantinf);
    cantinf.setText(String.valueOf(unidadesin));
    SDTProductos[0].Unidadesinf = unidadesin;
    SDTProductos[0].Guardar(1);
}

public void actualizartotal(String ArtSec){
    traerDetalle(ArtSec);
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

}
