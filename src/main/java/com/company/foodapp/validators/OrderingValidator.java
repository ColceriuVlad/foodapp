package com.company.foodapp.validators;

import com.company.foodapp.models.Ordering;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderingValidator {
    private Logger logger;

    @Autowired
    public OrderingValidator(Logger logger) {
        this.logger = logger;
    }

    public Ordering getValidatedOrdering(Ordering ordering) {
        if (ordering.totalPrice == null) {
            throw new NullPointerException("The total price of ordering was not found");
        } else if (ordering.timeStamp == null) {
            throw new NullPointerException("The timestamp of the ordering was not found");
        } else {
            return ordering;
        }
    }
}
