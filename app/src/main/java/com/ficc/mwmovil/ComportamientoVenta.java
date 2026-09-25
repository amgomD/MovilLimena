package com.ficc.mwmovil;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ComportamientoVenta extends AppCompatActivity {

    SDTVentasxLinea[] SDTVentasxLinea ;
    ListViewAdapterLinea ListViewAdapterLinea;
    Bundle Extras;
    String nitsec;
    Integer clisec;
    Integer plazo;
    String mantisficc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comportamiento_venta);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
       // getSupportActionBar().hide();
        GlobalVariables gGlobalVariables = GlobalVariables.getInstance();
        final String vEmpresa=gGlobalVariables.getEmpresa();
        try {

        Extras=this.getIntent().getExtras();
            nitsec = Extras.getString("nitsec");
            clisec = Extras.getInt("clisec");
            plazo = Extras.getInt("plazo");


            final BaseDatos BaseDeDatos;
            BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
            String Canal="XX";
            Cursor cursorCli = BaseDeDatos.getReadableDatabase().rawQuery("select CanCod from clientes where nitsec='"+nitsec+"' and clisec="+clisec+" ", null);
            if (cursorCli.getCount()>0){
                cursorCli.moveToFirst();
                do {
                    try {
                        Canal=cursorCli.getString(0);



                    }catch (Exception e){
                        Integer Error=1;
                    }
                } while (cursorCli.moveToNext());
            }


            ConBd conbd = new ConBd();
            conbd.Variables();
            mantisficc = conbd.MantisFicc;

            Cursor cursor = null;
            View fondo = findViewById(R.id.fondo);
            LinearLayout panel = findViewById(R.id.panel);

            fondo.setOnClickListener(v -> cerrarPanel());
            panel.post(() -> {
                int width = (int) (panel.getRootView().getWidth() * 0.5);
                panel.getLayoutParams().width = width;
                panel.requestLayout();
            });



            if(mantisficc.equalsIgnoreCase("N")) {

                cursor = BaseDeDatos.getWritableDatabase().rawQuery("select InvGruCod,InvGruNom" +
                        " ,(select total(karvaltotmendes) from VentasCliente vc where NitSec='" + Extras.getString("nitsec") + "' and clisec=" + Extras.getInt("clisec") + " and vc.InvGruCod=ig.InvGruCod) Venta " +
                        " ,(select count(*) from (select MovParBonSec from MovParBonProdBon mb left join articulos a on a.artsec=mb.MovParBonEncArtSec where a.InvGruCod=ig.InvGruCod group by MovParBonSec) hh) bonificado " +
                        " ,(select count(*) from (select MovParMixSec from MovParMixArticulos mb left join articulos a on a.artsec=mb.MovParMixDetArtSec where a.InvGruCod=ig.InvGruCod group by MovParMixSec) hh) mixto " +
                        " ,(select count(*) from (select MovParEscSec from MovParEsc mb left join articulos a on a.artsec=mb.MovParEscArtSec where a.InvGruCod=ig.InvGruCod group by MovParEscSec) hh) Escala " +
                        " ,ifNULL((select count(*)  from MovParArt m left join articulos a on a.artsec=m.MovParArtDetArtSec  where a.InvGruCod=ig.InvGruCod ),0) Promo " +
                        " ,ifNULL((select count(*)  from MovParLinea m left join articulos a on a.artsec=m.MovParLinArtSec  where a.InvGruCod=ig.InvGruCod),0) Linea " +
                        " from inventariogrupo ig where invgrucod in(select InvGruCod from articulos group by InvGruCod) and (InvCanCodStr='XX' or InvCanCodStr like '%," + Canal + "%') order by InvGruNom ", null); //order by nombre
            }else{
                if(vEmpresa.equalsIgnoreCase("SUHOGAR") || vEmpresa.equalsIgnoreCase("INDULAC")){
                    cursor = BaseDeDatos.getWritableDatabase().rawQuery("select InvCatCod,InvCatNom" +
                            " ,0 Venta " +
                            " ,0 bonificado " +
                            " ,0 mixto " +
                            " ,0 Escala " +
                            " ,0 Promo " +
                            " ,0 Linea " +
                            " from InvCategoria ig  ", null); //order by nombre
                    //where InvCatCod in (select InvCatCod from articulos group by InvCatCod) order by InvCatNom
                }else{
                    cursor = BaseDeDatos.getWritableDatabase().rawQuery("select InvGruCod,InvGruNom" +
                            " ,0 Venta " +
                            " ,0 bonificado " +
                            " ,0 mixto " +
                            " ,0 Escala " +
                            " ,0 Promo " +
                            " ,0 Linea " +
                            " from inventariogrupo ig  ", null); //order by nombre
                    //where InvCatCod in (select InvCatCod from articulos group by InvCatCod) order by InvCatNom
                }

            }
            //" ,ifNULL((select desc1 from articulos a where a.InvGruCod=ig.InvGruCod and desc1>0 LIMIT 1 ),0) Linea " +
            final String[] InvGruCod;
            SDTVentasxLinea = new SDTVentasxLinea[cursor.getCount()+2];
            if (cursor.getCount() > 0) {
                int vuelta = 0;
                cursor.moveToFirst();
                do {
                    SDTVentasxLinea SDTVentasxLineaItem = new SDTVentasxLinea();
                    SDTVentasxLineaItem.Codigo = cursor.getString(0);
                    SDTVentasxLineaItem.Nombre = cursor.getString(1);
                    SDTVentasxLineaItem.Venta = cursor.getDouble(2);
                    SDTVentasxLineaItem.DctoBonf = cursor.getDouble(3);
                    SDTVentasxLineaItem.DctoMix = cursor.getDouble(4);
                    SDTVentasxLineaItem.DctoEsc = cursor.getDouble(5);
                    SDTVentasxLineaItem.DctoProm = cursor.getDouble(6)+cursor.getDouble(7);
                    //SDTVentasxLineaItem.DctoLinea = cursor.getDouble(7);
                    SDTVentasxLinea[vuelta] = SDTVentasxLineaItem;
                    vuelta = vuelta + 1;
                } while (cursor.moveToNext());

                SDTVentasxLinea SDTVentasxLineaItem = new SDTVentasxLinea();
                SDTVentasxLineaItem.Codigo = "CREDITO";
                SDTVentasxLineaItem.Nombre = "Credito Malo";
                SDTVentasxLineaItem.Venta = 0.0;
                SDTVentasxLineaItem.DctoBonf = 0.0;
                SDTVentasxLineaItem.DctoMix = 0.0;
                SDTVentasxLineaItem.DctoEsc = 0.0;
                SDTVentasxLineaItem.DctoProm = 0.0;
                //SDTVentasxLineaItem.DctoLinea = cursor.getDouble(7);
                SDTVentasxLinea[vuelta] = SDTVentasxLineaItem;
                vuelta = vuelta + 1;
                SDTVentasxLineaItem = new SDTVentasxLinea();
                SDTVentasxLineaItem.Codigo = "CREDITOBU";
                SDTVentasxLineaItem.Nombre = "Credito Bueno";
                SDTVentasxLineaItem.Venta = 0.0;
                SDTVentasxLineaItem.DctoBonf = 0.0;
                SDTVentasxLineaItem.DctoMix = 0.0;
                SDTVentasxLineaItem.DctoEsc = 0.0;
                SDTVentasxLineaItem.DctoProm = 0.0;
                //SDTVentasxLineaItem.DctoLinea = cursor.getDouble(7);
                SDTVentasxLinea[vuelta] = SDTVentasxLineaItem;
            }

            final ListView listview_grupos = (ListView) findViewById(R.id.listview_grupos);
            ListViewAdapterLinea = new ListViewAdapterLinea(this, SDTVentasxLinea);
            listview_grupos.setAdapter(ListViewAdapterLinea);


            listview_grupos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView adapterView, final View view, int i, long l) {



                                /*Intent intent = new Intent(view.getContext(), EditarProductoV2.class);
                                intent.putExtra("nitsec", Extras.getString("nitsec"));
                                intent.putExtra("clisec", Extras.getInt("clisec"));
                                intent.putExtra("lisprecod", Extras.getInt("lisprecod"));
                                intent.putExtra("invgrucod", );
                                intent.putExtra("invsubgrucod", "");
                                intent.putExtra("invfamcod", "");
                                intent.putExtra("InvCatCod", "");
                                intent.putExtra("plazo", Extras.getInt("plazo"));
                                intent.putExtra("plazoNom", Extras.getString("plazoNom"));
                                intent.putExtra("prefijo", Extras.getString("prefijo"));
                                intent.putExtra("bodega", Extras.getInt("bodega"));
                                startActivity(intent);*/


       Intent data = new Intent();
                    data.putExtra("invgrucod", SDTVentasxLinea[i].Codigo);
                    data.putExtra("invgruNom", SDTVentasxLinea[i].Nombre);
        setResult(RESULT_OK, data);
        finish();
        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_right);
                }
            });

            /*listview_grupos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView adapterView, View view, int i, long l) {

                    Intent intent = new Intent(getApplicationContext(), BannerDescuentos.class);
                    intent.putExtra("timeractivo", "N");
                    intent.putExtra("invgrucod", SDTVentasxLinea[i].Codigo);
                    intent.putExtra("invsubgrucod", "");
                    intent.putExtra("invfamcod", "");
                    intent.putExtra("artsec", "");
                    startActivity(intent);
                    return true;
                }
            });*/


        }catch (Exception e){
            Log.e("Error> ",e.toString());
            int jj=0;
        }

        Button btn_resumenpedido = (Button) findViewById(R.id.btn_resumenpedido);
        btn_resumenpedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ResumenPedido.class);
                intent.putExtra("nitsec", Extras.getString("nitsec"));
                intent.putExtra("clisec", Extras.getInt("clisec"));
                intent.putExtra("lisprecod", Extras.getInt("lisprecod"));
                intent.putExtra("invgrucod", "");
                intent.putExtra("invsubgrucod", "");
                intent.putExtra("invfamcod", "");
                intent.putExtra("plazo", Extras.getInt("plazo"));
                intent.putExtra("plazoNom",  Extras.getString("plazoNom"));
                intent.putExtra("prefijo", Extras.getString("prefijo"));
                intent.putExtra("bodega",Extras.getInt("bodega"));
                startActivity(intent);
            }
        });

        Button btn_tomarpedido = (Button) findViewById(R.id.btn_tomarpedido);
        btn_tomarpedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CapturaPedido.class);
                intent.putExtra("nitsec", Extras.getString("nitsec"));
                intent.putExtra("clisec", Extras.getInt("clisec"));
                intent.putExtra("lisprecod", Extras.getInt("lisprecod"));
                intent.putExtra("invgrucod", "");
                intent.putExtra("invsubgrucod", "");
                intent.putExtra("invfamcod", "");
                intent.putExtra("plazo", Extras.getInt("plazo"));
                intent.putExtra("plazoNom",  Extras.getString("plazoNom"));
                intent.putExtra("prefijo", Extras.getString("prefijo"));
                intent.putExtra("bodega",Extras.getInt("bodega"));
                startActivity(intent);
            }
        });
       Button btn_tomarpedido2 = (Button) findViewById(R.id.btn_tomarpedido2);
        btn_tomarpedido2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditarProductoV2.class);
                intent.putExtra("nitsec", Extras.getString("nitsec"));
                intent.putExtra("clisec", Extras.getInt("clisec"));
                intent.putExtra("lisprecod", Extras.getInt("lisprecod"));
                intent.putExtra("invgrucod", "");
                intent.putExtra("invsubgrucod", "");
                intent.putExtra("invfamcod", "");
                intent.putExtra("InvCatCod", "");
                intent.putExtra("plazo", Extras.getInt("plazo"));
                intent.putExtra("plazoNom",  Extras.getString("plazoNom"));
                intent.putExtra("prefijo", Extras.getString("prefijo"));
                intent.putExtra("bodega",Extras.getInt("bodega"));
                startActivity(intent);
            }
        });


/*
        final String[] grupo;
        final String[] valor;
        if (Clientes.getCount()>0) {
            ArrayList<Map<String,Object>> itemDataList = new ArrayList<Map<String,Object>>();
            grupo= new String[Clientes.getCount()];
            valor = new String[Clientes.getCount()];
            Clientes.moveToFirst();
            int vuelta=Clientes.getCount();
            vuelta=0;
            do {
                grupo[vuelta]=Clientes.getString(0);
                Double tvalor =Clientes.getDouble(1);
                Double svalor =Clientes.getDouble(2);
                //values[vuelta]="[ "+String.format("%,d",varg.intValue())+" ] -"+nom+" -> "+res ; //Clientes.getInt(0);

                valor[vuelta]=String.format("%,d",svalor.intValue())+"/"+String.format("%,d",tvalor.intValue());
                Map<String,Object> listItemMap = new HashMap<String,Object>();
                listItemMap.put("title", grupo[vuelta]);
                listItemMap.put("description","Sub: "+String.format("%,d",svalor.intValue())+"/Tot: "+String.format("%,d",tvalor.intValue()));
                itemDataList.add(listItemMap);
                vuelta=vuelta+1;
            } while (Clientes.moveToNext());

            //   ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
            //      android.R.layout.simple_list_item_1, android.R.id.text1, valor);

            SimpleAdapter simpleAdapter = new SimpleAdapter(this,itemDataList,android.R.layout.simple_list_item_2,
                    new String[]{"title","description"},new int[]{android.R.id.text1,android.R.id.text2});
            Milist.setAdapter(simpleAdapter);

            Milist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int itemPosition = position;
                    //String itemValue = (String) Milist.getItemAtPosition(position);
                    Intent i = new Intent(getApplicationContext(), ReportesVendedor.class);
                    i.putExtra("Codvend", Extras.getString("Codvend"));
                    i.putExtra("permisos", Extras.getString("permisos"));
                    i.putExtra("GRUPOOSUBGRUPO", "S");
                    i.putExtra("FILGRUPONOM", grupo[itemPosition].trim());
                    i.putExtra("General", "SI");
                    startActivity(i);
                }

            });


        }else{

        }*/
    }

    private void cerrarPanel() {
       /* Intent data = new Intent();
        data.putExtra("posicion", Extras.getInt("position"));
        data.putExtra("Cantidad",antunidades);
        data.putExtra("PrecioNeto", antPrecioNeto);
        setResult(RESULT_OK, data);
        SDTProductos[0].Unidades = antunidades;
        SDTProductos[0].Unidadesinf= antunidadesinf;
        SDTProductos[0].Guardar(1);*/
        finish();
        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_right);
    }
    @Override
    public void onBackPressed() {
       /* Intent data = new Intent();
        data.putExtra("posicion", Extras.getInt("position"));
        data.putExtra("Cantidad", antunidades);
        data.putExtra("PrecioNeto", antPrecioNeto);
        setResult(RESULT_OK, data);
        SDTProductos[0].Unidades = antunidades;
        SDTProductos[0].Unidadesinf= antunidadesinf;
        SDTProductos[0].Guardar(1);*/
        finish();
        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_right);
    }
}
