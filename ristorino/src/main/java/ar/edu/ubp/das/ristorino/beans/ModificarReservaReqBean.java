package ar.edu.ubp.das.ristorino.beans;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public class ModificarReservaReqBean {
    Integer nroRestaurante;
    String codReservaSucursal;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaReserva;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime horaReserva;

    private int cantAdultos;
    private int cantMenores;
    private int codZona;

    public Integer getNroRestaurante() {
        return nroRestaurante;
    }

    public void setNroRestaurante(Integer nroRestaurante) {
        this.nroRestaurante = nroRestaurante;
    }

    public String getCodReservaSucursal() {
        return codReservaSucursal;
    }

    public void setCodReservaSucursal(String codReservaSucursal) {
        this.codReservaSucursal = codReservaSucursal;
    }

    public LocalDate getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(LocalDate fechaReserva) { this.fechaReserva = fechaReserva; }

    public LocalTime getHoraReserva() { return horaReserva; }
    public void setHoraReserva(LocalTime horaReserva) { this.horaReserva = horaReserva; }

    public int getCantAdultos() { return cantAdultos; }
    public void setCantAdultos(int cantAdultos) { this.cantAdultos = cantAdultos; }

    public int getCantMenores() { return cantMenores; }
    public void setCantMenores(int cantMenores) { this.cantMenores = cantMenores; }

    public int getCodZona() { return codZona; }
    public void setCodZona(int codZona) { this.codZona = codZona; }


}
