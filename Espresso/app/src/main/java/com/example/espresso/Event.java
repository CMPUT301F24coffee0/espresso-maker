package com.example.espresso;

import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.UUID;

public class Event {
    private final String id;
    private Facility facility;
    private String name;
    private String date;
    private String time;
    private String description;
    private String deadline;
    private int capacity;

    /**
     * Create a new event in a given facility.
     * @param facility  Facility the event takes place in.
     */
    public Event(String name, String date, String time, String description, String deadline, int capacity, Facility facility) {
        id = name+date+time;
        this.facility = facility;
        this.name = name;
        this.date = date;
        this.time = time;
        this.deadline = deadline;
        this.capacity = capacity;
        this.description = description;
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
     *  Get the URL of the poster image for the event.
     *
     */

    public interface OnUrlFetchedListener {
        void onUrlFetched(String url);
    }

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
