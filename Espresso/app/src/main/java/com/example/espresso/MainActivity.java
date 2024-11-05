package com.example.espresso;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    String deviceID;
    Button attendee_sign_in_btn, org_sign_in_btn, admin_sign_in_btn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.start_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.landing_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        deviceID = new User(this).getDeviceID(); // Get device ID

        DocumentReference docRef = db.collection("users").document(deviceID);

        (docRef.get()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    isLoggedIn = true;
                    Log.d("auth", "User exists " + document.getData());

                } else {
                    Log.d("auth", "User does not exist");
                    isLoggedIn = false;
                }
            } else {
                Log.d("auth", "get() failed with " + task.getException());
            }
        });

        org_sign_in_btn = findViewById(R.id.OrganizerSignInButton);
        admin_sign_in_btn = findViewById((R.id.AdminSignInButton));
        attendee_sign_in_btn = findViewById(R.id.AttendeeSignInButton);

        admin_sign_in_btn.setOnClickListener(v -> {
            Toast toast = Toast.makeText(this, "To be implemented", Toast.LENGTH_SHORT);
            toast.show();
        });

        attendee_sign_in_btn.setOnClickListener(v -> {
            if (isLoggedIn) {
                // Start Attendee Dashboard
            } else {
                // Create a new user
                DocumentReference newUser = db.collection("users").document(deviceID);

                Map<String, Object> docData = new HashMap<>();
                docData.put("type", "Not Admin");
                docData.put("deviceID", deviceID);
                docData.put("name", "P Diddy");
                docData.put("email", null);
                docData.put("phone", null);
                docData.put("facility", null);

                newUser.set(docData)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("add_user", "DocumentSnapshot successfully written!");
                            // Start Attendee Dashboard
                            isLoggedIn = true;
                        })
                        .addOnFailureListener(e -> Log.w("add_user", "Error writing document " + e));

            }
        });

        org_sign_in_btn.setOnClickListener(v -> {
            if (isLoggedIn) {
                Intent i = new Intent(MainActivity.this, OrganizerHomeActivity.class);
                i.putExtra("key", 0); //Optional parameters
                MainActivity.this.startActivity(i);

            } else {
                // Create a new user
                DocumentReference newUser = db.collection("users").document(deviceID);

                Map<String, Object> docData = new HashMap<>();
                docData.put("type", "organizer");
                docData.put("deviceID", deviceID);
                docData.put("name", "P Diddy");
                docData.put("email", null);
                docData.put("phone", null);
                docData.put("facility", null);

                newUser.set(docData)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("add_user", "DocumentSnapshot successfully written!");
                            // Start Organizer Dashboard
                            isLoggedIn = true;
                        })
                        .addOnFailureListener(e -> Log.w("add_user", "Error writing document", e));
            }
        });
    }
}