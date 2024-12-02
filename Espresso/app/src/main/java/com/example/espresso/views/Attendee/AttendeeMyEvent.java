package com.example.espresso.views.Attendee;

import android.os.Bundle;

import com.example.espresso.R;
import com.example.espresso.controllers.Attendee.ViewPageAdapter;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

/**
 * AttendeeMyEvent class displays the "My Events" screen for attendees, where they can navigate
 * through various event-related tabs using a ViewPager2 and a TabLayout. It includes navigation
 * to home, scan, and profile screens through a BottomNavigationView.
 */
public class AttendeeMyEvent extends Fragment {
    View view;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ViewPageAdapter adapter;

    /**
     * Initializes the activity, sets up the BottomNavigationView navigation, and configures
     * the TabLayout and ViewPager2 for event category navigation.
     *
     * @param savedInstanceState If the activity is being re-initialized, this contains the most recent data.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_attendee_my_event, container, false);

        tabLayout = view.findViewById(R.id.tabs);
        viewPager2 = view.findViewById(R.id.view_pager);
        adapter = new ViewPageAdapter(requireActivity());
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
        return view;

    }
}
