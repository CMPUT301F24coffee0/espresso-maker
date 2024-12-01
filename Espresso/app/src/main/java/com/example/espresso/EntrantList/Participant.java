package com.example.espresso.EntrantList;

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
