package com.example.espresso.db;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import android.app.Activity;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.espresso.MainActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;

/**
 * Test the profile image upload.
 */
@RunWith(AndroidJUnit4.class)
public class ProfileTest {
    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    private InputStream loadImage(Activity activity, String path) {
        try {
            return activity.getAssets().open(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Upload an profile picture to the database.
     */
    @Test
    public void uploadPoster() {
        rule.getScenario().onActivity(activity -> {
            InputStream img = loadImage(activity,"user-list-svgrepo-com.svg");

            // Where to upload
            String eventID = "1234";
            StorageReference storage = FirebaseStorage.getInstance().getReference();
            StorageReference imgRef = storage.child("pfps/" + eventID + ".svg");

            // Upload the image
            assertNotNull(img);
            UploadTask task = imgRef.putStream(img);
            task.addOnFailureListener(taskSnapshot -> {
                // Error out if the image failed to upload
                fail();
            });
        });
    }
}
