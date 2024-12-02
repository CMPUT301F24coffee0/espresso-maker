package com.example.espresso.EntrantList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.espresso.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * This adapter extends ArrayAdapter to display the invited participants. An invited participant is one
 * that has won the lottery and is displayed in a custom list item layout. The adapter also provides functionality
 * to cancel the invitation and update the participant's status to "declined".
 */
public class InvitedAdapter extends ArrayAdapter<Participant> {
    private final List<Participant> participants;
    private final LayoutInflater inflater;
    private final String eventId; // Event ID for updating status in Firestore

    /**
     * Constructor for the InvitedAdapter.
     *
     * @param context The context in which the adapter is being created.
     * @param participants The list of participants to be displayed.
     * @param eventId The ID of the event associated with the participants.
     */
    public InvitedAdapter(@NonNull Context context, @NonNull List<Participant> participants, @NonNull String eventId) {
        super(context, R.layout.listitem_invited, participants);
        this.participants = participants;
        this.inflater = LayoutInflater.from(context);
        this.eventId = eventId;
    }

    /**
     * Returns a view for each item in the list. It populates the view with participant data
     * and sets up the cancel button to update the status of a participant.
     *
     * @param position The position of the item within the data set.
     * @param convertView The recycled view to populate (if available).
     * @param parent The parent view to which the list item is attached.
     * @return The populated list item view.
     */
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
            // Update status to "declined" in participants collection
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("events")
                    .document(eventId)
                    .collection("participants")
                    .document(participant.getDeviceId())
                    .update("status", "declined")
                    .addOnSuccessListener(aVoid -> {
                        participants.remove(position);
                        notifyDataSetChanged();
                        Log.d("InvitedAdapter", "Status updated to declined for: " + participant.getDeviceId());
                    })
                    .addOnFailureListener(e -> Log.e("InvitedAdapter", "Error updating status", e));

            // Update status to "declined" in user's event record
            db.collection("users").document(participant.getDeviceId()).collection("events").document(eventId).update("status", "declined");
        });

        return convertView;
    }

    /**
     * ViewHolder pattern to optimize view lookup and prevent repeated calls to findViewById.
     */
    static class ViewHolder {
        TextView nameTextView;
        ImageButton cancelButton;
    }
}
