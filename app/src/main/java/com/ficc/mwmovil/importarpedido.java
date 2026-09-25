package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ficc.mwmovil.databinding.ActivityImportarpedidoBinding;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class importarpedido extends AppCompatActivity {
    ListViewAdapterPedidoImp ListViewAdapterPedidoImp;
    SDTPedidosImportados[] SDTPedidosImportados ;
    Bundle Extras;
    String nitsec;
    Integer UltimaPosicion=0;
    TextView clinom,bodega,vplazo,Vendedor;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_importarpedido);


        Extras=this.getIntent().getExtras();
        nitsec =Extras.getString("nitsec");
        int clisec =Extras.getInt("clisec");
        int plazo=Extras.getInt("plazo");
        String plazoNom = Extras.getString("plazoNom");
        int bodcod =  Extras.getInt("bodega");
        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
        Cursor cursorbodegas = BaseDeDatos.getWritableDatabase().rawQuery("select BodNom from Bodegas where bodcod = "+bodcod+" ", null); //order by nombre
        Cursor cursorcliente =BaseDeDatos.getWritableDatabase().rawQuery("select Nitcom || '-'|| CliNom  from Clientes where nitsec ='"+nitsec+"' and clisec ="+clisec+" ", null); //order by nombre
        GlobalVariables vGlobalVariables = GlobalVariables.getInstance();
        String vUsuario = vGlobalVariables.getUsuario();
        String bodnom = "";
        String vclinom = "";
        if(cursorcliente.getCount() > 0){
            cursorcliente.moveToFirst();
            vclinom = cursorcliente.getString(0);
        }
      if(cursorbodegas.getCount() > 0){
          cursorbodegas.moveToFirst();
          bodnom = cursorbodegas.getString(0);
      }
        clinom = findViewById(R.id.clinom);
        bodega = findViewById(R.id.bodega);
        Vendedor = findViewById(R.id.Vendedor);
        vplazo = findViewById(R.id.plazo);
        vplazo.setText(plazoNom);
        bodega.setText(bodnom);
        Vendedor.setText(vUsuario);
        clinom.setText(vclinom);

        init();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        init();
    }


    public void init(){
        GlobalVariables vGlobalVariables = GlobalVariables.getInstance();
        String vUsuario = vGlobalVariables.getUsuario();
        try {
            ConBd conbd = new ConBd();
            conbd.Variables();
            final BaseDatos BaseDeDatos;
            BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
String nVencod = "";
String  nVenId = "";
            Log.e("Vencod: ",vUsuario);

            Cursor cursorUsu = BaseDeDatos.getWritableDatabase().rawQuery("select vencod,venid from Usuarios where venid = '"+vUsuario+"' ", null); //order by nombre
            if(cursorUsu.getCount() > 0){
                cursorUsu.moveToFirst();
                nVencod  = cursorUsu.getString(0);
                nVenId  = cursorUsu.getString(1);
            }
            Log.e("nVencod: ",nVencod);
            Log.e("nVenId: ",nVenId);

            Connection conn = conbd.CargarConexion(getApplicationContext());
            Statement comm = conn.createStatement();
            String scriptcont = "select count(*) as rows from PedidoImportado  left join vendedores v on v.venid = pedimpvencod or v.vencod = pedimpvencod   where pedimpvencod = '"+vUsuario+"' or vencod = '"+vUsuario+"'  ";
            ResultSet rsImport = comm.executeQuery(scriptcont);
            int tamanors = 0;
            while (rsImport.next()) {
                tamanors = rsImport.getInt("rows");
            }
            int vuelta = 0;
            String Script = "select concat(upper(pedimpvencod),pedimpsec,pedimpdia,pedimpmes,pedimpano) as codigo,(select count(*) from PedidoImportadoarticulos where pedImpSec = p.pedimpsec) as items, pedimpvencod,p.pedimpsec,pedimpdia,pedimpmes,pedimpano,pedImpFecha from PedidoImportado p left join vendedores v on v.venid = pedimpvencod or v.vencod = pedimpvencod  where pedimpvencod = '"+vUsuario+"' or vencod =  '"+vUsuario+"' order by pedImpFecha DESC";
            ResultSet rsImportData = comm.executeQuery(Script);
            SDTPedidosImportados = new SDTPedidosImportados[tamanors];
            while (rsImportData.next()) {
                final SDTPedidosImportados SDTPedidosImportadositem = new SDTPedidosImportados();
                SDTPedidosImportadositem.Codigo =rsImportData.getString("codigo").trim();
                SDTPedidosImportadositem.Vendedor =rsImportData.getString("pedimpvencod").trim();
                SDTPedidosImportadositem.llave =rsImportData.getInt("pedimpsec");
                SDTPedidosImportadositem.dia =rsImportData.getInt("pedimpdia");
                SDTPedidosImportadositem.mes =rsImportData.getInt("pedimpmes");
                SDTPedidosImportadositem.ano =rsImportData.getInt("pedimpano");
                SDTPedidosImportadositem.items =rsImportData.getInt("items");
                SDTPedidosImportados[vuelta] = SDTPedidosImportadositem;
                vuelta = vuelta + 1;
            }

            final ListView listview_pedidosimport = (ListView) findViewById(R.id.listview_importados);

            ListViewAdapterPedidoImp = new ListViewAdapterPedidoImp(this, SDTPedidosImportados);
            listview_pedidosimport.setAdapter(ListViewAdapterPedidoImp);

            listview_pedidosimport.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView adapterView, final View view, int i, long l) {
                    UltimaPosicion = i;
                    Intent intent = new Intent(getApplicationContext(), confirmacargue.class);
                    intent.putExtra("nitsec", Extras.getString("nitsec"));
                    intent.putExtra("clisec", Extras.getInt("clisec"));
                    intent.putExtra("lisprecod", Extras.getInt("lisprecod"));
                    intent.putExtra("invgrucod", "");
                    intent.putExtra("invsubgrucod", "");
                    intent.putExtra("invfamcod", "");
                    intent.putExtra("prefijo", Extras.getString("prefijo"));
                    intent.putExtra("plazo", Extras.getInt("plazo"));
                    intent.putExtra("plazoNom",  Extras.getString("plazoNom"));
                    intent.putExtra("codigo", SDTPedidosImportados[i].Codigo);
                    intent.putExtra("llave", SDTPedidosImportados[i].llave);
                    intent.putExtra("bodega", Extras.getInt("bodega"));
                    intent.putExtra("total", SDTPedidosImportados[i].items);
                    startActivityForResult(intent, 3);
                }
            });








        } catch (Exception e) {
            Log.e("Errorsqlimport",e.toString());
        }
    }
}