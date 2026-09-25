package com.ficc.mwmovil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDatosCopia extends SQLiteOpenHelper {

    public BaseDatosCopia(Context context) {
        super(context, "historico.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String TablaPedidos = "CREATE TABLE IF NOT EXISTS pedido ("
                + "nitsec TEXT(25),"
                + "clisec INTEGER,"
                + "artsec TEXT(20),"
                + "prefijo TEXT(20),"
                + "NotaInv TEXT(1),"
                + "NotaCar TEXT(1),"
                + "PreArtCod TEXT(200),"
                + "pdyear INTEGER,"
                + "pdmonth INTEGER,"
                + "pdday INTEGER,"
                + "cant NUMERIC(16,2),"
                + "precio NUMERIC(16,2)"
                + ")";

        db.execSQL(TablaPedidos);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }


}
