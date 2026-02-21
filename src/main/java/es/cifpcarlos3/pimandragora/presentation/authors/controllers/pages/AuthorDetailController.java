package es.cifpcarlos3.pimandragora.presentation.authors.controllers.pages;

import es.cifpcarlos3.pimandragora.presentation.app.di.AppContext;
import es.cifpcarlos3.pimandragora.presentation.app.navigation.PageNavigator;
import es.cifpcarlos3.pimandragora.presentation.app.navigation.routes.PageRoutes;
import es.cifpcarlos3.pimandragora.presentation.authors.viewmodels.AuthorDetailViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorDetailController {

    private static final Logger log = LoggerFactory.getLogger(AuthorDetailController.class);
    public static String selectedAuthorId;

    private final AuthorDetailViewModel viewModel;


    @FXML private TextField nameField;
    @FXML private TextArea bioArea;
    @FXML private ListView<String> booksListView;

    public AuthorDetailController() {
        var ctx = AppContext.get();
        var repo = ctx.getAuthorCommandRepository();


        var getUseCase = new es.cifpcarlos3.pimandragora.application.authors.usecases.dtos.GetAuthorDetailUseCase(
                new es.cifpcarlos3.pimandragora.infrastructure.data.repositories.authors.SupabaseAuthorQueryRepository(
                        new es.cifpcarlos3.pimandragora.infrastructure.data.supabase.PostgrestApi(new es.cifpcarlos3.pimandragora.infrastructure.data.supabase.SupabaseHttpClient())
                )
        );
        var updateUseCase = new es.cifpcarlos3.pimandragora.application.authors.usecases.dtos.UpdateAuthorUseCase(repo);
        var deleteUseCase = new es.cifpcarlos3.pimandragora.application.authors.usecases.dtos.DeleteAuthorUseCase(repo);

        this.viewModel = new AuthorDetailViewModel(getUseCase, updateUseCase, deleteUseCase);
    }

    @FXML
    public void initialize() {
        if (selectedAuthorId != null) {
            setupBindings();
            viewModel.loadAuthor(selectedAuthorId);
        }
    }

    private void setupBindings() {
        if (nameField != null) {
            nameField.textProperty().bindBidirectional(viewModel.fullNameProperty());
        }
        if (bioArea != null) {
            bioArea.textProperty().bindBidirectional(viewModel.bioProperty());
        }


        if (booksListView != null) {
            viewModel.getBooks().addListener((javafx.collections.ListChangeListener<es.cifpcarlos3.pimandragora.application.authors.usecases.dtos.AuthorDetailResponse.AuthorBookResponse>) c -> {
                javafx.application.Platform.runLater(() -> {
                    var titles = viewModel.getBooks().stream()
                            .map(es.cifpcarlos3.pimandragora.application.authors.usecases.dtos.AuthorDetailResponse.AuthorBookResponse::title)
                            .toList();
                    booksListView.getItems().setAll(titles);
                    log.debug("Lista de libros actualizada: {} items", titles.size());
                });
            });
        }
    }
    @FXML
    private void handleBack(javafx.event.ActionEvent event) {
        PageNavigator.goTo(PageRoutes.AUTHORS);
    }

    @FXML
    private void handleSave(javafx.event.ActionEvent event) {
        if (viewModel != null) {
            try {
                viewModel.updateAuthor();


                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Éxito");
                alert.setHeaderText(null);
                alert.setContentText("¡Los cambios se han guardado correctamente!");


                alert.showAndWait();

                log.info("Mensaje de éxito mostrado al usuario");
            } catch (Exception e) {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("No se pudo guardar");
                alert.setContentText("Hubo un problema al conectar con la base de datos.");
                alert.showAndWait();
            }
        }
    }
}