package com.example.espresso;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
/**
 * This fragment displays the organizer's profile, allowing them to view and edit their information.
 * The profile includes the user's name and email, which are fetched from Firebase Firestore.
 * The fragment also provides the functionality for the user to log out and edit their profile.
 */
public class OrganizerProfile extends Fragment {

    private String name, email;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Called to inflate the fragment's layout and initialize UI components.
     * This method retrieves the organizer's profile data from Firestore, displays it,
     * and sets up the logout and profile editing functionality.
     *
     * @param inflater The LayoutInflater object to inflate the fragment's view.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_organizer_profile, container, false);

        // Get the device ID for the current user
        String deviceID = new User(getContext()).getDeviceID();

        if (deviceID == null) {
            Log.d("user", "Device ID is null");
            return view;
        }

        // Set up the logout button
        Button logout = view.findViewById(R.id.sign_out);
        logout.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                FirebaseAuth.getInstance().signOut();
                Log.d("user", "User signed out");
            }
            // Navigate back to the main activity (login screen)
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            startActivity(intent);
        });

        // Fetch and display the user's profile data from Firestore
        DocumentReference docRef = db.collection("users").document(deviceID);
        docRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.w("user", "Listen failed.", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                Map<String, Object> data = documentSnapshot.getData();
                assert data != null;

                // Extract user's name and email from the Firestore document
                name = Objects.requireNonNull(data.get("name")).toString();
                email = Objects.requireNonNull(data.get("email")).toString();

                // Update the UI with the fetched name and email
                ((TextView) view.findViewById(R.id.name)).setText(name);
                ((TextView) view.findViewById(R.id.email)).setText(String.format("Email: %s", email));
            } else {
                Log.d("user", "No such document");
            }
        });

        // Set up the profile settings button to allow editing the profile
        view.findViewById(R.id.settings).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            builder.setTitle("Edit Profile");
            builder.setMessage("Do you want to save changes?");

            // Inflate and set up the dialog for editing profile details
            LayoutInflater d = getLayoutInflater();
            View dialogView = d.inflate(R.layout.edit_profile_dialog, null);
            builder.setView(dialogView);

            EditText name_text = dialogView.findViewById(R.id.edit_name);
            EditText email_text = dialogView.findViewById(R.id.edit_email);
            dialogView.findViewById(R.id.edit_phone_number);

            // Pre-fill the dialog fields with current profile data
            name_text.setText(name);
            email_text.setText(email);

            // Hide the phone number field as it's not being edited
            dialogView.findViewById(R.id.edit_phone_number).setVisibility(View.GONE);

            builder.setPositiveButton("Save", (dialog, id) -> {
                String newName = name_text.getText().toString();
                String newEmail = email_text.getText().toString();

                // Prepare the updated data for Firestore
                Map<String, Object> updates = new HashMap<>();
                updates.put("name", newName);
                updates.put("email", newEmail);

                // Update the Firestore document with the new values
                docRef.update(updates)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("user", "DocumentSnapshot successfully updated!");
                            // Update the UI with the new name and email
                            ((TextView) view.findViewById(R.id.name)).setText(newName);
                            ((TextView) view.findViewById(R.id.email)).setText(String.format("Email: %s", newEmail));
                        })
                        .addOnFailureListener(e -> Log.w("user", "Error updating document", e));
            });

            builder.setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        return view;
    }
}
