package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;

import java.util.List;
@Data
public class ReservasClienteRespBean {
    private List<ReservaClienteBean> reservas;
    private List<EstadoBean> estados;
}
