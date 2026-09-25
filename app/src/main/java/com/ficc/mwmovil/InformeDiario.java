package com.ficc.mwmovil;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class InformeDiario extends AppCompatActivity {
    Time time = new Time();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informe_diario);
        getSupportActionBar().hide();
        try {

            time.setToNow();




            EditText fechaventas = findViewById(R.id.fechaventas);



            Calendar calendario = Calendar.getInstance();

            String fechaHoy = String.format(
                    Locale.US,
                    "%04d-%02d-%02d",
                    calendario.get(Calendar.YEAR),
                    calendario.get(Calendar.MONTH) + 1,
                    calendario.get(Calendar.DAY_OF_MONTH)
            );

            fechaventas.setText(fechaHoy);

            fechaventas.setOnClickListener(v -> {



                DatePickerDialog dialog = new DatePickerDialog(
                        this,
                        (view, year, month, dayOfMonth) -> {

                            String fecha = String.format(
                                    Locale.US,
                                    "%04d-%02d-%02d",
                                    year,
                                    month + 1,
                                    dayOfMonth
                            );

                            fechaventas.setText(fecha);

                            AppGlobals.year = year;
                            AppGlobals.dayOfMonth =  dayOfMonth;
                            AppGlobals.month = month ;


                            if(dayOfMonth > 0){
                                time.set(time.second, time.minute, time.hour,dayOfMonth , month, year);
                            }
                            Log.e("Time: ",String.valueOf(time.month));
                            Log.e("Time: ",String.valueOf(time.monthDay));
                            Log.e("Time: ",String.valueOf(time.year));
                            CargarDatos();

                        },
                        calendario.get(Calendar.YEAR),
                        calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH)





                );

                dialog.show();
            });






            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String fechaActual = sdf.format(new Date());
            CargarDatos();









        }catch (Exception e){
            Integer error=0;
        }

        Button btn_crearcliente= (Button)findViewById(R.id.btn_resumenporlinea);
        btn_crearcliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ResumenLinea.class);
                i.putExtra("tipo", "G");
                i.putExtra("pinvgrucod", "");
                i.putExtra("pinvsubgrucod", "");
                i.putExtra("pinvfamcod", "");
                startActivity(i);
            }
        });

    }

public void CargarDatos(){


    //  fechaventas.setText(fechaActual);
    GestorPedidos gestorpedidos = new GestorPedidos();
    SDTResumenPedidos sdtResumenPedidos = gestorpedidos.TotalesPedido(getApplicationContext(), "", "", 0,"","","");
    BaseDatos BaseDeDatos;
    BaseDeDatos = new BaseDatos(this, "MantisMovil", null, 5);

    TextView vendedor = findViewById(R.id.vendedor);
    String InfoVendedor = "Select VenNom from usuarios ";

    try{
        Cursor usuarios = BaseDeDatos.getWritableDatabase().rawQuery(InfoVendedor, null);

        if (usuarios.getCount() > 0) {
            usuarios.moveToFirst();
            do {
                vendedor.setText(usuarios.getString(0));
            } while (usuarios.moveToNext());
        }
    }catch (Exception e){
        Log.e("EROOOR",e.toString());
    }




    Integer Clientes = gestorpedidos.TotalClientesDia(getApplicationContext());

    Double Efectividad = 0.0;
    Double Subtotal = sdtResumenPedidos.Subtotal;
    Double Iva = sdtResumenPedidos.Iva;
    Double Total = sdtResumenPedidos.Total;

           /* if (Clientes != 0) {
                Efectividad = ((Impactos* 100.00) / Clientes) ;
            } else {
                Efectividad = 0.0;
            }*/
    TextView txt_subtotal = (TextView) findViewById(R.id.txt_abonoori);
    TextView txt_iva = (TextView) findViewById(R.id.txt_retencion);
    TextView txt_total = (TextView) findViewById(R.id.txt_neto);
    TextView txt_clientes = (TextView) findViewById(R.id.txt_clientes);
    TextView txt_impactados = (TextView) findViewById(R.id.txt_impactados);
    TextView txt_efectividad = (TextView) findViewById(R.id.txt_efectividad);

    txt_subtotal.setText(String.format("%.2f", Subtotal));
    txt_iva.setText(String.format("%.2f", Iva));
    txt_total.setText(String.format("%.2f", Total));
    txt_clientes.setText(String.format("%,d", Clientes));
    //txt_impactados.setText(String.format("%,d", Impactos));
    txt_efectividad.setText(String.format("%.2f", Efectividad)+'%');



    TextView totalpedido = findViewById(R.id.totalpedido);



    /*Nueva     */

    String infoclientes = "Select Nitide,CliNom, c.NitSec,c.CliSec, " +
            " sum((precio*cant) * ((100.00-p.pordesc)/100.00) * ((100.00-p.pordesc2)/100.00) * " +
            " ((100.00-p.pordesc3)/100.00)* ((100.00-p.pordesc4)/100.00) * ((100.00-p.pordesc5)/100.00)*" +
            " ((100.00-p.pordesc6)/100.00)  )  from pedido p " +
            " left join clientes c on p.nitsec = c.nitsec and p.clisec = c.clisec " +
            " where ifnull(NotaInv,'N') = 'N' and ifnull(NotaCar,'N') = 'N' and  (cant+ifnull(cantinf,0)) > 0" +
            " and   pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "   " +
            " group by Nitide,CliNom, c.NitSec,c.CliSec";

    TableLayout tabla = findViewById(R.id.tabla);
    // Reiniciar tabla
    tabla.removeAllViews();
    totalpedido.setText("TOTAL: $0");
    try{
        Cursor clientes = BaseDeDatos.getWritableDatabase().rawQuery(infoclientes, null);

        if (clientes.getCount() > 0) {
            clientes.moveToFirst();
            double pedTotal = 0.0;
            do {
                TableRow fila = new TableRow(this);

// parametros para cada columna
                TableRow.LayoutParams params1 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1);
                TableRow.LayoutParams params2 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3);
                TableRow.LayoutParams params3 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1);

                TextView col1 = new TextView(this);
                col1.setText(clientes.getString(0));
                col1.setLayoutParams(params1);
                col1.setPadding(10,10,10,10);

                TextView col2 = new TextView(this);
                col2.setText(clientes.getString(1));
                col2.setLayoutParams(params2);
                col2.setPadding(0,0,0,0);

                TextView col3 = new TextView(this);
                col3.setText("$"+String.format("%.2f",clientes.getDouble(4)) );
                col3.setLayoutParams(params3);
                col3.setPadding(10,10,10,10);
                col3.setGravity(Gravity.END);

                pedTotal += clientes.getDouble(4);

                fila.addView(col1);
                fila.addView(col2);
                fila.addView(col3);

                tabla.addView(fila);

            } while (clientes.moveToNext());

            totalpedido.setText("TOTAL: $"+String.format("%.2f",pedTotal));


        }
    }catch (Exception e){
        Log.e("EROOOR",e.toString());
    }

}


}


