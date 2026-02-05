package ar.edu.ubp.das.ristorino.repositories;

import ar.edu.ubp.das.ristorino.beans.*;
import ar.edu.ubp.das.ristorino.components.SimpleJdbcCallFactory;
import ar.edu.ubp.das.ristorino.service.GeminiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Types;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Repository
public class RistorinoRepository {
    @Autowired
    private SimpleJdbcCallFactory jdbcCallFactory;
   // @Autowired
    //private GeminiService geminiService;
    @Value("${security.jwt.secret}")
    private String jwtSecret;


    public String registrarCliente(ClienteBean clienteBean) {

        String preferenciasJson = null;

        try {
            if (clienteBean.getPreferencias() != null && !clienteBean.getPreferencias().isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                preferenciasJson = mapper.writeValueAsString(clienteBean.getPreferencias());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error serializando preferencias", e);
        }

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("apellido", clienteBean.getApellido())
                .addValue("nombre", clienteBean.getNombre())
                .addValue("clave", clienteBean.getClave())
                .addValue("correo", clienteBean.getCorreo())
                .addValue("telefonos", clienteBean.getTelefonos())
                .addValue("nom_localidad", clienteBean.getNomLocalidad())
                .addValue("nom_provincia", clienteBean.getNomProvincia())
                .addValue("observaciones", clienteBean.getObservaciones())

                // 🔹 LEGADO (opcional)
                .addValue("cod_categoria", clienteBean.getCodCategoria())
                .addValue("nro_valor_dominio", clienteBean.getNroValorDominio())

                // 🔹 CLAVE: JSON de preferencias
                .addValue("preferencias_json", preferenciasJson);
        System.out.println("Preferencias recibidas: " + clienteBean.getPreferencias());
        try {
            jdbcCallFactory.execute("registrar_cliente", "dbo", params);
            return "Cliente registrado correctamente.";
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar cliente", e);
        }
    }


    public Optional<SolicitudClienteBean> getClienteCorreo(String correo) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("correo", correo);

        SolicitudClienteBean cliente = jdbcCallFactory.executeSingle(
                "get_cliente_por_correo",
                "dbo",
                params,
                "result",
                SolicitudClienteBean.class
        );

        return Optional.ofNullable(cliente);
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
        Date expiracion = new Date(ahora.getTime() + 1000 * 60 * 60 * 2);

        return Jwts.builder()
                .setSubject(correo)
                .setIssuedAt(ahora)
                .setExpiration(expiracion)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public String obtenerPreferenciasClienteJson(String correo) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("correo", correo);

        List<Map<String, Object>> rs =
                jdbcCallFactory.executeList(
                        "sp_get_preferencias_cliente_por_email",
                        "dbo",
                        params
                );

        if (rs == null || rs.isEmpty()) {
            return null;
        }

        // El SP devuelve FOR JSON PATH, WITHOUT_ARRAY_WRAPPER
        Object json = rs.get(0).values().iterator().next();
        return json != null ? json.toString() : null;
    }


    /*--IA---*/
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
                .addValue("nombreRestaurante", filtro.getNombreRestaurante())
                .addValue("barrioZona", filtro.getBarrioZona())
                .addValue("horarioFlexible", filtro.getHorarioFlexible())
                .addValue("comida", filtro.getComida());

        try {
            return jdbcCallFactory.executeQueryAsMap(
                    "recomendar_restaurantes",
                    "dbo",
                    params,
                    "result"
            );
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener recomendaciones: " + e.getMessage(), e);
        }
    }

    /*--IA Promociones--*/
        // Obtener todos los contenidos pendientes de generación
        @SuppressWarnings("unchecked")
        public List<Map<String, Object>> obtenerContenidosPendientes() {
            return jdbcCallFactory.executeList("get_contenidos_a_generar", "dbo", new MapSqlParameterSource());
        }


        public void actualizarContenidoPromocional(Integer nroContenido, String textoGenerado) {
            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("nro_contenido", nroContenido)
                    .addValue("contenido_promocional", textoGenerado);
            jdbcCallFactory.execute("actualizar_contenido_promocional", "dbo", params);
        }


    /*-----*/
    public String getPromptIA(String tipoPrompt) {

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tipo_prompt", tipoPrompt);

        Map<String, Object> out =
                jdbcCallFactory.executeWithOutputs(
                        "sp_get_prompt_ia",
                        "dbo",
                        params
                );

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rs =
                (List<Map<String, Object>>) out.get("#result-set-1");

        if (rs == null || rs.isEmpty()) {
            throw new RuntimeException("No se encontró prompt IA para tipo " + tipoPrompt);
        }

        return rs.get(0).get("texto_prompt").toString();
    }

        /*--Clicks--*/
    public Map<String, Object> registrarClick(ClickBean clickBean) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("cod_restaurante", clickBean.getNroRestaurante())
                .addValue("nro_contenido", clickBean.getNroContenido())
                .addValue("correo_cliente", clickBean.getEmailUsuario() );

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
            log.info(" No hay clics para marcar como notificados.");
            return List.of();
        }

        // Convertimos la lista de clicks a JSON (como espera el SP)
        String jsonItems = clicks.stream()
                .map(click -> String.format("{\"nro_click\": %d}", click.getNroClick()))
                .collect(Collectors.joining(",", "[", "]"));

        // Armamos los parámetros
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("items_json", jsonItems)
                .addValue("nro_restaurante", nroRestaurante);

        try {
            //Ejecutamos el procedimiento con la factory
            List<ClickNotiBean> actualizados = jdbcCallFactory.executeQuery(
                    "sp_clicks_confirmar_notificados_obj",
                    "dbo",
                    params,
                    "clicks",
                    ClickNotiBean.class
            );

            log.info("{} clic(s) marcados como notificados para restaurante {}.",
                    actualizados.size(), nroRestaurante);

            return actualizados;

        } catch (Exception e) {
           log.error("Error al marcar clics como notificados: {}", e.getMessage(), e);
            return List.of();
        }
    }
        /*----------*/
        public BigDecimal guardarPromociones(List<ContenidoBean> promociones, int nroRestaurante) {
        // Crear JSON con las promociones
            ObjectMapper mapper = new ObjectMapper();
            ArrayNode jsonArray = mapper.createArrayNode();

            for (ContenidoBean c : promociones) {
                String codContenidoRestaurante = nroRestaurante + "-" + c.getNroContenido();

                ObjectNode promo = mapper.createObjectNode();
                promo.put("nro_contenido", c.getNroContenido());
                promo.put("nro_sucursal", c.getNroSucursal());
                promo.put("contenido_a_publicar", c.getContenidoAPublicar());
                promo.put("imagen_promocional", c.getImagenAPublicar());
                promo.put("cod_contenido_restaurante", codContenidoRestaurante);

                jsonArray.add(promo);
            }

            String promocionesJson = jsonArray.toString();


            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("nro_restaurante", nroRestaurante, Types.INTEGER)
                    .addValue("promociones_json", promocionesJson, Types.NVARCHAR);


            Map<String, Object> result = jdbcCallFactory.executeWithOutputs(
                    "ins_contenidos_restaurante_lote",
                    "dbo",
                    params);


            BigDecimal costoAplicado = (BigDecimal) result.get("costo_aplicado");
        return costoAplicado;

        }

    public Map<String, Object> guardarInfoRestaurante(SyncRestauranteBean restaurante) {
        try {
            ObjectMapper om = new ObjectMapper();


            String json = om.writeValueAsString(restaurante);
            System.out.println(json);
            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("json", json, Types.NVARCHAR);

            Map<String, Object> out =
                    jdbcCallFactory.executeWithOutputs("sync_restaurante_desde_json_full", "dbo", params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rs = (List<Map<String, Object>>) out.get("#result-set-1");

            return (rs != null && !rs.isEmpty()) ? rs.get(0) : Map.of("ok", true);

        } catch (Exception e) {
            throw new RuntimeException("Error en sync bulk restaurante JSON: " + e.getMessage(), e);
        }
    }

    public List<PromocionBean> obtenerPromociones(String codRestaurante, Integer nroSucursal) {

        String idiomaActual = LocaleContextHolder.getLocale().getLanguage();

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("cod_restaurante", codRestaurante)
                .addValue("nro_sucursal", nroSucursal)
                .addValue("idioma",idiomaActual);

        return jdbcCallFactory.executeQuery("get_promociones", "dbo", params,"", PromocionBean.class);
    }
    public String obtenerConfiguracionJson(int nroRestaurante) {

        log.debug("Obteniendo configuración JSON para restaurante #{}", nroRestaurante);

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_restaurante", nroRestaurante);

        try {
            List<Map<String, Object>> rs = jdbcCallFactory.executeList(
                    "sp_get_configuracion_restaurante",
                    "dbo",
                    params
            );

            if (rs == null || rs.isEmpty()) {
                throw new RuntimeException(
                        "No se encontró configuración para el restaurante #" + nroRestaurante
                );
            }

            // El SP retorna el JSON en la primera columna
            // Como usamos FOR JSON PATH, la columna tiene un nombre auto-generado
            // Tomamos el primer (y único) valor del Map
            Object jsonValue = rs.get(0).values().iterator().next();

            if (jsonValue == null) {
                throw new RuntimeException(
                        "Configuración vacía para restaurante #" + nroRestaurante
                );
            }

            String json = jsonValue.toString();

            log.debug("Configuración obtenida: {}", json);

            return json;

        } catch (Exception e) {
            log.error("Error al obtener configuración del restaurante #{}: {}",
                    nroRestaurante, e.getMessage(), e);
            throw new RuntimeException(
                    "Error al obtener configuración: " + e.getMessage(), e
            );
        }
    }
    public List<RestauranteHomeBean> listarRestaurantesHome() {
        String idiomaActual = LocaleContextHolder.getLocale().getLanguage();
        SqlParameterSource params = new MapSqlParameterSource().addValue("idioma_front",idiomaActual);
        List<Map<String, Object>> rs =
                jdbcCallFactory.executeList("sp_listar_restaurantes_home", "dbo", params);

        List<RestauranteHomeBean> restaurantes = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();

        if (rs != null) {
            for (Map<String, Object> row : rs) {

                RestauranteHomeBean restaurante = new RestauranteHomeBean();
                restaurante.setNroRestaurante(getStr(row.get("nro_restaurante")));
                restaurante.setRazonSocial(getStr(row.get("razon_social")));

                String categoriasJson = getStr(row.get("categorias_json"));

                if (categoriasJson != null && !categoriasJson.isEmpty()) {
                    try {

                        List<Map<String, String>> lista =
                                mapper.readValue(categoriasJson, new TypeReference<>() {});

                        Map<String, List<String>> categorias = new LinkedHashMap<>();

                        for (Map<String, String> c : lista) {
                            String categoria = c.get("categoria");
                            String valores = c.get("valores");

                            categorias.put(
                                    categoria,
                                    Arrays.asList(valores.split(","))
                            );
                        }

                        restaurante.setCategorias(categorias);

                    } catch (Exception e) {
                        throw new RuntimeException("Error parseando categorias_json", e);
                    }
                }

                restaurantes.add(restaurante);
            }
        }

        return restaurantes;
    }

    public RestauranteBean obtenerRestaurantePorId(String nroRestaurante) throws JsonProcessingException {

        String idiomaActual = LocaleContextHolder.getLocale().getLanguage();


            SqlParameterSource params = new MapSqlParameterSource()
                .addValue("cod_restaurante", nroRestaurante)
                    .addValue("idioma_front",idiomaActual);

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
                s.setNroCalle(getStr(row.get("nro_calle")));
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
                s.setPreferencias(new ArrayList<>());


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
                t.setHoraDesde(getStr(row.get("hora_desde")));
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
                z.setNomZona(getStr(row.get("zona")));
                z.setCantComensales(getInt(row.get("cant_comensales")));
                z.setPermiteMenores(getBool(row.get("permite_menores")));
                z.setHabilitada(getBool(row.get("habilitada")));

                SucursalBean s = sucursalesMap.get(nroSuc);
                if (s != null) s.getZonas().add(z);
            }
        }

        // =========================
        // RS5: Preferencias por sucursal
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

    public List<CategoriaPreferenciaBean> obtenerCategoriasPreferencias() {
        String idiomaActual = LocaleContextHolder.getLocale().getLanguage();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("idioma_front",idiomaActual);

        Map<String, Object> out =
                jdbcCallFactory.executeWithOutputs(
                        "get_categorias_preferencias",
                        "dbo",params
                );

        // RS1: Categorías
        List<Map<String, Object>> rs1 = castRS(out.get("#result-set-1"));
        Map<Integer, CategoriaPreferenciaBean> categoriasMap = new LinkedHashMap<>();

        if (rs1 != null) {
            for (Map<String, Object> row : rs1) {
                CategoriaPreferenciaBean c = new CategoriaPreferenciaBean();
                Integer codCategoria = getInt(row.get("cod_categoria"));

                c.setCodCategoria(codCategoria);
                c.setNomCategoria(getStr(row.get("nom_categoria")));
                c.setDominios(new ArrayList<>());

                categoriasMap.put(codCategoria, c);
            }
        }

        // RS2: Dominios
        List<Map<String, Object>> rs2 = castRS(out.get("#result-set-2"));
        if (rs2 != null) {
            for (Map<String, Object> row : rs2) {
                Integer codCategoria = getInt(row.get("cod_categoria"));

                DominioCategoriaPreferenciaBean d =
                        new DominioCategoriaPreferenciaBean();
                d.setNroValorDominio(getInt(row.get("nro_valor_dominio")));
                d.setNomValorDominio(getStr(row.get("nom_valor_dominio")));

                CategoriaPreferenciaBean c = categoriasMap.get(codCategoria);
                if (c != null) {
                    c.getDominios().add(d);
                }
            }
        }

        return new ArrayList<>(categoriasMap.values());
    }

    public ReservaConfirmadaBean insReservaConfirmadaRistorino(ConfirmarReservaResponseBean body, ReservaBean reservaBean, int nroRestaurante) {
        if (body == null) throw new IllegalArgumentException("body null");
        if (!body.isSuccess()) throw new RuntimeException("No confirmada: " + body.getMensaje());
        if (body.getCodReserva() == null || body.getCodReserva().isBlank())
            throw new IllegalArgumentException("codReserva vacío");

        if (reservaBean == null) throw new IllegalArgumentException("reservaBean null");

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("correo", reservaBean.getCorreo(), Types.NVARCHAR)
                .addValue("cod_reserva_restaurante", body.getCodReserva(), Types.NVARCHAR)
                .addValue("fecha_reserva", java.sql.Date.valueOf(reservaBean.getFechaReserva()), Types.DATE)
                .addValue("hora_reserva", java.sql.Time.valueOf(reservaBean.getHoraReserva()), Types.TIME)
                .addValue("nro_restaurante", nroRestaurante, Types.INTEGER)
                .addValue("nro_sucursal", reservaBean.getIdSucursal(), Types.INTEGER)
                .addValue("cod_zona", reservaBean.getCodZona(), Types.INTEGER)
                .addValue("cant_adultos", reservaBean.getCantAdultos(), Types.INTEGER)
                .addValue("cant_menores", reservaBean.getCantMenores(), Types.INTEGER)
                .addValue("costo_reserva", BigDecimal.valueOf((double)reservaBean.getCostoReserva()), Types.DECIMAL)
                .addValue("cod_estado", 1, Types.INTEGER);

        ReservaConfirmadaBean saved = jdbcCallFactory.executeSingle(
                "ins_reserva_confirmada_ristorino",
                "dbo",
                params,
                "result",
                ReservaConfirmadaBean.class
        );

        if (saved == null) throw new RuntimeException("SP no devolvió fila insertada");
        return saved;
    }

    public Map<String, Object> modificarReservaRistorino(ModificarReservaReqBean req) {

        Map<String, Object> resp = new HashMap<>();


        if (req == null) {
            resp.put("success", false);
            resp.put("status", "INVALID");
            resp.put("message", "Request nulo.");
            return resp;
        }

        if (req.getCodReservaSucursal() == null || req.getCodReservaSucursal().isBlank()) {
            resp.put("success", false);
            resp.put("status", "INVALID");
            resp.put("message", "codReservaSucursal es obligatorio.");
            return resp;
        }

        if (req.getFechaReserva() == null) {
            resp.put("success", false);
            resp.put("status", "INVALID");
            resp.put("message", "fechaReserva es obligatoria.");
            return resp;
        }

        if (req.getHoraReserva() == null) {
            resp.put("success", false);
            resp.put("status", "INVALID");
            resp.put("message", "horaReserva es obligatoria.");
            return resp;
        }


        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("cod_reserva_sucursal", req.getCodReservaSucursal(), Types.VARCHAR)
                .addValue("fecha_reserva", java.sql.Date.valueOf(req.getFechaReserva()), Types.DATE)
                .addValue("hora_reserva", java.sql.Time.valueOf(req.getHoraReserva()), Types.TIME)
                .addValue("cod_zona", req.getCodZona(), Types.INTEGER)
                .addValue("cant_adultos", req.getCantAdultos(), Types.INTEGER)
                .addValue("cant_menores", req.getCantMenores(), Types.INTEGER)
                .addValue("costo_reserva",req.getCostoReserva(), Types.DECIMAL);

        try {
            Map<String, Object> out =
                    jdbcCallFactory.executeWithOutputs("modificar_reserva_ristorino_por_codigo_sucursal", "dbo", params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rs =
                    (List<Map<String, Object>>) out.get("#result-set-1");

            if (rs == null || rs.isEmpty()) {
                resp.put("success", false);
                resp.put("status", "ERROR");
                resp.put("message", "El SP no devolvió resultado (#result-set-1 vacío).");
                return resp;
            }

            Map<String, Object> row = rs.get(0);


            boolean success = false;
            Object vSuccess = row.get("success");
            if (vSuccess instanceof Boolean) success = (Boolean) vSuccess;
            else if (vSuccess instanceof Number) success = ((Number) vSuccess).intValue() == 1;
            else if (vSuccess != null) {
                String s = vSuccess.toString();
                success = "1".equals(s) || "true".equalsIgnoreCase(s);
            }

            String status = row.get("status") != null ? row.get("status").toString() : "UNKNOWN";
            String message = row.get("message") != null ? row.get("message").toString() : "Sin mensaje.";

            resp.put("success", success);
            resp.put("status", status);
            resp.put("message", message);

            return resp;

        } catch (Exception e) {
            resp.put("success", false);
            resp.put("status", "ERROR");
            resp.put("message", "Error ejecutando SP en Ristorino: " + e.getMessage());
            return resp;
        }
    }

    public ClienteRestauranteConfigBean getConfiguracionClienteReservas(int nroRestaurante) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_restaurante", nroRestaurante, Types.INTEGER);

        ClienteRestauranteConfigBean config = jdbcCallFactory.executeSingle(
                "sp_get_configuracion_cliente_reservas", // nombre SP
                "dbo",                                   // esquema
                params,
                "result",                                // alias (puede ser cualquiera)
                ClienteRestauranteConfigBean.class
        );

        if (config == null || config.getTipoCliente() == null) {
            throw new RuntimeException(
                    "No se pudo obtener configuración de cliente para restaurante " + nroRestaurante
            );
        }

        return config;
    }


    public ReservasClienteRespBean getReservasCliente(String correo) {

        String idiomaActual = LocaleContextHolder.getLocale().getLanguage();
        SqlParameterSource params1 = new MapSqlParameterSource()
                .addValue("correo_cliente", correo);
        List<ReservaClienteBean> reservas = jdbcCallFactory.executeQuery(
                "obtener_reservas_cliente_por_correo",
                "dbo",
                params1,
                "#result-set-1",
                ReservaClienteBean.class
        );
        SqlParameterSource params2 = new MapSqlParameterSource()
                .addValue("idioma_front", idiomaActual);
        List<EstadoBean> estados = jdbcCallFactory.executeQuery(
                "get_estados",
                "dbo",
                params2,
                "#result-set-1",
                EstadoBean.class
        );
        ReservasClienteRespBean res = new ReservasClienteRespBean();
        res.setReservas(reservas);
        res.setEstados(estados);
        System.out.println(res);
        return res;

    }

    public List<ZonaBean> getZonasSucursal(int nroRestaurante, int nroSucursal) {

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nro_restaurante", nroRestaurante)
                .addValue("nro_sucursal", nroSucursal);

        List<Map<String, Object>> rs = jdbcCallFactory.executeQueryAsMap(
                "get_zonas_sucursal",
                "dbo",
                params,
                "result"
        );

        List<ZonaBean> out = new ArrayList<>();
        for (Map<String, Object> row : rs) {
            ZonaBean z = new ZonaBean();

            z.setCodZona(getInt(row.get("codZona")));
            z.setNomZona(getStr(row.get("nomZona")));
            z.setCantComensales(getInt(row.get("cantComensales")));
            z.setPermiteMenores(getBool(row.get("permiteMenores")));
            z.setHabilitada(getBool(row.get("habilitada")));

            out.add(z);
        }
        return out;
    }

    public Map<String, Object> cancelarReservaRistorinoPorCodigo(String codReservaSucursal) {

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("cod_reserva_sucursal", codReservaSucursal);


        List<Map<String, Object>> rs = jdbcCallFactory.executeQueryAsMap(
                "cancelar_reserva_ristorino_por_codigo",
                "dbo",
                params,
                "result"
        );


        return rs.isEmpty()
                ? Map.of("success", false, "status", "ERROR", "message", "SP no devolvió resultado.")
                : rs.get(0);
    }

    public BigDecimal obtenerCostoVigente(String tipoCosto, String fecha) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("tipo_costo", tipoCosto)
                .addValue("fecha", java.sql.Date.valueOf(fecha));

        Map<String, Object> out;

        try {
            out = jdbcCallFactory.executeWithOutputs(
                    "obtener_costo_vigente",
                    "dbo",
                    params
            );
        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo costo vigente: " + e.getMessage(), e);
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rs =
                (List<Map<String, Object>>) out.get("#result-set-1");

        if (rs == null || rs.isEmpty()) {
            throw new RuntimeException("El SP no devolvió monto.");
        }

        Object montoObj = rs.get(0).get("monto");

        if (montoObj instanceof BigDecimal) {
            return (BigDecimal) montoObj;
        }

        return new BigDecimal(montoObj.toString());
    }

    public List<Integer> obtenerNrosActivos() {
        List<NroRestBean> restaurantes =
                jdbcCallFactory.executeQuery(
                        "obtener_nroRestaurantes",
                        "dbo",
                        "nroRestaurante",
                        NroRestBean.class
                );
        List<Integer> nros = restaurantes.stream()
                .map(NroRestBean::getNroRestaurante)
                .toList();
        return nros;
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