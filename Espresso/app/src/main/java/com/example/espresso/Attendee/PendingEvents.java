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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
/**
 * This fragment displays a list of pending events for the organizer. It retrieves event data from Firestore,
 * filters it by "pending" status, and displays the events in a list. When an event is clicked, it opens a detailed
 * view of the event with more information.
 */
public class PendingEvents extends Fragment {
    /**
     * Called to inflate the fragment's layout and initialize UI components.
     * This method retrieves pending events from Firestore and displays them in a list.
     * When an event is clicked, it opens the event details in a new activity.
     *
     * @param inflater The LayoutInflater object to inflate the fragment's view.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the fragment layout
        View rootView = inflater.inflate(R.layout.fragment_pending_events, container, false);

        // List to hold events fetched from Firestore
        List<Event> events = new ArrayList<>();
        EventAdapter adapter = new EventAdapter(requireContext(), events);

        // Get Firestore instance and fetch events with 'pending' status
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(new User(requireContext()).getDeviceID())
                .collection("events")
                .whereIn("status", Arrays.asList("pending", "invited"))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Loop through the documents in the collection and add to events list
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("Event", "Event found: " + document.getId());
                            // Extract data from the document
                            Map<String, Object> data = document.getData();
                            String name = (String) data.get("name");
                            String date = (String) data.get("date");
                            String time = (String) data.get("time");
                            String location = (String) data.get("location");
                            String description = (String) data.get("description");
                            String deadline = (String) data.get("deadline");
                            Object capacityObj = data.get("capacity");
                            String status = (String) data.get("status");
                            boolean geolocation = Boolean.TRUE.equals(data.get("geolocation"));

                            int capacity = (capacityObj instanceof Number) ? ((Number) capacityObj).intValue() : 0;
                            events.add(new Event(name, date, time, description, deadline, capacity, new Facility(location), false, status, geolocation));
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.d("Event", "Error getting documents: ", task.getException());
                    }
                });

        // Set up the ListView and adapter to display events
        ListView listView = rootView.findViewById(R.id.event_list_view);
        listView.setAdapter(adapter);

        // Set item click listener to open event details
        listView.setOnItemClickListener((parent, view, position, id) -> {
            // Get clicked event data
            Event clickedEvent = events.get(position);
            String name = clickedEvent.getName();
            String date = clickedEvent.getDate();
            String time = clickedEvent.getTime();
            String location = clickedEvent.getFacility();
            String description = clickedEvent.getDescription();
            String deadline = clickedEvent.getDeadline();
            int capacity = clickedEvent.getCapacity();
            String eventId = clickedEvent.getId();
            String status = clickedEvent.getStatus();

            Log.d("Event", "Event clicked: Name=" + name + ", Date=" + date + ", Time=" + time + ", Location=" + location + ", Description=" + description + ", Deadline=" + deadline + ", Capacity=" + capacity + ", EventId=" + eventId);

            // Start the EventDetails activity to show detailed information
            Intent intent = new Intent(requireActivity(), EventDetails.class);
            intent.putExtra("name", name);
            intent.putExtra("date", date);
            intent.putExtra("time", time);
            intent.putExtra("location", location);
            intent.putExtra("description", description);
            intent.putExtra("deadline", deadline);
            intent.putExtra("capacity", capacity);
            intent.putExtra("eventId", eventId);
            intent.putExtra("status", status);
            // Fetch the event poster URL and pass it to the intent
            clickedEvent.getUrl(url -> intent.putExtra("posterUrl", url));
            startActivity(intent);
        });

        return rootView;
    }
}
