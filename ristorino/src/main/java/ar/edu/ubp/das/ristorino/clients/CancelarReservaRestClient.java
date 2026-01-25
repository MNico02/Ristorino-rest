package ar.edu.ubp.das.ristorino.clients;

import ar.edu.ubp.das.ristorino.clients.CancelarReservaClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
public class CancelarReservaRestClient implements CancelarReservaClient {

    private final String baseUrl;
    private final String token;
    private final RestTemplate restTemplate;

    public CancelarReservaRestClient(String baseUrl, String token) {
        this.baseUrl = baseUrl;
        this.token = token;

        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(15000);
        this.restTemplate = new RestTemplate(factory);
    }

    @Override
    public Map<String, Object> cancelarReserva(String codReservaSucursal) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = Map.of("codReservaSucursal", codReservaSucursal);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/cancelarReserva",
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }

            log.warn("Cancelar reserva respondió {}", response.getStatusCode());
            return Map.of(
                    "success", false,
                    "message", "Error en la respuesta del restaurante: " + response.getStatusCode()
            );

        } catch (Exception e) {
            log.error("Error REST cancelando reserva: {}", e.getMessage());
            return Map.of(
                    "success", false,
                    "message", "Error comunicándose con el restaurante: " + e.getMessage()
            );
        }
    }
}