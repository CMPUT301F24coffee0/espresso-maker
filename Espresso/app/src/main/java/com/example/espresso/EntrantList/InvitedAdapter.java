package com.example.espresso.EntrantList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.espresso.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * This object extends ArrayAdapter to provide a custom view for Invited participants. An invited participant is one that has won the lottery.
 */
public class InvitedAdapter extends ArrayAdapter<Participant> {
    private final List<Participant> participants;
    private final LayoutInflater inflater;
    private final String eventId; // Pass the eventId to the adapter
    String name;

    public InvitedAdapter(@NonNull Context context, @NonNull List<Participant> participants, @NonNull String eventId) {
        super(context, R.layout.listitem_invited, participants);
        this.participants = participants;
        this.inflater = LayoutInflater.from(context);
        this.eventId = eventId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_invited, parent, false);
            holder = new ViewHolder();
            holder.nameTextView = convertView.findViewById(R.id.nameText);
            holder.cancelButton = convertView.findViewById(R.id.cancelButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Populate data
        Participant participant = participants.get(position);
        holder.nameTextView.setText(participant.getName());

        // Handle cancel button click
        holder.cancelButton.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Get event name
            db.collection("events").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    name = documentSnapshot.getString("name");
                } else {
                    Log.d("InvitedAdapter", "Event document does not exist");
                }
            });
            db.collection("events")
                    .document(eventId)
                    .collection("participants")
                    .document(participant.getDeviceId())
                    .update("status", "declined")
                    .addOnSuccessListener(aVoid -> {
                        participants.remove(position);
                        notifyDataSetChanged();
                        Log.d("InvitedAdapter", "Status updated to declined for: " + participant.getDeviceId());

                        // Select a new participant with status "not-invited"
                        db.collection("events").document(eventId).collection("participants")
                                .whereEqualTo("status", "not-invited")
                                .get()
                                .addOnCompleteListener(queryTask -> {
                                    if (queryTask.isSuccessful() && queryTask.getResult() != null && !queryTask.getResult().isEmpty()) {
                                        // Get all participants with "not-invited" status
                                        List<DocumentSnapshot> notInvitedParticipants = queryTask.getResult().getDocuments();
                                        // Randomly pick one
                                        Collections.shuffle(notInvitedParticipants);
                                        DocumentSnapshot selectedParticipant = notInvitedParticipants.get(0);
                                        String selectedParticipantId = selectedParticipant.getId();

                                        // Update the status of the newly selected participant
                                        db.collection("users").document(selectedParticipantId).collection("events").document(eventId).update("status", "invited");
                                        db.collection("events").document(eventId).collection("participants").document(selectedParticipantId)
                                                .update("status", "invited")
                                                .addOnSuccessListener(bvoid -> {
                                                    // Notify the new participant
                                                    db.collection("users").document(selectedParticipantId).get()
                                                            .addOnCompleteListener(userTask -> {
                                                                if (userTask.isSuccessful()) {
                                                                    DocumentSnapshot userDoc = userTask.getResult();
                                                                    if (userDoc.exists()) {
                                                                        String userToken = userDoc.getString("deviceToken");
                                                                        String eventName = userDoc.getString("name");
                                                                        HashMap<String, String> map = new HashMap<>();
                                                                        map.put("eventId", eventId);
                                                                        map.put("title", "New update from event " + name + "!");
                                                                        map.put("msg", "You have been selected to participate in the event: " + eventName);

                                                                        assert userToken != null;
                                                                        db.collection("notifications").document(userToken).set(map);

                                                                        Toast.makeText(this.getContext(), "A new participant has been invited.", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                })
                                                .addOnFailureListener(e -> Log.e("update", "Error updating participant status to invited", e));
                                    } else {
                                        Log.e("update", "No eligible participants found.");
                                    }
                                });
                    })
                    .addOnFailureListener(e -> Log.e("InvitedAdapter", "Error updating status", e));

            db.collection("users").document(participant.getDeviceId()).collection("events").document(eventId).update("status", "declined");

        });

        return convertView;
    }

    static class ViewHolder {
        TextView nameTextView;
        ImageButton cancelButton;
    }
}
