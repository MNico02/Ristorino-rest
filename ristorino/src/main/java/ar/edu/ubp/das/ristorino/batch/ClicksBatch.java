package ar.edu.ubp.das.ristorino.batch;

import ar.edu.ubp.das.ristorino.beans.ClickNotiBean;
import ar.edu.ubp.das.ristorino.repositories.RistorinoRepository;
import ar.edu.ubp.das.ristorino.service.ClickNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@SpringBootApplication(scanBasePackages = "ar.edu.ubp.das.ristorino")

public class ClicksBatch {

    @Autowired
    private RistorinoRepository repository;

    @Autowired
    private ClickNotificationService clickNotificationService;

    @Transactional
    public void procesarClicksPendientes() {

        List<ClickNotiBean> clicksPendientes = repository.obtenerClicksPendientes();

        if (clicksPendientes.isEmpty()) {
            return;
        }

        // Agrupar por restaurante
        Map<Integer, List<ClickNotiBean>> clicksPorRestaurante = clicksPendientes.stream()
                .collect(Collectors.groupingBy(ClickNotiBean::getNroRestaurante));



        // Enviar por restaurante
        clicksPorRestaurante.forEach((nroRestaurante, listaClicks) -> {
            boolean exito = clickNotificationService.enviarClicksPorRestaurante(nroRestaurante, listaClicks);

            if (exito) {
                log.info("Clicks enviados correctamente al restaurante {}", nroRestaurante);
                List<ClickNotiBean> confirmados = repository.marcarClicksComoNotificados(listaClicks, nroRestaurante);
                log.info("{} clics confirmados como notificados.", confirmados.size());
            } else {
                log.warn("Falló el envío de clics al restaurante {}", nroRestaurante);
            }
        });

        log.info("Proceso de envío de clics finalizado.");
    }

    /**
     * Permite ejecutar el batch manualmente
     */
    public static void main(String[] args) {
        // Levanta el contexto de Spring sin iniciar el servidor web
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(ClicksBatch.class)
                .web(WebApplicationType.NONE)
                .profiles("batch")
                .run(args)) {

            ClicksBatch batch = context.getBean(ClicksBatch.class);
            batch.procesarClicksPendientes();
        }
    }
}
