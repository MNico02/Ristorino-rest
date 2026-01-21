package ar.edu.ubp.das.ristorino.service;

import ar.edu.ubp.das.ristorino.beans.ModificarReservaReqBean;
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
public class ModificarReservaService {

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

    public Map<String, Object> modificarReserva(ModificarReservaReqBean req) {

        Map<String, Object> resp = new HashMap<>();

        // 1) Validaciones mínimas
        Integer nroRestaurante = req.getNroRestaurante();
        String codReservaSucursal = req.getCodReservaSucursal();

        if (nroRestaurante == null || codReservaSucursal == null || codReservaSucursal.isBlank()) {
            resp.put("success", false);
            resp.put("status", "INVALID");
            resp.put("message", "Faltan datos: nroRestaurante y codReservaSucursal son obligatorios.");
            return resp;
        }




        String baseUrl = BASE_URLS.get(nroRestaurante);
        String token = TOKENS.get(nroRestaurante);

        if (baseUrl == null || token == null) {
            resp.put("success", false);
            resp.put("status", "NOT_CONFIGURED");
            resp.put("message", "Restaurante no configurado: " + nroRestaurante);
            return resp;
        }

        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);


            Map<String, Object> bodyRest = new HashMap<>();
            bodyRest.put("codReservaSucursal", codReservaSucursal);
            bodyRest.put("fechaReserva", req.getFechaReserva());   // "YYYY-MM-DD"
            bodyRest.put("horaReserva", req.getHoraReserva());     // "HH:mm:ss"
            bodyRest.put("codZona", req.getCodZona());
            bodyRest.put("cantAdultos", req.getCantAdultos());
            bodyRest.put("cantMenores", req.getCantMenores());
            bodyRest.put("costoReserva", req.getCostoReserva());
            log.info("BODY a restaurante {}: {}", nroRestaurante, bodyRest);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(bodyRest, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/modificarReserva",
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            Map<String, Object> rtaRest = response.getBody() != null ? response.getBody() : Map.of();
            boolean okRest = Boolean.TRUE.equals(rtaRest.get("success"));
            String statusRest = String.valueOf(rtaRest.getOrDefault("status", "UNKNOWN"));
            String msgRest = String.valueOf(rtaRest.getOrDefault("message", "Sin mensaje."));

            if (!okRest) {
                // Restaurante no pudo modificar
                resp.put("success", false);
                resp.put("status", statusRest);
                resp.put("message", msgRest);
                resp.put("restaurante", rtaRest);
                return resp;
            }

            // 3) Reflejar modificación en Ristorino (SP) usando repository
            // 👉 necesitás implementar este método en el repo para llamar a tu SP de Ristorino
            Map<String, Object> rtaRistorino = ristorinoRepository.modificarReservaRistorino(req);

            boolean okRis = Boolean.TRUE.equals(rtaRistorino.get("success"));
            String statusRis = String.valueOf(rtaRistorino.getOrDefault("status", "UNKNOWN"));
            String msgRis = String.valueOf(rtaRistorino.getOrDefault("message", "Sin mensaje."));

            if (!okRis) {
                // Restaurante modificó, pero Ristorino no reflejó
                resp.put("success", false);
                resp.put("status", "PARTIAL_FAILURE");
                resp.put("message", "El restaurante modificó la reserva, pero Ristorino no pudo reflejar el cambio.");
                resp.put("restaurante", rtaRest);
                resp.put("ristorino", rtaRistorino);
                return resp;
            }

            // OK total
            resp.put("success", true);
            resp.put("status", statusRest); // UPDATED o lo que devuelva el restaurante
            resp.put("message", "Reserva modificada correctamente en restaurante y reflejada en Ristorino.");
            resp.put("restaurante", rtaRest);
            resp.put("ristorino", rtaRistorino);
            return resp;

        } catch (Exception e) {
            log.error("Error modificando reserva en restaurante {}: {}", nroRestaurante, e.getMessage());
            resp.put("success", false);
            resp.put("status", "ERROR");
            resp.put("message", "Error comunicándose con el restaurante: " + e.getMessage());
            return resp;
        }
    }
}
