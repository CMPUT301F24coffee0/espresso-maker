package com.example.espresso.Attendee;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.espresso.MainActivity;
import com.example.espresso.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
/**
 * The AttendeeProfile activity allows users to view and edit their profile information,
 * including name, email, phone, and profile picture, which are retrieved from Firebase.
 * It also provides navigation to other activities via a BottomNavigationView.
 */
public class AttendeeProfile extends Fragment {
    String deviceID, name, email, phone, type;
    Button logout;
    TextView initial;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference pfpsRef;
    View view;

    private static final int GALLERY_REQUEST_CODE = 101;  // Request code for gallery
    private ImageButton profilePicButton;
    /**
     * Initializes the profile activity, setting up Firebase connections, navigation,
     * profile image retrieval, and profile data retrieval.
     *
     * @param savedInstanceState the saved instance state, if any, to restore the activity's state
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_profile_attendee, container, false);
        deviceID = new User(requireContext()).getDeviceID();

        if (deviceID == null) {
            Log.d("user", "Device ID is null");
            return view;
        }

        initial = view.findViewById(R.id.initial);


        String path = "pfps/"+deviceID+".png";
        pfpsRef = storageRef.child(path);
        // Fetch profile picture from Firebase Storage
        pfpsRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Got the download URL for pfp
            Log.d("user", "Got download URL for pfp");
            initial.setVisibility(View.GONE);
            Picasso.get().load(uri).into(profilePicButton);
        }).addOnFailureListener(exception -> {
            // Handle any errors
            Log.e("user", path);
            Log.e("user", "Error getting download URL for pfp", exception);
        });


        logout = view.findViewById(R.id.button);
        logout.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                FirebaseAuth.getInstance().signOut();
                Log.d("user", "User signed out");
            }
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            startActivity(intent);
        });

        // Remove profile picture button
        Button removeProfilePicButton = view.findViewById(R.id.removeProfilePicButton);
        removeProfilePicButton.setOnClickListener(v -> {
            // Remove the profile picture from Firebase Storage
            pfpsRef.delete().addOnSuccessListener(aVoid -> {
                Toast.makeText(requireContext(), "Profile picture removed successfully.", Toast.LENGTH_SHORT).show();
                initial.setVisibility(View.VISIBLE);
            }).addOnFailureListener(e -> {
                Toast.makeText(requireContext(), "You can't remove this profile picture.", Toast.LENGTH_SHORT).show();
            });
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
                name = Objects.requireNonNullElse(data.get("name"), "Default Name").toString();
                initial.setText(name.substring(0, 1));
                email = Objects.requireNonNull(data.get("email")).toString();
                phone = Objects.requireNonNull(data.get("phone")).toString();
                type = Objects.requireNonNull(data.get("type")).toString();

                ((TextView) view.findViewById(R.id.name)).setText(name);
                ((TextView) view.findViewById(R.id.email)).setText(String.format("Email: %s", email));
                ((TextView) view.findViewById(R.id.phoneNumber)).setText(String.format("Phone Number: %s", phone));
                ((TextView) view.findViewById(R.id.role)).setText(String.format("Role: %s", type));
            } else {
                Log.d("user", "No such document");
            }
        });

        view.findViewById(R.id.edit_profile_button).setOnClickListener(
                v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                    builder.setTitle("Edit Profile");
                    builder.setMessage("Do you want to save changes?");

                    LayoutInflater inflater2 = getLayoutInflater();
                    View dialogView = inflater2.inflate(R.layout.edit_profile_dialog, null);
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

                                    ((TextView) view.findViewById(R.id.name)).setText(newName);
                                    ((TextView) view.findViewById(R.id.email)).setText(String.format("Email: %s", newEmail));
                                    ((TextView) view.findViewById(R.id.phoneNumber)).setText(String.format("Phone Number: %s", newPhone));
                                    ((TextView) view.findViewById(R.id.role)).setText(String.format("Role: %s", type));
                                })
                                .addOnFailureListener(e -> Log.w("user", "Error updating document", e));
                    });

                    builder.setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
        );

        // Set up the profile picture button
        profilePicButton = view.findViewById(R.id.profilePicButton);
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        // Permission already granted, proceed with file access
        profilePicButton.setOnClickListener(this::selectProfileImage);

        return view;
    }
    /**
     * Opens the image gallery for selecting a profile picture.
     *
     * @param view The view that triggers this method.
     */
    // Method called by the profilePicButton click
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == 100 && data != null) {
            Uri selectedImageUri = data.getData();
            profilePicButton.setImageURI(selectedImageUri); // Display the selected image in the ImageButton

            if (selectedImageUri != null) {
                try {
                    // Open an InputStream from the content URI
                    InputStream inputStream = requireContext().getContentResolver().openInputStream(selectedImageUri);
                    // Upload the InputStream to Firebase Storage
                    UploadTask uploadTask = pfpsRef.putStream(inputStream);
                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(requireContext(), "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                        initial.setVisibility(View.GONE);

                        // Optionally, update the displayed image by reloading it from Firebase Storage
                        pfpsRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            Picasso.get().load(uri).into(profilePicButton); // Load image from Firebase Storage
                        }).addOnFailureListener(exception -> {
                            Log.e("user", "Error fetching uploaded image URL", exception);
                        });
                    }).addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Failed to upload image.", Toast.LENGTH_SHORT).show();
                        Log.e("user", "Error uploading image", e);
                    });

                } catch (Exception e) {
                    Log.e("user", "Error opening InputStream", e);
                    Toast.makeText(requireContext(), "Error opening image", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("user", "Selected image URI is null.");
            }
        }
    }

}

