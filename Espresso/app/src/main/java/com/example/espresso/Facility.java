package com.example.espresso;

import java.util.UUID;

public class Facility {
    private final UUID id;

    /**
     * Create a new facility.
     */
    public Facility() {
        id = UUID.randomUUID();
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
}
