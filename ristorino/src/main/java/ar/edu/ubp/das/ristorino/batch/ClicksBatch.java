package ar.edu.ubp.das.ristorino.batch;

import ar.edu.ubp.das.ristorino.beans.ClickNotiBean;
import ar.edu.ubp.das.ristorino.repositories.RistorinoRepository;
import ar.edu.ubp.das.ristorino.utils.Httpful;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@SpringBootApplication(scanBasePackages = "ar.edu.ubp.das.ristorino")
public class ClicksBatch {

    @Autowired
    private RistorinoRepository ristorinoRepository;

    private static final String RESTAURANT_API_URL =
            "http://localhost:8085/api/v1/restaurante1/registrarClicks";


    private static final String JWT_TOKEN =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyZXN0YXVyYW50ZTEiLCJuYW1lIjoiR3J1cG9kYXNGR00iLCJyb2xlIjoiYWRtaW4iLCJpYXQiOjE3MzAxMzQ4MDB9.iy_l8J91bSB3R2Bwe2-ywrndUaWV2QYJU13V1CgK0F0";

    public static void main(String[] args) {

        try (ConfigurableApplicationContext ctx =
                     new SpringApplicationBuilder(ClicksBatch.class)
                             .web(WebApplicationType.NONE)
                             .profiles("batch")
                             .run(args)) {

            ClicksBatch app = ctx.getBean(ClicksBatch.class);
            app.runBatch();
        }
    }

    private void runBatch() {
        System.out.println("Iniciando sincronización de clics con restaurante");

        try {
            // 1️⃣ Obtener clics pendientes
            List<ClickNotiBean> clicks = ristorinoRepository.obtenerClicksPendientes();

            if (clicks.isEmpty()) {
                System.out.println("No hay clics pendientes para enviar.");
                return;
            }

            System.out.println("Se encontraron " + clicks.size() + " clic(s) pendiente(s).");

            // 2️⃣ Enviar clics uno por uno
            for (ClickNotiBean click : clicks) {
                enviarClickAlRestaurante(click);
            }

            System.out.println("Sincronización finalizada correctamente.");

        } catch (Exception e) {
            System.err.println("Error durante la sincronización: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void enviarClickAlRestaurante(ClickNotiBean click) {
        try {
            Httpful http = new Httpful(RESTAURANT_API_URL)
                    .bearer(JWT_TOKEN)
                    .post(click);

            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> response = http.execute(type);

            System.out.println("Enviado clic ID " + click.getNroClick() + " → respuesta: " + response);
        } catch (Exception e) {
            System.err.println("No se pudo enviar el clic ID " + click.getNroClick() + ": " + e.getMessage());
        }
    }
}
