package es.cifpcarlos3.pimandragora.presentation.books.controllers.pages;

import es.cifpcarlos3.pimandragora.application.authors.usecases.FindAllAuthorsUseCase;
import es.cifpcarlos3.pimandragora.application.authors.usecases.dtos.FindAllAuthorsResponse;
import es.cifpcarlos3.pimandragora.application.books.usecases.create.CreateBookUseCase;
import es.cifpcarlos3.pimandragora.application.books.usecases.create.dtos.CreateBookCommand;
import es.cifpcarlos3.pimandragora.application.books.usecases.create.dtos.CreateBookResponse;
import es.cifpcarlos3.pimandragora.application.categories.usecases.findallcategories.FindAllCategoriesUseCase;
import es.cifpcarlos3.pimandragora.presentation.app.di.AppContext;
import es.cifpcarlos3.pimandragora.presentation.app.navigation.PageNavigator;
import es.cifpcarlos3.pimandragora.presentation.app.navigation.routes.PageRoutes;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class BookCreateController {

    private static final Logger log =
            LoggerFactory.getLogger(BookCreateController.class);

    private final AppContext context = AppContext.get();

    private final FindAllAuthorsUseCase findAllAuthorsUseCase = context.getFindAllAuthorsUseCase();
    private final FindAllCategoriesUseCase findAllCategoriesUseCase = context.getFindAllCategoriesUseCase();
    private final CreateBookUseCase createBookUseCase = context.getCreateBookUseCase();

    @FXML
    private StackPane contentHost;

    private BookFormController bookFormController;

    @FXML
    private void initialize() {
        log.info("Open: Create book");

        loadForm();
        loadReferenceData();
        bookFormController.setCreateMode();
    }

    private void loadForm() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                    getClass().getResource(PageRoutes.BOOK_FORM)
            ));
            Node node = loader.load();
            this.bookFormController = loader.getController();

            bookFormController.setOnSave(this::onSave);
            bookFormController.setOnCancel(this::onCancel);

            contentHost.getChildren().setAll(node);

        } catch (IOException e) {
            log.error("Failed to load book form FXML: {}", PageRoutes.BOOK_FORM, e);
            throw new RuntimeException("Fallo al cargar book-form.fxml", e);
        }
    }

    private void onSave() {
        log.info("Create book: save requested");

        try {
            var draft = bookFormController.getDraft();

            CreateBookCommand command = new CreateBookCommand(
                    draft.isbn(),
                    draft.title(),
                    draft.authorId(),
                    draft.categoryId(),
                    draft.publisher(),
                    draft.publicationDate(),
                    draft.price(),
                    draft.stock(),
                    draft.status(),
                    draft.newCoverFile()
            );

            CreateBookResponse created = createBookUseCase.execute(command);

            log.info("Create book: success id={}", created.id());

            showInfo();

            PageNavigator.goTo(
                    PageRoutes.BOOK_DETAILS,
                    (BookDetailsController controller) -> controller.setBookId(created.id())
            );

        } catch (Exception ex) {
            log.warn("Create book: failed", ex);
            showError("No se pudo crear el libro", ex);
        }
    }

    private void showInfo() {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Info");
            a.setHeaderText("Creado");
            a.setContentText("Libro creado correctamente");
            a.showAndWait();
        });
    }

    private void showError(String header, Exception ex) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText(header);
            a.setContentText(ex.getMessage());
            a.showAndWait();
        });
    }

    private void onCancel() {
        log.info("Create book: cancel requested");

        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION,
                "¿Descartar cambios?",
                ButtonType.CANCEL, ButtonType.OK
        );
        confirm.setHeaderText("Cancelar");

        confirm.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                log.info("Create book: canceled, back to list");
                PageNavigator.goTo(PageRoutes.BOOKS);
            } else {
                log.debug("Create book: cancel dismissed");
            }
        });
    }

    private void loadReferenceData() {
        try {
            var authors = findAllAuthorsUseCase.execute();
            var categories = findAllCategoriesUseCase.execute();

            bookFormController.setAuthors(authors);
            bookFormController.setCategories(categories);

            log.debug("Create book: reference data loaded authors={} categories={}",
                    authors.size(),
                    categories.size()
            );

        } catch (Exception ex) {
            log.warn("Create book: failed to load authors/categories", ex);
            showError("Error al cargar autores/categorias", ex);
        }
    }
}
