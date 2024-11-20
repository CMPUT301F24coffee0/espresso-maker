package com.example.espresso.Attendee;

import android.content.Context;

import java.util.Optional;
import java.util.UUID;

public class Entrant extends User {
    private String name;
    private String email;
    private Optional<String> phoneNumber;
    private Optional<UUID> profilePictureID;

    /**
     * Create a new entrant.
     * @param context   App context.
     */
    public Entrant(Context context) {
        super(context);

        name = "";
        email = "";
        phoneNumber = Optional.empty();         // No phone number by default
        profilePictureID = Optional.empty();    // No profile picture by default
    }

    /**
     * Get the name of the entrant.
     * @return  Entrant's name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of an entrant.
     * @param name  Name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the email of an entrant.
     * @return  Entrant's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the email of an entrant.
     * @param email Email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get an entrant's phone number.
     * @return  Phone number.
     */
    public Optional<String> getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sent the phone number of an entrant.
     * @param phoneNumber   Phone number to set to.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = Optional.of(phoneNumber);
    }

    /**
     * Get the ID of the entrant's profile picture.
     * @return  UUID of the profile picture.
     */
    public Optional<UUID> getProfilePictureID() {
        return profilePictureID;
    }

    /**
     * Set the UUID of the user's profile picture.
     * @param profilePictureID  UUID of the picture.
     */
    public void setProfilePictureID(UUID profilePictureID) {
        this.profilePictureID = Optional.of(profilePictureID);
    }
}
