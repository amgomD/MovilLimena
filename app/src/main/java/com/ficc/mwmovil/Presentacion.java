package com.ficc.mwmovil;

public class Presentacion {
    private String preartcod;
    private Double PrePefijval = 0.0;
    private String preartnom;

    private Integer PreArtFacConVal = 1;

    public Presentacion(String preartcod, String preartnom, Double prePefijval, Integer preArtFacConVal) {
        this.preartcod = preartcod;
        PrePefijval = prePefijval;
        this.preartnom = preartnom;
        PreArtFacConVal = preArtFacConVal;
    }

    public Integer getPreArtFacConVal() {
        if(PreArtFacConVal == 0){
            PreArtFacConVal = 1;
        }
        return PreArtFacConVal;
    }

    public void setPreArtFacConVal(Integer preArtFacConVal) {
        PreArtFacConVal = preArtFacConVal;
    }

    public String getPreartcod() {
        return preartcod;
    }

    public String getPreartnom() {
        return preartnom;
    }

    public void setPreartcod(String preartcod) {
        this.preartcod = preartcod;
    }

    public Double getPrePefijval() {
        return PrePefijval;
    }

    public void setPrePefijval(Double prePefijval) {
        PrePefijval = prePefijval;
    }

    public void setPreartnom(String preartnom) {
        this.preartnom = preartnom;
    }

    @Override
    public String toString() {
        return preartnom; // 👈 ESTO muestra el nombre en el spinner
    }
}