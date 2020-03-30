/*
DisplayUserInfoActivity
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

/**
 * Displays user information
 * @author Michael Antifaoff, Marcus Blair
 */
public class DisplayUserInfoActivity extends AppCompatActivity {
    String TAG = "DISPLAY_USER_ACCOUNT_INFO";
    TextView usernameText;
    TextView emailText;
    TextView phoneText;
    TextView numRatingsText;
    TextView ratingText;
    ImageView profilePictureLargeImage;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    /**
     * Called when activity is created
     * Displays user info, checks if username is the same as current user
     * @param {@code Bundle}savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // display user info
        // takes username through intent
        // checks if the username is the same as the current user
        // if so adds additional functionality
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user_info);

        profilePictureLargeImage = findViewById(R.id.profilePictureLargeImage);
        usernameText = findViewById(R.id.username_text);
        emailText = findViewById(R.id.email_text);
        phoneText = findViewById(R.id.phone_text);
        ratingText = findViewById(R.id.rating_text);
        numRatingsText = findViewById(R.id.num_ratings_text);

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        Intent i = getIntent();
        final String username = i.getStringExtra("username");

        // get the user object from the database and determine if it is a rider or driver
        final DocumentReference docRef = db.collection("users").document(username);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.getBoolean("driver")) {
                        Driver user = documentSnapshot.toObject(Driver.class);
                        displayUser(user);
                    } else {
                        User user = task.getResult().toObject(User.class);
                        displayUser(user);
                    }
                }
            }
        });

    }

    /**
     * Display user information depending on whether they are a driver or rider
     * allows user to sign out
     * @param {@code User}user
     */
    private void displayUser(final User user) {

        // Get the user's username
        final String username = user.getUsername();

        // Display profile picture
        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(username);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                boolean hasProfilePicture = false;
                DatabaseReference reff;
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
                        Picasso.get().load(url).into(profilePictureLargeImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        // display the user information
        usernameText.setText(getString(R.string.current_username, username));
        emailText.setText(getString(R.string.current_email, user.getEmail()));
        phoneText.setText(getString(R.string.current_phone, user.getPhone()));
        phoneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "tel:" + user.getPhone().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });

        // if the user is a driver display their rating and number of ratings
        if (user.getDriver()) {
            DecimalFormat numberFormat = new DecimalFormat(".00");
            String rating = String.valueOf(numberFormat.format(user.getRating()));
            ratingText.setText(getString(R.string.current_rating, rating));
            String numRatings = String.valueOf((int)user.getNumOfRatings());
            numRatingsText.setText(getString(R.string.current_number_of_ratings, numRatings));
        }

        // if this is the current user allow them to edit their account of sign out
        // add these buttons programmatically
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser.getDisplayName().equals(user.getUsername())) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.button_linear_layout);

            //set the properties for button
            Button editAccount = new Button(this);
            Button signOut = new Button(this);

            // edit account button
            editAccount.setText(getString(R.string.edit_info_button));
            editAccount.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            editAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // DynamicToast.make(DisplayUserInfoActivity.this, R.string.welcome_message, Color.parseColor("#E38249"), Color.parseColor("#000000"), Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(DisplayUserInfoActivity.this, EditContactInformationActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    finish();
                }
            });

            // sign out button
            signOut.setText(getString(R.string.sign_out_button));
            signOut.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,  ViewGroup.LayoutParams.WRAP_CONTENT));
            signOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAuth.signOut();
                    finish();
                    overridePendingTransition(0, 0);
                    Intent intent = new Intent(DisplayUserInfoActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0);
                }
            });

            // Add Buttons to LinearLayout
            if (layout != null) {
                layout.addView(editAccount);
                layout.addView(signOut);
            }

        }
    }
}