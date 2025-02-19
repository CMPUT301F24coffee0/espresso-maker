package com.example.espresso.Admin;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.espresso.Attendee.User;
import com.example.espresso.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment used to display and manage facilities on the app.
 * Allows administrators to view, delete facilities, and associated events.
 */
public class FacilitiesFragment extends Fragment {
    private FirebaseFirestore db;
    private ArrayAdapter<String> facilityAdapter;

    private List<String> IDs = new ArrayList<>();
    private List<String> names = new ArrayList<>();
    private List<String> facilities = new ArrayList<>();

    String deviceID;

    public FacilitiesFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment.
     *
     * @return a new instance of {@link FacilitiesFragment}.
     */
    public static FacilitiesFragment newInstance() {
        FacilitiesFragment fragment = new FacilitiesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facilities, container, false);

        ListView facilityListView = view.findViewById(R.id.event_list_view);

        facilityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, facilities);
        facilityListView.setAdapter(facilityAdapter);

        deviceID = new User(requireContext()).getDeviceID();

        db = FirebaseFirestore.getInstance();

        // Fetches user data and populates the facilities list.
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getString("facility") != null) {
                                IDs.add(document.getId());
                                names.add(document.getString("name"));
                                facilities.add(document.getString("facility"));

                                facilityAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        System.err.println("Error getting documents: " + task.getException());
                    }
                });

        // Sets up a click listener for the facility list to handle deletion of facilities.
        facilityListView.setOnItemClickListener(
                (parent, view1, position, id) -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Delete Facility")
                            .setMessage("Delete this facility and all events associated with it?")
                            .setPositiveButton("OK", (dialog, which) -> {
                                String removedFacility = facilities.remove(position);
                                facilityAdapter.notifyDataSetChanged();

                                String removedDeviceID = IDs.remove(position);
                                names.remove(position);

                                // Deletes events associated with the removed facility.
                                db.collection("events")
                                        .whereEqualTo("organizer", removedDeviceID)
                                        .get()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    db.collection("events").document(document.getId()).delete()
                                                            .addOnSuccessListener(aVoid -> Log.d("FacilitiesFragment", "Event deleted successfully"))
                                                            .addOnFailureListener(e -> Log.e("FacilitiesFragment", "Error deleting event", e));
                                                }
                                            } else {
                                                Log.e("FacilitiesFragment", "Error fetching events", task.getException());
                                            }
                                        });

                                // Removes facility from the user's record.
                                db.collection("users").document(removedDeviceID)
                                        .update("facility", null)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("FacilitiesFragment", "Facility field removed from user document");
                                            Toast.makeText(requireContext(), "Facility " + removedFacility + " deleted.", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("FacilitiesFragment", "Error removing facility field", e);
                                            Toast.makeText(requireContext(), "Error deleting facility.", Toast.LENGTH_SHORT).show();
                                        });

                                Toast.makeText(requireContext(), "Facility " + removedFacility + " deleted.", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
        );
        return view;
    }
}
