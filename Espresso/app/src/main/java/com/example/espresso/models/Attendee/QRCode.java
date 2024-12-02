package com.example.espresso.models.Attendee;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

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

    static String qrCodeData;

    /**
     * Constructor for the QRCode class.
     * Initializes the QR code with the provided data.
     *
     * @param data The data that represents the QR code.
     */
    public QRCode(String data) {
        qrCodeData = data;
    }


    /**
     * Generates a QR code bitmap from the QR code data.
     *
     * @return The QR code bitmap.
     * */
    public Bitmap generateQRCode() {
        BarcodeEncoder barcodeEncoder
                = new BarcodeEncoder();
        try {
            // This method returns a Bitmap image of the
            // encoded text with a height and width of 400
            // pixels.
            return barcodeEncoder.encodeBitmap(qrCodeData, BarcodeFormat.QR_CODE, 400, 400);
        }
        catch (WriterException e) {
            Log.e("QR", "generateQRCode: "+e.getMessage());
        }
        return null;
    }
}
