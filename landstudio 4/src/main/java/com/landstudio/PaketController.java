package com.landstudio;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Halaman "Paket": menampilkan 3 kategori paket (Pernikahan / Wisuda /
 * Pre-Wedding). Tiap kategori punya tombol "LIHAT PAKET" yang membuka halaman
 * detail paket kategori tersebut.
 */
public class PaketController {

    @FXML private ScrollPane rootScroll;
    @FXML private VBox pageBox;
    @FXML private Label userNameLabel;
    @FXML private StackPane catPernikahan;
    @FXML private StackPane catWisuda;
    @FXML private StackPane catPreWedding;

    @FXML
    private void initialize() {
        HeaderUtil.bindUserLabel(userNameLabel);
        pageBox.minHeightProperty().bind(rootScroll.heightProperty());
        rootScroll.addEventFilter(ScrollEvent.SCROLL, this::handlePageScroll);
        // Pasang foto kategori (dimuat lewat Java agar dijamin terbaca).
        ImageUtil.applyCategoryBackground(catPernikahan, "Pernikahan");
        ImageUtil.applyCategoryBackground(catWisuda, "Wisuda");
        ImageUtil.applyCategoryBackground(catPreWedding, "Pre-Wedding");
    }

    private void handlePageScroll(ScrollEvent event) {
        double contentHeight = pageBox.getBoundsInLocal().getHeight();
        double viewportHeight = rootScroll.getViewportBounds().getHeight();
        double overflow = contentHeight - viewportHeight;
        if (overflow <= 0) {
            return;
        }
        double newVvalue = rootScroll.getVvalue() - event.getDeltaY() / overflow;
        rootScroll.setVvalue(Math.max(0, Math.min(1, newVvalue)));
        event.consume();
    }

    public void bindResponsiveSizing(Scene scene) {
        // Kartu kategori memakai tinggi tetap, tidak perlu resize khusus.
    }

    @FXML
    private void goToBeranda() {
        SceneManager.switchTo("MainView.fxml");
    }

    @FXML
    private void goToPortfolio() {
        SceneManager.switchTo("PortfolioView.fxml");
    }

    @FXML
    private void goToBooking() {
        if (!Session.requireLogin("BookingView.fxml")) return;
        SceneManager.switchTo("BookingView.fxml");
    }

    @FXML
    private void openPernikahan() {
        SceneManager.switchTo("PaketTierView.fxml", "Pernikahan");
    }

    @FXML
    private void openWisuda() {
        SceneManager.switchTo("PaketTierView.fxml", "Wisuda");
    }

    @FXML
    private void openPreWedding() {
        SceneManager.switchTo("PaketTierView.fxml", "Pre-Wedding");
    }
}
