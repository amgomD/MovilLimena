package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

public class ResumenPedido extends AppCompatActivity  implements ListViewAdapterProductosRes.AdapterInteractions {

    Bundle Extras;
    SDTProductos[] SDTProductos ;
    ListViewAdapterProductosRes ListViewAdapterProductos;
    Integer UltimaPosicion=0;
    String nitsec;
    Integer clisec;
    Integer plazo;
    String prefijo, plazoNom;
    String invfamcod;
    TextView creditobueno;
    TextView creditomalo;
    TextView txt_impoconsumo;
    TextView txt_total;
     ListView listview_productos;
     Button importar ;

    String clisiniva = "S";
    Integer lisprecod = 0;
    String Exist;
    String Precio;
    int bodcod =0;
     BaseDatos BaseDeDatos;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_pedido);

        Extras=this.getIntent().getExtras();
        nitsec=Extras.getString("nitsec");
        invfamcod=Extras.getString("invfamcod");
        String invsubgrucod=Extras.getString("invsubgrucod");
        String invgrucod=Extras.getString("invgrucod");
        clisec=Extras.getInt("clisec");
        plazo=Extras.getInt("plazo");
        plazoNom = Extras.getString("plazoNom");
        lisprecod=Extras.getInt("lisprecod");
        prefijo=Extras.getString("prefijo");
        importar = findViewById(R.id.importar);
         bodcod =  Extras.getInt("bodega");

        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }



        View fondo = findViewById(R.id.fondo);
        LinearLayout panel = findViewById(R.id.panel);

        fondo.setOnClickListener(v -> cerrarPanel());
        //getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.TrasparenteGris));
        //   getWindow().setLayout(dm.widthPixels*90/100,dm.heightPixels*90/100);


        panel.post(() -> {
            int width = (int) (panel.getRootView().getWidth() * 0.8);
            panel.getLayoutParams().width = width;
            panel.requestLayout();
        });

        final SearchView search_nombre=(SearchView)findViewById(R.id.search_nombre);

        search_nombre.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean  onQueryTextSubmit(String query) {
                if (query.isEmpty()){
                    ListViewAdapterProductos.getFilter().filter(query);
                }else{

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() >= 3){
                    ListViewAdapterProductos.getFilter().filter(newText);
                }else{
                    if (newText.isEmpty()){
                        ListViewAdapterProductos.getFilter().filter(newText);
                    }
                }
                return false;
            }
        });

        creditobueno = findViewById(R.id.creditobueno);
        creditomalo= findViewById(R.id.creditomalo);

        actualizarinfopedido();
       //Nuevo andres--------------------------------------------------------------------------------------
        Cursor cursorCli = BaseDeDatos.getReadableDatabase().rawQuery("select CliIva from clientes where nitsec='"+nitsec+"' and clisec="+clisec+" ", null);//Nuevo andres
        if (cursorCli.getCount()>0){
            cursorCli.moveToFirst();
            do {
                try {
                    clisiniva = cursorCli.getString(0);//Nuevo andres
                }catch (Exception e){
                    Integer Error=1;
                }
            } while (cursorCli.moveToNext());
        }//Nuevo andres----------------------------------------------------------------------------------------------------------

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

    cargarlista();

    listview_productos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView adapterView, final View view, int i, long l) {
            UltimaPosicion=i;
            if (SDTProductos[i].PrecioNeto>0) {
                Intent intent = new Intent(getApplicationContext(), EditarCantidad.class);
                intent.putExtra("nitsec", Extras.getString("nitsec"));
                intent.putExtra("clisec", Extras.getInt("clisec"));
                intent.putExtra("lisprecod", Extras.getInt("lisprecod"));
                intent.putExtra("invgrucod", "");
                intent.putExtra("invsubgrucod", "");
                intent.putExtra("invfamcod", "");
                intent.putExtra("plazo", Extras.getInt("plazo"));
                intent.putExtra("plazoNom", Extras.getString("plazoNom"));
                intent.putExtra("prefijo", Extras.getString("prefijo"));
                intent.putExtra("bodega",Extras.getInt("bodega"));
                intent.putExtra("artsec", SDTProductos[i].ArtSec);
                intent.putExtra("NotaInv", SDTProductos[i].NotaInv);
                intent.putExtra("NotaCar", SDTProductos[i].NotaCar);
                intent.putExtra("PreArtCod", SDTProductos[i].PreArtCod);
                intent.putExtra("position", i);
                startActivityForResult(intent, 3);
                overridePendingTransition(R.anim.slide_in_right, R.anim.no_anim);
            }
        }
    });

    }catch (Exception e){
        int hh=1;
    }

        Totalizar();

        importar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), importarpedido.class);
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


        /*final ToggleButton toggleResumen=(ToggleButton)findViewById(R.id.toggleResumen);

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
        });*/


    }


    public void cargarlista(){

        Time time = new Time();
        time.setToNow();

/*    String Consulta = "select a.ArtSec,ArtCod,ArtNom," + Precio + ",Desc1,Desc2," + Exist + " Exist,artemb,ParConIva,ArtValImp,cant,InvGruNom,artmednomcom,KarUni,KarPrePub,cantcaj,ifNULL(pordesc,Desc1) pordesc,ifNULL(pordesc2,Desc2) pordesc2,ifNULL(pordesc3,0.0) pordesc3,ifNULL(pordesc4,0.0) pordesc4,ifNULL(pordesc5,0.0) pordesc5,ifNULL(pordesc6,0.0) pordesc6,confemp,confprov,confvend from Articulos a " +
            "left join ClientesDevoluciones d on d.nitsec='" + nitsec + "' and d.clisec='" + clisec + "' and d.artsec=a.artsec " +
            "left join pedido p on prefijo='" + prefijo + "' and p.nitsec='" + nitsec + "' and p.clisec=" + clisec + " and p.artsec=a.artsec where cant+cantcaj<>0 and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " order by Exist desc";
    Cursor cursor = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null); //order by nombre

    String Consulta2 = "select m.MovParPremArtSec,ArtCod,ArtNom,1 precio,0.0,0.0,0 Exist,1 artemb,0 ParConIva,0 ArtValImp,MovParPremCant cant,'' InvGruNom,'' artmednomcom,0 KarUni,0 KarPrePub,0 cantcaj,0.0 pordesc,0.0 pordesc2,0.0 pordesc3,0.0 pordesc4,0.0 pordesc5,0.0 pordesc6,0.0 confemp,0.0 confprov,0.0 confvend  from MovParPrem M " +
            "left join articulos a on a.artsec=m.MovParPremArtSec where Prefijo='"+prefijo+"' and MovParNitSec='"+nitsec+"' and MovParCliSec="+clisec+" and MovParPremAno=" + time.year + " and MovParPremMes=" + (time.month + 1) + " and MovParPremDia=" + time.monthDay;
    Cursor cursor2 = BaseDeDatos.getWritableDatabase().rawQuery(Consulta2, null); //order by nombre
*/
//" + Precio + " // se toma lo q ya esta guardado cuanto se genero el pedido

        String Consulta = "select * from(select a.ArtSec,ArtCod,ArtNom, precio,Desc1,Desc2," + Exist + " Exist,artemb,case  cliiva when 'S' then ParConIva else 0 end ParConIva,ArtValImp,cant,InvGruNom,artmednomcom, 0 KarUni , 0  KarPrePub,cantcaj,ifNULL(pordesc,Desc1) pordesc,ifNULL(pordesc2,Desc2) pordesc2,ifNULL(pordesc3,0.0) pordesc3,ifNULL(pordesc4,0.0) pordesc4,ifNULL(pordesc5,0.0) pordesc5,ifNULL(pordesc6,0.0) pordesc6,confemp,confprov,confvend,0 MovParPremSecLin,0 MovParPremSec,cantinf," +
                "  NotaInv, NotaCar, fechapedido , p.PreArtCod, ap.PreArtNom,Prefijo from Articulos a " +
              //  " left join ClientesDevoluciones d on d.nitsec='" + nitsec + "' and d.clisec='" + clisec + "' and d.artsec=a.artsec " +

                " left join Clientes cc on cc.nitsec='" + nitsec + "' and cc.clisec='" + clisec + "'  " +
                "left join pedido p on prefijo='" + prefijo + "' and p.nitsec='" + nitsec + "' and p.clisec=" + clisec + " and p.artsec=a.artsec " +
                " left join articulospresentacion ap on  ap.ArtSec=a.artsec and ap.PreArtcod = ifnull(p.PreArtCod,a.PreArtcod) and ap.LisPrecod =" + lisprecod + " " +
                " where (ifnull(cant,0)+ifnull(cantinf,0)) > 0 and prefijo='" + prefijo + "' and p.nitsec='" + nitsec + "' and p.clisec=" + clisec + " " +
                " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " " +

                " union " +
                "select m.MovParPremArtSec,ArtCod,ArtNom, 0 precio,0.0,0.0,0 Exist,1 artemb,0 ParConIva,0 ArtValImp,MovParPremCant cant,'' InvGruNom,'' artmednomcom,0 KarUni,0 KarPrePub,ifNULL(MovParPremCantCaj,0) cantcaj,0.0 pordesc,0.0 pordesc2,0.0 pordesc3,0.0 pordesc4,0.0 pordesc5,0.0 pordesc6,0.0 confemp,0.0 confprov,0.0 confvend,ifNULL(MovParPremSecLin,0) MovParPremSecLin,MovParPremSec,0 cantinf, 'N' NotaInv,'N' NotaCar,'' fechapedido, " +
                " ifnull(BonParBonPreArtCod,a.PreArtcod), ap.PreArtNom,Prefijo from MovParPrem M " +
                "left join articulos a on a.artsec=m.MovParPremArtSec " +
                " left join articulospresentacion ap on  ap.ArtSec=a.artsec and ap.PreArtcod = ifnull(BonParBonPreArtCod,a.PreArtcod) and ap.LisPrecod =" + lisprecod + " " +
                "where MovParPremCant <> 0 and  Prefijo='" + prefijo + "' and MovParNitSec='" + nitsec + "' and MovParCliSec=" + clisec + " and " +
                " MovParPremAno=" + time.year + " and MovParPremMes=" + (time.month + 1) + " and MovParPremDia=" + time.monthDay + ") jj order by fechapedido asc ";
 // p.PreArtCod =  a.PreArtcod and
        Log.e("Consulta: 1", Consulta);
        try {
            Cursor cursor = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null); //order by nombre

            int vuelta = 0;
            SDTProductos = new SDTProductos[cursor.getCount()]; //+cursor2.getCount()
            if (cursor.getCount() > 0) {

                cursor.moveToFirst();
                do {


                    SDTProductos SDTProductosItem = new SDTProductos();
                    SDTProductosItem.ArtSec = cursor.getString(0);
                    SDTProductosItem.Codigo =  cursor.getString(1);
                    SDTProductosItem.Nombre = cursor.getString(2) + " (" + cursor.getString(11) + ")";
                    SDTProductosItem.Nombrecomercial = cursor.getString(12);
                    SDTProductosItem.Precio = cursor.getDouble(3);
                    SDTProductosItem.PrecioIva = cursor.getDouble(3) * (1 + (Double.valueOf(cursor.getInt(8)) / 100));
                    SDTProductosItem.PrecioNeto = ((cursor.getDouble(3) * (1 + (Double.valueOf(cursor.getInt(8)) / 100))) * (1 - (cursor.getDouble(16) / 100)) * (1 - (cursor.getDouble(17) / 100)) * (1 - (cursor.getDouble(18) / 100)) * (1 - (cursor.getDouble(19) / 100)) * (1 - (cursor.getDouble(20) / 100)) * (1 - (cursor.getDouble(21) / 100))) + cursor.getDouble(9);
                    SDTProductosItem.ConfEmp = cursor.getDouble(22);
                    SDTProductosItem.ConfProv = cursor.getDouble(23);
                    SDTProductosItem.ConfVend = cursor.getDouble(24);
                    SDTProductosItem.Existencia = cursor.getDouble(6);
                    SDTProductosItem.Embalaje = cursor.getInt(7);
                    SDTProductosItem.Cajas = cursor.getInt(15); // cursor.getInt(0);
                    SDTProductosItem.Unidades = cursor.getDouble(10); //cursor.getDouble(0);
                    SDTProductosItem.PreArtCod = cursor.getString(31);
                    SDTProductosItem.Presentacion = cursor.getString(32);
                    Log.e("Unidaees", String.valueOf(cursor.getDouble(27)));

                    SDTProductosItem.Unidadesinf = cursor.getDouble(27); //cursor.getDouble(0);
                    SDTProductosItem.bodcod = bodcod;

                    if (clisiniva.equalsIgnoreCase("S")) {//Nuevo andres
                        SDTProductosItem.Iva = cursor.getInt(8);
                    } else {
                        SDTProductosItem.Iva = 0;
                    }

                    SDTProductosItem.Impoconsumo = cursor.getDouble(9);
                    SDTProductosItem.Dct1 = cursor.getDouble(16);
                    SDTProductosItem.Dct2 = cursor.getDouble(17);
                    SDTProductosItem.Dct3 = cursor.getDouble(18);
                    SDTProductosItem.Dct4 = cursor.getDouble(19);
                    SDTProductosItem.Dct5 = cursor.getDouble(20);
                    SDTProductosItem.Dct6 = cursor.getDouble(21);
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
                    SDTProductosItem.Plazo = 60; //preguntar
                    SDTProductosItem.plazoNom = plazoNom;
                    SDTProductosItem.NitSec = nitsec;
                    SDTProductosItem.NotaCar = cursor.getString(29);
                    SDTProductosItem.NotaInv = cursor.getString(28);
                    SDTProductosItem.CliSec = clisec;
                    SDTProductosItem.pContext = getApplicationContext();
                    SDTProductosItem.Calcular();
                    SDTProductos[vuelta] = SDTProductosItem;
                    vuelta = vuelta + 1;
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("ErrorSQL", e.toString());
        }


        listview_productos = (ListView) findViewById(R.id.listview_productos);
        ListViewAdapterProductos = new ListViewAdapterProductosRes(this, SDTProductos);
        listview_productos.setAdapter(ListViewAdapterProductos);


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1700){
            ListViewAdapterProductos.notifyDataSetChanged();
            Totalizar();
        }else{

            int pos = data.getIntExtra("posicion", -1);
            double nuevo = data.getDoubleExtra("Cantidad", 0);
            double precio = data.getDoubleExtra("PrecioNeto", SDTProductos[pos].PrecioNeto);
            String  NotaInv = data.getStringExtra("NotaInv");
            String  Credito = data.getStringExtra("Credito");
            SDTProductos[pos].ActualizarCantidad();
            SDTProductos[pos].Unidades=nuevo;
            SDTProductos[pos].PrecioNeto=precio;
            SDTProductos[pos].NotaInv=NotaInv;
            ListViewAdapterProductos.notifyDataSetChanged();
            actualizarinfopedido();
            Totalizar();
            cargarlista();
        }
        //if(requestCode == 3) {
            //finish();
            //startActivity(getIntent());
            //if(resultCode == Activity) {

                //ListViewAdapterProductos.Refrescar();
                /* String PrecioString=data.getStringExtra("PRECIO");
                Double Precio= Double.valueOf(PrecioString);
                SDTProductos[UltimaPosicion].Precio=Precio;
                ListViewAdapterProductos.notifyDataSetChanged();*/
           // }

        //}

    }
    public void Totalizar(){


        TextView txt_subtotal=(TextView)findViewById(R.id.txt_abonoori);
        TextView txt_iva=(TextView)findViewById(R.id.txt_retencion);
        TextView txt_impoconsumo=(TextView)findViewById(R.id.txt_retencionica);
        TextView txt_total=(TextView)findViewById(R.id.txt_neto);

        GestorPedidos GestorPedidos=new GestorPedidos();
        SDTResumenPedidos SDTResumenPedidos= GestorPedidos.TotalesPedido(getApplicationContext(),prefijo,nitsec,clisec,"","","");

        txt_subtotal.setText(String.format("%.2f",SDTResumenPedidos.Subtotal));
        txt_iva.setText(String.format("%,d",SDTResumenPedidos.Iva.intValue()));
        txt_impoconsumo.setText(String.format("%,d",SDTResumenPedidos.Impoconsumo.intValue()));
        txt_total.setText(String.format("%.2f",SDTResumenPedidos.Total));

    }


    public void onRestart() {
        super.onRestart();
        super.onResume();
        finish();
        Totalizar();
        startActivity(getIntent());
    }
    private void cerrarPanel() {
        Intent data = new Intent();
        data.putExtra("posicion", Extras.getInt("position"));
        setResult(RESULT_OK, data);

        finish();
        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra("posicion", Extras.getInt("position"));
        setResult(RESULT_OK, data);
        finish();
        overridePendingTransition(R.anim.no_anim, R.anim.slide_out_right);
    }
    public void  actualizarinfopedido(){
        try {
            Time time = new Time();
            time.setToNow();
            TextView creditobueno = findViewById(R.id.creditobueno);
            TextView creditomalo = findViewById(R.id.creditomalo);
            TextView txtValorneto = findViewById(R.id.txtTotal);

            GestorPedidos gestorpedidos = new GestorPedidos();

            BaseDatos BaseDeDatos;
            BaseDeDatos = new BaseDatos(this, "MantisMovil", null, 5);


            String SelectClienteImpactado =
                    "Select count(*) as total, " +

                            // VALOR (normal)
                            "sum( case when ifnull(NotaInv,'N') = 'N' and ifnull(NotaCar,'N') = 'N' then " +
                            "        (precio " +
                            "        * (1-(pordesc/100.0))) " +
                            "        * (1-(pordesc2/100.0)) " +
                            "        * (1-(pordesc3/100.0)) " +
                            "        * (1-(pordesc4/100.0)) " +
                            "        * (1-(pordesc5/100.0)) " +
                            "        * (1-(pordesc6/100.0)) " +
                            "        * cant " +
                            "    else 0 end ) as valor, " +

                            // VALOR BUENO
                            "sum( case when ifnull(NotaInv,'N') = 'S' and ifnull(NotaCar,'N') = 'N' then " +
                            "        precio " +
                            "        * (1-(pordesc/100.0)) " +
                            "        * (1-(pordesc2/100.0)) " +
                            "        * (1-(pordesc3/100.0)) " +
                            "        * (1-(pordesc4/100.0)) " +
                            "        * (1-(pordesc5/100.0)) " +
                            "        * (1-(pordesc6/100.0)) " +
                            "        * cant " +
                            "    else 0 end ) as valorbueno, " +

                            // VALOR MALO
                            "sum( case when ifnull(NotaInv,'N') = 'N' and ifnull(NotaCar,'N') = 'S' then " +
                            "        precio " +
                            "        * (1-(pordesc/100)) " +
                            "        * (1-(pordesc2/100)) " +
                            "        * (1-(pordesc3/100)) " +
                            "        * (1-(pordesc4/100)) " +
                            "        * (1-(pordesc5/100)) " +
                            "        * (1-(pordesc6/100)) " +
                            "        * cant " +
                            "    else 0 end ) as valormalo, " +
                            " Sum(pordesc4) " +

                            "from pedido p " +
                            "where cant + ifnull(cantinf,0) <> 0 " +
                            "and prefijo='" + prefijo + "' " +
                            "and p.nitsec='" + nitsec + "' " +
                            "and p.clisec='" + clisec + "' " +
                            "and pdyear=" + time.year + " " +
                            "and pdmonth=" + (time.month + 1) + " " +
                            "and pdday=" + time.monthDay;

            Log.e("SelectClienteImpactado",SelectClienteImpactado);
            Integer Impactos = 0;
            try{
                Cursor xClientes = BaseDeDatos.getWritableDatabase().rawQuery(SelectClienteImpactado, null);
                if (xClientes.getCount() > 0) {
                    xClientes.moveToFirst();
                    do {

                        Log.e("Descuento;: ",String.valueOf(xClientes.getDouble(4)));

                        double carritoTotal =xClientes.getDouble(1);
                        double vcreditobueno =xClientes.getDouble(2);
                        double vcreditomalo =xClientes.getDouble(3);

                        creditobueno.setText("$-"+String.format("%.2f",vcreditobueno));
                        creditomalo.setText("$-"+String.format("%.2f",vcreditomalo));
                        txtValorneto.setText("$"+String.format("%.2f",carritoTotal));


                    } while (xClientes.moveToNext());
                }






            }catch (Exception e){
                Log.e("EROOOR",e.toString());
            }




        }catch (Exception e){
            Integer error=0;
        }
    }
    @Override
    public void refreshActivity() {
        recreate();
    }
}
