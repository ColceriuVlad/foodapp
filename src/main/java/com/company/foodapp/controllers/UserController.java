package com.company.foodapp.controllers;

import com.company.foodapp.handlers.AuthHandler;
import com.company.foodapp.models.Email;
import com.company.foodapp.models.ErrorResponse;
import com.company.foodapp.models.User;
import com.company.foodapp.repositories.UserRepository;
import com.company.foodapp.services.EmailService;
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
    private EmailService emailService;

    @Autowired
    public UserController(UserRepository userRepository, Logger logger, UserValidator userValidator, StringUtils stringUtils, EmailService emailService) {
        this.userRepository = userRepository;
        this.logger = logger;
        this.userValidator = userValidator;
        this.stringUtils = stringUtils;
        this.emailService = emailService;
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
            logger.info("User was validated and saved in the database");

            var email = new Email(user.email, "Validation code confirmation", "Your validation code is " + validationCode);
            var couldSendEmail = emailService.sendMessage(email);

            if (couldSendEmail == true) {
                logger.info("Validation email was sent successfully to user " + user.username);
                return new ResponseEntity(HttpStatus.OK);
            } else {
                var errorMessage = "Could not send validation email to user " + user.username;
                logger.info(errorMessage);

                var errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
                return new ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST);
            }
        } else {
            var errorMessage = "User was not validated";
            logger.info(errorMessage);

            var errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
            return new ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST);
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
            var errorMessage = "Could not find user with validation code " + validationCode;
            logger.info(errorMessage);

            var errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), errorMessage);
            return new ResponseEntity(errorResponse, HttpStatus.NOT_FOUND);
        }
    }
}
