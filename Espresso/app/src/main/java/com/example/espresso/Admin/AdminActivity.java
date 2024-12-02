package com.example.espresso.Admin;

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
 * Launched after use chooses to continue as an Admin. Pathway into different Admin fragments
 */
public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Adjust padding for system bars (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the BottomNavigationView and set up the listener
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        // Set default fragment if no saved instance state
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment()); // Default fragment
        }

        // Bottom navigation item selection listener
        bottomNav.setOnNavigationItemSelectedListener(navListener);
    }

    /**
     * Bottom navigation listener to switch between fragments
     */
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment;

                // Select fragment based on the item clicked in the bottom navigation
                if (item.getItemId() == R.id.users) {
                    selectedFragment = new UsersFragment();
                    Log.d("AdminActivity", "UsersFragment selected");
                } else if (item.getItemId() == R.id.facilities) {
                    selectedFragment = new FacilitiesFragment();
                } else if (item.getItemId() == R.id.images) {
                    selectedFragment = new ImagesFragment();
                } else if (item.getItemId() == R.id.profile) {
                    selectedFragment = new ProfileFragment();
                } else {
                    selectedFragment = new HomeFragment(); // Default to HomeFragment
                }

                // Load the selected fragment
                loadFragment(selectedFragment);
                return true;
            };
    /**
     * Method to load fragments into the container
     */
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}
