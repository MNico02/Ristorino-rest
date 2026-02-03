package ar.edu.ubp.das.ristorino.service;

import ar.edu.ubp.das.ristorino.beans.ModificarReservaReqBean;
import ar.edu.ubp.das.ristorino.beans.ResponseBean;
import ar.edu.ubp.das.ristorino.clients.RestauranteClient;
import ar.edu.ubp.das.ristorino.clients.RestauranteClientFactory;
import ar.edu.ubp.das.ristorino.repositories.RistorinoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ModificarReservaService {

    private final RistorinoRepository ristorinoRepository;
    private final RestauranteClientFactory clientFactory;

    public ModificarReservaService(RistorinoRepository ristorinoRepository,
                                   RestauranteClientFactory clientFactory) {
        this.ristorinoRepository = ristorinoRepository;
        this.clientFactory = clientFactory;
    }

    public Map<String, Object> modificarReserva(ModificarReservaReqBean req) {

        Map<String, Object> resp = new HashMap<>();


        Integer nroRestaurante = req.getNroRestaurante();
        String codReservaSucursal = req.getCodReservaSucursal();

        if (nroRestaurante == null || codReservaSucursal == null || codReservaSucursal.isBlank()) {
            resp.put("success", false);
            resp.put("status", "INVALID");
            resp.put("message", "Faltan datos: nroRestaurante y codReservaSucursal son obligatorios.");
            return resp;
        }

        try {
            log.info("antes de el soapclient"+req.getHoraReserva().toString());

            RestauranteClient client = clientFactory.getClient(nroRestaurante);


            ResponseBean rtaRest = client.modificarReserva(req);

            /*boolean okRest = Boolean.TRUE.equals(rtaRest.get("success"));
            String statusRest = String.valueOf(rtaRest.getOrDefault("status", "UNKNOWN"));
            String msgRest = String.valueOf(rtaRest.getOrDefault("message", "Sin mensaje."));

            if (!okRest) {
                // Restaurante no pudo modificar
                resp.put("success", false);
                resp.put("status", statusRest);
                resp.put("message", msgRest);
                resp.put("restaurante", rtaRest);
                return resp;
            }*/


            Map<String, Object> rtaRistorino = ristorinoRepository.modificarReservaRistorino(req);

            boolean okRis = Boolean.TRUE.equals(rtaRistorino.get("success"));

            if (!okRis) {
                // Restaurante modificó, pero Ristorino no reflejó
                resp.put("success", false);
                resp.put("status", "PARTIAL_FAILURE");
                resp.put("message", "El restaurante modificó la reserva, pero Ristorino no pudo reflejar el cambio.");
                resp.put("restaurante", rtaRest);
                resp.put("ristorino", rtaRistorino);
                return resp;
            }


            resp.put("success", true);
            resp.put("status", rtaRest.getStatus());
            resp.put("message", "Reserva modificada correctamente en restaurante y reflejada en Ristorino.");
            resp.put("restaurante", rtaRest);
            resp.put("ristorino", rtaRistorino);
            return resp;

        } catch (IllegalArgumentException e) {
            log.error("Restaurante no configurado {}: {}", nroRestaurante, e.getMessage());
            resp.put("success", false);
            resp.put("status", "NOT_CONFIGURED");
            resp.put("message", "Restaurante no configurado: " + nroRestaurante);
            return resp;

        } catch (Exception e) {
            log.error("Error modificando reserva en restaurante {}: {}", nroRestaurante, e.getMessage());
            resp.put("success", false);
            resp.put("status", "ERROR");
            resp.put("message", "Error comunicándose con el restaurante: " + e.getMessage());
            return resp;
        }
    }
}