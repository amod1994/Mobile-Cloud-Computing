package com.example.amodgandhe.smartparking;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class History {

    public static void addToHistory(LatLng dest){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uID = firebaseAuth.getUid();
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("UserHistory").child(uID);
        String id = mDatabaseReference.push().getKey();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
        String datetime = dateformat.format(c.getTime());

        UserHistory uH = new UserHistory(dest.latitude, dest.longitude, datetime);
        mDatabaseReference.child(id).setValue(uH);
    }
}
