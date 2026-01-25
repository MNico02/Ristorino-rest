package ar.edu.ubp.das.ristorino.service;

import ar.edu.ubp.das.ristorino.beans.ClickNotiBean;
import ar.edu.ubp.das.ristorino.clients.ClickNotificationClient;
import ar.edu.ubp.das.ristorino.clients.ClickNotificationClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ClickNotificationService {

    private final ClickNotificationClientFactory clientFactory;

    public ClickNotificationService(ClickNotificationClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    public boolean enviarClicksPorRestaurante(int nroRestaurante, List<ClickNotiBean> clicks) {

        if (clicks == null || clicks.isEmpty()) {
            log.warn("No hay clicks para enviar al restaurante {}", nroRestaurante);
            return false;
        }

        try {
            // Obtener cliente según el restaurante
            ClickNotificationClient client = clientFactory.getClient(nroRestaurante);

            // Enviar clicks
            boolean resultado = client.enviarClicks(clicks);

            if (resultado) {
                log.info("Clicks registrados correctamente en restaurante {}", nroRestaurante);
            } else {
                log.warn("El restaurante {} no pudo registrar los clicks", nroRestaurante);
            }

            return resultado;

        } catch (IllegalArgumentException e) {
            log.warn("No se encontró configuración para el restaurante {}", nroRestaurante);
            return false;

        } catch (Exception e) {
            log.error("No se pudieron enviar los clicks al restaurante {}: {}",
                    nroRestaurante, e.getMessage());
            return false;
        }
    }
}