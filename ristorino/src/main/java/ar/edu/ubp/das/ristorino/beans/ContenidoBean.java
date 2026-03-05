package ar.edu.ubp.das.ristorino.beans;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ContenidoBean {
    private Integer nroSucursal; // null si es general del restaurante
    private int nroContenido;
    private String contenidoAPublicar;
    private String imagenAPublicar;
    private boolean publicado;
    private BigDecimal costoClick;

}