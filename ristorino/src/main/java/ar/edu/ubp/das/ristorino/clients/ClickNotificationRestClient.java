package ar.edu.ubp.das.ristorino.clients;

import ar.edu.ubp.das.ristorino.beans.ClickNotiBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
public class ClickNotificationRestClient implements ClickNotificationClient {

    private final String baseUrl;
    private final String token;
    private final RestTemplate restTemplate;

    public ClickNotificationRestClient(String baseUrl, String token) {
        this.baseUrl = baseUrl;
        this.token = token;

        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(15000);
        this.restTemplate = new RestTemplate(factory);
    }

    @Override
    public boolean enviarClicks(List<ClickNotiBean> clicks) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            HttpEntity<List<ClickNotiBean>> request = new HttpEntity<>(clicks, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    baseUrl + "/registrarClicks",
                    request,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful()
                    && Boolean.TRUE.equals(response.getBody().get("success"))) {

                log.info("Clicks registrados correctamente via REST");
                return true;
            }

            log.warn("Clicks REST respondió: {}", response.getBody());
            return false;

        } catch (Exception e) {
            log.error("Error REST enviando clicks: {}", e.getMessage());
            return false;
        }
    }
}