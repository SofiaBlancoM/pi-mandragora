package es.cifpcarlos3.pimandragora.application.authors.usecases.dtos;

import es.cifpcarlos3.pimandragora.application.authors.repositories.AuthorRepository;

public class DeleteAuthorUseCase {
    private final AuthorRepository authorRepository;

    public DeleteAuthorUseCase(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public void execute(String id) {
        authorRepository.delete(id);
    }
}