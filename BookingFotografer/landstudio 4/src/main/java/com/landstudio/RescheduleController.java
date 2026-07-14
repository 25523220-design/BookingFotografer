package com.landstudio;

import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

public class RescheduleController {

    @FXML private Label infoLabel;
    @FXML private DatePicker tanggalPicker;
    @FXML private ChoiceBox<String> jamChoice;
    @FXML private Label statusLabel;

    private Booking booking;

    @FXML
    private void initialize() {
        booking = Session.getRescheduleTarget();
        if (booking == null) {
            SceneManager.switchTo("MainView.fxml");
            return;
        }

        infoLabel.setText(booking.getPaket() + " — Jadwal saat ini: "
                + booking.getTanggalFormatted() + ", pukul " + booking.getJam());

        jamChoice.getItems().addAll(
                "08:00 - 10:00",
                "10:00 - 12:00",
                "13:00 - 15:00",
                "15:00 - 17:00",
                "19:00 - 21:00");
        jamChoice.setValue(booking.getJam());

        tanggalPicker.setValue(booking.getTanggal());

        // Nonaktifkan tanggal lampau & tanggal yang sudah dibooking pesanan LAIN.
        // Booking ini sendiri dikecualikan supaya tanggal aslinya tetap bisa dipilih.
        tanggalPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                boolean past = date != null && date.isBefore(LocalDate.now());
                boolean taken = Session.isDateTaken(date, booking);
                setDisable(empty || past || taken);
                getStyleClass().remove("date-booked"); // reset karena sel dipakai ulang
                if (!empty && taken && !past) {
                    getStyleClass().add("date-booked");
                }
            }
        });

        statusLabel.setText("");
    }

    public void bindResponsiveSizing(Scene scene) {
        // Kartu berukuran tetap, tidak perlu resize khusus.
    }

    @FXML
    private void handleSave() {
        LocalDate tanggalBaru = tanggalPicker.getValue();
        String jamBaru = jamChoice.getValue();

        if (tanggalBaru == null || jamBaru == null) {
            showError("Mohon pilih tanggal dan jam sesi yang baru.");
            return;
        }

        // Tolak kalau tanggal barunya bentrok dengan booking lain.
        if (Session.isDateTaken(tanggalBaru, booking)) {
            showError("Tanggal " + tanggalBaru + " sudah dibooking. Silakan pilih tanggal lain.");
            return;
        }

        booking.setTanggal(tanggalBaru);
        booking.setJam(jamBaru);
        // Jadwal ulang tidak memerlukan konfirmasi ulang: booking tetap terjadwal.
        booking.setStatus(Booking.BookingStatus.DIKONFIRMASI);
        Session.saveBookings();
        Database.logActivity(booking.getOwnerEmail(), "GANTI_JADWAL",
                "Pesanan " + booking.getId() + " dijadwalkan ulang ke "
                        + booking.getTanggalFormatted() + ", pukul " + jamBaru);
        Session.setRescheduleTarget(null);
        SceneManager.switchTo("MainView.fxml");
    }

    private void showError(String message) {
        statusLabel.getStyleClass().removeAll("form-status-success");
        if (!statusLabel.getStyleClass().contains("form-status-error")) {
            statusLabel.getStyleClass().add("form-status-error");
        }
        statusLabel.setText(message);
    }

    @FXML
    private void handleCancel() {
        Session.setRescheduleTarget(null);
        SceneManager.switchTo("MainView.fxml");
    }
}
