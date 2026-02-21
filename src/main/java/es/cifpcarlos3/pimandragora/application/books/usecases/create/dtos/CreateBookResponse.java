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
    public static CreateBookResponse from(Book book) {
        return new CreateBookResponse(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthorId(),
                book.getCategoryId(),
                book.getPublisher(),
                book.getPublicationDate(),
                book.getPrice(),
                book.getStock(),
                book.getStatus(),
                book.getCoverImagePath(),
                book.getCreatedAt(),
                book.getUpdatedAt()
        );
    }
}
