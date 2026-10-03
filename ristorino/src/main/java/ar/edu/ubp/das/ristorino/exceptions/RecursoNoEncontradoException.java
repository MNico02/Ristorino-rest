package ar.edu.ubp.das.ristorino.exceptions;

/** El recurso pedido no existe (restaurante, cliente, reserva). Mensaje apto para el usuario. */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String message) {
        super(message);
    }
}
