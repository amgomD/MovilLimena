package com.ficc.mwmovil;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class VentasxSubGrupo extends AppCompatActivity {

    SDTVentasxLinea[] SDTVentasxLinea ;
    ListViewAdapterLinea ListViewAdapterLinea;
    Bundle Extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventasx_sub_grupo);
        getSupportActionBar().hide();
        Extras=this.getIntent().getExtras();


            String nitsec = Extras.getString("nitsec");
            Integer clisec = Extras.getInt("clisec");
            String invgrucod = Extras.getString("invgrucod");

            final BaseDatos BaseDeDatos;
            BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
            //Cursor cursor2 = BaseDeDatos.getWritableDatabase().rawQuery("select InvSubGruCod,InvSubGruNom,0.0 Venta from inventariosubgrupo " +
            //        "where invgrucod='" + invgrucod + "' AND   invSUBgrucod in(select InvSUBGruCod from articulos group by InvSUBGruCod) order by InvSubGruNom ", null); //order by nombre

        Cursor cursor =null;
        try {
            cursor=BaseDeDatos.getWritableDatabase().rawQuery("select InvSubGruCod,InvSubGruNom" +
                    " ,(select total(karvaltotmendes) from VentasCliente vc where NitSec='" + Extras.getString("nitsec") + "' and clisec=" + Extras.getInt("clisec") + " and vc.InvSubGruCod=ig.InvSubGruCod) Venta " +
                    " ,(select count(*) from (select MovParBonSec from MovParBonProdBon mb left join articulos a on a.artsec=mb.MovParBonEncArtSec where a.InvSubGruCod=ig.InvSubGruCod group by MovParBonSec) hh) bonificado " +
                    " ,(select count(*) from (select MovParMixSec from MovParMixArticulos mb left join articulos a on a.artsec=mb.MovParMixDetArtSec where a.InvSubGruCod=ig.InvSubGruCod group by MovParMixSec) hh) mixto " +
                    " ,(select count(*) from (select MovParEscSec from MovParEsc mb left join articulos a on a.artsec=mb.MovParEscArtSec where a.InvSubGruCod=ig.InvSubGruCod group by MovParEscSec) hh) Escala " +
                    " ,ifNULL((select count(*)  from MovParArt m left join articulos a on a.artsec=m.MovParArtDetArtSec  where a.InvSubGruCod=ig.InvSubGruCod ),0) Promo " +
                    " ,ifNULL((select count(*)  from MovParLinea m left join articulos a on a.artsec=m.MovParLinArtSec  where a.InvSubGruCod=ig.InvSubGruCod),0) Linea " +
                    " from inventariosubgrupo ig where invgrucod='" + invgrucod + "' and invSUBgrucod in(select InvSubGruCod from articulos group by InvSubGruCod) order by InvSubGruNom ", null); //order by nombre
        }catch (Exception e){
            int Lala=0;
        }
            final String[] InvGruCod;
            SDTVentasxLinea = new SDTVentasxLinea[cursor.getCount()];
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
            }

            final ListView listview_subgrupo = (ListView) findViewById(R.id.listview_subgrupo);
            ListViewAdapterLinea = new ListViewAdapterLinea(this, SDTVentasxLinea);
            listview_subgrupo.setAdapter(ListViewAdapterLinea);


            listview_subgrupo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView adapterView, final View view, int i, long l) {

                    Intent intent = new Intent(view.getContext(), VentasxFamilia.class);
                    intent.putExtra("nitsec", Extras.getString("nitsec"));
                    intent.putExtra("clisec", Extras.getInt("clisec"));
                    intent.putExtra("invgrucod", Extras.getString("invgrucod"));
                    //intent.putExtra("invsubgrucod", Extras.getString("invsubgrucod"));
                    intent.putExtra("invsubgrucod", SDTVentasxLinea[i].Codigo);
                    intent.putExtra("lisprecod", Extras.getInt("lisprecod"));
                    intent.putExtra("plazo", Extras.getInt("plazo"));
                    intent.putExtra("plazoNom", Extras.getString("plazoNom"));
                    intent.putExtra("prefijo", Extras.getString("prefijo"));
                    intent.putExtra("bodega",Extras.getInt("bodega"));
                    startActivity(intent);
                }
            });

        listview_subgrupo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(), BannerDescuentos.class);
                intent.putExtra("timeractivo", "N");
                intent.putExtra("invgrucod", "");
                intent.putExtra("invsubgrucod", SDTVentasxLinea[i].Codigo);
                intent.putExtra("invfamcod", "");
                intent.putExtra("artsec", "");
                startActivity(intent);
                return true;
            }
        });



    }
}
