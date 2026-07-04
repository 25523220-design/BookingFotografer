package model;

public class Admin extends User{
    private String id_Admin;

    public Admin(String id_Admin, String nama, String emailNoTelp, String kataSandiBaru, String kataSandi) {
        super(nama, emailNoTelp, kataSandiBaru, kataSandi);
      this.id_Admin = id_Admin;
    }

    public String getId_Admin() {
        return id_Admin;
    }

    public void setId_Admin(String id_Admin) {
        this.id_Admin = id_Admin;
    }
}