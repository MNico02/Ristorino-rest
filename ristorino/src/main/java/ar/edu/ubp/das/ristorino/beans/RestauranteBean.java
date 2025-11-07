package ar.edu.ubp.das.ristorino.beans;


import java.util.List;

public class RestauranteBean {
    private int nroRestaurante;
    private String razonSocial;
    private List<SucursalBean> sucursales;

    public List<SucursalBean> getSucursales() {
        return sucursales;
    }

    public void setSucursales(List<SucursalBean> sucursales) {
        this.sucursales = sucursales;
    }

    public int getNroRestaurante() {
        return nroRestaurante;
    }

    public void setNroRestaurante(int nroRestaurante) {
        this.nroRestaurante = nroRestaurante;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }
}