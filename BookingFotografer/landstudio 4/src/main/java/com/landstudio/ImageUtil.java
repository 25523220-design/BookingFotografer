package com.landstudio;

import javafx.geometry.Side;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Region;

/**
 * Membantu memuat foto kategori dari classpath secara ANDAL (lewat
 * getResourceAsStream), lalu memasangnya sebagai background "cover" pada kartu.
 *
 * Sebelumnya foto dipanggil lewat CSS {@code url("images/...")} yang kadang
 * tidak terbaca tergantung cara aplikasi dijalankan. Dengan memuatnya langsung
 * di Java, foto dijamin terbaca selama file-nya ada di
 * {@code src/main/resources/com/landstudio/images/}.
 */
public final class ImageUtil {

    private ImageUtil() {
    }

    /** Nama file foto untuk sebuah kategori paket. */
    private static String fileFor(String category) {
        String c = category == null ? "" : category.toLowerCase();
        if (c.startsWith("wisuda")) {
            return "images/wisuda.jpg";
        }
        if (c.startsWith("pre")) {
            return "images/prewedding.jpg";
        }
        return "images/pernikahan.jpg"; // default: Pernikahan
    }

    /** Memuat Image untuk kategori. Mengembalikan null bila file tidak ditemukan. */
    public static Image categoryImage(String category) {
        String file = fileFor(category);
        try {
            var in = ImageUtil.class.getResourceAsStream(file);
            if (in == null) {
                System.err.println("[ImageUtil] Foto tidak ditemukan: " + file);
                return null;
            }
            return new Image(in);
        } catch (Exception e) {
            System.err.println("[ImageUtil] Gagal memuat foto " + file + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Memasang foto kategori sebagai background yang menutupi penuh (cover)
     * pada {@code region} (biasanya StackPane kartu). Aman dipanggil kapan saja;
     * bila foto tidak ada, region dibiarkan tanpa foto.
     */
    public static void applyCategoryBackground(Region region, String category) {
        if (region == null) {
            return;
        }
        Image img = categoryImage(category);
        if (img == null) {
            return;
        }
        BackgroundSize cover = new BackgroundSize(
                BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, false, true);

        // Posisi vertikal foto pada kartu. Default: tengah. Untuk Pre-Wedding,
        // fotonya di-"naikkan" sedikit (menampilkan bagian yang lebih bawah)
        // supaya pasangan tampak di tengah dan tidak tertutup teks di bawah kartu.
        String c = category == null ? "" : category.toLowerCase();
        double vpos = c.startsWith("pre") ? 0.72 : 0.5;
        BackgroundPosition pos = new BackgroundPosition(
                Side.LEFT, 0.5, true, Side.TOP, vpos, true);

        BackgroundImage bg = new BackgroundImage(
                img,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                pos,
                cover);
        region.setBackground(new Background(bg));
    }
}
