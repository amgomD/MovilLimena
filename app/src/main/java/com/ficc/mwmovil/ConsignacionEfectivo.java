package com.ficc.mwmovil;

import static android.R.layout.simple_spinner_dropdown_item;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Interpolator;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ficc.mwmovil.databinding.ActivityConsignacionEfectivoBinding;

import java.util.Arrays;
import java.util.List;

public class ConsignacionEfectivo extends AppCompatActivity {
    Bundle Extras;
    Spinner prefijo,prefijo2,ano,mes,dia;
    LinearLayout info ;
    BaseDatos BaseDeDatos;
    Integer[] colprefijo,ncolprefijo,colano,colmes,coldia;
    Cursor efectivo;
    ListView listaefectivo;
    SDTEfecitvo[] sdtEfecitvo;
    String[] nNitSec,nNitCom;
    int[] nNitProBancod;
    String[] BanNominf;
    LinearLayout contproveedor;
    TextView tituloprove;

    Double totalsel = 0.0;
    int numsel=0;
    int seldia=0;
    int modo=0;
    int bddia =0;
    EditText observacion;
    Switch Proveedor;
    int esnuevo = 0;
    TextView valorcon;
    Button editarcons;
    String ConNro;
    Integer BanCod;
    Spinner spproveedor,sepcuentaprov;

    ListViewAdapterEfectivo listViewAdapterEfectivo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consignacion_efectivo);
        getSupportActionBar().hide();
        Time time = new Time();
        time.setToNow();
        Extras=this.getIntent().getExtras();
         ConNro = Extras.getString("ConNro");
        tituloprove =  findViewById(R.id.tituloprove);
        contproveedor =  findViewById(R.id.contproveedor);
        Proveedor=  findViewById(R.id.Proveedor);
         spproveedor  = findViewById(R.id.spproveedor);
        tituloprove.setTextColor(Color.parseColor("#8A8A8A"));
        spproveedor.setVisibility(View.GONE);

        sepcuentaprov= findViewById(R.id.sepcuentaprov);
        sepcuentaprov.setVisibility(View.GONE);
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);


         observacion = findViewById(R.id.observacion);
         valorcon =  findViewById(R.id.valorcon);
        TextView txtConNro =  findViewById(R.id.ConNro);
        Button cargarrecibo = findViewById(R.id.cargarrecibo);
        Button guardarcons = findViewById(R.id.guardarcons);
        Button nuevacon = findViewById(R.id.nuevacon);
        info = findViewById(R.id.info);
        editarcons = findViewById(R.id.editarcons);
        editarcons.setVisibility(View.GONE);
        prefijo =  findViewById(R.id.prefijo);

        ano =  findViewById(R.id.ano);
        mes =  findViewById(R.id.mes);
        dia =  findViewById(R.id.dia);

        llenarspinner();
        cargarpre();
        listaefectivo = (ListView) findViewById(R.id.ListarEfectivo);
        listaefectivo.setVisibility(View.GONE);
        txtConNro.setText("");


        //
        int selano = Integer.valueOf(ano.getSelectedItem().toString());
        int selmes = Integer.valueOf(mes.getSelectedItem().toString());

        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();
     //   ConNro = "CON"+bdPrefijo+vUsuario+time.year +"-"+ (time.month + 1) +"-"+ time.monthDay;




        Cursor cuproveedor =  BaseDeDatos.getWritableDatabase().rawQuery("select NitSec,Nitcom from Proveedores",null);

        cuproveedor.moveToFirst();
        if(cuproveedor.getCount() > 0){
            cuproveedor.moveToFirst();
            nNitSec = new String[cuproveedor.getCount()+1];
            nNitCom = new String[cuproveedor.getCount()+1];
            nNitSec[0] = "";
            nNitCom[0] = "Ninguno";
            int vueltas = 1;
            do{
                nNitSec[vueltas] = cuproveedor.getString(0);
                nNitCom[vueltas] = cuproveedor.getString(1);
                vueltas+=1;
            }while (cuproveedor.moveToNext());

            spproveedor.setAdapter(new ArrayAdapter<String>(this, simple_spinner_dropdown_item, nNitCom));
        }



        traerdatos();

        editarcons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ConsignacionEfectivo.class);
                int bdPrefijo = colprefijo[prefijo.getSelectedItemPosition()];
                i.putExtra("ConNro",ConNro = "CON"+bdPrefijo+vUsuario+time.year +"-"+ (time.month + 1) +"-"+ time.monthDay);
                startActivity(i);
                overridePendingTransition(0, 0);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
            }
        });

      /*  prefijo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int prefij[] ;
                int vulpre = 0;
                int selpre = colprefijo[i];

                String nConNro =  "CON"+selpre+vUsuario+time.year +"-"+ (time.month + 1) +"-"+ time.monthDay;
                Log.e("nConNro",nConNro);
                Cursor pre = BaseDeDatos.getWritableDatabase().rawQuery("Select ConNro from  ConsignaRecibo where ConNro ='"+nConNro+"' ",null);
                pre.moveToFirst();
                //Log.e("pre.getCount() ",String.valueOf(pre.getString(0) ));



                cargarrecibo.setVisibility(View.VISIBLE);
                info.setVisibility(View.VISIBLE);
                guardarcons.setVisibility(View.GONE);
                editarcons.setVisibility(View.GONE);
                if(modo != 1){
                    if(pre.getCount() > 0 ) {
                        cargarrecibo.setVisibility(View.GONE);
                        info.setVisibility(View.GONE);
                        guardarcons.setVisibility(View.GONE);
                        editarcons.setVisibility(View.VISIBLE);
                    }
                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
*/
        Proveedor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tituloprove.setTextColor(Color.parseColor("#0A9510"));
                    contproveedor.setVisibility(View.VISIBLE);
                    spproveedor.setVisibility(View.VISIBLE);
                    sepcuentaprov.setVisibility(View.VISIBLE);

                }else{
                    tituloprove.setTextColor(Color.parseColor("#8A8A8A"));
                    spproveedor.setVisibility(View.GONE);
                    sepcuentaprov.setVisibility(View.GONE);

                }
                listaefectivo.setVisibility(View.GONE);
                guardarcons.setText("Confirmar ("+String.valueOf(numsel).trim()+")");
                cargarrecibo.setVisibility(View.VISIBLE);
                guardarcons.setVisibility(View.GONE);


            }
        });
        spproveedor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                listaefectivo.setVisibility(View.GONE);
                guardarcons.setText("Confirmar ("+String.valueOf(numsel).trim()+")");
                cargarrecibo.setVisibility(View.VISIBLE);
                guardarcons.setVisibility(View.GONE);
                String provNitSec = nNitSec[i];


                Cursor cuproveedor =  BaseDeDatos.getWritableDatabase().rawQuery("select NitProBanCod,BanNom,NitProBanInf from BancoProv where NitSec = '"+provNitSec+"' ",null);

                cuproveedor.moveToFirst();
                if(cuproveedor.getCount() > 0){
                    cuproveedor.moveToFirst();
                    BanNominf = new String[cuproveedor.getCount()];
                    nNitProBancod = new int[cuproveedor.getCount()];
                    int vueltas = 0;
                    do{
                        nNitProBancod[vueltas] = cuproveedor.getInt(0);
                        BanNominf[vueltas] = cuproveedor.getString(1)+" - "+cuproveedor.getString(2);
                        vueltas+=1;
                    }while (cuproveedor.moveToNext());
                    sepcuentaprov.setAdapter(new ArrayAdapter<String>(ConsignacionEfectivo.this, simple_spinner_dropdown_item, BanNominf));

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });






        cargarrecibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 int cargar = 0;
                if(Proveedor.isChecked()){
                    if(spproveedor.getSelectedItem().toString().equalsIgnoreCase("Ninguno")){
                        AlertDialog.Builder Alerta = new AlertDialog.Builder(ConsignacionEfectivo.this);
                        Alerta.setMessage("Debe seleccionar un proveedor");
                        Alerta.setTitle("Notificacion");
                        Alerta.setPositiveButton("OK",null);
                        Alerta.setCancelable(true);
                        Alerta.create().show();
                    }else{
                        cargar = 1;
                    }
                }else{
                    cargar = 1;
                }







                if(cargar == 1){
                    int bdPrefijo = 1;
                    if(esnuevo == 0){
                         bdPrefijo = ncolprefijo[prefijo.getSelectedItemPosition()];
                    }else{
                         bdPrefijo = colprefijo[prefijo.getSelectedItemPosition()];
                    }

                    int selano = Integer.valueOf(ano.getSelectedItem().toString());
                    int selmes = Integer.valueOf(mes.getSelectedItem().toString());
                    int seldia = Integer.valueOf(dia.getSelectedItem().toString());
                    GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                    String vUsuario=vGlobalVariables.getUsuario();
                    //
                    if(ConNro.isEmpty()){
                        ConNro = "CON"+bdPrefijo+vUsuario+time.year +"-"+ (time.month + 1) +"-"+ time.monthDay;
                    }
                    String consulta = "select Obs,Valor,rconyear,rconmonth,rconday from ConsignaRecibo where ConNro = '"+ConNro+"'  ";
                    String obs ="";
                    Cursor consigna = BaseDeDatos.getWritableDatabase().rawQuery(consulta,null);

                    if(consigna.getCount() > 0){
                        consigna.moveToFirst();
                        do{
                            observacion.setText(consigna.getString(0));
                            totalsel = consigna.getDouble(1);
                            valorcon.setText(String.format("%,d",totalsel.intValue()));

                            for(int i = 0;i<colano.length;i++){
                                if(consigna.getInt(2) == colano[i]) {
                                    ano.setSelection(i);
                                }
                            }
                            for(int i = 0;i<colmes.length;i++){
                                if(consigna.getInt(3) == colmes[i]) {
                                    mes.setSelection(i);
                                }
                            }



                        }while (consigna.moveToNext());




                    }
                    else{
                        bdPrefijo = ncolprefijo[prefijo.getSelectedItemPosition()];
                        ConNro = "CON"+bdPrefijo+vUsuario+time.year +"-"+ (time.month + 1) +"-"+ time.monthDay;
                        if(observacion.getText().toString().isEmpty()){
                            obs = "";
                        }else{
                            obs =  observacion.getText().toString();
                        }
                        //Agregar la funcion de insertar el encabezado
                        consulta = "Insert into ConsignaRecibo (prefijo,VenCod,RecNro,ConNro,Obs,Enviado,Valor,conyear,conmonth,conday,rconyear,rconmonth,rconday ) values ( " +
                                " "+bdPrefijo+",'"+vUsuario+"','','"+ConNro+"', '"+obs+"'  ,'N',"+totalsel+","+ time.year+","+(time.month + 1)+","+time.monthDay+" ,"+ selano+","+selmes+","+seldia+" )";
                        BaseDeDatos.getWritableDatabase().execSQL(consulta);
                    }

                    //ano.setEnabled(false);
                    //mes.setEnabled(false);
                    //dia.setEnabled(false);
                    prefijo.setEnabled(false);
                    txtConNro.setText(ConNro);
                    listaefectivo.setVisibility(View.VISIBLE);



                    try {
                        if(Proveedor.isChecked()){
                            efectivo= BaseDeDatos.getWritableDatabase().rawQuery(" select rf.RecNro,rf.nitsec,rf.clisec,Clinom,sum(Valor),rf.Tipo,ifnull(ConNro,'') ConNro,ifnull((select Enviado from recibo r where r.RecNro=rf.RecNro),'N') enviado,rf.Tipo,ifnull(rf.aldia,'N') aldia,ifnull(rf.postfecha,'N')postfecha   from Reciboforma rf  " +
                                    "left join clientes c on rf.nitsec=c.nitsec and rf.clisec= c.clisec " +
                                    // "left join Reciboforma rf on r.RecNro = rf.RecNro " +
                                    "where recicyear = "+time.year+" and recicmonth = "+(time.month + 1)+" and recicday = "+time.monthDay+" and rf.Tipo = 'Efectivo'  and (ConNro = '' or ConNro is null or ConNro ='"+ConNro+"') and enviado = 'S' group by rf.Tipo,rf.aldia, rf.RecNro ", null);

                        }else{
                            efectivo= BaseDeDatos.getWritableDatabase().rawQuery(" select rf.RecNro,rf.nitsec,rf.clisec,Clinom,sum(Valor),rf.Tipo,ifnull(ConNro,'') ConNro,ifnull((select Enviado from recibo r where r.RecNro=rf.RecNro),'N') enviado,rf.Tipo,ifnull(rf.aldia,'N') aldia,ifnull(rf.postfecha,'N')postfecha   from Reciboforma rf  " +
                                    "left join clientes c on rf.nitsec=c.nitsec and rf.clisec= c.clisec " +
                                    // "left join Reciboforma rf on r.RecNro = rf.RecNro " +
                                    "where recicyear = "+time.year+" and recicmonth = "+(time.month + 1)+" and recicday = "+time.monthDay+" and (rf.Tipo = 'Efectivo' or rf.Tipo = 'Cheque') and (ConNro = '' or ConNro is null or ConNro ='"+ConNro+"') and enviado = 'S' group by rf.Tipo,rf.aldia, rf.RecNro ", null);
                        }


                        sdtEfecitvo=new SDTEfecitvo[efectivo.getCount()];
                        Integer vuelta=0;
                        if (efectivo.getCount()>0) {
                            numsel=0;
                            totalsel = 0.0;
                            efectivo.moveToFirst();
                            do {
                                SDTEfecitvo    sdtEfecitvoItem= new SDTEfecitvo();
                                sdtEfecitvoItem.RecNro = efectivo.getString(0);
                                sdtEfecitvoItem.Clinom = efectivo.getString(3);
                                sdtEfecitvoItem.Valor = efectivo.getDouble(4);
                                sdtEfecitvoItem.tipo = efectivo.getString(8);
                                sdtEfecitvoItem.aldia = efectivo.getString(9);
                                sdtEfecitvoItem.postfecha = efectivo.getString(10);

                                if(efectivo.getString(6).equalsIgnoreCase(ConNro)){
                                    sdtEfecitvoItem.check = true;
                                    totalsel+= efectivo.getDouble(4);
                                    numsel+=1;
                                }


                                sdtEfecitvo[vuelta]=sdtEfecitvoItem;
                                vuelta+=1;

                            } while (efectivo.moveToNext());

                            listViewAdapterEfectivo = new ListViewAdapterEfectivo(ConsignacionEfectivo.this, sdtEfecitvo);
                            listaefectivo.setAdapter(listViewAdapterEfectivo);
                            if(numsel > 0){
                                valorcon.setText(String.format("%,d",totalsel.intValue()));
                                guardarcons.setText("Confirmar ("+String.valueOf(numsel).trim()+")");
                                guardarcons.setVisibility(View.VISIBLE);
                                cargarrecibo.setVisibility(View.GONE);
                            }
                        }
                    }catch (Exception e)
                    {
                        Log.e("ErrorLisforma",e.toString());
                    }
                }

            }
        });
        guardarcons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selano = Integer.valueOf(ano.getSelectedItem().toString());
                int selmes = Integer.valueOf(mes.getSelectedItem().toString());
                int nseldia = Integer.valueOf(dia.getSelectedItem().toString());

                String consulta = "";



                for(int i = 0;i<sdtEfecitvo.length;i++) {


                    if (sdtEfecitvo[i].check) {


                        consulta = "update Reciboforma set ConNro = '" + ConNro + "' where RecNro = '" + sdtEfecitvo[i].RecNro + "' and Tipo ='" + sdtEfecitvo[i].tipo + "' and (aldia = '"+sdtEfecitvo[i].aldia+"' or aldia is null  ) ";
                        BaseDeDatos.getWritableDatabase().execSQL(consulta);

                    }else{
                        consulta = "update Reciboforma set ConNro = '' where RecNro = '" + sdtEfecitvo[i].RecNro + "' and Tipo ='" + sdtEfecitvo[i].tipo + "' and (aldia = '"+sdtEfecitvo[i].aldia+"' or aldia is null  ) ";
                        BaseDeDatos.getWritableDatabase().execSQL(consulta);
                    }
                }


                try{
                    if(Proveedor.isChecked()){
                        consulta = "update Reciboforma set ConNro = '' where ConNro = '"+ConNro+"' and  Tipo = 'Cheque' ";
                        BaseDeDatos.getWritableDatabase().execSQL(consulta);
                    }


                    String tipcons = "";
                    String checktippro ="N";
                    String Nitsec = "";
                    int bancoProv = 0;
                    if (Proveedor.isChecked()){
                        tipcons = "CPRO";
                        checktippro = "S";
                        Nitsec = nNitSec[spproveedor.getSelectedItemPosition()];
                        bancoProv =nNitProBancod[sepcuentaprov.getSelectedItemPosition()];

                    }

                    consulta = "update ConsignaRecibo set   bancoProv ="+bancoProv+",  NitSec='"+Nitsec+"', TipoConsigna = '"+tipcons+"', checkproveedor='"+checktippro+"',Valor="+totalsel+" , rconyear = "+selano+" , rconmonth="+selmes+" , rconday="+nseldia+" where ConNro = '"+ConNro+"' ";
                    BaseDeDatos.getWritableDatabase().execSQL(consulta);
                }catch (Exception e){
                    Log.e("anosele",e.toString());
                }


                Intent intent = new Intent(getApplicationContext(), InfoConsigna.class);
                intent.putExtra("ConNro",ConNro);
                intent.putExtra("totalsel",totalsel);
                //intent.putExtra("porRetica", SDTAbono[i].PorRetIca);

                startActivity(intent);
                finish();
            }
        });
        nuevacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overridePendingTransition(0, 0);
                getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
            }
        });
        mes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Time time = new Time();
                time.setToNow();
                int anosel = Integer.parseInt(ano.getSelectedItem().toString());
                int mesactual = Integer.parseInt(mes.getSelectedItem().toString());
                int diael = Integer.parseInt(dia.getSelectedItem().toString());
                int diaactual = time.monthDay;

                final Integer[] dias ;
                int finaldias = 0;

                if(mesactual==1 || mesactual==3 || mesactual==5 || mesactual==7 || mesactual==8 || mesactual==10 || mesactual==12){
                    coldia = new Integer[31];
                    finaldias = 31;
                }else{
                    if(mesactual == 2){
                        if(bisiesto(anosel)){
                            //    Log.e("Es bisiesto",String.valueOf(anosel));
                            coldia = new Integer[29];
                            finaldias = 29;
                        }else{
                            coldia = new Integer[28];
                            finaldias = 28;
                        }

                    }else {
                        coldia = new Integer[30];
                        finaldias = 30;
                    }
                }

                int vueltaano = 0;
                int actual = 0;
                for (int j=1;j<=finaldias;j= j + 1){
                    if(j==diaactual){
                        actual = vueltaano;
                    }
                    coldia[vueltaano] = j;
                    vueltaano = vueltaano + 1;
                }


                dia.setAdapter(new ArrayAdapter<Integer>(ConsignacionEfectivo.this, R.layout.support_simple_spinner_dropdown_item, coldia)); // simple_spinner_item
                String consulta = "select rconday from ConsignaRecibo where ConNro = '"+ConNro+"' ";
                Cursor consigna = BaseDeDatos.getWritableDatabase().rawQuery(consulta,null);

                if(consigna.getCount() > 0) {
                    consigna.moveToFirst();
                    do {
                        for(int j = 0;j<coldia.length;j++){
                            if(consigna.getInt(0) == coldia[j]) {
                                dia.setSelection(j);
                                seldia = j;
                            }
                        }
                    } while (consigna.moveToNext());
                }



            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        listaefectivo.setAdapter(listViewAdapterEfectivo);
        listaefectivo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(sdtEfecitvo[i].check) {
                    totalsel -=   sdtEfecitvo[i].Valor;
                    numsel -= 1;
                    sdtEfecitvo[i].check = false;
                }else{
                    totalsel +=   sdtEfecitvo[i].Valor;
                    numsel += 1;
                    sdtEfecitvo[i].check = true;
                }
               if(totalsel > 0){
                   guardarcons.setText("Confirmar ("+String.valueOf(numsel).trim()+")");
                   guardarcons.setVisibility(View.VISIBLE);
                   cargarrecibo.setVisibility(View.GONE);
               }else{
                   guardarcons.setText("Confirmar ("+String.valueOf(numsel).trim()+")");
                   cargarrecibo.setVisibility(View.VISIBLE);
                   guardarcons.setVisibility(View.GONE);
               }

              if(totalsel < 0){
                  totalsel = 0.0;
              }
                valorcon.setText(String.format("%,d",totalsel.intValue()));
                listViewAdapterEfectivo.notifyDataSetChanged();
            }
        });


        observacion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(sdtEfecitvo != null){
                    String consulta = "update ConsignaRecibo set Obs = '"+editable.toString().trim()+"' where ConNro = '"+ConNro+"' ";
                    BaseDeDatos.getWritableDatabase().execSQL(consulta);
                }else{
                    Log.e("LENGHT","TA VACIA");
                }

            }
        });



    }



    private void traerdatos() {
        String consulta = "select Obs,Valor,rconyear,rconmonth,rconday,prefijo,ConNro,NitSec,ifnull(checkproveedor,'N') checkproveedor , ifnull(bancoProv,0) bancoProv  from ConsignaRecibo where ConNro = '"+ConNro+"'  ";
        String obs ="";
        Cursor consigna = BaseDeDatos.getWritableDatabase().rawQuery(consulta,null);

        if(consigna.getCount() > 0) {
            consigna.moveToFirst();
            do {
                prefijo.setEnabled(false);
                modo = 1;
                esnuevo = 1;
                prefijo.setAdapter(new ArrayAdapter<Integer>(this, R.layout.support_simple_spinner_dropdown_item, colprefijo)); // simple_spinner_item
                //prefijo.setSelection();

                observacion.setText(consigna.getString(0));
                totalsel = consigna.getDouble(1);
                valorcon.setText(String.format("%,d", totalsel.intValue()));
                ConNro = consigna.getString(6);
                BanCod = consigna.getInt(9);
                for (int i = 0; i < colano.length; i++) {
                    if (consigna.getInt(2) == colano[i]) {
                        ano.setSelection(i);
                    }
                }
                for (int i = 0; i < colmes.length; i++) {
                    if (consigna.getInt(3) == colmes[i]) {
                        mes.setSelection(i);
                    }
                }
                for(int i = 0; i<nNitSec.length;i++){
                    if(nNitSec[i].equalsIgnoreCase(consigna.getString(7))){
                        spproveedor.setSelection(i);
                    }
                }

                for(int i = 0; i<colprefijo.length;i++){
                    if(colprefijo[i] == (consigna.getInt(5))){
                        prefijo.setSelection(i);
                    }
                }


                Cursor cuproveedor =  BaseDeDatos.getWritableDatabase().rawQuery("select NitProBanCod,BanNom,NitProBanInf from BancoProv where NitSec = '"+consigna.getString(7)+"' ",null);

                cuproveedor.moveToFirst();
                if(cuproveedor.getCount() > 0){
                    cuproveedor.moveToFirst();
                    BanNominf = new String[cuproveedor.getCount()];
                    nNitProBancod = new int[cuproveedor.getCount()];
                    int vueltas = 0;
                    do{
                        nNitProBancod[vueltas] = cuproveedor.getInt(0);
                        BanNominf[vueltas] = cuproveedor.getString(1)+" - "+cuproveedor.getString(2);
                        vueltas+=1;
                    }while (cuproveedor.moveToNext());
                    sepcuentaprov.setAdapter(new ArrayAdapter<String>(ConsignacionEfectivo.this, simple_spinner_dropdown_item, BanNominf));

                }


             if(nNitProBancod != null){
                 for(int i = 0; i < nNitProBancod.length ;i++){
                     if(nNitProBancod[i] == consigna.getInt(9) ){
                         sepcuentaprov.setSelection(i);
                     }
                 }
             }





     if(consigna.getString(8).equalsIgnoreCase("S")){
         Proveedor.setChecked(true);
         tituloprove.setTextColor(Color.parseColor("#0A9510"));
         contproveedor.setVisibility(View.VISIBLE);
         spproveedor.setVisibility(View.VISIBLE);
         sepcuentaprov.setVisibility(View.VISIBLE);
     }


            } while (consigna.moveToNext());
        }
    }

    private void llenarspinner() {
        Time time = new Time();
        time.setToNow();
        int anoactual = time.year;
        int mesactual = time.month+1;
        int diaactual = time.monthDay;

        int anoini = anoactual-10;
        int anofinal = anoactual+10;
        colano = new Integer[21];
        colmes = new Integer[12];
        colprefijo = new Integer[17];
        int actual = 0;
        int vueltaano= 0;



        for(int i=0;i < 17 ; i = i + 1){
            colprefijo[vueltaano] = i+1;
            vueltaano = vueltaano + 1;
        }



        prefijo.setAdapter(new ArrayAdapter<Integer>(this, R.layout.support_simple_spinner_dropdown_item, colprefijo)); // simple_spinner_item
        prefijo.setSelection(actual);

        vueltaano= 0;
        for(int i=anoini;i <= anofinal ; i = i + 1){
            if(i==anoactual){
                actual = vueltaano;
            }
            colano[vueltaano] = i;
            vueltaano = vueltaano + 1;
        }

        ano.setAdapter(new ArrayAdapter<Integer>(this, R.layout.support_simple_spinner_dropdown_item, colano)); // simple_spinner_item
        ano.setSelection(actual);

        vueltaano = 0;
        for (int i=1;i<=12;i = i + 1){
            if(i==mesactual){
                actual = vueltaano;
            }
            colmes[vueltaano] = i;
            vueltaano = vueltaano + 1;
        }
        mes.setAdapter(new ArrayAdapter<Integer>(this, R.layout.support_simple_spinner_dropdown_item, colmes)); // simple_spinner_item
        mes.setSelection(actual);



        int finaldias = 0;

        if(mesactual==1 || mesactual==3 || mesactual==5 || mesactual==7 || mesactual==8 || mesactual==10 || mesactual==12){
            coldia = new Integer[31];
            finaldias = 31;
        }else{
            if(mesactual == 2){
                coldia = new Integer[28];
                finaldias = 28;
            }else {
                coldia = new Integer[30];
                finaldias = 30;
            }
        }
        vueltaano = 0;
        for (int i=1;i<=finaldias;i = i + 1){
            if(i==diaactual){
                actual = vueltaano;
            }
            coldia[vueltaano] = i;
            vueltaano = vueltaano + 1;
        }
        dia.setAdapter(new ArrayAdapter<Integer>(this, R.layout.support_simple_spinner_dropdown_item, coldia)); // simple_spinner_item
        dia.setSelection(actual);





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

    public void cargarpre(){
        Double totalFot = 0.0;
        Time time = new Time();
        time.setToNow();
        Integer[] prefijosel= null;


        Cursor fnotos= BaseDeDatos.getWritableDatabase().rawQuery("select prefijo from ConsignaRecibo  where Valor > 0 and conyear="+ time.year+" and conmonth = "+(time.month + 1) +" and conday = "+time.monthDay+" ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' OR
        if(fnotos.getCount()> 0){
            prefijosel= new Integer[fnotos.getCount()];
            fnotos.moveToFirst();
            int vuelta = 0;
            do{
                prefijosel[vuelta] = fnotos.getInt(0);
                vuelta+=1;
            }while(fnotos.moveToNext());
        }else{
            totalFot = 0.0;
        }
        if (prefijosel != null){
            List<Integer> excluidos= Arrays.asList(prefijosel);
            ncolprefijo = new Integer[17-prefijosel.length];
            Integer actual = 0;
            Integer bandera = 1;
            int vuelta = 0;
            while(bandera <= ncolprefijo.length){
                actual+=1;
                if(!excluidos.contains(actual)){
                    ncolprefijo[vuelta] = actual;
                    Log.e("Lsita",String.valueOf(actual));
                    vuelta+=1;
                    bandera+=1;
                }
            }
        }else{
            int vueltaano= 0;
            ncolprefijo = new Integer[17];

            for(int i=0;i < 17 ; i = i + 1){
                ncolprefijo[vueltaano] = i+1;
                vueltaano = vueltaano + 1;
            }

        }

        prefijo.setAdapter(new ArrayAdapter<Integer>(this, R.layout.support_simple_spinner_dropdown_item, ncolprefijo)); // simple_spinner_item
        prefijo.setSelection(0);



    }



}