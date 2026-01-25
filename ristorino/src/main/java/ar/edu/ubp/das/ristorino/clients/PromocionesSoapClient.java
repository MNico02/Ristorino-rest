package ar.edu.ubp.das.ristorino.clients;

import ar.edu.ubp.das.ristorino.beans.ContenidoBean;
import ar.edu.ubp.das.ristorino.soap.restaurante2.*;
import lombok.extern.slf4j.Slf4j;

import jakarta.xml.bind.JAXBElement;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
public class PromocionesSoapClient extends SoapClientBase
        implements PromocionesClient {

    public PromocionesSoapClient(String endpointUrl,
                                 String username,
                                 String password) {
        super(endpointUrl, username, password);
    }

    // =========================================================
    // OBTENER PROMOCIONES
    // =========================================================
    @Override
    public List<ContenidoBean> obtenerPromociones(int nroRestaurante) {

        try {
            // =========================
            // DATA
            // =========================
            ObtenerPromocionesReqBean data = new ObtenerPromocionesReqBean();
            data.setId(1); // SOAP siempre entiende 1

            ObtenerPromocionesRequest req = new ObtenerPromocionesRequest();
            req.setObtenerPromociones(data);

            // =========================
            // ROOT ELEMENT
            // =========================
            ObjectFactory factory = new ObjectFactory();
            JAXBElement<ObtenerPromocionesRequest> request =
                    factory.createObtenerPromocionesRequest(req);

            JAXBElement<ObtenerPromocionesResponse> response =
                    (JAXBElement<ObtenerPromocionesResponse>)
                            wsTemplate.marshalSendAndReceive(request);

            if (response == null
                    || response.getValue() == null
                    || response.getValue().getPromociones() == null) {
                log.warn("SOAP promociones vacío restaurante {}", nroRestaurante);
                return List.of();
            }

            // =========================
            // MAPEO A BEAN COMÚN
            // =========================
            return response.getValue().getPromociones()
                    .stream()
                    .map(p -> {
                        ContenidoBean cb = new ContenidoBean();
                        cb.setNroContenido(p.getNroContenido());
                        cb.setNroSucursal(p.getNroSucursal());
                        cb.setContenidoAPublicar(p.getContenidoAPublicar());
                        cb.setCostoClick(p.getCostoClick());
                        cb.setImagenAPublicar(p.getImagenAPublicar());
                        cb.setPublicado(p.isPublicado());
                        return cb;
                    })
                    .toList();

        } catch (Exception e) {
            log.error("Error SOAP obteniendo promociones restaurante {}: {}",
                    nroRestaurante, e.getMessage(), e);
            return List.of();
        }
    }

    // =========================================================
    // NOTIFICAR RESTAURANTE
    // =========================================================
    @Override
    public void notificarRestaurante(int nroRestaurante,
                                     BigDecimal costo,
                                     String contenidos) {

        try {
            // =========================
            // DATA
            // =========================
            NotiRestReqBean data = new NotiRestReqBean();
            data.setNroRestaurante(1); // SOAP siempre entiende 1
            data.setCostoAplicado(costo);
            data.setNroContenidos(contenidos);

            // =========================
            // REQUEST (WRAPPER)
            // =========================
            NotificarRestauranteRequest req =
                    new NotificarRestauranteRequest();
            req.setNotiRestReqBean(data);

            // =========================
            // ROOT ELEMENT
            // =========================
            ObjectFactory factory = new ObjectFactory();
            JAXBElement<NotificarRestauranteRequest> request =
                    factory.createNotificarRestauranteRequest(req);

            wsTemplate.marshalSendAndReceive(request);

            log.info("Notificación SOAP enviada restaurante {} (contenidos {})",
                    nroRestaurante, contenidos);

        } catch (Exception e) {
            log.error("Error SOAP notificando restaurante {}: {}",
                    nroRestaurante, e.getMessage(), e);
        }
    }

}