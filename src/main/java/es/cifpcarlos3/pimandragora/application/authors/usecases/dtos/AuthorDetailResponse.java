package es.cifpcarlos3.pimandragora.application.authors.usecases.dtos;

import java.util.List;

public record AuthorDetailResponse(
        String id,
        String fullName,
        String bio,
        List<AuthorBookResponse> books
) {

    public record AuthorBookResponse(
            String id,
            String title,
            String coverUrl
    ) {}
}