package ar.edu.ubp.das.ristorino.resources;


import ar.edu.ubp.das.ristorino.repositories.RistorinoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("ristorino")
public class RistorinoResource {
    @Autowired
    private RistorinoRepository ristorinoRepository;

}
