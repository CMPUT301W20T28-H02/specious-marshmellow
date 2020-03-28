/*
DriverConfirmActivity
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.squareup.picasso.Picasso;

/**
 * Shows map to driver and asks to confirm pickup
 * @author Micheal Antifaoff
 */
public class DriverConfirmActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    GoogleMap map;
    TextView waiting, riderUsernameTextView, riderPhoneNumberTextView, riderEmailTextView;
    Button driverConfirmPickupButton, cancelPickupButton;
    boolean driverReady = false;
    ListenerRegistration registration;
    DatabaseReference reff;
    String url;
    boolean hasProfilePicture;
    TextView usrNameText,usrEmailText;
    String email;
    ImageView profile;
    String riderUsername;
    String driverUsername;


    /**
     * Called when activity is created
     * displays map and confirm button
     * {@link RiderNewRequestActivity#addRequest(Request, String, String) addRequest}
     * @param {@code Bundle}savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_confirm);
        waiting = findViewById(R.id.waiting_for_rider);
        driverConfirmPickupButton = findViewById(R.id.driver_confirm_pickup_button);
        cancelPickupButton = findViewById(R.id.cancel_pickup_button_confirm_activity);
        riderUsernameTextView = findViewById(R.id.rider_username_driver_confirm_pickup);
        riderPhoneNumberTextView = findViewById(R.id.rider_phone_number_driver_confirm_pickup);
        riderEmailTextView = findViewById(R.id.rider_email_driver_confirm_pickup);
        NavigationView navi = findViewById(R.id.nav_view);
        View headerview = navi.getHeaderView(0);
        navi.setNavigationItemSelectedListener(this);
        usrNameText = headerview.findViewById(R.id.usrNameText);
        usrEmailText=headerview.findViewById(R.id.usrEmailText);
        profile=headerview.findViewById(R.id.profilepic);



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.driver_pickup_map);
        mapFragment.getMapAsync(this);

        Intent i = getIntent();
        riderUsername = i.getStringExtra("riderUsername");
        driverUsername = i.getStringExtra("driverUsername");
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
                        url = dataSnapshot.child("imageUrl").getValue().toString();


                        Log.d("Firebase", url);
                        Picasso.get().load( url ).into( profile );


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

        driverConfirmPickupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DocumentReference docRef = FirebaseFirestore.getInstance().collection("requests").document(riderUsername);

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Request request = task.getResult().toObject(Request.class);
                            request.driverConfirmation();
                            docRef.set(request);
                            driverReady = true;
                            driverConfirmPickupButton.setVisibility(View.INVISIBLE);
                            waiting.setVisibility(View.VISIBLE);
                            cancelPickupButton.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });

        cancelPickupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DocumentReference docRef = FirebaseFirestore.getInstance().collection("requests").document(riderUsername);

                docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent startRiderOrDriverInitial = new Intent(DriverConfirmActivity.this, DriverStartActivity.class);
                        // Activity expects:    boolean driver = intent.getBooleanExtra("driver", true);
                        //                      final String username = intent.getStringExtra("username");
                        //                      final String email = intent.getStringExtra("email");
                        startRiderOrDriverInitial.putExtra("driver", true);
                        startRiderOrDriverInitial.putExtra("username", driverUsername);
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

        /*LatLng UofAQuad = new LatLng( 53.526891, -113.525914 ); // putting long lat of a pin
        map.addMarker( new MarkerOptions().position(UofAQuad).title("U of A Quad") );  // add a pin
        map.moveCamera(CameraUpdateFactory.newLatLng( UofAQuad ) ); // center camera around the pin*/

        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("requests").document(riderUsername);

        registration = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                final Request request = documentSnapshot.toObject(Request.class);
                riderUsernameTextView.setText(getString(R.string.rider_confirm_driver_username, request.getRider().getUsername()));
                // Take user to info activity telling them about the driver
                riderUsernameTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(DriverConfirmActivity.this, DisplayUserInfoActivity.class);
                        i.putExtra("username", request.getRider().getUsername());
                        startActivity(i);
                    }
                });
                riderPhoneNumberTextView.setText(getString(R.string.rider_confirm_driver_phone_number, request.getRider().getPhone()));
                riderEmailTextView.setText(getString(R.string.rider_confirm_driver_email, request.getRider().getEmail()));

                if (driverReady) {
                    driverConfirmPickupButton.setVisibility(View.INVISIBLE);
                    waiting.setVisibility(View.VISIBLE);
                    cancelPickupButton.setVisibility(View.VISIBLE);
                }
                if(request.getRiderConfirmation()&&request.getDriverConfirmation()){
                    Intent i = new Intent(DriverConfirmActivity.this, DriverEndAndPay.class);
                    // Activity expects: final String riderUsername = i.getStringExtra("riderUsername");
                    //                   final String driverUsername = i.getStringExtra("driverUsername");
                    i.putExtra("riderUsername", riderUsername);
                    i.putExtra("driverUsername", driverUsername);
                    startActivity(i);
                    finish();
                }
                if (request.getDriverConfirmation()){
                    driverConfirmPickupButton.setVisibility(View.INVISIBLE);
                    waiting.setVisibility(View.VISIBLE);
                    cancelPickupButton.setVisibility(View.VISIBLE);
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
        });
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
                            Toast.makeText(getApplicationContext(), String.valueOf(wallet.getBalance()), Toast.LENGTH_LONG).show();

                        }
                    }
                });

                break;
            case R.id.sign_out_tab:
                /*mAuth.signOut();
                finish();
                Intent intent_2 = new Intent(getBaseContext(), SignInActivity.class);

                startActivity(intent_2);*/
                Toast.makeText(this, "Action restricted, cancel your request and try again", Toast.LENGTH_LONG).show();

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
