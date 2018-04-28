package com.example.amodgandhe.smartparking;

import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Amod Gandhe on 4/22/2018.
 */

public class AvailableSlots {

    private static DatabaseReference mDatabaseRef;
    public static ArrayList<ParkingSlot> slots = new ArrayList<ParkingSlot>();

    public static ArrayList<ParkingSlot> availableSlot(){
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("AllParkingSlots");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                slots.clear();
                for(DataSnapshot slotSnapshot : dataSnapshot.getChildren()){
                    ParkingSlot singleSlot = slotSnapshot.getValue(ParkingSlot.class);
                    if(singleSlot.isAvailable() == true){
                        slots.add(singleSlot);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return slots;
    }
}
