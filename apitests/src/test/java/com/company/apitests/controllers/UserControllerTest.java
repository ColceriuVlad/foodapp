package com.company.apitests.controllers;

import com.company.apitests.BaseTest;
import com.company.apitests.models.User;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;


public class UserControllerTest extends BaseTest {
    @Test
    public void testInsertUser() {
        var user = new User("vladee", "vlad123ewe", "admin", "vlad.colceriu@yahoo.com");

        var request = given();
        var completeRequest = request
                .spec(requestSpecification)
                .body(user)
                .when();

        var response = completeRequest
                .post("/users")
                .thenReturn();

        var responseStatusCode = response.statusCode();
        Assertions.assertEquals(responseStatusCode, HttpStatus.SC_OK);

    }
}
