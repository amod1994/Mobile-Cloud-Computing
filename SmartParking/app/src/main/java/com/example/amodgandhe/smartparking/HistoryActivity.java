package com.example.amodgandhe.smartparking;

import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    ArrayList<UserHistory> histories = new ArrayList<>();
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth firebaseAuth;
    private Geocoder geocoder;
    ListView listView;
    TextView price;
    TextView time;
    TextView location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


        firebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("UserHistory").child(firebaseAuth.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> childern = dataSnapshot.getChildren();
                for (DataSnapshot child : childern) {
                    UserHistory userHistory = child.getValue(UserHistory.class);
                    {
                        histories.add(userHistory);
                    }
                }
                listView = (ListView) findViewById(R.id.ListView);
                CustomAdapter customAdapter = new CustomAdapter();
                listView.setAdapter(customAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

/*        for (UserHistory booking : histories) {
            try {
                List<Address> addresses = geocoder.getFromLocation(booking.latitude, booking.longitude, 4);
                //String loc = (addresses.get(0)).toString() + (addresses.get(1)).toString() + (addresses.get(2)).toString() + (addresses.get(3)).toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return histories.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Geocoder geocoder = new Geocoder(getApplicationContext());


            convertView = getLayoutInflater().inflate(R.layout.customhistory, null);
            location = (TextView) convertView.findViewById(R.id.location);
            time = (TextView) convertView.findViewById(R.id.time);
            price = (TextView) convertView.findViewById(R.id.price);

            for(UserHistory booking : histories){
                try {
                    List<Address> addresses = geocoder.getFromLocation(booking.latitude, booking.longitude, 4);
                    //String loc = (addresses.get(0).getAdminArea()).toString() + (addresses.get(1)).toString() + (addresses.get(2)).toString() + (addresses.get(3)).toString();
                    String loc = addresses.get(0).getAdminArea();
                    location.setText(loc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                time.setText(booking.date);
                price.setText(booking.price);
            }


            return convertView;
        }
    }
}
