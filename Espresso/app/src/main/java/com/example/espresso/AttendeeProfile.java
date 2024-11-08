package com.example.espresso;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.espresso.databinding.AttendeeProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * AttendeeProfile is an activity that displays and manages the user's profile information.
 * It includes functionality for viewing, editing, and updating profile details,
 * as well as uploading a profile picture.
 *
 * This class interacts with Firebase Firestore and Firebase Storage to store and retrieve
 * user data and profile images. It also handles permissions for accessing media on the device.
 */
public class AttendeeProfile extends AppCompatActivity {

    /**
     * Request codes for selecting an image and requesting permissions.
     */
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST = 2;

    // Variables for Firebase instances, user data, and UI components
    private AttendeeProfileBinding binding;
    private String deviceID, name, email, phone, type, profileImageUrl;
    private Button logout;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ImageView editProfile;
    private Uri imageUri;

    /**
     * Called when the activity is first created.
     * Initializes Firebase, sets up views, and loads user data.
     *
     * @param savedInstanceState Saved instance state bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase instances and set up view binding
        binding = AttendeeProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        deviceID = new User(this).getDeviceID();
        if (deviceID == null) {
            Log.e("AttendeeProfile", "Device ID is null");
            Toast.makeText(this, "Error: Could not get device ID", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setupViews();
        loadImageFromPreferences();
        fetchDataFromFirebase();
    }

    /**
     * Sets up views and their event listeners, including logout and edit profile options.
     */
    private void setupViews() {
        logout = findViewById(R.id.button);
        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AttendeeProfile.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        editProfile = findViewById(R.id.profilePic);
        editProfile.setOnClickListener(v -> checkAndRequestPermissions());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.events) {
                startActivity(new Intent(AttendeeProfile.this, AttendeeMyEvent.class));
                finish();
            } else if (itemId == R.id.home) {
                startActivity(new Intent(AttendeeProfile.this, AttendeeHomeActivity.class));
                finish();
            }
            return true;
        });

        findViewById(R.id.edit_profile_button).setOnClickListener(v -> showEditProfileDialog());
    }

    /**
     * Checks and requests necessary permissions for accessing media on the device.
     */
    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST);
            } else {
                openImageChooser();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST);
            } else {
                openImageChooser();
            }
        }
    }

    /**
     * Handles the result of a permission request.
     *
     * @param requestCode  The request code for the permission.
     * @param permissions  The requested permissions.
     * @param grantResults The results for the requested permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageChooser();
            } else {
                Toast.makeText(this, "Permission denied. Cannot select image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Opens an image chooser to allow the user to select a profile picture.
     */
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * Handles the result of an image selection.
     *
     * @param requestCode The request code for the activity result.
     * @param resultCode  The result code from the activity.
     * @param data        The intent data containing the selected image.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                editProfile.setImageBitmap(bitmap);
                uploadImageToFirebase();
            } catch (IOException e) {
                Log.e("AttendeeProfile", "Error loading image", e);
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Uploads the selected image to Firebase Storage and updates the Firestore profile data.
     */
    private void uploadImageToFirebase() {
        if (imageUri != null) {
            AlertDialog progressDialog = new AlertDialog.Builder(this)
                    .setMessage("Uploading image...")
                    .setCancelable(false)
                    .show();

            Log.d("FirebaseUpload", "Starting upload process");
            Log.d("FirebaseUpload", "Device ID: " + deviceID);
            Log.d("FirebaseUpload", "Image URI: " + imageUri.toString());

            StorageReference profileRef = storageReference.child("profile_images/" + deviceID + ".jpg");

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpeg")
                    .build();

            profileRef.putFile(imageUri, metadata)
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploading: " + (int)progress + "%");
                    })
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d("FirebaseUpload", "Upload successful!");

                        profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            Log.d("FirebaseUpload", "Download URL obtained: " + uri.toString());

                            Map<String, Object> updates = new HashMap<>();
                            updates.put("profileImageUrl", uri.toString());

                            db.collection("users").document(deviceID)
                                    .update(updates)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("FirebaseUpload", "Firestore updated with new image URL");
                                        saveImageToPreferences(uri.toString());
                                        Picasso.get()
                                                .load(uri)
                                                .into(editProfile);
                                        progressDialog.dismiss();
                                        Toast.makeText(AttendeeProfile.this,
                                                "Profile picture updated successfully!",
                                                Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("FirebaseUpload", "Error updating Firestore", e);
                                        progressDialog.dismiss();
                                        Toast.makeText(AttendeeProfile.this,
                                                "Failed to update profile: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirebaseUpload", "Upload failed", e);
                        progressDialog.dismiss();
                        Toast.makeText(AttendeeProfile.this,
                                "Upload failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
        }
    }

    /**
     * Displays a dialog to edit profile details (name, email, phone).
     */
    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AttendeeProfile.this);
        builder.setTitle("Edit Profile");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_profile_dialog, null);
        builder.setView(dialogView);

        EditText nameText = dialogView.findViewById(R.id.edit_name);
        EditText emailText = dialogView.findViewById(R.id.edit_email);
        EditText phoneText = dialogView.findViewById(R.id.edit_phone_number);

        nameText.setText(name);
        emailText.setText(email);
        phoneText.setText(phone);

        builder.setPositiveButton("Save", (dialog, id) -> {
            String newName = nameText.getText().toString();
            String newEmail = emailText.getText().toString();
            String newPhone = phoneText.getText().toString();

            if (newName.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty()) {
                Toast.makeText(AttendeeProfile.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("name", newName);
            updates.put("email", newEmail);
            updates.put("phone", newPhone);

            db.collection("users").document(deviceID)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AttendeeProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        name = newName;
                        email = newEmail;
                        phone = newPhone;
                        updateUI();
                    })
                    .addOnFailureListener(e -> Toast.makeText(AttendeeProfile.this, "Error updating profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        builder.setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Fetches user data from Firebase Firestore and updates the UI.
     */
    private void fetchDataFromFirebase() {
        db.collection("users").document(deviceID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        name = documentSnapshot.getString("name");
                        email = documentSnapshot.getString("email");
                        phone = documentSnapshot.getString("phone");
                        profileImageUrl = documentSnapshot.getString("profileImageUrl");

                        if (profileImageUrl != null) {
                            Picasso.get()
                                    .load(profileImageUrl)
                                    .into(editProfile);
                        }

                        updateUI();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AttendeeProfile", "Error fetching data from Firestore", e);
                    Toast.makeText(AttendeeProfile.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Updates the UI with the fetched user data.
     */
    private void updateUI() {
        binding.name.setText(name);
        binding.email.setText(email);
        binding.phoneNumber.setText(phone);
    }

    /**
     * Loads the profile image URL from shared preferences.
     */
    private void loadImageFromPreferences() {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        profileImageUrl = preferences.getString("profileImageUrl", null);

        if (profileImageUrl != null) {
            Picasso.get().load(profileImageUrl).into(editProfile);
        }
    }

    /**
     * Saves the profile image URL to shared preferences.
     *
     * @param url The URL of the profile image.
     */
    private void saveImageToPreferences(String url) {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("profileImageUrl", url);
        editor.apply();
    }
}
