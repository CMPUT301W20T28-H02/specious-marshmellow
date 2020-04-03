/*
SimpleScannerActivity
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Scans code using the camera
 * @author Hussein Warsame
 * @deprecated
 */
public class SimpleScannerActivity extends BaseScannerActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private String TAG = "message";

    /**
     * Called when activity is created
     * sets up camera
     * @param {@code Bundle}savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(R.layout.activity_simple_scanner);

        ViewGroup contentFrame = findViewById(R.id.content_frame);
        contentFrame.addView(mScannerView);
    }

    /**
     * called when activity resumes
     * sets itself as handler and starts camera again
     */
    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    /**
     * called when activity pauses
     * stops camera
     */
    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    /**
     * called when result is complete
     * delays for 2 seconds to resume preview, gets format for encoded image
     * @param rawResult
     */
    @Override
    public void handleResult(Result rawResult) {
        DynamicToast.make(SimpleScannerActivity.this, "Contents = " + rawResult.getText() +
                ", Format = " + rawResult.getBarcodeFormat().toString(), Color.parseColor("#E38249"),
                Color.parseColor("#000000"), Toast.LENGTH_LONG).show();


        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(SimpleScannerActivity.this);
            }
        }, 2000);
    }
}

