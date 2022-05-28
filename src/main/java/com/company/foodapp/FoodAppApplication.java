package com.company.foodapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.kastkode.springsandwich.filter", "com.company.foodapp.*"})
public class FoodAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(FoodAppApplication.class, args);
    }
}
