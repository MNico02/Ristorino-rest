package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class DispoRespBean {
    private List<HorarioBean> horarios;
    private BigDecimal costo;
}
