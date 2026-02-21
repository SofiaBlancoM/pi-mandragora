package es.cifpcarlos3.pimandragora.infrastructure.data.repositories.authors;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.cifpcarlos3.pimandragora.application.authors.usecases.dtos.FindAllAuthorsResponse;

import java.util.UUID;

public record AuthorRow(
        UUID id,
        @JsonProperty("full_name") String fullName,
        @JsonProperty("bio") String bio
) {
    public FindAllAuthorsResponse toResponse() {
        return new FindAllAuthorsResponse(id, fullName, bio);
    }
}