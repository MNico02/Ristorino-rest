package ar.edu.ubp.das.ristorino.clients;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;

import java.io.ByteArrayOutputStream;

import org.springframework.ws.WebServiceMessage;


public class SoapLoggingInterceptor implements ClientInterceptor {

    @Override
    public boolean handleRequest(MessageContext messageContext) {
        logMessage("SOAP Request", messageContext.getRequest());
        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext) {
        logMessage("SOAP Response", messageContext.getResponse()); // <-- CLAVE
        return true;
    }

    @Override
    public boolean handleFault(MessageContext messageContext) {
        logMessage("SOAP Fault", messageContext.getResponse()); // <-- CLAVE
        return true;
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Exception ex) throws WebServiceClientException {

    }

    private void logMessage(String label, WebServiceMessage message) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            message.writeTo(out);
            System.out.println(label + ":\n" + out);
        } catch (Exception e) {
            System.out.println(label + ": <no se pudo imprimir>");
        }
    }
}