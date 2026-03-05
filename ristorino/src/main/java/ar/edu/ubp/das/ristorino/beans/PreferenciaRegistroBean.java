package ar.edu.ubp.das.ristorino.beans;

import java.util.List;
import lombok.Data;

@Data
//sirve para registrar la multiples preferencias en el registrar usuario
public class PreferenciaRegistroBean {

    private int codCategoria;
    private List<Integer> dominios;
}
