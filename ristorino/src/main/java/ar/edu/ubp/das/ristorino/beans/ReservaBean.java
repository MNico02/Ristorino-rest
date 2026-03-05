package ar.edu.ubp.das.ristorino.beans;
import lombok.Data;

@Data
public class ReservaBean {
    private String codSucursalRestaurante;
    private String correo;
    private int idSucursal;
    private String fechaReserva; // "yyyy-MM-dd"
    private String horaReserva;
    private int cantAdultos;
    private int cantMenores;
    private int codZona;
    private float costoReserva;
    private String voucher;


}