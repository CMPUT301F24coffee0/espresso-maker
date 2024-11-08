package com.example.espresso;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
/**
 * OrganizerHomeActivity is the main activity for the organizer's home screen.
 * It provides navigation to different sections such as home, facilities, and profile for the organizer.
 */
public class OrganizerHomeActivity extends AppCompatActivity {

    /**
     * Called when the activity is created. Initializes the UI, sets up navigation, and loads the default fragment.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_home);  // Sets the layout for the organizer's home screen

        // Adjust the window insets for edge-to-edge UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.organizer_home_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize and configure the bottom navigation view
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Load the default fragment (OrganizerHomeFragment) when the activity is first created
        if (savedInstanceState == null) {
            loadFragment(new OrganizerHomeFragment());
        }
    }

    /**
     * Listener for the bottom navigation view that handles item selection and loads the corresponding fragment.
     */
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                // Check which item is selected and assign the corresponding fragment
                if (item.getItemId() == R.id.nav_home_organizer) {
                    selectedFragment = new OrganizerHomeFragment();
                } else if (item.getItemId() == R.id.nav_facilities_organizer) {
                    selectedFragment = new OrganizerFacility();
                } else if (item.getItemId() == R.id.nav_profile_organizer) {
                    selectedFragment = new OrganizerProfile();
                }

                // If the selected fragment is not null, load the fragment
                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                }
                return true;
            };

    /**
     * Helper method to load a fragment into the fragment container.
     *
     * @param fragment The fragment to load.
     */
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment); // Replace the current fragment with the selected fragment
        transaction.commit();  // Commit the transaction
    }
}
