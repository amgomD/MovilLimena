package com.ficc.mwmovil;

import android.util.Log;

public class classbd {

    public String SQL ="";
    public String FormatearMysql(String SQL){
        String MYSQL  ="";
        ConBd conbd = new ConBd();
        String bdmysql =  "N";
        conbd.Variables();
        bdmysql  =    conbd.Mysql;
        Log.e("BDMNYSQ",bdmysql);
        if(bdmysql.equalsIgnoreCase("S")){
            MYSQL = SQL.replace("isnull","ifnull");
            MYSQL = MYSQL.replace("isNull","ifnull");
            MYSQL = MYSQL.replace("ISNULL","ifnull");
            MYSQL = MYSQL.replace("GETDATE","CURDATE");
            MYSQL = MYSQL.replace("getdate","curdate");
            MYSQL = MYSQL.replace("numeric","decimal");

        }else{
            MYSQL = SQL;
        }

        return  MYSQL;

    }
}
