package model;

import java.util.Date;
import java.time.LocalDateTime;

public class Pembayaran {

    private String idBooking;
    private String idPembayaran;
    private double nominal;
    private String buktiTransfer;
    private Date tanggalBayar;
    private LocalDateTime batasWaktuBayar;
    private StatusBayar statusBayar;

    public Pembayaran(String idBooking, String idPembayaran, double nominal,
                      String buktiTransfer, Date tanggalBayar,
                      LocalDateTime batasWaktuBayar, StatusBayar statusBayar) {

        this.idBooking = idBooking;
        this.idPembayaran = idPembayaran;
        this.nominal = nominal;
        this.buktiTransfer = buktiTransfer;
        this.tanggalBayar = tanggalBayar;
        this.batasWaktuBayar = batasWaktuBayar;
        this.statusBayar = statusBayar;
    }

    public String getIdBooking() {
        return idBooking;
    }

    public void setIdBooking(String idBooking) {
        this.idBooking = idBooking;
    }

    public String getIdPembayaran() {
        return idPembayaran;
    }

    public void setIdPembayaran(String idPembayaran) {
        this.idPembayaran = idPembayaran;
    }

    public double getNominal() {
        return nominal;
    }

    public void setNominal(double nominal) {
        this.nominal = nominal;
    }

    public String getBuktiTransfer() {
        return buktiTransfer;
    }

    public void setBuktiTransfer(String buktiTransfer) {
        this.buktiTransfer = buktiTransfer;
    }

    public Date getTanggalBayar() {
        return tanggalBayar;
    }

    public void setTanggalBayar(Date tanggalBayar) {
        this.tanggalBayar = tanggalBayar;
    }

    public LocalDateTime getBatasWaktuBayar() {
        return batasWaktuBayar;
    }

    public void setBatasWaktuBayar(LocalDateTime batasWaktuBayar) {
        this.batasWaktuBayar = batasWaktuBayar;
    }

    public StatusBayar getStatusBayar() {
        return statusBayar;
    }

    public void setStatusBayar(StatusBayar statusBayar) {
        this.statusBayar = statusBayar;
    }

    public void uploadBukti(String buktiTransfer) {
        this.buktiTransfer = buktiTransfer;
        this.statusBayar = StatusBayar.SUDAH_BAYAR;
        this.tanggalBayar = new Date();
    }

    public StatusBayar cekStatus() {
        return statusBayar;
    }

    public boolean cekKadaluarsa() {
        return LocalDateTime.now().isAfter(batasWaktuBayar);
    }

    public String getMetode() {
        return "Transfer Bank";
    }
}