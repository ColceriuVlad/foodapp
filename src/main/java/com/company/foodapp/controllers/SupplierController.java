package com.company.foodapp.controllers;

import com.company.foodapp.handlers.AuthHandler;
import com.company.foodapp.models.Supplier;
import com.company.foodapp.repositories.SupplierRepository;
import com.kastkode.springsandwich.filter.annotation.Before;
import com.kastkode.springsandwich.filter.annotation.BeforeElement;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {
    private SupplierRepository supplierRepository;
    private Logger logger;

    @Autowired
    public SupplierController(SupplierRepository supplierRepository, Logger logger) {
        this.supplierRepository = supplierRepository;
        this.logger = logger;
    }

    @GetMapping
    @Before(@BeforeElement(value = AuthHandler.class, flags = {"admin"}))
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        ResponseEntity<List<Supplier>> response;

        var suppliers = supplierRepository.findAll();

        if (!suppliers.isEmpty()) {
            logger.info("Successfully retrieved suppliers");
            response = new ResponseEntity<>(suppliers, HttpStatus.OK);

        } else {
            logger.info("Could not retrieve suppliers");
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return response;
    }

    @Before(@BeforeElement(value = AuthHandler.class, flags = {"admin"}))
    @GetMapping("{name}")
    public ResponseEntity<Supplier> getSupplier(@PathVariable String name) {
        ResponseEntity<Supplier> response;

        try {
            var supplier = supplierRepository.findByName(name).get();

            logger.info("Supplier with id " + supplier.id + " was retrieved successfully");
            response = new ResponseEntity<>(supplier, HttpStatus.OK);
        } catch (Exception e) {
            logger.info("Could not retrieve supplier with id " + name);
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return response;
    }

    @Before(@BeforeElement(value = AuthHandler.class, flags = {"admin"}))
    @PostMapping
    public ResponseEntity<String> insertSupplier(@RequestBody Supplier supplier) {
        ResponseEntity response = null;

        if (supplier.discountRate != null && supplier.transportationCost != null && supplier.transportationCurrency != null) {
            supplierRepository.save(supplier);

            logger.info("Supplier was saved");
            response = new ResponseEntity("Supplier was saved", HttpStatus.OK);
        } else {
            logger.info("Supplier was not saved");
            response = new ResponseEntity("Supplier was not saved", HttpStatus.NOT_ACCEPTABLE);
        }

        return response;
    }
}
