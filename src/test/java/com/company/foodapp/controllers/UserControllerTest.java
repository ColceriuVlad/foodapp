package com.company.foodapp.controllers;

import com.company.foodapp.models.User;
import com.company.foodapp.repositories.UserRepository;
import com.company.foodapp.services.HttpService;
import com.company.foodapp.utils.StringUtils;
import com.company.foodapp.validators.UserValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserRepository userRepository;
    private Logger logger;
    private UserController userController;
    private UserValidator userValidator;
    private StringUtils stringUtils;
    private HttpService httpService;

    public UserControllerTest() {
        userRepository = mock(UserRepository.class);
        logger = mock(Logger.class);
        userValidator = mock(UserValidator.class);
        stringUtils = mock(StringUtils.class);
        httpService = mock(HttpService.class);
        userController = new UserController(userRepository, logger, userValidator, stringUtils, httpService);
    }

    @Test
    public void getAllUsers() {
        List<User> expectedUsers = mock(List.class);
        var expectedStatusCode = HttpStatus.OK;

        when(userRepository.findAll()).thenReturn(expectedUsers);

        var response = userController.getAllUsers();
        Assertions.assertEquals(expectedUsers, response.getBody());
        Assertions.assertEquals(expectedStatusCode, response.getStatusCode());
    }

    @Test
    public void getAllUsersReturnsEmpty() {
        List<User> expectedUsers = mock(List.class);

        when(userRepository.findAll()).thenReturn(expectedUsers);
        when(expectedUsers.isEmpty()).thenReturn(true);

        var response = userController.getAllUsers();
        var responseBody = response.getBody();
        var responseStatusCode = response.getStatusCode();

        Assertions.assertEquals(null, responseBody);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseStatusCode);
    }

    @Test
    public void getUser() {
        var userId = 1;
        var user = mock(User.class);
        var optional = Optional.of(user);

        when(userRepository.findById(userId)).thenReturn(optional);

        var response = userController.getUser(userId);
        var responseBody = response.getBody();
        var responseStatusCode = response.getStatusCode();

        Assertions.assertEquals(user, responseBody);
        Assertions.assertEquals(HttpStatus.OK, responseStatusCode);
    }

    @Test
    public void getUserReturnsEmpty() {
        Integer userId = 1;

        when(userRepository.findById(userId)).thenThrow(NoSuchElementException.class);

        var response = userController.getUser(userId);
        var responseBody = response.getBody();
        var responseStatusCode = response.getStatusCode();

        Assertions.assertEquals(null, responseBody);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseStatusCode);
    }

    @Test
    public void insertUser() {
        var user = mock(User.class);

        user.username = "username";
        user.password = "password";
        user.role = "role";

        var response = userController.insertUser(user);
        var responseStatusCode = response.getStatusCode();

        Assertions.assertEquals(HttpStatus.OK, responseStatusCode);
    }

    @Test
    public void insertUserWithNullProperties() {
        var user = mock(User.class);

        var response = userController.insertUser(user);
        var responseStatusCode = response.getStatusCode();

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseStatusCode);
    }
}
