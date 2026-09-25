package com.ficc.mwmovil;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.Time;
import android.util.Log;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SDTProductos {
    android.content.Context pContext;
    String ArtSec;
    String Codigo;
    String Nombre;
    String Presentacion;
    String ArtCodBar;
    String Nombrecomercial;
    Double Precio;
    Double PrecioNuevo=0.0;

    String Historico;
    Double CostoPro;
    Double PrecioIva;
    Double PrecioNeto;
    Double ConfEmp=0.0;
    Double ConfProv=0.0;
    Double ConfVend=0.0;
    Double Existencia=0.0;
    Integer Embalaje;
    Integer Cajas=0;
    Double Unidades=0.0;
    Integer Cajasinf=0;
    Double Unidadesinf=0.0;
    String ArtPesFac = "N";
    String ArtIndMpm = "N";
    Integer Iva;
    Double Impoconsumo=0.0;
    Double RentLis=0.0;
    Double Dct1=0.0;
    Double Dct2=0.0;
    Double Dct3=0.0;
    Double Dct4=0.0;
    Double Dct5=0.0;
    Double Dct6=0.0;
    Double Dct7=0.0;
    Double Dct8=0.0;
    Double Dct1no=0.0;
    Double Dct2no=0.0;
    Double Dct3no=0.0;
    Double Dct4no=0.0;
    Double Dct5no=0.0;
    Double Dct6no=0.0;
    Double Dct7no=0.0;
    Double Dct8no=0.0;
    Double ArtRen=0.0;
    Double ArtLim=0.0;

    Integer LisPreCod=1;
    String Autorizacion="N";
    Integer Bloqueado=0;
    Integer fijDto1=0;
    Integer fijDto2=0;
    Integer fijDto3=0;
    Integer fijDto4=0;
    Integer fijDto5=0;
    Integer fijDto6=0;
    String Prefijo;
    String NitSec;
    Integer CliSec;
    Integer LisPrecod;
    String FiltroDcto;

    String NotaInv = "N";
    String NotaCar = "N";

     byte[] ArtImgBlob;

    Integer Plazo;
    String plazoNom;
    Double TotSubtotal=0.0;
    Double TotIva=0.0;
    Double TotImpoconsumo=0.0;
    Double Total=0.0;
    String[] CausalNombre;
    String CausalNombreSel;
    String TienePromo="N";
    String TxtBonificado="";
    String TxtDescuentos="";
    String TxtBonificadoUni="";

    Integer totalBon = 0;
    String TxtBonificadoCaj="";
    String ValorEnviado="";
    Double ValorPedidoEnviado=0.00;
      String checkmax ="";
    Integer bodcod = 0;
    String TieneBono = "N";
    String pNitSec = "";
    Integer pCliSec = 0;
    String PreArtCod ="";
    double totalpedido = 0.0;

    int dayOfMonth =  AppGlobals.dayOfMonth; // Extras.getInt("dia");
    int month = AppGlobals.month;//Extras.getInt("mes");
    int year = AppGlobals.year;//Extras.getInt("ano");



    public void Calcular(){
        try {
            TotSubtotal=(Precio*(1-(Dct1/100))*(1-(Dct2/100))*(1-(Dct3/100))*(1-(Dct4/100))*(1-(Dct5/100))*(1-(Dct6/100)))*(Unidades+(Cajas*Embalaje));
            Double PorIva=(Iva.doubleValue()/100);
            TotIva=TotSubtotal*PorIva;
            TotImpoconsumo=Impoconsumo*(Unidades+(Cajas*Embalaje));
            Total=TotSubtotal+TotIva+TotImpoconsumo;
        }catch (Exception e){
            TotSubtotal=0.0;
            TotIva=0.0;
            Total=0.0;
            TotImpoconsumo=0.0;
            int error=0;
        }

    }


    public void recalcularPrecios(){

      String txtPrecio = "Precio" + LisPreCod;
      String  Consulta = "select a.ArtSec,ArtCod,ArtNom," +
                " " + txtPrecio + " precio, case  cliiva when 'S' then ParConIva else 0 end ParConIva,ArtValImp," +
              " ifNULL(Desc1,0) pordesc,ifNULL(Desc2,0) pordesc2,ifNULL(Desc3,0) pordesc3,ifNULL(Desc4,0) pordesc4,0.0 pordesc5,0.0 pordesc6  from Articulos a " +
                " left join Clientes cc on cc.nitsec='" + NitSec + "' and cc.clisec='" + CliSec + "'  " +
                "left join ArticulosExi ex on ex.ArtSec=a.ArtSec and ArtBodCod = "+bodcod+" " +
                " left join ListaPorGrupoSubgrupo lgs on lgs.nitsec='" + NitSec + "' and lgs.clisec='" + CliSec + "' and  ((lgs.invgrucod=a.invgrucod and lgs.invsubgrucod =a.invsubgrucod ) or (lgs.invgrucod=a.invgrucod and lgs.invsubgrucod ='0')) " +
                " where rtrim(ltrim(a.artsec))='"+ArtSec.trim()+"' order by Exist desc ";

      try{
          BaseDatos BaseDeDatos ;
          BaseDeDatos =new BaseDatos(pContext,"MantisMovil", null, 5);




          Cursor cursor = BaseDeDatos.getWritableDatabase().rawQuery(Consulta, null); //order by nombre



          if (cursor.getCount() > 0) {
              int vuelta = 0;
              double precioNu = 0;
              cursor.moveToFirst();
              do {
                  Precio =  cursor.getDouble(3);
                  PrecioIva = Precio * (1 + (Double.valueOf(cursor.getInt(4)) / 100));
                  PrecioNeto = (  (Precio * (1 + (Double.valueOf(cursor.getInt(4)) / 100))) *(1-(cursor.getDouble(6)/100)) *(1-(cursor.getDouble(7)/100)) *(1-(cursor.getDouble(8)/100)) *(1-(cursor.getDouble(9)/100)) *(1-(cursor.getDouble(10)/100)) *(1-(cursor.getDouble(11)/100))   )   +cursor.getDouble(5);
                  Guardar(1);
              }while (cursor.moveToNext());
              cursor.close();
          }



      } catch (Exception e) {
          Log.e("E-recalcularPrecios: ",e.toString());
      }






    }


    public void Guardar(int modo){
        Calcular();
        Log.e("modo: ",String.valueOf(modo));

        Log.e("entroguardarDct4: ",Dct4.toString());
        Log.e("entroguardarDct4no: ",Dct4no.toString());
        if(NotaInv.equalsIgnoreCase("S") || NotaCar.equalsIgnoreCase("S")){
            Dct4 = 0.0;
            Dct4no = 0.0;
        }


        Time time = new Time();
        time.setToNow();
        if(dayOfMonth > 0){
            time.set(time.second, time.minute, time.hour,dayOfMonth , month, year);
        }

        try{

            if (CausalNombreSel == null || CausalNombreSel.isEmpty()) {
                if(NotaInv.equalsIgnoreCase("S")){
                    CausalNombreSel = "11";
                }

                if(NotaCar.equalsIgnoreCase("S")){
                    CausalNombreSel = "13";
                }
            }

        BaseDatos BaseDeDatos ;
        BaseDeDatos =new BaseDatos(pContext,"MantisMovil", null, 5);


       /* BaseDatosCopia dbHelper = new BaseDatosCopia(pContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        guardarPedido(db,NitSec,pCliSec,ArtSec,Prefijo,NotaInv,NotaCar,PreArtCod,time.year, (time.month + 1) ,time.monthDay ,Unidades,Precio);

*/
        Cursor Clientes = BaseDeDatos.getWritableDatabase().rawQuery("select * from pedido where nitsec='" + NitSec + "' and clisec=" + CliSec + " and artsec='" + ArtSec + "' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and prefijo= '" + Prefijo + "'  and ifnull(NotaInv,'N') = '"+NotaInv+"' and ifnull(NotaCar,'N') = '"+NotaCar+"'  and PreArtCod = '"+PreArtCod+"' ", null);
            Cursor cursorNCND = BaseDeDatos.getWritableDatabase().rawQuery("select ConNotCod,ConNotNom from ConceptoNCND where connotnom='"+CausalNombreSel+"' ", null); //order by nombre
          /*  int ConNotCod=0;
            if (cursorNCND.getCount()>0){
                cursorNCND.moveToFirst();
                do {
                    ConNotCod=cursorNCND.getInt(0);
                } while (cursorNCND.moveToNext());
            }*/

        if (Clientes.getCount() > 0) {


            Log.e("entroupdate: ",Unidades.toString());
            Log.e("entroupdateinf: ",Unidadesinf.toString());


            BaseDeDatos.getWritableDatabase().execSQL("update pedido set PedLisPreCod="+LisPreCod+" , ConPagnom ='"+plazoNom+"',bodcod="+ bodcod + ",cant=" + Unidades + ",cantcaj="+Cajas+",cantinf=" + Unidadesinf + ",cantcajinf="+Cajasinf+",plazo="+Plazo+",pordesc="+Dct1+",pordesc2="+Dct2+",pordesc3="+Dct3+",pordesc4="+Dct4+",pordesc5="+Dct5+",pordesc6="+Dct6+",confemp="+ConfEmp+",confprov="+ConfProv+",confvend="+ConfVend+",ConNotCod='"+CausalNombreSel+"',ArtTotImp="+TotImpoconsumo+",Autorizacion='"+Autorizacion+"' " +
                    ",pordescCero=" +fijDto1+",pordesc2Cero="+fijDto2+",pordesc3Cero="+fijDto3+",pordesc4Cero="+fijDto4+",pordesc5Cero="+fijDto5+", PreArtCod = '"+PreArtCod+"' , precio ="+Precio  +
                    " where nitsec='" + NitSec + "' and clisec=" + CliSec + " and artsec='" + ArtSec + "' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "  and prefijo='" + Prefijo+ "' and ifnull(NotaInv,'N')  = '"+NotaInv+"' and ifnull(NotaCar,'N') = '"+NotaCar+"' and PreArtCod = '"+PreArtCod+"' ");

           // EvaluarEventos Ev=new EvaluarEventos();
           // Ev.Evaluar(cntx, codigo[position2].trim(), Valtext, Fextras);
        } else {

            Log.e("entroinsertUnidades: ",String.valueOf(Unidades+Unidadesinf));

            if (Unidades+Unidadesinf != 0.0) {
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
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String fechaActual = sdf.format(new Date());

                GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
                String vendedor=vGlobalVariables.getUsuario();

                String consulta = "insert into pedido(prefijo,nitsec,clisec,PedLisPreCod,artsec,vencod,cant,cantcaj,cantinf,pdyear,pdmonth,pdday,pordescCero,pordesc2Cero,pordesc3Cero,pordesc4Cero,pordesc5Cero,precio,plazo,pordesc,pordesc2,pordesc3,pordesc4,pordesc5,pordesc6,pordescno,pordesc2no,pordesc3no,pordesc4no,pordesc5no,pordesc6no,fechahora,ArtTotImp,confemp,confprov,confvend,ConNotCod,pedartemb,Autorizacion,ConPagnom,bodcod,NotaInv,NotaCar,PreArtCod,FechaPedido)" +
                        "values('" + Prefijo +"','" +
                        NitSec.trim() + "'," +
                        CliSec+ "," +
                        LisPreCod+",'"+
                        ArtSec + "','"+vendedor+"'," +
                        Unidades + ",    " +
                        Cajas + ","+Unidadesinf+"," +
                        time.year + "," +
                        (time.month + 1) + "," +
                        time.monthDay + "," +
                        fijDto1+","+fijDto2+","+fijDto3+","+fijDto4+","+fijDto5+", "  +
                        Precio + "," + Plazo + "," + Dct1+ "," + Dct2 + ","+Dct3+ ","+Dct4+","+Dct5+","+Dct6+","+Dct1no+","+Dct2no+","+Dct3no+","+Dct4no+","+Dct5no+","+Dct6no+",'"+fecha+"',"+TotImpoconsumo+","+ConfEmp+","+ConfProv+","+ConfVend+",'"+CausalNombreSel+"',(select artemb from articulos where artsec='"+ArtSec+"'),'"+Autorizacion+"','"+plazoNom+"',"+bodcod+",'"+NotaInv+"','"+NotaCar+"','"+PreArtCod+"','"+fechaActual+"')";
                BaseDeDatos.getWritableDatabase().execSQL(consulta);


                Log.e("entroinsert: ",Unidades.toString());
                Log.e("entroinsert: ",Unidadesinf.toString());


            }
        }
        }catch (Exception e){
            int error=0;
            Log.e("UNIDADESGUARDAR: ",e.toString());
        }
       // if(modo==1){

       // }
        if(NotaInv.equalsIgnoreCase("S") || NotaCar.equalsIgnoreCase("S")){

        }else{
            EvaluarDescuentos();
        }


        Calcular();
    }





    public void EvaluarDescuentos(){
        ConBd conbd = new ConBd();
        String mantisficc =  "N";
        conbd.Variables();
        mantisficc  =    conbd.MantisFicc;
        GestorPedidos vGestorPedidos;
        vGestorPedidos=new GestorPedidos();
        vGestorPedidos.CalcularBonificados(pContext,ArtSec,Prefijo,NitSec,CliSec,Unidades,Cajas,Embalaje);
        Log.e("bandera ficc",mantisficc);
        if(mantisficc.equalsIgnoreCase("S")){

            vGestorPedidos.EvaluarFicc(pContext,PreArtCod,ArtSec,Prefijo,NitSec,CliSec,Unidades,Cajas,Embalaje,LisPrecod);
        }else{
            vGestorPedidos.Evaluar(pContext,ArtSec,Prefijo,NitSec,CliSec,Unidades,Cajas,Embalaje);
        }

        ActualizarDescuentos();
    }


    public void ActualizarDescuentos() {
        Time time = new Time();
        time.setToNow();

        if(dayOfMonth > 0){
            time.set(time.second, time.minute, time.hour,dayOfMonth , month, year);
        }

        BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 5);
        try {

            Cursor Clientes = BaseDeDatos.getWritableDatabase().rawQuery("select pordesc,pordesc2,pordesc3,pordesc4,pordesc5,pordesc6,pordescno,pordesc2no,pordesc3no,pordesc4no,pordesc5no,pordesc6no from pedido where nitsec='" + NitSec + "' and clisec=" + CliSec + " and artsec='" + ArtSec + "' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and prefijo= '" + Prefijo + "' and ifnull(NotaInv,'N') = '"+NotaInv+"' and ifnull(NotaCar,'N') = '"+NotaCar+"'  and PreArtCod = '"+PreArtCod+"' ", null);
            int ConNotCod = 0;
            if (Clientes.getCount() > 0) {
                Clientes.moveToFirst();
                do {

                    Dct1=Clientes.getDouble(0);
                    Dct2=Clientes.getDouble(1);
                    Dct3=Clientes.getDouble(2);
                    Dct4=Clientes.getDouble(3);
                    Dct5=Clientes.getDouble(4);
                    Dct6=Clientes.getDouble(5);
                    Dct1no=Clientes.getDouble(6);
                    Dct2no=Clientes.getDouble(7);
                    Dct3no=Clientes.getDouble(8);
                    Dct4no=Clientes.getDouble(9);
                    Dct5no=Clientes.getDouble(10);
                    Dct6no=Clientes.getDouble(11);
                } while (Clientes.moveToNext());
            }
        }catch (Exception e){
            int error=0;
        }

        try {
            Log.e("Prefijosdt",Prefijo);
            TxtBonificado="";
            TxtBonificadoUni="";
            TxtBonificadoCaj="";
            checkmax = "";
            String TempJJ = "select prefijo,MovParPremSec,MovParPremArtSec,artcod,artnom,MovParPremCant,MovParPremCantCaj, MovParPremcheckmax," +
                    " ifnull((select bontipo from BonificacionesProducto where BonProSec=MovParPremSec LIMIT 1),'X') tipo " +
                    " from MovParPrem left join articulos a on a.artsec=MovParPremArtSec  " +
                    " where  MovParNitSec='"+NitSec+"' and MovParCliSec="+CliSec+" " +
                    " and Prefijo='" + Prefijo + "' and MovParPremTip='NNP' " +
                    " and MovParPremArtSecOri='"+ArtSec+"' ";
            Cursor BonifiV2 = BaseDeDatos.getWritableDatabase().rawQuery(TempJJ, null);
            if (BonifiV2.getCount() > 0) {
                BonifiV2.moveToFirst();
                totalBon=0;
                do {
                    totalBon+=1;
                    if(BonifiV2.getInt(5) > 0) {
                        TxtBonificado += "\n *-Seleccionado:" + BonifiV2.getString(3) + "\n" + BonifiV2.getString(4) + '(' + BonifiV2.getInt(1) + ')' + "\n Cant: " + BonifiV2.getString(5) + "\n";
                    }
                    // TxtBonificadoUni+=BonifiV2.getString(5)+"Uni: "+;
                  //  TxtBonificadoUni=BonifiV2.getString(5)+" Uni";
                  //  TxtBonificadoCaj+=BonifiV2.getString(6)+" Caj";
                    checkmax = BonifiV2.getString(7);
                } while (BonifiV2.moveToNext());
            }
        }catch (Exception e){
            int error=0;
        }
        try {
            TxtDescuentos="";

            String TempJJ = "select DesSec,pordescapli1,pordescapli2,pordescapli3,pordescapli4 from pedidoDesc  where nitsec='"+NitSec+"' and clisec="+CliSec+" and prefijo='" + Prefijo + "' and artsec='"+ArtSec+"' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay ;
            Cursor BonifiV2 = BaseDeDatos.getWritableDatabase().rawQuery(TempJJ, null);
            if (BonifiV2.getCount() > 0) {
                BonifiV2.moveToFirst();
                do {

                    Log.e("DESC: edit ",BonifiV2.getString(0));

                    TxtDescuentos+="Sec: "+BonifiV2.getInt(0)+" - "+BonifiV2.getDouble(1)+' '+BonifiV2.getDouble(2)+' '+BonifiV2.getDouble(3)+' '+BonifiV2.getDouble(4)+" / ";

                } while (BonifiV2.moveToNext());
            }
        }catch (Exception e){
            int error=0;
        }

    }
    public void ActualizarCantidad() {
        Time time = new Time();
        time.setToNow();

        if(dayOfMonth > 0){
            time.set(time.second, time.minute, time.hour,dayOfMonth , month, year);
        }

        try {
            BaseDatos BaseDeDatos;
            BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 5);
            Cursor Clientes = BaseDeDatos.getWritableDatabase().rawQuery("select cant,cantcaj,cantinf,cantcajinf from pedido where nitsec='" + NitSec + "' and clisec=" + CliSec + " and artsec='" + ArtSec + "' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + " and prefijo= '" + Prefijo + "' and ifnull(NotaInv,'N') = '"+NotaInv+"' and ifnull(NotaCar,'N') = '"+NotaCar+"'  and PreArtCod = '"+PreArtCod+"' ", null);
            int ConNotCod = 0;
            if (Clientes.getCount() > 0) {
                Clientes.moveToFirst();
                do {
                    Unidades=Clientes.getDouble(0);
                    Cajas=Clientes.getInt(1);
                    Unidadesinf =Clientes.getDouble(2);
                    Cajasinf = Clientes.getInt(3);
                } while (Clientes.moveToNext());
            }

        }catch (Exception e){
            int error=0;
        }

    }

    public void guardarPedido(SQLiteDatabase db,
                              String nitsec,
                              int clisec,
                              String artsec,
                              String prefijo,
                              String NotaInv,
                              String NotaCar,
                              String PreArtCod,
                              int pdyear,
                              int pdmonth,
                              int pdday,
                              double cant,
                              double precio) {

        String update = "UPDATE pedido SET "
                + "cant=" + cant + ", "
                + "precio=" + precio
                + " WHERE nitsec='" + nitsec + "'"
                + " AND clisec=" + clisec
                + " AND artsec='" + artsec + "'"
                + " AND prefijo='" + prefijo + "'"
                + " AND IFNULL(NotaInv,'N')='" + NotaInv + "'"
                + " AND IFNULL(NotaCar,'N')='" + NotaCar + "'"
                + " AND PreArtCod='" + PreArtCod + "'"
                + " AND pdyear=" + pdyear
                + " AND pdmonth=" + pdmonth
                + " AND pdday=" + pdday;

        db.execSQL(update);

        Cursor cursor = db.rawQuery("SELECT changes()", null);
        cursor.moveToFirst();
        int rows = cursor.getInt(0);
        cursor.close();

        if (rows == 0) {

            String insert = "INSERT INTO pedido("
                    + "nitsec,clisec,artsec,prefijo,NotaInv,NotaCar,PreArtCod,pdyear,pdmonth,pdday,cant,precio"
                    + ") VALUES("
                    + "'" + nitsec + "',"
                    + clisec + ","
                    + "'" + artsec + "',"
                    + "'" + prefijo + "',"
                    + "'" + NotaInv + "',"
                    + "'" + NotaCar + "',"
                    + "'" + PreArtCod + "',"
                    + pdyear + ","
                    + pdmonth + ","
                    + pdday + ","
                    + cant + ","
                    + precio
                    + ")";

            db.execSQL(insert);
        }
    }



}
