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
 * The main entry point of the application. This activity handles the initial screen that
 * provides sign-in options for different user types (Attendee, Organizer, Admin). It checks
 * if the user is already registered in the Firebase Firestore database, and if not, creates a new
 * user profile with a randomly generated username.
 */
public class MainActivity extends AppCompatActivity {
    public String deviceID;
    Button attendee_sign_in_btn, org_sign_in_btn, admin_sign_in_btn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    boolean isLoggedIn;

    /**
     * Called when the activity is first created. Initializes the layout, sets up listeners for the
     * sign-in buttons, checks if the user exists in the Firestore database, and creates new user profiles
     * if the user is not found.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.start_up);

        // Adjust system bar insets for the landing page
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.landing_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve device ID for the current user
        deviceID = new User(this).getDeviceID();

        // Reference to the user document in Firestore
        DocumentReference docRef = db.collection("users").document(deviceID);

        // Check if the user already exists in Firestore
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    isLoggedIn = true;
                    Log.d("auth", "User exists: " + document.getData());
                } else {
                    isLoggedIn = false;
                    Log.d("auth", "User does not exist");
                }
            } else {
                Log.d("auth", "get() failed with " + task.getException());
            }
        });

        // Initialize sign-in buttons
        admin_sign_in_btn = findViewById(R.id.AdminSignInButton);
        org_sign_in_btn = findViewById(R.id.OrganizerSignInButton);
        attendee_sign_in_btn = findViewById(R.id.AttendeeSignInButton);

        // Set listener for admin sign-in button
        admin_sign_in_btn.setOnClickListener(v -> {
            Toast toast = Toast.makeText(this, "To be implemented", Toast.LENGTH_SHORT);
            toast.show();
        });

        // Set listener for attendee sign-in button
        attendee_sign_in_btn.setOnClickListener(v -> {
            if (isLoggedIn) {
                // Navigate to AttendeeHomeActivity if user is logged in
                Intent i = new Intent(MainActivity.this, AttendeeHomeActivity.class);
                MainActivity.this.startActivity(i);
            } else {
                // Create new user profile if not logged in
                createNewUserProfile("Attendee");
            }
        });

        // Set listener for organizer sign-in button
        org_sign_in_btn.setOnClickListener(v -> {
            if (isLoggedIn) {
                // Navigate to OrganizerHomeActivity if user is logged in
                Intent i = new Intent(MainActivity.this, OrganizerHomeActivity.class);
                MainActivity.this.startActivity(i);
            } else {
                // Create new user profile if not logged in
                createNewUserProfile("Organizer");
            }
        });
    }

    /**
     * Creates a new user profile in Firestore with random user data if the user is not logged in.
     * The profile includes a random username, placeholder email, phone number, and facility information.
     * Once the user profile is created, the activity navigates to the appropriate home screen
     * based on the user type.
     *
     * @param userType The type of user ("Attendee" or "Organizer") for the new profile.
     */
    private void createNewUserProfile(String userType) {
        // Create a new user document reference in Firestore
        DocumentReference newUser = db.collection("users").document(deviceID);

        // Generate a random username
        Random random = new Random();
        Map<String, Object> docData = new HashMap<>();
        docData.put("type", userType); // Set user type (Attendee or Organizer)
        docData.put("deviceID", deviceID);
        docData.put("name", random.ints(48, 122 + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(10)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString());
        docData.put("email", "Not set");
        docData.put("phone", 123);
        docData.put("facility", "Not set");

        // Add the new user profile data to Firestore
        newUser.set(docData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("add_user", "DocumentSnapshot successfully written!");
                    // Navigate to the appropriate home activity
                    Intent i;
                    if ("Attendee".equals(userType)) {
                        i = new Intent(MainActivity.this, AttendeeHomeActivity.class);
                    } else {
                        i = new Intent(MainActivity.this, OrganizerHomeActivity.class);
                    }
                    MainActivity.this.startActivity(i);
                    isLoggedIn = true;
                })
                .addOnFailureListener(e -> Log.w("add_user", "Error writing document", e));
    }
}
