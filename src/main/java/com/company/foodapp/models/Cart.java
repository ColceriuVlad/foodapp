package com.company.foodapp.models;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    @OneToOne
    public User user;
    @OneToMany
    public List<Food> foodList;

    public Cart() {

    }

    public Cart(int id, User user, List<Food> foodList) {
        this.id = id;
        this.user = user;
        this.foodList = foodList;
    }
}
