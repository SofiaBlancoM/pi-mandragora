package es.cifpcarlos3.pimandragora.presentation.authors.controllers.pages;

import es.cifpcarlos3.pimandragora.application.authors.usecases.dtos.CreateAuthorUseCase;
import es.cifpcarlos3.pimandragora.presentation.app.di.AppContext;
import es.cifpcarlos3.pimandragora.presentation.app.navigation.PageNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AuthorCreateController {

    @FXML private TextField nameField;
    @FXML private TextArea bioArea;


    private final CreateAuthorUseCase createAuthorUseCase = AppContext.get().getCreateAuthorUseCase();

    @FXML
    private void handleSave() {
        String name = nameField.getText();
        if (name == null || name.trim().isEmpty()) {
            showError("El nombre es obligatorio");
            return;
        }

        try {

            createAuthorUseCase.execute(name);
            PageNavigator.goTo("/views/pages/authors/authorList/authors-list.fxml");
        } catch (Exception e) {
            showError("Error al crear: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        PageNavigator.goTo("/views/pages/authors/authorList/authors-list.fxml");
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.show();
    }
}