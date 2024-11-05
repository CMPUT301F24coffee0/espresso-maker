package com.example.espresso;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.espresso.databinding.EntrantProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;


public class AttendeeProfile extends AppCompatActivity {
    EntrantProfileBinding binding;
    String deviceID, name, email, phone, type;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = EntrantProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        deviceID = new User(this).getDeviceID();
        if (deviceID == null) {
            Log.d("user", "Device ID is null");
            return;
        }
        // Navigation
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.events) {
                // Open events activity
                Log.d("BottomNav", "Events clicked");
            }
            else if (item.getItemId() == R.id.scan) {
                // Open scan activity
                Log.d("BottomNav", "Scan clicked");
            }
            else if (item.getItemId() == R.id.home) {
                // Open profile activity
                Log.d("BottomNav", "Home clicked");
                Intent intent = new Intent(AttendeeProfile.this, AttendeeDashboard.class);
                startActivity(intent);
            }
            return true;
        });

        //Fetching data from Firebase
        DocumentReference docRef = db.collection("users").document(deviceID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("user", "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> data = document.getData();
                        name = data.get("name").toString();
                        email = data.get("email").toString();
                        phone = data.get("phone").toString();
                        type = data.get("type").toString();

                        //Setting data to textviews
                        binding.name.setText(name);
                        binding.email.setText(String.format("Email: %s", email));
                        binding.phoneNumber.setText(String.format("Phone Number: %s", phone));
                        binding.role.setText(String.format("Role: %s", type));

                    } else {
                        Log.d("user", "No such document");
                    }
                } else {
                    Log.d("user", "get failed with ", task.getException());
                }
            }
        });
    }

}