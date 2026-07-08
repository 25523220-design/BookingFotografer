package com.landstudio;

import java.util.List;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainController {

    @FXML private ScrollPane rootScroll;
    @FXML private VBox pageBox;
    @FXML private Label userNameLabel;
    @FXML private StackPane card1;
    @FXML private StackPane card2;
    @FXML private StackPane card3;
    @FXML private VBox myOrdersSection;
    @FXML private VBox myOrdersContainer;

    @FXML
    private void initialize() {
        HeaderUtil.bindUserLabel(userNameLabel);
        pageBox.minHeightProperty().bind(rootScroll.heightProperty());
        rootScroll.addEventFilter(ScrollEvent.SCROLL, this::handlePageScroll);
        // Pasang foto kategori (dimuat lewat Java agar dijamin terbaca).
        ImageUtil.applyCategoryBackground(card1, "Pernikahan");
        ImageUtil.applyCategoryBackground(card2, "Wisuda");
        ImageUtil.applyCategoryBackground(card3, "Pre-Wedding");
        buildMyOrdersSection();
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
        NumberBinding topRowHeight = Bindings.createDoubleBinding(
                () -> clamp(scene.getHeight() * 0.34, 220, 480),
                scene.heightProperty());
        card1.prefHeightProperty().bind(topRowHeight);
        card2.prefHeightProperty().bind(topRowHeight);

        NumberBinding wideCardHeight = Bindings.createDoubleBinding(
                () -> clamp(scene.getHeight() * 0.27, 180, 380),
                scene.heightProperty());
        card3.prefHeightProperty().bind(wideCardHeight);
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

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    // ===================== PESANAN SAYA =====================

    private void buildMyOrdersSection() {
        myOrdersContainer.getChildren().clear();
        List<Booking> bookings = Session.isLoggedIn() ? Session.getMyBookings() : List.of();
        boolean show = !bookings.isEmpty();
        myOrdersSection.setVisible(show);
        myOrdersSection.setManaged(show);
        if (!show) {
            return;
        }
        for (Booking booking : bookings) {
            myOrdersContainer.getChildren().add(buildOrderCard(booking));
        }
    }

    private VBox buildOrderCard(Booking booking) {
        VBox card = new VBox(14);
        card.getStyleClass().add("order-card");

        HBox topRow = new HBox(16);
        topRow.setAlignment(Pos.TOP_LEFT);

        VBox infoBox = new VBox(6);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        Label idLabel = new Label(booking.getId() + "  —  " + booking.getPaket());
        idLabel.getStyleClass().add("order-title");
        Label scheduleLabel = new Label(booking.getTanggalFormatted() + "  ·  pukul " + booking.getJam());
        scheduleLabel.getStyleClass().add("order-detail");
        Label totalLabel = new Label("Total: " + booking.getEstimasiTotalFormatted());
        totalLabel.getStyleClass().add("order-detail");
        infoBox.getChildren().addAll(idLabel, scheduleLabel, totalLabel);

        VBox statusBox = new VBox(8);
        statusBox.setAlignment(Pos.TOP_RIGHT);
        Label bookingStatusPill = new Label(booking.getStatus().label());
        bookingStatusPill.getStyleClass().add("order-status-pill");
        bookingStatusPill.getStyleClass().add(statusStyleClass(booking.getStatus()));
        Label paymentStatusLabel = new Label("Pembayaran: " + booking.getPaymentStatus().label());
        paymentStatusLabel.getStyleClass().add("order-detail");
        statusBox.getChildren().addAll(bookingStatusPill, paymentStatusLabel);

        topRow.getChildren().addAll(infoBox, statusBox);

        Region divider = new Region();
        divider.getStyleClass().add("form-divider");
        divider.setMinHeight(1);
        divider.setMaxHeight(1);

        HBox actionsRow = new HBox(12);
        actionsRow.setAlignment(Pos.CENTER_RIGHT);

        boolean cancelled = booking.getStatus() == Booking.BookingStatus.DIBATALKAN;

        Button rescheduleButton = new Button("GANTI JADWAL");
        rescheduleButton.getStyleClass().add("btn-outline-dark");
        rescheduleButton.setDisable(cancelled);
        rescheduleButton.setOnAction(e -> {
            Session.setRescheduleTarget(booking);
            SceneManager.switchTo("RescheduleView.fxml");
        });

        Button cancelButton = new Button("BATAL PESANAN");
        cancelButton.getStyleClass().add("btn-danger-outline");
        cancelButton.setDisable(cancelled);
        cancelButton.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Yakin ingin membatalkan pesanan " + booking.getId() + "?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText(null);
            confirm.setTitle("Batalkan Pesanan");
            confirm.showAndWait().ifPresent(choice -> {
                if (choice == ButtonType.YES) {
                    booking.setStatus(Booking.BookingStatus.DIBATALKAN);
                    Session.saveBookings();
                    Database.logActivity(booking.getOwnerEmail(), "PESANAN_BATAL",
                            "Pelanggan membatalkan pesanan " + booking.getId());
                    buildMyOrdersSection();
                }
            });
        });

        if (cancelled) {
            Label cancelledLabel = new Label("Pesanan ini telah dibatalkan.");
            cancelledLabel.getStyleClass().add("order-detail");
            actionsRow.getChildren().add(cancelledLabel);
        } else {
            actionsRow.getChildren().addAll(rescheduleButton, cancelButton);
        }

        card.getChildren().addAll(topRow, divider, actionsRow);
        VBox.setMargin(card, new Insets(0));
        return card;
    }

    private String statusStyleClass(Booking.BookingStatus status) {
        return switch (status) {
            case DIKONFIRMASI -> "order-status-confirmed";
            case DIBATALKAN -> "order-status-cancelled";
            default -> "order-status-pending";
        };
    }
}
