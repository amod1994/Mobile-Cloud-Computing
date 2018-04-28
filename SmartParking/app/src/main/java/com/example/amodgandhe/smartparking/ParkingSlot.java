package com.example.amodgandhe.smartparking;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by Amod Gandhe on 4/22/2018.
 */

public class ParkingSlot {

    public double latitude;
    public double longitude;
    public boolean available;

    ParkingSlot(){

    }

    public ParkingSlot(double latitude, double longitude, boolean isAvailable) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.available = isAvailable;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isAvailable() {
        return available;
    }
}
