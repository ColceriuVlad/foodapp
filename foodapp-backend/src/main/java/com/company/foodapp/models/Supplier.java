package com.company.foodapp.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Supplier")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    @NotNull(message = "Supplier name is mandatory")
    public String name;
    @NotNull(message = "Transportation cost is mandatory")
    public Integer transportationCost;
    @NotNull(message = "Transportation currency is mandatory")
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
