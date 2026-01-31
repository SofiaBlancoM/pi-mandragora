package es.cifpcarlos3.pimandragora.application.books.usecases.getbooks.dtos;

import es.cifpcarlos3.pimandragora.domain.enums.BookStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record GetBooksListItemResponse(
        UUID id,
        String isbn,
        String title,
        UUID authorId,
        String authorName,
        UUID categoryId,
        String categoryName,
        String publisher,
        LocalDate publicationDate,
        BigDecimal price,
        int stock,
        BookStatus status,
        String coverImagePath,
        Instant coverImageUpdatedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
