package com.example.espresso;

import java.util.ArrayList;
import java.util.UUID;

public class WaitingList {
    private ArrayList<Entrant> entrants;

    /**
     * Create a new WaitingList.
     */
    public WaitingList() {
        entrants = new ArrayList<>();
    }

    /**
     * Get the list of entrants.
     * @return  List of entrants.
     */
    public ArrayList<Entrant> getEntrants() {
        return entrants;
    }

    /**
     * Add an entrant to the waiting list.
     * @param entrant
     */
    public void addEntrant(Entrant entrant) {
        entrants.add(entrant);
    }

    /**
     * Remove an entrant from the waiting list.
     * @param entrant
     */
    public void removeEntrant(Entrant entrant) {
        entrants.remove(entrant);
    }

    /**
     * Query for an entrant by name.
     * @param name The name of the entrant to search for.
     * @return The Entrant object if found, null otherwise.
     */
    public Entrant getEntrantByName(String name) {
        for (Entrant entrant : entrants) {
            if (entrant.getName().equalsIgnoreCase(name)) {
                return entrant;
            }
        }
        return null; 
    }

    /**
     * Query for an entrant by email.
     * @param email The email of the entrant to search for.
     * @return The Entrant object if found, null otherwise.
     */
    public Entrant getEntrantByEmail(String email) {
        for (Entrant entrant : entrants) {
            if (entrant.getEmail().equalsIgnoreCase(email)) {
                return entrant;
            }
        }
        return null;
    }

    /**
     * Query for an entrant by phone number.
     * @param phoneNumber The phone number of the entrant to search for.
     * @return The Entrant object if found, null otherwise.
     */
    public Entrant getEntrantByPhoneNumber(String phoneNumber) {
        for (Entrant entrant : entrants) {
            if (entrant.getPhoneNumber().isPresent() && entrant.getPhoneNumber().get().equals(phoneNumber)) {
                return entrant;
            }
        }
        return null;
    }

    /**
     * Query for an entrant by profile picture UUID.
     * @param profilePictureID The UUID of the profile picture.
     * @return The Entrant object if found, null otherwise.
     */
    public Entrant getEntrantByProfilePictureID(UUID profilePictureID) {
        for (Entrant entrant : entrants) {
            if (entrant.getProfilePictureID().isPresent() && entrant.getProfilePictureID().get().equals(profilePictureID)) {
                return entrant;
            }
        }
        return null;
    }

    /**
     * Updates the entrant lists in Firebase.
     */
    public void updateFirebase() {
        // Placeholder for Firebase db update logic
    }

}
