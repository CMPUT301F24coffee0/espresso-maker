package com.example.espresso;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.espresso.Admin.AdminActivity;
import com.example.espresso.Attendee.AttendeeHomeActivity;
import com.example.espresso.Attendee.User;
import com.example.espresso.Organizer.OrganizerHomeActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import android.Manifest;

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
    String deviceToken;

    String role;

    /**
     * Prompts the user for setting notification permissions. The user can either choose to allow
     * or disallow notifications. The app takes this choice into account before sending notifications
     * for event decisions.
     */
    private void showNotificationPermissionDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_notification_permission);

        // Set dialog width and height
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        // Initialize buttons
        Button allowButton = dialog.findViewById(R.id.button_allow);
        Button denyButton = dialog.findViewById(R.id.button_deny);

        // Handle "Allow Notifications"
        allowButton.setOnClickListener(v -> {
            dialog.dismiss();
            // Request notification permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                Toast.makeText(this, "Notification permissions not required for this Android version.", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle "No, Thanks"
        denyButton.setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(this, "Notifications disabled. You won't receive updates.", Toast.LENGTH_SHORT).show();
        });

        // Show the dialog
        dialog.show();
        dialog.getWindow().setAttributes(params);
    }

    /**
     * Declare the launcher at the top of your Activity/Fragment for Notification permissions.
     */
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                    Log.d("notification", "Notification permission granted");
                } else {
                    Toast.makeText(this, "Notifications disabled", Toast.LENGTH_LONG).show();
                }
            });


    /**
     * Notify the user about their selected notification permissions. The app require a minimum "Tiramisu" SDK
     * for this action.
     */
    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                Log.d("notification", "Notification permission already granted");
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // Show the custom dialog
                showNotificationPermissionDialog();
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    // Check subscribed topic

    /**
     * Handle notification subscriptions for a particular eventID
     * @param eventId EventID belonging to the Event Object in Firebase Database
     */
    public void checkSubscribedTopic(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(deviceID).collection("events").document(eventId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    if (Objects.equals(document.getString("status"), "confirmed")) {
                        // Subscribe to the topic
                        FirebaseMessaging.getInstance().subscribeToTopic(eventId+"confirmed");
                    }
                }
                else {
                    Log.d("notification", "Document does not exist");
                }
            } else {
                Log.d("notification", "get() failed with " + task.getException());
        }});
    }

    public interface TokenCallback { void onTokenReceived(String token);}

    /**
     * Retain a registration token for a given TokenCallback object.
     * @param callback Used for fetching FCM registration
     */
    public void getRegistrationToken(TokenCallback callback) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("fcm", "Fetching FCM registration token failed", task.getException());
                        callback.onTokenReceived(null); // Notify callback of failure
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.d("fcm", "Token: " + token);

                    // Notify the callback of the token
                    callback.onTokenReceived(token);
                });
    }

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

        // Ask for notification permission
        askNotificationPermission();
        // Get registration token
        getRegistrationToken(token -> {
            if (token != null) {
                Log.d("Token", "Received token: " + token);
                //Toast.makeText(MainActivity.this, "Token: " + token, Toast.LENGTH_SHORT).show();
                deviceToken = token;
            } else {
                Log.w("Token", "Failed to retrieve token.");
            }
        });


        // Retrieve device ID for the current user
        deviceID = new User(this).getDeviceID();

        // Reference to the user document in Firestore
        DocumentReference docRef = db.collection("users").document(deviceID);

        // Fetch all events for the current user from Firestore and check if they are subscribed to the topic
        docRef.collection("events").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            checkSubscribedTopic(document.getId());
                        }
                        } else {
                        Log.d("events", "Error getting documents: ", task.getException());
                    }
                });

        role = "Not Admin";
        // Check if the user already exists in Firestore
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    isLoggedIn = true;
                    Log.d("auth", "User exists: " + document.getData());
                    role = Objects.requireNonNull(Objects.requireNonNull(document.getData()).get("type")).toString();
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
            if (isLoggedIn && role.equals("Admin")) {
                Intent i = new Intent(MainActivity.this, AdminActivity.class);
                MainActivity.this.startActivity(i);
            } else {
                Toast.makeText(this, "Sorry, you don't have administrative permissions", Toast.LENGTH_LONG).show();
            }
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
        docData.put("type", "Not Admin"); // Set user type
        docData.put("deviceID", deviceID);
        docData.put("name", random.ints(48, 122 + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(10)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString());
        docData.put("email", "email@example.com");
        docData.put("phone", "0");
        docData.put("deviceToken", deviceToken);

        // Add the new user profile data to Firestore
        newUser.set(docData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("add_user", "DocumentSnapshot successfully written!");
                    // Navigate to the appropriate home activity
                    Intent i;

                    if ("Attendee".equals(userType)) i = new Intent(MainActivity.this, AttendeeHomeActivity.class);
                    else if ("Organizer".equals(userType)) i = new Intent(MainActivity.this, OrganizerHomeActivity.class);
                    else i = new Intent(MainActivity.this, AdminActivity.class);

                    MainActivity.this.startActivity(i);
                    isLoggedIn = true;
                })
                .addOnFailureListener(e -> Log.w("add_user", "Error writing document", e));
        Toast.makeText(this, "Welcome! Kindly Update your personal Information in Profile section", Toast.LENGTH_LONG).show();
    }

}
