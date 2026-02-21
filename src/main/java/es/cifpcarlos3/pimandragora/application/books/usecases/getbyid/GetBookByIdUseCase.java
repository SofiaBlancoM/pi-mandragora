package es.cifpcarlos3.pimandragora.application.books.usecases.getbyid;

import es.cifpcarlos3.pimandragora.application.books.repositories.BookRepository;
import es.cifpcarlos3.pimandragora.application.books.usecases.getbyid.dtos.GetBookByIdResponse;
import es.cifpcarlos3.pimandragora.domain.entities.Book;

import java.util.UUID;

/**
 * Caso de uso para obtener un libro por el id
 *
 * @param bookRepository
 */
public record GetBookByIdUseCase(BookRepository bookRepository) {

    public GetBookByIdResponse execute(UUID id) {
        if (id == null) throw new IllegalArgumentException("El id es obligatorio");

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado con el id: " + id));

        return GetBookByIdResponse.from(book);
    }
}
