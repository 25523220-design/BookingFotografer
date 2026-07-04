package model;

public class Admin extends User {

    private String idAdmin;

    public void tambahPaket() {}
    public void hapusPaket() {}
    public void editPaket() {}
    public void tambahPportofolio() {}
    public void hapusPortofolio() {}
    public void editPortofolio() {}

    public Admin(String idAdmin,
                     String nama,
                     String email,
                     String password) {

        super(nama, email, password);
        this.idAdmin = idAdmin;
    }

    public String getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(String idAdmin) {
        this.idAdmin = idAdmin;
    }
}