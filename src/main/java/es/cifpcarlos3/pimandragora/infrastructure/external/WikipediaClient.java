package es.cifpcarlos3.pimandragora.infrastructure.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.cifpcarlos3.pimandragora.domain.entities.Author;
import es.cifpcarlos3.pimandragora.infrastructure.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class WikipediaClient {

    private final HttpClient httpClient;
    private final ObjectMapper mapper = JsonMapper.get();

    public WikipediaClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }


    public void enrichAuthor(Author author) {
        if (author.getFullName() == null || author.getFullName().isBlank()) return;


        String formattedName = author.getFullName().trim().replace(" ", "_");
        String url = "https://es.wikipedia.org/api/rest_v1/page/summary/" + formattedName;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .header("User-Agent", "MandragoraBackoffice/1.0 (ProyectoEstudiante; DAM)")
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = mapper.readTree(response.body());

                String bio = root.path("extract").asText("Biografía no disponible.");


                author.setBio(bio);

            } else {
                author.setBio("No se encontró información para este autor en Wikipedia.");
            }

        } catch (Exception e) {

            System.err.println("Error consultando Wikipedia para: " + author.getFullName() + " - " + e.getMessage());
            author.setBio("Error al conectar con el servicio de biografías.");
        }
    }
}