package ar.edu.ubp.das.ristorino.service;

import ar.edu.ubp.das.ristorino.beans.*;
import ar.edu.ubp.das.ristorino.clients.RestauranteClient;
import ar.edu.ubp.das.ristorino.clients.RestauranteClientFactory;
import ar.edu.ubp.das.ristorino.repositories.RistorinoRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;



import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class ReservaServiceTest {
    @Mock
    private RestauranteClientFactory factory;

    @Mock
    private RestauranteClient restauranteClient;

    @InjectMocks
    private ReservaService reservaService;

    @Mock
    private RistorinoRepository ristorinoRepository;


    @Test
    void obtenerClienteFallo (){

            when(ristorinoRepository.getClienteCorreo(anyString()))
                .thenReturn(Optional.empty());

        ReservaBean reservaBean = new ReservaBean();
        reservaBean.setCorreo("juan@mail.com");

        assertThrows(RuntimeException.class, () ->
                reservaService.registrarReserva(reservaBean));
    }

    @Test
    void registrarReservaCodigoInvalido(){

        SolicitudClienteBean clienteMock = new SolicitudClienteBean();
        when(ristorinoRepository.getClienteCorreo(anyString()))
                .thenReturn(Optional.of(clienteMock));

        ReservaBean reserva = new ReservaBean();
        reserva.setCorreo("juan@mail.com");
        reserva.setCodSucursalRestaurante("1234");

        assertThrows(IllegalArgumentException.class, () ->
             reservaService.registrarReserva(reserva));

    }

    @Test
    void registrarReservaRestauranteDevuelveNulo(){

        SolicitudClienteBean clienteMock = new SolicitudClienteBean();
        when(ristorinoRepository.getClienteCorreo(anyString()))
                .thenReturn(Optional.of(clienteMock));
        int nroRestaurante = 1;
        ReservaBean reserva = new ReservaBean();
        reserva.setCorreo("juan@mail.com");
        reserva.setCodSucursalRestaurante("1-1");
        ConfirmarReservaResponseBean resp = new ConfirmarReservaResponseBean();


        when(factory.getClient(nroRestaurante)).thenReturn(restauranteClient);
        when(restauranteClient.confirmarReserva(anyString()))
                .thenReturn(resp);
       ResponseBean responseBean =  reservaService.registrarReserva(reserva);

        assertThat(responseBean.isSuccess()).isFalse();

    }

    @Test
    void registrarReservaRestauranteDevuelveRechazo(){

        SolicitudClienteBean clienteMock = new SolicitudClienteBean();
        when(ristorinoRepository.getClienteCorreo(anyString()))
                .thenReturn(Optional.of(clienteMock));
        int nroRestaurante = 1;
        ReservaBean reserva = new ReservaBean();
        reserva.setCorreo("juan@mail.com");
        reserva.setCodSucursalRestaurante("1-1");
        ConfirmarReservaResponseBean resp = new ConfirmarReservaResponseBean();
        resp.setEstado("RECHAZADO");
        resp.setSuccess(Boolean.FALSE);
        when(factory.getClient(nroRestaurante)).thenReturn(restauranteClient);
        when(restauranteClient.confirmarReserva(anyString()))
                .thenReturn(resp);

        assertThrows(RuntimeException.class, () ->
                reservaService.registrarReserva(reserva));

    }

    @Test
    void registrarReservaCodigoReservaNulo(){
        SolicitudClienteBean clienteMock = new SolicitudClienteBean();
        when(ristorinoRepository.getClienteCorreo(anyString()))
                .thenReturn(Optional.of(clienteMock));
        int nroRestaurante = 1;
        ReservaBean reserva = new ReservaBean();
        reserva.setCorreo("juan@mail.com");
        reserva.setCodSucursalRestaurante("1-1");
        ConfirmarReservaResponseBean resp = new ConfirmarReservaResponseBean();
        resp.setEstado("...");
        resp.setSuccess(Boolean.TRUE);
        when(factory.getClient(nroRestaurante)).thenReturn(restauranteClient);
        when(restauranteClient.confirmarReserva(anyString()))
                .thenReturn(resp);

        assertThrows(IllegalArgumentException.class, () ->
                reservaService.registrarReserva(reserva));

    }


    @Test
    void registrarReservaRegistroCorrecto(){
        SolicitudClienteBean clienteMock = new SolicitudClienteBean();
        when(ristorinoRepository.getClienteCorreo(anyString()))
                .thenReturn(Optional.of(clienteMock));
        int nroRestaurante = 1;
        ReservaBean reserva = new ReservaBean();
        reserva.setCorreo("juan@mail.com");
        reserva.setCodSucursalRestaurante("1-1");
        ConfirmarReservaResponseBean resp = new ConfirmarReservaResponseBean();
        resp.setEstado("...");
        resp.setSuccess(Boolean.TRUE);
        resp.setCodReserva("12312312-1");
        when(factory.getClient(nroRestaurante)).thenReturn(restauranteClient);
        when(restauranteClient.confirmarReserva(anyString()))
                .thenReturn(resp);
        ResponseBean responseBean =  reservaService.registrarReserva(reserva);

        assertThat(responseBean.isSuccess()).isTrue();
        verify(ristorinoRepository, times(1)).insReservaConfirmadaRistorino(anyString());

    }

}
