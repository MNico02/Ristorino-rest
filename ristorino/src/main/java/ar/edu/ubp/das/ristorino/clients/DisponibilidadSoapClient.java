package ar.edu.ubp.das.ristorino.clients;

import ar.edu.ubp.das.ristorino.beans.SoliHorarioBean;
import lombok.extern.slf4j.Slf4j;

import ar.edu.ubp.das.ristorino.soap.restaurante2.ConsultarDisponibilidadRequest;
import ar.edu.ubp.das.ristorino.soap.restaurante2.ConsultarDisponibilidadResponse;
import ar.edu.ubp.das.ristorino.soap.restaurante2.ObjectFactory;
import ar.edu.ubp.das.ristorino.soap.restaurante2.SoliHorario;

import jakarta.xml.bind.JAXBElement;

import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class DisponibilidadSoapClient extends SoapClientBase
        implements DisponibilidadClient {

    private static final DateTimeFormatter HH_MM = DateTimeFormatter.ofPattern("H:mm");
    private static final DateTimeFormatter HH_MM_SS = DateTimeFormatter.ofPattern("H:mm:ss");

    public DisponibilidadSoapClient(String endpointUrl,
                                    String username,
                                    String password) {
        super(endpointUrl, username, password);
    }

    @Override
    public List<ar.edu.ubp.das.ristorino.beans.HorarioBean> obtenerDisponibilidad(SoliHorarioBean soli) {

        try {
            SoliHorario soliSoap = mapToSoapSoliHorario(soli);

            ConsultarDisponibilidadRequest req = new ConsultarDisponibilidadRequest();
            req.setSoliHorario(soliSoap);

            ObjectFactory factory = new ObjectFactory();
            JAXBElement<ConsultarDisponibilidadRequest> request =
                    factory.createConsultarDisponibilidadRequest(req);

            @SuppressWarnings("unchecked")
            JAXBElement<ConsultarDisponibilidadResponse> response =
                    (JAXBElement<ConsultarDisponibilidadResponse>)
                            wsTemplate.marshalSendAndReceive(request);

            if (response == null || response.getValue() == null) {
                log.warn("SOAP disponibilidad devolvió response null");
                return List.of();
            }

            List<ar.edu.ubp.das.ristorino.soap.restaurante2.HorarioBean> horariosSoap =
                    response.getValue().getHorariosResponse();

            if (horariosSoap == null || horariosSoap.isEmpty()) {
                return List.of();
            }

            return horariosSoap.stream()
                    .map(this::mapToCommonHorarioBean)
                    .toList();

        } catch (Exception e) {
            log.error("Error SOAP consultarDisponibilidad: {}", e.getMessage(), e);
            return List.of();
        }
    }

    // =========================================================
    // MAPEOS
    // =========================================================

    private SoliHorario mapToSoapSoliHorario(SoliHorarioBean soli) {

        if (soli == null) {
            throw new IllegalArgumentException("Solicitud de disponibilidad null");
        }
        if (soli.getIdSucursal() <= 0) {
            throw new IllegalArgumentException("idSucursal inválido: " + soli.getIdSucursal());
        }
        if (soli.getCodZona() <= 0) {
            throw new IllegalArgumentException("codZona inválido: " + soli.getCodZona());
        }
        if (soli.getCantComensales() <= 0) {
            throw new IllegalArgumentException("cantComensales inválido: " + soli.getCantComensales());
        }

        SoliHorario s = new SoliHorario();
        s.setIdSucursal(soli.getIdSucursal());
        s.setCodZona(soli.getCodZona());
        s.setCantComensales(soli.getCantComensales());
        s.setMenores(soli.isMenores());
        s.setFecha(soli.getFecha() != null ? soli.getFecha().toString() : null); // yyyy-MM-dd

        return s;
    }

    private ar.edu.ubp.das.ristorino.beans.HorarioBean mapToCommonHorarioBean(
            ar.edu.ubp.das.ristorino.soap.restaurante2.HorarioBean h) {

        ar.edu.ubp.das.ristorino.beans.HorarioBean hb =
                new ar.edu.ubp.das.ristorino.beans.HorarioBean();

        hb.setHoraReserva(parseSqlTime(h.getHoraReserva(), "horaReserva"));
        hb.setHoraHasta(parseSqlTime(h.getHoraHasta(), "horaHasta"));

        return hb;
    }

    private Time parseSqlTime(String value, String fieldName) {

        if (value == null || value.isBlank()) {
            return null;
        }

        String v = value.trim();

        try {
            // acepta "HH:mm:ss"
            if (v.length() == 8) {
                LocalTime lt = LocalTime.parse(v, HH_MM_SS);
                return Time.valueOf(lt);
            }

            // acepta "HH:mm"
            if (v.length() == 4 || v.length() == 5) {
                LocalTime lt = LocalTime.parse(v, HH_MM);
                return Time.valueOf(lt);
            }

            // fallback: algunos devuelven "HH:mm:ss.SSS"
            if (v.length() > 8 && v.contains(".")) {
                String cut = v.substring(0, 8);
                LocalTime lt = LocalTime.parse(cut, HH_MM_SS);
                return Time.valueOf(lt);
            }

            // último recurso: Time.valueOf exige HH:mm:ss
            if (v.length() == 5) {
                v = v + ":00";
            }
            return Time.valueOf(v);

        } catch (Exception e) {
            log.warn("No se pudo parsear {}='{}' a Time", fieldName, value);
            return null;
        }
    }
}
