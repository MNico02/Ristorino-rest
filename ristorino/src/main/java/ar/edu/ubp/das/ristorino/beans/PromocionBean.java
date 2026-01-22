package ar.edu.ubp.das.ristorino.beans;
import java.util.Date;

public class PromocionBean {
    private String nroRestaurante;
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



    public String getNroRestaurante() {
        return nroRestaurante;
    }

    public void setNroRestaurante(String nroRestaurante) {
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


}
