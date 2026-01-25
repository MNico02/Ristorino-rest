package ar.edu.ubp.das.ristorino.service;

import ar.edu.ubp.das.ristorino.beans.HorarioBean;
import ar.edu.ubp.das.ristorino.beans.SoliHorarioBean;
import ar.edu.ubp.das.ristorino.clients.DisponibilidadClient;
import ar.edu.ubp.das.ristorino.clients.DisponibilidadClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DisponibilidadService {

    private final DisponibilidadClientFactory factory;

    public DisponibilidadService(DisponibilidadClientFactory factory) {
        this.factory = factory;
    }

    public List<HorarioBean> obtenerDisponibilidad(SoliHorarioBean soli) {

        int nroRestaurante = resolverRestaurante(soli.getCodSucursalRestaurante());

        DisponibilidadClient client = factory.getClient(nroRestaurante);

        return client.obtenerDisponibilidad(soli);
    }

    private int resolverRestaurante(String codigo) {

        if (codigo == null || !codigo.contains("-")) {
            throw new IllegalArgumentException(
                    "Código restaurante-sucursal inválido: " + codigo
            );
        }

        try {
            return Integer.parseInt(codigo.split("-")[0]);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "No se pudo resolver el restaurante desde el código: " + codigo
            );
        }
    }
}
