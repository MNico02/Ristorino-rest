package ar.edu.ubp.das.ristorino.beans;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
public class ReservaConfirmadaBean {

    private Integer nroCliente;
    private Integer nroReserva;
    private String codReservaSucursal;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaReserva;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime horaReserva;

    private Integer nroRestaurante;
    private Integer nroSucursal;
    private Integer codZona;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime horaDesde;

    private Integer cantAdultos;
    private Integer cantMenores;

    private Integer codEstado;
    private BigDecimal costoReserva;

    // getters/setters
    public Integer getNroCliente() { return nroCliente; }
    public void setNroCliente(Integer nroCliente) { this.nroCliente = nroCliente; }

    public Integer getNroReserva() { return nroReserva; }
    public void setNroReserva(Integer nroReserva) { this.nroReserva = nroReserva; }

    public String getCodReservaSucursal() { return codReservaSucursal; }
    public void setCodReservaSucursal(String codReservaSucursal) { this.codReservaSucursal = codReservaSucursal; }

    public LocalDate getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(LocalDate fechaReserva) { this.fechaReserva = fechaReserva; }

    public LocalTime getHoraReserva() { return horaReserva; }
    public void setHoraReserva(LocalTime horaReserva) { this.horaReserva = horaReserva; }

    public Integer getNroRestaurante() { return nroRestaurante; }
    public void setNroRestaurante(Integer nroRestaurante) { this.nroRestaurante = nroRestaurante; }

    public Integer getNroSucursal() { return nroSucursal; }
    public void setNroSucursal(Integer nroSucursal) { this.nroSucursal = nroSucursal; }

    public Integer getCodZona() { return codZona; }
    public void setCodZona(Integer codZona) { this.codZona = codZona; }

    public LocalTime getHoraDesde() { return horaDesde; }
    public void setHoraDesde(LocalTime horaDesde) { this.horaDesde = horaDesde; }

    public Integer getCantAdultos() { return cantAdultos; }
    public void setCantAdultos(Integer cantAdultos) { this.cantAdultos = cantAdultos; }

    public Integer getCantMenores() { return cantMenores; }
    public void setCantMenores(Integer cantMenores) { this.cantMenores = cantMenores; }

    public Integer getCodEstado() { return codEstado; }
    public void setCodEstado(Integer codEstado) { this.codEstado = codEstado; }

    public BigDecimal getCostoReserva() { return costoReserva; }
    public void setCostoReserva(BigDecimal costoReserva) { this.costoReserva = costoReserva; }
}
