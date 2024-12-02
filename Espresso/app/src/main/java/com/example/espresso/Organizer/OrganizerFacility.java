package com.example.espresso.Organizer;

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

import com.example.espresso.Attendee.User;
import com.example.espresso.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This object creates a Fragment for displaying, editing, adding, or deleting different facilities that belong
 * to an organizer with given deviceID. Each organizer must have a single facility which is stored as a field in different documents
 * in "users" collection.
 */
public class OrganizerFacility extends Fragment {
    private FirebaseFirestore db;
    private ArrayAdapter<String> facilityAdapter;
    private List<String> facilities = new ArrayList<>();
    private String deviceID;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_organizer_facility, container, false);

        ListView facilityListView = view.findViewById(R.id.event_list_view);
        Button addFacilityButton = view.findViewById(R.id.add_facility);

        facilityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, facilities);
        facilityListView.setAdapter(facilityAdapter);

        deviceID = new User(requireContext()).getDeviceID();

        db = FirebaseFirestore.getInstance();

        fetchUserFacilities();

        addFacilityButton.setOnClickListener(v -> showAddFacilityDialog());

        facilityListView.setOnItemClickListener((parent, view1, position, id) -> showEditConfirmationDialog(0));

        return view;
    }

    /**
     * Fetches different facilities that belong to a given deviceID
     */
    private void fetchUserFacilities() {
        db.collection("users").document(deviceID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String facility = documentSnapshot.getString("facility");

                    if (facility != null) {
                        facilities.add(facility);
                        facilityAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to load facility", Toast.LENGTH_SHORT).show();
                    Log.e("OrganizerFacility", "Error fetching facility", e);
                });
    }

    /**
     * Shows the dialog box that enables organizers to add new facilities.
     */
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
            } else Toast.makeText(requireContext(), "Facility name cannot be empty", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Adds a given facility referencing a deviceID to that specific document in the Firestore database.
     * @param facilityName Name of the newly added facility
     */
    private void addFacilityToDatabase(String facilityName) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("facility", facilityName);

        db.collection("users").document(deviceID)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    facilities.clear();
                    facilities.add(facilityName);
                    facilityAdapter.notifyDataSetChanged();
                    Toast.makeText(requireContext(), "Facility added successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to add facility", Toast.LENGTH_SHORT).show();
                    Log.e("OrganizerFacility", "Error adding facility", e);
                });
    }

    /**
     * Allow Organizers to Edit or Delete a certain facility by showing a dialog box
     * @param position Index of the facility in the ListView
     */
    private void showEditConfirmationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Facility Options");

        String[] options = {"Edit Facility", "Delete Facility"};
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    showEditFacilityDialog(position);
                    break;
                case 1:
                    deleteFacility();
                    break;
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    /**
     * Allows Organizers to change the name of a selected facility
     * @param position Index of the facility in the ListView
     */
    private void showEditFacilityDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Facility");

        final EditText input = new EditText(requireContext());
        input.setHint("Enter new facility name");
        input.setText(facilities.get(position));
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newFacilityName = input.getText().toString().trim();
            if (!newFacilityName.isEmpty()) {
                updateFacilityInDatabase(newFacilityName);
            } else {
                Toast.makeText(requireContext(), "Facility name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Updates the name of the selected facility in the Firestore database
     * @param newFacilityName Name of the newly added facility
     */
    private void updateFacilityInDatabase(String newFacilityName) {
        String oldFacilityName = facilities.get(0);

        Map<String, Object> updates = new HashMap<>();
        updates.put("facility", newFacilityName);

        db.collection("users").document(deviceID)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    db.collection("events")
                            .whereEqualTo("location", oldFacilityName)
                            .whereEqualTo("organizer", deviceID)
                            .get()
                            .addOnSuccessListener(querySnapshot -> {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    document.getReference().update("location", newFacilityName)
                                            .addOnSuccessListener(aVoid1 -> Log.d("EditFacility", "Event location updated"))
                                            .addOnFailureListener(e -> Log.e("EditFacility", "Error updating event location", e));
                                }
                            })
                            .addOnFailureListener(e -> Log.e("EditFacility", "Error querying events", e));

                    facilities.clear();
                    facilities.add(newFacilityName);
                    facilityAdapter.notifyDataSetChanged();
                    Toast.makeText(requireContext(), "Facility updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to update facility", Toast.LENGTH_SHORT).show();
                    Log.e("OrganizerFacility", "Error updating facility", e);
                });
    }

    /**
     * Deletes a given facility from the Firestore database. Since Organizer only have a single facility, it automatically deletes
     * the item at the zero index in the ListView
     */
    private void deleteFacility() {
        String currentFacilityName = facilities.get(0);

        db.collection("events")
                .whereEqualTo("location", currentFacilityName)
                .whereEqualTo("organizer", deviceID)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        Toast.makeText(requireContext(), "Cannot delete facility with existing events", Toast.LENGTH_SHORT).show();
                    } else {
                        db.collection("users").document(deviceID)
                                .update("facility", null)
                                .addOnSuccessListener(aVoid -> {
                                    facilities.clear();
                                    facilityAdapter.notifyDataSetChanged();
                                    Toast.makeText(requireContext(), "Facility deleted successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(requireContext(), "Failed to delete facility", Toast.LENGTH_SHORT).show();
                                    Log.e("OrganizerFacility", "Error deleting facility", e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error checking for events", Toast.LENGTH_SHORT).show();
                    Log.e("OrganizerFacility", "Error querying events", e);
                });
    }

}
