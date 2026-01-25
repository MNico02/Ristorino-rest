package ar.edu.ubp.das.ristorino.clients;

import ar.edu.ubp.das.ristorino.beans.ModificarReservaReqBean;
import ar.edu.ubp.das.ristorino.clients.SoapClientBase;
import ar.edu.ubp.das.ristorino.soap.restaurante2.ModificarReservaRequest;
import ar.edu.ubp.das.ristorino.soap.restaurante2.ModificarReservaResponse;
import ar.edu.ubp.das.ristorino.soap.restaurante2.ObjectFactory;
import lombok.extern.slf4j.Slf4j;

import jakarta.xml.bind.JAXBElement;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class ModificarReservaSoapClient extends SoapClientBase
        implements ModificarReservaClient {

    public ModificarReservaSoapClient(String endpointUrl, String username, String password) {
        super(endpointUrl, username, password);
    }

    @Override
    public Map<String, Object> modificarReserva(ModificarReservaReqBean req) {

        Map<String, Object> resultado = new HashMap<>();

        try {
            log.info(" "+req.getHoraReserva());
            // =========================
            // Datos SOAP
            // =========================
            ar.edu.ubp.das.ristorino.soap.restaurante2.ModificarReservaReqBean data = new ar.edu.ubp.das.ristorino.soap.restaurante2.ModificarReservaReqBean();
            data.setCodReservaSucursal(req.getCodReservaSucursal());
            data.setFechaReserva(req.getFechaReserva().toString());
            data.setHoraReserva(req.getHoraReserva().toString());
            data.setCodZona(req.getCodZona());
            data.setCantAdultos(req.getCantAdultos());
            data.setCantMenores(req.getCantMenores());
            data.setCostoReserva(req.getCostoReserva());
            ModificarReservaRequest requestData = new ModificarReservaRequest();
            requestData.setModificarReserva(data);

            log.info("BODY SOAP modificar reserva - Código: {}, Fecha: {}, Hora: {}",
                    req.getCodReservaSucursal(), req.getFechaReserva(), req.getHoraReserva());

            // =========================
            // ELEMENTO ROOT (JAXBElement)
            // =========================
            log.info(""+requestData.getModificarReserva().getHoraReserva());
            ObjectFactory factory = new ObjectFactory();
            JAXBElement<ModificarReservaRequest> request =
                    factory.createModificarReservaRequest(requestData);

            JAXBElement<ModificarReservaResponse> response =
                    (JAXBElement<ModificarReservaResponse>)
                            wsTemplate.marshalSendAndReceive(request);

            if (response == null || response.getValue() == null) {
                log.warn("SOAP vacío modificando reserva");
                resultado.put("success", false);
                resultado.put("status", "ERROR");
                resultado.put("message", "Respuesta SOAP vacía");
                return resultado;
            }

            // Mapear respuesta SOAP a Map
            ModificarReservaResponse soapResponse = response.getValue();
            resultado.put("success", soapResponse.getResponse().isSuccess());
            resultado.put("status", soapResponse.getResponse().getStatus());
            resultado.put("message", soapResponse.getResponse().getMessage());

            return resultado;

        } catch (Exception e) {
            log.error("Error SOAP modificando reserva: {}", e.getMessage());
            resultado.put("success", false);
            resultado.put("status", "ERROR");
            resultado.put("message", "Error SOAP: " + e.getMessage());
            return resultado;
        }
    }
}