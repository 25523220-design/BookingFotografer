package com.landstudio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Satu data pesanan/booking sesi foto milik seorang pelanggan.
 */
public class Booking {

    public enum PaymentStatus {
        BELUM_BAYAR("Belum Dibayar"),
        MENUNGGU_KONFIRMASI("Menunggu Konfirmasi"),
        LUNAS("Lunas");

        private final String label;

        PaymentStatus(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }
    }

    public enum BookingStatus {
        MENUNGGU_KONFIRMASI("Menunggu Konfirmasi"),
        DIKONFIRMASI("Dikonfirmasi"),
        DIBATALKAN("Dibatalkan");

        private final String label;

        BookingStatus(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }
    }

    private static final DateTimeFormatter TANGGAL_FORMAT =
            DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", new Locale("id", "ID"));

    private final String id;
    private final String ownerEmail;
    private String namaPelanggan;
    private String email;
    private String telepon;
    private String alamat;
    private String paket;
    private LocalDate tanggal;
    private String jam;
    private String catatan;
    private String metodePembayaran;
    private PaymentStatus paymentStatus;
    private BookingStatus status;
    private final LocalDateTime createdAt;

    public Booking(String id, String ownerEmail) {
        this(id, ownerEmail, LocalDateTime.now());
    }

    /**
     * Konstruktor untuk memulihkan booking dari penyimpanan (XML) dengan
     * mempertahankan waktu pembuatan aslinya. Dipakai oleh {@link Database}.
     */
    public Booking(String id, String ownerEmail, LocalDateTime createdAt) {
        this.id = id;
        this.ownerEmail = ownerEmail;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.paymentStatus = PaymentStatus.BELUM_BAYAR;
        this.status = BookingStatus.MENUNGGU_KONFIRMASI;
    }

    public String getId() {
        return id;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public String getNamaPelanggan() {
        return namaPelanggan;
    }

    public void setNamaPelanggan(String namaPelanggan) {
        this.namaPelanggan = namaPelanggan;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getPaket() {
        return paket;
    }

    public void setPaket(String paket) {
        this.paket = paket;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public String getTanggalFormatted() {
        return tanggal == null ? "-" : tanggal.format(TANGGAL_FORMAT);
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public String getMetodePembayaran() {
        return metodePembayaran;
    }

    public void setMetodePembayaran(String metodePembayaran) {
        this.metodePembayaran = metodePembayaran;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Total tagihan diambil langsung dari PackageCatalog berdasarkan paket
     * (mis. "Pernikahan Gold") yang dipilih pelanggan di form booking, sehingga
     * harganya selalu sama persis dengan yang tampil di halaman paket.
     */
    public long getEstimasiTotal() {
        PackageCatalog.Tier tier = PackageCatalog.byLabel(paket);
        if (tier != null) {
            return tier.price();
        }
        // Fallback (kalau paket hanya berupa nama kategori tanpa tier).
        if (paket == null) {
            return 0L;
        }
        if (paket.startsWith("Wisuda")) {
            return 500_000L;
        }
        if (paket.startsWith("Pre-Wedding")) {
            return 2_500_000L;
        }
        return 6_000_000L; // Pernikahan
    }

    public String getEstimasiTotalFormatted() {
        return PackageCatalog.formatRupiah(getEstimasiTotal());
    }
}
