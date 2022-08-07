package com.company.foodapp.models;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Entity
@Table(name = "Message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String username;
    public String role;
    public String name;
    public String description;
    @Email
    public String email;

    public Message() {

    }

    public Message(int id, String username, String role, String name, String description) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.name = name;
        this.description = description;
    }
}
