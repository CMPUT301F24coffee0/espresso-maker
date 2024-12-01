package com.example.espresso.Attendee;

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

import com.example.espresso.Event.Event;
import com.example.espresso.Event.EventAdapter;
import com.example.espresso.Event.EventDetails;
import com.example.espresso.Organizer.Facility;
import com.example.espresso.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * A Fragment that displays a list of confirmed events fetched from Firebase Firestore.
 * It retrieves events where the status is "confirmed" and displays them in a ListView.
 * Clicking on an event opens a detailed view of the event.
 */
public class ConfirmedEvents extends Fragment {

    /**
     * Inflates the layout and fetches confirmed events from Firebase Firestore.
     * It also sets up the ListView to display the events and handle item clicks.
     *
     * @param inflater The LayoutInflater object to inflate the fragment's view.
     * @param container The parent view group that the fragment's UI should be attached to.
     * @param savedInstanceState Any previously saved state of the fragment.
     * @return The root view for the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_confirmed_events, container, false);
        List<Event> events = new ArrayList<>();
        EventAdapter adapter = new EventAdapter(requireContext(), events);

        // Fetch confirmed events from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(new User(requireContext()).getDeviceID())
                .collection("events")
                .whereEqualTo("status", "confirmed")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Loop through the documents and add events to the list
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("Event", "Event found: " + document.getId());
                            // Extract event details from Firestore
                            Map<String, Object> data = document.getData();
                            String name = (String) data.get("name");
                            String date = (String) data.get("date");
                            String time = (String) data.get("time");
                            String location = (String) data.get("location");
                            String description = (String) data.get("description");
                            String deadline = (String) data.get("deadline");
                            Object capacityObj = data.get("capacity");
                            boolean geolocation = Boolean.TRUE.equals(data.get("geolocation"));
                            int capacity = (capacityObj instanceof Number) ? ((Number) capacityObj).intValue() : 0;

                            Object sampleObj = data.get("sample");
                            int sample = (sampleObj instanceof Number) ? ((Number) sampleObj).intValue() : 0;

                            Object drawnObj = data.get("drawn");
                            int drawn = (drawnObj instanceof Number) ? ((Number) drawnObj).intValue() : 0;
                            events.add(new Event(name, date, time, description, deadline, capacity, new Facility(location), drawn, "confirmed", geolocation, sample));

                        }
                        adapter.notifyDataSetChanged();
                    } else Log.d("Event", "Error getting documents: ", task.getException());
                });

        // Set up the ListView to display events
        ListView listView = rootView.findViewById(R.id.event_list_view);
        listView.setAdapter(adapter);

        // Item click listener for opening event details
        listView.setOnItemClickListener((parent, view, position, id) -> {
            // Get the clicked event and start the EventDetails activity
            Event clickedEvent = events.get(position);
            String name = clickedEvent.getName();
            String date = clickedEvent.getDate();
            String time = clickedEvent.getTime();
            String location = clickedEvent.getFacility();
            String description = clickedEvent.getDescription();
            String deadline = clickedEvent.getDeadline();
            int capacity = clickedEvent.getCapacity();
            String eventId = clickedEvent.getId();
            int sample = clickedEvent.getSample();

            Log.d("Event", "Event clicked: Name=" + name + ", Date=" + date + ", Time=" + time + ", Location=" + location + ", Description=" + description + ", Deadline=" + deadline + ", Capacity=" + capacity + ", eventId=" + eventId);

            // Start EventDetails activity with event information
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
            intent.putExtra("sample", sample);

            // Fetch and add the event poster URL to the intent
            clickedEvent.getUrl(url -> intent.putExtra("posterUrl", url));
            startActivity(intent);
        });

        return rootView;
    }
}
