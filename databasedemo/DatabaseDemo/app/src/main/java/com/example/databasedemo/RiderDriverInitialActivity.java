package com.example.databasedemo;

import androidx.annotation.NonNull;
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
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class RiderDriverInitialActivity extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {


    GoogleMap map;
    Button makeRequestButton;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static String TAG = "DISPLAY_USER_ACCOUNT_INFO";
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.rider_initial);
//        makeRequestButton = findViewById(R.id.make_request_button);
        Intent intent = getIntent();

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setActionBar(toolbar);
        }
        NavigationView navi = findViewById(R.id.nav_view);


        boolean driver = intent.getBooleanExtra("driver", true);

        if (driver) {
            Log.i(TAG, "We are here");
            setContentView(R.layout.driver_initial);
//            navi.setNavigationItemSelectedListener(this);
//            makeRequestButton = findViewById(R.id.make_request_button);
        } else {
            setContentView(R.layout.rider_initial);
            makeRequestButton = findViewById(R.id.make_request_button);
//            navi.setNavigationItemSelectedListener(this);

        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        requestPermission();

        if(ActivityCompat.checkSelfPermission(RiderDriverInitialActivity.this, ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(RiderDriverInitialActivity.this, new OnSuccessListener<Location>() {
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


        makeRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(RiderDriverInitialActivity.this, "Make Request", 10);
                Log.i(TAG, "Request made");
                Intent intent = new Intent(RiderDriverInitialActivity.this, RiderNewRequestActivity.class);
                startActivity(intent);
            }
        });

    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);
    }


    @Override
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
