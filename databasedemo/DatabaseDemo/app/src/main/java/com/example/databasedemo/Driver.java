package com.example.databasedemo;

import java.util.ArrayList;

public class Driver extends User {

  public Driver(String username, String password, String email, Wallet wallet, String phone, double rating, double numOfRatings, Location location) {
    super(username, password, email, wallet, phone, rating, numOfRatings, location);
  }

  public ArrayList<Request> searchRequests(Location driverLocation, ArrayList<Request> requestsToBeSorted) {
    // Calculate the distance from driverLocation to each Request's startLocation in our global arraylist of requests
    // Sort an arraylist of distances, based on smallest first
    // Create an arraylist with Requests that are sorted by distance

    // Implemented with a custom comparator
    requestsToBeSorted.sort(new RequestComparator(this.getLocation()));
    return requestsToBeSorted;
  }



  // Driver gets paid the amount of QRBucks
  public void getPaid(double amount) {
    Wallet wallet = this.getWallet();
    wallet.deposit(amount);
    // wallet.setBalance(wallet.getBalance() + amount);
    // User.setWallet(wallet);
  }

}
