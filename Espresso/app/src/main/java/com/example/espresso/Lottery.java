package com.example.espresso;

import android.app.Notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Lottery {
    private WaitingList waitingList;
    private EntrantList acceptedEntrants;
    private Random random;

    /**
     * Constructor for the Lottery class.
     *
     * @param waitingList The waiting list of entrants.
     * @param acceptedEntrants The list of accepted entrants.
     */
    public Lottery(WaitingList waitingList, EntrantList acceptedEntrants, Notification notificationService) {
        this.waitingList = waitingList;
        this.acceptedEntrants = acceptedEntrants;
        this.random = new Random();
    }

    /**
     * Randomly select a specified number of entrants from the waiting list.
     *
     * @param numberOfWinners Number of entrants to select from the waiting list.
     * @return A list of selected entrants.
     */
    public List<Entrant> selectRandomEntrants(int numberOfWinners) {
        List<Entrant> entrants = waitingList.getEntrants();
        List<Entrant> selectedEntrants = new ArrayList<>();

        while (selectedEntrants.size() < numberOfWinners) {
            int index = random.nextInt(entrants.size());
            Entrant selectedEntrant = entrants.get(index);

            if (!selectedEntrants.contains(selectedEntrant)) {
                selectedEntrants.add(selectedEntrant);
            }
        }

        for (Entrant entrant : selectedEntrants) {
            acceptedEntrants.acceptEntrant(entrant);
            waitingList.removeEntrant(entrant);
        }

        return selectedEntrants;
    }
}
