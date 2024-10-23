package com.example.espresso;

import java.util.UUID;

public class Event {
    private final UUID id;
    private Facility facility;

    /**
     * Create a new event in a given facility.
     * @param facility  Facility the event takes place in.
     */
    public Event(Facility facility) {
        id = UUID.randomUUID();
        this.facility = facility;
    }

    /**
     * Get the event ID.
     * @return  ID of the event.
     */
    public UUID getId() {
        return id;
    }
}
