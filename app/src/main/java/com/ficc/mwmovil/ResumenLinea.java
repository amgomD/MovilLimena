package com.ficc.mwmovil;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResumenLinea extends AppCompatActivity {

    Bundle Extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_linea);

        Time time = new Time();
        time.setToNow();
        Extras=this.getIntent().getExtras();
        final String tiporep = Extras.getString("tipo");
        String pinvgrucod = Extras.getString("pinvgrucod");
        String pinvsubgrucod = Extras.getString("pinvsubgrucod");
        String pinvfamcod = Extras.getString("pinvfamcod");
        final ListView Milist = (ListView) findViewById(R.id.listaresumen);


        GestorPedidos gestorpedidos = new GestorPedidos();


        String Consulta="";
        if(tiporep.equalsIgnoreCase("G")) {
            Consulta= "select invgrucod,invgrunom from pedido p left join articulos a on rtrim(a.artsec)=rtrim(p.artsec) where " +
                    " pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and cant+ifnull(cantcaj,0)<>0  Group by invgrucod,invgrunom";
        }
        if(tiporep.equalsIgnoreCase("S")) {
             Consulta = "select invsubgrucod,invsubgrunom from pedido p left join articulos a on rtrim(a.artsec)=rtrim(p.artsec) where " +
                    " pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and cant+ifnull(cantcaj,0)<>0 and invgrucod='"+pinvgrucod+"' Group by invsubgrucod,invsubgrunom";
        }
        if(tiporep.equalsIgnoreCase("F")) {
             Consulta = "select invfamcod,invfamnom from pedido p left join articulos a on rtrim(a.artsec)=rtrim(p.artsec) where " +
                    " pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and cant+ifnull(cantcaj,0)<>0 and invgrucod='"+pinvsubgrucod+"' Group by invfamcod,invfamnom";
        }
        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);

        Cursor Listado = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null);

        String[] grupo ;
        String[] gruponombre;
        final String[] valor;
        grupo= new String[Listado.getCount()];
        gruponombre= new String[Listado.getCount()];
        valor = new String[Listado.getCount()];
        if (Listado.getCount() > 0) {
            ArrayList<Map<String,Object>> itemDataList = new ArrayList<Map<String,Object>>();

            Listado.moveToFirst();
            int vuelta=0;
            do {

                if(tiporep.equalsIgnoreCase("G")) {
                    pinvgrucod=Listado.getString(0);
                }
                if(tiporep.equalsIgnoreCase("S")) {
                    pinvsubgrucod=Listado.getString(0);
                }
                if(tiporep.equalsIgnoreCase("F")) {
                    pinvfamcod=Listado.getString(0);
                }
                grupo[vuelta]=Listado.getString(0);
                gruponombre[vuelta]=Listado.getString(1);
                Double tvalor =Listado.getDouble(1);
                SDTResumenPedidos sdtResumenPedidos = gestorpedidos.TotalesPedido(getApplicationContext(), "", "", 0,pinvgrucod,pinvsubgrucod,pinvfamcod);
                valor[vuelta]=String.format("%,d",sdtResumenPedidos.Subtotal.intValue()); //+"/"+String.format("%,d",tvalor.intValue());

                Map<String,Object> listItemMap = new HashMap<String,Object>();
                listItemMap.put("title", gruponombre[vuelta]);
                listItemMap.put("description","Sub: "+valor[vuelta]);
                itemDataList.add(listItemMap);

                vuelta=vuelta+1;
            } while (Listado.moveToNext());
            SimpleAdapter simpleAdapter = new SimpleAdapter(this,itemDataList,android.R.layout.simple_list_item_2,
                    new String[]{"title","description"},new int[]{android.R.id.text1,android.R.id.text2});
            Milist.setAdapter(simpleAdapter);
        }

        final String[] finalGrupo = grupo;
        //Milist.setOnItemClickListener();

        Milist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, final View view, int i, long l) {


                if (tiporep.equalsIgnoreCase("G")) {
                    Intent intent = new Intent(getApplicationContext(), ResumenLinea.class);
                    intent.putExtra("tipo", "S");
                    intent.putExtra("pinvgrucod", finalGrupo[i]);
                    intent.putExtra("pinvsubgrucod", "");
                    intent.putExtra("pinvfamcod", "");
                    startActivity(intent);
                }
                if (tiporep.equalsIgnoreCase("S")) {
                    Intent intent = new Intent(getApplicationContext(), ResumenLinea.class);
                    intent.putExtra("tipo", "F");
                    intent.putExtra("pinvgrucod", "");
                    intent.putExtra("pinvsubgrucod", finalGrupo[i]);
                    intent.putExtra("pinvfamcod", "");
                    startActivity(intent);
                }



            }

        });





    }
}
