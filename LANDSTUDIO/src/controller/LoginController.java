package controller;

import dao.UserDAO;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.User;

public class LoginController {

    @FXML
    private TextField tfemail;

    @FXML
    private PasswordField tfsandi;

    @FXML
    private Hyperlink btndaftar;

    private UserDAO dao = new UserDAO();

    @FXML
    private void btnmasukklik(ActionEvent event) {

        String email = tfemail.getText().trim();
        String password = tfsandi.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Email dan Password harus diisi!");
            alert.showAndWait();
            return;

        }

        User user = dao.login(email, password);

        if (user != null) {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Selamat datang, " + user.getNama());
            alert.showAndWait();

            // Nanti di sini kita pindah ke Dashboard
            // menggunakan scene.setRoot(...)

        } else {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Email atau Password salah!");
            alert.showAndWait();

        }

    }

    @FXML
    private void btndaftarklik(ActionEvent event) {

        try {

            Parent root = FXMLLoader.load(getClass().getResource("/view/Register.fxml"));

            // Gunakan Scene yang sudah ada
            Scene scene = btndaftar.getScene();
            scene.setRoot(root);

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

}