package ar.edu.ubp.das.ristorino.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SyncSucursalBean {
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
    private int nroCategoria;
    private String categoriaPrecio;

    @Builder.Default
    private List<ContenidoBean> contenidos = new ArrayList<>();

    @Builder.Default
    private List<ZonaBean> zonas = new ArrayList<>();

    @Builder.Default
    private List<SyncEstiloBean> estilos = new ArrayList<>();

    @Builder.Default
    private List<SyncEspecialidadBean> especialidades = new ArrayList<>();

    @Builder.Default
    private List<SyncTipoComidaBean> tiposComidas = new ArrayList<>();

    @Builder.Default
    private List<TurnoBean> turnos = new ArrayList<>();

    @Builder.Default
    private List<ZonaTurnoBean> zonasTurnos = new ArrayList<>();

}
