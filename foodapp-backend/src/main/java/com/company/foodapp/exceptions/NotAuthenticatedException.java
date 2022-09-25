package com.company.foodapp.exceptions;

public class NotAuthenticatedException extends RuntimeException {
    public NotAuthenticatedException(String exceptionMessage) {
        super(exceptionMessage);
    }
}
