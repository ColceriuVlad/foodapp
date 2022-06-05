package com.company.foodapp.controllers;

import com.company.foodapp.handlers.AuthHandler;
import com.company.foodapp.models.User;
import com.company.foodapp.repositories.UserRepository;
import com.kastkode.springsandwich.filter.annotation.Before;
import com.kastkode.springsandwich.filter.annotation.BeforeElement;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserRepository userRepository;
    private Logger logger;

    @Autowired
    public UserController(UserRepository userRepository, Logger logger) {
        this.userRepository = userRepository;
        this.logger = logger;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        ResponseEntity<List<User>> response;

        var users = userRepository.findAll();

        if (!users.isEmpty()) {
            logger.info("Successfully retrieved users");
            response = new ResponseEntity<>(users, HttpStatus.OK);
        } else {
            logger.info("Could not retrieve users");
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return response;
    }

    @GetMapping("{id}")
    @Before(@BeforeElement(value = AuthHandler.class, flags = {"admin"}))
    public ResponseEntity<User> getUser(@PathVariable Integer id) {
        ResponseEntity<User> response;

        var user = userRepository.findById(id).get();

        if (user != null) {
            logger.info("Successfully retrieved user");
            response = new ResponseEntity<>(user, HttpStatus.OK);

        } else {
            logger.info("Could not retrieve user");
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return response;
    }

    @PostMapping
    public ResponseEntity insertUser(@RequestBody User user) {
        ResponseEntity response;

        try {
            userRepository.save(user);
            logger.info(String.format("User with id %s was successfully saved", user.id));
            response = new ResponseEntity(HttpStatus.OK);
        } catch (Exception exception) {
            logger.info("Could not save user");
            logger.info("\n" + exception.getMessage());
            response = new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
            exception.printStackTrace();
        }

        return response;
    }
}
