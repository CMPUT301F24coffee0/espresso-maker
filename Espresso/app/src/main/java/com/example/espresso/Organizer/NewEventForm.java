package com.example.espresso.Organizer;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.espresso.Attendee.User;
import com.example.espresso.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Activity for creating or editing a new event.
 * It collects event details from the user and allows them to upload an event image.
 * If editing an existing event, it loads the existing event data from Firestore.
 */

public class NewEventForm extends AppCompatActivity {
    // Firestore instance to interact with the database
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String selectedLocation = null;
    private String deviceID;

    // EditText fields for event details
    private EditText eventName, eventDate, eventTime, registrationDeadline, waitingListCapacity, attendee_sample_num;
    private Spinner eventLocation;
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

        deviceID = new User(this).getDeviceID();

        // Adjust UI for system bars (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.landing_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        eventLocation = findViewById(R.id.location);

        List<String> locations = new ArrayList<>();
        locations.add("Select Location");

        db.collection("users").document(deviceID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String facility = documentSnapshot.getString("facility");
                        if (facility != null && !facility.isEmpty()) {
                            locations.add(facility);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load facility", Toast.LENGTH_SHORT).show();
                    Log.e("OrganizerFacility", "Error fetching facility", e);
                });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                locations
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventLocation.setAdapter(adapter);

        eventLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedLocation = parent.getItemAtPosition(position).toString();
                } else {
                    selectedLocation = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedLocation = null;
            }
        });


        // Initialize EditText fields
        eventName = findViewById(R.id.event_name);
        eventLocation = findViewById(R.id.location);
        eventDate = findViewById(R.id.choose_date);
        eventTime = findViewById(R.id.choose_time);
        registrationDeadline = findViewById(R.id.registration_until);
        waitingListCapacity = findViewById(R.id.waiting_list_capacity);
        attendee_sample_num = findViewById(R.id.attendee_sample_num);

        eventDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    NewEventForm.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        eventDate.setText(date);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        eventTime.setOnClickListener(v -> {
            // Get the current time
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            // Create a TimePickerDialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    NewEventForm.this,
                    (view, selectedHour, selectedMinute) -> {
                        String time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                        eventTime.setText(time);
                    },
                    hour, minute, true
            );
            timePickerDialog.show();
        });

        // Retrieve the event type (create or edit) from the intent
        Intent intent = getIntent();
        String eventType = intent.getStringExtra("action");

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
            if (validateInputs()) {
                Bundle bundle = new Bundle();

                // Put the event details into the bundle
                bundle.putString("eventName", eventName.getText().toString());
                bundle.putString("eventLocation", selectedLocation);
                bundle.putString("eventDate", eventDate.getText().toString());
                bundle.putString("eventTime", eventTime.getText().toString());
                bundle.putString("registrationDeadline", registrationDeadline.getText().toString());
                bundle.putString("waitingListCapacity", waitingListCapacity.getText().toString());
                bundle.putString("documentId", documentId);
                bundle.putString("sample", attendee_sample_num.getText().toString());
                bundle.putString("status", eventType);
                bundle.putString("sample", attendee_sample_num.getText().toString());

                // Start the image upload fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ImageUploadFragment fragment = new ImageUploadFragment();
                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.landing_page, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;
        if (eventName.getText().toString().trim().isEmpty()) {
            eventName.setError("Event name is required");
            isValid = false;
        }

        if (selectedLocation == null) {
            Toast.makeText(this, "Please select a location", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if (eventDate.getText().toString().trim().isEmpty()) {
            eventDate.setError("Event date is required");
            isValid = false;
        }
        if (eventTime.getText().toString().trim().isEmpty()) {
            eventTime.setError("Event time is required");
            isValid = false;
        }
        if (attendee_sample_num.getText().toString().isEmpty() || !android.text.TextUtils.isDigitsOnly(attendee_sample_num.getText().toString())) {
            attendee_sample_num.setError("Enter a valid number");
            isValid = false;
        }
        if (!android.text.TextUtils.isDigitsOnly(waitingListCapacity.getText().toString())) {
            waitingListCapacity.setError("Enter a valid number");
        }
        return isValid;
    }

    private int getLocationPosition(String location) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) eventLocation.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(location)) {
                return i;
            }
        }
        return -1;
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

                        String location = documentSnapshot.getString("location");
                        if (location != null) {
                            int position = getLocationPosition(location);
                            if (position != -1) {
                                eventLocation.setSelection(position);
                            }
                        }

                        eventDate.setText(documentSnapshot.getString("date"));
                        eventTime.setText(documentSnapshot.getString("time"));
                        registrationDeadline.setText(documentSnapshot.getString("deadline"));
                        Long capacity = documentSnapshot.getLong("capacity");
                        waitingListCapacity.setText(capacity != null ? String.valueOf(capacity) : "");


                        Long sample = documentSnapshot.getLong("sample");
                        attendee_sample_num.setText(sample != null ? String.valueOf(sample) : "");
                    }
                    Log.d("document ID: ", documentId);
                })
                .addOnFailureListener(e -> Toast.makeText(NewEventForm.this, "Failed to load data", Toast.LENGTH_SHORT).show());
    }
}
