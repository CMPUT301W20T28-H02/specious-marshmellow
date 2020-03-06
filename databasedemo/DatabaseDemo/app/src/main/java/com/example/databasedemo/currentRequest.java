package com.example.databasedemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class currentRequest extends AppCompatActivity {
//    private GoogleMap map;
//    LatLng latLng;
//    mapFragment = ( SupportMapFragment ) getSupportFragmentManager()
//                .findFragmentById( R.id.map);
//    mapFragment.getMapAsync((OnMapReadyCallback) this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_request);

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



//    public void onMapReady(GoogleMap googleMap) {
//        map = googleMap;
//
//        /*LatLng UofAQuad = new LatLng( 53.526891, -113.525914 ); // putting long lat of a pin
//        map.addMarker( new MarkerOptions().position(UofAQuad).title("U of A Quad") );  // add a pin
//        map.moveCamera(CameraUpdateFactory.newLatLng( UofAQuad ) ); // center camera around the pin*/
//    }


}
