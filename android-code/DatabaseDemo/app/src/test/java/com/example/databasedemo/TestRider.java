package com.example.databasedemo;

import android.sax.StartElementListener;

import org.junit.Before;
import org.junit.Test;

import java.util.EnumSet;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class TestRider {
    private Rider rider;
    private Location StartLocation, EndLocation;
    @Before
    public void SetUp(){
        Wallet wallet = new Wallet(123);
        rider = new Rider("sample","sample@sampl.e",wallet,"1234567890",3.5,2,false);
        Random rnd = new Random();
        StartLocation = new Location(rnd.nextDouble()*90,rnd.nextDouble()*120);
        EndLocation = new Location (rnd.nextDouble()*90,rnd.nextDouble()*120);
    }

    @Test
    public void calculateDistance_isCorrect() {
        Request request = new Request(rider, StartLocation,EndLocation);
        double distance =request.getDistance(StartLocation,EndLocation);
        assertTrue( distance>=0);
        StartLocation.setLatitude(0);
        StartLocation.setLongitude(0);
        EndLocation.setLongitude(0);
        EndLocation.setLatitude(0);
        distance = request.getDistance(StartLocation, EndLocation);
        assertTrue(distance==0.0);


    }
}