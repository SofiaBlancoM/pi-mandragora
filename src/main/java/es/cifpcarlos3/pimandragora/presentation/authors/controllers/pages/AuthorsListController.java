package es.cifpcarlos3.pimandragora.presentation.authors.controllers.pages;

import es.cifpcarlos3.pimandragora.presentation.app.di.AppContext;
import es.cifpcarlos3.pimandragora.presentation.app.navigation.PageNavigator;
import es.cifpcarlos3.pimandragora.presentation.app.navigation.routes.PageRoutes;
import es.cifpcarlos3.pimandragora.presentation.authors.controllers.components.AuthorCardController;
import es.cifpcarlos3.pimandragora.presentation.authors.viewmodels.AuthorCardViewModel;
import es.cifpcarlos3.pimandragora.presentation.authors.viewmodels.AuthorsViewModel;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class AuthorsListController {

    private static final Logger log = LoggerFactory.getLogger(AuthorsListController.class);

    private final AppContext context = AppContext.get();
    private final AuthorsViewModel authorsViewModel = context.newAuthorsViewModel();

    // UI Components para la rejilla
    private final FlowPane cards = new FlowPane();
    private final ScrollPane scroll = new ScrollPane(cards);

    private boolean suppressFilterEvents = false;

    @FXML private Pagination pagination;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortFilter;
    @FXML private Button refreshButton;
    @FXML private Button newAuthorButton;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label errorLabel;
    @FXML private Label resultsLabel;

    @FXML
    private void initialize() {
        log.debug("AuthorsListController initialized");

        setupCards();
        setupFilters();
        setupBindings();
        setupPagination();
        setupActions();


        log.debug("Loading initial authors data");
        authorsViewModel.loadData(0);
    }

    private void setupCards() {
        cards.setHgap(30);
        cards.setVgap(30);
        cards.setPadding(new Insets(30));
        cards.setAlignment(Pos.TOP_LEFT);
        cards.prefWrapLengthProperty().set(1050);

        scroll.setFitToWidth(true);
        scroll.setPannable(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");


        authorsViewModel.getItems().addListener((ListChangeListener<AuthorCardViewModel>) c -> {
            cards.getChildren().setAll(
                    authorsViewModel.getItems().stream().map(this::createCard).toList()
            );
        });
    }

    private Node createCard(AuthorCardViewModel cardVm) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                    getClass().getResource("/views/components/authorsCard/authors-card.fxml")
            ));
            Node node = loader.load();
            AuthorCardController controller = loader.getController();
            controller.setData(cardVm);


            node.setOnMouseClicked(event -> {

                AuthorDetailController.selectedAuthorId = cardVm.getId().toString();
                PageNavigator.goTo(PageRoutes.AUTHOR_DETAILS);
            });

            node.setCursor(javafx.scene.Cursor.HAND);


            return node;
        } catch (IOException e) {
            log.error("Failed to load authors-card.fxml", e);
            return new Label("Error al cargar tarjeta");
        }
    }


    private void setupFilters() {
        sortFilter.getItems().setAll("Nombre (A-Z)", "Nombre (Z-A)");
        sortFilter.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleSearch() {

        authorsViewModel.searchTextProperty().set(searchField.getText());
        authorsViewModel.loadData(0);
        log.debug("Búsqueda activada: {}", searchField.getText());
    }

    @FXML
    private void handleSort() {
        String seleccion = sortFilter.getValue();


        if (seleccion != null && seleccion.contains("Z-A")) {
            authorsViewModel.sortOrderProperty().set("full_name.desc");
        } else {
            authorsViewModel.sortOrderProperty().set("full_name.asc");
        }


        authorsViewModel.loadData(0);
    }

    private void setupBindings() {
        loadingIndicator.visibleProperty().bind(authorsViewModel.loadingProperty());
        loadingIndicator.managedProperty().bind(authorsViewModel.loadingProperty());

        errorLabel.textProperty().bind(
                Bindings.when(authorsViewModel.errorProperty().isNull())
                        .then("")
                        .otherwise(authorsViewModel.errorProperty())
        );
        errorLabel.visibleProperty().bind(authorsViewModel.errorProperty().isNotNull());

        resultsLabel.textProperty().bind(authorsViewModel.totalItemsProperty().asString("Total: %d autores"));
        refreshButton.disableProperty().bind(authorsViewModel.loadingProperty());
        pagination.pageCountProperty().bind(authorsViewModel.totalPagesProperty());
    }

    private void setupPagination() {

        pagination.setPageFactory(pageIndex -> {
            log.debug("Pagination requested page: {}", pageIndex);
            authorsViewModel.loadData(pageIndex);
            return scroll;
        });
    }

    private void setupActions() {
        newAuthorButton.setOnAction(e -> PageNavigator.goTo(PageRoutes.AUTHOR_CREATE));

        refreshButton.setOnAction(e -> {
            suppressFilterEvents = true;
            try {
                searchField.clear();
                sortFilter.getSelectionModel().selectFirst();
                authorsViewModel.loadData(0);
            } finally {
                suppressFilterEvents = false;
            }
        });
    }
}