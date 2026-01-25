package ar.edu.ubp.das.ristorino.soap.security;

import jakarta.xml.soap.*;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessage;


public class WsSecurityUsernameTokenInterceptor implements ClientInterceptor {

    private final String username;
    private final String password;

    public WsSecurityUsernameTokenInterceptor(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean handleRequest(MessageContext messageContext) {
        try {
            SaajSoapMessage soapMessage =
                    (SaajSoapMessage) messageContext.getRequest();

            SOAPMessage message = soapMessage.getSaajMessage();
            SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
            SOAPHeader header = envelope.getHeader();

            if (header == null) {
                header = envelope.addHeader();
            }

            SOAPElement security =
                    header.addChildElement("Security", "wsse",
                            "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");

            SOAPElement token = security.addChildElement("UsernameToken", "wsse");

            token.addChildElement("Username", "wsse")
                    .addTextNode(username);

            token.addChildElement("Password", "wsse")
                    .addTextNode(password);

        } catch (Exception e) {
            throw new RuntimeException("Error agregando WS-Security", e);
        }

        return true;
    }

    @Override public boolean handleResponse(MessageContext ctx) { return true; }
    @Override public boolean handleFault(MessageContext ctx) { return true; }
    @Override public void afterCompletion(MessageContext ctx, Exception ex) {}
}