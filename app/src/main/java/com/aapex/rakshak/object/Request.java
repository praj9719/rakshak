package com.aapex.rakshak.object;

public class Request {
    private String name, email, identityNum, address, details;
    private int phone, postcode;
    private long time;
    private double latitude, longitude;

    public Request(){}

    public Request(String name, String email, String identityNum, String address, String details, int phone, int postcode, long time, double latitude, double longitude) {
        this.name = name;
        this.email = email;
        this.identityNum = identityNum;
        this.address = address;
        this.details = details;
        this.phone = phone;
        this.postcode = postcode;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getIdentityNum() {
        return identityNum;
    }

    public String getAddress() {
        return address;
    }

    public String getDetails() {
        return details;
    }

    public int getPhone() {
        return phone;
    }

    public int getPostcode() {
        return postcode;
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
