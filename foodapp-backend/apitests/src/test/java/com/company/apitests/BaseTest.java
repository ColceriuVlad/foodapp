package com.company.apitests;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class BaseTest {
    protected RequestSpecification requestSpecification;

    public BaseTest() {
        requestSpecification = getRequestSpecification();
    }

    private RequestSpecification getRequestSpecification() {
        var build = new RequestSpecBuilder();
        build.setBaseUri("http://localhost:8080/");
        build.setContentType(ContentType.JSON);
        build.addFilter(new RequestLoggingFilter());
        build.addFilter(new ResponseLoggingFilter());
        build.addFilter(new AllureRestAssured());

        requestSpecification = build.build();
        return requestSpecification;
    }
}
