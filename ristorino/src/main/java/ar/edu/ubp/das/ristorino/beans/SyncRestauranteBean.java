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
public class SyncRestauranteBean {
    private int nroRestaurante;
    private String razonSocial;
    private String cuit;

    @Builder.Default
    private List<ContenidoBean> contenidos = new ArrayList<>();

    @Builder.Default
    private List<SyncSucursalBean> sucursales = new ArrayList<>();

}
