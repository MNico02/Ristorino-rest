package ar.edu.ubp.das.ristorino.clients;

import ar.edu.ubp.das.ristorino.beans.ClickNotiBean;

import ar.edu.ubp.das.ristorino.soap.restaurante2.ObjectFactory;
import ar.edu.ubp.das.ristorino.soap.restaurante2.RegistrarClicksRequest;
import ar.edu.ubp.das.ristorino.soap.restaurante2.RegistrarClicksResponse;
import ar.edu.ubp.das.ristorino.soap.restaurante2.SoliClickBean;
import lombok.extern.slf4j.Slf4j;

import jakarta.xml.bind.JAXBElement;
import java.util.List;

// Importa las clases generadas por JAXB del WSDL
// import ar.edu.ubp.das.restaurante2.jaxws.*;

@Slf4j
public class ClickNotificationSoapClient extends SoapClientBase
        implements ClickNotificationClient {

    public ClickNotificationSoapClient(String endpointUrl, String username, String password) {
        super(endpointUrl, username, password);
    }

    @Override
    public boolean enviarClicks(List<ClickNotiBean> clicks) {
        try {
            // =========================
            // Datos SOAP
            // =========================
            RegistrarClicksRequest data = new RegistrarClicksRequest();

            for (ClickNotiBean click : clicks) {
                SoliClickBean soapClick = mapearClickBean(click);
                data.getClicks().add(soapClick);
            }

            log.info("SOAP enviando {} clicks", clicks.size());
            // =========================
            // ELEMENTO ROOT (JAXBElement)
            // =========================
            ObjectFactory factory = new ObjectFactory();
            JAXBElement<RegistrarClicksRequest> request =
                    factory.createRegistrarClicksRequest(data);

            JAXBElement<RegistrarClicksResponse> response =
                    (JAXBElement<RegistrarClicksResponse>)
                            wsTemplate.marshalSendAndReceive(request);

            if (response == null || response.getValue() == null) {
                log.warn("SOAP vacío registrando clicks");
                return false;
            }

            // Verificar respuesta
            RegistrarClicksResponse soapResponse = response.getValue();
            boolean success = soapResponse.getResponse().isSuccess();

            if (success) {
                log.info("Clicks registrados correctamente via SOAP");
            } else {
                log.warn("SOAP clicks respondió: {}", soapResponse.getResponse().getMessage());
            }

            return success;

        } catch (Exception e) {
            log.error("Error SOAP enviando clicks: {}", e.getMessage());
            return false;
        }
    }
    private SoliClickBean mapearClickBean(ClickNotiBean click) {
        SoliClickBean soapClick = new SoliClickBean();

        soapClick.setNroClick(click.getNroClick());
        soapClick.setNroRestaurante(1);
        soapClick.setCodContenidoRestaurante(click.getCodContenidoRestaurante());
        soapClick.setCorreoCliente(click.getCorreo_cliente());
        soapClick.setCostoClick(click.getCostoClick());
        soapClick.setFechaHoraRegistro(click.getFechaHoraRegistro());
        soapClick.setNotificado(click.isNotificado());

        return soapClick;
    }
}