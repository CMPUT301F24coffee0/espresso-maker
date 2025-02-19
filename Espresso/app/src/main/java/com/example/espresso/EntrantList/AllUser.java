package com.example.espresso.EntrantList;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * This object displays a list of all users registered with a particular event inside a fragment.
 */
public class AllUser extends Fragment {
    private String eventId;

    public AllUser() {
        super(R.layout.fragment_all_tab);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_tab, container, false);

        // Get the eventId from arguments
        eventId = requireArguments().getString("eventId");

        // Find the ListView
        ListView listView = view.findViewById(R.id.entrant_all_view);

        // Initialize an empty adapter and attach it to the ListView
        List<AllUserModel> allUsers = new ArrayList<>();
        AllUserAdapter adapter = new AllUserAdapter(requireContext(), allUsers);
        listView.setAdapter(adapter);

        // Fetch all users asynchronously from Firestore
        fetchAllUsers(allUsers, adapter);

        return view;
    }

    /**
     * Fetches all users assigned to a given event in Firebase
     * @param allUsers List of Users
     * @param adapter ListView Adapter
     */
    private void fetchAllUsers(List<AllUserModel> allUsers, AllUserAdapter adapter) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events")
                .document(eventId)
                .collection("participants")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String deviceId = document.getString("deviceId");
                                String status = document.getString("status");
                                Log.d("AllUser", "Event ID: " + deviceId);
                                // Fetch participant's name from the users collection
                                assert deviceId != null;
                                db.collection("users").document(deviceId).get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                DocumentSnapshot document1 = task1.getResult();
                                                if (document1 != null && document1.exists()) {
                                                    String name = document1.getString("name");
                                                    if (name != null) {
                                                        allUsers.add(new AllUserModel(name, status));

                                                        // Notify the adapter that data has changed
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                }
                                            } else {
                                                Log.e("AllUser", "Error fetching user name", task1.getException());
                                            }
                                        });
                            }
                        }
                    } else {
                        Log.e("AllUser", "Error fetching participants", task.getException());
                        Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
