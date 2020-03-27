package com.example.databasedemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.ListenerRegistration;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class RiderStartActivity extends FragmentActivity implements OnMapReadyCallback {
    GoogleMap map;
    Button makeRequestButton;
    EditText globalBoundsEditText;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static String TAG = "Hello";
    LatLng latLng, latLngDriver;
    double globalBound = 10000;
    ListenerRegistration registration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_initial);

        Intent intent = getIntent();


        //NavigationView navi = findViewById(R.id.nav_view);


        final String username = intent.getStringExtra("username");
        final String email = intent.getStringExtra("email");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        makeRequestButton = findViewById(R.id.make_request_button);
        makeRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(RiderDriverInitialActivity.this, "Make Request", 10);
                Log.i("Hello", "Rider Driver Initial Activity: inside make request button onItemClickListener");
                Intent intent = new Intent(getBaseContext(), RiderNewRequestActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("email", email );
                startActivity(intent);
                finish();
            }
        });
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);
    }
    @Override
    public void onMapReady(GoogleMap googleMap){
        map = googleMap;
        requestPermission();
    }
}
