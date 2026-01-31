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
import es.cifpcarlos3.pimandragora.application.common.images.CoverImageUrlResolver;
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

    // -------------------------
    // Manual wiring (TFG mode)
    // -------------------------
    private final AppContext context = AppContext.get();

    private final GetBookByIdUseCase getBookByIdUseCase = context.getGetBookByIdUseCase();
    private final FindAllAuthorsUseCase findAllAuthorsUseCase = context.getFindAllAuthorsUseCase();
    private final FindAllCategoriesUseCase findAllCategoriesUseCase = context.getFindAllCategoriesUseCase();
    private final UpdateBookUseCase updateBookUseCase = context.getUpdateBookUseCase();
    private final DeleteBookUseCase deleteBookUseCase = context.getDeleteBookUseCase();

    // session-scoped resolver (cached signed urls)
    private final CoverImageUrlResolver coverUrlResolver = context.session().coverResolver();
    // -------------------------
    // FXML
    // -------------------------
    @FXML
    private StackPane contentHost;

    private BookFormController form;

    private UUID bookId;

    public void setBookId(UUID bookId) {
        this.bookId = bookId;
        if (form != null) loadData();
    }

    private void loadData() {
        if (bookId == null) return;

        try {
            GetBookByIdResponse book = getBookByIdUseCase.execute(bookId);
            form.setEditMode(book);

            setCoverPreviewFrom(book.coverImagePath());

        } catch (Exception ex) {
            showError("Failed to load book details", ex);
        }
    }

    private void setCoverPreviewFrom(String coverImagePath) {
        if (coverImagePath == null || coverImagePath.isBlank()) return;

        String url = coverUrlResolver.resolve(coverImagePath);
        form.setCoverPreview(new Image(url, true));
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

    @FXML
    private void initialize() {
        loadForm();
        loadReferenceData();
        // loadData() runs when setBookId is called
    }

    private void loadForm() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                    getClass().getResource(PageRoutes.BOOK_FORM)
            ));
            Node node = loader.load();
            this.form = loader.getController();

            wireFormActions();

            contentHost.getChildren().setAll(node);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load book-form.fxml", e);
        }
    }

    private void wireFormActions() {
        form.setOnSave(this::onSave);
        form.setOnCancel(this::onCancel);
        form.setOnDelete(this::onDelete);
    }

    private void onSave() {
        try {
            var updated = getUpdateBookResponse();

            // refresh signed urls (important if cover changed)
            coverUrlResolver.clear();

            // reload data and keep UI consistent
            GetBookByIdResponse reloaded = getBookByIdUseCase.execute(updated.id());
            form.setEditMode(reloaded);
            setCoverPreviewFrom(reloaded.coverImagePath());

            showInfo("Saved", "Book updated successfully.");

        } catch (Exception ex) {
            showError("Could not save changes", ex);
        }
    }

    private UpdateBookResponse getUpdateBookResponse() {
        var draft = form.getDraft();

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

    private void showInfo(String header, String message) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Info");
            a.setHeaderText(header);
            a.setContentText(message);
            a.showAndWait();
        });
    }

    private void onCancel() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Discard changes?",
                ButtonType.CANCEL, ButtonType.OK);
        confirm.setHeaderText("Cancel edit");

        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                loadData();
            }
        });
    }

    private void loadReferenceData() {
        try {
            List<FindAllAuthorsResponse> authors = findAllAuthorsUseCase.execute();
            List<FindAllCategoriesResponse> categories = findAllCategoriesUseCase.execute();

            form.setAuthors(authors);
            form.setCategories(categories);

            if (bookId != null) {
                loadData();
            }

        } catch (Exception ex) {
            showError("Failed to load authors/categories", ex);
        }
    }

    private void onDelete() {
        if (bookId == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "This will permanently delete the book.\nContinue?",
                ButtonType.CANCEL, ButtonType.OK);
        confirm.setHeaderText("Delete book");

        confirm.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;

            try {
                deleteBookUseCase.execute(bookId);
                showInfo("Deleted", "Book deleted successfully.");

                // Navigate back to list
                PageNavigator.goTo(PageRoutes.BOOKS);

            } catch (Exception ex) {
                showError("Could not delete book", ex);
            }
        });
    }
}
