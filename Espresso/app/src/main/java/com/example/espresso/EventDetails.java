package com.example.espresso;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.Picasso;

public class EventDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_event_profile);

        Intent intent = getIntent();

        // Retrieve the extras
        String name = intent.getStringExtra("name");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String location = intent.getStringExtra("location");
        String description = intent.getStringExtra("description");
        String deadline = intent.getStringExtra("deadline");
        int capacity = intent.getIntExtra("capacity", 0); // Default value is 0
        String eventId = intent.getStringExtra("eventId");
        String posterUrl = intent.getStringExtra("posterUrl");

        Log.d("Event", "Event after clicked: Name=" + name + ", Date=" + date + ", Time=" + time + ", Location=" + location + ", Description=" + description + ", Deadline=" + deadline + ", Capacity=" + capacity + ", EventId=" + eventId);

        // Set the event details in the UI
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
        Picasso.get().load(posterUrl).into(imageView);
    }
}