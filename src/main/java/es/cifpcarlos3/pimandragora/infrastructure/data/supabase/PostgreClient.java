package es.cifpcarlos3.pimandragora.infrastructure.data.supabase;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.cifpcarlos3.pimandragora.infrastructure.json.JsonMapper;
import es.cifpcarlos3.pimandragora.shared.paging.Page;
import es.cifpcarlos3.pimandragora.shared.paging.PageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

public class PostgreClient {

    private static final Logger log = LoggerFactory.getLogger(PostgreClient.class);

    private final SupabaseHttpClient http;
    private final ObjectMapper mapper = JsonMapper.get();
    private final String schema;

    public PostgreClient(SupabaseHttpClient http) {
        this(http, "public");
    }

    public PostgreClient(SupabaseHttpClient http, String schema) {
        this.http = http;
        this.schema = schema;
    }



    public void delete(String table, Map<String, String> query) {
        String path = restPath(table, query);
        HttpRequest request = http.buildRequest(path).header("Accept-Profile", schema).DELETE().build();
        http.sendJson(request);
    }

    public <T> java.util.List<T> getList(String table, Map<String, String> query, TypeReference<java.util.List<T>> type) {
        String path = restPath(table, query);
        HttpRequest request = http.buildRequest(path).header("Accept", "application/json").header("Accept-Profile", schema).GET().build();
        HttpResponse<String> res = http.sendJson(request);
        try {
            return mapper.readValue(res.body(), type);
        } catch (Exception e) {
            throw new RuntimeException("Fallo al parsear lista: " + table, e);
        }
    }

    public <T> Page<T> getPage(String table, Map<String, String> query, PageRequest pageRequest, TypeReference<java.util.List<T>> listType) {
        try {
            Map<String, String> q = new HashMap<>();
            if (query != null) q.putAll(query);
            q.put("limit", String.valueOf(pageRequest.size()));
            q.put("offset", String.valueOf(pageRequest.offset()));
            String path = restPath(table, q);
            HttpRequest request = http.buildRequest(path).header("Accept", "application/json").header("Prefer", "count=exact").header("Accept-Profile", schema).GET().build();
            HttpResponse<String> res = http.sendJson(request);
            var items = mapper.readValue(res.body(), listType);
            long total = parseTotalFromContentRange(res.headers().firstValue("Content-Range")).orElse((long) items.size());
            return new Page<>(items, pageRequest.page(), pageRequest.size(), total);
        } catch (Exception e) {
            throw new RuntimeException("Fallo en paginación de: " + table, e);
        }
    }



    public void patch(String table, Map<String, String> query, Object payload) {
        String path = restPath(table, query);
        try {
            String json = mapper.writeValueAsString(payload);
            HttpRequest request = http.buildRequest(path)
                    .header("Content-Type", "application/json")
                    .header("Prefer", "return=minimal")
                    .header("Accept-Profile", schema)
                    .header("Content-Profile", schema)
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .build();
            http.sendJson(request);
        } catch (Exception e) {
            throw new RuntimeException("Fallo al realizar patch en: " + table, e);
        }
    }



    public <T> T getSingle(String table, Map<String, String> query, Class<T> type) {
        String path = restPath(table, query);
        HttpRequest request = http.buildRequest(path).header("Accept", "application/vnd.pgrst.object+json").header("Accept-Profile", schema).GET().build();
        HttpResponse<String> res = http.sendJson(request);
        try {
            return mapper.readValue(res.body(), type);
        } catch (Exception e) {
            throw new RuntimeException("Fallo al parsear objeto: " + table, e);
        }
    }

    public <T> java.util.List<T> upsert(String table, Object payload, Map<String, String> query, TypeReference<java.util.List<T>> type) {
        String path = restPath(table, query);
        try {
            String json = mapper.writeValueAsString(payload);
            HttpRequest request = http.buildRequest(path)
                    .header("Content-Type", "application/json")
                    .header("Prefer", "resolution=merge-duplicates,return=representation")
                    .header("Accept-Profile", schema)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            return mapper.readValue(http.sendJson(request).body(), type);
        } catch (Exception e) {
            throw new RuntimeException("Fallo en upsert: " + table, e);
        }
    }

    private static Optional<Long> parseTotalFromContentRange(Optional<String> contentRange) {
        if (contentRange.isEmpty()) return Optional.empty();
        String v = contentRange.get();
        int slash = v.indexOf('/');
        if (slash < 0) return Optional.empty();
        try { return Optional.of(Long.parseLong(v.substring(slash + 1).trim())); }
        catch (Exception e) { return Optional.empty(); }
    }

    private static String restPath(String table, Map<String, String> query) {
        String base = "/rest/v1/" + table;
        if (query == null || query.isEmpty()) return base;
        StringJoiner sj = new StringJoiner("&");
        query.forEach((k, v) -> sj.add(k + "=" + v));
        return base + "?" + sj;
    }
}