package com.example.amodgandhe.smartparking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;

import android.support.annotation.RequiresApi;
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
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/*import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;*/
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, ZXingScannerView.ResultHandler {


    GoogleMap mMap;


    private LocationManager locationManager;
    private FirebaseAuth firebaseAuth;
    private GeoFire geoFire;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference gDatabaseReference;
    private FusedLocationProviderClient mFusedLocationClient;
    private ZXingScannerView scannerView;

    private Button start;
    private Button end;
    private Button mRadius;

    private static final int LOCATION_REQUEST = 500;
    private static final int REQUEST_CAMERA = 1;

    private Marker mMarker;
    private String mKey;
    private LatLng myParkingSpot;
    private LatLng currentLoc;
    final ArrayList<String> nearByLoc = new ArrayList<>();
    ArrayList<String> temp = new ArrayList<>();
    int radius = 5;
    String[] uRadius = {"1", "5", "10", "15", "50", "150"};

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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
        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#010000")));
        //Drawable myImage = getResources().getDrawable(R.drawable.ic_user);


        if (navigationView != null) {
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_camera).setTitle("Pre Book");
            menu.findItem(R.id.nav_gallery).setTitle("My Bookings");
            menu.findItem(R.id.nav_slideshow).setVisible(false);//In case you want to remove menu item
            menu.findItem(R.id.nav_manage).setVisible(false);//In case you want to remove menu item
            navigationView.setNavigationItemSelectedListener(this);

            View hView =  navigationView.getHeaderView(0);
            ImageView imgvw = (ImageView)hView.findViewById(R.id.imageView);
            TextView tv = (TextView)hView.findViewById(R.id.textview);
            TextView tv1 = (TextView)hView.findViewById(R.id.textView);
            imgvw.setImageResource(R.drawable.ic_user);
            tv.setText("Amod Gandhe");
            tv.setTextColor(Color.parseColor("#FF525252"));
            tv1.setText("amodgandhes@gmail.com");
            tv1.setTextColor(Color.parseColor("#FF525252"));
            navigationView.setBackgroundColor(Color.parseColor("#f5f5f5"));
        }

        mRadius = (Button) findViewById(R.id.button);
        start = (Button) findViewById(R.id.start);
        start.setEnabled(false);
        end = (Button) findViewById(R.id.end);
        end.setEnabled(false);

        firebaseAuth = FirebaseAuth.getInstance();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("AllParkingSlots");
        gDatabaseReference = FirebaseDatabase.getInstance().getReference("GeoFire");
        geoFire = new GeoFire(gDatabaseReference);
        scannerView = new ZXingScannerView(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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
        LatLng currentLat;
        mMap = googleMap;

        Criteria criteria = new Criteria();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLoc, 10);
                            mMap.animateCamera(cameraUpdate);
                        }
                    }
                });

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

                //myParkingSpot = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);

                mMarker = marker;
                mKey = key;
                confirmBooking(mMarker, mKey);
                start.setEnabled(true);
                return false;
            }
        });
    }

    public void confirmBooking(final Marker marker, final String key){
        final Navigation startNavigation  = new Navigation(this, currentLoc, mMap);
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(MainActivity.this);
        confirmBuilder.setTitle("Confirm Slot")
        .setMessage("Please Start your Time! Scan QR code at Entry Point.")
        .setPositiveButton(Html.fromHtml("<font color='#f5f5f5'>Got It!</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                start.setEnabled(true);
                //bookSlot(marker, key);
                startNavigation.sendRequest(marker.getPosition());
            }
        });
        AlertDialog alert1D = confirmBuilder.create();
        alert1D.show();
        alert1D.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF7B7B7B")));
    }

    public void bookSlot(Marker marker, String key){
        myParkingSpot = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
        DatabaseReference temp = FirebaseDatabase.getInstance().getReference("AllParkingSlots").child(key);
        ParkingSlot update = new ParkingSlot(marker.getPosition().latitude, marker.getPosition().longitude, false);
        temp.setValue(update);
    }

    public void findNearBySpot(){
        if(currentLoc!=null) {
            GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(currentLoc.latitude, currentLoc.longitude), radius);
            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                LatLng pos;

                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    if (myParkingSpot == null)
                        if (nearByLoc.contains(key)) {
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
        mBuilder.setTitle(Html.fromHtml("<font color='#f5f5f5'>Select desired area radius</font>"));
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
        alert11.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF7B7B7B")));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void startSession(View view){
        start.setEnabled(false);
        Double lat = mMarker.getPosition().latitude;
        Double lng = mMarker.getPosition().longitude;
        Long tm = System.currentTimeMillis();
        temp.add(lat.toString());
        temp.add(lng.toString());
        temp.add(mKey);
        ///////////////////
        temp.add(tm.toString());
        ////////////////
        startActivity(new Intent(MainActivity.this, QRScanner.class).putExtra("slotDetails", temp));
        end.setEnabled(true);
    }

    public void endSession(View view){
        Double lat = mMarker.getPosition().latitude;
        Double lng = mMarker.getPosition().longitude;
        Long eTm = System.currentTimeMillis();

        //temp.add(lat.toString());
        //temp.add(lng.toString());
        //temp.add(mKey);
        temp.add(eTm.toString());
        startActivity(new Intent(MainActivity.this, QRScanner.class).putExtra("slotDetails", temp));
    }

    @Override
    public void handleResult(Result result) {
        final String myResult = result.getText();
        Log.d("QRCodeScanner", result.getText());
        Log.d("QRCodeScanner", result.getBarcodeFormat().toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //scannerView.resumeCameraPreview(MainActivity.this);
                //confirmBooking(mMarker, mKey);
                bookSlot(mMarker, mKey);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        builder.setNeutralButton("Visit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(myResult));
                startActivity(browserIntent);
            }
        });
        builder.setMessage(result.getText());
        AlertDialog alert1 = builder.create();
        alert1.show();
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
            Intent intent = new Intent(this, StartTimeSelect.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(this, HistoryActivity.class));

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
        /*String id = mDatabaseReference.push().getKey();
        String id1 = mDatabaseReference.push().getKey();
        String id2 = mDatabaseReference.push().getKey();*/
        String id3 = mDatabaseReference.push().getKey();
        String id4 = mDatabaseReference.push().getKey();
        String id5 = mDatabaseReference.push().getKey();
        String id6 = mDatabaseReference.push().getKey();
        String id7 = mDatabaseReference.push().getKey();
        String id8 = mDatabaseReference.push().getKey();
        String id9 = mDatabaseReference.push().getKey();
        String id10 = mDatabaseReference.push().getKey();


        /*ParkingSlot ps = new ParkingSlot(40.896227, -73.127687, true);
        mDatabaseReference.child(id).setValue(ps);

        ParkingSlot ps1 = new ParkingSlot(40.896397, -73.127650, true);
        mDatabaseReference.child(id1).setValue(ps1);

        ParkingSlot ps2 = new ParkingSlot(40.907504, -73.108130, true);
        mDatabaseReference.child(id2).setValue(ps2);*/

        ParkingSlot ps3 = new ParkingSlot(40.88308522083045, -73.0991649, true);
        mDatabaseReference.child(id3).setValue(ps3);
        geoFire.setLocation(id3, new GeoLocation(40.88308522083045, -73.0991649), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
            }
        });
        geoFire.setLocation(id3, new GeoLocation(40.88308522083045, -73.0991649), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {

            }
        });

        ParkingSlot ps4 = new ParkingSlot(40.863144, -73.082159, true);
        mDatabaseReference.child(id4).setValue(ps4);
        geoFire.setLocation(id4, new GeoLocation(40.863144, -73.082159), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
            }
        });

        ParkingSlot ps5 = new ParkingSlot(40.8527737, -73.185768, true);
        mDatabaseReference.child(id5).setValue(ps5);
        geoFire.setLocation(id5, new GeoLocation(40.8527737, -73.185768), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
            }
        });

        ParkingSlot ps6 = new ParkingSlot(40.856837, -73.188585, true);
        mDatabaseReference.child(id6).setValue(ps6);
        geoFire.setLocation(id6, new GeoLocation(40.856837, -73.188585), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
            }
        });

        ParkingSlot ps7 = new ParkingSlot(40.849949, -73.185611, true);
        mDatabaseReference.child(id7).setValue(ps7);
        geoFire.setLocation(id7, new GeoLocation(40.849949, -73.185611), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {

            }
        });

        ParkingSlot ps8 = new ParkingSlot(40.864977, -73.130149, true);
        mDatabaseReference.child(id8).setValue(ps8);
        geoFire.setLocation(id8, new GeoLocation(40.864977, -73.130149), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {

            }
        });

        ParkingSlot ps9 = new ParkingSlot(40.74904512, -73.51868391, true);
        mDatabaseReference.child(id9).setValue(ps9);
        geoFire.setLocation(id9, new GeoLocation(40.74904512, -73.51868391), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {

            }
        });

        ParkingSlot ps10 = new ParkingSlot(40.74787979, -73.51745187, true);
        mDatabaseReference.child(id10).setValue(ps10);
        geoFire.setLocation(id10, new GeoLocation(40.74787979, -73.51745187), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {

            }
        });
    }


}
