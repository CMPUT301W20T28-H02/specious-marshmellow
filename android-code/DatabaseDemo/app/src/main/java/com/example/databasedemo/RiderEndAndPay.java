/*
RiderEndAndPay
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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

/**
 * Activity shown while ride is ongoing, asks rider to click on button once the ride is done
 * @author Michael Antifaoff, Hussein Warsame, Rafaella Graña
 */
public class RiderEndAndPay extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    GoogleMap map;
    Button riderEndAndPayButton;
    TextView usrName,usrEmail;
    ImageView profile;
    boolean hasProfilePicture;
    DatabaseReference reff;
    String username;

    /**
     * Called when activity is created
     * shows rider the ride on the map and a button to indicate if ride has ended. then redirects
     * to {@link GenerateQR GenerateQR}
     * @param {@code Bundle} savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Hello", "We are inside onCreate() of RiderEndAndPay");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_end_and_pay);
        riderEndAndPayButton = findViewById(R.id.rider_end_and_pay_button);
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        NavigationView navi = findViewById(R.id.nav_view);
        View headerview = navi.getHeaderView(0);
        navi.setNavigationItemSelectedListener(this);
        usrName = headerview.findViewById(R.id.usrNameText);
        usrEmail = headerview.findViewById(R.id.usrEmailText);
        profile = headerview.findViewById(R.id.profilepic);
        usrEmail.setText(email);

        // Cancel any leftover notifications to prevent user from clicking on them after
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(22);

        Intent i = getIntent();
        username = i.getStringExtra("username");
        usrName.setText(username);
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
                        Picasso.get().load(url).into(profile);
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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.rider_ride_map);
        mapFragment.getMapAsync(this);

        riderEndAndPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RiderEndAndPay.this, GenerateQR.class);
                i.putExtra("username", username);
                startActivity(i);
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

        /*LatLng UofAQuad = new LatLng( 53.526891, -113.525914 ); // putting long lat of a pin
        map.addMarker( new MarkerOptions().position(UofAQuad).title("U of A Quad") );  // add a pin
        map.moveCamera(CameraUpdateFactory.newLatLng( UofAQuad ) ); // center camera around the pin*/

        map.setMyLocationEnabled(true);

        /*LatLng UofAQuad = new LatLng( 53.526891, -113.525914 ); // putting long lat of a pin
        map.addMarker( new MarkerOptions().position(UofAQuad).title("U of A Quad") );  // add a pin
        map.moveCamera(CameraUpdateFactory.newLatLng( UofAQuad ) ); // center camera around the pin*/
        FirebaseFirestore.getInstance()
                .collection("requests")
                .document(username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                final Request request = documentSnapshot.toObject(Request.class);

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
                intent.putExtra("activity",currentRequest.class.toString());
                startActivity(intent);
                break;
            case R.id.sign_out_tab:
                /*mAuth.signOut();
                finish();
                Intent intent_2 = new Intent(getBaseContext(), SignInActivity.class);

                startActivity(intent_2);*/
                DynamicToast.make(RiderEndAndPay.this, getString(R.string.rider_in_ride), Color.parseColor("#E38249"), Color.parseColor("#000000"), Toast.LENGTH_LONG).show();


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
