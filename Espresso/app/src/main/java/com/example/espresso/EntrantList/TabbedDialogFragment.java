package com.example.espresso.EntrantList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.espresso.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Shows different tabs to launch Confirmed, Invited, Declined, or All user fragments. This allows organizers to pick a certain view for a specific type of Entrants.
 */
public class TabbedDialogFragment extends DialogFragment {
    String eventId;

    public TabbedDialogFragment() {
        super(R.layout.dialog_tabbed_view);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_tabbed_view, container, false);
        eventId = requireArguments().getString("eventId"); // Retrieve eventId from arguments

        // Handle Close Button
        ImageButton closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dismiss()); // Dismiss the dialog

        // Other initializations (TabLayout and ViewPager2 setup)
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);

        // Pass eventId to the adapter
        viewPager.setAdapter(new TabPagerAdapter(requireActivity(), eventId));

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Confirmed");
                    break;
                case 1:
                    tab.setText("Invited");
                    break;
                case 2:
                    tab.setText("Declined");
                    break;
                case 3:
                    tab.setText("All");
                    break;
            }
        }).attach();

        return view;
    }



    private static class TabPagerAdapter extends FragmentStateAdapter {
        private final String eventId;

        public TabPagerAdapter(@NonNull FragmentActivity fragmentActivity, String eventId) {
            super(fragmentActivity);
            this.eventId = eventId; // Store eventId for use in fragments
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment fragment;
            switch (position) {
                case 1:
                    fragment = new InvitedUser();
                    break;
                case 2:
                    fragment = new DeclinedUser();
                    break;
                case 3:
                    fragment = new AllUser();
                    break;
                default:
                    fragment = new ConfirmedUser();
                    break;
            }

            // Pass eventId to the fragment
            Bundle args = new Bundle();
            args.putString("eventId", eventId);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public int getItemCount() {
            return 4; // Number of tabs
        }
    }

}

