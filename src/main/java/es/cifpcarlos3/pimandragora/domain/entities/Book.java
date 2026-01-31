package es.cifpcarlos3.pimandragora.domain.entities;

import es.cifpcarlos3.pimandragora.domain.enums.BookStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book extends Entity {

    private String isbn;
    private String title;

    // References to other entities (Author, Category)
    private UUID authorId;
    private UUID categoryId;

    private String publisher;
    private LocalDate publicationDate;

    private BigDecimal price;
    private int stock;

    private BookStatus status;

    // --- Cover image (Supabase Storage) ---
    private String coverImagePath;          // e.g. books/<bookId>/cover.jpg
    private Instant coverImageUpdatedAt;

    @Builder
    private Book(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            String isbn,
            String title,
            UUID authorId,
            UUID categoryId,
            String publisher,
            LocalDate publicationDate,
            BigDecimal price,
            int stock,
            BookStatus status,
            String coverImagePath,
            Instant coverImageUpdatedAt
    ) {
        super(id, createdAt, updatedAt);

        setIsbn(isbn);
        setTitle(title);
        setAuthorId(authorId);
        setCategoryId(categoryId);

        this.publisher = publisher;
        this.publicationDate = publicationDate;

        setPrice(price);
        setStock(stock);

        this.status = (status == null) ? BookStatus.ACTIVE : status;

        this.coverImagePath = coverImagePath;
        this.coverImageUpdatedAt = coverImageUpdatedAt;
    }

    // ------------------------
    // Domain behavior
    // ------------------------

    private void setIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) throw new IllegalArgumentException("ISBN is required");
        this.isbn = isbn.trim();
    }

    private void setTitle(String title) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title is required");
        this.title = title.trim();
    }

    private void setAuthorId(UUID authorId) {
        if (authorId == null) throw new IllegalArgumentException("AuthorId is required");
        this.authorId = authorId;
    }

    private void setCategoryId(UUID categoryId) {
        if (categoryId == null) throw new IllegalArgumentException("CategoryId is required");
        this.categoryId = categoryId;
    }

    private void setPrice(BigDecimal price) {
        if (price == null) throw new IllegalArgumentException("Price is required");
        if (price.signum() < 0) throw new IllegalArgumentException("Price cannot be negative");
        this.price = price;
    }

    private void setStock(int stock) {
        if (stock < 0) throw new IllegalArgumentException("Stock cannot be negative");
        this.stock = stock;
    }

    public void activate() {
        this.status = BookStatus.ACTIVE;
        touch();
    }
    // ------------------------
    // Validation helpers
    // ------------------------

    public void adjustStock(int newStock) {
        setStock(newStock);
        if (this.stock == 0 && this.status == BookStatus.ACTIVE) {
            this.status = BookStatus.OUT_OF_STOCK;
        }
        touch();
    }

    public void changePrice(BigDecimal newPrice) {
        setPrice(newPrice);
        touch();
    }

    public void discontinue() {
        this.status = BookStatus.DISCONTINUED;
        touch();
    }

    public void removeCoverImage() {
        this.coverImagePath = null;
        this.coverImageUpdatedAt = Instant.now();
        touch();
    }

    public void rename(String newTitle) {
        setTitle(newTitle);
        touch();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    /**
     * Call this AFTER uploading a new cover image to Supabase Storage.
     */
    public void updateCoverImage(String newPath) {
        if (newPath == null || newPath.isBlank()) {
            throw new IllegalArgumentException("Cover image path is required");
        }
        this.coverImagePath = newPath;
        this.coverImageUpdatedAt = Instant.now();
        touch();
    }
}
