package com.company.foodapp.models;


import javax.persistence.*;

@Entity
@Table(name = "Supplier")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String name;
    public Integer transportationCost;
    public String transportationCurrency;
    public Double discountRate;

    public Supplier() {

    }

    public Supplier(int id, String name, Integer transportationCost, Double discountRate) {
        this.id = id;
        this.name = name;
        this.transportationCost = transportationCost;
        this.discountRate = discountRate;
    }

    public Supplier(String name, Integer transportationCost, Double discountRate) {
        this.name = name;
        this.transportationCost = transportationCost;
        this.discountRate = discountRate;
    }
}
