package es.cifpcarlos3.pimandragora.application.books.usecases.delete;

import es.cifpcarlos3.pimandragora.application.books.repositories.BookRepository;
import es.cifpcarlos3.pimandragora.application.common.images.BookCoverImageStorage;
import es.cifpcarlos3.pimandragora.domain.entities.Book;

import java.util.UUID;

public record DeleteBookUseCase(BookRepository bookRepository, BookCoverImageStorage coverStorage) {

    public DeleteBookUseCase {
        if (bookRepository == null) throw new IllegalArgumentException("bookRepository is required");
        if (coverStorage == null) throw new IllegalArgumentException("coverStorage is required");
    }

    public void execute(UUID bookId) {
        if (bookId == null) throw new IllegalArgumentException("bookId is required");

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));

        deleteCoverIfExists(book);
        deleteBook(bookId);
    }

    private void deleteCoverIfExists(Book book) {
        String coverPath = book.getCoverImagePath();
        if (coverPath == null || coverPath.isBlank()) return;

        // If storage deletion fails, we fail fast to avoid DB deletion leaving inconsistent UI expectations.
        coverStorage.deleteCover(coverPath);
    }

    private void deleteBook(UUID bookId) {
        bookRepository.deleteById(bookId);
    }
}
