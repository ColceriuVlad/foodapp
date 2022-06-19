package com.company.foodapp.services;

import com.company.foodapp.models.Food;
import com.company.foodapp.models.Supplier;
import com.company.foodapp.repositories.FoodRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class FoodServiceTest {
    private FoodRepository foodRepository;
    private Logger logger;
    private FoodService foodService;

    public FoodServiceTest() {
        foodRepository = mock(FoodRepository.class);
        logger = mock(Logger.class);
        foodService = new FoodService(foodRepository, logger);
    }

    @Test
    public void getAllFoodsBySupplierName() {
        var supplierName = "testName";
        var foodList = new ArrayList<Food>();
        var food = new Food();
        var supplier = new Supplier();
        supplier.name = "testName";
        food.supplier = supplier;

        foodList.add(food);

        when(foodRepository.findAll()).thenReturn(foodList);

        var actualFoodList = foodService.getAllFoodsBySupplierName(supplierName);
        Assertions.assertEquals(foodList, actualFoodList);
        verify(logger, times(1)).info("Successfully retrieved all the food types of the application");
        verify(logger, times(1)).info("Successfully retrieved food types for supplier with name " + supplierName);
    }
}
