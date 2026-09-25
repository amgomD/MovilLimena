package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ficc.mwmovil.databinding.ActivityEditarAbonoBinding;

public class EditarAbono extends AppCompatActivity {

    private ActivityEditarAbonoBinding binding;
    Bundle Extras;
    Double NetoPago = 0.0;
    int tienenota = 0;
    String FacNroDev = " ";
    Double netodev = 0.0;
    Double Saldodev = 0.0;

    Double valRetn = 0.0;
    Double valRetivan = 0.0;
    Double valRetican = 0.0;
    Double financierodev = 0.0;
    String nitsec = "";
    Integer clisec = 0;

    String FacNro ="";
    String nJus = "";

    String[] justificacionlis;

    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_abono);
        getSupportActionBar().hide();
        Extras = this.getIntent().getExtras();
        getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.TrasparenteGris));

        nitsec = Extras.getString("nitsec");
        clisec = Extras.getInt("clisec");
        FacNro = Extras.getString("FacNro");

        Double porRet = Extras.getDouble("porRet");
        Double porRetiva = Extras.getDouble("porRetiva");
        Double porRetica = Extras.getDouble("porRetica");


        Double Subtotal = Extras.getDouble("Subtotal");
        Double Saldo = Extras.getDouble("Saldo");
        Spinner justificacion = findViewById(R.id.justificacion);

        Double Total = Extras.getDouble("Total");
        Double Retencion = Extras.getDouble("Retencion");
        Double Financiero =  Extras.getDouble("Financiero");
        Double ReteIva = Extras.getDouble("ReteIva");
        Double ReteIca = Extras.getDouble("ReteIca");
        Double Abono = Extras.getDouble("Abono");
        Double dctoprovsis = (Double) Math.ceil(Extras.getDouble("dctoprovsis")) ;
        Double dctonootosis = (Double) Math.ceil(Extras.getDouble("dctonootosis")) ;
        Double ddctoconfsis = (Double) Math.ceil(Extras.getDouble("dctoconfsis"));
        String Tipo = Extras.getString("Tipo");
        NetoPago = Extras.getDouble("NetoPago");
        Double Saldoimpuestos = Saldo - (Subtotal*porRet)-(Subtotal*porRetiva)-((Subtotal*porRetica)/1000) - Financiero;

        TextView pago = findViewById(R.id.pago);
        TextView saldotxt = findViewById(R.id.Saldotxt);
        TextView Pagarsinnota = findViewById(R.id.Pagarsinnota);
        LinearLayout saldoNota = findViewById(R.id.saldoNota);
        Pagarsinnota.setVisibility(View.GONE);

        saldoNota.setVisibility(View.GONE);
        saldotxt.setText(String.format("%,d",Saldoimpuestos.intValue()));
        EditText abono = findViewById(R.id.abono);
        EditText dctofin = findViewById(R.id.dctofin);
        EditText dctoprov = findViewById(R.id.dctoprov);
        EditText dctoconf = findViewById(R.id.dctoconf);
        EditText DctoNoOto = findViewById(R.id.DctoNoOto);
        EditText Aprove = findViewById(R.id.Aprove);

        TextView Observacion = findViewById(R.id.Observacion);
        TextView numNota = findViewById(R.id.numNota);
        TextView pagonota = findViewById(R.id.pagonota);

        TextView dctoconfsis = findViewById(R.id.dctoconfsis);
        TextView dctoprovgsis = findViewById(R.id.dctoprovgsis);
        TextView dctonotogsis = findViewById(R.id.dctonotogsis);
        dctofin.setEnabled(false);

   if(Tipo.equalsIgnoreCase("N")){
       dctofin.setEnabled(false);
        dctoprov.setEnabled(false);
        dctoconf.setEnabled(false);
        DctoNoOto.setEnabled(false);
        Aprove.setEnabled(false);
   }

        dctofin.setText(String.valueOf(Financiero.intValue()));

        BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
        Double netodev = saldonota(FacNro,"1");
        Double valAbono = 0.0;
        String nOBs = "";

        Cursor cursorrete = BaseDeDatos.getReadableDatabase().rawQuery("select ifnull(descuento,0) descuento, ifnull(dctoprovpor,0) dctoprovpor,ifnull(dctonootopor,0) dctonootopor,ifnull(aprove,0) aprove,ifnull(dctoconfpor,0) dctoconfpor,abono,pagototal,Observacion, Justificacion from recibo where nitsec ='"+nitsec+"' and clisec = "+clisec+" and facnro = '"+FacNro+"' ", null);
        if (cursorrete.getCount()>0) {
            cursorrete.moveToFirst();
            do {
                dctoprov.setText(String.valueOf(cursorrete.getInt(1)));
                dctoconf.setText(String.valueOf(cursorrete.getInt(4)));
                DctoNoOto.setText(String.valueOf(cursorrete.getInt(2)));
                Aprove.setText(String.valueOf(cursorrete.getInt(3)));
                if(cursorrete.getString(6).equalsIgnoreCase("N")){
                    valAbono = cursorrete.getDouble(5);
                    nOBs = cursorrete.getString(7);
                    nJus = cursorrete.getString(8);
                }


            } while (cursorrete.moveToNext());
        }

        dctoprovgsis.setText(String.format("%,d",dctoprovsis.intValue()));
        dctonotogsis.setText(String.format("%,d",dctonootosis.intValue()));
        dctoconfsis.setText(String.format("%,d",ddctoconfsis.intValue()));
        abono.setText(String.valueOf(valAbono.intValue()));
        Observacion.setText(nOBs);
        pago.setText(String.format("%,d",Saldoimpuestos.intValue()));
        saldoconabono(valAbono,Saldoimpuestos);



        Button pagar = findViewById(R.id.Pagar);

        Button Abonar = findViewById(R.id.Abonar);
        Button cancelar = findViewById(R.id.Cancelar);

        Time time = new Time();
        time.setToNow();
        int actualjus = 0;
        Cursor CursorJustificacion = BaseDeDatos.getReadableDatabase().rawQuery(" select JustSalSec , JustSalDes from JustificacionSaldo ", null);
          int vuelta = 0;
        if (CursorJustificacion.getCount()>0) {
            CursorJustificacion.moveToFirst();
            justificacionlis = new String[CursorJustificacion.getCount()+1];
            justificacionlis[vuelta] = "Ninguno";
            vuelta += 1;
            do {
                if(nJus.equalsIgnoreCase(CursorJustificacion.getString(1))){
                    actualjus = vuelta;
                }
                justificacionlis[vuelta] = CursorJustificacion.getString(1);
                vuelta += 1;
            } while (CursorJustificacion.moveToNext());
            justificacion.setAdapter(new ArrayAdapter<String>(EditarAbono.this, R.layout.support_simple_spinner_dropdown_item, justificacionlis)); // simple_spinner_item
            justificacion.setSelection(actualjus);
        }



        abono.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String abonotxt = editable.toString();
                Double abonoval = 0.0;
                if(abonotxt.isEmpty()){
                    abonoval = 0.0;
                }else{
                    abonoval = Double.valueOf(abonotxt);
                }
                saldoconabono(abonoval,Saldoimpuestos);
            }
        });
        dctoprov.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                if(s.isEmpty()){
                    s = "0.0";
                }


                Double dctoprovtxt = Double.valueOf(s);
                if(dctoprovtxt > dctoprovsis){
                    dctoprovtxt = dctoprovsis;
                    dctoprov.setText(String.valueOf(dctoprovsis.intValue()));
                }

                /*else{
                Double ctofintxt = Double.valueOf(dctofin.getText().toString());
                Double dctoconftxt = Double.valueOf(dctoconf.getText().toString());
                Double DctoNoOtotxt = Double.valueOf(DctoNoOto.getText().toString());
                Double Aprovetxt = Double.valueOf(Aprove.getText().toString());
                Double nto= Saldo-Retencion-ReteIva-ReteIca-ctofintxt-dctoprovtxt-dctoconftxt-DctoNoOtotxt+Aprovetxt;
                NetoPago = nto;
                pago.setText(String.format("%,d",nto.intValue()));
                }*/

            }
        });
        dctoconf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                if(s.isEmpty()){
                    s = "0.0";
                }
                Double dctoconftxt = Double.valueOf(s);
                if(dctoconftxt > ddctoconfsis){
                    dctoconftxt = ddctoconfsis;
                    dctoconf.setText(String.valueOf(ddctoconfsis.intValue()));
                }
                /*
                Double dctofintxt = Double.valueOf(dctofin.getText().toString());
                Double dctoprovftxt = Double.valueOf(dctoprov.getText().toString());
                Double DctoNoOtotxt = Double.valueOf(DctoNoOto.getText().toString());
                Double Aprovetxt = Double.valueOf(Aprove.getText().toString());


                Double nto= Saldo-Retencion-ReteIva-ReteIca-dctofintxt-dctoprovftxt-dctoconftxt-DctoNoOtotxt+Aprovetxt;
                NetoPago = nto;
                pago.setText(String.format("%,d",nto.intValue()));
                 */
            }
        });
        DctoNoOto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                if(s.isEmpty()){
                    s = "0.0";
                }
                Double DctoNoOtotxt = Double.valueOf(s);
                if(DctoNoOtotxt > dctonootosis){
                    DctoNoOtotxt = dctonootosis;
                    DctoNoOto.setText(String.valueOf(dctonootosis.intValue()));
                }



               /* Double dctofintxt = Double.valueOf(dctofin.getText().toString());
                Double dctoprovftxt = Double.valueOf(dctoprov.getText().toString());
                Double dctoconftxt = Double.valueOf(dctoconf.getText().toString());
                Double Aprovetxt = Double.valueOf(Aprove.getText().toString());


                Double nto= Saldo-Retencion-ReteIva-ReteIca-dctofintxt-dctoprovftxt-dctoconftxt-DctoNoOtotxt+Aprovetxt;
                NetoPago = nto;
                pago.setText(String.format("%,d",nto.intValue()));

                */
            }
        });
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseDeDatos.getWritableDatabase().execSQL("delete from Recibo where nitsec ='"+nitsec+"' and clisec = "+clisec+" and facnro = '"+FacNro+"' " );
                Intent intent = new Intent();
                intent.putExtra("abono", 0);
                intent.putExtra("pagototal", "N");
                intent.putExtra("Retencion", Retencion);
                intent.putExtra("ReteIca", ReteIca);
                intent.putExtra("ReteIva", ReteIva);
                intent.putExtra("Dcto", Financiero);
                intent.putExtra("dctoprov", 0);
                intent.putExtra("dctonooto", 0);
                intent.putExtra("aprove", 0);
                intent.putExtra("dctoconf", 0);
                setResult(RESULT_OK, intent);
                finish();

            }
        });
        pagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sdctofinxt =  (dctofin.getText().toString().isEmpty()) ? "0.0" : dctofin.getText().toString();
                String sdctoprovtxt = (dctoprov.getText().toString().isEmpty()) ? "0.0" : dctoprov.getText().toString();
                String sdctoconftxt = (dctoconf.getText().toString().isEmpty()) ? "0.0" : dctoconf.getText().toString();
                String sDctoNoOtotxt = (DctoNoOto.getText().toString().isEmpty()) ? "0.0" : DctoNoOto.getText().toString();
                String sAprovetxt =  (Aprove.getText().toString().isEmpty()) ? "0.0" : Aprove.getText().toString();

                Double dctofinxt = Double.valueOf(sdctofinxt);
                Double dctoprovtxt = Double.valueOf(sdctoprovtxt);
                Double dctoconftxt = Double.valueOf(sdctoconftxt);
                Double DctoNoOtotxt = Double.valueOf(sDctoNoOtotxt);
                Double Aprovetxt = Double.valueOf(sAprovetxt);





                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();

                NetoPago = Saldo-Retencion-ReteIva-ReteIca-dctofinxt+Aprovetxt;


                Double valorprov =  dctoprovtxt   ; //   (NetoPago*(dctoprovtxt/100))
                Double valorconf = dctoconftxt ; //(NetoPago*(/100))
                Double valornoto = DctoNoOtotxt ; //(NetoPago*(DctoNoOtotxt/100))
                int ResultOk = RESULT_OK;
                NetoPago = NetoPago-valorprov-valorconf-valornoto;
                if(NetoPago > 0){
                    String NumPedido = "REC"+nitsec+clisec+time.year +"-"+ (time.month + 1) +"-"+ time.monthDay;
                    BaseDeDatos.getWritableDatabase().execSQL("delete from Recibo where nitsec ='"+nitsec+"' and clisec = "+clisec+" and facnro = '"+FacNro+"' " );
                    String consulta = "Insert into Recibo (VenCod,nitsec,clisec,facnro,RecNro,saldo,abono,retefue,retica,retiva,descuento,rcyear,rcmonth,rcday,dctoprov,dctonooto,aprove,dctoconf,pagototal,dctoprovpor,dctonootopor,dctoconfpor,tipo) values ( " +
                            " '"+vUsuario+"','"+nitsec+"',"+clisec+",'"+FacNro+"','"+NumPedido+"',"+Saldo+","+NetoPago+","+Retencion+","+ReteIca+","+ReteIva+","+dctofinxt+","+ time.year+","+(time.month + 1)+","+time.monthDay+","+valorprov+","+valornoto+","+Aprovetxt+","+valorconf+",'S',"+dctoprovtxt+","+DctoNoOtotxt+","+dctoconftxt+",'"+Tipo+"' )";
                    BaseDeDatos.getWritableDatabase().execSQL(consulta);

                    if(tienenota==1){
                       Double slado = saldonota(FacNro,"2");
                        ResultOk = 166;
                    }

                    Intent intent = new Intent();
                    intent.putExtra("abono", NetoPago);
                    intent.putExtra("pagototal", "S");
                    intent.putExtra("Retencion", Retencion);
                    intent.putExtra("ReteIca", ReteIca);
                    intent.putExtra("ReteIva", ReteIva);
                    intent.putExtra("Dcto", dctofinxt);
                    intent.putExtra("dctoprov", dctoprovtxt);
                    intent.putExtra("dctonooto", DctoNoOtotxt);
                    intent.putExtra("aprove", Aprovetxt);
                    intent.putExtra("dctoconf", dctoconftxt);
                    setResult(ResultOk, intent);
                    finish();
                }else{
                    AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                    Alerta.setMessage("Valor por debajo de lo permitido");
                    Alerta.setTitle("Notificacion");
                    Alerta.setPositiveButton("OK", null);
                    Alerta.setCancelable(true);
                    Alerta.create().show();
                }

            }
        });
        Pagarsinnota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sdctofinxt =  (dctofin.getText().toString().isEmpty()) ? "0.0" : dctofin.getText().toString();
                String sdctoprovtxt = (dctoprov.getText().toString().isEmpty()) ? "0.0" : dctoprov.getText().toString();
                String sdctoconftxt = (dctoconf.getText().toString().isEmpty()) ? "0.0" : dctoconf.getText().toString();
                String sDctoNoOtotxt = (DctoNoOto.getText().toString().isEmpty()) ? "0.0" : DctoNoOto.getText().toString();
                String sAprovetxt =  (Aprove.getText().toString().isEmpty()) ? "0.0" : Aprove.getText().toString();

                Double dctofinxt = Double.valueOf(sdctofinxt);
                Double dctoprovtxt = Double.valueOf(sdctoprovtxt);
                Double dctoconftxt = Double.valueOf(sdctoconftxt);
                Double DctoNoOtotxt = Double.valueOf(sDctoNoOtotxt);
                Double Aprovetxt = Double.valueOf(sAprovetxt);





                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vUsuario=vGlobalVariables.getUsuario();

                NetoPago = Saldo-Retencion-ReteIva-ReteIca-dctofinxt+Aprovetxt;


                Double valorprov =  dctoprovtxt   ; //   (NetoPago*(dctoprovtxt/100))
                Double valorconf = dctoconftxt ; //(NetoPago*(/100))
                Double valornoto = DctoNoOtotxt ; //(NetoPago*(DctoNoOtotxt/100))

                NetoPago = NetoPago-valorprov-valorconf-valornoto;
                if(NetoPago > 0){
                    String NumPedido = "REC"+nitsec+clisec+time.year +"-"+ (time.month + 1) +"-"+ time.monthDay;
                    BaseDeDatos.getWritableDatabase().execSQL("delete from Recibo where nitsec ='"+nitsec+"' and clisec = "+clisec+" and facnro = '"+FacNro+"' " );
                    String consulta = "Insert into Recibo (VenCod,nitsec,clisec,facnro,RecNro,saldo,abono,retefue,retica,retiva,descuento,rcyear,rcmonth,rcday,dctoprov,dctonooto,aprove,dctoconf,pagototal,dctoprovpor,dctonootopor,dctoconfpor,tipo) values ( " +
                            " '"+vUsuario+"','"+nitsec+"',"+clisec+",'"+FacNro+"','"+NumPedido+"',"+Saldo+","+NetoPago+","+Retencion+","+ReteIca+","+ReteIva+","+dctofinxt+","+ time.year+","+(time.month + 1)+","+time.monthDay+","+valorprov+","+valornoto+","+Aprovetxt+","+valorconf+",'S',"+dctoprovtxt+","+DctoNoOtotxt+","+dctoconftxt+",'"+Tipo+"' )";
                    BaseDeDatos.getWritableDatabase().execSQL(consulta);
                    Intent intent = new Intent();
                    intent.putExtra("abono", NetoPago);
                    intent.putExtra("pagototal", "S");
                    intent.putExtra("Retencion", Retencion);
                    intent.putExtra("ReteIca", ReteIca);
                    intent.putExtra("ReteIva", ReteIva);
                    intent.putExtra("Dcto", dctofinxt);
                    intent.putExtra("dctoprov", dctoprovtxt);
                    intent.putExtra("dctonooto", DctoNoOtotxt);
                    intent.putExtra("aprove", Aprovetxt);
                    intent.putExtra("dctoconf", dctoconftxt);
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
                    AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                    Alerta.setMessage("Valor por debajo de lo permitido");
                    Alerta.setTitle("Notificacion");
                    Alerta.setPositiveButton("OK", null);
                    Alerta.setCancelable(true);
                    Alerta.create().show();
                }

            }
        });
        Abonar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!abono.getText().toString().isEmpty()){

                    Double impuestos = Retencion+ReteIva+ReteIca;
                    String Observaciontxt  = Observacion.getText().toString();
                    String sdctofinxt =  (dctofin.getText().toString().isEmpty()) ? "0.0" : dctofin.getText().toString();
                    String sdctoprovtxt = (dctoprov.getText().toString().isEmpty()) ? "0.0" : dctoprov.getText().toString();
                    String sdctoconftxt = (dctoconf.getText().toString().isEmpty()) ? "0.0" : dctoconf.getText().toString();
                    String sDctoNoOtotxt = (DctoNoOto.getText().toString().isEmpty()) ? "0.0" : DctoNoOto.getText().toString();
                    String sAprovetxt =  (Aprove.getText().toString().isEmpty()) ? "0.0" : Aprove.getText().toString();

                    Double dctofinxt = Double.valueOf(sdctofinxt);
                    Double dctoprovtxt = Double.valueOf(sdctoprovtxt);
                    Double dctoconftxt = Double.valueOf(sdctoconftxt);
                    Double DctoNoOtotxt = Double.valueOf(sDctoNoOtotxt);
                    Double Aprovetxt = Double.valueOf(sAprovetxt);
                    Double abonotxt = Double.valueOf(abono.getText().toString());
                    String mensaje = "";
                    int banerro = 0;

                    if(dctofinxt+dctoprovtxt+dctoconftxt+DctoNoOtotxt+Aprovetxt > 0){
                        mensaje = "Los descuentos y aprovechamiento solo aplican al pago total";
                        banerro = 1;
                    }
                    if(justificacion.getSelectedItem().toString().isEmpty() || justificacion.getSelectedItem().toString().equalsIgnoreCase("Ninguno")){
                        mensaje = "Debe seleccionar una justificación";
                        banerro = 1;
                    }

                    if((Saldo-abonotxt) < impuestos){
                        mensaje = "El abono no debe superar el valor total con impuestos";
                        banerro = 1;
                    }


                    if(banerro == 0){
                           String NumPedido = "REC"+nitsec+clisec+time.year +"-"+ (time.month + 1) +"-"+ time.monthDay;
                            GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                            String vUsuario=vGlobalVariables.getUsuario();
                            BaseDeDatos.getWritableDatabase().execSQL("delete from Recibo where nitsec ='"+nitsec+"' and clisec = "+clisec+" and facnro = '"+FacNro+"' " );
                            String consulta = "Insert into Recibo (VenCod,nitsec,clisec,facnro,RecNro,saldo,abono,Observacion,Justificacion,retefue,retica,retiva,descuento,rcyear,rcmonth,rcday,dctoprov,dctonooto,aprove,dctoconf,pagototal,dctoprovpor,dctonootopor,dctoconfpor,tipo) values ( " +
                                    " '"+vUsuario+"','"+nitsec+"',"+clisec+",'"+FacNro+"','"+NumPedido+"',"+Saldo+","+abono.getText().toString()+",'"+Observaciontxt+"','"+justificacion.getSelectedItem().toString()+"',0,0,0,"+dctofinxt+","+ time.year+","+(time.month + 1)+","+time.monthDay+","+dctoprovtxt+","+DctoNoOtotxt+","+Aprovetxt+","+dctoconftxt+",'N',0,0,0,'"+Tipo+"')";
                            BaseDeDatos.getWritableDatabase().execSQL(consulta);
                            Intent intent = new Intent();
                            intent.putExtra("abono", abonotxt);
                            intent.putExtra("pagototal", "N");
                            intent.putExtra("Retencion", 0);
                            intent.putExtra("ReteIca", 0);
                            intent.putExtra("ReteIva", 0);
                            intent.putExtra("Dcto", dctofinxt);
                            intent.putExtra("dctoprov", dctoprovtxt);
                            intent.putExtra("dctonooto", DctoNoOtotxt);
                            intent.putExtra("aprove", Aprovetxt);
                            intent.putExtra("dctoconf", dctoconftxt);
                            setResult(RESULT_OK, intent);
                            finish();
                    }
                  else{
                        AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                        Alerta.setMessage(mensaje);
                        Alerta.setTitle("Notificacion");
                        Alerta.setPositiveButton("OK", null);
                        Alerta.setCancelable(true);
                        Alerta.create().show();
                    }


                }else{
                    AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                    Alerta.setMessage("Debe ingresar valor al abono");
                    Alerta.setTitle("Notificacion");
                    Alerta.setPositiveButton("OK", null);
                    Alerta.setCancelable(true);
                    Alerta.create().show();
                }

            }
        });
    }

    public Double saldonota(String FacnroDev,String modo){
        netodev = 0.0;
        Double insertpago =0.0;
        BaseDatos BaseDeDatos;
        TextView numNota = findViewById(R.id.numNota);
        TextView pagonota = findViewById(R.id.pagonota);

        Double porRet =Extras.getDouble("porRet");
        Double porRetiva =Extras.getDouble("porRetiva");
        Double porRetica =Extras.getDouble("porRetica");

        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();

        Time time = new Time();
        time.setToNow();


        TextView Pagarsinnota = findViewById(R.id.Pagarsinnota);
        LinearLayout saldoNota = findViewById(R.id.saldoNota);
       String consulta ="";
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
        Cursor traerNota = BaseDeDatos.getReadableDatabase().rawQuery("select MovFacSec , FacSaldo,KarValTotMendes, financiero from cartera  left join DescuentosFac d on MovFacSec = FacNro  where FacNroDev = '"+FacnroDev+"' ", null);
        if (traerNota.getCount()>0) {
            numNota.setText(String.valueOf(traerNota.getCount())+" Notas");
            traerNota.moveToFirst();
            do {
                FacNroDev =  traerNota.getString(0);
                Saldodev = traerNota.getDouble(1)*-1;
                Double subTotal = traerNota.getDouble(2);
                valRetn = subTotal*porRet;
                valRetivan = subTotal*porRetiva;
                valRetican = (subTotal*porRetica)/1000;
                Pagarsinnota.setVisibility(View.VISIBLE);
                saldoNota.setVisibility(View.VISIBLE);
                financierodev = traerNota.getDouble(3);
                netodev += Saldodev-valRetn-valRetivan-valRetican-financierodev;
                insertpago = Saldodev-valRetn-valRetivan-valRetican-financierodev;



                pagonota.setText("-"+String.format("%,d",netodev.intValue()));
                tienenota = 1;

                if(modo.equalsIgnoreCase("2"))
                {
                    BaseDeDatos.getWritableDatabase().execSQL("delete from Recibo where nitsec ='"+nitsec+"' and clisec = "+clisec+" and facnro = '"+FacNroDev+"' " );
                    consulta = "Insert into Recibo (VenCod,nitsec,clisec,facnro,saldo,abono,retefue,retica,retiva,descuento,rcyear,rcmonth,rcday,dctoprov,dctonooto,aprove,dctoconf,pagototal,dctoprovpor,dctonootopor,dctoconfpor,tipo) values ( " +
                            " '"+vUsuario+"','"+nitsec+"',"+clisec+",'"+FacNroDev+"',"+Saldodev+","+insertpago+","+valRetn+","+valRetican+","+valRetivan+","+financierodev+","+ time.year+","+(time.month + 1)+","+time.monthDay+",0,0,0,0,'S',0,0,0,'N' )";
                    BaseDeDatos.getWritableDatabase().execSQL(consulta);
                }



            } while (traerNota.moveToNext());
        }

        return insertpago;
    }
  public void saldoconabono(Double abono, Double Saldoimpuestos){
      Double restante = Saldoimpuestos-abono;
      TextView Saldotxt = findViewById(R.id.Saldotxt);
      Saldotxt.setText(String.format("%,d",restante.intValue()));
  }


}