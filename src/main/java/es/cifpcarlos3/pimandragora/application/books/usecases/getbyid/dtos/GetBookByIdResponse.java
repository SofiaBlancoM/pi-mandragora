package es.cifpcarlos3.pimandragora.application.books.usecases.getbyid.dtos;

import es.cifpcarlos3.pimandragora.domain.entities.Book;
import es.cifpcarlos3.pimandragora.domain.enums.BookStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record GetBookByIdResponse(
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
        String coverImagePath
) {
    public static GetBookByIdResponse from(Book book) {
        return new GetBookByIdResponse(
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
                book.getCoverImagePath()
        );
    }

}
