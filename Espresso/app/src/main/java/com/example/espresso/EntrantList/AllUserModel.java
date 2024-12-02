package com.example.espresso.EntrantList;

/**
 * Model class representing a user with a name and status. The status can be one of the following:
 * "Pending", "Accepted", "Declined", or "Cancelled".
 */
public class AllUserModel {
    private final String name;
    private final String status;

    /**
     * Constructs an AllUserModel object with a name and status.
     *
     * @param name The name of the user.
     * @param status The status of the user (e.g., "Pending", "Accepted", "Declined", "Cancelled").
     */
    public AllUserModel(String name, String status) {
        this.name = name;
        this.status = status;
    }

    /**
     * Gets the name of the user.
     *
     * @return The name of the user.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the status of the user.
     *
     * @return The status of the user (e.g., "Pending", "Accepted", "Declined", "Cancelled").
     */
    public String getStatus() {
        return status;
    }
}


