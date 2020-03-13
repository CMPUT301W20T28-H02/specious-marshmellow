package com.example.databasedemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.gms.maps.model.LatLng;


public class DriverRideInfoActivity extends FragmentActivity implements OnMapReadyCallback {

    private static String TAG = "DRIVER_RIDE_INFO";
    private GoogleMap map;
    Button confirmRequestButton;
    Button cancelRequestButton;
    TextView riderUsernameTextView;
    TextView rideFareTextView;
    TextView rideDistanceTextView;
    LatLng startPoint, endPoint;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        // display the ride information for the driver
        // and allow them to accept or deny it
        // takes the rider and driver usernames as intent extras
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_ride_info);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.driver_ride_info_map);
        mapFragment.getMapAsync(this);
        Intent i = getIntent();
        final String riderUsername = i.getStringExtra("riderUsername");
        final String driverUsername = i.getStringExtra("driverUsername");

        riderUsernameTextView = findViewById(R.id.rider_username_TextView);
        riderUsernameTextView.setText(getString(R.string.driver_confirm_rider_username, riderUsername));

        rideFareTextView = findViewById(R.id.ride_fare_TextView);

        rideDistanceTextView = findViewById(R.id.ride_distance_TextView);

        confirmRequestButton = findViewById(R.id.confirm_request_button);
        cancelRequestButton = findViewById(R.id.cancel_request_button);

        riderUsernameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), DisplayUserInfoActivity.class);
                intent.putExtra("username", riderUsername);
                startActivity(intent);
            }
        });
        // get the ride
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("requests").document(riderUsername);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    // get the fare and the start and end locations from the database
                    Request request = task.getResult().toObject(Request.class);
                    String fare = String.valueOf(request.getFare());

                    com.example.databasedemo.Location startLocation = request.getStartLocation();
                    com.example.databasedemo.Location endLocation = request.getEndLocation();

                    // get the distance and convert to string
                    double doubleDistance = Request.getDistance(startLocation, endLocation);
                    String distance = String.valueOf(doubleDistance);

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
                Intent intent = new Intent(getBaseContext(), DriverConfirmActivity.class);
                intent.putExtra("riderUsername", riderUsername);
                intent.putExtra("driverUsername", driverUsername);
                startActivity(intent);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }
}