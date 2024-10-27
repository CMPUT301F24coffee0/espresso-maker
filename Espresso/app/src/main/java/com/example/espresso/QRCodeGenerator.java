package com.example.espresso;
// Source: https://www.geeksforgeeks.org/how-to-generate-qr-code-in-android/

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

/**
 * The QRCodeGenerator class is responsible for generating a unique QR code for an event.
 *
 * Responsibilities:
 * - Generates a unique QR code.
 * - Automatically uses event-specific data for the QR code.
 *
 * Collaborators:
 * - QRCode
 */
public class QRCodeGenerator extends AppCompatActivity {

    private ImageView qrCodeIV; // ImageView to display the QR code
    private Button generateQrBtn; // Button to trigger QR code generation

    /**
     * Called when the activity is first created. This method sets up the layout and initializes the UI
     * elements such as the ImageView and Button.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the saved data. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Set onClick listener for the button to generate the QR code
        generateQrBtn.setOnClickListener(new View.OnClickListener() {
            /**
             * Handles the click event for the generate QR button.
             * When the button is clicked, it calls the method to automatically generate the QR code for the event.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                // Automatically generate the QR code for the event
                generateQRCodeForEvent();
            }
        });
    }

    /**
     * Generates a QR code for the event using event-specific data.
     */
    private void generateQRCodeForEvent() {
        String eventID = UUID.randomUUID().toString(); // Use UUID for unique event ID

    }
}
