package ar.edu.ubp.das.ristorino.clients;

import ar.edu.ubp.das.ristorino.beans.ClienteRestauranteConfigBean;
import ar.edu.ubp.das.ristorino.repositories.RistorinoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CancelarReservaClientFactory {

    @Autowired
    private RistorinoRepository ristorinoRepository;

    private final Map<Integer, CancelarReservaClient> clients = new HashMap<>();

    public CancelarReservaClient getClient(int nroRestaurante) {

        // Cache: si ya existe, lo devolvemos
        if (clients.containsKey(nroRestaurante)) {
            return clients.get(nroRestaurante);
        }

        //  Traer configuración desde BD
        ClienteRestauranteConfigBean cfg =
                ristorinoRepository.getConfiguracionClienteReservas(nroRestaurante);

        if (cfg == null || cfg.getTipoCliente() == null) {
            throw new IllegalArgumentException(
                    "No hay cliente configurado para restaurante " + nroRestaurante
            );
        }

        String tipo = cfg.getTipoCliente().toUpperCase();

        CancelarReservaClient client;

        //  Construcción del cliente (sin switch)
        if ("REST".equals(tipo)) {

            client = new CancelarReservaRestClient(
                    cfg.getBaseUrl(),
                    cfg.getToken()
            );

        } else {

            client = new CancelarReservaSoapClient(
                    cfg.getBaseUrl(),
                    cfg.getSoapUser(),
                    cfg.getSoapPass()
            );

        }

        //  Guardamos en cache
        clients.put(nroRestaurante, client);
        return client;
    }
}
