package com.example.amodgandhe.smartparking;

public class UserHistory {
    public double latitude;
    public double longitude;
    public String date;
    public String price;

    UserHistory(){
    }

    public UserHistory(double latitude, double longitude, String date, String price) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.price = price;
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

    public String price(){
        return price;
    }
}
