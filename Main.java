package com.landstudio;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Halaman DAFTAR TIER untuk satu kategori paket. Menampilkan foto kategori
 * (hero) lalu daftar tier (mis. Silver / Gold / Premium) dengan nama + harga
 * SAJA — tanpa detail benefit. Menekan "LIHAT DETAIL" pada sebuah tier akan
 * membuka halaman Detail Paket khusus tier tersebut.
 */
public class PaketTierController {

    @FXML private ScrollPane rootScroll;
    @FXML private VBox pageBox;
    @FXML private Label userNameLabel;
    @FXML private StackPane heroPane;
    @FXML private Label categoryNumberLabel;
    @FXML private Label categoryTitleLabel;
    @FXML private Label categorySubtitleLabel;
    @FXML private VBox tierListContainer;

    @FXML
    private void initialize() {
        HeaderUtil.bindUserLabel(userNameLabel);
        pageBox.minHeightProperty().bind(rootScroll.heightProperty());
        rootScroll.addEventFilter(ScrollEvent.SCROLL, this::handlePageScroll);
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
        // Lebar daftar tier dibatasi maxWidth di FXML, tidak perlu binding khusus.
    }

    /**
     * Dipanggil SceneManager untuk menentukan kategori yang ditampilkan
     * (Pernikahan / Wisuda / Pre-Wedding).
     */
    public void setCategory(String category) {
        categoryTitleLabel.setText(category);
        // Foto kategori (dimuat lewat Java agar dijamin terbaca).
        ImageUtil.applyCategoryBackground(heroPane, category);

        switch (category) {
            case "Wisuda" -> categoryNumberLabel.setText("02");
            case "Pre-Wedding" -> categoryNumberLabel.setText("03");
            default -> categoryNumberLabel.setText("01");
        }

        tierListContainer.getChildren().clear();
        for (PackageCatalog.Tier tier : PackageCatalog.tiersFor(category)) {
            tierListContainer.getChildren().add(buildTierRow(tier));
        }
    }

    /** Satu baris tier: nama + harga + jumlah benefit, plus tombol LIHAT DETAIL. */
    private HBox buildTierRow(PackageCatalog.Tier tier) {
        HBox row = new HBox(16);
        row.getStyleClass().add("tier-row");
        row.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        HBox titleLine = new HBox(10);
        titleLine.setAlignment(Pos.CENTER_LEFT);
        Label name = new Label(tier.name());
        name.getStyleClass().add("tier-title");
        titleLine.getChildren().add(name);
        if (tier.highlighted()) {
            Label badge = new Label("PALING POPULER");
            badge.getStyleClass().add("price-badge");
            titleLine.getChildren().add(badge);
        }

        Label price = new Label(tier.priceFormatted() + "  " + tier.period());
        price.getStyleClass().add("tier-price");

        Label benefitCount = new Label(tier.features().size() + " benefit termasuk");
        benefitCount.getStyleClass().add("order-detail");

        info.getChildren().addAll(titleLine, price, benefitCount);

        Button detailBtn = new Button("LIHAT DETAIL");
        detailBtn.getStyleClass().add(tier.highlighted() ? "btn-gold" : "btn-outline-dark");
        detailBtn.setOnAction(e ->
                SceneManager.switchTo("PaketDetailView.fxml", tier.label()));

        row.getChildren().addAll(info, detailBtn);
        return row;
    }

    // ===================== NAVIGASI =====================

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
    private void goToPaket() {
        SceneManager.switchTo("PaketView.fxml");
    }
}
