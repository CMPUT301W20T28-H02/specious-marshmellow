/*
DriverRideInfoActivity
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

/**
 * Displays ride information to the driver
 * @author Marcus Blar and Michael Antifaoff
 */
public class DriverRideInfoActivity extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private static String TAG = "DRIVER_RIDE_INFO";
    private GoogleMap map;
    Button confirmRequestButton;
    Button cancelRequestButton;
    TextView riderUsernameTextView;
    TextView rideFareTextView;
    TextView distanceToRiderTextView;
    TextView rideDistanceTextView;
    TextView usrNameText,usrEmailText;
    ImageView profile;
    LatLng startPoint, endPoint;
    FusedLocationProviderClient fusedLocationProviderClient;
    String email;
    boolean hasProfilePicture;
    DatabaseReference reff;
    FirebaseAuth mAuth;

    /**
     * Displays ride information and allows driver accept it or cancel it
     * @param {@code Bundle}savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        // display the ride information for the driver
        // and allow them to accept or deny it
        // takes the rider and driver usernames as intent extras
        // sets the profile pic, username and useremail in the navigation menu/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_ride_info);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.driver_ride_info_map);
        mapFragment.getMapAsync(this);
        Intent i = getIntent();
        final String riderUsername = i.getStringExtra("riderUsername");
        final String driverUsername = i.getStringExtra("driverUsername");
        mAuth = FirebaseAuth.getInstance();

        NavigationView navi = findViewById(R.id.nav_view);
        View headerview = navi.getHeaderView(0);
        navi.setNavigationItemSelectedListener(this);
        usrNameText = headerview.findViewById(R.id.usrNameText);
        usrEmailText=headerview.findViewById(R.id.usrEmailText);
        profile=headerview.findViewById(R.id.profilepic);
        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        usrNameText.setText(driverUsername);
        usrEmailText.setText(email);
        final DocumentReference docRef_2 = FirebaseFirestore.getInstance().collection("users").document(driverUsername);
        docRef_2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Rider rider = task.getResult().toObject(Rider.class);
                    hasProfilePicture = rider.getHasProfilePicture();
                }

                if( hasProfilePicture )
                {
                    reff = FirebaseDatabase.getInstance().getReference().child("Profile pictures").child(driverUsername);
                } else {
                    reff = FirebaseDatabase.getInstance().getReference().child("Profile pictures").child("Will_be_username");
                }
                reff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String url = dataSnapshot.child("imageUrl").getValue().toString();


                        Log.d("Firebase", url);
                        Picasso.get()
                                .load( url )
                                .into( profile );


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


        });




        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), TakeProfilePicture.class);
                startActivity(intent);
            }
        });

        riderUsernameTextView = findViewById(R.id.rider_username_TextView);
        riderUsernameTextView.setText(getString(R.string.driver_confirm_rider_username, riderUsername));

        rideFareTextView = findViewById(R.id.ride_fare_TextView);

        distanceToRiderTextView = findViewById(R.id.distance_to_rider_TextView);
        rideDistanceTextView = findViewById(R.id.ride_distance_TextView);

        confirmRequestButton = findViewById(R.id.confirm_request_button);
        cancelRequestButton = findViewById(R.id.cancel_request_button);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        riderUsernameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DriverRideInfoActivity.this, DisplayUserInfoActivity.class);
                intent.putExtra("username", riderUsername);
                startActivity(intent);
                finish();
            }
        });
        // get the ride
        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("requests").document(riderUsername);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    // for two decimal places
                    final DecimalFormat numberFormat = new DecimalFormat("#.00");

                    // get the fare and the start and end locations from the database
                    final Request request = task.getResult().toObject(Request.class);
                    String fare = String.valueOf(numberFormat.format(request.getFare()));


                    com.example.databasedemo.Location startLocation = request.getStartLocation();
                    com.example.databasedemo.Location endLocation = request.getEndLocation();

                    // get the distance and convert to string
                    double doubleDistance = Request.getDistance(startLocation, endLocation);
                    String distance = String.valueOf(numberFormat.format(doubleDistance));

                    // Get distance to rider from driver's current location
                    fusedLocationProviderClient.getLastLocation().addOnSuccessListener(DriverRideInfoActivity.this, new OnSuccessListener<android.location.Location>() {
                        @Override
                        public void onSuccess(android.location.Location location) {
                            if(location != null){
                                double doubleDistanceToRider = Request.getDistance(new com.example.databasedemo.Location(location.getLatitude(),location.getLongitude()),
                                        request.getStartLocation());
                                String distanceToRider = String.valueOf(numberFormat.format(doubleDistanceToRider));
                                distanceToRiderTextView.setText(getString(R.string.driver_to_rider_distance, distanceToRider));
                            }
                        }
                    });

                    // display the fare and distance
                    rideFareTextView.setText(getString(R.string.driver_confirm_ride_fare, fare));
                    rideDistanceTextView.setText(getString(R.string.driver_confirm_ride_distance, distance));

                    // set start and end points as latlng
                    startPoint = new LatLng(startLocation.getLatitude(), startLocation.getLongitude());
                    endPoint = new LatLng(endLocation.getLatitude(), endLocation.getLongitude());

                    // add markers to map for start and end points
                    map.addMarker(new MarkerOptions().position(startPoint).title("Start Location"));
                    map.addMarker(new MarkerOptions().position(endPoint).title("End Location"));


                    // move map to show the start and end points
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    // set builder with start and end locations
                    builder.include(startPoint);
                    builder.include(endPoint);
                    LatLngBounds bounds = builder.build();
                    // construct a cameraUpdate with a buffer of 200
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 200);
                    // move the camera
                    map.animateCamera(cameraUpdate);
                }
            }
        });

        // confirm request button moves to the driver confirm activity
        confirmRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference docRefDriver = FirebaseFirestore.getInstance().collection("users").document(driverUsername);
                final DocumentReference docRefRequest = FirebaseFirestore.getInstance().collection("requests").document(riderUsername);
                docRefDriver.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            final Driver driver = task.getResult().toObject(Driver.class);
                            docRefRequest.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        Request request = task.getResult().toObject(Request.class);
                                        request.isAcceptedBy(driver);
                                        DocumentReference docRef = FirebaseFirestore.getInstance().collection("requests").document(request.getRider().getUsername());
                                        docRef.set(request);
                                        Intent i = new Intent(DriverRideInfoActivity.this, DriverConfirmActivity.class);  // Directions to start location and confirm pickup button
                                        i.putExtra("riderUsername", riderUsername);
                                        i.putExtra("driverUsername",driverUsername);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                            });
                        }
                    }
                });

//                Intent intent = new Intent(getBaseContext(), DriverConfirmActivity.class);
//                intent.putExtra("riderUsername", riderUsername);
//                intent.putExtra("driverUsername", driverUsername);
//                startActivity(intent);
            }
        });

        // cancel request button returns to previous activity
        cancelRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    /**
     * when map is loaded, assign it to the map attribute
     * @param {@code GoogleMap}googleMap Map Object
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        String driverUsername = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        final DocumentReference docRef_2 = FirebaseFirestore.getInstance().collection("users").document(driverUsername);

        switch (menuItem.getItemId()) {
            case R.id.nav_money:
                docRef_2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Driver driver = task.getResult().toObject(Driver.class);
                            Wallet wallet = driver.getWallet();
                            Toast.makeText(getApplicationContext(), "YOUR BALANCE:"+ wallet.getBalance(), Toast.LENGTH_LONG).show();

                        }
                    }
                });
                break;
            case R.id.sign_out_tab:
                mAuth.signOut();
                finish();
                Intent intent_2 = new Intent(getBaseContext(), SignInActivity.class);
                startActivity(intent_2);
                break;
            case R.id.contact_info:
                Intent intent1 = new Intent(getBaseContext(),EditContactInformationActivity.class);
                intent1.putExtra("username", driverUsername);
                startActivity(intent1);

                break;


        }
        return false;
    }
}
