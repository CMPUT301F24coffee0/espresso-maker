package com.example.espresso.Attendee;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private View view;

    public AttendeeHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_home_attendee, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Use getContext() with a null check to avoid IllegalStateException
        String deviceID = getContext() != null ? new User(getContext()).getDeviceID() : null;

        if (deviceID != null) {
            db.collection("users").document(deviceID).get().addOnCompleteListener(task -> {
                if (isAdded()) { // Ensure fragment is still attached
                    if (task.isSuccessful()) {
                        ((TextView) view.findViewById(R.id.page_name)).setText("Welcome!");
                    } else {
                        Log.d("User", "Error retrieving user data: ", task.getException());
                    }
                }
            });

            setupEventList(db, deviceID); // Pass deviceID to reuse it in other methods
        }
        return view;
    }

    private void setupEventList(FirebaseFirestore db, String deviceID) {
        List<Event> events = new ArrayList<>();
        List<Boolean> disableQRList = new ArrayList<>();
        ListView listView = view.findViewById(R.id.upc_events_list_view);

        EventAdapter adapter = new EventAdapter(requireContext(), events);
        listView.setAdapter(adapter);

        db.collection("events").get().addOnCompleteListener(task -> {
            if (isAdded() && task.isSuccessful()) { // Check if fragment is attached
                Log.d("Event", "Events found");
                processEvents(task, events, adapter, db, disableQRList, deviceID);
            } else if (task.getException() != null) {
                Log.d("Event", "Error getting events: ", task.getException());
            }
        });

        setupEventItemClickListener(listView, events, disableQRList);
    }

    private void processEvents(Task<QuerySnapshot> task, List<Event> events, EventAdapter adapter,
                               FirebaseFirestore db, List<Boolean> disableQRList, String deviceID) {
        events.clear();
        disableQRList.clear();

        for (DocumentSnapshot doc : task.getResult()) {
            String eventId = doc.getId();

            db.collection("events").document(eventId).collection("participants").document(deviceID).get()
                    .addOnCompleteListener(task1 -> {
                        if (isAdded() && task1.isSuccessful() && !task1.getResult().exists()) {
                            boolean disableQR = addEventToList(doc, events);
                            disableQRList.add(disableQR);
                            adapter.notifyDataSetChanged();
                        } else if (!isAdded()) {
                            Log.d("Event", "Fragment is not attached. Skipping event processing.");
                        } else {
                            Log.d("Event", "Event already joined: " + eventId);
                        }
                    });
        }
    }

    private void setupEventItemClickListener(ListView listView, List<Event> events, List<Boolean> disableQRList) {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Event clickedEvent = events.get(position);
            boolean disableQR = disableQRList.get(position); // Get disableQR for this specific event
            openEventDetails(clickedEvent, disableQR); // Pass disableQR for the clicked event
        });
    }

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
