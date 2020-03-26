/*
Rider
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import java.util.ArrayList;

/**
 * Rider class, inherits from user and holds rider-specific information
 * @author Michael Antifaoff
 */
public class Rider extends User {

  private static ArrayList<Request> requests = new ArrayList<Request>();

  public Rider(){

  }

  /**
   * constructor for Rider
   * @param {@code String}username
   * @param {@code String}email
   * @param {@code Wallet}wallet
   * @param {@code String}phone
   * @param {@code double}rating
   * @param {@code int}numOfRatings
   * @param {@code boolean}driver
   */
  public Rider(String username, String email, Wallet wallet, String phone, double rating, int numOfRatings, boolean driver, boolean hasProfilePicture) {
    super(username, email, wallet, phone, rating, numOfRatings, driver, hasProfilePicture);  // driver should be false
  }

  /**
   * adds request to requests array based on start and end location
   * returns newly created request
   * @param {@code Location} startLocation
   * @param {@code Location} endLocation
   * @return {@code Request} Request
   */
  public Request requestRide(Location startLocation, Location endLocation) {
    Request request = new Request(this, startLocation, endLocation);
    requests.add(request);

    // TODO: Need to add this request to the database
    return request;
  }

  // Takes in a request to be removed from the database and ArrayList of requests
  // If the request is in the ArrayList, it is removed from the ArrayList and the database

  /**
   *
   *  Take sin a request to be removed from the database and requests array,
   *  if request is in the requests array, it is removed from both array and database
   * @param {@code Request}request to be removed
   */
  public void cancelRequest(Request request) {
    for (Request r : this.requests){
      if (r.equals(request)){
        this.requests.remove(r);
        //TODO: Update database
      }
    }
  }

  /**
   * withdraws amount from wallet in order to pay driver
   * @param {@code double}amount
   */
  public void pay(double amount){
    Wallet wallet = this.getWallet();
    wallet.withdraw(amount);
  }

  /**
   * increases the amount on the wallet
   * @param amount
   */
  public void addMoney(double amount) {
    Wallet wallet = this.getWallet();
    wallet.deposit(amount);
    this.setWallet(wallet);
  }

  /**
   * compares if self is equal to given object
   * @param {@code Object} o object to compare
   * @return {@code boolean} whether self equals object
   */
  @Override
  public boolean equals(Object o) {
      if (o == null) {  return false; } // First, check if the object is null
      if (this == o) {  return true;  } // Then, check if they are the same object
      if (getClass() != o.getClass()) {  return false; } // If they are not both of Request class type, return false
      Rider rider = (Rider) o;    // Cast Object o to be of Request type
      return this.getUsername().equals(rider.getUsername()); // Can compare solely based on usernames, which are guarenteed unique
  }

}
