package es.cifpcarlos3.pimandragora.presentation.controllers.pages;

import es.cifpcarlos3.pimandragora.application.auth.dtos.AuthSessionDto;
import es.cifpcarlos3.pimandragora.presentation.app.config.AppConfig;
import es.cifpcarlos3.pimandragora.presentation.app.config.PropertyKey;
import es.cifpcarlos3.pimandragora.presentation.app.constants.LoginConstants;
import es.cifpcarlos3.pimandragora.presentation.app.di.AppContext;
import es.cifpcarlos3.pimandragora.presentation.app.navigation.SceneNavigator;
import es.cifpcarlos3.pimandragora.presentation.app.navigation.routes.LayoutRoutes;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {

    private static final Logger log =
            LoggerFactory.getLogger(LoginController.class);

    private final AppContext context = AppContext.get();

    @FXML
    public TextField emailField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public Label errorLabel;

    @FXML
    public void onAutoLoging(ActionEvent actionEvent) {
        hideError();

        String email = AppConfig.getProperty(PropertyKey.TEST_EMAIL);
        String password = AppConfig.getProperty(PropertyKey.TEST_PASSWORD);

        log.info("Auto-login attempt user={}", maskEmail(email));

        try {
            AuthSessionDto authSession = context.login(email, password);

            log.info("Login success user={}", maskEmail(email));

            SceneNavigator.setRoot(LayoutRoutes.MAIN_LAYOUT);

        } catch (Exception e) {
            log.warn("Auto-login failed user={}", maskEmail(email), e);
            showError();
        }
    }

    private void showError() {
        errorLabel.setText(LoginConstants.LoginError);
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
    }

    private void hideError() {
        errorLabel.setManaged(false);
        errorLabel.setVisible(false);
        errorLabel.setText("");
    }

    private static String maskEmail(String email) {
        if (email == null) return "-";
        String e = email.trim();
        int at = e.indexOf("@");
        if (at <= 1) return "***";
        return e.charAt(0) + "***" + e.substring(at);
    }

    @FXML
    private void onLogin() {
        hideError();

        String email = emailField.getText();
        String password = passwordField.getText();

        log.info("Login attempt user={}", maskEmail(email));

        try {
            AuthSessionDto authSession = context.login(email, password);

            log.info("Login success user={}", maskEmail(email));

            SceneNavigator.setRoot(LayoutRoutes.MAIN_LAYOUT);

        } catch (Exception e) {
            log.warn("Login failed user={}", maskEmail(email), e);
            showError();
        }
    }
}
