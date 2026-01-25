package ar.edu.ubp.das.ristorino.clients;

import ar.edu.ubp.das.ristorino.beans.SyncRestauranteBean;

public interface RestauranteClient {
    SyncRestauranteBean obtenerRestaurante(int nroRestaurante);
}
