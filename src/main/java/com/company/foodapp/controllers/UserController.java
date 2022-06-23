package com.company.foodapp.controllers;

import com.company.foodapp.handlers.AuthHandler;
import com.company.foodapp.models.Email;
import com.company.foodapp.models.User;
import com.company.foodapp.repositories.UserRepository;
import com.company.foodapp.services.HttpService;
import com.company.foodapp.utils.StringUtils;
import com.company.foodapp.validators.UserValidator;
import com.kastkode.springsandwich.filter.annotation.Before;
import com.kastkode.springsandwich.filter.annotation.BeforeElement;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserRepository userRepository;
    private Logger logger;
    private UserValidator userValidator;
    private StringUtils stringUtils;
    private HttpService httpService;

    @Autowired
    public UserController(UserRepository userRepository, Logger logger, UserValidator userValidator, StringUtils stringUtils, HttpService httpService) {
        this.userRepository = userRepository;
        this.logger = logger;
        this.userValidator = userValidator;
        this.stringUtils = stringUtils;
        this.httpService = httpService;
    }

    @GetMapping
    @Before(@BeforeElement(value = AuthHandler.class, flags = {"admin"}))
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

        try {
            var user = userRepository.findById(id).get();
            logger.info("Successfully logged in");

            response = new ResponseEntity<>(user, HttpStatus.OK);
        } catch (NoSuchElementException noSuchElementException) {
            logger.info("User could not be authenticated");

            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return response;
    }

    @PostMapping
    public ResponseEntity insertUser(@RequestBody User user) {
        var validationCode = stringUtils.generateValidationCode();
        user.validationCode = validationCode;
        user.activated = false;
        var validatedUser = userValidator.getValidatedUserDetails(user);

        if (validatedUser != null) {
            userRepository.save(user);
            logger.info("User was saved in the database");

            var email = new Email(user.email, "Validation code confirmation", "Your validation code is " + validationCode);
            var sendEmailStatus = httpService.sendEmailAndGetStatus(email);

            if (sendEmailStatus == HttpStatus.OK) {
                logger.info("Registration confirmation email was sent");

                return new ResponseEntity(HttpStatus.OK);
            } else {
                logger.info("Could not send registration confirmation email");

                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }

        } else {
            logger.info("User was not saved in the database");

            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("validation/{validationCode}")
    public ResponseEntity validateUser(@PathVariable String validationCode) {
        User user = null;

        try {
            user = userRepository.findByValidationCode(validationCode).get();
            logger.info("Successfully found user with validation code" + validationCode);
            user.validationCode = null;
            user.activated = true;
            userRepository.save(user);

            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception exception) {
            logger.info("Could not find user with validation code " + validationCode);

            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
}
