package com.example.espresso.Event;

import com.example.espresso.Organizer.Facility;
import com.google.firebase.storage.FirebaseStorage;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * This event model class holds essential information about a given event referenced with primary key eventID(id)
 * This class is used to store, collect, or display, events through the Firebase DB onto different fragments or activities
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
     * Create a new event in a given facility.
     * @param facility  Facility the event takes place in.
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
     * Get the event ID.
     * @return  ID of the event.
     */
    public String getId() {
        return id;
    }

    /**
     * Get the facility the event takes place in.
     * @return  Facility the event takes place in.
     */
    public String getFacility() {
        return facility.getName();
    }

    /**
     * Get the name of the event.
     * @return  Name of the event.
     */
    public String getName() { return name; }

    /**
     * Get the date of the event.
     * @return  Date of the event.
     */
    public String getDate() { return date; }

    /**
     * Get the time of the event.
     * @return  Time of the event.
     */
    public String getTime() { return time; }


    /**
     * Get the description of the event.
     * @return  Description of the event.
     */
    public String getDescription() { return description; }

    /**
     * Get the deadline of the event.
     * @return  Deadline of the event.
     */
    public String getDeadline() { return deadline; }

    /**
     * Get the capacity of the event.
     * @return  Capacity of the event.
     */
    public int getCapacity() { return capacity; }

    /**
     * Get the status of the event.
     * @return  Status of the event.
     */
    public int getDrawn() { return drawn; }

    /**
     * Get the status of the event.
     * @return  Status of the event.
     */
    public String getStatus() { return status; }

    /**
     *  Get the URL of the poster image for the event.
     *
     */
    public interface OnUrlFetchedListener {
        void onUrlFetched(String url);
    }

    public boolean getGeolocation() { return geolocation; }

    public int getSample() { return sample;}

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