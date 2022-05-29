package com.company.foodapp.controllers;

import com.company.foodapp.repositories.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FoodController {
    public FoodRepository foodRepository;

    @Autowired
    public FoodController(FoodRepository foodRepository){
        this.foodRepository = foodRepository;
    }
}
