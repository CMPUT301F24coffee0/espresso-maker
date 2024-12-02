package com.example.espresso.db;

import static org.junit.Assert.assertSame;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.espresso.Attendee.User;
import com.example.espresso.MainActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

/**
 * Test the user signup functionality
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class SignUpTest {
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
     * Test that a new user can be created in the database.
     */
    @Test
    public void testSignUp() {
        rule.getScenario().onActivity(activity -> {
            // Create a new user
            activity.createNewUserProfile("Attendee");

            // Ensure that the user was created
            withUser(activity, Assert::assertNotNull);

            // Remove the user
            deleteUser(activity);
        });
    }

    /**
     * Test that a user that has logged in once will use the same ID as the previous login.
     */
    @Test
    public void testSecondLogin() {
        rule.getScenario().onActivity(activity -> {
            // Create a new user
            activity.createNewUserProfile("Attendee");

            // Login once
            withUser(activity, d1 -> {
                withUser(activity, d2 -> {
                    assertSame(d1.getString("deviceID"), d2.getString("deviceID"));
                });
            });

            // Remove the user
            deleteUser(activity);
        });
    }
}
