package com.company.foodapp.controllers;

import com.company.foodapp.core.PropertiesFileReader;
import com.company.foodapp.dto.AuthenticationDetails;
import com.company.foodapp.handlers.AuthHandler;
import com.company.foodapp.mappers.ClaimsToAuthenticationDetailsMapper;
import com.company.foodapp.models.User;
import com.company.foodapp.repositories.UserRepository;
import com.company.foodapp.utils.CookieUtils;
import com.company.foodapp.utils.JwtUtils;
import com.kastkode.springsandwich.filter.annotation.Before;
import com.kastkode.springsandwich.filter.annotation.BeforeElement;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
    private PropertiesFileReader propertiesFileReader;
    private Logger logger;

    @Autowired
    public AuthorizationController(JwtUtils jwtUtils, CookieUtils cookieUtils, ClaimsToAuthenticationDetailsMapper claimsToAuthenticationDetailsMapper, UserRepository userRepository, PropertiesFileReader propertiesFileReader, Logger logger) {
        this.jwtUtils = jwtUtils;
        this.cookieUtils = cookieUtils;
        this.claimsToAuthenticationDetailsMapper = claimsToAuthenticationDetailsMapper;
        this.userRepository = userRepository;
        this.propertiesFileReader = propertiesFileReader;
        this.logger = logger;
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
                        Long.parseLong(propertiesFileReader.getProperty("JWT_DURATION")));

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
    @Before(@BeforeElement(value = AuthHandler.class, flags = {"admin"}))
    public AuthenticationDetails getCurrentAuthenticationDetails(HttpServletRequest httpServletRequest) {
        var authenticationToken = cookieUtils.getCookieValue("token", httpServletRequest);

        var claims = jwtUtils.decodeJWT(authenticationToken);
        var authenticationDetails = claimsToAuthenticationDetailsMapper.map(claims);

        return authenticationDetails;
    }
}