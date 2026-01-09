package ar.edu.ubp.das.ristorino.batch;

import ar.edu.ubp.das.ristorino.beans.RestauranteBean;
import ar.edu.ubp.das.ristorino.repositories.RistorinoRepository;
import ar.edu.ubp.das.ristorino.service.RestauranteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootApplication(scanBasePackages = "ar.edu.ubp.das.ristorino")
public class RestauranteBatch {
    @Autowired
    private RestauranteService restauranteService;
    @Autowired
    private RistorinoRepository repository;

    @Transactional
    public void ejecutar() {

        log.info("Iniciando batch de obtención de información de restaurantes");

        int nroRestaurante = 1; // luego puede venir de DB

        RestauranteBean restaurante =
                restauranteService.obtenerRestaurante(nroRestaurante);

        if (restaurante == null) {
            log.warn("No se obtuvo información del restaurante {}", nroRestaurante);
            return;
        }

        repository.guardarInfoRestaurante(restaurante);

        log.info("Información del restaurante {} guardada correctamente", nroRestaurante);
    }

    public static void main(String[] args) {

        try (ConfigurableApplicationContext context =
                     new SpringApplicationBuilder(RestauranteBatch.class)
                             .web(WebApplicationType.NONE)
                             .profiles("batch")
                             .run(args)) {

            RestauranteBatch batch =
                    context.getBean(RestauranteBatch.class);

            batch.ejecutar();
        }
    }
}

