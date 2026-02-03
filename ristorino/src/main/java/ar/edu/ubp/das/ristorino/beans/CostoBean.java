package ar.edu.ubp.das.ristorino.beans;

import java.time.LocalDate;

public class CostoBean {
    private String tipoCosto;
    private String fecha;

    public String getTipoCosto() {
        return tipoCosto;
    }

    public void setTipoCosto(String tipoCosto) {
        this.tipoCosto = tipoCosto;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFecha() {
        return fecha;
    }
}
