package com.company.foodapp.services;

import com.company.foodapp.exceptions.EntityNotFoundException;
import com.company.foodapp.models.User;
import com.company.foodapp.repositories.UserRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private UserRepository userRepository;
    private Logger logger;

    @Autowired
    public UserService(UserRepository userRepository, Logger logger) {
        this.userRepository = userRepository;
        this.logger = logger;
    }

    public List<User> getAllUsers() {
        var users = userRepository.findAll();

        if (!users.isEmpty()) {
            logger.info("Successfully retrieved users");
            return users;
        } else {
            throw new EntityNotFoundException("Could not retrieve any user from the application");
        }
    }
}
