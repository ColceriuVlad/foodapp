package com.company.foodapp.models;

import javax.persistence.*;

@Entity
@Table(name = "Ordering")
public class Ordering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String timeStamp;
    public Integer totalPrice;

    public Ordering() {

    }

    public Ordering(int id, String timeStamp, Integer totalPrice) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.totalPrice = totalPrice;
    }
}
