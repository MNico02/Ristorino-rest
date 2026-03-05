package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;

@Data
public class ZonaBean {
    private int codZona;
    private String nomZona;
    private int cantComensales;
    private Boolean permiteMenores;
    private Boolean habilitada;

}
