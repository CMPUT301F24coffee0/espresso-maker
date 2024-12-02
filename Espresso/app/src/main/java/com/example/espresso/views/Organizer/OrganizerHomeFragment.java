package com.example.espresso.views.Organizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.espresso.models.Attendee.User;
import com.example.espresso.models.Event;
import com.example.espresso.controllers.EventAdapter;
import com.example.espresso.views.EventDetails;
import com.example.espresso.R;
import com.example.espresso.models.Organizer.Facility;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * This fragment displays a list of events organized by the user (organizer).
 * The fragment retrieves event data from Firebase Firestore and populates the list view with the events.
 * When an event is clicked, the user is directed to a form to either edit or view the event details.
 */
public class OrganizerHomeFragment extends Fragment {
    /**
     * Called to inflate the fragment's layout and set up the list of events.
     * This method retrieves event data from Firestore, populates a list view with the events,
     * and sets up an item click listener to allow event editing.
     *
     * @param inflater The LayoutInflater object to inflate the fragment's view.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
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
                .get(Source.SERVER)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String date = document.getString("date");
                            String time = document.getString("time");
                            String location = document.getString("location");
                            String description = document.getString("description");
                            String deadline = document.getString("deadline");
                            String status = document.getString("status") ;

                            int sample = Objects.requireNonNull(document.getLong("sample")).intValue();
                            boolean geolocation = Boolean.TRUE.equals(document.get("geolocation"));

                            if (status == null) status = "edit";
                            int drawn = Objects.requireNonNull(document.getLong("drawn")).intValue();
                            int capacity = Objects.requireNonNull(document.getLong("capacity")).intValue();
                            events.add(new Event(name, date, time, description, deadline, capacity, new Facility(location), drawn, status, geolocation, sample));

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
            int drawn = clickedEvent.getDrawn();
            String status = clickedEvent.getStatus();
            int sample = clickedEvent.getSample();

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
                            ", EventId=" + eventId +
                            ", Status=" + status +
                            ", Drawn=" + drawn +
                            ", Sample=" + sample);

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
            intent.putExtra("drawn", drawn);
            intent.putExtra("sample", sample);
            startActivity(intent);

        }); return view;
    }
}