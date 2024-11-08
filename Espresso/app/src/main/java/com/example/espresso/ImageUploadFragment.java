package com.example.espresso;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
/**
 * A fragment that handles the uploading of event images and related data to Firestore.
 * It retrieves event details from the arguments, allows the user to input event information,
 * and saves the event data to the Firestore database when the "Create Event" button is clicked.
 */
public class ImageUploadFragment extends Fragment {
    private FirebaseFirestore db;
    private Button uploadButton;
    private String eventName, eventLocation, eventDate, eventTime, registrationDeadline, waitingListCapacity, documentId;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    Uri selectedImageUri;

    public ImageUploadFragment() {
        // Required empty public constructor
    }

    /**
     * Called to create the view for the fragment. This method inflates the layout,
     * retrieves event details passed through the fragment's arguments, and sets up
     * a listener for the "Create Event" button.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState A Bundle containing the fragment's previously saved state, if any.
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.event_image_upload, container, false);

        db = FirebaseFirestore.getInstance();

        // Retrieve arguments passed to the fragment
        if (getArguments() != null) {
            eventName = getArguments().getString("eventName");
            eventLocation = getArguments().getString("eventLocation");
            eventDate = getArguments().getString("eventDate");
            eventTime = getArguments().getString("eventTime");
            registrationDeadline = getArguments().getString("registrationDeadline");
            waitingListCapacity = getArguments().getString("waitingListCapacity");
            documentId = getArguments().getString("documentId");
        }

        // Set up listener for the "Create Event" button
        view.findViewById(R.id.create_event_button)
                .setOnClickListener(v -> saveEventDataToFirestore(view));

        uploadButton = view.findViewById(R.id.upload_poster_button);
        uploadButton.setOnClickListener(this::selectPoster);

        // Initialize image picker launcher
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == getActivity().RESULT_OK) {
                Intent data = result.getData();
                if (data != null && data.getData() != null) {
                    selectedImageUri = data.getData();

                }
            }
        });

        view.findViewById(R.id.create_event_button)
                .setOnClickListener(v -> {
                    saveEventDataToFirestore(view);
                    uploadImageToFirebase(selectedImageUri);
                });
        return view;
    }


    public void selectPoster(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void uploadImageToFirebase(Uri imageUri) {
        try {
            InputStream inputStream = requireActivity().getContentResolver().openInputStream(imageUri);
            Event event = new Event(eventName, eventDate, eventTime, "" , registrationDeadline, Integer.parseInt(waitingListCapacity), new Facility(eventLocation));
            String eventId = event.getId();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference pfpsRef = storageRef.child("posters/" + eventId + ".png");

            UploadTask uploadTask = pfpsRef.putStream(inputStream);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                Toast.makeText(getContext(), "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Failed to upload image.", Toast.LENGTH_SHORT).show();
                Log.e("ImageUploadFragment", "Error uploading image", e);
            });

        } catch (Exception e) {
            Log.e("ImageUploadFragment", "Error opening InputStream", e);
            Toast.makeText(getContext(), "Error opening image", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Saves the event data to Firestore.
     * This method creates a map of event details and either updates an existing event document
     * or creates a new one in the Firestore database.
     * After saving the event data, it navigates to the OrganizerHomeActivity.
     *
     * @param view The current fragment's view, used to retrieve additional UI input like description
     */

    private void saveEventDataToFirestore(View view) {
        // Map to hold event data
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("name", eventName);
        eventData.put("location", eventLocation);
        eventData.put("date", eventDate);
        eventData.put("time", eventTime);
        eventData.put("deadline", registrationDeadline);
        eventData.put("capacity", Integer.parseInt(waitingListCapacity));

        // Retrieve description from UI and add it to event data
        TextView descriptionView = view.findViewById(R.id.description);
        if (descriptionView != null) {
            String descriptionText = descriptionView.getText().toString();
            eventData.put("description", descriptionText);
        }

        // Retrieve geolocation status and add it to event data
        SwitchCompat geolocationSwitch = view.findViewById(R.id.geolocation_switch);
        if (geolocationSwitch != null) {
            eventData.put("geolocation", geolocationSwitch.isChecked());
        }


        eventData.put("organizer", new User(getContext()).getDeviceID());

        DocumentReference docRef = db.collection("events").document(documentId != null ? documentId : eventName + eventLocation + eventTime);

        // Add organizer's device ID to the event data
        eventData.put("organizer", (new User(getContext())).getDeviceID());

        DocumentReference docRef;

        // If documentId exists, update the existing event, otherwise create a new event
        if (documentId != null && !documentId.isEmpty()) {
            // Delete existing document and then create a new one
            docRef = db.collection("events").document(documentId);
            docRef.delete();

            docRef = db.collection("events").document(eventName + eventLocation + eventTime);
        } else {
            // Create new document
            docRef = db.collection("events").document(eventName + eventLocation + eventTime);
        }

        // Save the event data to Firestore and show a Toast message based on success or failure
        docRef.set(eventData)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Event saved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to save event", Toast.LENGTH_SHORT).show());

        startActivity(new Intent(getContext(), OrganizerHomeActivity.class));
        // Navigate to the OrganizerHomeActivity after saving the event data
        Intent intent = new Intent(getContext(), OrganizerHomeActivity.class);
        startActivity(intent);
    }
}
