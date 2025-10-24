package ar.edu.ubp.das.ristorino.repositories;

import ar.edu.ubp.das.ristorino.beans.ClienteBean;
import ar.edu.ubp.das.ristorino.beans.LoginBean;
import ar.edu.ubp.das.ristorino.components.SimpleJdbcCallFactory;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;


@Repository
public class RistorinoRepository {
    @Autowired
    private SimpleJdbcCallFactory jdbcCallFactory;

    @Value("${security.jwt.secret}")
    private String jwtSecret;


    public String registrarCliente(ClienteBean clienteBean) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("apellido", clienteBean.getApellido())
                .addValue("nombre", clienteBean.getNombre())
                .addValue("clave", clienteBean.getClave())
                .addValue("correo", clienteBean.getCorreo())
                .addValue("telefonos", clienteBean.getTelefonos())
                .addValue("nom_localidad", clienteBean.getNomLocalidad())
                .addValue("nom_provincia", clienteBean.getNomProvincia());

        try {
            jdbcCallFactory.execute("registrar_cliente", "dbo", params);
            return "Cliente registrado correctamente.";
        } catch (Exception e) {

            return "Error al registrar cliente: " + e.getMessage();
        }

    }


    public String login(LoginBean loginBean) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("correo", loginBean.getCorreo())
                .addValue("clave", loginBean.getClave());

        try {
            Map<String, Object> out = jdbcCallFactory.executeWithOutputs("login_cliente", "dbo", params);
            Integer loginValido = (Integer) out.get("login_valido");

            if (loginValido != null && loginValido == 1) {
                return generarToken(loginBean.getCorreo());
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al loguearse: " + e.getMessage());
        }
    }


    private String generarToken(String correo) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + 1000 * 60 * 60 * 2); // 2 horas

        return Jwts.builder()
                .setSubject(correo)
                .setIssuedAt(ahora)
                .setExpiration(expiracion)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }
}
