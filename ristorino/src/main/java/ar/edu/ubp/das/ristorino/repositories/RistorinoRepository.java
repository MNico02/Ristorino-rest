package ar.edu.ubp.das.ristorino.repositories;

import ar.edu.ubp.das.ristorino.components.SimpleJdbcCallFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RistorinoRepository {
    @Autowired
    private SimpleJdbcCallFactory jdbcCallFactory;
}
