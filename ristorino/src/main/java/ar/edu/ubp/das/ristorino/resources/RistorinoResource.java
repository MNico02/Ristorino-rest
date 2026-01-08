package ar.edu.ubp.das.ristorino.resources;

import ar.edu.ubp.das.ristorino.beans.*;
import ar.edu.ubp.das.ristorino.repositories.RistorinoRepository;
import ar.edu.ubp.das.ristorino.service.GeminiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @PostMapping("/registrarCliente")
    public ResponseEntity<Map<String, String>> registrarCliente(
            @RequestBody ClienteBean clienteBean) {

        String mensaje = ristorinoRepository.registrarCliente(clienteBean);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("mensaje", mensaje));
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> logueo(@RequestBody LoginBean loginBean) {
        System.out.println("ENTRÓ AL LOGIN: " + loginBean.getCorreo());
        try {
            String token = ristorinoRepository.login(loginBean);
            if (token != null) {
                return ResponseEntity.ok(Map.of("token", token));
            } else {
                return ResponseEntity.status(401).body(Map.of("error", "error en email o contraseña"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    /*
    * Consume el servicio del restaurante y registra una reserva
    * recive un reservaBean y si todo sale bien obtiene un codigo de reserva por parte del restuarnte
    * */
    @PostMapping("/RegistrarReserva")
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

    /*
    * A partir de un texto de busqueda, la IA genera filtros que son usados en la BD para obtener una lista de restaurantes
    *  que coincidan con el texto de busqueda.
    * Devuelve una lista de restaurantes.
    * */
    @PostMapping("/ia/recomendaciones")
    public ResponseEntity<?> procesarTexto(@RequestBody Map<String, String> body) {
        try {
            String texto = body.get("texto");
            FiltroRecomendacionBean filtros = geminiService.interpretarTexto(texto);
            List<Map<String, Object>> restaurantes = ristorinoRepository.obtenerRecomendaciones(filtros);
            return ResponseEntity.ok(restaurantes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    /*
    * Obtiene los contenidos pendientes, genera un texto promocional con la IA para cada uno, y lo registra en la BD.
    * Devuelve la cantidad de contenidos generados.
    * */
    @PostMapping("/ia/generarContenidosPromocionales")
    public ResponseEntity<?> generarContenidosPromocionales() {
        try {
            Map<String, Object> resultado = ristorinoRepository.generarContenidosPromocionales();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /*
    * Obtiene el contenido promocional desde la BD de ristorino
    * Se necesita antes haber registrado el contenido del restaurante en la BD de ristorino y haber generado a partir de este la promocion con IA
    * recive idRestaurante e idSucural devuelve una lista de las promociones
    * */
    @GetMapping("/obtenerPromociones")
    public ResponseEntity<List<PromocionBean>> obtenerPromociones(@RequestParam(required = false) Integer idRestaurante, @RequestParam(required = false) Integer idSucursal) {
        List<PromocionBean> resultado = ristorinoRepository.obtenerPromociones(idRestaurante, idSucursal);
        return ResponseEntity.ok(resultado);

    }
    /*
    * Obtiene desde la BD de ristorino toda la info, menos el contenido promocional, de un restaurante solicitado por id
    * recive el numero de restaurante y devuelve un restauranteBean
    * */
    @GetMapping("/obtenerRestaurante/{nro}")
    public ResponseEntity<RestauranteBean> obtenerRestaurante(@PathVariable String nro) throws JsonProcessingException {
        RestauranteBean restauranteBean = ristorinoRepository.obtenerRestaurantePorId(nro);
        return ResponseEntity.ok(restauranteBean);
    }


    /*
    * Se registra el click de una promocion en la base de datos de ristorino
    * Recive un clickBean y devuelve un json
    * //Todavia no guarda el cliente del click
    * */
    @PostMapping("/registrarClickPromocion")
    public ResponseEntity<Map<String, Object>> RegistrarClickPromocion(@RequestBody ClickBean clickBean) {
        Map<String, Object> body = ristorinoRepository.registrarClick(clickBean);
        boolean ok = (boolean) body.getOrDefault("success", false);
        return new ResponseEntity<>(body, ok ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }






}