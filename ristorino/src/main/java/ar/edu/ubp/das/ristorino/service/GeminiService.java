/*package ar.edu.ubp.das.ristorino.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@Service
public class GeminiService {


    private static final String API_KEY = "AIzaSyBfl_sUaEj1km5TX2dq_j7mtVmHlLm3O5A";

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + API_KEY;

    public Map<String, String> interpretarTexto(String textoUsuario) throws Exception {

        // 1️⃣ Crear el prompt que queremos enviar
        String prompt = """
            Analiza el siguiente texto de un usuario que busca un restaurante.
            Devuelve SOLO un JSON estructurado con los siguientes campos:
            {
              "tipoComida": "",
              "momentoDelDia": "",
              "ciudad": "",
              "preferencias": ""
            }
            Texto: "%s"
        """.formatted(textoUsuario);

        // 2️⃣ Construir el JSON de la petición
        String requestBody = """
        {
          "contents": [
            {
              "parts": [
                {"text": "%s"}
              ]
            }
          ]
        }
        """.formatted(prompt);

        // 3️⃣ Preparar conexión HTTP
        URL url = new URL(GEMINI_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBody.getBytes());
        }

        // 4️⃣ Leer respuesta
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        br.close();

        // 5️⃣ Parsear JSON devuelto por Gemini
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response.toString());
        String text = node.at("/candidates/0/content/parts/0/text").asText();

        // Gemini devuelve el JSON estructurado como texto
        return mapper.readValue(text, Map.class);
    }
}*/