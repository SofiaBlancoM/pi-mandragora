package es.cifpcarlos3.pimandragora.infrastructure.data.supabase;

import es.cifpcarlos3.pimandragora.presentation.app.config.AppConfig;
import es.cifpcarlos3.pimandragora.presentation.app.config.PropertyKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SupabaseHttpClient {

    private static final Logger log =
            LoggerFactory.getLogger(SupabaseHttpClient.class);

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String baseUrl = AppConfig.supabaseUrl();
    private final String anonKey = AppConfig.getProperty(PropertyKey.SUPABASE_ANON_KEY);

    public HttpRequest.Builder buildRequest(String path) {
        String bearer = SupabaseSession.hasToken()
                ? SupabaseSession.getAccessToken()
                : anonKey;

        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("apikey", anonKey)
                .header("Authorization", "Bearer " + bearer);
    }

    public HttpClient client() {
        return httpClient;
    }

    public HttpResponse<byte[]> sendBytes(HttpRequest request) {
        return send(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> handler) {
        long start = System.currentTimeMillis();

        try {
            log.debug("HTTP {} {}", request.method(), request.uri());

            HttpResponse<T> response = httpClient.send(request, handler);

            long elapsed = System.currentTimeMillis() - start;
            log.debug(
                    "HTTP {} {} -> {} ({} ms)",
                    request.method(),
                    request.uri(),
                    response.statusCode(),
                    elapsed
            );

            ensureSuccess(response);
            return response;

        } catch (Exception e) {
            log.error(
                    "HTTP {} {} failed",
                    request.method(),
                    request.uri(),
                    e
            );
            throw new RuntimeException("HTTP request failed: " + request.uri(), e);
        }
    }

    public void ensureSuccess(HttpResponse<?> response) {
        if (response.statusCode() >= 300) {
            log.error(
                    "HTTP {} returned status {}",
                    response.uri(),
                    response.statusCode()
            );

            String body = (response.body() != null)
                    ? response.body().toString()
                    : "";

            throw new RuntimeException(
                    "Request failed: " + response.statusCode() + " - " + body
            );
        }
    }

    public HttpResponse<String> sendJson(HttpRequest request) {
        return send(request, HttpResponse.BodyHandlers.ofString());
    }
}
