package com.example.databasedemo;

public class Ride{
	private Driver driver;
	private Rider rider;
	private Location startLocation;
	private Location endLocation;
	private double fare;


	public Ride(){

	}

	// This is created when both have hit the Confirm Pickup button
	public Ride(Driver driver, Rider rider, Location startLocation, Location endLocation, double fare){
		this.driver = driver;
		this.rider = rider;
		this.startLocation = startLocation;
		this.endLocation = endLocation;
		this.fare = fare;
	}

	// Called by the driver, amount comes from the QR Code AND includes the tip (so we can't simply use fare)
	public void endRide(double amount){
		rider.pay(amount);
		driver.getPaid(amount);
	}


	// Getters
	public Driver getDriver() {
		return this.driver;
	}

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

	// Setters
	public void setDriver(Driver driver) {
		this.driver = driver;
	}

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
}
