package com.example.espresso;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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


public class SignUp extends AppCompatActivity {
    private TextView adminTab, organizerTab;
    private EditText facilityNameField, fullNameField, emailField, passwordField, confirmPasswordField;
    private Button signUpButton_admin;
    private FirebaseAuth mAuth;
    private String type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_signup);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        adminTab = findViewById(R.id.admin_tab);
        organizerTab = findViewById(R.id.organizer_tab);
        facilityNameField = findViewById(R.id.facility_name);
        fullNameField = findViewById(R.id.full_name);
        emailField = findViewById(R.id.email_address);
        passwordField = findViewById(R.id.password);
        confirmPasswordField = findViewById(R.id.confirm_password);
        signUpButton_admin = findViewById(R.id.AdminSignUpButton);


        selectAdminTab();

        adminTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAdminTab();
            }
        });

        organizerTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOrganizerTab();
            }
        });

        signUpButton_admin.setOnClickListener(v -> {
            if (inputConstraint() && isVisible()) {
                // Proceed with sign-up logic for database then start new activity for organizer screen based on facility name field visibility screen
                signUp(db, emailField.getText().toString(), passwordField.getText().toString(), fullNameField.getText().toString(), facilityNameField.getText().toString(), type, null, null);
            }

            else if (inputConstraint() && !isVisible()) {
                // Proceed with sign-up logic for database then start new activity for admin screen based on facility name field visibility screen
                signUp(db, emailField.getText().toString(), passwordField.getText().toString(), fullNameField.getText().toString(), null, type, null, null);
            }
        });
    }

    private void selectAdminTab() {
        type = "admin";
        adminTab.setBackgroundResource(R.drawable.selected_tab_selector);
        organizerTab.setBackgroundResource(R.drawable.unselected_tab_selector);
        adminTab.setTextColor(getResources().getColor(R.color.black, getTheme()));
        organizerTab.setTextColor(getResources().getColor(R.color.blueGrey, getTheme()));
        facilityNameField.setVisibility(View.GONE);
    }

    private void selectOrganizerTab() {
        type = "organizer";
        adminTab.setBackgroundResource(R.drawable.unselected_tab_selector);
        organizerTab.setBackgroundResource(R.drawable.selected_tab_selector);
        facilityNameField.setVisibility(View.VISIBLE);
    }

    private boolean isVisible() {
        return facilityNameField.getVisibility() == View.VISIBLE;
    }

    private boolean inputConstraint() {
        if (fullNameField.length() == 0) {
            fullNameField.setError("Field Required");
            return false;
        }
        if (passwordField.length() == 0) {
            passwordField.setError("Field Required");
            return false;
        }
        if (confirmPasswordField.length() == 0) {
            confirmPasswordField.setError("Field Required");
            return false;
        }
        if (facilityNameField.getVisibility() == View.VISIBLE && facilityNameField.length() == 0) {
            facilityNameField.setError("Field Required");
            return false;
        }
        return true;
    }

    private void signUp(FirebaseFirestore db, String email, String password, String name, String facilityName, String type, String phone, String device_id) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("auth", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            String uid = user.getUid();

                            // Set user's basic information
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
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
                            userInfo.put("type", type);
                            userInfo.put("facilityName", facilityName);
                            userInfo.put("phone", phone);
                            userInfo.put("device_id", device_id);

                            db.collection("users").document(uid)
                                    .set(userInfo)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("user_info", "users' info successfully written!");
                                            // Start new activity for organizer screen based on facility name field visibility screen
                                            Toast.makeText(SignUp.this, "sign in successfully.",
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
                            // If sign up fails, display a message to the user.
                            Log.w("auth", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
