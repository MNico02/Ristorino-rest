package ar.edu.ubp.das.ristorino.batch;

import ar.edu.ubp.das.ristorino.beans.RestauranteBean;
import ar.edu.ubp.das.ristorino.beans.SyncRestauranteBean;
import ar.edu.ubp.das.ristorino.repositories.RistorinoRepository;
import ar.edu.ubp.das.ristorino.service.RestauranteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@SpringBootApplication(scanBasePackages = "ar.edu.ubp.das.ristorino")
public class RestauranteBatch {

    @Autowired private RestauranteService restauranteService;
    @Autowired private RistorinoRepository repository;

    public void ejecutar() {
        log.info("Iniciando batch de sync restaurante");

        int nroRestaurante = 2;

        SyncRestauranteBean restaurante = restauranteService.obtenerRestaurante(nroRestaurante);

        if (restaurante == null) {
            log.warn("No se obtuvo información del restaurante {}", nroRestaurante);
            return;
        }

        Map<String, Object> result = repository.guardarInfoRestaurante(restaurante);
        log.info("Sync OK restaurante {} -> {}", nroRestaurante, result);
    }

    public static void main(String[] args) {
        try (ConfigurableApplicationContext context =
                     new SpringApplicationBuilder(RestauranteBatch.class)
                             .web(WebApplicationType.NONE)
                             .profiles("batch")
                             .run(args)) {

            context.getBean(RestauranteBatch.class).ejecutar();
        }
    }
}
