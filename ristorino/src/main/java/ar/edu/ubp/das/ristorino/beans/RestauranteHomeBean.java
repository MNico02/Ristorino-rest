package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
@Data
public class RestauranteHomeBean {

    private String nroRestaurante;
    private String razonSocial;

    private Map<String, List<String>> categorias = new LinkedHashMap<>();
    private List<SucursalesHomeBean> sucursales = new ArrayList<>();

}
