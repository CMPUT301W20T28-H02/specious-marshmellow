/*
RiderConfirmPickup
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * ConfirmPickup from the rider side
 * @author Hussein Warsame, Michael Antifaoff
 */
public class RiderConfirmPickup extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    GoogleMap map;
    TextView waiting, driverUsernameTextView, driverRatingTextView, driverPhoneNumberTextView, driverEmailTextView;
    Button riderConfirmPickupButton, cancelRequestButton;
    FusedLocationProviderClient fusedLocationProviderClient;
    boolean riderReady = false;
    ListenerRegistration registration;
    TextView usrNameText,usrEmailText;
    ImageView profile;
    DatabaseReference reff;
    String url;
    boolean hasProfilePicture;
    String username;


    /**
     * Called when activity is created
     * shows screen before pickup, displays pick up button which confirms pick up when pressed
     * @param {@code Bundle}savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_confirm_pickup);
        riderConfirmPickupButton = findViewById(R.id.rider_confirm_pickup_button);
        cancelRequestButton = findViewById(R.id.cancel_request_button_confirm_activity);
        driverUsernameTextView = findViewById(R.id.driver_username_rider_confirm_pickup);
        driverRatingTextView = findViewById(R.id.driver_rating_rider_confirm_pickup);
        driverPhoneNumberTextView = findViewById(R.id.driver_phone_number_rider_confirm_pickup);
        driverEmailTextView = findViewById(R.id.driver_email_rider_confirm_pickup);
        NavigationView navi = findViewById(R.id.nav_view);
        View headerview = navi.getHeaderView(0);
        navi.setNavigationItemSelectedListener(this);
        usrNameText = headerview.findViewById(R.id.usrNameText);
        usrEmailText=headerview.findViewById(R.id.usrEmailText);

        waiting = findViewById(R.id.waiting_for_driver);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.rider_pickup_map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if(ActivityCompat.checkSelfPermission(RiderConfirmPickup.this, ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(RiderConfirmPickup.this, new OnSuccessListener<android.location.Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    MarkerOptions p3 = new MarkerOptions().position(latLng);
                    map.setMyLocationEnabled(true);

                }
            }
        });

        Intent i = getIntent();
        username = i.getStringExtra("username");
        Log.i("Hello", "RCP: The username is " + username);
        // TODO: Email is null here: Check to see if email is passed through MainActivity
        final String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        usrNameText.setText(username);
        usrEmailText.setText(email);
        profile = headerview.findViewById(R.id.profilepic);
        final DocumentReference docRef_p= FirebaseFirestore.getInstance().collection("users").document(username);
        docRef_p.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Rider rider = task.getResult().toObject(Rider.class);
                    hasProfilePicture = rider.getHasProfilePicture();
                }

                if( hasProfilePicture )
                {
                    reff = FirebaseDatabase.getInstance().getReference().child("Profile pictures").child(username);
                } else {
                    reff = FirebaseDatabase.getInstance().getReference().child("Profile pictures").child("Will_be_username");
                }
                reff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        url = dataSnapshot.child("imageUrl").getValue().toString();


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


        riderConfirmPickupButton.setOnClickListener(new View.OnClickListener() {
            // When they press confirm pickup, should modify the database to set rider confirmation

            @Override
            public void onClick(View view) {
                final DocumentReference docRef = FirebaseFirestore.getInstance().collection("requests").document(username);

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Request request = task.getResult().toObject(Request.class);
                            request.riderConfirmation();
                            docRef.set(request);
                            riderReady = true;
                            riderConfirmPickupButton.setVisibility(View.INVISIBLE);
                            waiting.setVisibility(View.VISIBLE);
                            cancelRequestButton.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });

        cancelRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(22);

                final DocumentReference docRef = FirebaseFirestore.getInstance().collection("requests").document(username);

                docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent startRiderOrDriverInitial = new Intent(RiderConfirmPickup.this, RiderStartActivity.class);
                        // Activity expects:    boolean driver = intent.getBooleanExtra("driver", true);
                        //                      final String username = intent.getStringExtra("username");
                        //                      final String email = intent.getStringExtra("email");
                        startRiderOrDriverInitial.putExtra("driver", false);
                        startRiderOrDriverInitial.putExtra("username", username);
                        startRiderOrDriverInitial.putExtra("email", email);
                        startActivity(startRiderOrDriverInitial);
                        finish();
                    }
                });
            }
        });


    }

    @Override
    public void onDestroy(){
        registration.remove();
        super.onDestroy();
    }

    /**
     * when map is loaded, assign it to the map attribute
     * @param {@code GoogleMap}googleMap Map Object
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);

        /*LatLng UofAQuad = new LatLng( 53.526891, -113.525914 ); // putting long lat of a pin
        map.addMarker( new MarkerOptions().position(UofAQuad).title("U of A Quad") );  // add a pin
        map.moveCamera(CameraUpdateFactory.newLatLng( UofAQuad ) ); // center camera around the pin*/

        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("requests").document(username);

        registration = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    final Request request = documentSnapshot.toObject(Request.class);
                    driverUsernameTextView.setText(getString(R.string.rider_confirm_driver_username, request.getDriver().getUsername()));
                    // Take user to info activity telling them about the driver
                    driverUsernameTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(RiderConfirmPickup.this, DisplayUserInfoActivity.class);
                            i.putExtra("username", request.getDriver().getUsername());
                            startActivity(i);
                        }
                    });
                    DecimalFormat numberFormat = new DecimalFormat("#.00");
                    driverRatingTextView.setText(getString(R.string.rider_confirm_driver_rating, numberFormat.format(request.getDriver().getRating())));
                    driverPhoneNumberTextView.setText(getString(R.string.rider_confirm_driver_phone_number, request.getDriver().getPhone()));
                    driverPhoneNumberTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String uri = "tel:" + request.getDriver().getPhone().trim();
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse(uri));
                            startActivity(intent);
                        }
                    });
                    driverEmailTextView.setText(getString(R.string.rider_confirm_driver_email, request.getDriver().getEmail()));
                    if (riderReady) {
                        riderConfirmPickupButton.setVisibility(View.INVISIBLE);
                        waiting.setVisibility(View.VISIBLE);
                        cancelRequestButton.setVisibility(View.VISIBLE);
                    }
                    if (request.getRiderConfirmation()&&request.getDriverConfirmation()) {

                        Intent i = new Intent(RiderConfirmPickup.this, RiderEndAndPay.class);
                        i.putExtra("username", username);
                        startActivity(i);
                        finish();
                    }
                    if (request.getRiderConfirmation()) {
                        riderConfirmPickupButton.setVisibility(View.INVISIBLE);
                        waiting.setVisibility(View.VISIBLE);
                        cancelRequestButton.setVisibility(View.VISIBLE);
                    }

                    com.example.databasedemo.Location startLocation = request.getStartLocation();
                    com.example.databasedemo.Location endLocation = request.getEndLocation();


                    // set start and end points as latlng
                    LatLng startPoint = new LatLng(startLocation.getLatitude(), startLocation.getLongitude());
                    LatLng endPoint = new LatLng(endLocation.getLatitude(), endLocation.getLongitude());

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
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        switch (menuItem.getItemId()) {
            case R.id.nav_money:
                Intent intent = new Intent(getBaseContext(), moneyScreen.class);
                intent.putExtra("username", username);
                intent.putExtra("activity",RiderConfirmPickup.class.toString());
                startActivity(intent);
                break;
            case R.id.sign_out_tab:
                /*mAuth.signOut();
                finish();
                Intent intent_2 = new Intent(getBaseContext(), SignInActivity.class);

                startActivity(intent_2);*/
                DynamicToast.make(RiderConfirmPickup.this, "Action restricted, ride on way", Color.parseColor("#E38249"), Color.parseColor("#000000"), Toast.LENGTH_LONG).show();

                break;
            case R.id.contact_info:
                Intent intent1 = new Intent(getBaseContext(),EditContactInformationActivity.class);
                intent1.putExtra("username", username);
                startActivity(intent1);

                break;


        }
        return false;
    }
}
