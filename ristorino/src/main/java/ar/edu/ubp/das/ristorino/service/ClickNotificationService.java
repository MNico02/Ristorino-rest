package ar.edu.ubp.das.ristorino.service;

import ar.edu.ubp.das.ristorino.beans.ClickNotiBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ClickNotificationService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final Map<Integer, String> URLS = Map.of(
            1, "http://localhost:8085/api/v1/restaurante1/registrarClicks"
            //2, "http://localhost:8086/api/v1/restaurante2/registrarClicks",
            //3, "http://localhost:8087/api/v1/restaurante3/registrarClicks",
            //4, "http://localhost:8088/api/v1/restaurante4/registrarClicks"
    );

    private static final Map<Integer, String> TOKENS = Map.of(
            1, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyZXN0YXVyYW50ZTEiLCJuYW1lIjoiR3J1cG9kYXNGR00iLCJyb2xlIjoiYWRtaW4iLCJpYXQiOjE3MzAxMzQ4MDB9.iy_l8J91bSB3R2Bwe2-ywrndUaWV2QYJU13V1CgK0F0"

    );

    public boolean enviarClicksPorRestaurante(int nroRestaurante, List<ClickNotiBean> clicks) {
        String url = URLS.get(nroRestaurante);
        String token = TOKENS.get(nroRestaurante);

        if (url == null || token == null) {
            log.warn("No se encontró configuración para el restaurante {}", nroRestaurante);
            return false;
        }

        try {


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            HttpEntity<List<ClickNotiBean>> request = new HttpEntity<>(clicks, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful()
                    && Boolean.TRUE.equals(response.getBody().get("success"))) {

                log.info("Clicks registrados correctamente en restaurante {}", nroRestaurante);
                return true;
            } else {
                log.warn("El restaurante {} respondió: {}", nroRestaurante, response.getBody());
                return false;
            }

        } catch (Exception e) {
            log.error("No se pudieron enviar los clics al restaurante {}: {}", nroRestaurante, e.getMessage());
            return false;
        }
    }
}