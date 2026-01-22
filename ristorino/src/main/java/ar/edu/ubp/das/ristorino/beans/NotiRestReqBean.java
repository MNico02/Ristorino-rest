package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class NotiRestReqBean {
    int nroRestaurante;
    BigDecimal costoAplicado;
    String nroContenidos;
}
