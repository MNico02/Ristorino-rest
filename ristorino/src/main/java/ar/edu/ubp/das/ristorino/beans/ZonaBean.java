package ar.edu.ubp.das.ristorino.beans;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ZonaBean {
    private int codZona;
    @JsonProperty("nomZona")
    private String descZona;
    private int cantComensales;
    private Boolean permiteMenores;
    private Boolean habilitada;

    public int getCodZona() {
        return codZona;
    }

    public void setCodZona(int codZona) {
        this.codZona = codZona;
    }

    public String getDescZona() {
        return descZona;
    }

    public void setDescZona(String descZona) {
        this.descZona = descZona;
    }

    public int getCantComensales() {
        return cantComensales;
    }

    public void setCantComensales(int cantComensales) {
        this.cantComensales = cantComensales;
    }

    public Boolean getPermiteMenores() {
        return permiteMenores;
    }

    public void setPermiteMenores(Boolean permiteMenores) {
        this.permiteMenores = permiteMenores;
    }

    public Boolean getHabilitada() {
        return habilitada;
    }

    public void setHabilitada(Boolean habilitada) {
        this.habilitada = habilitada;
    }
}
