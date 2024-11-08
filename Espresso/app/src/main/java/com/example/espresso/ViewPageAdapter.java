package com.example.espresso;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
/**
 * ViewPageAdapter is a custom adapter for managing fragments in a ViewPager.
 * It provides the necessary fragments based on the position and is used to
 * switch between different event categories (Confirmed, Pending, Declined).
 */
public class ViewPageAdapter extends FragmentStateAdapter {

    /**
     * Constructor for ViewPageAdapter.
     *
     * @param fragmentActivity The activity that hosts the fragments.
     */
    public ViewPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    /**
     * Creates and returns a fragment for the specified position.
     * This method is responsible for providing different fragments for each tab.
     *
     * @param position The position of the tab.
     * @return The fragment corresponding to the specified position.
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new PendingEvents();  // Fragment for pending events
            case 2:
                return new DeclinedEvents();  // Fragment for declined events
            default:
                return new ConfirmedEvents();  // Default fragment for confirmed events
        }
    }

    /**
     * Returns the number of fragments (pages) managed by the adapter.
     *
     * @return The number of fragments (pages) in the ViewPager.
     */
    @Override
    public int getItemCount() {
        return 3;  // We have three tabs: Confirmed, Pending, and Declined events
    }
}