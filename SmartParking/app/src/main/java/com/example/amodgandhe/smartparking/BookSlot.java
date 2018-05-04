package com.example.amodgandhe.smartparking;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BookSlot {

        public static void bookSlot(LatLng marker, String key){
            //myParkingSpot = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            DatabaseReference temp = FirebaseDatabase.getInstance().getReference("AllParkingSlots").child(key);
            ParkingSlot update = new ParkingSlot(marker.latitude, marker.longitude, false);
            temp.setValue(update);
        }

        public static void freeSlot(LatLng marker, String key){
            DatabaseReference temp = FirebaseDatabase.getInstance().getReference("AllParkingSlots").child(key);
            ParkingSlot update = new ParkingSlot(marker.latitude, marker.longitude, true);
            temp.setValue(update);
        }
}
