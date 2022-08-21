package com.company.foodapp.services;

import com.company.foodapp.exceptions.EntityNotFoundException;
import com.company.foodapp.models.Supplier;
import com.company.foodapp.repositories.SupplierRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {
    private SupplierRepository supplierRepository;
    private Logger logger;

    @Autowired
    public SupplierService(SupplierRepository supplierRepository, Logger logger) {
        this.supplierRepository = supplierRepository;
        this.logger = logger;
    }

    public List<Supplier> getAllSuppliers() {
        var suppliers = supplierRepository.findAll();

        if (!suppliers.isEmpty()) {
            logger.info("Successfully retrieved suppliers");

            return suppliers;
        } else {
            throw new EntityNotFoundException("Could not retrieve any supplier from the application");
        }
    }

    public Supplier getSupplier(String supplierName) {
        var supplier = supplierRepository
                .findByName(supplierName)
                .orElseThrow(() -> new EntityNotFoundException("Could not retrieve supplier " + supplierName + " from the application"));

        logger.info("Supplier with id " + supplier.id + " was retrieved successfully");

        return supplier;
    }

    public void insertSupplier(Supplier supplier) {
        supplierRepository.save(supplier);

        logger.info("Supplier was saved");
    }
}
