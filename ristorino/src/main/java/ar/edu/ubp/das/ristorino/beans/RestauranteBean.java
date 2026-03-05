package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;
import java.util.List;
@Data
public class RestauranteBean {
    private String nroRestaurante;
    private String razonSocial;
    private List<SucursalBean> sucursales;

}