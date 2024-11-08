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

public class OrganizerProfile extends Fragment {
    String name, email;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_profile, container, false);
        String deviceID = new User(getContext()).getDeviceID();

        if (deviceID == null) {
            Log.d("user", "Device ID is null");
            return view;
        }

        Button logout = view.findViewById(R.id.sign_out);

        logout.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                FirebaseAuth.getInstance().signOut();
                Log.d("user", "User signed out");
            }
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            startActivity(intent);
        });

        DocumentReference docRef = db.collection("users").document(deviceID);

        docRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.w("user", "Listen failed.", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                Map<String, Object> data = documentSnapshot.getData();

                assert data != null;
                name = Objects.requireNonNull(data.get("name")).toString();
                email = Objects.requireNonNull(data.get("email")).toString();

                ((TextView) view.findViewById(R.id.name)).setText(name);
                ((TextView)view.findViewById(R.id.email)).setText(String.format("Email: %s", email));
            } else {
                Log.d("user", "No such document");
            }
        });

        view.findViewById(R.id.settings).setOnClickListener(
                v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setTitle("Edit Profile");
                    builder.setMessage("Do you want to save changes?");

                    LayoutInflater d = getLayoutInflater();
                    View dialogView = d.inflate(R.layout.edit_profile_dialog, null);
                    builder.setView(dialogView);

                    EditText name_text = dialogView.findViewById(R.id.edit_name);
                    EditText email_text = dialogView.findViewById(R.id.edit_email);
                    dialogView.findViewById(R.id.edit_phone_number);

                    name_text.setText(name);
                    email_text.setText(email);

                    dialogView.findViewById(R.id.edit_phone_number).setVisibility(View.GONE);

                    builder.setPositiveButton("Save", (dialog, id) -> {
                        String newName = name_text.getText().toString();
                        String newEmail = email_text.getText().toString();

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("name", newName);
                        updates.put("email", newEmail);

                        docRef.update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("user", "DocumentSnapshot successfully updated!");

                                    ((TextView) view.findViewById(R.id.name)).setText(newName);
                                    ((TextView) view.findViewById(R.id.name)).setText(String.format("Email: %s", newEmail));
                                })
                                .addOnFailureListener(e -> Log.w("user", "Error updating document", e));
                    });

                    builder.setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
        );

        return view;
    }
}