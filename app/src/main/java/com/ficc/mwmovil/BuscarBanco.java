package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ficc.mwmovil.databinding.ActivityBuscarBancoBinding;

public class BuscarBanco extends AppCompatActivity {

    private ActivityBuscarBancoBinding binding;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_banco);
        Bundle Extras=this.getIntent().getExtras();
      final SearchView ShView=(SearchView)findViewById(R.id.Buscador);
        final ListView Milist = (ListView) findViewById(R.id.MyList);

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);

        Cursor Clientes;
        Clientes= BaseDeDatos.getWritableDatabase().rawQuery("select BANFINCOD,BANFINNOM,BanFinCheckValPuc from Bancos group by BANFINCOD,BANFINNOM  ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR


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
            } while (Clientes.moveToNext());
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        Milist.setAdapter(adapter);
        Milist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemPosition = position;
                String itemValue = (String) Milist.getItemAtPosition(position);

                Intent i = new Intent();
                i.putExtra("NOMBANCO", itemValue);
                Cursor aValorCursor;

                    aValorCursor = BaseDeDatos.getWritableDatabase().rawQuery("select BANFINCOD,BANFINNOM,BanFinCheckValPuc from Bancos  where BANFINNOM='"+itemValue+"'  group by BANFINCOD,BANFINNOM ", null);

                if (aValorCursor.getCount()>0) {
                    aValorCursor.moveToFirst();
                    int vuelta=aValorCursor.getCount();
                    vuelta=0;
                    do {
                        String lCiudad=aValorCursor.getString(0);
                        String BanFinCheckValPuc = aValorCursor.getString(2);
                        i.putExtra("CODBANCO",lCiudad );
                        i.putExtra("checkval",BanFinCheckValPuc );
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