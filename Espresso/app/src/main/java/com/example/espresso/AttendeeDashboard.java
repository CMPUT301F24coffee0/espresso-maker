package com.example.espresso;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.espresso.databinding.ActivityMainBinding;
import com.example.espresso.databinding.EntrantHomeBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AttendeeDashboard extends AppCompatActivity {
    EntrantHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = EntrantHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Navigation
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.events) {
                    // Open events activity
                    Log.d("BottomNav", "Events clicked");
            }
            else if (item.getItemId() == R.id.scan) {
                    // Open scan activity
                    Log.d("BottomNav", "Scan clicked");
            }
            else if (item.getItemId() == R.id.profile) {
                // Open profile activity
                Log.d("BottomNav", "Profile clicked");
                Intent intent = new Intent(AttendeeDashboard.this, AttendeeProfile.class);
                startActivity(intent);
            }
            return true;
        });

        ListView listView = findViewById(R.id.event_list_view);
        EventAdapter adapter = new EventAdapter(this, getEventsName(), getEventsDate(), getEventsTime(), getEventsLocation(), getEventsImage());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Open event details activity
            }
        });
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