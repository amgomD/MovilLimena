package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


public class InventarioAgrupado extends AppCompatActivity {
    ListViewAdapterArtFam ListViewAdapterArtFam;
    SDTArtFam[] SDTArtFam ;
    Bundle Extras;
    TextView txtGrupo;
    Integer UltimaPosicion=0;
    @SuppressLint("MissingInflatedId")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 setContentView(R.layout.activity_inventario_agrupado);

        Extras=this.getIntent().getExtras();
        String invgrucod = Extras.getString("invgrucod");
        String grupo =Extras.getString("invgruNom");
        txtGrupo = findViewById(R.id.grupo);
        txtGrupo.setText(grupo);

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
        Cursor cursor =null;
        ListView listart = findViewById(R.id.lista);
        String invfamcod = Extras.getString("invgrucod");
        String prefijo = Extras.getString("prefijo");
        String nitsec = Extras.getString("nitsec");
        int clisec = Extras.getInt("clisec");
        int lisprecod = Extras.getInt("lisprecod");
        Integer bodcod = Extras.getInt("bodega");
        prefijo=Extras.getString("prefijo");
        int plazo=Extras.getInt("plazo");
        String  plazoNom = Extras.getString("plazoNom");
        GlobalVariables gGlobalVariables = GlobalVariables.getInstance();
        final String vEmpresa=gGlobalVariables.getEmpresa();
        String vUsuario=gGlobalVariables.getUsuario();
        int vSucCod=gGlobalVariables.getSucCod();
        int vAliNegCod=gGlobalVariables.getAliNegCod();
        int perfil=99;
        String Canal="XX";
        String SubCanal="XX";
        String CiuCod="XX";
        String clisiniva = "S";
        int CliTamCan=0;
        Cursor cursorCli = BaseDeDatos.getReadableDatabase().rawQuery("select CanCod,PerCliCod,CanSubCod,CliTamCan,CiuCod,CliIva from clientes where nitsec='"+nitsec+"' and clisec="+clisec+" ", null);
        if (cursorCli.getCount()>0){
            cursorCli.moveToFirst();
            do {
                try {
                    Canal=cursorCli.getString(0);
                    perfil=cursorCli.getInt(1);
                    SubCanal=cursorCli.getString(2);
                    CliTamCan=cursorCli.getInt(3);
                    CiuCod=cursorCli.getString(4);
                    clisiniva = cursorCli.getString(5);
                }catch (Exception e){
                    Integer Error=1;
                }
            } while (cursorCli.moveToNext());
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

        String Exist;
        String Precio;
        if (prefijo.equalsIgnoreCase("NC")){
            Exist="KarUni";
            Precio="KarPrePub";

            WhereConsulta += " and KarUni>0 ";

        }else{
            Exist="Exist";
            Precio="Precio"+lisprecod;
        }
        Time time = new Time();
        time.setToNow();

        if(prefijo.equalsIgnoreCase("V15") & vEmpresa.trim().equalsIgnoreCase("IBANEZ")){
            Exist = "ExistFec";
        }
        String  Consulta = "select a.ArtSec , ArtCod , ArtNom," + Exist + " Exist,a.InvSubGruCod,a.invfamcod,a.invSubGruNom,a.invfamNom,  artemb,case cliiva when 'S' then ParConIva else 0 end ParConIva,ArtValImp,cant,a.InvGruNom,a.ArtcodBar,artmednomcom,KarUni,KarPrePub,ifNULL((select MovParLinDes from MovParLinea  where MovParLinArtSec=a.artsec),Desc1) pordesc,ifNULL((select MovParArtDetDesc from MovParArt where  MovParArtDetArtSec=a.artsec),Desc2) pordesc2,ifNULL(pordesc3,0.0) pordesc3,ifNULL(pordesc4,0.0) pordesc4,ifNULL(pordesc5,0.0) pordesc5,ifNULL(pordesc6,0.0) pordesc6,ifNULL(TieneDescEsp,'') TieneDescEsp,ifNULL(ConVenGruSec,0)  ConVenGruSec, ifnull(ArtExiAct,0) ArtExiAct from Articulos a " +
                "left join inventariogrupo ig on ig.invgrucod=a.invgrucod  " +
                "left join Clientes cc on cc.nitsec='" + nitsec + "' and cc.clisec='" + clisec + "'  " +
                "left join ListaPorGrupoSubgrupo lgs on lgs.nitsec='" + nitsec + "' and lgs.clisec='" + clisec + "' and ((lgs.invgrucod=a.invgrucod and lgs.invsubgrucod =a.invsubgrucod ) or (lgs.invgrucod=a.invgrucod and lgs.invsubgrucod ='0')) " +
                "left join ClientesDevoluciones d on d.nitsec='" + nitsec + "' and d.clisec='" + clisec + "' and d.artsec=a.artsec " +
                "left join ArticulosExi ex on ex.ArtSec=a.artsec and ArtBodCod = "+bodcod+" " +

                "left join ControlVentas cv " +
                "on (Subgrupo='XX,' OR Subgrupo like '%,'|| a.InvSubGruCod ||',%') and (familia='XX,' OR familia like '%,'|| a.invfamcod ||',%') and (ConVenGruGrup=a.invgrucod) " +
                "and (Cliente='XX,' OR Cliente like '%,"+nitsec+'-'+clisec+",%')  and (Canales='XX,' OR Canales like '%,"+Canal+",%') and (SubCanales='XX,' OR SubCanales like '%,"+SubCanal+",%') and (Tamano='XX,' OR Tamano like '%,"+CliTamCan+",%') " +
                "and (Sucursales='XX,' OR Sucursales like '%,"+vSucCod+",%') and (UnidadNegoc='XX,' OR UnidadNegoc like '%,"+vAliNegCod+",%') and (Vendedores='XX,' OR Vendedores like '%,"+vUsuario+",%') and (Ciudad='XX,' OR Ciudad like '%,"+CiuCod+",%') and (Articulos='XX,' OR Articulos like '%,' || rtrim(ltrim(a.artsec)) || '%') "+
                "left join pedido p on prefijo='" + prefijo + "' and  pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and p.nitsec='" + nitsec + "' and p.clisec='" + clisec + "' and p.artsec=a.artsec where a.InvGruCod ='"+invgrucod+"' and ArtIndMpm<>'S' " + WhereConsulta + "  order by a.InvSubGruCod,a.invfamcod ";  //AND " + Precio + ">10



        cursor = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null); //order by nombre
        SDTArtFam = new SDTArtFam[cursor.getCount()];
        if (cursor.getCount() > 0) {
            int vuelta = 0;
            double precioNu = 0;
            cursor.moveToFirst();
            String AntSubcod = "";
            String AntFamcod = "";
            String banSub = "";
            String banFam = "";
            do {
                if(!AntSubcod.equalsIgnoreCase(cursor.getString(6))){
                    AntSubcod = cursor.getString(6);
                    banSub = "S";
                }else{
                    banSub = "N";
                }
                if(!AntFamcod.equalsIgnoreCase(cursor.getString(7))){
                    AntFamcod = cursor.getString(7);
                    banFam = "S";
                }else{
                    banFam = "N";
                }

                SDTArtFam SDTArtFamItem = new SDTArtFam();
                SDTArtFamItem.mFam = banFam;
                SDTArtFamItem.mSub = banSub;
                SDTArtFamItem.InvSubNom = cursor.getString(6);
                SDTArtFamItem.InvFamNom = cursor.getString(7);
                SDTArtFamItem.ArtSec =cursor.getString(0);
                SDTArtFamItem.ArtCod =cursor.getString(1);
                SDTArtFamItem.ArtNom = cursor.getString(2) + " (" + cursor.getString(12) + ")";
                SDTArtFamItem.Ean =cursor.getString(13);
                SDTArtFamItem.PreArtNom =cursor.getString(1);
                SDTArtFamItem.Exi =cursor.getDouble(3);
                SDTArtFamItem.Bloqueado = cursor.getInt(24);
                SDTArtFam[vuelta] = SDTArtFamItem;
                vuelta = vuelta + 1;
            } while (cursor.moveToNext());
        }
        ListViewAdapterArtFam = new ListViewAdapterArtFam(this, SDTArtFam);
        listart.setAdapter(ListViewAdapterArtFam);
        listart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, final View view, int i, long l) {
                if (SDTArtFam[i].Bloqueado==0) {
                    UltimaPosicion = i;
                    Intent intent = new Intent(getApplicationContext(), EditarCantidad.class);
                    intent.putExtra("nitsec", Extras.getString("nitsec"));
                    intent.putExtra("clisec", Extras.getInt("clisec"));
                    intent.putExtra("lisprecod", Extras.getInt("lisprecod"));
                    intent.putExtra("invgrucod", "");
                    intent.putExtra("invsubgrucod", "");
                    intent.putExtra("invfamcod", "");
                    intent.putExtra("prefijo", Extras.getString("prefijo"));
                    intent.putExtra("artsec", SDTArtFam[i].ArtSec);
                    intent.putExtra("plazo", plazo);
                    intent.putExtra("plazoNom", plazoNom);
                    intent.putExtra("bodega", Extras.getInt("bodega"));
                    startActivityForResult(intent, 3);
                }else{
                    AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                    Alerta.setMessage("Producto restrigido para este cliente, restriccion # "+SDTArtFam[i].Bloqueado);
                    Alerta.setTitle("Alerta");
                    Alerta.setPositiveButton("OK", null);
                    Alerta.setCancelable(true);
                    Alerta.create().show();
                }
            }
        });


        listart.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(), BannerDescuentos.class);
                intent.putExtra("timeractivo", "N");
                intent.putExtra("invgrucod", "");
                intent.putExtra("invsubgrucod", "");
                intent.putExtra("invfamcod", "");
                intent.putExtra("artsec", SDTArtFam[i].ArtSec);
                startActivity(intent);
                return true;
            }
        });


    }




}