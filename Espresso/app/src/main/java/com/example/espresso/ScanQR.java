package com.example.espresso;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;
import java.util.Map;
import java.util.Objects;
/**
 * This activity allows the user to scan QR codes to fetch event details from Firestore.
 * It provides functionality for toggling the flashlight and navigating back to the previous screen.
 */
public class ScanQR extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;  // Define the permission code
    private CompoundBarcodeView barcodeView;
    private Button goBackButton, flashButton;
    private boolean isTorchOn = false;  // Variable to keep track of torch state
    String name, date, time, location, description, deadline;
    int capacity;

    /**
     * Called when the activity is first created. This method initializes the user interface,
     * checks for camera permission, and sets up barcode scanning.
     *
     * @param savedInstanceState If the activity is being reinitialized, this bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);  // Enables edge-to-edge screen mode
        setContentView(R.layout.activity_scan_qr);

        // Initialize UI components
        goBackButton = findViewById(R.id.go_back_button);
        flashButton = findViewById(R.id.flash_button);
        barcodeView = findViewById(R.id.barcode_scanner);

        // Request camera permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        } else {
            barcodeView.resume();  // Start barcode scanning if permission is granted
        }

        // Handle the back button click to navigate to the AttendeeHomeActivity
        goBackButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AttendeeHomeActivity.class);
            startActivity(intent);
            finish();
        });

        // Handle the flash button click to toggle the flashlight
        flashButton.setOnClickListener(v -> {
            if (isTorchOn) {
                barcodeView.setTorchOff();  // Turn off the torch
                Toast.makeText(this, "Flash turned off", Toast.LENGTH_SHORT).show();
                isTorchOn = false;
            } else {
                barcodeView.setTorchOn();  // Turn on the torch
                flashButton.setBackgroundResource(R.drawable.ic_flash_on);
                Toast.makeText(this, "Flash turned on", Toast.LENGTH_SHORT).show();
                isTorchOn = true;
            }
        });

        // Set up the barcode scan result callback
        barcodeView.decodeContinuous(new BarcodeCallback() {
            /**
             * Called when a barcode is scanned. This method fetches event details from Firestore
             * based on the QR code data and opens the EventDetails activity.
             *
             * @param result The result of the barcode scan.
             */
            @Override
            public void barcodeResult(BarcodeResult result) {
                // Handle the scanned QR code data
                String qrCodeData = result.getText();
                if (qrCodeData != null) {
                    Toast.makeText(ScanQR.this, "Scanned: " + qrCodeData, Toast.LENGTH_LONG).show();

                    // Fetch event details from Firestore using the QR code data
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("events").document(qrCodeData).get().addOnSuccessListener(documentSnapshot -> {
                        Map<String, Object> eventData = documentSnapshot.getData();
                        if (eventData != null) {
                            name = (String) eventData.get("name");
                            date = (String) eventData.get("date");
                            time = (String) eventData.get("time");
                            location = (String) eventData.get("location");
                            description = (String) eventData.get("description");
                            deadline = (String) eventData.get("deadline");
                            capacity = ((Long) Objects.requireNonNull(eventData.get("capacity"))).intValue();
                        }
                    });

                    // Pass the event details to the EventDetails activity
                    Intent intent = new Intent(ScanQR.this, EventDetails.class);
                    intent.putExtra("eventID", qrCodeData);
                    intent.putExtra("name", name);
                    intent.putExtra("date", date);
                    intent.putExtra("time", time);
                    intent.putExtra("location", location);
                    intent.putExtra("description", description);
                    intent.putExtra("deadline", deadline);
                    intent.putExtra("capacity", capacity);
                    startActivity(intent);

                    // Stop further scans once the result is processed
                    barcodeView.pause();
                }
            }

            /**
             * Called to handle possible result points (optional for debugging or enhancement purposes).
             *
             * @param resultPoints The list of result points.
             */
            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
                // Optional method to handle result points (e.g., for visual feedback or debugging)
            }
        });
    }

    /**
     * Handles the camera permission result.
     * If permission is granted, resumes barcode scanning. Otherwise, displays a permission warning and closes the activity.
     *
     * @param requestCode The request code passed in requestPermissions().
     * @param permissions The requested permissions.
     * @param grantResults The grant results for the corresponding permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, resume barcode scanning
                barcodeView.resume();
            } else {
                // Permission denied, show a message and close the activity
                Toast.makeText(this, "Camera permission is needed to scan QR codes", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
