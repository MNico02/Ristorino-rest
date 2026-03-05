package ar.edu.ubp.das.ristorino.beans;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ModificarReservaReqBean {
    Integer nroRestaurante;
    String codReservaSucursal;
    private String fechaReserva;
    private String horaReserva;
    private int cantAdultos;
    private int cantMenores;
    private int codZona;
    private BigDecimal costoReserva;
}
