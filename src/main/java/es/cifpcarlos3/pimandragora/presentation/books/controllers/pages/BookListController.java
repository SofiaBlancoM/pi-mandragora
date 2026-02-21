package es.cifpcarlos3.pimandragora.presentation.books.controllers.pages;

import es.cifpcarlos3.pimandragora.application.books.usecases.getbooks.dtos.GetBooksQuery;
import es.cifpcarlos3.pimandragora.application.categories.usecases.findallcategories.FindAllCategoriesUseCase;
import es.cifpcarlos3.pimandragora.application.categories.usecases.findallcategories.dtos.FindAllCategoriesResponse;
import es.cifpcarlos3.pimandragora.domain.enums.BookStatus;
import es.cifpcarlos3.pimandragora.presentation.app.di.AppContext;
import es.cifpcarlos3.pimandragora.presentation.app.navigation.PageNavigator;
import es.cifpcarlos3.pimandragora.presentation.app.navigation.routes.PageRoutes;
import es.cifpcarlos3.pimandragora.presentation.books.controllers.components.BookCardController;
import es.cifpcarlos3.pimandragora.presentation.books.viewmodels.BookCardListViewModel;
import es.cifpcarlos3.pimandragora.presentation.books.viewmodels.BooksViewModel;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;

public class BookListController {

    private static final Logger log =
            LoggerFactory.getLogger(BookListController.class);

    // Manual Dependency injection
    private final AppContext context = AppContext.get();

    private final BooksViewModel booksViewModel = context.newBooksViewModel();
    private final FindAllCategoriesUseCase getCategoriesUseCase = context.getFindAllCategoriesUseCase();

    // Single list UI (server-side pagination)
    private final FlowPane cards = new FlowPane();
    private final ScrollPane scroll = new ScrollPane(cards);

    private boolean suppressFilterEvents = false;

    // FXML
    @FXML
    private Pagination pagination;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<BookStatus> statusFilter;
    @FXML
    private ComboBox<FindAllCategoriesResponse> categoryFilter;
    @FXML
    private ComboBox<GetBooksQuery.Sort> sortFilter;
    @FXML
    private Button refreshButton;
    @FXML
    private Button newBookButton;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private Label errorLabel;
    @FXML
    private Label resultsLabel;

    public void setInitialCategory(UUID categoryId) {
        categoryFilter.getItems().stream()
                .filter(cat -> cat.id().equals(categoryId))
                .findFirst()
                .ifPresent(categoryFilter.getSelectionModel()::select);
    }

    @FXML
    private void initialize() {
        log.debug("BookListController initialized");

        setupCards();
        setupFilters();
        setupBindings();
        setupPagination();
        setupActions();

        log.debug("Loading books first page");
        booksViewModel.loadPage(0);
    }

    private void setupCards() {
        cards.setHgap(12);
        cards.setVgap(12);
        cards.setPadding(new Insets(16));
        cards.setPrefWrapLength(720);

        scroll.setFitToWidth(true);
        scroll.setPannable(true);

        booksViewModel.getItems().addListener((javafx.collections.ListChangeListener<BookCardListViewModel>) c -> {
            log.debug("Books list updated items={}", booksViewModel.getItems().size());
            cards.getChildren().setAll(
                    booksViewModel.getItems().stream().map(this::createCard).toList()
            );
        });
    }

    private Node createCard(BookCardListViewModel cardVm) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                    getClass().getResource("/views/components/bookCard/book-card.fxml")
            ));
            Node node = loader.load();

            BookCardController controller = loader.getController();
            controller.bind(cardVm);
            controller.setOnClick(() -> {
                log.info("Open book details id={}", cardVm.getId());
                PageNavigator.goTo(
                        PageRoutes.BOOK_DETAILS,
                        (BookDetailsController c) -> c.setBookId(cardVm.getId())
                );
            });

            return node;
        } catch (IOException e) {
            log.error("Failed to load book-card.fxml", e);
            throw new RuntimeException("Failed to load book-card.fxml", e);
        }
    }

    private void setupFilters() {
        // data
        statusFilter.getItems().setAll(
                Arrays.stream(BookStatus.values())
                        .sorted(Comparator.comparing(BookStatus::getDisplayName))
                        .toList()
        );

        sortFilter.getItems().setAll(GetBooksQuery.Sort.values());
        sortFilter.getSelectionModel().select(GetBooksQuery.Sort.CREATED_AT_DESC);

        try {
            categoryFilter.getItems().setAll(getCategoriesUseCase.execute());
            log.debug("Categories loaded count={}", categoryFilter.getItems().size());
        } catch (Exception ex) {
            // Category load failure shouldn't crash the whole page
            log.warn("Failed to load categories for filter", ex);
            categoryFilter.getItems().clear();
        }

        // renderers (unchanged)
        statusFilter.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(BookStatus item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getDisplayName());
            }
        });
        statusFilter.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(BookStatus item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getDisplayName());
            }
        });

        categoryFilter.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(FindAllCategoriesResponse item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.name());
            }
        });
        categoryFilter.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(FindAllCategoriesResponse item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.name());
            }
        });

        sortFilter.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(GetBooksQuery.Sort item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : toLabel(item));
            }
        });
        sortFilter.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(GetBooksQuery.Sort item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : toLabel(item));
            }
        });

        // listeners
        searchField.textProperty().addListener((obs, o, n) -> {
            if (suppressFilterEvents) return;
            log.debug("Filter changed: searchText={}", n);
            booksViewModel.searchTextProperty().set(n);
            goToFirstPageAndReload();
        });

        statusFilter.valueProperty().addListener((obs, o, n) -> {
            if (suppressFilterEvents) return;
            log.debug("Filter changed: status={}", n);
            booksViewModel.statusProperty().set(n);
            goToFirstPageAndReload();
        });

        categoryFilter.valueProperty().addListener((obs, o, n) -> {
            if (suppressFilterEvents) return;
            log.debug("Filter changed: categoryId={}", n == null ? null : n.id());
            booksViewModel.categoryIdProperty().set(n == null ? null : n.id());
            goToFirstPageAndReload();
        });

        sortFilter.valueProperty().addListener((obs, o, n) -> {
            if (suppressFilterEvents) return;
            log.debug("Filter changed: sort={}", n);
            booksViewModel.sortProperty().set(n);
            goToFirstPageAndReload();
        });
    }

    private void goToFirstPageAndReload() {
        log.debug("Reload books: first page");
        booksViewModel.loadPage(0);

        Platform.runLater(() -> {
            suppressFilterEvents = true;
            try {
                pagination.setCurrentPageIndex(0);
            } finally {
                suppressFilterEvents = false;
            }
        });
    }

    private static String toLabel(GetBooksQuery.Sort s) {
        return switch (s) {
            case CREATED_AT_DESC -> "Actualizados recientemente";
            case TITLE_ASC -> "Título Asc";
            case TITLE_DESC -> "Título Desc";
            case PRICE_ASC -> "Precio Asc";
            case PRICE_DESC -> "Precio Desc";
            case PUBLICATION_DATE_DESC -> "Publicados recientemente";
        };
    }

    private void setupBindings() {
        loadingIndicator.visibleProperty().bind(booksViewModel.loadingProperty());
        loadingIndicator.managedProperty().bind(booksViewModel.loadingProperty());

        errorLabel.textProperty().bind(
                Bindings.when(booksViewModel.errorProperty().isNull())
                        .then("")
                        .otherwise(booksViewModel.errorProperty())
        );
        errorLabel.visibleProperty().bind(booksViewModel.errorProperty().isNotNull());
        errorLabel.managedProperty().bind(errorLabel.visibleProperty());

        resultsLabel.textProperty().bind(booksViewModel.totalItemsProperty().asString("Total: %d"));
        refreshButton.disableProperty().bind(booksViewModel.loadingProperty());
    }

    private void setupPagination() {
        pagination.setPageFactory(pageIndex -> scroll);

        pagination.currentPageIndexProperty().addListener((obs, o, n) -> {
            if (suppressFilterEvents) return;
            log.debug("Pagination changed: pageIndex={}", n);
            booksViewModel.loadPage(n.intValue());
        });

        booksViewModel.pageCountProperty().addListener((obs, o, n) -> {
            int pc = Math.max(1, n.intValue());
            log.debug("Pagination pageCount updated: {}", pc);

            pagination.setPageCount(pc);

            if (pagination.getCurrentPageIndex() >= pc) {
                suppressFilterEvents = true;
                try {
                    pagination.setCurrentPageIndex(0);
                } finally {
                    suppressFilterEvents = false;
                }
            }
        });
    }

    private void setupActions() {
        newBookButton.setOnAction(e -> {
            log.info("Navigate: Create book");
            PageNavigator.goTo(PageRoutes.BOOK_CREATE);
        });

        refreshButton.setOnAction(e -> {
            log.info("Refresh books list (reset filters)");

            suppressFilterEvents = true;
            try {
                // Reset UI
                searchField.clear();
                statusFilter.getSelectionModel().clearSelection();
                categoryFilter.getSelectionModel().clearSelection();
                sortFilter.getSelectionModel().select(GetBooksQuery.Sort.CREATED_AT_DESC);

                // Reset VM
                booksViewModel.resetFilters();

                // Clear cached signed urls
                booksViewModel.clearCoverCache();

                goToFirstPageAndReload();
            } finally {
                suppressFilterEvents = false;
            }
        });
    }
}
