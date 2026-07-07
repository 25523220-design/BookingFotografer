package com.landstudio;

import java.util.List;

import javafx.fxml.FXML;
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
import javafx.scene.layout.VBox;

/**
 * Halaman utama dashboard admin: menampilkan SEMUA pesanan dari seluruh
 * pelanggan, lengkap dengan aksi mengonfirmasi, membatalkan, dan menandai
 * pembayaran lunas.
 */
public class AdminDashboardController {

    @FXML private ScrollPane rootScroll;
    @FXML private VBox pageBox;
    @FXML private Label adminNameLabel;
    @FXML private Label navPesananLabel;
    @FXML private Label navPaketLabel;

    // Kartu ringkasan di bagian atas.
    @FXML private Label statTotalLabel;
    @FXML private Label statPendingLabel;
    @FXML private Label statConfirmedLabel;
    @FXML private Label statRevenueLabel;

    @FXML private VBox ordersContainer;
    @FXML private Label emptyLabel;

    @FXML
    private void initialize() {
        // Halaman ini hanya untuk admin. Kalau bukan admin, tendang ke login.
        if (!Session.isAdminLoggedIn()) {
            SceneManager.switchTo("AuthView.fxml");
            return;
        }
        adminNameLabel.setText(Session.getCurrentUser().getFullName());
        navPesananLabel.getStyleClass().add("admin-nav-active");

        pageBox.minHeightProperty().bind(rootScroll.heightProperty());
        rootScroll.addEventFilter(ScrollEvent.SCROLL, this::handlePageScroll);

        refresh();
    }

    public void bindResponsiveSizing(Scene scene) {
        // Layout admin memakai lebar penuh, tidak perlu binding ukuran khusus.
    }

    private void handlePageScroll(ScrollEvent event) {
        double overflow = pageBox.getBoundsInLocal().getHeight()
                - rootScroll.getViewportBounds().getHeight();
        if (overflow <= 0) {
            return;
        }
        double v = rootScroll.getVvalue() - event.getDeltaY() / overflow;
        rootScroll.setVvalue(Math.max(0, Math.min(1, v)));
        event.consume();
    }

    // ===================== NAVIGASI =====================

    @FXML
    private void goToPesanan() {
        refresh();
    }

    @FXML
    private void goToKelolaPaket() {
        SceneManager.switchTo("AdminPaketView.fxml");
    }

    @FXML
    private void handleLogout() {
        Session.logout();
        SceneManager.switchTo("MainView.fxml");
    }

    // ===================== ISI HALAMAN =====================

    private void refresh() {
        List<Booking> all = Session.getAllBookings();
        updateStats(all);

        ordersContainer.getChildren().clear();
        boolean empty = all.isEmpty();
        emptyLabel.setVisible(empty);
        emptyLabel.setManaged(empty);
        for (Booking b : all) {
            ordersContainer.getChildren().add(buildOrderCard(b));
        }
    }

    private void updateStats(List<Booking> all) {
        int total = all.size();
        int pending = 0;
        int confirmed = 0;
        long revenue = 0;
        for (Booking b : all) {
            switch (b.getStatus()) {
                case MENUNGGU_KONFIRMASI -> pending++;
                case DIKONFIRMASI -> {
                    confirmed++;
                    revenue += b.getEstimasiTotal();
                }
                default -> {
                }
            }
        }
        statTotalLabel.setText(String.valueOf(total));
        statPendingLabel.setText(String.valueOf(pending));
        statConfirmedLabel.setText(String.valueOf(confirmed));
        statRevenueLabel.setText(PackageCatalog.formatRupiah(revenue));
    }

    private VBox buildOrderCard(Booking booking) {
        VBox card = new VBox(14);
        card.getStyleClass().add("order-card");

        // --- Baris atas: info pelanggan + status ---
        HBox topRow = new HBox(16);
        topRow.setAlignment(Pos.TOP_LEFT);

        VBox infoBox = new VBox(6);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        Label title = new Label(booking.getId() + "  —  " + booking.getPaket());
        title.getStyleClass().add("order-title");
        Label customer = new Label("Pelanggan: " + safe(booking.getNamaPelanggan())
                + "  ·  " + safe(booking.getEmail()));
        customer.getStyleClass().add("order-detail");
        Label contact = new Label("Telepon: " + safe(booking.getTelepon()));
        contact.getStyleClass().add("order-detail");
        Label schedule = new Label(booking.getTanggalFormatted() + "  ·  pukul " + safe(booking.getJam()));
        schedule.getStyleClass().add("order-detail");
        Label total = new Label("Total: " + booking.getEstimasiTotalFormatted());
        total.getStyleClass().add("order-detail");
        infoBox.getChildren().addAll(title, customer, contact, schedule, total);

        VBox statusBox = new VBox(8);
        statusBox.setAlignment(Pos.TOP_RIGHT);
        Label statusPill = new Label(booking.getStatus().label());
        statusPill.getStyleClass().addAll("order-status-pill", statusStyleClass(booking.getStatus()));
        Label paymentLabel = new Label("Pembayaran: " + booking.getPaymentStatus().label());
        paymentLabel.getStyleClass().add("order-detail");
        statusBox.getChildren().addAll(statusPill, paymentLabel);

        topRow.getChildren().addAll(infoBox, statusBox);

        Region divider = new Region();
        divider.getStyleClass().add("form-divider");
        divider.setMinHeight(1);
        divider.setMaxHeight(1);

        // --- Baris aksi admin ---
        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER_RIGHT);

        boolean cancelled = booking.getStatus() == Booking.BookingStatus.DIBATALKAN;
        boolean confirmed = booking.getStatus() == Booking.BookingStatus.DIKONFIRMASI;
        boolean paid = booking.getPaymentStatus() == Booking.PaymentStatus.LUNAS;

        Button confirmBtn = new Button("KONFIRMASI");
        confirmBtn.getStyleClass().add("btn-gold");
        confirmBtn.setDisable(cancelled || confirmed);
        confirmBtn.setOnAction(e -> {
            booking.setStatus(Booking.BookingStatus.DIKONFIRMASI);
            Session.saveBookings();
            Database.logActivity(adminEmail(), "PESANAN_KONFIRMASI",
                    "Pesanan " + booking.getId() + " dikonfirmasi");
            refresh();
        });

        Button paidBtn = new Button("TANDAI LUNAS");
        paidBtn.getStyleClass().add("btn-outline-dark");
        paidBtn.setDisable(cancelled || paid);
        paidBtn.setOnAction(e -> {
            booking.setPaymentStatus(Booking.PaymentStatus.LUNAS);
            Session.saveBookings();
            Database.logActivity(adminEmail(), "PESANAN_LUNAS",
                    "Pesanan " + booking.getId() + " ditandai lunas");
            refresh();
        });

        Button cancelBtn = new Button("BATALKAN");
        cancelBtn.getStyleClass().add("btn-danger-outline");
        cancelBtn.setDisable(cancelled);
        cancelBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Batalkan pesanan " + booking.getId() + " dari "
                            + safe(booking.getNamaPelanggan()) + "?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText(null);
            confirm.setTitle("Batalkan Pesanan");
            confirm.showAndWait().ifPresent(choice -> {
                if (choice == ButtonType.YES) {
                    booking.setStatus(Booking.BookingStatus.DIBATALKAN);
                    Session.saveBookings();
                    Database.logActivity(adminEmail(), "PESANAN_BATAL",
                            "Pesanan " + booking.getId() + " dibatalkan admin");
                    refresh();
                }
            });
        });

        if (cancelled) {
            Label note = new Label("Pesanan ini telah dibatalkan.");
            note.getStyleClass().add("order-detail");
            actions.getChildren().add(note);
        } else {
            actions.getChildren().addAll(confirmBtn, paidBtn, cancelBtn);
        }

        card.getChildren().addAll(topRow, divider, actions);
        return card;
    }

    private String statusStyleClass(Booking.BookingStatus status) {
        return switch (status) {
            case DIKONFIRMASI -> "order-status-confirmed";
            case DIBATALKAN -> "order-status-cancelled";
            default -> "order-status-pending";
        };
    }

    private String safe(String v) {
        return (v == null || v.isBlank()) ? "-" : v;
    }

    private String adminEmail() {
        User u = Session.getCurrentUser();
        return u == null ? Session.ADMIN_EMAIL : u.getEmail();
    }
}
