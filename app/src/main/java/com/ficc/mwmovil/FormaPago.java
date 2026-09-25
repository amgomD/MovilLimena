package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;


public class FormaPago extends AppCompatActivity {
    private final String ruta_fotos = Environment.getExternalStorageDirectory().toString() + "/MantisWeb/";
    private File file = new File(ruta_fotos);
    Bundle Extras;
    ImageView showImg ;
    List<ArrayFotos> arrayFotos;
    ListViewAdapterFotos ListViewAdapterFotos;
    String rrfile;
    SDTFotos[] SDTFotos;
    File mi_foto;
    Uri uri;
    int veces = 0;
    static String Path;
    private Uri imageUri;
    FormaPago CameraActivity = null;
    SDTFormaPago[] SDTFormaPago;
    static TextView imageDetails;
    LinearLayout todo;
    LinearLayout consignaefe;
    String vetodo ="S";
    ScrollView efectivo;
    Button verfotos;
    Double valorfotos;
    String pucsec;
    String checkval;
    Integer llave;
    String[] valuescuentaid;
    int valfotos;
    ListViewAdapterformapago listViewAdapterformapago;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forma_pago);
        Extras=this.getIntent().getExtras();
        getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.TrasparenteGris));
        String nitsec=Extras.getString("nitsec");
        Integer clisec=Extras.getInt("clisec");
        Double TotalAbonos = Extras.getDouble("TotalAbonos");
        Button btnconsignar = findViewById(R.id.btnconsignar);
        Button btntrans = findViewById(R.id.btntrans);

        Button btncheque = findViewById(R.id.btncheque);
        Button btnefectivo = findViewById(R.id.btnefectivo);
        Button volverefectivo = findViewById(R.id.volverefectivo);
        Button agregarchequecon = findViewById(R.id.agregarchequecon);
        Button agregarefectivo = findViewById(R.id.agregarefectivo);
        TextView fechaconche = findViewById(R.id.fechaconche);
        Button btnCiudad= (Button) findViewById(R.id.btnCiudad);
        Button btnbancos = (Button) findViewById(R.id.btnbancos);
         verfotos = (Button) findViewById(R.id.verfotos);
        Spinner spcuenta  = findViewById(R.id.spcuenta);
        RadioGroup radiogrupo = findViewById(R.id.radiogrupo);
        Time time = new Time();
        time.setToNow();

        String fechtxt = time.year+"-"+(time.month + 1)+"-"+time.monthDay;
        Button volvercheque = findViewById(R.id.volvercheque);
        TextView Formapago = findViewById(R.id.Formapago);
        TextView totaldigcheque = findViewById(R.id.totaldigcheque);
        TextView cambioregnum = findViewById(R.id.cambioregnum);
        TextView txtciudadcta = findViewById(R.id.txtciudadcta);
        //TextView Fecha = findViewById(R.id.fecha);
//        Fecha.setText(fechtxt);
        TextView totalfp = findViewById(R.id.totalfp);
        TextView totalrecibo = findViewById(R.id.totalrecibo);
        TextView totalconsignacion = findViewById(R.id.totalconsignacion);
        TextView totalCheque = findViewById(R.id.totalCheque);
        TextView TotalEfecttivo = findViewById(R.id.TotalEfecttivo);
        TextView Totaltra = findViewById(R.id.totaltransferencia);

        TextView totalagregado = findViewById(R.id.totalagregado);
        TextView totalaggefe = findViewById(R.id.totalrecibo2);
        radiogrupo.setVisibility(View.GONE);
        RadioButton aldia = findViewById(R.id.aldia);
        RadioButton postfechado = findViewById(R.id.postfechado);
        aldia.setChecked(true);
        TextView totalrecibo3 = findViewById(R.id.totalrecibo3);
        TextView totalrecibo2 = findViewById(R.id.totalrecibo2);
        TextView numeroop = findViewById(R.id.numeroop);

        EditText cicuenta = findViewById(R.id.cicuenta);
        EditText valor = findViewById(R.id.valor);
        EditText ctacheque = findViewById(R.id.ctacheque);
        //valor.setEnabled(false);


        cicuenta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                TextView totaldig = findViewById(R.id.totaldig);
                String s = editable.toString();
                if(s.isEmpty()){
                    s = "0";
                }
                Double edit = Double.valueOf(s);
                Double valortotal =edit;
                totaldig.setText(String.format("%,d",valortotal.intValue()));
              //  totalizarefectivo();
            }
        });
        valor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                TextView totaldig = findViewById(R.id.totaldigcheque);
                String s = editable.toString();
                if(s.isEmpty()){
                    s = "0";
                }
                Double edit = Double.valueOf(s);
                Double valortotal =edit;
                totaldig.setText(String.format("%,d",valortotal.intValue()));
            }
        });



        Spinner ano  = findViewById(R.id.ano);
        Spinner mes  = findViewById(R.id.mes);
        Spinner dia  = findViewById(R.id.dia);


        /*

*/

        totalrecibo.setText(String.format("%,d",TotalAbonos.intValue()));
        totalrecibo3.setText(String.format("%,d",TotalAbonos.intValue()));
        totalrecibo2.setText(String.format("%,d",TotalAbonos.intValue()));
        BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);


         todo = findViewById(R.id.todo);
         consignaefe = findViewById(R.id.chequeconsigna);
         efectivo =findViewById(R.id.efectivo);


        Double Totalfp=0.0;
        Double chequefp=0.0;
        Double consigfp=0.0;
        Double efecfp=0.0;
        Double trafp=0.0;
        try {
            Cursor cursor = BaseDeDatos.getReadableDatabase().rawQuery("select Tipo, Valor,facnro from Reciboforma where nitsec='" + nitsec + "' and clisec=" + clisec + " and recicyear=" + time.year + " and recicmonth=" + (time.month + 1) + " and recicday=" + time.monthDay + " ", null);
            SDTFormaPago=new SDTFormaPago[cursor.getCount()];
            Integer vuelta=0;
            llave = cursor.getCount()+1;
            if (cursor.getCount()>0) {
                cursor.moveToFirst();
                do {
                    SDTFormaPago SDTFormaPagoItem= new SDTFormaPago();
                //    Log.e("tipo: ",cursor.getString(0));
                    SDTFormaPagoItem.tipo = cursor.getString(0);

                    SDTFormaPagoItem.facnro = cursor.getString(2);
                    SDTFormaPagoItem.NitSec = nitsec;
                    SDTFormaPagoItem.clisec = clisec;
                    SDTFormaPagoItem.valor = cursor.getDouble(1);
                    if(cursor.getString(0).equalsIgnoreCase("Cheque")){
                        chequefp+= cursor.getDouble(1);
                    }
                    if(cursor.getString(0).equalsIgnoreCase("Consignacion")){
                        consigfp+= cursor.getDouble(1);
                    }
                    if(cursor.getString(0).equalsIgnoreCase("Efectivo")){
                        efecfp+= cursor.getDouble(1);
                    }

                    if(cursor.getString(0).equalsIgnoreCase("Transferencia")){
                        trafp+= cursor.getDouble(1);
                    }


                    Totalfp += cursor.getDouble(1);
                    SDTFormaPago[vuelta]=SDTFormaPagoItem;
                    vuelta+=1;
                } while (cursor.moveToNext());
            }


        }catch (Exception e)
        {
            Log.e("ErrorLisforma",e.toString());
        }

        final ListView ListaFormapago = (ListView) findViewById(R.id.ListaFormapago);
        listViewAdapterformapago = new ListViewAdapterformapago(this, SDTFormaPago);
        ListaFormapago.setAdapter(listViewAdapterformapago);
        totalfp.setText(String.format("%,d",Totalfp.intValue()));
        totalconsignacion.setText(String.format("%,d",consigfp.intValue()));
        totalCheque.setText(String.format("%,d",chequefp.intValue()));
        TotalEfecttivo.setText(String.format("%,d",efecfp.intValue()));
        Totaltra.setText(String.format("%,d",trafp.intValue()));
        ListaFormapago.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                Alerta.setMessage("Está seguro de eliminar esta forma de pago");
                Alerta.setTitle("Notificacion");
                Alerta.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BaseDatos BaseDeDatos;
                        BaseDeDatos = new BaseDatos(FormaPago.this, "MantisMovil", null, 6);
                        String consulta = "delete from Reciboforma where nitsec='" + SDTFormaPago[i].NitSec + "' and clisec=" + SDTFormaPago[i].clisec + " and facnro ='" + SDTFormaPago[i].facnro + "'  ";


                        BaseDeDatos.getWritableDatabase().execSQL(consulta);
                         consulta = "delete from ReciboFormaFotos where nitsec='" + SDTFormaPago[i].NitSec + "' and clisec=" + SDTFormaPago[i].clisec + " and facnro ='" + SDTFormaPago[i].facnro + "'  ";
                        BaseDeDatos.getWritableDatabase().execSQL(consulta);


                        overridePendingTransition(0, 0);
                        getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());


                    }


                });
                Alerta.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                Alerta.setCancelable(true);
                Alerta.create().show();
            }
        });
        todo.setVisibility(View.VISIBLE);
        consignaefe.setVisibility(View.GONE);
        efectivo.setVisibility(View.GONE);



        btnCiudad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), BuscarBarrioCiudad.class);
                i.putExtra("BUSCIUBAR","CIU");
                startActivityForResult(i, 2);

            }
        });

        btnbancos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), BuscarBanco.class);
                i.putExtra("BUSCIUBAR","CIU");
                startActivityForResult(i, 6);

            }
        });

        Double finalTotalfp = Totalfp;
        agregarefectivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView totaldig = findViewById(R.id.totaldig);
                EditText cicuenta = findViewById(R.id.cicuenta);
                Double Valordb =0.0;
                if(cicuenta.getText().toString().isEmpty()){
                    Valordb =0.0;
                }else {
                    Valordb = Double.valueOf(cicuenta.getText().toString());
                }

                Double totalval = Valordb + finalTotalfp;
                if(totalval <= TotalAbonos && Valordb > 0  ){

                    String NumPedido = "REC"+nitsec+clisec+time.year +"-"+ (time.month + 1) +"-"+ time.monthDay;
                    GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                    String vUsuario=vGlobalVariables.getUsuario();
                    String Tipo = "Efectivo";
                    String consulta = "Insert into Reciboforma (VenCod,nitsec,clisec,facnro,RecNro,NroCheque,CodBanco,Valor,Ciudad,CtaBco,Tipo,recicyear,recicmonth,recicday,rcyear,rcmonth,rcday,TipoConsigna) values ( " +
                            " '"+vUsuario+"','"+nitsec+"',"+clisec+",'"+NumPedido+Tipo+String.valueOf(totalval)+"', '"+NumPedido+"'  ,'','',"+Valordb+",'','','Efectivo',"+ time.year+","+(time.month + 1)+","+time.monthDay+" ,"+ time.year+","+(time.month + 1)+","+time.monthDay+",'' )";



                    BaseDeDatos.getWritableDatabase().execSQL(consulta);
                    finish();
                    startActivity(getIntent());
                }else{
                    AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                    Alerta.setMessage("Superó el valor del recibo");
                    Alerta.setTitle("Notificacion");
                    Alerta.setPositiveButton("OK", null);
                    Alerta.setCancelable(true);
                    Alerta.create().show();
                }
            }

        });


        agregarchequecon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView RegOperacheque = findViewById(R.id.RegOperacheque);
                TextView codbancod = findViewById(R.id.codbancod);
                TextView ciudadcta = findViewById(R.id.ciudadcta);
                TextView valor = findViewById(R.id.valor);
              Spinner tipoconsigna =  findViewById(R.id.tipoconsigna);
                Integer anoint = Integer.parseInt(ano.getSelectedItem().toString());
                Integer mesint =  Integer.parseInt(mes.getSelectedItem().toString());
                Integer diaint =  Integer.parseInt(dia.getSelectedItem().toString());


                String saldia = "N";
                String post = "N";
                if (aldia.isChecked()){
                    saldia = "S";
                }else{
                    post = "S";
                }

                String bandera = validarFecha(saldia);

                if(Formapago.getText().toString().equalsIgnoreCase("Consignación") || Formapago.getText().toString().equalsIgnoreCase("Transferencia")){
                    bandera = "S";
                }


                if(bandera.equalsIgnoreCase("S")){


                    Double Valordb =0.0;
                if(valor.getText().toString().isEmpty()){
                    Valordb =0.0;
                }else {
                    Valordb = Double.valueOf(valor.getText().toString());
                }


                String tipo = "Cheque";
                String TipoConsigna = "";
                String ciudad = "";
                String ctabanco = ctacheque.getText().toString();
                Double totalval = Valordb + finalTotalfp;
                int TCNSEC = 0;
                String intCodBanco = "0";
                if(codbancod.getText().toString().isEmpty()){
                     intCodBanco = "0";
                }else{
                    intCodBanco =  codbancod.getText().toString();
                }

                int banderarep = banderanum(RegOperacheque.getText().toString());
                    String PucBanco = "";
                if(Formapago.getText().toString().equalsIgnoreCase("Consignación")){

                    int idsel = (int) spcuenta.getSelectedItemId();
                    PucBanco = pucsec = valuescuentaid[idsel];

                    tipo = "Consignacion";
                    TipoConsigna = "CCLI";
                    banderarep = 0;
                    ciudad =  ciudadcta.getText().toString();
                    ctabanco = "";
                    totalval = Valordb + finalTotalfp;
                    if(tipoconsigna.getSelectedItem().toString().isEmpty()){
                        TCNSEC = 0;
                    }else{
                        TCNSEC = traertipo(tipoconsigna.getSelectedItem().toString());
                    }
                }
                    if(Formapago.getText().toString().equalsIgnoreCase("Transferencia")){
                        tipo = "Transferencia";
                        TipoConsigna = "CTRA";
                        banderarep = 0;
                        ciudad =  ciudadcta.getText().toString();
                        ctabanco = "";
                        totalval = Valordb + finalTotalfp;
                        if(tipoconsigna.getSelectedItem().toString().isEmpty()){
                            TCNSEC = 0;
                        }else{
                            TCNSEC = traertipo(tipoconsigna.getSelectedItem().toString());
                        }
                    }







               if(valfotos == 0){
                   AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                   Alerta.setMessage("Debe ingresar al menos un registro fotografico");
                   Alerta.setTitle("Notificacion");
                   Alerta.setPositiveButton("OK", null);
                   Alerta.setCancelable(true);
                   Alerta.create().show();
               }else{

                   if(banderaconsig() > 0){
                       AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                       Alerta.setMessage("Esta Consignacion ya esta Registrada");
                       Alerta.setTitle("Notificacion");
                       Alerta.setPositiveButton("OK", null);
                       Alerta.setCancelable(true);
                       Alerta.create().show();
                   }else{



                       if(banderarep == 0){


                           if(totalval <= TotalAbonos && Valordb > 0){


                               GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                               String vUsuario=vGlobalVariables.getUsuario();


                               String NumPedido = "REC"+nitsec+clisec+time.year +"-"+ (time.month + 1) +"-"+ time.monthDay;
                               String consulta = "Insert into Reciboforma (VenCod,nitsec,clisec,facnro,RecNro,NroCheque,CodBanco,TCNSEC,Valor,Ciudad,imgBase,CtaBco,Tipo,aldia,postfecha,recicyear,recicmonth,recicday,rcyear,rcmonth,rcday,TipoConsigna,PucBanco) values ( " +
                                       " '"+vUsuario+"','"+nitsec+"',"+clisec+",'"+NumPedido+tipo+String.valueOf(totalval)+"', '"+NumPedido+"'  ,'"+RegOperacheque.getText().toString()+"',"+intCodBanco+","+TCNSEC+","+Valordb+",'"+ciudad+"','','"+ctabanco+"','"+tipo+"','"+saldia+"','"+post+"',"+ time.year+","+(time.month + 1)+","+time.monthDay+" ,"+ anoint+","+mesint+","+diaint+",'"+TipoConsigna+"','"+PucBanco+"')";
                               BaseDeDatos.getWritableDatabase().execSQL(consulta);
                               cargarFotos(totalval);
                               consulta = " delete from fototemp ";
                               BaseDeDatos.getWritableDatabase().execSQL(consulta);

                               finish();
                               startActivity(getIntent());


                           }else{
                               AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                               Alerta.setMessage("Superó el valor del recibo");
                               Alerta.setTitle("Notificacion");
                               Alerta.setPositiveButton("OK", null);
                               Alerta.setCancelable(true);
                               Alerta.create().show();
                           }

                       }else{
                           AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                           Alerta.setMessage("Número de cheque ya ingresado");
                           Alerta.setTitle("Notificacion");
                           Alerta.setPositiveButton("OK", null);
                           Alerta.setCancelable(true);
                           Alerta.create().show();
                       }
                   }






               }




                }else{
                    String mensaje="";
                    if(saldia.equalsIgnoreCase("S")){
                        mensaje =  "Fecha mayor a la actual";
                    }else{
                        mensaje =  "Fecha menor o igual a la actual";
                    }
                    AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                    Alerta.setMessage(mensaje);
                    Alerta.setTitle("Notificacion");
                    Alerta.setPositiveButton("OK", null);
                    Alerta.setCancelable(true);
                    Alerta.create().show();
                }
            }

        });


        volverefectivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verfotos.setText("Tomar Fotos (0)");
                todo.setVisibility(View.VISIBLE);
                vetodo ="S";
                efectivo.setVisibility(View.GONE);
            }
        });
        volvercheque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                todo.setVisibility(View.VISIBLE);
                verfotos.setText("Tomar Fotos (0)");
                vetodo ="S";
                consignaefe.setVisibility(View.GONE);
            }
        });

        aldia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    llenarFecha("S");
                }

            }
        })  ;
        postfechado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    llenarFecha("N");
            }
            }

        })  ;



        btntrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                todo.setVisibility(View.GONE);
                vetodo ="N";
                Formapago.setText("Transferencia");
                fechaconche.setText("Fecha");
                String consulta = " delete from fototemp ";
                BaseDeDatos.getWritableDatabase().execSQL(consulta);
                EditText RegOperacheque =  findViewById(R.id.RegOperacheque);
                cambioregnum.setVisibility(View.GONE);
                RegOperacheque.setVisibility(View.GONE);
                txtciudadcta.setText("Ciudad");
                totalagregado.setText(totalfp.getText().toString());
                llenarFecha("N");
                consignaefe.setVisibility(View.VISIBLE);
                valor.setEnabled(false);
                radiogrupo.setVisibility(View.GONE);
                LinearLayout infociudad = findViewById(R.id.infociudad);
                LinearLayout tipoconsignalin = findViewById(R.id.tipoconsignalin);
                btnCiudad.setVisibility(View.VISIBLE );
                infociudad.setVisibility(View.VISIBLE );
                tipoconsignalin.setVisibility(View.VISIBLE);
                ctacheque.setVisibility(View.GONE);

            }
        });
        btnconsignar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                todo.setVisibility(View.GONE);
                vetodo ="N";
                Formapago.setText("Consignación");
                fechaconche.setText("Fecha Consignación");
                String consulta = " delete from fototemp ";
                BaseDeDatos.getWritableDatabase().execSQL(consulta);
                EditText RegOperacheque =  findViewById(R.id.RegOperacheque);
                cambioregnum.setVisibility(View.GONE);
                RegOperacheque.setVisibility(View.GONE);
                txtciudadcta.setText("Ciudad");
                totalagregado.setText(totalfp.getText().toString());
                llenarFecha("N");
                consignaefe.setVisibility(View.VISIBLE);
                valor.setEnabled(false);
                radiogrupo.setVisibility(View.GONE);
                LinearLayout infociudad = findViewById(R.id.infociudad);
                LinearLayout tipoconsignalin = findViewById(R.id.tipoconsignalin);
                btnCiudad.setVisibility(View.VISIBLE );
                infociudad.setVisibility(View.VISIBLE );
                tipoconsignalin.setVisibility(View.VISIBLE);
                ctacheque.setVisibility(View.GONE);

            }
        });
        btncheque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                todo.setVisibility(View.GONE);
                vetodo ="N";
                Formapago.setText("Cheque");
                fechaconche.setText("Fecha");
                String consulta = " delete from fototemp ";
                BaseDeDatos.getWritableDatabase().execSQL(consulta);
                llenarFecha("S");
                aldia.setChecked(true);
                valor.setEnabled(false);
                cambioregnum.setText("Numero cheque");
                totalagregado.setText(totalfp.getText().toString());
                txtciudadcta.setText("Cta Bco cliente");
                consignaefe.setVisibility(View.VISIBLE);
                LinearLayout infociudad = findViewById(R.id.infociudad);
                LinearLayout tipoconsignalin = findViewById(R.id.tipoconsignalin);
                radiogrupo.setVisibility(View.VISIBLE);
                btnCiudad.setVisibility(View.GONE);
                ctacheque.setVisibility(View.VISIBLE);
                infociudad.setVisibility(View.GONE);
                EditText RegOperacheque =  findViewById(R.id.RegOperacheque);
                cambioregnum.setVisibility(View.VISIBLE);
                RegOperacheque.setVisibility(View.VISIBLE);
                tipoconsignalin.setVisibility(View.GONE);
            }
        });
        btnefectivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                todo.setVisibility(View.GONE);
                String consulta = " delete from fototemp ";
                BaseDeDatos.getWritableDatabase().execSQL(consulta);
                vetodo ="N";
                totalaggefe.setText(totalfp.getText().toString());
                efectivo.setVisibility(View.VISIBLE);
            }
        });

        llenarFecha("S");
        mes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        int anosel = Integer.parseInt(ano.getSelectedItem().toString());
        int mesactual = Integer.parseInt(mes.getSelectedItem().toString());
        int diael = Integer.parseInt(dia.getSelectedItem().toString());
        int diaactual = time.monthDay;

        final Integer[] dias ;
        int finaldias = 0;

        if(mesactual==1 || mesactual==3 || mesactual==5 || mesactual==7 || mesactual==8 || mesactual==10 || mesactual==12){
            dias = new Integer[31];
            finaldias = 31;
        }else{
            if(mesactual == 2){
                if(bisiesto(anosel)){
                //    Log.e("Es bisiesto",String.valueOf(anosel));
                        dias = new Integer[29];
                        finaldias = 29;
                }else{
                    dias = new Integer[28];
                    finaldias = 28;
                }

            }else {
                dias = new Integer[30];
                finaldias = 30;
            }
        }

        int vueltaano = 0;
        int actual = 0;
        for (int j=1;j<=finaldias;j= j + 1){
            if(j==diaactual){
                actual = vueltaano;
            }
            dias[vueltaano] = j;
            vueltaano = vueltaano + 1;
        }
        dia.setAdapter(new ArrayAdapter<Integer>(FormaPago.this, R.layout.support_simple_spinner_dropdown_item, dias)); // simple_spinner_item
        dia.setSelection(actual);

    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
});
        ano.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                int anosel = Integer.parseInt(ano.getSelectedItem().toString());
                int mesactual = Integer.parseInt(mes.getSelectedItem().toString());
                int diael = Integer.parseInt(dia.getSelectedItem().toString());

                final Integer[] dias ;
                int finaldias = 0;

                if(mesactual==1 || mesactual==3 || mesactual==5 || mesactual==7 || mesactual==8 || mesactual==10 || mesactual==12){
                    dias = new Integer[31];
                    finaldias = 31;
                }else{
                    if(mesactual == 2){
                        if(bisiesto(anosel)){
                          //  Log.e("Es bisiesto",String.valueOf(anosel));
                            dias = new Integer[29];
                            finaldias = 29;
                        }else{
                            dias = new Integer[28];
                            finaldias = 28;
                        }

                    }else {
                        dias = new Integer[30];
                        finaldias = 30;
                    }
                }

                int vueltaano = 0;

                for (int j=1;j<=finaldias;j= j + 1){
                    dias[vueltaano] = j;
                    vueltaano = vueltaano + 1;
                }
                dia.setAdapter(new ArrayAdapter<Integer>(FormaPago.this, R.layout.support_simple_spinner_dropdown_item, dias)); // simple_spinner_item
                dia.setSelection(0);

            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spcuenta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int idsel = (int) spcuenta.getSelectedItemId();
                pucsec = valuescuentaid[idsel];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        verfotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /*  if(valor.getText().toString().isEmpty()){
                     AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                     Alerta.setMessage("Debe llenar el valor primero");
                     Alerta.setTitle("Notificacion");
                     Alerta.setPositiveButton("OK", null);
                     Alerta.setCancelable(true);
                     Alerta.create().show();
                 }else{
*/


                     EditText codbancod = findViewById(R.id.codbancod);
                     String tCodBanco = codbancod.getText().toString().trim();

                     if(tCodBanco.isEmpty()){
                         AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                         Alerta.setMessage("Debe Elegir el banco primero");
                         Alerta.setTitle("Notificacion");
                         Alerta.setPositiveButton("OK", null);
                         Alerta.setCancelable(true);
                         Alerta.create().show();
                     }else{

                     int idsel = (int) spcuenta.getSelectedItemId();

                     if(valuescuentaid == null){
                         llenarpuc(tCodBanco);
                         }

                         if(valuescuentaid != null) {
                             pucsec = valuescuentaid[idsel];
                         }
                         if(!pucsec.isEmpty() && pucsec != null){
                             Integer anoint = Integer.parseInt(ano.getSelectedItem().toString());
                             Integer mesint =  Integer.parseInt(mes.getSelectedItem().toString());
                             Integer diaint =  Integer.parseInt(dia.getSelectedItem().toString());
                             EditText ciudadcta = findViewById(R.id.ciudadcta);
                             Intent intent = new Intent(getApplicationContext(), SubirFotos.class);
                             intent.putExtra("llave", llave);
                             intent.putExtra("modo", "forma");
                             intent.putExtra("nitsec", Extras.getString("nitsec"));
                             intent.putExtra("clisec", Extras.getInt("clisec"));
                             intent.putExtra("checkval",checkval);
                             intent.putExtra("pucsec",pucsec);
                             //intent.putExtra("valor",  Double.valueOf(valor.getText().toString()));
                             intent.putExtra("valor", 0);
                             intent.putExtra("ano",  anoint);
                             intent.putExtra("mes", mesint);
                             intent.putExtra("dia",  diaint);
                             intent.putExtra("ciucod",  ciudadcta.getText().toString().trim());
                             startActivityForResult(intent,1);
                         }else{
                             AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                             Alerta.setMessage("Debe Elegir una cuenta");
                             Alerta.setTitle("Notificacion");
                             Alerta.setPositiveButton("OK", null);
                             Alerta.setCancelable(true);
                             Alerta.create().show();
                         }



                     }
                 //}


            }
        });










    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
            verfotos.setText("Tomar Fotos (0)");
           if(vetodo.equalsIgnoreCase("N")){
               vetodo = "S";
               todo.setVisibility(View.VISIBLE);
               consignaefe.setVisibility(View.GONE);
               efectivo.setVisibility(View.GONE);

           }else{
            finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    private void cargarFotos(Double totalval) {
        BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
        Cursor fotosc = BaseDeDatos.getReadableDatabase().rawQuery("select NroOp , imgBase,ValorOp from fototemp ", null);
        String nitsec=Extras.getString("nitsec");
        Integer clisec=Extras.getInt("clisec");
        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario =vGlobalVariables.getUsuario();
        if (fotosc.getCount() > 0) {
            fotosc.moveToFirst();
            do {

                Time time = new Time();
                time.setToNow();
                TextView Formapago = findViewById(R.id.Formapago);

                String tipo ="Cheque";
                if(Formapago.getText().toString().equalsIgnoreCase("Consignación")){
                    tipo ="Consignacion";
                }

                String NumPedido = "REC"+nitsec+clisec+time.year +"-"+ (time.month + 1) +"-"+ time.monthDay;

                String consulta = "Insert into ReciboFormaFotos (VenCod,nitsec,clisec,facnro,NroOp,ValorOp,imgBase) values ( " +
                        " '"+vUsuario+"','"+nitsec+"',"+clisec+",'"+NumPedido+tipo+String.valueOf(totalval)+"','"+fotosc.getString(0)+"',"+fotosc.getDouble(2)+",'"+fotosc.getString(1)+"')";
                BaseDeDatos.getWritableDatabase().execSQL(consulta);
            } while (fotosc.moveToNext());
        }
    }

    public static boolean bisiesto(int year) {
        if (year % 4 == 0) {
            if (year % 100 == 0) {
                if (year % 400 == 0) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
    public String validarFecha(String adia){
        String bandera = "N";
        Spinner ano  = findViewById(R.id.ano);
        Spinner mes  = findViewById(R.id.mes);
        Spinner dia  = findViewById(R.id.dia);
        TextView Formapago = findViewById(R.id.Formapago);



        Time time = new Time();
        time.setToNow();

        int anoactual = time.year;
        int mesactual = time.month+1;
        int diaactual = time.monthDay;

        int anosel = Integer.parseInt(ano.getSelectedItem().toString());
        int mesel = Integer.parseInt(mes.getSelectedItem().toString());
        int diael = Integer.parseInt(dia.getSelectedItem().toString());



        // Crear objetos LocalDate para las dos fechas
        LocalDate fechaSel = LocalDate.of(anosel, mesel, diael);
        LocalDate fechaActual = LocalDate.of(anoactual, mesactual, diaactual);

        // Comparar las fechas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if(adia.equalsIgnoreCase("S")) {
                if (fechaSel.isBefore(fechaActual) || fechaSel.isEqual(fechaActual)) {
                    bandera = "S";
                }
            }else{
                if (fechaSel.isAfter(fechaActual)) {
                    bandera = "S";
                }
            }
        }

        return bandera;
    }

    private void llenarFecha(String adia) {
        Spinner ano  = findViewById(R.id.ano);
        Spinner mes  = findViewById(R.id.mes);
        Spinner dia  = findViewById(R.id.dia);
        Time time = new Time();
        time.setToNow();
        int anoactual = time.year;
        int mesactual = time.month+1;
        int diaactual = time.monthDay;
        TextView Formapago = findViewById(R.id.Formapago);



        if(adia.equalsIgnoreCase("N")){
            diaactual += 1;
        }

        int anoini = 0;
        int anofinal=0;
        final Integer[] anos ;
        if(Formapago.getText().toString().equalsIgnoreCase("Consignación")){
            anoini = anoactual-10;
            anofinal = anoactual+10;
            anos = new Integer[21];
        }else{
            anos = new Integer[11];
            if(adia.equalsIgnoreCase("S")){
                anoini = anoactual-10;
                anofinal = anoactual;
            }else{
                anoini = anoactual;
                anofinal = anoactual+10;
            }
        }



        final Integer[] meses = new Integer[12];
        final Integer[] dias ;





        int vueltaano = 0;
        int actual = 0;


         for(int i=anoini;i <= anofinal ; i = i + 1){
             if(i==anoactual){
                 actual = vueltaano;
             }
             anos[vueltaano] = i;
             vueltaano = vueltaano + 1;
         }

        ano.setAdapter(new ArrayAdapter<Integer>(this, R.layout.support_simple_spinner_dropdown_item, anos)); // simple_spinner_item
        ano.setSelection(actual);
        vueltaano = 0;
         for (int i=1;i<=12;i = i + 1){
             if(i==mesactual){
                 actual = vueltaano;
             }
             meses[vueltaano] = i;
             vueltaano = vueltaano + 1;
         }
        mes.setAdapter(new ArrayAdapter<Integer>(this, R.layout.support_simple_spinner_dropdown_item, meses)); // simple_spinner_item
        mes.setSelection(actual);
        int finaldias = 0;

        if(mesactual==1 || mesactual==3 || mesactual==5 || mesactual==7 || mesactual==8 || mesactual==10 || mesactual==12){
            dias = new Integer[31];
            finaldias = 31;
        }else{
            if(mesactual == 2){
                dias = new Integer[28];
                finaldias = 28;
            }else {
                dias = new Integer[30];
                finaldias = 30;
            }
        }
        vueltaano = 0;
        for (int i=1;i<=finaldias;i = i + 1){
            if(i==diaactual){
                actual = vueltaano;
            }
            dias[vueltaano] = i;
            vueltaano = vueltaano + 1;
        }
        dia.setAdapter(new ArrayAdapter<Integer>(this, R.layout.support_simple_spinner_dropdown_item, dias)); // simple_spinner_item
        dia.setSelection(actual);
    }

  public int traertipo(String tipo){
        int tipocon = 0;
      Cursor bancos;

      final BaseDatos BaseDeDatos;
      BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
      TextView codbancod = (TextView) findViewById(R.id.codbancod);
      bancos= BaseDeDatos.getWritableDatabase().rawQuery("select TCNSEC,TCNNOM from Bancos  where BANFINCOD = "+codbancod.getText().toString()+" and TCNNOM = '"+tipo.trim()+"'  ", null);
      if (bancos.getCount()>0) {
          bancos.moveToFirst();
          do {
              tipocon =bancos.getInt(0);
          } while (bancos.moveToNext());
      }


        return tipocon;
  }

    public int banderanum(String numeroop){
        Spinner ano  = findViewById(R.id.ano);
        Spinner mes  = findViewById(R.id.mes);
        Spinner dia  = findViewById(R.id.dia);
        EditText rxvalor = findViewById(R.id.valor);
        if(rxvalor.getText().toString().isEmpty()){
            rxvalor.setText("0");
        }
        EditText ciudadcta = findViewById(R.id.ciudadcta);


        Integer anoint = Integer.parseInt(ano.getSelectedItem().toString());
        Integer mesint =  Integer.parseInt(mes.getSelectedItem().toString());
        Integer diaint =  Integer.parseInt(dia.getSelectedItem().toString());
        Double valor =  Double.valueOf(rxvalor.getText().toString());
        String Ciucod = ciudadcta.getText().toString().trim();

        int bandera = 0;

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);


     /*   Cursor numerodef;
        numerodef= BaseDeDatos.getWritableDatabase().rawQuery("" +
                " ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR

        bandera += numerodef.getCount();*/


        Cursor numeroog;
        numeroog= BaseDeDatos.getWritableDatabase().rawQuery("select * from ReciboFormaFotos f left join Reciboforma r on f.facnro = r.facnro   where  (NroOp = '"+numeroop+"' or NroCheque =  '"+numeroop+"' ) and Valor = "+valor+"  and  " +
                " rcyear = "+anoint+"  and rcmonth = "+mesint+"   and rcday = "+diaint+" and Ciudad = '"+Ciucod+"'  ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR

        bandera += numeroog.getCount();


        Cursor numeromantis;
        numeromantis= BaseDeDatos.getWritableDatabase().rawQuery("select * from NumeroProvisional where  (ReForPagFotosDesc = '"+numeroop+"' or  ReForPagFotosDesc = '"+numeroop+"') and " +
                " RecPagVal = "+valor+" and RecFecyear = "+anoint+"  and RecFecmonth = "+mesint+"   and RecFecdia = "+diaint+" and RecPagCiucod = '"+Ciucod+"'     ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR

        bandera += numeromantis.getCount();

        return bandera;
    }


    public int banderaconsig(){

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
        int bandera = 0;
        Cursor numerodef;
        numerodef= BaseDeDatos.getWritableDatabase().rawQuery("select NroOp from fototemp  ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR
        if(numerodef.getCount() > 0 ){



        Spinner ano  = findViewById(R.id.ano);
        Spinner mes  = findViewById(R.id.mes);
        Spinner dia  = findViewById(R.id.dia);
        EditText rxvalor = findViewById(R.id.valor);


        Integer anoint = Integer.parseInt(ano.getSelectedItem().toString());
        Integer mesint =  Integer.parseInt(mes.getSelectedItem().toString());
        Integer diaint =  Integer.parseInt(dia.getSelectedItem().toString());
        Double valor =  Double.valueOf(rxvalor.getText().toString());

     //  Log.e("Pucsec,",pucsec);

        String consulta = " select  count(*) as total from comprobantedetalle cd left join comprobante c " +
                " on cd.comsec = c.comsec left join tipos t on c.tipcod = t.tipcod " +
                " where ComEstado = 'A' " +
                " and ComPucSec = '"+pucsec+"' " +
                " and MovDeb+MovCre = "+valor+" " +
                " and TipConDcheck  = 'S' " +
                " and year(ComFecCon) = "+anoint+" " +
                " and month(ComFecCon) = "+mesint+" " +
                " and day(ComFecCon) = "+diaint+" ";
         //   Log.e("consultwwwwwwwwwa",consulta);
        try{
            ConBd conbd = new ConBd();
            Connection conn = conbd.CargarConexion(getApplicationContext());
            if(conn != null){
                Statement comm = conn.createStatement();
                ResultSet rsImport = comm.executeQuery(consulta);

                while (rsImport.next()){
                    bandera +=  rsImport.getInt("total");
                }
            }

        } catch (SQLException e) {
            Log.e("error",e.toString());
            throw new RuntimeException(e);

        }

        }
        return bandera;
    }









    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
                verfotos.setText("Tomar Fotos (" + data.getIntExtra("Totalfotos", 0) + ")");
                valfotos = data.getIntExtra("Totalfotos", 0);
                valorfotos = data.getDoubleExtra("TotalValor", 0);

                if (valfotos > 0){
                    EditText valor = findViewById(R.id.valor);
                valor.setText(String.valueOf(valorfotos.intValue()));
            }

            }
        }else{
            if (requestCode == 2) {
             //   Log.e("entro onresult: ",String.valueOf( Activity.RESULT_OK));
                if (resultCode == Activity.RESULT_OK) {

                    TextView ciudad = (TextView) findViewById(R.id.ciudad);
                    TextView ciudadcta = (TextView) findViewById(R.id.ciudadcta);
                    ciudad.setText(data.getStringExtra("CIUDAD"));
                    ciudadcta.setText(data.getStringExtra("CODCIUDAD"));


                }
            }
            else{
                if (resultCode == Activity.RESULT_OK) {
                TextView NomBancod = (TextView) findViewById(R.id.NomBancod);
                TextView codbancod = (TextView) findViewById(R.id.codbancod);

                NomBancod.setText(data.getStringExtra("NOMBANCO"));
                codbancod.setText(data.getStringExtra("CODBANCO"));
                checkval = data.getStringExtra("checkval");



                llenarpuc( data.getStringExtra("CODBANCO"));

                }



            }
        }

    }

public void llenarpuc(String CodBanco)
{
    final BaseDatos BaseDeDatos;
    BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);

    Cursor bancos;
    bancos= BaseDeDatos.getWritableDatabase().rawQuery("select TCNSEC,TCNNOM from Bancos  where BANFINCOD = "+CodBanco+"  ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR
    final String[] values= new String[bancos.getCount()];
    final String[] valuesid= new String[bancos.getCount()];

    if (bancos.getCount()>0) {
        bancos.moveToFirst();
        int vuelta=bancos.getCount();
        vuelta=0;
        do {
            values[vuelta]=bancos.getString(1);
            valuesid[vuelta]=bancos.getString(0);
            vuelta=vuelta+1;
        } while (bancos.moveToNext());
    }

    Spinner tipoconsigna  = findViewById(R.id.tipoconsigna);
    tipoconsigna.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, values)); // simple_spinner_item
    tipoconsigna.setSelection(0);


    Cursor bancoscuenta;
    bancoscuenta= BaseDeDatos.getWritableDatabase().rawQuery("select BanFinPucSec,PucNom from BancoCuenta  where BANFINCOD = "+CodBanco+"  ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR
    final String[] valuescuenta= new String[bancoscuenta.getCount()];
    valuescuentaid= new String[bancoscuenta.getCount()];

    if (bancoscuenta.getCount()>0) {
        bancoscuenta.moveToFirst();
        int vuelta=bancoscuenta.getCount();
        vuelta=0;
        do {
            valuescuenta[vuelta]=bancoscuenta.getString(1);
            valuescuentaid[vuelta]=bancoscuenta.getString(0);
            vuelta=vuelta+1;
        } while (bancoscuenta.moveToNext());
    }


    Spinner spcuenta  = findViewById(R.id.spcuenta);
    spcuenta.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, valuescuenta)); // simple_spinner_item
    spcuenta.setSelection(0);
}
}