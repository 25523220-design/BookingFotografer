package com.landstudio;

import java.io.IOException;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class PaymentController {

    @FXML private ScrollPane rootScroll;
    @FXML private VBox pageBox;
    @FXML private Label ringkasanPaket;
    @FXML private Label ringkasanTanggal;
    @FXML private Label ringkasanJam;
    @FXML private Label ringkasanNama;
    @FXML private Label ringkasanTotal;
    @FXML private VBox metodeSection;
    @FXML private Label tabQrisLabel;
    @FXML private Label tabTransferLabel;
    @FXML private VBox qrisPane;
    @FXML private GridPane qrGrid;
    @FXML private Label qrAmountLabel;
    @FXML private VBox transferPane;
    @FXML private Label transferAmountLabel;
    @FXML private HBox konfirmasiBox;
    @FXML private VBox successPane;
    @FXML private Label successIdLabel;

    private Booking booking;
    private String selectedMethod = "QRIS";

    @FXML
    private void initialize() {
        booking = Session.getPendingBooking();
        if (booking == null) {
            SceneManager.switchTo("MainView.fxml");
            return;
        }

        ringkasanPaket.setText(booking.getPaket());
        ringkasanTanggal.setText(booking.getTanggalFormatted());
        ringkasanJam.setText(booking.getJam());
        ringkasanNama.setText(booking.getNamaPelanggan());
        ringkasanTotal.setText(booking.getEstimasiTotalFormatted());
        qrAmountLabel.setText("Total: " + booking.getEstimasiTotalFormatted());
        transferAmountLabel.setText("Total: " + booking.getEstimasiTotalFormatted());

        buildMockQr();
        selectQris();

        successPane.setVisible(false);
        successPane.setManaged(false);

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
        // Kartu pembayaran berukuran tetap, tidak perlu resize khusus.
    }

    private void buildMockQr() {
        qrGrid.getChildren().clear();
        Random random = new Random(booking.getId().hashCode());
        int size = 14;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Region cell = new Region();
                boolean isCornerMarker =
                        (row < 3 && col < 3) || (row < 3 && col > size - 4) || (row > size - 4 && col < 3);
                boolean filled = isCornerMarker ? (row % 2 == 0 || col % 2 == 0) : random.nextBoolean();
                cell.setStyle("-fx-background-color: " + (filled ? "#1a1a1a" : "transparent") + ";");
                cell.setMinSize(11, 11);
                cell.setPrefSize(11, 11);
                qrGrid.add(cell, col, row);
            }
        }
    }

    @FXML
    private void selectQris() {
        selectedMethod = "QRIS";
        qrisPane.setVisible(true);
        qrisPane.setManaged(true);
        transferPane.setVisible(false);
        transferPane.setManaged(false);
        if (!tabQrisLabel.getStyleClass().contains("payment-tab-active")) {
            tabQrisLabel.getStyleClass().add("payment-tab-active");
        }
        tabTransferLabel.getStyleClass().remove("payment-tab-active");
    }

    @FXML
    private void selectTransfer() {
        selectedMethod = "Transfer Bank";
        transferPane.setVisible(true);
        transferPane.setManaged(true);
        qrisPane.setVisible(false);
        qrisPane.setManaged(false);
        if (!tabTransferLabel.getStyleClass().contains("payment-tab-active")) {
            tabTransferLabel.getStyleClass().add("payment-tab-active");
        }
        tabQrisLabel.getStyleClass().remove("payment-tab-active");
    }

    @FXML
    private void handleConfirmPayment() {
        booking.setMetodePembayaran(selectedMethod);
        booking.setPaymentStatus(Booking.PaymentStatus.LUNAS);
        booking.setStatus(Booking.BookingStatus.DIKONFIRMASI);
        Session.addBooking(booking);
        Session.clearPendingBooking();

        successIdLabel.setText("Kode Booking: " + booking.getId());
        metodeSection.setVisible(false);
        metodeSection.setManaged(false);
        konfirmasiBox.setVisible(false);
        konfirmasiBox.setManaged(false);
        successPane.setVisible(true);
        successPane.setManaged(true);
    }

    @FXML
    private void handleDownloadBukti() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Simpan Bukti Booking");
        chooser.setInitialFileName("Bukti-Booking-" + booking.getId() + ".txt");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Teks (*.txt)", "*.txt"));
        Stage stage = (Stage) pageBox.getScene().getWindow();
        java.io.File file = chooser.showSaveDialog(stage);
        if (file == null) {
            return;
        }
        try {
            Files.writeString(file.toPath(), buildReceiptText());
            Alert done = new Alert(Alert.AlertType.INFORMATION,
                    "Bukti booking berhasil disimpan di:\n" + file.getAbsolutePath());
            done.setHeaderText(null);
            done.setTitle("Berhasil Diunduh");
            done.showAndWait();
        } catch (IOException e) {
            Alert error = new Alert(Alert.AlertType.ERROR,
                    "Gagal menyimpan file: " + e.getMessage());
            error.setHeaderText(null);
            error.setTitle("Gagal Mengunduh");
            error.showAndWait();
        }
    }

    private String buildReceiptText() {
        DateTimeFormatter waktuFormat = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", new Locale("id", "ID"));
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("            LAND STUDIO\n");
        sb.append("      BUKTI BOOKING / RESERVASI\n");
        sb.append("========================================\n\n");
        sb.append("Kode Booking   : ").append(booking.getId()).append("\n");
        sb.append("Status Booking : ").append(booking.getStatus().label()).append("\n");
        sb.append("Status Bayar   : ").append(booking.getPaymentStatus().label()).append("\n");
        sb.append("Metode Bayar   : ").append(booking.getMetodePembayaran()).append("\n");
        sb.append("Dicetak pada   : ").append(booking.getCreatedAt().format(waktuFormat)).append("\n\n");
        sb.append("----------------------------------------\n");
        sb.append("DATA PELANGGAN\n");
        sb.append("----------------------------------------\n");
        sb.append("Nama        : ").append(booking.getNamaPelanggan()).append("\n");
        sb.append("Email       : ").append(booking.getEmail()).append("\n");
        sb.append("No. Telepon : ").append(booking.getTelepon()).append("\n");
        sb.append("Alamat      : ").append(booking.getAlamat()).append("\n\n");
        sb.append("----------------------------------------\n");
        sb.append("DETAIL SESI\n");
        sb.append("----------------------------------------\n");
        sb.append("Paket   : ").append(booking.getPaket()).append("\n");
        sb.append("Tanggal : ").append(booking.getTanggalFormatted()).append("\n");
        sb.append("Jam     : ").append(booking.getJam()).append("\n");
        sb.append("Catatan : ").append(
                booking.getCatatan() == null || booking.getCatatan().isEmpty() ? "-" : booking.getCatatan()
        ).append("\n\n");
        sb.append("----------------------------------------\n");
        sb.append("Total Tagihan : ").append(booking.getEstimasiTotalFormatted()).append("\n");
        sb.append("========================================\n");
        sb.append("Terima kasih telah booking di Land Studio.\n");
        sb.append("Simpan bukti ini sebagai referensi reservasi Anda.\n");
        return sb.toString();
    }

    @FXML
    private void goToBeranda() {
        SceneManager.switchTo("MainView.fxml");
    }
}
