package com.example.espresso.views.Organizer;

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
 * Fragment that displays declined users for a given event. A declined user is one
 * who has declined the event referenced by eventId.
 */
public class DeclinedUser extends Fragment {
    private String eventId;

    /**
     * Constructor for DeclinedUser fragment, using the layout file fragment_declined_tab.
     */
    public DeclinedUser() {
        super(R.layout.fragment_declined_tab);
    }

    /**
     * Called to create the fragment's view. Initializes the ListView and fetches declined participants.
     *
     * @param inflater The LayoutInflater object to inflate the fragment's view.
     * @param container The ViewGroup in which the fragment's UI should be inserted.
     * @param savedInstanceState The saved instance state of the fragment.
     * @return The View representing the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_declined_tab, container, false);

        // Get the eventId from arguments
        eventId = requireArguments().getString("eventId");

        // Find the ListView
        ListView listView = view.findViewById(R.id.entrant_declined_view);

        // Initialize an empty adapter and attach it to the ListView
        List<String> declinedEntrants = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                declinedEntrants
        );
        listView.setAdapter(adapter);

        // Fetch participants asynchronously from Firestore
        fetchDeclinedParticipants(declinedEntrants, adapter);

        return view;
    }

    /**
     * Fetches the list of declined participants from Firestore and updates the ListView.
     *
     * @param declinedEntrants The list to store participant names.
     * @param adapter The adapter to notify when data changes.
     */
    private void fetchDeclinedParticipants(List<String> declinedEntrants, ArrayAdapter<String> adapter) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events")
                .document(eventId)
                .collection("participants")
                .whereEqualTo("status", "declined")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String deviceID = document.getString("deviceId");

                                // Fetch participant's name from the users collection
                                db.collection("users").document(deviceID).get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                DocumentSnapshot document1 = task1.getResult();
                                                if (document1 != null && document1.exists()) {
                                                    String name = document1.getString("name");
                                                    if (name != null) {
                                                        declinedEntrants.add(name);

                                                        // Notify the adapter that data has changed
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                }
                                            } else {
                                                Log.e("DeclinedUser", "Error fetching participant name", task1.getException());
                                            }
                                        });
                            }
                        }
                    } else {
                        Log.e("DeclinedUser", "Error fetching declined participants", task.getException());
                        Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
