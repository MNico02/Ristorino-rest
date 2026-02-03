package ar.edu.ubp.das.ristorino.beans;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservaSolicitudBean {
    private String codSucursalRestaurante;
    private String correo;
    private int idSucursal;
    private String fechaReserva; // "yyyy-MM-dd"
    private String horaReserva;
    private int cantAdultos;
    private int cantMenores;
    private int codZona;
    private float costoReserva;

    public String getCodSucursalRestaurante() {
        return codSucursalRestaurante;
    }

    public void setCodSucursalRestaurante(String codSucursalRestaurante) {
        this.codSucursalRestaurante = codSucursalRestaurante;
    }

    public String getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(String fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public String getHoraReserva() {
        return horaReserva;
    }

    public void setHoraReserva(String horaReserva) {
        this.horaReserva = horaReserva;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }



    public int getCantAdultos() {
        return cantAdultos;
    }

    public void setCantAdultos(int cantAdultos) {
        this.cantAdultos = cantAdultos;
    }

    public int getCantMenores() {
        return cantMenores;
    }

    public void setCantMenores(int cantMenores) {
        this.cantMenores = cantMenores;
    }

    public int getCodZona() {
        return codZona;
    }

    public void setCodZona(int codZona) {
        this.codZona = codZona;
    }

    public float getCostoReserva() {
        return costoReserva;
    }

    public void setCostoReserva(float costoReserva) {
        this.costoReserva = costoReserva;
    }
}
