package com.example.espresso;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;

public class OrganizerHomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_organizer_home, container, false);

        // Find the button and set a click listener
        Button addButton = view.findViewById(R.id.add_event_button);
        addButton.setOnClickListener(v -> {
            // Create an Intent to start the NewEventFormActivity
            Intent intent = new Intent(requireActivity(), NewEventForm.class);
            startActivity(intent); // Start the activity
        });

        return view;
    }
}