package com.company.foodapp.models;

public class ResponseObject {
    public Integer statusCode;
    public String message;
    public String timeStamp;

    public ResponseObject(Integer statusCode, String message, String timeStamp) {
        this.statusCode = statusCode;
        this.message = message;
        this.timeStamp = timeStamp;
    }
}
