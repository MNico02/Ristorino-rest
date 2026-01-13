package ar.edu.ubp.das.ristorino.service;


import ar.edu.ubp.das.ristorino.beans.*;
import ar.edu.ubp.das.ristorino.repositories.RistorinoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReservaService {

    @Autowired
    private RistorinoRepository ristorinoRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final Map<Integer, String> BASE_URLS = Map.of(
            1, "http://localhost:8085/api/v1/restaurante1"
    );

    private static final Map<Integer, String> TOKENS = Map.of(
            1, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyZXN0YXVyYW50ZTEiLCJuYW1lIjoiR3J1cG9kYXNGR00iLCJyb2xlIjoiYWRtaW4iLCJpYXQiOjE3MzAxMzQ4MDB9.iy_l8J91bSB3R2Bwe2-ywrndUaWV2QYJU13V1CgK0F0"
    );

    public String registrarReserva(ReservaBean reserva) {

        // 1) Obtener cliente desde BD Ristorino
        SolicitudClienteBean cliente = ristorinoRepository
                .getClienteCorreo(reserva.getCorreo())
                .orElseThrow(() -> new RuntimeException("Cliente no registrado en Ristorino"));

        // 2) Resolver restaurante desde "Restaurante-Sucursal"
        int nroRestaurante = resolverRestaurante(reserva.getCodSucursalRestaurante());

        String baseUrl = BASE_URLS.get(nroRestaurante);
        String token = TOKENS.get(nroRestaurante);

        if (baseUrl == null || token == null) {
            throw new RuntimeException("No hay configuración para el restaurante " + nroRestaurante);
        }

        // 3) Armar request compuesto para el restaurante
        ReservaRestauranteBean payload = new ReservaRestauranteBean();
        payload.setSolicitudCliente(mapCliente(cliente)); // si tu bean difiere, ajustamos
        payload.setReserva(mapReservaSolicitud(reserva));  // importante: el restaurante espera ReservaSolicitudBean

        // 4) POST al restaurante (/confirmarReserva)
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ReservaRestauranteBean> request = new HttpEntity<>(payload, headers);

        ResponseEntity<ConfirmarReservaResponseBean> response = restTemplate.exchange(
                baseUrl + "/confirmarReserva",
                HttpMethod.POST,
                request,
                ConfirmarReservaResponseBean.class
        );

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Error al confirmar la reserva en el restaurante");
        }

        ConfirmarReservaResponseBean body = response.getBody();

        // 5) Si el restaurante rechaza, propagar mensaje
        if (!body.isSuccess()) {
            String msg = (body.getMensaje() != null) ? body.getMensaje() : "Reserva rechazada por el restaurante";
            throw new RuntimeException(msg);
        }

        // 6) Guardar en BD de Ristorino (recomendado)
        // Usá el codReserva que devolvió el restaurante como ID externo / código.
        // Ideal: guardar con los datos del RESPONSE (porque es la “verdad confirmada”)
        ristorinoRepository.insReservaConfirmadaRistorino(body,reserva,nroRestaurante);

        // 7) Devolver código
        return body.getCodReserva();
    }

    private int resolverRestaurante(String codigo) {
        return Integer.parseInt(codigo.split("-")[0]);
    }

    // --------- helpers de mapeo (ajustalos a tus beans reales) ----------

    private SolicitudClienteBean mapCliente(SolicitudClienteBean c) {
        SolicitudClienteBean sc = new SolicitudClienteBean();
        sc.setNombre(c.getNombre());
        sc.setApellido(c.getApellido());
        sc.setCorreo(c.getCorreo());
        sc.setTelefonos(c.getTelefonos());
        return sc;
    }

    private ReservaSolicitudBean mapReservaSolicitud(ReservaBean r) {
        ReservaSolicitudBean rs = new ReservaSolicitudBean();
        rs.setCodSucursalRestaurante(r.getCodSucursalRestaurante());
        rs.setCorreo(r.getCorreo());
        rs.setIdSucursal(r.getIdSucursal());
        rs.setFechaReserva(r.getFechaReserva());
        rs.setHoraReserva(r.getHoraReserva());
        rs.setCantAdultos(r.getCantAdultos());
        rs.setCantMenores(r.getCantMenores());
        rs.setCodZona(r.getCodZona());
        rs.setCostoReserva(r.getCostoReserva());
        return rs;
    }
}