package es.cifpcarlos3.pimandragora.application.books.usecases.update.dtos;

import es.cifpcarlos3.pimandragora.domain.enums.BookStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateBookResponse(
        UUID id,
        String isbn,
        String title,
        UUID authorId,
        UUID categoryId,
        String publisher,
        LocalDate publicationDate,
        BigDecimal price,
        int stock,
        BookStatus status,
        String coverImagePath,
        Instant createdAt,
        Instant updatedAt
) {
}
