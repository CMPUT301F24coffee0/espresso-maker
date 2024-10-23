package com.example.espresso;

import java.util.ArrayList;

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
     * Updates the entrant lists in Firebase.
     */
    public void updateFirebase() {
        // Placeholder for Firebase db update logic
    }

}
