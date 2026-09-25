package com.ficc.mwmovil;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;

public class GlobalVariables extends Application {
    private static GlobalVariables instance;
    private String Empresa;
    private String EmpresaIp;
    private int AliNegCod;
    private int ParMovPedMin;
    private int SucCod;
    private String ParMovBon;
    private String ParMovManCanCaj;
    private String ParMovManDesConf;
    private String ParMovNoOtorgar;
    private String ParMovDescV2="N";
    private String Version="21032025";

    private String CadenaConexion;

    public String getParMovDescV2() {
        return ParMovDescV2;
    }

    public void setParMovDescV2(String aParMovDescV2) {
        ParMovDescV2 = aParMovDescV2;
    }

    public String getParMovNoOtorgar() {
        return ParMovNoOtorgar;
    }
    public String getVersion() {
        return Version;
    }

    public void setParMovNoOtorgar(String aParMovNoOtorgar) {
        ParMovNoOtorgar = aParMovNoOtorgar;
    }

    public int getSucCod() {
        return SucCod;
    }

    public void setSucCod(int aSucCod) {
        SucCod = aSucCod;
    }

    public String getParMovManDesConf() {
        return ParMovManDesConf;
    }

    public void setParMovManDesConf(String aParMovManDesConf) {
        ParMovManDesConf = aParMovManDesConf;
    }

    public String getParMovManCanCaj() {
        return ParMovManCanCaj;
    }

    public void setParMovManCanCaj(String aParMovManCanCaj) {
        ParMovManCanCaj = aParMovManCanCaj;
    }
    public String getParMovBon() {
        return ParMovBon;
    }

    public void setParMovBon(String aParMovBon) {
        ParMovBon = aParMovBon;
    }

    public int getAliNegCod() {
        return AliNegCod;
    }

    public void setAliNegCod(int aliNegCod) {
        AliNegCod = aliNegCod;
    }

    public void setParMovPedMin(int pParMovPedMin) {
        ParMovPedMin = pParMovPedMin;
    }

    public int getParMovSec() {
        return ParMovSec;
    }

    public void setParMovSec(int parMovSec) {
        ParMovSec = parMovSec;
    }


    private int ParMovSec;

    public String getUsuario() {
        return Usuario;
    }

    public void setUsuario(String usuario) {
        Usuario = usuario;
    }

    private String Usuario;

    public String getCadenaConexion() {
        return CadenaConexion;
    }

    public void setCadenaConexion(String cadenaConexion) {
        CadenaConexion = cadenaConexion;
    }

    public String getEmpresaIp() {
        return EmpresaIp;
    }

    public void setEmpresaIp(String empresaIp) {
        EmpresaIp = empresaIp;
    }

    public String getEmpresa() {

        return Empresa;


    }

    public void setEmpresa(String empresa, Context contex) {
        try {
            Empresa = empresa;
            BaseDatos BaseDeDatos;
            BaseDeDatos = new BaseDatos(contex, "MantisMovil", null, 6);
            BaseDeDatos.getReadableDatabase().execSQL("delete from EmpresaMovil");
            BaseDeDatos.getReadableDatabase().execSQL("insert into EmpresaMovil (EmpMovCod) values('" + empresa + "')");
        }catch (Exception  e){
            Empresa = empresa;
            String kk="Error";
        }
    }

    public static synchronized GlobalVariables getInstance(){
        if (instance==null){
            instance=new GlobalVariables();
        }
        return instance;
    }
}
