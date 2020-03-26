/*
DisplayUserInfoActivity
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;

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
        // display the user information
        final String username = user.getUsername();
        usernameText.setText(getString(R.string.current_username, username));
        emailText.setText(getString(R.string.current_email, user.getEmail()));
        phoneText.setText(getString(R.string.current_phone, user.getPhone()));

        // if the user is a driver display their rating and number of ratings
        if (user.getDriver()) {
            String rating = String.valueOf(user.getRating());
            ratingText.setText(getString(R.string.current_rating, rating));
            String numRatings = String.valueOf(user.getNumOfRatings());
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
                    //Toast.makeText(MainActivity.this, R.string.welcome_message, Toast.LENGTH_LONG).show();
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