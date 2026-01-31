package es.cifpcarlos3.pimandragora.application.books.usecases.create.dtos;

import es.cifpcarlos3.pimandragora.domain.entities.Book;
import es.cifpcarlos3.pimandragora.domain.enums.BookStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record CreateBookResponse(
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
    public static CreateBookResponse from(Book b) {
        return new CreateBookResponse(
                b.getId(),
                b.getIsbn(),
                b.getTitle(),
                b.getAuthorId(),
                b.getCategoryId(),
                b.getPublisher(),
                b.getPublicationDate(),
                b.getPrice(),
                b.getStock(),
                b.getStatus(),
                b.getCoverImagePath(),
                b.getCreatedAt(),
                b.getUpdatedAt()
        );
    }
}
