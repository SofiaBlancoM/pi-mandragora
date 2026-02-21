package es.cifpcarlos3.pimandragora.application.books.repositories;

import es.cifpcarlos3.pimandragora.application.books.usecases.getbooks.dtos.GetBooksListItemResponse;
import es.cifpcarlos3.pimandragora.application.books.usecases.getbooks.dtos.GetBooksQuery;
import es.cifpcarlos3.pimandragora.shared.paging.Page;

/**
 * Repositorio solo para operaciones de lectura en base de datos sobre los libros
 */
public interface BookQueryRepository {

    /**
     * Llamada para obtener listado de libros
     *
     * @param query filtros, ordenación y paginación
     * @return devuelve un listado de libros paginado, ordenado y filtrado
     */
    Page<GetBooksListItemResponse> find(GetBooksQuery query);
}
