package com.company.foodapp.controllers;

import com.company.foodapp.core.PropertiesFileReader;
import com.company.foodapp.dto.UserDetails;
import com.company.foodapp.handlers.AuthHandler;
import com.company.foodapp.mappers.ClaimsToUserDetailsMapper;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/authorization")
public class AuthorizationController {
    private JwtUtils jwtUtils;
    private CookieUtils cookieUtils;
    private ClaimsToUserDetailsMapper claimsToUserDetailsMapper;
    private UserRepository userRepository;
    private PropertiesFileReader propertiesFileReader;
    private Logger logger;

    @Autowired
    public AuthorizationController(JwtUtils jwtUtils, CookieUtils cookieUtils, ClaimsToUserDetailsMapper claimsToUserDetailsMapper, UserRepository userRepository, PropertiesFileReader propertiesFileReader, Logger logger) {
        this.jwtUtils = jwtUtils;
        this.cookieUtils = cookieUtils;
        this.claimsToUserDetailsMapper = claimsToUserDetailsMapper;
        this.userRepository = userRepository;
        this.propertiesFileReader = propertiesFileReader;
        this.logger = logger;
    }


    @PostMapping("/login")
    public ResponseEntity login(@RequestBody User user, HttpServletResponse httpServletResponse) {
        var usersFromDb = userRepository.findAll();
        ResponseEntity response = null;
        String jwtToken = null;

        for (var userFromDb : usersFromDb) {
            if (user.username.equals(userFromDb.username) && user.password.equals(userFromDb.password)) {
                var userDetails = new UserDetails(
                        userFromDb.username,
                        userFromDb.role,
                        Long.parseLong(propertiesFileReader.getProperty("JWT_DURATION")));

                jwtToken = jwtUtils.createJWT(userDetails);
            }
        }

        if (jwtToken != null) {
            logger.info("User has logged in successfully");

            cookieUtils.createCookie("token", jwtToken, httpServletResponse);

            response = new ResponseEntity(HttpStatus.OK);
        } else {
            logger.info("User was not authenticated");
            response = new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        return response;
    }


    @PostMapping("/getCurrentUserDetails")
    @Before(@BeforeElement(value = AuthHandler.class, flags = {"admin"}))
    public UserDetails getCurrentUserDetails(HttpServletRequest httpServletRequest) {
        var authenticationToken = cookieUtils.getCookieValue("token", httpServletRequest);

        var claims = jwtUtils.decodeJWT(authenticationToken);
        var userDetails = claimsToUserDetailsMapper.map(claims);

        return userDetails;
    }
}