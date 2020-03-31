package com.example.databasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class RiderStartActivity extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    GoogleMap map;
    Button makeRequestButton;
    EditText globalBoundsEditText;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static String TAG = "Hello";
    LatLng latLng, latLngDriver;
    TextView usrNameText,usrEmailText;
    ImageView profile;
    DatabaseReference reff;
    FirebaseAuth mAuth;
    boolean hasProfilePicture;
    double globalBound = 10000;
    ListenerRegistration registration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_initial);
        makeRequestButton = findViewById(R.id.make_request_button);
        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        //NavigationView navi = findViewById(R.id.nav_view);
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
                        String url = dataSnapshot.child("imageUrl").getValue().toString();
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
                Intent intent = new Intent(getBaseContext(), TakeProfilePicture.class);
                startActivity(intent);

            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if(ActivityCompat.checkSelfPermission(RiderStartActivity.this, ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(RiderStartActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    MarkerOptions p3 = new MarkerOptions().position(latLng);
                    map.setMyLocationEnabled(true);
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                }
            }
        });

        makeRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DynamicToast.make(RiderStartActivity.this, "Make Request", Color.parseColor("#E38249"), Color.parseColor("#000000"), Toast.LENGTH_LONG).show();

                Log.i("Hello", "Rider Driver Initial Activity: inside make request button onItemClickListener");
                Intent intent = new Intent(getBaseContext(), RiderNewRequestActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("email", email );
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
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

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(RiderStartActivity.this, new OnSuccessListener<android.location.Location>() {
                @Override
                public void onSuccess(android.location.Location location) {
                    if (location != null) {
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                    }

                }
            });
            fusedLocationProviderClient.getLastLocation().addOnFailureListener(RiderStartActivity.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    DynamicToast.make(RiderStartActivity.this, getString(R.string.no_location_permissions),
                            Color.parseColor("#E38249"), Color.parseColor("#000000"), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            DynamicToast.make(RiderStartActivity.this, getString(R.string.no_location_permissions),
                    Color.parseColor("#E38249"), Color.parseColor("#000000"), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        switch (menuItem.getItemId()) {
            case R.id.nav_money:
                Intent intent = new Intent(getBaseContext(), moneyScreen.class);
                intent.putExtra("username", username);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                break;
            case R.id.sign_out_tab:
                mAuth.signOut();
                finish();
                Intent intent_2 = new Intent(getBaseContext(), SignInActivity.class);
                startActivity(intent_2);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
                break;
            case R.id.contact_info:
                Intent intent1 = new Intent(getBaseContext(),EditContactInformationActivity.class);
                intent1.putExtra("username", username);
                startActivity(intent1);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                break;
        }
        return false;
    }
}
