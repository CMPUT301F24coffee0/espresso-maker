package com.example.espresso.EntrantList;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.espresso.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * This fragment shows a ListView containing Invented participants. An invited participant is one that has won the lottery. This allows organizers to view a list of invited participants
 */
public class InvitedUser extends Fragment {
    private String eventId;

    public InvitedUser() {
        super(R.layout.fragment_invited_tab);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invited_tab, container, false);

        // Get the eventId from arguments
        eventId = requireArguments().getString("eventId");

        // Find the ListView
        ListView listView = view.findViewById(R.id.entrant_invited_view);

        // Initialize lists
        List<Participant> participants = new ArrayList<>();

        // Initialize the adapter and attach it to the ListView
        InvitedAdapter adapter = new InvitedAdapter(requireContext(), participants, eventId);
        listView.setAdapter(adapter);

        // Fetch participants asynchronously from Firestore
        fetchInvitedParticipants(participants, adapter);

        return view;
    }

    private void fetchInvitedParticipants(List<Participant> participants, ArrayAdapter<Participant> adapter) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events")
                .document(eventId)
                .collection("participants")
                .whereEqualTo("status", "invited")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String deviceId = document.getString("deviceId");

                                // Fetch participant's name from the users collection
                                db.collection("users").document(deviceId).get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                DocumentSnapshot document1 = task1.getResult();
                                                if (document1 != null && document1.exists()) {
                                                    String name = document1.getString("name");
                                                    if (name != null) {
                                                        participants.add(new Participant(deviceId, name));
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                }
                                            } else {
                                                Log.e("InvitedUser", "Error fetching participant name", task1.getException());
                                            }
                                        });
                            }
                        }
                    } else {
                        Log.e("InvitedUser", "Error fetching confirmed participants", task.getException());
                        Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
