/*
Result
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

/**
 * Handles and recognized what kind of barcode format is received.
 * Please see https://github.com/dm77/barcodescanner. This class has been built on
 * this ZXING barcode library.
 * @author Hussein Warsame
 */
public class Result {
    private String mContents;
    private BarcodeFormat mBarcodeFormat;

    /**
     * sets contents
     * @param {@code String}contents
     */
    public void setContents(String contents) {
        mContents = contents;
    }

    /**
     * sets barcode format
     * @param {@code BarcodeFormat}format
     */
    public void setBarcodeFormat(BarcodeFormat format) {
        mBarcodeFormat = format;
    }

    /**
     * gets barcode format
     * @return {@code BarcodeFormat} barcodeFormat
     */
    public BarcodeFormat getBarcodeFormat() {
        return mBarcodeFormat;
    }

    /**
     * gets contents
     * @return {@code String} contents
     */
    public String getContents() {
        return mContents;
    }
}
