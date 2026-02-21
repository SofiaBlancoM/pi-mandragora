package es.cifpcarlos3.pimandragora.application.authors.usecases.dtos;

import es.cifpcarlos3.pimandragora.application.authors.repositories.AuthorRepository;

public record CreateAuthorUseCase(AuthorRepository repository) {
    public void execute(String fullName) {

        repository.save(fullName);
    }
}