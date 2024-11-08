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
 * PendingEvents is a fragment that displays a list of events with a status of "pending".
 * It fetches events from Firebase Firestore, populates them into a ListView, and allows users
 * to click on an event to view its details.
 */
public class PendingEvents extends Fragment {

    /**
     * Called to create the fragment's view hierarchy.
     * This method inflates the layout for the fragment, fetches events from Firestore, and sets up
     * the ListView with event data.
     *
     * @param inflater The LayoutInflater object to inflate views in the fragment.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState A Bundle containing the fragment's previously saved state.
     * @return The View for the fragment's UI, or null if not ready.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_pending_events, container, false);

        // Create a list to hold the events and set up the adapter
        List<Event> events = new ArrayList<>();
        EventAdapter adapter = new EventAdapter(getContext(), events);

        // Get the Firestore instance and fetch events with the status 'pending'
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(new User(requireContext()).getDeviceID())  // Get the user's device ID
                .collection("events")
                .whereEqualTo("status", "pending")  // Filter events by the 'pending' status
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Loop through the result documents and extract event data
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("Event", "Event found: " + document.getId());
                            Map<String, Object> data = document.getData();
                            String name = (String) data.get("name");
                            String date = (String) data.get("date");
                            String time = (String) data.get("time");
                            String location = (String) data.get("location");
                            String description = (String) data.get("description");
                            String deadline = (String) data.get("deadline");
                            Object capacityObj = data.get("capacity");
                            int capacity = (capacityObj instanceof Number) ? ((Number) capacityObj).intValue() : 0;

                            // Add the event to the list
                            events.add(new Event(name, date, time, description, deadline, capacity, new Facility(location)));

                            // Notify the adapter that the dataset has changed
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.d("Event", "Error getting documents: ", task.getException());
                    }
                });

        // Find the ListView in the layout and set the adapter
        ListView listView = rootView.findViewById(R.id.event_list_view);
        listView.setAdapter(adapter);

        // Set an item click listener for the list items
        listView.setOnItemClickListener((parent, view, position, id) -> {
            // Get the clicked event
            Event clickedEvent = events.get(position);

            // Log the event details
            Log.d("Event", "Event clicked: Name=" + clickedEvent.getName() +
                    ", Date=" + clickedEvent.getDate() + ", Time=" + clickedEvent.getTime() +
                    ", Location=" + clickedEvent.getFacility() + ", Description=" +
                    clickedEvent.getDescription() + ", Deadline=" + clickedEvent.getDeadline() +
                    ", Capacity=" + clickedEvent.getCapacity() + ", EventId=" + clickedEvent.getId());

            // Create an intent to open the EventDetails activity
            Intent intent = new Intent(requireActivity(), EventDetails.class);
            intent.putExtra("name", clickedEvent.getName());
            intent.putExtra("date", clickedEvent.getDate());
            intent.putExtra("time", clickedEvent.getTime());
            intent.putExtra("location", clickedEvent.getFacility());
            intent.putExtra("description", clickedEvent.getDescription());
            intent.putExtra("deadline", clickedEvent.getDeadline());
            intent.putExtra("capacity", clickedEvent.getCapacity());
            intent.putExtra("eventId", clickedEvent.getId());
            intent.putExtra("status", "pending");

            // Get the event's poster URL asynchronously and pass it to the intent
            clickedEvent.getUrl(url -> intent.putExtra("posterUrl", url));

            // Start the EventDetails activity
            startActivity(intent);
        });

        return rootView;  // Return the inflated root view
    }
}
