package ar.edu.ubp.das.ristorino.resources;

import ar.edu.ubp.das.ristorino.beans.*;
import ar.edu.ubp.das.ristorino.repositories.RistorinoRepository;
import ar.edu.ubp.das.ristorino.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ar.edu.ubp.das.ristorino.utils.Httpful;

import java.math.BigDecimal;
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
    @Autowired
    private DisponibilidadService disponibilidadService;
    @Autowired
    private ReservaService reservaService;
    @Autowired
    private CancelarReservaService cancelarReserva;
    @Autowired
    private ModificarReservaService modificarReservaService;


    @PostMapping("/registrarCliente")
    public ResponseEntity<Map<String, String>> registrarCliente(@RequestBody ClienteBean clienteBean) {

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

    @GetMapping("/zonasSucursal")
    public ResponseEntity<List<ZonaBean>> obtenerZonasSucursal(@RequestParam int nroRestaurante, @RequestParam int nroSucursal) {
        List<ZonaBean> zonasSucursal = ristorinoRepository.getZonasSucursal(nroRestaurante,nroSucursal);
        return ResponseEntity.ok(zonasSucursal);
    }

    @GetMapping ("/misReservas")
    public ResponseEntity<List<ReservaClienteBean>> obtenerReservasCliente(Authentication auth) {   String correo = auth.getName();
        System.out.println("correo: " + correo);
        List<ReservaClienteBean> reserva = ristorinoRepository.getReservasCliente(correo);
        return ResponseEntity.ok(reserva);
    }

    @PostMapping("/cancelarReserva")
    public ResponseEntity<Map<String, Object>> cancelarReserva(@RequestBody CancelarReservaBean req) {
        return ResponseEntity.ok(cancelarReserva.cancelarReserva(req));
    }

    @PostMapping("/modificarReserva")
    public ResponseEntity<Map<String, Object>> modificarReserva(@RequestBody ModificarReservaReqBean reserva) {

        Map<String, Object> resp = modificarReservaService.modificarReserva(reserva);

        boolean ok = Boolean.TRUE.equals(resp.get("success"));
        return ok ? ResponseEntity.ok(resp) : ResponseEntity.badRequest().body(resp);
    }

    /*
    * Consume el servicio del restaurante y registra una reserva
    * recive un reservaBean y si todo sale bien obtiene un codigo de reserva por parte del restuarnte
    * */
    @PostMapping("/registrarReserva")
    public ResponseEntity<Map<String, String>> registrarReserva(@RequestBody ReservaBean reserva) {

        String codigoReserva = reservaService.registrarReserva(reserva);

        Map<String, String> response = new HashMap<>();
        response.put("codReserva", codigoReserva);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/consultarDisponibilidad")
    public ResponseEntity<List<HorarioBean>> consultarDisponibilidad(@RequestBody SoliHorarioBean soliHorarioBean) {
        return ResponseEntity.ok(disponibilidadService.obtenerDisponibilidad(soliHorarioBean));
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
    * SOLO ES DE PRUEBA,
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
    public ResponseEntity<List<PromocionBean>> obtenerPromociones(@RequestParam(required = false) String nroRestaurante, @RequestParam(required = false) Integer nroSucursal) {
        List<PromocionBean> resultado = ristorinoRepository.obtenerPromociones(nroRestaurante, nroSucursal);
        return ResponseEntity.ok(resultado);

    }

    @GetMapping("/listarRestaurantesHome")
    public ResponseEntity<List<RestauranteHomeBean>> listarRestaurantesHome() {
        return ResponseEntity.ok(
                ristorinoRepository.listarRestaurantesHome()
        );
    }


    /*
    * Obtiene desde la BD de ristorino toda la info, menos el contenido promocional, de un restaurante solicitado por id
    * recive el numero de restaurante y devuelve un restauranteBean
    * */
    @GetMapping("/obtenerRestaurante/{nro}")
    public ResponseEntity<RestauranteBean> obtenerRestaurante(@PathVariable String nro) throws JsonProcessingException {
        RestauranteBean restauranteBean = ristorinoRepository.obtenerRestaurantePorId(nro);
        System.out.println("restauranteBeannro = " + restauranteBean.getNroRestaurante());
        return ResponseEntity.ok(restauranteBean);
    }

    /*
    * Se registra el click de una promocion en la base de datos de ristorino
    * Recive un clickBean y devuelve un json
    * //Todavia no guarda el cliente del click
    * */
    @PostMapping("/registrarClickPromocion")
    public ResponseEntity<Map<String, Object>> RegistrarClickPromocion(@RequestBody ClickBean clickBean) {
        System.out.println("aca esta el correo " + clickBean.getEmailUsuario());
        Map<String, Object> body = ristorinoRepository.registrarClick(clickBean);
        boolean ok = (boolean) body.getOrDefault("success", false);
        return new ResponseEntity<>(body, ok ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/categoriasPreferencias")
    public ResponseEntity<List<CategoriaPreferenciaBean>> obtenerCategoriasPreferencias() {
        List<CategoriaPreferenciaBean> resultado =
                ristorinoRepository.obtenerCategoriasPreferencias();
        return ResponseEntity.ok(resultado);
    }


    @PostMapping("/obtenerCosto")
    public ResponseEntity<Map<String, Object>> obtenerCosto(@RequestBody CostoBean req) {

        if (req.getTipoCosto() == null || req.getFecha() == null) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", "tipoCosto y fecha son obligatorios"
                    )
            );
        }

        try {
            BigDecimal monto =
                    ristorinoRepository.obtenerCostoVigente(
                            req.getTipoCosto(),
                            req.getFecha()
                    );

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "monto", monto
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of(
                            "success", false,
                            "message", e.getMessage()
                    )
            );
        }
    }




}