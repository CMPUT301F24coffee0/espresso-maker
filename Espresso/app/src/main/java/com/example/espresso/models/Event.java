package com.example.espresso.models;

import com.example.espresso.models.Organizer.Facility;
import com.google.firebase.storage.FirebaseStorage;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This event model class holds essential information about a given event referenced with primary key eventID(id).
 * This class is used to store, collect, or display events through the Firebase DB onto different fragments or activities
 * viewed by Attendees, Organizers, and Admins.
 */
public class Event {
    private final String id;
    private Facility facility;
    private String name;
    private String date;
    private String time;
    private String description;
    private String deadline;
    private int capacity;
    private int drawn;
    private String status;
    private boolean geolocation;
    private int sample;

    /**
     * Hashes the provided text using SHA-256.
     *
     * @param text The text to hash
     * @return The hashed text as a hex string
     * @throws NoSuchAlgorithmException If SHA-256 algorithm is not found
     */
    public static String hashWithSHA256(String text) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));

        // Convert to hex string
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    /**
     * Creates a new event with the provided details.
     *
     * @param name        The name of the event
     * @param date        The date of the event
     * @param time        The time of the event
     * @param description A description of the event
     * @param deadline    The deadline for event registration
     * @param capacity    The maximum capacity of the event
     * @param facility    The facility where the event takes place
     * @param drawn       The number of participants drawn for the event
     * @param status      The current status of the event
     * @param geolocation Indicates whether geolocation is enabled for the event
     * @param sample      A sample parameter (purpose is unclear in the provided code)
     */
    public Event(String name, String date, String time, String description, String deadline, int capacity, Facility facility, int drawn, String status, boolean geolocation, int sample) {
        String text = name + facility.getName() + time;
        try {
            id = hashWithSHA256(text);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
        this.facility = facility;
        this.name = name;
        this.date = date;
        this.time = time;
        this.deadline = deadline;
        this.capacity = capacity;
        this.description = description;
        this.drawn = drawn;
        this.status = status;
        this.geolocation = geolocation;
        this.sample = sample;
    }

    /**
     * Gets the event ID.
     *
     * @return The ID of the event
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the facility name where the event takes place.
     *
     * @return The facility name
     */
    public String getFacility() {
        return facility.getName();
    }

    /**
     * Gets the name of the event.
     *
     * @return The name of the event
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the date of the event.
     *
     * @return The date of the event
     */
    public String getDate() {
        return date;
    }

    /**
     * Gets the time of the event.
     *
     * @return The time of the event
     */
    public String getTime() {
        return time;
    }

    /**
     * Gets the description of the event.
     *
     * @return The description of the event
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the deadline for event registration.
     *
     * @return The deadline of the event
     */
    public String getDeadline() {
        return deadline;
    }

    /**
     * Gets the capacity of the event (maximum number of participants).
     *
     * @return The capacity of the event
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Gets the number of participants drawn for the event.
     *
     * @return The number of drawn participants
     */
    public int getDrawn() {
        return drawn;
    }

    /**
     * Gets the current status of the event.
     *
     * @return The status of the event
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets the geolocation setting for the event.
     *
     * @return True if geolocation is enabled, false otherwise
     */
    public boolean getGeolocation() {
        return geolocation;
    }

    /**
     * Gets the sample parameter for the event (purpose unclear).
     *
     * @return The sample parameter
     */
    public int getSample() {
        return sample;
    }

    /**
     * Interface for listening to the URL fetch result from Firebase Storage.
     */
    public interface OnUrlFetchedListener {
        void onUrlFetched(String url);
    }

    /**
     * Fetches the event's poster URL from Firebase Storage.
     *
     * @param listener The listener to notify once the URL is fetched
     */
    public void getUrl(OnUrlFetchedListener listener) {
        // Fetch image from Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storage.getReference().child("posters").child(this.getId()).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    // Call the listener's method with the fetched URL
                    listener.onUrlFetched(uri.toString());
                })
                .addOnFailureListener(exception -> {
                    // Handle any errors here, such as passing a null or empty URL
                    listener.onUrlFetched(null);
                });
    }
}
