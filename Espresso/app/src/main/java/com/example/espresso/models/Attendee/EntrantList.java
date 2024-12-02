package com.example.espresso.models.Attendee;

import java.util.ArrayList;
import java.util.List;

/**
 * EntrantList class manages the list of all entrants to an event, categorized by status.
 * It handles the entry and exit of entrants from the list and receives notifications for changes.
 * Responsibilities:
 * - Shows list of entrants categorized as accepted, rejected, or cancelled.
 * - Handles entry/exit of entrants to/from the list.
 * - Receives notifications about updates.
 * - Updates the Firebase database.
 * Collaborators:
 * - WaitingList
 * - Entrant
 * - Notifications
 * - FirebaseDB
 */
public class EntrantList {

    /**
     * Lists to store accepted, rejected, and cancelled entrants.
     */
    private List<Entrant> acceptedEntrants;
    private List<Entrant> rejectedEntrants;
    private List<Entrant> cancelledEntrants;

    /**
     * Default constructor for the EntrantList class.
     * Initializes the accepted, rejected, and cancelled entrant lists.
     */
    public EntrantList() {
        acceptedEntrants = new ArrayList<>();
        rejectedEntrants = new ArrayList<>();
        cancelledEntrants = new ArrayList<>();
    }

    /**
     * Adds an entrant to the accepted list.
     *
     * @param entrant The entrant to be added.
     */
    public void acceptEntrant(Entrant entrant) {
        acceptedEntrants.add(entrant);
    }

    /**
     * Adds an entrant to the rejected list.
     *
     * @param entrant The entrant to be added.
     */
    public void rejectEntrant(Entrant entrant) {
        rejectedEntrants.add(entrant);
    }

    /**
     * Adds an entrant to the cancelled list.
     *
     * @param entrant The entrant to be added.
     */
    public void cancelEntrant(Entrant entrant) {
        cancelledEntrants.add(entrant);
    }

    /**
     * Updates the entrant lists in Firebase.
     */
    public void updateFirebase() {
        // Placeholder for Firebase db update logic
    }

    public void removeAcceptedEntrant(Entrant declinedEntrant) {
        acceptedEntrants.remove(declinedEntrant);
    }
}
