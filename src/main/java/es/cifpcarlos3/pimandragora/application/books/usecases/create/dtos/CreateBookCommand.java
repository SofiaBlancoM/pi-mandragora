package es.cifpcarlos3.pimandragora.application.books.usecases.create.dtos;


import es.cifpcarlos3.pimandragora.domain.enums.BookStatus;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.UUID;

public record CreateBookCommand(
        String isbn,
        String title,
        UUID authorId,
        UUID categoryId,
        String publisher,
        LocalDate publicationDate,
        BigDecimal price,
        Integer stock,
        BookStatus status,
        Path coverFile
) {
}
