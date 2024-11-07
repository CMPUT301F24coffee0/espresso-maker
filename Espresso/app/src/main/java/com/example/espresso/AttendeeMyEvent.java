package com.example.espresso;

import android.content.Intent;
import android.os.Bundle;

import com.example.espresso.databinding.EntrantHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.espresso.ui.main.SectionsPagerAdapter;
import com.example.espresso.databinding.ActivityAttendeeMyEventBinding;

public class AttendeeMyEvent extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ViewPageAdapter adapter;
    ActivityAttendeeMyEventBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttendeeMyEventBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        // Navigation
        binding.bottomNavigationView.setSelectedItemId(R.id.events);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                // Open Home activity
                Log.d("BottomNav", "Home clicked");
                Intent intent = new Intent(AttendeeMyEvent.this, AttendeeDashboard.class);
                startActivity(intent);
            }
            else if (item.getItemId() == R.id.scan) {
                // Open scan activity
                Log.d("BottomNav", "Scan clicked");
            }
            else if (item.getItemId() == R.id.profile) {
                // Open profile activity
                Log.d("BottomNav", "Profile clicked");
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
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
                tabLayout.getTabAt(position).select();
            }

        });
    }
}