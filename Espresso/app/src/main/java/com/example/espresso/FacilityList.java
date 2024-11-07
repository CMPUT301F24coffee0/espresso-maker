package com.example.espresso;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Manages a list of Facility objects, allowing for the addition and removal of facilities.
 */
public class FacilityList {
    private ArrayList<Facility> facilities;

    /**
     * Create a new FacilityList.
     */
    public FacilityList() {
        facilities = new ArrayList<>();
    }

    /**
     * Get the list of facilities.
     *
     * @return List of Facility objects.
     */
    public ArrayList<Facility> getFacilities() {
        return facilities;
    }

    /**
     * Add a facility to the list.
     *
     * @param facility Facility to add.
     */
    public void addFacility(Facility facility) {
        facilities.add(facility);
    }

    /**
     * Remove a facility from the list using its ID.
     *
     * @param facilityId UUID of the facility to remove.
     * @return True if the facility was successfully removed, false otherwise.
     */
    public boolean removeFacility(UUID facilityId) {
        return facilities.removeIf(facility -> facility.getId().equals(facilityId));
    }

    /**
     * Updates the facility lists in Firebase.
     */
    public void updateFirebase() {
        // Placeholder for Firebase db update logic
    }
}