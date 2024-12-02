package com.example.espresso.db;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.espresso.Attendee.User;
import com.example.espresso.MainActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

/**
 * Test the creation & querying of facilities.
 */
@RunWith(AndroidJUnit4.class)
public class FacilityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * Run user-supplied code on a the current users document.
     * @param activity  Activity to query from.
     * @param body      Function to run.
     */
    private void withUser(MainActivity activity, DocumentSupplier body) {
        String deviceID = new User(activity).getDeviceID();
        DocumentReference ref = activity.db.collection("users").document(deviceID);

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
     * Delete the current user
     * @param activity  Activity to query form.
     */
    private void deleteUser(MainActivity activity) {
        String deviceID = new User(activity).getDeviceID();
        DocumentReference ref = activity.db.collection("users").document(deviceID);
        ref.delete();
    }

    /**
     * Check that user's don't have a facility by default.
     */
    @Test
    public void noFacilityAtStartTest() {
        rule.getScenario().onActivity(activity -> {
            // Create a new user
            activity.createNewUserProfile("Attendee");

            // By default, the user should have no facility
            withUser(activity, data -> {
                assertNull(data.getString("facility"));
            });

            // Remove the user
            deleteUser(activity);
        });
    }

    /**
     * Add a facility to a user & check that the database is updated accordingly.
     */
    @Test
    public void addFacilityTest() {
        rule.getScenario().onActivity(activity -> {
            // Create a new user
            activity.createNewUserProfile("Attendee");

            // Get the user document
            String deviceID = new User(activity).getDeviceID();
            DocumentReference ref = activity.db.collection("users")
                    .document(deviceID);

            // Add a new facility
            Map<String, Object> update = new HashMap<>();
            String newFacility = "New facility";
            update.put("facility", newFacility);
            ref.update(update);

            // Check that there is a new facility
            withUser(activity, data -> {
                String facility = data.getString("facility");
                if (facility != null) {
                    assertSame(facility, newFacility);
                }
            });

            // Remove the user
            deleteUser(activity);
        });
    }
}
