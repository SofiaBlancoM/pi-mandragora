package es.cifpcarlos3.pimandragora.presentation.books.viewmodels;

import es.cifpcarlos3.pimandragora.application.books.usecases.getbooks.GetBooksUseCase;
import es.cifpcarlos3.pimandragora.application.books.usecases.getbooks.dtos.GetBooksListItemResponse;
import es.cifpcarlos3.pimandragora.application.books.usecases.getbooks.dtos.GetBooksQuery;
import es.cifpcarlos3.pimandragora.application.common.images.CoverImageUrlResolver;
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
    private final CoverImageUrlResolver coverResolver;
    private final int pageSize;

    // Filters
    private final StringProperty searchText = new SimpleStringProperty();
    private final ObjectProperty<BookStatus> status = new SimpleObjectProperty<>();
    private final ObjectProperty<UUID> categoryId = new SimpleObjectProperty<>();
    private final ObjectProperty<UUID> authorId = new SimpleObjectProperty<>();

    // State
    @Getter
    private final ObservableList<BookCardListViewModel> items = FXCollections.observableArrayList();
    private final IntegerProperty pageCount = new SimpleIntegerProperty(1);
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final StringProperty error = new SimpleStringProperty();
    private final LongProperty totalItems = new SimpleLongProperty(0);
    private final ObjectProperty<GetBooksQuery.Sort> sort =
            new SimpleObjectProperty<>(GetBooksQuery.Sort.CREATED_AT_DESC);

    public BooksViewModel(GetBooksUseCase getBooksUseCase, CoverImageUrlResolver coverResolver, int pageSize) {
        this.getBooksUseCase = getBooksUseCase;
        this.coverResolver = coverResolver;
        this.pageSize = pageSize;
    }

    public ObjectProperty<UUID> authorIdProperty() {
        return authorId;
    }

    public ObjectProperty<UUID> categoryIdProperty() {
        return categoryId;
    }

    public void clearCoverCache() {
        coverResolver.clear();
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
                items.setAll(page.items().stream().map(this::toCardVm).toList());
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

    private BookCardListViewModel toCardVm(GetBooksListItemResponse dto) {
        int year = dto.publicationDate() != null ? dto.publicationDate().getYear() : 0;
        String coverUrl = coverResolver.resolve(dto.coverImagePath());

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
        coverResolver.clear();   // clears signed-url cache
        resetFilters();          // optional: only if you want refresh to reset filters
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
