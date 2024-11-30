package com.example.espresso.Event;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.espresso.Attendee.AttendeeHomeActivity;
import com.example.espresso.Attendee.QRCode;
import com.example.espresso.Attendee.User;
import com.example.espresso.Organizer.NewEventForm;
import com.example.espresso.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Activity that displays detailed information about a specific event.
 * Users can view the event details, enter the lottery system for the event,
 * share the event via QR code, or navigate back to the home screen.
 */
public class EventDetails extends AppCompatActivity {
    Button enterLotteryButton, withdrawButton, drawLotteryButton, drawLotteryButton2, acceptInviteButton, declineInviteButton, sendNotificationButton;
    ImageButton editButton, notificationButton, shareBtn, goBackBtn, mapButton, entrantButton;
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
        String status = intent.getStringExtra("status") != null ? intent.getStringExtra("status") : "view";
        int drawn = intent.getIntExtra("drawn", 0);

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
        drawLotteryButton = findViewById(R.id.draw_lottery);
        acceptInviteButton = findViewById(R.id.accept_invite_button);
        declineInviteButton = findViewById(R.id.decline_invite_button);
        editButton = findViewById(R.id.edit_button);
        sendNotificationButton = findViewById(R.id.notification);
        notificationButton = findViewById(R.id.notification_button);
        shareBtn = findViewById(R.id.share_button);
        goBackBtn = findViewById(R.id.go_back_button);
        drawLotteryButton2 = findViewById(R.id.draw_lottery_2);
        mapButton = findViewById(R.id.map_button);
        entrantButton = findViewById(R.id.entrant_button);

        // Check if user wants to receive notifications
        assert eventId != null;
        db.collection("events").document(eventId)
                .collection("participants").document(deviceID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            // Check if notif field exists and its value
                            Boolean isNotificationEnabled = document.getBoolean("notif");

                            // Update button icon based on notification status
                            if (Boolean.TRUE.equals(isNotificationEnabled)) {
                                notificationButton.setImageResource(R.drawable.ic_notif);
                            } else {
                                notificationButton.setImageResource(R.drawable.ic_notif_off);
                            }
                        } else {
                            // Document doesn't exist, set default state
                            notificationButton.setImageResource(R.drawable.ic_notif_off);
                        }
                    } else {
                        // Error fetching document
                        Log.e("Firestore", "Error getting document", task.getException());
                        // Set default state on error
                        notificationButton.setImageResource(R.drawable.ic_notif_off);
                    }
                });

        switch (Objects.requireNonNull(status)) {
            case "edit": // When user is organizer
                enterLotteryButton.setEnabled(false);
                enterLotteryButton.setVisibility(View.GONE);
                notificationButton.setVisibility(View.GONE);
                shareBtn.setVisibility(View.GONE);
                mapButton.setVisibility(View.VISIBLE);
                entrantButton.setVisibility(View.VISIBLE);
                drawLotteryButton.setVisibility(View.VISIBLE);
                if (drawn == 0) {
                    drawLotteryButton.setEnabled(false);
                    drawLotteryButton.setText("You already drawn the lottery!");
                    drawLotteryButton.setTextColor(Color.RED);
                    drawLotteryButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("grey")));
                } else if (drawn == 1) {
                    drawLotteryButton.setVisibility(View.GONE);
                    drawLotteryButton2.setVisibility(View.VISIBLE);
                }
                editButton.setVisibility(View.VISIBLE);
                sendNotificationButton.setVisibility(View.VISIBLE);
                break;
            case "confirmed": // When user is confirmed attendee
                enterLotteryButton.setEnabled(false);
                enterLotteryButton.setText("Confirmed");
                enterLotteryButton.setTextColor(Color.BLACK);
                enterLotteryButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("green")));
                break;
            case "invited": // When user is invited attendee
                acceptInviteButton.setVisibility(View.VISIBLE);
                declineInviteButton.setVisibility(View.VISIBLE);
                enterLotteryButton.setVisibility(View.GONE);
                enterLotteryButton.setEnabled(false);
                break;
            case "pending": // When user is not confirmed attendee
                enterLotteryButton.setEnabled(false);
                enterLotteryButton.setText("Pending");
                enterLotteryButton.setTextColor(Color.BLACK);
                enterLotteryButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("yellow")));
                withdrawButton.setVisibility(View.VISIBLE);
                break;
            case "declined": // When user is declined attendee
                enterLotteryButton.setEnabled(false);
                enterLotteryButton.setText("Declined");
                enterLotteryButton.setTextColor(Color.WHITE);
                enterLotteryButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("red")));
                break;
        }

        // For organizer
        mapButton.setOnClickListener(v -> {
            // Show map here
        });

        entrantButton.setOnClickListener(v -> {
            // Create a dialog to show the list of entrants
            AlertDialog.Builder builder = new AlertDialog.Builder(EventDetails.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_entrant_list, null);

            // Find the ListView and close button in the dialog layout
            ListView entrantListView = dialogView.findViewById(R.id.entrant_list_view);
            ImageButton closeButton = dialogView.findViewById(R.id.close_button1);

            // Create the dialog
            AlertDialog dialog = builder.create();
            dialog.setView(dialogView);

            // Fetch participants from Firestore
            db.collection("events").document(eventId)
                    .collection("participants")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Create a list to hold participant details
                            List<Map<String, String>> participantDetailsList = new ArrayList<>();

                            // Process each participant document
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Get the participant's device ID
                                String participantId = document.getId();
                                String status1 = document.getString("status");

                                // Fetch user details for each participant
                                db.collection("users").document(participantId).get()
                                        .addOnSuccessListener(userDoc -> {
                                            if (userDoc.exists()) {
                                                String name1 = userDoc.getString("name");

                                                // Create a map for each participant
                                                Map<String, String> participantDetails = new HashMap<>();
                                                participantDetails.put("name", name1);
                                                participantDetails.put("status", status1);
                                                participantDetailsList.add(participantDetails);

                                                // Create a custom adapter for the list view
                                                SimpleAdapter adapter = new SimpleAdapter(
                                                        EventDetails.this,
                                                        participantDetailsList,
                                                        android.R.layout.simple_list_item_2,
                                                        new String[]{"name", "status"},
                                                        new int[]{android.R.id.text1, android.R.id.text2}
                                                );
                                                entrantListView.setAdapter(adapter);
                                            }
                                        });
                            }
                        } else {
                            // Handle error
                            Log.e("EntrantList", "Error getting participants", task.getException());
                            Toast.makeText(EventDetails.this, "Failed to load entrants", Toast.LENGTH_SHORT).show();
                        }
                    });

            // Set up close button
            closeButton.setOnClickListener(closeView -> dialog.dismiss());

            // Show the dialog
            dialog.show();
        });

        editButton.setOnClickListener(v -> {
            // Edit the event
            Intent intent2 = new Intent(EventDetails.this, NewEventForm.class);
            intent2.putExtra("eventId", eventId);
            intent2.putExtra("name", name);
            intent2.putExtra("date", date);
            intent2.putExtra("time", time);
            intent2.putExtra("location", location);
            intent2.putExtra("description", description);
            intent2.putExtra("deadline", deadline);
            intent2.putExtra("capacity", capacity);
            intent2.putExtra("eventId", eventId);
            intent2.putExtra("status", status);
            startActivity(intent2);
        });

        sendNotificationButton.setOnClickListener(v -> {
            // Create an AlertDialog with an EditText for notification input
            AlertDialog.Builder builder = new AlertDialog.Builder(EventDetails.this);
            builder.setTitle("Send Notification");

            // Inflate a custom layout with an EditText
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_send_notification, null);
            EditText notificationEditText = dialogView.findViewById(R.id.notification_message_input);
            Button cancelButton = dialogView.findViewById(R.id.cancel_notification_button);
            Button sendButton = dialogView.findViewById(R.id.send_notification_button);
            // Get the selected radio button
            RadioGroup userSelectionGroup = dialogView.findViewById(R.id.user_selection_group);
            String text = "";
            // Create the dialog but don't show it yet
            AlertDialog dialog = builder.create();
            dialog.setView(dialogView);

            // Cancel button listener
            cancelButton.setOnClickListener(cancelView -> dialog.dismiss());

            // Send button listener
            sendButton.setOnClickListener(sendView -> {
                String message = notificationEditText.getText().toString().trim();

                if (!message.isEmpty()) {
                    // Retrieve the current selected RadioButton ID dynamically
                    int selectedRadioButtonId = userSelectionGroup.getCheckedRadioButtonId();
                    String toastText; // Declare the toast text variable

                    if (selectedRadioButtonId == R.id.radio_confirmed_users) {
                        toastText = "Notification sent to confirmed participants";
                    } else if (selectedRadioButtonId == R.id.radio_all_users) {
                        toastText = "Notification sent to all users";
                    } else {
                        Toast.makeText(EventDetails.this, "Please select a user group", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    db.collection("events").document(eventId).collection("participants").get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    List<DocumentSnapshot> participants = task.getResult().getDocuments();

                                    for (DocumentSnapshot participant : participants) {
                                        String participantId = participant.getId();

                                        // Fetch user details to get device token
                                        db.collection("users").document(participantId).get()
                                                .addOnCompleteListener(userTask -> {
                                                    if (userTask.isSuccessful()) {
                                                        DocumentSnapshot userDoc = userTask.getResult();
                                                        if (userDoc.exists()) {
                                                            String userToken = userDoc.getString("deviceToken");

                                                            // Prepare notification data
                                                            HashMap<String, String> notificationData = new HashMap<>();
                                                            if (selectedRadioButtonId == R.id.radio_confirmed_users) {
                                                                notificationData.put("eventId", eventId + "confirmed");
                                                            } else if (selectedRadioButtonId == R.id.radio_all_users) {
                                                                notificationData.put("eventId", eventId);
                                                            }
                                                            notificationData.put("title", "You have a notification from event " + name + "!");
                                                            notificationData.put("msg", message);

                                                            // Store notification in Firestore
                                                            if (userToken != null) {
                                                                db.collection("notifications").document(userToken).set(notificationData);
                                                            }
                                                        }
                                                    }
                                                });
                                    }

                                    // Show success toast and dismiss dialog
                                    runOnUiThread(() -> {
                                        Toast.makeText(EventDetails.this, toastText, Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    });
                                } else {
                                    // Handle error
                                    runOnUiThread(() -> {
                                        Toast.makeText(EventDetails.this, "Failed to send notification", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    });
                                }
                            });
                } else {
                    Toast.makeText(EventDetails.this, "Please enter a notification message", Toast.LENGTH_SHORT).show();
                }
            });


            // Show the dialog
            dialog.show();
        });

        drawLotteryButton.setOnClickListener(v -> {
            db.collection("events").document(eventId).collection("participants").get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> participants = task.getResult().getDocuments();

                            // Check if the participants list is empty
                            if (participants.isEmpty()) {
                                Toast.makeText(this, "No participants have joined this event.", Toast.LENGTH_SHORT).show();
                                return; // Exit early since there's nothing to process
                            }

                            int size = participants.size();

                            // Ensure we don't select more than the total number of participants
                            int selectCount = Math.min(capacity, size);

                            // Shuffle the participant list to get a random order
                            Collections.shuffle(participants);

                            // Select the first `selectCount` participants from the shuffled list
                            List<DocumentSnapshot> selectedParticipants = participants.subList(0, selectCount);

                            // Create a set of IDs for the selected participants
                            Set<String> invitedParticipantIds = new HashSet<>();
                            for (DocumentSnapshot participant : selectedParticipants) {
                                invitedParticipantIds.add(participant.getId());
                            }

                            // Process all participants
                            for (DocumentSnapshot participant : participants) {
                                String participantId = participant.getId();

                                // Check if the participant is in the invited list
                                if (invitedParticipantIds.contains(participantId)) {
                                    // Update to "invited" and send invitation notification
                                    db.collection("events").document(eventId)
                                            .collection("participants")
                                            .get()
                                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                                for (QueryDocumentSnapshot participantDoc : queryDocumentSnapshots) {
                                                    String pID = participantDoc.getId();
                                                    db.collection("users").document(pID).collection("events").document(eventId).update("status", "invited");
                                                }
                                            });
                                    db.collection("events").document(eventId)
                                            .collection("participants").document(participantId)
                                            .update("status", "invited")
                                            .addOnSuccessListener(aVoid -> {
                                                db.collection("users").document(participantId).get()
                                                        .addOnCompleteListener(userTask -> {
                                                            if (userTask.isSuccessful()) {
                                                                DocumentSnapshot userDoc = userTask.getResult();
                                                                if (userDoc.exists()) {
                                                                    String userToken = userDoc.getString("deviceToken");
                                                                    HashMap<String, String> map = new HashMap<>();
                                                                    map.put("eventId",eventId);
                                                                    map.put("title", "New update from event " + name + "!");
                                                                    map.put("msg", "Congratulations! You have been invited to the event!");
                                                                    assert userToken != null;
                                                                    db.collection("notifications").document(userToken).set(map);
                                                                }
                                                            }
                                                        });
                                            })
                                            .addOnFailureListener(e -> Log.e("Lottery", "Error updating participant to invited", e));
                                } else {
                                    // Update to "not-invited" and send decline notification
                                    db.collection("users").document(deviceID).collection("events").document(eventId).update("status", "not-invited");
                                    db.collection("events").document(eventId)
                                            .collection("participants").document(participantId)
                                            .update("status", "not-invited")
                                            .addOnSuccessListener(aVoid -> {
                                                db.collection("users").document(participantId).get()
                                                        .addOnCompleteListener(userTask -> {
                                                            if (userTask.isSuccessful()) {
                                                                DocumentSnapshot userDoc = userTask.getResult();
                                                                if (userDoc.exists()) {
                                                                    String userToken = userDoc.getString("deviceToken");
                                                                    String eventName = userDoc.getString("name");
                                                                    HashMap<String, String> map = new HashMap<>();
                                                                    map.put("eventID",eventId);
                                                                    map.put("title", eventName);
                                                                    map.put("msg", "Unfortunately, you were not selected for the event. However, you still have a chance to participate!");
                                                                    assert userToken != null;
                                                                    db.collection("notifications").document(userToken).set(map);
                                                                }
                                                            }
                                                        });
                                            })
                                            .addOnFailureListener(e -> Log.e("Lottery", "Error updating participant to not-invited", e));
                                }
                            }

                            // Update the "drawn" status for the event
                            db.collection("events").document(eventId).update("drawn", 1);
                            db.collection("events").document(eventId).update("capacity", capacity - selectCount);
                            Toast.makeText(this, "Lottery drawn successfully!", Toast.LENGTH_SHORT).show();
                            drawLotteryButton.setVisibility(View.GONE);
                            drawLotteryButton2.setVisibility(View.VISIBLE);
                        } else {
                            Log.e("Lottery", "Error retrieving participants", task.getException());
                        }
                    });
        });

        drawLotteryButton2.setOnClickListener(v -> {
            db.collection("events").document(eventId).collection("participants").whereEqualTo("status", "not-invited")
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> participants = task.getResult().getDocuments();

                            // Check if the participants list is empty
                            if (participants.isEmpty()) {
                                Toast.makeText(this, "No participants left to invite.", Toast.LENGTH_SHORT).show();
                                return; // Exit early since there's nothing to process
                            }
                            int size = participants.size();

                            // Ensure we don't select more than the total number of participants
                            int selectCount = Math.min(capacity, size);

                            // Shuffle the participant list to get a random order
                            Collections.shuffle(participants);

                            // Select the first `selectCount` participants from the shuffled list
                            List<DocumentSnapshot> selectedParticipants = participants.subList(0, selectCount);

                            // Create a set of IDs for the selected participants
                            Set<String> invitedParticipantIds = new HashSet<>();
                            for (DocumentSnapshot participant : selectedParticipants) {
                                invitedParticipantIds.add(participant.getId());
                            }

                            // Process all participants
                            for (DocumentSnapshot participant : participants) {
                                String participantId = participant.getId();

                                // Check if the participant is in the invited list
                                if (invitedParticipantIds.contains(participantId)) {
                                    // Update to "invited" and send invitation notification
                                    db.collection("events").document(eventId)
                                            .collection("participants")
                                            .get()
                                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                                for (QueryDocumentSnapshot participantDoc : queryDocumentSnapshots) {
                                                    String pID = participantDoc.getId();
                                                    db.collection("users").document(pID).collection("events").document(eventId).update("status", "invited");
                                                }
                                            });
                                    db.collection("events").document(eventId)
                                            .collection("participants").document(participantId)
                                            .update("status", "invited")
                                            .addOnSuccessListener(aVoid -> {
                                                db.collection("users").document(participantId).get()
                                                        .addOnCompleteListener(userTask -> {
                                                            if (userTask.isSuccessful()) {
                                                                DocumentSnapshot userDoc = userTask.getResult();
                                                                if (userDoc.exists()) {
                                                                    String userToken = userDoc.getString("deviceToken");
                                                                    HashMap<String, String> map = new HashMap<>();
                                                                    map.put("eventId",eventId);
                                                                    map.put("title", "New update from event " + name + "!");
                                                                    map.put("msg", "Congratulations! You have been invited to the event!");
                                                                    assert userToken != null;
                                                                    db.collection("notifications").document(userToken).set(map);
                                                                }
                                                            }
                                                        });
                                            })
                                            .addOnFailureListener(e -> Log.e("Lottery", "Error updating participant to invited", e));
                                } else {
                                    // Update to "declined" and send decline notification
                                    db.collection("users").document(deviceID).collection("events").document(eventId).update("status", "not-invited");
                                    db.collection("events").document(eventId)
                                            .collection("participants").document(participantId)
                                            .update("status", "declined")
                                            .addOnSuccessListener(aVoid -> {
                                                db.collection("users").document(participantId).get()
                                                        .addOnCompleteListener(userTask -> {
                                                            if (userTask.isSuccessful()) {
                                                                DocumentSnapshot userDoc = userTask.getResult();
                                                                if (userDoc.exists()) {
                                                                    String userToken = userDoc.getString("deviceToken");
                                                                    String eventName = userDoc.getString("name");
                                                                    HashMap<String, String> map = new HashMap<>();
                                                                    map.put("eventID",eventId);
                                                                    map.put("title", eventName);
                                                                    map.put("msg", "Unfortunately, you were not selected for the event :(. Better luck next time!");
                                                                    assert userToken != null;
                                                                    db.collection("notifications").document(userToken).set(map);
                                                                }
                                                            }
                                                        });
                                            })
                                            .addOnFailureListener(e -> Log.e("Lottery", "Error updating participant to not-invited", e));
                                }
                            }
                            db.collection("events").document(eventId).update("drawn", 0);
                            drawLotteryButton2.setEnabled(false);
                            drawLotteryButton2.setText("You already drawn the lottery!");
                            drawLotteryButton2.setTextColor(Color.RED);
                            drawLotteryButton2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("grey")));
                            Toast.makeText(this, "Lottery drawn successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("Lottery", "Error retrieving participants", task.getException());
                        }
                    });
        });

        // For attendee
        notificationButton.setOnClickListener(v -> {
            // Check current notification status
            db.collection("events").document(eventId)
                    .collection("participants").document(deviceID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            boolean currentStatus = document != null &&
                                    Boolean.TRUE.equals(document.getBoolean("notif"));

                            // Toggle notification status
                            boolean newStatus = !currentStatus;

                            // Prepare update
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("notif", newStatus);

                            // Update Firestore
                            db.collection("events").document(eventId)
                                    .collection("participants").document(deviceID)
                                    .set(updates, SetOptions.merge())
                                    .addOnSuccessListener(unused -> {
                                        // Update successful
                                        if (newStatus) {
                                            // Subscribe to topic
                                            FirebaseMessaging.getInstance().subscribeToTopic(eventId)
                                                    .addOnCompleteListener(subscribeTask -> {
                                                        if (subscribeTask.isSuccessful()) {
                                                            notificationButton.setImageResource(R.drawable.ic_notif);
                                                            Toast.makeText(EventDetails.this,
                                                                    "Notifications enabled",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            // Unsubscribe from topic
                                            FirebaseMessaging.getInstance().unsubscribeFromTopic(eventId)
                                                    .addOnCompleteListener(unsubscribeTask -> {
                                                        if (unsubscribeTask.isSuccessful()) {
                                                            notificationButton.setImageResource(R.drawable.ic_notif_off);
                                                            Toast.makeText(EventDetails.this,
                                                                    "Notifications disabled",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        // Update failed
                                        Toast.makeText(EventDetails.this,
                                                "Failed to update notification settings",
                                                Toast.LENGTH_SHORT).show();
                                    });
                        }
                    });
        });

        enterLotteryButton.setOnClickListener(v -> {
            // User entered the lottery system
            db.collection("users").document(deviceID).collection("events").document(eventId).set(eventData);
            db.collection("events").document(eventId).collection("participants").document(deviceID).set(Map.of("status", "pending"))
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Pop-up ask for notification permission
                            AlertDialog.Builder builder = new AlertDialog.Builder(EventDetails.this);
                            builder.setTitle("Receive Notifications")
                                    .setMessage("Do you want to receive notifications for this event?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // User clicked Yes
                                            FirebaseMessaging.getInstance().subscribeToTopic(eventId)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            String msg = "Notification enabled";
                                                            if (!task.isSuccessful()) {
                                                                msg = "Subscribe failed";
                                                            } else {
                                                                Map<String, Object> notificationData = new HashMap<>();
                                                                notificationData.put("notif", true);

                                                                db.collection("events").document(eventId)
                                                                        .collection("participants").document(deviceID)
                                                                        .set(notificationData, SetOptions.merge())
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    Log.d("notif", "Notification field added successfully");
                                                                                } else {
                                                                                    Log.e("notif", "Failed to add notification field", task.getException());
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                            notificationButton.setImageResource(R.drawable.ic_notif);
                                                            Log.d("msg", msg);
                                                            Toast.makeText(EventDetails.this, msg, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // User clicked No
                                            Map<String, Object> notificationData = new HashMap<>();
                                            notificationData.put("notif", false);

                                            db.collection("events").document(eventId)
                                                    .collection("participants").document(deviceID)
                                                    .set(notificationData, SetOptions.merge())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.d("notif", "Notification field added successfully");
                                                            } else {
                                                                Log.e("notif", "Failed to add notification field", task.getException());
                                                            }
                                                        }
                                                    });
                                            Toast.makeText(EventDetails.this, "Notifications disabled", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .create()
                                    .show();
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

        acceptInviteButton.setOnClickListener(v -> {
            // Accept the invitation
            db.collection("users").document(deviceID).collection("events").document(eventId).update("status", "confirmed");
            db.collection("events").document(eventId).collection("participants").document(deviceID).update("status", "confirmed")
                    .addOnSuccessListener(aVoid ->{
                        Toast.makeText(this, "You have accepted the invitation!", Toast.LENGTH_SHORT).show();
                        acceptInviteButton.setEnabled(false);
                    });
        });

        declineInviteButton.setOnClickListener(v -> {
            // Decline the invitation
            db.collection("users").document(deviceID).collection("events").document(eventId).update("status", "declined");
            db.collection("events").document(eventId).collection("participants").document(deviceID).update("status", "declined")
                    .addOnSuccessListener(aVoid ->{
                        Toast.makeText(this, "You have declined the invitation!", Toast.LENGTH_SHORT).show();
                        declineInviteButton.setEnabled(false);
                    });
        });

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

        // For both organizer and attendee
        goBackBtn.setOnClickListener(v -> finish());
    }

}