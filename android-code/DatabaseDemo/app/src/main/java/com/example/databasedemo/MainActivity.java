package com.example.databasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
/*
MainActivity
Version 1
Date March 13 2020
 */

/**
 * Main Activity for app
 * @author Michael Antifaoff, Marcus Blair, Hussein Warsame,
 * Johnas Wong, Sirjan Chawla,Rafaella Graña
 */
public class MainActivity extends AppCompatActivity{

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    private static String TAG = "DISPLAY_USER_ACCOUNT_INFO";

    /**
     * Called when activity is created
     * goes to {@link MainActivity#displayDriverOrRiderScreen(String, String)
     * displayRiderOrDriverScreen} or {@link SignInActivity SignInActivity} depending on
     * whether user is signed in or not
     * @param {@code}savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
            final String username = user.getDisplayName();
            final String email = user.getEmail();

            DocumentReference docRef = db.collection("requests").document(username);

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            Request request = doc.toObject(Request.class);
                            if (request.getRequestStatus())
                            { // If rider has already been matched with a driver
                                // If rider has already confirmed pickup
                                if (request.getRiderConfirmation()&&request.getDriverConfirmation()){
                                    Intent startRiderEndandPay = new Intent(MainActivity.this, RiderEndAndPay.class);
                                    // Activity expects: final String username = i.getStringExtra("username");
                                    startRiderEndandPay.putExtra("username",username);
                                    startActivity(startRiderEndandPay);
                                    finish();
                                } else { // If rider has not yet confirmed pickup
                                    Intent startRiderConfirmPickup = new Intent(MainActivity.this, RiderConfirmPickup.class);
                                    // Activity expects: final String username = i.getStringExtra("username");
                                    startRiderConfirmPickup.putExtra("username", username);
                                    startActivity(startRiderConfirmPickup);
                                    finish();
                                }
                            } else { // If rider has not yet been matched with a driver
                                Intent startCurrentRequest = new Intent(MainActivity.this, currentRequest.class);
                                // Activity expects:    final String username = intent.getStringExtra("username");
                                //                      final String email = intent.getStringExtra("email");
                                startCurrentRequest.putExtra("username", username);
                                startCurrentRequest.putExtra("email", email);
                                startActivity(startCurrentRequest);
                                finish();
                            }
                        }
                        else {
                            new Thread(new Runnable() {
                                public void run() {
                                // Rider does not have an open request, or it is a driver
                                displayDriverOrRiderScreen(username, email);
                                Log.d(TAG, "When we do get here?");
                                }
                            }).start();
                        }
                    }
                }
            });

        } else {
            // No user is signed in
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * display the rider or driver screen. Get the user info from the database and initiate
     * {@link RiderDriverInitialActivity RiderDriverInitialActivity}
     * @param {@code String} username
     * @param {@code String} email
     */
    private void displayDriverOrRiderScreen(String username, String email) {
        final DocumentReference docRef = db.collection("users").document(username);


        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        final User currentUser = document.toObject(User.class);

                        if (currentUser.getDriver()) {

                            // increment Idling resource for UI test
                            EspressoIdlingResource.increment();

                            // If they are a driver, first we want to see if they are part of a request
                            // If they are part of a request, take them to DriverConfirmPickup activity
                            // Otherwise, take them to DriverStartActivity
                            CollectionReference collectionRef = FirebaseFirestore.getInstance().collection("requests");
                            collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                    // decrement Idling resource
                                    EspressoIdlingResource.decrement();

                                    if (task.isSuccessful()){
                                        if (!task.getResult().isEmpty()) {
                                            List<Request> requests = task.getResult().toObjects(Request.class);
                                            FirebaseUser thisUser = FirebaseAuth.getInstance().getCurrentUser();
                                            Log.i("Hello", "This user's display name is " + thisUser.getDisplayName().trim());
                                            Request driverRequest = null;
                                            for (Request request : requests) {
                                                Log.i("Hello", "This request's username name is " + request.getRider().getUsername());
                                                if (request.getDriver() != null) {
                                                    if (request.getDriver().getUsername() != null){
                                                        if (request.getDriver().getUsername().trim().equals(thisUser.getDisplayName().trim())) {
                                                            driverRequest = request;
                                                        }
                                                    }
                                                }
                                            }
                                            if (driverRequest!=null){
                                                Log.i("Hello", "This request's username name is " + driverRequest.getDriver().getUsername().trim()
                                                        + " and the username of the rider is " + driverRequest.getRider().getUsername().trim());
                                                // This driver is part of a request, send them to the confirm activity (and let it flow from there)
                                                Intent q = new Intent(MainActivity.this, DriverConfirmActivity.class);
                                                q.putExtra("riderUsername", driverRequest.getRider().getUsername());
                                                q.putExtra("driverUsername", driverRequest.getDriver().getUsername());
                                                Log.i("Hello", "This request's username name is " + driverRequest.getDriver().getUsername().trim()
                                                        + " and the username of the rider is " + driverRequest.getRider().getUsername().trim());
                                                Log.i("Hello", "Hi");
                                                startActivity(q);
                                                Log.i("Hello", "Hi Hi");
                                                finish();
                                                Log.i("Hello", "Hi Hi Hi");
                                            } else {
                                                Log.i("Hello", "Yo");
                                                // This driver is not a part of any request in the database
                                                Intent intent = new Intent(MainActivity.this, DriverStartActivity.class);
                                                intent.putExtra("driver", true);
                                                intent.putExtra("username", currentUser.getUsername());
                                                intent.putExtra("email", currentUser.getEmail());
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    }

                                }
                            });

                        } else {
                            // This user is a rider with no open requests
                            Intent intent = new Intent(MainActivity.this, RiderStartActivity.class);
                            intent.putExtra("driver", false);
                            intent.putExtra("username", currentUser.getUsername());
                            intent.putExtra("email", currentUser.getEmail());
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }
        });




//        User currentUser = docRef.get().getResult().toObject(User.class);
//        Intent intent = new Intent(getBaseContext(), RiderDriverInitialActivity.class);
//        if (currentUser.getDriver()) {
//            intent.putExtra("driver", true);
//        } else {
//            intent.putExtra("driver", false);
//        }
//        return true;

    }

//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//                        if (!document.getBoolean("driver")) {
//                            //addDriverRating(document.getData().get("rating").toString());
//                            Intent intent = new Intent(getBaseContext(), RiderDriverInitialActivity.class);
//                            intent.putExtra("driver", false);
//                            startActivity(intent);
//                        } else {
//                            Intent intent = new Intent(getBaseContext(), RiderDriverInitialActivity.class);
//                            intent.putExtra("driver", true);
//                            startActivity(intent);
//                        }
//                    } else {
//                        Log.d(TAG, "No such document");
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//                Log.d(TAG, "We get here inside 1, " + riderOrDriver);
//            }
//        });
//        Log.d(TAG, "We get here inside 2, " + riderOrDriver);
//        return riderOrDriver;
//    }

}
