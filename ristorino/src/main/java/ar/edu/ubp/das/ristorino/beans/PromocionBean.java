package ar.edu.ubp.das.ristorino.beans;
import java.util.Date;

public class PromocionBean {
    private int nroRestaurante;
    private int nroContenido;
    private int nroSucursal;
    private String contenidoPromocional;
    private String imagenPromocional;

    public String getImagenPromocional() {
        return imagenPromocional;
    }

    public void setImagenPromocional(String imagenPromocional) {
        this.imagenPromocional = imagenPromocional;
    }

    private Date fechaInicio;
    private Date fechaFin;

    public int getNroRestaurante() {
        return nroRestaurante;
    }

    public void setNroRestaurante(int nroRestaurante) {
        this.nroRestaurante = nroRestaurante;
    }

    public int getNroContenido() {
        return nroContenido;
    }

    public void setNroContenido(int nroContenido) {
        this.nroContenido = nroContenido;
    }

    public int getNroSucursal() {
        return nroSucursal;
    }

    public void setNroSucursal(int nroSucursal) {
        this.nroSucursal = nroSucursal;
    }

    public String getContenidoPromocional() {
        return contenidoPromocional;
    }

    public void setContenidoPromocional(String contenidoPromocional) {
        this.contenidoPromocional = contenidoPromocional;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }
}
