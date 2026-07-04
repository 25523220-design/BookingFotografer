package model;

public class Pelanggan extends User {

    private String id_Pelanggan;

    public Pelanggan(String id_Pelanggan,
                     String nama,
                     String emailNoTelp,
                     String kataSandiBaru,
                     String kataSandi) {

        super(nama, emailNoTelp, kataSandiBaru, kataSandi);
        this.id_Pelanggan = id_Pelanggan;
    }

    public String getId_Pelanggan() {
        return id_Pelanggan;
    }

    public void setId_Pelanggan(String id_Pelanggan) {
        this.id_Pelanggan = id_Pelanggan;
    }
}
