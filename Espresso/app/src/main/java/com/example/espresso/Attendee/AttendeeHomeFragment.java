package com.example.espresso.Attendee;

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

public class AttendeeHomeFragment extends Fragment {
    View view;
    public AttendeeHomeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_home_attendee, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(new User(requireContext()).getDeviceID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ((TextView) view.findViewById(R.id.page_name)).setText(String.format("Welcome!"));
            } else {
                // Handle failure (e.g., user not found or error fetching data)
                Log.d("User", "Error retrieving user data: ", task.getException());
            }
        });

        setupEventList(db);
        db.collection("users").document(new User(requireContext()).getDeviceID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //Toast.makeText(requireContext(), "Welcome " + userName + "!", Toast.LENGTH_SHORT).show();
                ((TextView) view.findViewById(R.id.page_name)).setText(String.format("Welcome!"));
            } else {
                // Handle failure (e.g., user not found or error fetching data)
                Log.d("User", "Error retrieving user data: ", task.getException());
            }
        });

        return view;
    }

    /**
     * Sets up the event list by fetching events from Firestore and adding them to a ListView.
     *
     * @param db The FirebaseFirestore instance used to retrieve events.
     */
    private void setupEventList(FirebaseFirestore db) {
        List<Event> events = new ArrayList<>();
        ListView listView = view.findViewById(R.id.upc_events_list_view);
        EventAdapter adapter = new EventAdapter(requireContext(), events);
        listView.setAdapter(adapter);

        db.collection("events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Event", "Events found");
                processEvents(task, events, adapter, db);  // Call processEvents to handle event data loading
            } else {
                Log.d("Event", "Error getting events: ", task.getException());
            }
        });

        setupEventItemClickListener(listView, events);
    }

    /**
     * Processes the retrieved events and adds them to the list if the user hasn't joined them.
     *
     * @param task     The Firestore task result containing the events.
     * @param events   The list of events to update.
     * @param adapter  The EventAdapter to notify of changes.
     * @param db       The FirebaseFirestore instance for participant data.
     */
    private void processEvents(Task<QuerySnapshot> task, List<Event> events, EventAdapter adapter, FirebaseFirestore db) {
        events.clear();  // Clear the list to prevent duplicates

        for (int i = 0; i < task.getResult().size(); i++) {
            String eventId = task.getResult().getDocuments().get(i).getId();
            String deviceID = new User(requireContext()).getDeviceID();

            int finalI = i;
            db.collection("events").document(eventId).collection("participants").document(deviceID).get()
                    .addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful() && !task1.getResult().exists()) {
                            addEventToList(task, events, finalI);
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("Event", "Event already joined: " + eventId);
                        }
                    });
        }
    }


    /**
     * Logs the status of event loading based on whether events were found.
     *
     * @param events The list of events loaded from Firestore.
     */
    private void logEventsLoadedStatus(List<Event> events) {
        if (events.isEmpty()) Log.d("Event", "No events found " + events.size());
        else Log.d("Event", "Events found " + events.get(0).getName());
    }

    /**
     * Adds an event to the events list using the document data at the specified index.
     *
     * @param task   The Firestore task result containing event documents.
     * @param events The list of events to update.
     * @param index  The index of the document to add.
     */
    private void addEventToList(Task<QuerySnapshot> task, List<Event> events, int index) {
        DocumentSnapshot doc = task.getResult().getDocuments().get(index);
        String name = doc.getString("name");
        String date = doc.getString("date");
        String time = doc.getString("time");
        String location = doc.getString("location");
        String description = doc.getString("description");
        String deadline = doc.getString("deadline");
        int capacity = Objects.requireNonNull(doc.getLong("capacity")).intValue();
        boolean drawed = Boolean.TRUE.equals(doc.getBoolean("drawed"));

         events.add(new Event(name, date, time, description, deadline, capacity, new Facility(location), drawed, "view"));
    }

    /**
     * Sets up the item click listener for the event ListView to open the event details activity.
     *
     * @param listView The ListView displaying the events.
     * @param events   The list of events to retrieve the clicked event from.
     */
    private void setupEventItemClickListener(ListView listView, List<Event> events) {
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
        intent.putExtra("status", "view");

        event.getUrl(url -> {
            intent.putExtra("posterUrl", url);
            startActivity(intent);
        });
    }

}