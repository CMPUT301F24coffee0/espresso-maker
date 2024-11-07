package com.example.espresso;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.example.espresso.databinding.AttendeeProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AttendeeProfile extends AppCompatActivity {
    AttendeeProfileBinding binding;
    String deviceID, name, email, phone, type;
    Button logout;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = AttendeeProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        deviceID = new User(this).getDeviceID();

        if (deviceID == null) {
            Log.d("user", "Device ID is null");
            return;
        }

        logout = findViewById(R.id.button);
        logout.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                FirebaseAuth.getInstance().signOut();
                Log.d("user", "User signed out");
            }
            Intent intent = new Intent(AttendeeProfile.this, MainActivity.class);
            startActivity(intent);
        });

        // Navigation
        binding.bottomNavigationView.setSelectedItemId(R.id.profile);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.events) {
                // Open events activity
                Log.d("BottomNav", "Events clicked");
                Intent intent = new Intent(AttendeeProfile.this, AttendeeMyEvent.class);
                startActivity(intent);
            }
            else if (item.getItemId() == R.id.scan) {
                // Open scan activity
                Log.d("BottomNav", "Scan clicked");
            }
            else if (item.getItemId() == R.id.home) {
                // Open profile activity
                Log.d("BottomNav", "Home clicked");
                Intent intent = new Intent(AttendeeProfile.this, AttendeeHomeActivity.class);
                startActivity(intent);
            }
            return true;
        });

        //Fetching data from Firebase
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
                phone = Objects.requireNonNull(data.get("phone")).toString();
                type = Objects.requireNonNull(data.get("type")).toString();

                binding.name.setText(name);
                binding.email.setText(String.format("Email: %s", email));
                binding.phoneNumber.setText(String.format("Phone Number: %s", phone));
                binding.role.setText(String.format("Role: %s", type));
            } else {
                Log.d("user", "No such document");
            }
        });

        findViewById(R.id.edit_profile_button).setOnClickListener(
                v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AttendeeProfile.this);

                    builder.setTitle("Edit Profile");
                    builder.setMessage("Do you want to save changes?");

                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.edit_profile_dialog, null);
                    builder.setView(dialogView);

                    EditText name_text = dialogView.findViewById(R.id.edit_name);
                    EditText email_text = dialogView.findViewById(R.id.edit_email);
                    EditText phone_number_text = dialogView.findViewById(R.id.edit_phone_number);

                    name_text.setText(name);
                    email_text.setText(email);
                    phone_number_text.setText(phone);

                    builder.setPositiveButton("Save", (dialog, id) -> {
                        String newName = name_text.getText().toString();
                        String newEmail = email_text.getText().toString();
                        String newPhone = phone_number_text.getText().toString();

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("name", newName);
                        updates.put("email", newEmail);
                        updates.put("phone", newPhone);

                        docRef.update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("user", "DocumentSnapshot successfully updated!");

                                    binding.name.setText(newName);
                                    binding.email.setText(String.format("Email: %s", newEmail));
                                    binding.phoneNumber.setText(String.format("Phone Number: %s", newPhone));
                                })
                                .addOnFailureListener(e -> Log.w("user", "Error updating document", e));
                    });

                    builder.setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
        );


    }
}