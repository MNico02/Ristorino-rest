package ar.edu.ubp.das.ristorino.clients;



import ar.edu.ubp.das.ristorino.beans.*;


import ar.edu.ubp.das.ristorino.soap.restaurante2.EspecialidadBean;
import ar.edu.ubp.das.ristorino.soap.restaurante2.GetInfoRestauranteRequest;
import ar.edu.ubp.das.ristorino.soap.restaurante2.GetInfoRestauranteResponse;
import ar.edu.ubp.das.ristorino.soap.restaurante2.ObjectFactory;
import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class RestauranteSoapClient extends SoapClientBase
        implements RestauranteClient {

    public RestauranteSoapClient(String endpointUrl,
                                 String username,
                                 String password) {
        super(endpointUrl, username, password);
    }

    @Override
    public SyncRestauranteBean obtenerRestaurante(int nroRestaurante) {

        // =========================
        // Datos SOAP
        // =========================
        GetInfoRestauranteRequest data = new GetInfoRestauranteRequest();
        data.setId(1); // SOAP siempre entiende 1

        // =========================
        // ELEMENTO ROOT (JAXBElement)
        // =========================
        ObjectFactory factory = new ObjectFactory();
        JAXBElement<GetInfoRestauranteRequest> request =
                factory.createGetInfoRestauranteRequest(data);

        JAXBElement<GetInfoRestauranteResponse> response =
                (JAXBElement<GetInfoRestauranteResponse>)
                        wsTemplate.marshalSendAndReceive(request);

        if (response == null || response.getValue() == null
                || response.getValue().getInfoRestaurante() == null) {
            log.warn("SOAP vacío restaurante {}", nroRestaurante);
            return null;
        }

        SyncRestauranteBean sync = mapear(response.getValue());
        sync.setNroRestaurante(nroRestaurante); // ID Ristorino
        return sync;
    }
    private SyncRestauranteBean mapear(GetInfoRestauranteResponse response) {

        var r = response.getInfoRestaurante();

        SyncRestauranteBean sync = new SyncRestauranteBean();

        // =========================
        // Datos restaurante
        // =========================
        sync.setNroRestaurante(r.getNroRestaurante()); // luego se pisa
        sync.setRazonSocial(r.getRazonSocial());
        sync.setCuit(r.getCuit());

        // =========================
        // Sucursales
        // =========================
        if (r.getSucursales() != null) {
            sync.setSucursales(
                    r.getSucursales()
                            .stream()
                            .map(s -> {

                                SyncSucursalBean ss = new SyncSucursalBean();

                                ss.setNroSucursal(s.getNroSucursal());
                                ss.setNomSucursal(s.getNomSucursal());
                                ss.setCalle(s.getCalle());
                                ss.setNroCalle(
                                        String.valueOf(s.getNroCalle() != null
                                                ? Integer.parseInt(s.getNroCalle())
                                                : null)
                                );
                                ss.setBarrio(s.getBarrio());

                                ss.setNroLocalidad(s.getNroLocalidad());
                                ss.setNomLocalidad(s.getNomLocalidad());
                                ss.setCodProvincia(s.getCodProvincia());
                                ss.setNomProvincia(s.getNomProvincia());
                                ss.setCodPostal(s.getCodPostal());
                                ss.setTelefonos(s.getTelefonos());

                                ss.setTotalComensales(s.getTotalComensales());
                                ss.setMinTolerenciaReserva(s.getMinTolerenciaReserva());

                                // =========================
                                // Zonas
                                // =========================
                                if (s.getZonas() != null) {
                                    ss.setZonas(
                                            s.getZonas().stream()
                                                    .map(z -> {
                                                        ZonaBean zb = new ZonaBean();
                                                        zb.setCodZona(z.getCodZona());
                                                        zb.setDescZona(z.getNomZona());
                                                        zb.setCantComensales(z.getCantComensales());
                                                        zb.setPermiteMenores(z.isPermiteMenores());
                                                        zb.setHabilitada(z.isHabilitada());
                                                        return zb;
                                                    })
                                                    .toList()
                                    );
                                }

                                // =========================
                                // Turnos
                                // =========================
                                if (s.getTurnos() != null) {
                                    ss.setTurnos(
                                            s.getTurnos().stream()
                                                    .map(t -> {
                                                        TurnoBean tb = new TurnoBean();
                                                        tb.setHoraDesde(t.getHoraDesde());
                                                        tb.setHoraHasta(t.getHoraHasta());
                                                        return tb;
                                                    })
                                                    .toList()
                                    );
                                }

                                // =========================
                                // Zonas por turno
                                // =========================
                                if (s.getZonasTurnos() != null) {
                                    ss.setZonasTurnos(
                                            s.getZonasTurnos().stream()
                                                    .map(zt -> {
                                                        ZonaTurnoBean ztb = new ZonaTurnoBean();
                                                        ztb.setCodZona(zt.getCodZona());
                                                        ztb.setHoraDesde(zt.getHoraDesde());
                                                        ztb.setPermiteMenores(zt.isPermiteMenores());
                                                        return ztb;
                                                    })
                                                    .toList()
                                    );
                                }

                                // =========================
                                // Especialidades
                                // =========================
                                if (s.getEspecialidades() != null) {
                                    ss.setEspecialidades(
                                            s.getEspecialidades().stream()
                                                    .map(e -> {
                                                        SyncEspecialidadBean eb = new SyncEspecialidadBean();
                                                        eb.setNroRestriccion(e.getNroRestriccion());
                                                        eb.setNomRestriccion(e.getNomRestriccion());
                                                        eb.setHabilitada(e.isHabilitada());
                                                        return eb;
                                                    })
                                                    .toList()
                                    );
                                }

                                // =========================
                                // Tipos de Comidas
                                // =========================
                                if (s.getTiposComidas() != null) {
                                    ss.setTiposComidas(
                                            s.getTiposComidas().stream()
                                                    .map(tc -> {
                                                        SyncTipoComidaBean tcb = new SyncTipoComidaBean();
                                                        tcb.setNroTipoComida(tc.getNroTipoComida());
                                                        tcb.setNomTipoComida(tc.getNomTipoComida());
                                                        tcb.setHabilitado(tc.isHabilitado());
                                                        return tcb;
                                                    })
                                                    .toList()
                                    );
                                }

                                // =========================
                                // Estilos
                                // =========================
                                if (s.getEstilos() != null) {
                                    ss.setEstilos(
                                            s.getEstilos().stream()
                                                    .map(est -> {
                                                        SyncEstiloBean estb = new SyncEstiloBean();
                                                        estb.setNroEstilo(est.getNroEstilo());
                                                        estb.setNomEstilo(est.getNomEstilo());
                                                        estb.setHabilitado(est.isHabilitado());
                                                        return estb;
                                                    })
                                                    .toList()
                                    );
                                }

                                return ss;
                            })
                            .toList()
            );
        }

        return sync;
    }
}