package com.company.foodapp.controllers;

import com.company.foodapp.mappers.ClaimsToAuthenticationDetailsMapper;
import com.company.foodapp.models.*;
import com.company.foodapp.repositories.UserRepository;
import com.company.foodapp.services.AuthorizationService;
import com.company.foodapp.services.EmailService;
import com.company.foodapp.utils.CookieUtils;
import com.company.foodapp.utils.DateUtils;
import com.company.foodapp.utils.JwtUtils;
import com.company.foodapp.utils.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/authorization")
public class AuthorizationController {
    private JwtUtils jwtUtils;
    private CookieUtils cookieUtils;
    private ClaimsToAuthenticationDetailsMapper claimsToAuthenticationDetailsMapper;
    private UserRepository userRepository;
    private Environment environment;
    private Logger logger;
    private EmailService emailService;
    private StringUtils stringUtils;
    private AuthorizationService authorizationService;
    private DateUtils dateUtils;

    @Autowired
    public AuthorizationController(JwtUtils jwtUtils, CookieUtils cookieUtils, ClaimsToAuthenticationDetailsMapper claimsToAuthenticationDetailsMapper, UserRepository userRepository, Environment environment, Logger logger, EmailService emailService, StringUtils stringUtils, AuthorizationService authorizationService, DateUtils dateUtils) {
        this.jwtUtils = jwtUtils;
        this.cookieUtils = cookieUtils;
        this.claimsToAuthenticationDetailsMapper = claimsToAuthenticationDetailsMapper;
        this.userRepository = userRepository;
        this.environment = environment;
        this.logger = logger;
        this.emailService = emailService;
        this.stringUtils = stringUtils;
        this.authorizationService = authorizationService;
        this.dateUtils = dateUtils;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody User user, HttpServletResponse httpServletResponse) {
        var usersFromDb = userRepository.findAll();

        for (var userFromDb : usersFromDb) {
            if (user.username.equals(userFromDb.username) && user.password.equals(userFromDb.password)) {
                if (userFromDb.activated == false) {
                    logger.info("User is not validated, please perform the email validation");

                    return new ResponseEntity(HttpStatus.UNAUTHORIZED);
                }
                var authenticationDetails = new AuthenticationDetails(
                        userFromDb.username,
                        userFromDb.role,
                        userFromDb.email,
                        Long.parseLong(environment.getProperty("JWT_AUTHENTICATION_TOKEN_DURATION")));

                var jwtToken = jwtUtils.createJWT(authenticationDetails);
                logger.info("User has logged in successfully");
                cookieUtils.createCookie("token", jwtToken, httpServletResponse);
                return new ResponseEntity(HttpStatus.OK);
            }
        }

        logger.info("User was not authenticated");
        return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/getCurrentAuthenticationDetails")
    public ResponseEntity getCurrentAuthenticationDetails(HttpServletRequest httpServletRequest) {
        var authenticationDetails = authorizationService.getCurrentAuthenticationDetails(httpServletRequest);

        return new ResponseEntity(authenticationDetails, HttpStatus.OK);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity resetPassword(@RequestBody User user, HttpServletResponse httpServletResponse) {
        var usersFromDb = userRepository.findAll();

        if (usersFromDb.isEmpty()) {
            logger.info("Could not find any user in the application");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            logger.info("Successfully retrieved users from the application");
        }

        for (var userFromDb : usersFromDb) {
            if (userFromDb.email.equals(user.email) && userFromDb.username.equals(user.username)) {
                logger.info("Successfully found user " + user.username);

                var validationCode = stringUtils.generateValidationCode();

                var forgotPasswordDetails = new ForgotPasswordDetails(
                        user.username,
                        user.email,
                        Long.parseLong(environment.getProperty("JWT_FORGOT_PASSWORD_TOKEN_DURATION")),
                        validationCode,
                        userFromDb.id);

                var forgotPasswordToken = jwtUtils.createJWT(forgotPasswordDetails);
                logger.info("Successfully created forgot password token");
                cookieUtils.createCookie("forgotPassword", forgotPasswordToken, httpServletResponse);

                var email = new Email(user.email, "Forgot password", "Your password reset code is " + validationCode);

                var couldSendEmail = emailService.sendMessage(email);

                if (couldSendEmail) {
                    logger.info("Successfully sent password reset validation code to " + email.to);
                } else {
                    logger.info("Could not send email to " + email.to);

                    return new ResponseEntity(HttpStatus.BAD_REQUEST);
                }

                return new ResponseEntity(HttpStatus.OK);
            }
        }

        logger.info("Could not find the correspondent user in the database");

        return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }


    @PostMapping("logout")
    public ResponseEntity logOut(HttpServletResponse httpServletResponse) {
        var couldLogOut = authorizationService.logOut(httpServletResponse);

        if (couldLogOut) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}