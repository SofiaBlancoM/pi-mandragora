package es.cifpcarlos3.pimandragora.application.authors.usecases.dtos;

import java.util.UUID;

public record FindAllAuthorsResponse(
        UUID id,
        String fullName,
        String bio
) {
}
