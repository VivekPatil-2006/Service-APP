package com.example.homeserviceadmin;

public class ServiceProvider {
    private String name, phone, age, location, service;

    // No-argument constructor required for Firestore
    public ServiceProvider() {}

    public ServiceProvider(String name, String phone, String age, String location, String service) {
        this.name = name;
        this.phone = phone;
        this.age = age;
        this.location = location;
        this.service = service;
    }

    // Getters
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getAge() { return age; }
    public String getLocation() { return location; }
    public String getService() { return service; }
}
