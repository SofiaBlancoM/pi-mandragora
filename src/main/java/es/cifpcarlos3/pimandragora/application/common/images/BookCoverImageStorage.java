package es.cifpcarlos3.pimandragora.application.common.images;

import java.nio.file.Path;
import java.util.UUID;

public interface BookCoverImageStorage {
    void deleteCover(String objectPath);

    /**
     * Uploads a cover image for a book and returns the storage object path that must be stored in Book.coverImagePath.
     * Example: books/<bookId>/cover.jpg
     */
    String uploadBookCover(UUID bookId, Path file);
}
