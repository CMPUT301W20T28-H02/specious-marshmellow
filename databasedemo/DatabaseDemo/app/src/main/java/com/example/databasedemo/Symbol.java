/*
Symbol
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

/**
 * helper class for Barcode Format
 * @see BarcodeFormat
 * @author Hussein Warsame
 */
public class Symbol {
    public class Rect
    {
        public int x, y, width, height;
    };

    /** No symbol decoded. */
    public static final int NONE = 0;
    /** Symbol detected but not decoded. */
    public static final int PARTIAL = 1;

    /** EAN-8. */
    public static final int EAN8 = 8;
    /** UPC-E. */
    public static final int UPCE = 9;
    /** ISBN-10 (from EAN-13). */
    public static final int ISBN10 = 10;
    /** UPC-A. */
    public static final int UPCA = 12;
    /** EAN-13. */
    public static final int EAN13 = 13;
    /** ISBN-13 (from EAN-13). */
    public static final int ISBN13 = 14;
    /** Interleaved 2 of 5. */
    public static final int I25 = 25;
    /** Code 39. */
    public static final int CODE39 = 39;
    /** PDF417. */
    public static final int PDF417 = 57;
    /** QR Code. */
    public static final int QRCODE = 64;
    /** Code 128. */
    public static final int CODE128 = 128;

    /** C pointer to a zbar_symbol_t. */
    private long peer;

    /** Cached attributes. */
    private int type;

    static
    {
        System.loadLibrary("zbarjni");
        init();
    }
    private static native void init();

    /** Symbols are only created by other package methods. */
    Symbol (long peer)
    {
        this.peer = peer;
    }

    protected void finalize ()
    {
        destroy();
    }

    /** Clean up native data associated with an instance. */
    public synchronized void destroy ()
    {
        if(peer != 0) {
            destroy(peer);
            peer = 0;
        }
    }

    /** Release the associated peer instance.  */
    private native void destroy(long peer);

    /** Retrieve type of decoded symbol. */
    public int getType ()
    {
        if(type == 0)
            type = getType(peer);
        return(type);
    }

    private native int getType(long peer);

    /** Retrieve data decoded from symbol as a String. */
    public native String getData();

    /** Retrieve raw data bytes decoded from symbol. */
    public native byte[] getDataBytes();

    /** Retrieve a symbol confidence metric.  Quality is an unscaled,
     * relative quantity: larger values are better than smaller
     * values, where "large" and "small" are application dependent.
     */
    public native int getQuality();

    /** Retrieve current cache count.  When the cache is enabled for
     * the image_scanner this provides inter-frame reliability and
     * redundancy information for video streams.
     * @returns < 0 if symbol is still uncertain
     * @returns 0 if symbol is newly verified
     * @returns > 0 for duplicate symbols
     */
    public native int getCount();

    /** Retrieve an approximate, axis-aligned bounding box for the
     * symbol.
     */
    public Rect getBounds ()
    {
        int n = getLocationSize(peer);
        if(n <= 0)
            return(null);

        Rect bounds = new Rect();
        int xmin = Integer.MAX_VALUE;
        int xmax = Integer.MIN_VALUE;
        int ymin = Integer.MAX_VALUE;
        int ymax = Integer.MIN_VALUE;

        for(int i = 0; i < n; i++) {
            int x = getLocationX(peer, i);
            if(xmin > x) xmin = x;
            if(xmax < x) xmax = x;

            int y = getLocationY(peer, i);
            if(ymin > y) ymin = y;
            if(ymax < y) ymax = y;
        }
        bounds.x = xmin;
        bounds.y = ymin;
        bounds.width = xmax - xmin;
        bounds.height = ymax - ymin;
        return(bounds);
    }

    private native int getLocationSize(long peer);
    private native int getLocationX(long peer, int idx);
    private native int getLocationY(long peer, int idx);

    /** Retrieve general axis-aligned, orientation of decoded
     * symbol.
     */
    public native int getOrientation();


    private native long getComponents(long peer);

    native long next();
}
