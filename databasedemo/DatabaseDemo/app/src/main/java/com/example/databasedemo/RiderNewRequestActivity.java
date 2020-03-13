/*
RiderNewRequestActivity
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import androidx.annotation.NonNull;

import androidx.annotation.RequiresApi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.annotation.Nullable;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import android.view.MenuItem;

import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.databasedemo.DirectionHelpers.TaskLoadedCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.example.databasedemo.DirectionHelpers.FetchURL;
import com.example.databasedemo.DirectionHelpers.TaskLoadedCallback;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.material.navigation.NavigationView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Displays map, lets user define pickup and dropoff locations in order to create a ride request
 * @author Sirjan Chawla, Johnas Wong, Michael Antifaoff
 */
// implements TaskLoadedCallback
public class RiderNewRequestActivity extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap map;
    SupportMapFragment mapFragment;
    SearchView searchView,searchView2;
    Button btnGetFare, btnAddTip, btnConfirmRequest;
    Polyline currentPolyline;
    FusedLocationProviderClient fusedLocationProviderClient;
    LatLng latLng,latLng2, latLng3;
    MarkerOptions p1, p2;
    TextView fareDisplay, offerDisplay,usrNameText,usrEmailText;
    TextView tipAmount;
    double fare;
    FirebaseAuth mAuth;

    private static String TAG = "Hello";

    /**
     * Called when activity is created
     * displays map, buttons and text fields where to enter start and end locations of ride
     * Checks that the locations are valid and then gives a fare estimate, with the option to add
     * a tip to the ride. once all fields are valid, calls
     * {@link RiderNewRequestActivity#addRequest(Request, String, String) addRequest}
     * @param {@code Bundle}savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_new_request);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setActionBar(toolbar);
        }
        NavigationView navi = findViewById(R.id.nav_view);
        View headerView = navi.getHeaderView(0);
        navi.setNavigationItemSelectedListener(this);
        mAuth = FirebaseAuth.getInstance();


        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");
        final String email = intent.getStringExtra("email");


        btnGetFare = findViewById(R.id.btnGetFare);

        btnAddTip = findViewById(R.id.addTipButton);
        btnConfirmRequest = findViewById(R.id.btnConfirmRequest);
        searchView = findViewById(R.id.sv_location);
        searchView2 = findViewById(R.id.sv2_location);
        fareDisplay = findViewById(R.id.fareDisplay);
        offerDisplay = findViewById(R.id.offerDisplay);
        tipAmount = findViewById(R.id.tipAmount);
        usrNameText = headerView.findViewById(R.id.usrNameText);
        usrEmailText=headerView.findViewById(R.id.usrEmailText);
        searchView.setQuery("Current Location", false);
        usrNameText.setText(username);
        usrEmailText.setText(email);


        mapFragment = ( SupportMapFragment ) getSupportFragmentManager()
                .findFragmentById( R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);


        btnConfirmRequest.setVisibility(View.INVISIBLE);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        requestPermission();

        if(ActivityCompat.checkSelfPermission(RiderNewRequestActivity.this, ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(RiderNewRequestActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    MarkerOptions p3 = new MarkerOptions().position(latLng);
                    map.addMarker(p3);
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));

                }

            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.i("Hello", "We get right here");
                String location = searchView.getQuery().toString();
                ArrayList<Address> addressList = new ArrayList<Address>();
                Log.i("Hello", "We get right here 1");
                if(location != null && !location.equals("")){
                    Log.i("Hello", "We get right here 2");
                    Geocoder geocoder = new Geocoder(RiderNewRequestActivity.this);
                    boolean goodNews = true;
                    try {
                        long start = System.currentTimeMillis();
                        while (addressList.size() < 1) {
                            addressList = (ArrayList<Address>) geocoder.getFromLocationName(location, 1);
                            long current = System.currentTimeMillis();
                            if ((current - start)/1000 > 2){          // Waits for two seconds
                                Log.i("Hello", "2 seconds passed ");
                                goodNews = false;
                                break;
                            }
                        }
                        Log.i("Hello", "This is address list 1 " + addressList.toString());
                    } catch (Exception e) {
                        System.out.print(e.getMessage());
                        Log.i("Hello", "Does this throw an IOException? 1" + addressList.toString());
                    }

                    //if (addressList.size()>0) {
                    //    Address address = addressList.get(0);
                    //}

                    Address address;

                    if (goodNews) {
                        address = addressList.get(0);
                        latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        p1 = new MarkerOptions().position(latLng);
                        map.addMarker(p1);
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    } else {
                        searchView.setQuery("Please Enter a Valid Location", false);
                        searchView.clearFocus();
                        Log.i("Hello", "Bad News");
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String errorString = searchView.getQuery().toString();
                if (errorString.equals("Please Enter a Valid Location")) {
                    //searchView.setQuery("", false);
                }
                return false;
            }
        });


        searchView2.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location2 = searchView2.getQuery().toString();
                ArrayList<Address> addressList2 = new ArrayList<Address>();
                if(location2 != null && !location2.equals("")){
                    Geocoder geocoder = new Geocoder(RiderNewRequestActivity.this);
                    boolean goodNews = true;
                    try {
                        long start = System.currentTimeMillis();
                        while (addressList2.size() < 1) {
                            addressList2 = (ArrayList<Address>) geocoder.getFromLocationName(location2, 1);
                            long current = System.currentTimeMillis();
                            if ((current - start)/1000 > 2){          // Waits for two seconds
                                Log.i("Hello", "2 seconds passed ");
                                goodNews = false;
                                break;
                            }
                        }
                        Log.i("Hello", "This is address list 2 " + addressList2.toString());
                    } catch (Exception e) {
                        System.out.print(e.getMessage());
                        Log.i("Hello", "Does this throw an IOException? 2" + addressList2.toString());
                    }

                    Address address;

                    if (goodNews) {
                        address = addressList2.get(0);
                        latLng2 = new LatLng(address.getLatitude(), address.getLongitude());
                        p2 = new MarkerOptions().position(latLng2);
                        map.addMarker(p2);
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng2, 10));
                    } else {
                        searchView2.setQuery("Please Enter a Valid Location", false);
                        searchView2.clearFocus();
                        Log.i("Hello", "Bad News");
                    }

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }

        });


        btnGetFare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(latLng == null){
                    searchView.setQuery("Please Enter a Valid Location", false);
                    return;
                }
                if(latLng2 == null){
                    searchView2.setQuery("Please Enter a Valid Location", false);
                    return;
                }

                com.example.databasedemo.Location startLocation = new com.example.databasedemo.Location(latLng.latitude,latLng.longitude);
                com.example.databasedemo.Location endLocation = new com.example.databasedemo.Location(latLng2.latitude,latLng2.longitude);
                double distance = Request.getDistance(startLocation, endLocation);
                Log.i(TAG, "the distance is" + distance);

                fare = Request.calculateFare(distance);
                DecimalFormat numberFormat = new DecimalFormat("#.00");
                Log.i(TAG, "the fare is " + numberFormat.format(fare));
                // String dist = String.valueOf(fare);
                // String url = getUrl(p1.getPosition(), p2.getPosition(), "driving");
                // new FetchURL(RiderNewRequestActivity.this).execute(url, "driving");

                fareDisplay.setVisibility(View.VISIBLE);
                offerDisplay.setVisibility(View.VISIBLE);
                fareDisplay.setText("Calculated Fare: " + numberFormat.format(fare));
                offerDisplay.setText("Offer: " + numberFormat.format(fare));


                btnConfirmRequest.setVisibility(View.VISIBLE);

                // Toast.makeText(getApplicationContext(),dist,Toast.LENGTH_LONG).show();

            }
        });



        btnConfirmRequest.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Rider.requestRide(new com.example.databasedemo.Location(latLng.latitude, latLng.longitude), new com.example.databasedemo.Location(latLng2.latitude, latLng2.longitude));

                // Add to the database
                FirebaseFirestore database = FirebaseFirestore.getInstance();

                DocumentReference myRef = FirebaseFirestore.getInstance().collection("users").document(username);

                myRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            Rider currentRider = task.getResult().toObject(Rider.class);
                            Request request = new Request(currentRider,
                                    new com.example.databasedemo.Location(latLng.latitude, latLng.longitude),
                                    new com.example.databasedemo.Location(latLng2.latitude, latLng2.longitude));
                            addRequest(request, username, email);
                        }
                    }
                });

            }
        }));


        btnAddTip.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tipToAdd = tipAmount.getText().toString();
                double tempTip = Double.valueOf(tipToAdd);
                double tempFare = fare;
                if( tempTip >= 0 )
                {
                    tempFare += tempTip;
                }
                // can put a "Toast" saying invalid tip amount
                offerDisplay.setText("Offer: " + tempFare);
            }
        }));


    }

    /**
     * Adds request to the database then goes to
     * {@link currentRequest#onCreate(Bundle) currentRequest}
     * @param {@code Request}request Request to be added
     * @param {@code String}username Username of user
     * @param {@code String}email Email of user
     */
    public void addRequest (Request request, String username, String email){
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection("requests").document(username).set(request);

        Intent ConfirmedRequest = new Intent(RiderNewRequestActivity.this, currentRequest.class );
        ConfirmedRequest.putExtra("username", username);
        ConfirmedRequest.putExtra("email", email);
//                ConfirmedRequest.putExtra("Latitude", latLng.latitude);
//                ConfirmedRequest.putExtra("Longitude", latLng.longitude);
        startActivity(ConfirmedRequest);
    }


    /**
     * Requests permission to access GPS information
     */
    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);
    }


    /**
     * when map is loaded, assign it to the map attribute
     * @param {@code GoogleMap}googleMap Map Object
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

    }

    /**
     * Shows items in the sidebar
     * @param {@code MenuItem}menuItem Item in the menu
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        switch (menuItem.getItemId()) {
            case R.id.nav_money:
                Intent intent = new Intent(getBaseContext(), moneyScreen.class);
                intent.putExtra("username", username);
                startActivity(intent);
                break;
            case R.id.sign_out_tab:
                mAuth.signOut();
                finish();
                Intent intent_2 = new Intent(getBaseContext(), SignInActivity.class);

                startActivity(intent_2);
                break;
        }


        return false;
    }


//    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
//        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
//        // Destination of route
//        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
//        // Mode
//        String mode = "mode=" + directionMode;
//        // Building the parameters to the web service
//        String parameters = str_origin + "&" + str_dest + "&" + mode;
//        // Output format
//        String output = "json";
//        // Building the url to the web service
//        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.map_key);
//
//        return url;
//    }
//
//
//
//    @Override
//    public void onTaskDone(Object... values) {
//        if(currentPolyline != null){
//            currentPolyline.remove();
//        }
//        //if (values[0] != null) {
//        currentPolyline = map.addPolyline((PolylineOptions) values[0]);
//            //map.addPolyline((PolylineOptions) values[0]);
//    }

}
