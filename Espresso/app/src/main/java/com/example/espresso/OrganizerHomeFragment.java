package com.example.espresso;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
/**
 * OrganizerHomeFragment displays the home screen for the organizer, including a list of events and
 * an option to add new events.
 * It handles navigation to the event creation screen when the "Add Event" button is clicked.
 */
public class OrganizerHomeFragment extends Fragment {

    // The event adapter used to display events in the ListView
    private EventAdapter eventAdapter;

    /**
     * Called to have the fragment instantiate its user interface view.
     * Inflates the layout for this fragment and sets up event handling.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState A Bundle containing the fragment's previously saved state.
     *                           If null, the fragment is being created for the first time.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_organizer_home, container, false);

        // Find the button and set a click listener to navigate to the NewEventFormActivity
        Button addButton = view.findViewById(R.id.add_event_button);
        addButton.setOnClickListener(v -> {
            // Create an Intent to start the NewEventForm activity
            Intent intent = new Intent(requireActivity(), NewEventForm.class);
            startActivity(intent); // Start the activity to add a new event
        });

        // Find the ListView where events will be displayed
        ListView event_list = view.findViewById(R.id.event_list_view);

        // Set up the eventAdapter to populate the ListView (not implemented in this fragment)

        return view; // Return the inflated view
    }
}
