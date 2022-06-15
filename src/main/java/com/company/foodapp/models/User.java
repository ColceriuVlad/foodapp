package com.company.foodapp.models;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Entity
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String username;
    public String password;
    public String role;
    @Email
    public String email;

    public User() {

    }

    public User(int id, String username, String password, String role, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
    }
}
