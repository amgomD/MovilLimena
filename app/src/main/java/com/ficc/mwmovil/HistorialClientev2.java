package com.ficc.mwmovil;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ficc.mwmovil.databinding.ActivityHistorialClientev2Binding;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HistorialClientev2 extends AppCompatActivity {
    Bundle Extras;
    ListViewAdapterHistorial ListViewAdapterHistorial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_clientev2);
        getSupportActionBar().hide();
        SDTHistorial[] SDTHistorial;
        ConBd conbd = new ConBd();
        conbd.Variables();
        Connection conn = conbd.CargarConexion(getApplicationContext());
        Extras=this.getIntent().getExtras();
        String nitsec=Extras.getString("nitsec");
        Integer clisec=Extras.getInt("clisec");
        String  Tipo =Extras.getString("tipo");
        String MantisFicc = conbd.MantisFicc;
        String consulta = "";

           consulta = "select * \n" +
                   "from(\n" +
                   "    select top 1000\n" +
                   "        ROW_NUMBER() OVER(ORDER BY f.FacFec desc) AS Row,\n" +
                   "        f.FacNro,\n" +
                   "        CAST(f.facfec AS DATE)  FacFec ,\n" +
                   "        f.FacSec,\n" +
                   "        isnull(sum(k.karvaltotMenDes + k.KarArtIva),0) FacTotalImpuestos\n" +
                   "    from Factura f\n" +
                   "    left join tipos t on f.FactipCod = t.TipCod\n" +
                   "    left join Facturakardex k on k.facsec = f.facsec\n" ;
           if(Tipo.equalsIgnoreCase("FAC")){
               consulta +=  "    where (FueCod='FACT' or FueCod='NCRE')\n";

           }else{
               consulta +=   "    where (FueCod='REMI')\n";

           }
        consulta +=   "      and FacNitSec = '"+nitsec+"'\n" +
                   "      and FacCliSec = "+clisec+"\n" +
                   "      and FacEst = 'A'\n" +
                   "    group by f.FacNro,f.FacFec,f.FacSec\n" +
                   "    order by f.Facfec asc\n" +
                   ") jj\n" +
                   "where FacTotalImpuestos > 0\n" +
                   "order by Row desc; ";




        int tamanors = 0;
        int vuelta = 0;

        Log.e("consulta: " ,consulta);

        try {
            Statement comm = conn.createStatement();
            ResultSet rsImport = comm.executeQuery(consulta);
            while (rsImport.next()) {
                tamanors =rsImport.getInt("Row");
                break;
            }

            ResultSet rsImportdata = comm.executeQuery(consulta);
            SDTHistorial = new SDTHistorial[tamanors];
            while (rsImportdata.next()) {

                final SDTHistorial SDTHistorialitem = new SDTHistorial();
                SDTHistorialitem.FacFec =rsImportdata.getString("FacFec").trim();
                SDTHistorialitem.FacNro =rsImportdata.getString("FacNro").trim();
                SDTHistorialitem.FacSec =rsImportdata.getString("FacSec").trim();
               int factotal =  rsImportdata.getInt("FacTotalImpuestos");
                SDTHistorialitem.FacTotalImpuestos=String.format("%,d",factotal).trim();
                SDTHistorial[vuelta] = SDTHistorialitem;
                vuelta = vuelta + 1;
            }
            final ListView listahistorial = (ListView) findViewById(R.id.ListaHistorial);

            ListViewAdapterHistorial = new ListViewAdapterHistorial(this, SDTHistorial);
            listahistorial.setAdapter(ListViewAdapterHistorial);

        } catch (SQLException e) {
            Log.e("Error historial",e.toString());
        }


    }

}