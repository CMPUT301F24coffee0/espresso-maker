package com.example.espresso.EntrantList;

/**
 * Participant Model class is used to contain essential information about participants of an event. This is used to display Organizers all participants pertaining to a given eventID
 */
public class Participant {
    private String deviceId;
    private String name;

    public Participant(String deviceId, String name) {
        this.deviceId = deviceId;
        this.name = name;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getName() {
        return name;
    }
}
