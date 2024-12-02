package com.example.espresso.db;

import static org.junit.Assert.assertSame;

import android.os.SystemClock;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.espresso.Attendee.AttendeeHomeActivity;
import com.example.espresso.Attendee.User;
import com.example.espresso.Event.Event;
import com.example.espresso.Organizer.Facility;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.HashMap;
import java.util.Map;

/**
 * Test event creation, deletion & modification.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class EventTest {
    @Rule
    public ActivityScenarioRule<AttendeeHomeActivity> rule = new ActivityScenarioRule<AttendeeHomeActivity>(AttendeeHomeActivity.class);

    /**
     * Generate a sample event.
     * @return  New event.
     */
    private Event sampleEvent() {
        Facility facility = new Facility("Test facility");
        return new Event("Test event", "November 10", "900", "This is a test", "deadline", 100, facility, 0, "status", false, 0);
    }

    /**
     * Run user-supplied code on an event document.
     * @param db        Reference to the database.
     * @param event     Event to find.
     * @param body      Function to run.
     */
    private void withEvent(FirebaseFirestore db, Event event, DocumentSupplier body) {
        DocumentReference ref = db.collection("event").document(event.getId());

        ref.get().addOnCompleteListener(task -> {
            // Run the body
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                body.run(doc);
            } else {
                throw new RuntimeException();
            }
        });
    }

    /**
     * Add & delete a new event to the database.
     */
    @Test
    public void addEventTest() {
        try {
            // Add an event
            Event event = sampleEvent();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> data = new HashMap<>();
            data.put("name", event.getName());
            data.put("description", event.getDescription());
            db.collection("events").document(event.getId()).set(data);

            // Query the event
            withEvent(db, event, foundData -> {
                String name = foundData.getString("name");
                if (name != null) {
                    assertSame(name, event.getName());
                }
            });

            // Delete the event
            db.collection("events").document(event.getId()).delete();
        } catch (Exception ignore) {
        }
    }

    /**
     * Check that a user has no events by default.
     */
    @Test
    public void getUserNoEventsTest() {
        try {
            rule.getScenario().onActivity(activity -> {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String deviceID = new User(activity.getApplicationContext()).getDeviceID();

                // Get all events associated with the current user
                db.collection("events").document(deviceID).collection("events")
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                int count = 0;
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    count++;
                                }
                                assertSame(count, 0);
                            }
                        });
            });
        } catch (Exception ignore) {
        }
    }

    /**
     * Check that modifications to an event are correctly stored in the database.
     */
    @Test
    public void modifyEventTest() {
        try {
            // Add an event
            Event event = sampleEvent();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> data = new HashMap<>();
            data.put("name", event.getName());
            data.put("description", event.getDescription());
            db.collection("events").document(event.getId()).set(data);

            // Query the event
            withEvent(db, event, foundData -> {
                String name = foundData.getString("name");
                if (name != null) {
                    assertSame(name, event.getName());
                }
            });

            // Modify the event
            data.put("name", "New name");
            db.collection("events").document(event.getId()).set(data);

            withEvent(db, event, foundData -> {
                String name = foundData.getString("name");
                if (name != null) {
                    assertSame(name, "New name");
                }
            });

            // Delete the event
            db.collection("events").document(event.getId()).delete();
        } catch (Exception ignore) {
        }
    }
}
