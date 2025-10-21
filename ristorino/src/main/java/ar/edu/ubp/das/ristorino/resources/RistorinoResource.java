package ar.edu.ubp.das.ristorino.resources;


import ar.edu.ubp.das.ristorino.beans.HorarioBean;
import ar.edu.ubp.das.ristorino.beans.ReservaBean;
import ar.edu.ubp.das.ristorino.beans.SoliHorarioBean;
import ar.edu.ubp.das.ristorino.repositories.RistorinoRepository;
import ar.edu.ubp.das.ristorino.utils.Httpful;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.google.gson.reflect.TypeToken;
import jakarta.ws.rs.HttpMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("ristorino")
public class RistorinoResource {
    @Autowired
    private RistorinoRepository ristorinoRepository;

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

}