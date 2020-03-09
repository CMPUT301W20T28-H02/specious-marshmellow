package com.example.databasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.annotation.Nullable;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

// implements TaskLoadedCallback
public class RiderNewRequestActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap map;
    SupportMapFragment mapFragment;
    SearchView searchView,searchView2;
    Button btnGetFare, btnAddTip, btnConfirmRequest;
    Polyline currentPolyline;
    FusedLocationProviderClient fusedLocationProviderClient;
    LatLng latLng,latLng2, latLng3;
    MarkerOptions p1, p2;
    TextView fareDisplay, offerDisplay;
    TextView tipAmount;
    float fare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_new_request);

        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");

        btnGetFare = findViewById(R.id.btnGetFare);
        btnAddTip = findViewById(R.id.addTipButton);
        btnConfirmRequest = findViewById(R.id.btnConfirmRequest);
        searchView = findViewById(R.id.sv_location);
        searchView2 = findViewById(R.id.sv2_location);
        fareDisplay = findViewById(R.id.fareDisplay);
        offerDisplay = findViewById(R.id.offerDisplay);
        tipAmount = findViewById(R.id.tipAmount);
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
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;
                if(location != null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(RiderNewRequestActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Address address = addressList.get(0);
                    latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    p1 = new MarkerOptions().position(latLng);
                    map.addMarker(p1);
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                return false;
            }
        });


        searchView2.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location2 = searchView2.getQuery().toString();
                List<Address> addressList2 = null;
                if(location2 != null || !location2.equals("")){
                    Geocoder geocoder = new Geocoder(RiderNewRequestActivity.this);
                    try {
                        addressList2 = geocoder.getFromLocationName(location2,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList2.get(0);
                    latLng2 = new LatLng(address.getLatitude(),address.getLongitude());
                    p2 = new MarkerOptions().position(latLng2);

                    map.addMarker(p2);

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng2,10));

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

                float results[]=new float[10];
                Location.distanceBetween(latLng.latitude,latLng.longitude,latLng2.latitude,latLng2.longitude,results);
                float res = results[0];
                fare = 4.0f + (2.0f * res / 1000f);
                String dist = String.valueOf(fare);
                // String url = getUrl(p1.getPosition(), p2.getPosition(), "driving");
                // new FetchURL(RiderNewRequestActivity.this).execute(url, "driving");

                fareDisplay.setVisibility(View.VISIBLE);
                offerDisplay.setVisibility(View.VISIBLE);
                fareDisplay.setText("Calculated Fare: " + fare);
                offerDisplay.setText("Offer: " + fare);


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
                            addRequest(request, username);
                        }
                    }
                });

            }
        }));


        btnAddTip.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tipToAdd = tipAmount.getText().toString();
                float tempTip = Float.valueOf(tipToAdd);
                if( tempTip >= 0 )
                {
                    fare += tempTip;
                }
                // can put a "Toast" saying invalid tip amount
                offerDisplay.setText("Offer: " + fare);
            }
        }));


    }

    public void addRequest (Request request, String username){
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection("requests").document(username).set(request);

        Intent ConfirmedRequest = new Intent(RiderNewRequestActivity.this,currentRequest.class );
//                ConfirmedRequest.putExtra("Latitude", latLng.latitude);
//                ConfirmedRequest.putExtra("Longitude", latLng.longitude);
        startActivity(ConfirmedRequest);
    }



    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

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
