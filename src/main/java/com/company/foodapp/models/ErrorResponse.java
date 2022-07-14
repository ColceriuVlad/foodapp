package com.company.foodapp.models;

public class ErrorResponse {
    public Integer status;
    public String message;
    public String timeStamp;

    public ErrorResponse() {

    }

    public ErrorResponse(Integer status, String message, String timeStamp) {
        this.status = status;
        this.message = message;
        this.timeStamp = timeStamp;
    }
}
