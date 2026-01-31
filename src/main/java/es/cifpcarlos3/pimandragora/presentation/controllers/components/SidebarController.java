package es.cifpcarlos3.pimandragora.presentation.controllers.components;

import es.cifpcarlos3.pimandragora.application.userprofile.usecases.getcurrentuser.GetCurrentUserUseCase;
import es.cifpcarlos3.pimandragora.application.userprofile.usecases.getcurrentuser.dtos.GetCurrentUserResponse;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.SupabaseSession;
import es.cifpcarlos3.pimandragora.presentation.app.di.AppContext;
import es.cifpcarlos3.pimandragora.presentation.app.navigation.PageNavigator;
import es.cifpcarlos3.pimandragora.presentation.app.navigation.SceneNavigator;
import es.cifpcarlos3.pimandragora.presentation.app.navigation.routes.LayoutRoutes;
import es.cifpcarlos3.pimandragora.presentation.app.navigation.routes.PageRoutes;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SidebarController {

    private static final Logger log =
            LoggerFactory.getLogger(SidebarController.class);

    private static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");

    // Manual wiring
    private final AppContext context = AppContext.get();
    private final GetCurrentUserUseCase getCurrentUserUseCase = context.getGetCurrentUserUseCase();

    // FXML (User)
    @FXML
    private Button profileButton;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label userEmailLabel;

    // FXML (Nav)
    @FXML
    private Button booksButton;
    @FXML
    private Button authorButton;
    @FXML
    private Button categoryButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button helpButton;

    // FXML (Footer)
    @FXML
    private Button logoutButton;

    private Button selectedButton;

    public void goToAuthors(ActionEvent e) {
        log.info("Navigate: Authors");
        selectNav(authorButton);
        PageNavigator.goTo(PageRoutes.AUTHORS);
    }

    // -------------------------
    // UI helpers
    // -------------------------
    private void selectNav(Button btn) {
        if (btn == null) return;

        if (selectedButton != null) {
            selectedButton.pseudoClassStateChanged(SELECTED, false);
        }

        selectedButton = btn;
        selectedButton.pseudoClassStateChanged(SELECTED, true);
    }

    public void goToBooks(ActionEvent e) {
        log.info("Navigate: Books");
        selectNav(booksButton);
        PageNavigator.goTo(PageRoutes.BOOKS);
    }

    public void goToCategories(ActionEvent e) {
        log.info("Navigate: Categories");
        selectNav(categoryButton);
        // PageNavigator.goTo(PageRoutes.CATEGORIES);
    }

    public void goToHelp(ActionEvent e) {
        log.info("Navigate: Help");
        selectNav(helpButton);
        // PageNavigator.goTo(PageRoutes.HELP);
    }

    public void goToSettings(ActionEvent e) {
        log.info("Navigate: Settings");
        selectNav(settingsButton);
        // PageNavigator.goTo(PageRoutes.SETTINGS);
    }

    // -------------------------
    // Logout
    // -------------------------
    public void logout(ActionEvent e) {
        log.info("Logout requested");

        try {
            context.logout();
            log.info("Logout completed");
        } catch (Exception ex) {
            // No bloquees el logout local por un fallo de red
            log.warn("Logout failed (will clear local session anyway)", ex);
        } finally {
            SupabaseSession.clear();
            Platform.runLater(() -> SceneNavigator.setRoot(LayoutRoutes.AUTH_LAYOUT));
        }
    }

    @FXML
    private void initialize() {
        log.debug("Sidebar initialized");

        setUserLabels("-", "-");

        Thread t = new Thread(this::loadCurrentUserSafely, "sidebar-load-user");
        t.setDaemon(true);
        t.start();

        selectNav(booksButton);
    }

    private void setUserLabels(String name, String email) {
        if (userNameLabel != null) userNameLabel.setText(name);
        if (userEmailLabel != null) userEmailLabel.setText(email);
    }

    // -------------------------
    // User loading
    // -------------------------
    private void loadCurrentUserSafely() {
        try {
            if (!SupabaseSession.hasToken()) {
                log.debug("No session token, sidebar user labels set to default");
                Platform.runLater(() -> setUserLabels("-", "-"));
                return;
            }

            log.debug("Loading current user for sidebar...");
            GetCurrentUserResponse u = getCurrentUserUseCase.execute();

            String name = pickBestName(u);
            String email = safe(u.email());

            log.debug("Sidebar user loaded name={} email={}", name, email);
            Platform.runLater(() -> setUserLabels(name, email));

        } catch (Exception ex) {
            log.warn("Failed to load current user for sidebar", ex);
            Platform.runLater(() -> setUserLabels("-", "-"));
        }
    }

    private static String pickBestName(GetCurrentUserResponse u) {
        if (u == null) return "-";

        String displayName = safe(u.displayName());
        if (!displayName.equals("-")) return displayName;

        String username = safe(u.username());
        if (!username.equals("-")) return username;

        String email = safe(u.email());
        if (!email.equals("-") && email.contains("@")) return email.substring(0, email.indexOf("@"));

        return "-";
    }

    private static String safe(String s) {
        if (s == null) return "-";
        String x = s.trim();
        return x.isBlank() ? "-" : x;
    }
}
