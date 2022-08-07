package com.company.foodapp.exceptions;

public class NotAuthorizedException extends RuntimeException {
    public NotAuthorizedException(String exceptionMessage){
        super(exceptionMessage);
    }
}
