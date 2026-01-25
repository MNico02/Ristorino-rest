package ar.edu.ubp.das.ristorino.clients;

import ar.edu.ubp.das.ristorino.beans.SyncRestauranteBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
public class RestauranteRestClient implements RestauranteClient {

    private final String baseUrl;
    private final String token;
    private final RestTemplate restTemplate;

    public RestauranteRestClient(String baseUrl, String token) {
        this.baseUrl = baseUrl;
        this.token = token;

        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(15000);
        this.restTemplate = new RestTemplate(factory);
    }

    @Override
    public SyncRestauranteBean obtenerRestaurante(int nroRestaurante) {

        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(baseUrl)
                    .queryParam("id", 1)
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<SyncRestauranteBean> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            request,
                            SyncRestauranteBean.class
                    );

            if (response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null) {

                SyncRestauranteBean sync = response.getBody();

                sync.setNroRestaurante(nroRestaurante);

                return sync;
            }

            log.warn("Restaurante {} respondió {}", nroRestaurante, response.getStatusCode());

        } catch (Exception e) {
            log.error("Error REST restaurante {}: {}", nroRestaurante, e.getMessage());
        }

        return null;
    }
}
