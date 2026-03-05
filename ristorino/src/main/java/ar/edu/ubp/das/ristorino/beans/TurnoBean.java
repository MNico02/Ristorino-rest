package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;

@Data
public class TurnoBean {
    private String horaDesde;
    private String horaHasta;
    private Boolean habilitado;

}
