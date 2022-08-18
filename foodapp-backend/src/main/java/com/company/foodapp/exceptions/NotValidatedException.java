package com.company.foodapp.exceptions;

public class NotValidatedException extends RuntimeException {
    public NotValidatedException(String message) {
        super(message);
    }
}
