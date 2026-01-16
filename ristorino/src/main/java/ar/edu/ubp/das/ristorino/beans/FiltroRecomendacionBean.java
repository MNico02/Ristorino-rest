package ar.edu.ubp.das.ristorino.beans;

public class FiltroRecomendacionBean {

    private String tipoComida;
    private String ciudad;
    private String provincia;
    private String momentoDelDia;
    private String rangoPrecio;
    private Integer cantidadPersonas;
    private String tieneMenores;
    private String restriccionesAlimentarias;
    private String preferenciasAmbiente;
    private Integer nroCliente;
    private String nombreRestaurante;

    // --- Getters y Setters ---
    public String getTipoComida() { return tipoComida; }
    public void setTipoComida(String tipoComida) { this.tipoComida = tipoComida; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }

    public String getMomentoDelDia() { return momentoDelDia; }
    public void setMomentoDelDia(String momentoDelDia) { this.momentoDelDia = momentoDelDia; }

    public String getRangoPrecio() { return rangoPrecio; }
    public void setRangoPrecio(String rangoPrecio) { this.rangoPrecio = rangoPrecio; }

    public Integer getCantidadPersonas() { return cantidadPersonas; }
    public void setCantidadPersonas(Integer cantidadPersonas) { this.cantidadPersonas = cantidadPersonas; }

    public String getTieneMenores() { return tieneMenores; }
    public void setTieneMenores(String tieneMenores) { this.tieneMenores = tieneMenores; }

    public String getRestriccionesAlimentarias() { return restriccionesAlimentarias; }
    public void setRestriccionesAlimentarias(String restriccionesAlimentarias) { this.restriccionesAlimentarias = restriccionesAlimentarias; }

    public String getPreferenciasAmbiente() { return preferenciasAmbiente; }
    public void setPreferenciasAmbiente(String preferenciasAmbiente) { this.preferenciasAmbiente = preferenciasAmbiente; }

    public Integer getNroCliente() { return nroCliente; }
    public void setNroCliente(Integer nroCliente) { this.nroCliente = nroCliente; }

    public String getNombreRestaurante() {
        return nombreRestaurante;
    }
    public void setNombreRestaurante(String nombreRestaurante) {
        this.nombreRestaurante = nombreRestaurante;
    }
}