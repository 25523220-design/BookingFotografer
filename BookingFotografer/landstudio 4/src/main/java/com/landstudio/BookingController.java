package com.landstudio;

import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;

public class BookingController {

    @FXML private ScrollPane rootScroll;
    @FXML private javafx.scene.layout.Region pageBox;
    @FXML private Label userNameLabel;

    // Data diri
    @FXML private TextField namaField;
    @FXML private TextField emailField;
    @FXML private TextField teleponField;
    @FXML private TextArea alamatField;

    // Jadwal sesi
    @FXML private ChoiceBox<String> paketChoice;
    @FXML private Label totalPriceLabel;
    @FXML private DatePicker tanggalPicker;
    @FXML private ChoiceBox<String> jamChoice;
    @FXML private TextArea catatanField;

    @FXML private Label statusLabel;
    @FXML private Button submitButton;

    @FXML
    private void initialize() {
        // Halaman booking mensyaratkan login. Kalau belum login, alihkan ke
        // halaman Masuk/Daftar dan ingat supaya kembali ke sini setelah berhasil.
        if (!Session.requireLogin("BookingView.fxml")) {
            return;
        }

        HeaderUtil.bindUserLabel(userNameLabel);
        pageBox.minHeightProperty().bind(rootScroll.heightProperty());
        rootScroll.addEventFilter(ScrollEvent.SCROLL, this::handlePageScroll);

        // ---- Pilihan paket DETAIL (kategori + tier), mis. "Pernikahan Gold" ----
        for (PackageCatalog.Tier tier : PackageCatalog.allTiers()) {
            paketChoice.getItems().add(tier.label());
        }

        // Preselect paket kalau pelanggan datang dari tombol "BOOKING PAKET INI".
        String preselect = Session.consumeSelectedPackageLabel();
        if (preselect != null && paketChoice.getItems().contains(preselect)) {
            paketChoice.setValue(preselect);
        } else {
            paketChoice.setValue(paketChoice.getItems().get(0));
        }

        // Harga tampil otomatis mengikuti paket yang dipilih.
        updateTotalLabel();
        paketChoice.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldV, newV) -> updateTotalLabel());

        // ---- Jam sesi ----
        jamChoice.getItems().addAll(
                "08:00 - 10:00",
                "10:00 - 12:00",
                "13:00 - 15:00",
                "15:00 - 17:00",
                "19:00 - 21:00");
        jamChoice.setValue("08:00 - 10:00");

        // ---- Tanggal: nonaktifkan tanggal lampau DAN tanggal yang sudah dibooking ----
        tanggalPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                boolean past = date != null && date.isBefore(LocalDate.now());
                boolean taken = Session.isDateTaken(date);
                setDisable(empty || past || taken);
                // Sel kalender dipakai ulang saat scroll bulan, jadi selalu reset dulu.
                getStyleClass().remove("date-booked");
                setTooltip(null);
                if (!empty && taken && !past) {
                    getStyleClass().add("date-booked"); // tandai tanggal penuh
                    setTooltip(new Tooltip("Tanggal ini sudah dibooking"));
                }
            }
        });

        // Pra-isi data diri dari akun yang sedang login supaya lebih cepat.
        User current = Session.getCurrentUser();
        if (current != null) {
            namaField.setText(current.getFullName());
            emailField.setText(current.getEmail());
        }

        statusLabel.setText("");
    }

    private void updateTotalLabel() {
        PackageCatalog.Tier tier = PackageCatalog.byLabel(paketChoice.getValue());
        if (tier != null) {
            totalPriceLabel.setText(tier.priceFormatted() + "  " + tier.period());
        } else {
            totalPriceLabel.setText("-");
        }
    }

    public void bindResponsiveSizing(Scene scene) {
        // Form booking lebar tetap, tidak perlu resize khusus.
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
    private void goToPaket() {
        SceneManager.switchTo("PaketView.fxml");
    }

    @FXML
    private void handleSubmit() {
        String nama = safeText(namaField.getText());
        String email = safeText(emailField.getText());
        String telepon = safeText(teleponField.getText());
        String alamat = safeText(alamatField.getText());
        LocalDate tanggal = tanggalPicker.getValue();
        String jam = jamChoice.getValue();
        String paket = paketChoice.getValue();
        String catatan = safeText(catatanField.getText());

        if (nama.isEmpty() || telepon.isEmpty() || alamat.isEmpty() || tanggal == null) {
            showError("Mohon lengkapi Nama, No. Telepon, Alamat, dan Tanggal sesi.");
            return;
        }

        // Pengaman terakhir: tolak kalau tanggalnya ternyata sudah dibooking.
        if (Session.isDateTaken(tanggal)) {
            showError("Tanggal " + tanggal + " sudah dibooking. Silakan pilih tanggal lain.");
            return;
        }

        statusLabel.getStyleClass().removeAll("form-status-error");
        if (!statusLabel.getStyleClass().contains("form-status-success")) {
            statusLabel.getStyleClass().add("form-status-success");
        }

        // Susun booking, lalu lanjutkan ke halaman pembayaran (QRIS/Transfer).
        Booking booking = new Booking(Session.nextBookingId(), Session.getCurrentUser().getEmail());
        booking.setNamaPelanggan(nama);
        booking.setEmail(email);
        booking.setTelepon(telepon);
        booking.setAlamat(alamat);
        booking.setPaket(paket);
        booking.setTanggal(tanggal);
        booking.setJam(jam);
        booking.setCatatan(catatan);

        Session.setPendingBooking(booking);
        SceneManager.switchTo("PaymentView.fxml");
    }

    private void showError(String message) {
        statusLabel.getStyleClass().removeAll("form-status-success");
        if (!statusLabel.getStyleClass().contains("form-status-error")) {
            statusLabel.getStyleClass().add("form-status-error");
        }
        statusLabel.setText(message);
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
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
}
