package com.example.espresso.Attendee;

import android.content.Context;
import android.provider.Settings;

public class User {
    private String deviceID;

    /**
     * Create a new user.
     * @param context   App context.
     */
    public User(Context context) {
        deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Get the device ID.
     * @return  Device ID.
     */
    public String getDeviceID() {
        return deviceID;
    }

    /**
     * Set the device ID.
     * @param deviceID The device ID.
     */
    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
}
