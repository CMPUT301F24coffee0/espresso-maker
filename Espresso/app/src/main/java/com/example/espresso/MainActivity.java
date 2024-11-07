package com.example.espresso;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button attendee_sign_in_btn, org_sign_in_btn, adm_sign_in_btn;
    String deviceID;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    boolean isLoggedIn = false, isAttendee = false, isOrganizer = false, isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.start_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.landing_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get device ID
        deviceID = new User(this).getDeviceID();
        DocumentReference docRef = db.collection("users").document(deviceID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        isLoggedIn = true;
                        Log.d("auth", "User exists" + document.getData());
                        String type = document.getString("type");
                        if (type.contains("admin")){
                            isAdmin = true;
                        }
                        if (type.contains("attendee")){
                            isAttendee = true;
                        }
                        if (type.contains("organizer")){
                            isOrganizer = true;
                        }
                    } else {
                        Log.d("auth", "User does not exist");
                        isLoggedIn = false;
                    }
                } else {
                    Log.d("auth", "get failed with ", task.getException());
                }
            }
        });

        attendee_sign_in_btn = findViewById(R.id.AttendeeSignInButton);
        org_sign_in_btn = findViewById(R.id.OrganizerSignInButton);
        adm_sign_in_btn = findViewById(R.id.AdminSignInButton);

        attendee_sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoggedIn) {
                    if (isAttendee) {
                        // Start Attendee AttendeeDashboard
                        Intent intent = new Intent(MainActivity.this, AttendeeDashboard.class);
                        startActivity(intent);
                    } else {
                        // Create a pop-up window saying that the user is not an attendee
                        Log.d("auth", "User is not an attendee");
                    }
                } else {
                    // Create a new user
                    DocumentReference newUser = db.collection("users").document(deviceID);
                    Map<String, Object> docData = new HashMap<>();
                    docData.put("type", "attendee");
                    docData.put("deviceID", deviceID);
                    docData.put("name", "P Diddy");
                    docData.put("email", "");
                    docData.put("phone", "");
                    docData.put("facility", "");
                    newUser.set(docData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("add_user", "DocumentSnapshot successfully written!");
                                    // Start Attendee AttendeeDashboard
                                    isLoggedIn = true;
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("add_user", "Error writing document", e);
                                }
                            });

                }
            }
        });

        org_sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoggedIn) {
                    if (isOrganizer) {
                        // Start Organizer AttendeeDashboard
                    } else {
                        // Create a pop-up window saying that the user is not an attendee
                        Log.d("auth", "User is not an organizer");
                    }
                } else {
                    // Create a new user
                    DocumentReference newUser = db.collection("users").document(deviceID);
                    Map<String, Object> docData = new HashMap<>();
                    docData.put("type", "organizer");
                    docData.put("deviceID", deviceID);
                    docData.put("name", "P Diddy");
                    docData.put("email", null);
                    docData.put("phone", null);
                    docData.put("facility", null);
                    newUser.set(docData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("add_user", "DocumentSnapshot successfully written!");
                                    // Start Organizer AttendeeDashboard
                                    isLoggedIn = true;
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("add_user", "Error writing document", e);
                                }
                            });
                }
            }
        });

        adm_sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoggedIn && isAdmin) {
                    // Start Admin AttendeeDashboard
                } else {
                    // Create a pop-up window saying that the user is not an admin
                    Log.d("auth", "User is not an admin");
                }
            }
        });
    }
}