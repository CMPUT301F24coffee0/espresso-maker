package com.example.espresso;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
/**
 * This class is an adapter for managing the fragments in a ViewPager2 widget.
 * It provides the appropriate fragment based on the position within the ViewPager.
 * The fragments displayed correspond to different types of events: confirmed, pending, and declined.
 */
public class ViewPageAdapter extends FragmentStateAdapter {

    /**
     * Constructor for the ViewPageAdapter.
     *
     * @param fragmentActivity The FragmentActivity to associate with the adapter. This is used to manage fragment transactions.
     */
    public ViewPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    /**
     * Returns the fragment to be displayed for the given position.
     *
     * @param position The position of the current item in the ViewPager.
     * @return The appropriate Fragment to be displayed.
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return the corresponding fragment based on the position
        switch (position) {
            case 1:
                return new PendingEvents();  // Show PendingEvents fragment for position 1
            case 2:
                return new DeclinedEvents();  // Show DeclinedEvents fragment for position 2
            default:
                return new ConfirmedEvents();  // Default: Show ConfirmedEvents fragment for position 0
        }
    }

    /**
     * Returns the total number of pages (fragments) in the ViewPager.
     *
     * @return The number of fragments.
     */
    @Override
    public int getItemCount() {
        return 3;  // There are three fragments: Confirmed, Pending, and Declined
    }
}
