 package model;

public class User {
    protected String nama;
    protected String emailNoTelp;
    protected String kataSandiBaru;
    protected String kataSandi;

    public void daftar() {}
    public void masuk() {}
    public void keluar() {}

    public User(String nama,
                String emailNoTelp,
                String kataSandiBaru,
                String kataSandi) {

        this.nama = nama;
        this.emailNoTelp = emailNoTelp;
        this.kataSandiBaru = kataSandiBaru;
        this.kataSandi = kataSandi;
    }

    public String getNama() {
        return nama;
    }

    public String getEmailNoTelp() {
        return emailNoTelp;
    }
    
    public String getKataSandiBaru() {
        return kataSandiBaru;
    }

    public String getKataSandi() {
        return kataSandi;
    }
    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setEmailNoTelp(String emailNoTelp) {
        this.emailNoTelp = emailNoTelp;
    }


    public void setKataSandiBaru(String kataSandiBaru) {
        this.kataSandiBaru = kataSandiBaru;
    }

    public void setKataSandi(String kataSandi) {
        this.kataSandi = kataSandi;
    }
}