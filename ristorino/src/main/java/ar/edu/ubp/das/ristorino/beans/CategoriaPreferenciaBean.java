package ar.edu.ubp.das.ristorino.beans;

import java.util.List;

public class CategoriaPreferenciaBean {
    private Integer codCategoria;
    private String nomCategoria;
    private List<DominioCategoriaPreferenciaBean> dominios;

    public Integer getCodCategoria() {
        return codCategoria;
    }

    public void setCodCategoria(Integer codCategoria) {
        this.codCategoria = codCategoria;
    }

    public String getNomCategoria() {
        return nomCategoria;
    }

    public void setNomCategoria(String nomCategoria) {
        this.nomCategoria = nomCategoria;
    }

    public List<DominioCategoriaPreferenciaBean> getDominios() {
        return dominios;
    }

    public void setDominios(List<DominioCategoriaPreferenciaBean> dominios) {
        this.dominios = dominios;
    }
}
