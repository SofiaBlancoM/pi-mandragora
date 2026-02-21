package es.cifpcarlos3.pimandragora.application.authors.usecases.dtos;

import es.cifpcarlos3.pimandragora.application.authors.repositories.AuthorRepository;
import java.util.Map;

public class UpdateAuthorUseCase {
    private final AuthorRepository authorRepository;

    public UpdateAuthorUseCase(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }


    public void execute(String id, Map<String, Object> data) {
        authorRepository.update(id, data);
    }
}