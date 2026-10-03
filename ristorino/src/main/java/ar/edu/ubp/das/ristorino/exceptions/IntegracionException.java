package ar.edu.ubp.das.ristorino.exceptions;

/** Falló un servicio externo (restaurante caído, timeout, SOAP Fault, respuesta ilegible). El mensaje va al log, no al usuario. */
public class IntegracionException extends RuntimeException {

    public IntegracionException(String message) {
        super(message);
    }

    public IntegracionException(String message, Throwable cause) {
        super(message, cause);
    }
}