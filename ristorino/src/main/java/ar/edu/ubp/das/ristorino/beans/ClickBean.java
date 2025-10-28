package ar.edu.ubp.das.ristorino.beans;

public class ClickBean {
    private int nroRestaurante;
    private int nroIdioma;
    private int nroContenido;
    private int nroCliente;
    private double costoClick;

    public int getNroRestaurante() {
        return nroRestaurante;
    }

    public void setNroRestaurante(int nroRestaurante) {
        this.nroRestaurante = nroRestaurante;
    }

    public int getNroIdioma() {
        return nroIdioma;
    }

    public void setNroIdioma(int nroIdioma) {
        this.nroIdioma = nroIdioma;
    }

    public int getNroContenido() {
        return nroContenido;
    }

    public void setNroContenido(int nroContenido) {
        this.nroContenido = nroContenido;
    }

    public int getNroCliente() {
        return nroCliente;
    }

    public void setNroCliente(int nroCliente) {
        this.nroCliente = nroCliente;
    }

    public double getCostoClick() {
        return costoClick;
    }

    public void setCostoClick(double costoClick) {
        this.costoClick = costoClick;
    }
}
