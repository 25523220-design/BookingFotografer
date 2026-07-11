package com.landstudio;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * "Database" sederhana berbasis file XML (tanpa server / library eksternal —
 * hanya memakai javax.xml bawaan JDK). Bertugas menyimpan dan memuat kembali
 * seluruh data aplikasi supaya TIDAK hilang saat program ditutup:
 *
 *   data/users.xml     -> data registrasi akun (pelanggan & admin)
 *   data/bookings.xml  -> semua pesanan/booking
 *   data/packages.xml  -> katalog paket (yang bisa diubah admin)
 *   data/activity.xml  -> log aktivitas admin (ubah paket, konfirmasi/batal pesanan, dll)
 *
 * Semua file otomatis dibuat di folder "data" (relatif terhadap folder tempat
 * aplikasi dijalankan) pada pemakaian pertama.
 *
 * Kelas ini sengaja tidak menyentuh JavaFX supaya mudah diuji dan dipakai
 * ulang. Session dan PackageCatalog memanggil kelas ini untuk load/save.
 */
public final class Database {

    /** Folder tempat semua file XML disimpan. */
    private static final File DATA_DIR =
            new File(System.getProperty("user.dir"), "data");

    private static final File USERS_FILE    = new File(DATA_DIR, "users.xml");
    private static final File BOOKINGS_FILE = new File(DATA_DIR, "bookings.xml");
    private static final File PACKAGES_FILE = new File(DATA_DIR, "packages.xml");
    private static final File ACTIVITY_FILE = new File(DATA_DIR, "activity.xml");

    private Database() {
    }

    // ===================== INISIALISASI =====================

    /** Pastikan folder data ada. Aman dipanggil berkali-kali. */
    public static synchronized void init() {
        if (!DATA_DIR.exists()) {
            DATA_DIR.mkdirs();
        }
    }

    public static boolean usersFileExists()    { return USERS_FILE.exists(); }
    public static boolean bookingsFileExists() { return BOOKINGS_FILE.exists(); }
    public static boolean packagesFileExists() { return PACKAGES_FILE.exists(); }

    /** Lokasi folder data (untuk ditampilkan / dilog kalau perlu). */
    public static String dataDirPath() {
        return DATA_DIR.getAbsolutePath();
    }

    // ===================== USERS =====================

    public static synchronized void saveUsers(Collection<User> users) {
        try {
            Document doc = newDocument();
            Element root = doc.createElement("users");
            doc.appendChild(root);
            for (User u : users) {
                Element e = doc.createElement("user");
                e.setAttribute("admin", String.valueOf(u.isAdmin()));
                appendText(doc, e, "fullName", u.getFullName());
                appendText(doc, e, "email", u.getEmail());
                appendText(doc, e, "password", u.getPassword());
                root.appendChild(e);
            }
            writeDocument(doc, USERS_FILE);
        } catch (Exception ex) {
            logError("Gagal menyimpan users.xml", ex);
        }
    }

    public static synchronized List<User> loadUsers() {
        List<User> result = new ArrayList<>();
        if (!USERS_FILE.exists()) {
            return result;
        }
        try {
            Document doc = readDocument(USERS_FILE);
            NodeList nodes = doc.getElementsByTagName("user");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element e = (Element) nodes.item(i);
                String fullName = childText(e, "fullName");
                String email = childText(e, "email");
                String password = childText(e, "password");
                boolean admin = "true".equalsIgnoreCase(e.getAttribute("admin"));
                result.add(new User(fullName, email, password, admin));
            }
        } catch (Exception ex) {
            logError("Gagal membaca users.xml", ex);
        }
        return result;
    }

    // ===================== BOOKINGS =====================

    public static synchronized void saveBookings(Collection<Booking> bookings) {
        try {
            Document doc = newDocument();
            Element root = doc.createElement("bookings");
            doc.appendChild(root);
            for (Booking b : bookings) {
                Element e = doc.createElement("booking");
                e.setAttribute("id", nz(b.getId()));
                appendText(doc, e, "ownerEmail", b.getOwnerEmail());
                appendText(doc, e, "namaPelanggan", b.getNamaPelanggan());
                appendText(doc, e, "email", b.getEmail());
                appendText(doc, e, "telepon", b.getTelepon());
                appendText(doc, e, "alamat", b.getAlamat());
                appendText(doc, e, "paket", b.getPaket());
                appendText(doc, e, "tanggal",
                        b.getTanggal() == null ? "" : b.getTanggal().toString());
                appendText(doc, e, "jam", b.getJam());
                appendText(doc, e, "catatan", b.getCatatan());
                appendText(doc, e, "metodePembayaran", b.getMetodePembayaran());
                appendText(doc, e, "paymentStatus", b.getPaymentStatus().name());
                appendText(doc, e, "status", b.getStatus().name());
                appendText(doc, e, "createdAt",
                        b.getCreatedAt() == null ? "" : b.getCreatedAt().toString());
                root.appendChild(e);
            }
            writeDocument(doc, BOOKINGS_FILE);
        } catch (Exception ex) {
            logError("Gagal menyimpan bookings.xml", ex);
        }
    }

    public static synchronized List<Booking> loadBookings() {
        List<Booking> result = new ArrayList<>();
        if (!BOOKINGS_FILE.exists()) {
            return result;
        }
        try {
            Document doc = readDocument(BOOKINGS_FILE);
            NodeList nodes = doc.getElementsByTagName("booking");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element e = (Element) nodes.item(i);
                String id = e.getAttribute("id");
                String ownerEmail = childText(e, "ownerEmail");
                LocalDateTime createdAt = parseDateTime(childText(e, "createdAt"));

                Booking b = new Booking(id, ownerEmail, createdAt);
                b.setNamaPelanggan(emptyToNull(childText(e, "namaPelanggan")));
                b.setEmail(emptyToNull(childText(e, "email")));
                b.setTelepon(emptyToNull(childText(e, "telepon")));
                b.setAlamat(emptyToNull(childText(e, "alamat")));
                b.setPaket(emptyToNull(childText(e, "paket")));
                b.setTanggal(parseDate(childText(e, "tanggal")));
                b.setJam(emptyToNull(childText(e, "jam")));
                b.setCatatan(emptyToNull(childText(e, "catatan")));
                b.setMetodePembayaran(emptyToNull(childText(e, "metodePembayaran")));
                b.setPaymentStatus(parsePaymentStatus(childText(e, "paymentStatus")));
                b.setStatus(parseBookingStatus(childText(e, "status")));
                result.add(b);
            }
        } catch (Exception ex) {
            logError("Gagal membaca bookings.xml", ex);
        }
        return result;
    }

    // ===================== PACKAGES =====================

    public static synchronized void savePackages(Collection<PackageCatalog.Tier> tiers) {
        try {
            Document doc = newDocument();
            Element root = doc.createElement("packages");
            doc.appendChild(root);
            for (PackageCatalog.Tier t : tiers) {
                Element e = doc.createElement("tier");
                e.setAttribute("highlighted", String.valueOf(t.highlighted()));
                appendText(doc, e, "category", t.category());
                appendText(doc, e, "name", t.name());
                appendText(doc, e, "price", String.valueOf(t.price()));
                appendText(doc, e, "period", t.period());
                Element feats = doc.createElement("features");
                for (String f : t.features()) {
                    appendText(doc, feats, "feature", f);
                }
                e.appendChild(feats);
                root.appendChild(e);
            }
            writeDocument(doc, PACKAGES_FILE);
        } catch (Exception ex) {
            logError("Gagal menyimpan packages.xml", ex);
        }
    }

    public static synchronized List<PackageCatalog.Tier> loadPackages() {
        List<PackageCatalog.Tier> result = new ArrayList<>();
        if (!PACKAGES_FILE.exists()) {
            return result;
        }
        try {
            Document doc = readDocument(PACKAGES_FILE);
            NodeList nodes = doc.getElementsByTagName("tier");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element e = (Element) nodes.item(i);
                String category = childText(e, "category");
                String name = childText(e, "name");
                long price = parseLong(childText(e, "price"));
                String period = childText(e, "period");
                boolean highlighted = "true".equalsIgnoreCase(e.getAttribute("highlighted"));

                List<String> features = new ArrayList<>();
                NodeList featNodes = e.getElementsByTagName("feature");
                for (int j = 0; j < featNodes.getLength(); j++) {
                    features.add(featNodes.item(j).getTextContent());
                }
                result.add(new PackageCatalog.Tier(
                        category, name, price, period, highlighted, features));
            }
        } catch (Exception ex) {
            logError("Gagal membaca packages.xml", ex);
        }
        return result;
    }

    // ===================== ACTIVITY LOG =====================

    /**
     * Mencatat satu aktivitas (mis. "admin mengubah paket X", "pesanan Y
     * dikonfirmasi") ke activity.xml. Entri baru ditambahkan tanpa menghapus
     * yang lama.
     *
     * @param actor  siapa pelakunya (mis. email admin)
     * @param action kode aksi singkat (mis. "PAKET_UBAH", "PESANAN_KONFIRMASI")
     * @param detail keterangan bebas
     */
    public static synchronized void logActivity(String actor, String action, String detail) {
        try {
            init();
            Document doc;
            Element root;
            if (ACTIVITY_FILE.exists()) {
                doc = readDocument(ACTIVITY_FILE);
                root = doc.getDocumentElement();
                if (root == null) {
                    root = doc.createElement("activities");
                    doc.appendChild(root);
                }
            } else {
                doc = newDocument();
                root = doc.createElement("activities");
                doc.appendChild(root);
            }
            Element e = doc.createElement("activity");
            e.setAttribute("time", LocalDateTime.now().toString());
            appendText(doc, e, "actor", actor);
            appendText(doc, e, "action", action);
            appendText(doc, e, "detail", detail);
            root.appendChild(e);
            writeDocument(doc, ACTIVITY_FILE);
        } catch (Exception ex) {
            logError("Gagal menulis activity.xml", ex);
        }
    }

    // ===================== HELPER XML =====================

    private static Document newDocument() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.newDocument();
    }

    private static Document readDocument(File file) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);
        doc.getDocumentElement().normalize();
        return doc;
    }

    private static void writeDocument(Document doc, File file) throws Exception {
        init();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        try {
            // Rapikan indentasi (opsional; jika JDK tidak mendukung, abaikan saja).
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "2");
        } catch (IllegalArgumentException ignored) {
            // beberapa implementasi transformer tidak mengenali properti ini
        }
        transformer.transform(new DOMSource(doc), new StreamResult(file));
    }

    /** Menambahkan elemen anak berisi teks, mis. &lt;email&gt;a@b.com&lt;/email&gt;. */
    private static void appendText(Document doc, Element parent, String tag, String value) {
        Element child = doc.createElement(tag);
        child.setTextContent(value == null ? "" : value);
        parent.appendChild(child);
    }

    /** Membaca teks anak langsung pertama dengan nama tag tertentu. */
    private static String childText(Element parent, String tag) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE && tag.equals(n.getNodeName())) {
                return n.getTextContent();
            }
        }
        return "";
    }

    // ===================== HELPER PARSE =====================

    private static String nz(String v) {
        return v == null ? "" : v;
    }

    private static String emptyToNull(String v) {
        return (v == null || v.isEmpty()) ? null : v;
    }

    private static long parseLong(String v) {
        try {
            return Long.parseLong(v.trim());
        } catch (Exception e) {
            return 0L;
        }
    }

    private static LocalDate parseDate(String v) {
        if (v == null || v.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(v.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private static LocalDateTime parseDateTime(String v) {
        if (v == null || v.isBlank()) {
            return LocalDateTime.now();
        }
        try {
            return LocalDateTime.parse(v.trim());
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }

    private static Booking.PaymentStatus parsePaymentStatus(String v) {
        try {
            return Booking.PaymentStatus.valueOf(v.trim());
        } catch (Exception e) {
            return Booking.PaymentStatus.BELUM_BAYAR;
        }
    }

    private static Booking.BookingStatus parseBookingStatus(String v) {
        try {
            return Booking.BookingStatus.valueOf(v.trim());
        } catch (Exception e) {
            return Booking.BookingStatus.MENUNGGU_KONFIRMASI;
        }
    }

    private static void logError(String message, Exception ex) {
        System.err.println("[Database] " + message + ": " + ex.getMessage());
    }
}
