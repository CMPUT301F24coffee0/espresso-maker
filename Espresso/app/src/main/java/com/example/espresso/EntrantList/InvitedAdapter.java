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
 * This object extends ArrayAdapter to provide a custom view for Invited participants. An invited participant is one that has won the lottery.
 */
public class InvitedAdapter extends ArrayAdapter<Participant> {
    private final List<Participant> participants;
    private final LayoutInflater inflater;
    private final String eventId; // Pass the eventId to the adapter

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

            db.collection("users").document(participant.getDeviceId()).collection("events").document(eventId).update("status", "declined");
        });

        return convertView;
    }

    static class ViewHolder {
        TextView nameTextView;
        ImageButton cancelButton;
    }
}
