package com.landstudio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Menyimpan status aplikasi selama program berjalan: akun terdaftar, siapa yang
 * sedang login, dan daftar booking tiap pelanggan.
 *
 * Catatan: penyimpanan di memori saja (bukan database), jadi semua data hilang
 * setiap aplikasi ditutup. Cukup untuk mendemonstrasikan alur login/daftar,
 * pemilihan paket, booking, pembayaran, manajemen jadwal, dan dashboard admin.
 */
public final class Session {

    private static final Map<String, User> registeredUsers = new LinkedHashMap<>();
    private static final Map<String, List<Booking>> bookingsByEmail = new LinkedHashMap<>();
    private static final AtomicInteger bookingCounter = new AtomicInteger(1);

    private static User currentUser;
    private static Booking pendingBooking;
    private static Booking rescheduleTarget;
    private static String redirectAfterLogin;
    private static String selectedPackageLabel;

    // Akun admin bawaan (demo). Login pakai kredensial ini untuk membuka dashboard admin.
    public static final String ADMIN_EMAIL = "admin@landstudio.com";
    public static final String ADMIN_PASSWORD = "admin123";

    static {
        seedInitialData();
    }

    private Session() {
    }

    /**
     * Mengisi data awal: 1 akun admin + 1 pelanggan contoh beserta beberapa
     * booking, supaya dashboard admin langsung ada isinya saat pertama dibuka.
     */
    private static void seedInitialData() {
        // --- Admin ---
        User admin = new User("Admin Land Studio", ADMIN_EMAIL, ADMIN_PASSWORD, true);
        registeredUsers.put(normalize(ADMIN_EMAIL), admin);
        bookingsByEmail.put(normalize(ADMIN_EMAIL), new ArrayList<>());

        // --- Pelanggan contoh ---
        String demoEmail = "budi@email.com";
        User budi = new User("Budi Santoso", demoEmail, "budi123");
        registeredUsers.put(normalize(demoEmail), budi);
        List<Booking> budiBookings = new ArrayList<>();
        bookingsByEmail.put(normalize(demoEmail), budiBookings);

        LocalDate today = LocalDate.now();

        Booking b1 = new Booking(nextBookingId(), demoEmail);
        b1.setNamaPelanggan("Budi Santoso");
        b1.setEmail(demoEmail);
        b1.setTelepon("081234567890");
        b1.setAlamat("Jl. Melati No. 10, Yogyakarta");
        b1.setPaket("Pernikahan Gold");
        b1.setTanggal(today.plusDays(7));
        b1.setJam("09:00");
        b1.setMetodePembayaran("Transfer Bank");
        b1.setPaymentStatus(Booking.PaymentStatus.MENUNGGU_KONFIRMASI);
        b1.setStatus(Booking.BookingStatus.MENUNGGU_KONFIRMASI);
        budiBookings.add(b1);

        Booking b2 = new Booking(nextBookingId(), demoEmail);
        b2.setNamaPelanggan("Budi Santoso");
        b2.setEmail(demoEmail);
        b2.setTelepon("081234567890");
        b2.setAlamat("Jl. Melati No. 10, Yogyakarta");
        b2.setPaket("Wisuda Premium");
        b2.setTanggal(today.plusDays(14));
        b2.setJam("13:00");
        b2.setMetodePembayaran("QRIS");
        b2.setPaymentStatus(Booking.PaymentStatus.LUNAS);
        b2.setStatus(Booking.BookingStatus.DIKONFIRMASI);
        budiBookings.add(b2);
    }

    // ===================== AUTH =====================

    public static boolean isEmailRegistered(String email) {
        return registeredUsers.containsKey(normalize(email));
    }

    public static boolean register(String fullName, String email, String password) {
        String key = normalize(email);
        if (registeredUsers.containsKey(key)) {
            return false;
        }
        User user = new User(fullName, email, password);
        registeredUsers.put(key, user);
        bookingsByEmail.put(key, new ArrayList<>());
        currentUser = user;
        return true;
    }

    public static boolean login(String email, String password) {
        User user = registeredUsers.get(normalize(email));
        if (user == null || !user.getPassword().equals(password)) {
            return false;
        }
        currentUser = user;
        return true;
    }

    public static void logout() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    /** true jika yang sedang login adalah admin. */
    public static boolean isAdminLoggedIn() {
        return currentUser != null && currentUser.isAdmin();
    }

    /**
     * Dipanggil halaman yang mensyaratkan login sebelum berpindah. Jika belum
     * login, mengarahkan ke AuthView dan mengingat halaman tujuan semula.
     *
     * @return true jika sudah login (boleh lanjut), false jika dialihkan ke login.
     */
    public static boolean requireLogin(String intendedFxml) {
        if (isLoggedIn()) {
            return true;
        }
        redirectAfterLogin = intendedFxml;
        SceneManager.switchTo("AuthView.fxml");
        return false;
    }

    public static String consumeRedirectAfterLogin() {
        String target = redirectAfterLogin;
        redirectAfterLogin = null;
        return target == null ? "MainView.fxml" : target;
    }

    // ===================== PILIHAN PAKET (dari halaman detail -> booking) ==========

    /** Disimpan saat pelanggan menekan "BOOKING" pada satu paket di halaman detail. */
    public static void setSelectedPackageLabel(String label) {
        selectedPackageLabel = label;
    }

    /** Dibaca sekali oleh BookingController untuk preselect paket, lalu dihapus. */
    public static String consumeSelectedPackageLabel() {
        String v = selectedPackageLabel;
        selectedPackageLabel = null;
        return v;
    }

    // ===================== BOOKING =====================

    public static String nextBookingId() {
        return String.format("LS-%04d", bookingCounter.getAndIncrement());
    }

    public static void addBooking(Booking booking) {
        if (currentUser == null) {
            return;
        }
        bookingsByEmail
                .computeIfAbsent(normalize(currentUser.getEmail()), k -> new ArrayList<>())
                .add(booking);
    }

    public static List<Booking> getMyBookings() {
        if (currentUser == null) {
            return List.of();
        }
        return bookingsByEmail.getOrDefault(normalize(currentUser.getEmail()), List.of());
    }

    /**
     * Semua booking dari semua pelanggan (untuk dashboard admin), diurutkan dari
     * yang paling baru dibuat.
     */
    public static List<Booking> getAllBookings() {
        List<Booking> all = new ArrayList<>();
        for (Map.Entry<String, List<Booking>> entry : bookingsByEmail.entrySet()) {
            all.addAll(entry.getValue());
        }
        all.sort(Comparator.comparing(Booking::getCreatedAt).reversed());
        return all;
    }

    public static void setPendingBooking(Booking booking) {
        pendingBooking = booking;
    }

    public static Booking getPendingBooking() {
        return pendingBooking;
    }

    public static void clearPendingBooking() {
        pendingBooking = null;
    }

    public static void setRescheduleTarget(Booking booking) {
        rescheduleTarget = booking;
    }

    public static Booking getRescheduleTarget() {
        return rescheduleTarget;
    }

    // ===================== JADWAL (cegah tanggal dobel) =====================

    public static boolean isDateTaken(LocalDate date) {
        return isDateTaken(date, null);
    }

    /**
     * @return true jika ada booking aktif (belum dibatalkan) pada tanggal tsb.
     *         Booking {@code exclude} diabaikan — dipakai saat ganti jadwal supaya
     *         tanggal booking itu sendiri tidak dianggap "sudah dibooking".
     */
    public static boolean isDateTaken(LocalDate date, Booking exclude) {
        if (date == null) {
            return false;
        }
        for (List<Booking> list : bookingsByEmail.values()) {
            for (Booking b : list) {
                if (b == exclude) {
                    continue;
                }
                if (b.getStatus() != Booking.BookingStatus.DIBATALKAN
                        && date.equals(b.getTanggal())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
