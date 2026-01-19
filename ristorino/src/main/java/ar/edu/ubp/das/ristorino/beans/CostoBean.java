package ar.edu.ubp.das.ristorino.beans;

import java.time.LocalDate;

public class CostoBean {
    private String tipoCosto;
    private LocalDate fecha;

    public String getTipoCosto() {
        return tipoCosto;
    }

    public void setTipoCosto(String tipoCosto) {
        this.tipoCosto = tipoCosto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
