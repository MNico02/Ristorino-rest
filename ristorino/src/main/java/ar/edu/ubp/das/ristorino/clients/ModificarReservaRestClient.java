package ar.edu.ubp.das.ristorino.clients;

import ar.edu.ubp.das.ristorino.beans.ModificarReservaReqBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ModificarReservaRestClient implements ModificarReservaClient {

    private final String baseUrl;
    private final String token;
    private final RestTemplate restTemplate;

    public ModificarReservaRestClient(String baseUrl, String token) {
        this.baseUrl = baseUrl;
        this.token = token;

        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(15000);
        this.restTemplate = new RestTemplate(factory);
    }

    @Override
    public Map<String, Object> modificarReserva(ModificarReservaReqBean req) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("codReservaSucursal", req.getCodReservaSucursal());
            body.put("fechaReserva", req.getFechaReserva());   // "YYYY-MM-DD"
            body.put("horaReserva", req.getHoraReserva());     // "HH:mm:ss"
            body.put("codZona", req.getCodZona());
            body.put("cantAdultos", req.getCantAdultos());
            body.put("cantMenores", req.getCantMenores());
            body.put("costoReserva", req.getCostoReserva());

            log.info("BODY REST modificar reserva: {}", body);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/modificarReserva",
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }

            log.warn("Modificar reserva REST respondió {}", response.getStatusCode());
            return Map.of(
                    "success", false,
                    "status", "ERROR",
                    "message", "Error en la respuesta del restaurante: " + response.getStatusCode()
            );

        } catch (Exception e) {
            log.error("Error REST modificando reserva: {}", e.getMessage());
            return Map.of(
                    "success", false,
                    "status", "ERROR",
                    "message", "Error comunicándose con el restaurante: " + e.getMessage()
            );
        }
    }
}