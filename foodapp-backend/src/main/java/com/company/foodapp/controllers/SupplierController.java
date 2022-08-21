package com.company.foodapp.controllers;

import com.company.foodapp.handlers.AuthHandler;
import com.company.foodapp.models.Supplier;
import com.company.foodapp.services.SupplierService;
import com.kastkode.springsandwich.filter.annotation.Before;
import com.kastkode.springsandwich.filter.annotation.BeforeElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {
    private SupplierService supplierService;

    @Autowired
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @Before(@BeforeElement(value = AuthHandler.class, flags = {"admin"}))
    @GetMapping
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        var suppliers = supplierService.getAllSuppliers();
        return new ResponseEntity<>(suppliers, HttpStatus.OK);
    }

    @Before(@BeforeElement(value = AuthHandler.class, flags = {"admin"}))
    @GetMapping("{supplierName}")
    public ResponseEntity<Supplier> getSupplier(@PathVariable String supplierName) {
        var supplier = supplierService.getSupplier(supplierName);
        return new ResponseEntity<>(supplier, HttpStatus.OK);
    }

    @Before(@BeforeElement(value = AuthHandler.class, flags = {"admin"}))
    @PostMapping
    public ResponseEntity<String> insertSupplier(@RequestBody Supplier supplier) {
        supplierService.insertSupplier(supplier);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
