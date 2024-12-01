package com.example.espresso.Admin;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.espresso.R;
import com.example.espresso.Attendee.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    private ListView usersListView;
    private com.example.espresso.Admin.UsersListAdapter usersListAdapter;
    private List<User> usersList;
    private FirebaseFirestore db;

    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize the ListView
        usersListView = view.findViewById(R.id.user_list_view);

        // Initialize the users list and adapter
        usersList = new ArrayList<>();
        usersListAdapter = new com.example.espresso.Admin.UsersListAdapter(getContext(), usersList);
        usersListView.setAdapter(usersListAdapter);

        // Fetch users from Firestore and update the ListView
        fetchUsersFromFirestore();

        // Set item click listener
        usersListView.setOnItemClickListener((parent, view1, position, id) -> {
            User selectedUser = usersList.get(position);
            showDeleteConfirmationDialog(getContext(), selectedUser);
        });

        return view;
    }

    /**
     * Fetch the list of users from Firestore.
     */
    private void fetchUsersFromFirestore() {
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Clear the previous list
                        usersList.clear();

                        // Process the results and add each user to the list
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = new User(getContext());
                            String deviceID = document.getString("deviceID");

                            if (deviceID != null) {
                                user.setDeviceID(deviceID);
                                usersList.add(user);
                            }
                        }

                        // Notify the adapter that the data has changed
                        usersListAdapter.notifyDataSetChanged();
                    } else {
                        // Handle the error
                        Toast.makeText(getContext(), "Error fetching users", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Show a confirmation dialog to delete the user.
     */
    private void showDeleteConfirmationDialog(Context context, User user) {
        new AlertDialog.Builder(context)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete this user?")
                .setPositiveButton("Yes", (dialog, which) -> deleteUser(user))
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Delete the user from Firestore.
     */
    private void deleteUser(User user) {
        String deviceID = user.getDeviceID();

        // Remove the user from the 'users' collection
        db.collection("users")
                .whereEqualTo("deviceID", deviceID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            document.getReference().delete();
                        }

                        // Remove the user from all 'participants' sub-collections
                        db.collection("events")
                                .get()
                                .addOnCompleteListener(eventTask -> {
                                    if (eventTask.isSuccessful()) {
                                        for (QueryDocumentSnapshot eventDoc : eventTask.getResult()) {
                                            eventDoc.getReference()
                                                    .collection("participants")
                                                    .whereEqualTo("deviceID", deviceID)
                                                    .get()
                                                    .addOnCompleteListener(participantTask -> {
                                                        if (participantTask.isSuccessful()) {
                                                            for (QueryDocumentSnapshot participantDoc : participantTask.getResult()) {
                                                                participantDoc.getReference().delete();
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });

                        // Update the UI
                        usersList.remove(user);
                        usersListAdapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "User deleted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error deleting user", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
