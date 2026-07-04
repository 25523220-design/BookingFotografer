package model;

public class Admin extends User {

    private String idAdmin;

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