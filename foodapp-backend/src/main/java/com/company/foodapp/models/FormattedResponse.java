package com.company.foodapp.models;

public class FormattedResponse {
    public Integer statusCode;
    public String body;

    public FormattedResponse(Integer statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }
}
