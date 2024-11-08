package com.example.espresso;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
/**
 * Activity that displays detailed information about a specific event.
 * The user can view event details such as name, date, time, location, description, capacity, and registration deadline.
 * Depending on the user's current registration status (confirmed, pending, declined),
 * the user may also have the option to enter the lottery for the event.
 */
public class EventDetails extends AppCompatActivity {

    private Button enterLotteryButton;  // Button for entering the lottery system

    /**
     * Called when the activity is created. Sets up the UI and handles interaction.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down
     *                           then this Bundle contains the data it most recently supplied.
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_event_profile);  // Set the content view layout

        FirebaseFirestore db = FirebaseFirestore.getInstance();  // Firebase Firestore instance
        String deviceID = new User(this).getDeviceID();  // Get the user's device ID
        Intent intent = getIntent();  // Retrieve intent with event details

        // Handle the onBackPressed event to navigate to AttendeeHomeActivity
        OnBackPressedDispatcher dispatcher = getOnBackPressedDispatcher();
        dispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(EventDetails.this, AttendeeHomeActivity.class);
                startActivity(intent);  // Navigate back to the home activity
            }
        });

        // Retrieve event details passed via intent
        String name = intent.getStringExtra("name");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String location = intent.getStringExtra("location");
        String description = intent.getStringExtra("description");
        String deadline = intent.getStringExtra("deadline");
        int capacity = intent.getIntExtra("capacity", 0);  // Default value is 0
        String eventId = intent.getStringExtra("eventId");
        String posterUrl = intent.getStringExtra("posterUrl");
        String status = intent.getStringExtra("status");

        Log.d("Event", "Event after clicked: Name=" + name + ", Date=" + date + ", Time=" + time +
                ", Location=" + location + ", Description=" + description + ", Deadline=" + deadline +
                ", Capacity=" + capacity + ", EventId=" + eventId);  // Log event details for debugging

        // Set event details into corresponding TextViews and ImageView
        TextView nameTextView = findViewById(R.id.attendee_event_profile_title);
        nameTextView.setText(name);
        TextView dateTextView = findViewById(R.id.attendee_event_profile_date);
        dateTextView.setText(String.format("%s - %s", date, time));
        TextView locationTextView = findViewById(R.id.attendee_event_profile_location);
        locationTextView.setText(location);
        TextView descriptionTextView = findViewById(R.id.attendee_event_profile_description);
        descriptionTextView.setText(description);
        TextView deadlineTextView = findViewById(R.id.attendee_event_profile_deadline);
        deadlineTextView.setText(String.format("Registration closes: %s", deadline));
        TextView capacityTextView = findViewById(R.id.attendee_event_profile_capacity_text);
        capacityTextView.setText(String.format("Capacity: %d", capacity));
        ImageView imageView = findViewById(R.id.attendee_event_profile_banner_img);
        Picasso.get().load(posterUrl).into(imageView);  // Load event poster using Picasso

        // Create a map to store event data for Firestore
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("name", name);
        eventData.put("date", date);
        eventData.put("time", time);
        eventData.put("location", location);
        eventData.put("description", description);
        eventData.put("deadline", deadline);
        eventData.put("capacity", capacity);
        eventData.put("status", "pending");

        // Button to enter the lottery for the event
        enterLotteryButton = findViewById(R.id.enter_lottery_button);

        // Switch button behavior based on the event's status (confirmed, pending, declined)
        switch (Objects.requireNonNull(status)) {
            case "confirmed":
                enterLotteryButton.setEnabled(false);
                enterLotteryButton.setText("Confirmed");
                enterLotteryButton.setTextColor(Color.BLACK);
                enterLotteryButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("green")));
                break;
            case "pending":
                enterLotteryButton.setEnabled(false);
                enterLotteryButton.setText("Pending");
                enterLotteryButton.setTextColor(Color.BLACK);
                enterLotteryButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("yellow")));
                break;
            case "declined":
                enterLotteryButton.setEnabled(false);
                enterLotteryButton.setText("Declined");
                enterLotteryButton.setTextColor(Color.WHITE);
                enterLotteryButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("red")));
                break;
        }

        // OnClickListener for the "Enter Lottery" button
        enterLotteryButton.setOnClickListener(v -> {
            // Add the event data to the user's events collection in Firestore
            db.collection("users").document(deviceID).collection("events").document(eventId).set(eventData);

            // Add the user as a participant in the event's "participants" collection in Firestore
            db.collection("events").document(eventId).collection("participants").document(deviceID).set(Map.of("status", "lottery"))
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Lottery entry successful
                            Log.d("Lottery", "Lottery entered successfully");

                            // Update the button UI to reflect the user's entry
                            enterLotteryButton.setEnabled(false);
                            enterLotteryButton.setText("You have entered the lottery!");
                            enterLotteryButton.setTextColor(Color.WHITE);
                            enterLotteryButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("grey")));
                        } else {
                            // Lottery entry failed
                            Log.d("Lottery", "Lottery entry failed");
                        }
                    });
        });
    }
}
