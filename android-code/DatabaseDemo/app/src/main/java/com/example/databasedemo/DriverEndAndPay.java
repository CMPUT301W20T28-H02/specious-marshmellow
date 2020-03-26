/*
DriverEndAndPay
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Activity shown while ride is ongoing, asks driver to click on button once the ride is done
 * @author Michael Antifaoff, Hussein Warsame
 */
public class DriverEndAndPay extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map;
    Button driverEndAndPayButton;
    // integer code to request camera permission
    private static final int ZXING_CAMERA_PERMISSION = 1;

    // used for creating intent
    private Class<?> mClss;

    String riderUsername;
    String driverUsername;

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

        /*LatLng UofAQuad = new LatLng( 53.526891, -113.525914 ); // putting long lat of a pin
        map.addMarker( new MarkerOptions().position(UofAQuad).title("U of A Quad") );  // add a pin
        map.moveCamera(CameraUpdateFactory.newLatLng( UofAQuad ) ); // center camera around the pin*/
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
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }
}
