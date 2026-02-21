package es.cifpcarlos3.pimandragora.application.books.usecases.getbooks;


import es.cifpcarlos3.pimandragora.application.books.repositories.BookQueryRepository;
import es.cifpcarlos3.pimandragora.application.books.usecases.getbooks.dtos.GetBooksListItemResponse;
import es.cifpcarlos3.pimandragora.application.books.usecases.getbooks.dtos.GetBooksQuery;
import es.cifpcarlos3.pimandragora.shared.paging.Page;

/**
 * Obtiene una lista de libros filtrada, ordenada y paginada
 *
 * @param repository
 */
public record GetBooksUseCase(BookQueryRepository repository) {

    public Page<GetBooksListItemResponse> execute(GetBooksQuery query) {
        return repository.find(query.normalized());
    }

}
