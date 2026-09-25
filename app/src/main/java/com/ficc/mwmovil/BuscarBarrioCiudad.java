package com.ficc.mwmovil;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

public class BuscarBarrioCiudad extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_barrio_ciudad);
        Bundle Extras=this.getIntent().getExtras();
        final SearchView ShView=(SearchView)findViewById(R.id.Buscador);
        final ListView Milist = (ListView) findViewById(R.id.MyList);

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
        Cursor Clientes;
        String SELCUIDAD = Extras.getString("LACIUDAD");
        final String TipoFiltro=Extras.getString("BUSCIUBAR");
        if(TipoFiltro.equalsIgnoreCase("CIU")) {
            Clientes = BaseDeDatos.getWritableDatabase().rawQuery("select ciucod,ciunom from Ciudades", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0
        }
        else{
            Clientes= BaseDeDatos.getWritableDatabase().rawQuery("select barcod,barnom from barrios WHERE ( Ciudades like '%,"+SELCUIDAD+",%') ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR
        }

        final String[] values= new String[Clientes.getCount()];
        final String[] valuesid= new String[Clientes.getCount()];
        if (Clientes.getCount()>0) {
            Clientes.moveToFirst();
            int vuelta=Clientes.getCount();
            vuelta=0;
            do {
                values[vuelta]=Clientes.getString(1);
                valuesid[vuelta]=Clientes.getString(0);
                vuelta=vuelta+1;
                //  if(vuelta==10){
                //      Clientes.moveToLast();
                //  }
            } while (Clientes.moveToNext());
        }
        // Defined Array values to show in ListView

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        Milist.setAdapter(adapter);
        Milist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemPosition = position;
                String itemValue = (String) Milist.getItemAtPosition(position);

                Intent i = new Intent();
                i.putExtra("CIUDAD", itemValue);
                Cursor aValorCursor;
                if(TipoFiltro.equalsIgnoreCase("CIU")) {
                    aValorCursor= BaseDeDatos.getWritableDatabase().rawQuery("select ciucod,ciunom from Ciudades where ciunom='"+itemValue+"'", null);
                }
                else{
                    aValorCursor = BaseDeDatos.getWritableDatabase().rawQuery("select barcod,barnom from barrios where barnom='"+itemValue+"'", null);
                }

                if (aValorCursor.getCount()>0) {
                    aValorCursor.moveToFirst();
                    int vuelta=aValorCursor.getCount();
                    vuelta=0;
                    do {
                       String lCiudad=aValorCursor.getString(0);
                        i.putExtra("CODCIUDAD",lCiudad );

                        vuelta=vuelta+1;

                    } while (aValorCursor.moveToNext());
                }



                setResult(Activity.RESULT_OK,i);
                finish();
            }

        });
        ShView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
