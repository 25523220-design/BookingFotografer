package controller;

import dao.UserDAO;
import model.User;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML
    private TextField tfnama;

    @FXML
    private TextField tfemail;

    @FXML
    private PasswordField tfsandi;

    @FXML
    private PasswordField tfkonfirmasisandi;

    @FXML
    private Button btndaftar;

    @FXML
    private Hyperlink btnmasuk;

    private UserDAO dao = new UserDAO();

    @FXML
    private void btndaftarklik(ActionEvent event) {

        if (tfnama.getText().isEmpty()
                || tfemail.getText().isEmpty()
                || tfsandi.getText().isEmpty()
                || tfkonfirmasisandi.getText().isEmpty()) {

            alert("Semua data harus diisi.");
            return;
        }

        if (!tfsandi.getText().equals(tfkonfirmasisandi.getText())) {

            alert("Konfirmasi password tidak sama.");
            return;
        }

        if (dao.emailSudahAda(tfemail.getText())) {

            alert("Email sudah digunakan.");
            return;
        }

        User user = new User(
                dao.getNextId(),
                tfnama.getText(),
                tfemail.getText(),
                tfsandi.getText());

        dao.save(user);

        alert("Pendaftaran berhasil.");

        bukaLogin();

    }

    @FXML
    private void btnmasukklik(ActionEvent event) {

        bukaLogin();

    }

    private void bukaLogin() {

        try {

            Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));

            // Tidak membuat Scene baru
            Scene scene = btnmasuk.getScene();
            scene.setRoot(root);

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    private void alert(String pesan) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();

    }

}