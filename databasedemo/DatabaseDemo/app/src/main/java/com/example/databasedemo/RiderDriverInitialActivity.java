package com.example.databasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class RiderDriverInitialActivity extends FragmentActivity implements OnMapReadyCallback {


    GoogleMap map;
    Button makeRequestButton;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static String TAG = "Hello";
    LatLng latLng;
    ListView requestListView;
    ArrayAdapter<Request> requestArrayAdapter;
    ArrayList<Request> requestArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        boolean driver = intent.getBooleanExtra("driver", true);
        final String username = intent.getStringExtra("username");

        requestPermission();

        if (driver) {
            Log.i(TAG, "We are here");
            setContentView(R.layout.driver_initial);
        } else {
            setContentView(R.layout.rider_initial);
        }

        makeRequestButton = findViewById(R.id.make_request_button);
        requestListView = findViewById(R.id.requestListView);
        requestArrayList = new ArrayList<>();
        requestArrayAdapter = new RequestAdapter(this, requestArrayList);

        requestListView.setAdapter(requestArrayAdapter);

        requestListView.setBackgroundColor(0xFFFFFF);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        requestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final Request request = requestArrayList.get(position);
                // Need to get the current driver, then call request.isAcceptedBy(driver)
                DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(username);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Driver driver = task.getResult().toObject(Driver.class);
                            request.isAcceptedBy(driver);
                            DocumentReference docRef = FirebaseFirestore.getInstance().collection("requests").document(request.getRider().getUsername());
                            docRef.set(request);
                            Intent i = new Intent(getBaseContext(), DriverConfirmActivity.class);  // Directions to start location and confirm pickup button
                            i.putExtra("riderUsername", request.getRider().getUsername());
                            i.putExtra("driverUsername",username);
                            startActivity(i);
                        }
                    }
                });
            }
        });

        makeRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(RiderDriverInitialActivity.this, "Make Request", 10);
                Log.i(TAG, "Request made");
                Intent intent = new Intent(RiderDriverInitialActivity.this, RiderNewRequestActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });


        CollectionReference myRef = FirebaseFirestore.getInstance().collection("requests");

        myRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot document : task.getResult()){
//                        Double itemName = document.getDouble("fare");
//                        endLatTextView.setText(document.toString());
                        Request request = document.toObject(Request.class);
                        if (!request.getRequestStatus()) {
                            addRequest(request);
                        }
                    }
                }
            }
        });


        myRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                requestArrayList.clear();
                for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
                    requestArrayList.add(doc.toObject(Request.class));
                }
                requestArrayAdapter.notifyDataSetChanged();
            }
        });

        // Code should work without this code -- give it three days from March 11
        // If no more complaints of bugs, then delete this code on Friday, March 13 before uploading
        /*if(ActivityCompat.checkSelfPermission(RiderDriverInitialActivity.this, ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "We are without permissions");
            return;
        }
        Log.i(TAG, "We are with permissions");

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
        });*/

    }

    public void addRequest(Request request){
        requestArrayList.add(request);
        requestArrayAdapter.notifyDataSetChanged();
    }

    private void requestPermission(){
        Log.i(TAG, "We are before permissions");
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);
        Log.i(TAG, "We are after permissions");
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        /*LatLng UofAQuad = new LatLng( 53.526891, -113.525914 ); // putting long lat of a pin
        map.addMarker( new MarkerOptions().position(UofAQuad).title("U of A Quad") );  // add a pin
        map.moveCamera(CameraUpdateFactory.newLatLng( UofAQuad ) ); // center camera around the pin*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (permissions[0].equals(ACCESS_FINE_LOCATION)) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(RiderDriverInitialActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        MarkerOptions p3 = new MarkerOptions().position(latLng);
                        map.addMarker(p3);
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                    }

                }
            });
        }

    }
}

