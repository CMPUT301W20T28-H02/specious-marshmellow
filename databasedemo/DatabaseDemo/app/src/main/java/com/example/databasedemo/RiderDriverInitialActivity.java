package com.example.databasedemo;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class RiderDriverInitialActivity extends FragmentActivity implements OnMapReadyCallback {


    GoogleMap map;
    private static String TAG = "DISPLAY_USER_ACCOUNT_INFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        boolean driver = intent.getBooleanExtra("driver", true);

        if (driver) {
            Log.i(TAG, "We are here");
            setContentView(R.layout.driver_initial);
        } else {
            setContentView(R.layout.rider_initial);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        LatLng UofAQuad = new LatLng( 53.526891, -113.525914 ); // putting long lat of a pin
        map.addMarker( new MarkerOptions().position(UofAQuad).title("U of A Quad") );  // add a pin
        map.moveCamera(CameraUpdateFactory.newLatLng( UofAQuad ) ); // center camera around the pin
    }
}
