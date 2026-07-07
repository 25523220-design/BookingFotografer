# Land Studio — JavaFX (versi diperbarui)

Aplikasi desktop studio foto (JavaFX + FXML). Data disimpan permanen ke
**database berbasis file XML** (folder `data/`), sehingga data registrasi,
booking, dan paket **tidak hilang** saat aplikasi ditutup.

## Database XML (folder `data/`)
Saat aplikasi pertama kali dijalankan, folder `data/` dibuat otomatis di folder
tempat aplikasi dijalankan, berisi:

| File            | Isi                                                                 |
|-----------------|---------------------------------------------------------------------|
| `users.xml`     | Data registrasi akun (pelanggan & admin, termasuk password).        |
| `bookings.xml`  | Semua pesanan/booking beserta status pembayaran & konfirmasi.       |
| `packages.xml`  | Katalog paket + harga (mengikuti perubahan yang dilakukan admin).   |
| `activity.xml`  | Log aktivitas: registrasi, booking baru, ganti jadwal, pembatalan, dan perubahan paket oleh admin. |

Cara kerjanya: `Database.java` membaca/menulis XML memakai `javax.xml` bawaan
JDK (tanpa library tambahan). `Session` memuat users & bookings saat start dan
menyimpannya lagi setiap ada perubahan; `PackageCatalog` memuat & menyimpan
paket. Pada pemakaian pertama (file belum ada), data contoh awal dibuat lalu
langsung ditulis ke XML. Menghapus folder `data/` akan mengembalikan aplikasi
ke data contoh awal.

## Cara menjalankan (paling gampang, lewat terminal)
Butuh **JDK 17+** dan **Maven** terpasang. Dari folder `landstudio/`:

```bash
mvn clean javafx:run
```

Plugin `javafx-maven-plugin` otomatis mengunduh JavaFX untuk platform kamu,
jadi tidak perlu instal JavaFX SDK terpisah.

> Kalau bukan Mac Apple Silicon, ganti `<classifier>mac-aarch64</classifier>`
> di `pom.xml` (2 tempat) sesuai platform: `mac` (Mac Intel), `win` (Windows),
> `linux`, atau `linux-aarch64`.

### Menjalankan lewat tombol Run/Debug VS Code
Jangan pakai tombol ▶ inline di atas `main()` (JavaFX tidak akan ketemu).
Gunakan panel **Run and Debug**, atau jalankan `mvn clean javafx:run` di terminal.
Entry point sudah diatur ke `com.landstudio.Launcher` supaya aman.

## Foto
Placeholder foto sudah disediakan di
`src/main/resources/com/landstudio/images/` (`pernikahan.jpg`, `wisuda.jpg`,
`prewedding.jpg`). Timpa dengan foto asli (nama sama) untuk menggantinya.

---

## Yang baru di versi ini (4 permintaan)

1. **Halaman Paket → tombol detail per kategori.**
   `PaketView` menampilkan kategori Pernikahan / Wisuda / Pre-Wedding, masing-masing
   punya tombol **LIHAT PAKET** menuju halaman detail kategori tersebut.

2. **Halaman Detail Paket lebih lengkap + booking otomatis.**
   `PaketDetailView` menampilkan foto (hero), harga, dan benefit tiap tier
   (Silver / Gold / Premium). Tombol **BOOKING PAKET INI** membawa langsung ke
   halaman Booking dengan paket yang bersangkutan **sudah terpilih otomatis**
   (lewat `Session.setSelectedPackageLabel`).

3. **Pemilihan paket di Booking lebih rinci + harga muncul.**
   Dropdown paket kini berisi kombinasi kategori + tier (mis. "Pernikahan Gold",
   "Wisuda Premium", dst). Saat pilihan berubah, **estimasi harga** ikut berubah
   otomatis. Sumber data harga terpusat di `PackageCatalog` sehingga harga di
   halaman detail, booking, pembayaran, dan bukti booking selalu konsisten.

4. **Tidak bisa memilih tanggal yang sudah dibooking.**
   Kalender di halaman Booking dan Ganti Jadwal otomatis menonaktifkan
   (dan menandai merah) tanggal yang sudah dipakai booking aktif pelanggan lain.
   Tanggal lampau juga tetap dinonaktifkan. Logika ada di `Session.isDateTaken(...)`;
   booking yang dibatalkan otomatis melepas kembali tanggalnya.

### File yang ditambah/diubah
- **Baru:** `PackageCatalog.java` (sumber tunggal data paket + harga).
- **Diubah:** `PaketDetailController` (pakai katalog + booking auto-select),
  `BookingController` (dropdown rinci + label harga + blokir tanggal penuh),
  `RescheduleController` (blokir tanggal penuh), `Booking` (total dari katalog),
  `Session` (memori paket terpilih + cek tanggal), `BookingView.fxml`
  (dropdown "Pilih Paket" + label "Estimasi Harga"), `styles.css` (kelas `.date-booked`).

---

## Dashboard Admin (baru)

Login sebagai admin untuk membuka dashboard. Akun demo sudah disediakan
(tertera juga di halaman login):

- **Admin:** `admin@landstudio.com` / `admin123`
- **Pelanggan contoh:** `budi@email.com` / `budi123` (punya 2 pesanan contoh)

Setelah admin login, aplikasi otomatis membuka **dashboard admin** yang terdiri
dari **2 halaman** (bisa berpindah lewat menu di header):

1. **Pesanan Pelanggan** (`AdminDashboardView`) — halaman utama. Menampilkan
   semua pesanan dari seluruh pelanggan beserta ringkasan angka (total, menunggu
   konfirmasi, dikonfirmasi, pendapatan). Tiap pesanan bisa **Dikonfirmasi**,
   **Ditandai Lunas**, atau **Dibatalkan**.

2. **Kelola Paket** (`AdminPaketView`) — admin bisa **menambah paket baru**,
   **mengedit** harga/periode/benefit/status unggulan, dan **menghapus** paket.
   Semua perubahan langsung berlaku di halaman paket & form booking pelanggan,
   karena seluruh aplikasi membaca dari satu sumber data `PackageCatalog`.

### File yang ditambah/diubah untuk fitur admin
- **Baru:** `AdminDashboardController.java`, `AdminPaketController.java`,
  `AdminDashboardView.fxml`, `AdminPaketView.fxml`.
- **Diubah:** `User` (tambah peran admin), `Session` (akun admin + data demo +
  `getAllBookings()`), `PackageCatalog` (jadi data yang bisa diubah admin:
  `addTier`/`updateTier`/`removeTier`), `AuthController` (admin diarahkan ke
  dashboard), `SceneManager` (registrasi 2 controller admin), `AuthView.fxml`
  (petunjuk akun demo), `styles.css` (gaya khusus admin).

> Catatan: data admin & booking disimpan di memori (tanpa database), jadi
> perubahan paket dan status pesanan kembali ke kondisi awal setiap aplikasi
> ditutup — sesuai sifat demo ini.

---

## Dashboard Admin (2 halaman)

Login sebagai admin untuk membuka dashboard. Dari halaman utama klik **Masuk**,
lalu gunakan kredensial admin bawaan:

- **Email:** `admin@landstudio.com`
- **Kata sandi:** `admin123`

Setelah login, admin otomatis diarahkan ke dashboard (pelanggan biasa tetap ke
beranda seperti biasa). Ada tautan **Pesanan** dan **Kelola Paket** di header
untuk berpindah antar dua halaman, plus **Keluar** untuk logout.

**Halaman 1 — Pesanan Pelanggan (`AdminDashboardView`)**
Menampilkan SEMUA pesanan dari seluruh pelanggan (bukan cuma milik sendiri),
diurutkan dari yang terbaru, lengkap dengan kartu ringkasan di atas (total
pesanan, menunggu konfirmasi, dikonfirmasi, dan pendapatan terkonfirmasi). Tiap
pesanan bisa di-**Konfirmasi**, ditandai **Lunas**, atau **Dibatalkan**
(dengan dialog konfirmasi). Data awal sudah diisi 2 contoh pesanan atas nama
"Budi Santoso" supaya halaman langsung ada isinya.

**Halaman 2 — Kelola Paket (`AdminPaketView`)**
Daftar semua paket per kategori di kiri (dengan tombol **Edit** & **Hapus**),
dan form **Tambah / Edit Paket** di kanan (kategori, nama tier, harga, periode,
tanda unggulan, dan benefit satu-per-baris). Karena semua halaman pelanggan
(detail paket, booking, perhitungan total) membaca dari `PackageCatalog` yang
sama, perubahan paket oleh admin **langsung berlaku** di sisi pelanggan.

> Catatan: seperti bagian lain aplikasi, data admin disimpan di memori saja —
> perubahan paket/pesanan kembali ke kondisi awal setiap aplikasi ditutup.

### File yang ditambah untuk fitur admin
- **Baru:** `AdminDashboardController.java` + `AdminDashboardView.fxml`
  (halaman pesanan), `AdminPaketController.java` + `AdminPaketView.fxml`
  (halaman kelola paket).
- **Diubah:** `Session` (akun admin, data awal, `getAllBookings`,
  `isAdminLoggedIn`), `User` (flag admin), `Booking` (status pesanan &
  pembayaran, `createdAt`), `PackageCatalog` (add/update/remove paket),
  `AuthController` (arahkan admin ke dashboard saat login), `SceneManager`
  (rute ke controller admin), `styles.css` (gaya header & kartu admin).
