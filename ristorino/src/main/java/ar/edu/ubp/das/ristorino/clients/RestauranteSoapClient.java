package ar.edu.ubp.das.ristorino.clients;



import ar.edu.ubp.das.ristorino.beans.*;


import ar.edu.ubp.das.ristorino.beans.ContenidoBean;
import ar.edu.ubp.das.ristorino.beans.HorarioBean;
import ar.edu.ubp.das.ristorino.beans.ModificarReservaReqBean;
import ar.edu.ubp.das.ristorino.beans.NotiRestReqBean;
import ar.edu.ubp.das.ristorino.beans.ReservaRestauranteBean;
import ar.edu.ubp.das.ristorino.beans.ResponseBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;

import javax.smartcardio.ResponseAPDU;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
public class RestauranteSoapClient implements RestauranteClient {

    private final String jsonConfig;

    private final Gson gson = new Gson();

    public RestauranteSoapClient(String jsonConfig) {
        this.jsonConfig = jsonConfig;
    }
    @Override
    public ConfirmarReservaResponseBean confirmarReserva(ReservaRestauranteBean payload, int nroRestaurante) {

        try {
            SOAPClient client = SOAPClient.SOAPClientBuilder
                    .fromConfig(jsonConfig)
                    .operationName("confirmarReservaRequest")
                    .build();

            String jsonPayload = gson.toJson(payload);

            JsonDataRequestBean requestBean = new JsonDataRequestBean();
            requestBean.setJsonRequest(jsonPayload);

            Map<String, Object> params = new HashMap<>();
            params.put("jsonRequest", jsonPayload);

            String jsonResponse = client.callServiceForString(
                    "confirmarReservaResponse",
                    params
            );
            ConfirmarReservaResponseBean result =
                    gson.fromJson(jsonResponse, ConfirmarReservaResponseBean.class);

            return result;

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public SyncRestauranteBean obtenerRestaurante(int nroRestaurante) {
        try{
            SOAPClient client = SOAPClient.SOAPClientBuilder
                    .fromConfig(jsonConfig)
                    .operationName("GetInfoRestauranteRequest")
                    .build();
            String jsonPayload = "{\"id\": 1}";

            //String jsonPayload = gson.toJson("id",1);
            JsonDataRequestBean requestBean = new JsonDataRequestBean();
            requestBean.setJsonRequest(jsonPayload);
            Map<String, Object> params = new HashMap<>();

            params.put("jsonRequest", jsonPayload);
            String jsonResponse = client.callServiceForString(
                    "GetInfoRestauranteResponse",
                    params
            );
            SyncRestauranteBean result = gson.fromJson(jsonResponse, SyncRestauranteBean.class);
            result.setNroRestaurante(nroRestaurante);
            return result;

        }catch (Exception e){
            log.error("Error: {}", e.getMessage(), e);
            return null;
        }
    }
    @Override
    public ResponseBean enviarClicks(List<ClickNotiBean> clicks) {
        try {
            SOAPClient client = SOAPClient.SOAPClientBuilder
                    .fromConfig(jsonConfig)
                    .operationName("registrarClicksRequest")
                    .build();

            String jsonPayload = gson.toJson(clicks);
            JsonDataRequestBean requestBean = new JsonDataRequestBean();
            requestBean.setJsonRequest(jsonPayload);

            Map<String, Object> params = new HashMap<>();
            params.put("jsonRequest", jsonPayload);

            String jsonResponse = client.callServiceForString(
                    "registrarClicksResponse",
                    params
            );
            ResponseBean result =
                    gson.fromJson(jsonResponse, ResponseBean.class);

            return result;

        } catch (Exception e) {
            log.error("Error SOAP enviando clicks: {}", e.getMessage());
            ResponseBean resp = new ResponseBean();
            resp.setSuccess(Boolean.FALSE);
            return resp;
        }
    }
    @Override
    public List<HorarioBean> obtenerDisponibilidad(SoliHorarioBean soli) {

        try {
            SOAPClient client = SOAPClient.SOAPClientBuilder
                    .fromConfig(jsonConfig)
                    .operationName("ConsultarDisponibilidadRequest")
                    .build();

            String jsonPayload = gson.toJson(soli);

            JsonDataRequestBean requestBean = new JsonDataRequestBean();
            requestBean.setJsonRequest(jsonPayload);

            Map<String, Object> params = new HashMap<>();
            params.put("jsonRequest", jsonPayload);

            String jsonResponse = client.callServiceForString(
                    "ConsultarDisponibilidadResponse",
                    params
            );
            Type listType = new TypeToken<List<HorarioBean>>(){}.getType();
            List<HorarioBean> result = gson.fromJson(jsonResponse, listType);
            if(result.isEmpty())
                return List.of();

            return result;

        } catch (Exception e) {
            log.error("Error SOAP consultarDisponibilidad: {}", e.getMessage(), e);
            return List.of();
        }
    }
    @Override
    public ResponseBean cancelarReserva(String codReservaSucursal) {
        try {
            SOAPClient client = SOAPClient.SOAPClientBuilder
                    .fromConfig(jsonConfig)
                    .operationName("cancelarReservaRequest")
                    .build();
            CancelarReservaReqBean data = new CancelarReservaReqBean();
            data.setCodReservaSucursal(codReservaSucursal);
            String jsonPayload = gson.toJson(data);
            JsonDataRequestBean requestBean = new JsonDataRequestBean();
            requestBean.setJsonRequest(jsonPayload);
            Map<String, Object> params = new HashMap<>();
            params.put("jsonRequest", jsonPayload);

            String jsonResponse = client.callServiceForString(
                    "cancelarReservaResponse",
                    params
            );
            ResponseBean  result = gson.fromJson(jsonResponse, ResponseBean.class);

            return result;

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public ResponseBean modificarReserva(ModificarReservaReqBean req) {
        try {
            SOAPClient client = SOAPClient.SOAPClientBuilder
                    .fromConfig(jsonConfig)
                    .operationName("ModificarReservaRequest")
                    .build();
            String jsonPayload = gson.toJson(req);
            JsonDataRequestBean requestBean = new JsonDataRequestBean();
            requestBean.setJsonRequest(jsonPayload);
            Map<String, Object> params = new HashMap<>();
            params.put("jsonRequest", jsonPayload);

            String jsonResponse = client.callServiceForString(
                    "ModificarReservaResponse",
                    params
            );
            ResponseBean  result = gson.fromJson(jsonResponse, ResponseBean.class);

            return result;

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<ContenidoBean> obtenerPromociones(int nroRestaurante) {

        try {
            SOAPClient client = SOAPClient.SOAPClientBuilder
                    .fromConfig(jsonConfig)
                    .operationName("obtenerPromocionesRequest")
                    .build();

            String jsonPayload = "{\"id\": 1}";

            JsonDataRequestBean requestBean = new JsonDataRequestBean();
            requestBean.setJsonRequest(jsonPayload);

            Map<String, Object> params = new HashMap<>();
            params.put("jsonRequest", jsonPayload);

            String jsonResponse = client.callServiceForString(
                    "obtenerPromocionesResponse",
                    params
            );

            Type listType = new TypeToken<List<ContenidoBean>>(){}.getType();
            List<ContenidoBean> result = gson.fromJson(jsonResponse, listType);

            if (result == null) {
                log.warn("SOAP promociones vacío restaurante {}", nroRestaurante);
                return List.of();
            }
            return result;

        } catch (Exception e) {
            log.error("Error SOAP obteniendo promociones restaurante {}: {}",
                    nroRestaurante, e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public void notificarRestaurante(int nroRestaurante, BigDecimal costo, String contenidos) {

        NotiRestReqBean req = new NotiRestReqBean();
        req.setNroRestaurante(1);
        req.setCostoAplicado(costo);
        req.setNroContenidos(contenidos);

        try {
            SOAPClient client = SOAPClient.SOAPClientBuilder
                    .fromConfig(jsonConfig)
                    .operationName("notificarRestauranteRequest")
                    .build();

            String jsonPayload = gson.toJson(req);

            JsonDataRequestBean requestBean = new JsonDataRequestBean();
            requestBean.setJsonRequest(jsonPayload);

            Map<String, Object> params = new HashMap<>();
            params.put("jsonRequest", jsonPayload);

            String jsonResponse = client.callServiceForString(
                    "notificarRestauranteResponse",
                    params
            );
            UpdPublicarContenidosRespBean result =
                    gson.fromJson(jsonResponse, UpdPublicarContenidosRespBean.class);

            log.info("Notificación SOAP enviada restaurante {} (contenidos {})",
                    nroRestaurante, contenidos);

        } catch (Exception e) {
            log.error("Error SOAP notificando restaurante {}: {}",
                    nroRestaurante, e.getMessage(), e);
        }
    }


}