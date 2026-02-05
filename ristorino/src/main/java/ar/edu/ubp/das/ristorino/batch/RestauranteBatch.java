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

        log.info("🚀 Iniciando batch de sync de restaurantes");

        int nroRestaurante =1;

        while (true) {
            try {
                log.info(" Intentando sincronizar restaurante {}", nroRestaurante);

                SyncRestauranteBean restaurante =
                        restauranteService.obtenerRestaurante(nroRestaurante);

                // por si el service devuelve null (caso REST)
                if (restaurante == null) {
                    log.info("⛔ Restaurante {} no existe → fin del batch", nroRestaurante);
                    break;
                }

                Map<String, Object> result =
                        repository.guardarInfoRestaurante(restaurante);

                log.info("✅ Sync OK restaurante {} -> {}", nroRestaurante, result);

                nroRestaurante++; // siguiente

            } catch (Exception e) {

                //  ESTE ES EL CASO QUE ACABÁS DE VER
                log.warn(
                        "⛔ Restaurante {} no existe o no tiene configuración ({}) → fin del batch",
                        nroRestaurante,
                        e.getMessage()
                );

                break;
            }
        }

        log.info("🏁 Batch de sync finalizado");
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

