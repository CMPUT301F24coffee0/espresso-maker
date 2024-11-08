package com.example.espresso;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.espresso.databinding.AttendeeProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import android.widget.ImageButton;
import android.widget.Toast;
import android.Manifest;

/**
 * The AttendeeProfile activity allows users to view and edit their profile information,
 * including name, email, phone, and profile picture, which are retrieved from Firebase.
 * It also provides navigation to other activities via a BottomNavigationView.
 */
public class AttendeeProfile extends AppCompatActivity {

    AttendeeProfileBinding binding;
    String deviceID, name, email, phone, type;
    Button logout;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference pfpsRef;
    private static final int GALLERY_REQUEST_CODE = 101;
    private ImageButton profilePicButton;

    /**
     * Initializes the profile activity, setting up Firebase connections, navigation,
     * profile image retrieval, and profile data retrieval.
     *
     * @param savedInstanceState the saved instance state, if any, to restore the activity's state
     */
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

        String path = "pfp/" + deviceID + ".png";
        pfpsRef = storageRef.child(path);

        pfpsRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.d("user", "Got download URL for pfp");
            Picasso.get().load(uri).into(profilePicButton);
        }).addOnFailureListener(exception -> {
            Log.e("user", path);
            Log.e("user", "Error getting download URL for pfp", exception);
        });

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

        binding.bottomNavigationView.setSelectedItemId(R.id.profile);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.events) {
                Intent intent = new Intent(AttendeeProfile.this, AttendeeMyEvent.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.scan) {
                Intent intent = new Intent(AttendeeProfile.this, ScanQR.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.home) {
                Intent intent = new Intent(AttendeeProfile.this, AttendeeHomeActivity.class);
                startActivity(intent);
            }
            return true;
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

        findViewById(R.id.edit_profile_button).setOnClickListener(v -> {
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
                        .addOnFailureListener(e1 -> Log.w("user", "Error updating document", e1));
            });

            builder.setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        profilePicButton = findViewById(R.id.profilePicButton);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        profilePicButton.setOnClickListener(this::selectProfileImage);
    }

    /**
     * Opens the image gallery for selecting a profile picture.
     *
     * @param view The view that triggers this method.
     */
    public void selectProfileImage(android.view.View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    /**
     * Handles the result from the image selection activity, uploading the selected image to Firebase Storage.
     *
     * @param requestCode The request code of the activity result.
     * @param resultCode The result code of the activity result.
     * @param data The intent containing the data returned by the activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            profilePicButton.setImageURI(selectedImageUri);

            if (selectedImageUri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                    UploadTask uploadTask = pfpsRef.putStream(inputStream);
                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                        pfpsRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            Picasso.get().load(uri).into(profilePicButton);
                        }).addOnFailureListener(exception -> {
                            Log.e("user", "Error fetching uploaded image URL", exception);
                        });
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                        Log.e("user", "Error uploading image", e);
                    });

                } catch (Exception e) {
                    Log.e("user", "Error opening InputStream", e);
                    Toast.makeText(this, "Error opening image", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("user", "Selected image URI is null.");
            }
        }
    }
}
