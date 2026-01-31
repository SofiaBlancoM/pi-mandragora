package es.cifpcarlos3.pimandragora.presentation.app.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class PageNavigator {
    private static final CopyOnWriteArrayList<Consumer<String>> listeners = new CopyOnWriteArrayList<>();
    private static StackPane host;
    // ---- new: current route + listeners ----
    private static volatile String currentRoute;

    public static void addRouteListener(Consumer<String> listener) {
        listeners.add(listener);
        // sync immediately so sidebar marks correctly on first render
        if (currentRoute != null) listener.accept(currentRoute);
    }

    public static void bindHost(StackPane contentHost) {
        host = contentHost;
    }

    public static String currentRoute() {
        return currentRoute;
    }

    public static void goTo(String fxmlPath) {
        goTo(fxmlPath, c -> {
        });
    }

    public static <T> void goTo(String fxmlPath, Consumer<T> controllerSetup) {
        Objects.requireNonNull(host, "Navigator host is not bound");

        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                    PageNavigator.class.getResource(fxmlPath)
            ));

            Node view = loader.load();
            T controller = loader.getController();

            if (controllerSetup != null && controller != null) {
                controllerSetup.accept(controller);
            }

            host.getChildren().setAll(view);

            // ---- new: publish route change AFTER successful navigation ----
            currentRoute = fxmlPath;
            listeners.forEach(l -> l.accept(fxmlPath));

        } catch (IOException e) {
            throw new RuntimeException("Failed to load: " + fxmlPath, e);
        }
    }

    public static void removeRouteListener(Consumer<String> listener) {
        listeners.remove(listener);
    }
}
