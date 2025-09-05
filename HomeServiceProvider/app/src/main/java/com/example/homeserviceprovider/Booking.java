package com.example.homeserviceprovider;

public class Booking {
    private String user;
    private String service;
    private String location;
    private String status;

    // Required empty constructor for Firebase
    public Booking() {}

    public Booking(String user, String service, String location, String status) {
        this.user = user;
        this.service = service;
        this.location = location;
        this.status = status;
    }

    // Getters
    public String getUser() { return user; }
    public String getService() { return service; }
    public String getLocation() { return location; }
    public String getStatus() { return status; }
}
