package ar.edu.ubp.das.ristorino.beans;

public class PreferenciaBean {
    private int codCategoria;
    private String nomCategoria;
    private int nroValorDominio;
    private String nomValorDominio;
    private int nroPreferencia;
    private String observaciones;

    public int getCodCategoria() {
        return codCategoria;
    }

    public void setCodCategoria(int codCategoria) {
        this.codCategoria = codCategoria;
    }

    public String getNomCategoria() {
        return nomCategoria;
    }

    public void setNomCategoria(String nomCategoria) {
        this.nomCategoria = nomCategoria;
    }

    public int getNroValorDominio() {
        return nroValorDominio;
    }

    public void setNroValorDominio(int nroValorDominio) {
        this.nroValorDominio = nroValorDominio;
    }

    public String getNomValorDominio() {
        return nomValorDominio;
    }

    public void setNomValorDominio(String nomValorDominio) {
        this.nomValorDominio = nomValorDominio;
    }

    public int getNroPreferencia() {
        return nroPreferencia;
    }

    public void setNroPreferencia(int nroPreferencia) {
        this.nroPreferencia = nroPreferencia;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
