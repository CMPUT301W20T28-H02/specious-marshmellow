/*
currentRequest
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.squareup.picasso.Picasso;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Displays the current request while waiting for a match and allows user to cancel request
 * @author Johnas Wong, Michael Antifaoff, Sirjan Chawla
 */
public class currentRequest extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {


     GoogleMap map;
     LatLng latLng, startPoint, endPoint;
     FusedLocationProviderClient fusedLocationProviderClient;
     Button can_Request;
     TextView usrNameText,usrEmailText;
     ImageView profile;
     FirebaseFirestore db;
     CollectionReference myRef = FirebaseFirestore.getInstance().collection("requests");
     String username = new String();
     String email;
     ListenerRegistration registration;
     DatabaseReference reff;
     String url;
     boolean hasProfilePicture;
     DrawerLayout drawerLayout;


     NotificationCompat.Builder riderMatchedWithDriverNotification;
     private static final int uniqueID = 22;

    /**
     * Called when the activity is created
     * Displays the user's current location on the map. In the future it will also display
     * all the nearby drivers' location on the map
     * @param {@Code Bundle}savedInstanceState data sent from caller activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_request);

        can_Request = findViewById(R.id.can_request);
        /*Finding the Navigation menu*/
        NavigationView navi = findViewById(R.id.nav_view);
        View headerview = navi.getHeaderView(0);
        navi.setNavigationItemSelectedListener(this);

        /*Setting username, user email, profile pic in navigation menu*/
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        email = intent.getStringExtra("email");
        usrNameText = headerview.findViewById(R.id.usrNameText);
        usrEmailText=headerview.findViewById(R.id.usrEmailText);
        usrNameText.setText(username);
        usrEmailText.setText(email);
        profile = headerview.findViewById(R.id.profilepic);

        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(username);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Rider rider = task.getResult().toObject(Rider.class);
                    hasProfilePicture = rider.getHasProfilePicture();
                }

                if( hasProfilePicture )
                {
                    reff = FirebaseDatabase.getInstance().getReference().child("Profile pictures").child(username);
                } else {
                    reff = FirebaseDatabase.getInstance().getReference().child("Profile pictures").child("Will_be_username");
                }
                reff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        url = dataSnapshot.child("imageUrl").getValue().toString();


                        Log.d("Firebase", url);
                        Picasso.get()
                                .load( url )
                                .into( profile );


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


        });




        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(currentRequest.this, TakeProfilePicture.class);
                startActivity(intent);

            }
        });

        can_Request = findViewById(R.id.can_request);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        requestPermission();
        if(ActivityCompat.checkSelfPermission(currentRequest.this, ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(currentRequest.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    MarkerOptions p3 = new MarkerOptions().position(latLng);
                    map.addMarker(p3.title("Current Location"));
                }
            }
        });
        can_Request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CollectionReference innerRef = FirebaseFirestore.getInstance().collection("requests");
                innerRef.document(username).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Log.i(TAG, "Data deletion successful");
                                Intent intent = new Intent(currentRequest.this, RiderStartActivity.class);
                                intent.putExtra("driver", false);
                                intent.putExtra("username", username);
                                intent.putExtra("email", email);

                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Log.i(TAG, "Data deletion unsuccessful");
                            }
                        });

            }
        });

        DocumentReference docRef1 = FirebaseFirestore.getInstance().collection("requests").document(username);

        registration = docRef1.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    Request request = documentSnapshot.toObject(Request.class);
                    if(request.getRequestStatus() == true){
                        // Change this line so that it switches to Rider on a ride activity
                        Log.d("Database", "here");
                        sendNotification();

                        Intent i = new Intent(currentRequest.this,RiderConfirmPickup.class);
                        i.putExtra("username", username);
                        i.putExtra("email", email);
                        startActivity(i);
                        finish();
                    }

                }
            }
        });

    }

    @Override
    public void onDestroy(){
        registration.remove();
        super.onDestroy();
    }

    public void sendNotification() {

        //Get an instance of NotificationManager//

//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.mipmap.marshmellow)
//                        .setContentTitle("My notification")
//                        .setContentText("Hello World!");
//
//        mBuilder.setAutoCancel(true);

        //Create an intent that’ll fire when the user taps the notification

//        Intent i = new Intent(currentRequest.this,RiderConfirmPickup.class);
//        i.putExtra("username", username);
//        i.putExtra("email", email);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
//
//        mBuilder.setContentIntent(pendingIntent);
//
//        //mBuilder.setSmallIcon(R.drawable.notification_icon);
//        mBuilder.setContentTitle("My notification");
//        mBuilder.setContentText("Hello World!");

//        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        mNotificationManager.notify(001, mBuilder.build());


        NotificationManager mNotificationManager;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "notify_001");
        Intent i = new Intent(this,RiderConfirmPickup.class);
        i.putExtra("username", username);
        i.putExtra("email", email);
        //startActivity(i);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.setBigContentTitle("Your Marshmellow is on its way!");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.marshmellow);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), (R.mipmap.marshmellow)));
        mBuilder.setContentTitle("Your Marshmellow is on its way!");    // Shows initially on the pop up
        mBuilder.setContentText("Your driver has selected you and is on their way to you!");    // Body text inside Notification Center
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);
        mBuilder.setAutoCancel(true);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(channelId, "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());
    }


    /**
     * gets permission to use location
     */
    private void requestPermission() {
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);
    }

    /**
     * when map is loaded, assign it to the map attribute
     * @param {@code GoogleMap}googleMap Map Object
     */
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("requests").document(username);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    // for two decimal places
                    //final DecimalFormat numberFormat = new DecimalFormat("#.00");

                    // get the fare and the start and end locations from the database
                    final Request request = task.getResult().toObject(Request.class);
                    //String fare = String.valueOf(numberFormat.format(request.getFare()));


                    com.example.databasedemo.Location startLocation = request.getStartLocation();
                    com.example.databasedemo.Location endLocation = request.getEndLocation();

                    // get the distance and convert to string
                    //double doubleDistance = Request.getDistance(startLocation, endLocation);
                    //String distance = String.valueOf(numberFormat.format(doubleDistance));

                    // Get distance to rider from driver's current location
//                    fusedLocationProviderClient.getLastLocation().addOnSuccessListener(DriverRideInfoActivity.this, new OnSuccessListener<android.location.Location>() {
//                        @Override
//                        public void onSuccess(android.location.Location location) {
//                            if(location != null){
//                                double doubleDistanceToRider = Request.getDistance(new com.example.databasedemo.Location(location.getLatitude(),location.getLongitude()),
//                                        request.getStartLocation());
//                                String distanceToRider = String.valueOf(numberFormat.format(doubleDistanceToRider));
//                                distanceToRiderTextView.setText(getString(R.string.driver_to_rider_distance, distanceToRider));
//                            }
//                        }
//                    });

                    // display the fare and distance
                    // rideFareTextView.setText(getString(R.string.driver_confirm_ride_fare, fare));
                    // rideDistanceTextView.setText(getString(R.string.driver_confirm_ride_distance, distance));

                    // set start and end points as latlng
                    startPoint = new LatLng(startLocation.getLatitude(), startLocation.getLongitude());
                    endPoint = new LatLng(endLocation.getLatitude(), endLocation.getLongitude());

                    // add markers to map for start and end points
                    map.addMarker(new MarkerOptions().position(startPoint).title("Start Location"));
                    map.addMarker(new MarkerOptions().position(endPoint).title("End Location"));


                    // move map to show the start and end points
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    // set builder with start and end locations
                    builder.include(startPoint);
                    builder.include(endPoint);
                    LatLngBounds bounds = builder.build();
                    // construct a cameraUpdate with a buffer of 200
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 200);
                    // move the camera
                    map.animateCamera(cameraUpdate);
                }
            }
        });

    }

    /**
     * Shows items in the sidebar
     * @param {@code MenuItem}menuItem Item in the menu
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        switch (menuItem.getItemId()) {
            case R.id.nav_money:
                Intent intent = new Intent(currentRequest.this, moneyScreen.class);
                intent.putExtra("username", username);
                intent.putExtra("activity",currentRequest.class.toString());
                startActivity(intent);
                break;
            case R.id.sign_out_tab:
                /*mAuth.signOut();
                finish();
                Intent intent_2 = new Intent(getBaseContext(), SignInActivity.class);

                startActivity(intent_2);*/
                Toast.makeText(this, "Action restricted, cancel your request and try again", Toast.LENGTH_LONG).show();

                break;
            case R.id.contact_info:
                Intent intent1 = new Intent(currentRequest.this,EditContactInformationActivity.class);
                intent1.putExtra("username", username);
                startActivity(intent1);

                break;


        }

        return false;
    }
}
