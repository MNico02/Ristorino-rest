package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class ReservaClienteBean {

    private Integer nroCliente;
    private Integer nroReserva;
    private String codReservaSucursal;

    private LocalDate fechaReserva;
    private LocalTime horaReserva;

    private Integer cantAdultos;
    private Integer cantMenores;
    private BigDecimal costoReserva;

    private Integer codEstado;
    private String nomEstado;

    private Integer nroRestaurante;
    private String nombreRestaurante;

    private Integer nroSucursal;
    private String nombreSucursal;

    private LocalDateTime fechaCancelacion;
}