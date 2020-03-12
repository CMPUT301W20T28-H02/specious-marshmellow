package com.example.databasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class currentRequest extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {


     GoogleMap map;
     LatLng latLng;
     FusedLocationProviderClient fusedLocationProviderClient;
     Button can_Request;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_request);


        can_Request = findViewById(R.id.can_request);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setActionBar(toolbar);
        }
        NavigationView navi = findViewById(R.id.nav_view);
        navi.setNavigationItemSelectedListener(this);



        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");

        can_Request = findViewById(R.id.can_request);



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        requestPermission();
        if(ActivityCompat.checkSelfPermission(currentRequest.this, ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(currentRequest.this, new OnSuccessListener<Location>() {
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
        can_Request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CollectionReference innerRef = FirebaseFirestore.getInstance().collection("requests");
                innerRef.document(username)//Not actually being removed from the database, only from the display
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Log.i(TAG, "Data deletion successful");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Log.i(TAG, "Data deletion unsuccessful");
                            }
                        });

                Intent intent = new Intent(getBaseContext(), RiderDriverInitialActivity.class);
                intent.putExtra("driver", false);
                intent.putExtra("username", username);

                startActivity(intent);


            }
        });

//        String longitudeString = getIntent().getStringExtra("Longitude");
//        String latitudeString = getIntent().getStringExtra("Latitude");
//
//        Double longitude = Double.valueOf(longitudeString);
//        Double latitude = Double.valueOf(latitudeString);
//
//        latLng = new LatLng(latitude, longitude);
//        MarkerOptions p1 = new MarkerOptions().position(latLng);
//        map.addMarker(p1);
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));


    }



    private void requestPermission() {
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);
    }


    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        /*LatLng UofAQuad = new LatLng( 53.526891, -113.525914 ); // putting long lat of a pin
        map.addMarker( new MarkerOptions().position(UofAQuad).title("U of A Quad") );  // add a pin
        map.moveCamera(CameraUpdateFactory.newLatLng( UofAQuad ) ); // center camera around the pin*/
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_money:
                Intent intent = new Intent(getBaseContext(), moneyScreen.class);

                startActivity(intent);
                break;
            case R.id.sign_out_tab:
                Intent intent_2 = new Intent(getBaseContext(), SignInActivity.class);

                startActivity(intent_2);
                break;
        }


        return false;
    }
}
