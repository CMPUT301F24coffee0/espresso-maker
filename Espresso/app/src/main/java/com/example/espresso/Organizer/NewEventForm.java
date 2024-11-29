package com.example.espresso.Organizer;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.espresso.R;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Activity for creating or editing a new event.
 * It collects event details from the user and allows them to upload an event image.
 * If editing an existing event, it loads the existing event data from Firestore.
 */

public class NewEventForm extends AppCompatActivity {
    // Firestore instance to interact with the database
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // EditText fields for event details
    private EditText eventName, eventLocation, eventDate, eventTime, registrationDeadline, waitingListCapacity, attendee_sample_num;

    // Document ID to identify the event for editing
    private String documentId;

    /**
     * Called when the activity is created. Sets up the UI elements, retrieves the event type
     * (create or edit), and initializes the event data if editing an existing event.
     *
     * @param savedInstanceState Saved state from a previous instance of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_event_form);

        // Adjust UI for system bars (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.landing_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize EditText fields
        eventName = findViewById(R.id.event_name);
        eventLocation = findViewById(R.id.location);
        eventDate = findViewById(R.id.choose_date);
        eventTime = findViewById(R.id.choose_time);
        registrationDeadline = findViewById(R.id.registration_until);
        waitingListCapacity = findViewById(R.id.waiting_list_capacity);
        // attendee_sample_num = findViewById(R.id.attendee_sample_num);

        // Retrieve the event type (create or edit) from the intent
        Intent intent = getIntent();
        String eventType = intent.getStringExtra("status");

        // If editing an event, load its data
        if ("edit".equals(eventType)) {
            documentId = intent.getStringExtra("eventId");
            if (documentId != null && !documentId.isEmpty()) {
                loadEventData();
            } else {
                Toast.makeText(this, "Invalid document ID", Toast.LENGTH_SHORT).show();
            }
        }

        // Set up the 'next' button to navigate to the image upload fragment
        Button nextButton = findViewById(R.id.next_button);

        ImageButton close = findViewById(R.id.close_button);

        close.setOnClickListener(v -> finish());

        nextButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();

            // Put the event details into the bundle
            bundle.putString("eventName", eventName.getText().toString());
            bundle.putString("eventLocation", eventLocation.getText().toString());
            bundle.putString("eventDate", eventDate.getText().toString());
            bundle.putString("eventTime", eventTime.getText().toString());
            bundle.putString("registrationDeadline", registrationDeadline.getText().toString());
            bundle.putString("waitingListCapacity", waitingListCapacity.getText().toString());
            bundle.putString("documentId", documentId);
            // bundle.putString("sample", attendee_sample_num.getText().toString());

            // Start the image upload fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            ImageUploadFragment fragment = new ImageUploadFragment();
            fragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.landing_page, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
    }

    /**
     * Loads event data from Firestore if editing an existing event.
     * This will populate the form fields with the current data of the event.
     */
    private void loadEventData() {
        if (documentId == null || documentId.isEmpty()) {
            Toast.makeText(this, "Invalid document ID, unable to load event data.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch event data from Firestore
        db.collection("events").document(documentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Populate form fields with event data
                        eventName.setText(documentSnapshot.getString("name"));
                        eventLocation.setText(documentSnapshot.getString("location"));
                        eventDate.setText(documentSnapshot.getString("date"));
                        eventTime.setText(documentSnapshot.getString("time"));
                        registrationDeadline.setText(documentSnapshot.getString("deadline"));
                        Long capacity = documentSnapshot.getLong("capacity");
                        waitingListCapacity.setText(capacity != null ? String.valueOf(capacity) : "");
                        // attendee_sample_num.setText(documentSnapshot.getString("sample"));
                    }
                    Log.d("document ID: ", documentId);
                })
                .addOnFailureListener(e -> Toast.makeText(NewEventForm.this, "Failed to load data", Toast.LENGTH_SHORT).show());
    }
}
