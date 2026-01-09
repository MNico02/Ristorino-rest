package ar.edu.ubp.das.ristorino.beans;

import java.util.List;

public class SucursalBean {
    private int nroSucursal;
    private String nomSucursal;
    private String calle;
    private String nroCalle;
    private String barrio;
    private int nroLocalidad;
    private String nomLocalidad;
    private int codProvincia;
    private String nomProvincia;
    private String codPostal;
    private String telefonos;
    private int totalComensales;
    private int  minTolerenciaReserva;
    private String codSucursalRestaurante;
    private List<TurnoBean> turnos;
    private List<ZonaBean> zonas;
    private List<PreferenciaBean> preferencias;
    private List<ZonaTurnoBean> zonasTurnos;


    public List<ZonaTurnoBean> getZonasTurnos() {
        return zonasTurnos;
    }

    public void setZonasTurnos(List<ZonaTurnoBean> zonasTurnos) {
        this.zonasTurnos = zonasTurnos;
    }

    public List<TurnoBean> getTurnos() {
        return turnos;
    }

    public void setTurnos(List<TurnoBean> turnos) {
        this.turnos = turnos;
    }

    public List<ZonaBean> getZonas() {
        return zonas;
    }

    public void setZonas(List<ZonaBean> zonas) {
        this.zonas = zonas;
    }

    public List<PreferenciaBean> getPreferencias() {
        return preferencias;
    }

    public void setPreferencias(List<PreferenciaBean> preferencias) {
        this.preferencias = preferencias;
    }

    public int getNroSucursal() {
        return nroSucursal;
    }

    public void setNroSucursal(int nroSucursal) {
        this.nroSucursal = nroSucursal;
    }

    public String getCodSucursalRestaurante() {
        return codSucursalRestaurante;
    }

    public void setCodSucursalRestaurante(String codSucursalRestaurante) {
        this.codSucursalRestaurante = codSucursalRestaurante;
    }

    public int getMinTolerenciaReserva() {
        return minTolerenciaReserva;
    }

    public void setMinTolerenciaReserva(int minToleranciaReserva) {
        this.minTolerenciaReserva = minToleranciaReserva;
    }

    public int getTotalComensales() {
        return totalComensales;
    }

    public void setTotalComensales(int totalComensales) {
        this.totalComensales = totalComensales;
    }

    public String getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(String telefonos) {
        this.telefonos = telefonos;
    }

    public String getCodPostal() {
        return codPostal;
    }

    public void setCodPostal(String codPostal) {
        this.codPostal = codPostal;
    }

    public String getNomProvincia() {
        return nomProvincia;
    }

    public void setNomProvincia(String nomProvincia) {
        this.nomProvincia = nomProvincia;
    }

    public int getCodProvincia() {
        return codProvincia;
    }

    public void setCodProvincia(int codProvincia) {
        this.codProvincia = codProvincia;
    }

    public String getNomLocalidad() {
        return nomLocalidad;
    }

    public void setNomLocalidad(String nomLocalidad) {
        this.nomLocalidad = nomLocalidad;
    }

    public int getNroLocalidad() {
        return nroLocalidad;
    }

    public void setNroLocalidad(int nroLocalidad) {
        this.nroLocalidad = nroLocalidad;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public String getNroCalle() {
        return nroCalle;
    }

    public void setNroCalle(String nroCalle) {
        this.nroCalle = nroCalle;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNomSucursal() {
        return nomSucursal;
    }

    public void setNomSucursal(String nomSucursal) {
        this.nomSucursal = nomSucursal;
    }
}