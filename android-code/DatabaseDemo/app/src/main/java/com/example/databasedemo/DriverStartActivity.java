package com.example.databasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class DriverStartActivity extends FragmentActivity implements OnMapReadyCallback {
    GoogleMap map;
    EditText globalBoundsEditText;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static String TAG = "Hello";
    LatLng latLng, latLngDriver;
    ListView requestListView;
    ArrayAdapter<Request> requestArrayAdapter;
    ArrayList<Request> requestArrayList;
    double globalBound = 10000;
    boolean driver;
    ListenerRegistration registration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_initial);

        Intent intent = getIntent();

        final String username = intent.getStringExtra("username");
        final String email = intent.getStringExtra("email");


        globalBoundsEditText = findViewById(R.id.global_bounds_EditText);
        globalBoundsEditText.setText(String.valueOf((int)globalBound));
        requestListView = findViewById(R.id.requestListView);
        requestArrayList = new ArrayList<>();
        requestArrayAdapter = new RequestAdapter(this, requestArrayList);

        requestListView.setAdapter(requestArrayAdapter);

        requestListView.setBackgroundColor(0xFFFFFF);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        globalBoundsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                requestArrayList.clear();
                map.clear();
                if(!charSequence.toString().equals("")) {
                    globalBound = Double.valueOf(globalBoundsEditText.getText().toString());
                    CollectionReference myRef = FirebaseFirestore.getInstance().collection("requests");
                    myRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    final Request request = doc.toObject(Request.class);
                                    if (!request.getRequestStatus()) {

                                        if (driver) {
                                            // set rider start point as latlng
                                            LatLng riderLocation = new LatLng(request.getStartLocation().getLatitude(), request.getStartLocation().getLongitude());
                                            // add markers to map for rider start location
                                            map.addMarker(new MarkerOptions().position(riderLocation).title(request.getRider().getUsername()));
                                        }

                                        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(DriverStartActivity.this, new OnSuccessListener<android.location.Location>() {
                                            @Override
                                            public void onSuccess(android.location.Location location) {
                                                if(location != null){
                                                    double distance = Request.getDistance(new com.example.databasedemo.Location(location.getLatitude(),location.getLongitude()),
                                                            request.getStartLocation());
                                                    Log.i("Hello", "Even Better! Distance: " + distance + "Global Bound: " + globalBound);
                                                    if (distance < globalBound){
                                                        Log.i("Hello", "Is this getting run?");
                                                        addRequest(request);
                                                    } else{
                                                        requestArrayAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }

                                map.setMyLocationEnabled(true);

                                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(DriverStartActivity.this, new OnSuccessListener<android.location.Location>() {
                                    @Override
                                    public void onSuccess(android.location.Location location) {
                                        if (location != null) {
                                            latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

                                        }

                                    }
                                });
                            }
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        requestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.i("Hello", "Rider Driver Initial Activity: inside ListView onItemClickListener");
                final Request request = requestArrayList.get(position);
                Intent i = new Intent(getBaseContext(), DriverRideInfoActivity.class);  // Directions to start location and confirm pickup button
                i.putExtra("riderUsername", request.getRider().getUsername());
                i.putExtra("driverUsername",username);
                i.putExtra("email", email);
                startActivity(i);
                finish();
            }
        });

    }

    public void addRequest(Request request){
        requestArrayList.add(request);
        Collections.sort(requestArrayList, new Comparator<Request>() {
            @Override
            public int compare(Request request, Request request2) {
                return request.getFare() < request2.getFare() ? 1
                        : request.getFare() > request2.getFare() ? -1
                        : 0;
            }
        });
        requestArrayAdapter.notifyDataSetChanged();
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);
    }
    @Override
    public void onMapReady(GoogleMap googleMap){
        map = googleMap;
        requestPermission();

    }
    public void onDestroy(){
        registration.remove();
        super.onDestroy();
    }
    /**
     * update map once permission is granted
     * @param {@code int}requestCode
     * @param {@code String[]}permissions
     * @param {@code int[]}grantResults
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (permissions[0].equals(ACCESS_FINE_LOCATION)) {

            map.setMyLocationEnabled(true);

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(DriverStartActivity.this, new OnSuccessListener<android.location.Location>() {
                @Override
                public void onSuccess(android.location.Location location) {
                    if (location != null) {
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    }

                }
            });
            fusedLocationProviderClient.getLastLocation().addOnFailureListener(DriverStartActivity.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DriverStartActivity.this, "Without your location, we cannot show you a list of rides near you. Please enable location services and try again.", Toast.LENGTH_LONG).show();

                }
            });
        } else {
            Toast.makeText(DriverStartActivity.this,
                    "Without your location, we cannot show you a list of rides near you. Please enable location services and try again.", Toast.LENGTH_LONG);
        }


        CollectionReference myRef = FirebaseFirestore.getInstance().collection("requests");

        registration = myRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                requestArrayList.clear();
                map.clear();
                for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
                    final Request request = doc.toObject(Request.class);
                    if (!request.getRequestStatus()) {

                        if (driver) {
                            // set rider start point as latlng
                            LatLng riderLocation = new LatLng(request.getStartLocation().getLatitude(), request.getStartLocation().getLongitude());
                            // add markers to map for rider start location
                            map.addMarker(new MarkerOptions().position(riderLocation).title(request.getRider().getUsername()));
                        }

                        fusedLocationProviderClient.getLastLocation().addOnSuccessListener( DriverStartActivity.this, new OnSuccessListener<android.location.Location>() {
                            @Override
                            public void onSuccess(android.location.Location location) {
                                if(location != null){
                                    double distance = Request.getDistance(new com.example.databasedemo.Location(location.getLatitude(),location.getLongitude()),
                                            request.getStartLocation());
                                    if (distance < globalBound){
                                        addRequest(request);
                                    }
                                }
                            }
                        });
                    }
                }

                map.setMyLocationEnabled(true);
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(DriverStartActivity.this, new OnSuccessListener<android.location.Location>() {
                    @Override
                    public void onSuccess(android.location.Location location) {
                        if (location != null) {
                            latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

                        }

                    }
                });
            }
        });
    }
}
