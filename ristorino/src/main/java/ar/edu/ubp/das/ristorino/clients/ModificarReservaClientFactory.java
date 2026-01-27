package ar.edu.ubp.das.ristorino.clients;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ModificarReservaClientFactory {

    private final Map<Integer, ModificarReservaClient> clients = new HashMap<>();

    public ModificarReservaClient getClient(int nroRestaurante) {

        // 🔁 Si ya existe, lo devuelvo
        if (clients.containsKey(nroRestaurante)) {
            return clients.get(nroRestaurante);
        }

        ModificarReservaClient client;

        switch (nroRestaurante) {

            case 1 -> {
                client = new ModificarReservaRestClient(
                        "http://localhost:8085/api/v1/restaurante1",
                        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyZXN0YXVyYW50ZTEiLCJuYW1lIjoiR3J1cG9kYXNGR00iLCJyb2xlIjoiYWRtaW4iLCJpYXQiOjE3MzAxMzQ4MDB9.iy_l8J91bSB3R2Bwe2-ywrndUaWV2QYJU13V1CgK0F0"
                );
            }

            case 2 -> {
                client = new ModificarReservaSoapClient(
                        "http://localhost:8080/services",
                        "usr_admin",
                        "pwd_admin"
                );
            }
            case 3 -> {
                client = new ModificarReservaRestClient(
                        "http://localhost:8095/api/v1/restaurante3",
                        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyZXN0YXVyYW50ZTEiLCJuYW1lIjoiR3J1cG9kYXNGR00iLCJyb2xlIjoiYWRtaW4iLCJpYXQiOjE3MzAxMzQ4MDB9.iy_l8J91bSB3R2Bwe2-ywrndUaWV2QYJU13V1CgK0F0"
                );
            }
            case 4 -> {
                client = new ModificarReservaSoapClient(
                        "http://localhost:8090/services",
                        "usr_admin",
                        "pwd_admin"
                );
            }

            default -> throw new IllegalArgumentException(
                    "No hay cliente configurado para restaurante " + nroRestaurante
            );
        }

        clients.put(nroRestaurante, client);
        return client;
    }
}