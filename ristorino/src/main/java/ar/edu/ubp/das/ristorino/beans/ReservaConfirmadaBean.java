package ar.edu.ubp.das.ristorino.beans;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Data;

@Data
public class ReservaConfirmadaBean {

    private Integer nroCliente;
    private Integer nroReserva;
    private String codReservaSucursal;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaReserva;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime horaReserva;

    private Integer nroRestaurante;
    private Integer nroSucursal;
    private Integer codZona;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime horaDesde;

    private Integer cantAdultos;
    private Integer cantMenores;

    private Integer codEstado;
    private BigDecimal costoReserva;
    private String voucher;


}
