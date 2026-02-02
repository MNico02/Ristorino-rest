package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;
@Data
public class ClienteRestauranteConfigBean {
    private String tipoCliente; // REST | SOAP
    private String baseUrl;
    private String token;
    private String soapUser;
    private String soapPass;
}
