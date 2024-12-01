package com.example.espresso.EntrantList;

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

