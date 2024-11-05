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

        // Navigation
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.events) {
                    // Open events activity
                    Log.d("BottomNav", "Events clicked");
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
        events.add(new Event("Event 1", "Date 1", "Time 1", "description 1", "deadline", 10, new Facility("hub")));
        events.add(new Event("Event 2", "Date 2", "Time 2", "description 2", "deadline", 10, new Facility("hub")));
        events.add(new Event("Event 3", "Date 3", "Time 3", "description 3", "deadline", 10, new Facility("hub")));
        events.add(new Event("Event 4", "Date 4", "Time 4", "description 4", "deadline", 10, new Facility("hub")));


        ListView listView = findViewById(R.id.event_list_view);
        EventAdapter adapter = new EventAdapter(this,events);
        listView.setAdapter(adapter);
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