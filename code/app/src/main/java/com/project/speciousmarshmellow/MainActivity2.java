package com.project.speciousmarshmellow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SearchView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.project.speciousmarshmellow.DirectionHelpers.FetchURL;
import com.project.speciousmarshmellow.DirectionHelpers.TaskLoadedCallback;

import java.io.IOException;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback,TaskLoadedCallback {

    private GoogleMap map;
    SupportMapFragment mapFragment;
    SearchView searchView,searchView2;
    Button btnGetDirection;
    Polyline currentPolyline;
    LatLng latLng2,latLng;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnGetDirection = findViewById(R.id.btnGetDirection);
        searchView = findViewById(R.id.sv_location);
        searchView2 = findViewById(R.id.sv2_location);
        mapFragment = ( SupportMapFragment ) getSupportFragmentManager()
                .findFragmentById( R.id.map);
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
                    MarkerOptions p1 = new MarkerOptions().position(latLng2).title(location);
                    map.addMarker(p1);
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));


                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
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
                    MarkerOptions p2 = new MarkerOptions().position(latLng).title(location2);
                    map.addMarker(p2);
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng2,10));



                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
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
        /*if(currentPolyline != null){
            currentPolyline.remove();
        }*/

        currentPolyline = map.addPolyline((PolylineOptions)values[0]);
    }
}
