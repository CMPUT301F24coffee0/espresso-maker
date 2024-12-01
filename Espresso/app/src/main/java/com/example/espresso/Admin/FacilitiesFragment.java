package com.example.espresso.Admin;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.espresso.Attendee.User;
import com.example.espresso.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FacilitiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FacilitiesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FacilitiesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FacilitiesFragment newInstance(String param1, String param2) {
        FacilitiesFragment fragment = new FacilitiesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
                                Toast.makeText(requireContext(), "Facility " + removedFacility + " deleted.", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                dialog.dismiss();
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
        ); return view;
    }
}