package model;

public class DetailPaket {
    private String id_paket;
    private String nama_paket;
    private String harga_paket;
    private String durasi_paket;
    private String deskripsi_paket;
    private String foto_paket;

    public void tampilkanPaket() {}

    public DetailPaket(String id_paket, String nama_paket, String harga_paket, String durasi_paket, String deskripsi_paket, String foto_paket) {
        this.id_paket = id_paket;
        this.nama_paket = nama_paket;
        this.harga_paket = harga_paket;
        this.durasi_paket = durasi_paket;
        this.deskripsi_paket = deskripsi_paket;
        this.foto_paket = foto_paket;
    }

    public String getId_paket() {
        return id_paket;
    }

    public void setId_paket(String id_paket) {
        this.id_paket = id_paket;
    }

    public String getNama_paket() {
        return nama_paket;
    }

    public void setNama_paket(String nama_paket) {
        this.nama_paket = nama_paket;
    }

    public String getHarga_paket() {
        return harga_paket;
    }

    public void setHarga_paket(String harga_paket) {
        this.harga_paket = harga_paket;
    }

    public String getDurasi_paket() {
        return durasi_paket;
    }

    public void setDurasi_paket(String durasi_paket) {
        this.durasi_paket = durasi_paket;
    }

    public String getDeskripsi_paket() {
        return deskripsi_paket;
    }

    public void setDeskripsi_paket(String deskripsi_paket) {
        this.deskripsi_paket = deskripsi_paket;
    }

    public String getFoto_paket() {
        return foto_paket;
    }

    public void setFoto_paket(String foto_paket) {
        this.foto_paket = foto_paket;
    }
}
