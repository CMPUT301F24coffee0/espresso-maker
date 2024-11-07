package com.example.espresso;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PendingEvents extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pending_events, container, false);
        List<Event> events = new ArrayList<>();
        events.add(new Event("Event 1", "Date 1", "Time 1", "Location 1", "deadline 1", 10, new Facility("fsadf")));
        events.add(new Event("Event 2", "Date 2", "Time 2", "Location 2", "deadline 2", 10, new Facility("fsadf")));

        EventAdapter adapter = new EventAdapter(getContext(),events);
        ListView listView = rootView.findViewById(R.id.event_list_view);
        listView.setAdapter(adapter);

        return rootView;
    }
}