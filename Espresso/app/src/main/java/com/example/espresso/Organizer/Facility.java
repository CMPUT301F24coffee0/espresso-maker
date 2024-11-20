package com.example.espresso.Organizer;

import java.util.UUID;

public class Facility {
    private final UUID id;
    private String name;
    /**
     * Create a new facility.
     */
    public Facility(String name) {
        id = UUID.randomUUID();
        this.name = name;
    }

    /**
     * Create a facility from an ID.
     * @param id    ID of the facility.
     */
    public Facility(UUID id) {
        this.id = id;
    }

    /**
     * Get the id of the facility.
     * @return  Facility ID.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Get the name of the facility.
     * @return  Name of the facility.
     */
    public String getName() { return name; }
}