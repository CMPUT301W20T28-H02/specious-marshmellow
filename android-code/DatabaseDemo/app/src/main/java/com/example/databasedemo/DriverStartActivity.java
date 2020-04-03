package com.example.databasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * The activity that the Driver begins with. It has a GoogleMap view and a Requests ListView.
 * @author Michael Antifaoff, Sirjan Chawla, Johnas Wong, Rafaella Grana, Hussein Warsame, Marcus Blair
 */
public class DriverStartActivity extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    GoogleMap map;
    EditText globalBoundsEditText;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static String TAG = "Hello";
    LatLng latLng, latLngDriver;
    ListView requestListView;
    ArrayAdapter<Request> requestArrayAdapter;
    ArrayList<Request> requestArrayList;
    TextView usrNameText,usrEmailText;
    CircularImageView profile;
    boolean hasProfilePicture;
    DatabaseReference reff;
    FirebaseAuth mAuth;
    double globalBound = 10000;
    ListenerRegistration registration;
    /**
     * Called when activity is created
     * Displays intitial activity for driver, displays requests based on distance and lets them choose one.
     * @param {@code Bundle}savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_initial);
        Log.i("Hello", "We are inside OnCreate of DriverStartActivity");
        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");
        final String email = intent.getStringExtra("email");
        NavigationView navi = findViewById(R.id.nav_view);
        View headerview = navi.getHeaderView(0);
        navi.setNavigationItemSelectedListener(this);
        usrNameText = headerview.findViewById(R.id.usrNameText);
        usrEmailText=headerview.findViewById(R.id.usrEmailText);
        usrNameText.setText(username);
        usrEmailText.setText(email);
        profile = headerview.findViewById(R.id.profilepic);

        /* null check is only to enable UI testing, since any intent extras will be null
          when testing an activity in isolation.
         */
        if (username != null) {

            DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(username);

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Rider rider = task.getResult().toObject(Rider.class);
                        hasProfilePicture = rider.getHasProfilePicture();
                    }

                    if (hasProfilePicture) {
                        reff = FirebaseDatabase.getInstance().getReference().child("Profile pictures").child(username);
                    } else {
                        reff = FirebaseDatabase.getInstance().getReference().child("Profile pictures").child("Will_be_username");
                    }
                    reff.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String url = dataSnapshot.child("imageUrl").getValue().toString();


                            Log.d("Firebase", url);
                            Picasso.get()
                                    .load(url)
                                    .into(profile);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            });
        }

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), TakeProfilePicture.class);
                startActivity(intent);

            }
        });


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

                                        // set rider start point as latlng
                                        LatLng riderLocation = new LatLng(request.getStartLocation().getLatitude(), request.getStartLocation().getLongitude());
                                        // add markers to map for rider start location
                                        map.addMarker(new MarkerOptions().position(riderLocation).title(request.getRider().getUsername()));

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
                } else {
                    requestArrayAdapter.notifyDataSetChanged();

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

    /**
     * add request to request array sorted by distance
     * @param {@code Request}request
     */
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
    /**
     * requests permission to access location services
     */
    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);
    }

    /**
     * when map is loaded, assign it to the map attribute
     * @param {@code GoogleMap}googleMap Map Object
     */
    @Override
    public void onMapReady(GoogleMap googleMap){
        map = googleMap;
        requestPermission();

    }

    /**
     * Removes listener registration
     *
     */

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
                    DynamicToast.make(DriverStartActivity.this, getString(R.string.no_location_permissions),
                            Color.parseColor("#E38249"), Color.parseColor("#000000"), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            DynamicToast.make(DriverStartActivity.this, getString(R.string.no_location_permissions),
                    Color.parseColor("#E38249"), Color.parseColor("#000000"), Toast.LENGTH_LONG).show();
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

                        // set rider start point as latlng
                        LatLng riderLocation = new LatLng(request.getStartLocation().getLatitude(), request.getStartLocation().getLongitude());
                        // add markers to map for rider start location
                        map.addMarker(new MarkerOptions().position(riderLocation).title(request.getRider().getUsername()));

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

    /**
     * Shows items in the sidebar
     * @param {@code MenuItem}menuItem Item in the menu
     * @return {@code Boolean}
     */

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        String driverUsername = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        final DocumentReference docRef_2 = FirebaseFirestore.getInstance().collection("users").document(driverUsername);

        switch (menuItem.getItemId()) {
            case R.id.nav_money:
                docRef_2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DecimalFormat numberFormat = new DecimalFormat(".00");
                            Driver driver = task.getResult().toObject(Driver.class);
                            Wallet wallet = driver.getWallet();
                            DynamicToast.make(getBaseContext(), getString(R.string.your_balance,
                                    String.valueOf(numberFormat.format(wallet.getBalance()))), Color.parseColor("#E38249"),
                                    Color.parseColor("#000000"), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;
            case R.id.sign_out_tab:
                mAuth.signOut();
                finish();
                Intent intent_2 = new Intent(getBaseContext(), SignInActivity.class);
                startActivity(intent_2);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                break;
            case R.id.contact_info:
                Intent intent1 = new Intent(getBaseContext(),EditContactInformationActivity.class);
                intent1.putExtra("username", driverUsername);
                startActivity(intent1);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                break;


        }
        return false;
    }
}
