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
public class SyncSucursalBean {
    private int nroSucursal;
    private String nomSucursal;
    private String calle;
    private String nroCalle; // en tu JSON viene como String, perfecto
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

    public int getNroSucursal() {
        return nroSucursal;
    }

    public void setNroSucursal(int nroSucursal) {
        this.nroSucursal = nroSucursal;
    }

    public String getNomSucursal() {
        return nomSucursal;
    }

    public void setNomSucursal(String nomSucursal) {
        this.nomSucursal = nomSucursal;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNroCalle() {
        return nroCalle;
    }

    public void setNroCalle(String nroCalle) {
        this.nroCalle = nroCalle;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public int getNroLocalidad() {
        return nroLocalidad;
    }

    public void setNroLocalidad(int nroLocalidad) {
        this.nroLocalidad = nroLocalidad;
    }

    public String getNomLocalidad() {
        return nomLocalidad;
    }

    public void setNomLocalidad(String nomLocalidad) {
        this.nomLocalidad = nomLocalidad;
    }

    public int getCodProvincia() {
        return codProvincia;
    }

    public void setCodProvincia(int codProvincia) {
        this.codProvincia = codProvincia;
    }

    public String getNomProvincia() {
        return nomProvincia;
    }

    public void setNomProvincia(String nomProvincia) {
        this.nomProvincia = nomProvincia;
    }

    public String getCodPostal() {
        return codPostal;
    }

    public void setCodPostal(String codPostal) {
        this.codPostal = codPostal;
    }

    public String getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(String telefonos) {
        this.telefonos = telefonos;
    }

    public int getTotalComensales() {
        return totalComensales;
    }

    public void setTotalComensales(int totalComensales) {
        this.totalComensales = totalComensales;
    }

    public int getMinTolerenciaReserva() {
        return minTolerenciaReserva;
    }

    public void setMinTolerenciaReserva(int minTolerenciaReserva) {
        this.minTolerenciaReserva = minTolerenciaReserva;
    }

    public int getNroCategoria() {
        return nroCategoria;
    }

    public void setNroCategoria(int nroCategoria) {
        this.nroCategoria = nroCategoria;
    }

    public String getCategoriaPrecio() {
        return categoriaPrecio;
    }

    public void setCategoriaPrecio(String categoriaPrecio) {
        this.categoriaPrecio = categoriaPrecio;
    }

    public List<ContenidoBean> getContenidos() {
        return contenidos;
    }

    public void setContenidos(List<ContenidoBean> contenidos) {
        this.contenidos = contenidos;
    }

    public List<ZonaBean> getZonas() {
        return zonas;
    }

    public void setZonas(List<ZonaBean> zonas) {
        this.zonas = zonas;
    }

    public List<SyncEstiloBean> getEstilos() {
        return estilos;
    }

    public void setEstilos(List<SyncEstiloBean> estilos) {
        this.estilos = estilos;
    }

    public List<SyncEspecialidadBean> getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(List<SyncEspecialidadBean> especialidades) {
        this.especialidades = especialidades;
    }

    public List<SyncTipoComidaBean> getTiposComidas() {
        return tiposComidas;
    }

    public void setTiposComidas(List<SyncTipoComidaBean> tiposComidas) {
        this.tiposComidas = tiposComidas;
    }

    public List<TurnoBean> getTurnos() {
        return turnos;
    }

    public void setTurnos(List<TurnoBean> turnos) {
        this.turnos = turnos;
    }

    public List<ZonaTurnoBean> getZonasTurnos() {
        return zonasTurnos;
    }

    public void setZonasTurnos(List<ZonaTurnoBean> zonasTurnos) {
        this.zonasTurnos = zonasTurnos;
    }
}
