package es.cifpcarlos3.pimandragora.application.books.usecases.create;

import es.cifpcarlos3.pimandragora.application.books.repositories.BookRepository;
import es.cifpcarlos3.pimandragora.application.books.usecases.create.dtos.CreateBookCommand;
import es.cifpcarlos3.pimandragora.application.books.usecases.create.dtos.CreateBookResponse;
import es.cifpcarlos3.pimandragora.application.common.images.BookCoverImageStorage;
import es.cifpcarlos3.pimandragora.domain.entities.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.UUID;

/**
 * Caso de uso para crear un libro en base de datos y subir su imagen al bucket de supabase
 *
 * @param repository
 * @param coverStorage
 */
public record CreateBookUseCase(BookRepository repository, BookCoverImageStorage coverStorage) {

    private static final Logger logger = LoggerFactory.getLogger(CreateBookUseCase.class);

    public CreateBookResponse execute(CreateBookCommand command) {

        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        int stock = (command.stock() == null) ? 0 : command.stock();

        String coverPath = null;
        Instant coverUpdatedAt = null;
        if (command.coverFile() != null) {
            coverPath = coverStorage.uploadBookCover(id, command.coverFile());
            coverUpdatedAt = now;
        }

        Book book = Book.builder()
                .id(id)
                .createdAt(now)
                .updatedAt(now)
                .isbn(command.isbn())
                .title(command.title())
                .authorId(command.authorId())
                .categoryId(command.categoryId())
                .publisher(command.publisher())
                .publicationDate(command.publicationDate())
                .price(command.price())
                .stock(stock)
                .status(command.status())
                .coverImagePath(coverPath)
                .coverImageUpdatedAt(coverUpdatedAt)
                .build();

        if (coverPath != null) {
            book.updateCoverImage(coverPath);
        }

        Book saved = repository.save(book);
        logger.info("Libro: {} creado", saved.getId());
        return CreateBookResponse.from(saved);
    }

}
