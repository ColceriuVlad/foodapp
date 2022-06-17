package com.company.foodapp.validators;

import com.company.foodapp.dto.MessageDetails;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageDetailsValidator {
    private Logger logger;

    @Autowired
    public MessageDetailsValidator(Logger logger) {
        this.logger = logger;
    }

    public MessageDetails getValidatedMessageDetails(MessageDetails messageDetails) {
        if (messageDetails.name != null && messageDetails.description != null) {
            logger.info("Message details were validated");

            return messageDetails;
        } else {
            logger.info("Message details were not validated");
            return null;
        }
    }

    public MessageDetails getValidatedMessageDetailsWithEmail(MessageDetails messageDetails) {
        if (messageDetails.name != null && messageDetails.description != null && messageDetails.email != null) {
            logger.info("Message details were validated (with email)");

            return messageDetails;
        } else {
            logger.info("Message details were not validated (with email");
            return null;
        }
    }
}
