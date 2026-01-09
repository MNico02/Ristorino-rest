package ar.edu.ubp.das.ristorino.service;


import ar.edu.ubp.das.ristorino.beans.FiltroRecomendacionBean;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class GeminiService {

    private static final String API_KEY = "AIzaSyAOEXO331ZksWR2ke3P7zdSd90MPa_STuM";
    //clave original AIzaSyBfl_sUaEj1km5TX2dq_j7mtVmHlLm3O5A
    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;

    public FiltroRecomendacionBean interpretarTexto(String textoUsuario) throws Exception {

        String prompt = """
            Analiza el siguiente texto del usuario que busca un restaurante.
            Devuelve SOLO un JSON **válido** con los siguientes campos exactamente:
            {
              "tipoComida": "",
              "momentoDelDia": "",
              "ciudad": "",
              "provincia": "",
              "rangoPrecio": "",
              "tieneMenores": "",
              "restriccionesAlimentarias": "",
              "preferenciasAmbiente": "",
              "cantidadPersonas": ""
            }
            Texto: "%s"
        """.formatted(textoUsuario);

        String requestBody = """
        {
          "contents": [
            {
              "parts": [{"text": "%s"}]
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
        while ((line = br.readLine()) != null) response.append(line);
        br.close();

        if (status != 200) {
            throw new IOException("Error HTTP " + status + ": " + response);
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response.toString());
        String text = node.at("/candidates/0/content/parts/0/text").asText();

        text = text.trim();
        if (text.startsWith("```")) {
            text = text.replaceAll("```json", "").replaceAll("```", "").trim();
        }



        try {
            return mapper.readValue(text, FiltroRecomendacionBean.class);
        } catch (Exception ex) {
            System.err.println("Error parseando JSON IA: " + ex.getMessage());
            System.err.println("Texto devuelto por Gemini: " + text);
            throw new RuntimeException("Respuesta IA inválida o mal formada.");
        }
    }


    public String generarTextoPromocional(String textoBase, String idioma, Integer nroRestaurante, Integer nroSucursal) throws Exception {

        String prompt = String.format("""
    Eres un redactor gastronómico experto en marketing culinario 🍽️.
    Tu tarea es crear un texto PROMOCIONAL muy atractivo, breve y natural (entre 300 y 600 caracteres) en idioma %s.

    Basate en la siguiente idea o campaña del restaurante:
    👉 "%s"

    Instrucciones:
    - Escribe en tono entusiasta y cercano, como una publicación de redes sociales.
    - Usa emojis relacionados con comida o celebración (🥩🍕🍝🍔🍷🍰🔥🎉, etc.), pero sin abusar.
    - Si la información lo permite, destacá la propuesta (precio, combo, tipo de comida o experiencia).
    - Si hay datos del restaurante o sucursal, podés mencionarlos de forma natural (ej: “en nuestra sucursal del centro”).
    - Cierra el texto con una invitación atractiva (por ejemplo: “¡Te esperamos hoy!” o “No te lo pierdas 🍴”).

    Devuelve solo el texto final, sin comillas ni formato adicional.
""", idioma, textoBase);


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