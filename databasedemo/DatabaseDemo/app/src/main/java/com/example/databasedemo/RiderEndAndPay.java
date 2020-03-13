package com.example.databasedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class RiderEndAndPay extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map;
    Button riderEndAndPayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_end_and_pay);
        riderEndAndPayButton = findViewById(R.id.rider_end_and_pay_button);

        Intent i = getIntent();
        final String riderUsername = i.getStringExtra("riderUsername");
        final String driverUsername = i.getStringExtra("driverusername");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.driver_ride_map);
        mapFragment.getMapAsync(this);

        riderEndAndPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), RiderEndAndPay.class);
                i.putExtra("riderUsername", riderUsername);
                i.putExtra("driverUusername", driverUsername);

            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        /*LatLng UofAQuad = new LatLng( 53.526891, -113.525914 ); // putting long lat of a pin
        map.addMarker( new MarkerOptions().position(UofAQuad).title("U of A Quad") );  // add a pin
        map.moveCamera(CameraUpdateFactory.newLatLng( UofAQuad ) ); // center camera around the pin*/
    }
}