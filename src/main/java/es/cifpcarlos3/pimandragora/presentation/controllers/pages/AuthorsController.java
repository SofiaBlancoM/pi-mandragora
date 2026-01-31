package es.cifpcarlos3.pimandragora.presentation.controllers.pages;

import es.cifpcarlos3.pimandragora.domain.entities.Author;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AuthorsController {
    @FXML
    private TableView<Author> authorsTable;
    @FXML
    private TableColumn<Author, String> firstNameColumn;
    @FXML
    private TableColumn<Author, String> lastNameColumn;
    @FXML
    private TableColumn<Author, Integer> booksColumn;

    @FXML
    private void initialize() {


    }
}
