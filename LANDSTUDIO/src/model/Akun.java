package model;

public class Akun {
    private String nama;
    private String emailOrNoTelp;
    private String kataSandiBaru;
    private String kataSandi;

    public Akun() {
    }

    public Akun(String nama, String email, String emailOrNoTelp, String kataSandiBaru, String kataSandi) {
        this.nama = nama;
        this.emailOrNoTelp = emailOrNoTelp;
        this.kataSandiBaru = kataSandiBaru;
        this.kataSandi = kataSandi;
    }

    public String getNama() {
        return nama;
    }

    public String getEmailOrNoTelp() {
        return emailOrNoTelp;
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

    public void setEmailOrNoTelp(String emailOrNoTelp) {
        this.emailOrNoTelp = emailOrNoTelp;
    }


    public void setKataSandiBaru(String kataSandiBaru) {
        this.kataSandiBaru = kataSandiBaru;
    }

    public void setKataSandi(String kataSandi) {
        this.kataSandi = kataSandi;
    }
}
