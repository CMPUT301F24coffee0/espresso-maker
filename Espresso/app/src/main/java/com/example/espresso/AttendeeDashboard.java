package com.example.espresso;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.espresso.databinding.ActivityMainBinding;
import com.example.espresso.databinding.EntrantHomeBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AttendeeDashboard extends AppCompatActivity {
    EntrantHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = EntrantHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Navigation
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.events) {
                    // Open events activity
                    Log.d("BottomNav", "Events clicked");
                    Intent intent = new Intent(AttendeeDashboard.this, AttendeeMyEvent.class);
                    startActivity(intent);
            }
            else if (item.getItemId() == R.id.scan) {
                    // Open scan activity
                    Log.d("BottomNav", "Scan clicked");
            }
            else if (item.getItemId() == R.id.profile) {
                // Open profile activity
                Log.d("BottomNav", "Profile clicked");
                Intent intent = new Intent(AttendeeDashboard.this, AttendeeProfile.class);
                startActivity(intent);
            }
            return true;
        });

        List<Event> events = new ArrayList<>();
        ListView listView = findViewById(R.id.event_list_view);
        EventAdapter adapter = new EventAdapter(this,events);
        listView.setAdapter(adapter);
        db.collection("events").get().addOnCompleteListener( task -> {
            if (task.isSuccessful()) {
                Log.d("Event", "Events found");
                for (int i = 0; i < task.getResult().size(); i++) {
                    // Filter out events that have already joined
                    String eventId = task.getResult().getDocuments().get(i).getId();
                    String deviceID = new User(this).getDeviceID();
                    db.collection("events").document(eventId).collection("participants").document(deviceID).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            if (task1.getResult().exists()) {
                                Log.d("Event", "Event already joined: " + eventId);
                            } else {

                            }
                        }
                    });
                    String name = task.getResult().getDocuments().get(i).getString("name");
                    String date = task.getResult().getDocuments().get(i).getString("date");
                    String time = task.getResult().getDocuments().get(i).getString("time");
                    String location = task.getResult().getDocuments().get(i).getString("location");
                    String description = task.getResult().getDocuments().get(i).getString("description");
                    String deadline = task.getResult().getDocuments().get(i).getString("deadline");
                    int capacity = task.getResult().getDocuments().get(i).getLong("capacity").intValue();
                    events.add(new Event(name, date, time, description, deadline, capacity, new Facility(location)));
                    adapter.notifyDataSetChanged();
                }
            }
        }).addOnCompleteListener( task -> {
            if (task.isSuccessful()) {
                if (events.isEmpty()) {
                    Log.d("Event", "No events found " + events.size());
                } else {
                    Log.d("Event", "Events found " + events.get(0).getName());
                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Open event details activity
                Event clickedEvent = events.get(position);

                String name = clickedEvent.getName();
                String date = clickedEvent.getDate();
                String time = clickedEvent.getTime();
                String location = clickedEvent.getFacility();
                String description = clickedEvent.getDescription();
                String deadline = clickedEvent.getDeadline();
                int capacity = clickedEvent.getCapacity();
                String eventId = clickedEvent.getId();

                Log.d("Event", "Event clicked: Name=" + name + ", Date=" + date + ", Time=" + time + ", Location=" + location + ", Description=" + description + ", Deadline=" + deadline + ", Capacity=" + capacity + ", EventId=" + eventId);

                // Start a new activity to display event details
                Intent intent = new Intent(AttendeeDashboard.this, EventDetails.class);
                intent.putExtra("name", name);
                intent.putExtra("date", date);
                intent.putExtra("time", time);
                intent.putExtra("location", location);
                intent.putExtra("description", description);
                intent.putExtra("deadline", deadline);
                intent.putExtra("capacity", capacity);
                intent.putExtra("eventId", eventId);
                clickedEvent.getUrl(url -> {
                    intent.putExtra("posterUrl", url);
                });
                startActivity(intent);
            }
        });
    }


}