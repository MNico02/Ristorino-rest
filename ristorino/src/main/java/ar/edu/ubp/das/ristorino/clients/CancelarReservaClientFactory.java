package ar.edu.ubp.das.ristorino.clients;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CancelarReservaClientFactory {

    private final Map<Integer, CancelarReservaClient> clients = new HashMap<>();

    public CancelarReservaClient getClient(int nroRestaurante) {

        // 🔁 Si ya existe, lo devuelvo
        if (clients.containsKey(nroRestaurante)) {
            return clients.get(nroRestaurante);
        }

        CancelarReservaClient client;

        switch (nroRestaurante) {

            case 1 -> {
                client = new CancelarReservaRestClient(
                        "http://localhost:8081/api/v1/restaurante1",
                        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyZXN0YXVyYW50ZTEiLCJuYW1lIjoiR3J1cG9kYXNGR00iLCJyb2xlIjoiYWRtaW4iLCJpYXQiOjE3MzAxMzQ4MDB9.iy_l8J91bSB3R2Bwe2-ywrndUaWV2QYJU13V1CgK0F0"
                );
            }

            case 2 -> {
                client = new CancelarReservaSoapClient(
                        "http://localhost:8082/services",
                        "usr_admin",
                        "pwd_admin"
                );
            }
            case 3 -> {
                client = new CancelarReservaRestClient(
                        "http://localhost:8083/api/v1/restaurante3",
                        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyZXN0YXVyYW50ZTEiLCJuYW1lIjoiR3J1cG9kYXNGR00iLCJyb2xlIjoiYWRtaW4iLCJpYXQiOjE3MzAxMzQ4MDB9.iy_l8J91bSB3R2Bwe2-ywrndUaWV2QYJU13V1CgK0F0"
                );
            }
            case 4 -> {
                client = new CancelarReservaSoapClient(
                        "http://localhost:8084/services",
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