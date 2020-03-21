/*
Wallet
Version 1
Date March 13 2020
 */
package com.example.databasedemo;

/**
 * Holds an amount of money and can be deposited to and withdrawn from
 * @author Michael Antifaoff
 */
public class Wallet {
    private double balance;

    /**
     * constructor method for Wallet
     */
    public Wallet() {
        this.balance = 0;
    }

    /**
     * Constructor with initializing amount
     * @param {@code double} balance
     */
    public Wallet(double balance) {
        this.balance = balance;
    }

    // Getters

    /**
     * returns balance
     * @return {@code double balance
     */
    public double getBalance() {
        return this.balance; // Return the amount in the user's wallet
    }

    // Add money to the wallet

    /**
     * increases balance by amount
     * @param {@code double}amount
     */
    public void deposit(double amount){
      this.balance += amount;
    }

    // Take money out of the wallet

    /**
     * decreases balance by amount
     * @param {@code double} amount
     */
    public void withdraw(double amount){
      this.balance -= amount;
    }
}
