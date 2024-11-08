package com.example.espresso;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.util.Log;
import android.view.View;
import com.example.espresso.databinding.ActivityAttendeeMyEventBinding;

import java.util.Objects;

/**
 * AttendeeMyEvent class displays the "My Events" screen for attendees, where they can navigate
 * through various event-related tabs using a ViewPager2 and a TabLayout. It includes navigation
 * to home, scan, and profile screens through a BottomNavigationView.
 */
public class AttendeeMyEvent extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ViewPageAdapter adapter;
    ActivityAttendeeMyEventBinding binding;

    /**
     * Initializes the activity, sets up the BottomNavigationView navigation, and configures
     * the TabLayout and ViewPager2 for event category navigation.
     *
     * @param savedInstanceState If the activity is being re-initialized, this contains the most recent data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttendeeMyEventBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.bottomNavigationView.setSelectedItemId(R.id.events);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                Intent intent = new Intent(AttendeeMyEvent.this, AttendeeHomeActivity.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.scan) {
                Intent intent = new Intent(AttendeeMyEvent.this, ScanQR.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.profile) {
                Intent intent = new Intent(AttendeeMyEvent.this, AttendeeProfile.class);
                startActivity(intent);
            }
            return true;
        });

        tabLayout = findViewById(R.id.tabs);
        viewPager2 = findViewById(R.id.view_pager);
        adapter = new ViewPageAdapter(this);
        viewPager2.setAdapter(adapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
                Objects.requireNonNull(tabLayout.getTabAt(position)).select();
            }
        });
    }
}
