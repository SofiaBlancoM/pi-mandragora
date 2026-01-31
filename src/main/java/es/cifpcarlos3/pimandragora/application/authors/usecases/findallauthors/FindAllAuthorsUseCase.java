package es.cifpcarlos3.pimandragora.application.authors.usecases.findallauthors;


import es.cifpcarlos3.pimandragora.application.authors.repositories.AuthorQueryRepository;
import es.cifpcarlos3.pimandragora.application.authors.usecases.findallauthors.dtos.FindAllAuthorsResponse;

import java.util.Comparator;
import java.util.List;

public record FindAllAuthorsUseCase(AuthorQueryRepository repository) {

    public FindAllAuthorsUseCase {
        if (repository == null) throw new IllegalArgumentException("repository is required");
    }

    public List<FindAllAuthorsResponse> execute() {
        // Sort here so UI is stable regardless of backend default
        return repository.findAll().stream()
                .sorted(Comparator.comparing(FindAllAuthorsResponse::fullName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }
}
