package com.example.espresso;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPageAdapter extends FragmentStateAdapter {
    public ViewPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ConfirmedEvents();
            case 1:
                return new PendingEvents();
            case 2:
                return new DeclinedEvents();
            default:
                return new ConfirmedEvents();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
