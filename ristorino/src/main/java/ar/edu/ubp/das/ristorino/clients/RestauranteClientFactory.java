package ar.edu.ubp.das.ristorino.clients;


import ar.edu.ubp.das.ristorino.repositories.RistorinoRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RestauranteClientFactory {
    @Autowired
    private RistorinoRepository repository;
    private final Map<Integer, RestauranteClient> clients = new HashMap<>();

    public RestauranteClient getClient(int nroRestaurante) {
        String jsonConfig = repository.obtenerConfiguracionJson(nroRestaurante);


        JsonObject json = JsonParser.parseString(jsonConfig).getAsJsonObject();
        String tipo = json.get("tipoIntegracion").getAsString();

        return switch (tipo.toUpperCase()) {
            case "SOAP" -> new RestauranteSoapClient(jsonConfig);
            case "REST" -> new RestauranteRestClient(
                    json.get("baseUrl").getAsString(),
                    json.get("token").getAsString()
            );
            default -> throw new IllegalArgumentException("Tipo no soportado: " + tipo);
        };
    }
}

