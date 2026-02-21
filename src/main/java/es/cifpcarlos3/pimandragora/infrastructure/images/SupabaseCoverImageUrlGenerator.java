package es.cifpcarlos3.pimandragora.infrastructure.images;

import es.cifpcarlos3.pimandragora.application.common.images.CoverImageUrlGenerator;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.StorageApi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SupabaseCoverImageUrlGenerator implements CoverImageUrlGenerator {

    private final StorageApi storageApi;
    private final int expiresInSeconds;

    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public SupabaseCoverImageUrlGenerator(StorageApi storageApi, int expiresInSeconds) {
        this.storageApi = storageApi;
        this.expiresInSeconds = expiresInSeconds;
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public String generate(String objectPath) {
        if (objectPath == null || objectPath.isBlank()) return null;

        return cache.computeIfAbsent(objectPath, p ->
                storageApi.createSignedUrl(p, expiresInSeconds)
        );
    }

}
