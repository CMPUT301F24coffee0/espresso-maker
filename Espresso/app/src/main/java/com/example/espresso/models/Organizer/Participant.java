package com.example.espresso.models.Organizer;

/**
 * Participant Model class is used to contain essential information about participants of an event.
 * This is used to display Organizers all participants pertaining to a given eventID.
 */
public class Participant {
    private String deviceId;
    private String name;

    /**
     * Constructor for creating a Participant object.
     *
     * @param deviceId The unique device ID of the participant.
     * @param name The name of the participant.
     */
    public Participant(String deviceId, String name) {
        this.deviceId = deviceId;
        this.name = name;
    }

    /**
     * Gets the device ID of the participant.
     *
     * @return The device ID of the participant.
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Gets the name of the participant.
     *
     * @return The name of the participant.
     */
    public String getName() {
        return name;
    }
}
