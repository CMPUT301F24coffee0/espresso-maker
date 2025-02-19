package com.example.espresso.Attendee;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.espresso.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * The AttendeeHomeActivity class is responsible for displaying the attendee home screen,
 * where users can view a list of events, scan QR codes, and access their profiles.
 * It interacts with Firebase Firestore to fetch event data and uses a BottomNavigationView
 * for navigation to different activities.
 */
public class AttendeeHomeActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created. Sets up the view, initializes Firebase Firestore,
     * sets up the BottomNavigationView listener, and loads event data.
     *
     * @param savedInstanceState If the activity is being re-initialized, this contains the most recent data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.attendee_home_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;});

        // Set up bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Check if there is a target fragment passed in the Intent
        if (savedInstanceState == null) {
            String targetFragment = getIntent().getStringExtra("targetFragment");
            if ("AttendeeMyEvent".equals(targetFragment)) {
                bottomNav.setSelectedItemId(R.id.events); // Update the BottomNavigationView selection
                loadFragment(new AttendeeMyEvent());
            } else {
                loadFragment(new AttendeeHomeFragment()); // Default fragment
            }
        }
    }

    /**
     * Listener for navigation item selection in the bottom navigation bar.
     * It switches between different fragments based on the selected item.
     */
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                // Select fragment based on the item clicked in the bottom navigation
                if (item.getItemId() == R.id.events) selectedFragment = new AttendeeMyEvent();
                else if (item.getItemId() == R.id.scan) {
                    Log.d("BottomNav", "Scan clicked");
                    Intent intent = new Intent(this, ScanQR.class);
                    startActivity(intent);
                }
                else if (item.getItemId() == R.id.profile) selectedFragment = new AttendeeProfile();
                else if (item.getItemId() == R.id.home) selectedFragment = new AttendeeHomeFragment();
                if (selectedFragment != null) loadFragment(selectedFragment);
                return true;};

    /**
     * Loads the specified fragment into the container of the activity.
     * This replaces the current fragment with the new one.
     *
     * @param fragment The fragment to be loaded.
     */
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}