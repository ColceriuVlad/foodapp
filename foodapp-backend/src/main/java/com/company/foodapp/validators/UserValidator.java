package com.company.foodapp.validators;

import com.company.foodapp.models.User;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {
    private Logger logger;

    @Autowired
    public UserValidator(Logger logger) {
        this.logger = logger;
    }

    public User getValidatedUserDetails(User user) {
        if (user.username != null && user.password != null && user.role != null && user.validationCode != null && user.email != null) {
            logger.info("User information was validated");

            return user;
        } else {
            logger.info("User information was not validated");
            return null;
        }
    }
}
