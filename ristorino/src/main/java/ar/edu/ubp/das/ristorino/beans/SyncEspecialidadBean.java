package ar.edu.ubp.das.ristorino.beans;

public class SyncEspecialidadBean {
    private int nroRestriccion;
    private String nomRestriccion;
    private boolean habilitada;

    public int getNroRestriccion() { return nroRestriccion; }
    public void setNroRestriccion(int nroRestriccion) { this.nroRestriccion = nroRestriccion; }
    public String getNomRestriccion() { return nomRestriccion; }
    public void setNomRestriccion(String nomRestriccion) { this.nomRestriccion = nomRestriccion; }
    public boolean isHabilitada() { return habilitada; }
    public void setHabilitada(boolean habilitada) { this.habilitada = habilitada; }
}
