package com.ficc.mwmovil;

import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DescargarCartera extends AppCompatActivity {
    Bundle Extras;
    String nitsec;
    Integer clisec;
    String afactura;
    TextView CarAbono ;
    TextView CarReteica ;
    TextView CarDescuento ;
    TextView CarRetencion ;
    TextView CarTotal ;
    String psaldo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descargar_cartera);
        getSupportActionBar().hide();
        getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.TrasparenteGris));
        Extras=this.getIntent().getExtras();
        nitsec=Extras.getString("nitsec");
        clisec=Extras.getInt("clisec");
        afactura=Extras.getString("numero");
        psaldo=Extras.getString("saldo");
        TextView Factura = (TextView) findViewById(R.id.Factura);
        final TextView Saldo = (TextView) findViewById(R.id.Saldo);

        CarAbono = (TextView) findViewById(R.id.CarAbono);
        CarReteica = (TextView) findViewById(R.id.CarReteica);
        CarDescuento = (TextView) findViewById(R.id.CarDescuento);
        CarRetencion = (TextView) findViewById(R.id.CarRetencion);
        CarTotal = (TextView) findViewById(R.id.textTotal);

        Time time = new Time();
        time.setToNow();

        Factura.setText(afactura);
        Saldo.setText(psaldo);
        CarTotal.setText("");

        BaseDatos BaseDeDatos ;
        BaseDeDatos =new BaseDatos(getApplicationContext(),"MantisMovil", null, 5);
        Cursor Clientes = BaseDeDatos.getWritableDatabase().rawQuery("select abono,retefue,retica,descuento from Recibo where nitsec='" + nitsec + "' and clisec=" + clisec + " and facnro='" + afactura + "' and rcyear=" + time.year + " and rcmonth=" + (time.month + 1) + " and rcday=" + time.monthDay  , null);

        if (Clientes.getCount()>0){
            Clientes.moveToFirst();
                do {
                    CarAbono.setText(String.format("%,d",Clientes.getInt(0)));
                    CarRetencion.setText(String.format("%,d",Clientes.getInt(1)));
                    CarReteica.setText(String.format("%,d",Clientes.getInt(2)));
                    CarDescuento.setText(String.format("%,d",Clientes.getInt(3)));

                    Integer atotal=Clientes.getInt(2)-Clientes.getInt(1)-Clientes.getInt(2)-Clientes.getInt(3);

                    CarTotal.setText(String.format("%,d", atotal ));
                } while (Clientes.moveToNext());
            }

        Button btn_cancelar= (Button)findViewById(R.id.btnCancelar);
        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }});
        Button btn_guardar= (Button)findViewById(R.id.button2);
        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final TextView CarAbono = (TextView) findViewById(R.id.CarAbono);

                Integer aCarAbono;
                if (CarAbono.getText().toString().trim().isEmpty()) {
                    aCarAbono = 0;
                } else {
                    aCarAbono = Integer.valueOf(CarAbono.getText().toString().trim());
                }

                final TextView CarRetencion = (TextView) findViewById(R.id.CarRetencion);
                Integer aCarRetencion;
                if (CarRetencion.getText().toString().trim().isEmpty()) {
                    aCarRetencion = 0;
                } else {
                    aCarRetencion = Integer.valueOf(CarRetencion.getText().toString().trim());
                }

                final TextView CarReteica = (TextView) findViewById(R.id.CarReteica);
                Integer aCarReteica;
                if (CarReteica.getText().toString().trim().isEmpty()) {
                    aCarReteica = 0;
                } else {
                    aCarReteica = Integer.valueOf(CarReteica.getText().toString().trim());
                }

                final TextView CarDescuento = (TextView) findViewById(R.id.CarDescuento);
                Integer aCarDescuento;
                if (CarDescuento.getText().toString().trim().isEmpty()) {
                    aCarDescuento = 0;
                } else {
                    aCarDescuento = Integer.valueOf(CarDescuento.getText().toString().trim());
                }
               // final TextView CarDescuento = (TextView) findViewById(R.id.CarDescuento);
                Integer aSaldo;
                //.getText().toString().trim().isEmpty()
                if (psaldo.isEmpty()) {
                    aSaldo = 0;
                } else {
                    aSaldo = Integer.valueOf(psaldo.replace(",","").trim());
                }



                Time time = new Time();
                time.setToNow();



                try{
                    BaseDatos BaseDeDatos ;
                    BaseDeDatos =new BaseDatos(getApplicationContext(),"MantisMovil", null, 5);
                    Cursor Clientes = BaseDeDatos.getWritableDatabase().rawQuery("select * from Recibo where nitsec='" + nitsec + "' and clisec=" + clisec + " and facnro='" + afactura + "' and rcyear=" + time.year + " and rcmonth=" + (time.month + 1) + " and rcday=" + time.monthDay  , null);

                    if (Clientes.getCount() > 0) {
                        BaseDeDatos.getWritableDatabase().execSQL("update Recibo set saldo="+aSaldo+", abono=" + aCarAbono + ",retefue="+aCarRetencion+",retica="+aCarReteica+",descuento="+aCarDescuento+" where nitsec='" + nitsec + "' and clisec=" + clisec + " and facnro='" + afactura + "' and rcyear=" + time.year + " and rcmonth=" + (time.month + 1) + " and rcday=" + time.monthDay );

                        // EvaluarEventos Ev=new EvaluarEventos();
                        // Ev.Evaluar(cntx, codigo[position2].trim(), Valtext, Fextras);
                    } else {
                        if (aCarAbono!= 0.0 ) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.getDefault());
                            Date date = new Date();
                            String fecha = dateFormat.format(date);


                            String ValDesc1="0.0";
                            String ValDesc2="0.0";
                            String ValDesc3="0.0";
                            String ValDesc4="0.0";
                            String ValDesc1no="0.0";
                            String ValDesc2no="0.0";
                            String ValDesc3no="0.0";
                            String ValDesc4no="0.0";


                            GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                            String vendedor=vGlobalVariables.getUsuario();


                            //               "insert into Recibo("
                            //                     + "VenCod text(40) ,"
                            //                   + "nitsec text(25),"
                            //                 + "clisec INTEGER,"
                            //               + "facnro INTEGER,"
                            //             + "abono numeric(16,2), "
                            //           + "retefue numeric(16,2), "
                            //         + "retica numeric(16,2), "
                            //       + "descuento numeric(16,2), "
                            //     + "rcyear INTEGER,"
                            //   + "rcmonth INTEGER,"
                            // + "rcday INTEGER)";

                            String consulta = "insert into Recibo(vencod,nitsec,clisec,facnro,saldo,abono,retefue,retica,descuento,rcyear,rcmonth,rcday)" +
                                    "values('" + vendedor +"','" +
                                    nitsec.trim() + "'," +
                                    clisec+ ",'" +
                                    afactura + "'," +
                                    aSaldo + "," +
                                    aCarAbono + "," +
                                    aCarRetencion + "," +
                                    aCarReteica+ "," +
                                    aCarDescuento + "," +
                                    time.year + "," +
                                    (time.month + 1) + "," +
                                    time.monthDay +")";
                            BaseDeDatos.getWritableDatabase().execSQL(consulta);

                            // EvaluarEventos Ev=new EvaluarEventos();
                            // Ev.Evaluar(cntx, codigo[position2].trim(), Valtext, Fextras);
                        }
                    }
                    finish();
                }catch (Exception e){
                    AlertDialog.Builder AlertaLim = new AlertDialog.Builder(DescargarCartera.this );
                    AlertaLim.setMessage("Error al guardar");
                    AlertaLim.setTitle("Alerta");
                    AlertaLim.setPositiveButton("OK", null);
                    AlertaLim.setCancelable(true);
                    AlertaLim.create().show();
                    int error=0;
                }






            }
        });



        CarAbono.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable s) {

                Double Retencion;
                if (s.toString().trim().isEmpty()) {
                    Retencion = 0.0;
                } else {
                    Retencion = Double.valueOf(s.toString());
                }

                Integer aCarAbono;

                if (CarAbono.getText().toString().trim().isEmpty()) {
                    aCarAbono = 0;
                } else {
                    aCarAbono = Integer.valueOf(CarAbono.getText().toString().trim());
                }
                Integer aCarReteica;
                if (CarReteica.getText().toString().trim().isEmpty()) {
                    aCarReteica = 0;
                } else {
                    aCarReteica = Integer.valueOf(CarReteica.getText().toString().trim());
                }
                Integer aCarDescuento;
                if (CarDescuento.getText().toString().trim().isEmpty()) {
                    aCarDescuento = 0;
                } else {
                    aCarDescuento = Integer.valueOf(CarDescuento.getText().toString().trim());
                }
                Integer aCarRetencion;
                if (CarRetencion.getText().toString().trim().isEmpty()) {
                    aCarRetencion = 0;
                } else {
                    aCarRetencion = Integer.valueOf(CarRetencion.getText().toString().trim());
                }
                Integer atotal=aCarAbono-aCarRetencion-aCarReteica-aCarDescuento;

                CarTotal.setText(String.format("%,d", atotal ));

            }
        });

        CarRetencion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable s) {

                Double Retencion;
                if (s.toString().trim().isEmpty()) {
                    Retencion = 0.0;
                } else {
                    Retencion = Double.valueOf(s.toString());
                }

                Integer aCarAbono;

                if (CarAbono.getText().toString().trim().isEmpty()) {
                    aCarAbono = 0;
                } else {
                    aCarAbono = Integer.valueOf(CarAbono.getText().toString().trim());
                }
                Integer aCarReteica;
                if (CarReteica.getText().toString().trim().isEmpty()) {
                    aCarReteica = 0;
                } else {
                    aCarReteica = Integer.valueOf(CarReteica.getText().toString().trim());
                }
                Integer aCarDescuento;
                if (CarDescuento.getText().toString().trim().isEmpty()) {
                    aCarDescuento = 0;
                } else {
                    aCarDescuento = Integer.valueOf(CarDescuento.getText().toString().trim());
                }
                Integer aCarRetencion;
                if (CarRetencion.getText().toString().trim().isEmpty()) {
                    aCarRetencion = 0;
                } else {
                    aCarRetencion = Integer.valueOf(CarRetencion.getText().toString().trim());
                }
                Integer atotal=aCarAbono-aCarRetencion-aCarReteica-aCarDescuento;

                CarTotal.setText(String.format("%,d", atotal ));

            }
        });

        CarReteica.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable s) {

                Double Retencion;
                if (s.toString().trim().isEmpty()) {
                    Retencion = 0.0;
                } else {
                    Retencion = Double.valueOf(s.toString());
                }

                Integer aCarAbono;

                if (CarAbono.getText().toString().trim().isEmpty()) {
                    aCarAbono = 0;
                } else {
                    aCarAbono = Integer.valueOf(CarAbono.getText().toString().trim());
                }
                Integer aCarReteica;
                if (CarReteica.getText().toString().trim().isEmpty()) {
                    aCarReteica = 0;
                } else {
                    aCarReteica = Integer.valueOf(CarReteica.getText().toString().trim());
                }
                Integer aCarDescuento;
                if (CarDescuento.getText().toString().trim().isEmpty()) {
                    aCarDescuento = 0;
                } else {
                    aCarDescuento = Integer.valueOf(CarDescuento.getText().toString().trim());
                }
                Integer aCarRetencion;
                if (CarRetencion.getText().toString().trim().isEmpty()) {
                    aCarRetencion = 0;
                } else {
                    aCarRetencion = Integer.valueOf(CarRetencion.getText().toString().trim());
                }
                Integer atotal=aCarAbono-aCarRetencion-aCarReteica-aCarDescuento;

                CarTotal.setText(String.format("%,d", atotal ));

            }
        });

        CarDescuento.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable s) {

                Double Retencion;
                if (s.toString().trim().isEmpty()) {
                    Retencion = 0.0;
                } else {
                    Retencion = Double.valueOf(s.toString());
                }

                Integer aCarAbono;

                if (CarAbono.getText().toString().trim().isEmpty()) {
                    aCarAbono = 0;
                } else {
                    aCarAbono = Integer.valueOf(CarAbono.getText().toString().trim());
                }
                Integer aCarReteica;
                if (CarReteica.getText().toString().trim().isEmpty()) {
                    aCarReteica = 0;
                } else {
                    aCarReteica = Integer.valueOf(CarReteica.getText().toString().trim());
                }
                Integer aCarDescuento;
                if (CarDescuento.getText().toString().trim().isEmpty()) {
                    aCarDescuento = 0;
                } else {
                    aCarDescuento = Integer.valueOf(CarDescuento.getText().toString().trim());
                }
                Integer aCarRetencion;
                if (CarRetencion.getText().toString().trim().isEmpty()) {
                    aCarRetencion = 0;
                } else {
                    aCarRetencion = Integer.valueOf(CarRetencion.getText().toString().trim());
                }
                Integer atotal=aCarAbono-aCarRetencion-aCarReteica-aCarDescuento;

                CarTotal.setText(String.format("%,d", atotal ));

            }
        });
    }
}