package com.company.foodapp.controllers;

import com.company.foodapp.models.Supplier;
import com.company.foodapp.repositories.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {
    private SupplierRepository supplierRepository;
    private Logger logger;

    @Autowired
    public SupplierController(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
        this.logger = Logger.getLogger("SuppliersController");
    }

    @GetMapping
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        ResponseEntity<List<Supplier>> response;

        var suppliers = supplierRepository.findAll();

        if (suppliers.isEmpty()) {
            logger.info("Could not retrieve suppliers");
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else{
            logger.info("Successfully retrieved suppliers");
            response = new ResponseEntity<>(suppliers, HttpStatus.OK);
        }

        return response;
    }

    @PostMapping
    public ResponseEntity insertSupplier(@RequestBody Supplier supplier) {
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
