package com.example.databasedemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.annotation.Nullable;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.databasedemo.DirectionHelpers.TaskLoadedCallback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.example.databasedemo.DirectionHelpers.FetchURL;
import com.example.databasedemo.DirectionHelpers.TaskLoadedCallback;

import java.io.IOException;
import java.util.List;

// implements TaskLoadedCallback
public class RiderNewRequestActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private GoogleMap map;
    SupportMapFragment mapFragment;
    SearchView searchView,searchView2;
    Button btnGetFare, btnAddTip, btnMinusTip;
    Polyline currentPolyline;
    LatLng latLng2,latLng;
    MarkerOptions p1, p2;
    TextView fareDisplay, offerDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_new_request);

        btnGetFare = findViewById(R.id.btnGetFare);
        btnAddTip = findViewById(R.id.addTipButton);
        btnMinusTip = findViewById(R.id.minusTipButton);
        searchView = findViewById(R.id.sv_location);
        searchView2 = findViewById(R.id.sv2_location);
        fareDisplay = findViewById(R.id.fareDisplay);
        offerDisplay = findViewById(R.id.offerDisplay);
        mapFragment = ( SupportMapFragment ) getSupportFragmentManager()
                .findFragmentById( R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;
                if(location != null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(RiderNewRequestActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Address address = addressList.get(0);
                    latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    p1 = new MarkerOptions().position(latLng);
                    map.addMarker(p1);
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                map.clear();
                return false;
            }
        });
        searchView2.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location2 = searchView2.getQuery().toString();
                List<Address> addressList2 = null;
                if(location2 != null || !location2.equals("")){
                    Geocoder geocoder = new Geocoder(RiderNewRequestActivity.this);
                    try {
                        addressList2 = geocoder.getFromLocationName(location2,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList2.get(0);
                    latLng2 = new LatLng(address.getLatitude(),address.getLongitude());
                    p2 = new MarkerOptions().position(latLng2);

                    map.addMarker(p2);

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng2,10));

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                map.clear();
                return false;
            }

        });
        btnGetFare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                float results[]=new float[10];
                Location.distanceBetween(latLng.latitude,latLng.longitude,latLng2.latitude,latLng2.longitude,results);
                float res = results[0];
                float fare = 4.0f + (2.0f * res / 1000f);
                String dist = String.valueOf(fare);
                String url = getUrl(p1.getPosition(), p2.getPosition(), "driving");
                new FetchURL(RiderNewRequestActivity.this).execute(url, "driving");

                fareDisplay.setVisibility(View.VISIBLE);
                offerDisplay.setVisibility(View.VISIBLE);
                fareDisplay.setText("Calculated Fare: " + fare);
                offerDisplay.setText("Offer: " + fare);



                // Toast.makeText(getApplicationContext(),dist,Toast.LENGTH_LONG).show();

            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

    }


    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.map_key);

        return url;
    }



    @Override
    public void onTaskDone(Object... values) {
        if(currentPolyline != null){
            currentPolyline.remove();
        }
        //if (values[0] != null) {
        currentPolyline = map.addPolyline((PolylineOptions) values[0]);
            //map.addPolyline((PolylineOptions) values[0]);
    }

}
