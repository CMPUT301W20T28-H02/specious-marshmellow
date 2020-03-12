package com.example.databasedemo;

import java.util.ArrayList;

public class Rider extends User {

  private static ArrayList<Request> requests = new ArrayList<Request>();

  public Rider(){

  }

  public Rider(String username, String email, Wallet wallet, String phone, double rating, double numOfRatings, boolean driver) {
    super(username, email, wallet, phone, rating, numOfRatings, driver);  // driver should be false
  }

  public Request requestRide(Location startLocation, Location endLocation) {
    Request request = new Request(this, startLocation, endLocation);
    requests.add(request);

    // TODO: Need to add this request to the database
    return request;
  }

  // Takes in a request to be removed from the database and ArrayList of requests
  // If the request is in the ArrayList, it is removed from the ArrayList and the database
  public void cancelRequest(Request request) {
    for (Request r : this.requests){
      if (r.equals(request)){
        this.requests.remove(r);
        //TODO: Update database
      }
    }
  }

  public void pay(double amount){
    Wallet wallet = this.getWallet();
    wallet.withdraw(amount);
  }

  public void addMoney(double amount) {
    Wallet wallet = this.getWallet();
    wallet.deposit(amount);
  }

  @Override
  public boolean equals(Object o) {
      if (o == null) {  return false; } // First, check if the object is null
      if (this == o) {  return true;  } // Then, check if they are the same object
      if (getClass() != o.getClass()) {  return false; } // If they are not both of Request class type, return false
      Rider rider = (Rider) o;    // Cast Object o to be of Request type
      return this.getUsername().equals(rider.getUsername()); // Can compare solely based on usernames, which are guarenteed unique
  }

}
