package es.cifpcarlos3.pimandragora.application.books.usecases.update;

import es.cifpcarlos3.pimandragora.application.books.repositories.BookRepository;
import es.cifpcarlos3.pimandragora.application.books.usecases.update.dtos.UpdateBookCommand;
import es.cifpcarlos3.pimandragora.application.books.usecases.update.dtos.UpdateBookResponse;
import es.cifpcarlos3.pimandragora.application.common.images.BookCoverImageStorage;
import es.cifpcarlos3.pimandragora.domain.entities.Book;
import es.cifpcarlos3.pimandragora.domain.enums.BookStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateBookUseCase(BookRepository repository, BookCoverImageStorage coverStorage) {

    public UpdateBookUseCase {
        if (repository == null) throw new IllegalArgumentException("repository is required");
        if (coverStorage == null) throw new IllegalArgumentException("coverStorage is required");
    }

    public UpdateBookResponse execute(UpdateBookCommand command) {
        require(command, "command is required");
        require(command.id(), "id is required");

        validate(command);

        Book existing = getBook(command.id());

        // 1) Cover side-effects first (upload/delete), decide final coverPath
        CoverDecision coverDecision = resolveCover(existing, command);

        // 2) Build updated aggregate (keeps constructor validations)
        Book updated = buildUpdatedAggregate(existing, command, coverDecision.finalCoverPath());

        // 3) Apply domain behaviors to keep invariants consistent (status/updatedAt)
        changeStatus(updated, command.status());

        // 4) Persist
        Book saved = repository.save(updated);
        return toResponse(saved);
    }

    // --------------------------
    // Cover handling
    // --------------------------

    private CoverDecision resolveCover(Book existing, UpdateBookCommand command) {
        String oldPath = existing.getCoverImagePath();

        // Replace cover
        if (command.newCoverFile() != null) {
            // Best practice: delete previous cover (if any) before replacing
            deleteIfPresent(oldPath);

            String newPath = coverStorage.uploadBookCover(existing.getId(), command.newCoverFile());
            if (isBlank(newPath)) {
                throw new IllegalStateException("Cover upload returned empty path");
            }
            return CoverDecision.replaced(newPath);
        }

        // Remove cover
        if (command.removeCover()) {
            deleteIfPresent(oldPath);
            return CoverDecision.removed();
        }

        // Keep existing cover
        return CoverDecision.unchanged(oldPath);
    }

    private void deleteIfPresent(String objectPath) {
        if (isBlank(objectPath)) return;
        coverStorage.deleteCover(objectPath);
    }

    // --------------------------
    // Aggregate building
    // --------------------------

    private Book buildUpdatedAggregate(Book existing, UpdateBookCommand c, String finalCoverPath) {
        int stock = (c.stock() == null) ? existing.getStock() : c.stock();
        BookStatus status = (c.status() == null) ? existing.getStatus() : c.status();

        // publisher can be null/blank
        String publisher = blankToNull(c.publisher());

        Book updated = Book.builder()
                .id(existing.getId())
                .createdAt(existing.getCreatedAt())
                .updatedAt(existing.getUpdatedAt())
                .isbn(c.isbn())
                .title(c.title())
                .authorId(c.authorId())
                .categoryId(c.categoryId())
                .publisher(publisher)
                .publicationDate(c.publicationDate())
                .price(c.price())
                .stock(stock)
                .status(status)
                .coverImagePath(finalCoverPath)
                .coverImageUpdatedAt(existing.getCoverImageUpdatedAt())
                .build();

        // Apply domain methods to ensure invariants & updatedAt are consistent
        // (yes, these look redundant, but they centralize domain rules)
        updated.rename(updated.getTitle());
        updated.changePrice(updated.getPrice());
        updated.adjustStock(updated.getStock());

        // Cover timestamps / nulling in a single place:
        if (c.newCoverFile() != null) {
            updated.updateCoverImage(finalCoverPath);
        } else if (c.removeCover()) {
            // Requires: Book.removeCoverImage() (you should add it to the domain entity)
            updated.removeCoverImage();
        }

        return updated;
    }

    private static String blankToNull(String s) {
        if (s == null) return null;
        String x = s.trim();
        return x.isBlank() ? null : x;
    }

    // --------------------------
    // Validation
    // --------------------------

    private void changeStatus(Book updated, BookStatus desiredStatus) {
        BookStatus target = (desiredStatus == null) ? updated.getStatus() : desiredStatus;

        // DISCONTINUED always wins
        if (target == BookStatus.DISCONTINUED) {
            updated.discontinue();
            return;
        }

        // OUT_OF_STOCK requires stock == 0
        if (target == BookStatus.OUT_OF_STOCK) {
            if (updated.getStock() > 0) {
                throw new IllegalArgumentException("Cannot set OUT_OF_STOCK when stock > 0");
            }
            // adjustStock already sets OUT_OF_STOCK when stock == 0 and status ACTIVE
            // If it was something else (e.g. DISCONTINUED), we don't override here.
            return;
        }

        // ACTIVE: if stock == 0, keep OUT_OF_STOCK (don’t lie)
        if (target == BookStatus.ACTIVE) {
            if (updated.getStock() == 0) return;
            updated.activate();
        }
    }

    private static void validate(UpdateBookCommand c) {
        if (isBlank(c.title())) throw new IllegalArgumentException("title is required");
        if (isBlank(c.isbn())) throw new IllegalArgumentException("isbn is required");
        if (c.authorId() == null) throw new IllegalArgumentException("authorId is required");
        if (c.categoryId() == null) throw new IllegalArgumentException("categoryId is required");
        if (c.price() == null) throw new IllegalArgumentException("price is required");
        if (isNegative(c.price())) throw new IllegalArgumentException("price cannot be negative");

        if (c.stock() != null && c.stock() < 0) {
            throw new IllegalArgumentException("stock cannot be negative");
        }

        if (c.newCoverFile() != null && c.removeCover()) {
            throw new IllegalArgumentException("removeCover cannot be true when newCoverFile is provided");
        }
    }

    // --------------------------
    // Mapping
    // --------------------------

    private static boolean isNegative(BigDecimal v) {
        return v.signum() < 0;
    }

    // --------------------------
    // Small utils
    // --------------------------

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private Book getBook(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));
    }

    private static UpdateBookResponse toResponse(Book book) {
        return new UpdateBookResponse(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthorId(),
                book.getCategoryId(),
                book.getPublisher(),
                book.getPublicationDate(),
                book.getPrice(),
                book.getStock(),
                book.getStatus(),
                book.getCoverImagePath(),
                book.getCreatedAt(),
                book.getUpdatedAt()
        );
    }

    private static <T> void require(T value, String message) {
        if (value == null) throw new IllegalArgumentException(message);
    }

    // --------------------------
    // Local value object
    // --------------------------

    private record CoverDecision(Type type, String finalCoverPath) {
        static CoverDecision unchanged(String path) {
            return new CoverDecision(Type.UNCHANGED, path);
        }

        static CoverDecision replaced(String path) {
            return new CoverDecision(Type.REPLACED, path);
        }

        static CoverDecision removed() {
            return new CoverDecision(Type.REMOVED, null);
        }

        enum Type {UNCHANGED, REPLACED, REMOVED}
    }
}
