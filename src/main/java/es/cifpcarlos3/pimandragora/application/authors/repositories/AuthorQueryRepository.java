package es.cifpcarlos3.pimandragora.application.authors.repositories;

import es.cifpcarlos3.pimandragora.application.authors.usecases.findallauthors.dtos.FindAllAuthorsResponse;

import java.util.List;

public interface AuthorQueryRepository {
    List<FindAllAuthorsResponse> findAll();
}
