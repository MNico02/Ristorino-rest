package ar.edu.ubp.das.ristorino.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

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


    public int getNroRestaurante() {
        return nroRestaurante;
    }

    public void setNroRestaurante(int nroRestaurante) {
        this.nroRestaurante = nroRestaurante;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public List<ContenidoBean> getContenidos() {
        return contenidos;
    }

    public void setContenidos(List<ContenidoBean> contenidos) {
        this.contenidos = contenidos;
    }

    public List<SyncSucursalBean> getSucursales() {
        return sucursales;
    }

    public void setSucursales(List<SyncSucursalBean> sucursales) {
        this.sucursales = sucursales;
    }
}
