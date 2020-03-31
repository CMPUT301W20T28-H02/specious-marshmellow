/*
User
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

/**
 * parent class for Driver and Rider. holds all the user information
 * @see Driver
 * @see Rider
 * @author Marcus Blair, Michael Antifaoff, Rafaella Graña
 */
public class User {
	private String username;
	private String email;
	private Wallet wallet;	// Should be private? Or move getPaid() and pay() up to User?
	private String phone;
	private double rating;
	private int numOfRatings; 	// Need this to generate an average rating
	private boolean driver;
	private boolean hasProfilePicture = false;

	public User(){

	}

	/**
	 * constructor for User
	 * @param {@code String} username
	 * @param {@code String}email
	 * @param {@code Wallet}wallet
	 * @param {@code String}phone
	 * @param {@code double}rating
	 * @param {@code int}numOfRatings
	 * @param {@code boolean}driver
	 */
	public User(String username, String email,
		Wallet wallet, String phone, double rating, int numOfRatings, boolean driver, boolean hasProfilePicture){
		this.username = username;
		this.email = email;
		this.wallet = wallet;
		this.phone = phone;
		this.rating = rating;
		this.numOfRatings = numOfRatings;
		this.driver = driver;
		this.hasProfilePicture = hasProfilePicture;
	}

	// What are these two supposed to do?
	// public void openWallet(Wallet wallet){}	// Add to Sidebar, not backend
	// public void viewRequest(Request request){} 	// For offline viewing of the request, not backend

	// Do we need a boolean to implement this? No, implement in requestWithDriver class
	//public void confirmPickup(Location location){}

	/**
	 * calculates its new average rating given a new rating
	 * @param {@code double}givenRating given rating
	 */
	// Add a rating for a user (Rider or Driver)
	public void rateUser(double givenRating){
		this.rating = calculateNewRating(givenRating);
  }

	// Calculate the new rating as the current rating times the number of ratings,
	// to give the current total rating points, then add the newest rating,
	// then divide by the new number of total ratings (the old numOfRatings + 1)

	/**
	 * Calculate the new rating the current rating based on simple average
	 * @param {@code double}givenRating
	 * @return {@code} new average rating
	 */
	public double calculateNewRating(double givenRating){
		double newRating = ((getRating()*getNumOfRatings())+givenRating)/(getNumOfRatings()+1);
		this.numOfRatings += 1;
		return newRating;
	}


	// Getters

	/**
	 * gets username
	 * @return{@code String} username
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * gets email
	 * @return{@code String} email
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * gets wallet
	 * @return{@code Wallet} wallet
	 */
	public Wallet getWallet() {
		return this.wallet;
	}

	/**
	 * gets phone
	 * @return{@code String} phone
	 */
	public String getPhone() {
		return this.phone;
	}

	/**
	 * gets rating
	 * @return{@code double} rating
	 */
	public double getRating() {
		return this.rating;
	}

	/**
	 * gets number of ratings
	 * @return{@code int} numOfRatings
	 */
	public int getNumOfRatings() {
		return this.numOfRatings;
	}

	/**
	 * gets whether is driver
	 * @return{@code boolean} driver
	 */
	public boolean getDriver() {
		return this.driver;
	}


	/**
	 * gets whether the user has a profile picture or not
	 * @return boolean} hasProfilePicture
	 */
	public boolean getHasProfilePicture()
	{
		return this.hasProfilePicture;
	}

	// Setters

	/**
	 * sets username
	 * @param {@code String}username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * sets email
	 * @param {@code String} email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * sets wallet
	 * @param {@code Wallet} wallet
	 */
	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
	}

	/**
	 * sets phone
	 * @param {@code String} phone
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * sets rating
	 * @param {@code double} rating
	 */
	public void setRating(double rating) {
		this.rating = rating;
	}

	/**
	 * sets number of ratings
	 * @param {@code int} numOfRatings
	 */
	public void setNumOfRatings(int numOfRatings) {
		this.numOfRatings = numOfRatings;
	}

	/**
	 * sets whether is driver
	 * @param {@code boolean} driver
	 */
	public void setDriver(boolean driver) {
		this.driver = driver;
	}


	public void setHasProfilePicture( boolean hasProfilePicture )
	{
		this.hasProfilePicture = hasProfilePicture;
	}
}
