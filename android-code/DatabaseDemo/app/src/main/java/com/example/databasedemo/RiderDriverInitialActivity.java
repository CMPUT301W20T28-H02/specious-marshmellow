/*
RiderDriverInitialActivity
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import androidx.annotation.NonNull;


import androidx.annotation.Nullable;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.material.navigation.NavigationView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 *
 */
public class RiderDriverInitialActivity extends FragmentActivity implements OnMapReadyCallback{


    GoogleMap map;
    Button makeRequestButton;
    EditText globalBoundsEditText;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static String TAG = "Hello";
    LatLng latLng, latLngDriver;
    ListView requestListView;
    ArrayAdapter<Request> requestArrayAdapter;
    ArrayList<Request> requestArrayList;
    double globalBound = 10000;
    boolean driver;
    ListenerRegistration registration;

    /**
     * Called when activity is created
     * Displays intial activity for rider and driver. For rider, lets rider define pickup locations
     * for driver, displays requests based on distance and lets choose one
     * @param {@code Bundle}savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Hello", "We are inside onCreate() of RiderDriverInitialActivity");
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();


        //NavigationView navi = findViewById(R.id.nav_view);


        driver = intent.getBooleanExtra("driver", true);
        final String username = intent.getStringExtra("username");
        final String email = intent.getStringExtra("email");

        if (driver) {
            Log.i(TAG, "We are here");
            setContentView(R.layout.driver_initial);
//          navi.setNavigationItemSelectedListener(this);
        } else {
            setContentView(R.layout.rider_initial);

        }

        makeRequestButton = findViewById(R.id.make_request_button);
        globalBoundsEditText = findViewById(R.id.global_bounds_EditText);
        globalBoundsEditText.setText(String.valueOf((int)globalBound));
        requestListView = findViewById(R.id.requestListView);
        requestArrayList = new ArrayList<>();
        requestArrayAdapter = new RequestAdapter(this, requestArrayList);

        requestListView.setAdapter(requestArrayAdapter);

        requestListView.setBackgroundColor(0xFFFFFF);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        globalBoundsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                requestArrayList.clear();
                map.clear();
                if(!charSequence.toString().equals("")) {
                    globalBound = Double.valueOf(globalBoundsEditText.getText().toString());
                    CollectionReference myRef = FirebaseFirestore.getInstance().collection("requests");
                    myRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    final Request request = doc.toObject(Request.class);
                                    if (!request.getRequestStatus()) {

                                        if (driver) {
                                            // set rider start point as latlng
                                            LatLng riderLocation = new LatLng(request.getStartLocation().getLatitude(), request.getStartLocation().getLongitude());
                                            // add markers to map for rider start location
                                            map.addMarker(new MarkerOptions().position(riderLocation).title(request.getRider().getUsername()));
                                        }

                                        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(RiderDriverInitialActivity.this, new OnSuccessListener<Location>() {
                                            @Override
                                            public void onSuccess(Location location) {
                                                if(location != null){
                                                    double distance = Request.getDistance(new com.example.databasedemo.Location(location.getLatitude(),location.getLongitude()),
                                                            request.getStartLocation());
                                                    Log.i("Hello", "Even Better! Distance: " + distance + "Global Bound: " + globalBound);
                                                    if (distance < globalBound){
                                                        Log.i("Hello", "Is this getting run?");
                                                        addRequest(request);
                                                    } else{
                                                        requestArrayAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }

                                map.setMyLocationEnabled(true);

                                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(RiderDriverInitialActivity.this, new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        if (location != null) {
                                            latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

                                        }

                                    }
                                });
                            }
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        requestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.i("Hello", "Rider Driver Initial Activity: inside ListView onItemClickListener");
                final Request request = requestArrayList.get(position);
                Intent i = new Intent(RiderDriverInitialActivity.this, DriverRideInfoActivity.class);  // Directions to start location and confirm pickup button
                i.putExtra("riderUsername", request.getRider().getUsername());
                i.putExtra("driverUsername",username);
                i.putExtra("email", email);
                startActivity(i);
                finish();
            }
        });

        makeRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DynamicToast.make(RiderDriverInitialActivity.this, "Make Request", Color.parseColor("#E38249"), Color.parseColor("#000000"), Toast.LENGTH_LONG).show();

                Log.i("Hello", "Rider Driver Initial Activity: inside make request button onItemClickListener");
                Intent intent = new Intent(RiderDriverInitialActivity.this, RiderNewRequestActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("email", email );
                startActivity(intent);
                finish();
            }
        });

    }

    public void onDestroy(){
        registration.remove();
        super.onDestroy();
    }

    /**
     * add request to request array sorted by distance
     * @param {@code Request}request
     */
    public void addRequest(Request request){
        requestArrayList.add(request);
        Collections.sort(requestArrayList, new Comparator<Request>() {
            @Override
            public int compare(Request request, Request request2) {
                return request.getFare() < request2.getFare() ? 1
                        : request.getFare() > request2.getFare() ? -1
                        : 0;
            }
        });
        requestArrayAdapter.notifyDataSetChanged();
    }

    /**
     * requests permission to access location services
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

        requestPermission();
    }



    /**
     * update map once permission is granted
     * @param {@code int}requestCode
     * @param {@code String[]}permissions
     * @param {@code int[]}grantResults
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (permissions[0].equals(ACCESS_FINE_LOCATION)) {

            map.setMyLocationEnabled(true);

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(RiderDriverInitialActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    }

                }
            });
            fusedLocationProviderClient.getLastLocation().addOnFailureListener(RiderDriverInitialActivity.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    DynamicToast.make(RiderDriverInitialActivity.this, "Without your location, we cannot show you a list of rides near you. Please enable location services and try again.", Color.parseColor("#E38249"), Color.parseColor("#000000"), Toast.LENGTH_LONG).show();

                }
            });
        } else {
            DynamicToast.make(RiderDriverInitialActivity.this,
                    "Without your location, we cannot show you a list of rides near you. Please enable location services and try again.",
                    Color.parseColor("#E38249"), Color.parseColor("#000000"), Toast.LENGTH_LONG).show();

        }


        CollectionReference myRef = FirebaseFirestore.getInstance().collection("requests");

        registration = myRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                requestArrayList.clear();
                map.clear();
                for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
                    final Request request = doc.toObject(Request.class);
                    if (!request.getRequestStatus()) {

                        if (driver) {
                            // set rider start point as latlng
                            LatLng riderLocation = new LatLng(request.getStartLocation().getLatitude(), request.getStartLocation().getLongitude());
                            // add markers to map for rider start location
                            map.addMarker(new MarkerOptions().position(riderLocation).title(request.getRider().getUsername()));
                        }

                        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(RiderDriverInitialActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if(location != null){
                                    double distance = Request.getDistance(new com.example.databasedemo.Location(location.getLatitude(),location.getLongitude()),
                                            request.getStartLocation());
                                    if (distance < globalBound){
                                        addRequest(request);
                                    }
                                }
                            }
                        });
                    }
                }

                map.setMyLocationEnabled(true);
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(RiderDriverInitialActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

                        }

                    }
                });
            }
        });
    }
}

