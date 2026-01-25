package ar.edu.ubp.das.ristorino.clients;

import ar.edu.ubp.das.ristorino.beans.ModificarReservaReqBean;
import java.util.Map;

public interface ModificarReservaClient {
    Map<String, Object> modificarReserva(ModificarReservaReqBean request);
}