package ar.edu.ubp.das.ristorino.service;


import ar.edu.ubp.das.ristorino.beans.FiltroRecomendacionBean;
import ar.edu.ubp.das.ristorino.repositories.RistorinoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class GeminiService {

    @Autowired
    private RistorinoRepository ristorinoRepository;
    private static final String API_KEY = "";
    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;

    public FiltroRecomendacionBean interpretarTexto(String textoUsuario) throws Exception {

        String promptBase =
                ristorinoRepository.getPromptIA("BUSQUEDA");
        String prompt = promptBase
                .replace("{TEXTO_BASE}", textoUsuario);
        String requestBody = """
    {
      "contents": [
        {
          "parts": [
            { "text": "%s" }
          ]
        }
      ]
    }
    """.formatted(prompt.replace("\"", "\\\""));

        URL url = new URL(GEMINI_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBody.getBytes(StandardCharsets.UTF_8));
        }

        int status = conn.getResponseCode();
        InputStream input = (status >= 200 && status < 300)
                ? conn.getInputStream()
                : conn.getErrorStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        br.close();

        if (status != 200) {
            throw new IOException("Error HTTP " + status + ": " + response);
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response.toString());
        String text = node.at("/candidates/0/content/parts/0/text").asText();

        text = text.trim();
        if (text.startsWith("```")) {
            text = text.replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();
        }

        try {
            System.out.println("🔮 JSON IA = " + text);
            return mapper.readValue(text, FiltroRecomendacionBean.class);
        } catch (Exception ex) {
            System.err.println("❌ Error parseando JSON IA: " + ex.getMessage());
            System.err.println("Texto devuelto por Gemini: " + text);
            throw new RuntimeException("Respuesta IA inválida o mal formada.");
        }
    }

    @Transactional
    public Map<String, Object> generarContenidosPromocionalesBatch() {

        var pendientes = ristorinoRepository.obtenerContenidosPendientes();

        if (pendientes == null || pendientes.isEmpty()) {
            return Map.of("mensaje", "No hay contenidos pendientes para generar.");
        }

        int generados = 0;

        for (Map<String, Object> row : pendientes) {

            String textoBase = (String) row.get("contenido_a_publicar");
            Integer nroContenido = (Integer) row.get("nro_contenido");
            Integer nroIdioma = (Integer) row.get("nro_idioma");

            String idioma = (nroIdioma != null && nroIdioma == 2)
                    ? "English"
                    : "Spanish";

            try {
                String textoGenerado = generarTextoPromocional(
                        textoBase,
                        idioma,
                        (Integer) row.get("nro_restaurante"),
                        (Integer) row.get("nro_sucursal")
                );

                ristorinoRepository.actualizarContenidoPromocional(
                        nroContenido,
                        textoGenerado
                );

                generados++;

            } catch (Exception e) {
                System.err.println(
                        "❌ Error generando contenido IA nro_contenido=" + nroContenido
                );
                e.printStackTrace();
            }
        }

        return Map.of(
                "mensaje", "Contenidos generados correctamente.",
                "cantidad", generados
        );
    }


    public String generarTextoPromocional(String textoBase, String idioma, Integer nroRestaurante, Integer nroSucursal) throws Exception {
        System.out.println("textoBase = " + textoBase);
        System.out.println("idioma = " + idioma);

        //  Buscar prompt desde BD
        String promptBase =
                ristorinoRepository.getPromptIA("PROMOCION");

        // idioma de salida
        String idiomaSalida =
                "English".equalsIgnoreCase(idioma) ? "English" : "Spanish";

        //  Reemplazar variables del prompt
        String prompt = promptBase
                .replace("{TEXTO_BASE}", textoBase)
                .replace("{IDIOMA_SALIDA}", idiomaSalida);


        String requestBody = """
    {
      "contents": [
        { "parts": [ { "text": "%s" } ] }
      ]
    }
    """.formatted(prompt.replace("\"", "\\\""));

        URL url = new URL(GEMINI_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBody.getBytes(StandardCharsets.UTF_8));
        }

        int status = conn.getResponseCode();
        InputStream input = (status >= 200 && status < 300)
                ? conn.getInputStream()
                : conn.getErrorStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) response.append(line);
        br.close();

        if (status != 200)
            throw new IOException("Error HTTP " + status + ": " + response);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response.toString());
        String texto = node.at("/candidates/0/content/parts/0/text").asText().trim();

        if (texto.startsWith("```"))
            texto = texto.replaceAll("```json", "").replaceAll("```", "").trim();

        return texto;
    }


}