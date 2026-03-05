package ar.edu.ubp.das.ristorino.beans;
import lombok.Data;

@Data
public class ConfirmarReservaResponseBean {

    private boolean success;
    private String estado;
    private String mensaje;

    private String codReserva;


}
