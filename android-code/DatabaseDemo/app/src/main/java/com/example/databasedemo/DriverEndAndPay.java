/*
DriverEndAndPay
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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

/**
 * Activity shown while ride is ongoing, asks driver to click on button once the ride is done
 * @author Michael Antifaoff, Hussein Warsame
 */
public class DriverEndAndPay extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    GoogleMap map;
    Button driverEndAndPayButton;
    // integer code to request camera permission
    private static final int ZXING_CAMERA_PERMISSION = 1;

    // used for creating intent
    private Class<?> mClss;

    String riderUsername;
    String driverUsername;
    TextView usrNameText,usrEmailText;
    ImageView profile;
    String email;
    boolean hasProfilePicture;
    DatabaseReference reff;


    /**
     * Called when activity is created
     * @param {@code Bundle}savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_end_and_pay);
        driverEndAndPayButton = findViewById(R.id.driver_end_and_pay_button);

        mClss = DriverScannerActivity.class;

        Intent i = getIntent();
        riderUsername = i.getStringExtra("riderUsername");
        driverUsername = i.getStringExtra("driverUsername");
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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.driver_ride_map);
        mapFragment.getMapAsync(this);

        driverEndAndPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(getBaseContext(), DriverScannerActivity.class);
//                i.putExtra("riderUsername", riderUsername);
//                i.putExtra("driverUusername", driverUsername);

                if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions( DriverEndAndPay.this,
                            new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
                } else {
                    Intent intent = new Intent(DriverEndAndPay.this, mClss);
                    intent.putExtra("riderUsername", riderUsername);
                    intent.putExtra("driverUsername", driverUsername);
                    startActivity(intent);
                    finish();
                }

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
        map.setMyLocationEnabled(true);

        /*LatLng UofAQuad = new LatLng( 53.526891, -113.525914 ); // putting long lat of a pin
        map.addMarker( new MarkerOptions().position(UofAQuad).title("U of A Quad") );  // add a pin
        map.moveCamera(CameraUpdateFactory.newLatLng( UofAQuad ) ); // center camera around the pin*/
        FirebaseFirestore.getInstance()
                .collection("requests")
                .document(riderUsername)
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

    /**
     * requests permission to use camera in order to scan code, on granted permission goes to
     * {@link DriverScannerActivity#onCreate(Bundle) DriverScannerActivity}
     * @param {@code int}requestCode
     * @param {@code String}permissions
     * @param {@code int[]}grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ZXING_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(mClss != null) {
                        Intent intent = new Intent(this, mClss);
                        intent.putExtra("riderUsername", riderUsername);
                        intent.putExtra("driverUsername", driverUsername);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    DynamicToast.make(DriverEndAndPay.this, getString(R.string.driver_no_camera_permissions),
                            Color.parseColor("#E38249"), Color.parseColor("#000000"), Toast.LENGTH_LONG).show();
                }
                return;
        }
    }
    // Sets intent for different button on the sidebar
    // Can change profile pic, Contact info
    
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
                            DecimalFormat numberFormat = new DecimalFormat(".00");
                            Driver driver = task.getResult().toObject(Driver.class);
                            Wallet wallet = driver.getWallet();
                            DynamicToast.make(DriverEndAndPay.this, getString(R.string.your_balance, String.valueOf(numberFormat.format(wallet.getBalance()))), Color.parseColor("#E38249"), Color.parseColor("#000000"), Toast.LENGTH_LONG).show();

                        }
                    }
                });
                break;
            case R.id.sign_out_tab:
                /*mAuth.signOut();
                finish();
                Intent intent_2 = new Intent(getBaseContext(), SignInActivity.class);
                startActivity(intent_2);*/
                DynamicToast.make(DriverEndAndPay.this, getString(R.string.rider_in_ride), Color.parseColor("#E38249"), Color.parseColor("#000000"), Toast.LENGTH_LONG).show();

                break;
            case R.id.contact_info:
                Intent intent1 = new Intent(getBaseContext(), EditContactInformationActivity.class);
                intent1.putExtra("username", driverUsername);
                startActivity(intent1);

                break;
        }
        return false;
    }

}
