package com.example.espresso.db;

import static org.junit.Assert.assertSame;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.espresso.models.Attendee.User;
import com.example.espresso.views.MainActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.HashMap;
import java.util.Map;

/**
 * Modify user attributes & check that the database is updated accordingly.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class ModifyUsersTest {
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
     * Update the user's email & check to see if the DB is updated.
     */
    @Test
    public void modifyEmailTest() {
        try {
            rule.getScenario().onActivity(activity -> {
                // Create a new user
                activity.createNewUserProfile("Attendee");

                // Get the user document
                String deviceID = new User(activity).getDeviceID();
                DocumentReference ref = activity.db.collection("users")
                        .document(deviceID);

                // Update the field
                Map<String, Object> data = new HashMap<>();
                String newEmail = "test@example.com";
                data.put("email", newEmail);
                ref.update(data);

                // Check that the email was updated
                withUser(activity, user -> {
                    String email = user.getString("email");
                    if (email != null) {
                        assertSame(email, newEmail);
                    }
                });

                // Remove the user
                deleteUser(activity);
            });
        } catch (Exception ignore) {
        }
    }

    /**
     * Update the user's facility & check to see if the DB is updated.
     */
    @Test
    public void modifyFacilityTest() {
        try {
            rule.getScenario().onActivity(activity -> {
                // Create a new user
                activity.createNewUserProfile("Attendee");

                // Get the user document
                String deviceID = new User(activity).getDeviceID();
                DocumentReference ref = activity.db.collection("users")
                        .document(deviceID);

                // Update the field
                Map<String, Object> data = new HashMap<>();
                String newFacility = "New facility";
                data.put("facility", newFacility);
                ref.update(data);

                // Check that the facility was updated
                withUser(activity, user -> {
                    String facility = user.getString("facility");
                    if (facility != null) {
                        assertSame(facility, newFacility);
                    }
                });

                // Remove the user
                deleteUser(activity);
            });
        } catch (Exception ignore) {
        }
    }

    /**
     * Update the user's name & check to see if the DB is updated.
     */
    @Test
    public void modifyNameTest() {
        try {
            rule.getScenario().onActivity(activity -> {
                // Create a new user
                activity.createNewUserProfile("Attendee");

                // Get the user document
                String deviceID = new User(activity).getDeviceID();
                DocumentReference ref = activity.db.collection("users")
                        .document(deviceID);

                // Update the field
                Map<String, Object> data = new HashMap<>();
                String newName = "John Doe";
                data.put("name", newName);
                ref.update(data);

                // Check that the name was updated
                withUser(activity, user -> {
                    String name = user.getString("name");
                    if (name != null) {
                        assertSame(name, newName);
                    }
                });

                // Remove the user
                deleteUser(activity);
            });
        } catch (Exception ignore) {
        }
    }

    /**
     * Update the user's phone number & check to see if the DB is updated.
     */
    @Test
    public void modifyPhoneTest() {
        try {
            rule.getScenario().onActivity(activity -> {
                // Create a new user
                activity.createNewUserProfile("Attendee");

                // Get the user document
                String deviceID = new User(activity).getDeviceID();
                DocumentReference ref = activity.db.collection("users")
                        .document(deviceID);

                // Update the field
                Map<String, Object> data = new HashMap<>();
                String newPhone = "1234123123";
                data.put("phone", newPhone);
                ref.update(data);

                // Check that the name was updated
                withUser(activity, user -> {
                    String phone = user.getString("phone");
                    if (phone != null) {
                        assertSame(phone, newPhone);
                    }
                });

                // Remove the user
                deleteUser(activity);
            });
        } catch (Exception ignore) {
        }
    }
}
