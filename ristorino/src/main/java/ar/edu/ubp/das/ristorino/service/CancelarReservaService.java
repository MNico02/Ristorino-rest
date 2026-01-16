package ar.edu.ubp.das.ristorino.service;


import ar.edu.ubp.das.ristorino.beans.CancelarReservaBean;
import ar.edu.ubp.das.ristorino.repositories.RistorinoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CancelarReservaService {
    @Autowired
    private RistorinoRepository ristorinoRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final Map<Integer, String> BASE_URLS = Map.of(
            1, "http://localhost:8085/api/v1/restaurante1",
            2, "http://localhost:8086/api/v1/restaurante2"
    );

    private static final Map<Integer, String> TOKENS = Map.of(
            1, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyZXN0YXVyYW50ZTEiLCJuYW1lIjoiR3J1cG9kYXNGR00iLCJyb2xlIjoiYWRtaW4iLCJpYXQiOjE3MzAxMzQ4MDB9.iy_l8J91bSB3R2Bwe2-ywrndUaWV2QYJU13V1CgK0F0",
            2, "TOKEN_RESTAURANTE_2"
    );


    public Map<String, Object> cancelarReserva(CancelarReservaBean req) {

        Map<String, Object> resp = new HashMap<>();

        Integer nroRestaurante = req.getNroRestaurante();
        String codReservaSucursal = req.getCodReservaSucursal();

        if (nroRestaurante == null || codReservaSucursal == null || codReservaSucursal.isBlank()) {
            resp.put("success", false);
            resp.put("message", "Faltan datos: nroRestaurante y codReservaSucursal son obligatorios.");
            return resp;
        }

        String baseUrl = BASE_URLS.get(nroRestaurante);
        String token = TOKENS.get(nroRestaurante);

        if (baseUrl == null || token == null) {
            resp.put("success", false);
            resp.put("message", "Restaurante no configurado: " + nroRestaurante);
            return resp;
        }

        try {
            // 1) Llamar al restaurante para que cancele primero
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> bodyRest = Map.of("codReservaSucursal", codReservaSucursal);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(bodyRest, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/cancelarReserva",
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            Map<String, Object> rtaRest = response.getBody() != null ? response.getBody() : Map.of();
            boolean okRest = Boolean.TRUE.equals(rtaRest.get("success"));
            String statusRest = String.valueOf(rtaRest.getOrDefault("status", "UNKNOWN"));
            String msgRest = String.valueOf(rtaRest.getOrDefault("message", "Sin mensaje."));

            if (!okRest) {
                resp.put("success", false);
                resp.put("status", statusRest);
                resp.put("message", msgRest);
                return resp;
            }

            // 2) Reflejar en Ristorino (SP) usando el repository
            Map<String, Object> rtaRistorino = ristorinoRepository.cancelarReservaRistorinoPorCodigo(codReservaSucursal);

            boolean okRis = Boolean.TRUE.equals(rtaRistorino.get("success"));
            String statusRis = String.valueOf(rtaRistorino.getOrDefault("status", "UNKNOWN"));
            String msgRis = String.valueOf(rtaRistorino.getOrDefault("message", "Sin mensaje."));

            if (!okRis) {
                // Restaurante canceló, pero Ristorino no pudo reflejar.
                // No lo ocultes: devolvé info clara para debug / reintentos.
                resp.put("success", false);
                resp.put("status", "PARTIAL_FAILURE");
                resp.put("message", "El restaurante canceló, pero Ristorino no pudo reflejar la cancelación.");
                resp.put("restaurante", rtaRest);
                resp.put("ristorino", rtaRistorino);
                return resp;
            }

            // OK total
            resp.put("success", true);
            resp.put("status", statusRest); // CANCELLED o ALREADY_CANCELLED
            resp.put("message", "Reserva cancelada correctamente en restaurante y reflejada en Ristorino.");
            resp.put("restaurante", rtaRest);
            resp.put("ristorino", rtaRistorino);
            return resp;

        } catch (Exception e) {
            log.error("Error cancelando reserva en restaurante {}: {}", nroRestaurante, e.getMessage());
            resp.put("success", false);
            resp.put("message", "Error comunicándose con el restaurante: " + e.getMessage());
            return resp;
        }
    }
}