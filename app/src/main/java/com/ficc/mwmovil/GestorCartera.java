package com.ficc.mwmovil;

import android.content.Context;
import android.database.Cursor;

public class GestorCartera {

    Integer CarteraVendedor=0;
    Integer CarteraGeneral=0;
    Integer MoraVendedor=0;
    Integer MoraGeneral=0;

    public void TotalesCatera(Context pContext,String nitsec,Integer clisec){

        BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 6);

        String ConsultaWhere="";

        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();

        if (!nitsec.isEmpty()){
            if (!ConsultaWhere.isEmpty()){
                ConsultaWhere+=" and ";
            }
            ConsultaWhere+=" movnitsec='"+nitsec+"' ";
        }
        if (clisec>0){
            if (!ConsultaWhere.isEmpty()){
                ConsultaWhere+=" and ";
            }
            ConsultaWhere+=" movclisec="+clisec+" ";
        }

        if (!ConsultaWhere.isEmpty()){
            ConsultaWhere=" where "+ConsultaWhere;
        }

        try {
            Cursor cursor = BaseDeDatos.getReadableDatabase().rawQuery("select MovFacSec,FacConPag,FacFec,FacVen,FacMora,FacTotalImpuestos,FacAbonos,FacSaldo,FacVenCod,FacPedCon from cartera "+ConsultaWhere+" ", null);
            //SDTCartera=new SDTCartera[cursor.getCount()];
            Integer vuelta=0;
            if (cursor.getCount()>0){
                cursor.moveToFirst();
                do {

                    try {
                        if(cursor.getString(8).equalsIgnoreCase(vUsuario)){
                            if (MoraVendedor<cursor.getInt(4) && (cursor.getString(9).equalsIgnoreCase("ACTIVA") ||  cursor.getString(9).isEmpty())){
                                MoraVendedor=cursor.getInt(4);
                            }
                            CarteraVendedor+=cursor.getInt(7);
                        }
                        if (MoraGeneral<cursor.getInt(4) && (cursor.getString(9).equalsIgnoreCase("ACTIVA") ||  cursor.getString(9).isEmpty())){
                            MoraGeneral=cursor.getInt(4);
                            //CarteraGeneral+=cursor.getInt(7);
                        }
                        CarteraGeneral+=cursor.getInt(7);

                    }catch (Exception e){
                        Integer Error=1;
                    }
                    vuelta=vuelta+1;
                } while (cursor.moveToNext());
            }

        }catch (Exception e){
            Integer Error=1;
        }

    }

    public void TotalesCateraCliente(Context pContext,String nitsec,Integer clisec){

        BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(pContext, "MantisMovil", null, 6);

        String ConsultaWhere="";
        ConsultaWhere=" where movnitsec='"+nitsec+"' and movclisec="+clisec+" ";

        try {
            Cursor cursor = BaseDeDatos.getReadableDatabase().rawQuery("select total(FacSaldo) from cartera "+ConsultaWhere+" ", null);
            //SDTCartera=new SDTCartera[cursor.getCount()];
            Integer vuelta=0;
            if (cursor.getCount()>0){
                cursor.moveToFirst();
                do {

                    try {
                        CarteraGeneral+=cursor.getInt(0);
                    }catch (Exception e){
                        Integer Error=1;
                    }
                    vuelta=vuelta+1;
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            Integer Error=1;
        }

    }


}
