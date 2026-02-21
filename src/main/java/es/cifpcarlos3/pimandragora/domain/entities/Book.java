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

    private UUID authorId;
    private UUID categoryId;

    private String publisher;
    private LocalDate publicationDate;

    private BigDecimal price;
    private int stock;

    private BookStatus status;

    private String coverImagePath;
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
        setStatus(status);

        this.coverImagePath = coverImagePath;
        this.coverImageUpdatedAt = coverImageUpdatedAt;
    }

    public void setIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) throw new IllegalArgumentException("La ISBN es necesaria");
        this.isbn = isbn.trim();
    }

    public void setTitle(String title) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("El título es necessario");
        this.title = title.trim();
    }

    public void setAuthorId(UUID authorId) {
        if (authorId == null) throw new IllegalArgumentException("El autor es necesario");
        this.authorId = authorId;
    }

    public void setCategoryId(UUID categoryId) {
        if (categoryId == null) throw new IllegalArgumentException("La categoría es necesaria");
        this.categoryId = categoryId;
    }

    public void setPrice(BigDecimal price) {
        if (price == null) throw new IllegalArgumentException("El precio es obligatorio");
        if (price.signum() < 0) throw new IllegalArgumentException("El precio no puede ser negativo");
        this.price = price;
    }

    public void setStock(int stock) {
        if (stock < 0) throw new IllegalArgumentException("El stock no puede ser negativo");
        if (this.stock == 0 && this.status == BookStatus.ACTIVE) {
            this.status = BookStatus.OUT_OF_STOCK;
        }
        this.stock = stock;
    }

    public void setStatus(BookStatus newBookStatus) {

        if (newBookStatus == null) {
            throw new IllegalArgumentException("El estado del libro es obligatorio");
        }
        switch (newBookStatus) {
            case ACTIVE -> {
                if (getStock() == 0) {
                    throw new IllegalArgumentException("El estado no puede ser " + BookStatus.ACTIVE.getDisplayName() + " si el stock es cero");
                }
            }
            case OUT_OF_STOCK -> {
                if (getStock() > 0) {
                    throw new IllegalArgumentException("El estado no puede ser " + BookStatus.OUT_OF_STOCK.getDisplayName() + " si el stock es mayor que cero");
                }
            }
        }
        this.status = newBookStatus;
        touch();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    public void removeCoverImage() {
        this.coverImagePath = null;
        this.coverImageUpdatedAt = Instant.now();
        touch();
    }

    public void setPublicationDate(LocalDate publicationDate) {
        if (publicationDate == null) throw new IllegalArgumentException("La fecha de publicación es necessaria");
        this.publicationDate = publicationDate;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher.trim();
    }

    public void updateCoverImage(String newPath) {
        if (newPath == null || newPath.isBlank()) {
            throw new IllegalArgumentException("La ruta de la imagen de portada es obligatoria");
        }
        this.coverImagePath = newPath;
        this.coverImageUpdatedAt = Instant.now();
        touch();
    }
}
