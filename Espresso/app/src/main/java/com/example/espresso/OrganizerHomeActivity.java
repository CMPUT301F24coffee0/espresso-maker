package com.example.espresso;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
/**
 * This activity represents the home screen for the organizer, featuring a bottom navigation bar to navigate between different fragments.
 * The activity contains options for viewing the home, facilities, and profile sections.
 */
public class OrganizerHomeActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created. This method sets up the layout, bottom navigation, and loads the default fragment.
     * It applies window insets for proper padding to accommodate system bars and handles navigation item selections.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_home);

        // Adjust padding for system bars (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.organizer_home_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Load the default fragment if no saved instance state
        if (savedInstanceState == null) loadFragment(new OrganizerHomeFragment());
    }

    /**
     * Listener for navigation item selection in the bottom navigation bar.
     * It switches between different fragments based on the selected item.
     */
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                // Select fragment based on the item clicked in the bottom navigation
                if (item.getItemId() == R.id.nav_home_organizer) {
                    selectedFragment = new OrganizerHomeFragment();
                } else if (item.getItemId() == R.id.nav_facilities_organizer) {
                    selectedFragment = new OrganizerFacility();
                } else if (item.getItemId() == R.id.nav_profile_organizer) {
                    selectedFragment = new OrganizerProfile();
                }

                // If a fragment is selected, load it
                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                }
                return true;
            };

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

    /**
     * Launches the form to create a new event. This method starts the `NewEventForm` activity.
     *
     * @param view The view that was clicked to trigger this method.
     */
    public void launchEventForm(View view) {
        Intent intent = new Intent(this, NewEventForm.class);
        intent.putExtra("type", "new");
        startActivity(intent);
    }
}
