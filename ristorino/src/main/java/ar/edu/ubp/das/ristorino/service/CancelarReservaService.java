package ar.edu.ubp.das.ristorino.service;

import ar.edu.ubp.das.ristorino.beans.CancelarReservaBean;
import ar.edu.ubp.das.ristorino.clients.CancelarReservaClient;
import ar.edu.ubp.das.ristorino.clients.CancelarReservaClientFactory;
import ar.edu.ubp.das.ristorino.repositories.RistorinoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CancelarReservaService {

    private final RistorinoRepository ristorinoRepository;
    private final CancelarReservaClientFactory clientFactory;

    public CancelarReservaService(RistorinoRepository ristorinoRepository,
                                  CancelarReservaClientFactory clientFactory) {
        this.ristorinoRepository = ristorinoRepository;
        this.clientFactory = clientFactory; //decide el cliente segun el nro de restaurante
    }

    public Map<String, Object> cancelarReserva(CancelarReservaBean req) {

        Map<String, Object> resp = new HashMap<>();

        Integer nroRestaurante = req.getNroRestaurante();
        String codReservaSucursal = req.getCodReservaSucursal();

        // Validación de datos
        if (nroRestaurante == null || codReservaSucursal == null || codReservaSucursal.isBlank()) {
            resp.put("success", false);
            resp.put("message", "Faltan datos: nroRestaurante y codReservaSucursal son obligatorios.");
            return resp;
        }

        try {
            // Obtener cliente según el restaurante, va a la interfaz y decide el cliente
            CancelarReservaClient client = clientFactory.getClient(nroRestaurante);

            // 1) Llamar al restaurante para que cancele primero
            Map<String, Object> rtaRest = client.cancelarReserva(codReservaSucursal);

            boolean okRest = Boolean.TRUE.equals(rtaRest.get("success"));
            String statusRest = String.valueOf(rtaRest.getOrDefault("status", "UNKNOWN"));
            String msgRest = String.valueOf(rtaRest.getOrDefault("message", "Sin mensaje."));

            if (!okRest) {
                resp.put("success", false);
                resp.put("status", statusRest);
                resp.put("message", msgRest);
                return resp;
            }

            // 2) Reflejar en Ristorino (SP) usando el repository
            Map<String, Object> rtaRistorino =
                    ristorinoRepository.cancelarReservaRistorinoPorCodigo(codReservaSucursal);

            boolean okRis = Boolean.TRUE.equals(rtaRistorino.get("success"));

            if (!okRis) {
                // Restaurante canceló, pero Ristorino no pudo reflejar.
                resp.put("success", false);
                resp.put("status", "PARTIAL_FAILURE");
                resp.put("message", "El restaurante canceló, pero Ristorino no pudo reflejar la cancelación.");
                resp.put("restaurante", rtaRest);
                resp.put("ristorino", rtaRistorino);
                return resp;
            }

            // OK total
            resp.put("success", true);
            resp.put("status", statusRest);
            resp.put("message", "Reserva cancelada correctamente en restaurante y reflejada en Ristorino.");
            resp.put("restaurante", rtaRest);
            resp.put("ristorino", rtaRistorino);
            return resp;

        } catch (IllegalArgumentException e) {
            log.error("Restaurante no configurado {}: {}", nroRestaurante, e.getMessage());
            resp.put("success", false);
            resp.put("message", "Restaurante no configurado: " + nroRestaurante);
            return resp;

        } catch (Exception e) {
            log.error("Error cancelando reserva en restaurante {}: {}", nroRestaurante, e.getMessage());
            resp.put("success", false);
            resp.put("message", "Error comunicándose con el restaurante: " + e.getMessage());
            return resp;
        }
    }
}