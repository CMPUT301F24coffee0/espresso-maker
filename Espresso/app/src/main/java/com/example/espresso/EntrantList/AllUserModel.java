package com.example.espresso.EntrantList;

/**
 * Model class for an AllUserModel object. The user has name and a status, which could be "Pending", "Accepted", "Declined", or "Cancelled".
 */
public class AllUserModel {
    private final String name;
    private final String status;

    public AllUserModel(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }
}

