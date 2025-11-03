package ar.edu.ubp.das.ristorino.repositories;

import ar.edu.ubp.das.ristorino.beans.*;
import ar.edu.ubp.das.ristorino.components.SimpleJdbcCallFactory;
import ar.edu.ubp.das.ristorino.service.GeminiService;
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
import java.util.HashMap;
import java.util.Map;
import java.util.List;


@Repository
public class RistorinoRepository {
    @Autowired
    private SimpleJdbcCallFactory jdbcCallFactory;
    @Autowired
    private GeminiService geminiService;
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

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> obtenerRecomendaciones(FiltroRecomendacionBean filtro) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tipoComida", filtro.getTipoComida())
                .addValue("ciudad", filtro.getCiudad())
                .addValue("provincia", filtro.getProvincia())
                .addValue("momentoDelDia", filtro.getMomentoDelDia())
                .addValue("rangoPrecio", filtro.getRangoPrecio())
                .addValue("cantidadPersonas", filtro.getCantidadPersonas())
                .addValue("tieneMenores", filtro.getTieneMenores())
                .addValue("restriccionesAlimentarias", filtro.getRestriccionesAlimentarias())
                .addValue("preferenciasAmbiente", filtro.getPreferenciasAmbiente())
                .addValue("nroCliente", filtro.getNroCliente());

        try {

            return jdbcCallFactory.executeQueryAsMap("recomendar_restaurantes", "dbo", params, "result");
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener recomendaciones: " + e.getMessage(), e);
        }
    }

    // Obtener todos los contenidos pendientes de generación
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> obtenerContenidosPendientes() {
        return jdbcCallFactory.executeList("get_contenidos_a_generar", "dbo", new MapSqlParameterSource());
    }

    // Actualizar un contenido con el texto generado y duración configurable
    public void actualizarContenidoPromocional(Integer nroContenido, String textoGenerado, int duracionHoras) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_contenido", nroContenido)
                .addValue("contenido_promocional", textoGenerado)
                .addValue("duracion_horas", duracionHoras); // 👈 nuevo parámetro

        jdbcCallFactory.execute("actualizar_contenido_promocional", "dbo", params);
    }

    // Generar todos los contenidos pendientes
    public Map<String, Object> generarContenidosPromocionales() {
        try {
            List<Map<String, Object>> pendientes = obtenerContenidosPendientes();

            if (pendientes == null || pendientes.isEmpty()) {
                return Map.of("mensaje", "No hay contenidos pendientes para generar.");
            }

            int generados = 0;
            for (Map<String, Object> row : pendientes) {
                String textoBase = (String) row.get("contenido_a_publicar");
                Integer nroContenido = (Integer) row.get("nro_contenido");
                Integer nroIdioma = (Integer) row.get("nro_idioma");

                String idioma = (nroIdioma != null && nroIdioma == 2) ? "English" : "Spanish";

                String textoGenerado = geminiService.generarTextoPromocional(
                        textoBase,
                        idioma,
                        (Integer) row.get("nro_restaurante"),
                        (Integer) row.get("nro_sucursal")
                );

                //Duración automática (24h por defecto)
                int duracion = 24;

                actualizarContenidoPromocional(nroContenido, textoGenerado, duracion);
                generados++;
            }

            return Map.of(
                    "mensaje", "Contenidos generados correctamente.",
                    "cantidad", generados
            );

        } catch (Exception e) {
            throw new RuntimeException("Error al generar contenidos promocionales: " + e.getMessage(), e);
        }
    }

    public String registrarClick(ClickBean clickBean) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_restaurante", clickBean.getNroRestaurante())
                .addValue("nro_idioma", clickBean.getNroIdioma())
                .addValue("nro_contenido", clickBean.getNroContenido())
                .addValue("nro_cliente", clickBean.getNroCliente())
                .addValue("costo_click",clickBean.getCostoClick());
        try {
            jdbcCallFactory.execute("registrar_click_contenido", "dbo", params);
            return "Click registrado correctamente.";
        } catch (Exception e) {

            return "Error al registrar cliente: " + e.getMessage();
        }

    }


    public List<PromocionBean> obtenerPromociones(Integer idRestaurante, Integer idSucursal) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_restaurante", idRestaurante)
                .addValue("nro_sucursal", idSucursal);

        return jdbcCallFactory.executeQuery("get_promociones", "dbo", params,"", PromocionBean.class);
    }



}