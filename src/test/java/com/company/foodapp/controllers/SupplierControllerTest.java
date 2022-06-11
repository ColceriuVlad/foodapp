package com.company.foodapp.controllers;

import com.company.foodapp.models.Supplier;
import com.company.foodapp.repositories.SupplierRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SupplierControllerTest {
    private SupplierRepository supplierRepository;
    private Logger logger;
    private SupplierController supplierController;

    public SupplierControllerTest() {
        supplierRepository = mock(SupplierRepository.class);
        logger = mock(Logger.class);
        supplierController = new SupplierController(supplierRepository, logger);
    }

    @Test
    public void getAllSuppliers() {
        List<Supplier> suppliers = mock(List.class);

        when(supplierRepository.findAll()).thenReturn(suppliers);

        var response = supplierController.getAllSuppliers();
        var responseBody = response.getBody();
        var responseStatusCode = response.getStatusCode();

        Assertions.assertEquals(suppliers, responseBody);
        Assertions.assertEquals(HttpStatus.OK, responseStatusCode);
    }

    @Test
    public void couldNotGetAllSuppliers() {
        List<Supplier> suppliers = mock(List.class);

        when(supplierRepository.findAll()).thenReturn(suppliers);
        when(suppliers.isEmpty()).thenReturn(true);

        var response = supplierController.getAllSuppliers();
        var responseBody = response.getBody();
        var responseStatusCode = response.getStatusCode();

        Assertions.assertEquals(null, responseBody);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseStatusCode);
    }

    @Test
    public void getSupplier() {
        var name = "test";
        var supplier = mock(Supplier.class);
        var supplierOptional = Optional.of(supplier);

        when(supplierRepository.findByName(name)).thenReturn(supplierOptional);
        var response = supplierController.getSupplier(name);
        var responseBody = response.getBody();
        var responseStatusCode = response.getStatusCode();

        Assertions.assertEquals(supplier, responseBody);
        Assertions.assertEquals(HttpStatus.OK, responseStatusCode);
    }

    @Test
    public void couldNotGetSupplier() {
        var name = "test";

        when(supplierRepository.findByName(name)).thenThrow(Exception.class);

        var response = supplierController.getSupplier(name);
        var responseBody = response.getBody();
        var responseStatusCode = response.getStatusCode();

        Assertions.assertEquals(null, responseBody);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseStatusCode);
    }

    @Test
    public void insertSupplier() {
        var supplier = mock(Supplier.class);
        supplier.id = 1;
        supplier.name = "supplier";
        supplier.transportationCost = 30;
        supplier.transportationCurrency = "USD";
        supplier.discountRate = 0.2;

        var response = supplierController.insertSupplier(supplier);
        var responseBody = response.getBody();
        var responseStatusCode = response.getStatusCode();

        Assertions.assertEquals("Supplier was saved", responseBody);
        Assertions.assertEquals(HttpStatus.OK, responseStatusCode);
    }

    @Test
    public void couldNotInsertSupplier() {
        var supplier = mock(Supplier.class);

        var response = supplierController.insertSupplier(supplier);
        var responseBody = response.getBody();
        var responseStatusCode = response.getStatusCode();

        Assertions.assertEquals("Supplier was not saved", responseBody);
        Assertions.assertEquals(HttpStatus.NOT_ACCEPTABLE, responseStatusCode);
    }
}
