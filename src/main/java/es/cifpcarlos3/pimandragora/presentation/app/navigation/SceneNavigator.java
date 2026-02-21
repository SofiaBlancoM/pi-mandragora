package es.cifpcarlos3.pimandragora.presentation.app.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.Objects;

/**
 * Navegación entre el auth y main layout
 */
public final class SceneNavigator {

    private static Scene scene;

    private SceneNavigator() {
    }

    public static void bindScene(Scene appScene) {
        scene = appScene;
    }

    public static void setRoot(String fxmlPath) {
        Objects.requireNonNull(scene, "La escena no está ligada al SceneNavigator");

        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(
                    SceneNavigator.class.getResource(fxmlPath)
            ));
            scene.setRoot(root);
        } catch (IOException e) {
            throw new RuntimeException("Fallo al cargar la ruta: " + fxmlPath, e);
        }
    }
}
