package es.cifpcarlos3.pimandragora.application.books.usecases.getbyid;

import es.cifpcarlos3.pimandragora.application.books.repositories.BookRepository;
import es.cifpcarlos3.pimandragora.application.books.usecases.getbyid.dtos.GetBookByIdResponse;
import es.cifpcarlos3.pimandragora.domain.entities.Book;

import java.util.UUID;

public record GetBookByIdUseCase(BookRepository bookRepository) {

    public GetBookByIdUseCase {
        if (bookRepository == null) throw new IllegalArgumentException("repository is required");
    }

    public GetBookByIdResponse execute(UUID id) {
        if (id == null) throw new IllegalArgumentException("id is required");

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));

        return GetBookByIdResponse.from(book);
    }
}
