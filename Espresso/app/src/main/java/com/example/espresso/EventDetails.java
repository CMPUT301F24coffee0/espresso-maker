package com.example.espresso;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Objects;
/**
 * Activity that displays detailed information about a specific event.
 * Users can view the event details, enter the lottery system for the event,
 * share the event via QR code, or navigate back to the home screen.
 */
public class EventDetails extends AppCompatActivity {
    Button enterLotteryButton, withdrawButton;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference posterRef;
    /**
     * Initializes the activity, sets up the event details UI, and handles user interactions.
     * Users can enter the lottery, share the event via QR code, and navigate back to the home screen.
     *
     * @param savedInstanceState The saved instance state of the activity, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_event_profile);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String deviceID = new User(this).getDeviceID();
        Intent intent = getIntent();

        // Handle onBackPressed()
        OnBackPressedDispatcher dispatcher = getOnBackPressedDispatcher();
        dispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(EventDetails.this, AttendeeHomeActivity.class);
                startActivity(intent);
            }
        });

        // Retrieve the extras
        String name = intent.getStringExtra("name");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String location = intent.getStringExtra("location");
        String description = intent.getStringExtra("description");
        String deadline = intent.getStringExtra("deadline");
        int capacity = intent.getIntExtra("capacity", 0); // Default value is 0
        String eventId = intent.getStringExtra("eventId");
        String posterUrl = intent.getStringExtra("posterUrl");
        String status = intent.getStringExtra("status");

        // Fetch poster from Firebase Storage
        String path = "posters/"+eventId+".png";
        posterRef = storageRef.child(path);
        posterRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Got the download URL for poster
            Log.d("Event", "Got download URL for poster");
            // Load the image using Picasso
            ImageView imageView = findViewById(R.id.attendee_event_profile_banner_img);
            Picasso.get().load(uri).into(imageView);
        }).addOnFailureListener(exception -> {
            // Handle any errors
            Log.e("Event", path);
            Log.e("Event", "Error getting download URL for poster", exception);
        });

        Log.d("Event", "Event after clicked: Name=" + name + ", Date=" + date + ", Time=" + time + ", Location=" + location + ", Description=" + description + ", Deadline=" + deadline + ", Capacity=" + capacity + ", EventId=" + eventId);

        // Set the event details in the UI
        TextView nameTextView = findViewById(R.id.attendee_event_profile_title);
        nameTextView.setText(name);
        TextView dateTextView = findViewById(R.id.attendee_event_profile_date);
        dateTextView.setText(String.format("%s - %s", date, time));
        TextView locationTextView = findViewById(R.id.attendee_event_profile_location);
        locationTextView.setText(location);
        TextView descriptionTextView = findViewById(R.id.attendee_event_profile_description);
        descriptionTextView.setText(description);
        TextView deadlineTextView = findViewById(R.id.attendee_event_profile_deadline);
        deadlineTextView.setText(String.format("Registration closes: %s", deadline));
        TextView capacityTextView = findViewById(R.id.attendee_event_profile_capacity_text);
        capacityTextView.setText(String.format("Capacity: %d", capacity));
        ImageView imageView = findViewById(R.id.attendee_event_profile_banner_img);
        Picasso.get().load(posterUrl).into(imageView);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("name", name);
        eventData.put("date", date);
        eventData.put("time", time);
        eventData.put("location", location);
        eventData.put("description", description);
        eventData.put("deadline", deadline);
        eventData.put("capacity", capacity);
        eventData.put("status", "pending");

        enterLotteryButton = findViewById(R.id.enter_lottery_button);
        withdrawButton = findViewById(R.id.withdraw_button);
        switch (Objects.requireNonNull(status)) {
            case "edit":
                enterLotteryButton.setEnabled(false);
                enterLotteryButton.setVisibility(View.INVISIBLE);
                break;
            case "confirmed":
                enterLotteryButton.setEnabled(false);
                enterLotteryButton.setText("Confirmed");
                enterLotteryButton.setTextColor(Color.BLACK);
                enterLotteryButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("green")));
                break;
            case "pending":
                enterLotteryButton.setEnabled(false);
                enterLotteryButton.setText("Pending");
                enterLotteryButton.setTextColor(Color.BLACK);
                enterLotteryButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("yellow")));
                withdrawButton.setVisibility(View.VISIBLE);
                break;
            case "declined":
                enterLotteryButton.setEnabled(false);
                enterLotteryButton.setText("Declined");
                enterLotteryButton.setTextColor(Color.WHITE);
                enterLotteryButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("red")));
                break;
        }

        enterLotteryButton.setOnClickListener(v -> {
            // User entered the lottery system
            assert eventId != null;
            db.collection("users").document(deviceID).collection("events").document(eventId).set(eventData);
            db.collection("events").document(eventId).collection("participants").document(deviceID).set(Map.of("status", "pending"))
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Lottery entered successfully
                            Log.d("Lottery", "Lottery entered successfully");
                            // Disable the button
                            enterLotteryButton.setEnabled(false);
                            enterLotteryButton.setText("You have entered the lottery!");
                            enterLotteryButton.setTextColor(Color.WHITE);
                            enterLotteryButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("grey")));

                        } else {
                            // Lottery entry failed
                            Log.d("Lottery", "Lottery entry failed");
                        }
                    });
        });

        withdrawButton.setOnClickListener(v -> {
            // User withdrew from the lottery
            assert eventId != null;
            db.collection("users").document(deviceID).collection("events").document(eventId).delete();
            db.collection("events").document(eventId).collection("participants").document(deviceID).delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Lottery withdrawn successfully
                    Toast.makeText(this, "You have successfully withdrawn from the lottery!", Toast.LENGTH_SHORT).show();
                    // Go back to the previous activity
                    Intent intent2 = new Intent(EventDetails.this, AttendeeHomeActivity.class);
                    startActivity(intent2);
                } else {
                    // Lottery withdrawal failed
                    Toast.makeText(this, "Failed to withdraw from the lottery.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Share button
        ImageButton shareBtn = findViewById(R.id.share_button);
        shareBtn.setOnClickListener(v -> {
            //Generate qr code from eventID
            Bitmap bitmap = new QRCode(eventId).generateQRCode();
            // Open a pop-up
            AlertDialog.Builder builder = new AlertDialog.Builder(EventDetails.this);
            builder.setTitle("Share QR Code");
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.share_qr_code, null);
            builder.setView(dialogView);

            ImageView qrCodeImage = dialogView.findViewById(R.id.qr_code_image);
            qrCodeImage.setImageBitmap(bitmap);

            Button shareButton = dialogView.findViewById(R.id.share_qr_button);
            shareButton.setOnClickListener(v1 -> {
                //Share qr code
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            Button cancelButton = dialogView.findViewById(R.id.cancel_button);
            cancelButton.setOnClickListener(v1 -> {
                dialog.dismiss();
            });
        });

        // Go back button
        ImageButton goBackBtn = findViewById(R.id.go_back_button);
        goBackBtn.setOnClickListener(v -> finish());
    }

}