package com.company.foodapp.controllers;

import com.company.foodapp.models.Food;
import com.company.foodapp.repositories.FoodRepository;
import com.company.foodapp.repositories.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.logging.Logger;

@RestController
public class FoodController {
    private FoodRepository foodRepository;
    private SupplierRepository supplierRepository;
    private Logger logger;

    @Autowired
    public FoodController(FoodRepository foodRepository, SupplierRepository supplierRepository) {
        this.foodRepository = foodRepository;
        this.supplierRepository = supplierRepository;
        this.logger = Logger.getLogger("FoodController");
    }


    @PostMapping("suppliers/{supplierName}")
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
}
