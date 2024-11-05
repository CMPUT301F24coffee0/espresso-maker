package com.example.espresso;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AttendeeDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.entrant_home);
        // fetch  list of events from firebase
        ListView listView = findViewById(R.id.event_list_view);
        EventAdapter adapter = new EventAdapter(this, getEventsName(), getEventsDate(), getEventsTime(), getEventsLocation(), getEventsImage());
        listView.setAdapter(adapter);
    }

    private String[] getEventsName() {
        String[] events = {"Event 1", "Event 2", "Event 3"};
        return events;
    }

    private String[] getEventsDate() {
        String[] events = {"nov 10th, 2023", "dec 10th, 2023", "feb 10th, 2023"};
        return events;
    }
    private String[] getEventsTime() {
        String[] events = {"10:00 AM", "9:00 AM", "3:00 PM"};
        return events;
    }
    private String[] getEventsLocation() {
        String[] events = {"Los Angeles", "New York", "Toronto"};
        return events;
    }
    private String[] getEventsImage() {
        String[] events = {"https://plus.unsplash.com/premium_photo-1683865776032-07bf70b0add1?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8dXJsfGVufDB8fDB8fHww", "https://plus.unsplash.com/premium_photo-1683865776032-07bf70b0add1?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8dXJsfGVufDB8fDB8fHww", "https://plus.unsplash.com/premium_photo-1683865776032-07bf70b0add1?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8dXJsfGVufDB8fDB8fHww"};
        return events;
    }


}