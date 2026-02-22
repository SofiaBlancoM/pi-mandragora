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

    private final AppContext context = AppContext.get();

    private final BooksViewModel booksViewModel = context.newBooksViewModel();
    private final FindAllCategoriesUseCase getCategoriesUseCase = context.getFindAllCategoriesUseCase();

    private final FlowPane cards = new FlowPane();
    private final ScrollPane scroll = new ScrollPane(cards);

    private boolean suppressFilterEvents = false;

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

    private Node createCard(BookCardListViewModel cardListViewModel) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                    getClass().getResource("/views/components/bookCard/book-card.fxml")
            ));
            Node node = loader.load();

            BookCardController controller = loader.getController();
            controller.bind(cardListViewModel);
            controller.setOnClick(() -> {
                log.info("Open book details id={}", cardListViewModel.getId());
                PageNavigator.goTo(
                        PageRoutes.BOOK_DETAILS,
                        (BookDetailsController bookDetailsController) -> bookDetailsController.setBookId(cardListViewModel.getId())
                );
            });

            return node;
        } catch (IOException e) {
            log.error("Failed to load book-card.fxml", e);
            throw new RuntimeException("Error al cargar el book-card.fxml", e);
        }
    }

    private void setupFilters() {

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
            log.warn("Failed to load categories for filter", ex);
            categoryFilter.getItems().clear();
        }

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

        searchField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (suppressFilterEvents) return;
            log.debug("Filter changed: searchText={}", newValue);
            booksViewModel.searchTextProperty().set(newValue);
            goToFirstPageAndReload();
        });

        statusFilter.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (suppressFilterEvents) return;
            log.debug("Filter changed: status={}", newValue);
            booksViewModel.statusProperty().set(newValue);
            goToFirstPageAndReload();
        });

        categoryFilter.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (suppressFilterEvents) return;
            log.debug("Filter changed: categoryId={}", newValue == null ? null : newValue.id());
            booksViewModel.categoryIdProperty().set(newValue == null ? null : newValue.id());
            goToFirstPageAndReload();
        });

        sortFilter.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (suppressFilterEvents) return;
            log.debug("Filter changed: sort={}", newValue);
            booksViewModel.sortProperty().set(newValue);
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

    private static String toLabel(GetBooksQuery.Sort sort) {
        return switch (sort) {
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

        pagination.currentPageIndexProperty().addListener((observableValue, oldValue, newValue) -> {
            if (suppressFilterEvents) return;
            log.debug("Pagination changed: pageIndex={}", newValue);
            booksViewModel.loadPage(newValue.intValue());
        });

        booksViewModel.pageCountProperty().addListener((observableValue, oldValue, newValue) -> {
            int pageCount = Math.max(1, newValue.intValue());
            log.debug("Pagination pageCount updated: {}", pageCount);

            pagination.setPageCount(pageCount);

            if (pagination.getCurrentPageIndex() >= pageCount) {
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
        newBookButton.setOnAction(event -> {
            log.info("Navigate: Create book");
            PageNavigator.goTo(PageRoutes.BOOK_CREATE);
        });

        refreshButton.setOnAction(event -> {
            log.info("Refresh books list (reset filters)");

            suppressFilterEvents = true;
            try {
                searchField.clear();
                statusFilter.getSelectionModel().clearSelection();
                categoryFilter.getSelectionModel().clearSelection();
                sortFilter.getSelectionModel().select(GetBooksQuery.Sort.CREATED_AT_DESC);

                booksViewModel.resetFilters();

                booksViewModel.clearCoverCache();

                goToFirstPageAndReload();
            } finally {
                suppressFilterEvents = false;
            }
        });
    }
}
