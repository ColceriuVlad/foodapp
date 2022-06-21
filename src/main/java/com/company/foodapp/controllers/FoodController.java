package com.company.foodapp.controllers;

import com.company.foodapp.handlers.AuthHandler;
import com.company.foodapp.models.Food;
import com.company.foodapp.repositories.FoodRepository;
import com.company.foodapp.repositories.SupplierRepository;
import com.company.foodapp.services.FoodService;
import com.kastkode.springsandwich.filter.annotation.Before;
import com.kastkode.springsandwich.filter.annotation.BeforeElement;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("food")
@RestController
public class FoodController {
    private FoodRepository foodRepository;
    private FoodService foodService;
    private SupplierRepository supplierRepository;
    private Logger logger;

    @Autowired
    public FoodController(FoodRepository foodRepository, FoodService foodService, SupplierRepository supplierRepository, Logger logger) {
        this.foodRepository = foodRepository;
        this.foodService = foodService;
        this.supplierRepository = supplierRepository;
        this.logger = logger;
    }

    @GetMapping("{supplierName}")
    @Before(@BeforeElement(value = AuthHandler.class, flags = {"admin"}))
    public ResponseEntity<List<Food>> getAllFoodTypes(@PathVariable String supplierName) {
        ResponseEntity response;

        var foods = foodService.getAllFoodsBySupplierName(supplierName);

        if (!foods.isEmpty()) {
            response = new ResponseEntity(foods, HttpStatus.OK);
        } else {
            response = new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return response;
    }

    @GetMapping("{supplierName}/{foodName}")
    @Before(@BeforeElement(value = AuthHandler.class, flags = {"admin"}))
    public ResponseEntity<Food> getFoodFromSupplier(@PathVariable String supplierName, @PathVariable String foodName) {
        ResponseEntity response;

        var food = foodService.getFoodFromSupplier(supplierName, foodName);

        if (food != null) {
            response = new ResponseEntity(food, HttpStatus.OK);
        } else {
            response = new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return response;
    }

    @PostMapping("{supplierName}")
    @Before(@BeforeElement(value = AuthHandler.class, flags = {"admin"}))
    public ResponseEntity addFoodToSupplier(@PathVariable String supplierName, @RequestBody Food food) {
        ResponseEntity response;

        try {
            var supplier = supplierRepository.findByName(supplierName).get();
            food.supplier = supplier;
            foodRepository.save(food);

            logger.info("Successfully added food: " + food.name + " to supplier: " + supplierName);
            response = new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            logger.info("Could not add food: " + food.name + " to supplier: " + supplierName);

            response = new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return response;
    }

    @PutMapping("{supplierName}/{foodName}")
    @Before(@BeforeElement(value = AuthHandler.class, flags = {"admin"}))
    public ResponseEntity updateFoodPrice(@PathVariable String supplierName, @PathVariable String foodName, @RequestBody Food food) {
        var actualFood = foodRepository.findByName(foodName).get();
        var actualSupplier = actualFood.supplier;
        ResponseEntity response = new ResponseEntity(HttpStatus.OK);

        if (actualFood.name.equals(foodName)) {
            logger.info(String.format("Food %s was found", foodName));
        } else {
            logger.info(String.format("Food %s was not found", foodName));
            response = new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        if (actualSupplier.name.equals(supplierName)) {
            logger.info(String.format("Supplier %s was found for food %s", supplierName, foodName));
        } else {
            logger.info(String.format("Supplier %s was not found for food %s", supplierName, foodName));
            response = new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        try {
            var previousFoodPrice = actualFood.price;
            actualFood.price = food.price;

            foodRepository.save(actualFood);

            logger.info(String.format("Price of the food was updated from %s to %s", previousFoodPrice, actualFood.price));
        } catch (Exception exception) {
            logger.info(exception.getMessage());
            response = new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        return response;
    }
}
