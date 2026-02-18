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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.google.gson.Gson;

@Slf4j
@SpringBootApplication(scanBasePackages = "ar.edu.ubp.das.ristorino")
public class PromocionesBatch {

    @Autowired
    private PromocionesService promocionesService;

    @Autowired
    private RistorinoRepository repository;

    public void procesarPromociones() {

        log.info("Iniciando batch de promociones");

        List<Integer> restaurantes = repository.obtenerNrosActivos();

        if (restaurantes.isEmpty()) {
            log.info("No hay restaurantes activos para procesar");
            return;
        }

        for (Integer nroRestaurante : restaurantes) {

            log.info("Procesando promociones restaurante {}", nroRestaurante);

            try {
                // Obtener promociones
                List<ContenidoBean> promociones =
                        promocionesService.obtenerPromociones(nroRestaurante);

                if (promociones.isEmpty()) {
                    log.info("No hay promociones para restaurante {}", nroRestaurante);
                    continue;
                }
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("nroRestaurante", nroRestaurante);
                body.put("promociones", promociones);
                String json = new Gson().toJson(body);
                // Guardar promociones
                BigDecimal costoAplicado =
                        repository.guardarPromociones(json);

                log.info("Se guardaron {} promociones del restaurante {} | Costo aplicado: {}",
                        promociones.size(), nroRestaurante, costoAplicado);

                // Armar string de contenidos
                String nroContenidos = promociones.stream()
                        .map(c -> String.valueOf(c.getNroContenido()))
                        .collect(Collectors.joining(","));

                // Notificar restaurante
                promocionesService.notificarRestaurante(
                        nroRestaurante,
                        costoAplicado,
                        nroContenidos
                );

            } catch (Exception e) {
                log.error("Error procesando promociones del restaurante {}. Se continúa con el siguiente.",
                        nroRestaurante, e);
            }
        }

        log.info("Batch de promociones finalizado");
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
