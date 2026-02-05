package ar.edu.ubp.das.ristorino.beans;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RestauranteHomeBean {

    private String nroRestaurante;
    private String razonSocial;

    private Map<String, List<String>> categorias = new LinkedHashMap<>();
    private List<SucursalesHomeBean> sucursales = new ArrayList<>();

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

    public Map<String, List<String>> getCategorias() {
        return categorias;
    }

    public void setCategorias(Map<String, List<String>> categorias) {
        this.categorias = categorias;
    }

    public List<SucursalesHomeBean> getSucursales() {
        return sucursales;
    }

    public void setSucursales(List<SucursalesHomeBean> sucursales) {
        this.sucursales = sucursales;
    }
}
