package ar.edu.ubp.das.ristorino.clients;

import ar.edu.ubp.das.ristorino.soap.security.WsSecurityUsernameTokenInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

public class SoapClientBase {

    protected final WebServiceTemplate wsTemplate;

    protected SoapClientBase(String endpointUrl,
                             String username,
                             String password) {

        try {
            // =========================
            // SOAP Message Factory
            // =========================
            SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory();
            messageFactory.afterPropertiesSet();

            // =========================
            // JAXB Marshaller
            // =========================
            Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
            marshaller.setContextPath(
                    "ar.edu.ubp.das.ristorino.soap.restaurante2"
            );
            marshaller.afterPropertiesSet();

            // =========================
            // WebServiceTemplate
            // =========================
            this.wsTemplate = new WebServiceTemplate(messageFactory);
            this.wsTemplate.setDefaultUri(endpointUrl);
            this.wsTemplate.setMarshaller(marshaller);
            this.wsTemplate.setUnmarshaller(marshaller);

            // =========================
            // WS-Security UsernameToken
            // =========================
            this.wsTemplate.setInterceptors(new ClientInterceptor[]{
                    new WsSecurityUsernameTokenInterceptor(username, password),
                    new SoapLoggingInterceptor()
            });
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Error inicializando cliente SOAP (Spring-WS)", e
            );
        }
    }
}