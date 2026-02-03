package ar.edu.ubp.das.ristorino.clients;

import ar.edu.ubp.das.ristorino.beans.*;

import java.math.BigDecimal;
import java.util.List;

public interface RestauranteClient {
    SyncRestauranteBean obtenerRestaurante(int nroRestaurante);
    ResponseBean enviarClicks(List<ClickNotiBean> clicks);
    ConfirmarReservaResponseBean confirmarReserva(ReservaRestauranteBean payload, int nroRestaurante);
    ResponseBean cancelarReserva(String codReservaSucursal);
    ResponseBean modificarReserva(ModificarReservaReqBean request);
    List<HorarioBean> obtenerDisponibilidad(SoliHorarioBean soli);
    List<ContenidoBean> obtenerPromociones(int nroRestaurante);
    void notificarRestaurante(int nroRestaurante, BigDecimal costoAplicado, String nroContenidos);
}
