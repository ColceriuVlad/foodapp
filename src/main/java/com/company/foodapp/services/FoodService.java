package com.company.foodapp.services;

import com.company.foodapp.models.Food;
import com.company.foodapp.repositories.FoodRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FoodService {
    private FoodRepository foodRepository;
    private Logger logger;

    @Autowired
    public FoodService(FoodRepository foodRepository, Logger logger) {
        this.foodRepository = foodRepository;
        this.logger = logger;
    }

    public List<Food> getAllFoodsBySupplierName(String supplierName) {
        var foodList = foodRepository.findAll();
        var parsedFoodList = new ArrayList<Food>();


        if (!foodList.isEmpty()) {
            logger.info("Successfully retrieved all the food types of the application");

            for (var food : foodList) {
                if (food.supplier.name.equals(supplierName)) {
                    parsedFoodList.add(food);
                }
            }
        } else {
            logger.info("Could not retrieve any food from the application");
        }

        if (!parsedFoodList.isEmpty()) {
            logger.info("Successfully retrieved food types for supplier with name " + supplierName);
        } else {
            logger.info("Could not retrieve any food for supplier with name " + supplierName);
        }

        return parsedFoodList;
    }

    public Food getFoodFromSupplier(String supplierName, String foodName) {
        var foodList = getAllFoodsBySupplierName(supplierName);
        Food actualFood = null;

        if (!foodList.isEmpty()) {
            for (var food : foodList) {
                if (food.name.equals(foodName)) {
                    actualFood = food;
                }
            }
        }

        if (actualFood != null) {
            logger.info("Successfully retrieved food of type " + foodName + " from supplier " + supplierName);
        } else {
            logger.info("Could not retrieve food of type " + foodName + " from supplier " + supplierName);
        }

        return actualFood;
    }
}
