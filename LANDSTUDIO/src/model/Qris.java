package model;

import javafx.scene.image.Image;

public class Qris {

    private String merchantId;
    private String qrString;
    private String qrCode;

    public Qris(String merchantId, String qrString, String qrCode) {
        this.merchantId = merchantId;
        this.qrString = qrString;
        this.qrCode = qrCode;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getQrString() {
        return qrString;
    }

    public void setQrString(String qrString) {
        this.qrString = qrString;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }


    public Image tampilkanQR() {
        return new Image(getClass().getResourceAsStream("/images/" + qrCode));
    }

    public String getMetode() {
        return "QRIS";
    }


    public String tampilkanInstruksi() {
        return "Scan QR Code menggunakan aplikasi pembayaran "
                + "(Mobile Banking, E-Wallet, atau aplikasi yang mendukung QRIS), "
                + "kemudian lakukan pembayaran sesuai nominal.";
    }

    public String generateQRCode(double nominal) {
        qrString = "QRIS|"
                + merchantId
                + "|"
                + nominal;

        return qrString;
    }
}