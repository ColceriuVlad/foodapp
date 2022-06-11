package com.company.foodapp.controllers;

import com.company.foodapp.models.Food;
import com.company.foodapp.models.Supplier;
import com.company.foodapp.repositories.FoodRepository;
import com.company.foodapp.repositories.SupplierRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FoodControllerTest {
    private FoodRepository foodRepository;
    private SupplierRepository supplierRepository;
    private Logger logger;
    private FoodController foodController;

    public FoodControllerTest() {
        foodRepository = mock(FoodRepository.class);
        supplierRepository = mock(SupplierRepository.class);
        logger = mock(Logger.class);
        foodController = new FoodController(foodRepository, supplierRepository, logger);
    }

    @Test
    public void addFoodToSupplier() {
        var supplierName = "supplier";
        var food = mock(Food.class);

        var supplier = mock(Supplier.class);
        var supplierOptional = Optional.of(supplier);

        when(supplierRepository.findByName(supplierName)).thenReturn(supplierOptional);

        var response = foodController.addFoodToSupplier(supplierName, food);
        var responseStatusCode = response.getStatusCode();

        Assertions.assertEquals(HttpStatus.OK, responseStatusCode);
    }

    @Test
    public void couldNotAddFoodToSupplier() {
        var supplierName = "supplier";
        var food = mock(Food.class);

        when(supplierRepository.findByName(supplierName)).thenThrow(Exception.class);

        var response = foodController.addFoodToSupplier(supplierName, food);
        var responseStatusCode = response.getStatusCode();

        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseStatusCode);
    }
}
