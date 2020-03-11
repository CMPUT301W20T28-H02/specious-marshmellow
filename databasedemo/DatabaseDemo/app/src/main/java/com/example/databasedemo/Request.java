package com.example.databasedemo;

import java.util.ArrayList;

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

    private static ArrayList<Request> requests = new ArrayList<Request>();


    public Request() {

    }

    public Request (Rider rider, Location startLocation, Location endLocation) {
      this.rider = rider;
      this.driver = new Driver();
      this.startLocation = startLocation;
      this.endLocation = endLocation;
      this.fare = calculateFare(getDistance(startLocation, endLocation));
      this.requestStatus = false;
    }

    // Taken care of by Sirjan, Goggle Maps API and LatLng object
    // Left in for now so the code still works
    public static double getDistance(Location startLocation, Location endLocation){
       // Get distance between start and end location using Maps API, or some other formula
        float results[]=new float[10];
        android.location.Location.distanceBetween(startLocation.getLatitude(),startLocation.getLongitude(),endLocation.getLatitude(),endLocation.getLongitude(),results);
        double result = (double)results[0];
        return result/1000;
    }

    public static double calculateFare(double distance){
      return distance*fareMultiplier + fareScaler;
    }

    // Adds a driver associated with the request
    public void isAcceptedBy(Driver driver){
      this.requestStatus = true;
      this.driver = driver;
    }

    public void riderConfirmation(){
      this.riderConfirmation = true;
    }

    public void driverConfirmation(){
      this.driverConfirmation = true;
    }

    public boolean isConfirmedByRiderAndDriver() {
      return riderConfirmation && driverConfirmation;
    }

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

    @Override
    public String toString(){
        String str = "" + this.getRider().getUsername() + " Distance: " +
                this.getDistance(this.getStartLocation(), this.getEndLocation()) + " Fare: " +
                this.getFare();
        return str;

    }

  // Getters
	public Rider getRider() {
		return this.rider;
	}

	public Location getStartLocation() {
		return this.startLocation;
	}

	public Location getEndLocation() {
		return this.endLocation;
	}

	public double getFare() {
		return this.fare;
	}

	public Boolean getRequestStatus() {
		return this.requestStatus;
	}

	public double getFareMultiplier() {
		return this.fareMultiplier;
	}

  public boolean getRiderConfirmation() {
		return this.riderConfirmation;
	}

  public boolean getDriverConfirmation() {
		return this.driverConfirmation;
	}

  // Setters
	public void setRider(Rider rider) {
		this.rider = rider;
	}

	public void setStartLocation(Location startLocation) {
		this.startLocation = startLocation;
	}

	public void setEndLocation(Location endLocation) {
		this.endLocation = endLocation;
	}

	public void setFare(double fare) {
		this.fare = fare;
	}

	public void setRequestStatus(Boolean requestStatus) {
		this.requestStatus = requestStatus;
	}

	public void setFareMultiplier(double fareMultiplier) {
		this.fareMultiplier = fareMultiplier;
	}

}
