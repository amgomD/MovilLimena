package com.ficc.mwmovil;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

public class ClientesPedido extends AppCompatActivity {

    SDTClientes[] SDTClientes ;
    ListViewAdapterClientes ListViewAdapterClientes;
    Bundle Extras=null;
    EditText txxciudad ;
    TextView ciudadele,ciucodele;
    Button filtrociudad ;
    ImageButton limpiar;
  //  ImageView limpiarfiltro;
    SearchView search_clientes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes_pedido);
        getSupportActionBar().hide();

        Extras=this.getIntent().getExtras();
        String SoloRuta=Extras.getString("ruta");
        filtrociudad = findViewById(R.id.filtrociudad);
        ciudadele = findViewById(R.id.ciudadele);
        ciucodele = findViewById(R.id.ciucodele);

        limpiar = findViewById(R.id.limpiar);
        GestorPedidos GestorPedidos=new GestorPedidos();
        //SDTResumenPedidos SDTResumenPedidos= GestorPedidos.TotalesPedido(getApplicationContext(),prefijo,nitsec,clisec);
       ConBd conBd = new ConBd();
       conBd.Variables();
       String mantisficc = conBd.MantisFicc;
       // filtrociudad.setVisibility(View.GONE);
      //  limpiarfiltro.setVisibility(View.GONE);
       // limpiarfiltro = findViewById(R.id.limpiarfiltro);

        BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
//ifnull((select count(*) from ClientesDevoluciones d where d.nitsec=c.nitsec and d.clisec=c.clisec),0)

        Time time = new Time();
        time.setToNow();
        final int day=time.weekDay;

        String OrdenConsulta=" order by ";
        String WhereConsulta="";
        switch(day) {
            case 1 :
                OrdenConsulta+=" cliintlun desc ,CliIntOrdDet ";
                WhereConsulta+=" cliintlun='S' ";
                break;
            case 2 :
                OrdenConsulta+=" cliintmar desc,CliIntOrdDet ";
                WhereConsulta+=" cliintmar='S' ";
                break;
            case 3 :
                OrdenConsulta+=" cliintmie desc,CliIntOrdDet ";
                WhereConsulta+=" cliintmie='S' ";
                break;
            case 4 :
                OrdenConsulta+=" cliintjue desc,CliIntOrdDet ";
                WhereConsulta+=" cliintjue='S' ";
                break;
            case 5 :
                OrdenConsulta+=" cliintvie desc,CliIntOrdDet ";
                WhereConsulta+=" cliintvie='S' ";
                break;
            case 6 :
                OrdenConsulta+=" cliintsab desc,CliIntOrdDet " ;
                WhereConsulta+=" cliintsab='S' ";
                break;
            case 0 :
                OrdenConsulta+=" cliintdom,CliIntOrdDet desc";
                WhereConsulta+=" cliintdom='S' ";
                break;
            default :
                OrdenConsulta="";
        }
        if(SoloRuta.equalsIgnoreCase("S")){
            WhereConsulta=" where "+WhereConsulta;
        }else{
            WhereConsulta="";
        }


        try {
            String Frenom = "FreNom";
  if(mantisficc.equalsIgnoreCase("S")){
      Frenom  ="case FreNom when 'DIA' then 'Diaria' " +
              "when 'SEM' then 'Semanal' " +
              "when 'QUIN1' then 'Quincenal semana 1 - 2' " +
              "when 'QUIN2' then 'Quincenal semana 1- 3' " +
              "when 'QUIN3' then 'Quincenal semana 1 - 4' " +
              "when 'QUIN4' then 'Quincenal semana 2 - 3' " +
              "when 'QUIN5' then 'Quincenal semana 2 - 4' " +
              "when 'QUIN6' then 'Quincenal semana 3 - 4' " +
              "when 'MEN1' then 'Mensual semana 1' " +
              "when 'MEN2' then 'Mensual semana 2' " +
              "when 'MEN3' then 'Mensual semana 3' " +
              "when 'MEN4' then 'Mensual semana 4' " +
              "else 'Diaria'  end FreNom";
  }


            String SubconsultaPedido= "ifnull((select  count(*) hay from pedido p  where " +
                    " pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and cant+ifnull(cantcaj,0)<>0 and p.nitsec=c.NitSec and p.clisec=c.clisec Group by p.nitsec,p.clisec),0)";


            String Consulta = "select c.NitSec,c.CliSec,NitIde,NitCom,CliNom,CliDir,Lisprecod,ifnull(MovCauNom,'') MovCauNom," +
                    " cliintlun,cliintmar,cliintmie,cliintjue,cliintvie,cliintsab,cliintdom,CliIntTiempo,CliIntOrdDet,"+Frenom+" ," +
                    " CliDiasUltVen,CiuNom,CliTel,CarteraVend,"+SubconsultaPedido+" ped " +
                    " from clientes c left join Visita v on c.nitsec=v.nitsec and  c.clisec=v.clisec  " +
                    "  and VisAno=" + time.year + "  and VisMes=" + (time.month + 1) + "  and visdia=" + time.monthDay +" "+ WhereConsulta + OrdenConsulta;

             Consulta =
                    "SELECT " +
                            " c.NitSec," +
                            " c.CliSec," +
                            " NitIde," +
                            " NitCom," +
                            " CliNom," +
                            " CliDir," +
                            " Lisprecod," +
                            " IFNULL(MovCauNom,'') MovCauNom," +
                            " cliintlun," +
                            " cliintmar," +
                            " cliintmie," +
                            " cliintjue," +
                            " cliintvie," +
                            " cliintsab," +
                            " cliintdom," +
                            " CliIntTiempo," +
                            " CliIntOrdDet," +
                            Frenom + "," +
                            " CliDiasUltVen," +
                            " CiuNom," +
                            " CliTel," +
                            " IFNULL(car.TotalCartera,0) AS Cartera," +
                            SubconsultaPedido + " ped " +
                            "FROM clientes c " +

                            "LEFT JOIN Visita v ON c.nitsec = v.nitsec " +
                            " AND c.clisec = v.clisec " +
                            " AND VisAno = " + time.year +
                            " AND VisMes = " + (time.month + 1) +
                            " AND visdia = " + time.monthDay + " " +

                            "LEFT JOIN ( " +
                            "    SELECT movnitsec, movclisec, TOTAL(FacSaldo) AS TotalCartera " +
                            "    FROM cartera " +
                            "    GROUP BY movnitsec, movclisec " +
                            ") car ON car.movnitsec = c.NitSec " +
                            " AND car.movclisec = c.CliSec " +

                            WhereConsulta + " " +
                            OrdenConsulta;







            //Consulta=Consulta+" limit 50";
            Cursor cursor = BaseDeDatos.getReadableDatabase().rawQuery(Consulta, null);
            SDTClientes = new SDTClientes[cursor.getCount()];
            Integer vuelta = 0;
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    try {
                        SDTClientes SDTClientesItem = new SDTClientes();
                        SDTClientesItem.NitSec = cursor.getString(0);
                        SDTClientesItem.CliSec = cursor.getInt(1);
                        SDTClientesItem.NitIde = cursor.getString(2); //+'-'+String.valueOf(cursor.getInt(8));
                        SDTClientesItem.NitCom = cursor.getString(3);
                        SDTClientesItem.DiasUltVen=cursor.getInt(1);
                        SDTClientesItem.CliCiudad = cursor.getString(19);
                        SDTClientesItem.CliNom = cursor.getString(4);
                        SDTClientesItem.CliDir = cursor.getString(5);
                        SDTClientesItem.Causal = cursor.getString(7);
                        SDTClientesItem.CliTel = cursor.getString(20);
                        SDTClientesItem.Cliintlun = cursor.getString(8);
                        SDTClientesItem.Cliintmar = cursor.getString(9);
                        SDTClientesItem.Cliintmie = cursor.getString(10);
                        SDTClientesItem.Cliintjue = cursor.getString(11);
                        SDTClientesItem.Cliintvie = cursor.getString(12);
                        SDTClientesItem.Cliintsab = cursor.getString(13);
                        SDTClientesItem.Cliintdom = cursor.getString(14);
                        SDTClientesItem.CliIntTiempo = cursor.getInt(15);
                        SDTClientesItem.CliIntOrdDet = cursor.getInt(16);
                        SDTClientesItem.FreNom = cursor.getString(17);
                        SDTClientesItem.Vuelta=vuelta;
                        SDTClientesItem.Cartera = cursor.getInt(21);  //cartera(cursor.getString(0),cursor.getInt(1));// //

                        //SDTClientesItem.Cartera =   cursor.getInt(21);
                        if (cursor.getInt(22)>0) {
                             SDTClientesItem.TotalPedido = GestorPedidos.TotalesPedido(getApplicationContext(), "", SDTClientesItem.NitSec, SDTClientesItem.CliSec,"","","").Total;
                        }
                        Integer kk = cursor.getInt(6);
                        SDTClientesItem.LisPreCod = cursor.getInt(6);
                        SDTClientes[vuelta] = SDTClientesItem;
                    } catch (Exception e) {
                        Integer Error = 1;
                    }
                    vuelta = vuelta + 1;
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            int hh=0;
        }

        ClientesPedido clientesPedido = this;

        final ListView list_clientes = (ListView) findViewById(R.id.list_clientes);
        ListViewAdapterClientes = new ListViewAdapterClientes(this, SDTClientes,clientesPedido);
        list_clientes.setAdapter(ListViewAdapterClientes);

        list_clientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, final View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), DatosCliente.class);
                intent.putExtra("nitsec", SDTClientes[i].NitSec);
                intent.putExtra("clisec", SDTClientes[i].CliSec);
                intent.putExtra("dias", SDTClientes[i].DiasUltVen);
                Integer lista=SDTClientes[i].LisPreCod;
                intent.putExtra("lisprecod", SDTClientes[i].LisPreCod);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.no_anim);
               // finish();
            }
        });
         search_clientes=(SearchView)findViewById(R.id.search_clientes);
        search_clientes.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ListViewAdapterClientes.getFilter().filter(newText);
                return false;
            }
        });

        filtrociudad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), BuscarBarrioCiudad.class);
                i.putExtra("BUSCIUBAR","CIU");
                startActivityForResult(i, 2);
            }
        });


     limpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ciudadele.setText("Todos");
                ciucodele.setText("");
                cargarciuda("");
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                ciucodele.setText(data.getStringExtra("CODCIUDAD"));
            ciudadele.setText(data.getStringExtra("CIUDAD"));
                cargarciuda(data.getStringExtra("CODCIUDAD"));
           }else{
                ciudadele.setText("Todos");
                ciucodele.setText("");
                cargarciuda("");
            }
        }
    }

    private Integer cartera(String NitSec, int CliSec) {
        Integer valrocartera = 0;
        GestorCartera gestorCartera = new GestorCartera();
        gestorCartera.TotalesCateraCliente(getApplicationContext(), NitSec,CliSec);
        valrocartera = gestorCartera.CarteraGeneral;
        return  valrocartera ;
    }

    @Override
    public void onRestart() {


        super.onRestart();
        super.onResume();

        cargarciuda("");
    }

    public void cargarciuda(String Ciucod){

        Extras=this.getIntent().getExtras();
        String SoloRuta=Extras.getString("ruta");
        filtrociudad = findViewById(R.id.filtrociudad);
        GestorPedidos GestorPedidos=new GestorPedidos();
        //SDTResumenPedidos SDTResumenPedidos= GestorPedidos.TotalesPedido(getApplicationContext(),prefijo,nitsec,clisec);
        ConBd conBd = new ConBd();
        conBd.Variables();
        String mantisficc = conBd.MantisFicc;


        BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
//ifnull((select count(*) from ClientesDevoluciones d where d.nitsec=c.nitsec and d.clisec=c.clisec),0)

        Time time = new Time();
        time.setToNow();
        final int day=time.weekDay;

        String OrdenConsulta=" order by ";
        String WhereConsulta="";
        switch(day) {
            case 1 :
                OrdenConsulta+=" cliintlun desc ,CliIntOrdDet ";
                WhereConsulta+=" cliintlun='S' ";
                break;
            case 2 :
                OrdenConsulta+=" cliintmar desc,CliIntOrdDet ";
                WhereConsulta+=" cliintmar='S' ";
                break;
            case 3 :
                OrdenConsulta+=" cliintmie desc,CliIntOrdDet ";
                WhereConsulta+=" cliintmie='S' ";
                break;
            case 4 :
                OrdenConsulta+=" cliintjue desc,CliIntOrdDet ";
                WhereConsulta+=" cliintjue='S' ";
                break;
            case 5 :
                OrdenConsulta+=" cliintvie desc,CliIntOrdDet ";
                WhereConsulta+=" cliintvie='S' ";
                break;
            case 6 :
                OrdenConsulta+=" cliintsab desc,CliIntOrdDet " ;
                WhereConsulta+=" cliintsab='S' ";
                break;
            case 0 :
                OrdenConsulta+=" cliintdom,CliIntOrdDet desc";
                WhereConsulta+=" cliintdom='S' ";
                break;
            default :
                OrdenConsulta="";
        }
        if(SoloRuta.equalsIgnoreCase("S")){
            WhereConsulta=" where "+WhereConsulta;
            if(!Ciucod.isEmpty()){
                WhereConsulta += " and CiuCod = '"+Ciucod+"'";
            }
        }else{
            WhereConsulta="";
            if(!Ciucod.isEmpty()){
                WhereConsulta += " where CiuCod = '"+Ciucod+"' ";
            }
        }


        try {
            String Frenom = "FreNom";
            if(mantisficc.equalsIgnoreCase("S")){
                Frenom  ="case FreNom when 'DIA' then 'Diaria' " +
                        "when 'SEM' then 'Semanal' " +
                        "when 'QUIN1' then 'Quincenal semana 1 - 2' " +
                        "when 'QUIN2' then 'Quincenal semana 1- 3' " +
                        "when 'QUIN3' then 'Quincenal semana 1 - 4' " +
                        "when 'QUIN4' then 'Quincenal semana 2 - 3' " +
                        "when 'QUIN5' then 'Quincenal semana 2 - 4' " +
                        "when 'QUIN6' then 'Quincenal semana 3 - 4' " +
                        "when 'MEN1' then 'Mensual semana 1' " +
                        "when 'MEN2' then 'Mensual semana 2' " +
                        "when 'MEN3' then 'Mensual semana 3' " +
                        "when 'MEN4' then 'Mensual semana 4' " +
                        "else 'Diaria'  end FreNom";
            }


            String SubconsultaPedido= "ifnull((select  count(*) hay from pedido p  where " +
                    " pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and cant+ifnull(cantcaj,0)<>0 and p.nitsec=c.NitSec and p.clisec=c.clisec Group by p.nitsec,p.clisec),0)";


            String Consulta = "select c.NitSec,c.CliSec,NitIde,NitCom,CliNom,CliDir,Lisprecod,ifnull(MovCauNom,'') MovCauNom,cliintlun,cliintmar,cliintmie,cliintjue,cliintvie,cliintsab,cliintdom,CliIntTiempo,CliIntOrdDet,"+Frenom+" ,CliDiasUltVen,CiuNom,CliTel,CarteraVend,"+SubconsultaPedido+" ped from clientes c left join Visita v on c.nitsec=v.nitsec and  c.clisec=v.clisec  and VisAno=" + time.year + "  and VisMes=" + (time.month + 1) + "  and visdia=" + time.monthDay +" "+ WhereConsulta + OrdenConsulta;
            //Consulta=Consulta+" limit 50";
            Cursor cursor = BaseDeDatos.getReadableDatabase().rawQuery(Consulta, null);
            SDTClientes = new SDTClientes[cursor.getCount()];
            Integer vuelta = 0;
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    try {
                        SDTClientes SDTClientesItem = new SDTClientes();
                        SDTClientesItem.NitSec = cursor.getString(0);
                        SDTClientesItem.CliSec = cursor.getInt(1);
                        SDTClientesItem.NitIde = cursor.getString(2); //+'-'+String.valueOf(cursor.getInt(8));
                        SDTClientesItem.NitCom = "(" + cursor.getString(18) + ")" + cursor.getString(3);
                        SDTClientesItem.DiasUltVen=cursor.getInt(1);
                        SDTClientesItem.CliCiudad = cursor.getString(19);
                        SDTClientesItem.CliNom = cursor.getString(4);
                        SDTClientesItem.CliDir = cursor.getString(5);
                        SDTClientesItem.Causal = cursor.getString(7);
                        SDTClientesItem.CliTel = cursor.getString(20);
                        SDTClientesItem.Cliintlun = cursor.getString(8);
                        SDTClientesItem.Cliintmar = cursor.getString(9);
                        SDTClientesItem.Cliintmie = cursor.getString(10);
                        SDTClientesItem.Cliintjue = cursor.getString(11);
                        SDTClientesItem.Cliintvie = cursor.getString(12);
                        SDTClientesItem.Cliintsab = cursor.getString(13);
                        SDTClientesItem.Cliintdom = cursor.getString(14);
                        SDTClientesItem.CliIntTiempo = cursor.getInt(15);
                        SDTClientesItem.CliIntOrdDet = cursor.getInt(16);
                        SDTClientesItem.FreNom = cursor.getString(17);
                        SDTClientesItem.Vuelta=vuelta;
                        SDTClientesItem.Cartera =  cartera(cursor.getString(0),cursor.getInt(1));

                        //SDTClientesItem.Cartera =   cursor.getInt(21);
                        if (cursor.getInt(22)>0) {
                            SDTClientesItem.TotalPedido = GestorPedidos.TotalesPedido(getApplicationContext(), "", SDTClientesItem.NitSec, SDTClientesItem.CliSec,"","","").Total;
                        }
                        Integer kk = cursor.getInt(6);
                        SDTClientesItem.LisPreCod = cursor.getInt(6);
                        SDTClientes[vuelta] = SDTClientesItem;
                    } catch (Exception e) {
                        Integer Error = 1;
                    }
                    vuelta = vuelta + 1;
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            int hh=0;
        }

        ClientesPedido clientesPedido = this;

        final ListView list_clientes = (ListView) findViewById(R.id.list_clientes);
        ListViewAdapterClientes = new ListViewAdapterClientes(this, SDTClientes,clientesPedido);
        list_clientes.setAdapter(ListViewAdapterClientes);
        //  finish();
        // startActivity(getIntent());
    }
}
