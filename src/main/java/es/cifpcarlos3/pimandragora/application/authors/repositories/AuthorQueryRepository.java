package es.cifpcarlos3.pimandragora.application.authors.repositories;

import es.cifpcarlos3.pimandragora.application.authors.usecases.dtos.AuthorDetailResponse;
import es.cifpcarlos3.pimandragora.application.authors.usecases.dtos.FindAllAuthorsResponse;
import es.cifpcarlos3.pimandragora.shared.paging.Page;
import es.cifpcarlos3.pimandragora.shared.paging.PageRequest;

import java.util.List;

public interface AuthorQueryRepository {
    List<FindAllAuthorsResponse> findAll();
    Page<FindAllAuthorsResponse> findPage(PageRequest pageRequest);
    AuthorDetailResponse findById(String id);
}