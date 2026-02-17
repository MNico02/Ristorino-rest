package ar.edu.ubp.das.ristorino.service;

import ar.edu.ubp.das.ristorino.beans.*;
import ar.edu.ubp.das.ristorino.clients.RestauranteClient;
import ar.edu.ubp.das.ristorino.clients.RestauranteClientFactory;
import ar.edu.ubp.das.ristorino.repositories.RistorinoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReservaService {

    private final RistorinoRepository ristorinoRepository;
    private final RestauranteClientFactory factory;

    public ReservaService(RistorinoRepository ristorinoRepository,
                          RestauranteClientFactory factory) {
        this.ristorinoRepository = ristorinoRepository;
        this.factory = factory;
    }

    public String registrarReserva(ReservaBean reserva) {

        // 1) Cliente desde BD Ristorino
        SolicitudClienteBean cliente = ristorinoRepository
                .getClienteCorreo(reserva.getCorreo())
                .orElseThrow(() -> new RuntimeException("Cliente no registrado en Ristorino"));

        // 2) Resolver restaurante
        int nroRestaurante = resolverRestaurante(reserva.getCodSucursalRestaurante());

        // 3) Armar payload para restaurante
        ReservaRestauranteBean payload = new ReservaRestauranteBean();
        payload.setSolicitudCliente(mapCliente(cliente));
        payload.setReserva(mapReservaSolicitud(reserva));

        // 4) Delegar en el client correspondiente (REST o SOAP)
        RestauranteClient client = factory.getClient(nroRestaurante);

        ConfirmarReservaResponseBean body = client.confirmarReserva(payload);

        if (body == null) {
            throw new RuntimeException("Error al confirmar la reserva en el restaurante " + nroRestaurante);
        }

        // 5) Si el restaurante rechaza
        if (!body.isSuccess()) {
            String msg = (body.getMensaje() != null) ? body.getMensaje() : "Reserva rechazada por el restaurante";
            throw new RuntimeException(msg);
        }

        // 6) Guardar en Ristorino
        ristorinoRepository.insReservaConfirmadaRistorino(body, reserva, nroRestaurante);

        // 7) Devolver código
        return body.getCodReserva();
    }

    private int resolverRestaurante(String codigo) {
        if (codigo == null || !codigo.contains("-")) {
            throw new IllegalArgumentException("Código restaurante-sucursal inválido: " + codigo);
        }
        return Integer.parseInt(codigo.split("-")[0]);
    }

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
