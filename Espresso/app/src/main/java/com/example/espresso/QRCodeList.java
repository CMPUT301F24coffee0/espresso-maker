package com.example.espresso;

import java.util.ArrayList;

public class QRCodeList {
    private ArrayList<QRCode> qrCodes;

    /**
     * Create a new QRCodeList.
     */
    public QRCodeList() {
        qrCodes = new ArrayList<>();
    }

    /**
     * Add a QR code to the list.
     * @param qrCode
     */
    public void addQRCode(QRCode qrCode) {
        qrCodes.add(qrCode);
    }

    /**
     * Remove a QR code from the list.
     * @param qrCode
     */
    public void removeQRCode(QRCode qrCode) {
        qrCodes.remove(qrCode);
    }

    /**
     * Get the list of QR codes.
     * @return  List of QR codes.
     */
    public ArrayList<QRCode> getQRCodes() {
        return qrCodes;
    }
}
