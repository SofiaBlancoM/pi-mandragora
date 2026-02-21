package es.cifpcarlos3.pimandragora.application.books.usecases.update;

import es.cifpcarlos3.pimandragora.application.books.repositories.BookRepository;
import es.cifpcarlos3.pimandragora.application.books.usecases.update.dtos.UpdateBookCommand;
import es.cifpcarlos3.pimandragora.application.books.usecases.update.dtos.UpdateBookResponse;
import es.cifpcarlos3.pimandragora.application.common.images.BookCoverImageStorage;
import es.cifpcarlos3.pimandragora.domain.entities.Book;
import es.cifpcarlos3.pimandragora.domain.enums.BookStatus;

/**
 * Caso de uso para actualizar un libro en base de datos y su imagen de portada en el bucket
 *
 * @param repository
 * @param coverStorage
 */
public record UpdateBookUseCase(BookRepository repository, BookCoverImageStorage coverStorage) {

    public UpdateBookResponse execute(UpdateBookCommand command) {

        Book book = repository.findById(command.id())
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado: " + command.id()));

        int stock = (command.stock() == null) ? book.getStock() : command.stock();
        BookStatus status = (command.status() == null) ? book.getStatus() : command.status();

        book.setIsbn(command.isbn());
        book.setTitle(command.title());
        book.setAuthorId(command.authorId());
        book.setCategoryId(command.categoryId());
        book.setPublisher(command.publisher());
        book.setPublicationDate(command.publicationDate());
        book.setPrice(command.price());
        book.setStock(stock);
        book.setStatus(status);

        updateCover(command, book);

        Book saved = repository.save(book);
        return UpdateBookResponse.from(saved);
    }

    private void updateCover(UpdateBookCommand command, Book book) {
        if (command.removeCover()) {
            coverStorage.deleteCover(book.getCoverImagePath());
            book.removeCoverImage();
        } else if (command.newCoverFile() != null) {

            String oldCoverImagePath = book.getCoverImagePath();
            if (oldCoverImagePath != null) {
                coverStorage.deleteCover(oldCoverImagePath);
            }

            String newCoverImagePath = coverStorage().uploadBookCover(book.getId(), command.newCoverFile());
            book.updateCoverImage(newCoverImagePath);
        }
    }

}
