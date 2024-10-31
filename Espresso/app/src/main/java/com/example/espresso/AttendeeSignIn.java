package com.example.espresso; 

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AttendeeSignIn extends AppCompatActivity {
    private EditText firstNameField, lastNameField, emailAddressField, phoneNumField;
    private Button signUpButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_signup);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        firstNameField = findViewById(R.id.first_name);
        lastNameField = findViewById(R.id.last_name);
        emailAddressField = findViewById(R.id.email_address);
        phoneNumField = findViewById(R.id.optional_phone_num);
        signUpButton = findViewById(R.id.attendeeSignUpButton);
        User user = new User(this);
        String device_id = user.getDeviceID();

        signUpButton.setOnClickListener(v -> {
            if (inputConstraint()) {
                // Proceed with sign-up logic for database then start new activity for attendee screen
                mAuth.signInAnonymously()
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("auth", "signInAnonymously:success");
                                    Toast.makeText(AttendeeSignIn.this, "Authentication succeeded.",
                                            Toast.LENGTH_SHORT).show();

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String uid = user.getUid();

                                    // Set user's basic information
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(firstNameField.getText().toString() + " " + lastNameField.getText().toString())
                                            .build();

                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("userinfo", "User profile updated.");
                                                    }
                                                }
                                            });

                                    Map<String, Object> userInfo = new HashMap<>();
                                    userInfo.put("name", firstNameField.getText().toString() + " " + lastNameField.getText().toString());
                                    userInfo.put("email", emailAddressField.getText().toString());
                                    userInfo.put("phone", phoneNumField.getText().toString().isEmpty() ? null : phoneNumField.getText().toString());
                                    userInfo.put("device_id", device_id);
                                    userInfo.put("type", "attendee");
                                    db.collection("users").document(uid)
                                            .set(userInfo)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("user_info", "users' info successfully written!");
                                                    // Start new activity for organizer screen based on facility name field visibility screen
                                                    Toast.makeText(AttendeeSignIn.this, "sign in successfully.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("userinfo", "Error writing document", e);
                                                }
                                            });
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("auth", "signInAnonymously:failure", task.getException());
                                    Toast.makeText(AttendeeSignIn.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private boolean inputConstraint() {
        if (firstNameField.length() == 0) {
            firstNameField.setError("Field Required");
            return false;
        }
        if (lastNameField.length() == 0) {
            lastNameField.setError("Field Required");
            return false;
        }
        if (emailAddressField.length() == 0) {
            emailAddressField.setError("Field Required");
            return false;
        }
        return true;
    }
}
