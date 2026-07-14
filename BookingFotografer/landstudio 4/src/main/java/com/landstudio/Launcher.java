package com.landstudio;

/**
 * Kelas ini HANYA dibutuhkan jika menjalankan "Main.java" langsung dari IDE
 * memunculkan error "JavaFX runtime components are missing".
 *
 * Menjalankan Launcher (kelas biasa, bukan Application) sebagai entry point
 * akan menghindari masalah tersebut.
 */
public class Launcher {
    public static void main(String[] args) {
        Main.main(args);
    }
}
