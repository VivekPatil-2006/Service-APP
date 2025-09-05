package com.example.homeserviceuser;

public class ServiceProvider {
    private String name;
    private String service;
    private String location;
    private String phone;
    private String age;

    // Required empty constructor for Firestore
    public ServiceProvider() {}

    public ServiceProvider(String name, String service, String location, String phone, String age) {
        this.name = name;
        this.service = service;
        this.location = location;
        this.phone = phone;
        this.age = age;
    }

    // Getters
    public String getName() { return name; }
    public String getService() { return service; }
    public String getLocation() { return location; }
    public String getPhone() { return phone; }
    public String getAge() { return age; }
}
