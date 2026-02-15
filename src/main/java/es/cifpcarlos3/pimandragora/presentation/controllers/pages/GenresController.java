package es.cifpcarlos3.pimandragora.presentation.controllers.pages;

import es.cifpcarlos3.pimandragora.application.categories.usecases.findallcategories.FindAllCategoriesUseCase;
import es.cifpcarlos3.pimandragora.application.categories.usecases.findallcategories.dtos.FindAllCategoriesResponse;
import es.cifpcarlos3.pimandragora.presentation.app.di.AppContext;
import es.cifpcarlos3.pimandragora.presentation.app.navigation.PageNavigator;
import es.cifpcarlos3.pimandragora.presentation.app.navigation.routes.PageRoutes;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import java.util.List;

public class GenresController {

    @FXML
    private FlowPane categoriesGrid;

    private final FindAllCategoriesUseCase findAllCategoriesUseCase = AppContext.get().getFindAllCategoriesUseCase();

    @FXML
    public void initialize() {
        loadCategories();
    }


    @FXML
    private void goToCreateForm(ActionEvent event) {

        PageNavigator.goTo(PageRoutes.GENRE_FORM);
    }

    private void loadCategories() {
        Thread thread = new Thread(() -> {
            try {
                List<FindAllCategoriesResponse> categories = findAllCategoriesUseCase.execute();
                Platform.runLater(() -> renderCategories(categories));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void renderCategories(List<FindAllCategoriesResponse> categories) {
        categoriesGrid.getChildren().clear();

        for (FindAllCategoriesResponse category : categories) {
            VBox card = new VBox();
            card.getStyleClass().add("category-card");

            Label nameLabel = new Label(category.name().toUpperCase());
            nameLabel.getStyleClass().add("category-name");

            card.getChildren().add(nameLabel);


            card.setOnMouseClicked(event -> {
                PageNavigator.goTo(
                        PageRoutes.GENRE_FORM,

                        (GenreFormController controller) -> controller.setCategoryId(category.id())
                );
            });

            categoriesGrid.getChildren().add(card);
        }
    }
}