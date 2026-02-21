package es.cifpcarlos3.pimandragora.presentation.app;

import atlantafx.base.theme.PrimerLight;
import es.cifpcarlos3.pimandragora.presentation.app.config.AppConfig;
import es.cifpcarlos3.pimandragora.presentation.app.config.PropertyKey;
import es.cifpcarlos3.pimandragora.presentation.app.constants.ResourcesConstants;
import es.cifpcarlos3.pimandragora.presentation.app.di.AppContext;
import es.cifpcarlos3.pimandragora.presentation.app.navigation.SceneNavigator;
import es.cifpcarlos3.pimandragora.presentation.app.navigation.routes.LayoutRoutes;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Objects;

public class MandragoraApplication extends Application {

    @Override
    public void start(Stage stage) {
        AppContext.init();
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        Scene scene = new Scene(new StackPane(), 1200, 800);
        SceneNavigator.bindScene(scene);

        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource(ResourcesConstants.BASE_STYLES_PATH)).toExternalForm()
        );

        SceneNavigator.setRoot(LayoutRoutes.AUTH_LAYOUT);

        stage.setTitle(AppConfig.getProperty(PropertyKey.APP_NAME));
        stage.setScene(scene);
        stage.show();
    }
}
