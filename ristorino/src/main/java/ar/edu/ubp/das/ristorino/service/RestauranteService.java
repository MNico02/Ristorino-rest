package ar.edu.ubp.das.ristorino.service;

import ar.edu.ubp.das.ristorino.beans.SyncRestauranteBean;
import ar.edu.ubp.das.ristorino.clients.RestauranteClient;
import ar.edu.ubp.das.ristorino.clients.RestauranteClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RestauranteService {

    private final RestauranteClientFactory clientFactory;

    public RestauranteService(RestauranteClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    public SyncRestauranteBean obtenerRestaurante(int nroRestaurante) {

        RestauranteClient client =   clientFactory.getClient(nroRestaurante);

        if (client == null) {
            log.warn("No hay cliente configurado para restaurante {}", nroRestaurante);
            return null;
        }
        return client.obtenerRestaurante(nroRestaurante);
    }
}
