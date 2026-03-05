package ar.edu.ubp.das.ristorino.beans;
import lombok.Data;

@Data
public class FiltroRecomendacionBean {

    private String tipoComida;
    private String ciudad;
    private String provincia;
    private String momentoDelDia;
    private String rangoPrecio;
    private Integer cantidadPersonas;
    private String tieneMenores;
    private String restriccionesAlimentarias;
    private String preferenciasAmbiente;
    private String nombreRestaurante;
    private String barrioZona;
    private Boolean horarioFlexible;
    private String comida;
}