package ar.edu.ubp.das.ristorino.resources;

import ar.edu.ubp.das.ristorino.beans.*;
import ar.edu.ubp.das.ristorino.repositories.RistorinoRepository;
import ar.edu.ubp.das.ristorino.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ar.edu.ubp.das.ristorino.utils.Httpful;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import jakarta.ws.rs.HttpMethod;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("ristorino")
public class RistorinoResource {
    @Autowired
    private RistorinoRepository ristorinoRepository;
    @Autowired
    private GeminiService geminiService;

    @PostMapping("/confirmarReserva")
    public ResponseEntity<Map<String, String>> insertarReserva(@RequestBody ReservaBean reserva) {

        String codReserva = new Httpful("http://localhost:8085/api/v1/restaurante1").path("/confirmarReserva").method(HttpMethod.GET)
                .bearer("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIn0.S3xN0RG6Gf9QMRfVL3YRHLUaQbqewtZTXfzxQ9-9gak")
                .execute(new TypeToken<List<ReservaBean>>() {}.getType());
        Map<String, String> response = new HashMap<>();
        response.put("codReserva", codReserva);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/consultarDisponibilidad")
    public ResponseEntity<List<HorarioBean>> obtenerHorarios(@RequestBody SoliHorarioBean soliHorarioBean) {
        List<HorarioBean> horarios = new Httpful("http://localhost:8085/api/v1/restaurante1").path("/consultarDisponibilidad").method(HttpMethod.GET)
                .bearer("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIn0.S3xN0RG6Gf9QMRfVL3YRHLUaQbqewtZTXfzxQ9-9gak")
                .execute(new TypeToken<List<HorarioBean>>() {}.getType());
        return ResponseEntity.ok(horarios);
    }


    @PostMapping("/ia/recomendaciones")
    public ResponseEntity<?> procesarTexto(@RequestBody Map<String, String> body) {
        try {
            String texto = body.get("texto");
            FiltroRecomendacionBean filtros = geminiService.interpretarTexto(texto);
            System.out.println("🎯 Filtro recibido desde IA: " + filtros);
            List<Map<String, Object>> restaurantes = ristorinoRepository.obtenerRecomendaciones(filtros);
            return ResponseEntity.ok(restaurantes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }



    @PostMapping("/registrarCliente")
    public ResponseEntity<String> RegistrarCliente(@RequestBody ClienteBean clienteBean) {
        String cliente = ristorinoRepository.registrarCliente(clienteBean);
        return ResponseEntity.ok(cliente);
    }

    @PostMapping("/login")
    public ResponseEntity<?> logueo(@RequestBody LoginBean loginBean) {
        try {
            String token = ristorinoRepository.login(loginBean);
            if (token != null) {
                return ResponseEntity.ok(Map.of("token", token));
            } else {
                return ResponseEntity.status(401).body("Correo o clave incorrectos.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al procesar el login: " + e.getMessage());
        }
    }

}
