package es.cifpcarlos3.pimandragora.infrastructure.images;

import es.cifpcarlos3.pimandragora.application.common.images.CoverImageUrlResolver;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.StorageApi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SupabaseCoverImageUrlResolver implements CoverImageUrlResolver {

    private final StorageApi storageApi;
    private final int expiresInSeconds;

    // Optional: cache to avoid calling sign endpoint repeatedly
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public SupabaseCoverImageUrlResolver(StorageApi storageApi, int expiresInSeconds) {
        this.storageApi = storageApi;
        this.expiresInSeconds = expiresInSeconds;
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public String resolve(String objectPath) {
        if (objectPath == null || objectPath.isBlank()) return null;

        return cache.computeIfAbsent(objectPath, p ->
                storageApi.createSignedUrl(p, expiresInSeconds)
        );
    }

}
