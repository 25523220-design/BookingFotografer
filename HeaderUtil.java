package com.landstudio;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Halaman kedua dashboard admin: mengelola paket foto (tambah, ubah harga /
 * benefit, hapus). Semua perubahan langsung terpakai di halaman paket & booking
 * pelanggan karena datanya bersumber dari {@link PackageCatalog}.
 */
public class AdminPaketController {

    @FXML private ScrollPane rootScroll;
    @FXML private VBox pageBox;
    @FXML private Label adminNameLabel;
    @FXML private Label navPesananLabel;
    @FXML private Label navPaketLabel;

    // Daftar paket (dibangun via kode).
    @FXML private VBox tierListContainer;

    // Form tambah/edit paket.
    @FXML private Label formTitleLabel;
    @FXML private ComboBox<String> kategoriCombo;
    @FXML private TextField namaField;
    @FXML private TextField hargaField;
    @FXML private TextField periodeField;
    @FXML private CheckBox unggulanCheck;
    @FXML private TextArea fiturArea;
    @FXML private Label statusLabel;
    @FXML private Button simpanButton;

    /** Label paket yang sedang diedit; null berarti mode "tambah baru". */
    private String editingLabel = null;

    @FXML
    private void initialize() {
        if (!Session.isAdminLoggedIn()) {
            SceneManager.switchTo("AuthView.fxml");
            return;
        }
        adminNameLabel.setText(Session.getCurrentUser().getFullName());
        navPaketLabel.getStyleClass().add("admin-nav-active");

        pageBox.minHeightProperty().bind(rootScroll.heightProperty());
        rootScroll.addEventFilter(ScrollEvent.SCROLL, this::handlePageScroll);

        refreshKategoriOptions();
        buildTierList();
        resetForm();
    }

    public void bindResponsiveSizing(Scene scene) {
        // Layout memakai lebar penuh, tidak perlu binding khusus.
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
        SceneManager.switchTo("AdminDashboardView.fxml");
    }

    @FXML
    private void goToKelolaPaket() {
        buildTierList();
    }

    @FXML
    private void handleLogout() {
        Session.logout();
        SceneManager.switchTo("MainView.fxml");
    }

    // ===================== DAFTAR PAKET =====================

    private void refreshKategoriOptions() {
        String current = kategoriCombo.getEditor().getText();
        kategoriCombo.getItems().setAll(PackageCatalog.categories());
        kategoriCombo.getEditor().setText(current);
    }

    private void buildTierList() {
        tierListContainer.getChildren().clear();
        for (String category : PackageCatalog.categories()) {
            Label header = new Label(category.toUpperCase());
            header.getStyleClass().add("admin-cat-header");
            tierListContainer.getChildren().add(header);
            for (PackageCatalog.Tier tier : PackageCatalog.tiersFor(category)) {
                tierListContainer.getChildren().add(buildTierRow(tier));
            }
        }
    }

    private VBox buildTierRow(PackageCatalog.Tier tier) {
        VBox row = new VBox(8);
        row.getStyleClass().add("tier-row");

        HBox top = new HBox(12);
        top.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(3);
        HBox.setHgrow(info, Priority.ALWAYS);

        HBox titleLine = new HBox(10);
        titleLine.setAlignment(Pos.CENTER_LEFT);
        Label name = new Label(tier.label());
        name.getStyleClass().add("tier-title");
        titleLine.getChildren().add(name);
        if (tier.highlighted()) {
            Label badge = new Label("UNGGULAN");
            badge.getStyleClass().add("price-badge");
            titleLine.getChildren().add(badge);
        }

        Label price = new Label(tier.priceFormatted() + "  " + tier.period());
        price.getStyleClass().add("tier-price");

        Label feat = new Label(tier.features().size() + " benefit  ·  "
                + String.join(", ", tier.features()));
        feat.getStyleClass().add("order-detail");
        feat.setWrapText(true);
        feat.setMaxWidth(520);

        info.getChildren().addAll(titleLine, price, feat);

        VBox actions = new VBox(8);
        actions.setAlignment(Pos.TOP_RIGHT);
        Button editBtn = new Button("EDIT");
        editBtn.getStyleClass().add("btn-outline-dark");
        editBtn.setOnAction(e -> loadIntoForm(tier));
        Button deleteBtn = new Button("HAPUS");
        deleteBtn.getStyleClass().add("btn-danger-outline");
        deleteBtn.setOnAction(e -> confirmDelete(tier));
        actions.getChildren().addAll(editBtn, deleteBtn);

        top.getChildren().addAll(info, actions);
        row.getChildren().add(top);
        return row;
    }

    private void confirmDelete(PackageCatalog.Tier tier) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Hapus paket \"" + tier.label() + "\"? Tindakan ini tidak bisa dibatalkan.",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.setTitle("Hapus Paket");
        confirm.showAndWait().ifPresent(choice -> {
            if (choice == ButtonType.YES) {
                PackageCatalog.removeTier(tier.label());
                Database.logActivity(adminEmail(), "PAKET_HAPUS",
                        "Menghapus paket \"" + tier.label() + "\"");
                if (tier.label().equals(editingLabel)) {
                    resetForm();
                }
                refreshKategoriOptions();
                buildTierList();
            }
        });
    }

    // ===================== FORM TAMBAH / EDIT =====================

    private void loadIntoForm(PackageCatalog.Tier tier) {
        editingLabel = tier.label();
        formTitleLabel.setText("Edit Paket: " + tier.label());
        simpanButton.setText("SIMPAN PERUBAHAN");
        kategoriCombo.getEditor().setText(tier.category());
        kategoriCombo.setValue(tier.category());
        namaField.setText(tier.name());
        hargaField.setText(String.valueOf(tier.price()));
        periodeField.setText(tier.period());
        unggulanCheck.setSelected(tier.highlighted());
        fiturArea.setText(String.join("\n", tier.features()));
        clearStatus();
    }

    @FXML
    private void handleReset() {
        resetForm();
    }

    private void resetForm() {
        editingLabel = null;
        formTitleLabel.setText("Tambah Paket Baru");
        simpanButton.setText("TAMBAH PAKET");
        kategoriCombo.getEditor().clear();
        kategoriCombo.setValue(null);
        namaField.clear();
        hargaField.clear();
        periodeField.setText("/ sesi");
        unggulanCheck.setSelected(false);
        fiturArea.clear();
        clearStatus();
    }

    @FXML
    private void handleSave() {
        String category = safe(kategoriCombo.getEditor().getText());
        String name = safe(namaField.getText());
        String periode = safe(periodeField.getText());
        String hargaRaw = safe(hargaField.getText()).replaceAll("[^0-9]", "");

        if (category.isEmpty() || name.isEmpty()) {
            showError("Kategori dan nama tier wajib diisi.");
            return;
        }
        if (hargaRaw.isEmpty()) {
            showError("Harga wajib diisi berupa angka.");
            return;
        }
        long price;
        try {
            price = Long.parseLong(hargaRaw);
        } catch (NumberFormatException ex) {
            showError("Harga tidak valid.");
            return;
        }
        if (price < 0) {
            showError("Harga tidak boleh negatif.");
            return;
        }
        if (periode.isEmpty()) {
            periode = "/ sesi";
        }

        // Fitur: satu benefit per baris.
        List<String> features = new ArrayList<>();
        for (String line : fiturArea.getText().split("\\R")) {
            String f = line.trim();
            if (!f.isEmpty()) {
                features.add(f);
            }
        }
        if (features.isEmpty()) {
            showError("Isi minimal satu benefit (satu baris per benefit).");
            return;
        }

        PackageCatalog.Tier tier = new PackageCatalog.Tier(
                category, name, price, periode, unggulanCheck.isSelected(), features);

        boolean ok;
        if (editingLabel == null) {
            ok = PackageCatalog.addTier(tier);
            if (!ok) {
                showError("Paket \"" + tier.label() + "\" sudah ada. Gunakan nama lain.");
                return;
            }
            Database.logActivity(adminEmail(), "PAKET_TAMBAH",
                    "Menambah paket \"" + tier.label() + "\" ("
                            + tier.priceFormatted() + ")");
        } else {
            String oldLabel = editingLabel;
            ok = PackageCatalog.updateTier(editingLabel, tier);
            if (!ok) {
                showError("Gagal menyimpan: nama \"" + tier.label() + "\" bentrok dengan paket lain.");
                return;
            }
            Database.logActivity(adminEmail(), "PAKET_UBAH",
                    "Mengubah paket \"" + oldLabel + "\" menjadi \"" + tier.label()
                            + "\" (" + tier.priceFormatted() + ")");
        }

        refreshKategoriOptions();
        buildTierList();
        resetForm();
        showSuccess("Paket \"" + tier.label() + "\" berhasil disimpan.");
    }

    // ===================== UTIL =====================

    private void showError(String msg) {
        statusLabel.getStyleClass().removeAll("form-status-success");
        if (!statusLabel.getStyleClass().contains("form-status-error")) {
            statusLabel.getStyleClass().add("form-status-error");
        }
        statusLabel.setText(msg);
    }

    private void showSuccess(String msg) {
        statusLabel.getStyleClass().removeAll("form-status-error");
        if (!statusLabel.getStyleClass().contains("form-status-success")) {
            statusLabel.getStyleClass().add("form-status-success");
        }
        statusLabel.setText(msg);
    }

    private void clearStatus() {
        statusLabel.setText("");
    }

    private String safe(String v) {
        return v == null ? "" : v.trim();
    }

    private String adminEmail() {
        User u = Session.getCurrentUser();
        return u == null ? Session.ADMIN_EMAIL : u.getEmail();
    }
}
