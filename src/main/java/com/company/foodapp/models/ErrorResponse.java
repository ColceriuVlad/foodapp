package com.company.foodapp.models;

public class ErrorResponse {
    public Integer status;
    public String message;

    public ErrorResponse() {

    }

    public ErrorResponse(Integer status, String message) {
        this.status = status;
        this.message = message;
    }
}
