package es.cifpcarlos3.pimandragora.infrastructure.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.cifpcarlos3.pimandragora.application.auth.AuthClient;
import es.cifpcarlos3.pimandragora.application.auth.dtos.AuthSessionDto;
import es.cifpcarlos3.pimandragora.application.auth.dtos.AuthUserDto;
import es.cifpcarlos3.pimandragora.application.auth.dtos.LoginRequest;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.SupabaseHttpClient;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.SupabaseSession;
import es.cifpcarlos3.pimandragora.infrastructure.json.JsonMapper;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

/**
 * Clase que implementa las llamadas http a supabase para las funcionalidades de autenticación que ofrece la plataforma
 */
public class SupabaseAuthClient implements AuthClient {

    private final SupabaseHttpClient supabaseHttpClient;
    private final ObjectMapper mapper = JsonMapper.get();

    public SupabaseAuthClient(SupabaseHttpClient supabaseHttpClient) {
        this.supabaseHttpClient = Objects.requireNonNull(supabaseHttpClient, "supabase es necesaria");
    }

    public AuthUserDto getCurrentUser() {
        try {
            HttpRequest request = supabaseHttpClient.buildRequest("/auth/v1/user")
                    .setHeader("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = supabaseHttpClient.sendJson(request);
            return mapper.readValue(response.body(), AuthUserDto.class);

        } catch (Exception ex) {
            throw new RuntimeException("No se ha podido obtener el usuario actual", ex);
        }
    }

    @Override
    public AuthSessionDto login(String email, String password) {
        try {
            String json = mapper.writeValueAsString(new LoginRequest(email, password));

            HttpRequest request = supabaseHttpClient.buildRequest("/auth/v1/token?grant_type=password")
                    .setHeader("Accept", "application/json")
                    .setHeader("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = supabaseHttpClient.sendJson(request);
            AuthSessionDto authSessionDto = mapper.readValue(response.body(), AuthSessionDto.class);

            SupabaseSession.setAccessToken(authSessionDto.accessToken());
            return authSessionDto;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void logout() {

        String token = SupabaseSession.getAccessToken();
        if (token == null || token.isBlank()) {
            SupabaseSession.clear();
            return;
        }

        try {
            HttpRequest request = supabaseHttpClient.buildRequest("/auth/v1/logout")
                    .setHeader("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = supabaseHttpClient.sendJson(request);

        } catch (Exception ex) {
            throw new RuntimeException("Failed to logout from Supabase", ex);
        } finally {
            SupabaseSession.clear();
        }
    }
}
