/*
Location
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

/**
 * Defines a location based on latitude and longitude
 * @author Michael Antifaoff
 */
public class Location {

  private double latitude;
  private double longitude;

  public Location(){

  }

  /**
   * constructor method for Longitude
   * @param {@code double} latitude the latitude
   * @param {@code double} longitude the longitude
   */
  public Location (double latitude, double longitude){
    this.latitude = latitude;
    this.longitude = longitude;
  }

  /**
   * Compares itself and tell whether it is equal to another object o
   * @param {@code Object}o object to be compared to
   * @return {@code boolean} whether they are equal
   */
  // Need this to be able to compare if two requests are equal
  @Override
  public boolean equals(Object o) {
      if (o == null) {  return false; } // First, check if the object is null
      if (this == o) {  return true;  } // Then, check if they are the same object
      if (getClass() != o.getClass()) {  return false; } // If they are not both of Request class type, return false
      Location location = (Location) o;    // Cast Object o to be of Request type
      return (this.getLatitude() == location.getLatitude())
          && (this.getLongitude() == location.getLongitude());
  }

  /**
   * getter for latitude
   * @return {@code double} Latitude
   */
  // Getters
  public double getLatitude(){
    return latitude;
  }

  /**
   * getter for longitude
   * @return {@code double} longitude
   */
  public double getLongitude(){
    return longitude;
  }

  // Setters

  /**
   * setter for latitude
   * @param {@code double} latitude new latitude
   */
  public void setLatitude(double latitude){
    this.latitude = latitude;
  }
  /**
   * setter for longitude
   * @param {@code double} longitude new longitude
   */
  public void setLongitude(double longitude){
    this.longitude = longitude;
  }

}
