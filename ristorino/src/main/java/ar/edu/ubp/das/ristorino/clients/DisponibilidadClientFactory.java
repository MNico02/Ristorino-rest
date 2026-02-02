package ar.edu.ubp.das.ristorino.clients;

import ar.edu.ubp.das.ristorino.beans.ClienteRestauranteConfigBean;
import ar.edu.ubp.das.ristorino.repositories.RistorinoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DisponibilidadClientFactory {

    @Autowired
    private RistorinoRepository ristorinoRepository;

    public DisponibilidadClient getClient(int nroRestaurante) {

        //  Traer configuración desde BD
        ClienteRestauranteConfigBean cfg =
                ristorinoRepository.getConfiguracionClienteReservas(nroRestaurante);

        if (cfg == null || cfg.getTipoCliente() == null) {
            throw new IllegalArgumentException(
                    "No hay cliente de disponibilidad para restaurante " + nroRestaurante
            );
        }

        String tipo = cfg.getTipoCliente().toUpperCase();

        //  Construcción del cliente (sin switch)
        if ("REST".equals(tipo)) {

            return new DisponibilidadRestClient(
                    cfg.getBaseUrl(),
                    cfg.getToken()
            );

        } else {

            return new DisponibilidadSoapClient(
                    cfg.getBaseUrl(),
                    cfg.getSoapUser(),
                    cfg.getSoapPass()
            );

        }
    }
}
