package ar.edu.ubp.das.ristorino.resources;

import ar.edu.ubp.das.ristorino.beans.*;
import ar.edu.ubp.das.ristorino.repositories.RistorinoRepository;
import ar.edu.ubp.das.ristorino.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// -----------------------------------------------------------------
// @WebMvcTest: levanta SOLO la capa web (el controller).
// No levanta la BD, ni los services reales. Todo lo demás se mockea.
// -----------------------------------------------------------------
@WebMvcTest(RistorinoResource.class)
public class RistorinoResourceTest {

    // MockMvc simula llamadas HTTP sin levantar un servidor real
    @Autowired
    private MockMvc mockMvc;

    // ObjectMapper convierte objetos Java a JSON para armar el body del request
    @Autowired
    private ObjectMapper objectMapper;

    // @MockitoBean reemplaza cada dependencia del controller por un "doble" de prueba.
    // Así el test no necesita base de datos ni servicios externos reales.
    @MockitoBean
    private RistorinoRepository ristorinoRepository;
    @MockitoBean
    private GeminiService geminiService;
    @MockitoBean
    private DisponibilidadService disponibilidadService;
    @MockitoBean
    private ReservaService reservaService;
    @MockitoBean
    private CancelarReservaService cancelarReserva;
    @MockitoBean
    private ModificarReservaService modificarReservaService;

    // ---------------------------------------------------------------
    // TEST 1: POST /ristorino/registrarCliente → 201 CREATED
    //
    // Verifica que al registrar un cliente:
    //   - El endpoint responde con HTTP 201
    //   - El body tiene el campo "mensaje" con el valor esperado
    // ---------------------------------------------------------------
    @Test
    @WithMockUser // simula un usuario autenticado para pasar el filtro de seguridad
    public void registrarCliente_debeRetornar201() throws Exception {

        // ARRANGE: preparamos el objeto que enviará el frontend
        ClienteBean cliente = new ClienteBean();
        cliente.setNombre("Juan");
        cliente.setApellido("Perez");
        cliente.setCorreo("juan@mail.com");
        cliente.setClave("1234");

        // Le decimos al mock: "cuando llamen a registrarCliente con cualquier String,
        // devolvé este mensaje"
        when(ristorinoRepository.registrarCliente(anyString()))
                .thenReturn("Cliente registrado correctamente");

        // ACT + ASSERT: ejecutamos el request y verificamos la respuesta
        mockMvc.perform(
                        post("/ristorino/registrarCliente")
                                .with(csrf())                                      // CSRF token requerido en POST
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cliente)) // body del request
                )
                .andExpect(status().isCreated())                               // HTTP 201
                .andExpect(jsonPath("$.mensaje")                               // campo "mensaje" en el JSON
                        .value("Cliente registrado correctamente"));
    }

    // ---------------------------------------------------------------
    // TEST 2: POST /ristorino/login con credenciales válidas → 200 + token
    //
    // Verifica que al hacer login correcto:
    //   - El endpoint responde con HTTP 200
    //   - El body tiene el campo "token"
    // ---------------------------------------------------------------
    @Test
    @WithMockUser
    public void login_credencialesValidas_debeRetornarToken() throws Exception {

        // ARRANGE
        LoginBean loginBean = new LoginBean();
        loginBean.setCorreo("juan@mail.com");
        loginBean.setClave("1234");

        when(ristorinoRepository.login(any(LoginBean.class)))
                .thenReturn("token-jwt-falso-123");

        // ACT + ASSERT
        mockMvc.perform(
                        post("/ristorino/login")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginBean))
                )
                .andExpect(status().isOk())           // HTTP 200
                .andExpect(jsonPath("$.token")        // el JSON debe tener "token"
                        .value("token-jwt-falso-123"));
    }

    // ---------------------------------------------------------------
    // TEST 3: POST /ristorino/login con credenciales inválidas → 401
    //
    // Verifica que si el repositorio devuelve null (credenciales malas),
    // el controller responde con HTTP 401
    // ---------------------------------------------------------------
    @Test
    @WithMockUser
    public void login_credencialesInvalidas_debeRetornar401() throws Exception {

        // ARRANGE: el repositorio devuelve null → credenciales incorrectas
        LoginBean loginBean = new LoginBean();
        loginBean.setCorreo("malo@mail.com");
        loginBean.setClave("wrong");

        when(ristorinoRepository.login(any(LoginBean.class)))
                .thenReturn(null);

        // ACT + ASSERT
        mockMvc.perform(
                        post("/ristorino/login")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginBean))
                )
                .andExpect(status().isUnauthorized()); // HTTP 401
    }

    // ---------------------------------------------------------------
    // TEST 4: GET /ristorino/listarRestaurantesHome → 200 + lista
    //
    // Verifica que el endpoint de home devuelva HTTP 200
    // y que el JSON sea un array con al menos un elemento
    // ---------------------------------------------------------------

    @Test
    @WithMockUser
    public void listarRestaurantesHome_debeRetornarLista() throws Exception {

        // ARRANGE: creamos una respuesta simulada
        RestauranteHomeBean restaurante = new RestauranteHomeBean();
        restaurante.setRazonSocial("La Trattoria");

        when(ristorinoRepository.listarRestaurantesHome())
                .thenReturn(List.of(restaurante));

        // ACT + ASSERT
        mockMvc.perform(get("/ristorino/listarRestaurantesHome"))
                .andExpect(status().isOk())               // HTTP 200
                .andExpect(jsonPath("$").isArray())        // la respuesta es un array
                .andExpect(jsonPath("$[0].razonSocial")         // el primer elemento tiene nombre
                        .value("La Trattoria"));
    }

    // ---------------------------------------------------------------
    // TEST 5: GET /ristorino/categoriasPreferencias → 200 + lista
    //
    // Simple test de un GET sin parámetros
    // ---------------------------------------------------------------

    @Test
    @WithMockUser
    public void obtenerCategoriasPreferencias_debeRetornarLista() throws Exception {

        // ARRANGE
        CategoriaPreferenciaBean categoria = new CategoriaPreferenciaBean();
        categoria.setNomCategoria("Italiana");

        when(ristorinoRepository.obtenerCategoriasPreferencias())
                .thenReturn(List.of(categoria));

        // ACT + ASSERT
        mockMvc.perform(get("/ristorino/categoriasPreferencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nomCategoria").value("Italiana"));
    }


}