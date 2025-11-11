package ar.edu.ubp.das.ristorino.beans;


import java.util.List;

public class RestauranteBean {
    private String nroRestaurante;
    private String razonSocial;
    private List<SucursalBean> sucursales;

    public List<SucursalBean> getSucursales() {
        return sucursales;
    }

    public void setSucursales(List<SucursalBean> sucursales) {
        this.sucursales = sucursales;
    }

    public String getNroRestaurante() {
        return nroRestaurante;
    }

    public void setNroRestaurante(String nroRestaurante) {
        this.nroRestaurante = nroRestaurante;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }
}