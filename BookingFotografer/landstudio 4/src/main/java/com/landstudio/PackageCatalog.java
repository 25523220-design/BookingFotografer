package com.landstudio;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Sumber tunggal data paket + harga. Sekarang datanya BISA DIUBAH oleh admin
 * lewat dashboard (tambah / edit / hapus paket) dan perubahannya langsung
 * terpakai di seluruh aplikasi karena semua halaman membaca dari sini:
 *  - PaketDetailController  (kartu harga per kategori)
 *  - BookingController      (pilihan paket + harga)
 *  - Booking                (menghitung total tagihan)
 *  - AdminPaketController   (mengelola paket)
 *
 * Data disimpan di memori saja (hilang saat aplikasi ditutup), cukup untuk demo.
 */
public final class PackageCatalog {

    /** Satu tier/paket harga, mis. kategori "Pernikahan" tier "Gold". */
    public record Tier(String category, String name, long price,
                       String period, boolean highlighted, List<String> features) {

        /** Nama lengkap paket, mis. "Pernikahan Gold" — dipakai sebagai
         *  label pilihan di halaman Booking. */
        public String label() {
            return category + " " + name;
        }

        public String priceFormatted() {
            return formatRupiah(price);
        }

        /** Untuk ditampilkan di dropdown booking, mis. "Pernikahan Gold — Rp 6.000.000". */
        public String labelWithPrice() {
            return label() + "  —  " + priceFormatted();
        }
    }

    /** Daftar paket aktif. Diisi data default saat kelas pertama dipakai. */
    private static final List<Tier> TIERS = new ArrayList<>();

    static {
        Database.init();
        List<Tier> saved = Database.loadPackages();
        if (saved.isEmpty()) {
            // Belum ada packages.xml -> pakai data bawaan lalu simpan.
            TIERS.addAll(defaultTiers());
            Database.savePackages(TIERS);
        } else {
            // Sudah ada -> pakai data hasil ubahan admin yang tersimpan.
            TIERS.addAll(saved);
        }
    }

    private PackageCatalog() {
    }

    // ===================== BACA (dipakai semua halaman) =====================

    /** Kategori yang tersedia saat ini, dengan urutan kanonik didahulukan. */
    public static List<String> categories() {
        Set<String> ordered = new LinkedHashSet<>();
        // Urutan tampilan yang diutamakan.
        for (String c : List.of("Pernikahan", "Wisuda", "Pre-Wedding")) {
            if (hasCategory(c)) {
                ordered.add(c);
            }
        }
        // Kategori lain (misalnya buatan admin) menyusul.
        for (Tier t : TIERS) {
            ordered.add(t.category());
        }
        return new ArrayList<>(ordered);
    }

    private static boolean hasCategory(String category) {
        for (Tier t : TIERS) {
            if (t.category().equals(category)) {
                return true;
            }
        }
        return false;
    }

    /** Daftar tier untuk satu kategori. */
    public static List<Tier> tiersFor(String category) {
        List<Tier> result = new ArrayList<>();
        for (Tier t : TIERS) {
            if (t.category().equals(category)) {
                result.add(t);
            }
        }
        return result;
    }

    /** Semua tier dari semua kategori (untuk dropdown pilihan booking & admin). */
    public static List<Tier> allTiers() {
        return new ArrayList<>(TIERS);
    }

    /** Cari tier dari label lengkap ("Pernikahan Gold"). null jika tidak ada. */
    public static Tier byLabel(String label) {
        if (label == null) {
            return null;
        }
        for (Tier t : TIERS) {
            if (t.label().equals(label)) {
                return t;
            }
        }
        return null;
    }

    public static boolean containsLabel(String label) {
        return byLabel(label) != null;
    }

    // ===================== TULIS (khusus admin) =====================

    /** Tambah paket baru. Mengembalikan false jika label (kategori+nama) sudah ada. */
    public static boolean addTier(Tier tier) {
        if (tier == null || containsLabel(tier.label())) {
            return false;
        }
        TIERS.add(tier);
        Database.savePackages(TIERS); // simpan perubahan ke packages.xml
        return true;
    }

    /**
     * Ganti paket lama (dikenali dari {@code oldLabel}) dengan data baru.
     * Mengembalikan false jika paket lama tidak ditemukan, atau jika label baru
     * bentrok dengan paket lain yang sudah ada.
     */
    public static boolean updateTier(String oldLabel, Tier updated) {
        if (updated == null) {
            return false;
        }
        int index = indexOfLabel(oldLabel);
        if (index < 0) {
            return false;
        }
        // Cek bentrok label baru dengan paket lain (selain dirinya sendiri).
        Tier existing = byLabel(updated.label());
        if (existing != null && !updated.label().equals(oldLabel)) {
            return false;
        }
        TIERS.set(index, updated);
        Database.savePackages(TIERS); // simpan perubahan ke packages.xml
        return true;
    }

    /** Hapus paket berdasarkan label. Mengembalikan false jika tidak ada. */
    public static boolean removeTier(String label) {
        int index = indexOfLabel(label);
        if (index < 0) {
            return false;
        }
        TIERS.remove(index);
        Database.savePackages(TIERS); // simpan perubahan ke packages.xml
        return true;
    }

    private static int indexOfLabel(String label) {
        for (int i = 0; i < TIERS.size(); i++) {
            if (TIERS.get(i).label().equals(label)) {
                return i;
            }
        }
        return -1;
    }

    // ===================== UTIL =====================

    public static String formatRupiah(long value) {
        return String.format(new Locale("id", "ID"), "Rp %,d", value).replace(',', '.');
    }

    /** Data paket bawaan (nilai awal sebelum admin mengubahnya). */
    private static List<Tier> defaultTiers() {
        List<Tier> d = new ArrayList<>();
        // ---- Pernikahan ----
        d.add(new Tier("Pernikahan", "Silver", 3_500_000L, "/ acara", false, List.of(
                "1 fotografer profesional",
                "Durasi sesi 4 jam",
                "30 foto hasil edit",
                "Softcopy semua foto")));
        d.add(new Tier("Pernikahan", "Gold", 6_000_000L, "/ acara", true, List.of(
                "2 fotografer profesional",
                "Durasi sesi 8 jam",
                "80 foto hasil edit",
                "1 album cetak eksklusif",
                "Video highlight durasi 1 menit")));
        d.add(new Tier("Pernikahan", "Premium", 10_000_000L, "/ acara", false, List.of(
                "2 fotografer + 1 videografer",
                "Liputan seharian penuh",
                "Seluruh foto hasil edit",
                "Album premium hardcover",
                "Video sinematik 3-5 menit",
                "Dokumentasi udara (drone)")));
        // ---- Wisuda ----
        d.add(new Tier("Wisuda", "Silver", 300_000L, "/ sesi", false, List.of(
                "Durasi sesi 30 menit",
                "1 lokasi di area kampus",
                "15 foto hasil edit",
                "Softcopy (tanpa cetak)")));
        d.add(new Tier("Wisuda", "Gold", 500_000L, "/ sesi", true, List.of(
                "Durasi sesi 1 jam",
                "2 lokasi di area kampus",
                "30 foto hasil edit",
                "1 cetak foto ukuran besar",
                "Softcopy semua foto")));
        d.add(new Tier("Wisuda", "Premium", 800_000L, "/ sesi", false, List.of(
                "Durasi sesi 1.5 jam",
                "3 lokasi bebas pilih",
                "50 foto hasil edit",
                "Album mini cetak",
                "Sesi tambahan bersama keluarga")));
        // ---- Pre-Wedding ----
        d.add(new Tier("Pre-Wedding", "Silver", 1_500_000L, "/ paket", false, List.of(
                "1 lokasi pilihan studio",
                "Durasi sesi 2 jam",
                "20 foto hasil edit",
                "Softcopy semua foto")));
        d.add(new Tier("Pre-Wedding", "Gold", 2_500_000L, "/ paket", true, List.of(
                "2 lokasi (indoor & outdoor)",
                "Durasi sesi 4 jam",
                "40 foto hasil edit",
                "1 cetak kanvas ukuran besar",
                "Konsultasi konsep & kostum")));
        d.add(new Tier("Pre-Wedding", "Premium", 4_000_000L, "/ paket", false, List.of(
                "3 lokasi bebas pilih",
                "Sesi seharian penuh",
                "80 foto hasil edit",
                "Video sinematik singkat",
                "Styling & wardrobe konsultasi")));
        return d;
    }
}
