package com.ficc.mwmovil;

public class SdtPedido {

        private String facnro;
        private String fechaCreacion;
        private String nitide;
        private String rutdes;
        private String alistado;
        private String FacSecRel;
    private String empacado;
    private String FacSec;
    private String fechaAlistamiento;
    private String FechaNueva;

        public SdtPedido(String facnro, String fechaCreacion, String nitide,
                      String rutdes, String alistado, String empacado,
                      String fechaAlistamiento,String FechaNueva,String FacSec,String FacSecRel) {

            this.facnro = facnro;
            this.fechaCreacion = fechaCreacion;
            this.nitide = nitide;
            this.rutdes = rutdes;
            this.alistado = alistado;
            this.empacado = empacado;
            this.fechaAlistamiento = fechaAlistamiento;
            this.FechaNueva = FechaNueva;
            this.FacSec = FacSec;
            this.FacSecRel  = FacSecRel;
        }

    public String getFacSecRel() {
        return FacSecRel;
    }

    public void setFacSecRel(String facSecRel) {
        FacSecRel = facSecRel;
    }

    public String getFacSec() {
        return FacSec;
    }

    public void setFacSec(String facSec) {
        FacSec = facSec;
    }

    public String getFacnro() {
            return facnro;
        }

    public String getFechaNueva() {
        return FechaNueva;
    }

    public void setFechaNueva(String fechaNueva) {
        FechaNueva = fechaNueva;
    }

    public String getFechaCreacion() {
            return fechaCreacion;
        }

        public String getNitide() {
            return nitide;
        }

        public String getRutdes() {
            return rutdes;
        }

        public String getAlistado() {
            return alistado;
        }

        public String getEmpacado() {
            return empacado;
        }

        public String getFechaAlistamiento() {
            return fechaAlistamiento;
        }
    }