package es.cifpcarlos3.pimandragora.presentation.books.viewmodels;

import es.cifpcarlos3.pimandragora.application.books.usecases.getbooks.GetBooksUseCase;
import es.cifpcarlos3.pimandragora.application.books.usecases.getbooks.dtos.GetBooksListItemResponse;
import es.cifpcarlos3.pimandragora.application.books.usecases.getbooks.dtos.GetBooksQuery;
import es.cifpcarlos3.pimandragora.application.common.images.CoverImageUrlGenerator;
import es.cifpcarlos3.pimandragora.domain.enums.BookStatus;
import es.cifpcarlos3.pimandragora.shared.paging.Page;
import es.cifpcarlos3.pimandragora.shared.paging.PageRequest;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.util.UUID;

public class BooksViewModel {

    private final GetBooksUseCase getBooksUseCase;
    private final CoverImageUrlGenerator coverImageUrlGenerator;
    private final int pageSize;

    private final StringProperty searchText = new SimpleStringProperty();
    private final ObjectProperty<BookStatus> status = new SimpleObjectProperty<>();
    private final ObjectProperty<UUID> categoryId = new SimpleObjectProperty<>();
    private final ObjectProperty<UUID> authorId = new SimpleObjectProperty<>();

    @Getter
    private final ObservableList<BookCardListViewModel> items = FXCollections.observableArrayList();
    private final IntegerProperty pageCount = new SimpleIntegerProperty(1);
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final StringProperty error = new SimpleStringProperty();
    private final LongProperty totalItems = new SimpleLongProperty(0);
    private final ObjectProperty<GetBooksQuery.Sort> sort =
            new SimpleObjectProperty<>(GetBooksQuery.Sort.CREATED_AT_DESC);

    public BooksViewModel(GetBooksUseCase getBooksUseCase, CoverImageUrlGenerator coverImageUrlGenerator, int pageSize) {
        this.getBooksUseCase = getBooksUseCase;
        this.coverImageUrlGenerator = coverImageUrlGenerator;
        this.pageSize = pageSize;
    }

    public ObjectProperty<UUID> authorIdProperty() {
        return authorId;
    }

    public ObjectProperty<UUID> categoryIdProperty() {
        return categoryId;
    }

    public void clearCoverCache() {
        coverImageUrlGenerator.clear();
    }

    public StringProperty errorProperty() {
        return error;
    }

    public BooleanProperty loadingProperty() {
        return loading;
    }

    public IntegerProperty pageCountProperty() {
        return pageCount;
    }

    public void refreshFromFirstPage() {
        loadPage(0);
    }

    public void loadPage(int pageIndex) {
        loading.set(true);
        error.set(null);

        try {
            GetBooksQuery query = new GetBooksQuery(
                    searchText.get(),
                    authorId.get(),
                    categoryId.get(),
                    status.get(),
                    null, null,
                    null, null,
                    sort.get(),
                    new PageRequest(pageIndex, pageSize)
            );

            Page<GetBooksListItemResponse> page = getBooksUseCase.execute(query);

            Platform.runLater(() -> {
                items.setAll(page.items().stream().map(this::toCardViewModel).toList());
                pageCount.set((int) Math.max(1, page.totalPages()));
                totalItems.set(page.totalItems());
                loading.set(false);
            });

        } catch (Exception ex) {
            Platform.runLater(() -> {
                loading.set(false);
                error.set(ex.getMessage());
            });
        }
    }

    private BookCardListViewModel toCardViewModel(GetBooksListItemResponse dto) {
        int year = dto.publicationDate() != null ? dto.publicationDate().getYear() : 0;
        String coverUrl = coverImageUrlGenerator.generate(dto.coverImagePath());

        return new BookCardListViewModel(
                dto.id(),
                dto.title(),
                dto.authorName(),
                dto.categoryName(),
                year,
                coverUrl,
                dto.status(),
                dto.price()
        );
    }

    public void refreshHard() {
        coverImageUrlGenerator.clear();
        resetFilters();
        loadPage(0);
    }

    public void resetFilters() {
        searchText.set(null);
        status.set(null);
        categoryId.set(null);
        authorId.set(null);
        sort.set(GetBooksQuery.Sort.CREATED_AT_DESC); // keep your default
    }

    public StringProperty searchTextProperty() {
        return searchText;
    }

    public ObjectProperty<GetBooksQuery.Sort> sortProperty() {
        return sort;
    }

    public ObjectProperty<BookStatus> statusProperty() {
        return status;
    }

    public LongProperty totalItemsProperty() {
        return totalItems;
    }
}
