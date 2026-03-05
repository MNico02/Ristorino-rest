package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
@Data
public class SucursalesHomeBean {

    private Integer nroSucursal;
    private String nomSucursal;
    private String calle;
    private Integer nroCalle;
    private String barrio;
    private String codPostal;
    private String telefonos;

    private Map<String, List<String>> preferencias = new LinkedHashMap<>();
}
