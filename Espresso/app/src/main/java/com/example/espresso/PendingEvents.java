package com.example.espresso;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PendingEvents extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pending_events, container, false);
        List<Event> events = new ArrayList<>();
        EventAdapter adapter = new EventAdapter(getContext(),events);
        // Fetch confirmed events from the database and add them to the list
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(new User(getContext()).getDeviceID()).collection("events").whereEqualTo("status", "pending").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Loop through the documents in the collection
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d("Event", "Event found: " + document.getId());
                    // Get the data from the document
                    Map<String, Object> data = document.getData();
                    String name = (String) data.get("name");
                    String date = (String) data.get("date");
                    String time = (String) data.get("time");
                    String location = (String) data.get("location");
                    String description = (String) data.get("description");
                    String deadline = (String) data.get("deadline");
                    Object capacityObj = data.get("capacity");
                    int capacity = (capacityObj instanceof Number) ? ((Number) capacityObj).intValue() : 0;
                    events.add(new Event(name, date, time, description, deadline, capacity, new Facility(location)));
                    adapter.notifyDataSetChanged();
                }
            } else {
                Log.d("Event", "Error getting documents: ", task.getException());
            }
        });


        ListView listView = rootView.findViewById(R.id.event_list_view);
        listView.setAdapter(adapter);

        return rootView;
    }
}