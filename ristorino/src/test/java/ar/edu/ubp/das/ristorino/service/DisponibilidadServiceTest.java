package ar.edu.ubp.das.ristorino.service;


import ar.edu.ubp.das.ristorino.beans.HorarioBean;
import ar.edu.ubp.das.ristorino.beans.SoliHorarioBean;
import ar.edu.ubp.das.ristorino.clients.RestauranteClient;
import ar.edu.ubp.das.ristorino.clients.RestauranteClientFactory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DisponibilidadServiceTest {

    @Mock
    private RestauranteClientFactory factory;

    @Mock
    private RestauranteClient restauranteClient;

    @InjectMocks
    private DisponibilidadService disponibilidadService;

    @Test
    void obtenerHorariosClienteExiste_debeRetornarHorarios ()  {
        SoliHorarioBean soliHorarioBean = new SoliHorarioBean();
        soliHorarioBean.setCodSucursalRestaurante("1-2");
        HorarioBean horarioBean = new HorarioBean();
        horarioBean.setHoraReserva("14:00:00");
        int nroRestaurante = 1;
        when(factory.getClient(nroRestaurante)).thenReturn(restauranteClient);
        when(restauranteClient.obtenerDisponibilidad(anyString()))
                .thenReturn(List.of(horarioBean));

        List<HorarioBean> resultado = disponibilidadService.obtenerDisponibilidad(soliHorarioBean);

        assertThat(resultado.get(0).getHoraReserva()).isEqualTo("14:00:00");


        verify(factory, times(1)).getClient(nroRestaurante);
        verify(restauranteClient, times(1)).obtenerDisponibilidad(anyString());
    }


    @Test
    void obtenerHorariosCodigoNulo (){
        SoliHorarioBean soliHorarioBean = new SoliHorarioBean();
        soliHorarioBean.setCodSucursalRestaurante(null);
        assertThrows(IllegalArgumentException.class,() ->
                disponibilidadService.obtenerDisponibilidad(soliHorarioBean));

    }

    @Test
    void obtenerHorariosCodigoSinFormato (){
        SoliHorarioBean soliHorarioBean = new SoliHorarioBean();
        soliHorarioBean.setCodSucursalRestaurante("123");
        assertThrows(IllegalArgumentException.class,() ->
                disponibilidadService.obtenerDisponibilidad(soliHorarioBean));

    }

@Test
void obtenerDisponibilidad_debeDelgarAlClient() {
    SoliHorarioBean soliHorarioBean = new SoliHorarioBean();
    soliHorarioBean.setCodSucursalRestaurante("1-2");

    when(factory.getClient(1)).thenReturn(restauranteClient);
    when(restauranteClient.obtenerDisponibilidad(anyString()))
            .thenReturn(List.of());

    disponibilidadService.obtenerDisponibilidad(soliHorarioBean);

    verify(restauranteClient, times(1)).obtenerDisponibilidad(anyString());
}
}
