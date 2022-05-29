package com.company.foodapp.controllers;

import com.company.foodapp.handlers.AuthHandler;
import com.company.foodapp.models.User;
import com.company.foodapp.repositories.UserRepository;
import com.kastkode.springsandwich.filter.annotation.Before;
import com.kastkode.springsandwich.filter.annotation.BeforeElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        ResponseEntity<List<User>> response;

        try {
            var users = userRepository.findAll();
            response = new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception exception) {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            exception.printStackTrace();
        }

        return response;
    }

    @GetMapping("{id}")
    @Before(@BeforeElement(value = AuthHandler.class, flags = {"admin"}))
    public ResponseEntity<User> getUser(@PathVariable Integer id) {
        ResponseEntity<User> response;

        try {
            var user = userRepository.findById(id).get();
            response = new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception exception) {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            exception.printStackTrace();
        }

        return response;
    }

    @PostMapping
    public ResponseEntity insertUser(@RequestBody User user) {
        ResponseEntity response;

        try {
            userRepository.save(user);
            response = new ResponseEntity("User with username: " + user.username + " was introduced", HttpStatus.OK);
        } catch (Exception exception) {
            response = new ResponseEntity("User could not be introduced", HttpStatus.NOT_ACCEPTABLE);
            exception.printStackTrace();
        }

        return response;
    }
}
