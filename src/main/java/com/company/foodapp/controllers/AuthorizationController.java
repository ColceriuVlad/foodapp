package com.company.foodapp.controllers;

import com.company.foodapp.core.CustomRequest;
import com.company.foodapp.core.PropertiesFileReader;
import com.company.foodapp.models.User;
import com.company.foodapp.utils.JacksonUtils;
import com.company.foodapp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;

@RestController
@RequestMapping("/authorization")
public class AuthorizationController {
    private JwtUtils jwtUtils;
    private JacksonUtils jacksonUtils;
    private PropertiesFileReader propertiesFileReader;
    private Properties properties;

    @Autowired
    public AuthorizationController(JwtUtils jwtUtils, JacksonUtils jacksonUtils, PropertiesFileReader propertiesFileReader) {
        this.jwtUtils = jwtUtils;
        this.jacksonUtils = jacksonUtils;
        this.propertiesFileReader = propertiesFileReader;
        this.properties = this.propertiesFileReader.getProperties("application.properties");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        var usersFromDbString = new CustomRequest(properties.getProperty("ENDPOINT_USERS")).getResponse();
        var usersFromDb = jacksonUtils.parseJsonAsUsersList(usersFromDbString);
        String jwt = null;
        ResponseEntity<String> response;

        for (var userFromDb : usersFromDb) {
            if (user.username.equals(userFromDb.username) && user.password.equals(userFromDb.password)) {
                jwt = jwtUtils.createJWT(user.username, String.valueOf(userFromDb.id), userFromDb.role, 1000000);
            }
        }

        if (jwt != null) {
            response = new ResponseEntity<>(jwt, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return response;
    }
}
