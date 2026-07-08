package com.landstudio;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Utility sederhana untuk berpindah halaman (mengganti root Scene) tanpa
 * membuka window baru, supaya ukuran & posisi window tetap sama.
 */
public final class SceneManager {

    private static Stage stage;

    private SceneManager() {
    }

    public static void init(Stage primaryStage) {
        stage = primaryStage;
    }

    public static void switchTo(String fxmlFile) {
        switchTo(fxmlFile, null);
    }

    /**
     * @param fxmlFile nama file FXML di package com.landstudio, mis. "MainView.fxml".
     * @param param    data opsional untuk halaman tujuan (mis. nama kategori paket).
     *                 Hanya dipakai kalau controller tujuan punya setCategory(String).
     */
    public static void switchTo(String fxmlFile, String param) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlFile));
            Parent root = loader.load();
            Object controller = loader.getController();

            Scene scene = stage.getScene();
            scene.setRoot(root);

            if (controller instanceof MainController mc) {
                mc.bindResponsiveSizing(scene);
            } else if (controller instanceof PortfolioController pc) {
                pc.bindResponsiveSizing(scene);
            } else if (controller instanceof BookingController bc) {
                bc.bindResponsiveSizing(scene);
            } else if (controller instanceof PaketController pkc) {
                pkc.bindResponsiveSizing(scene);
            } else if (controller instanceof PaketTierController ptc) {
                ptc.bindResponsiveSizing(scene);
                if (param != null) {
                    ptc.setCategory(param);
                }
            } else if (controller instanceof PaketDetailController pdc) {
                pdc.bindResponsiveSizing(scene);
                if (param != null) {
                    pdc.setTier(param);
                }
            } else if (controller instanceof AuthController ac) {
                ac.bindResponsiveSizing(scene);
            } else if (controller instanceof PaymentController payc) {
                payc.bindResponsiveSizing(scene);
            } else if (controller instanceof RescheduleController rc) {
                rc.bindResponsiveSizing(scene);
            } else if (controller instanceof AdminDashboardController adc) {
                adc.bindResponsiveSizing(scene);
            } else if (controller instanceof AdminPaketController apc) {
                apc.bindResponsiveSizing(scene);
            }
        } catch (Exception e) {
            throw new RuntimeException("Gagal memuat halaman: " + fxmlFile, e);
        }
    }
}
