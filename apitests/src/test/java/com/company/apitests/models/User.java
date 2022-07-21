package com.company.apitests.models;

public class User {
    public String username;
    public String password;
    public String role;
    public String email;

    public User(String username, String password, String role, String email) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
    }
}
