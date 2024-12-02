package com.example.espresso.views.Attendee;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.espresso.models.Attendee.User;
import com.example.espresso.models.Event;
import com.example.espresso.controllers.EventAdapter;
import com.example.espresso.views.EventDetails;
import com.example.espresso.models.Organizer.Facility;
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
            if (task.isSuccessful()) ((TextView) view.findViewById(R.id.page_name)).setText(String.format("Welcome!"));
            else Log.d("User", "Error retrieving user data: ", task.getException());});

        setupEventList(db);
        db.collection("users").document(new User(requireContext()).getDeviceID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) ((TextView) view.findViewById(R.id.page_name)).setText(String.format("Welcome!"));
            else Log.d("User", "Error retrieving user data: ", task.getException());});
        return view;
    }

    /**
     * Sets up the item click listener for the event ListView to open the event details activity.
     *
     * @param listView     The ListView displaying the events.
     * @param events       The list of events to retrieve the clicked event from.
     * @param disableQRList The list of disableQR flags for each event, indicating whether QR is disabled for the event.
     */
    private void setupEventItemClickListener(ListView listView, List<Event> events, List<Boolean> disableQRList) {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Event clickedEvent = events.get(position);
            boolean disableQR = disableQRList.get(position); // Get disableQR for this specific event
            openEventDetails(clickedEvent, disableQR); // Pass disableQR for the clicked event
        });
    }

    /**
     * Processes the retrieved events, checks if the user has joined them, and adds them to the list if not.
     * It also updates the disableQR list for each event.
     *
     * @param task          The Firestore task result containing the events.
     * @param events        The list of events to update.
     * @param adapter       The EventAdapter to notify of changes.
     * @param db            The FirebaseFirestore instance for participant data.
     * @param disableQRList The list of disableQR flags for each event, indicating whether QR is disabled for the event.
     */
    private void processEvents(Task<QuerySnapshot> task, List<Event> events, EventAdapter adapter, FirebaseFirestore db, List<Boolean> disableQRList) {
        events.clear(); // Clear the list to prevent duplicates
        disableQRList.clear(); // Clear the disableQR list to ensure alignment

        String deviceID = new User(requireContext()).getDeviceID();
        for (DocumentSnapshot doc : task.getResult()) {
            String eventId = doc.getId();

            db.collection("events").document(eventId).collection("participants").document(deviceID).get()
                    .addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful() && !task1.getResult().exists()) {
                            boolean disableQR = addEventToList(doc, events); // Process and get disableQR
                            disableQRList.add(disableQR); // Add disableQR to the list
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("Event", "Event already joined: " + eventId);
                        }
                    });
        }
    }

    /**
     * Sets up the event list by fetching events from Firestore, adding them to the event list, and
     * updating the UI with event data.
     *
     * @param db The FirebaseFirestore instance used to retrieve events.
     */
    private void setupEventList(FirebaseFirestore db) {
        List<Event> events = new ArrayList<>();
        List<Boolean> disableQRList = new ArrayList<>(); // To track disableQR for each event
        ListView listView = view.findViewById(R.id.upc_events_list_view);
        EventAdapter adapter = new EventAdapter(requireContext(), events);
        listView.setAdapter(adapter);

        db.collection("events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Event", "Events found");
                processEvents(task, events, adapter, db, disableQRList);
            } else {
                Log.d("Event", "Error getting events: ", task.getException());
            }
        });

        setupEventItemClickListener(listView, events, disableQRList);
    }

    /**
     * Adds an event to the events list using the document data. It also returns the disableQR flag
     * for the event, indicating whether QR code is disabled for this event.
     *
     * @param doc   The Firestore document containing event data.
     * @param events The list of events to update.
     * @return boolean The disableQR flag for the event.
     */
    private boolean addEventToList(DocumentSnapshot doc, List<Event> events) {
        String name = doc.getString("name");
        String date = doc.getString("date");
        String time = doc.getString("time");
        String location = doc.getString("location");
        String description = doc.getString("description");
        String deadline = doc.getString("deadline");

        int capacity = Objects.requireNonNull(doc.getLong("capacity")).intValue();
        int sample = Objects.requireNonNull(doc.getLong("sample")).intValue();
        int drawn = Objects.requireNonNull(doc.getLong("drawn")).intValue();
        boolean geolocation = Boolean.TRUE.equals(doc.getBoolean("geolocation"));
        boolean disableQR = Boolean.TRUE.equals(doc.getBoolean("disableQR"));

        events.add(new Event(name, date, time, description, deadline, capacity, new Facility(location), drawn, "view", geolocation, sample));
        return disableQR; // Return disableQR for this specific event
    }

    /**
     * Opens the EventDetails activity for a clicked event, passing relevant event information.
     *
     * @param event   The Event object containing details to display.
     * @param disableQR The disableQR flag indicating whether QR code is disabled for the event.
     */
    private void openEventDetails(Event event, boolean disableQR) {
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
        intent.putExtra("geo", event.getGeolocation());
        intent.putExtra("sample", event.getSample());
        intent.putExtra("disableQR", disableQR); // Pass disableQR for the clicked event

        event.getUrl(url -> {
            intent.putExtra("posterUrl", url);
            startActivity(intent);
        });
    }
}