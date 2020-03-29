/*
moneyScreen
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * Displays available money and allows user to add more
 * @author Sirjan Chawla
 */
public class moneyScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Button btn5, btn25, btn50, makeReq;
    TextView bal, bal_setter,usrName, usrEmail;
    ListenerRegistration registration;
    ImageView Profile;
    boolean hasProfilePicture;
    DatabaseReference reff;
    FirebaseAuth mAuth;
    String called;

    /**
     *  Shows current balance and three buttons that allow user to add more money to their wallet
     *  balance is taken from database as well as updated when user adds more
     * @param {@code Bundle}savedInstanceState Information passed from the caller activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_screen);

        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");
        btn5 = findViewById(R.id.add_5);
        bal = findViewById(R.id.balance);
        bal_setter = findViewById(R.id.balance_set);
        mAuth = FirebaseAuth.getInstance();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        NavigationView navi = findViewById(R.id.nav_view);
        View headerview = navi.getHeaderView(0);
        navi.setNavigationItemSelectedListener(this);
        usrName = headerview.findViewById(R.id.usrNameText);
        usrEmail = headerview.findViewById(R.id.usrEmailText);
        Profile = headerview.findViewById(R.id.profilepic);
        usrName.setText(username);
        usrEmail.setText(email);


        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(username);
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
                                .into(Profile);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


        });
        Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), TakeProfilePicture.class);
                startActivity(intent);

            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Rider rider = task.getResult().toObject(Rider.class);
                            Wallet wallet = rider.getWallet();
                            wallet.deposit(5.0);
                            rider.setWallet(wallet);
                            docRef.set(rider);
                        }
                    }
                });
            }
        });
        btn25 = findViewById(R.id.add_25);
        btn25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Rider rider = task.getResult().toObject(Rider.class);
                            Wallet wallet = rider.getWallet();
                            wallet.deposit(25.0);
                            rider.setWallet(wallet);
                            docRef.set(rider);
                        }
                    }
                });
            }
        });
        btn50 = findViewById(R.id.add_50);
        btn50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Rider rider = task.getResult().toObject(Rider.class);
                            Wallet wallet = rider.getWallet();
                            wallet.deposit(50.0);
                            rider.setWallet(wallet);
                            docRef.set(rider);
                        }
                    }
                });
            }
        });
        makeReq = findViewById(R.id.back_home);
        makeReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*if (called.equals(currentRequest.class.toString())&& called != null) {
                    Intent intent = new Intent(getBaseContext(), currentRequest.class);
                    intent.putExtra("username", username);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                if (called.equals(RiderNewRequestActivity.class.toString())&& called != null) {
                    Intent intent = new Intent(getBaseContext(), RiderNewRequestActivity.class);
                    intent.putExtra("username", username);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                if (called.equals(RiderConfirmPickup.class.toString())&& called != null) {
                    Intent intent = new Intent(getBaseContext(), RiderConfirmPickup.class);
                    intent.putExtra("username", username);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }*/
                finish();
            }
        });
        registration = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Rider rider = documentSnapshot.toObject(Rider.class);
                Wallet wallet = rider.getWallet();
                bal_setter.setText(String.valueOf(wallet.getBalance()));
            }
        });

    }








    @Override
    public void onDestroy(){
        registration.remove();
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        switch (menuItem.getItemId()) {
            case R.id.nav_money:
                DynamicToast.make(moneyScreen.this, "Action restricted, Already in Money Screen", Color.parseColor("#E38249"), Color.parseColor("#000000"), Toast.LENGTH_LONG).show();
                break;

            case R.id.contact_info:
                Intent intent1 = new Intent(getBaseContext(),EditContactInformationActivity.class);
                intent1.putExtra("username", username);
                startActivity(intent1);

                break;


        }

        return false;
    }
}

