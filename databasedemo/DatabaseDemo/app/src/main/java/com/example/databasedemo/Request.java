/*
Request
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import java.util.ArrayList;

/**
 * Request high-level abstraction class. Contains all information regarding a single request
 * @author Michael Antifaoff
 */
public class Request
{
    private Rider rider;
    private Driver driver;
    private Location startLocation;
    private Location endLocation;
    private double fare;
    private Boolean requestStatus;  // False when open, becomes true when driver matches with rider
                                            // True means the request is in progress
    private static double fareMultiplier = 2;
    private static double fareScaler = 5.0;

    private boolean riderConfirmation = false;
    private boolean driverConfirmation = false;

    private boolean paymentComplete = false;

    private static ArrayList<Request> requests = new ArrayList<Request>();


    public Request() {

    }

    /**
     * constructor class for request
     * assigns parameter values and initializes other private attributes
     * @param {@code Rider}rider Rider who made the request
     * @param {@code Location}  startLocation Defined start location
     * @param {@code Location}endLocation Defined end location
     */
    public Request (Rider rider, Location startLocation, Location endLocation) {
      this.rider = rider;
      this.driver = new Driver();
      this.startLocation = startLocation;
      this.endLocation = endLocation;
      this.fare = calculateFare(getDistance(startLocation, endLocation));
      this.requestStatus = false;
    }

    /**
     * Calculate and return distance between two points
     * @param {@code Location}startLocation
     * @param {@code Location}endLocation
     * @return Distance between two Locations in kilometers
     */
    public static double getDistance(Location startLocation, Location endLocation){
       // Get distance between start and end location using Maps API, or some other formula
        float results[]=new float[10];
        android.location.Location.distanceBetween(startLocation.getLatitude(),
                startLocation.getLongitude(),endLocation.getLatitude(),
                endLocation.getLongitude(),results);
        double result = (double)results[0];
        return result/1000;
    }

    /**
     * calculate and return fare based on distance
     * @param {@code double}distance
     * @return {@code double} fare
     */
    public static double calculateFare(double distance){
      return distance*fareMultiplier + fareScaler;
    }

    // Adds a driver associated with the request

    /**
     *  adds driver once request is accepted
     * @param {@code Driver}driver
     */
    public void isAcceptedBy(Driver driver){
      this.requestStatus = true;
      this.driver = driver;
    }

    /**
     * confirms rider
     */
    public void riderConfirmation(){
      this.riderConfirmation = true;
    }

    /**
     * confirms driver
     */
    public void driverConfirmation(){
      this.driverConfirmation = true;
    }

    /**
     * @deprecated
     * is true if rider and driver have confirmed
     * @return {@code boolean} whether both parties have compared
     */
    public boolean isConfirmedByRiderAndDriver() {
      return riderConfirmation && driverConfirmation;
    }

    /**
     * @deprecated
     * creates ride based on request data
     * @return
     */
    public Ride createRide(){
      Ride ride = new Ride(driver, rider, startLocation, endLocation, fare);
      return ride;
    }

    // Please ignore this comment:
    // Want the code to look something like this, in both rider and driver Confirm Pickup button onClickListeners:
    //
    // This is the Confirm Button onClickListener, for either the rider or driver:
    //
    // riderConfirmation(); or driverConfirmation();    // Depends which onClickListener we are in
    // Ride ride;
    // if(isConfirmedByRiderAndDriver()){         // If both the rider and driver have confirmed pickup, create a ride
    //     ride = request.createRide();
    //     // Need to add ride to ride database
    // } else {
    //    while(!isConfirmedByRiderAndDriver()){} // Otherwise, wait for the other user (the complement rider/driver) to confirm and create the ride
    // }

    /**
     * checks if this equals the given object
     * @param {@code Object} o Object to be compared to
     * @return {@code boolean} whether object is equal to self
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {  return false; } // First, check if the object is null
        if (this == o) {  return true;  } // Then, check if they are the same object
        if (getClass() != o.getClass()) {  return false; } // If they are not both of Request class type, return false
        Request request = (Request) o;    // Cast Object o to be of Request type
        return rider.equals(request.getRider())  // Check that the rider, start and end locations are all the same
            && startLocation.equals(request.getStartLocation())
            && endLocation.equals(request.getEndLocation());
    }

    /**
     * converts its string representation
     * @return {@code String} String representation of self
     */
    @Override
    public String toString(){
        String str = "" + this.getRider().getUsername() + " Distance: " +
                this.getDistance(this.getStartLocation(), this.getEndLocation()) + " Fare: " +
                this.getFare();
        return str;

    }


  // Getters

    /**
     * gets rider
     * @return {@code Rider} rider
     */
	public Rider getRider() {
		return this.rider;
	}

    /**
     * gets driver
     * @return {@code Driver} driver
     */
	public Driver getDriver(){
        return this.driver;
    }

    /**
     * get start location
     * @return {@code Location} startLocation
     */
	public Location getStartLocation() {
		return this.startLocation;
	}

    /**
     * get end location
     * @return {@code Location} endLocation
     */
	public Location getEndLocation() {
		return this.endLocation;
	}

    /**
     * gets fare
     * @return {@code double} fare
     */
	public double getFare() {
		return this.fare;
	}

    /**
     * gets requestStatus
     * @return {@code Boolean} requestStatus
     */
	public Boolean getRequestStatus() {
		return this.requestStatus;
	}

    /**
     * gets fare multiplier
     * @return {@code double} fareMultiplier
     */
	public double getFareMultiplier() {
		return this.fareMultiplier;
	}

    /**
     * gets rider confirmation
     * @return {@code Boolean} riderConfirmation
     */
    public boolean getRiderConfirmation() {
		return this.riderConfirmation;
	}

    /**
     * gets driver confirmation
     * @return {@code Boolean} driverConfirmation
     */
    public boolean getDriverConfirmation() {
		return this.driverConfirmation;
	}

    /**
     * gets payment complete
     * @return {@code boolean} paymentComplete
     */
	public boolean getPaymentComplete(){
        return this.paymentComplete;
    }

  // Setters

    /**
     * sets rider
     * @param {@code Rider }rider
     */
	public void setRider(Rider rider) {
		this.rider = rider;
	}

    /**
     * gets driver
     * @param {@code Driver} driver
     */
	public void setDriver(Driver driver){
        this.driver = driver;
    }

    /**
     * sets start location
     * @param {@code Location} startLocation
     */
	public void setStartLocation(Location startLocation) {
		this.startLocation = startLocation;
	}

    /**
     * sets end location
     * @param {@code Location} endLocation
     */
	public void setEndLocation(Location endLocation) {
		this.endLocation = endLocation;
	}

    /**
     * sets fare
     * @param {@code double}fare
     */
	public void setFare(double fare) {
		this.fare = fare;
	}

    /**
     * sets requestStatus
     * @param {@code boolean}requestStatus
     */
	public void setRequestStatus(Boolean requestStatus) {
		this.requestStatus = requestStatus;
	}

    /**
     * sets fare multiplier
     * @param {@code double}fareMultiplier
     */
	public void setFareMultiplier(double fareMultiplier) {
		this.fareMultiplier = fareMultiplier;
	}

    /**
     * sets paymentComplete
     * @param {@code boolean}paymentComplete
     */
	public void setPaymentComplete(boolean paymentComplete){
        this.paymentComplete = paymentComplete;
    }

}
