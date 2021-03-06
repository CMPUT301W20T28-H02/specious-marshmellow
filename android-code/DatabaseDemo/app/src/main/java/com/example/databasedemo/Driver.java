/*
Driver
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

import java.util.ArrayList;

/**
 * Driver. A user that has driver privileges
 * @see User
 * @author Michael Antifaoff
 */
public class Driver extends User {

  public Driver(){

  }

  /**
   * calls parent constructor
   * @param {@code String}username
   * @param {@code String}email
   * @param {@code Wallet} wallet
   * @param {@code String} phone
   * @param {@code double}rating
   * @param {@code int}numOfRatings
   * @param {@code boolean}driver
   */
  public Driver(String username, String email, Wallet wallet, String phone, double rating, int numOfRatings, boolean driver, boolean hasProfilePicture) {
    super(username, email, wallet, phone, rating, numOfRatings, driver, hasProfilePicture);
  }

  public Driver(User user){
    super(user.getUsername(), user.getEmail(), user.getWallet(), user.getPhone(), user.getRating(), user.getNumOfRatings(), user.getDriver(), user.getHasProfilePicture());
  }

  /*public ArrayList<Request> searchRequests(Location driverLocation, ArrayList<Request> requestsToBeSorted) {
    // Calculate the distance from driverLocation to each Request's startLocation in our global arraylist of requests
    // Sort an arraylist of distances, based on smallest first
    // Create an arraylist with Requests that are sorted by distance

    // Implemented with a custom comparator
    requestsToBeSorted.sort(new RequestComparator(this.getLocation()));
    return requestsToBeSorted;
  }*/


  /**
   * deposits money into the driver's wallet
   * @param {@code double}amount
   */
  // Driver gets paid the amount of QRBucks
  public void getPaid(double amount) {
    Wallet wallet = this.getWallet();
    wallet.deposit(amount);
    this.setWallet(wallet);
    // wallet.setBalance(wallet.getBalance() + amount);
    // User.setWallet(wallet);
  }

}
