package com.company.foodapp.models;

public class FormattedRequest {
    public String url;
    public String methodType;
    public String body;

    public FormattedRequest() {

    }

    public FormattedRequest(String url, String methodType, String body) {
        this.url = url;
        this.methodType = methodType;
        this.body = body;
    }
}
