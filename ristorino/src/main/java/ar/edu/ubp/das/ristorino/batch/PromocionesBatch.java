package ar.edu.ubp.das.ristorino.batch;

import ar.edu.ubp.das.ristorino.beans.ContenidoBean;
import ar.edu.ubp.das.ristorino.repositories.RistorinoRepository;
import ar.edu.ubp.das.ristorino.service.PromocionesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootApplication(scanBasePackages = "ar.edu.ubp.das.ristorino")
public class PromocionesBatch {

    @Autowired
    private PromocionesService promocionesService;

    @Autowired
    private RistorinoRepository repository;

    @Transactional
    public void procesarPromociones() {

        log.info("Iniciando batch de promociones");

        List<Integer> restaurantes = repository.obtenerNrosActivos();

        for (Integer nroRestaurante : restaurantes) {
            try {
                List<ContenidoBean> promociones =
                        promocionesService.obtenerPromociones(nroRestaurante);

                if (promociones.isEmpty()) {
                    log.info("No hay promociones para procesar para restaurante {}", nroRestaurante);
                    continue;
                }

                BigDecimal costoAplicado = repository.guardarPromociones(promociones, nroRestaurante);

                log.info("Se guardaron {} promociones del restaurante {}",
                        promociones.size(), nroRestaurante);
                String nroContenidos = promociones.stream()
                        .map(c -> String.valueOf(c.getNroContenido()))
                        .collect(Collectors.joining(","));


                promocionesService.notifiarRestaurante(nroRestaurante, costoAplicado, nroContenidos);

            } catch (Exception e) {
                log.error("Error procesando promociones del restaurante {}",
                        nroRestaurante, e);
                // seguir con el siguiente restaurante
            }
        }
    }

    public static void main(String[] args) {
        try (ConfigurableApplicationContext context =
                     new SpringApplicationBuilder(PromocionesBatch.class)
                             .web(WebApplicationType.NONE)
                             .profiles("batch")
                             .run(args)) {

            PromocionesBatch batch = context.getBean(PromocionesBatch.class);
            batch.procesarPromociones();
        }
    }


}