package com.example.espresso.Admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.espresso.Attendee.User;
import com.example.espresso.Event.Event;
import com.example.espresso.Event.EventAdapter;
import com.example.espresso.Event.EventDetails;
import com.example.espresso.Organizer.Facility;
import com.example.espresso.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Used to display the Home page for Admins. This page includes a ListView containing all events on the App.
 */
public class HomeFragment extends Fragment {
    View view;
    private List<Event> events;  // Declare the events list here.
    private EventAdapter adapter; // Declare the adapter here.

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    @SuppressLint("SetTextI18n")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_home, container, false);

        // Log to confirm the view is inflated
        if (view == null) {
            Log.e("HomeFragment", "View is null after inflating fragment_home layout.");
        } else {
            Log.d("HomeFragment", "View successfully inflated.");
        }

        // Initialize the events list and adapter
        events = new ArrayList<>();
        adapter = new EventAdapter(requireContext(), events);

        ListView listView = view.findViewById(R.id.event_list_view);

        // Log to confirm ListView is found
        if (listView == null) {
            Log.e("HomeFragment", "ListView with ID event_list_view not found.");
        } else {
            Log.d("HomeFragment", "ListView successfully found.");
            listView.setAdapter(adapter);  // Only set adapter if ListView is found
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(new User(requireContext()).getDeviceID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                TextView pageNameTextView = view.findViewById(R.id.page_name);
                if (pageNameTextView != null) {
                    pageNameTextView.setText("Welcome Admin!");
                } else {
                    Log.e("HomeFragment", "TextView with ID page_name not found");
                }
            } else {
                Log.d("User", "Error retrieving user data: ", task.getException());
            }
        });

        setupEventList(db);  // Load events
        return view;
    }

    /**
     * Sets up the event list by fetching events from Firestore and adding them to a ListView.
     *
     * @param db The FirebaseFirestore instance used to retrieve events.
     */
    private void setupEventList(FirebaseFirestore db) {
        db.collection("events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Event", "Events found");
                processEvents(task, adapter, db);  // Process events and populate the adapter
            } else {
                Log.d("Event", "Error getting events: ", task.getException());
            }
        });

        ListView listView = view.findViewById(R.id.event_list_view);
        if (listView != null) {
            setupEventItemClickListener(listView, events, db);
        } else {
            Log.e("HomeFragment", "ListView with ID event_list_view not found.");
        }
    }

    /**
     * Processes the retrieved events and adds them to the list for admin to view and delete.
     *
     * @param task     The Firestore task result containing the events.
     * @param adapter  The EventAdapter to notify of changes.
     * @param db       The FirebaseFirestore instance for event deletion.
     */
    private void processEvents(Task<QuerySnapshot> task, EventAdapter adapter, FirebaseFirestore db) {
        events.clear();  // Clear the list to prevent duplicates

        for (int i = 0; i < task.getResult().size(); i++) {
            DocumentSnapshot doc = task.getResult().getDocuments().get(i);
            String name = doc.getString("name");
            String date = doc.getString("date");
            String time = doc.getString("time");
            String location = doc.getString("location");
            String description = doc.getString("description");
            String deadline = doc.getString("deadline");
            String status = doc.getString("status");

            int capacity = Objects.requireNonNull(doc.getLong("capacity")).intValue();
            int sample = Objects.requireNonNull(doc.getLong("sample")).intValue();

            int drawn = Objects.requireNonNull(doc.getLong("drawn")).intValue();
            boolean geolocation = Boolean.TRUE.equals(doc.getBoolean("geolocation"));

            events.add(new Event(
                    name,
                    date,
                    time,
                    description,
                    status,
                    capacity,
                    new Facility(location),
                    drawn,
                    deadline, geolocation, sample
            ));
        }
        adapter.notifyDataSetChanged();  // Notify the adapter to refresh the ListView
    }

    /**
     * Sets up the item click listener for the event ListView to open the event details or delete an event.
     *
     * @param listView The ListView displaying the events.
     * @param events   The list of events to retrieve the clicked event from.
     * @param db       The FirebaseFirestore instance to delete events from Firestore.
     */
    private void setupEventItemClickListener(ListView listView, List<Event> events, FirebaseFirestore db) {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Event clickedEvent = events.get(position);
            openEventDetails(clickedEvent);
        });
    }

    /**
     * Opens the EventDetails activity for a clicked event, passing relevant event information.
     *
     * @param event The Event object containing details to display.
     */
    private void openEventDetails(Event event) {
        Intent intent = new Intent(requireActivity(), EventDetails.class);
        intent.putExtra("name", event.getName());
        intent.putExtra("date", event.getDate());
        intent.putExtra("time", event.getTime());
        intent.putExtra("location", event.getFacility());
        intent.putExtra("description", event.getDescription());
        intent.putExtra("deadline", event.getDeadline());
        intent.putExtra("capacity", event.getCapacity());
        intent.putExtra("eventId", event.getId());
        intent.putExtra("status", "admin");
        intent.putExtra("geo", event.getGeolocation());
        intent.putExtra("sample", event.getSample());
        event.getUrl(url -> {
            intent.putExtra("posterUrl", url);
            startActivity(intent);
        });
    }

    /**
     * Loads events from Firestore and updates the events list.
     */
    private void loadEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                events.clear();
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    String name = document.getString("name");
                    String date = document.getString("date");
                    String time = document.getString("time");
                    String location = document.getString("location");
                    String description = document.getString("description");
                    String deadline = document.getString("deadline");
                    int capacity = Objects.requireNonNull(document.getLong("capacity")).intValue();

                    int drawn = Objects.requireNonNull(document.getLong("drawn")).intValue();
                    boolean geolocation = Boolean.TRUE.equals(document.getBoolean("geolocation"));

                    int sample = Objects.requireNonNull(document.getLong("sample")).intValue();

                    events.add(new Event(
                            document.getId(),
                            name,
                            date,
                            time,
                            description,
                            capacity,
                            new Facility(location),
                            drawn,
                            deadline,
                            geolocation, sample
                    ));
                }
                adapter.notifyDataSetChanged();  // Refresh the adapter
            } else {
                Toast.makeText(requireContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
                Log.e("AdminHomeFragment", "Error loading events", task.getException());
            }
        });
    }
}
