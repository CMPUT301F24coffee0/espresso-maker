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
import java.util.Random;
/**
 * The MainActivity class represents the landing page of the application.
 * It allows users to sign in as attendees or organizers. If the user is not already registered,
 * a new user profile is created in Firestore.
 */
public class MainActivity extends AppCompatActivity {

    private String deviceID;  // The device ID of the user
    private Button attendee_sign_in_btn, org_sign_in_btn, admin_sign_in_btn;  // Buttons for sign-in
    private FirebaseFirestore db = FirebaseFirestore.getInstance();  // Firebase Firestore instance
    private boolean isLoggedIn;  // Flag to check if the user is logged in or not

    /**
     * Called when the activity is created. Initializes the UI and sets up listeners for the sign-in buttons.
     * It also checks if the user is already logged in by querying Firestore.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);  // Enables edge-to-edge display on the activity
        setContentView(R.layout.start_up);  // Sets the layout for the activity

        // Adjust the window insets for edge-to-edge UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.landing_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        deviceID = new User(this).getDeviceID();  // Retrieve the device ID from the User class

        DocumentReference docRef = db.collection("users").document(deviceID);  // Reference to Firestore document

        // Check if the user already exists in Firestore
        (docRef.get()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    isLoggedIn = true;  // User is logged in
                    Log.d("auth", "User exists: " + document.getData());
                } else {
                    isLoggedIn = false;  // User is not logged in
                    Log.d("auth", "User does not exist");
                }
            } else {
                Log.d("auth", "get() failed with " + task.getException());
            }
        });

        // Initialize the sign-in buttons
        admin_sign_in_btn = findViewById(R.id.AdminSignInButton);
        org_sign_in_btn = findViewById(R.id.OrganizerSignInButton);
        attendee_sign_in_btn = findViewById(R.id.AttendeeSignInButton);

        // Set onClick listener for the admin sign-in button (not yet implemented)
        admin_sign_in_btn.setOnClickListener(v -> {
            Toast toast = Toast.makeText(this, "To be implemented", Toast.LENGTH_SHORT);
            toast.show();
        });

        // Set onClick listener for the attendee sign-in button
        attendee_sign_in_btn.setOnClickListener(v -> {
            if (isLoggedIn) {
                // If the user is logged in, navigate to the AttendeeHomeActivity
                Intent i = new Intent(MainActivity.this, AttendeeHomeActivity.class);
                MainActivity.this.startActivity(i);
            } else {
                // If the user is not logged in, create a new user in Firestore
                createNewUserAndNavigate("Attendee");
            }
        });

        // Set onClick listener for the organizer sign-in button
        org_sign_in_btn.setOnClickListener(v -> {
            if (isLoggedIn) {
                // If the user is logged in, navigate to the OrganizerHomeActivity
                Intent i = new Intent(MainActivity.this, OrganizerHomeActivity.class);
                MainActivity.this.startActivity(i);
            } else {
                // If the user is not logged in, create a new user in Firestore
                createNewUserAndNavigate("Organizer");
            }
        });
    }

    /**
     * Creates a new user in Firestore and navigates to the corresponding home activity.
     *
     * @param userType The type of user (either "Attendee" or "Organizer").
     */
    private void createNewUserAndNavigate(String userType) {
        DocumentReference newUser = db.collection("users").document(deviceID);  // Reference to the new user document
        Random random = new Random();

        // Create a map to store the user's data
        Map<String, Object> docData = new HashMap<>();
        docData.put("type", userType);  // Set user type (Attendee or Organizer)
        docData.put("deviceID", deviceID);

        // Generate a random username
        docData.put("name", random.ints(48, 122 + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(10)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString());
        docData.put("email", "Not set");  // Default values for email and phone
        docData.put("phone", 0);
        docData.put("facility", "Not set");

        // Save the new user to Firestore
        newUser.set(docData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("add_user", "DocumentSnapshot successfully written!");

                    // Navigate to the appropriate home activity based on user type
                    if (userType.equals("Attendee")) {
                        Intent i = new Intent(MainActivity.this, AttendeeHomeActivity.class);
                        MainActivity.this.startActivity(i);
                    } else if (userType.equals("Organizer")) {
                        Intent i = new Intent(MainActivity.this, OrganizerHomeActivity.class);
                        MainActivity.this.startActivity(i);
                    }

                    isLoggedIn = true;  // Set login status to true
                })
                .addOnFailureListener(e -> Log.w("add_user", "Error writing document", e));
    }
}
