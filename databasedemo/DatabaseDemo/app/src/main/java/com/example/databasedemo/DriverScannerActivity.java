/*
DriverScannerActivity
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Uses camera to scan QR code given by rider
 * @author Hussein Warsame
 */
public class DriverScannerActivity extends BaseScannerActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private String TAG = "message";

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
        Toast.makeText(this, "Contents = " + rawResult.getText() +
                ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();

        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(DriverScannerActivity.this);
            }
        }, 2000);
    }
}
