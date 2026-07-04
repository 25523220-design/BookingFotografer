package model;

import java.util.UUID;

public class TransferBank {

    private String codePembayaran;
    private String namaBank;
    private String nomorRekening;
    private String namaPenerima;

    public TransferBank(String codePembayaran, String namaBank,
                        String nomorRekening, String namaPenerima) {

        this.codePembayaran = codePembayaran;
        this.namaBank = namaBank;
        this.nomorRekening = nomorRekening;
        this.namaPenerima = namaPenerima;
    }

    public String getCodePembayaran() {
        return codePembayaran;
    }

    public void setCodePembayaran(String codePembayaran) {
        this.codePembayaran = codePembayaran;
    }

    public String getNamaBank() {
        return namaBank;
    }

    public void setNamaBank(String namaBank) {
        this.namaBank = namaBank;
    }

    public String getNomorRekening() {
        return nomorRekening;
    }

    public void setNomorRekening(String nomorRekening) {
        this.nomorRekening = nomorRekening;
    }

    public String getNamaPenerima() {
        return namaPenerima;
    }

    public void setNamaPenerima(String namaPenerima) {
        this.namaPenerima = namaPenerima;
    }


    public String getMetode() {
        return "TRANSFER";
    }


    public String tampilkanInstruksi() {
        return "Silakan transfer ke rekening berikut:\n"
                + "Bank : " + namaBank + "\n"
                + "No. Rekening : " + nomorRekening + "\n"
                + "Atas Nama : " + namaPenerima + "\n"
                + "Gunakan kode pembayaran " + codePembayaran
                + " pada keterangan transfer jika diperlukan.";
    }

    
    public String generateCodePembayaran() {
        codePembayaran = "TRF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return codePembayaran;
    }
}