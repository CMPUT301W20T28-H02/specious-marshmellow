/*
RiderConfirmPickup
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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * ConfirmPickup from the rider side
 * @author Hussein Warsame, Michael Antifaoff
 */
public class RiderConfirmPickup extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map;
    TextView waiting, driverUsernameTextView, driverPhoneNumberTextView, driverEmailTextView;
    Button riderConfirmPickupButton, cancelRequestButton;
    boolean riderReady = false;
    ListenerRegistration registration;


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
        driverPhoneNumberTextView = findViewById(R.id.driver_phone_number_rider_confirm_pickup);
        driverEmailTextView = findViewById(R.id.driver_email_rider_confirm_pickup);

        waiting = findViewById(R.id.waiting_for_driver);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.rider_pickup_map);
        mapFragment.getMapAsync(this);

        Intent i = getIntent();
        final String username = i.getStringExtra("username");
        Log.i("Hello", "RCP: The username is " + username);
        // TODO: Email is null here: Check to see if email is passed through MainActivity
        final String email = i.getStringExtra("email");


        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("requests").document(username);

        registration = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    Request request = documentSnapshot.toObject(Request.class);
                    // TODO: Take user to a display info activity instead that tells them all about the driver
                    driverUsernameTextView.setText(getString(R.string.rider_confirm_driver_username, request.getDriver().getUsername()));
                    driverPhoneNumberTextView.setText(getString(R.string.rider_confirm_driver_phone_number, request.getDriver().getPhone()));
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
                }
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
                final DocumentReference docRef = FirebaseFirestore.getInstance().collection("requests").document(username);

                docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent startRiderOrDriverInitial = new Intent(RiderConfirmPickup.this, RiderDriverInitialActivity.class);
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

        /*LatLng UofAQuad = new LatLng( 53.526891, -113.525914 ); // putting long lat of a pin
        map.addMarker( new MarkerOptions().position(UofAQuad).title("U of A Quad") );  // add a pin
        map.moveCamera(CameraUpdateFactory.newLatLng( UofAQuad ) ); // center camera around the pin*/
    }
}
