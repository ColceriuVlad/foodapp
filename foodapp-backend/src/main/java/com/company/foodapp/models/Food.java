package com.company.foodapp.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Food")
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    @NotNull
    public String name;
    @NotNull
    public int price;
    @NotNull
    public String currency;
    public Double discount;
    @NotNull
    @ManyToOne
    public Supplier supplier;

    public Food() {

    }

    public Food(int id, String name, int price, Double discount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.discount = discount;
    }
}
