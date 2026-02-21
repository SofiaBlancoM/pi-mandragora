package es.cifpcarlos3.pimandragora.infrastructure.images;

import es.cifpcarlos3.pimandragora.application.common.images.BookCoverImageStorage;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.StorageApi;

import java.nio.file.Path;
import java.util.UUID;

public record SupabaseBookCoverImageStorage(StorageApi storageApi) implements BookCoverImageStorage {

    @Override
    public void deleteCover(String objectPath) {
        storageApi.deleteObject(objectPath);
    }

    @Override
    public String uploadBookCover(UUID bookId, Path file) {
        if (bookId == null) throw new IllegalArgumentException("El id del libro es necesario");
        if (file == null) throw new IllegalArgumentException("La ruta del fichero es necesaria");

        String objectPath = "books/" + bookId + "/cover.jpg";

        storageApi.uploadImage(file, objectPath);

        return objectPath;
    }
}
