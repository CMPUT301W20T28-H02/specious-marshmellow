/*
BaseScannerActivity
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;

/**
 * Basic scanner activity
 * Please see https://github.com/dm77/barcodescanner. This class has been built on
 * this ZXING barcode library.
 * @author Hussein Warsame
 * @deprecated
 * */
public class BaseScannerActivity extends AppCompatActivity {
    /*public void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    } */

    /**
     * Return home when home button is pressed
     * @param {@code MenuItem}item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
