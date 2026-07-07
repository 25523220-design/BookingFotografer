package com.landstudio;

/**
 * Data akun pengguna (pelanggan atau admin) yang sudah daftar/login.
 * Disimpan sementara di memori (lihat Session) — cukup untuk demo desktop app
 * tanpa database/backend sungguhan.
 */
public class User {

    private final String fullName;
    private final String email;
    private final String password;
    private final boolean admin;

    /** Konstruktor pelanggan biasa (bukan admin). */
    public User(String fullName, String email, String password) {
        this(fullName, email, password, false);
    }

    public User(String fullName, String email, String password, boolean admin) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.admin = admin;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    /** true jika akun ini adalah admin studio (boleh membuka dashboard admin). */
    public boolean isAdmin() {
        return admin;
    }
}
