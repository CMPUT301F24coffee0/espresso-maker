package com.example.espresso;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfirmedEvents extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_confirmed_events, container, false);
        List<Event> events = new ArrayList<>();
        EventAdapter adapter = new EventAdapter(requireContext(),events);
        // Fetch confirmed events from the database and add them to the list
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(new User(requireContext()).getDeviceID()).collection("events").whereEqualTo("status", "confirmed").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Loop through the documents in the collection
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d("Event", "Event found: " + document.getId());
                    // Get the data from the document
                    Map<String, Object> data = document.getData();
                    String name = (String) data.get("name");
                    String date = (String) data.get("date");
                    String time = (String) data.get("time");
                    String location = (String) data.get("location");
                    String description = (String) data.get("description");
                    String deadline = (String) data.get("deadline");
                    Object capacityObj = data.get("capacity");
                    int capacity = (capacityObj instanceof Number) ? ((Number) capacityObj).intValue() : 0;
                    events.add(new Event(name, date, time, description, deadline, capacity, new Facility(location)));
                }
                adapter.notifyDataSetChanged();
            } else {
                Log.d("Event", "Error getting documents: ", task.getException());
            }
        });


        ListView listView = rootView.findViewById(R.id.event_list_view);
        listView.setAdapter(adapter);
        // Item onClick listener
        listView.setOnItemClickListener((parent, view, position, id) -> {
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
            Intent intent = new Intent(requireActivity(), EventDetails.class);
            intent.putExtra("name", name);
            intent.putExtra("date", date);
            intent.putExtra("time", time);
            intent.putExtra("location", location);
            intent.putExtra("description", description);
            intent.putExtra("deadline", deadline);
            intent.putExtra("capacity", capacity);
            intent.putExtra("eventId", eventId);
            intent.putExtra("status", "confirmed");
            clickedEvent.getUrl(url -> intent.putExtra("posterUrl", url));
            startActivity(intent);
        });
        return rootView;
    }

}