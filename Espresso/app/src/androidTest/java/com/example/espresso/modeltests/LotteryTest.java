package com.example.espresso.modeltests;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.example.espresso.Attendee.Entrant;
import com.example.espresso.Attendee.EntrantList;
import com.example.espresso.Organizer.Lottery;
import com.example.espresso.Organizer.WaitingList;

import org.junit.Before;
import org.junit.Test;

public class LotteryTest {
    private Lottery lottery;
    private Entrant entrant;
    private WaitingList waitingList;
    private EntrantList entrantList;
    private int maxEntrants;

    @Before
    public void setUp() {
        // Initialize mock objects
        Context context = ApplicationProvider.getApplicationContext();
        entrant = new Entrant(context);
        entrant.setName("Test Name");
        entrant.setEmail("TestEmail@email.com");
        entrant.setPhoneNumber("1234567890");
        waitingList = new WaitingList();
        waitingList.addEntrant(entrant);
        entrantList = new EntrantList();
        maxEntrants = 5;
        lottery = new Lottery(waitingList, entrantList, maxEntrants);
    }

    /**
     * Test the Lottery model.
     */
    @Test
    public void testConductLottery() {
        // Test the lottery process
        lottery.conductLottery();
        assertEquals(0, waitingList.getEntrants().size());
    }

}
