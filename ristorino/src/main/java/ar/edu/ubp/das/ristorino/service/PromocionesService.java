package ar.edu.ubp.das.ristorino.service;

import ar.edu.ubp.das.ristorino.beans.ContenidoBean;
import ar.edu.ubp.das.ristorino.clients.PromocionesClient;
import ar.edu.ubp.das.ristorino.clients.PromocionesClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class PromocionesService {

    private final PromocionesClientFactory factory;

    public PromocionesService(PromocionesClientFactory factory) {
        this.factory = factory;
    }

    public List<ContenidoBean> obtenerPromociones(int nroRestaurante) {

        PromocionesClient client = factory.getClient(nroRestaurante);
        if (client == null) {
            log.warn("No hay cliente promociones para {}", nroRestaurante);
            return List.of();
        }

        return client.obtenerPromociones(nroRestaurante);
    }

    public void notificarRestaurante(
            int nroRestaurante,
            BigDecimal costoAplicado,
            String nroContenidos) {

        PromocionesClient client = factory.getClient(nroRestaurante);
        if (client == null) {
            log.warn("No hay cliente promociones para {}", nroRestaurante);
            return;
        }

        client.notificarRestaurante(nroRestaurante, costoAplicado, nroContenidos);
    }
}
