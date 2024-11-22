package com.example.espresso.Organizer;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.espresso.Attendee.User;
import com.example.espresso.Event.Event;
import com.example.espresso.Event.EventAdapter;
import com.example.espresso.Event.EventDetails;
import com.example.espresso.R;
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
                            if (status == null) status = "edit";
                            boolean drawed = Boolean.TRUE.equals(document.getBoolean("drawed"));
                            int capacity = Objects.requireNonNull(document.getLong("capacity")).intValue();
                            events.add(new Event(name, date, time, description, deadline, capacity, new Facility(location), drawed, status));
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
            boolean drawed = clickedEvent.getDrawed();
            String status = clickedEvent.getStatus();

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
                            ", Drawed=" + drawed);

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
            intent.putExtra("drawed", drawed);
            startActivity(intent);

        });

        listView.setOnItemLongClickListener((parent, view1, position, id) -> {
            Event clickedEvent = events.get(position);
            String eventId = clickedEvent.getId();

            db.collection("events")
                    .document(eventId)
                    .collection("participants")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<String> participants = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                String participantName = document.getString("name");
                                if (participantName != null) {
                                    participants.add(participantName);
                                } else {
                                    participants.add(document.getId()); // Use document ID if name is missing
                                }
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                            builder.setTitle("Participants");

                            if (participants.isEmpty()) {
                                builder.setMessage("No participants yet.");
                            } else {
                                StringBuilder participantList = new StringBuilder();
                                for (String participant : participants) {
                                    participantList.append(participant).append("\n");
                                }
                                builder.setMessage(participantList.toString());
                            }

                            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                            builder.show();
                        } else {
                            Log.d("Participants", "Error getting participants: ", task.getException());
                        }
                    });

            return true;
        });

        return view;
    }
}