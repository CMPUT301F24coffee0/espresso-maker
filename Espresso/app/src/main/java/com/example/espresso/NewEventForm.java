package com.example.espresso;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewEventForm extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private EditText
            eventName,
            eventLocation,
            eventDate,
            eventTime,
            registrationDeadline,
            waitingListCapacity;

    private String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_event_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.landing_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        eventName = findViewById(R.id.event_name);
        eventLocation = findViewById(R.id.location);
        eventDate = findViewById(R.id.choose_date);
        eventTime = findViewById(R.id.choose_time);
        registrationDeadline = findViewById(R.id.registration_until);
        waitingListCapacity = findViewById(R.id.waiting_list_capacity);

        Intent intent = getIntent();
        String eventType = intent.getStringExtra("type");

        if ("edit".equals(eventType)) {
            documentId = intent.getStringExtra("eventId");
            if (documentId != null && !documentId.isEmpty()) {
                loadEventData();
            } else {
                Toast.makeText(this, "Invalid document ID", Toast.LENGTH_SHORT).show();
            }
        }

        Button nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();

            bundle.putString("eventName", eventName.getText().toString());
            bundle.putString("eventLocation", eventLocation.getText().toString());
            bundle.putString("eventDate", eventDate.getText().toString());
            bundle.putString("eventTime", eventTime.getText().toString());
            bundle.putString("registrationDeadline", registrationDeadline.getText().toString());
            bundle.putString("waitingListCapacity", waitingListCapacity.getText().toString());
            bundle.putString("documentId", documentId);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            ImageUploadFragment fragment = new ImageUploadFragment();

            fragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.landing_page, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
    }

    private void loadEventData() {
        if (documentId == null || documentId.isEmpty()) {
            Toast.makeText(this, "Invalid document ID, unable to load event data.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("events").document(documentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        eventName.setText(documentSnapshot.getString("name"));
                        eventLocation.setText(documentSnapshot.getString("location"));
                        eventDate.setText(documentSnapshot.getString("date"));
                        eventTime.setText(documentSnapshot.getString("time"));
                        registrationDeadline.setText(documentSnapshot.getString("deadline"));
                        Long capacity = documentSnapshot.getLong("capacity");
                        waitingListCapacity.setText(capacity != null ? String.valueOf(capacity) : "");
                    }
                    Log.d("document ID: ", documentId);
                })
                .addOnFailureListener(e -> Toast.makeText(NewEventForm.this, "Failed to load data", Toast.LENGTH_SHORT).show());
    }
}
