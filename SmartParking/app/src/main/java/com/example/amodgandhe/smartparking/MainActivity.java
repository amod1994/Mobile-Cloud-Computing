package com.example.amodgandhe.smartparking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

/*import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;*/
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.security.auth.callback.Callback;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {


    GoogleMap mMap;


    private LocationManager locationManager;
    private FirebaseAuth firebaseAuth;
    private GeoFire geoFire;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference gDatabaseReference;

    private static final int LOCATION_REQUEST = 500;

    private LatLng myParkingSpot;
    private LatLng currentLoc;
    final ArrayList<String> nearByLoc = new ArrayList<>();
    int radius = 1;
    String[] uRadius = {"1", "5", "10", "15", "50", "150"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        firebaseAuth = FirebaseAuth.getInstance();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("AllParkingSlots");
        gDatabaseReference = FirebaseDatabase.getInstance().getReference("GeoFire");
        geoFire = new GeoFire(gDatabaseReference);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //populateDatabase();
        mapInit();
    }

    public void mapInit() {
        SupportMapFragment sMapFragment = SupportMapFragment.newInstance();
        sMapFragment.getMapAsync(MainActivity.this);
        FragmentManager sFm = getSupportFragmentManager();
        sFm.beginTransaction().add(R.id.map, sMapFragment).commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Criteria criteria = new Criteria();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager
                .getBestProvider(criteria, false));
        currentLoc = new LatLng(location.getLatitude(), location.getLongitude());

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLoc, 10);
        mMap.animateCamera(cameraUpdate);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        initialMarker();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                myParkingSpot = marker.getPosition();
                String key = marker.getTag().toString();
                confirmBooking(marker, key);
                return false;
            }
        });
    }

    public void confirmBooking(final Marker marker, final String key){
        final Navigation startNavigation  = new Navigation(this, currentLoc, mMap);
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(MainActivity.this);
        confirmBuilder.setTitle("Confirm Slot")
        .setMessage("Do you want to reserve this slot for you?")
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        })
        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                bookSlot(marker, key);
                startNavigation.sendRequest(marker.getPosition());
            }
        });
        AlertDialog alert1D = confirmBuilder.create();
        alert1D.show();
    }

    public void bookSlot(Marker marker, String key){
        DatabaseReference temp = FirebaseDatabase.getInstance().getReference("AllParkingSlots").child(key);
        ParkingSlot update = new ParkingSlot(marker.getPosition().latitude, marker.getPosition().longitude, false);
        temp.setValue(update);

    }

    public void findNearBySpot(){
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(currentLoc.latitude, currentLoc.longitude), radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            LatLng pos;
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(nearByLoc.contains(key)){
                    addMarker(location, key);
                }
            }
            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    public void initialMarker() {
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            LatLng temp;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(myParkingSpot == null) {
                    mMap.clear();
                    nearByLoc.clear();
                    Iterable<DataSnapshot> childern = dataSnapshot.getChildren();
                    for (DataSnapshot child : childern) {
                            ParkingSlot singleSlot = child.getValue(ParkingSlot.class);
                            if (singleSlot.isAvailable() == true) {
                                nearByLoc.add(child.getKey());
                            }
                    }
                }
                findNearBySpot();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void addMarker(GeoLocation position, String key) {
        mMap.addMarker(new MarkerOptions().position(new LatLng(position.latitude, position.longitude)))
                    .setTag(key);
    }

    public void logoutUser(MenuItem item) {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void setRadius(View view){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setTitle("Select desired area radius");
        mBuilder.setItems(uRadius, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                radius = Integer.parseInt(uRadius[which]);
                mMap.clear();
                findNearBySpot();
            }
        });
        AlertDialog alert11 = mBuilder.create();
        alert11.show();
    }



    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permssion, @NonNull int[] grantResults){
        switch(requestCode){
            case LOCATION_REQUEST:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mMap.setMyLocationEnabled(true);
                }
                break;
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void populateDatabase(){
        String id = mDatabaseReference.push().getKey();
        String id1 = mDatabaseReference.push().getKey();
        String id2 = mDatabaseReference.push().getKey();

        ParkingSlot ps = new ParkingSlot(40.896227, -73.127687, true);
        mDatabaseReference.child(id).setValue(ps);


        ParkingSlot ps1 = new ParkingSlot(40.896397, -73.127650, true);
        mDatabaseReference.child(id1).setValue(ps1);

        ParkingSlot ps2 = new ParkingSlot(40.907504, -73.108130, true);
        mDatabaseReference.child(id2).setValue(ps2);
    }
}
