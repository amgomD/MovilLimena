package com.ficc.mwmovil;

import android.content.Intent;
import android.database.Cursor;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BannerDescuentos extends AppCompatActivity {

    Bundle Extras;
    TextView txt_conteo;

    private MiContador timer;
    private long lastCountDown = 100; //Milliseconds for view ad
    private Boolean isCountDown = false;
    String TimerActivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_descuentos);

        getSupportActionBar().hide();
        Extras=this.getIntent().getExtras();

        TimerActivo=Extras.getString("timeractivo");
        String pinvgrucod=Extras.getString("invgrucod");
        String pinvsubgrucod=Extras.getString("invsubgrucod");
        String pinvfamcod=Extras.getString("invfamcod");
        String partsec=Extras.getString("artsec");

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);

        Time time = new Time();
        time.setToNow();

        /*Cursor cursor = BaseDeDatos.getWritableDatabase().rawQuery("SELECT  * FROM (" +
                " (select count(*) from  (select MovParBonSec from MovParBonProdBon mb left join articulos a on a.artsec=mb.MovParBonEncArtSec where a.InvGruCod=ig.InvGruCod group by MovParBonSec) hh) bonificado " +
                " Union all " +
                " (select count(*) from (select MovParMixSec from MovParMixArticulos mb left join articulos a on a.artsec=mb.MovParMixDetArtSec where a.InvGruCod=ig.InvGruCod group by MovParMixSec) hh) mixto " +
                " Union all " +
                " (select count(*) from (select MovParEscSec from MovParEsc mb left join articulos a on a.artsec=mb.MovParEscArtSec where a.InvGruCod=ig.InvGruCod group by MovParEscSec) hh) Escala " +
                " Union all " +
                 " (select count(*) from articulos a where a.InvGruCod=ig.InvGruCod and desc2>0) Articulo " +
                ") KK ORDER BY Fecha,ArtCod DESC"

                " from inventariogrupo ig where invgrucod in(select InvGruCod from articulos group by InvGruCod) order by InvGruNom ", null); //order by nombre
*/

        //BaseDatos BaseDeDatos;
        //BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);

        String whereBonificado="";
        String whereMixto="";
        String whereProm=" and 1=1 ";
        String whereEsca=" and 1=1 ";

        if (TimerActivo.equalsIgnoreCase("S")) {
            Cursor Clientes = BaseDeDatos.getWritableDatabase().rawQuery("select SinAno,SinMes,SinDay,SinHor,SinMin,SinSeg,SerDay,SerMes,SerAno,horcie from Sincronizaciones where SinAno=" + time.year + " and SinMes=" + (time.month + 1) + " and SinDay=" + (time.monthDay), null);
            if (Clientes.getCount() == 0) {
                Clientes.moveToFirst();
                String fecha = Clientes.getString(0) + '-' + Clientes.getString(1) + Clientes.getString(2);
                whereBonificado = " where MovParBonFecMod>=DATE('" + fecha + "','-1 day')";
                whereMixto = " where MovParMixFecMod>=DATE('" + fecha + "','-1 day')";
                whereProm = " and 1=1 AND MovParArtFecMod>=DATE('" + fecha + "','-1 day') ";
                whereEsca = " and 1=1 AND MovParEscfecmod>=DATE('" + fecha + "','-1 day') ";
            }
        }

        if (!pinvgrucod.isEmpty()){

             whereBonificado=" where a.InvGruCod='"+pinvgrucod+"' ";
             whereMixto=" where  a.InvGruCod='"+pinvgrucod+"' ";
             whereProm=" and a.InvGruCod='"+pinvgrucod+"' ";
             whereEsca="  and a.InvGruCod='"+pinvgrucod+"' ";

        }
        if (!pinvsubgrucod.isEmpty()){
            whereBonificado=" where a.InvSubGruCod='"+pinvsubgrucod+"' ";
            whereMixto=" where  a.InvSubGruCod='"+pinvsubgrucod+"' ";
            whereProm=" and a.InvSubGruCod='"+pinvsubgrucod+"' ";
            whereEsca=" and a.InvSubGruCod='"+pinvsubgrucod+"' ";
        }
        if (!pinvfamcod.isEmpty()){
            whereBonificado=" where a.InvFamCod='"+pinvfamcod+"' ";
            whereMixto=" where  a.InvFamCod='"+pinvfamcod+"' ";
            whereProm=" and a.InvFamCod='"+pinvfamcod+"' ";
            whereEsca=" and a.InvFamCod='"+pinvfamcod+"' ";
        }
        if (!pinvfamcod.isEmpty()){
            whereBonificado=" where a.InvFamCod='"+pinvfamcod+"' ";
            whereMixto=" where  a.InvFamCod='"+pinvfamcod+"' ";
            whereProm=" and a.InvFamCod='"+pinvfamcod+"' ";
            whereEsca=" and a.InvFamCod='"+pinvfamcod+"' ";
        }
        if (!partsec.isEmpty()){
            whereBonificado=" where a.artsec='"+partsec+"' ";
            whereMixto=" where a.artsec='"+partsec+"' ";
            whereProm=" and a.artsec='"+partsec+"' ";
            whereEsca=" and a.artsec='"+partsec+"' ";
        }


        try {
            String ConsultaDcto="SELECT  * FROM (";
            ConsultaDcto += "select 'Bonificado' T,MovParBonFecMod Fecha,a.artcod ArtCod,'(Bn) Compra ' || MovParBonCant || ' Unid de ' || Artnom ||' Bonf ' || (select MovParBonDetCant || ' Unid de ' || aa.artnom || ' ' from MovParBonBonificados mb left join articulos aa on aa.artsec=mb.MovParBonArtSec where mb.MovParBonSec=m.MovParBonSec  LIMIT 1 ) Texto from MovParBonProdBon m left join articulos a on a.artsec=m.MovParBonEncArtSec "+whereBonificado  ;
            ConsultaDcto += " Union all ";
            ConsultaDcto += "select 'Prepack' T ,MovParMixFecMod Fecha,'1' ArtCod,'(Mx)' || MovParMixNom Texto from MovParMix m WHERE MovParMixSec IN (select MovParMixSec from MovParMixArticulos mb left join articulos a on a.artsec=mb.MovParMixDetArtSec "+whereMixto+"  group by MovParMixSec) ";
            ConsultaDcto += " Union all ";
            ConsultaDcto += "select 'Descuento' T ,MovParArt.MovParArtFecMod Fecha,ArtCod,'(Prm) ' || desc2 || '% EN ' || Artnom Texto from MovParArt left join Articulos a on a.artsec=MovParArtSec WHERE  MovParArtDetDesc<>0 and artnom is not null  "+whereProm ;
            ConsultaDcto += " Union all ";
 //           ConsultaDcto += "select MovParLINfecmod Fecha,ArtCod,'(Lin) ' || MovParLinNomDes Texto from MovParLinea m left join articulos a on a.artsec=m.artsec  WHERE  MovParLinDes<>0  "+whereProm ;
   //         ConsultaDcto += " Union all ";
            ConsultaDcto += "select 'Escala' T ,MovParEscfecmod Fecha,'1' ArtCod,'(Esc) ' || MovParEscDesc from MovParEsc mb left join articulos a on a.artsec=mb.MovParEscArtSec  WHERE artnom is not null and MovParEscDesc1<>0  "+whereEsca+" GROUP BY MovParEscfecmod,MovParEscDesc ";
            ConsultaDcto += ") KK ORDER BY Fecha DESC,ArtCod ";
            Cursor cursor = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaDcto, null); //order by nombre
            ArrayList<Map<String,Object>> itemDataList = new ArrayList<Map<String,Object>>();
            String[] sistemas = new String[cursor.getCount()];
            String[] Tipo = new String[cursor.getCount()];
            int vuelta = 0;
            if (cursor.getCount() > 0 ) {

                cursor.moveToFirst();
                do {
                    sistemas[vuelta] = cursor.getString(3);
                    Tipo[vuelta] = cursor.getString(0);

                    Map<String,Object> listItemMap = new HashMap<String,Object>();
                    listItemMap.put("title", sistemas[vuelta]);
                    listItemMap.put("description",Tipo[vuelta]);
                    itemDataList.add(listItemMap);
                    vuelta = vuelta + 1;
                } while (cursor.moveToNext());
            }
        //    sistemas[vuelta+1] = "No se encontro registros";
        /*String[] sistemas = {"Compra surtido paketicos de kellogs y obsequio uno gratis de cualquier referenncia"
                , "Compra 4 atunes surtidos, lleva una libra de arroz"
                , "5% en toda la linea de ALUMINA"
                , "4% En compras superiores a 100 mil pesos"
                , "Descuents mixtos en linea de aldor"};*/

            //  String[] sistemas={""};



            try {
            ListView listview_banner = (ListView) findViewById(R.id.listview_banner);
                SimpleAdapter simpleAdapter = new SimpleAdapter(this,itemDataList,android.R.layout.simple_list_item_2,
                        new String[]{"title","description"},new int[]{android.R.id.text1,android.R.id.text2});
            //ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, sistemas);
            listview_banner.setAdapter(simpleAdapter);
            txt_conteo = (TextView) findViewById(R.id.txt_conteo);
            }catch (Exception e){
                int jj =0;
            }
       /*     TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Intent i = new Intent(getApplicationContext(), ComportamientoVenta.class);

                    i.putExtra("nitsec", Extras.getString("nitsec"));
                    i.putExtra("clisec", Extras.getInt("clisec"));
                    i.putExtra("lisprecod", Extras.getInt("lisprecod"));
                    i.putExtra("prefijo", Extras.getString("prefijo"));
                    startActivity(i);
                    finish();
                }
            };*/
/*
            if (TimerActivo.equalsIgnoreCase("S")) {
                Timer timer1 = new Timer();
                timer1.schedule(task, 1000); //10000
                timer = new MiContador(lastCountDown, 1000);
                timer.start();
            }
*/
        }catch (Exception e){
            int jj=0;
            if (TimerActivo.equalsIgnoreCase("S")) {
                Intent i = new Intent(getApplicationContext(), ComportamientoVenta.class);
                i.putExtra("nitsec", Extras.getString("nitsec"));
                i.putExtra("clisec", Extras.getInt("clisec"));
                i.putExtra("lisprecod", Extras.getInt("lisprecod"));
                i.putExtra("prefijo", Extras.getString("prefijo"));
                startActivity(i);

                finish();
            }
        }

    }


    public class MiContador extends CountDownTimer {
        private final String TAG = MiContador.class.getSimpleName();
        public MiContador(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            isCountDown = true;
        }

        @Override
        public void onFinish() {
            //Log.i(TAG, "onFinish: ");
            isCountDown = false;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //Log.d(TAG, "onTick: " + String.valueOf(millisUntilFinished/1000));
            lastCountDown = millisUntilFinished;
            txt_conteo.setText(String.valueOf(lastCountDown/1000));
            //countdownText.setText((millisUntilFinished / 1000 + ""));

        }
    }
    @Override
    protected void onPause() {
        if (TimerActivo.equalsIgnoreCase("S")) {
            timer.cancel();
        }

       // Log.i(TAG, "onPause: ");
        super.onPause();

    }
}
