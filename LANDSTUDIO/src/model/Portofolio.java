package model;

public class Portofolio {

    private String idPortofolio;
    private String judul;
    private String kategori;
    private String foto;
    private String deskripsi;

    public Portofolio(String idPortofolio, String judul, String kategori, String foto, String deskripsi) {
        this.idPortofolio = idPortofolio;
        this.judul = judul;
        this.kategori = kategori;
        this.foto = foto;
        this.deskripsi = deskripsi;
    }

    public String getIdPortofolio() {
        return idPortofolio;
    }

    public void setIdPortofolio(String idPortofolio) {
        this.idPortofolio = idPortofolio;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }


    public void tambahFoto(String foto) {
        this.foto = foto;
    }


    public void hapusFoto() {
        this.foto = "";
    }

    public void ubahPortofolio(String judul, String kategori, String foto, String deskripsi) {
        this.judul = judul;
        this.kategori = kategori;
        this.foto = foto;
        this.deskripsi = deskripsi;
    }

}