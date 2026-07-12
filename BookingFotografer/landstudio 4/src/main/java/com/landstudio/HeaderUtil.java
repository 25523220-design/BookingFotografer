package com.landstudio;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

/**
 * Menyeragamkan perilaku label nama pengguna di pojok kanan atas header:
 *  - Belum login  -> teks "MASUK / DAFTAR", klik membuka halaman Auth.
 *  - Sudah login  -> menampilkan nama lengkap, klik menawarkan logout.
 */
public final class HeaderUtil {

    private HeaderUtil() {
    }

    public static void bindUserLabel(Label label) {
        refresh(label);
        label.setOnMouseClicked(e -> {
            if (Session.isLoggedIn()) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                        "Keluar dari akun " + Session.getCurrentUser().getFullName() + "?",
                        ButtonType.YES, ButtonType.NO);
                confirm.setHeaderText(null);
                confirm.setTitle("Keluar Akun");
                confirm.showAndWait().ifPresent(choice -> {
                    if (choice == ButtonType.YES) {
                        Session.logout();
                        SceneManager.switchTo("MainView.fxml");
                    }
                });
            } else {
                SceneManager.switchTo("AuthView.fxml");
            }
        });
    }

    public static void refresh(Label label) {
        if (Session.isLoggedIn()) {
            label.setText(Session.getCurrentUser().getFullName());
        } else {
            label.setText("MASUK / DAFTAR");
        }
    }
}
