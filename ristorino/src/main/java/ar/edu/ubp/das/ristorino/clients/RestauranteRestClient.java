package ar.edu.ubp.das.ristorino.clients;

import ar.edu.ubp.das.ristorino.beans.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class RestauranteRestClient implements RestauranteClient {
    private final Gson gson = new Gson();
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
    public SyncRestauranteBean obtenerRestaurante() {

        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(baseUrl + "/restaurante")
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


                return sync;
            }

          //  log.warn("Restaurante {} respondió {}", nroRestaurante, response.getStatusCode());

        } catch (Exception e) {
            log.error("Error REST restaurante : {}", e.getMessage());
        }

        return null;
    }

    @Override
    public ResponseBean enviarClicks(List<ClickNotiBean> clicks) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            String jsonClicks = gson.toJson(clicks);
            HttpEntity<String> request = new HttpEntity<>(jsonClicks, headers);

            ResponseEntity<ResponseBean> response = restTemplate.postForEntity(
                    baseUrl + "/registrarClicks",
                    request,
                    ResponseBean.class
            );

            if (response.getStatusCode().is2xxSuccessful()
                    && Boolean.TRUE.equals(response.getBody().isSuccess())) {

                log.info("Clicks registrados correctamente via REST");
                return response.getBody();
            }

            log.warn("Clicks REST respondió: {}", response.getBody());
            ResponseBean resp = new ResponseBean();
            resp.setSuccess(Boolean.FALSE);
            return resp;

        } catch (Exception e) {
            log.error("Error REST enviando clicks: {}", e.getMessage());
            ResponseBean resp = new ResponseBean();
            resp.setSuccess(Boolean.FALSE);
            return resp;
        }
    }

    @Override
    public ConfirmarReservaResponseBean confirmarReserva(ReservaRestauranteBean payload) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);
            String json = gson.toJson(payload);
            HttpEntity<String> request = new HttpEntity<>(json, headers);

            ResponseEntity<ConfirmarReservaResponseBean> response =
                    restTemplate.exchange(
                            baseUrl + "/confirmarReserva",
                            HttpMethod.POST,
                            request,
                            ConfirmarReservaResponseBean.class
                    );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.warn("REST confirmarReserva restaurante  -> {}", response.getStatusCode());
                return null;
            }

            return response.getBody();

        } catch (Exception e) {
            log.error("Error REST confirmarReserva restaurante : {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public ResponseBean cancelarReserva(String codReservaSucursal) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String json = gson.toJson(Map.of(
                    "codReservaSucursal", codReservaSucursal
            ));
            HttpEntity<String> request = new HttpEntity<>(json, headers);

            ResponseEntity<ResponseBean> response = restTemplate.exchange(
                    baseUrl + "/cancelarReserva",
                    HttpMethod.POST,
                    request,
                    ResponseBean.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }

            log.warn("Cancelar reserva respondió {}", response.getStatusCode());
            response.getBody().setSuccess(Boolean.FALSE);
            response.getBody().setMessage("Error en la respuesta del restaurante: " + response.getStatusCode());
            return response.getBody();
        } catch (Exception e) {
            log.error("Error REST cancelando reserva: {}", e.getMessage());
            ResponseBean resp = new ResponseBean();
            resp.setSuccess(Boolean.FALSE);
            resp.setMessage("Error comunicándose con el restaurante: "  + e.getMessage());
            return resp;
        }
    }

    @Override
    public ResponseBean modificarReserva(ModificarReservaReqBean req) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String json = gson.toJson(req);
            HttpEntity<String> request = new HttpEntity<>(json, headers);

            ResponseEntity<ResponseBean> response = restTemplate.exchange(
                    baseUrl + "/modificarReserva",
                    HttpMethod.POST,
                    request,
                    ResponseBean.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }

            log.warn("Modificar reserva REST respondió {}", response.getStatusCode());
            response.getBody().setSuccess(Boolean.FALSE);
            response.getBody().setStatus("ERROR");
            response.getBody().setMessage("Error en la respuesta del restaurante: " + response.getStatusCode());
            return response.getBody();
        } catch (Exception e) {
            log.error("Error REST modificando reserva: {}", e.getMessage());
            ResponseBean resp = new ResponseBean();
            resp.setSuccess(Boolean.FALSE);
            resp.setStatus("ERROR");
            resp.setMessage("Error comunicándose con el restaurante: "  + e.getMessage());
            return resp;
        }
    }

    @Override
    public List<HorarioBean> obtenerDisponibilidad(SoliHorarioBean soli) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String json = gson.toJson(soli);
            System.out.println("el json tiene en ristorino: " + json);
            HttpEntity<String> request = new HttpEntity<>(json, headers);

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

    @Override
    public List<ContenidoBean> obtenerPromociones() {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<ContenidoBean[]> response =
                    restTemplate.exchange(
                            baseUrl + "/obtenerPromociones",
                            HttpMethod.GET,
                            request,
                            ContenidoBean[].class
                    );

            return response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null
                    ? List.of(response.getBody())
                    : List.of();

        } catch (Exception e) {
            log.error("Error obteniendo promociones REST : {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public void notificarRestaurante(BigDecimal costoAplicado, String nroContenidos) {
        try {
            String json = gson.toJson(Map.of(
                    "costoAplicado", costoAplicado,
                    "nroContenidos", nroContenidos
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(json, headers);

            restTemplate.exchange(
                    baseUrl + "/notificarRestaurante",
                    HttpMethod.POST,
                    request,
                    UpdPublicarContenidosRespBean.class
            );



        } catch (Exception e) {
            log.error("Error notificando REST restaurante : {}", e.getMessage());
        }
    }

}
