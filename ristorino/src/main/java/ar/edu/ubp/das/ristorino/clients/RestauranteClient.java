package ar.edu.ubp.das.ristorino.clients;

import ar.edu.ubp.das.ristorino.beans.*;

import java.math.BigDecimal;
import java.util.List;

public interface RestauranteClient {
    SyncRestauranteBean obtenerRestaurante();

    ResponseBean enviarClicks(String json);

    ConfirmarReservaResponseBean confirmarReserva(String json);

    ResponseBean cancelarReserva(String codReservaSucursal);

    ResponseBean modificarReserva(String json);

    List<HorarioBean> obtenerDisponibilidad(String json);

    List<ContenidoBean> obtenerPromociones();

    void notificarRestaurante(BigDecimal costoAplicado, String nroContenidos);
}
