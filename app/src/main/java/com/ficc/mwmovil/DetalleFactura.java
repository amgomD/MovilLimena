package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ficc.mwmovil.databinding.ActivityDetalleFacturaBinding;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DetalleFactura extends AppCompatActivity {

    private ActivityDetalleFacturaBinding binding;
    Bundle Extras;
    ListViewAdapterKardex ListViewAdapterKardex;
    TextView dFacNro,FacFec,FacEst,subtotal,iva,Total,Devolucion;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_factura);
        getSupportActionBar().hide();
        Extras=this.getIntent().getExtras();
        SDTKardex[] SDTKardex;
        dFacNro = findViewById(R.id.dFacNro);
        FacFec = findViewById(R.id.FacFec);
        FacEst= findViewById(R.id.FacEst);
        subtotal= findViewById(R.id.subtotal);
        iva= findViewById(R.id.iva);
        Total= findViewById(R.id.Total);
         Devolucion= findViewById(R.id.Devolucion);

        String Facsec=Extras.getString("Facsec");
        ConBd conbd = new ConBd();
        conbd.Variables();
        Connection conn = conbd.CargarConexion(getApplicationContext());
        String MantisFicc = conbd.MantisFicc;
        String consultaen = "";
        if(MantisFicc.equalsIgnoreCase("S")){
            consultaen =  " select CAST(FacFec as date) as FacFec,FacNro,FacSec, CASE FacEst When 'A' Then 'Activo'When 'I' Then 'Inactivo' ELSE 'Pendiente'END FacEst,"+
                    " isnull((select sum(karvaltotMenDes) from Facturakardex k where k.facsec=f.facsec),0) subtotal,"+
                    " isnull((select sum(karvaltotMenDes+KarArtIva) from Facturakardex k where k.facsec=f.facsec),0) FacTotalImpuestos,"+
                    " isnull((select sum(KarArtIva) from Facturakardex k where k.facsec=f.facsec),0) iva,"+
                    " isnull((select sum(karvaltotMenDes+KarArtIva) from Facturakardex k left join factura d on k.facsec = d.facsec where d.facsecdev=f.facsec),0) devolucion  "+
                    " from factura f where facsec = '"+Facsec+"' ";
        }else{

            consultaen = "select CAST(FacFec as date) as FacFec,FacNro,FacSec, CASE FacEst When 'A' Then 'Activo'When 'I' Then 'Inactivo' ELSE 'Pendiente'END FacEst," +
                    " isnull((select sum(karvaltotMenDes) from kardex k where k.facsec=f.facsec),0) subtotal," +
                    " isnull((select sum(karvaltotMenDes+KarArtIva) from kardex k where k.facsec=f.facsec),0) FacTotalImpuestos," +
                    " isnull((select sum(KarArtIva) from kardex k where k.facsec=f.facsec),0) iva," +
                    " isnull((select sum(karvaltotMenDes+KarArtIva) from kardex k left join factura d on k.facsec = d.facsec where d.facsecdev=f.facsec),0) devolucion" +
                    " from factura f where facsec = "+Facsec+" ";
        }




        Statement comm = null;
        try {
            comm = conn.createStatement();
            ResultSet rsImport = comm.executeQuery(consultaen);
            while (rsImport.next()) {
                dFacNro.setText("#"+rsImport.getString("FacNro"));
                FacFec.setText(rsImport.getString("FacFec"));
                FacEst.setText(rsImport.getString("FacEst"));
                int insub =  rsImport.getInt("subtotal");
                int iniva =  rsImport.getInt("iva");
                double inTotal = rsImport.getDouble("FacTotalImpuestos");
                int inDev = rsImport.getInt("devolucion");
                subtotal.setText(String.format("%,d",insub).trim());
                iva.setText(String.format("%,d",iniva).trim());
                Total.setText(String.format("%.2f",inTotal).trim());
                Devolucion.setText(String.format("%,d",inDev).trim());
            }

        } catch (SQLException e) {
            Log.e("Errorsqlencabezado",e.toString());
        }
        String consultaKardex = "";
     if(MantisFicc.equalsIgnoreCase("S")) {
            consultaKardex = "select * from(select  ROW_NUMBER() OVER(ORDER BY karsec desc) AS Row,  ArtCod, ArtNom," +
                    " isnull(KarUni,0) KarTotUni, isnull(karvaltotMenDes+KarArtIva,0) KarVal , KarPorIva,  " +
                    " (KarDesCua+kardesuno+kardesdos+kardestre) as Dcto, " +
                    " (KarPrePub*(1-(kardesuno/100))*(1-(kardesdos/100))*(1-(kardestre/100))*(1-(kardescua/100))) as preciodcto  " +
                    " from Facturakardex k left join Articulos a on k.Artsec = a.Artsec where Facsec = '" + Facsec + "')jj order by Row desc";
        }else{
         consultaKardex =  "select * from(select  ROW_NUMBER() OVER(ORDER BY karsec desc) AS Row,  ArtCod, ArtNom, isnull(((KarCaj*KarArtEmb)+KarUni)*KarPreFacCon,0) KarTotUni, isnull(karvaltotMenDes+KarArtIva,0) KarVal, KarPorIva, " +
                 " (KarDesCua+kardesuno+kardesdos+kardestre) as Dcto, " +
                 " (KarPrePub*(1-(kardesuno/100))*(1-(kardesdos/100))*(1-(kardestre/100))*(1-(kardescua/100))) as preciodcto  " +
                 " from kardex k left join Articulos a on k.Artsec = a.Artsec where Facsec = "+Facsec+")jj order by Row desc";

     }


      int tamano = 0;
      int vuelta = 0;
        try {

            Log.e("consulta: ",consultaKardex);

            ResultSet rsImportconta = comm.executeQuery(consultaKardex);
            while (rsImportconta.next()) {
                tamano =rsImportconta.getInt("Row");
                break;
            }

            comm = conn.createStatement();
            ResultSet rsImportData = comm.executeQuery(consultaKardex);
            SDTKardex = new SDTKardex[tamano];
            while (rsImportData.next()) {
                final SDTKardex SDTKardexitem = new SDTKardex();
                SDTKardexitem.ArtCod =rsImportData.getString("ArtCod").trim();
                SDTKardexitem.ArtNom =rsImportData.getString("ArtNom").trim();
                int karuni =  rsImportData.getInt("KarTotUni");
                SDTKardexitem.KarTotUni = String.format("%,d",karuni).trim();
                double factotal =  rsImportData.getDouble("KarVal");
                SDTKardexitem.karvaltotMenDes=String.format("%.2f",factotal).trim();
                int KarPorIva =  rsImportData.getInt("KarPorIva");
                SDTKardexitem.KarArtIva=String.format("%,d",KarPorIva).trim();
                double preciodcto =   rsImportData.getDouble("preciodcto");
                SDTKardexitem.KarPrePub = String.format("%.2f",preciodcto).trim();
                double Kardes =   rsImportData.getDouble("Dcto");
                SDTKardexitem.Kardes = String.format("%.2f",Kardes).trim();

                SDTKardex[vuelta] = SDTKardexitem;
                vuelta = vuelta + 1;

            }
            final ListView listakardex = (ListView) findViewById(R.id.Listadetalle);

            ListViewAdapterKardex = new ListViewAdapterKardex(this, SDTKardex);
            listakardex.setAdapter(ListViewAdapterKardex);



        } catch (SQLException e) {
            Log.e("Errorsqldetalle",e.toString());
        }



    }

}