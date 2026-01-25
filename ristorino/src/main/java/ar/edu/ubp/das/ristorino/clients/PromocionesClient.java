package ar.edu.ubp.das.ristorino.clients;

import ar.edu.ubp.das.ristorino.beans.ContenidoBean;

import java.math.BigDecimal;
import java.util.List;

public interface PromocionesClient {
    List<ContenidoBean> obtenerPromociones(int nroRestaurante);
    void notificarRestaurante(
            int nroRestaurante,
            BigDecimal costoAplicado,
            String nroContenidos
    );
}