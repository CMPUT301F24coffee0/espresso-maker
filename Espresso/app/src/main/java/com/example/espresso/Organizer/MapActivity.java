package com.example.espresso.Organizer;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.espresso.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap mMap;
    private FirebaseFirestore db;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get event ID from intent
        eventId = getIntent().getStringExtra("eventId");

        // Initialize MapView
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        // Initialize the map
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Fetch user locations from Firestore
        fetchUserLocations();
    }

    private void fetchUserLocations() {
        db.collection("events").document(eventId).collection("participants")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                        boolean hasParticipants = false;

                        for (DocumentSnapshot document : task.getResult()) {
                            Double latitude = document.getDouble("latitude");
                            Double longitude = document.getDouble("longitude");

                            if (latitude != null && longitude != null) {
                                LatLng userLocation = new LatLng(latitude, longitude);
                                mMap.addMarker(new MarkerOptions().position(userLocation).title("Participant Location"));

                                // Include this location in the bounds
                                boundsBuilder.include(userLocation);
                                hasParticipants = true;
                            }
                        }

                        if (hasParticipants) {
                            // Adjust the map to show all markers
                            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100));
                        } else {
                            Log.d("MapActivity", "No participant locations to display.");
                        }
                    } else {
                        Log.e("MapActivity", "Failed to fetch user locations", task.getException());
                    }
                });
    }



    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
