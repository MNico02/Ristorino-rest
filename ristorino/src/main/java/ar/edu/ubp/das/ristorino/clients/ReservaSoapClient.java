package ar.edu.ubp.das.ristorino.clients;

import ar.edu.ubp.das.ristorino.beans.ConfirmarReservaResponseBean;
import ar.edu.ubp.das.ristorino.beans.ContenidoBean;
import ar.edu.ubp.das.ristorino.beans.ReservaRestauranteBean;
import ar.edu.ubp.das.ristorino.beans.SyncRestauranteBean;
import ar.edu.ubp.das.ristorino.soap.restaurante2.*;
import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class ReservaSoapClient extends SoapClientBase implements ReservaClient {

    public ReservaSoapClient(String endpointUrl, String username, String password) {
        super(endpointUrl, username, password);
    }

    @Override
    public ConfirmarReservaResponseBean confirmarReserva(ReservaRestauranteBean payload, int nroRestaurante) {
        try{
            LocalTime hora = payload.getReserva().getHoraReserva();

            ConfirmarReservaRequest req = new ConfirmarReservaRequest();
            ar.edu.ubp.das.ristorino.soap.restaurante2.ReservaRestauranteBean data = new ar.edu.ubp.das.ristorino.soap.restaurante2.ReservaRestauranteBean();
            ReservaSolicitudBean solicitud = new ReservaSolicitudBean();
            SolicitudClienteBean cliente = new SolicitudClienteBean();
            solicitud.setCostoReserva(payload.getReserva().getCostoReserva());
            solicitud.setFechaReserva(payload.getReserva().getFechaReserva().toString());
            solicitud.setHoraReserva(hora.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            System.out.println(solicitud.getHoraReserva());
            solicitud.setCantAdultos(payload.getReserva().getCantAdultos());
            solicitud.setCorreo(payload.getReserva().getCorreo());
            solicitud.setCantMenores(payload.getReserva().getCantMenores());
            solicitud.setCodSucursalRestaurante(payload.getReserva().getCodSucursalRestaurante());
            solicitud.setCodZona(payload.getReserva().getCodZona());
            solicitud.setIdSucursal(payload.getReserva().getIdSucursal());
            data.setReserva(solicitud);
            cliente.setApellido(payload.getSolicitudCliente().getApellido());
            cliente.setNombre(payload.getSolicitudCliente().getNombre());
            cliente.setCorreo(payload.getSolicitudCliente().getCorreo());
            cliente.setTelefonos(payload.getSolicitudCliente().getTelefonos());
            data.setSolicitudCliente(cliente);
            req.setReservaRestaurante(data);

            ObjectFactory factory = new ObjectFactory();
            JAXBElement<ConfirmarReservaRequest> request = factory.createConfirmarReservaRequest(req);
            JAXBElement<ConfirmarReservaResponse> response = (JAXBElement<ConfirmarReservaResponse>)
                    wsTemplate.marshalSendAndReceive(request);
                var r = response.getValue().getReservaResponse();
                ConfirmarReservaResponseBean bean = new ConfirmarReservaResponseBean();
                bean.setCodReserva(r.getCodReserva());
                bean.setEstado(r.getEstado());
                bean.setMensaje(r.getMensaje());
                bean.setSuccess(r.isSuccess());
                return bean;
        }catch (Exception e){
            log.error(e.getMessage());
            return null;
        }


    }
}
