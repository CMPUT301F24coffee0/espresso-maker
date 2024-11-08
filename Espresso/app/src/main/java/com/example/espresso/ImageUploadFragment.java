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

public class ImageUploadFragment extends Fragment {
    private FirebaseFirestore db;
    private Button uploadButton;
    private String eventName, eventLocation, eventDate, eventTime, registrationDeadline, waitingListCapacity, documentId;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    Uri selectedImageUri;
    public ImageUploadFragment() {
        // Required empty public constructor
    }

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
            String deviceID = new User(getContext()).getDeviceID();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference pfpsRef = storageRef.child("poster/" + deviceID + ".png");

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

        SwitchCompat geolocationSwitch = view.findViewById(R.id.geolocation_switch);
        if (geolocationSwitch != null) {
            eventData.put("geolocation", geolocationSwitch.isChecked());
        }

        eventData.put("organizer", new User(getContext()).getDeviceID());

        DocumentReference docRef = db.collection("events").document(documentId != null ? documentId : eventName + eventLocation + eventTime);
        docRef.set(eventData)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Event saved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to save event", Toast.LENGTH_SHORT).show());

        startActivity(new Intent(getContext(), OrganizerHomeActivity.class));
    }
}
