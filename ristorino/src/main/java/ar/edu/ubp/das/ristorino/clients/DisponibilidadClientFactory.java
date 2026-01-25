package ar.edu.ubp.das.ristorino.clients;

import org.springframework.stereotype.Component;

@Component
public class DisponibilidadClientFactory {

    public DisponibilidadClient getClient(int nroRestaurante) {

        return switch (nroRestaurante) {

            case 1 -> new DisponibilidadRestClient(
                    "http://localhost:8085/api/v1/restaurante1",
                    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyZXN0YXVyYW50ZTEiLCJuYW1lIjoiR3J1cG9kYXNGR00iLCJyb2xlIjoiYWRtaW4iLCJpYXQiOjE3MzAxMzQ4MDB9.iy_l8J91bSB3R2Bwe2-ywrndUaWV2QYJU13V1CgK0F0"
            );

            case 2 -> new DisponibilidadSoapClient(
                    "http://localhost:8080/services",
                    "usr_admin",
                    "pwd_admin"
            );

            default -> throw new IllegalArgumentException(
                    "No hay cliente de disponibilidad para restaurante " + nroRestaurante
            );
        };
    }
}
