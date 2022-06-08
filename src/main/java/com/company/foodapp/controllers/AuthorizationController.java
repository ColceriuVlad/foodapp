package com.company.foodapp.controllers;

import com.company.foodapp.core.PropertiesFileReader;
import com.company.foodapp.dto.JwtDetails;
import com.company.foodapp.models.User;
import com.company.foodapp.repositories.UserRepository;
import com.company.foodapp.utils.JwtUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authorization")
public class AuthorizationController {
    private JwtUtils jwtUtils;
    private UserRepository userRepository;
    private PropertiesFileReader propertiesFileReader;
    private Logger logger;

    @Autowired
    public AuthorizationController(JwtUtils jwtUtils, UserRepository userRepository, PropertiesFileReader propertiesFileReader, Logger logger) {
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.propertiesFileReader = propertiesFileReader;
        this.logger = logger;
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        var usersFromDb = userRepository.findAll();
        ResponseEntity response = null;
        String jwtToken = null;

        for (var userFromDb : usersFromDb) {
            if (user.username.equals(userFromDb.username) && user.password.equals(userFromDb.password)) {
                var jwtDetails = new JwtDetails(
                        userFromDb.username,
                        userFromDb.password,
                        userFromDb.role,
                        Long.parseLong(propertiesFileReader.getProperty("JWT_DURATION")));

                jwtToken = jwtUtils.createJWT(jwtDetails.id, jwtDetails.subject, jwtDetails.role, jwtDetails.duration);
            }
        }

        if (jwtToken != null) {
            logger.info("User has logged in successfully");
            response = new ResponseEntity(jwtToken, HttpStatus.OK);
        } else {
            logger.info("User was not authenticated");
            response = new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        return response;
    }
}