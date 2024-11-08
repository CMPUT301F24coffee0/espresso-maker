package com.example.espresso;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrganizerFacility extends Fragment {
    private FirebaseFirestore db;
    private ArrayAdapter<String> facilityAdapter;
    private List<String> facilityNames = new ArrayList<>();
    private List<String> facilityIds = new ArrayList<>();
    private String deviceID;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_facility, container, false);

        ListView facilityListView = view.findViewById(R.id.event_list_view);
        Button addFacilityButton = view.findViewById(R.id.add_facility);

        facilityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, facilityNames);
        facilityListView.setAdapter(facilityAdapter);

        deviceID = new User(requireContext()).getDeviceID();

        db = FirebaseFirestore.getInstance();
        fetchUserFacilities();
        addFacilityButton.setOnClickListener(v -> showAddFacilityDialog());

        facilityListView.setOnItemClickListener((parent, view1, position, id) -> showDeleteConfirmationDialog(position));

        return view;
    }

    private void fetchUserFacilities() {
        db.collection("users").document(deviceID).collection("facilities")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    facilityNames.clear();
                    facilityIds.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String name = document.getString("name");
                        if (name != null) {
                            facilityNames.add(name);
                            facilityIds.add(document.getId());
                        }
                    }
                    facilityAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to load facilities", Toast.LENGTH_SHORT).show();
                    Log.e("OrganizerFacility", "Error fetching facilities", e);
                });
    }

    private void showAddFacilityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add New Facility");

        final EditText input = new EditText(requireContext());
        input.setHint("Enter facility name");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String facilityName = input.getText().toString().trim();
            if (!facilityName.isEmpty()) {
                addFacilityToDatabase(facilityName);
            } else {
                Toast.makeText(requireContext(), "Facility name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void addFacilityToDatabase(String facilityName) {
        // Create facility data
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("name", facilityName);

        // Add facility to Firestore under the current user's deviceID
        db.collection("users").document(deviceID).collection("facilities")
                .add(facilityData)
                .addOnSuccessListener(documentReference -> {
                    // Update list view to show the new facility
                    facilityNames.add(facilityName);
                    facilityIds.add(documentReference.getId()); // Store the new document ID
                    facilityAdapter.notifyDataSetChanged();
                    Toast.makeText(requireContext(), "Facility added successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to add facility", Toast.LENGTH_SHORT).show();
                    Log.e("OrganizerFacility", "Error adding facility", e);
                });
    }

    private void showDeleteConfirmationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Facility");
        builder.setMessage("Are you sure you want to delete this facility?");

        builder.setPositiveButton("Yes", (dialog, which) -> deleteFacility(position));
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void deleteFacility(int position) {
        String facilityId = facilityIds.get(position); // Get the document ID of the facility to delete

        db.collection("users").document(deviceID).collection("facilities").document(facilityId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Remove the facility from the list and update the adapter
                    facilityNames.remove(position);
                    facilityIds.remove(position);
                    facilityAdapter.notifyDataSetChanged();
                    Toast.makeText(requireContext(), "Facility deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to delete facility", Toast.LENGTH_SHORT).show();
                    Log.e("OrganizerFacility", "Error deleting facility", e);
                });
    }
}
