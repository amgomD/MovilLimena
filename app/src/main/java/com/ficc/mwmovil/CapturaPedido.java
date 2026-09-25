package com.ficc.mwmovil;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class CapturaPedido extends AppCompatActivity  {

    Bundle Extras;
    SDTProductos[] SDTProductos ;
    ListViewAdapterProductos ListViewAdapterProductos;
    Integer UltimaPosicion=0;
    String nitsec;
    Integer clisec;
    Integer plazo;
    String prefijo,plazoNom;
    String invfamcod;
    TextView txt_subtotal;
    TextView txt_iva;
    TextView txt_impoconsumo;
    TextView txt_total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captura_pedido);
        getSupportActionBar().hide();
        Extras=this.getIntent().getExtras();
        nitsec=Extras.getString("nitsec");
        invfamcod=Extras.getString("invfamcod");
        String invsubgrucod=Extras.getString("invsubgrucod");
        String invgrucod=Extras.getString("invgrucod");
        clisec=Extras.getInt("clisec");
        plazo=Extras.getInt("plazo");
        plazoNom = Extras.getString("plazoNom");
        Integer lisprecod=Extras.getInt("lisprecod");
        Integer bodcod = Extras.getInt("bodega");
        prefijo=Extras.getString("prefijo");



        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);


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


    String Consulta = "select a.ArtSec,ArtCod,ArtNom," + Precio + ",Desc1,Desc2," + Exist + " Exist,artemb,ParConIva,ArtValImp,cant,InvGruNom,artmednomcom,KarUni,KarPrePub from Articulos a " +
            "left join ClientesDevoluciones d on d.nitsec='" + nitsec + "' and d.clisec='" + clisec + "' and d.artsec=a.artsec " +
            "left join pedido p on prefijo='" + prefijo + "' and p.nitsec='" + nitsec + "' and p.clisec='" + clisec + "' and p.artsec=a.artsec " + WhereConsulta + " order by Exist desc";
    Cursor cursor = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null); //order by nombre

    SDTProductos = new SDTProductos[cursor.getCount()];
    if (cursor.getCount() > 0) {
        int vuelta = 0;
        cursor.moveToFirst();
        do {
            SDTProductos SDTProductosItem = new SDTProductos();
            SDTProductosItem.ArtSec = cursor.getString(0);
            SDTProductosItem.Codigo = cursor.getString(1);
            SDTProductosItem.Nombre = cursor.getString(2) + " (" + cursor.getString(11) + ")";
            SDTProductosItem.Nombrecomercial = cursor.getString(12);
            SDTProductosItem.Precio = cursor.getDouble(3);
            SDTProductosItem.PrecioIva = cursor.getDouble(3) * (1 + (Double.valueOf(cursor.getInt(8)) / 100));
            SDTProductosItem.PrecioNeto = cursor.getDouble(3) * (1 + (Double.valueOf(cursor.getInt(8)) / 100));
            SDTProductosItem.ConfEmp = 0.0;// cursor.getDouble(0);
            SDTProductosItem.ConfProv = 0.0; // cursor.getDouble(0);
            SDTProductosItem.Existencia = cursor.getDouble(6);
            SDTProductosItem.Embalaje = cursor.getInt(7);
            SDTProductosItem.Cajas = 0; // cursor.getInt(0);
            SDTProductosItem.Unidades = cursor.getDouble(10); //cursor.getDouble(0);
            SDTProductosItem.Iva = cursor.getInt(8);
            SDTProductosItem.Impoconsumo = cursor.getDouble(9);
            SDTProductosItem.Dct1 = cursor.getDouble(4);
            SDTProductosItem.Dct2 = cursor.getDouble(5);
            SDTProductosItem.Dct3 = 0.0; // cursor.getDouble(0);
            SDTProductosItem.Dct4 = 0.0; // cursor.getDouble(0);
            SDTProductosItem.Dct5 = 0.0; // cursor.getDouble(0);
            SDTProductosItem.Dct6 = 0.0; // cursor.getDouble(0);
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
            SDTProductosItem.Plazo = plazo;
            SDTProductosItem.plazoNom = plazoNom;
            SDTProductosItem.NitSec = nitsec;
            SDTProductosItem.CliSec = clisec;
            SDTProductosItem.pContext = getApplicationContext();
            SDTProductosItem.Calcular();
            SDTProductos[vuelta] = SDTProductosItem;
            vuelta = vuelta + 1;
        } while (cursor.moveToNext());
    }

    CapturaPedido pCapturaPedido = this;
    final ListView listview_productos = (ListView) findViewById(R.id.listview_productos);
    ListViewAdapterProductos = new ListViewAdapterProductos(this, SDTProductos, pCapturaPedido, CausalNombre);
    listview_productos.setAdapter(ListViewAdapterProductos);

    // ConceptoNCND ("
    //                + "ConNotCod INTEGER(4) ,"
    //                + "ConNotNom text(100))
}catch (Exception e){
    int hh=1;
}

        Totalizar();

        /*
        listview_productos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, final View view, int i, long l) {
                UltimaPosicion=i;
                Intent intent = new Intent(getApplicationContext(), EditarCantidad.class);
                startActivityForResult(intent, 3);
            }
        });*/

        final ToggleButton toggleResumen=(ToggleButton)findViewById(R.id.toggleResumen);

        toggleResumen.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(toggleResumen.isChecked()){
                    ListViewAdapterProductos.getFilter().filter("****");
                }else{

                    ListViewAdapterProductos.getFilter().filter("");
                }
                // your click actions go here
            }
        });

        SearchView search_codigo=(SearchView)findViewById(R.id.search_codigo);
        search_codigo.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ListViewAdapterProductos.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        SearchView search_nombre=(SearchView)findViewById(R.id.search_nombre);
        search_nombre.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ListViewAdapterProductos.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 3) {
            if(resultCode == Activity.RESULT_OK) {
                String PrecioString=data.getStringExtra("PRECIO");
                Double Precio= Double.valueOf(PrecioString);
                SDTProductos[UltimaPosicion].Precio=Precio;
                ListViewAdapterProductos.notifyDataSetChanged();
            }
        }

    }
    public void Totalizar(){


        TextView txt_subtotal=(TextView)findViewById(R.id.txt_retencionica);
        TextView txt_iva=(TextView)findViewById(R.id.txt_abonoori);
        TextView txt_impoconsumo=(TextView)findViewById(R.id.txt_retencion);
        TextView txt_total=(TextView)findViewById(R.id.txt_neto);

        GestorPedidos GestorPedidos=new GestorPedidos();
        SDTResumenPedidos SDTResumenPedidos= GestorPedidos.TotalesPedido(getApplicationContext(),prefijo,nitsec,clisec,"","","");

        txt_subtotal.setText(String.format("%,d",SDTResumenPedidos.Subtotal.intValue()));
        txt_iva.setText(String.format("%,d",SDTResumenPedidos.Iva.intValue()));
        txt_impoconsumo.setText(String.format("%,d",SDTResumenPedidos.Impoconsumo.intValue()));
        txt_total.setText(String.format("%,d",SDTResumenPedidos.Total.intValue()));

    }

}
