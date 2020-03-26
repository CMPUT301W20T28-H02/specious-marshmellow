/*
DriverScannerActivity
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Uses camera to scan QR code given by rider
 * @author Hussein Warsame
 */
public class DriverScannerActivity extends BaseScannerActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private String TAG = "message";
    private String riderUsername;
    private String driverUsername;

    /**
     * Called when activity is created
     * Displays camera view
     * @param {@code Bundle}savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(R.layout.activity_driver_scanner);
        Intent i = getIntent();
        riderUsername = i.getStringExtra("riderUsername");
        driverUsername = i.getStringExtra("driverUsername");

        ViewGroup contentFrame = findViewById(R.id.content_frame);
        contentFrame.addView(mScannerView);
    }

    /**
     * Called when activity is resumed
     * sets itself as handler and starts camera
     */
    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    /**
     * called when activity is paused
     * stops camera
     */
    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    /**
     * called after obtaining results
     * decodes image
     * @param {@code Result}rawResult
     */
    @Override
    public void handleResult(Result rawResult) {
        Toast.makeText(this, "Amount = " + rawResult.getText(), Toast.LENGTH_SHORT).show();

        final double amount = Double.valueOf(rawResult.getText());

        Log.i("Hello", "Before request ref");

        new Thread(new Runnable() {
            public void run() {

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                final DocumentReference requestRef = db.collection("requests").document(riderUsername);
                final CollectionReference userRef = db.collection("users");

                requestRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            //TODO java.lang.NullPointerException: Attempt to invoke virtual method 'void com.example.databasedemo.Wallet.deposit(double)' on a null object reference
                            //        at com.example.databasedemo.Driver.getPaid(Driver.java:53)
                            //        at com.example.databasedemo.Request.endRide(Request.java:127)
                            //        at com.example.databasedemo.DriverScannerActivity$1$1.onComplete(DriverScannerActivity.java:104)
                            final Request request = task.getResult().toObject(Request.class);
                            Log.i("Hello", "After getting request");
                            request.endRide(amount);
                            Log.i("Hello", "After getting request");

                            WriteBatch batch = FirebaseFirestore.getInstance().batch();
                            DocumentReference driverRef = userRef.document(driverUsername);
                            batch.set(driverRef, request.getDriver());
                            DocumentReference riderRef = userRef.document(riderUsername);
                            batch.set(riderRef, request.getRider());
                            // Need to write the request back as well to indicate payment is complete
                            batch.set(requestRef, request);

                            Log.i("Hello", "Just before committing the batch request");
                            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //runOnUiThread(new Runnable() {
                                        //public void run() {
                                            Log.i("Hello", "After rider pays driver");
                                            Intent i = new Intent(DriverScannerActivity.this, RiderDriverInitialActivity.class);
                                            i.putExtra("driver", true);
                                            Log.i("Hello", "Driver username:" + driverUsername);
                                            i.putExtra("username", driverUsername);
                                            Log.i("Hello", "Email:" + request.getDriver().getEmail());
                                            i.putExtra("email", request.getDriver().getEmail());
                                            Log.i("Hello", "Driver Scanner Activity: Just before starting RiderDriverInitialActivity");
                                            startActivity(i);
                                        //}
                                   // });
                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }
}
