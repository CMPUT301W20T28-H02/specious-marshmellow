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

        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //mScannerView.resumeCameraPreview(DriverScannerActivity.this);
            }
        }, 10000);

        final double amount = Double.valueOf(rawResult.getText());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference requestRef = db.collection("requests").document(riderUsername);
        final CollectionReference userRef = db.collection("users");

        Log.i("Hello", "Before request ref");

        requestRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    final Request request = task.getResult().toObject(Request.class);
                    Log.i("Hello", "After getting request");
                    request.endRide(amount);
                    Rider rider = request.getRider();
                    rider.setPaymentComplete(true);
                    request.setRider(rider);
                    final Driver driver = request.getDriver();
                    driver.setPaymentComplete(true);
                    request.setDriver(driver);
                    Log.i("Hello", "After setting payment (objects)");
                    userRef.document(riderUsername).set(request.getRider()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i("Hello", "After setting payment (rider database)");
                            userRef.document(driverUsername).set(request.getDriver()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i("Hello", "After setting payment (driver database)");
                                    Intent i = new Intent(getBaseContext(), RiderDriverInitialActivity.class);
                                    i.putExtra("driver",true);
                                    Log.i("Hello", driverUsername);
                                    i.putExtra("username", driverUsername);
                                    Log.i("Hello", request.getDriver().getEmail());
                                    i.putExtra("email", request.getDriver().getEmail());
                                    startActivity(i);
                                }
                            });

                        }
                    });

                }
            }
        });
    }
}
