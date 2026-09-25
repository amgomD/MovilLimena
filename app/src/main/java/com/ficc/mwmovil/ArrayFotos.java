package com.ficc.mwmovil;

public class ArrayFotos {
    public String Vencod;
    public String NitSec;
    public  Integer CliSec;
    public String RegOperacion;
    public  String image64;


    public ArrayFotos(String vencod, String nitSec, Integer cliSec, String regOperacion, String image64) {
        Vencod = vencod;
        NitSec = nitSec;
        CliSec = cliSec;
        RegOperacion = regOperacion;
        this.image64 = image64;
    }

    public String getVencod() {
        return Vencod;
    }

    public void setVencod(String vencod) {
        Vencod = vencod;
    }

    public String getNitSec() {
        return NitSec;
    }

    public void setNitSec(String nitSec) {
        NitSec = nitSec;
    }

    public Integer getCliSec() {
        return CliSec;
    }

    public void setCliSec(Integer cliSec) {
        CliSec = cliSec;
    }

    public String getRegOperacion() {
        return RegOperacion;
    }

    public void setRegOperacion(String regOperacion) {
        RegOperacion = regOperacion;
    }

    public String getImage64() {
        return image64;
    }

    public void setImage64(String image64) {
        this.image64 = image64;
    }
}
