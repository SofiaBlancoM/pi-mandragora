package es.cifpcarlos3.pimandragora.infrastructure.data.repositories.books;

import es.cifpcarlos3.pimandragora.domain.entities.Book;
import es.cifpcarlos3.pimandragora.domain.enums.BookStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record BookRow(
        UUID id,
        Instant created_at,
        Instant updated_at,
        String isbn,
        String title,
        UUID author_id,
        UUID category_id,
        String publisher,
        LocalDate publication_date,
        BigDecimal price,
        int stock,
        BookStatus status,
        String cover_image_path,
        Instant cover_image_updated_at
) {
    public static BookRow from(Book b) {
        return new BookRow(
                b.getId(),
                b.getCreatedAt(),
                b.getUpdatedAt(),
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
                b.getCoverImageUpdatedAt()
        );
    }

    public Book toDomain() {
        return Book.builder()
                .id(id)
                .createdAt(created_at)
                .updatedAt(updated_at)
                .isbn(isbn)
                .title(title)
                .authorId(author_id)
                .categoryId(category_id)
                .publisher(publisher)
                .publicationDate(publication_date)
                .price(price)
                .stock(stock)
                .status(status)
                .coverImagePath(cover_image_path)
                .coverImageUpdatedAt(cover_image_updated_at)
                .build();
    }
}
