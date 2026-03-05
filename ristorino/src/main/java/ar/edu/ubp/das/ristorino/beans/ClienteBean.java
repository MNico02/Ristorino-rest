package ar.edu.ubp.das.ristorino.beans;

import java.util.List;
import lombok.Data;

@Data
public class ClienteBean {

    private String apellido;
    private String nombre;
    private String clave;
    private String correo;
    private String telefonos;
    private String nomLocalidad;
    private String nomProvincia;
    private String observaciones;
    private Integer codCategoria;
    private Integer nroValorDominio;
    private List<PreferenciaRegistroBean> preferencias;

}
