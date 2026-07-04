package model;

public class Booking {
    private String idBooking;
    private String namaPaket;
    private String lokasiAcara;
    private String tanggalAcara;
    private String jamAcara;
    private String tanggalBooking;
    private statusBooking status;

    private enum statusBooking {
        TERKONFIRMASI,
        DIBATALKAN,
        SELESAI
    }

    public void buatBooking() {}
    public void cekKetersediaan() {}
    public void ubahJadwal() {}
    public void batalkanBooking() {}
    
    public Booking(String idBooking, String namaPaket, String lokasiAcara, String tanggalAcara, String jamAcara, String tanggalBooking, statusBooking status) {
        this.idBooking = idBooking;
        this.namaPaket = namaPaket;
        this.lokasiAcara = lokasiAcara;
        this.tanggalAcara = tanggalAcara;
        this.jamAcara = jamAcara;
        this.tanggalBooking = tanggalBooking;
        this.status = status;
    }

}