package ar.edu.ubp.das.ristorino.service;


import ar.edu.ubp.das.ristorino.beans.ContenidoBean;
import ar.edu.ubp.das.ristorino.beans.NotiRestReqBean;
import ar.edu.ubp.das.ristorino.beans.UpdPublicarContenidosRespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class PromocionesService {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final Map<Integer, String> URLS = Map.of(
            1, "http://localhost:8085/api/v1/restaurante1"
            // 2, ...
    );

    private static final Map<Integer, String> TOKENS = Map.of(
            1, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyZXN0YXVyYW50ZTEiLCJuYW1lIjoiR3J1cG9kYXNGR00iLCJyb2xlIjoiYWRtaW4iLCJpYXQiOjE3MzAxMzQ4MDB9.iy_l8J91bSB3R2Bwe2-ywrndUaWV2QYJU13V1CgK0F0"
    );

    public List<ContenidoBean> obtenerPromociones(int nroRestaurante) {

        String url = URLS.get(nroRestaurante);
        String token = TOKENS.get(nroRestaurante);

        if (url == null || token == null) {
            log.warn("No hay configuración para el restaurante {}", nroRestaurante);
            return List.of();
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<ContenidoBean[]> response =
                    restTemplate.exchange(
                            url + "/obtenerPromociones?id=" + nroRestaurante,
                            HttpMethod.GET,
                            request,
                            ContenidoBean[].class
                    );

            if (response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null) {

                log.info("Se obtuvieron {} promociones del restaurante {}",
                        response.getBody().length, nroRestaurante);

                return List.of(response.getBody());
            }

        } catch (Exception e) {
            log.error("Error obteniendo promociones del restaurante {}: {}",
                    nroRestaurante, e.getMessage());
        }

        return List.of();
    }

    public void notifiarRestaurante(int nroRestaurante, BigDecimal costoAplicado, String nroContenidos) {
        NotiRestReqBean req = new NotiRestReqBean();
        req.setNroRestaurante(nroRestaurante);
        req.setCostoAplicado(costoAplicado);
        req.setNroContenidos(nroContenidos);
        String url = URLS.get(nroRestaurante);
        String token = TOKENS.get(nroRestaurante);

        if (url == null || token == null) {
            log.warn("No hay configuración para el restaurante {}", nroRestaurante);
            return;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<NotiRestReqBean> request =
                    new HttpEntity<>(req, headers);

            ResponseEntity<UpdPublicarContenidosRespBean> response =
                    restTemplate.exchange(
                            url + "/notificarRestaurante",
                            HttpMethod.POST,
                            request,
                            UpdPublicarContenidosRespBean.class
                    );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Notificación enviada al restaurante {}. Contenidos: {}. Costo: {}",
                        nroRestaurante, nroContenidos, costoAplicado);
                log.info("SP upd_publicar_contenidos_lote -> resultado: {}, actualizados: {}/{}",
                        Objects.requireNonNull(response.getBody()).getResultado(),
                        response.getBody().getRegistrosActualizados(),
                        response.getBody().getRegistrosSolicitados());
            } else {
                log.warn("Respuesta no OK al notificar restaurante {}: {}",
                        nroRestaurante, response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Error notificando promociones al restaurante {}: {}",
                    nroRestaurante, e.getMessage(), e);
        }
    }
}
