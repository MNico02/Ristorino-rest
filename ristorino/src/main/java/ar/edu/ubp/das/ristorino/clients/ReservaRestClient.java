package ar.edu.ubp.das.ristorino.clients;

import ar.edu.ubp.das.ristorino.beans.ConfirmarReservaResponseBean;
import ar.edu.ubp.das.ristorino.beans.ReservaRestauranteBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class ReservaRestClient implements ReservaClient {

    private final String baseUrl;
    private final String token;
    private final RestTemplate restTemplate;

    public ReservaRestClient(String baseUrl, String token) {
        this.baseUrl = baseUrl;
        this.token = token;

        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(15000);
        this.restTemplate = new RestTemplate(factory);
    }

    @Override
    public ConfirmarReservaResponseBean confirmarReserva(ReservaRestauranteBean payload, int nroRestaurante) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<ReservaRestauranteBean> request = new HttpEntity<>(payload, headers);

            ResponseEntity<ConfirmarReservaResponseBean> response =
                    restTemplate.exchange(
                            baseUrl + "/confirmarReserva",
                            HttpMethod.POST,
                            request,
                            ConfirmarReservaResponseBean.class
                    );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.warn("REST confirmarReserva restaurante {} -> {}", nroRestaurante, response.getStatusCode());
                return null;
            }

            return response.getBody();

        } catch (Exception e) {
            log.error("Error REST confirmarReserva restaurante {}: {}", nroRestaurante, e.getMessage(), e);
            return null;
        }
    }
}
