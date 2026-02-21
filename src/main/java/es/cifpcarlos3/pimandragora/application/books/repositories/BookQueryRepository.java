package es.cifpcarlos3.pimandragora.application.books.repositories;

import es.cifpcarlos3.pimandragora.application.books.usecases.getbooks.dtos.GetBooksListItemResponse;
import es.cifpcarlos3.pimandragora.application.books.usecases.getbooks.dtos.GetBooksQuery;
import es.cifpcarlos3.pimandragora.shared.paging.Page;


public interface BookQueryRepository {
    Page<GetBooksListItemResponse> find(GetBooksQuery query);
}
