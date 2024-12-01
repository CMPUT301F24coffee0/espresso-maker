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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    EditText name_text;
    EditText email_text;

    String name;
    String email;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String deviceID;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

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

        String deviceID = new User(requireContext()).getDeviceID();

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
                    });

                    dialog.show();
                }
        );


        return view;
    }
}
