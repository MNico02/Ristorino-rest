package ar.edu.ubp.das.ristorino.service;

import ar.edu.ubp.das.ristorino.beans.*;
import ar.edu.ubp.das.ristorino.clients.RestauranteClient;
import ar.edu.ubp.das.ristorino.clients.RestauranteClientFactory;
import ar.edu.ubp.das.ristorino.repositories.RistorinoRepository;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;

@Service
@Slf4j
public class ReservaService {

    private final RistorinoRepository ristorinoRepository;
    private final RestauranteClientFactory factory;
    private final Gson gson = new Gson();

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
        payload.setSolicitudCliente(cliente);
        payload.setReserva(reserva);
        String json = gson.toJson(payload);

        // 4) Delegar en el client correspondiente (REST o SOAP)
        RestauranteClient client = factory.getClient(nroRestaurante);

        ConfirmarReservaResponseBean body = client.confirmarReserva(json);

        if (body == null) {
            throw new RuntimeException("Error al confirmar la reserva en el restaurante " + nroRestaurante);
        }

        // 5) Si el restaurante rechaza
        if (!body.isSuccess()) {
            String msg = (body.getMensaje() != null) ? body.getMensaje() : "Reserva rechazada por el restaurante";
            throw new RuntimeException(msg);
        }
        if (body.getCodReserva() == null || body.getCodReserva().isBlank())
            throw new IllegalArgumentException("codReserva vacío");
        JsonObject jsonObject = gson.toJsonTree(reserva).getAsJsonObject();
        jsonObject.addProperty("codReservaRestaurante", body.getCodReserva());
        String jsonRistorino = jsonObject.toString();

        // 6) Guardar en Ristorino
        ristorinoRepository.insReservaConfirmadaRistorino(jsonRistorino);

        // 7) Devolver código
        return body.getCodReserva();
    }

    private int resolverRestaurante(String codigo) {
        if (codigo == null || !codigo.contains("-")) {
            throw new IllegalArgumentException("Código restaurante-sucursal inválido: " + codigo);
        }
        return Integer.parseInt(codigo.split("-")[0]);
    }

}
