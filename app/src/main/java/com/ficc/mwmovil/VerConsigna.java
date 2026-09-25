package com.ficc.mwmovil;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class VerConsigna extends AppCompatActivity {
    Bundle Extras;
    String nConNro ;
    ListView listaefectivo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_verconsigna);
        Extras = this.getIntent().getExtras();
        TextView fecha = findViewById(R.id.fecha);
        TextView proveedor = findViewById(R.id.proveedor);
        TextView Cuenta = findViewById(R.id.Cuenta);
        TextView BanNom = findViewById(R.id.BanNom);
        TextView tipocon = findViewById(R.id.tipocon);
        TextView Cuentaban = findViewById(R.id.Cuentaban);
        TextView ciudad = findViewById(R.id.ciudad);
        TextView valor = findViewById(R.id.valor);
        TextView Obse = findViewById(R.id.Obse);
        listaefectivo = (ListView) findViewById(R.id.ListarEfectivo);
        TextView ConNro = findViewById(R.id.ConNro);
        TextView tipo = findViewById(R.id.tipo);
        nConNro = Extras.getString("ConNro");
        ConNro.setText(nConNro);
        Time time = new Time();
        time.setToNow();
        ListViewAdapterEfectivo listViewAdapterEfectivo;
        SDTEfecitvo[] sdtEfecitvo;
        BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);

        String consulta = "select Obs,Valor,rconyear,rconmonth,rconday,prefijo,ConNro,NitSec,ifnull(checkproveedor,'N') checkproveedor , ifnull(bancoProv,0) bancoProv,TipoConsigna,  " +
                " CodBanco, TCNSEC, Ciudad, PucSec from ConsignaRecibo where ConNro = '" + nConNro + "'  ";
        Cursor consigna = BaseDeDatos.getWritableDatabase().rawQuery(consulta, null);

        if (consigna.getCount() > 0) {
            consigna.moveToFirst();
            do {
                String obs = consigna.getString(0);
                Double nvalor = consigna.getDouble(1);
                String nfecha = consigna.getString(4) + '/' + consigna.getString(3) + '/' + consigna.getString(2);
                String tipocons = consigna.getString(10);

                if (tipocons.equalsIgnoreCase("CPRO")) {
                    tipo.setText("Proovedor");
                    BanNom.setVisibility(View.GONE);
                    tipocon.setVisibility(View.GONE);
                    Cuentaban.setVisibility(View.GONE);
                    ciudad.setVisibility(View.GONE);
                }
                if (tipocons.equalsIgnoreCase("CTES")) {
                    BanNom.setVisibility(View.GONE);
                    tipocon.setVisibility(View.GONE);
                    Cuentaban.setVisibility(View.GONE);
                    ciudad.setVisibility(View.GONE);
                    tipo.setText("Tesoreria");
                }
                if (tipocons.equalsIgnoreCase("CVEN")) {
                    tipo.setText("Vendedor");
                }


                Obse.setText(obs);
                valor.setText(String.format("%,d", nvalor.intValue()));
                fecha.setText(nfecha);


                String joinc = "    select NitCom,BanNom,BANFINNOM,TCNNOM,PucNom,ciunom from ConsignaRecibo cr left join  Proveedores p on cr.NitSec = p.NitSec " +
                        "left join BancoProv b on cr.NitSec = b.NitSec and cr.bancoProv= b.NitProBanCod  left join Bancos bc on " +
                        " CodBanco = bc.BANFINCOD and cr.TCNSEC = bc.TCNSEC left join BancoCuenta bp on CodBanco = bp.BANFINCOD left join ciudades on ciucod = ciucod  where ConNro = '" + nConNro + "'  ";
                Log.e("consultabancos", joinc);
                Cursor banco = BaseDeDatos.getWritableDatabase().rawQuery(joinc, null);
                if (banco.getCount() > 0) {
                    banco.moveToFirst();
                    do {
                        String NitCom = banco.getString(0);
                        String nBanNom = banco.getString(1);
                        String BANFINNOM = banco.getString(2);
                        String TCNNOM = banco.getString(3);
                        String PucNom = banco.getString(4);
                        String ciunom = banco.getString(5);

                        proveedor.setText(NitCom);
                        Cuenta.setText(nBanNom);
                        BanNom.setText(BANFINNOM);
                        tipocon.setText(TCNNOM);
                        Cuentaban.setText(PucNom);
                        ciudad.setText(ciunom);


                    } while (banco.moveToNext());
                }


            } while (consigna.moveToNext());
        }

        Cursor efectivo = BaseDeDatos.getWritableDatabase().rawQuery(" select rf.RecNro,rf.nitsec,rf.clisec,Clinom,sum(Valor),rf.Tipo,ifnull(ConNro,'') ConNro,ifnull((select Enviado from recibo r where r.RecNro=rf.RecNro),'N') enviado,rf.Tipo,ifnull(rf.aldia,'N') aldia,ifnull(rf.postfecha,'N')postfecha   from Reciboforma rf  " +
                "left join clientes c on rf.nitsec=c.nitsec and rf.clisec= c.clisec " +
                // "left join Reciboforma rf on r.RecNro = rf.RecNro " +
                "where recicyear = " + time.year + " and recicmonth = " + (time.month + 1) + " and recicday = " + time.monthDay + " and (rf.Tipo = 'Efectivo' or rf.Tipo = 'Cheque') and  ConNro ='" + nConNro + "' and enviado = 'S' group by rf.Tipo,rf.aldia, rf.RecNro ", null);


        sdtEfecitvo = new SDTEfecitvo[efectivo.getCount()];
        Integer vuelta = 0;
        if (efectivo.getCount() > 0) {

            efectivo.moveToFirst();
            do {
                SDTEfecitvo sdtEfecitvoItem = new SDTEfecitvo();
                sdtEfecitvoItem.RecNro = efectivo.getString(0);
                sdtEfecitvoItem.Clinom = efectivo.getString(3);
                sdtEfecitvoItem.Valor = efectivo.getDouble(4);
                sdtEfecitvoItem.tipo = efectivo.getString(8);
                sdtEfecitvoItem.aldia = efectivo.getString(9);
                sdtEfecitvoItem.postfecha = efectivo.getString(10);

                if (efectivo.getString(6).equalsIgnoreCase(nConNro)) {
                    sdtEfecitvoItem.check = true;
                }


                sdtEfecitvo[vuelta] = sdtEfecitvoItem;
                vuelta += 1;

            } while (efectivo.moveToNext());

            listViewAdapterEfectivo = new ListViewAdapterEfectivo(VerConsigna.this, sdtEfecitvo);
            listaefectivo.setAdapter(listViewAdapterEfectivo);


        }
    }

}
