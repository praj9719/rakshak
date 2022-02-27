package com.aapex.rakshak.object;

public class Request {
    private String name, phone, email, identityNum, postcode, address, category, details;
    private long time;
    private double latitude, longitude;

    public Request(){}

    public Request(String name, String phone, String email, String identityNum, String postcode, String address, String category, String details, long time, double latitude, double longitude) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.identityNum = identityNum;
        this.postcode = postcode;
        this.address = address;
        this.category = category;
        this.details = details;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getIdentityNum() {
        return identityNum;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getAddress() {
        return address;
    }

    public String getCategory() {
        return category;
    }

    public String getDetails() {
        return details;
    }

    public long getTime() {
        return time;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
