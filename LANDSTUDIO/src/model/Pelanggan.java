package model;

public class Pelanggan extends User {

    private String idPelanggan;

    public Pelanggan(String idPelanggan,
                     String nama,
                     String email,
                     String password) {

        super(nama, email, password);
        this.idPelanggan = idPelanggan;
    }

    public String getIdPelanggan() {
        return idPelanggan;
    }

    public void setIdPelanggan(String idPelanggan) {
        this.idPelanggan = idPelanggan;
    }
}