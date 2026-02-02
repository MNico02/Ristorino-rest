package ar.edu.ubp.das.ristorino.batch;

import ar.edu.ubp.das.ristorino.repositories.RistorinoRepository;
import ar.edu.ubp.das.ristorino.service.GeminiService;
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
public class GenerarContenidosIABatch {

    @Autowired
    private GeminiService geminiService;


    public void ejecutar() {

        log.info("Iniciando batch de generación de contenidos promocionales con IA");

        Map<String, Object> resultado =
                geminiService.generarContenidosPromocionalesBatch();

        log.info("Resultado del batch IA: {}", resultado);

        log.info("Batch de generación de contenidos promocionales finalizado");
    }


    public static void main(String[] args) {

        try (ConfigurableApplicationContext context =
                     new SpringApplicationBuilder(GenerarContenidosIABatch.class)
                             .web(WebApplicationType.NONE)
                             .profiles("batch")
                             .run(args)) {

            GenerarContenidosIABatch batch =
                    context.getBean(GenerarContenidosIABatch.class);

            batch.ejecutar();
        }
    }
}
