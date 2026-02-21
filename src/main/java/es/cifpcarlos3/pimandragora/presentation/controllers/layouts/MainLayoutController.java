package es.cifpcarlos3.pimandragora.presentation.controllers.layouts;

import es.cifpcarlos3.pimandragora.presentation.app.navigation.PageNavigator;
import es.cifpcarlos3.pimandragora.presentation.app.navigation.routes.PageRoutes;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class MainLayoutController {

    @FXML
    private StackPane contentHost;

    @FXML
    private void initialize() {
        PageNavigator.bindHost(contentHost);
        PageNavigator.goTo(PageRoutes.BOOKS);
    }
}
