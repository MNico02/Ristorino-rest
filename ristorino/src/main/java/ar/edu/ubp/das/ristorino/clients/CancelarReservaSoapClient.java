package ar.edu.ubp.das.ristorino.clients;

import ar.edu.ubp.das.ristorino.clients.SoapClientBase;
import ar.edu.ubp.das.ristorino.soap.restaurante2.CancelarReservaReqBean;
import ar.edu.ubp.das.ristorino.soap.restaurante2.CancelarReservaRequest;
import ar.edu.ubp.das.ristorino.soap.restaurante2.CancelarReservaResponse;
import ar.edu.ubp.das.ristorino.soap.restaurante2.ObjectFactory;
import lombok.extern.slf4j.Slf4j;

import jakarta.xml.bind.JAXBElement;
import java.util.HashMap;
import java.util.Map;

// Importa las clases generadas por JAXB del WSDL del restaurante 2
// import ar.edu.ubp.das.restaurante2.jaxws.*;

@Slf4j
public class CancelarReservaSoapClient extends SoapClientBase
        implements CancelarReservaClient {

    public CancelarReservaSoapClient(String endpointUrl, String username, String password) {
        super(endpointUrl, username, password);
    }

    @Override
    public Map<String, Object> cancelarReserva(String codReservaSucursal) {

        Map<String, Object> resultado = new HashMap<>();

        try {
            CancelarReservaReqBean data = new CancelarReservaReqBean();
            data.setCodReservaSucursal(codReservaSucursal);
            CancelarReservaRequest req = new CancelarReservaRequest();
            req.setCancelarReserva(data);

            // =========================
            // ELEMENTO ROOT (JAXBElement)
            // =========================
            ObjectFactory factory = new ObjectFactory();
            JAXBElement<CancelarReservaRequest> request =
                    factory.createCancelarReservaRequest(req);

            JAXBElement<CancelarReservaResponse> response =
                    (JAXBElement<CancelarReservaResponse>)
                            wsTemplate.marshalSendAndReceive(request);

            if (response == null || response.getValue() == null) {
                log.warn("SOAP vacío cancelando reserva");
                resultado.put("success", false);
                resultado.put("message", "Respuesta SOAP vacía");
                return resultado;
            }

            // Mapear respuesta SOAP a Map
            CancelarReservaResponse soapResponse = response.getValue();
            resultado.put("success", soapResponse.getResponse().isSuccess());
            resultado.put("status", soapResponse.getResponse().getStatus());
            resultado.put("message", soapResponse.getResponse().getMessage());

            return resultado;


        } catch (Exception e) {
            log.error("Error SOAP cancelando reserva: {}", e.getMessage());
            resultado.put("success", false);
            resultado.put("message", "Error SOAP: " + e.getMessage());
            return resultado;
        }
    }
}