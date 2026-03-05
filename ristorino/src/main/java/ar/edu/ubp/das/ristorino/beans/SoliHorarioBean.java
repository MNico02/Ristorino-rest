package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;

@Data
public class SoliHorarioBean {
    private String codSucursalRestaurante;
    private int idSucursal;
    private int codZona;
    private String fecha;
    private int cantComensales;
    private boolean menores;

}