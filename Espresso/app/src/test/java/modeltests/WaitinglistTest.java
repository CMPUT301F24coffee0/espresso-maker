package modeltests;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.example.espresso.Attendee.Entrant;
import com.example.espresso.Organizer.Facility;
import com.example.espresso.Organizer.WaitingList;

import org.junit.Test;

import java.util.UUID;

public class WaitinglistTest {

    private Entrant mockEntrant(){
        Context context = ApplicationProvider.getApplicationContext();
        Entrant mockEntrant = new Entrant(context);
        mockEntrant.setName("Test Name");
        mockEntrant.setEmail("TestEmail@email.com");
        mockEntrant.setPhoneNumber("1234567890");

        return mockEntrant;
    }

    private WaitingList mockWaitingList(){
        WaitingList mockWaitingList = new WaitingList();
        mockWaitingList.addEntrant(mockEntrant());
        return mockWaitingList;
    }

    private Facility mockFacility(){
        Facility mockFacility = new Facility("Test Facility");
        return mockFacility;
    }

    @Test
    public void testWaitingList() {
        WaitingList waitingList = mockWaitingList();
        assertEquals(mockEntrant(), waitingList.getEntrantByName("Test Name"));
        assertEquals(mockEntrant(), waitingList.getEntrantByEmail("TestEmail@email.com"));
        assertEquals(mockEntrant(), waitingList.getEntrantByPhoneNumber("1234567890"));
    }

    @Test
    public void testFacility() {
        Facility facility = mockFacility();
        assertEquals("Test Facility", facility.getName());
    }

}
