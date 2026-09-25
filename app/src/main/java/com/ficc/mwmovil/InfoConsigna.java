package com.ficc.mwmovil;

import static android.R.layout.select_dialog_item;
import static android.R.layout.simple_spinner_dropdown_item;
import static android.R.layout.simple_spinner_item;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ficc.mwmovil.databinding.ActivityInfoConsignaBinding;

public class InfoConsigna extends AppCompatActivity {
    Bundle Extras;
    String ConNro ;
    Double totalsel;
    BaseDatos BaseDeDatos;
    String checkval;
    int valfotos;
    String[] valuescuentaid;
    TextView titulotipo,tituloprove;
    Switch Tipocons,Proveedor;
    int[] valuesid,nNitProBancod;
    LinearLayout contproveedor;
    String[] nNitSec,nNitCom;
    String[] BanNominf;
    Button btnFotos;
    TextView valorfoto;
    Double totalFot =0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_consigna);
        getSupportActionBar().hide();
        Time time = new Time();
        time.setToNow();
        Extras=this.getIntent().getExtras();
        ConNro = Extras.getString("ConNro");
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);
        totalsel= Extras.getDouble("totalsel",0.0);
        Button btnCiudad= (Button) findViewById(R.id.btnCiudad);
        Button btnbancos = (Button) findViewById(R.id.btnbancos);
        Button limpiarinfo = (Button) findViewById(R.id.limpiarinfo);
        Button btnguardarcon = (Button) findViewById(R.id.btnguardarcon);
        btnFotos = (Button) findViewById(R.id.btnFotos);
        titulotipo =  findViewById(R.id.titulotipo);
        tituloprove =  findViewById(R.id.tituloprove);
        contproveedor =  findViewById(R.id.contproveedor);
        valorfoto =  findViewById(R.id.valorfoto);
        Tipocons =  findViewById(R.id.Tipocons);
        Proveedor=  findViewById(R.id.Proveedor);
        TextView NomBancod = (TextView) findViewById(R.id.NomBancod);
        TextView codbancod = (TextView) findViewById(R.id.codbancod);
        Spinner tipoconsigna  = findViewById(R.id.tipoconsigna);
        Spinner spcuenta  = findViewById(R.id.spcuenta);
        Spinner spproveedor  = findViewById(R.id.spproveedor);

        contproveedor.setVisibility(View.GONE);
        TextView txtConNro = findViewById(R.id.ConNro);
        TextView valorcon = findViewById(R.id.valorcon);
        TextView ciudad = (TextView) findViewById(R.id.ciudad);
        TextView ciudadcta = (TextView) findViewById(R.id.ciudadcta);
        valorcon.setText(String.format("%,d",totalsel.intValue()));
        txtConNro.setText(ConNro);

        titulotipo.setTextColor(Color.parseColor("#8A8A8A"));
        tituloprove.setTextColor(Color.parseColor("#8A8A8A"));
        String consulta = "select ifnull(CodBanco,0) CodBanco,ifnull(TCNSEC,0) TCNSEC,ifnull(Ciudad,'') Ciudad,ifnull(PucSec,'') PucSec,ifnull(checktesoreria,'N') checktesoreria,ifnull(checkproveedor,'N') checkproveedor ,NitSec from ConsignaRecibo where ConNro = '"+ConNro+"' ";
        String obs ="";
        Cursor consigna = BaseDeDatos.getWritableDatabase().rawQuery(consulta,null);

         Cursor cuproveedor =  BaseDeDatos.getWritableDatabase().rawQuery("select NitSec,Nitcom from Proveedores",null);

        cuproveedor.moveToFirst();
        if(cuproveedor.getCount() > 0){
            cuproveedor.moveToFirst();
            nNitSec = new String[cuproveedor.getCount()];
            nNitCom = new String[cuproveedor.getCount()];
            int vueltas = 0;
            do{
                nNitSec[vueltas] = cuproveedor.getString(0);
                nNitCom[vueltas] = cuproveedor.getString(1);
                vueltas+=1;
            }while (cuproveedor.moveToNext());

            spproveedor.setAdapter(new ArrayAdapter<String>(this, simple_spinner_dropdown_item, nNitCom));
        }


        if(consigna.getCount() > 0){
            consigna.moveToFirst();
            do{

                String banco =consigna.getString(0);
                if(banco.isEmpty() || banco == null){
                    banco = "0";
                }
                Cursor fotos= BaseDeDatos.getWritableDatabase().rawQuery("select  ConNro,Valorfoto from ConsignaRecibofoto  where ConNro = '"+ConNro+"'  ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR
                btnFotos.setText("Subir fotos ("+fotos.getCount()+")");
                   if(fotos.getCount()> 0){
                       fotos.moveToFirst();
                       totalFot = 0.0;
                       do{
                           totalFot+= fotos.getDouble(1);
                       }while(fotos.moveToNext());
                   }
                valorfoto.setText(String.format("%,d",totalFot.intValue()));


                for(int i = 0; i<nNitSec.length;i++){
                    if(nNitSec[i].equalsIgnoreCase(consigna.getString(6))){
                        spproveedor.setSelection(i);
                    }
                }

                if(consigna.getString(4).equalsIgnoreCase("S")){
                    Tipocons.setChecked(true);
                    titulotipo.setTextColor(Color.parseColor("#FFE91E63"));
                    tituloprove.setTextColor(Color.parseColor("#8A8A8A"));
                    Proveedor.setChecked(false);
                    contproveedor.setVisibility(View.GONE);
                    NomBancod.setText("");
                    codbancod.setText("");
                    ciudad.setText("");
                    ciudadcta.setText("");

                    tipoconsigna.setAdapter(null);
                    spcuenta.setAdapter(null);
                    spcuenta.setEnabled(false);
                    tipoconsigna.setEnabled(false);
                    btnbancos.setEnabled(false);
                    btnbancos.setBackgroundColor(Color.parseColor("#8A8A8A"));
                    btnCiudad.setBackgroundColor(Color.parseColor("#8A8A8A"));
                    btnCiudad.setEnabled(false);
                }else  if(consigna.getString(5).equalsIgnoreCase("S")) {

                    Proveedor.setChecked(true);
                    tituloprove.setTextColor(Color.parseColor("#0A9510"));
                    titulotipo.setTextColor(Color.parseColor("#8A8A8A"));
                    Tipocons.setChecked(false);
                    contproveedor.setVisibility(View.GONE);
                    NomBancod.setText("");
                    codbancod.setText("");
                    ciudad.setText("");
                    ciudadcta.setText("");
                    tipoconsigna.setAdapter(null);
                    spcuenta.setAdapter(null);
                    spcuenta.setEnabled(false);
                    tipoconsigna.setEnabled(false);
                    btnbancos.setEnabled(false);
                    btnbancos.setBackgroundColor(Color.parseColor("#8A8A8A"));
                    btnCiudad.setBackgroundColor(Color.parseColor("#8A8A8A"));
                    btnCiudad.setEnabled(false);


                }else{
                    traerdatos(consigna.getString(0),consigna.getInt(1),consigna.getString(2),consigna.getString(3));
                }
            }while (consigna.moveToNext());
        }

Tipocons.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    titulotipo.setTextColor(Color.parseColor("#FFE91E63"));
                    tituloprove.setTextColor(Color.parseColor("#8A8A8A"));
                    Proveedor.setChecked(false);
                    contproveedor.setVisibility(View.GONE);
                    NomBancod.setText("");
                    codbancod.setText("");
                    ciudad.setText("");
                    ciudadcta.setText("");
                    tipoconsigna.setAdapter(null);
                    spcuenta.setAdapter(null);
                    spcuenta.setEnabled(false);
                    tipoconsigna.setEnabled(false);
                    btnbancos.setEnabled(false);
                    btnbancos.setBackgroundColor(Color.parseColor("#8A8A8A"));
                    btnCiudad.setBackgroundColor(Color.parseColor("#8A8A8A"));
                    btnCiudad.setEnabled(false);
                }else{

                    btnbancos.setBackgroundColor(Color.parseColor("#1267AA"));
                    btnCiudad.setBackgroundColor(Color.parseColor("#1267AA"));
                    titulotipo.setTextColor(Color.parseColor("#8A8A8A"));
                    spcuenta.setEnabled(true);
                    tipoconsigna.setEnabled(true);
                    btnbancos.setEnabled(true);
                    btnCiudad.setEnabled(true);
                }

            }
        });


        Proveedor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tituloprove.setTextColor(Color.parseColor("#0A9510"));
                    titulotipo.setTextColor(Color.parseColor("#8A8A8A"));
                    contproveedor.setVisibility(View.GONE);
                    Tipocons.setChecked(false);
                    NomBancod.setText("");
                    codbancod.setText("");
                    ciudad.setText("");
                    ciudadcta.setText("");
                    tipoconsigna.setAdapter(null);
                    spcuenta.setAdapter(null);
                    spcuenta.setEnabled(false);
                    tipoconsigna.setEnabled(false);
                    btnbancos.setEnabled(false);
                    btnbancos.setBackgroundColor(Color.parseColor("#8A8A8A"));
                    btnCiudad.setBackgroundColor(Color.parseColor("#8A8A8A"));
                    btnCiudad.setEnabled(false);
                }else{

                    btnbancos.setBackgroundColor(Color.parseColor("#1267AA"));
                    btnCiudad.setBackgroundColor(Color.parseColor("#1267AA"));
                    tituloprove.setTextColor(Color.parseColor("#8A8A8A"));
                    spcuenta.setEnabled(true);
                    tipoconsigna.setEnabled(true);
                    btnbancos.setEnabled(true);
                    btnCiudad.setEnabled(true);
                }

            }
        });
        btnFotos.setOnClickListener(new View.OnClickListener() {
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

              /*  if(tCodBanco.isEmpty()){
                    AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                    Alerta.setMessage("Debe Elegir el banco primero");
                    Alerta.setTitle("Notificacion");
                    Alerta.setPositiveButton("OK", null);
                    Alerta.setCancelable(true);
                    Alerta.create().show();
                }else{
*/

                EditText ciudadcta = findViewById(R.id.ciudadcta);
                    Intent intent = new Intent(getApplicationContext(), SubirFotos.class);
                   intent.putExtra("modo", "consigna");
                 String tipo ="N";

                if (Tipocons.isChecked()){
                    tipo = "T";
                }

                if (Proveedor.isChecked()){
                    tipo ="N";
                }
                    intent.putExtra("tipo", tipo);
                    intent.putExtra("ConNro", ConNro);
                intent.putExtra("RecNro", "");
                intent.putExtra("valor", totalsel);



                    startActivityForResult(intent,1);


               // }
                //}


            }
        });

        limpiarinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NomBancod.setText("");
                codbancod.setText("");
                ciudad.setText("");
                ciudadcta.setText("");
                tipoconsigna.setAdapter(null);
                spcuenta.setAdapter(null);
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

        btnCiudad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), BuscarBarrioCiudad.class);
                i.putExtra("BUSCIUBAR","CIU");
                startActivityForResult(i, 2);

            }
        });

        btnguardarcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int TCNSEC = 0;
                String pucsec = "";
                String Codbanco = "0";
                if (!Tipocons.isChecked() && !Proveedor.isChecked()) {

                    if (tipoconsigna.getSelectedItem().toString().isEmpty()) {
                        TCNSEC = 0;
                    } else {
                        TCNSEC = traertipo(tipoconsigna.getSelectedItem().toString());
                    }
                    int idsel = (int) spcuenta.getSelectedItemId();

                    if (valuescuentaid == null) {
                        llenarpuc(codbancod.getText().toString());
                    }
                     pucsec = valuescuentaid[idsel];
                    Codbanco = codbancod.getText().toString();
                }


                String tipcons = "";
                String checktip ="N";
                String checktippro ="N";
                String Nitsec = "";


               if (Tipocons.isChecked()){
                tipcons = "CTES";
                checktip = "S";
               }else{
                tipcons = "CVEN";
                checktip = "N";
               }


               if (Proveedor.isChecked()){
                    tipcons = "CPRO";
                    checktippro = "S";
                    Nitsec = nNitSec[spproveedor.getSelectedItemPosition()];
                }


                Log.e("totalsel" , String.valueOf(totalsel));
                Log.e("totalFot" , String.valueOf(totalFot));
               if(totalFot.intValue() == totalsel.intValue()){
                   String consulta = "update ConsignaRecibo set NitSec='"+Nitsec+"', TipoConsigna = '"+tipcons+"', checkproveedor='"+checktippro+"', checktesoreria='"+checktip+"',CodBanco="+Codbanco+" , TCNSEC = "+TCNSEC+" , Ciudad='"+ciudadcta.getText().toString()+"' , PucSec='"+pucsec+"' where ConNro = '"+ConNro+"' ";
                   BaseDeDatos.getWritableDatabase().execSQL(consulta);
                   //Intent intent = new Intent(getApplicationContext(), ResumenConsignacion.class);
                   //startActivity(intent);
                   finish();
               }else{
                   AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                   Alerta.setMessage("Valor de los comprobantes diferente al total de la consignación");
                   Alerta.setTitle("Notificacion");
                   Alerta.setPositiveButton("OK", null);
                   Alerta.setCancelable(true);
                   Alerta.create().show();
               }


            }
        });


    }



    private void traerdatos(String codbanco, int tcnSec, String Ciudad, String pucsec) {
        Cursor bancos,ciudades,fotos;
        Spinner tipoconsigna  = findViewById(R.id.tipoconsigna);
        Spinner spcuenta  = findViewById(R.id.spcuenta);
        TextView NomBancod = (TextView) findViewById(R.id.NomBancod);
        TextView codbancod = (TextView) findViewById(R.id.codbancod);
        TextView ciudad = (TextView) findViewById(R.id.ciudad);
        TextView ciudadcta = (TextView) findViewById(R.id.ciudadcta);
        codbancod.setText(codbanco);
        fotos= BaseDeDatos.getWritableDatabase().rawQuery("select  ConNro from ConsignaRecibofoto  where ConNro = '"+ConNro+"'  ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR
        btnFotos.setText("Subir fotos ("+fotos.getCount()+")");

        bancos= BaseDeDatos.getWritableDatabase().rawQuery("select BANFINNOM from Bancos  where BANFINCOD = "+codbanco+"  ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR
            if(bancos.getCount() > 0){
                bancos.moveToFirst();
                do{
                    NomBancod.setText(bancos.getString(0));
                }while (bancos.moveToNext());
            }
            llenarpuc(codbanco);

            for(int i = 0; i<valuesid.length;i++){
                if(valuesid[i] == tcnSec ){
                    tipoconsigna.setSelection(i);
                }
            }
        for(int i = 0; i<valuescuentaid.length;i++){
            if(valuescuentaid[i].equalsIgnoreCase(pucsec)){
                spcuenta.setSelection(i);
            }
        }
        ciudades= BaseDeDatos.getWritableDatabase().rawQuery("select CiuNom from ciudades  where ciucod = '"+Ciudad+"' ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR
        if(ciudades.getCount() > 0){
            ciudades.moveToFirst();
            do{
                ciudad.setText(ciudades.getString(0));
                ciudadcta.setText(Ciudad);

            }while (ciudades.moveToNext());
        }




    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
                btnFotos.setText("Subir fotos (" + data.getIntExtra("Totalfotos", 0) + ")");
                valfotos = data.getIntExtra("Totalfotos", 0);
                totalFot = data.getDoubleExtra("TotalValor",0);
                valorfoto.setText(String.format("%,d",totalFot.intValue()));

            }else{
                Cursor fotos= BaseDeDatos.getWritableDatabase().rawQuery("select  ConNro,Valorfoto from ConsignaRecibofoto  where ConNro = '"+ConNro+"'  ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR
                btnFotos.setText("Subir fotos ("+fotos.getCount()+")");
                if(fotos.getCount()> 0){
                    fotos.moveToFirst();
                    totalFot = 0.0;
                    do{
                        totalFot+= fotos.getDouble(1);
                    }while(fotos.moveToNext());
                }
                valorfoto.setText(String.format("%,d",totalFot.intValue()));

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
                if(resultCode == Activity.RESULT_OK){
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
       valuesid = new int[bancos.getCount()];

        if (bancos.getCount()>0) {
            bancos.moveToFirst();
            int vuelta=bancos.getCount();
            vuelta=0;
            do {
                values[vuelta]=bancos.getString(1);
                valuesid[vuelta]=bancos.getInt(0);
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
}