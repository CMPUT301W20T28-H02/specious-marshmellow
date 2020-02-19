package com.example.databasedemo;

public class User {
    private String username;
    private String password;
    private String email;
    private int phone;
    private boolean driver;

    public User (String username, String password, String email, int phone, boolean driver) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.driver = driver;
    }
}
