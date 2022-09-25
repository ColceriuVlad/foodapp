package com.company.foodapp.exceptions;

public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String exceptionMessage) {
        super(exceptionMessage);
    }
}
