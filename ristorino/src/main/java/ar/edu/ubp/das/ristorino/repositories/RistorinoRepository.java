package ar.edu.ubp.das.ristorino.repositories;

import ar.edu.ubp.das.ristorino.beans.*;
import ar.edu.ubp.das.ristorino.components.SimpleJdbcCallFactory;
import ar.edu.ubp.das.ristorino.service.GeminiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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



    public Map<String, Object> registrarClick(ClickBean clickBean) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("cod_restaurante", clickBean.getNroRestaurante())
                .addValue("nro_contenido", clickBean.getNroContenido())
                .addValue("nro_cliente", null);

        Map<String, Object> resp = new HashMap<>();
        try {
            jdbcCallFactory.execute("registrar_click_contenido", "dbo", params);
            resp.put("success", true);
            resp.put("message", "Click registrado correctamente.");
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", "Error al registrar click: " + e.getMessage());
        }
        return resp;
    }

    public List<ClickNotiBean> obtenerClicksPendientes() {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_restaurante", null, Types.INTEGER)
                .addValue("top", null, Types.INTEGER);
        return jdbcCallFactory.executeQuery("sp_clicks_pendientes", "dbo", params,"", ClickNotiBean.class);
    }

    public List<ClickNotiBean> marcarClicksComoNotificados(List<ClickNotiBean> clicks, Integer nroRestaurante) {
        if (clicks == null || clicks.isEmpty()) {
            log.info("⚠️ No hay clics para marcar como notificados.");
            return List.of();
        }

        // 1️⃣ Convertimos la lista de clicks a JSON (como espera el SP)
        String jsonItems = clicks.stream()
                .map(click -> String.format("{\"nro_click\": %d}", click.getNroClick()))
                .collect(Collectors.joining(",", "[", "]"));

        // 2️⃣ Armamos los parámetros
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("items_json", jsonItems)
                .addValue("nro_restaurante", nroRestaurante);

        try {
            // 3️⃣ Ejecutamos el procedimiento con la factory
            List<ClickNotiBean> actualizados = jdbcCallFactory.executeQuery(
                    "sp_clicks_confirmar_notificados_obj",   // nombre del SP
                    "dbo",                                   // esquema
                    params,
                    "clicks",                                // alias del resultset (puede ser cualquiera)
                    ClickNotiBean.class                      // clase mapeada
            );

            log.info("{} clic(s) marcados como notificados para restaurante {}.",
                    actualizados.size(), nroRestaurante);

            return actualizados;

        } catch (Exception e) {
           log.error("Error al marcar clics como notificados: {}", e.getMessage(), e);
            return List.of();
        }
    }

    public List<PromocionBean> obtenerPromociones(Integer idRestaurante, Integer idSucursal) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_restaurante", idRestaurante)
                .addValue("nro_sucursal", idSucursal);

        return jdbcCallFactory.executeQuery("get_promociones", "dbo", params,"", PromocionBean.class);
    }

    public RestauranteBean obtenerRestaurantePorId(String nroRestaurante) throws JsonProcessingException {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("cod_restaurante", nroRestaurante);

        // 👇 Cambiamos al nombre real del SP con 5 result sets
        Map<String, Object> out =
                jdbcCallFactory.executeWithOutputs("get_restaurante_info", "dbo", params);

        // =========================
        // RS1: Restaurante
        // =========================
        List<Map<String, Object>> rs1 = castRS(out.get("#result-set-1"));
        if (rs1 == null || rs1.isEmpty()) return null;

        Map<String, Object> r1 = rs1.get(0);
        RestauranteBean restaurante = new RestauranteBean();
        restaurante.setNroRestaurante(getStr(r1.get("nro_restaurante")));
        restaurante.setRazonSocial(getStr(r1.get("razon_social")));

        // =========================
        // RS2: Sucursales + localidad/provincia
        // =========================
        List<Map<String, Object>> rs2 = castRS(out.get("#result-set-2"));
        Map<Integer, SucursalBean> sucursalesMap = new LinkedHashMap<>();

        if (rs2 != null) {
            for (Map<String, Object> row : rs2) {
                int nroSuc = getInt(row.get("nro_sucursal"));
                SucursalBean s = new SucursalBean();

                s.setNroSucursal(nroSuc);
                s.setNomSucursal(getStr(row.get("nom_sucursal")));
                s.setCalle(getStr(row.get("calle")));
                s.setNroCalle(getStr(row.get("nro_calle")));   // si es INT en bean, usa getInt
                s.setBarrio(getStr(row.get("barrio")));
                s.setNroLocalidad(getInt(row.get("nro_localidad")));
                s.setNomLocalidad(getStr(row.get("nom_localidad")));
                s.setCodProvincia(getInt(row.get("cod_provincia")));
                s.setNomProvincia(getStr(row.get("nom_provincia")));
                s.setCodPostal(getStr(row.get("cod_postal")));
                s.setTelefonos(getStr(row.get("telefonos")));
                s.setTotalComensales(getInt(row.get("total_comensales")));
                s.setMinTolerenciaReserva(getInt(row.get("min_tolerencia_reserva")));
                s.setCodSucursalRestaurante(getStr(row.get("cod_sucursal_restaurante")));

                // Inicializamos colecciones que vamos a llenar abajo
                s.setTurnos(new ArrayList<>());
                s.setZonas(new ArrayList<>());
                s.setPreferencias(new ArrayList<>()); // ⬅️ agregá esta lista al SucursalBean si no existe


                sucursalesMap.put(nroSuc, s);
            }
        }

        // =========================
        // RS3: Turnos por sucursal
        // =========================
        List<Map<String, Object>> rs3 = castRS(out.get("#result-set-3"));
        if (rs3 != null) {
            for (Map<String, Object> row : rs3) {
                int nroSuc = getInt(row.get("nro_sucursal"));
                TurnoBean t = new TurnoBean();
                t.setHoraDesde(getStr(row.get("hora_desde")));  // o getTime(...) si tu bean usa java.sql.Time
                t.setHoraHasta(getStr(row.get("hora_hasta")));
                t.setHabilitado(getBool(row.get("habilitado")));

                SucursalBean s = sucursalesMap.get(nroSuc);
                if (s != null) s.getTurnos().add(t);
            }
        }

        // =========================
        // RS4: Zonas por sucursal
        // =========================
        List<Map<String, Object>> rs4 = castRS(out.get("#result-set-4"));
        if (rs4 != null) {
            for (Map<String, Object> row : rs4) {
                int nroSuc = getInt(row.get("nro_sucursal"));
                ZonaBean z = new ZonaBean();
                z.setCodZona(getInt(row.get("cod_zona")));
                // El RS4 trae "desc_zona". Si tu bean tiene "nomZona", mapealo ahí:
                // z.setNomZona(getStr(row.get("desc_zona")));
                z.setDescZona(getStr(row.get("desc_zona")));
                z.setCantComensales(getInt(row.get("cant_comensales")));
                z.setPermiteMenores(getBool(row.get("permite_menores")));
                z.setHabilitada(getBool(row.get("habilitada")));

                SucursalBean s = sucursalesMap.get(nroSuc);
                if (s != null) s.getZonas().add(z);
            }
        }

        // =========================
        // RS5: Preferencias por sucursal
        // (con nom_valor_dominio y nom_categoria)
        // =========================
        List<Map<String, Object>> rs5 = castRS(out.get("#result-set-5"));
        if (rs5 != null) {
            for (Map<String, Object> row : rs5) {
                int nroSuc = getInt(row.get("nro_sucursal"));
                PreferenciaBean p = new PreferenciaBean();
                p.setCodCategoria(getInt(row.get("cod_categoria")));
                p.setNomCategoria(getStr(row.get("nom_categoria")));
                p.setNroValorDominio(getInt(row.get("nro_valor_dominio")));
                p.setNomValorDominio(getStr(row.get("nom_valor_dominio")));
                p.setNroPreferencia(getInt(row.get("nro_preferencia")));
                p.setObservaciones(getStr(row.get("observaciones")));

                SucursalBean s = sucursalesMap.get(nroSuc);
                if (s != null) s.getPreferencias().add(p);
            }
        }

        restaurante.setSucursales(new ArrayList<>(sucursalesMap.values()));
        return restaurante;
    }


    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> castRS(Object o) {
        return (o instanceof List) ? (List<Map<String, Object>>) o : null;
    }

    private static String getStr(Object o) {
        return (o == null) ? null : o.toString();
    }

    private static int getInt(Object o) {
        if (o == null) return 0;
        if (o instanceof Integer) return (Integer) o;
        if (o instanceof Long)    return ((Long) o).intValue();
        if (o instanceof Short)   return ((Short) o).intValue();
        if (o instanceof BigDecimal) return ((BigDecimal) o).intValue();
        try { return Integer.parseInt(o.toString()); } catch (Exception e) { return 0; }
    }

    private static Integer getIntObj(Object o) {
        return (o == null) ? null : getInt(o);
    }

    private static boolean getBool(Object o) {
        if (o == null) return false;
        if (o instanceof Boolean) return (Boolean) o;
        if (o instanceof Number)  return ((Number) o).intValue() != 0;
        return Boolean.parseBoolean(o.toString());
    }

    private static BigDecimal getBigDec(Object o) {
        if (o == null) return null;
        if (o instanceof BigDecimal) return (BigDecimal) o;
        if (o instanceof Number)     return BigDecimal.valueOf(((Number) o).doubleValue());
        try { return new BigDecimal(o.toString()); } catch (Exception e) { return null; }
    }

    /**
     * Devuelve un String "HH:mm:ss" a partir de columnas TIME (java.sql.Time),
     * LocalTime, Number (milis) o String ("HH:mm" / "HH:mm:ss"). Si no puede, null.
     * Útil para RS3 (turnos: hora_desde / hora_hasta).
     */
    private static String getTimeStr(Object o) {
        if (o == null) return null;
        try {
            if (o instanceof java.sql.Time) {
                return o.toString(); // ya viene "HH:mm:ss"
            }
            if (o instanceof java.time.LocalTime) {
                return o.toString(); // "HH:mm:ss.nnn" -> generalmente "HH:mm:ss"
            }
            if (o instanceof Number) {
                long ms = ((Number) o).longValue();
                return new java.sql.Time(ms).toString();
            }
            String s = o.toString().trim();
            // normalizamos: si viene "HH:mm", devolvemos "HH:mm:00"
            if (s.matches("^\\d{2}:\\d{2}$")) return s + ":00";
            if (s.matches("^\\d{2}:\\d{2}:\\d{2}$")) return s;
            // último intento: parsear LocalTime
            return java.time.LocalTime.parse(s).toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Devuelve un String ISO de fecha "yyyy-MM-dd" a partir de java.sql.Date, LocalDate o String.
     * No lo usa este SP, pero queda disponible si luego agregás fechas.
     */
    private static String getDateStr(Object o) {
        if (o == null) return null;
        try {
            if (o instanceof java.sql.Date) {
                return o.toString();
            }
            if (o instanceof java.time.LocalDate) {
                return o.toString();
            }
            String s = o.toString().trim();
            // Si ya viene ISO, lo devolvemos
            if (s.matches("^\\d{4}-\\d{2}-\\d{2}$")) return s;
            // Intento de parseo
            return java.time.LocalDate.parse(s).toString();
        } catch (Exception e) {
            return null;
        }
    }



}