package ar.edu.ubp.das.ristorino.service;

import ar.edu.ubp.das.ristorino.beans.ClickNotiBean;
import ar.edu.ubp.das.ristorino.beans.ResponseBean;
import ar.edu.ubp.das.ristorino.clients.RestauranteClient;
import ar.edu.ubp.das.ristorino.clients.RestauranteClientFactory;
import jakarta.xml.ws.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ClickNotificationService {

    private final RestauranteClientFactory clientFactory;

    public ClickNotificationService(RestauranteClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    public ResponseBean enviarClicksPorRestaurante(int nroRestaurante, List<ClickNotiBean> clicks) {

        if (clicks == null || clicks.isEmpty()) {
            log.warn("No hay clicks para enviar al restaurante {}", nroRestaurante);
            ResponseBean resp = new ResponseBean();
            resp.setSuccess(false);
            return resp;
        }

        try {
            // Obtener cliente según el restaurante
            RestauranteClient client = clientFactory.getClient(nroRestaurante);

            // Enviar clicks
            ResponseBean resultado = client.enviarClicks(clicks);

            if (resultado.isSuccess()) {
                log.info("Clicks registrados correctamente en restaurante {}", nroRestaurante);
            } else {
                log.warn("El restaurante {} no pudo registrar los clicks", nroRestaurante);
            }

            return resultado;

        } catch (IllegalArgumentException e) {
            log.warn("No se encontró configuración para el restaurante {}", nroRestaurante);
            ResponseBean resp = new ResponseBean();
            resp.setSuccess(false);
            return resp;

        } catch (Exception e) {
            log.error("No se pudieron enviar los clicks al restaurante {}: {}",
                    nroRestaurante, e.getMessage());
            ResponseBean resp = new ResponseBean();
            resp.setSuccess(false);
            return resp;
        }
    }
}