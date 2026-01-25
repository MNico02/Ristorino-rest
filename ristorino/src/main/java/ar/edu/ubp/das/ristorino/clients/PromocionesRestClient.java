package ar.edu.ubp.das.ristorino.clients;

import ar.edu.ubp.das.ristorino.beans.ContenidoBean;
import ar.edu.ubp.das.ristorino.beans.NotiRestReqBean;
import ar.edu.ubp.das.ristorino.beans.UpdPublicarContenidosRespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;


@Slf4j
public class PromocionesRestClient implements PromocionesClient {

    private final String baseUrl;
    private final String token;
    private final RestTemplate restTemplate;

    public PromocionesRestClient(String baseUrl, String token) {
        this.baseUrl = baseUrl;
        this.token = token;

        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(15000);
        this.restTemplate = new RestTemplate(factory);
    }

    @Override
    public List<ContenidoBean> obtenerPromociones(int nroRestaurante) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<ContenidoBean[]> response =
                    restTemplate.exchange(
                            baseUrl + "/obtenerPromociones?id=" + nroRestaurante,
                            HttpMethod.GET,
                            request,
                            ContenidoBean[].class
                    );

            return response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null
                    ? List.of(response.getBody())
                    : List.of();

        } catch (Exception e) {
            log.error("Error obteniendo promociones REST {}: {}", nroRestaurante, e.getMessage());
            return List.of();
        }
    }

    @Override
    public void notificarRestaurante(
            int nroRestaurante,
            BigDecimal costoAplicado,
            String nroContenidos) {

        NotiRestReqBean req = new NotiRestReqBean();
        req.setNroRestaurante(nroRestaurante);
        req.setCostoAplicado(costoAplicado);
        req.setNroContenidos(nroContenidos);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<NotiRestReqBean> request =
                    new HttpEntity<>(req, headers);

            restTemplate.exchange(
                    baseUrl + "/notificarRestaurante",
                    HttpMethod.POST,
                    request,
                    UpdPublicarContenidosRespBean.class
            );

            log.info("Notificación enviada REST restaurante {}", nroRestaurante);

        } catch (Exception e) {
            log.error("Error notificando REST restaurante {}: {}", nroRestaurante, e.getMessage());
        }
    }
}
