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


public class DisplayUserInfoActivity extends AppCompatActivity {
    String TAG = "DISPLAY_USER_ACCOUNT_INFO";
    TextView usernameText;
    TextView emailText;
    TextView phoneText;
    TextView numRatingsText;
    TextView ratingText;
    //Button editAccount;
    //Button signOut;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user_info);

        usernameText = findViewById(R.id.username_text);
        emailText = findViewById(R.id.email_text);
        phoneText = findViewById(R.id.phone_text);
        ratingText = findViewById(R.id.rating_text);
        numRatingsText = findViewById(R.id.num_ratings_text);


        //editAccount = findViewById(R.id.edit_information);
        //signOut = findViewById(R.id.sign_out);

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        Intent i = getIntent();
        final String username = i.getStringExtra("username");

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

    private void displayUser(User user) {
        usernameText.setText(user.getUsername());
        emailText.setText(user.getEmail());
        phoneText.setText(user.getPhone());
        if (user.getDriver()) {
            String rating = String.valueOf(user.getRating());
            ratingText.setText(rating);
            String numRatings = String.valueOf(user.getNumOfRatings());
            numRatingsText.setText(numRatings);

        }
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser.getDisplayName().equals(user.getUsername())) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.button_linear_layout);

            //set the properties for button
            Button editAccount = new Button(this);
            Button signOut = new Button(this);

            editAccount.setText("Edit Account");
            editAccount.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //editAccount.setGravity(Gravity.CENTER_HORIZONTAL);
            editAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(MainActivity.this, R.string.welcome_message, Toast.LENGTH_LONG).show();
                }
            });

            signOut.setText("Sign Out");
            signOut.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,  ViewGroup.LayoutParams.WRAP_CONTENT));
            //signOut.setGravity(Gravity.CENTER_HORIZONTAL);
            signOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAuth.signOut();
                    finish();
                    overridePendingTransition(0, 0);
                    Intent intent = new Intent(getBaseContext(), SignInActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });

            // Add Button to LinearLayout
            if (layout != null) {
                layout.addView(editAccount);
                layout.addView(signOut);
            }

        }
    }
}

//        FirebaseUser user = mAuth.getCurrentUser();
//
//        if (user != null) {
//            // User is signed in
//            String username = user.getDisplayName();
//            String email = user.getEmail();
//
//
//
//            displayUser(username, email);
//        } else {
//            // No user is signed in
//            Intent intent = new Intent(getBaseContext(), SignInActivity.class);
//            startActivity(intent);
//        }
//
//        editAccount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getBaseContext(), EditContactInformation.class));
//            }
//        });
//
//        signOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mAuth.signOut();
//                finish();
//                overridePendingTransition(0, 0);
//                startActivity(getIntent());
//                overridePendingTransition(0, 0);
//            }
//        });
//
//    }
//
//    private void displayDriverOrRiderScreen(String username, String email){
//        final DocumentReference docRef = db.collection("users").document(username);
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//                        if (!document.getBoolean("driver")) {
//                            //addDriverRating(document.getData().get("rating").toString());
//                            LinearLayout linearLayout = findViewById(R.id.main_layout);
//                            linearLayout.removeView((View) ratingText.getParent());
//                        } else {
//                            String rating = document.getData().get("rating").toString();
//                            ratingText.setText(getString(R.string.show_rating, rating));
//                        }
//                    } else {
//                        Log.d(TAG, "No such document");
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            }
//        });
//    }
//
//
//    private void displayUser(String username, String email) {
//        usernameText.setText(getString(R.string.show_username, username));
//        emailText.setText(getString(R.string.show_email, email));
//        final DocumentReference docRef = db.collection("users").document(username);
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//                        if (document.getData().get("phone") != null) {
//                            String phone = document.getData().get("phone").toString();
//                            phoneText.setText(getString(R.string.show_phone, phone));
//                        }
//                        if (document.getData().get("address") != null) {
//                            String address = document.getData().get("address").toString();
//                            addressText.setText(getString(R.string.show_address, address));
//                        }
//                        if (!document.getBoolean("driver")) {
//                            //addDriverRating(document.getData().get("rating").toString());
//                            LinearLayout linearLayout = findViewById(R.id.main_layout);
//                            linearLayout.removeView((View) ratingText.getParent());
//                        } else {
//                            String rating = document.getData().get("rating").toString();
//                            ratingText.setText(getString(R.string.show_rating, rating));
//                        }
//                    } else {
//                        Log.d(TAG, "No such document");
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            }
//        });
//
//    }
//}
