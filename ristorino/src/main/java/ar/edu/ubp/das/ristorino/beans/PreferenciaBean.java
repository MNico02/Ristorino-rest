package ar.edu.ubp.das.ristorino.beans;
import lombok.Data;

@Data
public class PreferenciaBean {
    private int codCategoria;
    private String nomCategoria;
    private int nroValorDominio;
    private String nomValorDominio;
    private int nroPreferencia;
    private String observaciones;

}
