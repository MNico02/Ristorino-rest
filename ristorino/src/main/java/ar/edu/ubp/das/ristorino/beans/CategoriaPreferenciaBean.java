package ar.edu.ubp.das.ristorino.beans;

import java.util.List;
import lombok.Data;

@Data
public class CategoriaPreferenciaBean {
    private Integer codCategoria;
    private String nomCategoria;
    private List<DominioCategoriaPreferenciaBean> dominios;

}
