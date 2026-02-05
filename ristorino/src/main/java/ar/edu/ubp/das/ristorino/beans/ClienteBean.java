package ar.edu.ubp.das.ristorino.beans;

import java.util.List;

public class ClienteBean {

    private String apellido;
    private String nombre;
    private String clave;
    private String correo;
    private String telefonos;
    private String nomLocalidad;
    private String nomProvincia;
    private String observaciones;

    // 🔹 LEGADO (no se rompe)
    private Integer codCategoria;
    private Integer nroValorDominio;

    // 🔹 NUEVO
    private List<PreferenciaRegistroBean> preferencias;


    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(String telefonos) {
        this.telefonos = telefonos;
    }

    public String getNomLocalidad() {
        return nomLocalidad;
    }

    public void setNomLocalidad(String nomLocalidad) {
        this.nomLocalidad = nomLocalidad;
    }

    public String getNomProvincia() {
        return nomProvincia;
    }

    public void setNomProvincia(String nomProvincia) {
        this.nomProvincia = nomProvincia;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Integer getCodCategoria() {
        return codCategoria;
    }

    public void setCodCategoria(Integer codCategoria) {
        this.codCategoria = codCategoria;
    }

    public Integer getNroValorDominio() {
        return nroValorDominio;
    }

    public void setNroValorDominio(Integer nroValorDominio) {
        this.nroValorDominio = nroValorDominio;
    }

    public List<PreferenciaRegistroBean> getPreferencias() {
        return preferencias;
    }

    public void setPreferencias(List<PreferenciaRegistroBean> preferencias) {
        this.preferencias = preferencias;
    }
}
