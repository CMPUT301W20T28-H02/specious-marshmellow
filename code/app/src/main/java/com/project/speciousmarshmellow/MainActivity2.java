package com.project.speciousmarshmellow;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SearchView;
import android.widget.Toast;
import android.util.Log;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.project.speciousmarshmellow.DirectionHelpers.FetchURL;
import com.project.speciousmarshmellow.DirectionHelpers.TaskLoadedCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import java.io.IOException;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    SupportMapFragment mapFragment;
    SearchView searchView, searchView2;
    Button btnGetDirection,btnGetCurrentLocation;
    Polyline currentPolyline;
    LatLng latLng2, latLng,latLng3;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int  REQUEST_CODE = 101;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        requestPermission();
        btnGetDirection = findViewById(R.id.btnGetDirection);
        btnGetCurrentLocation = findViewById(R.id.btnGetCurrentLocation);
        btnGetCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            if(ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        latLng3 = new LatLng(location.getLatitude(),location.getLongitude());
                        MarkerOptions p3 = new MarkerOptions().position(latLng3);
                        map.addMarker(p3);
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng3,10));




                    }

                }
            });
            }

        });





        searchView = findViewById(R.id.sv_location);
        searchView2 = findViewById(R.id.sv2_location);
        mapFragment = ( SupportMapFragment ) getSupportFragmentManager()
                .findFragmentById( R.id.map);
        mapFragment.getMapAsync(this);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;
                if(location != null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    MarkerOptions p1 = new MarkerOptions().position(latLng);
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
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try {
                        addressList2 = geocoder.getFromLocationName(location2,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList2.get(0);
                    latLng2 = new LatLng(address.getLatitude(),address.getLongitude());
                    MarkerOptions p2 = new MarkerOptions().position(latLng2);



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
        btnGetDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                float results[]=new float[10];
                Location.distanceBetween(latLng3.latitude,latLng3.longitude,latLng2.latitude,latLng2.longitude,results);
                float res = results[0];
                String dist = String.valueOf(res);


                Toast.makeText(getApplicationContext(),dist,Toast.LENGTH_LONG).show();




            }
        });

    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);
    }







    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;









    }





    /*@Override
    public void onLocationChanged(Location location) {


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }*/

    /*private String getUrl(LatLng origin, LatLng dest, String directionMode) {
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
        *//*if(currentPolyline != null){
            currentPolyline.remove();
        }*//*

        currentPolyline = map.addPolyline((PolylineOptions)values[0]);
    }*/
}
