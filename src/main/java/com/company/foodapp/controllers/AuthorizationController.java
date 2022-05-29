package com.company.foodapp.controllers;

import com.company.foodapp.core.CustomRequest;
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

@RestController
@RequestMapping("/authorization")
public class AuthorizationController {
    private JwtUtils jwtUtils;
    private JacksonUtils jacksonUtils;

    @Autowired
    public AuthorizationController(JwtUtils jwtUtils, JacksonUtils jacksonUtils) {
        this.jwtUtils = jwtUtils;
        this.jacksonUtils = jacksonUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        var usersFromDbString = new CustomRequest("http://localhost:8080/users").getResponse();
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
