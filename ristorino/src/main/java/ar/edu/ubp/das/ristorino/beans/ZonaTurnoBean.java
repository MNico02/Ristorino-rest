package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;

@Data
public class ZonaTurnoBean {
    private int codZona;
    private String horaDesde;
    private boolean permiteMenores;
}
