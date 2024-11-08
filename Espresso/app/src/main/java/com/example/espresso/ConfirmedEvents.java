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
/**
 * A Fragment that displays a list of confirmed events from the Firebase Firestore database.
 * This fragment retrieves events from the "users" collection, where each user's events are stored.
 * It filters the events by status and displays only those marked as "confirmed".
 *
 * Users can click on an event to view its detailed information in a new activity.
 */
public class ConfirmedEvents extends Fragment {

    /**
     * Called when the fragment's view is being created.
     * It inflates the layout for the fragment, sets up the list view, and retrieves the confirmed events from Firestore.
     *
     * @param inflater The LayoutInflater used to inflate the view.
     * @param container The container that holds the fragment's view (may be null).
     * @param savedInstanceState The saved instance state (if available).
     * @return The root view of the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the fragment's layout
        View rootView = inflater.inflate(R.layout.fragment_confirmed_events, container, false);

        // Initialize the events list and adapter
        List<Event> events = new ArrayList<>();
        EventAdapter adapter = new EventAdapter(getContext(), events);

        // Fetch confirmed events from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(new User(requireContext()).getDeviceID())  // Get the device ID of the user
                .collection("events")
                .whereEqualTo("status", "confirmed")  // Filter events by status
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Loop through the fetched documents and add them to the events list
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("Event", "Event found: " + document.getId());

                            // Retrieve event data from Firestore
                            Map<String, Object> data = document.getData();
                            String name = (String) data.get("name");
                            String date = (String) data.get("date");
                            String time = (String) data.get("time");
                            String location = (String) data.get("location");
                            String description = (String) data.get("description");
                            String deadline = (String) data.get("deadline");
                            Object capacityObj = data.get("capacity");
                            int capacity = (capacityObj instanceof Number) ? ((Number) capacityObj).intValue() : 0;

                            // Add event to the list
                            events.add(new Event(name, date, time, description, deadline, capacity, new Facility(location)));

                            // Notify the adapter that the data has changed
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.d("Event", "Error getting documents: ", task.getException());
                    }
                });

        // Set up the ListView and its item click listener
        ListView listView = rootView.findViewById(R.id.event_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            // When an event is clicked, retrieve its details
            Event clickedEvent = events.get(position);
            String name = clickedEvent.getName();
            String date = clickedEvent.getDate();
            String time = clickedEvent.getTime();
            String location = clickedEvent.getFacility();
            String description = clickedEvent.getDescription();
            String deadline = clickedEvent.getDeadline();
            int capacity = clickedEvent.getCapacity();
            String eventId = clickedEvent.getId();

            Log.d("Event", "Event clicked: Name=" + name + ", Date=" + date + ", Time=" + time +
                    ", Location=" + location + ", Description=" + description + ", Deadline=" + deadline +
                    ", Capacity=" + capacity + ", EventId=" + eventId);

            // Start EventDetails activity to show event details
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

            // Fetch event poster URL and add it to the intent
            clickedEvent.getUrl(url -> intent.putExtra("posterUrl", url));

            // Start the EventDetails activity
            startActivity(intent);
        });

        return rootView;
    }
}
