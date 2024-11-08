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

public class ScanQR extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;  // Define the permission code
    private CompoundBarcodeView barcodeView;
    private Button goBackButton, flashButton;
    private boolean isTorchOn = false;  // Variable to keep track of torch state
    String name, date, time, location, description, deadline;
    int capacity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scan_qr);
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
            barcodeView.resume();
        }

        // Handle the back button click
        goBackButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AttendeeHomeActivity.class);
            startActivity(intent);
            finish();
        });

        // Handle the flash button click
        flashButton.setOnClickListener(v -> {
            if (isTorchOn) {
                barcodeView.setTorchOff();
                //flashButton.setBackgroundResource(R.drawable.ic_flash_off);
                Toast.makeText(this, "Flash turned off", Toast.LENGTH_SHORT).show();
                isTorchOn = false;
            } else {
                barcodeView.setTorchOn();
                flashButton.setBackgroundResource(R.drawable.ic_flash_on);
                Toast.makeText(this, "Flash turned on", Toast.LENGTH_SHORT).show();
                isTorchOn = true;
            }
        });

        // Handle the barcode scan result
        // Set up the barcode callback for handling scan results
        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                // Handle the scanned result
                String qrCodeData = result.getText();
                if (qrCodeData != null) {
                    Toast.makeText(ScanQR.this, "Scanned: " + qrCodeData, Toast.LENGTH_LONG).show();
                    // Fetch event details based on the QR code data
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
                    // Optionally, you could open a new activity or perform any action with the result
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

                    // Stop further scans once you have the result
                    barcodeView.pause();
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
                // Optionally handle possible result points (optional)
            }
        });
    }

    // Handle the camera permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                barcodeView.resume();
            } else {
                // Permission denied
                Toast.makeText(this, "Camera permission is needed to scan QR codes", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
