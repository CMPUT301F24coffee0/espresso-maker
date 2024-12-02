package com.example.espresso.Admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.espresso.Attendee.User;
import com.example.espresso.MainActivity;
import com.example.espresso.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    Button logout;

    EditText name_text;
    EditText email_text;

    String name;
    String email;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String deviceID;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Handles all actions inside ProfileFragment including showing user email and name, and editing user email and name
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return View Object
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        logout = view.findViewById(R.id.logout_button);
        logout.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            startActivity(intent);
        });

        deviceID = new User(requireContext()).getDeviceID();

        ImageView edit = view.findViewById(R.id.imageView);

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
                ((TextView) view.findViewById(R.id.email)).setText(String.format("Email: %s", email));
            } else {
                Log.d("user", "No such document");
            }
        });

        edit.setOnClickListener(
                v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                    builder.setTitle("Edit Profile");
                    builder.setMessage("Do you want to save changes?");

                    LayoutInflater inflater2 = getLayoutInflater();
                    View dialogView = inflater2.inflate(R.layout.edit_profile_dialog, null);
                    builder.setView(dialogView);

                    name_text = dialogView.findViewById(R.id.edit_name);
                    email_text = dialogView.findViewById(R.id.edit_email);
                    dialogView.findViewById(R.id.edit_phone_number);

                    // Pre-fill the dialog fields with current profile data
                    name_text.setText(name);
                    email_text.setText(email);

                    dialogView.findViewById(R.id.edit_phone_number).setVisibility(View.GONE);

                    builder.setPositiveButton("Save", null);
                    builder.setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

                    AlertDialog dialog = builder.create();
                    dialog.setOnShowListener(dialogInterface -> {
                        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        positiveButton.setOnClickListener(v1 -> {
                            name_text.setError(null);
                            email_text.setError(null);

                            String newName = name_text.getText().toString().trim();
                            String newEmail = email_text.getText().toString().trim();


                            boolean isValid;
                            if (newName.isEmpty()) {
                                name_text.setError("Name must be non-empty");
                                isValid = false;
                            }
                            else if (newEmail.isEmpty() || !newEmail.matches("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
                                email_text.setError("Enter a valid email");
                                isValid = false;
                            }
                            else isValid = true;


                            if (isValid) {
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("name", newName);
                                updates.put("email", newEmail);

                                docRef.update(updates)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("user", "DocumentSnapshot successfully updated!");

                                            ((TextView) view.findViewById(R.id.name)).setText(newName);
                                            ((TextView) view.findViewById(R.id.email)).setText(String.format("Email: %s", newEmail));
                                            dialog.dismiss();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.w("user", "Error updating document", e);
                                            Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        });
                    }); dialog.show();
                }
        ); return view;
    }
}
