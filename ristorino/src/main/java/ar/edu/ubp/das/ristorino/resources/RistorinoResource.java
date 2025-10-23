package ar.edu.ubp.das.ristorino.resources;


import ar.edu.ubp.das.ristorino.Beans.ClienteBean;
import ar.edu.ubp.das.ristorino.Beans.LoginBean;
import ar.edu.ubp.das.ristorino.repositories.RistorinoRepository;
//import ar.edu.ubp.das.ristorino.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("ristorino")
public class RistorinoResource {
    @Autowired
    private RistorinoRepository ristorinoRepository;
   // private GeminiService geminiService;


   /* @PostMapping("/ia/recomendaciones")
    public ResponseEntity<Map<String, String>> procesarTexto(@RequestBody Map<String, String> body) {
        try {
            String texto = body.get("texto");
            Map<String, String> resultado = geminiService.interpretarTexto(texto);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }*/

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
