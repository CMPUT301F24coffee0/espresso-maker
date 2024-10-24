package com.example.espresso;

/**
 * The QRCode class stores a unique QR code associated with an event.
 * The QR code can be used to view event details or sign up for the event.
 *
 * Responsibilities:
 * - Stores unique QR codes.
 *
 * Collaborators:
 * - Organizer
 */
public class QRCode {

    /**
     * The data representing the QR code.
     */
    private String qrCodeData;

    /**
     * Constructor for the QRCode class.
     * Initializes the QR code with the provided data.
     *
     * @param data The data that represents the QR code.
     */
    public QRCode(String data) {
        this.qrCodeData = data;
    }

    /**
     * Returns the QR code data.
     *
     * @return The QR code data as a string.
     */
    public String getQRCodeData() {
        return qrCodeData;
    }
}
