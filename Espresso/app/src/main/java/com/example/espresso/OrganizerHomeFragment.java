package com.example.espresso;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrganizerHomeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        View view = inflater.inflate(R.layout.fragment_organizer_home, container, false);

        List<Event> events = new ArrayList<>();
        ListView listView = view.findViewById(R.id.event_list_view);
        EventAdapter adapter = new EventAdapter(requireActivity(), events);
        listView.setAdapter(adapter);

        String deviceID = new User(requireActivity()).getDeviceID();

        db.collection("events")
                .whereEqualTo("organizer", deviceID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String date = document.getString("date");
                            String time = document.getString("time");
                            String location = document.getString("location");
                            String description = document.getString("description");
                            String deadline = document.getString("deadline");
                            int capacity = Objects.requireNonNull(document.getLong("capacity")).intValue();
                            events.add(new Event(name, date, time, description, deadline, capacity, new Facility(location)));
                        }
                        adapter.notifyDataSetChanged();

                        if (events.isEmpty()) Log.d("Event", "No events found.");
                        else Log.d("Event", "Events found: " + events.size());

                    } else Log.d("Event", "Error getting events: ", task.getException());
                });

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Event clickedEvent = events.get(position);

            String name = clickedEvent.getName();
            String date = clickedEvent.getDate();
            String time = clickedEvent.getTime();
            String location = clickedEvent.getFacility();
            String description = clickedEvent.getDescription();
            String deadline = clickedEvent.getDeadline();

            int capacity = clickedEvent.getCapacity();
            String eventId = clickedEvent.getId();

            Log.d("Event",
                    "Event clicked: Name=" + name +
                            ", Date=" + date +
                            ", Time=" + time +
                            ", Location=" + location +
                            ", Description=" + description +
                            ", Deadline=" + deadline +
                            ", Capacity=" + capacity +
                            ", EventId=" + eventId);

            Intent intent = new Intent(requireActivity(), EventDetails.class);

            intent.putExtra("name", name);
            intent.putExtra("date", date);
            intent.putExtra("time", time);
            intent.putExtra("location", location);
            intent.putExtra("description", description);
            intent.putExtra("deadline", deadline);
            intent.putExtra("capacity", capacity);
            intent.putExtra("eventId", eventId);
            intent.putExtra("status", "edit");

            startActivity(intent);

        }); return view;
    }
}