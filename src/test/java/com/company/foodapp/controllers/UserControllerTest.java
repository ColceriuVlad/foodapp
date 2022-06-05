package com.company.foodapp.controllers;

import com.company.foodapp.models.User;
import com.company.foodapp.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserRepository userRepository;
    private Logger logger;
    private UserController userController;

    public UserControllerTest() {
        userRepository = mock(UserRepository.class);
        logger = mock(Logger.class);
        userController = new UserController(userRepository, logger);
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
}
