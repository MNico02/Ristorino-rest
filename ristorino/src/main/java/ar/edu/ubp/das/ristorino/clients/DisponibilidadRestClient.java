package ar.edu.ubp.das.ristorino.clients;

import ar.edu.ubp.das.ristorino.beans.HorarioBean;
import ar.edu.ubp.das.ristorino.beans.SoliHorarioBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
public class DisponibilidadRestClient implements DisponibilidadClient {

    private final String baseUrl;
    private final String token;
    private final RestTemplate restTemplate;

    public DisponibilidadRestClient(String baseUrl, String token) {
        this.baseUrl = baseUrl;
        this.token = token;

        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(15000);
        this.restTemplate = new RestTemplate(factory);
    }

    @Override
    public List<HorarioBean> obtenerDisponibilidad(SoliHorarioBean soli) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<SoliHorarioBean> request = new HttpEntity<>(soli, headers);

            ResponseEntity<HorarioBean[]> response =
                    restTemplate.exchange(
                            baseUrl + "/consultarDisponibilidad",
                            HttpMethod.POST,
                            request,
                            HorarioBean[].class
                    );

            return response.getStatusCode().is2xxSuccessful() && response.getBody() != null
                    ? List.of(response.getBody())
                    : List.of();

        } catch (Exception e) {
            log.error("Error REST disponibilidad: {}", e.getMessage(), e);
            return List.of();
        }
    }
}
