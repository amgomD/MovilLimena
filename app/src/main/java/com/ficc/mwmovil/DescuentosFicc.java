package com.ficc.mwmovil;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;



import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DescuentosFicc extends AppCompatActivity {

    ListViewAdapterDescuentoFicc ListViewAdapterDescuentoFicc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descuentos_ficc);
        getSupportActionBar().hide();
        SDTDescuentosFicc[] SDTDescuentosFicc;
        ConBd conbd = new ConBd();
        conbd.Variables();
        String MantisFicc = conbd.MantisFicc;
        Connection conn = conbd.CargarConexion(getApplicationContext());
        String consulta = " select * from(select  ROW_NUMBER() OVER(ORDER BY DesoBonSec ASC) AS Row, " +
                " *from(  select DesoBonSec,DesoBonDescr, " +
                " REPLACE(ISNULL((SELECT ' ,de '  + rtrim(ltrim(cast(DesoBonEscRan as varchar))) + ' hasta ' +  " +
                " rtrim(ltrim(cast(DesoBonEscHasta as varchar))) + ' dcto '+rtrim(ltrim(cast(DesoBonEscDesc as varchar)))+'%' " +
                " FROM DescuentosoBonificadosEscala TABLA WHERE TABLA.DesoBonSec=DG.DesoBonSec FOR XML PATH('')),'XX'),'','')+' , ' As ESCALA " +
                "  from DescuentosoBonificados DG where DesoBonFecIni<=CONVERT(date, GETDATE()) and DesoBonFecFin>=CONVERT(date, GETDATE()) ) kk  ) Consulta order by Row desc   ";
        int tamanors = 0;
        int vuelta = 0;
        try {
            Statement comm = conn.createStatement();
            ResultSet rsImport = comm.executeQuery(consulta);
            while (rsImport.next()) {
                tamanors =rsImport.getInt("Row");
                break;
            }

            ResultSet rsImportdata = comm.executeQuery(consulta);
            SDTDescuentosFicc = new SDTDescuentosFicc[tamanors];
            while (rsImportdata.next()) {
                final SDTDescuentosFicc SDTDescuentosFiccitem = new SDTDescuentosFicc();
                SDTDescuentosFiccitem.DesoBonsec =rsImportdata.getString("DesoBonSec").trim();
                SDTDescuentosFiccitem.DesoBonDescr =rsImportdata.getString("DesoBonDescr").trim();
                String Escala = rsImportdata.getString("ESCALA").trim();
                Escala = Escala.replace(",","\n");
                SDTDescuentosFiccitem.Escala =Escala.trim();
                SDTDescuentosFicc[vuelta] = SDTDescuentosFiccitem;
                vuelta = vuelta + 1;
            }
            final ListView listaDescuento = (ListView) findViewById(R.id.listview_banner);

            ListViewAdapterDescuentoFicc = new ListViewAdapterDescuentoFicc(this, SDTDescuentosFicc);
            listaDescuento.setAdapter(ListViewAdapterDescuentoFicc);

        } catch (SQLException e) {
            Log.e("Error historial",e.toString());
        }





    }

}