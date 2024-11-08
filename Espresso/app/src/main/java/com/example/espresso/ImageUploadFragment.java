package com.example.espresso;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class ImageUploadFragment extends Fragment {
    private FirebaseFirestore db;

    private String
            eventName,
            eventLocation,
            eventDate,
            eventTime,
            registrationDeadline,
            waitingListCapacity,
            documentId;

    public ImageUploadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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

        view.findViewById(R.id.create_event_button)
                .setOnClickListener(v -> saveEventDataToFirestore(view));

        return view;
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

        eventData.put("organizer", (new User(getContext())).getDeviceID());

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
                        new Facility(eventLocation)).getId());

        docRef.set(eventData)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Event saved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to save event", Toast.LENGTH_SHORT).show());

        Intent intent = new Intent(getContext(), OrganizerHomeActivity.class);
        startActivity(intent);
    }
}