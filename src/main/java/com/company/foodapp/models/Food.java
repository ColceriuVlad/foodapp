package com.company.foodapp.models;

import javax.persistence.*;

@Entity
@Table(name = "Food")
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String name;
    public int price;
    public String currency;

    public Food() {

    }

    public Food(int id, String name, int price, String currency) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.currency = currency;
    }

    public Food(String name, int price, String currency) {
        this.name = name;
        this.price = price;
        this.currency = currency;
    }
}
