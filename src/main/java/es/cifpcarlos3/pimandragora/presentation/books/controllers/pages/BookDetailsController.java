package es.cifpcarlos3.pimandragora.presentation.books.controllers.pages;

import es.cifpcarlos3.pimandragora.application.authors.usecases.findallauthors.FindAllAuthorsUseCase;
import es.cifpcarlos3.pimandragora.application.authors.usecases.findallauthors.dtos.FindAllAuthorsResponse;
import es.cifpcarlos3.pimandragora.application.books.usecases.delete.DeleteBookUseCase;
import es.cifpcarlos3.pimandragora.application.books.usecases.getbyid.GetBookByIdUseCase;
import es.cifpcarlos3.pimandragora.application.books.usecases.getbyid.dtos.GetBookByIdResponse;
import es.cifpcarlos3.pimandragora.application.books.usecases.update.UpdateBookUseCase;
import es.cifpcarlos3.pimandragora.application.books.usecases.update.dtos.UpdateBookCommand;
import es.cifpcarlos3.pimandragora.application.books.usecases.update.dtos.UpdateBookResponse;
import es.cifpcarlos3.pimandragora.application.categories.usecases.findallcategories.FindAllCategoriesUseCase;
import es.cifpcarlos3.pimandragora.application.categories.usecases.findallcategories.dtos.FindAllCategoriesResponse;
import es.cifpcarlos3.pimandragora.application.common.images.CoverImageUrlGenerator;
import es.cifpcarlos3.pimandragora.presentation.app.di.AppContext;
import es.cifpcarlos3.pimandragora.presentation.app.navigation.PageNavigator;
import es.cifpcarlos3.pimandragora.presentation.app.navigation.routes.PageRoutes;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BookDetailsController {

    private final AppContext context = AppContext.get();

    private final GetBookByIdUseCase getBookByIdUseCase = context.getGetBookByIdUseCase();
    private final FindAllAuthorsUseCase findAllAuthorsUseCase = context.getFindAllAuthorsUseCase();
    private final FindAllCategoriesUseCase findAllCategoriesUseCase = context.getFindAllCategoriesUseCase();
    private final UpdateBookUseCase updateBookUseCase = context.getUpdateBookUseCase();
    private final DeleteBookUseCase deleteBookUseCase = context.getDeleteBookUseCase();

    private final CoverImageUrlGenerator coverUrlResolver = context.session().coverResolver();

    @FXML
    private StackPane contentHost;

    private BookFormController bookFormController;

    private UUID bookId;

    public void setBookId(UUID bookId) {
        this.bookId = bookId;
        if (bookFormController != null) loadData();
    }

    private void loadData() {
        if (bookId == null) return;

        try {
            GetBookByIdResponse book = getBookByIdUseCase.execute(bookId);
            bookFormController.setEditMode(book);

            setCoverPreviewFrom(book.coverImagePath());

        } catch (Exception ex) {
            showError("Fallo al cargar los detalles del libro", ex);
        }
    }

    private void setCoverPreviewFrom(String coverImagePath) {
        if (coverImagePath == null || coverImagePath.isBlank()) return;

        String url = coverUrlResolver.generate(coverImagePath);
        bookFormController.setCoverPreview(new Image(url, true));
    }

    private void showError(String title, Exception ex) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText(title);
            a.setContentText(ex.getMessage());
            a.showAndWait();
        });
    }

    private UpdateBookResponse getUpdateBookResponse() {
        var draft = bookFormController.getDraft();

        UpdateBookCommand cmd = new UpdateBookCommand(
                draft.id(),
                draft.isbn(),
                draft.title(),
                draft.authorId(),
                draft.categoryId(),
                draft.publisher(),
                draft.publicationDate(),
                draft.price(),
                draft.stock(),
                draft.status(),
                draft.newCoverFile(),
                draft.removeCover()
        );

        return updateBookUseCase.execute(cmd);
    }

    @FXML
    private void initialize() {
        loadForm();
        loadReferenceData();
    }

    private void loadForm() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                    getClass().getResource(PageRoutes.BOOK_FORM)
            ));
            Node node = loader.load();
            this.bookFormController = loader.getController();

            wireFormActions();

            contentHost.getChildren().setAll(node);
        } catch (IOException e) {
            throw new RuntimeException("Fallo al cargar el book-form.fxml", e);
        }
    }

    private void loadReferenceData() {
        try {
            List<FindAllAuthorsResponse> authors = findAllAuthorsUseCase.execute();
            List<FindAllCategoriesResponse> categories = findAllCategoriesUseCase.execute();

            bookFormController.setAuthors(authors);
            bookFormController.setCategories(categories);

            if (bookId != null) {
                loadData();
            }

        } catch (Exception ex) {
            showError("Fallo al cargar autores/categorias", ex);
        }
    }

    private void onCancel() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Descartar cambios?",
                ButtonType.CANCEL, ButtonType.OK);
        confirm.setHeaderText("Cancelar");

        confirm.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                loadData();
            }
        });
    }

    private void onDelete() {
        if (bookId == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Vas a borrar este libro de forma permanente.\n¿Continuar?",
                ButtonType.CANCEL, ButtonType.OK);
        confirm.setHeaderText("Borrar libro");

        confirm.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;

            try {
                deleteBookUseCase.execute(bookId);
                showInfo("Borrado", "Libro borrado exitosamente");

                PageNavigator.goTo(PageRoutes.BOOKS);

            } catch (Exception ex) {
                showError("No se pudo eliminar el libro", ex);
            }
        });
    }

    private void onSave() {
        try {
            var updated = getUpdateBookResponse();

            coverUrlResolver.clear();

            GetBookByIdResponse reloaded = getBookByIdUseCase.execute(updated.id());
            bookFormController.setEditMode(reloaded);
            setCoverPreviewFrom(reloaded.coverImagePath());

            showInfo("Guardado", "Libro actualizado correctamente");

        } catch (Exception ex) {
            showError("No se pudieron guardar los cambios", ex);
        }
    }

    private void showInfo(String header, String message) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Info");
            a.setHeaderText(header);
            a.setContentText(message);
            a.showAndWait();
        });
    }

    private void wireFormActions() {
        bookFormController.setOnSave(this::onSave);
        bookFormController.setOnCancel(this::onCancel);
        bookFormController.setOnDelete(this::onDelete);
    }
}
