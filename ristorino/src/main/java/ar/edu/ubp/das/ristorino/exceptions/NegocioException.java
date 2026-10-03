package ar.edu.ubp.das.ristorino.exceptions;


public class NegocioException extends RuntimeException {
    /** El dominio rechaza una operación válida (sin cupo, reserva ya cancelada). Mensaje apto para el usuario. */
    public NegocioException(String message) {
        super(message);
    }

    public NegocioException(String message, Throwable cause) {
        super(message, cause);
    }
}
