/*
RiderNewRequestActivity
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
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
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

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
    TextView offerDisplay,tipLabel,usrNameText,usrEmailText;
    EditText tipAmount;
    double globalFare = 0;
    double globalTip = 0;
    FirebaseAuth mAuth;
    ImageView profile;
    DatabaseReference reff;
    String url;
    boolean hasProfilePicture;

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


        NavigationView navi = findViewById(R.id.nav_view);
        View headerView = navi.getHeaderView(0);
        navi.setNavigationItemSelectedListener(this);
        mAuth = FirebaseAuth.getInstance();


        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");
        final String email = intent.getStringExtra("email");

        btnGetFare = findViewById(R.id.btnGetFare);

        //btnAddTip = findViewById(R.id.addTipButton);
        btnConfirmRequest = findViewById(R.id.btnConfirmRequest);
        searchView = findViewById(R.id.sv_location);
        searchView2 = findViewById(R.id.sv2_location);
        offerDisplay = findViewById(R.id.offerDisplay);
        tipLabel = findViewById(R.id.tipLabel);
        tipAmount = findViewById(R.id.tipAmount);
        usrNameText = headerView.findViewById(R.id.usrNameText);
        usrEmailText=headerView.findViewById(R.id.usrEmailText);
        profile=headerView.findViewById(R.id.profilepic);

        /* null check is only to enable UI testing, since any intent extras will be null
          when testing an activity in isolation.
         */
        if (username != null) {

            final DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(username);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Rider rider = task.getResult().toObject(Rider.class);
                        hasProfilePicture = rider.getHasProfilePicture();
                    }

                    if (hasProfilePicture) {
                        reff = FirebaseDatabase.getInstance().getReference().child("Profile pictures").child(username);
                    } else {
                        reff = FirebaseDatabase.getInstance().getReference().child("Profile pictures").child("Will_be_username");
                    }
                    reff.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String url = dataSnapshot.child("imageUrl").getValue().toString();


                            Log.d("Firebase", url);
                            Picasso.get()
                                    .load(url)
                                    .into(profile);


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }


            });
        }



        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), TakeProfilePicture.class);
                startActivity(intent);

            }
        });
        usrNameText.setText(username);
        usrEmailText.setText(email);

        searchView.setQuery(getString(R.string.query_current_location), false);
        mapFragment = ( SupportMapFragment ) getSupportFragmentManager()
                .findFragmentById( R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);


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
                    map.setMyLocationEnabled(true);
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
                        Log.i("Hello", "Does this throw an IOException? 1" + addressList.toString());
                        goodNews = false;
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
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    } else {
                        searchView.setQuery(getString(R.string.query_invalid_location), false);
                        searchView.clearFocus();
                        DynamicToast.make(RiderNewRequestActivity.this, getString(R.string.internet_and_valid_location),
                                Color.parseColor("#E38249"), Color.parseColor("#000000"), Toast.LENGTH_LONG).show();
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

                btnGetFare.setVisibility(View.VISIBLE);
                offerDisplay.setVisibility(View.INVISIBLE);
                tipLabel.setVisibility(View.INVISIBLE);
                tipAmount.setVisibility(View.INVISIBLE);
                btnConfirmRequest.setVisibility(View.INVISIBLE);


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
                        Log.i("Hello", "Does this throw an IOException? 2" + addressList2.toString());
                        goodNews = false;
                    }

                    Address address;

                    if (goodNews) {
                        address = addressList2.get(0);
                        latLng2 = new LatLng(address.getLatitude(), address.getLongitude());
                        p2 = new MarkerOptions().position(latLng2);
                        map.addMarker(p2);
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng2, 15));
                    } else {
                        searchView2.setQuery(getString(R.string.query_invalid_location), false);
                        searchView2.clearFocus();
                        DynamicToast.make(RiderNewRequestActivity.this, getString(R.string.internet_and_valid_location),
                                Color.parseColor("#E38249"), Color.parseColor("#000000"), Toast.LENGTH_LONG).show();
                        Log.i("Hello", "Bad News");
                    }

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                btnGetFare.setVisibility(View.VISIBLE);
                offerDisplay.setVisibility(View.INVISIBLE);
                tipLabel.setVisibility(View.INVISIBLE);
                tipAmount.setVisibility(View.INVISIBLE);
                btnConfirmRequest.setVisibility(View.INVISIBLE);

                return false;
            }

        });


        btnGetFare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(latLng == null){
                    searchView.setQuery(getString(R.string.query_invalid_location), false);
                    return;
                }
                if(latLng2 == null){
                    searchView2.setQuery(getString(R.string.query_invalid_location), false);
                    return;
                }

                Log.i(TAG, "Latitude: " + latLng.latitude + " Longitude: " + latLng.longitude);
                Log.i(TAG, "Latitude: " + latLng2.latitude + " Longitude: " + latLng2.longitude);
                com.example.databasedemo.Location startLocation = new com.example.databasedemo.Location(latLng.latitude,latLng.longitude);
                com.example.databasedemo.Location endLocation = new com.example.databasedemo.Location(latLng2.latitude,latLng2.longitude);
                double distance = Request.getDistance(startLocation, endLocation);
                Log.i(TAG, "the distance is" + distance);

                globalFare = Request.calculateFare(distance);
                DecimalFormat numberFormat = new DecimalFormat("#.00");
                Log.i(TAG, "the fare is " + numberFormat.format(globalFare));
                // String dist = String.valueOf(fare);
                // String url = getUrl(p1.getPosition(), p2.getPosition(), "driving");
                // new FetchURL(RiderNewRequestActivity.this).execute(url, "driving");

                offerDisplay.setVisibility(View.VISIBLE);
                offerDisplay.setText(getString(R.string.offer_rider_new_request, numberFormat.format(globalFare)));

                tipLabel.setVisibility(View.VISIBLE);

                tipAmount.setVisibility(View.VISIBLE);

                btnConfirmRequest.setVisibility(View.VISIBLE);

                btnGetFare.setVisibility(View.INVISIBLE);
                // DynamicToast.make(RiderNewRequestActivity.this, "Distance: " + dist, Color.parseColor("#E38249"), Color.parseColor("#000000"), Toast.LENGTH_LONG).show();

            }
        });

        tipAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().equals("")) {
                    globalTip = Double.valueOf(tipAmount.getText().toString());
                    DecimalFormat numberFormat = new DecimalFormat("#.00");
                    offerDisplay.setText(getString(R.string.offer_rider_new_request, numberFormat.format(globalFare + globalTip)));
                } else {
                    DecimalFormat numberFormat = new DecimalFormat("#.00");
                    offerDisplay.setText(getString(R.string.offer_rider_new_request, numberFormat.format(globalFare)));
                    globalTip = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

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
                            request.setFare(request.getFare()+globalTip);

                            addRequest(request, username, email);

                        }
                    }
                });

            }
        }));


//        btnAddTip.setOnClickListener((new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                String tipToAdd = tipAmount.getText().toString();
//                double tempTip = Double.valueOf(tipToAdd);
//                double farePlusTip = fare;
//                if( tempTip >= 0 )
//                {
//                    farePlusTip += tempTip;
//                }
//                globalTip = tempTip;
//                DecimalFormat numberFormat = new DecimalFormat("#.00");
//                // can put a "Toast" saying invalid tip amount
//                offerDisplay.setText(getString(R.string.offer_rider_new_request, numberFormat.format(farePlusTip)));
//            }
//        }));


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
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
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
                Intent intent = new Intent(RiderNewRequestActivity.this, moneyScreen.class);
                intent.putExtra("username", username);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                break;
            case R.id.sign_out_tab:
                mAuth.signOut();
                finish();
                Intent intent_2 = new Intent(RiderNewRequestActivity.this, SignInActivity.class);
                intent_2.putExtra("activity",moneyScreen.class.toString());
                startActivity(intent_2);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
                break;
            case R.id.contact_info:
                Intent intent1 = new Intent(RiderNewRequestActivity.this,EditContactInformationActivity.class);
                intent1.putExtra("username", username);
                startActivity(intent1);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
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
