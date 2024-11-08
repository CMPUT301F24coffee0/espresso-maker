package com.example.espresso;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.espresso.databinding.ActivityAttendeeHomeBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * The AttendeeHomeActivity class is responsible for displaying the attendee home screen,
 * where users can view a list of events, scan QR codes, and access their profiles.
 * It interacts with Firebase Firestore to fetch event data and uses a BottomNavigationView
 * for navigation to different activities.
 */
public class AttendeeHomeActivity extends AppCompatActivity {

    /** Binding object for ActivityAttendeeHome layout. */
    ActivityAttendeeHomeBinding binding;

    /**
     * Called when the activity is first created. Sets up the view, initializes Firebase Firestore,
     * sets up the BottomNavigationView listener, and loads event data.
     *
     * @param savedInstanceState If the activity is being re-initialized, this contains the most recent data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttendeeHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        setupBottomNavigationView();
        setupEventList(db);
        db.collection("users").document(new User(this).getDeviceID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userName = task.getResult().getString("name");
                ((TextView) findViewById(R.id.name_title)).setText("Welcome " + userName + "!");
            } else {
                // Handle failure (e.g., user not found or error fetching data)
                Log.d("User", "Error retrieving user data: ", task.getException());
            }
        });
        
    }

    /**
     * Sets up the BottomNavigationView listener to handle navigation to different activities
     * based on the selected item.
     */
    private void setupBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.events) {
                navigateToActivity(AttendeeMyEvent.class, "Events clicked");
            } else if (item.getItemId() == R.id.scan) {
                navigateToActivity(ScanQR.class, "Scan clicked");
            } else if (item.getItemId() == R.id.profile) {
                navigateToActivity(AttendeeProfile.class, "Profile clicked");
            }
            return true;
        });
    }

    /**
     * Navigates to a specified activity and logs a message.
     *
     * @param activityClass The class of the activity to navigate to.
     * @param logMessage    The message to log.
     */
    private void navigateToActivity(Class<?> activityClass, String logMessage) {
        Log.d("BottomNav", logMessage);
        Intent intent = new Intent(AttendeeHomeActivity.this, activityClass);
        startActivity(intent);
    }

    /**
     * Sets up the event list by fetching events from Firestore and adding them to a ListView.
     *
     * @param db The FirebaseFirestore instance used to retrieve events.
     */
    private void setupEventList(FirebaseFirestore db) {
        List<Event> events = new ArrayList<>();
        ListView listView = findViewById(R.id.event_list_view);
        EventAdapter adapter = new EventAdapter(this, events);
        listView.setAdapter(adapter);

        db.collection("events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Event", "Events found");
                for (int i = 0; i < task.getResult().size(); i++) {
                    String eventId = task.getResult().getDocuments().get(i).getId();
                    String deviceID = new User(this).getDeviceID();

                    int finalI = i;
                    db.collection("events")
                            .document(eventId).collection("participants")
                            .document(deviceID).get().addOnCompleteListener(task1 -> {

                                if (task1.isSuccessful() && !task1.getResult().exists()) {
                                    Log.d("Event", "Event not joined: " + eventId);

                                    String name = task.getResult().getDocuments().get(finalI).getString("name");
                                    String date = task.getResult().getDocuments().get(finalI).getString("date");
                                    String time = task.getResult().getDocuments().get(finalI).getString("time");
                                    String location = task.getResult().getDocuments().get(finalI).getString("location");
                                    String description = task.getResult().getDocuments().get(finalI).getString("description");
                                    String deadline = task.getResult().getDocuments().get(finalI).getString("deadline");

                                    int capacity = Objects.requireNonNull(
                                            task.getResult().getDocuments().get(finalI).getLong("capacity")).intValue();

                                    events.add(new Event(name, date, time, description, deadline, capacity, new Facility(location)));
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Log.d("Event", "Event already joined: " + eventId);
                                }
                            });
                }
                processEvents(task, events, adapter, db);
            } else {
                Log.d("Event", "Error getting events: ", task.getException());
            }
        }).addOnCompleteListener(task -> logEventsLoadedStatus(events));

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
        for (int i = 0; i < task.getResult().size(); i++) {
            String eventId = task.getResult().getDocuments().get(i).getId();
            String deviceID = new User(this).getDeviceID();

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

        events.add(new Event(name, date, time, description, deadline, capacity, new Facility(location)));
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
        Intent intent = new Intent(AttendeeHomeActivity.this, EventDetails.class);
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
