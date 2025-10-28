package ar.edu.ubp.das.ristorino.beans;

public class ContenidoPromocionalBean {
    private Integer nroContenido;
    private Integer nroRestaurante;
    private Integer nroSucursal;
    private Integer nroIdioma;
    private String contenidoAPublicar;
    private String contenidoPromocional;
    private String imagenPromocional;
    private Double costoClick;

    // --- Getters y Setters ---
    public Integer getNroContenido() { return nroContenido; }
    public void setNroContenido(Integer nroContenido) { this.nroContenido = nroContenido; }

    public Integer getNroRestaurante() { return nroRestaurante; }
    public void setNroRestaurante(Integer nroRestaurante) { this.nroRestaurante = nroRestaurante; }

    public Integer getNroSucursal() { return nroSucursal; }
    public void setNroSucursal(Integer nroSucursal) { this.nroSucursal = nroSucursal; }

    public Integer getNroIdioma() { return nroIdioma; }
    public void setNroIdioma(Integer nroIdioma) { this.nroIdioma = nroIdioma; }

    public String getContenidoAPublicar() { return contenidoAPublicar; }
    public void setContenidoAPublicar(String contenidoAPublicar) { this.contenidoAPublicar = contenidoAPublicar; }

    public String getContenidoPromocional() { return contenidoPromocional; }
    public void setContenidoPromocional(String contenidoPromocional) { this.contenidoPromocional = contenidoPromocional; }

    public String getImagenPromocional() { return imagenPromocional; }
    public void setImagenPromocional(String imagenPromocional) { this.imagenPromocional = imagenPromocional; }

    public Double getCostoClick() { return costoClick; }
    public void setCostoClick(Double costoClick) { this.costoClick = costoClick; }
}
