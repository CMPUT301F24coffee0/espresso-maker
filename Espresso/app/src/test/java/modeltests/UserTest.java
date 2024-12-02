package modeltests;

import static org.junit.Assert.*;
import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;

import com.example.espresso.Attendee.Entrant;
import com.example.espresso.Attendee.User;
import com.example.espresso.EntrantList.AllUserModel;
import com.example.espresso.EntrantList.Participant;

import java.util.UUID;

public class UserTest {
    private User mockUser(){
        Context context = ApplicationProvider.getApplicationContext();
        User mockUser = new User(context);
        mockUser.setDeviceID("Test Device ID");
        return mockUser;
    }


    private Entrant mockEntrant(){
        Context context = ApplicationProvider.getApplicationContext();
        Entrant mockEntrant = new Entrant(context);
        mockEntrant.setName("Test Name");
        mockEntrant.setEmail("TestEmail@email.com");
        mockEntrant.setPhoneNumber("1234567890");
        UUID profilePictureID = UUID.randomUUID();
        mockEntrant.setProfilePictureID(profilePictureID);

        return mockEntrant;
    }

    private Participant mockParticipant(){
        Participant mockParticipant = new Participant("Test Device ID", "Test Name");
        return mockParticipant;
    }

    private AllUserModel mockAllUserModel(){
        AllUserModel mockAllUserModel = new AllUserModel("Test Name", "Test Status");
        return mockAllUserModel;
    }

    @Test
    public void testUser() {
        User user = mockUser();
        assertEquals("Test Device ID", user.getDeviceID());
    }

    @Test
    public void testEntrant() {
        Entrant entrant = mockEntrant();
        assertEquals("Test Name", entrant.getName());
        assertEquals("TestEmail@email.com", entrant.getEmail());
        assertEquals("1234567890", entrant.getPhoneNumber());
        assertNotNull(entrant.getProfilePictureID());
    }

    @Test
    public void testParticipant() {
        Participant participant = mockParticipant();
        assertEquals("Test Device ID", participant.getDeviceId());
        assertEquals("Test Name", participant.getName());
    }

    @Test
    public void testAllUserModel() {
        AllUserModel allUserModel = mockAllUserModel();
        assertEquals("Test Name", allUserModel.getName());
        assertEquals("Test Status", allUserModel.getStatus());
    }

}
