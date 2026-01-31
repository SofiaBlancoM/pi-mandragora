package es.cifpcarlos3.pimandragora.application.books.usecases.create;


import es.cifpcarlos3.pimandragora.application.books.repositories.BookRepository;
import es.cifpcarlos3.pimandragora.application.books.usecases.create.dtos.CreateBookCommand;
import es.cifpcarlos3.pimandragora.application.books.usecases.create.dtos.CreateBookResponse;
import es.cifpcarlos3.pimandragora.application.common.images.BookCoverImageStorage;
import es.cifpcarlos3.pimandragora.domain.entities.Book;
import es.cifpcarlos3.pimandragora.domain.enums.BookStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CreateBookUseCase(BookRepository repository, BookCoverImageStorage coverStorage) {

    public CreateBookUseCase {
        if (repository == null) throw new IllegalArgumentException("repository is required");
        if (coverStorage == null) throw new IllegalArgumentException("coverStorage is required");
    }

    public CreateBookResponse execute(CreateBookCommand command) {
        require(command);

        validate(command);

        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        int stock = (command.stock() == null) ? 0 : command.stock();
        BookStatus status = normalizeStatus(command.status(), stock);

        // Upload cover (needs bookId)
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
                .publisher(blankToNull(command.publisher()))
                .publicationDate(command.publicationDate())
                .price(command.price())
                .stock(stock)
                .status(status)
                .coverImagePath(coverPath)
                .coverImageUpdatedAt(coverUpdatedAt)
                .build();

        // Domain behavior (keeps status rules consistent)
        book.adjustStock(book.getStock());
        applyStatus(book, status);

        if (coverPath != null) {
            book.updateCoverImage(coverPath);
        }

        Book saved = repository.save(book);
        return CreateBookResponse.from(saved);
    }

    private static void validate(CreateBookCommand c) {
        if (isBlank(c.title())) throw new IllegalArgumentException("title is required");
        if (isBlank(c.isbn())) throw new IllegalArgumentException("isbn is required");
        if (c.authorId() == null) throw new IllegalArgumentException("authorId is required");
        if (c.categoryId() == null) throw new IllegalArgumentException("categoryId is required");
        if (c.price() == null) throw new IllegalArgumentException("price is required");
        if (isNegative(c.price())) throw new IllegalArgumentException("price cannot be negative");
        if (c.stock() != null && c.stock() < 0) throw new IllegalArgumentException("stock cannot be negative");
    }

    private static boolean isNegative(BigDecimal v) {
        return v.signum() < 0;
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private static BookStatus normalizeStatus(BookStatus requested, int stock) {
        if (requested == null) {
            return (stock == 0) ? BookStatus.OUT_OF_STOCK : BookStatus.ACTIVE;
        }

        if (requested == BookStatus.OUT_OF_STOCK && stock > 0) {
            throw new IllegalArgumentException("Cannot set OUT_OF_STOCK when stock > 0");
        }
        if (requested == BookStatus.ACTIVE && stock == 0) {
            return BookStatus.OUT_OF_STOCK;
        }
        return requested;
    }

    private static void applyStatus(Book book, BookStatus s) {
        if (s == null) return;
        switch (s) {
            case DISCONTINUED -> book.discontinue();
            case ACTIVE -> {
                if (book.getStock() > 0) book.activate();
            }
            case OUT_OF_STOCK -> {
                if (book.getStock() > 0) {
                    throw new IllegalArgumentException("Cannot set OUT_OF_STOCK when stock > 0");
                }
            }
        }
    }

    private static String blankToNull(String s) {
        if (s == null) return null;
        String x = s.trim();
        return x.isBlank() ? null : x;
    }

    private static <T> void require(T v) {
        if (v == null) throw new IllegalArgumentException("command is required");
    }
}
