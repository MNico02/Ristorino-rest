package ar.edu.ubp.das.ristorino.beans;

public class ReservaRestauranteBean {
    private SolicitudClienteBean solicitudCliente;
    private ReservaBean reserva;

    public SolicitudClienteBean getSolicitudCliente() {
        return solicitudCliente;
    }

    public void setSolicitudCliente(SolicitudClienteBean solicitudCliente) {
        this.solicitudCliente = solicitudCliente;
    }

    public ReservaBean getReserva() {
        return reserva;
    }

    public void setReserva(ReservaBean reserva) {
        this.reserva = reserva;
    }
}
