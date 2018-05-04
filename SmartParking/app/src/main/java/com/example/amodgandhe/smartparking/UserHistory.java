package com.example.amodgandhe.smartparking;

public class UserHistory {
    public double latitude;
    public double longitude;
    public String date;

    UserHistory(){
    }

    public UserHistory(double latitude, double longitude, String date) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String date() {
        return date;
    }
}
