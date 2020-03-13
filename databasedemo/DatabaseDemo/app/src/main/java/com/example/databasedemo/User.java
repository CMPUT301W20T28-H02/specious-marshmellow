package com.example.databasedemo;

public class User {
	private String username;
	private String email;
	private Wallet wallet;	// Should be private? Or move getPaid() and pay() up to User?
	private String phone;
	private double rating;
	private int numOfRatings; 	// Need this to generate an average rating
	private boolean driver;

	public User(){

	}

	public User(String username, String email,
		Wallet wallet, String phone, double rating, int numOfRatings, boolean driver){
		this.username = username;
		this.email = email;
		this.wallet = wallet;
		this.phone = phone;
		this.rating = rating;
		this.numOfRatings = numOfRatings;
		this.driver = driver;
	}

	// What are these two supposed to do?
	// public void openWallet(Wallet wallet){}	// Add to Sidebar, not backend
	// public void viewRequest(Request request){} 	// For offline viewing of the request, not backend

	// Do we need a boolean to implement this? No, implement in requestWithDriver class
	//public void confirmPickup(Location location){}

	// Add a rating for a user (Rider or Driver)
	public void rateUser(double givenRating){
		this.rating = calculateNewRating(givenRating);
  }

	// Calculate the new rating as the current rating times the number of ratings,
	// to give the current total rating points, then add the newest rating,
	// then divide by the new number of total ratings (the old numOfRatings + 1)
	public double calculateNewRating(double givenRating){
		double newRating = ((getRating()*getNumOfRatings())+givenRating)/(getNumOfRatings()+1);
		this.numOfRatings += 1;
		return newRating;
	}


	// Getters
	public String getUsername() {
		return this.username;
	}

	public String getEmail() {
		return this.email;
	}

	public Wallet getWallet() {
		return this.wallet;
	}

	public String getPhone() {
		return this.phone;
	}

	public double getRating() {
		return this.rating;
	}

	public double getNumOfRatings() {
		return this.numOfRatings;
	}

	public boolean getDriver() {
		return this.driver;
	}

	// Setters
	public void setUsername(String username) {
		this.username = username;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public void setNumOfRatings(int numOfRatings) {
		this.numOfRatings = numOfRatings;
	}

	public void setDriver(boolean driver) {
		this.driver = driver;
	}

}
