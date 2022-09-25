package com.company.foodapp.controllers;

import com.company.foodapp.exceptions.InvalidOperationException;
import com.company.foodapp.exceptions.NotValidatedException;
import com.company.foodapp.handlers.AuthHandler;
import com.company.foodapp.models.Email;
import com.company.foodapp.models.User;
import com.company.foodapp.models.UserPasswordHolder;
import com.company.foodapp.repositories.UserRepository;
import com.company.foodapp.services.AuthorizationService;
import com.company.foodapp.services.CartService;
import com.company.foodapp.services.EmailService;
import com.company.foodapp.services.UserService;
import com.company.foodapp.utils.CookieUtils;
import com.company.foodapp.utils.JwtUtils;
import com.company.foodapp.utils.StringUtils;
import com.company.foodapp.validators.UserValidator;
import com.kastkode.springsandwich.filter.annotation.Before;
import com.kastkode.springsandwich.filter.annotation.BeforeElement;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;
    private UserRepository userRepository;
    private Logger logger;
    private UserValidator userValidator;
    private StringUtils stringUtils;
    private EmailService emailService;
    private CookieUtils cookieUtils;
    private JwtUtils jwtUtils;
    private AuthorizationService authorizationService;
    private CartService cartService;

    @Autowired
    public UserController(UserService userService, UserRepository userRepository, Logger logger, UserValidator userValidator, StringUtils stringUtils, EmailService emailService, CookieUtils cookieUtils, JwtUtils jwtUtils, AuthorizationService authorizationService, CartService cartService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.logger = logger;
        this.userValidator = userValidator;
        this.stringUtils = stringUtils;
        this.emailService = emailService;
        this.cookieUtils = cookieUtils;
        this.jwtUtils = jwtUtils;
        this.authorizationService = authorizationService;
        this.cartService = cartService;
    }

    @GetMapping
    @Before(@BeforeElement(value = AuthHandler.class, flags = {"admin"}))
    public ResponseEntity<List<User>> getAllUsers() {
        var users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("{id}")
    @Before(@BeforeElement(value = AuthHandler.class, flags = {"admin"}))
    public ResponseEntity<User> getUser(@PathVariable Integer id) {
        ResponseEntity<User> response;

        try {
            var user = userRepository.findById(id).get();
            logger.info("Successfully retrieved user with id " + id);

            response = new ResponseEntity<>(user, HttpStatus.OK);
        } catch (NoSuchElementException noSuchElementException) {
            logger.info("Could not retrieve user with id " + id);

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
                logger.info("Validation email was sent successfully to  " + email.to);
                return new ResponseEntity(HttpStatus.OK);
            } else {
                var errorMessage = "Could not send validation email to user " + email.to;
                throw new InvalidOperationException(errorMessage);
            }
        } else {
            throw new NotValidatedException("User validations have failed, please respect the corresponding user format");
        }
    }

    @PutMapping("validation/{validationCode}")
    public ResponseEntity validateUser(@PathVariable String validationCode) {
        try {
            var user = userRepository.findByValidationCode(validationCode).get();
            logger.info("Successfully found user with validation code" + validationCode);
            user.validationCode = null;
            user.activated = true;
            userRepository.save(user);

            var couldAddCartToUser = cartService.addCartToUser(user);

            if (couldAddCartToUser == true) {
                logger.info("Successfully added cart to user after validation was performed for user " + user.username);
                return new ResponseEntity(HttpStatus.OK);
            } else {
                logger.info("Could not add cart to user after validation was performed for user " + user.username);
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception exception) {
            var errorMessage = "Could not find user with validation code " + validationCode;
            throw new EntityNotFoundException(errorMessage);
        }
    }

    @PutMapping("updatePassword/{validationCode}")
    public ResponseEntity updatePassword(@PathVariable String validationCode, @RequestBody UserPasswordHolder userPasswordHolder, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        var validationToken = cookieUtils.getCookieValue("forgotPassword", httpServletRequest);

        if (validationToken != null) {
            logger.info("Successfully retrieved password reset token");
        } else {
            logger.info("Could not retrieve password reset token");

            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        var validationTokenClaims = jwtUtils.decodeJWT(validationToken);

        if (validationTokenClaims != null) {
            logger.info("Successfully decoded password reset token");

        } else {
            logger.info("Could not decode the password reset token");

            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        var validationCodeFromClaims = validationTokenClaims.get("validationCode");
        var userId = (Integer) validationTokenClaims.get("id");

        if (validationCodeFromClaims.equals(validationCode)) {
            try {
                var user = userRepository.findById(userId).get();
                logger.info("Successfully retrieved user with id " + userId);

                user.password = userPasswordHolder.password;
                userRepository.save(user);
                logger.info("Successfully updated password for user with id " + userId);

                authorizationService.logOut(httpServletResponse);
                cookieUtils.deleteCookie("forgotPassword", httpServletResponse);

                return new ResponseEntity(HttpStatus.OK);
            } catch (NoSuchElementException noSuchElementException) {
                logger.info("Could not retrieve user with id " + userId);

                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
        } else {
            logger.info("Validation code is not correct");

            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
    }
}
