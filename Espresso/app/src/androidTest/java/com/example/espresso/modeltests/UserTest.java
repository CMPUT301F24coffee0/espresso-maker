package com.example.espresso.modeltests;

import static org.junit.Assert.*;
import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;

import com.example.espresso.Attendee.Entrant;
import com.example.espresso.Attendee.User;
import com.example.espresso.EntrantList.AllUserModel;
import com.example.espresso.EntrantList.Participant;

import java.util.Optional;
import java.util.UUID;

public class UserTest {
    private User user;
    private Entrant entrant;
    private Participant participant;
    private AllUserModel allUserModel;

    private User mockUser(){
        Context context = ApplicationProvider.getApplicationContext();
        User mockUser = new User(context);
        mockUser.setDeviceID("Test Device ID");
        return mockUser;
    }


    private Entrant mockEntrant(){
        //Context context = ApplicationProvider.getApplicationContext();
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

    /**
     * Test the User model.
     */
    @Test
    public void testUser() {
        User user = mockUser();
        assertEquals("Test Device ID", user.getDeviceID());
    }

    /**
     * Test the Entrant model.
     */
    @Test
    public void testEntrant() {
        Entrant entrant = mockEntrant();
        assertEquals("Test Name", entrant.getName());
        assertEquals("TestEmail@email.com", entrant.getEmail());
        assertEquals(Optional.of("1234567890"), entrant.getPhoneNumber());
        assertNotNull(entrant.getProfilePictureID());
    }

    /**
     * Test the Participant model.
     */
    @Test
    public void testParticipant() {
        Participant participant = mockParticipant();
        assertEquals("Test Device ID", participant.getDeviceId());
        assertEquals("Test Name", participant.getName());
    }

    /**
     * Test the AllUserModel model.
     */
    @Test
    public void testAllUserModel() {
        AllUserModel allUserModel = mockAllUserModel();
        assertEquals("Test Name", allUserModel.getName());
        assertEquals("Test Status", allUserModel.getStatus());
    }

}
