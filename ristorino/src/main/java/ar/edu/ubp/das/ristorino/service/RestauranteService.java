package ar.edu.ubp.das.ristorino.service;

import ar.edu.ubp.das.ristorino.beans.ContenidoBean;
import ar.edu.ubp.das.ristorino.beans.RestauranteBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RestauranteService {
    private final RestTemplate restTemplate = new RestTemplate();

    private static final Map<Integer, String> URLS = Map.of(
            1, "http://localhost:8085/api/v1/restaurante1/restaurante"
            // 2, ...
    );

    private static final Map<Integer, String> TOKENS = Map.of(
            1, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyZXN0YXVyYW50ZTEiLCJuYW1lIjoiR3J1cG9kYXNGR00iLCJyb2xlIjoiYWRtaW4iLCJpYXQiOjE3MzAxMzQ4MDB9.iy_l8J91bSB3R2Bwe2-ywrndUaWV2QYJU13V1CgK0F0"
    );

    public RestauranteBean obtenerRestaurante(int nroRestaurante) {
        String url = URLS.get(nroRestaurante);
        String token = TOKENS.get(nroRestaurante);

        if (url == null || token == null) {
            log.warn("No hay configuración para el restaurante {}", nroRestaurante);
            return null;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<RestauranteBean> response =
                    restTemplate.exchange(
                            url + "?id=" + nroRestaurante,
                            HttpMethod.GET,
                            request,
                            RestauranteBean.class
                    );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Información del restaurante {} obtenida correctamente", nroRestaurante);
                return response.getBody();
            }

        } catch (Exception e) {
            log.error("Error obteniendo restaurante {}: {}", nroRestaurante, e.getMessage());
        }

        return null;
    }
}
