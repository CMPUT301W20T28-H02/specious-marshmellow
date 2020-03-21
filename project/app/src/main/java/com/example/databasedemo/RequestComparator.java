/*
RequestComparator
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import java.lang.Math;
import java.util.Comparator;

/**
 * comparator class for Request
 * @see Request
 * @author Michael Antifaoff
 */
public class RequestComparator implements Comparator<Request>
{
    Location driverLocation;

    /**
     * constructor for comparator
     * @param {@code Location} driverLocation
     */
    public RequestComparator(Location driverLocation) {
      this.driverLocation = driverLocation;
    }

    /**
     * Compares both requests and returns a value based on their relative
     * proximity to driverLocation
     * @param {@code Request}a
     * @param {@code Request}b
     * @return
     */
    // Used for sorting in ascending order of distance between driver location and request
    public int compare(Request a, Request b)
    {
        if (Math.abs(a.getDistance(a.getStartLocation(), driverLocation)) == Math.abs(b.getDistance(b.getStartLocation(), driverLocation))){
          return 0;   // If the Requests are equally far away from the driver
        } else if (Math.abs(a.getDistance(a.getStartLocation(), driverLocation)) < Math.abs(b.getDistance(b.getStartLocation(), driverLocation))){
          return -1;  // If Request a is closer to the driver's current location than Request b, return -1 because a < b
        } else {
          return 1;
        }
    }
}
