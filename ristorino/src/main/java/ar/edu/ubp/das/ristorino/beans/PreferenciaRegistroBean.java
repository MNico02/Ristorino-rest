package ar.edu.ubp.das.ristorino.beans;

import java.util.List;
//sirve para registrar la multiples preferencias en el registrar usuario
public class PreferenciaRegistroBean {

    private int codCategoria;
    private List<Integer> dominios;

    public int getCodCategoria() {
        return codCategoria;
    }

    public void setCodCategoria(int codCategoria) {
        this.codCategoria = codCategoria;
    }

    public List<Integer> getDominios() {
        return dominios;
    }

    public void setDominios(List<Integer> dominios) {
        this.dominios = dominios;
    }
}
