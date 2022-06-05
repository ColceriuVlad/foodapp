package com.company.foodapp.controllers;

import com.company.foodapp.models.Supplier;
import com.company.foodapp.repositories.SupplierRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

import java.util.List;

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
}
