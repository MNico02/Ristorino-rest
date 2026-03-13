package ar.edu.ubp.das.ristorino.service;

import ar.edu.ubp.das.ristorino.beans.ContenidoBean;
import ar.edu.ubp.das.ristorino.clients.RestauranteClient;
import ar.edu.ubp.das.ristorino.clients.RestauranteClientFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

// -----------------------------------------------------------------
// @ExtendWith(MockitoExtension.class): activa Mockito en el test.
// A diferencia de @WebMvcTest, acá NO se levanta Spring en absoluto.
// Es el test más liviano y rápido que existe.
// -----------------------------------------------------------------
@ExtendWith(MockitoExtension.class)
public class PromocionesServiceTest {

    // @Mock crea un doble del objeto (sin Spring, solo Mockito)
    @Mock
    private RestauranteClientFactory factory;

    @Mock
    private RestauranteClient restauranteClient;

    // @InjectMocks crea una instancia REAL del service e inyecta los mocks
    @InjectMocks
    private PromocionesService promocionesService;

    // Datos de prueba reutilizables en todos los tests
    private ContenidoBean contenido1;
    private ContenidoBean contenido2;

    // @BeforeEach se ejecuta ANTES de cada test, para preparar los datos
    @BeforeEach
    void setUp() {
        contenido1 = new ContenidoBean();
        contenido1.setNroContenido(1);
        contenido1.setContenidoAPublicar("2x1 en pizzas los viernes");
        contenido1.setCostoClick(new BigDecimal("0.50"));

        contenido2 = new ContenidoBean();
        contenido2.setNroContenido(2);
        contenido2.setContenidoAPublicar("Menú ejecutivo $5000");
        contenido2.setCostoClick(new BigDecimal("0.30"));
    }

    // ---------------------------------------------------------------
    // TEST 1: obtenerPromociones cuando el restaurante tiene un cliente
    //         configurado y devuelve promociones
    //
    // Req 12 / Req 46: el sistema consulta al restaurante sus promociones
    // ---------------------------------------------------------------
    @Test
    void obtenerPromociones_clienteExiste_debeRetornarLista() {

        // ARRANGE
        int nroRestaurante = 1;

        // Cuando la factory pida el cliente del restaurante 1, devuelve el mock
        when(factory.getClient(nroRestaurante)).thenReturn(restauranteClient);

        // Cuando se llame a obtenerPromociones() en el client, devuelve la lista
        when(restauranteClient.obtenerPromociones())
                .thenReturn(List.of(contenido1, contenido2));

        // ACT
        List<ContenidoBean> resultado = promocionesService.obtenerPromociones(nroRestaurante);

        // ASSERT
        // Verificamos que la lista tenga 2 elementos
        assertThat(resultado).hasSize(2);

        // Verificamos que el primer contenido sea el que esperamos
        assertThat(resultado.get(0).getNroContenido()).isEqualTo(1);
        assertThat(resultado.get(0).getContenidoAPublicar()).isEqualTo("2x1 en pizzas los viernes");

        // Verificamos que se llamó exactamente 1 vez a la factory y al client
        verify(factory, times(1)).getClient(nroRestaurante);
        verify(restauranteClient, times(1)).obtenerPromociones();
    }

    // ---------------------------------------------------------------
    // TEST 2: obtenerPromociones cuando el restaurante NO tiene cliente
    //         configurado (factory devuelve null)
    //
    // Caso borde: restaurante integrado sin cliente disponible
    // ---------------------------------------------------------------
    @Test
    void obtenerPromociones_sinCliente_debeRetornarListaVacia() {

        // ARRANGE: la factory no encuentra cliente para ese restaurante
        int nroRestaurante = 99;
        when(factory.getClient(nroRestaurante)).thenReturn(null);

        // ACT
        List<ContenidoBean> resultado = promocionesService.obtenerPromociones(nroRestaurante);

        // ASSERT: debe devolver lista vacía, no null ni excepción
        assertThat(resultado).isEmpty();

        // Verificamos que nunca se intentó llamar al client (porque era null)
        verify(restauranteClient, never()).obtenerPromociones();
    }

    // ---------------------------------------------------------------
    // TEST 3: obtenerPromociones cuando el restaurante existe pero
    //         no tiene promociones cargadas
    // ---------------------------------------------------------------
    @Test
    void obtenerPromociones_clienteExisteSinPromociones_debeRetornarListaVacia() {

        // ARRANGE
        int nroRestaurante = 2;
        when(factory.getClient(nroRestaurante)).thenReturn(restauranteClient);
        when(restauranteClient.obtenerPromociones()).thenReturn(List.of());

        // ACT
        List<ContenidoBean> resultado = promocionesService.obtenerPromociones(nroRestaurante);

        // ASSERT
        assertThat(resultado).isEmpty();
        verify(restauranteClient, times(1)).obtenerPromociones();
    }

    // ---------------------------------------------------------------
    // TEST 4: notificarRestaurante cuando el cliente existe
    //
    // Req 50: después de guardar las promociones, se notifica al restaurante
    // ---------------------------------------------------------------
    @Test
    void notificarRestaurante_clienteExiste_debeInvocarNotificacion() {

        // ARRANGE
        int nroRestaurante = 1;
        BigDecimal costoAplicado = new BigDecimal("1.50");
        String nroContenidos = "1,2";

        when(factory.getClient(nroRestaurante)).thenReturn(restauranteClient);

        // ACT
        promocionesService.notificarRestaurante(nroRestaurante, costoAplicado, nroContenidos);

        // ASSERT: verificamos que se llamó al mét odo de notificación con los parámetros correctos
        verify(restauranteClient, times(1))
                .notificarRestaurante(costoAplicado, nroContenidos);
    }

    // ---------------------------------------------------------------
    // TEST 5: notificarRestaurante cuando el cliente NO existe
    //         debe fallar silenciosamente sin lanzar excepción
    // ---------------------------------------------------------------
    @Test
    void notificarRestaurante_sinCliente_noDebeArrojarExcepcion() {

        // ARRANGE
        int nroRestaurante = 99;
        when(factory.getClient(nroRestaurante)).thenReturn(null);

        // ACT + ASSERT: no debe lanzar ninguna excepción
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
                promocionesService.notificarRestaurante(
                        nroRestaurante,
                        new BigDecimal("0.50"),
                        "1,2"
                )
        );

        // Verificamos que nunca se intentó notificar al client
        verify(restauranteClient, never())
                .notificarRestaurante(any(), any());
    }
}