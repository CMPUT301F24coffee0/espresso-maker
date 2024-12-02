package com.example.espresso.Organizer;

import com.example.espresso.Attendee.Entrant;
import com.example.espresso.Attendee.EntrantList;

import java.util.List;
import java.util.Random;

/**
 * Lottery class handles the lottery selection process for entrants.
 * Responsibilities:
 * - Randomly select entrants from the waiting list.
 * - Handle re-selection when an invitation is declined.
 * - Send notifications to entrants of their lottery outcome.
 * Collaborators:
 * - WaitingList
 * - EntrantList
 * - Notifications
 */
public class Lottery {
    private WaitingList waitingList;
    private EntrantList entrantList;
    private int maxEntrants;

    /**
     * Constructor for the Lottery class.
     * Initializes the Lottery with a waiting list, an entrant list, notifications, and the maximum number of entrants.
     *
     * @param waitingList The list of entrants waiting for selection.
     * @param entrantList The list managing accepted and rejected entrants.
     * @param maxEntrants The maximum number of entrants to accept.
     */
    public Lottery(WaitingList waitingList, EntrantList entrantList, int maxEntrants) {
        this.waitingList = waitingList;
        this.entrantList = entrantList;
        this.maxEntrants = maxEntrants;
    }

    /**
     * Conducts the lottery to randomly select entrants.
     * Selected entrants receive notifications regarding their selection.
     */
    public void conductLottery() {
        // Get the list of entrants from the waiting list
        List<Entrant> entrants = waitingList.getEntrants();

        // Check if there are any entrants to select from
        if (entrants.isEmpty()) {
            System.out.println("No entrants available for selection.");
            return;
        }

        // Determine the number of entrants to select, limited by maxEntrants
        int numberToSelect = Math.min(maxEntrants, entrants.size());

        // Randomly select an entrant from the list based on specified count
        Random random = new Random();
        for (int i = 0; i < numberToSelect; i++) {
            int index = random.nextInt(entrants.size()); // Generate random index
            Entrant selectedEntrant = entrants.get(index); // Select entrant at random index

            // Accept the selected entrant and notify them
            entrantList.acceptEntrant(selectedEntrant);

            // Remove the selected entrant from the waiting list to prevent reselection
            entrants.remove(index);
        }

    }

    /**
     * Handles the re-selection process when an invitation is declined.
     *
     * @param declinedEntrant The entrant who declined the invitation.
     */
    public void handleDecline(Entrant declinedEntrant) {
        // Remove the declined entrant from the accepted list
        entrantList.removeAcceptedEntrant(declinedEntrant);

        // Conduct the lottery again for a new selection
        conductLottery();
    }
}
