package es.cifpcarlos3.pimandragora.application.books.usecases.update.dtos;

import es.cifpcarlos3.pimandragora.domain.enums.BookStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateBookCommand(
        UUID id,
        String isbn,
        String title,
        UUID authorId,
        UUID categoryId,
        String publisher,
        LocalDate publicationDate,
        BigDecimal price,
        Integer stock,
        BookStatus status,
        java.nio.file.Path newCoverFile,
        boolean removeCover
) {
}
