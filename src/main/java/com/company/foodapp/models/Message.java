package com.company.foodapp.models;

import javax.persistence.*;

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
