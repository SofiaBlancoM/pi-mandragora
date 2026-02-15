package es.cifpcarlos3.pimandragora.presentation.controllers.pages;

import es.cifpcarlos3.pimandragora.application.categories.usecases.createcategory.CreateCategoryUseCase;
import es.cifpcarlos3.pimandragora.application.categories.usecases.createcategory.dtos.CreateCategoryRequest;
import es.cifpcarlos3.pimandragora.application.categories.usecases.findcategorybyid.FindCategoryByIdUseCase;
import es.cifpcarlos3.pimandragora.application.categories.usecases.deletecategory.DeleteCategoryUseCase;
import es.cifpcarlos3.pimandragora.presentation.app.di.AppContext;
import es.cifpcarlos3.pimandragora.presentation.app.navigation.PageNavigator;
import es.cifpcarlos3.pimandragora.presentation.app.navigation.routes.PageRoutes;
import es.cifpcarlos3.pimandragora.presentation.books.controllers.pages.BookListController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.util.Optional;
import java.util.UUID;

public class GenreFormController {

    @FXML private Label formTitle;
    @FXML private TextField nameField;
    @FXML private Button viewBooksButton;
    @FXML private Button deleteButton;

    private UUID categoryId;
    private final CreateCategoryUseCase createCategoryUseCase = AppContext.get().getCreateCategoryUseCase();
    private final FindCategoryByIdUseCase findCategoryByIdUseCase = AppContext.get().getFindCategoryByIdUseCase();
    private final DeleteCategoryUseCase deleteCategoryUseCase = AppContext.get().getDeleteCategoryUseCase();

    @FXML
    public void initialize() {
        viewBooksButton.setVisible(false);
        if (deleteButton != null) deleteButton.setVisible(false);
    }

    public void setCategoryId(UUID id) {
        this.categoryId = id;
        if (id != null) {
            this.formTitle.setText("Editar Categoría");
            this.viewBooksButton.setVisible(true);
            if (deleteButton != null) deleteButton.setVisible(true);

            var category = findCategoryByIdUseCase.execute(id);
            if (category != null) {
                this.nameField.setText(category.name());
            }
        } else {
            this.formTitle.setText("Nueva Categoría");
            this.nameField.clear();
            this.viewBooksButton.setVisible(false);
            if (deleteButton != null) deleteButton.setVisible(false);
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        String name = nameField.getText();

        if (name == null || name.trim().isEmpty()) {
            return;
        }

        try {
            if (categoryId == null) {
                createCategoryUseCase.execute(new CreateCategoryRequest(name));
            } else {
                var repo = new es.cifpcarlos3.pimandragora.infrastructure.data.repositories.categories.SupabaseCategoryQueryRepository(
                        new es.cifpcarlos3.pimandragora.infrastructure.data.supabase.PostgrestApi(
                                new es.cifpcarlos3.pimandragora.infrastructure.data.supabase.SupabaseHttpClient()
                        )
                );
                repo.update(categoryId, name);
            }

            PageNavigator.goTo(PageRoutes.GENRES);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        if (categoryId != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar eliminación");
            alert.setHeaderText("¿Estás seguro?");
            alert.setContentText("Esta categoría se borrará permanentemente.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    deleteCategoryUseCase.execute(categoryId);
                    PageNavigator.goTo(PageRoutes.GENRES);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        PageNavigator.goTo(PageRoutes.GENRES);
    }

    @FXML
    private void handleViewBooks(ActionEvent event) {
        if (categoryId != null) {
            PageNavigator.goTo(PageRoutes.BOOKS, (BookListController controller) -> controller.setInitialCategory(categoryId));
        }
    }
}