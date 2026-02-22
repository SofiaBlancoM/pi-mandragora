package es.cifpcarlos3.pimandragora.presentation.authors.controllers.components;

import es.cifpcarlos3.pimandragora.presentation.authors.viewmodels.AuthorCardViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AuthorCardController {
    @FXML private Label nameLabel;
    @FXML private Label bioLabel;


    public void setData(AuthorCardViewModel authorVm) {

        nameLabel.textProperty().bind(authorVm.getFullName());
        bioLabel.textProperty().bind(authorVm.getBiography());
    }
}