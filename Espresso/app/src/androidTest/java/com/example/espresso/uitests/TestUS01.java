package com.example.espresso.uitests;

import static org.junit.Assert.*;
import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

import com.example.espresso.Entrant;
import com.example.espresso.Event;
import com.example.espresso.Facility;
import com.example.espresso.WaitingList;

import java.util.Optional;
import java.util.UUID;

/**
 * This class tests the functionality of the Attendee (entrant) related to
 * joining and leaving waiting lists, updating profile information, and handling notifications.
 */
public class TestUS01 {

    private Entrant entrant;
    private Event event;

    @Before
    public void setUp() {
        // Use ApplicationProvider to get a context
        Context context = ApplicationProvider.getApplicationContext();

        // Initialize entrant with the provided context
        entrant = new Entrant(context);
        Facility facility = new Facility("Sample Facility");
        event = new Event("Sample Event", "2024-12-01", "10:00 AM", "Sample event description", "2024-11-30", 100, facility);
    }

    /**
     * Tests for US 01.01.01
     * As an entrant, I want to join the waiting list for a specific event.
     */
    @Test
    public void testJoinWaitingList() {
        WaitingList waitingList = new WaitingList();
        waitingList.addEntrant(entrant);
        assertTrue("Entrant should be on the waiting list", waitingList.getEntrants().contains(entrant));
    }

    /**
     * Tests for US 01.01.02
     * As an entrant, I want to leave the waiting list for a specific event.
     */
    @Test
    public void testLeaveWaitingList() {
        WaitingList waitingList = new WaitingList();
        waitingList.addEntrant(entrant);
        waitingList.removeEntrant(entrant);
        assertFalse("Entrant should not be on the waiting list", waitingList.getEntrants().contains(entrant));
    }

    /**
     * Tests for US 01.02.01
     * As an entrant, I want to provide my personal information such as name, email, and optional phone number in the app.
     */
    @Test
    public void testProvidePersonalInformation() {
        entrant.setName("Jane Doe");
        entrant.setEmail("jane@example.com");
        entrant.setPhoneNumber("123-456-7890");
        assertEquals("Jane Doe", entrant.getName());
        assertEquals("jane@example.com", entrant.getEmail());
        assertEquals("123-456-7890", entrant.getPhoneNumber().orElse(null));
    }

    /**
     * Tests for US 01.02.02
     * As an entrant, I want to update information such as name, email, and contact information on my profile.
     */
    @Test
    public void testUpdatePersonalInformation() {
        entrant.setName("Jane Doe");
        entrant.setEmail("jane@example.com");
        entrant.setPhoneNumber("123-456-7890");
        entrant.setName("Jane Smith");
        entrant.setEmail("jane.smith@example.com");
        entrant.setPhoneNumber("098-765-4321");
        assertEquals("Jane Smith", entrant.getName());
        assertEquals("jane.smith@example.com", entrant.getEmail());
        assertEquals("098-765-4321", entrant.getPhoneNumber().orElse(null));
    }

    /**
     * Tests for US 01.03.01
     * As an entrant, I want to upload a profile picture for a more personalized experience.
     */
    @Test
    public void testUploadProfilePicture() {
        UUID profilePictureID = UUID.randomUUID();
        entrant.setProfilePictureID(profilePictureID);
        assertEquals(profilePictureID, entrant.getProfilePictureID().orElse(null));
    }

    /**
     * Tests for US 01.03.02
     * As an entrant, I want to remove profile picture if need be.
     */
    @Test
    public void testRemoveProfilePicture() {
        // Placeholder for future implementation
    }


    /**
     * Tests for US 01.03.03
     * As an entrant, I want my profile picture to be deterministically generated from my profile name if I haven't uploaded a profile image yet.
     */
    @Test
    public void testGenerateProfilePictureFromName() {
        String name = "Jane Doe";
        entrant.setName(name);
        UUID generatedProfilePictureID = UUID.nameUUIDFromBytes(name.getBytes());
        entrant.setProfilePictureID(generatedProfilePictureID);
        assertEquals("Generated profile picture should match the name-based UUID", generatedProfilePictureID, entrant.getProfilePictureID().orElse(null));
    }

    /**
     * Placeholder test for US 01.04.01
     * As an entrant, I want to receive a notification when chosen from the waiting list (when I "win" the lottery).
     */
    @Test
    public void testReceiveNotificationWhenChosen() {
        // Placeholder for future implementation
    }

    /**
     * Placeholder test for US 01.04.02
     * As an entrant, I want to receive a notification if not chosen on the app (when I "lose" the lottery).
     */
    @Test
    public void testReceiveNotificationWhenNotChosen() {
        // Placeholder for future implementation
    }

    /**
     * Placeholder test for US 01.04.03
     * As an entrant, I want to opt out of receiving notifications from organizers and admin.
     */
    @Test
    public void testOptOutOfNotifications() {
        // Placeholder for future implementation
    }

    /**
     * Placeholder test for US 01.05.01
     * As an entrant, I want another chance to be chosen from the waiting list if a selected user declines an invitation to sign up.
     */
    @Test
    public void testAnotherChanceWhenInvitationDeclined() {
        // Placeholder for future implementation
    }

    /**
     * Placeholder test for US 01.05.02
     * As an entrant, I want to be able to accept the invitation to register/sign up when chosen to participate in an event.
     */
    @Test
    public void testAcceptInvitation() {
        // Placeholder for future implementation
    }

    /**
     * Placeholder test for US 01.05.03
     * As an entrant, I want to be able to decline an invitation when chosen to participate in an event.
     */
    @Test
    public void testDeclineInvitation() {
        // Placeholder for future implementation
    }

    /**
     * Placeholder test for US 01.06.01
     * As an entrant, I want to view event details within the app by scanning the promotional QR code.
     */
    @Test
    public void testViewEventDetailsByQRCode() {
        // Placeholder for future implementation
    }

    /**
     * Placeholder test for US 01.06.02
     * As an entrant, I want to be able to sign up for an event by scanning the QR code.
     */
    @Test
    public void testSignUpForEventByQRCode() {
        // Placeholder for future implementation
    }

    /**
     * Placeholder test for US 01.07.01
     * As an entrant, I want to be identified by my device, so that I don't have to use a username and password.
     */
    @Test
    public void testDeviceIdentification() {
        // Placeholder for future implementation
    }

    /**
     * Placeholder test for US 01.08.01
     * As an entrant, I want to be warned before joining a waiting list that requires geolocation.
     */
    @Test
    public void testWarningForGeolocationRequiredWaitingList() {
        // Placeholder for future implementation
    }
}
