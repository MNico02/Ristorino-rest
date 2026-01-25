package ar.edu.ubp.das.ristorino.clients;

import ar.edu.ubp.das.ristorino.beans.HorarioBean;
import ar.edu.ubp.das.ristorino.beans.SoliHorarioBean;

import java.util.List;

public interface DisponibilidadClient {
    List<HorarioBean> obtenerDisponibilidad(SoliHorarioBean soli);
}
