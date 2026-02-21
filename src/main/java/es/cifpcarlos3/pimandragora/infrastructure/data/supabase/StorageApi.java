package es.cifpcarlos3.pimandragora.infrastructure.data.supabase;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.cifpcarlos3.pimandragora.application.auth.dtos.SignedUrlDto;
import es.cifpcarlos3.pimandragora.infrastructure.json.JsonMapper;
import es.cifpcarlos3.pimandragora.presentation.app.config.AppConfig;
import es.cifpcarlos3.pimandragora.presentation.app.config.PropertyKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

public class StorageApi {

    private static final Logger log =
            LoggerFactory.getLogger(StorageApi.class);

    private final SupabaseHttpClient supabaseHttpClient;
    private final String bucket;
    private final ObjectMapper mapper = JsonMapper.get();

    public StorageApi(SupabaseHttpClient supabaseHttpClient) {
        this.supabaseHttpClient = supabaseHttpClient;
        this.bucket = AppConfig.getProperty(PropertyKey.STORAGE_BUCKET);
    }

    /**
     * Creates a signed URL for private buckets.
     * expiresInSeconds: e.g. 3600 (1 hour)
     */
    public String createSignedUrl(String objectPath, int expiresInSeconds) {
        try {
            log.debug("Storage signedUrl bucket={} objectPath={} ttlSeconds={}", bucket, objectPath, expiresInSeconds);

            String encodedPath = encodePath(objectPath);
            String body = "{\"expiresIn\":" + expiresInSeconds + "}";

            HttpRequest request = supabaseHttpClient.buildRequest(
                            "/storage/v1/object/sign/" + bucket + "/" + encodedPath
                    )
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = supabaseHttpClient.sendJson(request);

            SignedUrlDto dto = mapper.readValue(response.body(), SignedUrlDto.class);

            String signed = dto.signedURL();

            // Local Supabase returns "/object/..." but the real route is "/storage/v1/object/..."
            if (signed.startsWith("/object/")) {
                signed = "/storage/v1" + signed;
            }

            // IMPORTANT: do not log the signed URL (it's basically temporary access)
            if (signed.startsWith("http")) return signed;
            return AppConfig.supabaseUrl() + signed;

        } catch (Exception e) {
            log.error("Failed to create signed url bucket={} objectPath={}", bucket, objectPath, e);
            throw new RuntimeException("Failed to create signed url for: " + objectPath, e);
        }
    }

    private static String encodePath(String path) {
        return Arrays.stream(path.split("/"))
                .map(seg -> URLEncoder.encode(seg, StandardCharsets.UTF_8))
                .collect(Collectors.joining("/"));
    }

    public void deleteObject(String objectPath) {
        try {
            log.debug("Storage delete bucket={} objectPath={}", bucket, objectPath);

            String encodedPath = encodePath(objectPath);

            HttpRequest request = supabaseHttpClient.buildRequest(
                            "/storage/v1/object/" + bucket + "/" + encodedPath
                    )
                    .DELETE()
                    .build();

            supabaseHttpClient.sendJson(request);

        } catch (Exception e) {
            log.error("Failed to delete object bucket={} objectPath={}", bucket, objectPath, e);
            throw new RuntimeException("Failed to delete object: " + objectPath, e);
        }
    }

    public void uploadImage(Path imagePath, String objectPath) {
        try {
            log.debug("Storage upload bucket={} objectPath={} file={}", bucket, objectPath, imagePath);

            byte[] bytes = Files.readAllBytes(imagePath);
            String contentType = Files.probeContentType(imagePath);
            if (contentType == null) contentType = "application/octet-stream";

            HttpRequest request = supabaseHttpClient
                    .buildRequest("/storage/v1/object/" + bucket + "/" + objectPath)
                    .header("Content-Type", contentType)
                    .header("x-upsert", "true")
                    .PUT(HttpRequest.BodyPublishers.ofByteArray(bytes))
                    .build();

            supabaseHttpClient.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            log.error("Failed to upload image bucket={} objectPath={} file={}", bucket, objectPath, imagePath, e);
            throw new RuntimeException("Failed to upload image: " + imagePath, e);
        }
    }
}
