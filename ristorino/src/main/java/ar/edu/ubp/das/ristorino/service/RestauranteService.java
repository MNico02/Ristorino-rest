package ar.edu.ubp.das.ristorino.service;

import ar.edu.ubp.das.ristorino.beans.ContenidoBean;
import ar.edu.ubp.das.ristorino.beans.RestauranteBean;
import ar.edu.ubp.das.ristorino.beans.SyncRestauranteBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RestauranteService {

    private final RestTemplate restTemplate;

    public RestauranteService() {
        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(15000);
        this.restTemplate = new RestTemplate(factory);
    }

    private static final Map<Integer, String> URLS = Map.of(
            1, "http://localhost:8085/api/v1/restaurante1/restaurante"
    );

    private static final Map<Integer, String> TOKENS = Map.of(
            1, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyZXN0YXVyYW50ZTEiLCJuYW1lIjoiR3J1cG9kYXNGR00iLCJyb2xlIjoiYWRtaW4iLCJpYXQiOjE3MzAxMzQ4MDB9.iy_l8J91bSB3R2Bwe2-ywrndUaWV2QYJU13V1CgK0F0"
    );

    public SyncRestauranteBean obtenerRestaurante(int nroRestaurante) {
        String baseUrl = URLS.get(nroRestaurante);
        String token = TOKENS.get(nroRestaurante);

        if (baseUrl == null || token == null) {
            log.warn("No hay configuración para el restaurante {}", nroRestaurante);
            return null;
        }

        try {
            String url = org.springframework.web.util.UriComponentsBuilder
                    .fromHttpUrl(baseUrl)
                    .queryParam("id", nroRestaurante)
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<SyncRestauranteBean> response =
                    restTemplate.exchange(url, HttpMethod.GET, request, SyncRestauranteBean.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }

            log.warn("Restaurante {} respondió {} {}", nroRestaurante,
                    response.getStatusCodeValue(), response.getStatusCode());

        } catch (Exception e) {
            log.error("Error obteniendo restaurante {}: {}", nroRestaurante, e.getMessage());
        }

        return null;
    }
}
