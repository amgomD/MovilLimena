package com.ficc.mwmovil;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class VerificacionPedido extends AppCompatActivity {

TextView Confirped ;
 Button buscarnro;
 EditText NroFac ;
    SDTProductos[] SDTProductos ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_verificacion_pedido);
         Confirped  = findViewById(R.id.Confirped);
         buscarnro = findViewById(R.id.buscarnro);
         NroFac = findViewById(R.id.NroFac);


        buscarnro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                importar();
            }
        });

    }

    private void importar() {
        String PedNum = NroFac.getText().toString();

        ConBd conbd = new ConBd();
        conbd.Variables();
            Connection conn = conbd.CargarConexion(getApplicationContext());
            Statement comm = null;
            String Sinbd="N";
            try {
                comm = conn.createStatement();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                Sinbd="S";
            }

        BaseDatos BaseDeDatos = new BaseDatos(this, "MantisMovil", null, 5);

            GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
            String vUsuario=vGlobalVariables.getUsuario();
            String Empresa=vGlobalVariables.getEmpresa();
            Empresa=Empresa.toUpperCase();
        Time time = new Time();
        time.setToNow();

            String sExistencia="0.00";

            try {

                String script = "select count(*) as rows,cotsubnitsec,cotsubclisec,CotCliConPag,cotLisprecod,Bodsucccsec from cotizaciones1 c left join cotizacionesdetalle1 d  on c.cotsec = d.cotsec where cotnum = '"+PedNum+"' group by cotsubnitsec,cotsubclisec,cotcliconpag,cotLisprecod,Bodsucccsec";
                ResultSet rsImportconta = comm.executeQuery(script);
                int tamanors = 0;
                String nitsec = "";
                int clisec = 0;
                int plazo = 0;
                String   plazoNom =  "";
                Integer lisprecod =0;
                String prefijo =  "";
                Integer bodcod = 0;
                while (rsImportconta.next()) {
                    tamanors = rsImportconta.getInt("rows");
                     nitsec = rsImportconta.getString("cotsubnitsec");
                     clisec = rsImportconta.getInt("cotsubclisec");
                     plazo = rsImportconta.getInt("CotCliConPag");
                     lisprecod =    rsImportconta.getInt("cotLisprecod");
                     bodcod = rsImportconta.getInt("Bodsucccsec");
                }

                int vuelta = 0;

                BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);
                int traerdcto=0;
                Cursor cursorCla = BaseDeDatos.getReadableDatabase().rawQuery("select * from PerfilClientesClase ", null);
                if (cursorCla.getCount()>0){
                    traerdcto=1;
                }

                conbd.Variables();
                GlobalVariables gGlobalVariables = GlobalVariables.getInstance();
                final String vEmpresa=gGlobalVariables.getEmpresa();

                int perfil=99;
                String clisiniva = "S";
                Cursor cursorCli = BaseDeDatos.getReadableDatabase().rawQuery("select PerCliCod,CliIva from clientes where nitsec='"+nitsec+"' and clisec="+clisec+" ", null);
                if (cursorCli.getCount()>0){
                    cursorCli.moveToFirst();
                    do {
                        try {
                            perfil=cursorCli.getInt(0);
                            clisiniva = cursorCli.getString(1);
                        }catch (Exception e){
                            Integer Error=1;
                        }
                    } while (cursorCli.moveToNext());
                }

                String Exist;
                String Precio;
                String PrecioPorRen = "precio" + lisprecod+"PorRen";
                if(lisprecod==0) {
                    lisprecod = 1;
                }
                if (prefijo.equalsIgnoreCase("NC")){
                    Exist = "KarUni";
                    Precio = "KarPrePub";
                    PrecioPorRen= "0 ";
                }else{
                    Exist = "Exist";
                    Precio = "Precio" + lisprecod;
                    PrecioPorRen = "precio" + lisprecod+"PorRen";
                }
                if(prefijo.equalsIgnoreCase("V15") & vEmpresa.trim().equalsIgnoreCase("IBANEZ")){
                    Exist = "ExistFec";
                }



                ResultSet rsImport = comm.executeQuery("select CotNum,REPLACE(cotnum," +
                        "CONCAT(cotsubvencod,cotsubnitsec,cotsubclisec,year(cotfecha),'-',month(cotfecha),'-',day(cotfecha)),'')as prefijo" +
                        ", cotsubnitsec, cotsubclisec,ArtSec,cotsubvencod,CotArtEmb,year(cotfecha) as ano,month(cotfecha)as mes,day(cotfecha) as dia" +
                        ",CotArtPrecio,cotcliconpag,cotLisprecod,cotartconiva,CotArtDesUno,CotArtDesDos,CotArtDesTre ,CotArtDesCua,CotLisPreCod,cotArtuni,CotArtcaj" +
                        " from cotizaciones1 c " +
                        " left join cotizacionesdetalle1 d " +
                        " on c.cotsec = d.cotsec " +
                        " where cotnum = '"+PedNum+"'");

                SDTProductos = new SDTProductos[tamanors];
                int cont = 0;


                while (rsImport.next()) {

                    SDTProductos SDTProductosItem = new SDTProductos();
                    String artsec = rsImport.getString("ArtSec").trim();
                    double karuni = rsImport.getDouble("cotArtuni");

                    String Consulta = "select a.ArtSec,ArtCod,ArtNom," +
                            " case lgs.LisPreCod when 1 then Precio1 when 2 then Precio2 when 3 then Precio3 when 4 then Precio4 when 5 then Precio5 when 6 then Precio6 when 7 then Precio7 when 8 then Precio8 when 9 then Precio9  " +
                            " else " + Precio + " end precio,Desc1,(ifNULL(Desc2,0)+ifNULL((select PerCliDetDes1 from PerfilClientesClase pc where pc.ClaArtCod=a.ClaArtCod and pc.PerCliCod="+perfil+"),0)) Desc2," + Exist + " Exist,artemb,case  cliiva when 'S' then ParConIva else 0 end ParConIva,ArtValImp,0 cant,0 cantcaj,InvGruNom,artmednomcom,0 KarUni,0 KarPrePub,ifNULL(Desc1,0) pordesc,ifNULL(Desc2,0) pordesc2,ifNULL(Desc3,0) pordesc3,ifNULL(Desc4,0) pordesc4,0.0 pordesc5,0.0 pordesc6,ArtRen,ArtLim,PrePreFijCosPro," + PrecioPorRen + " PrecioPorRen, " +
                            " case lgs.LisPreCod when 1 then 1 when 2 then 2 when 3 then 3 when 4 then 4 when 5 then 5 when 6 then 6 when 7 then 7 when 8 then 8 when 9 then 9  else " + lisprecod + " end listafin, ifnull(ArtExiAct,0) ArtExiAct ,GruCheck,SubCheck,FamCheck " +
                            " from Articulos a " +
                            " left join Clientes cc on cc.nitsec='" + nitsec + "' and cc.clisec='" + clisec + "'  " +
                            "left join ArticulosExi ex on ex.ArtSec=a.ArtSec and ArtBodCod = "+bodcod+" " +
                            " left join ListaPorGrupoSubgrupo lgs on lgs.nitsec='" + nitsec + "' and lgs.clisec='" + clisec + "' and  ((lgs.invgrucod=a.invgrucod and lgs.invsubgrucod =a.invsubgrucod ) or (lgs.invgrucod=a.invgrucod and lgs.invsubgrucod ='0')) " +
                            " where rtrim(ltrim(a.artsec))='"+artsec.trim()+"' order by Exist desc ";
                    Cursor cursor = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null); //order by nombre
                    if (cursor.getCount() > 0) {

                        double precioNu = 0;
                        cursor.moveToFirst();
                        do {

                            precioNu = 0;
                            try{
                                String consultaart = "select precioesp from PreciosEspeciales where peNitSec = '"+nitsec+"' and peArtSec = '"+cursor.getString(0).trim()+"'";
                                Cursor cursorart = BaseDeDatos.getWritableDatabase().rawQuery(consultaart, null);
                                cursorart.moveToFirst();
                                if(cursorart.getCount() > 0){
                                    cursorart.moveToFirst();
                                    precioNu = cursorart.getDouble(0);
                                }else{
                                    precioNu = cursor.getDouble(3);
                                }
                            }catch (Exception e){
                                precioNu = cursor.getDouble(3);
                            }

                            SDTProductosItem.ArtSec = cursor.getString(0);
                            SDTProductosItem.Codigo = cursor.getString(1);
                            SDTProductosItem.Nombre = cursor.getString(2) + " (" + cursor.getString(12) + ")";
                            SDTProductosItem.Nombrecomercial = cursor.getString(13);
                            Double exi = cursor.getDouble(27);
                            Double karuniinf = 0.0;

                            if(cursor.getDouble(27) < karuni){
                                karuni = cursor.getDouble(27);
                                karuniinf = karuni - exi;
                            }
                            SDTProductosItem.Unidades = karuni;
                            SDTProductosItem.Unidadesinf = karuniinf;
                            Double precio = precioNu;

                            if(clisiniva.equalsIgnoreCase("S")){//Nuevo andres
                                SDTProductosItem.Iva = cursor.getInt(8);
                            }else{
                                SDTProductosItem.Iva = 0;
                            }

                            SDTProductosItem.Precio = precio;
                            SDTProductosItem.PrecioIva = precio * (1 + (Double.valueOf(cursor.getInt(8)) / 100));
                            SDTProductosItem.PrecioNeto = (  (precio * (1 + (Double.valueOf(cursor.getInt(8)) / 100))) *(1-(cursor.getDouble(16)/100)) *(1-(cursor.getDouble(17)/100)) *(1-(cursor.getDouble(18)/100)) *(1-(cursor.getDouble(19)/100)) *(1-(cursor.getDouble(20)/100)) *(1-(cursor.getDouble(21)/100))   )   +cursor.getDouble(9);
                            SDTProductosItem.ConfEmp = 0.0;// cursor.getDouble(0);
                            SDTProductosItem.ConfProv = 0.0; // cursor.getDouble(0);

                            SDTProductosItem.Existencia = cursor.getDouble(27);
                            Log.e("BodegaCargue: ",String.valueOf(bodcod));
                            SDTProductosItem.bodcod = bodcod;
                            SDTProductosItem.Embalaje = cursor.getInt(7);
                            SDTProductosItem.ArtRen = cursor.getDouble(22);
                            SDTProductosItem.ArtLim = cursor.getDouble(23);
                            SDTProductosItem.CostoPro = cursor.getDouble(24);
                            SDTProductosItem.RentLis = cursor.getDouble(25);
                            SDTProductosItem.LisPreCod = cursor.getInt(26);
                            SDTProductosItem.Impoconsumo = cursor.getDouble(9);

                            if (traerdcto==1 || vEmpresa.trim().equalsIgnoreCase("PROMEFAR") )  {
                                SDTProductosItem.PrecioNeto = (  (precio * (1 + (Double.valueOf(cursor.getInt(8)) / 100))) *(1-(cursor.getDouble(16)/100)) *(1-(cursor.getDouble(5)/100)) *(1-(cursor.getDouble(18)/100)) *(1-(cursor.getDouble(19)/100)) *(1-(cursor.getDouble(20)/100)) *(1-(cursor.getDouble(21)/100))   )   +cursor.getDouble(9);
                                SDTProductosItem.Dct1 = cursor.getDouble(4); //cursor.getDouble(16)
                                SDTProductosItem.Dct2 = cursor.getDouble(5); //cursor.getDouble(17)
                            }else{
                                SDTProductosItem.Dct1 = 0.0; //cursor.getDouble(18)
                                SDTProductosItem.Dct2 = 0.0; //cursor.getDouble(19);
                            }
                            SDTProductosItem.Dct3 = 0.0; //cursor.getDouble(18)
                            SDTProductosItem.Dct4 = 0.0; //cursor.getDouble(19);
                            SDTProductosItem.Dct5 = 0.0; //cursor.getDouble(20);
                            SDTProductosItem.Dct6 = 0.0; //cursor.getDouble(21);
                            SDTProductosItem.Dct1no = 0.0;
                            SDTProductosItem.Dct2no = 0.0;
                            SDTProductosItem.Dct3no = 0.0; // cursor.getDouble(0);
                            SDTProductosItem.Dct4no = 0.0; // cursor.getDouble(0);
                            SDTProductosItem.Dct5no = 0.0; // cursor.getDouble(0);
                            SDTProductosItem.Dct6no = 0.0; // cursor.getDouble(0);
                            SDTProductosItem.Dct7no = 0.0; // cursor.getDouble(0);
                            SDTProductosItem.Dct8no = 0.0; // cursor.getDouble(0);
                            SDTProductosItem.Dct6no = cursor.getDouble(25);
                            SDTProductosItem.Dct7 = 0.0; // cursor.getDouble(0);
                            SDTProductosItem.Dct8 = 0.0; // cursor.getDouble(0);
                            SDTProductosItem.Prefijo = prefijo;
                            SDTProductosItem.Plazo = plazo;
                            SDTProductosItem.plazoNom = plazoNom;
                            SDTProductosItem.NitSec = nitsec;
                            SDTProductosItem.CliSec = clisec;
                            SDTProductosItem.pContext = getApplicationContext();
                            SDTProductosItem.EvaluarDescuentos();
                            SDTProductosItem.Calcular();
                            SDTProductos[vuelta] = SDTProductosItem;
                        } while (cursor.moveToNext());
                        SDTProductos[vuelta].Guardar(1);
                    }
                    vuelta = vuelta + 1;
                }
                Confirped.setText("Pedido cargado");

            }catch (Exception e){
                Log.e("Errorsqlimport",e.toString());
                Confirped.setText("Ha ocurrido un error ");
            }

    }

}