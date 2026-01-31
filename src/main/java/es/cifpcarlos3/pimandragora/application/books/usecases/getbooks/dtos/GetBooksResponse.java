package es.cifpcarlos3.pimandragora.application.books.usecases.getbooks.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record GetBooksResponse(
        UUID id,
        String isbn,
        String title,

        @JsonProperty("author_id") UUID authorId,
        @JsonProperty("category_id") UUID categoryId,

        String publisher,
        @JsonProperty("publication_date") LocalDate publicationDate,

        BigDecimal price,
        int stock,
        String status,

        @JsonProperty("cover_image_path") String coverImagePath,
        @JsonProperty("cover_image_updated_at") Instant coverImageUpdatedAt,

        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt,

        AuthorEmbed authors,
        CategoryEmbed categories
) {
    public record AuthorEmbed(@JsonProperty("full_name") String fullName) {
    }

    public record CategoryEmbed(String name) {
    }
}
