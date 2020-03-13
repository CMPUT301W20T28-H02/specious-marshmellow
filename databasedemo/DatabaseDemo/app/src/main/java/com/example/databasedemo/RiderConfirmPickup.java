package com.example.databasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RiderConfirmPickup extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map;
    Button riderConfirmPickupButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_confirm_pickup);
        riderConfirmPickupButton = findViewById(R.id.rider_confirm_pickup_button);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.rider_pickup_map);
        mapFragment.getMapAsync(this);

        Intent i = getIntent();
        final String username = i.getStringExtra("username");

        riderConfirmPickupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DocumentReference docRef = FirebaseFirestore.getInstance().collection("requests").document(username);

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Request request = task.getResult().toObject(Request.class);
                            request.riderConfirmation();
                            docRef.set(request);
                            Intent i = new Intent(getBaseContext(), RiderEndAndPay.class);
                            i.putExtra("username", username);
                            startActivity(i);
                        }
                    }
                });
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        /*LatLng UofAQuad = new LatLng( 53.526891, -113.525914 ); // putting long lat of a pin
        map.addMarker( new MarkerOptions().position(UofAQuad).title("U of A Quad") );  // add a pin
        map.moveCamera(CameraUpdateFactory.newLatLng( UofAQuad ) ); // center camera around the pin*/
    }
}
