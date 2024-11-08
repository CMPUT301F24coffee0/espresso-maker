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

public class OrganizerHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.organizer_home_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        if (savedInstanceState == null)
            loadFragment(new OrganizerHomeFragment());
    }


    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.nav_home_organizer) {
                    selectedFragment = new OrganizerHomeFragment();
                } else if (item.getItemId() == R.id.nav_facilities_organizer) {
                    selectedFragment = new OrganizerFacility();
                } else if (item.getItemId() == R.id.nav_profile_organizer) {
                    selectedFragment = new OrganizerProfile();
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                }
                return true;
            };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }


    public void launchEventForm(View view) {
        Intent intent = new Intent(this, NewEventForm.class);
        intent.putExtra("type", "new");
        startActivity(intent);
    }
}