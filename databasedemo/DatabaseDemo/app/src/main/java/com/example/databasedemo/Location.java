package com.example.databasedemo;

public class Location {

  private double latitude;
  private double longitude;

  public Location (double latitude, double longitude){
    this.latitude = latitude;
    this.longitude = longitude;
  }

  // Need this to be able to compare if two requests are equal
  @Override
  public boolean equals(Object o) {
      if (o == null) {  return false; } // First, check if the object is null
      if (this == o) {  return true;  } // Then, check if they are the same object
      if (getClass() != o.getClass()) {  return false; } // If they are not both of Request class type, return false
      Location location = (Location) o;    // Cast Object o to be of Request type
      return (this.getLatitude() == location.getLatitude())
          && (this.getLongitude() == location.getLongitude()); // Can compare solely based on usernames, which are guarenteed unique
  }

  // Getters
  public double getLatitude(){
    return latitude;
  }
  public double getLongitude(){
    return longitude;
  }

  // Setters
  public void setLatitude(double latitude){
    this.latitude = latitude;
  }
  public void setLongitude(double longitude){
    this.longitude = longitude;
  }

}
