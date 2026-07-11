package com.landstudio;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Halaman detail satu kategori paket: menampilkan hero (foto + judul kategori)
 * lalu daftar kartu harga (Silver / Gold / Premium) lengkap dengan benefit.
 * Tiap kartu punya tombol "BOOKING PAKET INI" yang langsung membawa ke halaman
 * Booking DENGAN paket yang bersangkutan sudah terpilih otomatis.
 */
public class PaketDetailController {

    @FXML private ScrollPane rootScroll;
    @FXML private VBox pageBox;
    @FXML private Label userNameLabel;
    @FXML private StackPane heroPane;
    @FXML private Label categoryNumberLabel;
    @FXML private Label categoryTitleLabel;
    @FXML private Label categorySubtitleLabel;
    @FXML private HBox pricingContainer;

    /** Kategori dari tier yang sedang ditampilkan (untuk tombol kembali). */
    private String currentCategory = "Pernikahan";

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
        // Kartu harga memakai lebar fleksibel (HBox.hgrow), tidak perlu resize khusus.
    }

    /**
     * Dipanggil SceneManager dengan LABEL tier (mis. "Pernikahan Gold") untuk
     * menampilkan detail lengkap SATU paket yang dipilih pelanggan di halaman
     * daftar tier.
     */
    public void setTier(String tierLabel) {
        PackageCatalog.Tier tier = PackageCatalog.byLabel(tierLabel);
        pricingContainer.getChildren().clear();

        if (tier == null) {
            // Data tier tidak ditemukan (mis. sudah dihapus admin) -> kembali aman.
            categoryTitleLabel.setText(tierLabel == null ? "Paket" : tierLabel);
            categorySubtitleLabel.setText("Paket tidak ditemukan. Silakan kembali dan pilih paket lain.");
            return;
        }

        currentCategory = tier.category();
        // Foto kategori (dimuat lewat Java agar dijamin terbaca).
        ImageUtil.applyCategoryBackground(heroPane, tier.category());
        categoryTitleLabel.setText(tier.label());

        switch (tier.category()) {
            case "Wisuda" -> {
                categoryNumberLabel.setText("02");
                categorySubtitleLabel.setText(
                        "Mendokumentasikan puncak pencapaian akademik dengan gaya yang elegan dan berkesan.");
            }
            case "Pre-Wedding" -> {
                categoryNumberLabel.setText("03");
                categorySubtitleLabel.setText(
                        "Narasi intim yang ditangkap dalam momen tenang sebelum perayaan agung.");
            }
            default -> { // Pernikahan
                categoryNumberLabel.setText("01");
                categorySubtitleLabel.setText(
                        "Menangkap simfoni emosi dalam penyatuan paling sakral Anda dengan kualitas sinematik yang abadi.");
            }
        }

        pricingContainer.getChildren().add(buildPriceCard(tier));
    }

    private VBox buildPriceCard(PackageCatalog.Tier tier) {
        VBox card = new VBox(16);
        card.getStyleClass().add("price-card");
        card.setMaxWidth(460);
        if (tier.highlighted()) {
            card.getStyleClass().add("price-card-highlight");
        }
        HBox.setHgrow(card, Priority.ALWAYS);

        if (tier.highlighted()) {
            Label badge = new Label("PALING POPULER");
            badge.getStyleClass().add("price-badge");
            card.getChildren().add(badge);
        }

        Label tierName = new Label(tier.name());
        tierName.getStyleClass().add("price-tier");

        HBox priceRow = new HBox(6);
        priceRow.setAlignment(Pos.BASELINE_LEFT);
        Label priceValue = new Label(tier.priceFormatted());
        priceValue.getStyleClass().add("price-value");
        Label pricePeriod = new Label(tier.period());
        pricePeriod.getStyleClass().add("price-period");
        priceRow.getChildren().addAll(priceValue, pricePeriod);

        Region divider = new Region();
        divider.getStyleClass().add("price-divider");
        divider.setMinHeight(1);
        divider.setMaxHeight(1);

        VBox featureList = new VBox(10);
        for (String feature : tier.features()) {
            Label featureLabel = new Label("\u2022  " + feature);
            featureLabel.getStyleClass().add("price-feature");
            featureLabel.setWrapText(true);
            featureList.getChildren().add(featureLabel);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button bookingButton = new Button("BOOKING PAKET INI");
        bookingButton.getStyleClass().add(tier.highlighted() ? "btn-gold" : "btn-outline-dark");
        bookingButton.setMaxWidth(Double.MAX_VALUE);
        bookingButton.setOnAction(e -> {
            // Ingat paket yang dipilih supaya di halaman Booking langsung terpilih.
            Session.setSelectedPackageLabel(tier.label());
            if (!Session.requireLogin("BookingView.fxml")) return;
            SceneManager.switchTo("BookingView.fxml");
        });
        VBox.setMargin(bookingButton, new Insets(6, 0, 0, 0));

        card.getChildren().addAll(tierName, priceRow, divider, featureList, spacer, bookingButton);
        return card;
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
    private void goToPaket() {
        SceneManager.switchTo("PaketView.fxml");
    }

    /** Tombol "Kembali" pada halaman detail: kembali ke daftar tier kategori ini. */
    @FXML
    private void goBackToTier() {
        SceneManager.switchTo("PaketTierView.fxml", currentCategory);
    }
}
