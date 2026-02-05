package ar.edu.ubp.das.ristorino.beans;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SucursalesHomeBean {

    private Integer nroSucursal;
    private String nomSucursal;
    private String calle;
    private Integer nroCalle;
    private String barrio;
    private String codPostal;
    private String telefonos;

    private Map<String, List<String>> preferencias = new LinkedHashMap<>();

    public Integer getNroSucursal() {
        return nroSucursal;
    }

    public void setNroSucursal(Integer nroSucursal) {
        this.nroSucursal = nroSucursal;
    }

    public String getNomSucursal() {
        return nomSucursal;
    }

    public void setNomSucursal(String nomSucursal) {
        this.nomSucursal = nomSucursal;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public Integer getNroCalle() {
        return nroCalle;
    }

    public void setNroCalle(Integer nroCalle) {
        this.nroCalle = nroCalle;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public String getCodPostal() {
        return codPostal;
    }

    public void setCodPostal(String codPostal) {
        this.codPostal = codPostal;
    }

    public String getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(String telefonos) {
        this.telefonos = telefonos;
    }

    public Map<String, List<String>> getPreferencias() {
        return preferencias;
    }

    public void setPreferencias(Map<String, List<String>> preferencias) {
        this.preferencias = preferencias;
    }
}
