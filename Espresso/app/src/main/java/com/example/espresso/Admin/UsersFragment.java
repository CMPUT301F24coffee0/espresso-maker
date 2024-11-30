package com.example.espresso.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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
        // Inflate the layout for this fragment
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
                            // Create a new User object with deviceID (assuming this is the unique identifier)
                            User user = new User(getContext());
                            // Assuming the deviceID is stored as a field in Firestore
                            String deviceID = document.getString("deviceID");

                            if (deviceID != null) {
                                // Set the deviceID in the user object
                                user.setDeviceID(deviceID);
                                usersList.add(user);
                            }
                        }

                        // Notify the adapter that the data has changed
                        usersListAdapter.notifyDataSetChanged();
                    } else {
                        // Handle the error
                        // e.g., show a Toast or Log the error
                    }
                });
    }
}
