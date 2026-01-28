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


    private static final String API_KEY = "AIzaSyA4LXo6RM5obvQx5120B6z-DGPMAi7aj3Y";
    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;

    public FiltroRecomendacionBean interpretarTexto(String textoUsuario) throws Exception {

        String prompt = """
Analizá el texto del usuario que busca un restaurante.
El texto puede estar en español o en inglés.

Tu objetivo es INTERPRETAR LA INTENCIÓN del usuario y mapearla a filtros
compatibles con una base de datos de restaurantes y sucursales.

REGLAS GENERALES (OBLIGATORIAS):
- NO inventes información que el usuario no menciona.
- Normalizá sinónimos a valores simples.
- Si un dato no está claro, dejá el campo vacío ("").
- Devolvé SIEMPRE un JSON válido.
- NO agregues explicaciones, comentarios, texto extra ni markdown.

-----------------------------------
NORMALIZACIÓN DE PRECIO:
-----------------------------------
- "barato", "económico", "low cost", "cheap" → rangoPrecio = "bajo"
- "precio medio", "normal", "average" → rangoPrecio = "medio"
- "caro", "lujoso", "premium", "expensive" → rangoPrecio = "alto"

-----------------------------------
NORMALIZACIÓN DE HORARIO:
-----------------------------------
- "desayuno", "mañana", "breakfast" → momentoDelDia = "mañana"
- "almuerzo", "mediodía", "lunch" → momentoDelDia = "mediodía"
- "tarde", "merienda" → momentoDelDia = "tarde"
- "cena", "noche", "dinner" → momentoDelDia = "noche"

-----------------------------------
UBICACIÓN (IMPORTANTE):
-----------------------------------
- Si menciona una CIUDAD o PROVINCIA clara, completar ciudad / provincia.
- Si menciona un BARRIO o ZONA (ej: Güemes, Centro, Nueva Córdoba)
  y NO hay campo específico para barrio,
  usar el campo "ciudad" para almacenar ese valor.
  (Ejemplo: ciudad = "Güemes")

-----------------------------------
RESTAURANTE / SUCURSAL:
-----------------------------------
- Si menciona un nombre propio que parece restaurante o sucursal,
  completar nombreRestaurante.
- NO confundir tipo de comida con nombre de restaurante.

-----------------------------------
PERSONAS Y MENORES:
-----------------------------------
- Si menciona cantidad de personas, usar SOLO el número en cantidadPersonas.
- Si menciona niños, familia, menores, kids → tieneMenores = "si".
- Si menciona solo adultos → tieneMenores = "no".

-----------------------------------
RESTRICCIONES ALIMENTARIAS:
-----------------------------------
- Mapear a restriccionesAlimentarias valores como:
  vegetariano, vegano, sin gluten, kosher, halal, etc.

-----------------------------------
AMBIENTE:
-----------------------------------
- Mapear preferenciasAmbiente con valores como:
  tranquilo, familiar, romántico, bar, moderno, gourmet, informal.

-----------------------------------
TIPO DE COMIDA:
-----------------------------------
- Si menciona un tipo de comida (italiana, japonesa, mexicana, rápida, etc.)
  completar tipoComida.

-----------------------------------
DEVOLVÉ EXACTAMENTE ESTE JSON
(con estos campos, sin agregar ni quitar ninguno):

{
  "tipoComida": "",
  "momentoDelDia": "",
  "ciudad": "",
  "provincia": "",
  "barrioZona": "",
  "rangoPrecio": "",
  "tieneMenores": "",
  "restriccionesAlimentarias": "",
  "preferenciasAmbiente": "",
  "cantidadPersonas": "",
  "nombreRestaurante": "",
  "horarioFlexible": false/true
}

Texto del usuario:
"%s"
""".formatted(textoUsuario);

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