package ar.edu.ubp.das.ristorino.beans;

import lombok.Data;
import java.util.List;
@Data
public class SucursalBean {
    private int nroSucursal;
    private String nomSucursal;
    private String calle;
    private String nroCalle;
    private String barrio;
    private int nroLocalidad;
    private String nomLocalidad;
    private int codProvincia;
    private String nomProvincia;
    private String codPostal;
    private String telefonos;
    private int totalComensales;
    private int minTolerenciaReserva;
    private String codSucursalRestaurante;
    private List<TurnoBean> turnos;
    private List<ZonaBean> zonas;
    private List<PreferenciaBean> preferencias;
    private List<ZonaTurnoBean> zonasTurnos;

}