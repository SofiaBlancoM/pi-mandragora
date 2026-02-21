package es.cifpcarlos3.pimandragora.infrastructure.data.repositories.books;

import es.cifpcarlos3.pimandragora.application.books.usecases.getbooks.dtos.GetBooksListItemResponse;
import es.cifpcarlos3.pimandragora.domain.enums.BookStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record BookListRow(
        UUID id,
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
        Instant cover_image_updated_at,
        Instant created_at,
        Instant updated_at,
        AuthorEmbed author,
        CategoryEmbed category
) {
    public GetBooksListItemResponse toResponse() {
        return new GetBooksListItemResponse(
                id,
                isbn,
                title,
                author_id,
                author != null ? author.full_name() : null,
                category_id,
                category != null ? category.name() : null,
                publisher,
                publication_date,
                price,
                stock,
                status,
                cover_image_path,
                cover_image_updated_at,
                created_at,
                updated_at
        );
    }

    public record AuthorEmbed(String full_name) {
    }

    public record CategoryEmbed(String name) {
    }
}
