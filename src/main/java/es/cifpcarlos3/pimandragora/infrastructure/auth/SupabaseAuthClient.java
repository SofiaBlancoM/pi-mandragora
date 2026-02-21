package es.cifpcarlos3.pimandragora.infrastructure.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.cifpcarlos3.pimandragora.application.auth.AuthClient;
import es.cifpcarlos3.pimandragora.application.auth.dtos.AuthSessionDto;
import es.cifpcarlos3.pimandragora.application.auth.dtos.AuthUserDto;
import es.cifpcarlos3.pimandragora.application.auth.dtos.LoginRequest;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.SupabaseHttpClient;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.SupabaseSession;
import es.cifpcarlos3.pimandragora.infrastructure.json.JsonMapper;
import es.cifpcarlos3.pimandragora.presentation.app.config.AppConfig;
import es.cifpcarlos3.pimandragora.presentation.app.config.PropertyKey;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public class SupabaseAuthClient implements AuthClient {

    private final SupabaseHttpClient supabase;
    private final ObjectMapper mapper = JsonMapper.get();

    public SupabaseAuthClient(SupabaseHttpClient supabase) {
        this.supabase = Objects.requireNonNull(supabase, "supabase is required");
    }

    // ✅ NEW: get current user from Supabase Auth (source of truth for email + id)
    public AuthUserDto getCurrentUser() {
        String token = SupabaseSession.getAccessToken();
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("No access token. Call login() first.");
        }

        try {
            String anonKey = AppConfig.getProperty(PropertyKey.SUPABASE_ANON_KEY);

            HttpRequest req = supabase.buildRequest("/auth/v1/user")
                    .setHeader("Accept", "application/json")
                    .setHeader("apikey", anonKey)                    // overwrite if exists
                    .setHeader("Authorization", "Bearer " + token)   // overwrite if exists
                    .GET()
                    .build();

            HttpResponse<String> res = supabase.sendJson(req);
            ensureSuccess(res);
            return mapper.readValue(res.body(), AuthUserDto.class);

        } catch (Exception ex) {
            throw new RuntimeException("Failed to get current user from Supabase Auth", ex);
        }
    }


    @Override
    public AuthSessionDto login(String email, String password) {
        try {
            String json = mapper.writeValueAsString(new LoginRequest(email, password));

            HttpRequest req = supabase.buildRequest("/auth/v1/token?grant_type=password")
                    .setHeader("Accept", "application/json")
                    .setHeader("apikey", AppConfig.getProperty(PropertyKey.SUPABASE_ANON_KEY))
                    .setHeader("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> res = supabase.sendJson(req);
            AuthSessionDto authSessionDto = mapper.readValue(res.body(), AuthSessionDto.class);

            SupabaseSession.setAccessToken(authSessionDto.accessToken());
            return authSessionDto;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void ensureSuccess(HttpResponse<String> res) {
        int code = res.statusCode();
        if (code >= 200 && code < 300) return;

        throw new RuntimeException("Request failed: " + code + " - " + res.body());
    }

    public void logout() {
        String token = SupabaseSession.getAccessToken();
        if (token == null || token.isBlank()) {
            SupabaseSession.clear();
            return;
        }

        try {
            String anonKey = AppConfig.getProperty(PropertyKey.SUPABASE_ANON_KEY);

            HttpRequest req = supabase.buildRequest("/auth/v1/logout")
                    .setHeader("Accept", "application/json")
                    .setHeader("apikey", anonKey)
                    .setHeader("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> res = supabase.sendJson(req);

            // Supabase puede devolver 204/200; si no es 2xx, aún así limpiamos local.
            // Si quieres ser estricto, llama a ensureSuccess(res).
        } catch (Exception ex) {
            throw new RuntimeException("Failed to logout from Supabase", ex);
        } finally {
            SupabaseSession.clear();
        }
    }
}