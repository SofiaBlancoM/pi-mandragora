package es.cifpcarlos3.pimandragora.presentation.books.viewmodels;

import es.cifpcarlos3.pimandragora.domain.enums.BookStatus;
import javafx.beans.property.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

@Getter
public class BookCardListViewModel {

    private final UUID id;

    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty authorFullName = new SimpleStringProperty();
    private final StringProperty categoryName = new SimpleStringProperty();

    private final IntegerProperty year = new SimpleIntegerProperty();
    private final StringProperty coverImageUrl = new SimpleStringProperty();

    private final ObjectProperty<BookStatus> status = new SimpleObjectProperty<>(BookStatus.ACTIVE);

    private final ObjectProperty<BigDecimal> price = new SimpleObjectProperty<>();
    private final StringProperty priceText = new SimpleStringProperty();

    public BookCardListViewModel(
            UUID id,
            String title,
            String author,
            String categoryName,
            int year,
            String coverUrl,
            BookStatus status,
            BigDecimal price
    ) {
        this.id = id;

        this.title.set(title);
        this.authorFullName.set(author);
        this.categoryName.set(categoryName);

        this.year.set(year);
        this.coverImageUrl.set(coverUrl);

        this.status.set(status == null ? BookStatus.ACTIVE : status);

        this.price.set(price);
        this.priceText.set(formatPrice(price));
    }

    private static String formatPrice(BigDecimal price) {
        if (price == null) return "";
        NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("es", "ES"));
        return fmt.format(price);
    }
}
