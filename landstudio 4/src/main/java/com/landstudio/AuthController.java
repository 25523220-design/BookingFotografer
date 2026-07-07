package com.landstudio;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class AuthController {

    @FXML private VBox loginPane;
    @FXML private VBox registerPane;
    @FXML private Label tabLoginLabel;
    @FXML private Label tabRegisterLabel;

    @FXML private TextField loginEmailField;
    @FXML private PasswordField loginPasswordField;
    @FXML private Label loginStatusLabel;

    @FXML private TextField registerNameField;
    @FXML private TextField registerEmailField;
    @FXML private PasswordField registerPasswordField;
    @FXML private PasswordField registerPasswordConfirmField;
    @FXML private Label registerStatusLabel;

    @FXML
    private void initialize() {
        showLoginTab();
    }

    public void bindResponsiveSizing(Scene scene) {
        // Kartu auth berukuran tetap, tidak perlu resize khusus.
    }

    @FXML
    private void goToBeranda() {
        SceneManager.switchTo("MainView.fxml");
    }

    @FXML
    private void showLoginTab() {
        loginPane.setVisible(true);
        loginPane.setManaged(true);
        registerPane.setVisible(false);
        registerPane.setManaged(false);
        if (!tabLoginLabel.getStyleClass().contains("auth-tab-active")) {
            tabLoginLabel.getStyleClass().add("auth-tab-active");
        }
        tabRegisterLabel.getStyleClass().remove("auth-tab-active");
    }

    @FXML
    private void showRegisterTab() {
        registerPane.setVisible(true);
        registerPane.setManaged(true);
        loginPane.setVisible(false);
        loginPane.setManaged(false);
        if (!tabRegisterLabel.getStyleClass().contains("auth-tab-active")) {
            tabRegisterLabel.getStyleClass().add("auth-tab-active");
        }
        tabLoginLabel.getStyleClass().remove("auth-tab-active");
    }

    @FXML
    private void handleLogin() {
        String email = safeText(loginEmailField.getText());
        String password = safeText(loginPasswordField.getText());

        if (email.isEmpty() || password.isEmpty()) {
            showError(loginStatusLabel, "Mohon isi email dan kata sandi.");
            return;
        }
        if (!Session.isEmailRegistered(email)) {
            showError(loginStatusLabel, "Email belum terdaftar. Silakan daftar terlebih dahulu.");
            return;
        }
        if (!Session.login(email, password)) {
            showError(loginStatusLabel, "Email atau kata sandi salah.");
            return;
        }
        loginStatusLabel.setText("");
        if (Session.isAdminLoggedIn()) {
            SceneManager.switchTo("AdminDashboardView.fxml");
        } else {
            SceneManager.switchTo(Session.consumeRedirectAfterLogin());
        }
    }

    @FXML
    private void handleRegister() {
        String name = safeText(registerNameField.getText());
        String email = safeText(registerEmailField.getText());
        String password = safeText(registerPasswordField.getText());
        String confirm = safeText(registerPasswordConfirmField.getText());

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError(registerStatusLabel, "Mohon lengkapi nama, email, dan kata sandi.");
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            showError(registerStatusLabel, "Format email tidak valid.");
            return;
        }
        if (password.length() < 6) {
            showError(registerStatusLabel, "Kata sandi minimal 6 karakter.");
            return;
        }
        if (!password.equals(confirm)) {
            showError(registerStatusLabel, "Konfirmasi kata sandi tidak cocok.");
            return;
        }
        if (Session.isEmailRegistered(email)) {
            showError(registerStatusLabel, "Email sudah terdaftar. Silakan masuk.");
            return;
        }
        Session.register(name, email, password);
        registerStatusLabel.setText("");
        SceneManager.switchTo(Session.consumeRedirectAfterLogin());
    }

    private void showError(Label label, String message) {
        label.getStyleClass().removeAll("form-status-success");
        if (!label.getStyleClass().contains("form-status-error")) {
            label.getStyleClass().add("form-status-error");
        }
        label.setText(message);
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }
}
