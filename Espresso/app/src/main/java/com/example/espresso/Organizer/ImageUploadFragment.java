package com.example.espresso.Organizer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.espresso.Attendee.User;
import com.example.espresso.Event.Event;

import com.example.espresso.Organizer.OrganizerHomeActivity;
import com.example.espresso.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
    SwitchCompat geolocationSwitch;

    /**
     * Default constructor for the fragment.
     * Required for fragment instantiation. No implementation needed.
     */
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

        if (getArguments() != null) {
            eventName = getArguments().getString("eventName");
            eventLocation = getArguments().getString("eventLocation");
            eventDate = getArguments().getString("eventDate");
            eventTime = getArguments().getString("eventTime");
            registrationDeadline = getArguments().getString("registrationDeadline");
            waitingListCapacity = getArguments().getString("waitingListCapacity");
            documentId = getArguments().getString("documentId");
        }

        geolocationSwitch = view.findViewById(R.id.geolocation_switch);

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
            Event event = new Event(eventName, eventDate, eventTime, "", registrationDeadline, Integer.parseInt(waitingListCapacity), new Facility(eventLocation), false, "view");
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
     * Handles the geolocation switch by setting the geolocation flag in the event data.
     * If geolocation is enabled, an additional step to fetch geolocation information can be implemented here.
     *
     * @param view The current fragment's view, used to retrieve additional UI input like description.
     */
    private void saveEventDataToFirestore(View view) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("name", eventName);
        eventData.put("location", eventLocation);
        eventData.put("date", eventDate);
        eventData.put("time", eventTime);
        eventData.put("deadline", registrationDeadline);
        eventData.put("capacity", Integer.parseInt(waitingListCapacity));

        TextView descriptionView = view.findViewById(R.id.description);
        if (descriptionView != null) {
            String descriptionText = descriptionView.getText().toString();
            eventData.put("description", descriptionText);
        }

        // Handle geolocation switch

        eventData.put("geolocation", geolocationSwitch.isChecked());


        eventData.put("organizer", (new User(getContext())).getDeviceID());
        eventData.put("drawed", false);

        DocumentReference docRef;
        if (documentId != null && !documentId.isEmpty()) {
            // Update existing document
            docRef = db.collection("events").document(documentId);
            docRef.delete();
        }

        docRef = db.collection("events").document(
                new Event(
                        eventName,
                        eventDate,
                        eventTime,
                        null,
                        eventDate,
                        Integer.valueOf(waitingListCapacity),
                        new Facility(eventLocation),
                        false,
                        "view").getId());

        docRef.set(eventData)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Event saved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to save event", Toast.LENGTH_SHORT).show());

        startActivity(new Intent(getContext(), OrganizerHomeActivity.class));
    }
}