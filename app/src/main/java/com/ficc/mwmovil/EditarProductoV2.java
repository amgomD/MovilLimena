package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class EditarProductoV2 extends AppCompatActivity {
    SDTProductos[] SDTProductos ;
    ListViewAdapterProductosv2 ListViewAdapterProductosv2;
    Integer UltimaPosicion=0;
    String nitsec;
    Integer clisec;
    SDTVentasxLinea[] SDTVentasxLinea ;
    ListViewAdapterLinea ListViewAdapterLinea;
    Integer plazo;
    String prefijo, plazoNom;
    String invfamcod;
    String InvCatCod ;
    double carritoTotal = 0.0;
    GridView listview_productos;
    ListView listview_grupos ;
    Button descuentos,Filtro;
    TextView pedidonum,txtTotal,txtValorTotal,tipocredito;
    Button btnVerPedido,Infocliente,btnEnviopedido,btnNuevoPedido;
    ImageButton btnBorrar;
    //ImageButton btnMenu;
    Bundle Extras;
    GlobalVariables gGlobalVariables;
    ConBd conbd;
    String invgrucod,invgruNom, xinvgrucod = "";
    String invsubgrucod;
    String Exist;
    String Precio;
    String WhereConsulta="";
    Integer bodcod, lisprecod;
     BaseDatos BaseDeDatos;
    Set<String> articulosConDescuento = new HashSet<>();
    Set<String> articulosBonificados = new HashSet<>();
    LinearLayout menuCategorias;

    FrameLayout layoutCarga;
    Button btnFijar;

    ImageButton btnMenu;
    Switch switchFijar;

    boolean menuFijo = false;


    String vEmpresa;
    String[] CausalNombre;

    String Canal="XX";
    String SubCanal="XX";
    String CiuCod="XX";
    String xhistorico = "N";
    String mostrarSale = "N";
    int perfil=99;
    String clisiniva = "S";
    ImageView Categorias,home,historico;
    GestorPedidos gestorPedidos;
    //LinearLayout menu;
    final boolean[] xabierto = {false};
    String ArtCantInf = "N";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_producto_v2);




            getSupportActionBar().hide();
            Extras=this.getIntent().getExtras();
            nitsec=Extras.getString("nitsec");
            invfamcod=Extras.getString("invfamcod");
             invsubgrucod=Extras.getString("invsubgrucod");
             invgrucod=Extras.getString("invgrucod");
            clisec=Extras.getInt("clisec");
             lisprecod=Extras.getInt("lisprecod");
             bodcod = Extras.getInt("bodega");
            prefijo=Extras.getString("prefijo");
            plazo=Extras.getInt("plazo");
            plazoNom = Extras.getString("plazoNom");
            InvCatCod= Extras.getString("InvCatCod");
        layoutCarga = findViewById(R.id.layoutCarga);
            conbd = new ConBd();
            TextView bodeganom = findViewById(R.id.bodeganom);
            conbd.Variables();

             gGlobalVariables = GlobalVariables.getInstance();
        vEmpresa=gGlobalVariables.getEmpresa();
            pedidonum = findViewById(R.id.pedidonum);
            listview_productos  = (GridView) findViewById(R.id.listview_productos);
             //btnMenu = findViewById(R.id.btnMenu);
            // menu = findViewById(R.id.menuLateral);

            BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);

        descuentos = findViewById(R.id.descuentos);
        Categorias = findViewById(R.id.Categorias);
        home = findViewById(R.id.home);
        listview_grupos = (ListView) findViewById(R.id.listview_grupos);
        Filtro = findViewById(R.id.Filtro);
        txtTotal = findViewById(R.id.txtTotal);
        txtValorTotal  = findViewById(R.id.txtValorTotal );
        tipocredito  = findViewById(R.id.tipocredito );
        btnNuevoPedido= findViewById(R.id.btnNuevoPedido );
        tipocredito.setVisibility(View.GONE);
        btnVerPedido = findViewById(R.id.btnVerPedido);
        btnEnviopedido = findViewById(R.id.btnEnviopedido);
        Infocliente = findViewById(R.id.Infocliente);
     ImageView compartir = findViewById(R.id.compartir);
        historico = findViewById(R.id.historico);
        btnBorrar = findViewById(R.id.btnBorrar);


        hashARticulos();
        hashBonificado();
        cargarCategorias();
        actualizarinfopedido();
        compartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Time time = new Time();
                time.setToNow();

                String NumPed = prefijo+nitsec +  clisec +  time.year +"-"+  (time.month + 1) +"-"+  time.monthDay  ;

                File file = ReportePedido.generarPDF(EditarProductoV2.this,nitsec,
                        NumPed, clisec,
                        prefijo,lisprecod);

                Intent intent = new Intent(Intent.ACTION_VIEW);

                Uri uri = FileProvider.getUriForFile(
                        EditarProductoV2.this,
                        EditarProductoV2.this.getPackageName() + ".fileprovider",
                        file
                );

                intent.setDataAndType(uri, "application/pdf");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                EditarProductoV2.this.startActivity(intent);
            }
        });


        menuCategorias = findViewById(R.id.menuCategorias);
        ajustarAnchoMenu(menuCategorias, 0.30, 150, 300);



        historico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   layoutCarga.setVisibility(View.VISIBLE);
                if(xhistorico.equalsIgnoreCase("N")){
                    xhistorico = "S";
                    historico.setImageResource(R.mipmap.relojhiscerr);
                }else{

                    xhistorico = "N";
                    historico.setImageResource(R.mipmap.relojhis);
                }

                cargarArticulos(mostrarSale);
               // layoutCarga.setVisibility(View.GONE);

            }
        });



        btnEnviopedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), Wp_ResumenPedidosnew.class);
                i.putExtra("nitsec", Extras.getString("nitsec"));
                i.putExtra("clisec",Extras.getInt("clisec"));
                i.putExtra("prefijo", Extras.getString("prefijo"));

                startActivity(i);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditarProductoV2.this, Principal.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        btnNuevoPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ClientesPedido.class);
                i.putExtra("ruta", "N");
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);


            }
        });

        Infocliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DatosCliente.class);
                intent.putExtra("nitsec", nitsec);
                intent.putExtra("clisec", clisec);
                intent.putExtra("dias", 1005238220);
                intent.putExtra("lisprecod", lisprecod);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.no_anim);
            }
        });


        btnBorrar.setOnClickListener(v -> {

            new AlertDialog.Builder(this)
                    .setTitle("Confirmación")
                    .setMessage("¿Deseas eliminar el pedido?")
                    .setIcon(android.R.drawable.ic_dialog_alert)

                    .setPositiveButton("Sí", (dialog, which) -> {
                        eliminarPedido();
                    })

                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss(); // Cierra el diálogo
                    })

                    .show();
        });

        btnVerPedido.setOnClickListener(new View.OnClickListener() {
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
                startActivityForResult(intent, 133);
                overridePendingTransition(R.anim.slide_in_right, R.anim.no_anim);
            }
        });

        descuentos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mostrarSale.equalsIgnoreCase("N")){
                    mostrarSale = "S";
                    descuentos.setText("SALE X");
                    descuentos.setBackground(ContextCompat.getDrawable(EditarProductoV2.this, R.drawable.bg_button_rojo));
                    descuentos.setTextColor(ContextCompat.getColor(EditarProductoV2.this, R.color.Blanco));
                }else{
                    mostrarSale = "N";
                    descuentos.setText("SALE ✔");
                    descuentos.setBackground(ContextCompat.getDrawable(EditarProductoV2.this, R.drawable.bg_button_sinco));
                    descuentos.setTextColor(ContextCompat.getColor(EditarProductoV2.this, R.color.Blanco));
                }
                actualizarinfopedido();
                cargarArticulos(mostrarSale);

            }
        });

        Filtro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invgrucod ="";
                Filtro.setText("Todos");
                Filtro.setBackground(ContextCompat.getDrawable(EditarProductoV2.this, R.drawable.bg_button_card));
                Filtro.setTextColor(ContextCompat.getColor(EditarProductoV2.this, R.color.Negro));
                actualizarinfopedido();
                cargarArticulos(mostrarSale);
            }
        });
        menuCategorias.setVisibility(View.GONE);
        Categorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(menuFijo){

                    menuCategorias.animate()
                            .translationX(-menuCategorias.getWidth())
                            .setDuration(300)
                            .withEndAction(() -> menuCategorias.setVisibility(View.GONE));
                 //   btnFijar.setText("Mostrar menú");
                    Categorias.setImageResource(R.mipmap.filtericon);
                    menuFijo = false;

                }else{

                   menuCategorias.setVisibility(View.VISIBLE);
                    Categorias.setImageResource(R.mipmap.cerrar);
                    menuCategorias.setTranslationX(-menuCategorias.getWidth());

                    menuCategorias.animate()
                            .translationX(0)
                            .setDuration(300);

                 //   btnFijar.setText("Ocultar menú");
                    menuFijo = true;

                }

                /*
                Intent intent ;
                intent = new Intent(view.getContext(), ComportamientoVenta.class);
                intent.putExtra("nitsec", Extras.getString("nitsec"));
                intent.putExtra("clisec", Extras.getInt("clisec"));
                intent.putExtra("lisprecod", Extras.getInt("lisprecod"));
                intent.putExtra("invgrucod", "");
                intent.putExtra("invsubgrucod", "");
                intent.putExtra("invfamcod", "");
                intent.putExtra("artsec", "");
                intent.putExtra("plazo", plazo);
                intent.putExtra("plazoNom", plazoNom);
                intent.putExtra("prefijo", prefijo);
                intent.putExtra("timeractivo", "S");
                intent.putExtra("bodega",bodcod);

                startActivityForResult(intent, 13);
                overridePendingTransition(R.anim.slide_in_right, R.anim.no_anim);*/
            }
        });


        tipocredito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xinvgrucod ="";
                tipocredito.setText("");
                tipocredito.setBackground(ContextCompat.getDrawable(EditarProductoV2.this, R.drawable.bg_button_card));
                actualizarinfopedido();
                cargarArticulos(mostrarSale);
                tipocredito.setVisibility(View.GONE);

            }
        });

        listview_grupos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, final View view, int i, long l) {
               /* Intent data = new Intent();
                data.putExtra("invgrucod", );
                data.putExtra("invgruNom", SDTVentasxLinea[i].Nombre);


                setResult(RESULT_OK, data);
                finish();
                overridePendingTransition(R.anim.no_anim, R.anim.slide_out_right);*/


                invgrucod = SDTVentasxLinea[i].Codigo;
                invgruNom = SDTVentasxLinea[i].Nombre;
                if(Objects.equals(SDTVentasxLinea[i].Codigo, "CREDITO") ||
             Objects.equals(SDTVentasxLinea[i].Codigo, "CREDITOBU")){
                    tipocredito.setText("X "+SDTVentasxLinea[i].Nombre);
                    tipocredito.setVisibility(View.VISIBLE);
                    if(Objects.equals(SDTVentasxLinea[i].Codigo, "CREDITO")){
                        tipocredito.setBackground(ContextCompat.getDrawable(EditarProductoV2.this, R.drawable.bg_button_credito));

                    }
                    if(Objects.equals(SDTVentasxLinea[i].Codigo, "CREDITOBU")){
                        tipocredito.setBackground(ContextCompat.getDrawable(EditarProductoV2.this, R.drawable.bg_button_creditomalo));
                    }
                    xinvgrucod = SDTVentasxLinea[i].Codigo;
                }



                Filtro.setText(invgruNom+" X");
                Filtro.setBackground(ContextCompat.getDrawable(EditarProductoV2.this, R.drawable.bg_button_rojo));
                Filtro.setTextColor(ContextCompat.getColor(EditarProductoV2.this, R.color.Blanco));
                actualizarinfopedido();
                cargarArticulos(mostrarSale);
            }
        });




        Integer bodcodbon = 0;
        String bodnom = "";
        SQLiteDatabase BdSql=BaseDeDatos.getReadableDatabase();
        Cursor bodeganombre = BaseDeDatos.getWritableDatabase().rawQuery("select BodNom from Bodegas where BodCod = "+bodcod+" ", null);
        if(bodeganombre.getCount() > 0){
            bodeganombre.moveToFirst();
            do{
                bodnom = bodeganombre.getString(0);
            }while (bodeganombre.moveToNext());
        }
        bodeganombre.close();
        bodeganom.setText(bodnom);




/*
        btnMenu.setOnClickListener(v -> {

            if (!xabierto[0]) {
                menu.animate().translationX(0).setDuration(300);
            } else {
                menu.animate().translationX(-menu.getWidth()).setDuration(300);
            }

            xabierto[0] = !xabierto[0];
        });
*/




        Cursor traerArt = BaseDeDatos.getWritableDatabase().rawQuery("select BodCod,BodCheckPred from Bodegas where BodCheckPred = 'N' ", null);
        if(traerArt.getCount() > 0){
            traerArt.moveToFirst();
            do{
                bodcodbon = traerArt.getInt(0);
            }while (traerArt.moveToNext());
        }
        traerArt.close();







            int CliTamCan=0;
            Cursor cursorCli = BaseDeDatos.getReadableDatabase().rawQuery("select CanCod,PerCliCod,CanSubCod,CliTamCan,CiuCod,ifnull(CliIva,'S') CliIva from clientes where nitsec='"+nitsec+"' and clisec="+clisec+" ", null);
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
            }else{
                clisiniva = "S";
            }
            cursorCli.close();

            Cursor cursorNCND = BaseDeDatos.getWritableDatabase().rawQuery("select ConNotCod,ConNotNom from ConceptoNCND ", null); //order by nombre


            CausalNombre = new String[cursorNCND.getCount()];
            if (cursorNCND.getCount()>0){
                int vuelta=0;
                cursorNCND.moveToFirst();
                do {
                    CausalNombre[vuelta]=cursorNCND.getString(1);
                    vuelta=vuelta+1;
                } while (cursorNCND.moveToNext());
            }

            if(!invfamcod.isEmpty()){
                WhereConsulta+=" and invfamcod='"+invfamcod+"' ";
            }
        cursorNCND.close();
      /*  if(!invsubgrucod.isEmpty()){
            WhereConsulta+=" and invsubgrucod='"+invsubgrucod+"' ";
        }
        if(!invgrucod.isEmpty()){
            WhereConsulta+=" and invgrucod='"+invgrucod+"' ";
        }*/


            if (prefijo.equalsIgnoreCase("NC")){
                Exist="KarUni";
                Precio="KarPrePub";

                WhereConsulta += " and KarUni>0 ";

            }else{
                Exist="Exist";
                Precio="Precio"+lisprecod;
            }

            Log.e("Plisprecod",String.valueOf(lisprecod));

            if(prefijo.equalsIgnoreCase("V15") & vEmpresa.trim().equalsIgnoreCase("IBANEZ")){
                Exist = "ExistFec";
            }

        actualizarinfopedido();
        cargarArticulos(mostrarSale);

        listview_productos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, final View view, int i, long l) {

                /*String pos=String.valueOf(i);

                AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                Alerta.setMessage(pos);
                Alerta.setTitle("Alerta");
                Alerta.setPositiveButton("OK", null);
                Alerta.setCancelable(true);
                Alerta.create().show();*/

                if (SDTProductos[i].Bloqueado==0) {
                    UltimaPosicion = i;

                        Intent intent = new Intent(getApplicationContext(), EditarCantidad.class);
                    if(SDTProductos[i].ArtPesFac.equalsIgnoreCase("S")){
                         intent = new Intent(getApplicationContext(), EditarCantidadInf.class);
                    }
              Log.e("lisprecod editar",String.valueOf(lisprecod));
                    intent.putExtra("nitsec", Extras.getString("nitsec"));
                    intent.putExtra("clisec", Extras.getInt("clisec"));
                    intent.putExtra("lisprecod",lisprecod);
                    intent.putExtra("invgrucod", "");
                    intent.putExtra("invsubgrucod", "");
                    intent.putExtra("invfamcod", "");
                    intent.putExtra("prefijo", Extras.getString("prefijo"));
                    intent.putExtra("artsec", SDTProductos[i].ArtSec);
                    intent.putExtra("plazo", plazo);
                    intent.putExtra("plazoNom", plazoNom);
                    intent.putExtra("bodega", Extras.getInt("bodega"));
                    startActivityForResult(intent, 3);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.no_anim);
                }else{
                    AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                    Alerta.setMessage("Producto restrigido para este cliente, restriccion # "+SDTProductos[i].Bloqueado);
                    Alerta.setTitle("Alerta");
                    Alerta.setPositiveButton("OK", null);
                    Alerta.setCancelable(true);
                    Alerta.create().show();
                }
            }
        });


        listview_productos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(), DescuentosFicc.class);
                intent.putExtra("timeractivo", "N");
                intent.putExtra("invgrucod", "");
                intent.putExtra("invsubgrucod", "");
                intent.putExtra("invfamcod", "");
                intent.putExtra("artsec", SDTProductos[i].ArtSec);
                startActivity(intent);
                return true;
            }
        });


        final SearchView search_nombre=(SearchView)findViewById(R.id.search_nombre);
        final SearchView search_codigo=(SearchView)findViewById(R.id.search_codigo);
        final ToggleButton toggleButton=(ToggleButton)findViewById(R.id.toggleButton);

        search_nombre.setVisibility(View.VISIBLE);
        search_codigo.setVisibility(View.GONE);

        toggleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(toggleButton.isChecked()){
                   // ListViewAdapterProductosv2.getFilter().filter("****");
                    search_nombre.setVisibility(View.GONE);
                    search_codigo.setVisibility(View.VISIBLE);
                }else{
                    search_nombre.setVisibility(View.VISIBLE);
                    search_codigo.setVisibility(View.GONE);
                   // ListViewAdapterProductosv2.getFilter().filter("");
                }
                // your click actions go here
            }
        });



        search_nombre.setOnClickListener(v -> {
            search_nombre.setIconified(false);
            search_nombre.requestFocus();
        });
        //SearchView search_nombre=(SearchView)findViewById(R.id.search_nombre);
        search_nombre.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean  onQueryTextSubmit(String query) {
                if (query.isEmpty()){
                    ListViewAdapterProductosv2.getFilter().filter(query);
                }else{

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() >= 3){
                    ListViewAdapterProductosv2.getFilter().filter(newText);
                }else{
                    if (newText.isEmpty()){
                        ListViewAdapterProductosv2.getFilter().filter(newText);
                    }
                }
                return false;
            }
        });
        search_codigo.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() >= 3){
                    ListViewAdapterProductosv2.getFilter().filter(newText);
                }
                return false;
            }
        });
    }

public void cargarArticulos(String tienedcto){
    layoutCarga.setVisibility(View.VISIBLE);

    new Thread(() -> {


    try {

        int vSucCod=gGlobalVariables.getSucCod();
        int vAliNegCod=gGlobalVariables.getAliNegCod();
        String vUsuario=gGlobalVariables.getUsuario();
        Cursor Clientes = null;
        String Consulta;
        String notainvini = "";
        String notacarini = "";

        Time time = new Time();
        time.setToNow();

        conbd.Variables();
        String mantisficc = conbd.MantisFicc;
        //ArtIndMpm<>'S'
        WhereConsulta="";
        if(!InvCatCod.isEmpty() && mantisficc.equalsIgnoreCase("S")){
            WhereConsulta+=" and InvCatCod='"+InvCatCod+"' ";
        }
        if(!Objects.equals(invgrucod, "CREDITO") && !Objects.equals(invgrucod, "CREDITOBU")
                &&  !invgrucod.isEmpty() && mantisficc.equalsIgnoreCase("S")){
            WhereConsulta+=" and invgrucod='"+invgrucod+"' ";
        }




        Log.e("WhereConsulta>",WhereConsulta);



        Consulta = "select a.ArtSec,ArtCod,ArtNom," + Precio + " ,Desc1,Desc2," + Exist + " Exist,artemb,case cliiva when 'S' then ParConIva else 0 end ParConIva,ArtValImp,cant,InvGruNom,artmednomcom,0 KarUni ,0 KarPrePub," +
                " ifNULL((select MovParLinDes from MovParLinea  where MovParLinArtSec=a.artsec),Desc1) pordesc," +
                " ifNULL((select MovParArtDetDesc from MovParArt where  MovParArtDetArtSec=a.artsec and   (CANALES='XX,' OR CANALES like '%,'|| "+Canal+" ||',%')  and  (CLIENTES='XX,' OR CLIENTES like '%,'|| "+nitsec+" ||',%')  and (CLIENTESEX='XX,' OR CLIENTESEX NOT like '%,'|| "+nitsec+" ||',%')      ),Desc2)+ifNULL((select PerCliDetDes1 from PerfilClientesClase pc where pc.ClaArtCod=a.ClaArtCod and pc.PerCliCod="+perfil+"),0) pordesc2," +
                " ifNULL(pordesc3,0.0) pordesc3,ifNULL(pordesc4,0.0) pordesc4,ifNULL(pordesc5,0.0) pordesc5," +
                " ifNULL(pordesc6,0.0) pordesc6,ifNULL(TieneDescEsp,'') TieneDescEsp,0 ConVenGruSec, ifnull(ex.ArtExiAct/ifnull(ap.PreArtFacConVal,1),0) ArtExiAct," +
                "  a.InvGruCod,ifnull(ArtCantInf,'N')ArtCantInf, ap.PreArtNom , " +
                " ifnull(p.NotaInv,'N') NotaInv,ifnull(p.NotaCar,'N') NotaCar,ArtImgBlob,ifnull(cantinf,0)cantinf,ifnull(precio, " + Precio + ") precio, " +
                " ifnull(P.PreArtCod, a.PreArtCod ) PreArtCod, ArtIndMpm ,  ifnull(GROUP_CONCAT(\n" +
                "    '(' || cd.Fecha || ') ' || " +
                "    cd.KarUni || ' @: ' || " +
                "    cd.KarPrePub || ' - ', " +
                "    '\n' " +
                ") ,'') AS Historico  from Articulos a ";


        if(xhistorico.equalsIgnoreCase("S")){
            Consulta = Consulta +"INNER JOIN ClientesDevoluciones cd \n" +
                    "ON cd.ArtSec = a.ArtSec " +
                    "AND cd.NitSec = '"+ nitsec +"' \n" +
                    "AND cd.CliSec = '"+ clisec +"'";


             /*Consulta = Consulta +"WHERE EXISTS (\n" +
                     "    SELECT 1\n" +
                     "    FROM ClientesDevoluciones cd\n" +
                     "    WHERE cd.ArtSec = a.ArtSec\n" +
                     "      AND cd.NitSec = '"+ nitsec +"' " +
                     "      AND cd.CliSec = '"+ clisec +"' " +
                     ")";*/

        }else{
            Consulta = Consulta + " " +
                    " left join ClientesDevoluciones cd " +
                    " on cd.nitsec='" + nitsec + "' " +
                    " and cd.clisec='" + clisec + "' " +
                    " and cd.artsec=a.artsec ";
        }




                Consulta = Consulta +"left join Clientes cc on cc.nitsec='" + nitsec + "' and cc.clisec='" + clisec + "'  " +
                "left join ArticulosExi ex on ex.ArtSec=a.artsec and ArtBodCod = "+bodcod+" " +
                " left join ( " +
                "SELECT 'I'  AS cat " +
                "UNION ALL " +
                "SELECT 'C'  " +
                "UNION ALL " +
                " SELECT 'D'  ) mat ";



        if(Objects.equals(xinvgrucod, "CREDITO")){
            Consulta = Consulta+" left join pedido p on  cant+ifnull(cantinf,0) > 0 " +
                    " AND (ifnull(p.NotaCar,'N')='S' )";
        }else{
            if(Objects.equals(xinvgrucod, "CREDITOBU")){

                Consulta = Consulta+" left join pedido p on  cant+ifnull(cantinf,0) > 0 " +
                        " AND (ifnull(p.NotaInv,'N')='S') ";

            } else{
                Consulta = Consulta+" left join pedido p on  cant+ifnull(cantinf,0) > 0 " +
                        " AND (ifnull(p.NotaInv,'N')='S' or cat<>'I') AND (ifnull(p.NotaCar,'N')='S' or (cat<>'C' ))" +
                        " AND ((ifnull(p.NotaCar,'N')<>'S' AND ifnull(p.NotaInv,'N')<>'S') or cat<>'D')";
            }
        }








      Consulta = Consulta+" and prefijo='" + prefijo + "' and p.nitsec='" + nitsec + "' and p.clisec='" + clisec + "'" +
                " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  "+
                " and p.artsec=a.artsec " ;



        Consulta =  Consulta + " left join articulospresentacion ap on  ap.ArtSec=a.artsec and ap.PreArtcod = ifnull(p.PreArtCod,a.PreArtcod) and ap.LisPrecod ="+lisprecod+" "  +
                " where (cant+ifnull(cantinf,0) > 0 or cat ='D')    " + WhereConsulta + " " ;



        if(Objects.equals(xinvgrucod, "CREDITO")){
            Consulta =  Consulta + " group by a.artsec,ifnull(p.NotaInv,'N'), ifnull(p.NotaCar,'N'),ifnull(P.PreArtCod, a.PreArtCod )   order by Artnom ; ";  //AND " + Precio + ">10

        }else{
            if(Objects.equals(xinvgrucod, "CREDITOBU")){

                Consulta =  Consulta + " group by a.artsec,ifnull(p.NotaInv,'N'), ifnull(p.NotaCar,'N'),ifnull(P.PreArtCod, a.PreArtCod )   order by Artnom ; ";  //AND " + Precio + ">10


            } else{
                Consulta =  Consulta + " group by cat,a.artsec,ifnull(p.NotaInv,'N'), ifnull(p.NotaCar,'N'),ifnull(P.PreArtCod, a.PreArtCod )   order by Artnom ; ";  //AND " + Precio + ">10

            }
        }






// and " + Precio + " > 0
        Log.e("Consulta",Consulta);
        Cursor cursor =null;
        try{
            cursor = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null); //order by nombre
            SDTProductos = new SDTProductos[cursor.getCount()];
        }catch (Exception e){
            Log.e("Errore",e.toString());
        }

        if (cursor.getCount() > 0) {
            int vuelta = 0;
            double precioNu = 0;
            cursor.moveToFirst();
            do {
                runOnUiThread(() -> {
                pedidonum.setText("#"+prefijo+vUsuario+nitsec+String.valueOf(clisec)+String.valueOf(time.year)+"-"+String.valueOf((time.month + 1))+"-"+String.valueOf(time.monthDay ));
                });
                precioNu = 0;
                precioNu = cursor.getDouble(3);


                /*if(vEmpresa.equalsIgnoreCase("Brillo") || vEmpresa.equalsIgnoreCase("Surtimarcas")){
                    precioNu = cursor.getDouble(3);
                }else{
                    String consultaart = "select precioesp from PreciosEspeciales where peNitSec = '"+nitsec+"' and peArtSec = '"+cursor.getString(0).trim()+"'";
                    Cursor cursorart = BaseDeDatos.getWritableDatabase().rawQuery(consultaart, null);
                    cursorart.moveToFirst();

                    if(cursorart.getCount() > 0){
                        cursorart.moveToFirst();
                        precioNu = cursorart.getDouble(0);
                    }else{

                    }
                    cursorart.close();
                }*/



/*
                if(vEmpresa.equalsIgnoreCase("Brillo") || vEmpresa.equalsIgnoreCase("IBANEZPRU") || vEmpresa.equalsIgnoreCase("IBANEZ")|| vEmpresa.equalsIgnoreCase("Surtimarcas") ){

                }else{
                    try{
                        String consultardesc = "select clidesdcto from DescGrupo where NitSec = '"+nitsec+"' and Clisec = "+clisec+" and clidesinvgrucod = '"+cursor.getString(24)+"' ";
                        Cursor cursordesc = BaseDeDatos.getWritableDatabase().rawQuery(consultardesc, null);
                        cursordesc.moveToFirst();
                        if(cursordesc.getCount() > 0){
                            cursordesc.moveToFirst();
                            precioNu = precioNu/(1-(cursordesc.getDouble(0)/100));
                        }
                        cursordesc.close();

                    }catch (Exception e){
                        Log.e("eror precio",e.toString());
                    }
                }*/

                String tienepromo = cursor.getString(21);






                if(mantisficc.equalsIgnoreCase("S")){
                 /*String Consultades = "select ARTICULOS from descuentos where ARTICULOS like '%,"+cursor.getString(0)+cursor.getString(32)+",%' ";
                    Cursor cursorartdes = BaseDeDatos.getWritableDatabase().rawQuery(Consultades, null);
                    cursorartdes.moveToFirst();

                    if(cursorartdes.getCount() > 0){
                        cursorartdes.moveToFirst();
                        tienepromo = "S";

                    }else{
                        tienepromo = cursor.getString(21);

                    }
                    cursorartdes.close();*/



                        String codigoArticulo =
                                cursor.getString(0) + cursor.getString(32);
                        if (articulosConDescuento.contains(codigoArticulo)) {
                            tienepromo = "S";
                        }
                }









                String bonificado = "N";
                if(mantisficc.equalsIgnoreCase("S")){
                   /* String Consultades = "select BonProArtSec from BonificacionesProductodet d " +
                            " where BonProArtSec ='"+cursor.getString(0)+"' " ;
                    ;
                    Cursor cursorartdes = BaseDeDatos.getWritableDatabase().rawQuery(Consultades, null);
                    cursorartdes.moveToFirst();
                    if(cursorartdes.getCount() > 0){
                        cursorartdes.moveToFirst();
                        bonificado = "S";
                    }else{
                        bonificado =  "N";
                    }
                    cursorartdes.close();*/

                    String artSec = cursor.getString(0);

                    if (articulosBonificados.contains(artSec)) {
                        bonificado = "S";
                    }


                }



                SDTProductos SDTProductosItem = new SDTProductos();
                if(cursor.getString(0).equalsIgnoreCase("17761")){
                    Log.e("saliooo: ",cursor.getString(0)+ cursor.getString(27));
                }
                SDTProductosItem.ArtSec = cursor.getString(0);

                SDTProductosItem.ArtPesFac = cursor.getString(25);
                SDTProductosItem.Presentacion = cursor.getString(26);


                String hex = cursor.getString(29);
                byte[] imgBytes = hexToBytes(hex);
                SDTProductosItem.ArtImgBlob = imgBytes;
                SDTProductosItem.Unidadesinf = cursor.getDouble(30);
                SDTProductosItem.Nombre = cursor.getString(2) ;
                SDTProductosItem.totalpedido = carritoTotal;
                SDTProductosItem.Historico = "";
               // if(xhistorico.equalsIgnoreCase("S")){
                    SDTProductosItem.Historico = cursor.getString(34);//;historico();
               // }


                if(xinvgrucod.equalsIgnoreCase("CREDITO") || cursor.getString(28).equalsIgnoreCase("S")){
                    SDTProductosItem.Codigo = "C"+cursor.getString(1);
                }else{
                    SDTProductosItem.Codigo = cursor.getString(1);
                }

                if(cursor.getString(27).equalsIgnoreCase("S")){

                }else{
                    /*precioNu = cursor.getDouble(3);
                    SDTProductosItem.PreArtCod = "" ;*/
                }


                precioNu = cursor.getDouble(31);
                SDTProductosItem.Nombrecomercial = cursor.getString(11); //grupo
                Double precio = precioNu;
                // Log.e("clisiniva",clisiniva);
                // Log.e("Precio art",String.valueOf(precio));

                if(clisiniva.equalsIgnoreCase("S")){//Nuevo andres
                    SDTProductosItem.Iva = cursor.getInt(8);
                }else{
                    SDTProductosItem.Iva = 0;
                }

                SDTProductosItem.PreArtCod = cursor.getString(32) ;
                SDTProductosItem.ArtIndMpm = cursor.getString(33) ;


                SDTProductosItem.Precio =   precio;
                SDTProductosItem.PrecioIva = precio * (1 + (Double.valueOf(cursor.getInt(8)) / 100));
                SDTProductosItem.PrecioNeto = (precio* (1 + (Double.valueOf(cursor.getInt(8)) / 100)) *(1-(cursor.getDouble(4)/100)) *(1-(cursor.getDouble(16)/100)) *(1-(cursor.getDouble(17)/100)) *(1-(cursor.getDouble(18)/100)) *(1-(cursor.getDouble(19)/100))  )  +cursor.getDouble(9);   //*(1-(cursor.getDouble(20)/100))
                SDTProductosItem.ConfEmp = 0.0;// cursor.getDouble(0);
                SDTProductosItem.ConfProv = 0.0; // cursor.getDouble(0);
        if(cursor.getDouble(23)< 0.0 ){
            SDTProductosItem.Existencia = 0.0;
        }else{
            SDTProductosItem.Existencia = cursor.getDouble(23);
        }





                SDTProductosItem.bodcod = bodcod;

                SDTProductosItem.Embalaje = cursor.getInt(7);
                SDTProductosItem.Cajas = 0; // cursor.getInt(0);
                SDTProductosItem.Unidades = cursor.getDouble(10); //cursor.getDouble(0);
                if(cursor.getString(27).equalsIgnoreCase("N") && cursor.getString(28).equalsIgnoreCase("N") ){
                if( cursor.getDouble(23) <= 0 ){

                    SDTProductosItem.Unidades =  SDTProductosItem.Unidadesinf;

                }else{
                    SDTProductosItem.Unidades = cursor.getDouble(10); //cursor.getDouble(0);
                }
                }


                if(xinvgrucod.equalsIgnoreCase("CREDITO")) {
                    SDTProductosItem.NotaCar = "S";
                    if(cursor.getString(28).equalsIgnoreCase("N")){
                        SDTProductosItem.Unidades = 0.0;
                    }
                }else{
                    SDTProductosItem.NotaCar = cursor.getString(28);
                }


                if(xinvgrucod.equalsIgnoreCase("CREDITOBU")) {
                    SDTProductosItem.NotaInv = "S";
                    if(cursor.getString(27).equalsIgnoreCase("N")){
                        SDTProductosItem.Unidades = 0.0;
                    }
                }else{
                    SDTProductosItem.NotaInv = cursor.getString(27);
                }








                SDTProductosItem.Impoconsumo = cursor.getDouble(9);
                SDTProductosItem.Dct1 = cursor.getDouble(15); //cursor.getDouble(4)


                SDTProductosItem.Dct2 = cursor.getDouble(16); //cursor.getDouble(5)


                SDTProductosItem.Dct3 = 0.0; // ursocursor.getDouble(0);
                SDTProductosItem.Dct4 = 0.0; // cr.getDouble(0);
                SDTProductosItem.Dct5 = 0.0; // cursor.getDouble(0);
                SDTProductosItem.Dct6 = 0.0; // cursor.getDouble(0);
                SDTProductosItem.Dct7 = 0.0; // cursor.getDouble(0);
                SDTProductosItem.Dct8 = 0.0; // cursor.getDouble(0);
                SDTProductosItem.Dct1no = 0.0 ; //cursor.getDouble(4)
                SDTProductosItem.Dct2no = 0.0 ; //cursor.getDouble(5)
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
                SDTProductosItem.LisPrecod = lisprecod;//  Extras.getInt("lisprecod");

                SDTProductosItem.TienePromo = tienepromo;
                SDTProductosItem.TieneBono = bonificado ;

                SDTProductosItem.FiltroDcto = tienedcto;

                SDTProductosItem.Bloqueado=cursor.getInt(22);
                SDTProductosItem.pContext = getApplicationContext();
                String partsec=SDTProductosItem.ArtSec;
/*
                        try {
                            String ConsultaDcto = "SELECT  * FROM (";
                            ConsultaDcto += "select MovParBonFecMod Fecha,a.artcod ArtCod,'(Bn) Compra ' || MovParBonCant || ' Unid de ' || Artnom ||' Bonf ' || (select MovParBonDetCant || ' Unid de ' || aa.artnom || ' ' from MovParBonBonificados mb left join articulos aa on aa.artsec=mb.MovParBonArtSec where mb.MovParBonSec=m.MovParBonSec  LIMIT 1 ) Texto from MovParBonProdBon m left join articulos a on a.artsec=m.MovParBonEncArtSec where a.artsec='" + partsec + "'"  ;
                            ConsultaDcto += " Union all ";
                            ConsultaDcto += "select MovParMixFecMod Fecha,'1' ArtCod,'(Mx)' || MovParMixNom Texto from MovParMix m WHERE MovParMixSec IN (select MovParMixSec from MovParMixArticulos mb left join articulos a on a.artsec=mb.MovParMixDetArtSec where a.artsec='" + partsec + "'   group by MovParMixSec) ";
                            ConsultaDcto += " Union all ";
                            ConsultaDcto += "select MovParArtFecMod Fecha,ArtCod,'(Prm) ' || desc2 || '% EN ' || Artnom Texto from Articulos a WHERE  desc2<>0  and a.artsec='" + partsec + "'";
                            ConsultaDcto += " Union all ";
                            ConsultaDcto += "select MovParEscfecmod Fecha,'1' ArtCod,'(Esc) ' || MovParEscDesc from MovParEsc mb left join articulos a on a.artsec=mb.MovParEscArtSec  WHERE  MovParEscDesc1<>0  and a.artsec='" + partsec + "'  GROUP BY MovParEscfecmod,MovParEscDesc ";
                            ConsultaDcto += ") KK ORDER BY Fecha,ArtCod DESC";
                            Cursor cursortienedes = BaseDeDatos.getWritableDatabase().rawQuery(ConsultaDcto, null); //order by nombre
                            if (cursortienedes.getCount() > 0) {

                            }
                        }catch (Exception e){
                            int hh=0;
                        }*/
                //SDTProductosItem.Calcular();

                    SDTProductos[vuelta] = SDTProductosItem;
                    vuelta = vuelta + 1;



            } while (cursor.moveToNext());
            cursor.close();

        }



        EditarProductoV2 pEditarProductoV2 = this;
        if (tienedcto.equalsIgnoreCase("S")) {

            List<SDTProductos> listaFiltrada = new ArrayList<>();

            for (SDTProductos item : SDTProductos) {
                if ((item.TienePromo != null && item.TienePromo.equalsIgnoreCase("S")) || item.TieneBono != null && item.TieneBono.equalsIgnoreCase("S")) {
                    listaFiltrada.add(item);
                }
            }

            SDTProductos = listaFiltrada.toArray(new SDTProductos[0]);
        }

        if(xinvgrucod.equalsIgnoreCase("CREDITO")){

            List<SDTProductos> listaFiltrada = new ArrayList<>();

            for (SDTProductos item : SDTProductos) {
                if (!item.NotaInv.equalsIgnoreCase("S")) {
                    listaFiltrada.add(item);
                }
            }

            SDTProductos = listaFiltrada.toArray(new SDTProductos[0]);
        }

        if(xinvgrucod.equalsIgnoreCase("CREDITOBU")){

            List<SDTProductos> listaFiltrada = new ArrayList<>();

            for (SDTProductos item : SDTProductos) {
                if (!item.NotaCar.equalsIgnoreCase("S")) {
                    listaFiltrada.add(item);
                }
            }

            SDTProductos = listaFiltrada.toArray(new SDTProductos[0]);
        }



        runOnUiThread(() -> {

        ListViewAdapterProductosv2 = new ListViewAdapterProductosv2(this,this, SDTProductos, pEditarProductoV2, CausalNombre);
        //  ListViewAdapterProductosv2.getFilter().filter("xxxxxxxxx");
        listview_productos.setAdapter(ListViewAdapterProductosv2);
        });
    }catch(Exception e){
        Log.e("Errolistvue",e.toString());
        int aa=0;
    }finally {

        runOnUiThread(() -> {
            layoutCarga.setVisibility(View.GONE);
        });
    }


    }).start();
}

public void hashBonificado(){

    Cursor cursorBonificados = BaseDeDatos.getReadableDatabase().rawQuery(
            "SELECT BonProArtSec FROM BonificacionesProductodet " +
                    "WHERE BonProArtSec IS NOT NULL",
            null
    );

    while (cursorBonificados.moveToNext()) {

        String artSec = cursorBonificados.getString(0);

        if (artSec != null && !artSec.isEmpty()) {
            articulosBonificados.add(artSec.trim());
        }
    }

    cursorBonificados.close();
}
public void hashARticulos(){
    Cursor cursorDescuentos = BaseDeDatos.getReadableDatabase().rawQuery(
            "SELECT ARTICULOS FROM descuentos WHERE ARTICULOS IS NOT NULL",
            null
    );

    while (cursorDescuentos.moveToNext()) {

        String articulos = cursorDescuentos.getString(0);

        if (articulos != null && !articulos.isEmpty()) {

            String[] lista = articulos.split(",");

            for (String articulo : lista) {

                if (!articulo.trim().isEmpty()) {
                    articulosConDescuento.add(articulo.trim());
                }
            }
        }
    }

    cursorDescuentos.close();
}

    public  byte[] hexToBytes(String hex) {

        if (hex.startsWith("0x")) {
            hex = hex.substring(2);
        }

        int len = hex.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] =
                    (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                            + Character.digit(hex.charAt(i+1), 16));
        }

        return data;
    }

    public Double precioesp(String NitSec,String Artsec,Double precioArt){
   //     Log.e("Entro","hola");

        Double precioNu = 0.0;
        /* BaseDatos BaseDeDatos2;
        BaseDeDatos2 = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
        String consulta = "select precioesp from PreciosEspeciales where peNitSec = '"+NitSec+"' and peArtSec = '"+Artsec+"'";
        Cursor cursor = BaseDeDatos2.getWritableDatabase().rawQuery(consulta, null);
        cursor.moveToFirst();
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

    /*@Override
    public void onBackPressed() {
        if (xabierto[0]) {
            menu.animate().translationX(-menu.getWidth()).setDuration(300);
            xabierto[0] = false;
        } else {
            super.onBackPressed();
        }
    }*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 3) {
            if(resultCode == Activity.RESULT_OK) {
                int pos = data.getIntExtra("posicion", -1);
                double nuevo = data.getDoubleExtra("Cantidad", 0);
                double precio = data.getDoubleExtra("PrecioNeto", SDTProductos[pos].PrecioNeto);
                String  NotaInv = data.getStringExtra("NotaInv");
                String  Credito = data.getStringExtra("Credito");

                SDTProductos[pos].Unidades=nuevo;
                SDTProductos[pos].PrecioNeto=precio;
                SDTProductos[pos].NotaInv=NotaInv;

                ListViewAdapterProductosv2.notifyDataSetChanged();
                     actualizarinfopedido();
                    // cargarArticulos(mostrarSale);

            }
        }

        if(requestCode == 13) {
            if(resultCode == Activity.RESULT_OK) {
                invgrucod = data.getStringExtra("invgrucod");
                invgruNom = data.getStringExtra("invgruNom");
                Filtro.setText(invgruNom+" X");
                Filtro.setBackground(ContextCompat.getDrawable(EditarProductoV2.this, R.drawable.bg_button_rojo));
                Filtro.setTextColor(ContextCompat.getColor(EditarProductoV2.this, R.color.Blanco));
                actualizarinfopedido();
                cargarArticulos(mostrarSale);
            }
        }

        if(requestCode == 133){
            actualizarinfopedido();
            cargarArticulos(mostrarSale);
        }

    }

    public void  actualizarinfopedido(){
        try {
            Time time = new Time();
            time.setToNow();
            TextView creditobueno = findViewById(R.id.creditobueno);
            TextView creditomalo = findViewById(R.id.creditomalo);
            TextView txtValorneto = findViewById(R.id.txtValorneto);

            GestorPedidos gestorpedidos = new GestorPedidos();
            SDTResumenPedidos sdtResumenPedidos = gestorpedidos.TotalesPedido(getApplicationContext(), prefijo, nitsec, clisec,"","","");

            BaseDatos BaseDeDatos;
            BaseDeDatos = new BaseDatos(this, "MantisMovil", null, 5);

           /* String SelectClienteImpactado = "Select count(*) as total,    sum(\n" +
                    "        precio\n" +
                    "        * (1-(pordesc/100))\n" +
                    "        * (1-(pordesc2/100))\n" +
                    "        * (1-(pordesc3/100))\n" +
                    "        * (1-(pordesc4/100))\n" +
                    "        * (1-(pordesc5/100))\n" +
                    "        * (1-(pordesc6/100))\n" +
                    "        * cant\n" +
                    "    ) as valor  " +
                    " from pedido p where ifnull(NotaInv,'N') = 'N' and ifnull(NotaCar,'N')  = 'N' and   cant+ifnull(cantinf,0)<>0 and prefijo='" + prefijo + "' and p.nitsec='" + nitsec + "' and p.clisec='" + clisec + "' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "" +
                    " ";
*/

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
                        txtTotal.setText("Total items: "+String.valueOf(xClientes.getInt(0)));
                        carritoTotal =xClientes.getDouble(1);
                        double vcreditobueno =xClientes.getDouble(2);
                        double vcreditomalo =xClientes.getDouble(3);

                        txtValorTotal.setText("Valor pedido: $"+String.format("%.2f",carritoTotal));
                        creditobueno.setText("Valor credito bueno: $-"+String.format("%.2f",vcreditobueno));
                        creditomalo.setText("Valor credito malo: $-"+String.format("%.2f",vcreditomalo));
                        txtValorneto.setText("Valor neto: $"+String.format("%.2f",carritoTotal-vcreditomalo-vcreditobueno));


                    } while (xClientes.moveToNext());
                }






            }catch (Exception e){
                Log.e("EROOOR",e.toString());
            }




        }catch (Exception e){
            Integer error=0;
        }
    }


    public void eliminarPedido(){
        Time time = new Time();
        time.setToNow();
        BaseDeDatos.getWritableDatabase().execSQL("delete from pedido where nitsec='" + nitsec + "' and clisec=" + clisec + " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo='" + prefijo+ "' ");
        BaseDeDatos.getWritableDatabase().execSQL("delete from movparprem where MovParNitSec='" + nitsec + "' and MovParCliSec=" + clisec + " and MovParPremAno=" + time.year + " and MovParPremMes=" + (time.month + 1) + " and MovParPremDia=" + time.monthDay + "  and prefijo='" + prefijo+ "' ");


        actualizarinfopedido();
        cargarArticulos(mostrarSale);

    }


    public void cargarCategorias(){
        Time time = new Time();
        time.setToNow();
       // BaseDeDatos.getWritableDatabase().execSQL("delete from pedido where nitsec='" + nitsec + "' and clisec=" + clisec + " and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo='" + prefijo+ "' ");
        Cursor cursor = BaseDeDatos.getWritableDatabase().rawQuery("select InvGruCod,InvGruNom" +
                " ,0 Venta " +
                " ,0 bonificado " +
                " ,0 mixto " +
                " ,0 Escala " +
                " ,0 Promo " +
                " ,0 Linea " +
                " from inventariogrupo ig  ", null); //order by nombre



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


        ListViewAdapterLinea = new ListViewAdapterLinea(this, SDTVentasxLinea);
        listview_grupos.setAdapter(ListViewAdapterLinea);



    }


    public void ajustarAnchoMenu(View menu, double porcentaje, int minDp, int maxDp) {

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int screenWidth = metrics.widthPixels;

        int width = (int) (screenWidth * porcentaje);

        int minPx = dpToPx(minDp);
        int maxPx = dpToPx(maxDp);

        if (width < minPx) width = minPx;
        if (width > maxPx) width = maxPx;

        ViewGroup.LayoutParams params = menu.getLayoutParams();
        params.width = width;
        menu.setLayoutParams(params);
    }

    public int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    public String  historico(String Artsec){
        String historicolong = "";
        TextView descuentostext = findViewById(R.id.descuentostext);
        TextView Historialtext = findViewById(R.id.Historialtext);

        String consultaHistorial = "select KarUni,KarPrePub, Fecha from ClientesDevoluciones " +
                " where ArtSec = "+Artsec+" and NitSec = '"+nitsec+"' and CliSec = "+clisec+" ";





        Cursor cursorhistorial = BaseDeDatos.getWritableDatabase().rawQuery(consultaHistorial, null);
        cursorhistorial.moveToFirst();
        String historial = "";
        if(cursorhistorial.getCount() > 0){
            cursorhistorial.moveToFirst();
            do{

                historial += "("+ cursorhistorial.getString(2)+") \n"+cursorhistorial.getInt(0)+" @: "+cursorhistorial.getDouble(1)+" - ";

            } while (cursorhistorial.moveToNext());

        }else{


        }



        return historial;

    }

}

