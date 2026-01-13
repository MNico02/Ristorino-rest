package ar.edu.ubp.das.ristorino.service;

import ar.edu.ubp.das.ristorino.beans.HorarioBean;
import ar.edu.ubp.das.ristorino.beans.SoliHorarioBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DisponibilidadService {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final Map<Integer, String> BASE_URLS = Map.of(
            1, "http://localhost:8085/api/v1/restaurante1",
            2, "http://localhost:8086/api/v1/restaurante2"
    );

    private static final Map<Integer, String> TOKENS = Map.of(
            1, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyZXN0YXVyYW50ZTEiLCJuYW1lIjoiR3J1cG9kYXNGR00iLCJyb2xlIjoiYWRtaW4iLCJpYXQiOjE3MzAxMzQ4MDB9.iy_l8J91bSB3R2Bwe2-ywrndUaWV2QYJU13V1CgK0F0",
            2, ""
    );

    public List<HorarioBean> obtenerDisponibilidad(SoliHorarioBean soli) {



        int nroRestaurante = resolverRestaurante(soli.getCodSucursalRestaurante());

        String baseUrl = BASE_URLS.get(nroRestaurante);
        String token = TOKENS.get(nroRestaurante);

        if (baseUrl == null || token == null) {
            log.warn("Restaurante {} no configurado", nroRestaurante);
            return List.of();
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<SoliHorarioBean> request =
                    new HttpEntity<>(soli, headers);

            ResponseEntity<HorarioBean[]> response =
                    restTemplate.exchange(
                            baseUrl + "/consultarDisponibilidad",
                            HttpMethod.POST,
                            request,
                            HorarioBean[].class
                    );

            return response.getBody() != null
                    ? List.of(response.getBody())
                    : List.of();

        } catch (Exception e) {
            log.error("Error consultando disponibilidad restaurante {}: {}",
                    nroRestaurante, e.getMessage());
            return List.of();
        }
    }
    private int resolverRestaurante(String codigo) {

        if (codigo == null || !codigo.contains("-")) {
            throw new IllegalArgumentException(
                    "Código restaurante-sucursal inválido: " + codigo
            );
        }

        try {
            String[] partes = codigo.split("-");
            return Integer.parseInt(partes[0]);

        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "No se pudo resolver el restaurante desde el código: " + codigo
            );
        }
    }

}
