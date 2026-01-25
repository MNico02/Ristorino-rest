package ar.edu.ubp.das.ristorino.clients;

import ar.edu.ubp.das.ristorino.beans.ConfirmarReservaResponseBean;
import ar.edu.ubp.das.ristorino.beans.ReservaRestauranteBean;

public interface ReservaClient {
    ConfirmarReservaResponseBean confirmarReserva(ReservaRestauranteBean payload, int nroRestaurante);
}
