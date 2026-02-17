package ar.edu.ubp.das.ristorino.service;

import ar.edu.ubp.das.ristorino.beans.ContenidoBean;
import ar.edu.ubp.das.ristorino.clients.RestauranteClient;
import ar.edu.ubp.das.ristorino.clients.RestauranteClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class PromocionesService {

    private final RestauranteClientFactory factory;

    public PromocionesService(RestauranteClientFactory factory) {
        this.factory = factory;
    }

    public List<ContenidoBean> obtenerPromociones(int nroRestaurante) {

        RestauranteClient client = factory.getClient(nroRestaurante);
        if (client == null) {
            log.warn("No hay cliente promociones para {}", nroRestaurante);
            return List.of();
        }

        return client.obtenerPromociones();
    }

    public void notificarRestaurante(
            int nroRestaurante,
            BigDecimal costoAplicado,
            String nroContenidos) {

        RestauranteClient client = factory.getClient(nroRestaurante);
        if (client == null) {
            log.warn("No hay cliente promociones para {}", nroRestaurante);
            return;
        }

        client.notificarRestaurante(costoAplicado, nroContenidos);
        log.info("Notificación enviada REST restaurante {}", nroRestaurante);
    }
}
